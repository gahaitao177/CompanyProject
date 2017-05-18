package com.caiyi.financial.nirvana.batch.jifen;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by been on 2016/12/30.
 */
public  abstract  class BaseActor extends UntypedActor {
    public Logger logger = LoggerFactory.getLogger(getClass());
}
