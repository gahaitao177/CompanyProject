package com.caiyi.financial.nirvana.core.client.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.storm.utils.DRPCClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/8/23.
 */
public class PoolProvider {

    public static GenericKeyedObjectPool<String, DRPCClient> getPool2(){
        Map<String,Object> map = new HashMap<>();
        map.put("drpcIp","192.168.1.207");
        map.put("drpcPort",3772);
        map.put("timeout",3000);
        map.put("storm.thrift.transport","org.apache.storm.security.auth.SimpleTransportPlugin");
        map.put("storm.nimbus.retry.times",3);
        map.put("storm.nimbus.retry.interval.millis",10000);
        map.put("storm.nimbus.retry.intervalceiling.millis",60000);
        map.put("drpc.max_buffer_size",104857600);
        List<String> list = new ArrayList<>();
        list.add("192.168.1.207:3772");
        list.add("192.168.1.207:3772");
        map.put("servers",list);
        BaseKeyedPooledObjectFactory<String,DRPCClient> factory = new DrpcClientKeyedFactory(map);


        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMinIdlePerKey(3);
        config.setMaxIdlePerKey(10);
        config.setMaxTotal(10);
        config.setTimeBetweenEvictionRunsMillis(10000);
        config.setMaxWaitMillis(5000l);

        GenericKeyedObjectPool<String,DRPCClient> pool = new GenericKeyedObjectPool<String,DRPCClient>(factory,config);
        return pool;
    }

    public  static GenericObjectPool<DRPCClient> getPool(){
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
        return pool;
    }
}
