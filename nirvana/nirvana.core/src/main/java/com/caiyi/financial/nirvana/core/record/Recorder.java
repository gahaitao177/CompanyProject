package com.caiyi.financial.nirvana.core.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mario on 2016/11/30 0030.
 * Recorder工厂
 */
@SuppressWarnings("unused")
public abstract class Recorder implements IRecorder{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取KafkaRecorder实例
     * @return Recorder
     */
    public static Recorder getKafkaRecorder(String topic, String kafkaBrokerList){
        return new KafkaRecorder(topic,kafkaBrokerList);
    }
}
