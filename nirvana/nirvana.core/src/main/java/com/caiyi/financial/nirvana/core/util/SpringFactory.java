package com.caiyi.financial.nirvana.core.util;

import com.caiyi.financial.nirvana.core.constant.ApplicationConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wenshiliang on 2016/7/25.
 */
public class SpringFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringFactory.class);
    private static String SPRING_STR;
    private static String QUARTZ_STR;
    private static String SPRING_CREATE_LOCK_PATH;
    private static String SPRING_CREATE_QUARTZ_MARK_PATH;
    private static String addressIp;

    private  static ApplicationContext springcontext;
    private static ExecutorService zkListenerThread = Executors.newSingleThreadExecutor();

    static {
        SPRING_STR = SystemConfig.get(ApplicationConstant.SPRING_CONTEXT);
        QUARTZ_STR = SystemConfig.get(ApplicationConstant.SPRING_QUARTZ);
        SPRING_CREATE_LOCK_PATH = ApplicationConstant.ZK_ROOT_PATH+"/"+SystemConfig.get(ApplicationConstant.DRPC_SERVICE)+"/springCreateLock";
        SPRING_CREATE_QUARTZ_MARK_PATH = ApplicationConstant.ZK_ROOT_PATH+"/"+SystemConfig.get(ApplicationConstant.DRPC_SERVICE)+"/springCreateQuartzMark";
    }

    public static String getAddressIp(){
        if(addressIp!=null){
            return addressIp;
        }
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            LOGGER.error("",e);
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                {
                    String ipStr = ip.getHostAddress();
                    if(ipStr.indexOf("127")!=0){
                        addressIp = ipStr;
                        return addressIp;
                    }
                }
            }
        }
        throw new RuntimeException("无法获取当前ip，初始化spring失败");
    }


    public static ApplicationContext newApplicationContext(){
        if(springcontext!=null){
            return springcontext;
        }
        CuratorFramework client = ZKConfig.getClient();
        InterProcessMutex lock = new InterProcessMutex(client,SPRING_CREATE_LOCK_PATH);
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            lock.acquire();
            jvmName = getAddressIp()+":"+jvmName;
            LOGGER.info("开启zk lock锁，path={}",SPRING_CREATE_LOCK_PATH);
            if(springcontext == null){
                LOGGER.info("初始化当前jvm 所在{}",jvmName);
                if(QUARTZ_STR!=null){

                    springcontext = new ClassPathXmlApplicationContext(SPRING_STR,QUARTZ_STR);
                    //判断是否存在SchedulerFactoryBean，不存在。则认为不存quartz 调度。当spring有更新时候，需要查看
                    SchedulerFactoryBean scheduler = springcontext.getBean(SchedulerFactoryBean.class);
                    if(scheduler==null){
                        LOGGER.error("org.springframework.scheduling.quartz.SchedulerFactoryBean不存在");
                    }else{
                        /**
                         * 使用共享锁
                         *
                         * 1,判断是否存在节点，不存在。则为master，启动调度；并创建临时节点存放调度jvmName
                         * 2,存在节点，stop调度，开启zk listener
                         * 3,节点有变动，监听器启用，判断节点情况。节点不存在，重新竞争，节点已经存在，根据节点 value 是否启用scheduler
                         */
                        Stat stat =  client.checkExists().forPath(SPRING_CREATE_QUARTZ_MARK_PATH);
                        if(stat==null){
                            if(!scheduler.isRunning()){
                                scheduler.start();
                                LOGGER.info("quartz scheduler start");
                            }
                            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(SPRING_CREATE_QUARTZ_MARK_PATH,jvmName.getBytes());
                        }else{
                            if (scheduler.isRunning()){
                                scheduler.stop();
                                LOGGER.info("quartz scheduler stop");
                            }
                            NodeCache nodeCache = new NodeCache(client, SPRING_CREATE_QUARTZ_MARK_PATH, false);
                            nodeCache.getListenable().addListener(new QuartzNodeCacheListener(jvmName,nodeCache,client,scheduler),zkListenerThread);
                            nodeCache.start();

                        }
                    }
                }
            }
            if(springcontext == null){
                springcontext = new ClassPathXmlApplicationContext(SPRING_STR);
                LOGGER.info("SpringFactory构造ClassPathXmlApplicationContext ：使用配置{}",SPRING_STR);
            }else{
                LOGGER.info("SpringFactory存在并返回ApplicationContext");
            }
        } catch (Exception e) {
            LOGGER.error("springFactory 初始化spring出错",e);
        }
        try {
            if(lock!=null){
                lock.release();
            }
            LOGGER.info("关闭zk lock锁，path={}",SPRING_CREATE_LOCK_PATH);
        } catch (Exception e) {
            LOGGER.error("关闭spring 初始化 lock 失败",e);
        }
        return springcontext;
    }

    @Deprecated
    public static ApplicationContext newApplicationContext1(){
        CuratorFramework client = ZKConfig.getClient();
        if(springcontext!=null){
            return springcontext;
        }
        InterProcessMutex lock = new InterProcessMutex(client,SPRING_CREATE_LOCK_PATH);
        try {
            lock.acquire();
            LOGGER.info("开启zk lock锁，path={}",SPRING_CREATE_LOCK_PATH);
            if(springcontext == null){
                LOGGER.info("初始化当前jvm ip所在{}",getAddressIp());
                if(QUARTZ_STR!=null){
                    Stat stat =  client.checkExists().forPath(SPRING_CREATE_QUARTZ_MARK_PATH);
                    if(stat==null){
                        springcontext = new ClassPathXmlApplicationContext(SPRING_STR,QUARTZ_STR);
                        //修改quartz 启动配置节点为临时节点，当zkclint失效时，自动删除。
                        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(SPRING_CREATE_QUARTZ_MARK_PATH,addressIp.getBytes());
                        LOGGER.info("SpringFactory构造ClassPathXmlApplicationContext ：使用配置{},{}",SPRING_STR,QUARTZ_STR);
                    }else{
                        byte[] bytes = client.getData().forPath(SPRING_CREATE_QUARTZ_MARK_PATH);
                        String quartz_AddressIp = new String(bytes);
                        LOGGER.info("当前topology[{}]中，启动quartz的首次jvm ip为:[{}]，当前ip;[{}]",SystemConfig.get(ApplicationConstant.DRPC_SERVICE),quartz_AddressIp,getAddressIp());
                        if(addressIp.equals(quartz_AddressIp)){
                            springcontext = new ClassPathXmlApplicationContext(SPRING_STR,QUARTZ_STR);
                            LOGGER.info("SpringFactory重新构造ClassPathXmlApplicationContext ：使用配置{},{}",SPRING_STR,QUARTZ_STR);
                        }
                    }

                }
            }
            if(springcontext == null){
                springcontext = new ClassPathXmlApplicationContext(SPRING_STR);
                LOGGER.info("SpringFactory构造ClassPathXmlApplicationContext ：使用配置{}",SPRING_STR);
            }
        } catch (Exception e) {
            LOGGER.error("=pringFactory 初始化spring出错",e);
        }
        try {
            if(lock!=null){
                lock.release();
            }
            LOGGER.info("关闭zk lock锁，path={}",SPRING_CREATE_LOCK_PATH);
        } catch (Exception e) {
            LOGGER.error("关闭spring 初始化 lock 失败",e);
        }
        return springcontext;
//        springcontext = new ClassPathXmlApplicationContext("spring-context.xml","spring-quartz.xml");
    }

    public static void clear(){
        //构造tpology 清除zk上一些配置
        //清除quartz调度任务创建标记
        ZKConfig.deletingChildrenIfNeeded(SPRING_CREATE_LOCK_PATH);
        if(QUARTZ_STR!=null){
            ZKConfig.deletingChildrenIfNeeded(SPRING_CREATE_QUARTZ_MARK_PATH);
        }
    }


    /**
     * 监听quartz的启动节点的listener
     */
    public static class QuartzNodeCacheListener implements NodeCacheListener {

        private String jvmName;
        private NodeCache nodeCache;
        private CuratorFramework client;
        private SchedulerFactoryBean scheduler;

        public QuartzNodeCacheListener(String jvmName, NodeCache nodeCache, CuratorFramework client, SchedulerFactoryBean scheduler) {
            this.jvmName = jvmName;
            this.nodeCache = nodeCache;
            this.client = client;
            this.scheduler = scheduler;
        }

        @Override
        public void nodeChanged() throws Exception {
            ChildData data = nodeCache.getCurrentData();
            String masterJvmName = null;
            if(data!=null){
                masterJvmName = new String(data.getData());
            }
            LOGGER.info("监听到变动path={}",SPRING_CREATE_QUARTZ_MARK_PATH);
            if(data==null){
                //当节点不存在时候,开启共享锁竞争
                InterProcessMutex lock = new InterProcessMutex(client,SPRING_CREATE_LOCK_PATH);
                if(!lock.isAcquiredInThisProcess()){
                    lock.acquire();
                }
                Stat stat =  client.checkExists().forPath(SPRING_CREATE_QUARTZ_MARK_PATH);
                if(stat==null){
                    if(!scheduler.isRunning()){
                        scheduler.start();
                        LOGGER.info("quartz scheduler start");
                    }
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(SPRING_CREATE_QUARTZ_MARK_PATH,jvmName.getBytes());
                }
                if(lock.isAcquiredInThisProcess()){
                    lock.release();
                }
            }else if(masterJvmName.equals(jvmName)){
                //当值与当前jvm相同时候，判断 scheduler 是否启动，没有则启动
                if(!scheduler.isRunning()){
                    scheduler.start();
                    LOGGER.info("quartz scheduler start");
                }
            }else{
                //当值与当前jvm不同时，判断 scheduler 是否启动，启动则停止
                if(scheduler.isRunning()){
                    scheduler.stop();
                    LOGGER.info("quartz scheduler stop");
                }
            }
        }
    }

}
