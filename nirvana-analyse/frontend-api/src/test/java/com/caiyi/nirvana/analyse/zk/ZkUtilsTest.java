package com.caiyi.nirvana.analyse.zk;

import org.apache.storm.shade.org.apache.curator.RetryPolicy;
import org.apache.storm.shade.org.apache.curator.framework.CuratorFramework;
import org.apache.storm.shade.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.storm.shade.org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 * Created by been on 2017/2/21.
 */
public class ZkUtilsTest {
    @Test
    public void test() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("192.168.1.69:2181")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();
//        curatorFramework.create().forPath("/kafka-spout-root");
        curatorFramework.create().forPath("/dcos-service-kafka/kafka-spout-root");
        Thread.sleep(2000);
    }
}
