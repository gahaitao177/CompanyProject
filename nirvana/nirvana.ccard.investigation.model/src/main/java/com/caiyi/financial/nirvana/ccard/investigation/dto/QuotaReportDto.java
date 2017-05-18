package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by Linxingyu on 2017/3/1.
 */
public class QuotaReportDto {
    private  Integer ibillid;
    private String cmonth;
    private String crepaymentdate;
    private Double ishouldrepayment;
    private Double ilowestrepayment;
    private Double imonthquota;
    private Double imoney;
    private String coccurdate;
    private String ctradedate;

    public Integer getIbillid() {
        return ibillid;
    }

    public void setIbillid(Integer ibillid) {
        this.ibillid = ibillid;
    }

    public String getCmonth() {
        return cmonth;
    }

    public void setCmonth(String cmonth) {
        this.cmonth = cmonth;
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

    public Double getImonthquota() {
        return imonthquota;
    }

    public void setImonthquota(Double imonthquota) {
        this.imonthquota = imonthquota;
    }

    public Double getImoney() {
        return imoney;
    }

    public void setImoney(Double imoney) {
        this.imoney = imoney;
    }

    public String getCoccurdate() {
        return coccurdate;
    }

    public void setCoccurdate(String coccurdate) {
        this.coccurdate = coccurdate;
    }

    public String getCtradedate() {
        return ctradedate;
    }

    public void setCtradedate(String ctradedate) {
        this.ctradedate = ctradedate;
    }

    @Override
    public String toString() {
        return "QuotaReportDto{" +
                "ibillid=" + ibillid +
                ", cmonth='" + cmonth + '\'' +
                ", crepaymentdate='" + crepaymentdate + '\'' +
                ", ishouldrepayment=" + ishouldrepayment +
                ", ilowestrepayment=" + ilowestrepayment +
                ", imonthquota=" + imonthquota +
                ", imoney=" + imoney +
                ", coccurdate='" + coccurdate + '\'' +
                ", ctradedate='" + ctradedate + '\'' +
                '}';
    }
}
