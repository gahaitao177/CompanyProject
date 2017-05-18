package com.caiyi.nirvana.analyse.monitor.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by been on 2017/3/8.
 */
public class ProducerTest {
    @Test
    public void test() {
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", "192.168.1.88:9460");
//        props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

//            String topic = "test2";
            String topic = "nirvana_monitor";
            Producer<String, String> producer = new KafkaProducer<>(props);
            for (int i = 2; i < 4; i++) {
                producer.send(new ProducerRecord<>(topic, Integer.toString(i), Integer.toString(i)));
            }
            producer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
