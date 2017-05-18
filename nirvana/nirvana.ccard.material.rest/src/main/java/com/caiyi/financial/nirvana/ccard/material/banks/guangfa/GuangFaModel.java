package com.caiyi.financial.nirvana.ccard.material.banks.guangfa;


import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;

/**
 * 广发客户资料
 * @author lwg
 *
 */
public class GuangFaModel {
    private GuangFaModel(){}

    /**
     * 本地客户资料转为广发客户资料。没有做验证。
     * @param bean
     * @return
     */
    public static GuangFaModel transfer(MaterialBean bean){
        MaterialModel materialModel = bean.getModel();
        GuangFaModel guangFaModel = new GuangFaModel();
        //第一页数据
        guangFaModel.setCbsProvince(bean.getApplyProvinceId());//*常驻城市-省份
        guangFaModel.setCbsCity(bean.getApplyCityId());// *常驻城市-市区
        guangFaModel.setCbsNameCn(materialModel.getCname());//*申请人姓名
        guangFaModel.setCbsNameSpell(materialModel.getCenglishname());//*拼音
        guangFaModel.setCbsIdCardNo(materialModel.getCidcard());//*身份证
        String effectDate = materialModel.getCidexpirationtime();
        if (BankUtils.isEmpty(effectDate)||"-1".equals(effectDate)){
            effectDate = BankUtils.getDefaultEffectDate(materialModel.getCidcard());
        }else{
            effectDate = effectDate.split(",")[1];
        }
        guangFaModel.setCbsEffectDate(effectDate);//身份证有效期
        String sexAndBirthday = BankUtils.getSexAndBirthday(materialModel.getCidcard());
        guangFaModel.setCbsSex(sexAndBirthday.split("\\|")[0]);//*性别
        guangFaModel.setCbsBirthday(sexAndBirthday.split("\\|")[1]);//*生日
        guangFaModel.setCbsMobile(materialModel.getCphone());//*手机号
        guangFaModel.setSmsPassword(bean.getPhoneauthcode());//*短信验证码

        //第二页数据
        guangFaModel.setCwkCompanyName(materialModel.getCcompanyname());// *单位名称
        guangFaModel.setCwkCompanyProv(bean.getApplyCompanyProvince());// *单位地址-省份
        guangFaModel.setCwkCompanyCity(bean.getApplyCompanyCity());// *单位地址-城市
        guangFaModel.setCwkCompanyAddr(materialModel.getCcompany_detailaddress());// *单位地址-详细地址
        String cpmpanyTel = materialModel.getCcompany_telnum();
        guangFaModel.setCbsCompanyTel(cpmpanyTel);// *单位电话
        if (BankUtils.isNotEmpty(cpmpanyTel)&&cpmpanyTel.contains("-")){
            String[] workPhone = cpmpanyTel.split("-");
            guangFaModel.setArea_code(workPhone[0]);// *单位电话--区号
            guangFaModel.setHost_num(workPhone[1]);// *单位电话--主机号
            if (workPhone.length>2){
                guangFaModel.setMobilef(workPhone[2]);// *单位电话--分机号(如无分机号不需填写)
            }
        }
        guangFaModel.setCwkCompanyKind(bean.getApplyCompanyPropery());// *单位性质:1机关、事业；2大型国有、股份制；3外商独资；4中外合作企业;5私营、集体;6个体
        guangFaModel.setLargeKind(bean.getApplyIndustryType());// *行业性质--行业大类
        guangFaModel.setCwkIndustryKind(bean.getApplyIndustryType2());// *行业性质--行业小类
        guangFaModel.setCwkEmployeeNo("6".equals(bean.getApplyCompanyPropery()) ? "2" : "3");// *员工人数:1:10人以下,2:10-50人,3:50-200人,4:200-1000人,5:1000人以上
        guangFaModel.setCwkDepartment(materialModel.getCdepartmentname());// *任职部门
        String cwkJob = materialModel.getIdepartment();
        //错误代码：EBLN0017;提示信息：职位格式有误
        if (BankUtils.isEmpty(cwkJob)||"1".equals(cwkJob)){
            cwkJob = "一般员工";
        }else if ("2".equals(cwkJob)){
            if ("1".equals(bean.getApplyCompanyPropery())||"2".equals(bean.getApplyCompanyPropery())){
                cwkJob = "处级";
            }else {
                cwkJob = "部门经理";
            }
        }else if ("3".equals(cwkJob)){
            if ("1".equals(bean.getApplyCompanyPropery())||"2".equals(bean.getApplyCompanyPropery())){
                cwkJob = "局级以上";
            }else {
                cwkJob = "经理";
            }
        }else if ("4".equals(cwkJob)){
            if ("1".equals(bean.getApplyCompanyPropery())||"2".equals(bean.getApplyCompanyPropery())){
                cwkJob = "科级";
            }else {
                cwkJob = "主管";
            }
        }
        guangFaModel.setCwkJob(cwkJob);// *职位:1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级
        guangFaModel.setCwkJobYear("6".equals(materialModel.getItimeinjob())?"7":materialModel.getItimeinjob());// *任职年数。0请选择，1:1年以下,2:1年,3:2年,4:3年,5:4年,6:5年,7:6年及6年以上:
        double yearPay = Double.parseDouble(BankUtils.isEmpty(materialModel.getIannualsalary())?"0":materialModel.getIannualsalary());
        //年薪格式，整形
        String sYearPay = "";
        if(yearPay<1.5){
            sYearPay = "1";
        }else if (yearPay<=2){
            sYearPay = "2";
        }else if (yearPay<3){
            sYearPay = "4";
        }else if (yearPay<4.1){
            sYearPay = "5";
        }else if (yearPay<5.1){
            sYearPay = "6";
        }else if (yearPay<8.1){
            sYearPay = "7";
        }else if (yearPay>8){
            sYearPay = "8";
        }
        guangFaModel.setCwkYearPay(sYearPay);// 年薪。0请选择，1:1.5万以下，2:1.5-2万，3:2-2.5万，4:2.5-3万，5:3.4万，6:4-5万，7:5-8万，8:8万以上

        // 第三步填写内容
        guangFaModel.setCpsEmail(materialModel.getCemail());// *电子邮箱
        guangFaModel.setCpsIsMarry(materialModel.getMaritalstatus());// *婚姻状况.1未婚,2已婚,3其他
        guangFaModel.setCpsDegree(materialModel.getIdegree());// *学历:1博士或以上毕业,2硕士,3本科,4大专,5高中、中专及以下
        guangFaModel.setCpsHomeProv(bean.getApplyProvinceId());// 住宅-省份
        guangFaModel.setCpsHomeCity(bean.getApplyCityId());// 住宅-市
        guangFaModel.setCpsHomeAddr(materialModel.getChome_detailaddress());// 住宅-详细地址
        String cpsHouseType = materialModel.getResidencestatus();
        if ("1".equals(cpsHouseType)){
            cpsHouseType = "5";
        }else if ("2".equals(cpsHouseType)){
            cpsHouseType = "4";
        }else if ("3".equals(cpsHouseType)){
            cpsHouseType = "1";
        }else if ("4".equals(cpsHouseType)){
            cpsHouseType = "2";
        }else if ("5".equals(cpsHouseType)){
            cpsHouseType = "6";
        }
        guangFaModel.setCpsHouseType(cpsHouseType);// *住宅类型:1租用,2与父母同住,4无按揭自置（已付清房款）,5有按揭自置,6其它
        guangFaModel.setCpsYear("6".equals(materialModel.getItimeinjob())?"7":materialModel.getItimeinjob());// *居住年限：1:1年以下，2:1年，3:2年,4:3年,5:4年,6:5年,7:6年及6年以上
        guangFaModel.setCpsFamilyName(materialModel.getFamilyname());// *亲属姓名，不能是自己名字
        guangFaModel.setCpsFamilyMobile(materialModel.getCfamilyphonenum());// *亲属手机
        guangFaModel.setCpsFamilyRelation("");// 亲属与持卡人关系。(隐藏信息，手机版不需要填)
        guangFaModel.setCsvBillType("1");// *账单类型： 1电子账单
        guangFaModel.setCsvBillAddr("2");//固定，来自上一页面
        guangFaModel.setCsvPostAddr("2");// *寄卡地址： 2单位地址(固定)
        return guangFaModel;
    }
    //第一页数据
    private String cbsProvince = "";//*常驻城市-省份
    private String cbsCity = "";// *常驻城市-市区
    private String cbsNameCn = "";//*申请人姓名
    private String cbsNameSpell = "";//*拼音
    private String cbsIdCardNo = "";//*身份证
    private String cbsEffectDate = "";//身份证有效期，格式yyyyMMdd，留空代表永久有效
    private String cbsSex = "";//*性别
    private String cbsBirthday = "";//*生日
    private String cbsMobile = "";//手机号
    private String smsPassword = "";//短信验证码

