package com.caiyi.financial.nirvana.core.bean;

/**
 * Created by shaoqinghua on 2017/3/16.
 */
public class HskBaseBean {
    private String cuserId; //用户唯一ID
    private String ipAddr; //IP地址
    private String uid; //用户编号
    private String pwd; //用户密码
    private String pwd9188; //9188用户密码(9188的md5key加密)
    private String cnickname = ""; //惠刷卡用户昵称
    private String icon; //头像地址
    private String mobileNo; //手机号码
    private String captcha; //验证码
    private String captchaType; //验证码类型
    private String channelType = "0"; //验证码渠道类型 0:短信(默认) 1:语音

    private String copenid; //微信openid
    private String cidfa; //ios设备唯一标识

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
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

    public String getCnickname() {
        return cnickname;
    }

    public void setCnickname(String cnickname) {
        this.cnickname = cnickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getCopenid() {
        return copenid;
    }

    public void setCopenid(String copenid) {
        this.copenid = copenid;
    }

    public String getCidfa() {
        return cidfa;
    }

    public void setCidfa(String cidfa) {
        this.cidfa = cidfa;
    }
}
