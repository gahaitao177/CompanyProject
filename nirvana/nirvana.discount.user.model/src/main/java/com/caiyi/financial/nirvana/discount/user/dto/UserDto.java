package com.caiyi.financial.nirvana.discount.user.dto;

/**
 * Created by heshaohua on 2016/5/20.
 */
public class UserDto {
    private String lastTime;
    private String expiresin;
    private String istate;
    private String cphone;
    private String cnickid;
    private String ctinyurl;
    private String cusername;
    private String username;
    private String banks;
    private String stores;
    private String cards;
    private String coupons;
    private String total;

    private String cuserId;
    private String pwd;
    private String pwd9188;
    private String paramJson;

    private String busiErrCode;
    private String busiErrDesc;

    private String iloginfrom;//登录来源0惠刷卡1公积金2记账

    public String getIloginfrom() {
        return iloginfrom;
    }

    public void setIloginfrom(String iloginfrom) {
        this.iloginfrom = iloginfrom;
    }

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

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getCtinyurl() {
        return ctinyurl;
    }

    public void setCtinyurl(String ctinyurl) {
        this.ctinyurl = ctinyurl;
    }

    public String getCusername() {
        return cusername;
    }

    public void setCusername(String cusername) {
        this.cusername = cusername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBanks() {
        return banks;
    }

    public void setBanks(String banks) {
        this.banks = banks;
    }

    public String getStores() {
        return stores;
    }

    public void setStores(String stores) {
        this.stores = stores;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public String getCoupons() {
        return coupons;
    }

    public void setCoupons(String coupons) {
        this.coupons = coupons;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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
