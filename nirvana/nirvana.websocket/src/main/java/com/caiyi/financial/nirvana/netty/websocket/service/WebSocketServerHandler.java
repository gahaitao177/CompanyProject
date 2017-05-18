package com.caiyi.financial.nirvana.netty.websocket.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.netty.websocket.akka.SpringExtension;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;


/**
 * Created by wenshiliang on 2016/10/25.
 */
@MVCComponent("WebSocketServerHandler")
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private WebSocketServerHandshaker handshaker;

    private static final LongAdder totalWebsocketNum = new LongAdder();

    @Autowired
    private ActorSystem actorSystem;

    private Map<ChannelHandlerContext, ActorRef> actorRefMap = new ConcurrentHashMap<>();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 处理HTTP的代码,本项目中netty只有websocket。直接转换为http为websocket
     * 理论上channelRead0 第一次进入该方法
     * WebSocket client.connect() 进入该方法，转换http为websocket
     *
     * @param ctx
     * @param req
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {


        String[] strs = req.uri().split("\\?");
        String url = strs[0];
        Map<String, String[]> paramMap = new HashMap<>();
        if (strs.length != 1) {
            for (String str : strs[1].split("&")) {
                int index = str.indexOf("=");
                String key = str.substring(0, index);
                String value = str.substring(index + 1);


                if (paramMap.containsKey(key)) {
                    String[] p = paramMap.get(key);
                    String[] copyArray = Arrays.copyOf(p, p.length + 1);
                    copyArray[p.length] = value;
                    paramMap.put(key, copyArray);
                } else {
                    paramMap.put(key, new String[]{value});
                }
            }
        }


        Map<String, String> headers = new HashMap<>();
        req.headers().forEach(entry -> {
            headers.put(entry.getKey(), entry.getValue());
        });

//        logger.info("uri:{} {}", ctx.channel().remoteAddress(), req.uri());
//        logger.info("url {}", url);
//        logger.info(JsonUtil.toJSONString(paramMap));


//        WebSocketBaseActor actor = (WebSocketBaseActor) SpringContextUtilBro.getBean(url);
        ActorRef actorRef = null;
        try {
//            HttpRequest
             actorRef = actorSystem.actorOf(
                    SpringExtension.SpringExtProvider.get(actorSystem).props(url, ctx, this, paramMap, headers, url),
                    UUID.randomUUID().toString());
        } catch (Exception e) {
            logger.error("ActorRef 创建失败", e);
        }
        if (actorRef != null) {
            actorRefMap.put(ctx, actorRef);
            totalWebsocketNum.increment();
            logger.info("websocket connection size[{}]", totalWebsocketNum.sum());

            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    getWebSocketLocation(req), null, true);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }
            //校验一次,是否应该可用
            actorRef.tell("checkAvailable", null);
        }


    }

    /**
     * websocket 连接成功后，进行消息通信。  WebSocket client.sentText() 后进入该方法
     *
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {


        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            ActorRef actorRef = actorRefMap.get(ctx);
            if (actorRef != null) {
                actorRef.tell(text, null);
            }
            return;
        }
    }

    /**
     * 链接出现异常
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("websocket error interrupt", cause);
        ctx.close();
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(io.netty.handler.codec.http.HttpHeaderNames.HOST);
        logger.info(location);
        if (NettyServer.SSL) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {//如果是HTTP请求，进行HTTP操作
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//如果是Websocket请求，则进行websocket操作
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }


    /**
     * 释放链接，调用 ctx.close() 会触发此方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        /**
         * 释放 actor，下次gc时候销毁actor对象
         * 从map中移除actorref
         * websocket num -1
         *
         */
        ActorRef actorRef = actorRefMap.get(ctx);
        if (actorRef != null) {
            actorRefMap.remove(ctx);
            totalWebsocketNum.decrement();
            logger.info("websocket close num[{}]  {}", totalWebsocketNum.sum(), ctx);
            if (!actorRef.isTerminated()) {
                actorRef.tell("stop", null);
            }
        }
        super.channelInactive(ctx);

//        logger.info("channel id:{}",ctx.channel().id());
//        logger.info("ctx {} actor ctx.channel().isOpen() {}",ctx.isRemoved(),ctx.channel().isOpen());

    }

    @PreDestroy//销毁时调用
    public void destroy() {
        System.out.println("--------------------------------- WebSocketServerHandler destroy");
    }
}
