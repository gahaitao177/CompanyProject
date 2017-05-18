package com.caiyi.financial.nirvana.discount.ccard.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by heshaohua on 2016/5/3.
 */
public class Cheap extends BaseBean {
    private Long id; // 优惠编号
    private Long bankid; // 优惠编号
    private Long businessid; // 优惠编号
    private String title;// 优惠标题
    private String content;// 优惠内容
    private Integer pv;// 访问量
    private Integer pv1;// 收藏量
    private Integer pv2;// 是否限量
    private Integer pv3;// 是否长期有效
    private Integer pv4;// 是否限制卡中
    private Integer cityid;// 城市
    private Integer week;// 星期
    private Integer star;// 星级
    private Integer iopen;// 是否开启

    private Long icityid;//城市id

    private Integer ipareaid;

    private String istoreid;
    private String ibusinessid;

    private String citycode;

    private String query;

    private Double clat;
    private Double clng;

    private String searchtype = "0";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBankid() {
        return bankid;
    }

    public void setBankid(Long bankid) {
        this.bankid = bankid;
    }

    public Long getBusinessid() {
        return businessid;
    }

    public void setBusinessid(Long businessid) {
        this.businessid = businessid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public Integer getPv1() {
        return pv1;
    }

    public void setPv1(Integer pv1) {
        this.pv1 = pv1;
    }

    public Integer getPv2() {
        return pv2;
    }

    public void setPv2(Integer pv2) {
        this.pv2 = pv2;
    }

    public Integer getPv3() {
        return pv3;
    }

    public void setPv3(Integer pv3) {
        this.pv3 = pv3;
    }

    public Integer getPv4() {
        return pv4;
    }

    public void setPv4(Integer pv4) {
        this.pv4 = pv4;
    }

    public Integer getCityid() {
        return cityid;
    }

    public void setCityid(Integer cityid) {
        this.cityid = cityid;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getIopen() {
        return iopen;
    }

    public void setIopen(Integer iopen) {
        this.iopen = iopen;
    }

    public Long getIcityid() {
        return icityid;
    }

    public void setIcityid(Long icityid) {
        this.icityid = icityid;
    }

    public Integer getIpareaid() {
        return ipareaid;
    }

    public void setIpareaid(Integer ipareaid) {
        this.ipareaid = ipareaid;
    }

    public String getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(String istoreid) {
        this.istoreid = istoreid;
    }

    public String getIbusinessid() {
        return ibusinessid;
    }

    public void setIbusinessid(String ibusinessid) {
        this.ibusinessid = ibusinessid;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Double getClat() {
        return clat;
    }

    public void setClat(Double clat) {
        this.clat = clat;
    }

    public Double getClng() {
        return clng;
    }

    public void setClng(Double clng) {
        this.clng = clng;
    }

    public String getSearchtype() {
        return searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }
}
