package com.caiyi.financial.nirvana.discount.ccard.dto;


import java.util.List;

/**
 * Created by A-0106 on 2016/5/19.
 */
public class WindowDto{
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

    private String busiXml =""; //业务处理后返回的XML
    private  String busiErrCode ; //返回代码
    private String busiErrDesc;//返回描述
    // 排序
    private String sorttype;
    // 设备类型
    private String type = "ad";
    private String info1 = "";
    private int startNum;
    private int endNum;
    //主题列表
    private List<TopicDto> topicList;
    private  List<TopicBussiDto> topicBussiDtoList;
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
    public int getPs() {
        return ps;
    }
    public void setPs(int ps) {
        this.ps = ps;
    }
    public int getPn() {
        return pn;
    }
    public void setPn(int pn) {
        this.pn = pn;
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
    public List<TopicDto> getTopicList() { return topicList;}
    public void setTopicList(List<TopicDto> topicList) { this.topicList = topicList;}
    public List<TopicBussiDto> getTopicBussiDtoList() {
        return topicBussiDtoList;
    }
    public void setTopicBussiDtoList(List<TopicBussiDto> topicBussiDtoList) {
        this.topicBussiDtoList = topicBussiDtoList;
    }
    public String getBusiXml() { return busiXml;}
    public void setBusiXml(String busiXml) {this.busiXml = busiXml;}
    public String getDesc() {return busiErrDesc; }
    public void setDesc(String desc) { this.busiErrDesc = desc; }
    public String getCode() {return busiErrCode; }
    public void setCode(String code) {this.busiErrCode = code;}
}

