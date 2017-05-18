package com.caiyi.financial.nirvana.core;

import com.caiyi.financial.nirvana.core.util.SpringFactory;
import com.caiyi.financial.nirvana.core.util.StormUtil;
import com.caiyi.financial.nirvana.heartbeat.server.spout.DynamicDRPCSpout;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.KillOptions;
import org.apache.storm.shade.org.json.simple.JSONValue;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Utils;

import java.util.Map;

/**
 * Created by huashao on 2016/12/7.
 */
public class NimbusSubmit {

    public static void main(String[] args) {
        StormUtil.DrpcConfig conf = StormUtil.DrpcConfig.newInstance();

        if(!conf.containsKey(Config.TOPOLOGY_ACKER_EXECUTORS)){
            conf.put(Config.TOPOLOGY_ACKER_EXECUTORS,0);
        }
        //// TODO: 2016/11/18 硬代码，先用着。测试环境不设置可用内存
        if(!"192.168.1.207".equals(SpringFactory.getAddressIp())){
            if(!conf.containsKey(Config.TOPOLOGY_WORKER_CHILDOPTS)){
                conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, "-Xmx3072m -XX:NewRatio=3 -XX:SurvivorRatio=8 " +
                        "-XX:+PrintTenuringDistribution -XX:+DisableExplicitGC");
            }
        }else{
            System.out.println("测试环境：不设置 TOPOLOGY_WORKER_CHILDOPTS");
        }

        String topologyFile = args[0];

        String drcpService = conf.getDrcpService();
        TopologyBuilder builder = new TopologyBuilder();
        DRPCSpout drpcSpout = new DynamicDRPCSpout(drcpService);
        StormUtil.buildTopolpoly(builder, drpcSpout, conf);


        /**
         * 集群中的storm.yaml配置文件必须在classpath下(src/main/resources)
         * 根据配置初始化一个Nimbus.Client
         */
        Map stormConfig = Utils.readStormConfig();
        NimbusClient nimbus = NimbusClient.getConfiguredClient(stormConfig);

        //stormConfig.put("nimbus.host", nimbusNode);
        //stormConfig.put("topology.acker.executors", 1);
        //NimbusClient nimbus;

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
                nimbus.getClient().submitTopology(drcpService, submittedJar, jsonConfig, builder.createTopology());
            } catch(AlreadyAliveException e) {
                try {
                    KillOptions killOptions = new KillOptions();
                    killOptions.set_wait_secs(90);
                    nimbus.getClient().killTopologyWithOpts(drcpService, killOptions);

                    Thread.sleep(10000);
                    /**
                     * 提交topologyDAG及序列化后的配置信息serconf(json)
                     */
                    nimbus.getClient().submitTopology(drcpService, submittedJar, jsonConfig, builder.createTopology());
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
                System.exit(0);;
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
            }finally{
                if(nimbus != null){
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
