package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by jianghao on 2016/12/16.
 * 对应表 tb_zx_gjj
 */
public class ProvidentFundDto {
    private int providentFundId;//公积金id
    private String title;//标题
    private String status;//状态
    private String url;//公积金url
    private String addTime;//添加时间
    private String updateTime;//更新时间
    private int mounthNum;//缴纳月数
    private double mounthTotal;//月缴额度
    private int addMonth;

    public int getProvidentFundId() {
        return providentFundId;
    }

    public void setProvidentFundId(int providentFundId) {
        this.providentFundId = providentFundId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getMounthNum() {
        return mounthNum;
    }

    public void setMounthNum(int mounthNum) {
        this.mounthNum = mounthNum;
    }

    public double getMounthTotal() {
        return mounthTotal;
    }

    public void setMounthTotal(double mounthTotal) {
        this.mounthTotal = mounthTotal;
    }

    public int getAddMonth() {
        return addMonth;
    }

    public void setAddMonth(int addMonth) {
        this.addMonth = addMonth;
    }
}
