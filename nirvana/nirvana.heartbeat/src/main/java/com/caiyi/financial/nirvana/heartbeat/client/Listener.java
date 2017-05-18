package com.caiyi.financial.nirvana.heartbeat.client;


/**
 * Created by wenshiliang on 2016/10/11.
 */
public interface Listener {

    /**
     * 向heartbeat请求失败后调用,返回client和异常,不要对client进行操作
     * @param client
     * @param ex
     */
    void errorEvent(final HeartbeatClient client,final Exception ex);

    /**
     * 向heartbeat请求后调用，返回client。不要对client进行操作
     * @param client
     */
    void successEvent(final HeartbeatClient client);

//    /**
//     * client创建失败时调用
//     * @param server
//     * @param ex
//     */
//    void clientCreateErrorEvent(final DrpcServer server,final Exception ex);
}
