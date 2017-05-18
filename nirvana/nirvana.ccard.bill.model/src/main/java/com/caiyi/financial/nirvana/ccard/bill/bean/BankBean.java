package com.caiyi.financial.nirvana.ccard.bill.bean;

/**
 * Created by Linxingyu on 2017/1/12.
 * 抓取银行网点时用到的银行信息
 */
public class BankBean {
    private Integer bankId;//银行id
    private String bankName;//银行名称
    private String gaodeTypes;//查询高德api用到的参数

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getGaodeTypes() {
        return gaodeTypes;
    }

    public void setGaodeTypes(String gaodeTypes) {
        this.gaodeTypes = gaodeTypes;
    }

    public BankBean(Integer bankId, String bankName, String gaodeTypes) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.gaodeTypes = gaodeTypes;
    }
}
