package com.caiyi.nirvana.analyse.test;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.LinearDRPCTopologyBuilder;

/**
 * Created by been on 2017/2/22.
 */
public class TestDrpcTopology {
    public static void main(String[] args) throws Exception {
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("exclamation");
        int workerNum = 1;
        try {
            if (args != null && args.length > 0) {
                workerNum = Integer.parseInt(args[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.addBolt(new ExclaimBolt(), workerNum * 2);

        Config conf = new Config();
        String topologyName = "Drpc-Test-Topology";
        conf.setNumWorkers(workerNum);
        String JVM_OPTS = "-Xms512m -Xmx512m -XX:NewRatio=3 -XX:SurvivorRatio=8 -XX:+PrintTenuringDistribution -XX:+DisableExplicitGC";

        conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, JVM_OPTS);
        conf.put(Config.TOPOLOGY_ACKER_EXECUTORS, 0);

        StormSubmitter.submitTopologyWithProgressBar(topologyName, conf, builder.createRemoteTopology());

    }
}
