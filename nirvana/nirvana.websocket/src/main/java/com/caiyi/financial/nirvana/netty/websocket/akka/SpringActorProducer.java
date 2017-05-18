package com.caiyi.financial.nirvana.netty.websocket.akka;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by been on 16/9/21.
 */
public class SpringActorProducer implements IndirectActorProducer {
    final ApplicationContext applicationContext;
    final String actorBeanName;
    final ChannelHandlerContext ctx;
    final WebSocketServerHandler handler;
    final Map<String, String[]> requestParameterMap;
    final Map<String, String> requestHeaderMap;
    final String url;

    public SpringActorProducer(ApplicationContext applicationContext,
                               String actorBeanName, ChannelHandlerContext ctx,
                               WebSocketServerHandler handler,
                               Map<String, String[]> requestParameterMap,
                               Map<String, String> requestHeaderMap,
                               String url) {
        this.applicationContext = applicationContext;

        this.actorBeanName = actorBeanName;
        this.ctx = ctx;
        this.handler = handler;
        this.requestParameterMap = requestParameterMap;
        this.requestHeaderMap = requestHeaderMap;
        this.url = url;
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(actorBeanName, ctx, handler, requestParameterMap, requestHeaderMap,
                url);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}
