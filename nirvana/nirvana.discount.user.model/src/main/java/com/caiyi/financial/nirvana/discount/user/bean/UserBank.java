package com.caiyi.financial.nirvana.discount.user.bean;

/**
 * Created by dengh on 2016/8/1.
 */
public class UserBank {
    private String cnickid;  // 用户名
    private  Integer ibankid; // 银行id
    private  Integer ismade; // 是否关注

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public Integer getIsmade() {
        return ismade;
    }

    public void setIsmade(Integer ismade) {
        this.ismade = ismade;
    }
}
