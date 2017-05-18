package com.caiyi.financial.nirvana.ccard.ccardinfo.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/7.
 */
public class ChannelDao {
    private  String id;
    private  String name;
    private  String contents;
    private  String desc;
    private  String contentNumber;

    private  String busiErrCode;
    private  String busiErrDesc;

    private List<Map<String,String>> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Map<String,String>> getData() {
        return data;
    }

    public void setData(List<Map<String,String>> data) {
        this.data = data;
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

    public String getContentNumber() {
        return contentNumber;
    }

    public void setContentNumber(String contentNumber) {
        this.contentNumber = contentNumber;
    }
}
