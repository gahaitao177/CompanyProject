package com.caiyi.financial.nirvana.bill.base;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by ljl on 2016/10/27.
 */
public abstract class LoggingSupport {
    public Logger logger = LogManager.getLogger(getClass());
}
