package com.caiyi.financial.nirvana.discount.user.dto;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.io.Serializable;

/**
 * Created by wenshiliang on 2016/11/17.
 * 存在两个TokenBean，存在对数据库操作
 * 合并成TokenDto
 * 去除无关参数
 */
public class TokenDto implements Serializable {
    private String pwd;//用户密码
    private String cpwd9188;//9188加密的用户密码
    private String mtype;//客户端类型
    private String cuserId;//用户唯一序列ID
    private String copenid;//微信openid
    private String iloanid;//信贷人id
    private String isreal;//是否认证实名0未实名1审核中2已审核实名3审核失败

    private String accessToken;//令牌字符串
    private String appid;//令牌密钥

    private String paramJson;//token中传递的参数,取代之前存放在session中的额外参数
    private String ipAddr;//ip地址

    private Integer expiresin;//有效时常（秒）
    private Integer mobiletype;//客户端类型 1 安卓 2 iOS
    private String cpassword;//密码
    private String lastTime;//最后登陆时间
    private Integer istate;//状态
    private Integer iloginfrom;//登录来源0惠刷卡1公积金2记账


    public  BaseBean makeBaseBean(BaseBean base){
        base.setCuserId(getCuserId());
        base.setIlendid(getIloanid());
        base.setCopenid(getCopenid());
        base.setIsreal(getIsreal());
        base.setPwd(getPwd());
        if(base.getPwd()==null){
            base.setPwd(getCpassword());
        }
        base.setPwd9188(getCpwd9188());
        base.setBusiErrCode(1);
        base.setParamJson(getParamJson());
        base.setBusiErrDesc("success");
        return base;
    }

    public static TokenDto parseBaseBean(BaseBean bean){
        TokenDto dto = new TokenDto();
        dto.setAppid(bean.getAppId());
        dto.setAccessToken(bean.getAccessToken());
        dto.setIpAddr(bean.getIpAddr());
        return dto;
    }

    public Integer getIloginfrom() {
        return iloginfrom;
    }

    public void setIloginfrom(Integer iloginfrom) {
        this.iloginfrom = iloginfrom;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public Integer getExpiresin() {
        return expiresin;
    }

    public void setExpiresin(Integer expiresin) {
        this.expiresin = expiresin;
    }



    @Deprecated
    public String getPwd() {
        return pwd;
    }

    @Deprecated
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(Integer mobiletype) {
        this.mobiletype = mobiletype;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

//    public String getPwd() {
//        return pwd;
//    }
//
//    public void setPwd(String pwd) {
//        this.pwd = pwd;
//    }

    public String getCpwd9188() {
        return cpwd9188;
    }

    public void setCpwd9188(String cpwd9188) {
        this.cpwd9188 = cpwd9188;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
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
