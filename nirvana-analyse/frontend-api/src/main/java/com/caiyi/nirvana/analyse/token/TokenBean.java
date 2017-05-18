package com.caiyi.nirvana.analyse.token;

import java.io.Serializable;


/**
 * @author huzhiqiang
 */
public class TokenBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid; //用户信息 
    private String pwd; //用户密码
    private String cuserId;//用户唯一标示码

    private String accessToken; //令牌字符串
    private String appId; //令牌密钥
    private String role;// 1 代表个人用户   2  代表企业用户
    private String paramJson; //token中传递的参数,取代之前存放在session中的额外参数

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }
}
