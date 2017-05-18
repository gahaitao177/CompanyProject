package com.caiyi.financial.nirvana.batch.jifen;

import akka.actor.Actor;
import akka.actor.Props;
import akka.japi.Creator;

/**
 * Created by been on 2016/12/30.
 */
public class ActorC extends BaseActor {
    public static Props props() {
        return Props.create(new Creator<Actor>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Actor create() throws Exception {
                return new ActorC();
            }
        });
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.info("社保 ...");
        String siInfo = "siInfo, 3";
        context().parent().tell(siInfo, self());
    }
}
