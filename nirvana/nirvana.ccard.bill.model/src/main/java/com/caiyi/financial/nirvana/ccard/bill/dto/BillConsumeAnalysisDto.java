package com.caiyi.financial.nirvana.ccard.bill.dto;

import java.text.DecimalFormat;

/**
 * Created byLinxingyu on 2016/12/19.
 * 消费类型分析
 */
public class BillConsumeAnalysisDto {


    private Integer consumeTypeId;
    private String consumeType;
    private String money;

    public Integer getConsumeTypeId() {
        return consumeTypeId;
    }

    public void setConsumeTypeId(Integer consumeTypeId) {
        this.consumeTypeId = consumeTypeId;
    }

    public String getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

}
