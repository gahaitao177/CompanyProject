package com.caiyi.financial.nirvana.core.util;

import com.caiyi.financial.nirvana.core.constant.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 帮助类
 * 用来从zk读取配置
 * 需要其他方法再添加
 * 现在有 del  get
 * Created by wenshiliang on 2016/7/21.
 * update by lcs on 2016/09/14  添加set方法
 */
public class ZKConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(ZKConfig.class);

    private static CuratorFramework client;
    static {
        String connectString = SystemConfig.get(ApplicationConstant.ZK_CONNECT);
        if(StringUtils.isEmpty(connectString)){
            throw new RuntimeException("读取config中的zk配置失败");
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,3);//重试策略
        client = CuratorFrameworkFactory
                .builder()
                .connectString(connectString)
                .retryPolicy(retryPolicy)
//                .connectionTimeoutMs(6000)
//                .sessionTimeoutMs(100)
                .build();
        client.start();
    }
    public static void setClient(CuratorFramework client){
        CloseableUtils.closeQuietly(ZKConfig.client);
        ZKConfig.client = client;
    }
    public static CuratorFramework getClient(){
        return client;
    }

    /**
     * 从zookeeper 节点取值，不存在取默认值，并创建节点设为默认值
     * @param path
     * @param bytes
     * @return
     */
    public static byte[] get(String path,byte[] bytes){
        if(bytes==null){
            bytes = new byte[]{};
        }
        try {
            Stat stat =  client.checkExists().forPath(path);
            if(stat==null){
                //set
                client.create().creatingParentsIfNeeded().forPath(path,bytes);
            }else{
                //get
                return client.getData().forPath(path);
            }
        } catch (Exception e) {
            LOGGER.error("从zk取值失败",e);
        }
        return bytes;
    }

    /**
     *
     * @param path
     * @param defaultValue
     * @return
     * add by lcs 20160914
     */
    public static String set(String path,String defaultValue){
        byte[] bytes = defaultValue.getBytes();
        bytes = set(path,bytes);
        return new String(bytes);
    }

    /**
     * 设置zk虚拟节点 断开后自动删除
     * @param path
     * @param bytes
     * @return
     * add by lcs 20160914
     */
    public static byte[] set(String path,byte[] bytes){
        if(bytes==null){
            bytes = new byte[]{};
        }
        try {
            Stat stat =  client.checkExists().forPath(path);
            if(stat==null){
                //set
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,bytes);
            }else{
                //get
                client.setData().forPath(path,bytes);
            }
        } catch (Exception e) {
            LOGGER.error("从zk取值失败",e);
        }
        return bytes;
    }


    public static String get(String path,String defaultValue){
        byte[] bytes = defaultValue.getBytes();
        bytes = get(path,bytes);
        return new String(bytes);
    }

    public static int get(String path,int defaultValue){
        String str = get(path,Integer.toString(defaultValue));
        try {
            return Integer.valueOf(str);
        }catch (NumberFormatException e){
            LOGGER.error("获得int数据失败");
            if(deletingChildrenIfNeeded(path)){
                return get(path,defaultValue);
            }
        }
        return defaultValue;
    }

    public static boolean deletingChildrenIfNeeded(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
            return true;
        } catch (Exception e) {
            LOGGER.warn("删除zk节点失败：path={},异常：{}",path,e.getMessage());
            return false;
        }
    }


}
