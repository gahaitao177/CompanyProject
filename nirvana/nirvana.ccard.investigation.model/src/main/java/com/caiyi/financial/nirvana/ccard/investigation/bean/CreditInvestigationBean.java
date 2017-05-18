package com.caiyi.financial.nirvana.ccard.investigation.bean;

/**
 * Created by jianghao on 2016/12/16.
 * 对应表 tb_zx_credit_investigation
 */
public class CreditInvestigationBean {
    private int creditInvestigationId;//id
    private String title;//标题
    private Integer status;//状态
    private int isOverStay=-1;//是否无逾期  -1表示为空
    private int isCard=-1;//是否有卡 -1表示为空
    private int isLoan=-1;//是否有贷款  -1表示为空

    public int getCreditInvestigationId() {
        return creditInvestigationId;
    }

    public void setCreditInvestigationId(int creditInvestigationId) {
        this.creditInvestigationId = creditInvestigationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
}
