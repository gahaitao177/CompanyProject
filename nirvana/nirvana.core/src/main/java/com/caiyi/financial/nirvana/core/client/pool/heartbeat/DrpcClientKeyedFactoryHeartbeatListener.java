package com.caiyi.financial.nirvana.core.client.pool.heartbeat;

import com.caiyi.financial.nirvana.core.client.pool.DrpcClientKeyedFactory;
import com.caiyi.financial.nirvana.heartbeat.client.DrpcServer;
import com.caiyi.financial.nirvana.heartbeat.client.HeartbeatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wenshiliang on 2016/10/19.
 */
public class DrpcClientKeyedFactoryHeartbeatListener implements com.caiyi.financial.nirvana.heartbeat.client.Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrpcClientKeyedFactoryHeartbeatListener.class);

    private DrpcClientKeyedFactory factory;

    public DrpcClientKeyedFactoryHeartbeatListener(DrpcClientKeyedFactory factory) {
        this.factory = factory;
    }

    @Override
    public void errorEvent(HeartbeatClient client, Exception ex) {
        DrpcServer server = client.getDrpcServer();
        List<DrpcServer> list =  factory.getServers();
        synchronized (list){
            if(list.contains(server)){
                int size = list.size();
                list.remove(server);
                LOGGER.info("remove server {}; size {} --> {}",server.getAddress(),size,list.size());
            }

        }
    }

    @Override
    public void successEvent(HeartbeatClient client) {
        DrpcServer server = client.getDrpcServer();
        List<DrpcServer> list =  factory.getServers();
        if(! list.contains(server)){
            synchronized (list){
                if(! list.contains(server)){
                    int size = list.size();
                    list.add(server);
                    LOGGER.info("add server {}; size {} --> {}",server.getAddress(),size,list.size());
                }
            }
        }
    }
}
