package com.caiyi.financial.nirvana.batch.web;

import akka.actor.ActorSystem;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by lichuanshun on 16/10/21.
 */
public class SpringServletDispatcher extends DispatcherServlet {
    public static final ActorSystem ACTOR_SYSTEM  = ActorSystem.create("batchSystem");


    @Override
    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext context = super.initWebApplicationContext();
        System.out.println("===============================================================1111");
        System.out.println("create:" + ACTOR_SYSTEM == null);
        return context;
    }

    @Override
    public void destroy() {
        ACTOR_SYSTEM.terminate();
        ACTOR_SYSTEM.awaitTermination();
        System.out.println("===============================================================");
        super.destroy();

    }
}
