package com.caiyi.financial.nirvana.core.client.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.storm.thrift.TException;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wenshiliang on 2016/8/17.
 */
public class RemoteDrpcClientImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDrpcClientImplTest.class);

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("drpcIp","192.168.1.207");
        map.put("drpcPort",3772);
        map.put("timeout",3000);
        map.put("storm.thrift.transport","org.apache.storm.security.auth.SimpleTransportPlugin");
        map.put("storm.nimbus.retry.times",3);
        map.put("storm.nimbus.retry.interval.millis",10000);
        map.put("storm.nimbus.retry.intervalceiling.millis",60000);
        map.put("drpc.max_buffer_size",104857600);


//        drpc.drpcIp=192.168.1.207
//        drpc.drpcPort=3772
//        drpc.timeout=3000
//        drpc.storm.thrift.transport =org.apache.storm.security.auth.SimpleTransportPlugin
//        drpc.storm.nimbus.retry.times=3
//        drpc.storm.nimbus.retry.interval.millis=10000
//        drpc.storm.nimbus.retry.intervalceiling.millis=60000
//        drpc.drpc.max_buffer_size=104857600
//        drpc.maxIdle=40
//        drpc.minIdle=3
//        drpc.maxTotal=500
//        drpc.timeBetweenEvictionRunsMillis=10000
//        drpc.maxWaitMillis=5000

//        <entry key="drpcPort" value="${drpc.drpcPort}"></entry>
//        <entry key="timeout" value="${drpc.timeout}"></entry>
//        <entry key="storm.thrift.transport" value="${drpc.storm.thrift.transport}"></entry>
//        <entry key="storm.nimbus.retry.times" value="${drpc.storm.nimbus.retry.times}"></entry>
//        <entry key="storm.nimbus.retry.interval.millis" value="${drpc.storm.nimbus.retry.interval.millis}"></entry>
//        <entry key="storm.nimbus.retry.intervalceiling.millis" value="${drpc.storm.nimbus.retry.intervalceiling.millis}"></entry>
//        <entry key="drpc.max_buffer_size" value="${drpc.drpc.max_buffer_size}"></entry>

        DrpcPooledObjectFactory factory = new DrpcPooledObjectFactory(map);

//        <property name="maxIdle" value="${drpc.maxIdle}"></property>
//        <property name="minIdle" value="${drpc.minIdle}"></property>
//        <property name="maxTotal" value="${drpc.maxTotal}"></property>
//        <property name="timeBetweenEvictionRunsMillis" value="${drpc.timeBetweenEvictionRunsMillis}"></property>
//        <property name="maxWaitMillis" value="${drpc.maxWaitMillis}"></property>

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(10);
        config.setMinIdle(3);
        config.setMaxTotal(10);
        config.setTimeBetweenEvictionRunsMillis(10000);
        config.setMaxWaitMillis(5000l);

        GenericObjectPool<DRPCClient> pool = new GenericObjectPool<DRPCClient>(factory,config);

//        try {
//            DRPCClient client = pool.borrowObject();
//            String s = client.execute("hskCcardInfo","{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":204},\"method\":\"index\"}");
//            pool.getFactory().destroyObject(new DefaultPooledObject(client));
////            pool.returnObject(client);
//
//            client = pool.borrowObject();
//            pool.getFactory().destroyObject(new DefaultPooledObject(client));
////            pool.returnObject(client);
//            client = pool.borrowObject();
//            pool.getFactory().destroyObject(new DefaultPooledObject(client));
////            pool.returnObject(client);
//            client = pool.borrowObject();
//            pool.getFactory().destroyObject(new DefaultPooledObject(client));
////            pool.returnObject(client);
//            client = pool.borrowObject();
//            pool.getFactory().destroyObject(new DefaultPooledObject(client));
////            pool.returnObject(client);
//            client = pool.borrowObject();
//            s = client.execute("hskCcardInfo","{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":204},\"method\":\"index\"}");
//            pool.getFactory().destroyObject(new DefaultPooledObject(client));
////            pool.returnObject(client);
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }









//        try {
//            DRPCClient client = pool.borrowObject();
//            String s = client.execute("hskCcardInfo","{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":204},\"method\":\"index\"}");
//            LOGGER.info(s);
//            System.out.println(client.transport());
////            client.close();
//            client.reconnect();
//            System.out.println(client.transport());
//            s = client.execute("hskCcardInfo","{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":204},\"method\":\"index\"}");
//            LOGGER.info(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            DRPCClient client = pool.borrowObject();
            pool.invalidateObject(client);
            pool.returnObject(client);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(1==1){
            return;
        }



        ExecutorService service = Executors.newCachedThreadPool();
        for(int i = 0;i<12;i++){
            service.submit(new PoolThread(pool));
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();
        while(!service.isShutdown()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("ok");
        try {

            for(int i=0;i<10;i++){
                DRPCClient client = pool.borrowObject();
                String s = client.execute("hskCcardInfo","{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":204},\"method\":\"index\"}");
                LOGGER.info(s);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class PoolThread implements Runnable{
    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    GenericObjectPool<DRPCClient> pool;
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolThread.class);


    public PoolThread(GenericObjectPool<DRPCClient> pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        for(int i = 0;i<10;i++){
            test();
        }
    }

    void test(){
        DRPCClient client = null;
        boolean isDestroy = false;
        try {
            client = pool.borrowObject();
            String s = client.execute("hskCcardInfo","{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":204},\"method\":\"index\"}");
            System.out.println(s);
            int i = new Random().nextInt(3);
            System.out.println(i);
            if(i==0){
                LOGGER.info("正常");
            }else if(i==1){
                LOGGER.info("测试 TException");
                throw new TException("测试 TException");
            }else if(i==2){
                LOGGER.info("测试 RuntimeException");
                throw new RuntimeException("测试 RuntimeException");
            }
        } catch (Exception e) {
//            if(isRetry(e)){
//                LOGGER.info("drpc重试:{}",retryCount.get());
//                return execute(drpcService,drpcRequest);
//            }

            if(e instanceof TException){
                LOGGER.error("drpc 连接出错了",e);
                if(client!=null){
                    try {
                        isDestroy = true;
                        client.reconnect();
//                        PooledObjectFactory<DRPCClient> factory = pool.getFactory();
//                        factory.destroyObject(new DefaultPooledObject(client));
//                        client = factory.makeObject().getObject();
//                        pool.getFactory().destroyObject(new DefaultPooledObject(client));
                        pool.invalidateObject(client);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }else{
//                LOGGER.error("execute 执行出错了",e);
                e.printStackTrace();
            }
//            throw new ClientException(BoltResult.Error_405,"服务器异常!!!");
        } finally {

            if(client!=null  && !isDestroy){
                LOGGER.info("放回连接池");
                pool.returnObject(client);
//                try {
//                    pool.addObject();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }else{
                try {
//                    pool.getFactory().destroyObject(new DefaultPooledObject(client));
//                    client.reconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LOGGER.info("----------处理client，是否放回连接池："+ !isDestroy);
            }
            LOGGER.info("finally：{}",atomicInteger.incrementAndGet());
//            retryCount.remove();
        }
    }
}