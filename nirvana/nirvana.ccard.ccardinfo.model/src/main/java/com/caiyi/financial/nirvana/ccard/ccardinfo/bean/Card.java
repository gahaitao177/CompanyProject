package com.caiyi.financial.nirvana.ccard.ccardinfo.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by lizhijie on 2016/6/20.
 */
public class Card extends BaseBean {

    private String iapplyid;//预约o2o记录ID
    private String iorderid;//信贷员订单ID
    // 城市id
    private String cityid = "";
    // 银行id
    private String bankid ;
    private String ibankids;
    private String exibankid;
    // 用途id
    private String useid ;
    // 卡等级
    private String cardlevel ;
    // 卡id
    private String cardid = "";

    //合作级别，0不合作，1-9越大合作力度越大
    private String icooperation;

    private String[] useids;

    // 卡申请相关字段
    // 年龄
    private String age = "";
    // 职业 0:事业单位,1:白领上班族,2:美容美发,3:小型餐饮,4:房地经纪,5:个体户,6保安保洁,7其它,8大型/连锁餐饮
    private String profession = "0";
    // 是否有信用卡 0:没,1:有本行卡,2:他行1-11个月,3:他行12个月以上，4持他行信用卡6个月以内，5持他行信用卡6个月以上
    private String use = "0";
    // 是否逾期 0无逾期  1：三个月以内 2：六个月以内 3：六个月及以上，4:逾期3次以内，5:逾期3次及以上，6三个月及以上
    private String overdue = "0";
    // 社保缴纳 0:无,1:3个月以上,2:6个月以上,3:1年以上
    private String socialpay = "0";
    // 工作证明:0工牌1名片2工作证明3营业执照4无工作证明，5其它,6银行流水/税单
    private String workprove = "";
    // 他行信用卡使用0无它行信用卡1使用3个月以内2使用三个月以上 3 六个月以上4一年以上
    private String otherbank = "";
    // 个人优势 0有本地私家车1本地有商品房2大专以上学历3有其他资产
    private String advantage = "";
    /** 0租房，1自有住房无贷款，2本市按揭房贷，3其它*/
    private String ihouse = "";
    /** 0无本市汽车，1:5年以内本市拍照汽车，2:5年以上本市拍照汽车*/
    private String icar = "";
    // 姓名
    private String name = "";
    // 手机号
    private String phonenum = "";
    // 工作地点
    private String workplace = "";

    private String timestamp = "";
    private String key = "";
    private String yzm = "";
    //private String yzmType = "3";
    // 省code
    private String privincecode = "";
    // 市
    private String citycode = "";
    // 县
    private String countycode = "";
    /**商圈*/
    private String cgroupcode = "";
    // 经度
    private String lat = "";
    // 经度
    private String lng = "";
    // 性别
    private String gender = "";
    /**
     * 学历：0专科，1本科及以上，2高中及以下
     */
    private String idegree = "";
    /**
     * 工作单位
     */
    private String cworkorg = "";
    /**
     * 是否申请成功：0申请成功。bean.setIsuccess("1");//1申请失败，资料不符合
     bean.setIstatus("2");//2下架。2申请失败，不在规定城市内
     */
    private String isuccess = "";
    /**
     * 商品状态：0待出售1已出售2已下架
     */
    private String istatus = "";

    private String ichannelid;//办卡推广渠道id
    private String cchannel_name;//办卡推广渠道名称
    private String hskcityid;//新版本，惠刷卡tb_area表中城市id


    // add by lcs 20161020 银行回调用参数 start
    private String adid;//用于区分类型
    private String orderid;//用于区分客户;
    // add by lcs 20161020 银行回调用参数 end


    /**
     * 有鱼金融资讯请求参数
     */
    private String newsType;//新闻类别
    private String newsId; //新闻ID
    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }


    public String getIapplyid() {
        return iapplyid;
    }

    public void setIapplyid(String iapplyid) {
        this.iapplyid = iapplyid;
    }

    public String getIorderid() {
        return iorderid;
    }

    public void setIorderid(String iorderid) {
        this.iorderid = iorderid;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

    public String getIbankids() {
        return ibankids;
    }

    public void setIbankids(String ibankids) {
        this.ibankids = ibankids;
    }

    public String getExibankid() {
        return exibankid;
    }

    public void setExibankid(String exibankid) {
        this.exibankid = exibankid;
    }

    public String getUseid() {
        return useid;
    }

    public void setUseid(String useid) {
        this.useid = useid;
    }

    public String getCardlevel() {
        return cardlevel;
    }

    public void setCardlevel(String cardlevel) {
        this.cardlevel = cardlevel;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getIcooperation() {
        return icooperation;
    }

    public void setIcooperation(String icooperation) {
        this.icooperation = icooperation;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getOverdue() {
        return overdue;
    }

    public void setOverdue(String overdue) {
        this.overdue = overdue;
    }

    public String getSocialpay() {
        return socialpay;
    }

    public void setSocialpay(String socialpay) {
        this.socialpay = socialpay;
    }

    public String getWorkprove() {
        return workprove;
    }

    public void setWorkprove(String workprove) {
        this.workprove = workprove;
    }

    public String getOtherbank() {
        return otherbank;
    }

    public void setOtherbank(String otherbank) {
        this.otherbank = otherbank;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public String getIhouse() {
        return ihouse;
    }

    public void setIhouse(String ihouse) {
        this.ihouse = ihouse;
    }

    public String getIcar() {
        return icar;
    }

    public void setIcar(String icar) {
        this.icar = icar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

//    public String getYzmType() {
//        return yzmType;
//    }
//
//    public void setYzmType(String yzmType) {
//        this.yzmType = yzmType;
//    }

    public String getPrivincecode() {
        return privincecode;
    }

    public void setPrivincecode(String privincecode) {
        this.privincecode = privincecode;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCountycode() {
        return countycode;
    }

    public void setCountycode(String countycode) {
        this.countycode = countycode;
    }

    public String getCgroupcode() {
        return cgroupcode;
    }

    public void setCgroupcode(String cgroupcode) {
        this.cgroupcode = cgroupcode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdegree() {
        return idegree;
    }

    public void setIdegree(String idegree) {
        this.idegree = idegree;
    }

    public String getCworkorg() {
        return cworkorg;
    }

    public void setCworkorg(String cworkorg) {
        this.cworkorg = cworkorg;
    }

    public String getIsuccess() {
        return isuccess;
    }

    public void setIsuccess(String isuccess) {
        this.isuccess = isuccess;
    }

    public String getIstatus() {
        return istatus;
    }

    public void setIstatus(String istatus) {
        this.istatus = istatus;
    }

    public String getIchannelid() {
        return ichannelid;
    }

    public void setIchannelid(String ichannelid) {
        this.ichannelid = ichannelid;
    }

    public String getCchannel_name() {
        return cchannel_name;
    }

    public void setCchannel_name(String cchannel_name) {
        this.cchannel_name = cchannel_name;
    }

    public String getHskcityid() {
        return hskcityid;
    }
    public void setHskcityid(String hskcityid) {
        this.hskcityid = hskcityid;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String[] getUseids() {
        return useids;
    }

    public void setUseids(String[] useids) {
        this.useids = useids;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
