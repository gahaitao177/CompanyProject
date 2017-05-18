package com.caiyi.financial.nirvana.ccard.investigation.dto;

import java.util.Date;

/**
 * Created by jianghao on 2016/12/16.
 * 对应表 tb_zx_credit_investigation
 */
public class CreditInvestigationDto {
    private int creditId; //id
    private String title;//标题
    private String status;//状态
    private int isOverStay;//是否无逾期
    private int isCard;//是否有卡
    private int isLoan;//是否有贷款
    private Date addTime;
    private Date updateTime;
    private int addMonth;

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

    public int getIsOverStay() {
        return isOverStay;
    }

    public void setIsOverStay(int isOverStay) {
        this.isOverStay = isOverStay;
    }

    public int getIsCard() {
        return isCard;
    }

    public void setIsCard(int isCard) {
        this.isCard = isCard;
    }

    public int getIsLoan() {
        return isLoan;
    }

    public void setIsLoan(int isLoan) {
        this.isLoan = isLoan;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getAddMonth() {
        return addMonth;
    }

    public void setAddMonth(int addMonth) {
        this.addMonth = addMonth;
    }
}
