package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Linxingyu on 2016/12/15.
 */
public class BillDetailSelectedDto {
    private Double money;
    private String billDate;
    private String billDesc;
    private Integer consumeType;

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillDesc() {
        return billDesc;
    }

    public void setBillDesc(String billDesc) {
        this.billDesc = billDesc;
    }

    public Integer getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Integer consumeType) {
        this.consumeType = consumeType;
    }
}
