package com.caiyi.financial.nirvana.batch.controller;

import akka.actor.ActorSystem;
import com.caiyi.financial.nirvana.batch.web.SpringServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by been on 2016/12/30.
 */
public class DemoController {
    public Logger logger = LoggerFactory.getLogger(getClass());
    public ActorSystem ACTOR_SYSTEM = SpringServletDispatcher.ACTOR_SYSTEM;

    @RequestMapping("/demo")
    @ResponseBody
    public String demo() {


        return "demo";
    }
}
