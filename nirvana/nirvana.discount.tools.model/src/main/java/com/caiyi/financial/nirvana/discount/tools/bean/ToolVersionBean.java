package com.caiyi.financial.nirvana.discount.tools.bean;

import java.util.Date;

/**
 * Created by dengh on 2016/8/11.
 */
public class ToolVersionBean {
    private String ctoolid;
    private Integer itype;
    private String csource;
    private String cversion;
    private Date   cadddate;
    private String cadduser;
    private String cdownloadurl;
    private Integer iisopen;
    private Date copendate;
    private String copenuser;
    private String ccontent;

    public String getCtoolid() {
        return ctoolid;
    }

    public void setCtoolid(String ctoolid) {
        this.ctoolid = ctoolid;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public String getCsource() {
        return csource;
    }

    public void setCsource(String csource) {
        this.csource = csource;
    }

    public String getCversion() {
        return cversion;
    }

    public void setCversion(String cversion) {
        this.cversion = cversion;
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

    public String getCdownloadurl() {
        return cdownloadurl;
    }

    public void setCdownloadurl(String cdownloadurl) {
        this.cdownloadurl = cdownloadurl;
    }

    public Integer getIisopen() {
        return iisopen;
    }

    public void setIisopen(Integer iisopen) {
        this.iisopen = iisopen;
    }

    public Date getCopendate() {
        return copendate;
    }

    public void setCopendate(Date copendate) {
        this.copendate = copendate;
    }

    public String getCopenuser() {
        return copenuser;
    }

    public void setCopenuser(String copenuser) {
        this.copenuser = copenuser;
    }

    public String getCcontent() {
        return ccontent;
    }

    public void setCcontent(String ccontent) {
        this.ccontent = ccontent;
    }
}
