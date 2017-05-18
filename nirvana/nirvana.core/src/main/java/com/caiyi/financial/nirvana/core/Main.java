package com.caiyi.financial.nirvana.core;

import com.caiyi.financial.nirvana.core.util.SpringFactory;
import com.caiyi.financial.nirvana.core.util.StormUtil;
import com.caiyi.financial.nirvana.heartbeat.server.spout.DynamicDRPCSpout;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wenshiliang on 2016/6/21.
 */
public class Main {
    //./bin/storm jar deploy/nirvana.ccard.ccardinfo-1.0.0-SNAPSHOT.jar com.caiyi.financial.nirvana.core.Main
    //./bin/storm jar deploy/nirvana.discount.user-1.0.0-SNAPSHOT.jar com.caiyi.financial.nirvana.core.Main
    //./bin/storm jar deploy/nirvana.discount.user-1.0.0-SNAPSHOT.jar com.caiyi.financial.nirvana.core.Main
    public static void main(String[] args) {
        StormUtil.DrpcConfig conf = StormUtil.DrpcConfig.newInstance();

        if(!conf.containsKey(Config.TOPOLOGY_ACKER_EXECUTORS)){
            conf.put(Config.TOPOLOGY_ACKER_EXECUTORS,0);
        }
        // TODO: 2016/11/18 硬代码，先用着。测试环境不设置可用内存
        if(!"192.168.1.207".equals(SpringFactory.getAddressIp())){
            if(!conf.containsKey(Config.TOPOLOGY_WORKER_CHILDOPTS)){
                conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS,"-Xmx3072m -XX:NewRatio=3 -XX:SurvivorRatio=8 " +
                        "-XX:+PrintTenuringDistribution -XX:+DisableExplicitGC");
            }
        }else{
            System.out.println("测试环境：不设置 TOPOLOGY_WORKER_CHILDOPTS");
        }


        String drcpService = conf.getDrcpService();
        TopologyBuilder builder = new TopologyBuilder();
        DRPCSpout drpcSpout = new DynamicDRPCSpout(drcpService);
        StormUtil.buildTopolpoly(builder, drpcSpout, conf);

        try {
            StormSubmitter.submitTopologyWithProgressBar(drcpService, conf, builder.createTopology());
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        } catch (InvalidTopologyException e) {
            e.printStackTrace();
        } catch (AuthorizationException e) {
            e.printStackTrace();
        }
    }
}
