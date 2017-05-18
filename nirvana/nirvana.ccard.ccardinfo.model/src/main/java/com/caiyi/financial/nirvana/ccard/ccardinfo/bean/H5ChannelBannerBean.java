package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import java.util.Date;

public class H5ChannelBannerBean {
    private Long ibannerid;

    private String ctitle;

    private String cpicurl;

    private String curl;

    private Short iorder;

    private String cadduser;

    private Date cadddate;

    private String cupdateuser;

    private Date cupdatedate;

    public Long getIbannerid() {
        return ibannerid;
    }

    public void setIbannerid(Long ibannerid) {
        this.ibannerid = ibannerid;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle == null ? null : ctitle.trim();
    }

    public String getCpicurl() {
        return cpicurl;
    }

    public void setCpicurl(String cpicurl) {
        this.cpicurl = cpicurl == null ? null : cpicurl.trim();
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl == null ? null : curl.trim();
    }

    public Short getIorder() {
        return iorder;
    }

    public void setIorder(Short iorder) {
        this.iorder = iorder;
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
}