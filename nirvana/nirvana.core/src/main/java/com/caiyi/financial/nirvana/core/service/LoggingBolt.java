package com.caiyi.financial.nirvana.core.service;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by wenshiliang on 2016/4/25.
 * 日志bolt
 */
public abstract class LoggingBolt extends BaseBasicBolt  {
    public Logger logger = null;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        logger = LoggerFactory.getLogger(getClass());
    }

}
