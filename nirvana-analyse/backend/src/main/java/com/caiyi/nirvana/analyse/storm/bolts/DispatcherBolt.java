package com.caiyi.nirvana.analyse.storm.bolts;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;

/**
 * Created by been on 2017/1/11.
 */
public class DispatcherBolt extends BaseBolt {
    /**
     * Declare the output schema for all the streams of this topology.
     *
     * @param declarer this is used to declare output stream ids, output fields, and whether or not each output stream is a direct stream
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("executeInfo", "return-info"));
    }
}
