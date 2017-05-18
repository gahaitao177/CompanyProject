package com.caiyi.financial.nirvana.discount.ccard.dto;


import java.util.List;
import java.util.Map;

/**
 * Created by A-0106 on 2016/5/31.
 */
public class ResultDto{
    private List<Map<String,String>> data;
    private String busiXml =""; //业务处理后返回的XML
    private String busiErrCode ; //返回代码
    private String busiErrDesc;//返回描述
    private Integer rc;
    private Integer pn;
    private Integer ps;
    private Integer tp;
    private Integer ccount;

    public Integer getRc() {
        return rc;
    }

    public void setRc(Integer rc) {
        this.rc = rc;
    }

    public String getBusiXml() {
        return busiXml;
    }

    public void setBusiXml(String busiXml) {
        this.busiXml = busiXml;
    }

    public String getBusiErrDesc() {
        return busiErrDesc;
    }

    public void setBusiErrDesc(String busiErrDesc) {
        this.busiErrDesc = busiErrDesc;
    }

    public String getBusiErrCode() {
        return busiErrCode;
    }

    public void setBusiErrCode(String busiErrCode) {
        this.busiErrCode = busiErrCode;
    }
    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public Integer getPn() {
        return pn;
    }

    public void setPn(Integer pn) {
        this.pn = pn;
    }

    public Integer getPs() {
        return ps;
    }

    public void setPs(Integer ps) {
        this.ps = ps;
    }

    public Integer getTp() {
        return tp;
    }

    public void setTp(Integer tp) {
        this.tp = tp;
    }

    public Integer getCcount() {
        return ccount;
    }

    public void setCcount(Integer ccount) {
        this.ccount = ccount;
    }
}
