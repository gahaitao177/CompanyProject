package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

public class H5ChannelCardUseBean extends BaseBean {
    private Long iuseid;

    private String cusename;//主题名

    private Long channelid;//渠道id

    private Short iorder;//排序

    private String picurl;//图片地址

    private Short ishot;//是否热门

    private String csubtitle;//副标题

    public String getCsubtitle() {
        return csubtitle;
    }

    public void setCsubtitle(String csubtitle) {
        this.csubtitle = csubtitle;
    }

    public String getCusename() {
        return cusename;
    }

    public void setCusename(String cusename) {
        this.cusename = cusename;
    }

    public Short getIorder() {
        return iorder;
    }

    public void setIorder(Short iorder) {
        this.iorder = iorder;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl == null ? null : picurl.trim();
    }

    public Short getIshot() {
        return ishot;
    }

    public void setIshot(Short ishot) {
        this.ishot = ishot;
    }

    public Long getIuseid() {
        return iuseid;
    }

    public void setIuseid(Long iuseid) {
        this.iuseid = iuseid;
    }

    public Long getChannelid() {
        return channelid;
    }

    public void setChannelid(Long channelid) {
        this.channelid = channelid;
    }
}