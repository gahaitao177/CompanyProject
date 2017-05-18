package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by jianghao on 2016/12/06.
 * 对应表 TB_ZX_LEVEL_PRIVILEGE
 */
public class CreditPrivilegeDto {
    private int privilegeCode;//等级特权code
    private String privilegeTitle;//标题
    private String imageUrl;//图片url
    private String imageActionUrl;//返回url参数
    private int actionType;//跳转类型
    private int privilegeOrder;//特权排序
    private  String title;
    private  String param01;
    private  String param02;

    public int getPrivilegeCode() {
        return privilegeCode;
    }

    public void setPrivilegeCode(int privilegeCode) {
        this.privilegeCode = privilegeCode;
    }

    public String getPrivilegeTitle() {
        return privilegeTitle;
    }

    public void setPrivilegeTitle(String privilegeTitle) {
        this.privilegeTitle = privilegeTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageActionUrl() {
        return imageActionUrl;
    }

    public void setImageActionUrl(String imageActionUrl) {
        this.imageActionUrl = imageActionUrl;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getPrivilegeOrder() {
        return privilegeOrder;
    }

    public void setPrivilegeOrder(int privilegeOrder) {
        this.privilegeOrder = privilegeOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
