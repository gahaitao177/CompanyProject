package com.caiyi.financial.nirvana.ccard.bill.dto;

import java.util.Date;

/**
 * Created by ljl on 2016/7/11.
 */
public class ConsumeTaskDto {
    private int itaskid;
    private String cuserid;
    private int ibankid;
    private int itype;
    private int istate;
    private String cdesc;
    private String curlparams;
    private int isend;
    private String caccountname;
    private Date cadddate;
    private Date cupdate;

    public int getItaskid() {
        return itaskid;
    }

    public void setItaskid(int itaskid) {
        this.itaskid = itaskid;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public int getIbankid() {
        return ibankid;
    }

    public void setIbankid(int ibankid) {
        this.ibankid = ibankid;
    }

    public int getItype() {
        return itype;
    }

    public void setItype(int itype) {
        this.itype = itype;
    }

    public int getIstate() {
        return istate;
    }

    public void setIstate(int istate) {
        this.istate = istate;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public String getCurlparams() {
        return curlparams;
    }

    public void setCurlparams(String curlparams) {
        this.curlparams = curlparams;
    }

    public int getIsend() {
        return isend;
    }

    public void setIsend(int isend) {
        this.isend = isend;
    }

    public String getCaccountname() {
        return caccountname;
    }

    public void setCaccountname(String caccountname) {
        this.caccountname = caccountname;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public Date getCupdate() {
        return cupdate;
    }

    public void setCupdate(Date cupdate) {
        this.cupdate = cupdate;
    }
}
