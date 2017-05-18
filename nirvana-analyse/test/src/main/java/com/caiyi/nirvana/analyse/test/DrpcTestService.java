package com.caiyi.nirvana.analyse.test;

import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Created by been on 2017/3/6.
 */
public class DrpcTestService {

    private DRPCClient getDrpcClient(String host) throws TTransportException {
        Map<String, Object> conf = new HashMap<>();
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put("storm.nimbus.retry.times", 3);
        System.out.println("");
        conf.put("storm.nimbus.retry.interval.millis", 10000);
        conf.put("storm.nimbus.retry.intervalceiling.millis", 10000);
        conf.put("drpc.max_buffer_size", 10485760);
        return new DRPCClient(conf, host, 3772, 30000);
    }

    public void testDrpc(String host) throws Exception {
        DRPCClient drpcClient = getDrpcClient(host);
        try {
            IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).forEach(value -> {
                try {
                    String params = "hello, world";
                    String result = drpcClient.execute("exclamation", params);
                    System.out.println("#############" + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            drpcClient.close();
        }
    }
}
