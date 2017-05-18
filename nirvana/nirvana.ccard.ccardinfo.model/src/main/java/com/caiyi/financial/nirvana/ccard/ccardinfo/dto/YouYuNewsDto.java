package com.caiyi.financial.nirvana.ccard.ccardinfo.dto;

/**
 * Created by Linxingyu on 2016/12/23.
 * 有鱼金融资讯返回数据
 */
public class YouYuNewsDto {

    private String newsId;//资讯id
    private String newsType;//资讯类型
    private String source;//资讯来源
    private String newsTitle;//资讯标题
    private String newsTime;//资讯创建时间
    private String isCollect;//资讯是否被收藏 0 未收藏  1 已收藏
    private String target;//资讯链接
    private String logoUrl;//资讯logo来源
    private String showType;//资讯显示方式   0(正常模式) | 1(大图模式) | 2(banner模式)
    private String tag;//标签

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }
}
