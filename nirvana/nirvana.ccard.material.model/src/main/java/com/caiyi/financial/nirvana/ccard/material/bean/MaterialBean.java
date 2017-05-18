package com.caiyi.financial.nirvana.ccard.material.bean;


/**
 * Created by lizhijie on 2016/7/13.
 */
public class MaterialBean extends ViewBean{

    private static final long serialVersionUID = 1L;
    private String mediatype;

    private String busiJSON;

    private String ibankid;
    private String exibankid;//排除银行
    private int ltype;//0 筛选列表，1匹配卡类别，2成功失败等推荐列表
    private String ishot;//"1" 代表查询精选卡
    private String icityid;
    private String idistrictid;//区Id
    private String ibizid;//商圈Id
    private String ipostaddr;//邮寄地址：（1、单位地址 2、住宅地址
    private String iapplyid;

    private String cfield;
    private String cbankids;
    private String clabel;
    private String ccreditids;
    private String cxml;
    private String cdesc;
    private String cphone;
    private String idcardid;
    private String orderid;
    private String cstatus;

    private String cname;//真实姓名

    private String imgauthcode;//图片验证码
    private String phoneauthcode;//手机验证码

    private String cidcard;//身份证id

    private String yzm = "";//验证码
    private String yzmType = "";//验证码类型  0：注册 1：找回密码
    // add by lcs 20160616 start
    private String onlyCheckSms = "0";//
    // add by lcs 20160616 start
    //转化后对应银行的参数
    private String applyBankCardId;//申请卡片的银行ID
    private String applyBankCardLevel;//申请的卡等级
    private String applyCityId="";//住宅城市
    private String applyProvinceId="";//住宅省市
    private String applyAddress="";
    private String applyNativeProvince="";//籍贯 省份
    private String applyNativeCity="";//籍贯 城市
    private String applyCompanyProvince="";//公司所属省份(单位省)
    private String applyCompanyCity="";//公司所属城市(单位市)
    private String applyCompanyAddress="";//公司所属城市区(单位地区或县)
    private String applyCompanyPropery="";//单位性质:
    private String applyIndustryType="";//行业类别
    private String applyIndustryType2 = "";//行业小类-广发银行用
    private String applyJobPost="";//职位ID
    private String cityName="";//住宅城市名称
    private String provinceName="";//住宅省市名称
    private String applyCompanyCityName="";//公司城市名称
    private String applyCompanyProvinceName="";//公司省市名称



    private String chome_pname;//住宅省名称
    private String chome_cname;//住宅市名称
    private String chome_dname;//住宅区名称
    private String ccompany_pname;//公司省名称
    private String ccompany_cname;//公司市名称
    private String ccompany_dname;//公司区名称

    private String ccardcity="";//申卡城市

    private String cardid = "";//申请的信用卡id

    private String ichannelid;//渠道id
    private String ispreadid;//推广id
    private String hskcityid;//新版本，惠刷卡tb_area表中城市id
    private String citycode;//新版本，城市区号

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getBusiJSON() {
        return busiJSON;
    }

    public void setBusiJSON(String busiJSON) {
        this.busiJSON = busiJSON;
    }



    public String getIchannelid() {
        return ichannelid;
    }

    public void setIchannelid(String ichannelid) {
        this.ichannelid = ichannelid;
    }

    public String getIspreadid() {
        return ispreadid;
    }

    public void setIspreadid(String ispreadid) {
        this.ispreadid = ispreadid;
    }

    public String getApplyCityId() {
        return applyCityId;
    }

    public void setApplyCityId(String applyCityId) {
        this.applyCityId = applyCityId;
    }

    public String getApplyProvinceId() {
        return applyProvinceId;
    }

    public void setApplyProvinceId(String applyProvinceId) {
        this.applyProvinceId = applyProvinceId;
    }

    public String getApplyNativeProvince() {
        return applyNativeProvince;
    }

    public void setApplyNativeProvince(String applyNativeProvince) {
        this.applyNativeProvince = applyNativeProvince;
    }

    public String getApplyNativeCity() {
        return applyNativeCity;
    }

    public void setApplyNativeCity(String applyNativeCity) {
        this.applyNativeCity = applyNativeCity;
    }

    public String getApplyCompanyProvince() {
        return applyCompanyProvince;
    }

    public void setApplyCompanyProvince(String applyCompanyProvince) {
        this.applyCompanyProvince = applyCompanyProvince;
    }

    public String getApplyCompanyCity() {
        return applyCompanyCity;
    }

    public void setApplyCompanyCity(String applyCompanyCity) {
        this.applyCompanyCity = applyCompanyCity;
    }

    public String getApplyCompanyAddress() {
        return applyCompanyAddress;
    }

    public void setApplyCompanyAddress(String applyCompanyAddress) {
        this.applyCompanyAddress = applyCompanyAddress;
    }

    public String getApplyCompanyPropery() {
        return applyCompanyPropery;
    }

    public void setApplyCompanyPropery(String applyCompanyPropery) {
        this.applyCompanyPropery = applyCompanyPropery;
    }

    public String getApplyIndustryType() {
        return applyIndustryType;
    }

    public void setApplyIndustryType(String applyIndustryType) {
        this.applyIndustryType = applyIndustryType;
    }

    public String getApplyIndustryType2() {
        return applyIndustryType2;
    }

    public void setApplyIndustryType2(String applyIndustryType2) {
        this.applyIndustryType2 = applyIndustryType2;
    }

    public String getApplyJobPost() {
        return applyJobPost;
    }

