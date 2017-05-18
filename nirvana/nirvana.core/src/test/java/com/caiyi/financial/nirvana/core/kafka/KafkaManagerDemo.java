package com.caiyi.financial.nirvana.core.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import java.util.List;

/**
 * Created by Mario on 2016/11/30 0030.
 * kafka例子
 */
@SuppressWarnings("unused")
public class KafkaManagerDemo {
    public static void main(String[] args) throws InterruptedException {
        String topic = "billSessionTopic";
        String zkConnString = "192.168.1.55:2181,192.168.1.61:2181,192.168.1.69:2181/dcos-service-kafka";
        String kafkaBrokers = "192.168.1.71:9164,192.168.1.73:9312,192.168.1.88:9460";

        //创建主题
        KafkaTopicManager manager = new KafkaTopicManager(zkConnString);
        manager.createTopic(topic,3,2);
        //消费主题
        KafkaConsumer consumer = new KafkaConsumer(zkConnString);
        List<KafkaStream<byte[],byte[]>> stream = consumer.getKafkaStreams(
                topic,
                "testGroup",
                3);
        for(KafkaStream<byte[],byte[]> item : stream ){
            Runnable task = ()->{
                System.out.println(Thread.currentThread().getName());
                ConsumerIterator<byte[], byte[]> it = item.iterator();
                while (it.hasNext()) {
                    System.out.println(Thread.currentThread().getName() + " 收到消息:" + new String(it.next().message()));
                }
            };
            new Thread(task).start();
        }
        //发送消息
        KafkaProducer producer = new KafkaProducer(kafkaBrokers);
        producer.sendMessage(topic, "这是测试消息123");

        Thread.sleep(5000);
    }
}
