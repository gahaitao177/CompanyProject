package com.caiyi.financial.nirvana.discount.tools.dto;

/**
 * Created by chenli on 2017/3/9.
 */
public class TbHomePageDto {
    /**
     * 图片的地址
     */
    private String picUrl;
    /**
     * 跳转类型
     */
    private Integer actionType;
    /**
     * 描述标题
     */
    private String subTitle;
    /**
     * 值1
     */
    private String param01;
    /**
     * 值2
     */
    private String param02;
    /**
     * 类型
     * BANNER:广告
     * PACT:产品运营
     * QUICK:动态快速入口
     * SEM:广告推广
     * POINT:积分
     * CARDSHOW:信用卡推荐
     * HOTMSG 今日头条
     * SERVICE_BANNER 服务
     * banner LINES_PROMOTION 提额推广
     */
    private String type;

    /**
     * 标题
     */
    private String title;


    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getParam01() {
        return param01;
    }

    public void setParam01(String param01) {
        this.param01 = param01;
    }

    public String getParam02() {
        return param02;
    }

    public void setParam02(String param02) {
        this.param02 = param02;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
