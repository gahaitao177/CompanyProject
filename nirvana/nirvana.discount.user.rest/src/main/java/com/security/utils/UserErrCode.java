//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.security.utils;

public class UserErrCode {
    public static final int ERR_CHECK = 1000;
    public static final int ERR_CALL_SP = 1001;
    public static final int ERR_EXCEPTION = 1002;
    public static final int ERR_USER_NOT_EXITS = 2000;

    public UserErrCode() {
    }

    public static final String getErrDesc(int errCode) {
        String errDesc = "未知错误[" + errCode + "]";
        switch(errCode) {
        case 1000:
            errDesc = "验证参数失败";
            break;
        case 1001:
            errDesc = "调用存储过程失败";
            break;
        case 1002:
            errDesc = "调用出现异常";
            break;
        case 2000:
            errDesc = "用户不存在";
            break;
        default:
            errDesc = "未知错误[" + errCode + "]";
        }

        return errDesc;
    }
}
