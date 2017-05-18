package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Linxingyu on 2017/2/6.
 */
public class CardQuotaInfoDto {
    private Integer imonthid;
    private String cmonth;//月份
    private Double imonthquota;//当月额度
    private Double ishouldrepayment;//使用额度
    private Double ilowestrepayment;//最低应还额度
    private String crepaymentdate;//还款日
    private Double itotalquota;//总额度

    public Double getItotalquota() {
        return itotalquota;
    }

    public void setItotalquota(Double itotalquota) {
        this.itotalquota = itotalquota;
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

    public Double getImonthquota() {
        return imonthquota;
    }

    public void setImonthquota(Double imonthquota) {
        this.imonthquota = imonthquota;
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
