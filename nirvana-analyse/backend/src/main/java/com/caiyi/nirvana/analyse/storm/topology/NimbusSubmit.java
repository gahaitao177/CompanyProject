package com.caiyi.nirvana.analyse.storm.topology;

import com.caiyi.nirvana.analyse.common.config.TopologyConfig;
import com.caiyi.nirvana.analyse.env.Profile;
import com.caiyi.nirvana.analyse.storm.bolts.DBBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.KillOptions;
import org.apache.storm.kafka.*;
import org.apache.storm.shade.org.json.simple.JSONValue;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huashao on 2016/12/7.
 */
public class NimbusSubmit {

    public static void buildTopology(TopologyBuilder builder, KafkaSpout kafkaSpout) {
        String kafkaSpoutId = "nirvana-kafka-spout";
        String dbBoltId = "db-bolt";
        builder.setSpout(kafkaSpoutId, kafkaSpout);
        builder.setBolt(dbBoltId, new DBBolt(), 4).shuffleGrouping(kafkaSpoutId);
    }

    public static KafkaSpout buildKafkaSpout() {
        boolean prod = Profile.instance.isProd();
        String kafkaZkRoot = "/dcos-service-kafka/kafka-spout-root";
        String brokerZkUrl = "192.168.1.55";
        if (prod) {
            brokerZkUrl = "192.168.83.11";
        }
        String topicName = "nirvana_analyses_topic";
//        String topicName = "test2";
        String brokerZkPath = "/dcos-service-kafka/brokers";
        BrokerHosts hosts = new ZkHosts(brokerZkUrl, brokerZkPath);
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, kafkaZkRoot, "nirvana-analyse-spout");
        spoutConfig.zkPort = 2181;
        List<String> zkServers = new ArrayList<>();
        zkServers.add(brokerZkUrl);
        spoutConfig.zkServers = zkServers;
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        return kafkaSpout;
    }

    public static void main(String[] args) {
        String topologyFile = args[0];
        TopologyBuilder builder = new TopologyBuilder();
        KafkaSpout kafkaSpout = buildKafkaSpout();
        buildTopology(builder, kafkaSpout);
        Map stormConfig = Utils.readStormConfig();
        NimbusClient nimbus = NimbusClient.getConfiguredClient(stormConfig);

        try {
            //nimbus = new NimbusClient(stormConfig, nimbusNode, 6627);
            String submittedJar = null;
            String jsonConfig = null;

            try {
                /**
                 * 提交启动拓扑
                 */
                submittedJar = StormSubmitter.submitJar(stormConfig, topologyFile);
                jsonConfig = JSONValue.toJSONString(stormConfig);

                /**
                 * 提交topologyDAG及序列化后的配置信息serconf(json)
                 */
                nimbus.getClient().submitTopology(TopologyConfig.TOPOLOGY_NAME, submittedJar, jsonConfig, builder.createTopology());
            } catch (AlreadyAliveException e) {
                try {
                    KillOptions killOptions = new KillOptions();
                    killOptions.set_wait_secs(10);
                    nimbus.getClient().killTopologyWithOpts(TopologyConfig.TOPOLOGY_NAME, killOptions);

                    Thread.sleep(10000);
                    /**
                     * 提交topologyDAG及序列化后的配置信息serconf(json)
                     */
                    nimbus.getClient().submitTopology(TopologyConfig.TOPOLOGY_NAME, submittedJar, jsonConfig, builder.createTopology());
                } catch (TException e1) {
                    System.err.println("An error occured submitting the topology.");
                    e1.printStackTrace();
                    System.exit(1);
                } catch (InterruptedException e1) {
                    System.err.println("An error occured submitting the topology.");
                    e1.printStackTrace();
                    System.exit(1);
                }
                System.out.println("An instance of the topology successfully restarted");
                System.exit(0);
                ;
            } catch (InvalidTopologyException e) {
                System.err.println("The topology is invalid.");
                System.exit(1);
            } catch (TException e) {
                System.err.println("An error occured submitting the topology.");
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                System.err.println("An error occured submitting the topology.");
                e.printStackTrace();
                System.exit(1);
            } finally {
                if (nimbus != null) {
                    nimbus.close();
                }
            }
        } catch (Exception e) {
            System.err.println("There was an error connecting to the Nimbus host node.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Topology successfully submitted.");
        System.exit(0);
    }
}
