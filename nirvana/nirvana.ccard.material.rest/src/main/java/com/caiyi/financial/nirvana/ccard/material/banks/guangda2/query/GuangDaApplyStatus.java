package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.query;

import java.io.Serializable;

/**
 * Created by wsl on 2016/2/29.
 */
public class GuangDaApplyStatus implements Serializable {
    private String name;//持卡人
    private String cardName;//申请卡种
    private String replyCardName;//批复卡种
    private String date;//进件日期
    private String stateValue;//主卡申请状态
    private String waybillNo;//运单号
    private int state = 0;//主卡申请状态

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getReplyCardName() {
        return replyCardName;
    }

    public void setReplyCardName(String replyCardName) {
        this.replyCardName = replyCardName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

}
