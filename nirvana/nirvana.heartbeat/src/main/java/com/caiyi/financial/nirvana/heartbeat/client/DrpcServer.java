package com.caiyi.financial.nirvana.heartbeat.client;

import java.util.Map;

/**
 * Created by wenshiliang on 2016/10/11.
 * drpc client创建需要参数
 */
public class DrpcServer {
    private Map conf;
    private String host;
    private int port;
    private Integer timeout;
    private String address;

    public DrpcServer(Map conf, String host, int port, Integer timeout) {
        this.conf = conf;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        address = host + ":" + port;
    }

    public Map getConf() {
        return conf;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrpcServer that = (DrpcServer) o;

        if (port != that.port) return false;
        if (conf != null ? !conf.equals(that.conf) : that.conf != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (timeout != null ? !timeout.equals(that.timeout) : that.timeout != null) return false;
        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        int result = conf != null ? conf.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (timeout != null ? timeout.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
