package com.caiyi.financial.nirvana.core.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenshiliang on 2016/12/1.
 */
public class AkkaBoltActor extends UntypedActor {
    private static Logger LOGGER = LoggerFactory.getLogger(AkkaBoltActor.class);
    private final Tuple tuple;
    private final BasicOutputCollector collector;
    private final BaseBolt baseBolt;


    public static Props props(final Tuple tuple, final BasicOutputCollector collector,final BaseBolt baseBolt) {
        return Props.create(new Creator<AkkaBoltActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public AkkaBoltActor create() throws Exception {
                return new AkkaBoltActor(tuple, collector,baseBolt);
            }
        });
    }

    public AkkaBoltActor(Tuple tuple, BasicOutputCollector collector,BaseBolt baseBolt) {
        this.tuple = tuple;
        this.collector = collector;
        this.baseBolt = baseBolt;
    }


    public void onReceive(Object message) throws Exception {
        JSONObject jsonObject = (JSONObject) message;
        String result =  null;
        try{
            result = baseBolt.execute(jsonObject.getString("method"),jsonObject.getString("data"),tuple);
            collector.emit(new Values(result, tuple.getValue(1)));
        }catch (Exception e){
            LOGGER.error("error",e);
        }finally {

            context().stop(self());
        }
    }
}
