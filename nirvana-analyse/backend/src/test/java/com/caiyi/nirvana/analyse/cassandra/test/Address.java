package com.caiyi.nirvana.analyse.cassandra.test;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

/**
 * Created by been on 2016/12/27.
 */
@UDT(keyspace = "demo", name = "address")
public class Address {

    private String street;
    @Field(name = "zip_code")
    private int zipCode;

    public Address() {
    }

    public Address(String street, int zipCode) {
        this.street = street;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
