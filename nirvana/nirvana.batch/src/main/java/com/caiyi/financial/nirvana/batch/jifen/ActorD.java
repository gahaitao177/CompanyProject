package com.caiyi.financial.nirvana.batch.jifen;

import akka.actor.Actor;
import akka.actor.Props;
import akka.japi.Creator;

/**
 * Created by been on 2016/12/30.
 * 根据获取到的数据计算积分
 */
public class ActorD extends BaseActor {

    public static Props props(){
        return Props.create(new Creator<Actor>() {

            private static final long serialVersionUID = 1L;
            @Override
            public Actor create() throws Exception {
                return new ActorD();
            }
        });
    }
    @Override
    public void onReceive(Object message) throws Exception {
        String jifen1 = "12";
        context().parent().tell(jifen1, self());
        context().stop(self());
    }
}
