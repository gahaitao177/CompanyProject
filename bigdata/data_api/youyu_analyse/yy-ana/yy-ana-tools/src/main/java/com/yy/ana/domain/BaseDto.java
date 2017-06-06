package com.yy.ana.domain;


import com.yy.ana.tools.TypeCaseHelper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 2017/5/26.
 */
public class BaseDto extends HashMap<String, Object> implements Dto, Serializable {
    public BaseDto() {
    }

    public BaseDto(String key, Object value) {
        put(key, value);
    }

    public BaseDto(Boolean success) {
        setSuccess(success);
    }

    public BaseDto(Boolean success, String msg) {
        setSuccess(success);
        setMsg(msg);
    }

    public BigDecimal getAsBigDecimal(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "BigDecimal", null);
        if (obj != null)
            return (BigDecimal) obj;
        else
            return null;
    }

    public Date getAsDate(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Date", "yyyy-MM-dd");
        if (obj != null)
            return (Date) obj;
        else
            return null;
    }

    public Date getAsDate(String key, String dateFormat) {
        Object obj = TypeCaseHelper.convert(get(key), "Date", dateFormat);
        if (obj != null)
            return (Date) obj;
        else
            return null;
    }


    public Integer getAsInteger(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Integer", null);
        if (obj != null)
            return (Integer) obj;
        else
            return null;
    }

    public Long getAsLong(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Long", null);
        if (obj != null)
            return (Long) obj;
        else
            return null;
    }

    public Float getAsFloat(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Float", null);
        if (obj != null)
            return (Float) obj;
        else
            return null;
    }

    public String getAsString(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "String", null);
        if (obj != null) {
            if (obj instanceof Date || obj instanceof Timestamp) {
                String time = String.valueOf(obj);
                if (time.length() > 18) {
                    return time.substring(0, 19);
                } else {
                    return time.substring(0, 10);
                }
            }
            return (String) obj;
        } else {
            return "";
        }
    }

    public List getAsList(String key) {
        return (List) get(key);
    }

    public Timestamp getAsTimestamp(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Timestamp", "yyyy-MM-dd HH:mm:ss");
        if (obj != null)
            return (Timestamp) obj;
        else
            return null;
    }

    public Boolean getAsBoolean(String key) {
        Object obj = TypeCaseHelper.convert(get(key), "Boolean", null);
        if (obj != null)
            return (Boolean) obj;
        else
            return null;
    }


    public void setDefaultAList(List pList) {
        put("defaultAList", pList);
    }

    public void setDefaultBList(List pList) {
        put("defaultBList", pList);
    }

    public List getDefaultAList() {
        return (List) get("defaultAList");
    }


    public List getDefaultBList() {
        return (List) get("defaultBList");
    }

    public void setDefaultJson(String jsonString) {
        put("defaultJsonString", jsonString);
    }

    public String getDefaultJson() {
        return getAsString("defaultJsonString");
    }

    public String toXml(String pStyle) {
        System.out.println("XML 暂不支持");
        return "";
    }

    public String toXml() {
        System.out.println("XML 暂不支持");
        return "";
    }

    public String toJson() {
        String strJson = null;
        // strJson = JsonHelper.encodeObject2Json(this);
        return strJson;
    }

    public String toJson(String pFormat) {
        String strJson = null;
        // strJson = JsonHelper.encodeObject2Json(this, pFormat);
        return strJson;
    }

    public void setSuccess(Boolean pSuccess) {
        put("success", pSuccess);
        if (pSuccess) {
            put("bflag", "1");
        } else {
            put("bflag", "0");
        }

    }

    public Boolean getSuccess() {
        return getAsBoolean("success");
    }

    public void setMsg(String pMsg) {
        put("msg", pMsg);
    }

    public String getMsg() {
        return getAsString("msg");
    }
}

