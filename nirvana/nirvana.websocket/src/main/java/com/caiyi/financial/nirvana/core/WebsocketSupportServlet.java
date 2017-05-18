package com.caiyi.financial.nirvana.core;

import com.caiyi.financial.nirvana.netty.websocket.service.NettyServer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by terry on 2016/11/23.
 */
public class WebsocketSupportServlet extends DispatcherServlet {
    @Override
    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext ctx = super.initWebApplicationContext();

        try {
            NettyServer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ctx;
    }

    @Override
    public void destroy() {
        NettyServer.destroy();
        super.destroy();
    }
}
