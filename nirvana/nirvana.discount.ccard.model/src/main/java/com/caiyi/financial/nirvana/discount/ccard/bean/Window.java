package com.caiyi.financial.nirvana.discount.ccard.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by A-0106 on 2016/5/19.
 */
public class Window extends BaseBean {

    private static final long serialVersionUID = 1L;
    // 所在城市ID
    private String cityId;
    // 用户经度
    private String userLot;
    // 用户纬度
    private String userLat;
    private int tp = 0;//总页数
    private int ps = 25;//页面大小
    private int pn = 1;//页码
    private int rc = 0;//总记录数
    private String topicId;
    // add by lcs 20150629
    // 关注银行id
    private String bankid;
    private String query = "";
    public String getBankid() {
        return bankid;
    }
    public void setBankid(String bankid) {
        this.bankid = bankid;
    }
    public String getSorttype() {
        return sorttype;
    }
    public void setSorttype(String sorttype) {
        this.sorttype = sorttype;
    }
    // 排序
    private String sorttype;

    // 设备类型
    private String type = "ad";
    private String info1 = "";
    private String packagename;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getInfo1() {
        return info1;
    }
    public void setInfo1(String info1) {
        this.info1 = info1;
    }
    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    private int startNum;
    private int endNum;

    public int getStartNum() {
        return startNum;
    }
    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }
    public int getEndNum() {
        return endNum;
    }
    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }
    public int getTp() {
        return tp;
    }
    public void setTp(int tp) {
        this.tp = tp;
    }
//    public int getPs() {return ps;}
//    public void setPs(int ps) {
//        this.ps = ps;
//    }
//    public int getPn() {
//        return pn;
//    }
//    public void setPn(int pn) {
//        this.pn = pn;
//    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public int getRc() {
        return rc;
    }
    public void setRc(int rc) {
        this.rc = rc;
    }
    public String getCityId() {
        return cityId;
    }
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
    public String getUserLot() {
        return userLot;
    }
    public void setUserLot(String userLot) {
        this.userLot = userLot;
    }
    public String getUserLat() {
        return userLat;
    }
    public void setUserLat(String userLat) {
        this.userLat = userLat;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
}

