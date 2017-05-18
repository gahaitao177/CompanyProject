package com.caiyi.financial.nirvana.discount.ccard.dto;

/**
 * Created by heshaohua on 2016/5/17.
 */
public class TopicDto {
    /** 专题标题 **/
    private String title;
    /** 专题地址 **/
    private String picurl;
    /** 专题ID **/
    private String topicid;
    //主题副标题
    private String csubtitle;
    //主题点击量
    private String iclickcount;
    //主题关键词
    private String ckeywords;
    private String tag="rows";

    public String getCsubtitle() {
        return csubtitle;
    }

    public void setCsubtitle(String csubtitle) {
        this.csubtitle = csubtitle;
    }

    public String getIclickcount() {
        return iclickcount;
    }

    public void setIclickcount(String iclickcount) {
        this.iclickcount = iclickcount;
    }

    public String getCkeywords() {
        return ckeywords;
    }
    public void setCkeywords(String ckeywords) {
        this.ckeywords = ckeywords;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getTopicid() {
        return topicid;
    }

    public void setTopicid(String topicid) {
        this.topicid = topicid;
    }
    @Override
    public  String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("<"+tag+" ");
        if(getTopicid()!=null)
            sb.append("itopicid="+getTopicid());
        if(getTitle()!=null)
            sb.append(" ctitle="+getTitle());
        if(getCsubtitle()!=null)
            sb.append(" csubtitle="+getCsubtitle());
        if(getPicurl()!=null)
            sb.append(" cpicurl="+getPicurl());
        if(getIclickcount()!=null)
            sb.append(" iclickcount="+getIclickcount());
        if(getCkeywords()!=null)
            sb.append(" ckeywords="+getCkeywords());
        sb.append("</"+tag+">");
        return sb.toString();
    }
}

