package com.caiyi.financial.nirvana.bill.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mario on 2015/10/30.
 */
public class ConsumerGroup {
    /**
     *
     */

    private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;
    private String type;
    public Logger logger = LoggerFactory.getLogger(ConsumerGroup.class);

    public ConsumerGroup(String a_zookeeper, String a_groupId, String a_topic, String a_type) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));
        topic = a_topic;
        type = a_type;
    }

    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        if (executor != null) {
            try {
                if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                    System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted during shutdown, exiting uncleanly");
            }
        }
    }

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    public void run(int a_numThreads) {
        logger.info("ConsumerGroup:run:start---" + a_numThreads);
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, a_numThreads);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        logger.info("ConsumerGroup:run:streams size---" + streams.size());
        executor = Executors.newFixedThreadPool(a_numThreads);
        int threadNumber = 0;
        for (final KafkaStream<byte[], byte[]> stream : streams) {
            Thread t = new Thread(new KafkaConsumer(stream, threadNumber, type));
//            t.setDaemon(true);
            executor.execute(t);
            threadNumber++;
        }
    }


    public static void main(String[] args) {
//        System.out.print("WebSocketServer.getOnlineCount()="+ WebSocketServer.getOnlineCount());

        String zookeeper = "192.168.1.55:2181";
        String groupId = "1";
        String topic = "topic_notification_billResult";
        int threads = 1;

        ConsumerGroup example = new ConsumerGroup(zookeeper, groupId, topic, "");
        example.run(threads);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {

        }
        //example.shutdown();
    }
}
