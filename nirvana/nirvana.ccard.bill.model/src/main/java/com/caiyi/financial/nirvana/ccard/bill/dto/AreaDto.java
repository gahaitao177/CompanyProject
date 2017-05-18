package com.caiyi.financial.nirvana.ccard.bill.dto;

import java.util.Date;

/**
 * Created by Linxingyu on 2017/1/12.
 * TB_AREA
 */
public class AreaDto {

    private Integer iareaid;//地区id
    private String careaname;//地区名称
    private String clng;//经度
    private String clat;//纬度
    private Integer iareatype;
    private Integer iroot;
    private Integer icount;
    private Integer ipareaid;
    private String citycode;
    private String iamapid;
    private String adcode;
    private Date caddtime;

    public Integer getIareaid() {
        return iareaid;
    }

    public void setIareaid(Integer iareaid) {
        this.iareaid = iareaid;
    }

    public String getCareaname() {
        return careaname;
    }

    public void setCareaname(String careaname) {
        this.careaname = careaname;
    }

    public String getClng() {
        return clng;
    }

    public void setClng(String clng) {
        this.clng = clng;
    }

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public Integer getIareatype() {
        return iareatype;
    }

    public void setIareatype(Integer iareatype) {
        this.iareatype = iareatype;
    }

    public Integer getIroot() {
        return iroot;
    }

    public void setIroot(Integer iroot) {
        this.iroot = iroot;
    }

    public Integer getIcount() {
        return icount;
    }

    public void setIcount(Integer icount) {
        this.icount = icount;
    }

    public Integer getIpareaid() {
        return ipareaid;
    }

    public void setIpareaid(Integer ipareaid) {
        this.ipareaid = ipareaid;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getIamapid() {
        return iamapid;
    }

    public void setIamapid(String iamapid) {
        this.iamapid = iamapid;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public Date getCaddtime() {
        return caddtime;
    }

    public void setCaddtime(Date caddtime) {
        this.caddtime = caddtime;
    }
}
