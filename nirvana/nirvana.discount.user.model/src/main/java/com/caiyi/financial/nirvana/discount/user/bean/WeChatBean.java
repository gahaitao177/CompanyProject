package com.caiyi.financial.nirvana.discount.user.bean;

/**
 * Created by dengh on 2016/7/22.
 */

import com.caiyi.financial.nirvana.core.bean.HskHeaderBean;

public class WeChatBean extends HskHeaderBean {
    private static final long serialVersionUID = 1L;
    //
    private String code = "";
    // 应用密钥AppSecret
    private String secret = "";
    // unionid
    private String unionid = "";
    // 微信昵称
    private String nickname = "";
    // 微信头像
    private String headimgurl = "";
    // 普通用户的标识，对当前开发者帐号唯一
    private String openid = "";
    // 普通用户性别，1为男性，2为女性
    private int sex = 0;
    // 普通用户个人资料填写的省份
    private String province = "";
    // 普通用户个人资料填写的城市
    private String city = "";
    // 国家，如中国为CN
    private String country = "";
    // 微信昵称
    private String wxNickName = "";
    // 用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
    private String privilege = "";

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getWxNickName() {
        return wxNickName;
    }

    public void setWxNickName(String wxNickName) {
        this.wxNickName = wxNickName;
    }

}
