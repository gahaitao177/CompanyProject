package com.caiyi.nirvana.analyse.kafka;


import org.junit.Test;

import java.util.stream.IntStream;

/**
 * Created by been on 2017/2/20.
 */
public class KafkaServiceTest {

    @Test
    public void testProducerLocal() throws Exception {
        KafkaService kafkaService = new KafkaService("localhost:9042");
        String topic = "test";
        sendMessage(kafkaService, topic);
    }


    @Test
    public void testProducerDev() throws Exception {
        System.out.println("test produce message  dev ......");
        String brokerUrl = "192.168.1.88:9460";
        KafkaService kafkaService = new KafkaService(brokerUrl);
        String topic = "nirvana_analyses";
//        String topic = "test2";
        sendMessage(kafkaService, topic);
    }

    @Test
    public void testProducerProd() throws Exception {
        System.out.println("test produce prod .......");
        String brokerUrl = "192.168.83.24:9400";
        KafkaService kafkaService = new KafkaService(brokerUrl);
        String topic = "been";
        sendMessage(kafkaService, topic);
    }

    @Test
    public void createTopic() throws Exception {

    }

    private void sendMessage(KafkaService kafkaService, String topic) throws InterruptedException {

        String msg = "test1-------->";
        IntStream.of(1, 2).forEach(value -> kafkaService.sendMessage(topic, msg + value));

        Thread.sleep(5000);
    }

}