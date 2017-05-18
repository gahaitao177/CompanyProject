package com.caiyi.financial.nirvana.heartbeat.client.impl;


import com.caiyi.financial.nirvana.heartbeat.Constant;
import com.caiyi.financial.nirvana.heartbeat.client.DrpcServer;
import com.caiyi.financial.nirvana.heartbeat.client.Scheduler;
import org.apache.storm.thrift.TException;
import org.apache.storm.utils.DRPCClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/10/11.
 */
public class DefaultSchedulerBuilderTest {


    public static Map getMap(){
        Map<String,Object> map = new HashMap<>();
//        map.put("drpcIp","192.168.1.207");
//        map.put("drpcPort",3772);
//        map.put("timeout",3000);
        map.put("storm.thrift.transport","org.apache.storm.security.auth.SimpleTransportPlugin");
        map.put("storm.nimbus.retry.times",3);
        map.put("storm.nimbus.retry.interval.millis",10000);
        map.put("storm.nimbus.retry.intervalceiling.millis",60000);
        map.put("drpc.max_buffer_size",104857600);
        return map;
    }

    @Test
    public void testBuilder() throws Exception {
        Map<String,Object> map = getMap();

        DefaultSchedulerBuilder builder = new DefaultSchedulerBuilder();
        DrpcServer server1 = new DrpcServer(map,"192.168.2.211",3772,3000);
        DrpcServer server2 = new DrpcServer(map,"192.168.2.212",3772,3000);
        Scheduler scheduler = builder.addDrpcServer(server1,server2)
                .setListener(new DemoListener())
                .build();
        scheduler.start();
        TimeUnit.MINUTES.sleep(20);
    }

    @Test
    public void test1(){
        Map<String,Object> map = getMap();
        DRPCClient client = null;
        try{
            client = new DRPCClient(map,"192.168.2.211",3772,3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        while(true){
            try {
                String result = client.execute(Constant.DRPC_SERVICE,"192.168.2.211:3772");
                System.out.println(result);
            } catch (TException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void test2(){
        Map<String,Object> map = getMap();
        DRPCClient client = null;
        while(true){
            try{
                client = new DRPCClient(map,"192.168.2.212",3772,3000);
                System.out.println("创建成功");
                String result = client.execute(Constant.DRPC_SERVICE,"192.168.2.212");
                System.out.println(result);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(client!=null){
                    client.close();
                }
            }
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    public void test3(){
        Map<String,Object> map = getMap();
        DrpcServer server1 = new DrpcServer(map,"192.168.2.211",3772,3000);
        DrpcServer server2 = new DrpcServer(map,"192.168.2.211",3772,3000);
        System.out.println(server1.equals(server2));
        List<DrpcServer> serverList = new ArrayList<>();
        serverList.add(server1);
        System.out.println(serverList.contains(server2));
    }



}