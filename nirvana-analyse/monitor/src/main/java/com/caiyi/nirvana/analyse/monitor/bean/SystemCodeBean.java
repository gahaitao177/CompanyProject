package com.caiyi.nirvana.analyse.monitor.bean;

/**
 * Created by pc on 2017/3/13.
 */
public class SystemCodeBean {

    private Boolean providenFund;
    private Boolean socialSecurity;
    private Boolean account;
    private Boolean creditCard;
    private Boolean loan;

    public Boolean getProvidenFund() {
        return providenFund;
    }

    public void setProvidenFund(Boolean providenFund) {
        this.providenFund = providenFund;
    }

    public Boolean getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(Boolean socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public Boolean getAccount() {
        return account;
    }

    public void setAccount(Boolean account) {
        this.account = account;
    }

    public Boolean getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(Boolean creditCard) {
        this.creditCard = creditCard;
    }

    public Boolean getLoan() {
        return loan;
    }

    public void setLoan(Boolean loan) {
        this.loan = loan;
    }

    @Override
    public String toString() {
        String result = "说明：\n" +
                "更改请在url后追加参数；\n" +
                "启用：?providenFund=true  或者 ?providenFund=1  \n" +
                "禁用：?providenFund=false 或者 ?providenFund=0 \n";


        return result + "\n" +
                "     系统     |       code         |       是否告警        |\n " +
                "公积金        |providenFund       |" + providenFund + "\n " +
                "社保          |socialSecurity     |" + socialSecurity + "\n " +
                "记账          |account            |" + account + "\n " +
                "信用卡        |creditCard         |" + creditCard + "\n " +
                "贷款          |loan               |" + loan + "\n ";
    }
}
