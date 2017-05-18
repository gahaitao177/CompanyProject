package com.caiyi.nirvana.analyse;

import com.alibaba.fastjson.JSONObject;
import com.mina.rbc.util.CheckUtil;

import java.io.Serializable;

public class BaseBean implements Serializable {


    private static final long serialVersionUID = 1L;
    private String uid = "";
    private String cuserId = ""; //用户编号
    private String cuserid = ""; //用户编号

    private String imei = ""; //用户手机imei号
    private String cnickid = ""; //主站用户昵称
    private String pwd = ""; //账户密码
    private String fpwd = ""; //资金密码
    private String mobileNo = ""; //手机号
    private String isource = "";
    private int busiErrCode = -1; //业务处理错误号
    private String busiErrDesc = "";//业务处理错误描叙
    private String busiJson = ""; //业务处理后返回的json
    private String merchantacctId = ""; //合作的信任ID
    private String icon = "";//用户头像
    private String accessToken = "";//token令牌字符串
    private String appId = "";//token令牌字密钥
    private String paramJson = "";//token登录中传递的参数,取代之前存放在session中的参数
    private String appVersion = "";//app应用版本
    private String releaseVersion = ""; //版本号  android和ios 统一
    private String ipAddr = "";//IP地址
    private String yzm = "";
    private String type = "";
    private String userName = "";

    private String timestamp = "";
    private String key = "";
    private String signType = "";    //加密方式
    private String signMsg = "";    //加密后的字符串

    private String newValue = "";
    private String oldValue = "";
    private String today = "";

    //user类
    private String cimei;//手机IMEI
    private String cmodel;//手机型号
    private String cphoneos;//手机操作系统
    private String cphonebrand;//手机品牌

    private String cres;//分辨率
    private String cgetuiid;//推送ＩＤ
    private String cxmid;//小米ID
    private String chwid;//华为ID
    private String cleadcloudid;//    leadcloudid
    private int pn = 1;//页码
    private int ps = 25;//页面大小
    private int tp = 0;//总页数
    private int tr = 0;//总记录数
    private int source;//投注来源
    private String service;

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getFpwd() {
        return fpwd;
    }

    public void setFpwd(String fpwd) {
        this.fpwd = fpwd;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getIsource() {
        return isource;
    }

    public void setIsource(String isource) {
        this.isource = isource;
    }

    public int getBusiErrCode() {
        return busiErrCode;
    }

    public void setBusiErrCode(int busiErrCode) {
        this.busiErrCode = busiErrCode;
    }

    public String getBusiErrDesc() {
        return busiErrDesc;
    }

    public void setBusiErrDesc(String busiErrDesc) {
        this.busiErrDesc = busiErrDesc;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }

    public int getTr() {
        return tr;
    }

    public void setTr(int tr) {
        this.tr = tr;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMerchantacctId() {
        return merchantacctId;
    }

    public void setMerchantacctId(String merchantacctId) {
        this.merchantacctId = merchantacctId;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSignMsg() {
        return signMsg;
    }

    public void setSignMsg(String signMsg) {
        this.signMsg = signMsg;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getBusiJson() {
        return busiJson;
    }

    public void setBusiJson(String busiJson) {
        this.busiJson = busiJson;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }


    public String getCimei() {
        return cimei;
    }

    public void setCimei(String cimei) {
        this.cimei = cimei;
    }

    public String getCmodel() {
        return cmodel;
    }

    public void setCmodel(String cmodel) {
        this.cmodel = cmodel;
    }

    public String getCphoneos() {
        return cphoneos;
    }

    public void setCphoneos(String cphoneos) {
        this.cphoneos = cphoneos;
    }

    public String getCphonebrand() {
        return cphonebrand;
    }

    public void setCphonebrand(String cphonebrand) {
        this.cphonebrand = cphonebrand;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }


    public String getCres() {
        return cres;
    }

    public void setCres(String cres) {
        this.cres = cres;
    }

    public String getCgetuiid() {
        return cgetuiid;
    }

    public void setCgetuiid(String cgetuiid) {
        this.cgetuiid = cgetuiid;
    }

    public String getCxmid() {
        return cxmid;
    }

    public void setCxmid(String cxmid) {
        this.cxmid = cxmid;
    }

    public String getChwid() {
        return chwid;
    }

    public void setChwid(String chwid) {
        this.chwid = chwid;
    }

    public String getCleadcloudid() {
        return cleadcloudid;
    }

    public void setCleadcloudid(String cleadcloudid) {
        this.cleadcloudid = cleadcloudid;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("code", busiErrCode);
        json.put("desc", busiErrDesc);

        if (!CheckUtil.isNullString(busiJson)) {
            json.put("results", JSONObject.parseObject(busiJson));
        }
        return json.toString();

    }


}
