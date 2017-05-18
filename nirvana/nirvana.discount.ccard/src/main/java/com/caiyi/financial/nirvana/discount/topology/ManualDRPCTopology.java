package com.caiyi.financial.nirvana.discount.topology;

import com.caiyi.financial.nirvana.core.util.StormUtil;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.topology.TopologyBuilder;


public class ManualDRPCTopology {
	//bin/storm jar deploy/nirvana.discount.ccard-1.0.0-SNAPSHOT.jar com.caiyi.financial.nirvana.discount.topology.ManualDRPCTopology drpc_test_service
	public static void main(String[] args) {
		StormUtil.DrpcConfig conf = StormUtil.DrpcConfig.newInstance();
		System.out.println("--------------------"+conf.getDrcpService());
		TopologyBuilder builder = new TopologyBuilder();
		try {

			DRPCSpout drpcSpout = new DRPCSpout(conf.getDrcpService());
			StormUtil.buildTopolpoly(builder, drpcSpout, conf);
			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
