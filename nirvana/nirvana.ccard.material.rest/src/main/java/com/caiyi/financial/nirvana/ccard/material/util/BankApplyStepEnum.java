package com.caiyi.financial.nirvana.ccard.material.util;

/**
 * Created by wsl on 2016/4/1.
 */
public enum BankApplyStepEnum {
    img_code("发送图片验证码"),
    phone_code("发送手机验证码"),
    submit_apply("提交申请"),
    query_apply("查询申请结果");

    private String value;

    BankApplyStepEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
