package com.caiyi.financial.nirvana.ccard.material.banks.guangda2;

/**
 * Created by wsl on 2016/2/23.
 */
public class GuangDaException extends RuntimeException {

    private int code = 0;

    public GuangDaException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GuangDaException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }
}
