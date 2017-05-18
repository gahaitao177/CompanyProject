package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.netty.websocket.akka.WebSocketBaseActor;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wenshiliang on 2016/10/26.
 */
@MVCComponent
@Named("/demo")
@Scope("prototype")
public class DemoWebSocketActor extends WebSocketBaseActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoWebSocketActor.class);

    public DemoWebSocketActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler,
                              Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap, String
                                      url) {
        super(channelHandlerContext, handler, requestParameterMap, requestHeaderMap, url);
    }


    @Override
    protected void onReceive0(Object message) {
        if (Objects.equals("ip", message)) {
            String ip = getClientIp();
            LOGGER.info("得到client ip 为 {}", ip);
        }
        LOGGER.info("---{}", message);
    }

    @Override
    protected void preDestroy() {

    }


//    @PostConstruct//初始化
//    public void init(){
//        LOGGER.info("---------------------- PostConstruct ");
//    }


//    @PreDestroy
//    public void  destroy(){
//        LOGGER.info("----------------------destroy调用");
//    }

    @Override
    protected void finalize() throws Throwable {
        LOGGER.info("------------finalize");
        super.finalize();
    }
}
