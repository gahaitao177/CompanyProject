package com.caiyi.financial.nirvana.core.record;

/**
 * Created by Mario on 2016/11/30 0030.
 * 记录器demo
 * 记录，分析约定好主题
 */
@SuppressWarnings("unused")
public class RecordDemo {
    public static void main(String[] args) {
        Recorder recorder = Recorder.getKafkaRecorder("记录主题","kafka地址");
        recorder.record(new Object());
    }
}
