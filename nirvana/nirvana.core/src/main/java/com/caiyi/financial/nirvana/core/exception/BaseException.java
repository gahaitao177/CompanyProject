package com.caiyi.financial.nirvana.core.exception;

import com.caiyi.financial.nirvana.core.bean.BoltResult;

/**
 * Created by wenshiliang on 2016/4/22.
 * 核心异常类。所有自定义异常继承这个类
 */
public abstract class BaseException extends RuntimeException {
    private String code = BoltResult.ERROR;

    public BoltResult getBoltResult(){
        return new BoltResult(code,this.getMessage()!=null ? this.getMessage():"出错");
    }

    public BaseException() {
        code = BoltResult.ERROR;
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String code,String message) {
        super(message);
        this.code = code;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
