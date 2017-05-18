package com.caiyi.financial.nirvana.discount.user.bean;


import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.Date;

/**
 * Created by dengh on 2016/7/25.
 */

public class TokenBean extends BaseBean{
    private  String accesstoken;//令牌字符串
    private  Date  createtime;
    private  Date  lasttime;
    private  Integer expiresin;
    private  Integer mobiletype;
    private  String cuserid;//用户唯一序列ID
    private  String cpassword;
    private  Integer istate;
    private  String cause;
    private  String  appid;//令牌密钥
    private  String  paramjson;//token中传递的参数,取代之前存放在session中的额外参数
    private  String  deadtime;

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getLasttime() {
        return lasttime;
    }

    public void setLasttime(Date lasttime) {
        this.lasttime = lasttime;
    }

    public Integer getExpiresin() {
        return expiresin;
    }

    public void setExpiresin(Integer expiresin) {
        this.expiresin = expiresin;
    }

    public Integer getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(Integer mobiletype) {
        this.mobiletype = mobiletype;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getParamjson() {
        return paramjson;
    }

    public void setParamjson(String paramjson) {
        this.paramjson = paramjson;
    }

    public String getDeadtime() {
        return deadtime;
    }

    public void setDeadtime(String deadtime) {
        this.deadtime = deadtime;
    }

//    public String getCpwd9188() {
//        return cpwd9188;
//    }
//
//    public void setCpwd9188(String cpwd9188) {
//        this.cpwd9188 = cpwd9188;
//    }
}
