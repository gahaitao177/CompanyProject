package com.caiyi.financial.nirvana.batch.jifen;

import akka.actor.ActorRef;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by been on 2016/12/30.
 */
public class Master extends BaseActor {
    public Map<String, String> data = new HashMap<>();
    public Map<String, String> results = new HashMap<>();


    @Override
    public void onReceive(Object message) throws Exception {
        String msg = (String) message;
        if (msg.equals("task-begin")) {
            ActorRef actorRefA = context().actorOf(ActorA.props());
            actorRefA.tell(msg, self());
            ActorRef actorRefB = context().actorOf(ActorB.props());
            actorRefB.tell(msg, self());
            ActorRef actorRefC = context().actorOf(ActorC.props());
            actorRefC.tell(msg, self());
        } else if (msg.equals("a")) {
            data.put("a", msg);
        } else if (msg.equals("b")) {
            data.put("b", "b");
        } else if (msg.equals("c")) {
            data.put("c", "c");
        }
        //说明获取数据结束
        if (data.size() == 3) {
            ActorRef actorRefD = context().actorOf(ActorD.props());
            actorRefD.tell(data.get("a"), self());
        }
        if (msg.equals("d")) {
            results.put("d", "d");
        }

        //说明计算任务结束
        if (results.size() == 3) {

//            results

            context().stop(self());
        }

    }
}
