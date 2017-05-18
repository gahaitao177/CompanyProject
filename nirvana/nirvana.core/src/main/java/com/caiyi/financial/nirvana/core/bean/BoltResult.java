package com.caiyi.financial.nirvana.core.bean;


import com.caiyi.financial.nirvana.core.util.JsonUtil;

import java.io.Serializable;

/**
 * Created by wenshiliang on 2016/4/22.
 * 封装的drpc 标准返回值
 */
public class BoltResult<T> implements Serializable {
    public static final String SUCCESS = "1";
    public static final String ERROR_404 = "404";
    public static final String ERROR = "-1";
    public static final String Error_405 = "405";

    private String code;
    private String desc;
    private T data;

    public T getData() {
        return data;
    }

    public BoltResult(String code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BoltResult() {
    }

    public BoltResult(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String toJsonString(){
        return JsonUtil.toJSONString(this);
    }

    /**
     * 该方法针对通用设计 code=1 代表成功
     * 但是存在特殊情况， 修改老方法的时候需要注意
     * @return
     */
    public boolean isSuccess(){
        if(SUCCESS.equals(code)){
            return true;
        }
        return false;
    }
}

