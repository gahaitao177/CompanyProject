package com.caiyi.financial.nirvana.batch.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.caiyi.financial.nirvana.batch.akka.QueryActor;
import com.caiyi.financial.nirvana.batch.service.UpdateService;
import com.caiyi.financial.nirvana.batch.web.SpringServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by been on 2016/10/21.
 */
@Controller
public class BatchController {
    public AtomicInteger counter = new AtomicInteger(0);
    public Logger logger = LoggerFactory.getLogger(getClass());
    public ActorSystem ACTOR_SYSTEM = SpringServletDispatcher.ACTOR_SYSTEM;

    @Autowired
    UpdateService updateService;

    @RequestMapping("/batch")
    @ResponseBody
    public String batch() {
        System.out.println("batched ....start----");
        if (counter.getAndAdd(1) >= 1) {
            logger.info("batched ....");
        } else {
            System.out.println("=================== " + now());
            String sql = "select cphone  from tb_user tu where tu.cphone" +
                    " is not null and tu.IPHONEATTRIBUTION is null and rownum < 2000";
            List<String> userPhones = updateService.query(sql);
            System.out.println(userPhones.size() + "");
            if (userPhones != null && userPhones.size() > 0) {
                for (String phone : userPhones) {
                    ActorRef actorRef = ACTOR_SYSTEM.actorOf(QueryActor.props(updateService));
                    actorRef.tell(phone, null);
//                    actorRef.tell("18810675066", null);
                }
            }
//            ActorRef actorRef = ACTOR_SYSTEM.actorOf(QueryActor.props(updateService));
//            actorRef.tell("18245170841", null);


        }
        return "batching";
    }

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "index";
    }

    private String now() {
        LocalDateTime now = LocalDateTime.now();
        String result = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return result;
    }

}
