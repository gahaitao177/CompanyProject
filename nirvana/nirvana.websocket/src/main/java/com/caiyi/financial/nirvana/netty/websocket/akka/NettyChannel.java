package com.caiyi.financial.nirvana.netty.websocket.akka;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wenshiliang on 2016/10/27.
 */
public interface NettyChannel {
    ChannelHandlerContext getChannelHandlerContext();

    void close();

    void sendMsg(String messg);

    boolean isOpen();

    long getCreateTime();

}
