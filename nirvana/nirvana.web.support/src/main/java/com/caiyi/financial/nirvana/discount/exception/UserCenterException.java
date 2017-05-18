package com.caiyi.financial.nirvana.discount.exception;

import com.caiyi.financial.nirvana.core.exception.BaseException;

/**
 * Created by liuweiguo on 2016/8/25.
 */
public class UserCenterException extends BaseException{
    public UserCenterException() {
    }

    public UserCenterException(String message) {
        super(message);
    }

    public UserCenterException(String code, String message) {
        super(code, message);
    }

    public UserCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCenterException(Throwable cause) {
        super(cause);
    }

    public UserCenterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
