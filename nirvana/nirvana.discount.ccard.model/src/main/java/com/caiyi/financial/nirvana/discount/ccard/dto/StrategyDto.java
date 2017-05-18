package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by heshaohua on 2016/5/17.
 */
public class StrategyDto {
    /** 专题标题 **/
    private String title;
    /** 专题标题 **/
    private String subtitle;
    /** 专题地址 **/
    private String picurl;
    /** 专题地址 **/
    private String targeturl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getTargeturl() {
        return targeturl;
    }

    public void setTargeturl(String targeturl) {
        this.targeturl = targeturl;
    }
}
