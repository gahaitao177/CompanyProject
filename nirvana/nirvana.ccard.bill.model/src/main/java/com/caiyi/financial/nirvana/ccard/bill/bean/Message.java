package com.caiyi.financial.nirvana.ccard.bill.bean;

import java.util.Date;

/**
 * Created by lizhijie on 2016/9/18.
 */
public class Message {
    private  Integer msgid;
    private  String title; //主标题
    private  String subtitle;//副标题
    private  String imgurl;
    private  String desc1; //消息描述1
    private  String desc2;
    private  String desc3;
    private  String target;
    // 【出账单通知0】【需要还款通知1】【微信还款支付通知2】【微信还款成功3】
    // 【账单更新提醒4】【资讯提醒5】
    private  Integer type;//消息类型

    private Date addTime; //消息添加时间

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getMsgid() {
        return msgid;
    }

    public void setMsgid(Integer msgid) {
        this.msgid = msgid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getDesc3() {
        return desc3;
    }

    public void setDesc3(String desc3) {
        this.desc3 = desc3;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