    // 第二页数据
    private String cwkCompanyName = "";// *单位名称
    private String cwkCompanyProv = "";// *单位地址-省份
    private String cwkCompanyCity = "";// *单位地址-城市
    private String cwkCompanyAddr = "";// *单位地址-详细地址58
    private String cbsCompanyTel = "";// *单位电话
    private String area_code = "";// *单位电话--区号
    private String host_num = "";// *单位电话--主机号
    private String mobilef = "";// *单位电话--分机号(如无分机号不需填写)
    private String cwkCompanyKind = "";// *//单位性质:1机关、事业；2大型国有、股份制；3外商独资；4中外合作企业;5私营、集体;6个体
    private String largeKind = "";// *行业性质--行业大类
    private String cwkIndustryKind = "";// *行业性质--行业小类
    private String cwkEmployeeNo = "";// *员工人数:1:10人以下,2:10-50人,3:50-200人,4:200-1000人,5:1000人以上
    private String cwkJobLevel = "";// 不填
    private String cwkDepartment = "";// *任职部门
    private String cwkJob = "";// *职位
    private String cwkJobYear = "";// *任职年数。0请选择，1:1年以下,2:1年,3:2年,4:3年,5:4年,6:5年,7:6年及6年以上
    private String cwkYearPay = "";// 年薪。0请选择，1:1.5万以下，2:1.5-2万，3:2-2.5万，4:2.5-3万，5:3.4万，6:4-5万，7:5-8万，8:8万以上

