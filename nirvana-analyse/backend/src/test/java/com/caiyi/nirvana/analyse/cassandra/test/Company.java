package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

/**
 * Created by been on 2016/12/27.
 */
@Table(keyspace = "demo", name = "company")
public class Company {
    @PartitionKey
    @Field(name = "company_id")
    private UUID companyId;
    private String name;
    private Address address;

    public Company() {
    }

    public Company(UUID companyId, String name, Address address) {
        this.companyId = companyId;
        this.name = name;
        this.address = address;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
