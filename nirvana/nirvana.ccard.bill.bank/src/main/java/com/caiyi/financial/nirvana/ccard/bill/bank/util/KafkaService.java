package com.caiyi.financial.nirvana.ccard.bill.bank.util;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaService {
    private static Logger logger = LoggerFactory.getLogger(KafkaService.class);
//    private static String KAFKA_ADRESS_HSK = "192.168.83.36:9092,192.168.83.37:9092,192.168.83.38:9092";
//private static String KAFKA_ADRESS_HSK = "192.168.1.88:1027,192.168.1.71:1026,192.168.1.89:1026";
//    private static String SCLASS = "kafka.serializer.StringEncoder";
//    private static String ACKS = "1";
//    private static String EMAIL_RECEIVE_TOPIC = "topic_billImport_mailTask";
//    public static String EMAIL_TASK_TOPIC = "topic_email_task";

    public static boolean pushToTopic(String params) {
        logger.info("mail:KafkaService pushToTopic----start");
        try {
            Properties proper = new Properties();
            // kafka 地址
            proper.setProperty("metadata.broker.list", LocalConfig.getString("kafka.metadata.broker.list"));
            proper.setProperty("request.required.acks", LocalConfig.getString("kafka.request.required.acks"));
            proper.setProperty("serializer.class", LocalConfig.getString("kafka.serializer.class"));
            ProducerConfig config = new ProducerConfig(proper);
            Producer<String, String> producer = new Producer<String, String>(config);
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(LocalConfig.getString("kafka.bank.import.task"), params);
            producer.send(data);
            producer.close();
        } catch (Exception e) {
            logger.error("Kafka发送信息错误:", e);
            return false;
        }
        logger.info("BaseImpl:pushToTopic----end");
        return true;
    }

    public static boolean pushToTopic(String topic, String params) {
        logger.info("BaseImpl:pushToTopic----start");
        try {
            Properties proper = new Properties();
            // kafka 地址
            proper.setProperty("metadata.broker.list", LocalConfig.getString("kafka.metadata.broker.list"));
            proper.setProperty("serializer.class", LocalConfig.getString("kafka.serializer.class"));
            proper.setProperty("request.required.acks", LocalConfig.getString("kafka.request.required.acks "));
            ProducerConfig config = new ProducerConfig(proper);
            Producer<String, String> producer = new Producer<String, String>(config);
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, params);
            producer.send(data);
            producer.close();
        } catch (Exception e) {
            logger.error("Kafka发送信息错误:", e);
            return false;
        }
        logger.info("BaseImpl:pushToTopic----end");
        return true;
    }

}
