package com.caiyi.nirvana.analyse.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Created by been on 2017/3/7.
 */
public class KafkaService {
    private String brokers;
    private Producer<String, String> producer;

    public KafkaService(String brokers) {
        this.brokers = brokers;
        initProducer();
    }

    private void initProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    public Future<RecordMetadata> sendMessage(String topic, String message) {
        Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topic, message));
        return future;
    }


}
