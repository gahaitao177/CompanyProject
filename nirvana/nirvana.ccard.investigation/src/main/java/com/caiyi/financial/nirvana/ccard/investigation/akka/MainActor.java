package com.caiyi.financial.nirvana.ccard.investigation.akka;

import akka.actor.Actor;
import akka.actor.Props;
import akka.japi.Creator;
import com.caiyi.financial.nirvana.ccard.investigation.core.BaseActor;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * Created by been on 2016/12/30.
 */
public class MainActor extends BaseActor {
    private final Tuple tuple;
    private final BasicOutputCollector collector;

    public MainActor(Tuple tuple, BasicOutputCollector collector) {
        this.tuple = tuple;
        this.collector = collector;
    }


    public static Props props(final Tuple tuple, final BasicOutputCollector collector) {
        return Props.create(new Creator<Actor>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Actor create() throws Exception {
                return new MainActor(tuple, collector);
            }
        });
    }

    @Override
    public void onReceive(Object message) throws Exception {
        String msg = tuple.getString(0);
        Object returnInfo = tuple.getString(1);
        if (msg.equals("hello")) {
            logger.info("test hello ........  ");
            //响应客户端
            collector.emit(new Values(msg, returnInfo));
            context().stop(self());
        } else {
            // TODO: 2016/12/30 规划业务逻辑
        }
    }
}
