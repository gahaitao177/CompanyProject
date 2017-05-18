package com.caiyi.financial.nirvana.ccard.investigation.dto;

import java.util.Date;

/**
 * Created by terry on 2016/10/31.
 */
public class CreditAccountDto {
    private String cuserId;
    private String cloginname;
    private String cloginpwd;
    private String cstatus;
    private Date clastlogindate;
    private Date capplydate;
    private Date cvprotdate;


    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getCloginname() {
        return cloginname;
    }

    public void setCloginname(String cloginname) {
        this.cloginname = cloginname;
    }

    public String getCloginpwd() {
        return cloginpwd;
    }

    public void setCloginpwd(String cloginpwd) {
        this.cloginpwd = cloginpwd;
    }

    public String getCstatus() {
        return cstatus;
    }

    public void setCstatus(String cstatus) {
        this.cstatus = cstatus;
    }

    public Date getClastlogindate() {
        return clastlogindate;
    }

    public void setClastlogindate(Date clastlogindate) {
        this.clastlogindate = clastlogindate;
    }

    public Date getCapplydate() {
        return capplydate;
    }

    public void setCapplydate(Date capplydate) {
        this.capplydate = capplydate;
    }

    public Date getCvprotdate() {
        return cvprotdate;
    }

    public void setCvprotdate(Date cvprotdate) {
        this.cvprotdate = cvprotdate;
    }
}
