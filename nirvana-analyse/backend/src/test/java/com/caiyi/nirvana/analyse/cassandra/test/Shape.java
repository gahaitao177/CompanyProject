package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.PartitionKey;

import java.util.UUID;

/**
 * Created by been on 2016/12/27.
 */
public abstract class Shape {
    private UUID id;

    @PartitionKey
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
