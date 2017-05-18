package com.caiyi.financial.nirvana.core.client;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.util.StormUtil;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 *
 * Created by wsl on 2016/1/7.
 * 本地drpc clent
 * 依赖StormUtil类
 */
public class LocalDrpcClient implements IDrpcClient {

    public LocalDRPC drpc;
    private String drcpService;

    public LocalDrpcClient() {
        if(drpc==null){
            StormUtil.DrpcConfig conf = StormUtil.DrpcConfig.newInstance();
            drcpService = conf.getDrcpService();
            TopologyBuilder builder = new TopologyBuilder();
            drpc = new LocalDRPC();
            DRPCSpout drpcSpout = new DRPCSpout(drcpService, drpc);
            StormUtil.buildTopolpoly(builder, drpcSpout, conf);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("manual-drpc-demo", conf, builder.createTopology());
        }
    }

    @Override
    public String getDrpcService() {
        return drcpService;
    }

    public String execute(DrpcRequest drpcRequest) {
        return drpc.execute(drcpService, drpcRequest.toRequest());
    }

    @Override
    public <T> T execute(DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcRequest),clazz);
    }

    @Override
    public String execute(String drpcService, DrpcRequest drpcRequest) {
       return execute(drpcRequest);
    }

    @Override
    public <T> T execute(String drpcService, DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcRequest),clazz);
    }

    @Override
    public void close() {
        System.out.println("未实现");
    }

}
