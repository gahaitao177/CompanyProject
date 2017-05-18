package com.caiyi.nirvana.analyse.enums;

/**
 * Created by pc on 2017/3/9.
 */
public enum SystemEnum {
    PROVIDENT_FUND("公积金", "provident_fund"),
    SOCIAL_SECURITY("社保", "social_security"),
    ACCOUNT("记账", "account"),
    CREDIT_CARD("信用卡", "credit_card"),
    LOAN("贷款", "loan");

    private String name;
    private String code;

    private SystemEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(String code) {
        for (SystemEnum se : SystemEnum.values()) {
            if (se.getCode().equals(code)) {
                return se.name;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
