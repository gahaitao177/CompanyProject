package com.caiyi.financial.nirvana.core.record;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.core.kafka.KafkaProducer;

/**
 * Created by Mario on 2016/11/30 0030.
 * kafka记录器
 */
@SuppressWarnings("unused")
class KafkaRecorder extends Recorder {
    //kafka记录主题
    private String topic;
    //kafka生产者
    private KafkaProducer producer;

    /**
     * kafka记录器
     *
     * @param kafkaBrokerList kafka brokers地址
     * @param topic           主题
     */
    public KafkaRecorder(String topic, String kafkaBrokerList) {
        this.topic = topic;
        this.producer = new KafkaProducer(kafkaBrokerList);
        logger.info("KafkaRecorder BrokerList:" + kafkaBrokerList);
    }

    /**
     * 记录到kafka
     *
     * @param data data
     */
    @Override
    public void record(Object data) {
        new Thread(()->{
            try {
                producer.sendMessage(topic, JSON.toJSONString(data));
            } catch (Exception e) {
                logger.error("记录出现异常:" + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
