package com.caiyi.financial.nirvana.discount.user.bean;

/**
 * Created by dengh on 2016/8/1.
 */
public class BankPointBean {
 //   icard4num ,ibankid, ipoint
    private String icard4num;
    private  Integer ibankid;
    private  Double ipoint;

    public String getIcard4num() {
        return icard4num;
    }

    public void setIcard4num(String icard4num) {
        this.icard4num = icard4num;
    }

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public Double getIpoint() {
        return ipoint;
    }

    public void setIpoint(Double ipoint) {
        this.ipoint = ipoint;
    }
}
