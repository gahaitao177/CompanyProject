package com.caiyi.financial.nirvana.core.client;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.exception.ClientException;
import org.apache.storm.thrift.TException;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsl on 2016/1/7.
 * 远程drpc client
 */
@Deprecated
public class RemoteDrpcClient implements IDrpcClient {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(RemoteDrpcClient.class);

    DRPCClient client = null;
    private String drpcService;
    private String drpcIp;
    private int drpcPort;

    @Override
    public String getDrpcService() {
        return drpcService;
    }

    public static Map<String,Object> getDefaultConf(){
        Map<String, Object> conf = new HashMap<>();

        conf.put("storm.thrift.transport", "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put("storm.nimbus.retry.times", 3);
        conf.put("storm.nimbus.retry.interval.millis", 3000);
        conf.put("storm.nimbus.retry.intervalceiling.millis", 3000);
        conf.put("drpc.max_buffer_size", 104857600);

        return conf;
    }

    public RemoteDrpcClient(String drpcIp,int drpcPort,String drpcService) {
            this.drpcService = drpcService;
            this.drpcIp = drpcIp;
            this.drpcPort = drpcPort;
    }

    @Override
    public String execute(DrpcRequest drpcRequest) {
        return execute(this.drpcService,drpcRequest);
    }

    @Override
    public <T> T execute(DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcRequest),clazz);
    }

    @Override
    public String execute(String drpcService, DrpcRequest drpcRequest) {
        String result = null;
        DRPCClient client = null;
        try {
            long start = System.currentTimeMillis();
            String str = drpcRequest.toRequest();
            logger.debug("storm:{},request:{}", drpcService, str);
            client = new DRPCClient(getDefaultConf(), drpcIp, drpcPort);
            result = client.execute(drpcService, str);
            logger.info("耗时: " + (System.currentTimeMillis() - start));
            logger.debug(result);
        } catch (TException e) {
            logger.error("drpc请求失败,服务器异常", e);
            throw new ClientException(BoltResult.Error_405, "服务器异常!!!");
        }finally {
            if(client!=null){
                
                client.close();
            }
        }
        return result;
    }

    @Override
    public <T> T execute(String drpcService, DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcService,drpcRequest),clazz);
    }

    @Override
    public void close() {
        client.close();
    }
}
