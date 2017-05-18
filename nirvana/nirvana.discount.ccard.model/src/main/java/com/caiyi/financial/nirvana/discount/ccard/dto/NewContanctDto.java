package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by lizhijie on 2016/10/25.
 */
public class NewContanctDto {
    private  String caccessulr;
    private  Integer icontactid;
    private  String ccontent;
    private  String cpicurl;
    private  String ctitle;
    private  String csummary;

    public String getCaccessulr() {
        return caccessulr;
    }

    public void setCaccessulr(String caccessulr) {
        this.caccessulr = caccessulr;
    }

    public Integer getIcontactid() {
        return icontactid;
    }

    public void setIcontactid(Integer icontactid) {
        this.icontactid = icontactid;
    }

    public String getCcontent() {
        return ccontent;
    }

    public void setCcontent(String ccontent) {
        this.ccontent = ccontent;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCpicurl() {
        return cpicurl;
    }

    public void setCpicurl(String cpicurl) {
        this.cpicurl = cpicurl;
    }

    public String getCsummary() {
        return csummary;
    }

    public void setCsummary(String csummary) {
        this.csummary = csummary;
    }
}
