package com.caiyi.nirvana.analyse.common;

/**
 * 账户流水
 *
 * @author DONGYA
 */
public class FundCharge {
    private String fundName; //账户名称
    private String income; //收入
    private String expend; //支出
    private String rate; //结余

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpend() {
        return expend;
    }

    public void setExpend(String expend) {
        this.expend = expend;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

}
