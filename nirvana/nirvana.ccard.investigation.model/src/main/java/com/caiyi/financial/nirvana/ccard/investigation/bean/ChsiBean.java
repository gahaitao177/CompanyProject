package com.caiyi.financial.nirvana.ccard.investigation.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by shaoqinghua on 2017/1/24.
 * 学信网账号相关参数
 */
public class ChsiBean extends BaseBean {
    //学信网账号
    private String username;
    //学信网密码
    private String password;
    //确认密码
    private String confirmPwd;
    //图片验证码类型 1：忘记密码第一步验证码；2：忘记密码第二步验证码 3：登录图片验证码 ； 4：注册图片验证码
    private String imgCodeType;
    //是否需要图片验证码
    private String iskeep;
    //学信网验证码
    private String code;
    //学信网短信校验码
    private String vcode;
    //注册手机号
    private String mphone;
    //姓名
    private String xm;
    //身份证号
    private String sfzh;
    //token信息
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }

    public String getImgCodeType() {
        return imgCodeType;
    }

    public void setImgCodeType(String imgCodeType) {
        this.imgCodeType = imgCodeType;
    }

    public String getIskeep() {
        return iskeep;
    }

    public void setIskeep(String iskeep) {
        this.iskeep = iskeep;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
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
}
