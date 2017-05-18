package com.caiyi.nirvana.analyse.storm.bolts;

import akka.actor.ActorRef;
import com.caiyi.nirvana.analyse.akka.CassandraActor;
import com.caiyi.nirvana.analyse.service.AppProfileService;
import com.caiyi.nirvana.analyse.service.RedisService;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by been on 2017/2/20.
 */
public abstract class BaseBolt extends AkkaSupport {
    public AppProfileService service;
    public RedisService redisService;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        service = new AppProfileService();
        redisService = new RedisService();

    }

    @Override
    protected ActorRef createActor(Tuple input, BasicOutputCollector collector) {
        return actorSystem.actorOf(CassandraActor.props(input, collector, service, redisService));
    }

}
