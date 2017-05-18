package com.caiyi.financial.nirvana.heartbeat.server;

import com.caiyi.financial.nirvana.heartbeat.Constant;
import com.caiyi.financial.nirvana.heartbeat.server.bolt.HeartbeatBolt;
import com.caiyi.financial.nirvana.heartbeat.server.spout.DynamicDRPCSpout;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wenshiliang on 2016/10/11.
 */
// ./bin/storm jar deploy/heartbeat-1.0.0-SNAPSHOT.jar com.caiyi.financial.nirvana.heartbeat.server.Main
public class Main {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        String drcpService = Constant.DRPC_SERVICE;
        TopologyBuilder builder= new TopologyBuilder();

        Config conf =   new Config();
        conf.setDebug(false);
        conf.setNumWorkers(1);

        IRichSpout drpcSpout = new DynamicDRPCSpout(drcpService);
        builder.setSpout("drpcSpout",drpcSpout,2);


        builder.setBolt("heartbeatBolt", new HeartbeatBolt(),2)
                .shuffleGrouping("drpcSpout");
        builder.setBolt("return", new ReturnResults(), 2).shuffleGrouping("heartbeatBolt");

        StormSubmitter.submitTopologyWithProgressBar(drcpService, conf, builder.createTopology());

    }
}
