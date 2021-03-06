package com.caiyi.financial.nirvana.core.cassandra.client;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;

/**
 * Created by Mario on 2016/10/11 0011.
 * CassandraClient
 */
public class CassandraClient {
    public CassandraClient(CassandraPoolOptions conf){
        this.conf = conf;
    }

    private CassandraPoolOptions conf;

    public CassandraPoolOptions getConf() {
        return conf;
    }

    public void setConf(CassandraPoolOptions conf) {
        this.conf = conf;
    }

    private Cluster cluster;

    private void initCluster() {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions
                .setConnectionsPerHost(HostDistance.LOCAL, conf.getLocalCoreConnectNum(), conf.getLocalMaxConnectNum())
                .setConnectionsPerHost(HostDistance.REMOTE, conf.getRemoteCoreConnectNum(), conf.getRemoteMaxConnectNum())
                .setHeartbeatIntervalSeconds(conf.getHeartbeatIntervalSeconds());
        cluster = Cluster.builder()
                .withPoolingOptions(poolingOptions)
                .addContactPoints(conf.getContactPoints())
                .withPort(conf.getPort())
                .build();
        System.out.println(">>>>>>>>>>>>>Cassandra Cluster 初始化完成<<<<<<<<<<<<<");
    }

    public Cluster getCluster() {
        if (cluster == null || cluster.isClosed()) {
            this.initCluster();
        }
        return cluster;
    }

    public Session connect() {
        return getCluster().connect();
    }

    public Session connect(String keySpace) {
        return getCluster().connect(keySpace);
    }
}
