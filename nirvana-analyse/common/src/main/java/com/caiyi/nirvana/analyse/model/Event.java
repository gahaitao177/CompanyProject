package com.caiyi.nirvana.analyse.model;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import java.util.Date;

/**
 * Created by been on 2017/1/12.
 */
@UDT(keyspace = "nirvana", name = "event")
public class Event {
    @Field(name = "event_id")
    private String eventId;
    @Field
    private Date time;
    @Field
    private String extra;

    public Event(String eventId, Date time, String extra) {
        this.eventId = eventId;
        this.time = time;
        this.extra = extra;
    }

    public Event() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
