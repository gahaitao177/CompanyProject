package com.caiyi.financial.nirvana.core.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by wenshiliang on 2016/7/21.
 */
public class Barrier {
    static String barrier_path = "/zktest/barrier_path";
    static DistributedBarrier barrier;

    public static void main(String[] args) throws Exception {
        for(int i =0;i<5;i++){
            new Thread(()->{
                CuratorFramework client = CuratorFrameworkFactory
                        .builder()
                        .connectString("192.168.1.55:2181,192.168.1.205:2181")
                        .retryPolicy(new ExponentialBackoffRetry(6000,3))
                        .connectionTimeoutMs(6000)
                        .sessionTimeoutMs(100)
                        .build();
                client.start();

                barrier = new DistributedBarrier(client,barrier_path);
                System.out.println(Thread.currentThread().getName()+"号 设置barrier");
                try {
                    barrier.setBarrier();
                    barrier.waitOnBarrier();
                    System.out.println("启动....");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(2000);
        barrier.removeBarrier();
    }
}
