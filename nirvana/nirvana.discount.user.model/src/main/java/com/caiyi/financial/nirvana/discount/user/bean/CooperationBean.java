package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/7/29.
 */
public class CooperationBean {
    private String  cuserid;
    private String  ccooperationid;
    private Integer itype;
    private Integer ilogincount;
    private Date    cadddate;
    private Date    cupdate;
    private Integer istate;
    private String  cip;
    private Integer iclient;

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getCcooperationid() {
        return ccooperationid;
    }

    public void setCcooperationid(String ccooperationid) {
        this.ccooperationid = ccooperationid;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public Integer getIlogincount() {
        return ilogincount;
    }

    public void setIlogincount(Integer ilogincount) {
        this.ilogincount = ilogincount;
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

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public Integer getIclient() {
        return iclient;
    }

    public void setIclient(Integer iclient) {
        this.iclient = iclient;
    }
}
