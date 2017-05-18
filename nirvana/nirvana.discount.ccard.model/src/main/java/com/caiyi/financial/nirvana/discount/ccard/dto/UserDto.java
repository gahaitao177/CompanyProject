package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by heshaohua on 2016/5/20.
 */
public class UserDto {
    private String lastTime;
    private String expiresin;
    private String istate;

    private String cuserId;
    private String pwd;
    private String pwd9188;
    private String paramJson;

    private String busiErrCode;
    private String busiErrDesc;

    public String getBusiErrCode() {
        return busiErrCode;
    }

    public void setBusiErrCode(String busiErrCode) {
        this.busiErrCode = busiErrCode;
    }

    public String getBusiErrDesc() {
        return busiErrDesc;
    }

    public void setBusiErrDesc(String busiErrDesc) {
        this.busiErrDesc = busiErrDesc;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getExpiresin() {
        return expiresin;
    }

    public void setExpiresin(String expiresin) {
        this.expiresin = expiresin;
    }

    public String getIstate() {
        return istate;
    }

    public void setIstate(String istate) {
        this.istate = istate;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd9188() {
        return pwd9188;
    }

    public void setPwd9188(String pwd9188) {
        this.pwd9188 = pwd9188;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
