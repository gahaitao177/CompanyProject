package com.caiyi.nirvana.analyse.model;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.enums.LevelEnum;
import com.caiyi.nirvana.analyse.enums.SystemEnum;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by been on 2017/3/8.
 */
public class MonitorEvent implements Serializable {

    public static final String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    private String key;                     //唯一标识      当前类名 + 当前方法名 + 当前异常的唯一标识（用于区分开同一方法中的多个异常信息）

    private String systemName;              //系统名称      SystemEnum.*

    private String systemCode;              //系统code      SystemEnum.*

    private String url;                     //接口url       若是接口则传入接口url 或 uri

    private String ip;                      //机器ip地址

    private String content;                 //消息内容      异常信息

    private int level;                      //等级          LevelEnum.*

    private String timestamp;               //时间戳       yyyyMMdd HH:mm:ss  无需手动维护此字段

    public MonitorEvent() {
        this.timestamp = new SimpleDateFormat(DATE_FORMAT_STR).format(new Date());
    }

    public MonitorEvent(String key, SystemEnum system, LevelEnum level, String url, String ip, String content) {
        this(key, system.getName(), system.getCode(), level.getIndex(), url, ip, content);
    }

    public MonitorEvent(String key, String systemName, String systemCode, int level, String url, String ip, String content) {
        this.key = key;
        this.systemName = systemName;
        this.systemCode = systemCode;
        this.url = url;
        this.ip = ip;
        this.content = content;
        this.level = level;
        this.timestamp = new SimpleDateFormat(DATE_FORMAT_STR).format(new Date());
    }

    public static MonitorEvent jsonString2Obj(String jsonString) {
        return JSONObject.parseObject(jsonString, MonitorEvent.class);
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }

    public void setSystem(SystemEnum system) {
        this.systemName = system.getName();
        this.systemCode = system.getCode();
    }

    public void setLevel(LevelEnum level) {
        this.level = level.getIndex();
    }

    public String getSystemName() {
        return systemName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static String getDateFormatStr() {
        return DATE_FORMAT_STR;
    }

    public int getLevel() {
        return level;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "MonitorEvent{" +
                "key='" + key + '\'' +
                "  systemName='" + systemName + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", url='" + url + '\'' +
                ", ip='" + ip + '\'' +
                ", content='" + content + '\'' +
                ", level=" + level +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
