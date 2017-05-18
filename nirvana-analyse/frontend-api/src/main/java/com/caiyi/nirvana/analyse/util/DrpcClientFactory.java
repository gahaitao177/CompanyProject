package com.caiyi.nirvana.analyse.util;

import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by been on 16/1/4.
 */
public class DrpcClientFactory {

    private DrpcClientFactory() {

    }

    //300 秒超时
    public static DRPCClient getDefaultDRPCClient() throws TTransportException {
        System.out.println("drpc server  " + StormConfig.STORM_SERVCE);
        return new DRPCClient(getConf(), StormConfig.STORM_SERVCE, 3772, 300000);
    }

    private static Map getConf() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put("storm.nimbus.retry.times", 3);
        conf.put("storm.nimbus.retry.interval.millis", 10000);
        conf.put("storm.nimbus.retry.intervalceiling.millis", 10000);
        conf.put("drpc.max_buffer_size", 10485760);
        return conf;
    }
}
