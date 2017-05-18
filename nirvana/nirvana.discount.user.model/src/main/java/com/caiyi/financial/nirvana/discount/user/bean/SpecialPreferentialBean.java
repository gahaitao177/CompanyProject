package com.caiyi.financial.nirvana.discount.user.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/8/5.
 */
public class SpecialPreferentialBean {
    private Integer   preferentialid;
    private String    ctitle;
    private String    curl;
    private String    cpicurl;
    private Integer   ipraisenum;
    private Date      cadddate;
    private String    cadduser;
    private Integer   is_hidden;
    private Integer   is_del;
    private String    citycodes;
    private String    ibankid;
    private String    ccontent;

    public Integer getPreferentialid() {
        return preferentialid;
    }

    public void setPreferentialid(Integer preferentialid) {
        this.preferentialid = preferentialid;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl;
    }

    public String getCpicurl() {
        return cpicurl;
    }

    public void setCpicurl(String cpicurl) {
        this.cpicurl = cpicurl;
    }

    public Integer getIpraisenum() {
        return ipraisenum;
    }

    public void setIpraisenum(Integer ipraisenum) {
        this.ipraisenum = ipraisenum;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCadduser() {
        return cadduser;
    }

    public void setCadduser(String cadduser) {
        this.cadduser = cadduser;
    }

    public Integer getIs_hidden() {
        return is_hidden;
    }

    public void setIs_hidden(Integer is_hidden) {
        this.is_hidden = is_hidden;
    }

    public Integer getIs_del() {
        return is_del;
    }

    public void setIs_del(Integer is_del) {
        this.is_del = is_del;
    }

    public String getCitycodes() {
        return citycodes;
    }

    public void setCitycodes(String citycodes) {
        this.citycodes = citycodes;
    }

    public String getIbankid() {
        return ibankid;
    }

    public void setIbankid(String ibankid) {
        this.ibankid = ibankid;
    }

    public String getCcontent() {
        return ccontent;
    }

    public void setCcontent(String ccontent) {
        this.ccontent = ccontent;
    }
}
