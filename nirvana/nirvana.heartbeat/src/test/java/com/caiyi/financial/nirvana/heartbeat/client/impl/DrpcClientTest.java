package com.caiyi.financial.nirvana.heartbeat.client.impl;

import com.caiyi.financial.nirvana.heartbeat.client.DRPCClientPool;
import org.apache.storm.utils.DRPCClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/10/18.
 */
public class DrpcClientTest {
    @Test
    public void test1(){
        DRPCClient c = null;
        Map conf = DefaultSchedulerBuilderTest.getMap();
        String server = "192.168.2.212";
        int port = 3772;
        long start = System.currentTimeMillis();
        try {
            c = new DRPCClient(conf,server,port,3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("new DRPCClient"+(end-start));

        if(c!=null) {
            while (true) {
                System.out.println(c.transport().isOpen());
                System.out.println(c.transport().peek());
                c.reconnect();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testClientPool() {
        Map<String, List<String>> config = new HashMap<>();
        List<String> serverSeeds = new ArrayList<>();
        serverSeeds.add("192.168.1.202:3772");
        serverSeeds.add("192.168.1.207:3772");
        config.put(DRPCClientPool.SERVER_SEEDS, serverSeeds);
        try {
            for (int i = 0; i < 4; i++) {
                DRPCClient drpcClient = DRPCClientPool.getResouce(config);
                System.out.println(drpcClient.execute("drpc_heartbeat", "hello"));
                drpcClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
