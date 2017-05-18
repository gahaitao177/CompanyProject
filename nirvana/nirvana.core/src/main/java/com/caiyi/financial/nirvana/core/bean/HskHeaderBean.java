package com.caiyi.financial.nirvana.core.bean;

/**
 * 惠刷卡公共请求参数
 * Created by shaoqinghua on 2017/3/16.
 */
public class HskHeaderBean extends HskBaseBean {
    private String token; //token令牌字符串
    private String appId; //token令牌密钥
    private String devType; //设备类型 android 或 ios
    private String source; //渠道值
    private String appPkgName; //包名
    private String appVersionName; //APP应用版本
    private String appMgr; //0 表示卡管理包 1 表示其他包
    private String appVersionCode;
    private String gps;
    private String adCode; //高德地图行政区划编码 6位
    private String hskCityId; //惠刷卡城市id
    private String cityCode; //电话区号

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAppPkgName() {
        return appPkgName;
    }

    public void setAppPkgName(String appPkgName) {
        this.appPkgName = appPkgName;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppMgr() {
        return appMgr;
    }

    public void setAppMgr(String appMgr) {
        this.appMgr = appMgr;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getHskCityId() {
        return hskCityId;
    }

    public void setHskCityId(String hskCityId) {
        this.hskCityId = hskCityId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
