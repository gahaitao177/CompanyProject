package com.caiyi.financial.nirvana.ccard.investigation.bean;

/**
 * Created by jianghao on 2016/12/16.
 * 对应表 tb_zx_gjj
 */
public class ProvidentFundBean {
    private int providentFundId;//公积金id
    private String title;//标题
    private String status;//状态
    private String url;//公积金url
    private int mounthNum;//缴纳月数
    private double mounthTotal;//月缴额度
    private String lastUpdateMonth;//上次更新月

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

    public String getLastUpdateMonth() {
        return lastUpdateMonth;
    }

    public void setLastUpdateMonth(String lastUpdateMonth) {
        this.lastUpdateMonth = lastUpdateMonth;
    }
}
