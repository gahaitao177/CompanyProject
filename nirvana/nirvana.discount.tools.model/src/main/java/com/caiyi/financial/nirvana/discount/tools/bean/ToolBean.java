package com.caiyi.financial.nirvana.discount.tools.bean;

/**
 * Created by lizhijie on 2016/8/10.
 */
public class ToolBean extends ViewBean {
    private String toolid;//工具code
    private String typeid;//文章类型id；app类型：0 android,1 ios
    private String articleid;//文章id
    private String bversion;//推广版本；app版本
    private String csource;//渠道

    // add by lcs 20160628
    private String data; // 装修数据

    private String action = "";//操作类型

    private int dataversion = 0;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDataversion() {
        return dataversion;
    }

    public void setDataversion(int dataversion) {
        this.dataversion = dataversion;
    }
}
