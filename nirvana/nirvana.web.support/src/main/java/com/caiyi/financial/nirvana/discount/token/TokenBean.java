package com.caiyi.financial.nirvana.discount.token;

import java.io.Serializable;

/**
 * 
 * @author hsh
 *
 */
public class TokenBean implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
//    private String uid;//用户信息
    private String pwd;//用户密码
    private String pwd9188;//9188加密的用户密码
    private String mtype;//客户端类型
    private String cuserId;//用户唯一序列ID
    private String copenid;//微信openid
    private String iloanid;//信贷人id
    private String isreal;//是否认证实名0未实名1审核中2已审核实名3审核失败

	private String accessToken;//令牌字符串
    private String appid;//令牌密钥

    
    private String paramJson;//token中传递的参数,取代之前存放在session中的额外参数

//    public String getUid() {
//        return uid;
//    }
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
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
    public String getMtype() {
        return mtype;
    }
    public void setMtype(String mtype) {
        this.mtype = mtype;
    }
    public String getCuserId()
    {
        return cuserId;
    }
    public void setCuserId(String cuserid) {

        this.cuserId = cuserid;
    }

    public String getCopenid() {
        return copenid;
    }

    public void setCopenid(String copenid) {
        this.copenid = copenid;
    }

    public String getIloanid() {
        return iloanid;
    }

    public void setIloanid(String iloanid) {
        this.iloanid = iloanid;
    }

    public String getIsreal() {
        return isreal;
    }

    public void setIsreal(String isreal) {
        this.isreal = isreal;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAppid() {
        return appid;
    }
    public void setAppid(String appid) {
        this.appid = appid;
    }
    public String getParamJson() {
        return paramJson;
    }
    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
