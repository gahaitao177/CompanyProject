package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by Linxingyu on 2017/3/21.
 */
public class CreditScoreNewDto {

    private String userId;//用户唯一ID
    private Integer zxId;//征信ID
    private String name;//姓名
    private String marStatus;//婚姻状态
    private Integer cardNum;//信用卡账户数
    private Integer cardOverdueStatus;//信用卡逾期状态
    private Integer loanOverdueStatus;//贷款逾期状态
    private Integer gjjNum;//公积金缴存月数
    private Double gjjMonthMoney;//公积金月缴额

    private Integer billId;//账单ID
    private Double cardQuota;//卡额度

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public Double getCardQuota() {
        return cardQuota;
    }

    public void setCardQuota(Double cardQuota) {
        this.cardQuota = cardQuota;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getZxId() {
        return zxId;
    }

    public void setZxId(Integer zxId) {
        this.zxId = zxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarStatus() {
        return marStatus;
    }

    public void setMarStatus(String marStatus) {
        this.marStatus = marStatus;
    }

    public Integer getCardOverdueStatus() {
        return cardOverdueStatus;
    }

    public void setCardOverdueStatus(Integer cardOverdueStatus) {
        this.cardOverdueStatus = cardOverdueStatus;
    }

    public Integer getLoanOverdueStatus() {
        return loanOverdueStatus;
    }

    public void setLoanOverdueStatus(Integer loanOverdueStatus) {
        this.loanOverdueStatus = loanOverdueStatus;
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public Integer getGjjNum() {
        return gjjNum;
    }

    public void setGjjNum(Integer gjjNum) {
        this.gjjNum = gjjNum;
    }

    public Double getGjjMonthMoney() {
        return gjjMonthMoney;
    }

    public void setGjjMonthMoney(Double gjjMonthMoney) {
        this.gjjMonthMoney = gjjMonthMoney;
    }
}
