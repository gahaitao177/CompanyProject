package com.caiyi.financial.nirvana.core.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.akka.AkkaBoltActor;
import com.caiyi.financial.nirvana.core.bean.BoltAttr;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.exception.BoltException;
import com.caiyi.financial.nirvana.core.spring.CoreAnnotationBeanScannerConfigurer;
import com.caiyi.financial.nirvana.core.util.JsonUtil;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsl on 2015/12/29.
 * 分发bolt类，
 * 将drpc client中参数解析为com.caiyi.financial.nirvana.core.bean.DrpcRequest
 * 根据DrpcRequest.bolt查找指定的streamId
 * 未找到返回异常，执行出错返回异常
 */
public class DispatcherBolt extends SpringBolt {


    private List<BoltAttr> boltAttrList;
    Map<String, BaseBolt> boltMap;
    private ActorSystem actorSystem;

    public DispatcherBolt() {
    }

    public DispatcherBolt(List<BoltAttr> boltAttrList) {
        this.boltAttrList = boltAttrList;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        init(stormConf, context);

        boltMap = new HashMap<>();


        try {
            CoreAnnotationBeanScannerConfigurer coreBeanScannerConfigurer = getBean
                    (CoreAnnotationBeanScannerConfigurer.class);
            coreBeanScannerConfigurer.getBoltNameList().forEach(boltName -> {
                BaseBolt baseBolt = (BaseBolt) getBean(boltName);
                baseBolt.prepare(stormConf, context);
                boltMap.put(boltName, baseBolt);
                logger.info("初始化bolt {}",boltName);
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化异常",e);
            throw new BoltException(e);
//            boltAttrList.forEach(boltAttr -> {
//                BaseBolt baseBolt = boltAttr.newInstance();
//                baseBolt.setApplicationContext(getSpringContext());
//                baseBolt.prepare(stormConf, context);
//                boltMap.put(boltAttr.getBoltId(), baseBolt);
//            });
        }
        actorSystem = ActorSystemSingleton.newInstance();
    }


    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String param = tuple.getString(0);
        Object retInfo = tuple.getValue(1);
        logger.debug("dispatch--------------请求--------------{}", param);
        try {
            JSONObject obj = JSONObject.parseObject(param);
            String bolt = obj.getString("bolt");
            if (boltMap.containsKey(bolt)) {
                BaseBolt baseBolt = boltMap.get(bolt);
                ActorRef actorRef = actorSystem.actorOf(AkkaBoltActor.props(tuple, collector, baseBolt));
                actorRef.tell(obj, null);
            } else {
                logger.error("bolt is empty ! bolt name is {}", bolt);
                Object result = new BoltResult(BoltResult.ERROR_404, "bolt is empty!");
                collector.emit(new Values(JsonUtil.toJSONString(result), retInfo));
            }
        } catch (Exception e) {
            logger.error("出错", e);
            logger.info("参数{}", param);
            Object result = new BoltResult(BoltResult.ERROR, "dispatcherBolt execute error");
            collector.emit(new Values(JsonUtil.toJSONString(result), retInfo));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("result", "return-info"));
    }

    static class ActorSystemSingleton {
        private static ActorSystem actorSystem;

        static {
            System.out.println("--------------stormActorSystem 创建--------------");
            actorSystem = ActorSystem.create("stormActorSystem");
        }

        public static ActorSystem newInstance() {
            return actorSystem;
        }
    }
}
