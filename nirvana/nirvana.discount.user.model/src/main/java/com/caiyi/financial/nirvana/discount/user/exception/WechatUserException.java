package com.caiyi.financial.nirvana.discount.user.exception;

/**
 * Created by wenshiliang on 2016/11/8.
 */
public class WechatUserException extends UserException {

    public WechatUserException() {
    }

    public WechatUserException(String message) {
        super(message);
    }

    public WechatUserException(String code, String message) {
        super(code, message);
    }

    public WechatUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public WechatUserException(Throwable cause) {
        super(cause);
    }

    public WechatUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
