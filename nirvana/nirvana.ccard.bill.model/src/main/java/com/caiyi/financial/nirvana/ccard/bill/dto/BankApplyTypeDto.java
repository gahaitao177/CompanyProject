package com.caiyi.financial.nirvana.ccard.bill.dto;

/**
 * Created by Linxingyu on 2017/2/13.
 */
public class BankApplyTypeDto {
    private Integer ibankid;//银行ID
    private String msg;//短信申请方式
    private String msgnum;//短信申请号码
    private String ebanklink;//网银申请地址
    private String name;//银行名称
    private String wechat;//微信申请方式
    private String tel;//电话申请方式
    private String validity;//有效期
    private String ebank;//网银申请方式
    private String telnum;//电话申请号码

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgnum() {
        return msgnum;
    }

    public void setMsgnum(String msgnum) {
        this.msgnum = msgnum;
    }

    public String getEbanklink() {
        return ebanklink;
    }

    public void setEbanklink(String ebanklink) {
        this.ebanklink = ebanklink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getEbank() {
        return ebank;
    }

    public void setEbank(String ebank) {
        this.ebank = ebank;
    }

    public String getTelnum() {
        return telnum;
    }

    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }
}
