package com.yy.ana.bean;

import java.io.Serializable;

/**
 * Created by User on 2017/5/27.
 */
public class KeyValue implements Serializable {
    private byte[] family;
    private byte[] qualifier;
    private byte[] value;
    private long timestamp;

    public KeyValue() {
    }

    public KeyValue(byte[] family, byte[] qualifier, byte[] value) {
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
    }

    public KeyValue(byte[] family, byte[] qualifier, byte[] value, long timestamp) {
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
        this.timestamp = timestamp;
    }

    public byte[] getFamily() {
        return family;
    }

    public void setFamily(byte[] family) {
        this.family = family;
    }

    public byte[] getQualifier() {
        return qualifier;
    }

    public void setQualifier(byte[] qualifier) {
        this.qualifier = qualifier;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

