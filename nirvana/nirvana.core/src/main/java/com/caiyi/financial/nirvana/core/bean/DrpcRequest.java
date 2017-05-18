package com.caiyi.financial.nirvana.core.bean;


import com.caiyi.financial.nirvana.core.util.JsonUtil;

/**
 * Created by wsl on 2015/12/30.
 * 封装的drpc 请求类
 *
 */
public class DrpcRequest<T> {
    /**
     * 根据bolt在DispatcherBolt中查找指定的stream流传入下一个bolt
     */
    private String bolt;
    /**
     * 根据method在业务bolt中查找业务方法
     */
    private String method;
    /**
     * 传入业务方法的参数，交由fastjson解析成bolt的中方法参数
     */
    private  T data;

    public DrpcRequest() {
    }

    public DrpcRequest(String bolt, String method) {
        this.bolt = bolt;
        this.method = method;
    }

    public DrpcRequest(String bolt, String method, T data) {
        this.bolt = bolt;
        this.method = method;
        this.data = data;
    }

    public String getBolt() {
        return bolt;
    }

    public void setBolt(String bolt) {
        this.bolt = bolt;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toRequest(){
        return JsonUtil.toJSONString(this);
    }
}
