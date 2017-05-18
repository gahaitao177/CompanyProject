package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by lizhijie on 2017/1/11. 推荐卡
 */
public class RecommendCardBean extends BaseBean {
    private  int cardId;
    private  String picURL;
    private  int orderNum;
    private  String cardName;
    private  String bankIds;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBankIds() {
        return bankIds;
    }

    public void setBankIds(String bankIds) {
        this.bankIds = bankIds;
    }
}
