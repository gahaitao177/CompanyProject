package com.caiyi.financial.nirvana.heartbeat.client;

import org.apache.storm.drpc.DRPCInvocationsClient;
import org.apache.storm.thrift.transport.TTransportException;

import java.util.Map;

/**
 * Created by wenshiliang on 2016/10/18.
 */
public class HeartbeatClient extends DRPCInvocationsClient {


    private DrpcServer drpcServer;

    public HeartbeatClient(DrpcServer drpcServer) throws TTransportException {
        super(drpcServer.getConf(), drpcServer.getHost(), drpcServer.getPort());
        this.drpcServer = drpcServer;
    }

    public HeartbeatClient(Map conf, String host, int port) throws TTransportException {
        super(conf, host, port);
    }

    public DrpcServer getDrpcServer() {
        return drpcServer;
    }
}
