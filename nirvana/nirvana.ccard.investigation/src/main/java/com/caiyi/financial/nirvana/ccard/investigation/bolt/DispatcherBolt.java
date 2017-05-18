package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import akka.actor.ActorRef;
import com.caiyi.financial.nirvana.ccard.investigation.akka.MainActor;
import com.caiyi.financial.nirvana.ccard.investigation.core.AkkaSupport;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

/**
 * Created by been on 2016/12/30.
 */
public class DispatcherBolt extends AkkaSupport {

    @Override
    protected ActorRef createActor(Tuple input, BasicOutputCollector collector) {
        return actorSystem.actorOf(MainActor.props(input, collector));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message", "return-info"));
    }
}
