package com.caiyi.financial.nirvana.heartbeat.client;

/**
 * Created by wenshiliang on 2016/10/11.
 */
public interface Scheduler {
    void start();
    void stop();
    boolean isRuning();
}
