package com.caiyi.financial.nirvana.ccard.investigation;

import com.caiyi.financial.nirvana.ccard.investigation.bolt.DispatcherBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by been on 2016/12/30.
 */
public class InvestigationTopology {
    public static String JVM_OPTS = "-Xmx3072m";

    public static void main(String[] args) {
        try {
            Config config = new Config();
            if (args != null && args.length == 2) {
                config.setNumWorkers(1);
                config.put(Config.TOPOLOGY_WORKER_CHILDOPTS, JVM_OPTS);
                String topologName = args[0];
                String drpcFuncName = args[1];
                DRPCSpout drpcSpout = new DRPCSpout(drpcFuncName);

                StormTopology topology = buildTopology(drpcSpout);
                StormSubmitter.submitTopologyWithProgressBar(topologName, config, topology);
            } else {
                LocalCluster cluster = new LocalCluster();
                String topologyName = InvestigationTopology.class.getSimpleName();
                String drpcFuncName = InvestigationTopology.class.getSimpleName();
                LocalDRPC localDRPC = new LocalDRPC();
                DRPCSpout drpcSpout = new DRPCSpout(drpcFuncName, localDRPC);
                StormTopology topology = buildTopology(drpcSpout);
                cluster.submitTopology(topologyName, config, topology);
                System.out.println("拓扑构造成功");
                String result = localDRPC.execute(drpcFuncName, "hello");
                System.out.println("storm drpc  本地模式测试结构 --------->  " + result);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static StormTopology buildTopology(DRPCSpout drpcSpout) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(DRPCSpout.class.getSimpleName(), drpcSpout);
        builder.setBolt(DispatcherBolt.class.getSimpleName(), new DispatcherBolt())
                .shuffleGrouping(DRPCSpout.class.getSimpleName());
        builder.setBolt(ReturnResults.class.getSimpleName(), new ReturnResults(), 4)
                .shuffleGrouping(DispatcherBolt.class.getSimpleName());
        return builder.createTopology();
    }
}
