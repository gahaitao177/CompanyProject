package com.caiyi.financial.nirvana.ccard.material.exception;

import com.caiyi.financial.nirvana.core.exception.BaseException;

/**
 * Created by lizhijie on 2016/7/18.
 */
public class MaterialExcetion extends BaseException {

    public MaterialExcetion() {
    }

    public MaterialExcetion(String message) {
        super(message);
    }

    public MaterialExcetion(String code, String message) {
        super(code, message);
    }

    public MaterialExcetion(String message, Throwable cause) {
        super(message, cause);
    }

    public MaterialExcetion(Throwable cause) {
        super(cause);
    }

    public MaterialExcetion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
