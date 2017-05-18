package com.caiyi.nirvana.analyse.cassandra;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Created by been on 2017/1/11.
 */
public class TestLogger {
    @Test
    public void test() {
        Logger logger = LogManager.getLogger(getClass());
        logger.info("test");
    }
}
