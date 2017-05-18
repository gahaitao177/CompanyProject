package com.caiyi.financial.nirvana.heartbeat.client;

import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by been on 2016/10/19.
 */
public class DRPCClientPool {

    public static Logger logger = LoggerFactory.getLogger(DRPCClientPool.class);
    public static String SERVER_SEEDS = "server_seeds";

    /**
     * @param config 唯一的thread unsafe
     * @return
     * @throws Exception
     */
    public static DRPCClient getResouce(Map<String, List<String>> config) throws Exception {

        List<String> initialServers = config.get(SERVER_SEEDS);
        if (initialServers == null || initialServers.size() == 0) {
            throw new RuntimeException("必须提供drpc server[ip:host] 连接信息");
        }
        Collections.shuffle(initialServers);
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put("storm.nimbus.retry.times", 3);
        conf.put("storm.nimbus.retry.interval.millis", 3000);
        conf.put("storm.nimbus.retry.intervalceiling.millis", 1000);
        conf.put("drpc.max_buffer_size", 1048576);
        for (String server : initialServers) {
            try {
                String[] hostPort = server.split(":");
                DRPCClient drpcClient = new DRPCClient(conf, hostPort[0], Integer.parseInt(hostPort[1]));
                logger.info("通过 " + server + " 获取DrpcClient成功");
                return drpcClient;
            } catch (Exception e) {
                logger.info("drpc server " + server + "异常, 无法建立连接");
                continue;
            }
        }
        throw new Exception("无法根据提供的drpc servers 资源池建立drpcclient 连接 -->" +
                initialServers.stream().reduce("", (s, s2) -> {
                    if (s.equals("")) {
                        return s + s2;

                    } else {
                        return s + "," + s2;
                    }
                }).toString());
    }

}
