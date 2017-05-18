package com.caiyi.nirvana.analyse.akka;

import akka.actor.UntypedActor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;

/**
 * Created by been on 2016/11/4.
 */
public abstract class BaseActor extends UntypedActor {
    public final Tuple tuple;
    public final BasicOutputCollector collector;
    public final Logger logger = LogManager.getLogger(getClass());

    public BaseActor(Tuple tuple, BasicOutputCollector collector) {
        this.tuple = tuple;
        this.collector = collector;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        try {
            String msg = (String) message;
            if (msg.equals("execute")) {
                doBusiness(tuple, collector);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            context().stop(self());
        }
    }

    protected abstract void doBusiness(Tuple tuple, BasicOutputCollector collector);

}
