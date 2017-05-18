package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.caiyi.financial.nirvana.netty.websocket.akka.NettyChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by wenshiliang on 2016/10/27.
 */
public class DefaultChannel implements NettyChannel {

    private ChannelHandlerContext context;
    private long createTime;

    public DefaultChannel(ChannelHandlerContext context) {
        this.context = context;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public ChannelHandlerContext getChannelHandlerContext() {
        return context;
    }

    @Override
    public void close() {
        context.close();
    }

    @Override
    public void sendMsg(String msg) {
        context.channel().writeAndFlush(new TextWebSocketFrame(msg));
    }

    @Override
    public boolean isOpen() {
        return context.channel().isOpen();
    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return "DefaultChannel{" +
                "context=" + context +
                ", createTime=" + createTime +
                '}';
    }
}
