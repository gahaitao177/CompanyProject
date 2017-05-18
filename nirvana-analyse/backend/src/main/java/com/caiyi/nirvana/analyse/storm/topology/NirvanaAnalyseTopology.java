package com.caiyi.nirvana.analyse.storm.topology;

import com.caiyi.nirvana.analyse.common.config.TopologyConfig;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;


public class NirvanaAnalyseTopology {
    public static void main(String[] args) throws Exception {
        deployTopology(args);
    }

    private static void deployTopology(String[] args) throws InterruptedException {
        TopologyBuilder builder = new TopologyBuilder();
        Config conf = new Config();
        String topologyName = TopologyConfig.TOPOLOGY_NAME;
        KafkaSpout kafkaSpout = NimbusSubmit.buildKafkaSpout();
        NimbusSubmit.buildTopology(builder, kafkaSpout);
        LocalCluster cluster = new LocalCluster();
        //自定义公积金拓扑jvm opts,与storm.yaml中一起作为woker的jvm opts
        String JVM_OPTS = "-Xms2048m -Xmx2048m -XX:NewRatio=3 -XX:SurvivorRatio=8 -XX:+PrintTenuringDistribution -XX:+DisableExplicitGC";
        if (args == null || args.length == 0) {
            cluster.submitTopology(topologyName, conf, builder.createTopology());
            System.out.println("topology submit success");
            Thread.sleep(30000000);
            cluster.shutdown();
        } else {
            try {
                conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, JVM_OPTS);
                conf.put(Config.TOPOLOGY_ACKER_EXECUTORS, 0);
                conf.setNumWorkers(4);
                StormSubmitter.submitTopologyWithProgressBar(topologyName, conf, builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
