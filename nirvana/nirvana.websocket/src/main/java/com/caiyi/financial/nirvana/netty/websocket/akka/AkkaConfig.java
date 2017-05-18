package com.caiyi.financial.nirvana.netty.websocket.akka;

import akka.actor.ActorSystem;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;


/**
 * Created by been on 16/9/21.
 */

@MVCComponent
@Configuration
public class AkkaConfig {

    @Autowired
    private ApplicationContext applicationContext;

    private ActorSystem actorSystem = null;

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {
        actorSystem = ActorSystem.create("actorSystem");
        SpringExtension.SpringExtProvider.get(actorSystem).initialize(applicationContext);
        return actorSystem;
    }

    @PreDestroy//销毁时调用
    public void destroy() {
        System.out.println("--------------------------------- AkkaConfig destroy");
        if (actorSystem != null && !actorSystem.isTerminated()) {
            actorSystem.shutdown();
        }
    }
}
