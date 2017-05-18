package com.caiyi.financial.nirvana.ccard.investigation.core;

import akka.actor.UntypedActor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by been on 2016/12/30.
 */
public abstract class BaseActor extends UntypedActor {
    public Logger logger = LogManager.getLogger(getClass());

}
