package com.caiyi.financial.nirvana.discount.exception;

import com.caiyi.financial.nirvana.core.exception.BaseException;

/**
 * Created by wenshiliang on 2016/5/6.
 */
public class CheapException extends BaseException {

    public CheapException() {
    }

    public CheapException(String message) {
        super(message);
    }

    public CheapException(String code, String message) {
        super(code, message);
    }

    public CheapException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheapException(Throwable cause) {
        super(cause);
    }

    public CheapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
