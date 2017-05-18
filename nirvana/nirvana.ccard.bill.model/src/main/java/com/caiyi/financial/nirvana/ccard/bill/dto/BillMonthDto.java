package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Mario on 2016/7/15 0015.
 * TB_BILL_MONTH
 */
public class BillMonthDto {
    private Integer imonthid;
    private String cmonth;
    private Integer ibillid;
    private String cbilldate;
    private String crepaymentdate;
    private Double ishouldrepayment;
    private Double ilowestrepayment;
    private Integer isbill;
    private Integer iscomplete;

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

    public Integer getIbillid() {
        return ibillid;
    }

    public void setIbillid(Integer ibillid) {
        this.ibillid = ibillid;
    }

    public String getCbilldate() {
        return cbilldate;
    }

    public void setCbilldate(String cbilldate) {
        this.cbilldate = cbilldate;
    }

    public String getCrepaymentdate() {
        return crepaymentdate;
    }

    public void setCrepaymentdate(String crepaymentdate) {
        this.crepaymentdate = crepaymentdate;
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

    public Integer getIsbill() {
        return isbill;
    }

    public void setIsbill(Integer isbill) {
        this.isbill = isbill;
    }

    public Integer getIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(Integer iscomplete) {
        this.iscomplete = iscomplete;
    }

}
