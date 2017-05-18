package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by been on 2017/1/12.
 */

@Table(keyspace = "demo", name = "been",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM",
        caseSensitiveKeyspace = false,
        caseSensitiveTable = false)
public class Been {

    @PartitionKey
    @Column(name = "id")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "ctime")
    private Date ctime;

    @Column(name = "beans")
    private Set<Bean> beans = new HashSet<>();

    public Been() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCtime() {
        return ctime;
    }

    @Frozen
    public Set<Bean> getBeans() {
        return beans;
    }

    public void setBeans(Set<Bean> beans) {
        this.beans = beans;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }


    public Been(UUID id, String name, Date ctime) {
        this.id = id;
        this.name = name;
        this.ctime = ctime;
    }
}
