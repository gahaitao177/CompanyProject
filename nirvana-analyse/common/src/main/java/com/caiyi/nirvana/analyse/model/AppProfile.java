package com.caiyi.nirvana.analyse.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by been on 2016/12/26.
 */
@Table(keyspace = "nirvana", name = "app_profile",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM")
public class AppProfile implements Serializable {
    @PartitionKey
    private UUID id;
    @Column(name = "app_key")
    private String appKey; //唯一标识app
    @Column(name = "device_id")
    private String deviceId;//用户唯一标识
    @Column(name = "device_type")
    private String deviceType;//设备类型
    @Column(name = "device_os")
    private String deviceOs;//手机系统版本号
    @Column(name = "device_model")
    private String deviceModel;//手机型号
    @Column(name = "device_brand")
    private String deviceBrand;//手机品牌
    @Column(name = "device_res")
    private String deviceRes;//分辨率
    @Column(name = "app_version")
    private String appVersion;
    @Column(name = "app_name")
    private String appName;
    @Column(name = "app_source")
    private String appSource; //app渠道
    @Column(name = "app_channel")
    private String appChannel; //应用市场
    @Column(name = "app_network")
    private String appNetWork; //网络状态
    @Column(name = "app_gps")
    private String appGps;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_name")
    private String userName;

    @Column(name = "app_ip")
    private String appIp;

//    @Column( name = "app_ip_loc")
//    private String appIpLoc;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "city_code")
    private String cityCode;


    @Column
    private Date ctime;
    private Set<Event> events = new HashSet<>();

    private Set<History> histories = new HashSet<>();

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceRes() {
        return deviceRes;
    }

    public void setDeviceRes(String deviceRes) {
        this.deviceRes = deviceRes;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppSource() {
        return appSource;
    }

    public void setAppSource(String appSource) {
        this.appSource = appSource;
    }

    public String getAppChannel() {
        return appChannel;
    }

    public void setAppChannel(String appChannel) {
        this.appChannel = appChannel;
    }

    public String getAppNetWork() {
        return appNetWork;
    }

    public void setAppNetWork(String appNetWork) {
        this.appNetWork = appNetWork;
    }

    public String getAppGps() {
        return appGps;
    }

    public void setAppGps(String appGps) {
        this.appGps = appGps;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAppIp() {
        return appIp;
    }

    public void setAppIp(String appIp) {
        this.appIp = appIp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


//    public String getAppIpLoc() {
//        return appIpLoc;
//    }
//
//    public void setAppIpLoc(String appIpLoc) {
//        this.appIpLoc = appIpLoc;
//    }

    @Frozen
    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @Frozen
    public Set<History> getHistories() {
        return histories;
    }

    public void setHistories(Set<History> histories) {
        this.histories = histories;
    }
}
