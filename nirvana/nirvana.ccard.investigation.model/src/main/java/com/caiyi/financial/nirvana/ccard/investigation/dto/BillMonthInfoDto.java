package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by Linxingyu on 2017/2/6.
 */
public class BillMonthInfoDto {
    private Integer imonthid;
    private String cmonth;//月份
    private Double ishouldrepayment;//使用额度
    private Double ilowestrepayment;//最低应还额度
    private String crepaymentdate;//还款日
    private Integer consumeNum;//月消费频次

    public Integer getConsumeNum() {
        return consumeNum;
    }

    public void setConsumeNum(Integer consumeNum) {
        this.consumeNum = consumeNum;
    }

    public String getCrepaymentdate() {
        return crepaymentdate;
    }

    public void setCrepaymentdate(String crepaymentdate) {
        this.crepaymentdate = crepaymentdate;
    }

    public Integer getImonthid() {
        return imonthid;
    }

    public void setImonthid(Integer imonthid) {
        this.imonthid = imonthid;
    }

    public String getCmonth() {
        return cmonth;
    }

    public void setCmonth(String cmonth) {
        this.cmonth = cmonth;
    }

    public Double getIshouldrepayment() {
        return ishouldrepayment;
    }

    public void setIshouldrepayment(Double ishouldrepayment) {
        this.ishouldrepayment = ishouldrepayment;
    }

    public Double getIlowestrepayment() {
        return ilowestrepayment;
    }

    public void setIlowestrepayment(Double ilowestrepayment) {
        this.ilowestrepayment = ilowestrepayment;
    }
}
