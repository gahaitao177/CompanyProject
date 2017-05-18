package com.caiyi.nirvana.analyse.util;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.storm.utils.DRPCClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by been on 16/8/29.
 */
public class DRPCClientPooledFactory extends BasePooledObjectFactory<DRPCClient> {
    private String host;
    private int port;

    public DRPCClientPooledFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public DRPCClient create() throws Exception {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put("storm.nimbus.retry.interval.millis", 3000);
        conf.put("storm.nimbus.retry.intervalceiling.millis", 1000);
        conf.put("storm.nimbus.retry.times", 3);
        conf.put("drpc.max_buffer_size", 1048576);

        DRPCClient client = new DRPCClient(conf, host, port, 10000);
        return client;
    }


    @Override
    public PooledObject<DRPCClient> wrap(DRPCClient client) {
        return new DefaultPooledObject<>(client);
    }
}
