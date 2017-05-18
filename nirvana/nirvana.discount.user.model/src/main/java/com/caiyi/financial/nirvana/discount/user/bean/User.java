package com.caiyi.financial.nirvana.discount.user.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.Date;

/**
 * Created by heshaohua on 2016/5/20.
 */
public class User extends BaseBean {

    public final static int REGISTER = 1; //注册
    public final static String YZM_TYPE = "0"; // 惠刷卡注册发送验证码
    public final static String DK_YZM = "4"; // 有鱼贷款注册/快速登录发送验证码
    public final static String DK_PWD = "5"; // 有鱼贷款注册成功发送密码

    public final static String MD5_KEY = "http://www.huishuaka.com/";
    public final static String MD5_KEY_9188 = "http://www.9188.com/";

    private String yzm = "";//验证码
    private String yzmType = "";//验证码类型  0：注册 1：找回密码
    // 新密码
    private String newPwd = "";
    // 旧密码
    private String oldPwd = "";
    // 登录状态下修改密码标识 0：修改密码 1：忘记密码
    private String modifFlag = "99";
    //银行ID
    private String bankId = "";
    // 操作名称
    private String actionName;
    // 优惠商家ID
    private String businessId;
    // 门店ID
    private String storeId;
    // 门店ID used by preference
    private String istoreid;
    private int tp = 0;//总页数
//    private int ps = 25;//页面大小
//    private int pn = 1;//页码
    private int rc = 0;//总记录数
    private int startNum;
    private int endNum;
    private String imgYzm = "";
    private String coupon = "";
    // add by lcs 20150924
    private String collectType = "0";

    // add by lcs 20151019
    private String leanCloudId = "";
    private String deviceType = "android";

    // add by lcs 20151119
    private String cityId = "";

    // 合作方id add by lcs 20160509
    private String cooperationid;

    private String realname;
    private String idcard;
    private Date createTime;
    private Integer userType;

    private String cimei;//  设备imei码
    private String cidfa; //ios设备唯一标识

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getYzmType() {
        return yzmType;
    }

    public void setYzmType(String yzmType) {
        this.yzmType = yzmType;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getModifFlag() {
        return modifFlag;
    }

    public void setModifFlag(String modifFlag) {
        this.modifFlag = modifFlag;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getIstoreid() {
        return istoreid;
    }

    public void setIstoreid(String istoreid) {
        this.istoreid = istoreid;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }


//    public void setPs(int ps) {
//        this.ps = ps;
//    }
//
//    public void setPn(int pn) {
//        this.pn = pn;
//    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }

    public String getImgYzm() {
        return imgYzm;
    }

    public void setImgYzm(String imgYzm) {
        this.imgYzm = imgYzm;
    }

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getLeanCloudId() {
        return leanCloudId;
    }

    public void setLeanCloudId(String leanCloudId) {
        this.leanCloudId = leanCloudId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCooperationid() {
        return cooperationid;
    }

    public void setCooperationid(String cooperationid) {
        this.cooperationid = cooperationid;
    }

    public String getCimei() {
        return cimei;
    }

    public void setCimei(String cimei) {
        this.cimei = cimei;
    }

    public String getCidfa() {
        return cidfa;
    }

    public void setCidfa(String cidfa) {
        this.cidfa = cidfa;
    }

    public int transformSource() {
        int source = getSource();
        if (source >= 1000 && source < 2000) {
            source += 4000;
        } else if (source == 1 || source == 0) {
            source = 5000;
        } else if (source == 2001) {
            source = 6000;
        }
        return source;
    }
}
