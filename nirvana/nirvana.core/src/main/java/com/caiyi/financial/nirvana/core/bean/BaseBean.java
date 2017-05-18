package com.caiyi.financial.nirvana.core.bean;

import com.caiyi.financial.nirvana.core.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 当设置pageNum 和pageSize 查询时自动分页,
 * 如果orderBy存在，则替换mapper中的排序。
 * Created by wsl on 2015/12/30.
 * update by lcs on 2016/10/26  增加共有字段
 */
public class BaseBean implements Serializable {
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;

    //兼容老后台的setPn,setPs,sort
    private Integer pn;
    private Integer ps;
    private int tp = 0;// 总页数
    private int tr = 0;// 总记录数
    private String sort;

    public void setPn(Integer pn){
        this.pn = pn;
        this.pageNum = pn;
    }
    public void setPs(Integer ps){
        this.ps = ps;
        this.pageSize = ps;
    }

    // 验证码 加密验证key
    private String key = "";
    // 时间戳
    private String timeStamp ="";

    private Integer appMgr;//0表示卡管理包 1表示其他包
    private String packagename;//包名
    private Integer source ;//投注来源
    private Integer iclient=-1;//新版本设备类型 0 Android，1 iOS
    private String appVersion;//app应用版本
    private String accessToken ;//token令牌字符串
    private String appId ;//token令牌字密钥
    private String yzm = "";//验证码
    private String yzmType = "";//验证码类型

    // add by lcs 20161026 增加共有字段 start
    private String hskcityid;  // 100 开始 三位
    private String citycode;   // 北京 010  电话区号
    private String adcode;     // 六位
    // add by lcs 20161026 增加共有字段 end

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * 以下为old api中参数，当有使用到时，将其上移
     * busiErrCode busiErrDesc busiXml busiObject 已经过期，请不要再使用
     */

    private String icon;
    @Deprecated
    private Integer busiErrCode=0; //业务处理错误号
    @Deprecated
    private String busiErrDesc="";//业务处理错误描叙
    @Deprecated
    private String busiXml; //业务处理后返回的XML
    @Deprecated
    private Object busiObject; //业务处理后结果

    private String func;//功能号
    private String payorderid;
    protected String uid;//用户编号
    protected String pwd;//用户密码
    protected String pwd9188;//9188用户密码，用9188的md5key加密
    private Integer upay;
    private String coupon;
    private String ipAddr;//IP地址
    private String comeFrom;//来源

    //公众号字段
    private String ilendid;//信贷人id
    private String copenid;//微信openid
    private String isreal;//是否认证实名0未实名1审核中2已审核实名3审核失败
    //公众号字段

    protected Integer hztype;  //联合登录类型  支付宝用
    private String imei; //手机端imei号
    private String cuserId;//用户唯一ID
    private String ioutUserId;//供外部使用的用户唯一ID
    private String cusername = "";//用户名（9188）
    private String cnickname = "";//惠刷卡用户昵称
    private Integer imobbind;//手机号绑定 0未绑定1已绑定
    private Integer startrow;
    private Integer endrow;
    private Integer gopaymoney;
    private Boolean fastlogin;//是否通过合作方式快速登录
    private String merchantacctId; //合作的信任ID
    private String signType;	//加密方式
    private String signMsg;	//加密后的字符串
    private String forward;
    private String payendTime;//方按截止时间
    private Integer logintype ;//登录类型 0是普通登录 1是token登录

    private String paramJson ;//token登录中传递的参数,取代之前存放在session中的参数
//    @Deprecated
//    private String appversion;//app应用版本

    private Integer userType ;//移动设备类型  1 android 2 ios
    private String mobileNo;
    private Integer itype;//0:惠刷卡老用户，1:惠刷卡新注册用户,2:9188用户,3:惠刷卡和9188都注册过的用户

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public Integer getAppMgr() {
        return appMgr;
    }