    public void setApplyJobPost(String applyJobPost) {
        this.applyJobPost = applyJobPost;
    }

    public String getApplyAddress() {
        return applyAddress;
    }

    public void setApplyAddress(String applyAddress) {
        this.applyAddress = applyAddress;
    }

    public String getApplyBankCardId() {
        return applyBankCardId;
    }

    public void setApplyBankCardId(String applyBankCardId) {
        this.applyBankCardId = applyBankCardId;
    }

    public String getApplyBankCardLevel() {
        return applyBankCardLevel;
    }

    public void setApplyBankCardLevel(String applyBankCardLevel) {
        this.applyBankCardLevel = applyBankCardLevel;
    }

    public String getCidcard() {
        return cidcard;
    }

    public int getLtype() {
        return ltype;
    }

    public void setLtype(int ltype) {
        this.ltype = ltype;
    }

    public String getIshot() {
        return ishot;
    }

    public void setIshot(String ishot) {
        this.ishot = ishot;
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

    public void setCidcard(String cidcard) {
        this.cidcard = cidcard;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCstatus() {
        return cstatus;
    }

    public void setCstatus(String cstatus) {
        this.cstatus = cstatus;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getIdcardid() {
        return idcardid;
    }

    public void setIdcardid(String idcardid) {
        this.idcardid = idcardid;
    }

    public String getExibankid() {
        return exibankid;
    }

    public void setExibankid(String exibankid) {
        this.exibankid = exibankid;
    }

    public String getImgauthcode() {
        return imgauthcode;
    }

    public void setImgauthcode(String imgauthcode) {
        this.imgauthcode = imgauthcode;
    }

    public String getPhoneauthcode() {
        return phoneauthcode;
    }

    public void setPhoneauthcode(String phoneauthcode) {
        this.phoneauthcode = phoneauthcode;
    }

    private MaterialModel model;

    //参数
    private String data;


    public String getIapplyid() {
        return iapplyid;
    }

    public void setIapplyid(String iapplyid) {
        this.iapplyid = iapplyid;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getIbankid() {
        return ibankid;
    }

    public void setIbankid(String ibankid) {
        this.ibankid = ibankid;
    }

    public String getCfield() {
        return cfield;
    }

    public void setCfield(String cfield) {
        this.cfield = cfield;
    }

    public String getCbankids() {
        return cbankids;
    }

    public void setCbankids(String cbankids) {
        this.cbankids = cbankids;
    }

    public String getClabel() {
        return clabel;
    }

    public void setClabel(String clabel) {
        this.clabel = clabel;
    }

    public String getCcreditids() {
        return ccreditids;
    }

    public void setCcreditids(String ccreditids) {
        this.ccreditids = ccreditids;
    }

    public String getCxml() {
        return cxml;
    }

    public void setCxml(String cxml) {
        this.cxml = cxml;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public String getIcityid() {
        return icityid;
    }

    public void setIcityid(String icityid) {
        this.icityid = icityid;
    }

    public String getIdistrictid() {
        return idistrictid;
    }

    public void setIdistrictid(String idistrictid) {
        this.idistrictid = idistrictid;
    }

    public String getIbizid() {
        return ibizid;
    }

    public void setIbizid(String ibizid) {
        this.ibizid = ibizid;
    }

    public String getIpostaddr() {
        return ipostaddr;
    }

    public void setIpostaddr(String ipostaddr) {
        this.ipostaddr = ipostaddr;
    }

    public MaterialModel getModel() {
        if(model==null){
            model = new MaterialModel();
            this.initModel();
        }
        return model;
    }
    public void setModel(MaterialModel model) {
        this.model = model;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
//		model.setTest1("setDate setTest1");
    }

    private void initModel(){
        if(this.data!=null && model !=null){
            this.model.initMaterialModel(data);
        }
    }

    public String getChome_pname() {
        return chome_pname;
    }

    public void setChome_pname(String chome_pname) {
        this.chome_pname = chome_pname;
    }

    public String getChome_cname() {
        return chome_cname;
    }

    public void setChome_cname(String chome_cname) {
        this.chome_cname = chome_cname;
    }

    public String getChome_dname() {
        return chome_dname;
    }

    public void setChome_dname(String chome_dname) {
        this.chome_dname = chome_dname;
    }

    public String getCcompany_pname() {
        return ccompany_pname;
    }

    public void setCcompany_pname(String ccompany_pname) {
        this.ccompany_pname = ccompany_pname;
    }

    public String getCcompany_cname() {
        return ccompany_cname;
    }

    public void setCcompany_cname(String ccompany_cname) {
        this.ccompany_cname = ccompany_cname;
    }

    public String getCcompany_dname() {
        return ccompany_dname;
    }

    public void setCcompany_dname(String ccompany_dname) {
        this.ccompany_dname = ccompany_dname;
    }

    public String getCcardcity() {
        return ccardcity;
    }

    public void setCcardcity(String ccardcity) {
        this.ccardcity = ccardcity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getApplyCompanyCityName() {
        return applyCompanyCityName;
    }

    public void setApplyCompanyCityName(String applyCompanyCityName) {
        this.applyCompanyCityName = applyCompanyCityName;
    }

    public String getApplyCompanyProvinceName() {
        return applyCompanyProvinceName;
    }

    public void setApplyCompanyProvinceName(String applyCompanyProvinceName) {
        this.applyCompanyProvinceName = applyCompanyProvinceName;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getOnlyCheckSms() {
        return onlyCheckSms;
    }

    public void setOnlyCheckSms(String onlyCheckSms) {
        this.onlyCheckSms = onlyCheckSms;
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
}