    // 第三步填写内容
    private String cpsEmail = "";// *电子邮箱
    private String cpsIsMarry = "";// *婚姻状况.1未婚,2已婚,3其他
    private String cpsDegree = "";// *学历:1博士或以上毕业,2硕士,3本科,4大专,5高中、中专及以下
    private String cpsHomeProv = "";// 住宅-省份
    private String cpsHomeCity = "";// 住宅-市
    private String cpsHomeAddr = "";// 住宅-详细地址
    private String cpsHouseType = "";// *住宅类型:1租用,2与父母同住,4无按揭自置（已付清房款）,5有按揭自置,6其它
    private String cpsYear = "";// *居住年限
    private String cpsFamilyName = "";// *亲属姓名，不能是自己名字
    private String cpsFamilyMobile = "";// *亲属手机
    private String cpsFamilyRelation = "";// 亲属与持卡人关系。(隐藏信息，手机版不需要填)
    private String csvBillType = "1";// *账单类型： 1电子账单
    private String csvBillAddr = "2";//固定，来自上一页面
    private String csvPostAddr = "2";// *寄卡地址： 2单位地址（固定）

    public String getCbsProvince() {
        return cbsProvince;
    }
    public void setCbsProvince(String cbsProvince) {
        this.cbsProvince = cbsProvince;
    }
    public String getCbsCity() {
        return cbsCity;
    }
    public void setCbsCity(String cbsCity) {
        this.cbsCity = cbsCity;
    }
    public String getCbsNameCn() {
        return cbsNameCn;
    }
    public void setCbsNameCn(String cbsNameCn) {
        this.cbsNameCn = cbsNameCn;
    }
    public String getCbsNameSpell() {
        return cbsNameSpell;
    }
    public void setCbsNameSpell(String cbsNameSpell) {
        this.cbsNameSpell = cbsNameSpell;
    }
    public String getCbsIdCardNo() {
        return cbsIdCardNo;
    }
    public void setCbsIdCardNo(String cbsIdCardNo) {
        this.cbsIdCardNo = cbsIdCardNo;
    }
    public String getCbsEffectDate() {
        return cbsEffectDate;
    }
    public void setCbsEffectDate(String cbsEffectDate) {
        this.cbsEffectDate = cbsEffectDate;
    }
    public String getCbsSex() {
        return cbsSex;
    }
    public void setCbsSex(String cbsSex) {
        this.cbsSex = cbsSex;
    }
    public String getCbsBirthday() {
        return cbsBirthday;
    }
    public void setCbsBirthday(String cbsBirthday) {
        this.cbsBirthday = cbsBirthday;
    }
    public String getCbsMobile() {
        return cbsMobile;
    }
    public void setCbsMobile(String cbsMobile) {
        this.cbsMobile = cbsMobile;
    }

    public String getSmsPassword() {
        return smsPassword;
    }

    public void setSmsPassword(String smsPassword) {
        this.smsPassword = smsPassword;
    }

