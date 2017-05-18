package com.caiyi.financial.nirvana.core.client.pool.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/6/14.
 */
public class DrpcClientConf extends HashMap<String,Object> {
    private String drpcIp;
    private int drpcPort;
    private int timeout;

    public DrpcClientConf(Map<? extends String, ? extends String> m) {
        super(m);
    }

    public DrpcClientConf(){
        put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        put("storm.nimbus.retry.times", 3);
        put("storm.nimbus.retry.interval.millis", 3000);
        put("storm.nimbus.retry.intervalceiling.millis", 60000);
        put("drpc.max_buffer_size", 104857600);
    }

    public String getDrpcIp() {
        return drpcIp;
    }

    public void setDrpcIp(String drpcIp) {
        this.drpcIp = drpcIp;
    }

    public int getDrpcPort() {
        return drpcPort;
    }

    public void setDrpcPort(int drpcPort) {
        this.drpcPort = drpcPort;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
