package com.caiyi.financial.nirvana.discount.ccard.dto;

import java.util.List;

/**
 * 地区级联
 * Created by heshaohua on 2016/5/11.
 */
public class AreaDto {
    /** 地区ID **/
    private String iareaid;
    /** 地区名称 **/
    private String careaname;
    /** 地区经度 **/
    private String clat;
    /** 地区纬度 **/
    private String clng;
    /** 该地区下优惠数量 **/
    private String icount;
    /** 地区类型 **/
    private String iareatype;
    /** 是否根级 **/
    private String iroot;
    /** 地区父类ID **/
    private String ipareaid;
    /** 城市code **/
    private String citycode;
    /** 高等序号 **/
    private String adcode;

    private String isleaf;


    public List<AreaDto> getChild() {
        return child;
    }

    public void setChild(List<AreaDto> child) {
        this.child = child;
    }

    List<AreaDto> child;

    public String getIsleaf() {
        return isleaf;
    }

    public void setIsleaf(String isleaf) {
        this.isleaf = isleaf;
    }

    public String getIareaid() {
        return iareaid;
    }

    public void setIareaid(String iareaid) {
        this.iareaid = iareaid;
    }

    public String getCareaname() {
        return careaname;
    }

    public void setCareaname(String careaname) {
        this.careaname = careaname;
    }

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public String getClng() {
        return clng;
    }

    public void setClng(String clng) {
        this.clng = clng;
    }

    public String getIcount() {
        return icount;
    }

    public void setIcount(String icount) {
        this.icount = icount;
    }

    public String getIareatype() {
        return iareatype;
    }

    public void setIareatype(String iareatype) {
        this.iareatype = iareatype;
    }

    public String getIroot() {
        return iroot;
    }

    public void setIroot(String iroot) {
        this.iroot = iroot;
    }

    public String getIpareaid() {
        return ipareaid;
    }

    public void setIpareaid(String ipareaid) {
        this.ipareaid = ipareaid;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }
}
