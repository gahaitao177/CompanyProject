package com.caiyi.financial.nirvana.netty.websocket.service;

import com.caiyi.financial.nirvana.annotation.MVCComponent;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.annotation.Resource;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * Created by wenshiliang on 2016/10/25.
 */
@MVCComponent("WebSocketServerInitializer")
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {


    private final SslContext sslCtx;


    @Resource(name = "WebSocketServerHandler")
    private WebSocketServerHandler webSocketServerHandler;

    public WebSocketServerInitializer() throws CertificateException, SSLException {
        if (NettyServer.SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("respDecoder-reqEncoder", new HttpServerCodec())
                .addLast("http-aggregator", new HttpObjectAggregator(65536))
                .addLast(new ChunkedWriteHandler())
                .addLast("action-handler", webSocketServerHandler);
    }

}
