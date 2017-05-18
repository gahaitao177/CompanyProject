package com.caiyi.nirvana.analyse.storm.bolts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseBasicBolt;

import java.util.Map;

/**
 * Created by been on 16/10/8.
 */
public abstract class LoggingBolt extends BaseBasicBolt {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public Logger logger;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        logger = LogManager.getLogger(getClass());

    }
}
