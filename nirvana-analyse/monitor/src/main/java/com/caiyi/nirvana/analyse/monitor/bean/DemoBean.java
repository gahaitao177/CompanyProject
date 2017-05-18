package com.caiyi.nirvana.analyse.monitor.bean;

/**
 * Created by been on 2017/3/8.
 */
public class DemoBean {
    public DemoBean() {
    }

    private String requestId;

    public DemoBean(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "DemoBean{" +
                "requestId='" + requestId + '\'' +
                '}';
    }
}
