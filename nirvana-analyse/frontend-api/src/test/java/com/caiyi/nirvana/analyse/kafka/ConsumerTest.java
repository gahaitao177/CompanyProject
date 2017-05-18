package com.caiyi.nirvana.analyse.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by been on 2017/2/20.
 */
public class ConsumerTest {
    public static void main(String[] args) throws Exception {
        testDCOSDev();
//        testDCOSProd();
    }


    public static void testDCOSDev() {
        System.out.println("test consume message  dev........");
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.88:9460");

//        _test(props, "nirvana_analyses");
        _test(props, "test2");
    }

    public static void testDCOSProd() {
        System.out.println("test prod............");
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.83.24:9400");
        _test(props, "been");
    }


    private static void testLocal() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        _test(props, "test2");
    }

    private static void _test(Properties props, String topic) {
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("#################offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
                System.out.println();
            }
        }
    }
}
