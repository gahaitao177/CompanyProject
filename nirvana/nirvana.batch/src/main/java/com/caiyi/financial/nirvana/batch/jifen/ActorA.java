package com.caiyi.financial.nirvana.batch.jifen;

import akka.actor.Actor;
import akka.actor.Props;
import akka.japi.Creator;

/**
 * Created by been on 2016/12/30.
 */
public class ActorA extends BaseActor {

    public static Props props() {
        return Props.create(new Creator<Actor>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Actor create() throws Exception {
                return new ActorA();
            }
        });
    }
    @Override
    public void onReceive(Object message) throws Exception {
        logger.info("获取账单信息");
        String msga = "msga, 3";
        context().parent().tell(msga, self());
    }
}
