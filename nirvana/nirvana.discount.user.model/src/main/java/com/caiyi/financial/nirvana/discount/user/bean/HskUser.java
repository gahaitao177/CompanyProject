package com.caiyi.financial.nirvana.discount.user.bean;

/**
 * Created by lizhijie on 2017/2/10.
 */
public class HskUser{
    public final static int REGISTER = 1; //注册
    public final static String YZM_TYPE = "0"; // 惠刷卡注册发送验证码
    public final static String DK_YZM = "4"; // 有鱼贷款注册/快速登录发送验证码
    public final static String DK_PWD = "5"; // 有鱼贷款注册成功发送密码

    public final static String COME_FROM="HSK";
    public final static String MD5_KEY = "http://www.huishuaka.com/";
    public final static String MD5_KEY_9188 = "http://www.9188.com/";

    // 验证码 加密验证key
    private String key = "";
    // 时间戳
    private String timeStamp ="";

    private Integer appMgr;//0表示卡管理包 1表示其他包
    private String packageName;//包名
    private Integer csource ;//投注来源
    private Integer mobileType=3;//新版本设备类型 0 Android，1 iOS
    private Integer iloginfrom=0;//新版本设备类型 0 Android，1 iOS
//    private String appVersion;//app应用版本
//    private String accessToken ;//token令牌字符串
//    private String appId ;//token令牌字密钥
    private String yzm = "";//验证码
    private String yzmType = "";//验证码类型  0：注册 1：找回密码
    private  String cimei;//  设备imei码
    private String cuserId;//用户唯一ID
    private String ipAddr;//IP地址
    private String cpassword;//IP地址
    private String cpassword9188;//IP地址
    private String oldPassword;//IP地址
    private String newPassword;//IP地址
    private String cphone;//IP地址
    private int channelType;//IP地址

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getAppMgr() {
        return appMgr;
    }

    public void setAppMgr(Integer appMgr) {
        this.appMgr = appMgr;
    }

    public String getPackagename() {
        return packageName;
    }

    public void setPackagename(String packagename) {
        this.packageName = packagename;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getYzmType() {
        return yzmType;
    }

    public void setYzmType(String yzmType) {
        this.yzmType = yzmType;
    }

    public String getCimei() {
        return cimei;
    }

    public void setCimei(String cimei) {
        this.cimei = cimei;
    }

    public Integer getCsource() {
        return csource;
    }

    public void setCsource(Integer csource) {
        this.csource = csource;
    }

    public Integer getMobileType() {
        return mobileType;
    }

    public void setMobileType(Integer mobileType) {
        this.mobileType = mobileType;
    }

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

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public Integer getIloginfrom() {
        return iloginfrom;
    }

    public void setIloginfrom(Integer iloginfrom) {
        this.iloginfrom = iloginfrom;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCpassword9188() {
        return cpassword9188;
    }

    public void setCpassword9188(String cpassword9188) {
        this.cpassword9188 = cpassword9188;
    }
}
