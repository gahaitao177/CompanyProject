package com.caiyi.financial.nirvana.ccard.material.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by susan on 2016/4/1.
 */
public class BankApplyListener {
    public static Logger logger = LoggerFactory.getLogger("BankApplyListener");
    private static KafkaProducer kafkaProducer = new KafkaProducer();
    private static DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 发送申卡请求正常，kafka
     *
     * @param bankEnum
     * @param stepEnum
     */
    public static void sendSucess(BankEnum bankEnum, BankApplyStepEnum stepEnum) {
        send(bankEnum, stepEnum, BankApplyStatusEnum.sucess, null,null);
    }

    /**
     * 发送申卡请求异常情况
     * @param bankEnum 银行类型
     * @param stepEnum 申卡的步骤
     * @param req 错误的请求
     */
    public static void sendError(BankEnum bankEnum, BankApplyStepEnum stepEnum, ErrorRequestBean req) {
        String parentPath = getParentPath(bankEnum, stepEnum);
        if(StringUtils.isNotEmpty(req.getResult())){
            ErrorFileUtil.saveFile(req.getUrl(), req.getParam(), req.getResult(), req.getFileName(), parentPath);
        }
        send(bankEnum, stepEnum, BankApplyStatusEnum.error, parentPath+"/"+req.getFileName(),req);

    }
    /**
     * 发送申卡请求异常情况
     * @param bankEnum 银行类型
     * @param stepEnum 申卡的步骤
     * @param reqList 错误的请求
     */
    public static void sendError(BankEnum bankEnum, BankApplyStepEnum stepEnum, List<ErrorRequestBean> reqList) {
        String parentPath = getParentPath(bankEnum,stepEnum);
        StringBuilder data = new StringBuilder();
        for(ErrorRequestBean req : reqList){
            if(StringUtils.isNotEmpty(req.getResult())){
                ErrorFileUtil.saveFile(req.getUrl(), req.getParam(), req.getResult(), req.getFileName(), parentPath);
            }
            data.append(parentPath).append(req.getFileName()).append(";");
            send(bankEnum, stepEnum, BankApplyStatusEnum.error, data.toString(),req);
        }
    }

    public static String getParentPath(BankEnum bankEnum, BankApplyStepEnum stepEnum){
        String parentPath;
        if (BankApplyStepEnum.query_apply.equals(stepEnum)) {
            parentPath = SystemConfig.get("apply_listener.apply.query.file.path");
        } else {
            parentPath = SystemConfig.get("apply_listener.apply.file.path");
        }
        parentPath += "/" + sdf.format(new Date()) + "/" + bankEnum;
        return parentPath;
    }


    private static void send(BankEnum bankEnum, BankApplyStepEnum stepEnum, BankApplyStatusEnum statusEnum, String data,ErrorRequestBean bean) {
        Map<String, Object> map = new HashMap<>();
        map.put("bankEnum", bankEnum);
        map.put("stepEnum", stepEnum);
        map.put("statusEnum", statusEnum);
        map.put("data", data);
        if(bean!=null){
            map.put("param",bean.getParam());
            map.put("url",bean.getUrl());
            map.put("ierrortype",bean.getIerrortype());
            map.put("cerrordesc",bean.getCerrordesc());
            map.put("param",bean.getParam());
            map.put("cphone",bean.getCphone());
        }


//         发送kafka
//        System.out.println(kafkaProducer.toJSONString(map));
        logger.info("发送kafka消息:"+kafkaProducer.toJSONString(map));
        kafkaProducer.sendJsonString(map);

    }


    public static void main(String[] args) {
        List<ErrorRequestBean> list = new ArrayList<>();
//        list.add(new ErrorRequestBean("1.txt","参数","结果","url地址"));
//        list.add(new ErrorRequestBean("2.txt","参数","结果","url地址"));
//        list.add(new ErrorRequestBean("大哥333.txt","参数","结果","url地址"));
        ErrorRequestBean bean = new ErrorRequestBean();
        bean.setCphone("18301852937");
        bean.setCerrordesc("abdfsadff");
        bean.setIerrortype(-1);
        bean.setParam("{test:test,'aa':'bb'}");
        bean.setResult("asdasda");
        bean.setUrl("http://www.test.com");

        BankApplyListener.sendError(BankEnum.guangda, BankApplyStepEnum.submit_apply,bean);


//        ExecutorService service = Executors.newCachedThreadPool();
//        KafkaProducer producer = new KafkaProducer();
//        for (int i = 0; i < 20; i++) {
//            service.submit(new ProThread(i, producer));
//        }
//        service.shutdown();
    }

    static class ProThread implements Runnable {
        private KafkaProducer kafkaProducer;
        Random random = new Random();
        private int id;

        public ProThread(int id, KafkaProducer kafkaProducer) {
            this.id = id;
            this.kafkaProducer = kafkaProducer;
        }

        @Override
        public void run() {
            int statusLength = BankApplyStatusEnum.values().length;
            int steplength = BankApplyStepEnum.values().length;
            int bankLenth = BankEnum.values().length;
            for (int i = 0; i < 100; i++) {
                Map<String, Object> map = new HashMap<>();
                int statusIndex = random.nextInt(100);
                if (statusIndex > 90) {
                    map.put("statusEnum", BankApplyStatusEnum.values()[random.nextInt(statusLength)]);
                } else {
                    map.put("statusEnum", BankApplyStatusEnum.error);
                }

                map.put("stepEnum", BankApplyStepEnum.values()[random.nextInt(steplength)]);
                map.put("bankEnum", BankEnum.values()[random.nextInt(bankLenth)]);
                map.put("data", "\\opt\\export\\data\\apply_error\\2016\\04\\05\\guangda\\1459841354094.html");
                kafkaProducer.sendJsonString(map);
            }

        }
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
