package com.caiyi.nirvana.analyse.monitor;


import com.caiyi.nirvana.analyse.enums.SystemEnum;
import com.caiyi.nirvana.analyse.model.MonitorEvent;
import com.caiyi.nirvana.analyse.monitor.mail.MailService;
import com.caiyi.nirvana.analyse.monitor.meters.MeterUnit;
import com.caiyi.nirvana.analyse.monitor.meters.MetersCallback;
import com.caiyi.nirvana.analyse.monitor.meters.MetersHandler;
import com.caiyi.nirvana.analyse.monitor.sms.SMSService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by been on 2017/3/7.
 */
public class MonitorService extends BaseService {

    private SMSService smsService;
    private MailService mailService;
    private String topic;
    private String groupId;
    private String borkers;

    public static Map<String, Boolean> enableMap = new HashMap<String, Boolean>();

    public MonitorService(SMSService smsService, MailService mailService, String topic,
                          String groupId, String brokers) {
        this.smsService = smsService;
        this.mailService = mailService;
        this.topic = topic;
        this.groupId = groupId;
        this.borkers = brokers;
    }

    /**
     * 启动kafka consumer
     *
     * @throws Exception
     */
    public void startUp(Properties properties) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", borkers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));

        for (SystemEnum se : SystemEnum.values()) {
            String code = se.getCode();
            String enableStr = properties.getProperty(code + ".enable");
            enableMap.put(code, Boolean.valueOf(enableStr));
        }

        String thresholdCount = properties.getProperty("meters.thresholdCount");
        String thresholdTime = properties.getProperty("meters.thresholdTime");
        String triggerRules = properties.getProperty("meters.triggerRules");

        String str[] = triggerRules.split(",");
        int[] triggerRulesArray = new int[str.length];
        for (int i = 0; i < str.length; i++)
            triggerRulesArray[i] = Integer.parseInt(str[i]);

        MetersHandler metersHandler = new MetersHandler(Integer.valueOf(thresholdCount), Integer.valueOf(thresholdTime), triggerRulesArray);//一分钟内4次
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                String data = record.value();
                try {
                    MonitorEvent monitorEvent = MonitorEvent.jsonString2Obj(data);
                    logger.info("收到数据: \n" + monitorEvent.toJSONString());

                    //触发后通知业务
                    metersHandler.setMetersCallback(new MetersCallback<MeterUnit>() {
                        @Override
                        public void report(MeterUnit meterUnit) {
                            logger.info("告警->第" + meterUnit.getRepeatedCount() + "次：" + meterUnit.toString());
                            //系统code
                            String systemCode = meterUnit.getMonitorEvent().getSystemCode();
                            //根据系统code获取配置
                            String phone = properties.getProperty(systemCode + ".sms.phone");
                            String inceptAddress = properties.getProperty(systemCode + ".mail.inceptAddress");
                            String title = properties.getProperty(systemCode + ".mail.title");
                            Boolean enable = enableMap.get(systemCode);
                            if (enable) {
                                String content =
                                        "系统：" + meterUnit.getMonitorEvent().getSystemName() +
                                                "；错误code：" + meterUnit.getMonitorEvent().getKey() +
                                                "；第" + meterUnit.getRepeatedCount() + "次告警" +
                                                "；日志：</br>" + meterUnit.getMonitorEvent().getContent();
//                                logger.info("发送消息");
                                //发送邮件
                                mailService.sendMsg(inceptAddress, title, content);
                                try {
//                                    发送短信
                                    smsService.sendSMS(phone,
                                            "系统：" + meterUnit.getMonitorEvent().getSystemName() +
                                                    "；错误code：" + meterUnit.getMonitorEvent().getKey() +
                                                    "；第" + meterUnit.getRepeatedCount() + "次告警。告警信息请查看邮件！");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    metersHandler.mark(monitorEvent);//计量
                } catch (Exception e) {
                    logger.info(e.getMessage(), e);
                }
            }
        }
    }
}
