package com.caiyi.financial.nirvana.discount.ccard.dto;

import java.util.Date;

/**
 * 优惠详情接口中
 * 门店单个优惠的dto
 * Created by wenshiliang on 2016/5/9.
 */
public class CheapDto {
    private String icheapid;
    private String ctitle;
    private String ishortname;
    private String cbankname;
    private String cruledesc;
    private String cptype;
    private String ccontent;
    private Date cstartdate;
    private Date cenddate;
    private Integer iweek;
    private String activitytime;


    public String getIcheapid() {
        return icheapid;
    }

    public void setIcheapid(String icheapid) {
        this.icheapid = icheapid;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getIshortname() {
        return ishortname;
    }

    public void setIshortname(String ishortname) {
        this.ishortname = ishortname;
    }

    public String getCbankname() {
        return cbankname;
    }

    public void setCbankname(String cbankname) {
        this.cbankname = cbankname;
    }

    public String getCruledesc() {
        return cruledesc;
    }

    public void setCruledesc(String cruledesc) {
        this.cruledesc = cruledesc;
    }

    public String getCptype() {
        return cptype;
    }

    public void setCptype(String cptype) {
        this.cptype = cptype;
    }

    public String getCcontent() {
        return ccontent;
    }

    public void setCcontent(String ccontent) {
        this.ccontent = ccontent;
    }

    public Date getCstartdate() {
        return cstartdate;
    }

    public void setCstartdate(Date cstartdate) {
        this.cstartdate = cstartdate;
    }

    public Date getCenddate() {
        return cenddate;
    }

    public void setCenddate(Date cenddate) {
        this.cenddate = cenddate;
    }

    public Integer getIweek() {
        return iweek;
    }

    public void setIweek(Integer iweek) {
        this.iweek = iweek;
    }

    public String getActivitytime() {
        return activitytime;
    }

    public void setActivitytime(String activitytime) {
        this.activitytime = activitytime;
    }
}
