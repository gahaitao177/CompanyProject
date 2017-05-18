package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by jianghao on 2016/12/16.
 * 对应表 tb_zx_credit_card
 */
public class CreditCardDto {
    private int creditCardId;//信用卡id
    private String title;//标题
    private String status;//状态
    private String addTime;//添加时间
    private String updateTime;//更新时间
    private String account;//信用卡账号
    private double totalNum;//信用卡额度
    private double shouldPayment;//应还额度
    private int billId;  //账单id
    private int repay; //是否还清
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

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTIme() {
        return updateTime;
    }

    public void setUpdateTIme(String updateTIme) {
        this.updateTime = updateTIme;
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

    public double getShouldPayment() {
        return shouldPayment;
    }

    public void setShouldPayment(double shouldPayment) {
        this.shouldPayment = shouldPayment;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getRepay() {
        return repay;
    }

    public void setRepay(int repay) {
        this.repay = repay;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }
}
