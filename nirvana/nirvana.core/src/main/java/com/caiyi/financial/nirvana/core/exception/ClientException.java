package com.caiyi.financial.nirvana.core.exception;

/**
 * Created by wenshiliang on 2016/6/14.
 */
public class ClientException extends BaseException {

    public ClientException() {
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String code, String message) {
        super(code, message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