    public void setAppMgr(Integer appMgr) {
        this.appMgr = appMgr;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPn() {
        return pn;
    }

    public Integer getPs() {
        return ps;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Deprecated
    public Integer getBusiErrCode() {
        return busiErrCode;
    }
    @Deprecated
    public void setBusiErrCode(Integer busiErrCode) {
        this.busiErrCode = busiErrCode;
    }
    @Deprecated
    public String getBusiErrDesc() {
        return busiErrDesc;
    }
    @Deprecated
    public void setBusiErrDesc(String busiErrDesc) {
        this.busiErrDesc = busiErrDesc;
    }
    @Deprecated
    public String getBusiXml() {
        return busiXml;
    }
    @Deprecated
    public void setBusiXml(String busiXml) {
        this.busiXml = busiXml;
    }

    @Deprecated
    public Object getBusiObject() {
        return busiObject;
    }

    @Deprecated
    public void setBusiObject(Object busiObject) {
        this.busiObject = busiObject;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getPayorderid() {
        return payorderid;
    }

    public void setPayorderid(String payorderid) {
        this.payorderid = payorderid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd9188() {
        return pwd9188;
    }

    public void setPwd9188(String pwd9188) {
        this.pwd9188 = pwd9188;
    }

    public Integer getUpay() {
        return upay;
    }

    public void setUpay(Integer upay) {
        this.upay = upay;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getHztype() {
        return hztype;
    }

    public void setHztype(Integer hztype) {
        this.hztype = hztype;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getIoutUserId() {
        return ioutUserId;
    }

    public void setIoutUserId(String ioutUserId) {
        this.ioutUserId = ioutUserId;
    }

    public String getCusername() {
        return cusername;
    }

    public void setCusername(String cusername) {
        this.cusername = cusername;
    }

    public String getCnickname() {
        return cnickname;
    }

    public void setCnickname(String cnickname) {
        this.cnickname = cnickname;
    }

    public Integer getImobbind() {
        return imobbind;
    }

    public void setImobbind(Integer imobbind) {
        this.imobbind = imobbind;
    }

    public Integer getStartrow() {
        return startrow;
    }

    public void setStartrow(Integer startrow) {
        this.startrow = startrow;
    }

    public Integer getEndrow() {
        return endrow;
    }

    public void setEndrow(Integer endrow) {
        this.endrow = endrow;
    }

    public Integer getGopaymoney() {
        return gopaymoney;
    }

    public void setGopaymoney(Integer gopaymoney) {
        this.gopaymoney = gopaymoney;
    }

    public Boolean getFastlogin() {
        return fastlogin;
    }

    public void setFastlogin(Boolean fastlogin) {
        this.fastlogin = fastlogin;
    }

    public String getMerchantacctId() {
        return merchantacctId;
    }

    public void setMerchantacctId(String merchantacctId) {
        this.merchantacctId = merchantacctId;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSignMsg() {
        return signMsg;
    }

    public void setSignMsg(String signMsg) {
        this.signMsg = signMsg;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getPayendTime() {
        return payendTime;
    }

    public void setPayendTime(String payendTime) {
        this.payendTime = payendTime;
    }

    public Integer getLogintype() {
        return logintype;
    }

    public void setLogintype(Integer logintype) {
        this.logintype = logintype;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    /**
     * 为了兼容某个版本出现的大小写问题
     * 添加了一个不区分大小写的getAppversion setAppversion 方法
     * 都是设置appVersion的值
     * @return
     */
    public String getAppversion() {
        return appVersion;
    }

    public void setAppversion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public Integer getIclient() {
        return iclient;
    }

    public void setIclient(Integer iclient) {
        this.iclient = iclient;
    }

    public String getOrderBy() {
        // SQL过滤，防止注入
        if(!StringUtils.isEmpty(orderBy)){
            String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
                    + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
            Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
            if (sqlPattern.matcher(orderBy).find()) {
                return "";
            }
        }
        return orderBy;
    }

    public String toJsonString(){
        return JsonUtil.toJSONString(this);
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getIlendid() {
        return ilendid;
    }

    public void setIlendid(String ilendid) {
        this.ilendid = ilendid;
    }

    public String getCopenid() {
        return copenid;
    }

    public void setCopenid(String copenid) {
        this.copenid = copenid;
    }

    public String getIsreal() {
        return isreal;
    }

    public void setIsreal(String isreal) {
        this.isreal = isreal;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }

    public int getTr() {
        return tr;
    }

    public void setTr(int tr) {
        this.tr = tr;
    }

    public String getHskcityid() {
        return hskcityid;
    }

    public void setHskcityid(String hskcityid) {
        this.hskcityid = hskcityid;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }
}
