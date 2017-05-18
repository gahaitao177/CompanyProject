package com.caiyi.financial.nirvana.core.zk.curator;

import com.caiyi.financial.nirvana.core.util.ZKConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wenshiliang on 2016/10/9.
 */
public class ListenerTest {
    static Logger logger = LoggerFactory.getLogger(ListenerTest.class);
    @Test
    public void test1(){
        CuratorFramework client = ZKConfig.getClient();
        byte[] data = new byte[0];
        try {
            data = client.getData().usingWatcher(new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("获取 test1 节点 监听器 : " + event);
                }
            }).forPath("/test/test1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(new String(data));
        while(true){

        }
    }
    @Test
    public void test2(){
        CuratorFramework client = ZKConfig.getClient();
        ExecutorService pool = Executors.newCachedThreadPool();

        CuratorListener listener = new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                logger.info("监听器  : "+ event.toString());
                logger.info("{},{}",client,event);
            }
        };
        client.getCuratorListenable().addListener(listener,pool);
        try {
            client.getData().inBackground().forPath("/test/test1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true){

        }
//        client.getData().inBackground().forPath("/two");
    }


    @Test
    public void test3() throws Exception {
        CuratorFramework client = ZKConfig.getClient();

//        ExecutorService pool = Executors.newCachedThreadPool();

        PathChildrenCache childrenCache = new PathChildrenCache(client, "/test/test1", true);
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                logger.info("开始进行事件分析:-----");
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        logger.info("CHILD_ADDED : "+ data.getPath() +"  数据:"+ data.getData());
                        break;
                    case CHILD_REMOVED:
                        logger.info("CHILD_REMOVED : "+ data.getPath() +"  数据:"+ data.getData());
                        break;
                    case CHILD_UPDATED:
                        logger.info("CHILD_UPDATED : "+ data.getPath() +"  数据:"+ data.getData());
                        break;
                    default:
                        break;
                }
            }
        };
        childrenCache.getListenable().addListener(childrenCacheListener);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        while(true){

        }
    }


    @Test
    public void test5() throws Exception {
//        ExecutorService pool = Executors.newSingleThreadExecutor();
        CuratorFramework client = ZKConfig.getClient();
        final NodeCache nodeCache = new NodeCache(client, "/test/test1", false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                logger.info("getCurrentData:" + nodeCache.getCurrentData());
                logger.info("stat : "+nodeCache.getCurrentData().getStat());
                logger.info("path : "+nodeCache.getCurrentData().getPath());
                logger.info("data : "+new String(nodeCache.getCurrentData().getData()));
            }
        });
        nodeCache.start();
        while(true);
    }

}
