package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by User on 2016/12/16.
 */
public class SocialInsuranceDto {
    private int socialInsuranceId;//社保id
    private String title;//标题
    private String status;//状态
    private String addTime;//添加时间
    private String updateTime;//更新时间
    private String url;//社保请求地址

    public int getSocialInsuranceId() {
        return socialInsuranceId;
    }

    public void setSocialInsuranceId(int socialInsuranceId) {
        this.socialInsuranceId = socialInsuranceId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
