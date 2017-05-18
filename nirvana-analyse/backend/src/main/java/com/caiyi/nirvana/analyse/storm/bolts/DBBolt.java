package com.caiyi.nirvana.analyse.storm.bolts;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

/**
 * Created by been on 2017/2/20.
 */
public class DBBolt extends BaseBolt {
    /**
     * Declare the output schema for all the streams of this topology.
     *
     * @param declarer this is used to declare output stream ids, output fields, and whether or not each output stream is a direct stream
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {


    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        super.execute(input, collector);
    }
}
