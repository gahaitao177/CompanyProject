package com.caiyi.financial.nirvana.core.client;


import com.caiyi.financial.nirvana.core.bean.DrpcRequest;

/**
 * Created by wsl on 2016/1/7.
 * drpc client抽象
 */
public interface IDrpcClient {

    String getDrpcService();

    /**
     * 请求默认的drpc
     * @param drpcRequest
     * @return
     */
    String execute(DrpcRequest drpcRequest);


    <T> T execute(DrpcRequest drpcRequest,Class<T> clazz);

    /**
     * @param drpcService  DRPCSpout的_function属性。根据此参数查找storm topology 中唯一drpc服务
     * @param drpcRequest
     * @return
     */
    String execute(String drpcService,DrpcRequest drpcRequest);

    <T> T execute(String drpcService,DrpcRequest drpcRequest,Class<T> clazz);

    void close() throws CloneNotSupportedException;
}
