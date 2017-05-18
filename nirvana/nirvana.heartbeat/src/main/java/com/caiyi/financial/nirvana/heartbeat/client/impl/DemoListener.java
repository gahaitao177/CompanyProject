package com.caiyi.financial.nirvana.heartbeat.client.impl;

import com.caiyi.financial.nirvana.heartbeat.client.HeartbeatClient;
import com.caiyi.financial.nirvana.heartbeat.client.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenshiliang on 2016/10/11.
 */
public class DemoListener implements Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoListener.class);

    @Override
    public void errorEvent(HeartbeatClient client, Exception ex) {
        LOGGER.error("errorEvent {} {}",client.getDrpcServer().getAddress(),ex.getMessage());
    }

    @Override
    public void successEvent(HeartbeatClient client) {
        LOGGER.error("sucessEvent {}",client.getDrpcServer().getAddress());
    }
}
