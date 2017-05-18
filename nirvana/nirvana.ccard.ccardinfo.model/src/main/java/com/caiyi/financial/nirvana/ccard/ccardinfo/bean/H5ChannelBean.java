package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import java.util.Date;

public class H5ChannelBean {
    private Long ichannelid;

    private String cname;

    private String curl;

    private Short istatus;

    private Long iclicknum;

    private String cadduser;

    private Date cadddate;

    private String cupdateuser;

    private Date cupdatedate;

    private String cchannelurl;

    public Long getIchannelid() {
        return ichannelid;
    }

    public void setIchannelid(Long ichannelid) {
        this.ichannelid = ichannelid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname == null ? null : cname.trim();
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl == null ? null : curl.trim();
    }

    public Short getIstatus() {
        return istatus;
    }

    public void setIstatus(Short istatus) {
        this.istatus = istatus;
    }

    public Long getIclicknum() {
        return iclicknum;
    }

    public void setIclicknum(Long iclicknum) {
        this.iclicknum = iclicknum;
    }

    public String getCadduser() {
        return cadduser;
    }

    public void setCadduser(String cadduser) {
        this.cadduser = cadduser == null ? null : cadduser.trim();
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCupdateuser() {
        return cupdateuser;
    }

    public void setCupdateuser(String cupdateuser) {
        this.cupdateuser = cupdateuser == null ? null : cupdateuser.trim();
    }

    public Date getCupdatedate() {
        return cupdatedate;
    }

    public void setCupdatedate(Date cupdatedate) {
        this.cupdatedate = cupdatedate;
    }

    public String getCchannelurl() {
        return cchannelurl;
    }

    public void setCchannelurl(String cchannelurl) {
        this.cchannelurl = cchannelurl == null ? null : cchannelurl.trim();
    }
}