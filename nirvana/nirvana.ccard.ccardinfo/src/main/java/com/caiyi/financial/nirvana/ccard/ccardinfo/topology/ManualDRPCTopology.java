package com.caiyi.financial.nirvana.ccard.ccardinfo.topology;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ManualDRPCTopology {
	//bin/storm jar deploy/nirvana1.discount.ccard-1.0.0-SNAPSHOT.jar com.caiyi.financial.nirvana.discount.topology.ManualDRPCTopology drpc_test_service
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml","spring-quartz.xml");
//		BankMapper mapper = context.getBean(BankMapper.class);
//		System.out.println(mapper.select(null));

//		context.getBean(BankImportService.class);
//		StormUtil.DrpcConfig conf = StormUtil.DrpcConfig.newInstance();
//		System.out.println("--------------------"+conf.getDrcpService());
//		TopologyBuilder builder = new TopologyBuilder();
//		try {
//			DRPCSpout drpcSpout = new DRPCSpout(conf.getDrcpService());
//			StormUtil.buildTopolpoly(builder, drpcSpout, conf);
//			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
}
