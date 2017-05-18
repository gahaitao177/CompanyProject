package com.caiyi.financial.nirvana.ccard.bill.bean;

/**
 * Created by lichuanshun on 16/8/12.
 */
public class Bill {
    // 账单id
    private String billid;
    // 银行id
    private String bankid;
    // 银行名字
    private String bankname;
    // 信用卡尾号
    private String card4num;
    // 账单日
    private String billdate;
    // 剩余出账单日
    private String billdays;
    // 还款日
    private String paymentdate;
    // 剩余最后还款日 小于0表示超出还款日期
    private String paymentdays;
    // 未出账单金额
    private String unsettledbill;
    // 应还金额
    private String shouldpayment;
    // 可用额度
    private String availablebalance;
    // 总额度
    private String balance;
    // 导入类型 邮箱还是网银 0:网银 1:邮箱
    private String importtype;
    // 邮箱类型
    private String mailtype ;
    // 是否已还款标记：用户在前端设置 默认未还 0:未还 1:已还
    private String repayment;
    // 主卡副卡标记 1:主卡 2:副卡
    private String principal;
    // 距上一次更新时间
    private String fromlastupdate;
    // 是否更新标记：用于账单长久不更新提示用户更新
    private String updateflag;
    /**是否支持网银导入 1表示支持 0不支持*/
    private String supportebank;
    /**最低应还金额*/
    private String minshouldpayment;
    /**免息期*/
    private String freedays;
    /**积分*/
    private String integration;
    /**真实姓名*/
    private String realname;
    /**取现额度*/
    private String cash;

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getCard4num() {
        return card4num;
    }

    public void setCard4num(String card4num) {
        this.card4num = card4num;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public String getBilldays() {
        return billdays;
    }

    public void setBilldays(String billdays) {
        this.billdays = billdays;
    }

    public String getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(String paymentdate) {
        this.paymentdate = paymentdate;
    }

    public String getPaymentdays() {
        return paymentdays;
    }

    public void setPaymentdays(String paymentdays) {
        this.paymentdays = paymentdays;
    }

    public String getUnsettledbill() {
        return unsettledbill;
    }

    public void setUnsettledbill(String unsettledbill) {
        this.unsettledbill = unsettledbill;
    }

    public String getShouldpayment() {
        return shouldpayment;
    }

    public void setShouldpayment(String shouldpayment) {
        this.shouldpayment = shouldpayment;
    }

    public String getAvailablebalance() {
        return availablebalance;
    }

    public void setAvailablebalance(String availablebalance) {
        this.availablebalance = availablebalance;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getImporttype() {
        return importtype;
    }

    public void setImporttype(String importtype) {
        this.importtype = importtype;
    }

    public String getMailtype() {
        return mailtype;
    }

    public void setMailtype(String mailtype) {
        this.mailtype = mailtype;
    }

    public String getRepayment() {
        return repayment;
    }

    public void setRepayment(String repayment) {
        this.repayment = repayment;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getFromlastupdate() {
        return fromlastupdate;
    }

    public void setFromlastupdate(String fromlastupdate) {
        this.fromlastupdate = fromlastupdate;
    }

    public String getUpdateflag() {
        return updateflag;
    }

    public void setUpdateflag(String updateflag) {
        this.updateflag = updateflag;
    }

    public String getSupportebank() {
        return supportebank;
    }

    public void setSupportebank(String supportebank) {
        this.supportebank = supportebank;
    }

    public String getMinshouldpayment() {
        return minshouldpayment;
    }

    public void setMinshouldpayment(String minshouldpayment) {
        this.minshouldpayment = minshouldpayment;
    }

    public String getFreedays() {
        return freedays;
    }

    public void setFreedays(String freedays) {
        this.freedays = freedays;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }
}
