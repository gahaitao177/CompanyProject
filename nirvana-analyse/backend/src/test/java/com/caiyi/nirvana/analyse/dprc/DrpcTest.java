package com.caiyi.nirvana.analyse.dprc;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.common.config.TopologyConfig;
import org.apache.storm.shade.org.apache.commons.io.IOUtils;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Created by been on 2017/1/13.
 */
public class DrpcTest {
    @Test
    public void test() throws Exception {
        DRPCClient drpcClient = getDrpcClient("192.168.1.88");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", "modifyPwd");
        new Date().getTime();
        System.out.println(drpcClient.execute(TopologyConfig.DRPC_FUNCNAME, jsonObject.toJSONString()));
//        System.out.println(drpcClient.execute("drpc-test", jsonObject.toJSONString()));

    }


    @Test
    public void testSaveAppProfileDev() throws Exception {
        DRPCClient drpcClient = getDrpcClient("192.168.1.88");
        byte[] data = IOUtils.toByteArray(new FileInputStream(new File("data-template.json")));
//        AppProfile appProfile = JSONObject.parseObject(new String(data), AppProfile.class);
//        System.out.println(JSONObject.toJSONString(appProfile, true));
        String content = new String(data);
        String result = drpcClient.execute(TopologyConfig.DRPC_FUNCNAME, content);
        System.out.println(result);
    }


    @Test
    public void testSaveAppProfileProd() throws Exception {
        DRPCClient drpcClient = getDrpcClient("192.168.83.66");
        byte[] data = IOUtils.toByteArray(new FileInputStream(new File("data-template.json")));
//        AppProfile appProfile = JSONObject.parseObject(new String(data), AppProfile.class);
//        System.out.println(JSONObject.toJSONString(appProfile, true));
        String content = new String(data);
        drpcClient.execute(TopologyConfig.DRPC_FUNCNAME, content);
    }

    private DRPCClient getDrpcClient(String host) throws TTransportException {
        Map<String, Object> conf = new HashMap<>();
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put("storm.nimbus.retry.times", 3);
        conf.put("storm.nimbus.retry.interval.millis", 10000);
        conf.put("storm.nimbus.retry.intervalceiling.millis", 10000);
        conf.put("drpc.max_buffer_size", 10485760);
        return new DRPCClient(conf, host, 3772, 30000);
    }

    @Test
    public void testDrpcProd() throws Exception {
        DRPCClient drpcClient = getDrpcClient("192.168.83.66");
        try {
            IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).forEach(new IntConsumer() {
                @Override
                public void accept(int value) {
                    try {
                        String params = "hello, world";
                        String result = drpcClient.execute("exclamation", params);
                        System.out.println("#############" + result);
                        Assert.assertEquals(params + "!", result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            drpcClient.close();
        }
    }


}
