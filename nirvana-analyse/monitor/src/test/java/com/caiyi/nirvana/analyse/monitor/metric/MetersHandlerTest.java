package com.caiyi.nirvana.analyse.monitor.metric;


import com.caiyi.nirvana.analyse.enums.LevelEnum;
import com.caiyi.nirvana.analyse.enums.SystemEnum;
import com.caiyi.nirvana.analyse.model.MonitorEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by pc on 2017/3/9.
 */
public class MetersHandlerTest {
    public static Logger logger = LogManager.getLogger(MetersHandlerTest.class);
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.88:9460");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        String topic = "nirvana_monitor";
        int i = 0;
        int num = 5;
        while(i<num){
            try {
                Thread.sleep(11900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Producer<String, String> producer = new KafkaProducer<>(props);
            try {


//                SystemEnum[] systemArr = SystemEnum.values();
//                MonitorEvent monitorEvent = new MonitorEvent();
//                monitorEvent.setContent("error info");
//                monitorEvent.setIp("127.0.0.1");
//                monitorEvent.setKey(new Random().nextInt(5) +"");
//                monitorEvent.setLevel(LevelEnum.ERROR);
//                monitorEvent.setSystem(systemArr[new Random().nextInt(5)]);
//                monitorEvent.setUrl("localhost:9090/users");

                List<MonitorEvent> monitorEvents = new ArrayList<MonitorEvent>();

                monitorEvents.add(new MonitorEvent("0000",SystemEnum.ACCOUNT,LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("1111",SystemEnum.ACCOUNT,LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("2222",SystemEnum.CREDIT_CARD, LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("3333",SystemEnum.CREDIT_CARD, LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("4444",SystemEnum.LOAN, LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("5555",SystemEnum.LOAN, LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("6666",SystemEnum.PROVIDENT_FUND,LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("7777",SystemEnum.PROVIDENT_FUND,LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("8888",SystemEnum.SOCIAL_SECURITY,LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
                monitorEvents.add(new MonitorEvent("9999",SystemEnum.SOCIAL_SECURITY,LevelEnum.ERROR,"localhost:9090/users","127.0.0.1","error info"));
//                new Random().nextInt(10)
//                producer.send(new ProducerRecord<>(topic, "data", monitorEvents.get(0).toJSONString()));
                for (MonitorEvent me:monitorEvents) {
                    producer.send(new ProducerRecord<>(topic, "data",me.toJSONString()));

                }

//                MetersHandler metersHandler = new MetersHandler(3,2);//一分钟内5次
//                logger.info("触发：" + monitorEvent.getKey());
//                metersHandler.mark(monitorEvent);//计量
//                if(i==10){
//                    int r = new Random().nextInt(10)*1000;
//                    Thread.sleep(1000*12);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                producer.close();
                i++;
            }
        }
    }
}
