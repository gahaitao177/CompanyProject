package com.caiyi.nirvana.analyse.model;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import java.util.Date;

/**
 * Created by been on 2017/1/12.
 * user defined types 用@Field 注解,不能用@Column注解
 */

@UDT(keyspace = "nirvana", name = "history")
public class History {
    @Field(name = "page")
    private String page;
    @Field(name = "enter_time")
    private Date enterTime;
    @Field(name = "exit_time")
    private Date exitTime;

    public History() {
    }

    public History(String page, Date enterTime, Date exitTime) {
        this.page = page;
        this.enterTime = enterTime;
        this.exitTime = exitTime;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

}
