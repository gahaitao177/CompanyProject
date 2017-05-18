package com.caiyi.financial.nirvana.ccard.investigation.dto;

import java.util.Date;

/**
 * Created by shaoqinghua on 2017/1/24.
 * 对应表 tb_xx_credit_account 学信账号表
 */
public class ChsiAccountDto {
    //学信网账号id
    private int chsiAccountId;
    //学信网登录名
    private String loginName;
    //学信网登录密码
    private String loginPwd;
    //手机号码
    private String phone;
    //添加时间
    private Date addTime;
    //更新时间
    private Date updateTime;
    //邮箱
    private String mail;
    //身份证号
    private String idCode;
    //慧刷卡用户id
    private String userId;
    //信息状态 0：不可用 1：可用（默认）
    private int state = 1;
    //最高学历(0:其他,1:专科,2:本科,3:硕士,4:博士)
    private int educationLevel;

    public int getChsiAccountId() {
        return chsiAccountId;
    }

    public void setChsiAccountId(int chsiAccountId) {
        this.chsiAccountId = chsiAccountId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(int educationLevel) {
        this.educationLevel = educationLevel;
    }
}
