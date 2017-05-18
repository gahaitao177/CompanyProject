package com.caiyi.financial.nirvana.ccard.ccardinfo.exception;

import com.caiyi.financial.nirvana.core.exception.BaseException;

/**
 * Created by lizhijie on 2016/6/20.
 */
public class CardinfoException extends BaseException {

    public CardinfoException() {
    }

    public CardinfoException(String message) {
        super(message);
    }

    public CardinfoException(String code, String message) {
        super(code, message);
    }

    public CardinfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardinfoException(Throwable cause) {
        super(cause);
    }

    public CardinfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
