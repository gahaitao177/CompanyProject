package com.caiyi.financial.nirvana.discount.user.exception;

import com.caiyi.financial.nirvana.core.exception.BaseException;

/**
 * Created by heshaohua on 2016/5/20.
 */
public class UserException extends BaseException {
    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String code, String message) {
        super(code, message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
