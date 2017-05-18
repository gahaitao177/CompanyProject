package com.caiyi.nirvana.analyse;

import com.datastax.driver.core.Cluster;

/**
 * Created by been on 2017/1/13.
 */
public abstract class CassandraTestSupport {
    public Cluster getCluster(String contractPoints, int port) {
        Cluster cluster;
        cluster = Cluster.builder()
                .addContactPoint(contractPoints)
                .withPort(port)
                .build();
        return cluster;
    }

}
