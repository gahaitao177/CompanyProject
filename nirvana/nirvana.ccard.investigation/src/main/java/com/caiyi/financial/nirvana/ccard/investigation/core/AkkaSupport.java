package com.caiyi.financial.nirvana.ccard.investigation.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by been on 2016/12/30.
 */
public abstract class AkkaSupport extends LoggingBolt {
    public static ActorSystem actorSystem;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        synchronized (AkkaSupport.class) {
            if (actorSystem == null) {
                actorSystem = ActorSystem.create("accountActorSystem");
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                actorSystem.terminate();
            }
        });
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        ActorRef actorRef = createActor(input, collector);
        actorRef.tell("execute", null);
    }

    protected abstract ActorRef createActor(Tuple input, BasicOutputCollector collector);

}
