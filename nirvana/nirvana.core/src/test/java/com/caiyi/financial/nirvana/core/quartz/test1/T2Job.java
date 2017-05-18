package com.caiyi.financial.nirvana.core.quartz.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenshiliang on 2016/10/9.
 */
public class T2Job {
    public Logger logger = LoggerFactory.getLogger(getClass());
    public String run(){
        logger.info(" T2Job run ");
        return "T2Job run result";
    }

}
