package com.caiyi.financial.nirvana.ccard.bill.dto;

import java.sql.Timestamp;

/**
 * Created by lizhijie on 2016/9/18.
 */
public class MessageDto {
    private  String imsgid;
    private  String ctitle; //主标题
    private  String csubtitle;//副标题
    private  String cimgurl;
    private  String cdesc1; //消息描述1
    private  String cdesc2;
    private  String cdesc3;
    private  String ctarget;

    // 【出账单通知0】【需要还款通知1】【微信还款支付通知2】【微信还款成功3】
    // 【账单更新提醒4】【资讯提醒5】
    private  String itype;//消息类型

    private Timestamp caddTime; //消息添加时间

    private String cuserid;

    private String convertTime;

    public String getImsgid() {
        return imsgid;
    }

    public void setImsgid(String imsgid) {
        this.imsgid = imsgid;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCsubtitle() {
        return csubtitle;
    }

    public void setCsubtitle(String csubtitle) {
        this.csubtitle = csubtitle;
    }

    public String getCimgurl() {
        return cimgurl;
    }

    public void setCimgurl(String cimgurl) {
        this.cimgurl = cimgurl;
    }

    public String getCdesc1() {
        return cdesc1;
    }

    public void setCdesc1(String cdesc1) {
        this.cdesc1 = cdesc1;
    }

    public String getCdesc2() {
        return cdesc2;
    }

    public void setCdesc2(String cdesc2) {
        this.cdesc2 = cdesc2;
    }

    public String getCdesc3() {
        return cdesc3;
    }

    public void setCdesc3(String cdesc3) {
        this.cdesc3 = cdesc3;
    }

    public String getCtarget() {
        return ctarget;
    }

    public void setCtarget(String ctarget) {
        this.ctarget = ctarget;
    }

    public String getItype() {
        return itype;
    }

    public void setItype(String itype) {
        this.itype = itype;
    }

    public Timestamp getCaddTime() {
        return caddTime;
    }

    public void setCaddTime(Timestamp caddTime) {
        this.caddTime = caddTime;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getConvertTime() {
        return convertTime;
    }

    public void setConvertTime(String convertTime) {
        this.convertTime = convertTime;
    }
}
