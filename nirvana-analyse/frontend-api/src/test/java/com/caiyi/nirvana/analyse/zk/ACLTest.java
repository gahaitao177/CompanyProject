package com.caiyi.nirvana.analyse.zk;

import org.apache.storm.shade.org.apache.curator.RetryPolicy;
import org.apache.storm.shade.org.apache.curator.framework.CuratorFramework;
import org.apache.storm.shade.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.storm.shade.org.apache.curator.framework.api.ACLProvider;
import org.apache.storm.shade.org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.storm.shade.org.apache.zookeeper.ZooDefs;
import org.apache.storm.shade.org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by been on 2017/2/17.
 */
public class ACLTest {
    private String path = "/hello";
    private CuratorFramework client = null;

    @Before
    public void before() {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory
                    .builder().namespace("com/caiyi/been/test")
                    .retryPolicy(retryPolicy)
                    .connectString("192.168.1.69:2181")
                    .authorization("digest", "been:123456".getBytes())
                    .aclProvider(new ACLProvider() {
                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }

                        @Override
                        public List<ACL> getAclForPath(String s) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    })
                    .build();
            client.start();

//            client.delete().forPath(path);
        } catch (Exception e) {

        }
    }

    @Test
    public void test() throws Exception {
//        client.create()
//                .withMode(CreateMode.PERSISTENT)
//                .forPath(path, "world".getBytes());
        String hello = new String(client.getData().forPath(path));
        Assert.assertEquals("world", hello);
    }
}
