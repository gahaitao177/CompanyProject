package com.caiyi.financial.nirvana.core.exception;

/**
 * Created by wenshiliang on 2016/4/25.
 * bolt中出现的异常基类，包括创建bolt，basebolt，dispatcherBolt 中一些执行异常
 */
public class BoltException extends BaseException {

    public BoltException() {
    }

    public BoltException(String message) {
        super(message);
    }

    public BoltException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoltException(Throwable cause) {
        super(cause);
    }

    public BoltException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
