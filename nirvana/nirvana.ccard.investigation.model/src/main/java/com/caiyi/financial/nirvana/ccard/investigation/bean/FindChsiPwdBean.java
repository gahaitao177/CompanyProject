package com.caiyi.financial.nirvana.ccard.investigation.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by jianghao on 2016/12/1.
 */
public class FindChsiPwdBean extends BaseBean {
    private String loginName;//用户名
    private String captcha1;//找回密码验证码1
    private String captcha2;//找回密码验证码2
    private String mphone;//电话号码
    private String xm;//姓名
    private String sfzh;//身份证号
    private String token;//token
    private String clst;//token
    private String password;//密码
    private String password1;//确认密码
    private String vcode;//短信验证码

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCaptcha1() {
        return captcha1;
    }

    public void setCaptcha1(String captcha1) {
        this.captcha1 = captcha1;
    }

    public String getCaptcha2() {
        return captcha2;
    }

    public void setCaptcha2(String captcha2) {
        this.captcha2 = captcha2;
    }

    public String getMphone() {
        return mphone;
    }

    public void setMphone(String mphone) {
        this.mphone = mphone;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getSfzh() {
        return sfzh;
    }

    public void setSfzh(String sfzh) {
        this.sfzh = sfzh;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClst() {
        return clst;
    }

    public void setClst(String clst) {
        this.clst = clst;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }
}
