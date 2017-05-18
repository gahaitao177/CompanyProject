package com.caiyi.financial.nirvana.ccard.bill.mail.util;

import com.caiyi.financial.nirvana.core.util.SystemConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaService {
    private static Logger logger = LoggerFactory.getLogger(KafkaService.class);
//
//    private static Producer<String, String> producer;
//
//    static {
//        Properties proper = new Properties();
//        // kafka 地址
//        proper.setProperty("metadata.broker.list", SystemConfig.get("kafka.metadata.broker.list"));
//        proper.setProperty("request.required.acks", SystemConfig.get("kafka.request.required.acks"));
//        proper.setProperty("serializer.class", SystemConfig.get("kafka.serializer.class"));
//        ProducerConfig config = new ProducerConfig(proper);
//        producer = new Producer<>(config);
//    }

    public static boolean pushToTopic(String params) {
        String topic = LocalConfig.getString("kafka.email.import.task");
        logger.info("mail:KafkaService pushToTopic---------" + topic + "----------start");

        try {
            Properties proper = new Properties();
            // kafka 地址
            proper.setProperty("metadata.broker.list", LocalConfig.getString("kafka.metadata.broker.list"));
            proper.setProperty("request.required.acks", LocalConfig.getString("kafka.request.required.acks"));
            proper.setProperty("serializer.class", LocalConfig.getString("kafka.serializer.class"));
            ProducerConfig config = new ProducerConfig(proper);
            Producer<String, String> producer = new Producer<String, String>(config);
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, params);
            producer.send(data);
            producer.close();
//            KeyedMessage<String, String> data = new KeyedMessage<>(topic, params);
//            producer.send(data);
        } catch (Exception e) {
            logger.error("Kafka发送信息错误:", e);
            return false;
        }
        logger.info("BaseImpl:pushToTopic----end");
        return true;
    }

}
