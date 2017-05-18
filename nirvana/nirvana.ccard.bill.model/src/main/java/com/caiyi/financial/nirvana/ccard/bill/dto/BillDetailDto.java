package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Mario on 2016/7/15 0015.
 * TB_BILL_DETAIL
 */
public class BillDetailDto {
    private Integer idetailid;
    private Integer imonthid;
    private Integer itype;
    private String imoney;
    private String coccurdate;
    private String ctradedate;
    private String ctradeaddr;
    private String cdesc;
    private Integer icurrency;
    private Integer icosttype;
    private String cmonth;
    private Integer itypeupdate;
    private String ccosttypename;

    public String getCcosttypename() {
        return ccosttypename;
    }

    public void setCcosttypename(String ccosttypename) {
        this.ccosttypename = ccosttypename;
    }

    public Integer getIdetailid() {
        return idetailid;
    }

    public void setIdetailid(Integer idetailid) {
        this.idetailid = idetailid;
    }

    public Integer getImonthid() {
        return imonthid;
    }

    public void setImonthid(Integer imonthid) {
        this.imonthid = imonthid;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public String getImoney() {
        return imoney;
    }

    public void setImoney(String imoney) {
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

    public String getCtradeaddr() {
        return ctradeaddr;
    }

    public void setCtradeaddr(String ctradeaddr) {
        this.ctradeaddr = ctradeaddr;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public Integer getIcurrency() {
        return icurrency;
    }

    public void setIcurrency(Integer icurrency) {
        this.icurrency = icurrency;
    }

    public Integer getIcosttype() {
        return icosttype;
    }

    public void setIcosttype(Integer icosttype) {
        this.icosttype = icosttype;
    }

    public String getCmonth() {
        return cmonth;
    }

    public void setCmonth(String cmonth) {
        this.cmonth = cmonth;
    }

    public Integer getItypeupdate() {
        return itypeupdate;
    }

    public void setItypeupdate(Integer itypeupdate) {
        this.itypeupdate = itypeupdate;
    }

}
