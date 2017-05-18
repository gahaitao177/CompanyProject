package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by A-0106 on 2016/5/31.
 */
public class CommodityDto {
    private String  icard4num;
    private Integer  ibankid;
    private String  ipoint;
    private String  cbankName;
    private String  icateid;
    private String  ccategory;

    public String getIcard4num() {
        return icard4num;
    }

    public void setIcard4num(String icard4num) {
        this.icard4num = icard4num;
    }

    public Integer getIbankid() {
        return ibankid;
    }

    public void setIbankid(Integer ibankid) {
        this.ibankid = ibankid;
    }

    public String getIpoint() {
        return ipoint;
    }

    public void setIpoint(String ipoint) {
        this.ipoint = ipoint;
    }

    public String getCbankName() {
        return cbankName;
    }

    public void setCbankName(String cbankName) {
        this.cbankName = cbankName;
    }

    public String getIcateid() {
        return icateid;
    }

    public void setIcateid(String icateid) {
        this.icateid = icateid;
    }

    public String getCcategory() {
        return ccategory;
    }

    public void setCcategory(String ccategory) {
        this.ccategory = ccategory;
    }
}