    public String getCwkCompanyName() {
        return cwkCompanyName;
    }
    public void setCwkCompanyName(String cwkCompanyName) {
        this.cwkCompanyName = cwkCompanyName;
    }
    public String getCwkCompanyProv() {
        return cwkCompanyProv;
    }
    public void setCwkCompanyProv(String cwkCompanyProv) {
        this.cwkCompanyProv = cwkCompanyProv;
    }
    public String getCwkCompanyCity() {
        return cwkCompanyCity;
    }
    public void setCwkCompanyCity(String cwkCompanyCity) {
        this.cwkCompanyCity = cwkCompanyCity;
    }
    public String getCwkCompanyAddr() {
        return cwkCompanyAddr;
    }
    public void setCwkCompanyAddr(String cwkCompanyAddr) {
        this.cwkCompanyAddr = cwkCompanyAddr;
    }
    public String getCbsCompanyTel() {
        return cbsCompanyTel;
    }
    public void setCbsCompanyTel(String cbsCompanyTel) {
        this.cbsCompanyTel = cbsCompanyTel;
    }
    public String getArea_code() {
        return area_code;
    }
    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }
    public String getHost_num() {
        return host_num;
    }
    public void setHost_num(String host_num) {
        this.host_num = host_num;
    }
    public String getMobilef() {
        return mobilef;
    }
    public void setMobilef(String mobilef) {
        this.mobilef = mobilef;
    }
    public String getCwkCompanyKind() {
        return cwkCompanyKind;
    }
    public void setCwkCompanyKind(String cwkCompanyKind) {
        this.cwkCompanyKind = cwkCompanyKind;
    }
    public String getLargeKind() {
        return largeKind;
    }
    public void setLargeKind(String largeKind) {
        this.largeKind = largeKind;
    }
    public String getCwkIndustryKind() {
        return cwkIndustryKind;
    }
    public void setCwkIndustryKind(String cwkIndustryKind) {
        this.cwkIndustryKind = cwkIndustryKind;
    }
    public String getCwkEmployeeNo() {
        return cwkEmployeeNo;
    }
    public void setCwkEmployeeNo(String cwkEmployeeNo) {
        this.cwkEmployeeNo = cwkEmployeeNo;
    }
    public String getCwkJobLevel() {
        return cwkJobLevel;
    }
    public void setCwkJobLevel(String cwkJobLevel) {
        this.cwkJobLevel = cwkJobLevel;
    }
    public String getCwkDepartment() {
        return cwkDepartment;
    }
    public void setCwkDepartment(String cwkDepartment) {
        this.cwkDepartment = cwkDepartment;
    }
    public String getCwkJob() {
        return cwkJob;
    }
    public void setCwkJob(String cwkJob) {
        this.cwkJob = cwkJob;
    }
    public String getCwkJobYear() {
        return cwkJobYear;
    }
    public void setCwkJobYear(String cwkJobYear) {
        this.cwkJobYear = cwkJobYear;
    }
    public String getCwkYearPay() {
        return cwkYearPay;
    }
    public void setCwkYearPay(String cwkYearPay) {
        this.cwkYearPay = cwkYearPay;
    }
    public String getCpsEmail() {
        return cpsEmail;
    }
    public void setCpsEmail(String cpsEmail) {
        this.cpsEmail = cpsEmail;
    }
    public String getCpsIsMarry() {
        return cpsIsMarry;
    }
    public void setCpsIsMarry(String cpsIsMarry) {
        this.cpsIsMarry = cpsIsMarry;
    }
    public String getCpsDegree() {
        return cpsDegree;
    }
    public void setCpsDegree(String cpsDegree) {
        this.cpsDegree = cpsDegree;
    }
    public String getCpsHomeProv() {
        return cpsHomeProv;
    }
    public void setCpsHomeProv(String cpsHomeProv) {
        this.cpsHomeProv = cpsHomeProv;
    }
    public String getCpsHomeCity() {
        return cpsHomeCity;
    }
    public void setCpsHomeCity(String cpsHomeCity) {
        this.cpsHomeCity = cpsHomeCity;
    }
    public String getCpsHomeAddr() {
        return cpsHomeAddr;
    }
    public void setCpsHomeAddr(String cpsHomeAddr) {
        this.cpsHomeAddr = cpsHomeAddr;
    }
    public String getCpsHouseType() {
        return cpsHouseType;
    }
    public void setCpsHouseType(String cpsHouseType) {
        this.cpsHouseType = cpsHouseType;
    }
    public String getCpsYear() {
        return cpsYear;
    }
    public void setCpsYear(String cpsYear) {
        this.cpsYear = cpsYear;
    }
    public String getCpsFamilyName() {
        return cpsFamilyName;
    }
    public void setCpsFamilyName(String cpsFamilyName) {
        this.cpsFamilyName = cpsFamilyName;
    }
    public String getCpsFamilyMobile() {
        return cpsFamilyMobile;
    }
    public void setCpsFamilyMobile(String cpsFamilyMobile) {
        this.cpsFamilyMobile = cpsFamilyMobile;
    }
    public String getCpsFamilyRelation() {
        return cpsFamilyRelation;
    }
    public void setCpsFamilyRelation(String cpsFamilyRelation) {
        this.cpsFamilyRelation = cpsFamilyRelation;
    }
    public String getCsvBillType() {
        return csvBillType;
    }
    public void setCsvBillType(String csvBillType) {
        this.csvBillType = csvBillType;
    }
    public String getCsvBillAddr() {
        return csvBillAddr;
    }
    public void setCsvBillAddr(String csvBillAddr) {
        this.csvBillAddr = csvBillAddr;
    }
    public String getCsvPostAddr() {
        return csvPostAddr;
    }
    public void setCsvPostAddr(String csvPostAddr) {
        this.csvPostAddr = csvPostAddr;
    }

}