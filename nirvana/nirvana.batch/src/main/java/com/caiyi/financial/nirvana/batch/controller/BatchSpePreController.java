package com.caiyi.financial.nirvana.batch.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.caiyi.financial.nirvana.batch.akka.SpePreQueryActor;
import com.caiyi.financial.nirvana.batch.service.UpdateSpePreService;
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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhukai on 2016/10/21.
 */
@Controller
public class BatchSpePreController {

    public AtomicInteger counter = new AtomicInteger(0);
    public Logger logger = LoggerFactory.getLogger(getClass());
    public ActorSystem ACTOR_SYSTEM = SpringServletDispatcher.ACTOR_SYSTEM;
    @Autowired
    UpdateSpePreService updateSpePreService;

    @RequestMapping("/batchTest")
    @ResponseBody
    public String batch() {
        System.out.println("BatchSpePre ....start----");
        if (counter.getAndAdd(1) >= 1) {
            logger.info("batched ....");
        } else {
            System.out.println("=================== " + now());
            List<Map<String, Object>> idAndUrlList = updateSpePreService.query();
            System.out.println(idAndUrlList.size() + "");
            if (idAndUrlList != null && idAndUrlList.size() > 0) {
                for (Map idAndUrl : idAndUrlList) {
                    ActorRef actorRef = ACTOR_SYSTEM.actorOf(SpePreQueryActor.props(updateSpePreService));
                    actorRef.tell(idAndUrl, null);
                }
            }
        }
        return "batching";
    }

    @RequestMapping("/testIndex")
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
