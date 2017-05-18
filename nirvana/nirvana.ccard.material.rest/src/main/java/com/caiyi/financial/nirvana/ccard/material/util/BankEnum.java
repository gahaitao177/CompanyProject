package com.caiyi.financial.nirvana.ccard.material.util;

/**
 * Created by wsl on 2016/4/1.
 * update by lcs on 20160719 add  花旗&渣打
 */
public enum BankEnum {
    guangda("光大"),guangfa("广发"),jiaotong("交通"),minsheng("民生"),pingan("平安"),xingye("兴业"),zhongxin("中信"),huaqi("花旗"),zhada("渣打");

    private String value;

    BankEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
