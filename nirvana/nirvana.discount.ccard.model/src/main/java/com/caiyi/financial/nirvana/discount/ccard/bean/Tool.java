package com.caiyi.financial.nirvana.discount.ccard.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by lizhijie on 2016/6/17.
 */
public class Tool extends BaseBean{
    private String toolid;//工具code
    private String typeid;//文章类型id；app类型：0 android,1 ios
    private String articleid;//文章id
    private String bversion;//推广版本；app版本

    private String csource;//渠道

    public String getToolid() {
        return toolid;
    }

    public void setToolid(String toolid) {
        this.toolid = toolid;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public String getBversion() {
        return bversion;
    }

    public void setBversion(String bversion) {
        this.bversion = bversion;
    }

    public String getCsource() {
        return csource;
    }

    public void setCsource(String csource) {
        this.csource = csource;
    }
}
