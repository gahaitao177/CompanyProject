package com.caiyi.financial.nirvana.core.event;

import cn.aofeng.event4j.AbstractEventListener;
import cn.aofeng.event4j.Event;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

/**
 * Created by terry on 2016/9/20.
 */
public class SendLogs extends AbstractEventListener<LogInfo>{
    private static KafkaProducer kafkaProducer = new KafkaProducer();


    @Override
    public void execute(Event<LogInfo> event) {
        LogInfo info = event.getData();
        System.out.println("cuserid="+info.getCuserId());
        kafkaProducer.sendJsonString(info);

    }

    static class KafkaProducer {
        private Producer<String, String> producer;
        private String kafkaTopic;
        private Object obj = new Object();

        public KafkaProducer() {
            initProducer();
        }

        public void sendJsonString(Object obj) {
            String str = toJSONString(obj);
            KeyedMessage<String, String> data = new KeyedMessage<>(kafkaTopic, str);
            producer.send(data);
        }

        public String toJSONString(Object obj) {
            return JSONObject.toJSONString(obj, SerializerFeature.WriteEnumUsingToString, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteDateUseDateFormat);

        }

        private void initProducer() {
            synchronized (obj) {
                if (producer != null) {
                    producer.close();
                }
                kafkaTopic = SystemConfig.get("apply_listener.apply.listener.topic");
//                kafkaTopic = "test2";
                Properties proper = new Properties();
                proper.setProperty("metadata.broker.list", SystemConfig.get("apply_listener.metadata.broker.list"));
                proper.setProperty("serializer.class", SystemConfig.get("apply_listener.serializer.class"));
                proper.setProperty("request.required.acks", SystemConfig.get("apply_listener.request.required.acks"));
//                proper.setProperty("partitioner.class", "com.superwen0001.mixproject.kafka.MyPartitioner");

                ProducerConfig config = new ProducerConfig(proper);
                producer = new Producer<>(config);

            }
        }
    }
}
