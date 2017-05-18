package com.caiyi.financial.nirvana.ccard.ccardinfo.dto;

/**
 * Created by lizhijie on 2016/11/21. 办卡进度
 */
public class CardProgressDto {
    private  String cbankname;
    private  String iapplyid;
    private  String applyStatus;  //申卡状态
    private  String isuccess;
    private  String cadddate;
    private  String orderStatus; //订单状态
    private  String cphone;
    private  String buyDate;
    private  String cloanname;

    public String getCbankname() {
        return cbankname;
    }

    public void setCbankname(String cbankname) {
        this.cbankname = cbankname;
    }

    public String getIapplyid() {
        return iapplyid;
    }

    public void setIapplyid(String iapplyid) {
        this.iapplyid = iapplyid;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getIsuccess() {
        return isuccess;
    }

    public void setIsuccess(String isuccess) {
        this.isuccess = isuccess;
    }

    public String getCadddate() {
        return cadddate;
    }

    public void setCadddate(String cadddate) {
        this.cadddate = cadddate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getCloanname() {
        return cloanname;
    }

    public void setCloanname(String cloanname) {
        this.cloanname = cloanname;
    }
}
