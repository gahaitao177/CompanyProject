package com.caiyi.financial.nirvana.ccard.bill.dto;

import java.util.Date;

/**
 * Created by terry on 2016/6/14.
 */
public class ImportTaskDto {
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

    private int isauto;

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
        this.cuserid = cuserid == null ? null : cuserid.trim();
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
        this.cdesc = cdesc == null ? null : cdesc.trim();
    }

    public String getCurlparams() {
        return curlparams;
    }

    public void setCurlparams(String curlparams) {
        this.curlparams = curlparams == null ? null : curlparams.trim();
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
        this.caccountname = caccountname == null ? null : caccountname.trim();
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

    public int getIsauto() {
        return isauto;
    }

    public void setIsauto(int isauto) {
        this.isauto = isauto;
    }
}
