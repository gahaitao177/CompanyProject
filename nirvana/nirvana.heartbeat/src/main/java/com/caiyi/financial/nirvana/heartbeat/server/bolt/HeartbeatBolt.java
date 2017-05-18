package com.caiyi.financial.nirvana.heartbeat.server.bolt;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by wenshiliang on 2016/10/11.
 */
public class HeartbeatBolt extends BaseBasicBolt {
    public Logger logger = null;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String resultStr = input.getString(0);
        Object retInfo = input.getValue(1);
        logger.info(resultStr);
        collector.emit(new Values(resultStr, retInfo));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("result", "return-info"));
    }
}
