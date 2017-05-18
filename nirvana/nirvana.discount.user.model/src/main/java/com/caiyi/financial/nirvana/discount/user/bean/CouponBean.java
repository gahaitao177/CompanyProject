package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/7/26.
 */
public class CouponBean {
    private  String  clogo;
    private  String clogolist;
    private  Integer icheapid;
    private  Integer imarketid;
    private  String  cdiscount;
    private  String  cname;
    private  String  ctitle;
    private  String  cimgurl;
    private  Date    cenddate;
    private  Integer  iexpire;

    public String getClogo() {
        return clogo;
    }

    public void setClogo(String clogo) {
        this.clogo = clogo;
    }

    public String getClogolist() {
        return clogolist;
    }

    public void setClogolist(String clogolist) {
        this.clogolist = clogolist;
    }

    public Integer getIcheapid() {
        return icheapid;
    }

    public void setIcheapid(Integer icheapid) {
        this.icheapid = icheapid;
    }

    public Integer getImarketid() {
        return imarketid;
    }

    public void setImarketid(Integer imarketid) {
        this.imarketid = imarketid;
    }

    public String getCdiscount() {
        return cdiscount;
    }

    public void setCdiscount(String cdiscount) {
        this.cdiscount = cdiscount;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCimgurl() {
        return cimgurl;
    }

    public void setCimgurl(String cimgurl) {
        this.cimgurl = cimgurl;
    }

    public Date getCenddate() {
        return cenddate;
    }

    public void setCenddate(Date cenddate) {
        this.cenddate = cenddate;
    }

    public Integer getIexpire() {
        return iexpire;
    }

    public void setIexpire(Integer iexpire) {
        this.iexpire = iexpire;
    }
}
