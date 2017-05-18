package com.caiyi.nirvana.analyse.cassandra.test;

/**
 * Created by been on 2017/1/12.
 */

import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "demo", name = "bean")
public class Bean {

    private int age;
    private String street;

    public Bean(int age, String street) {
        this.age = age;
        this.street = street;
    }

    public Bean() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
