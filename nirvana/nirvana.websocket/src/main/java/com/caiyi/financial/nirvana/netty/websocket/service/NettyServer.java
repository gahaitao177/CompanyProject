package com.caiyi.financial.nirvana.netty.websocket.service;

import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.utils.SpringContextUtilBro;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenshiliang on 2016/10/25.
 */
public class NettyServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    static final boolean SSL = System.getProperty("ssl") != null;

    //    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));
//    static  int PORT = 20000;


    final static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    final static EventLoopGroup workerGroup = new NioEventLoopGroup();


//    private static WebSocketServerInitializer webSocketServerInitializer = null;


    public static void start() throws InterruptedException {
        int port = 20001;
        try {
            new WebSocketServerInitializer();
            port = SystemConfig.getInt("websocket_port");
        } catch (Exception e) {
            LOGGER.info("未能读取websocket_port，使用默认端口：{}", port);
        }
        ServerBootstrap b = new ServerBootstrap();
        WebSocketServerInitializer webSocketServerInitializer = (WebSocketServerInitializer) SpringContextUtilBro
                .getBean("WebSocketServerInitializer");
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(webSocketServerInitializer)
                .bind(port).sync();
        LOGGER.info("start NettyServer to ws://localhost:" + port);
    }


    public static void destroy() {
        System.out.println("-------------------------------------------- NettyServer destroy");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
