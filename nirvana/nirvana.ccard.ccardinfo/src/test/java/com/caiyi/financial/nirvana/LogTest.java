package com.caiyi.financial.nirvana;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenshiliang on 2016/7/1.
 */
public class LogTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTest.class);
    public static void main(String[] args) {
        LOGGER.info("aaaaaaaaaaaaaa");
        LOGGER.debug("aaaaaaaaaaaaa");
        LOGGER.error("error");
    }
}
