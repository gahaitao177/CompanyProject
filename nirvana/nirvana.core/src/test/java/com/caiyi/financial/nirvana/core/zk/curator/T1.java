package com.caiyi.financial.nirvana.core.zk.curator;

import com.caiyi.financial.nirvana.core.util.ZKConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wenshiliang on 2016/7/19.
 */
public class T1 {

    static  Logger logger = LoggerFactory.getLogger(T1.class);

    @Test
    public void test1() throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,3);//重试策略

        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString("192.168.1.204")
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(6000)
                .sessionTimeoutMs(100)

                .build();

        client.start();


        String rootPath = "/hsk_nirvana";
        String drpcservice = "local_service";


        byte[] bytes = client.getData().forPath(rootPath+"/"+drpcservice);
        if(bytes!=null)
            System.out.println("---"+new String(bytes));

        Stat  stat = client.checkExists().forPath(rootPath+"/"+drpcservice);
        if(stat==null){
            String result = client.create().forPath(rootPath+"/"+drpcservice);
//            System.out.println(result);
        }





//        TimeUnit.SECONDS.sleep(10);

//        CloseableUtils.closeQuietly(client);
//        client.getState();

//        System.out.println(client.getState());

//        int version = client.setData().withVersion(6).forPath("/zktest/t2","wetawer111111".getBytes()).getCversion();
//        System.out.println("version : "+ version);


//        byte[] bytes = client.getData().forPath("/zktest/t2");
//        System.out.println(new String(bytes));


//        Stat stat = client.checkExists().forPath("/zktest/t2");

//        System.out.println("--------------: "+stat);



//        Object obj =



//        String str = client.create().creatingParentContainersIfNeeded().forPath("/zktest/t3/tt","zktestt3tt".getBytes());
//        System.out.println(str);

        List<String> list =  client.getChildren().forPath("/zktest");
        list.forEach(path->{
            System.out.println(path);
        });

//        Collection<CuratorTransactionResult> results =     client.inTransaction().check().forPath("/zktest/t2").and().commit();
//
//
//
//        results.forEach((result)->{
//            System.out.println(result);
//        });
//        System.out.println(obj);


//

    }

    @Test
    public void test2() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,3);//重试策略

        String path = "/zktest1/test5";
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString("192.168.1.55:2181,192.168.1.205:2181")
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(6000)
                .sessionTimeoutMs(100)

                .build();

        client.start();
//        CloseableUtils.closeQuietly(client);
//        System.out.println(client.getState());
//        client.start();


        client.create()
                .creatingParentsIfNeeded()
//                .withMode(CreateMode.EPHEMERAL)
                .forPath(path,"init".getBytes());

        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        System.out.println("new version---:"+client.setData().withVersion(stat.getVersion()).forPath(path));
        client.setData().withVersion(stat.getVersion()).forPath(path);

    }


    @Test
    public void test3() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,3);//重试策略

        String path = "/hsk_nirvana";
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString("192.168.1.55:2181,192.168.1.205:2181")
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(6000)
                .sessionTimeoutMs(100)
                .build();

        client.start();
//        client.start();


//        client.create().creatingParentsIfNeeded().forPath(path,new byte[]{});

//        byte[] bytes = client.getData().forPath(path);
//        System.out.println(bytes);
//        System.out.println(new String(bytes));

        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public static  CuratorFramework getClient(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,3);//重试策略
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString("192.168.1.55:2181,192.168.1.205:2181")
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(6000)
                .sessionTimeoutMs(100)
                .build();

        client.start();
        return client;
    }


    /**
     * zk分布式锁test
     * @throws Exception
     */
    public static void testLock() throws Exception {
        String lockPath = "/test_lock";
        final InterProcessMutex lock = new InterProcessMutex(getClient(),lockPath);
        final CountDownLatch down = new CountDownLatch(1);
        ExecutorService service = Executors.newCachedThreadPool();
        for(int i = 0;i<10;i++){
            service.submit(()->{
                try {
                    down.await();
                    lock.acquire();
                }catch (Exception e){
                    e.printStackTrace();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                logger.info("---------"+sdf.format(new Date()));
                try {
                    lock.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }
        down.countDown();


        System.out.println("1111111");
    }

    public static void testAtomicInt() throws Exception {
        String atomicIntegerPath = "/zktest/atomicIntegerPath";
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(getClient(), atomicIntegerPath, new RetryNTimes(3, 1000));

        AtomicValue<Integer> rc = atomicInteger.add(4);
        System.out.println("result:"+rc.succeeded());
        System.out.println("result:"+rc.preValue());
        System.out.println("result:"+rc.postValue());
        System.out.println(rc.getStats());
    }


//    public static void tsstBarrier(){
//        String path  = "/zktest/barrier";
//        DistributedBarrier barrier = new DistributedBarrier(getClient(),path);
//
//
//    }



    @Test
    public void test4() throws Exception {
        CuratorFramework client = ZKConfig.getClient();
        String path = "/test/test1";
        byte[] bytes = "测试".getBytes();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,bytes);
        while(true){

        }
    }
    public static void main(String[] args) throws Exception {
//        testAtomicInt();
//        tsstBarrier();
    }

    @Test
    public void test5()   {
        CuratorFramework client = ZKConfig.getClient();
        String path = "/test/a1";
        boolean flag = false;
        long start = 0;
        long end = 0;
        while(true){
            try {
                byte[] bytes = client.getData().forPath(path);
                start = System.currentTimeMillis();
                flag = true;
                logger.info("存在此节点");
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        while(flag){
            try {
                byte[] bytes = client.getData().forPath(path);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                end = System.currentTimeMillis();
                break;
//                e.printStackTrace();
            }
        }
        logger.info("存在到删除共用时{}",end-start);

    }
}
