package com.caiyi.financial.nirvana.ccard.bill.mail.exception;

import com.caiyi.financial.nirvana.core.exception.BaseException;

/**
 * Created by lcs on 2016/5/6.
 */
public class MailException extends BaseException {

    public MailException() {
    }

    public MailException(String message) {
        super(message);
    }

//    public CheapException(String code, String message) {
//        super(code, message);
//    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailException(Throwable cause) {
        super(cause);
    }

    public MailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
