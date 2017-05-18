package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by terry on 2016/8/22.
 */
public class BillDetailCountDayDto {
    private String ccountday;
    private Integer ibankid;
    private String cdesc;
    private Integer ifailcount;
    private Integer itype;

    public String getCcountday() {
        return ccountday;
    }

    public void setCcountday(String ccountday) {
        this.ccountday = ccountday;
    }

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public Integer getIfailcount() {
        return ifailcount;
    }

    public void setIfailcount(Integer ifailcount) {
        this.ifailcount = ifailcount;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }
}
