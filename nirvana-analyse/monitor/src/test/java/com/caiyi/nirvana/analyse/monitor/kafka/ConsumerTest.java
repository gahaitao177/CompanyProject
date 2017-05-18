package com.caiyi.nirvana.analyse.monitor.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by been on 2017/3/8.
 */

public class ConsumerTest{
//    public static void main(String[] args) throws Exception{

    @Test
    public void test() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.88:9460");
        props.put("group.id", "test-been");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        String topic = "test2";
        String topic = "nirvana_monitor";
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("#################offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
                System.out.println("");
            }
        }
    }
}
