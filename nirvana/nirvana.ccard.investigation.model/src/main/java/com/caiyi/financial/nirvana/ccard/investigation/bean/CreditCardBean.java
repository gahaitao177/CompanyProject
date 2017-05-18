package com.caiyi.financial.nirvana.ccard.investigation.bean;

/**
 * Created by jianghao on 2016/12/16.
 * 对应表 tb_zx_credit_card
 */
public class CreditCardBean {
    private int creditCardId;//信用卡id
    private String title;//标题
    private String status;//状态
    private String account;//信用卡账号
    private double  totalNum;//信用卡额度
    private int cardNum; //信用卡数量

    public int getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(int creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(double totalNum) {
        this.totalNum = totalNum;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }
}
