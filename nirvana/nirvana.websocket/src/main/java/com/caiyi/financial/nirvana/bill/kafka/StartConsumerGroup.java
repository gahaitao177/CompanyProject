package com.caiyi.financial.nirvana.bill.kafka;

import com.caiyi.financial.nirvana.core.util.SpringFactory;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Servlet implementation class test
 */

public class StartConsumerGroup implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartConsumerGroup.class);

    private ConsumerGroup bank = null;
    private ConsumerGroup mail = null;
    private ConsumerGroup message = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        LOGGER.info("StartConsumerGroup监听器启动，监听网银，邮箱账单导入情况");
        try {
            initKafka();
        } catch (Exception e) {
            for (int i = 0; i < 10; i++) {
                LOGGER.error("StartConsumerGroup初始化异常\n\n\n\n\n\n");
            }
            LOGGER.error("", e);
        }
        LOGGER.info("------------------");

    }


    public void initKafka() throws Exception {
        String ip = "local";
//		try {
        ip = SpringFactory.getAddressIp();
        String websocket_port = SystemConfig.get("websocket_port");
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
        String zkAdress = SystemConfig.get("zookeeper.connect");
//		 	if ("1".equals(BaseImpl.isDevelopEnv())){
//		 		zkAdress = BaseImpl.ZOOKEEPER_ADDRESS_TEST;
//		 	}

        String bank_group = SystemConfig.get("zookeeper.bill.bank.group") + ip + "_" + websocket_port;
        bank = new ConsumerGroup(zkAdress, bank_group,
                SystemConfig.get("zookeeper.bill.bank.topic"), "");
        bank.run(SystemConfig.getInt("zookeeper.bill.bank.run"));

        String mail_group = SystemConfig.get("zookeeper.bill.mail.group") + ip + "_" + websocket_port;
        mail = new ConsumerGroup(zkAdress, mail_group,
                SystemConfig.get("zookeeper.bill.mail.topic"), "mail");
        mail.run(SystemConfig.getInt("zookeeper.bill.mail.run"));

        String message_group = SystemConfig.get("zookeeper.bill.message.group") + ip + "_" + websocket_port;
        message = new ConsumerGroup(zkAdress, message_group,
                SystemConfig.get("zookeeper.bill.message.topic"), "message");
        message.run(SystemConfig.getInt("zookeeper.bill.message.run"));

        LOGGER.info(bank_group);
        LOGGER.info(mail_group);
        LOGGER.info(message_group);
        LOGGER.info("------------------");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (bank != null) {
            bank.shutdown();
        }
        if (mail != null) {
            mail.shutdown();
        }
        if (message != null) {
            message.shutdown();
        }
        System.out.println("--------------------------------------------- StartConsumerGroup contextDestroyed");

    }

}


