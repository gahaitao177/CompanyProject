package com.caiyi.financial.nirvana.heartbeat.client.impl;

import org.apache.storm.drpc.DRPCInvocationsClient;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/10/17.
 */
public class DRPCInvocationsClientTest {
    @Test
    public void test1(){
        Map conf = DefaultSchedulerBuilderTest.getMap();
        String server = "192.168.2.212";
        int port = 3772;
        DRPCInvocationsClient c = null;
        long start = System.currentTimeMillis();
        try {
             c = new DRPCInvocationsClient(conf, server, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("第一次创建 成功or失败 用时"+(end-start));
        if(c!=null){
            while(true){
                System.out.println(c.isConnected());
                if(!c.isConnected()){
                    c.close();
                    try {
                        c =  new DRPCInvocationsClient(conf, server, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    c.reconnectClient();
                    System.out.println("reconnectClient");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try{
                    start = System.currentTimeMillis();
                    c.reconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
                end = System.currentTimeMillis();
                System.out.println("reconnect " +(end-start));


                try {
                    start = System.currentTimeMillis();
                    new DRPCInvocationsClient(conf, server, port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                end = System.currentTimeMillis();
                System.out.println("new DRPCInvocationsClient " +(end-start));
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
