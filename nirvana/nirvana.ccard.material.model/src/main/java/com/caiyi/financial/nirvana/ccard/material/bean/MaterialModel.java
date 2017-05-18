package com.caiyi.financial.nirvana.ccard.material.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lizhijie on 2016/7/13.
 */
public class MaterialModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ibankid;
    private String cardid;
    private String icityid;
    private String imaterialid;//资料id
    private String cphone;//手机号
    private String precphone;
    private String cname;//姓名
    private String cenglishname;//姓名拼音
    private String cemail;//邮箱
    private String iannualsalary;//年薪(万)
    private String cidcard;//身份证号
    private String maritalstatus;//1、未婚 2、已婚 3、其它
    private String idegree;//1、博士及以上 2、硕士 3、本科 4、大专 5、高中、中专一下
    private String residencestatus;//住宅类型，1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它
    private String ihome_pid;//住宅省
    private String ihome_cid;//住宅市
    private String ihome_did;//住宅区
    private String chome_telnum;//住宅电话
    private String chome_detailaddress;//住宅地址
    private String chome_postcode;//住宅邮编
    private String ilivelen="3";//居住年限
    private String ipostaddress;
    private String ccompanyname;//单位名称
    private String icompany_pid;//公司省
    private String icompany_cid;//公司市
    private String icompany_did;//公司区
    private String ccompany_postcode;//公司邮编
    private String ccompany_detailaddress;//公司地址
    private String inatureofunit;//单位性质：1、机关/事业 2、国有 3、股份制 4、外商独资 5、中外合作企业 6、私营/集体 7、个体
    private String ccompany_telnum;//公司电话
    private String idepartment;//职位：1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级
    private String cdepartmentname;//任职部门
    private String itimeinjob;//任职年数：1、一年以下 2、一年 3、两年 4、三年 5、四年 6、五年及以上
    private String iniatureofbusiness;//行业性质：1、金融业 2、IT通讯 3、服务业 4、制造业 5、建筑地产 6、商贸零售 7、运输物流 8、法律咨询 9、教育科研 10、医疗卫生 11、机关团体 12、体育娱乐 13、旅游酒店餐饮 14、其它
    private String familyname;//亲属姓名
    private String ifamilyties;//亲属关系1.配偶 2.父母 3.子女 4.兄弟姐妹
    private String cfamilyphonenum;//亲属手机号
    private String cemergencycontactname;//紧急联系人姓名
    private String icemergencyties="1";//紧急联系人关系
    private String cemergencyphone;//紧急联系人手机
    private String cidexpirationtime;//身份证有效期
    private String cidissueaddress;
    private String chavebankcard="";
    private String chavebankcredit="";
    private String isex="1";
    private String ctheme;
    private String cstartinfo;
    private String icreditamount;
    private String cprimaryschoolname;
    private String cbak3="";
    private String cbak2="";
    private String cbak1="";
    private String istatus="0";
    private String cmonthlypayment="0";//房贷月供
    private String ccardcity="";//申卡城市
    private String ctmpphone="";//临时手机号
    // add by lcs 20160616  花旗银行使用 start
    // 住宅所在区县
    private String chomedistrictname = "";
    // 公司所在区县
    private String ccompanydistrictname = "";
    // add by lcs 20160616  花旗银行使用 end


    private JSONObject json;



    public MaterialModel() {
    }

    public void initMaterialModel(String data) {
        if(data==null){return;}
        json = JSONObject.parseObject(data);

        String ctheme = json.getString("theme");// 1、秒批卡 2、高额卡 3、新手卡 4、热申卡 5、商旅卡
        // 6、车主卡 7、购物卡 8、女人卡
        this.setCtheme(ctheme);

        String cstartinfo = json.getString("startInfo");// 1、无固定工资 2、没有信用卡
        // 3、有不良征信 4、在校学生
        // 5、以上都不是
        this.setCstartinfo(cstartinfo);

        String idegree = json.getString("degree");// 1、硕士 2、博士及以上 3、本科 4、大专
        // 5、高中、中专一下
        this.setIdegree(idegree);

        String maritalStatus = json.getString("maritalStatus");// 1、未婚 2、已婚 3、其它
        this.setMaritalstatus(maritalStatus);

        String inatureOfUnit = json.getString("natureOfUnit");// 1、机关/事业 2、国有 3、股份制 4、外商独资 5、中外合作企业 6、私营/集体 7、个体
        this.setInatureofunit(inatureOfUnit);

        String iniatureOfBusiness = json.getString("natureOfBusiness");// 1、金融业 2、IT通讯 3、服务业 4、制造业 5、建筑地产
        this.setIniatureofbusiness(iniatureOfBusiness);

        String idepartment = json.getString("post");// 1、一般员工 2、部门经理/处级
        this.setIdepartment(idepartment);

        String itimeInJob = json.getString("timeInJob");// 任职时间 1、一年以下 2、一年
        this.setItimeinjob(itimeInJob);

        String residenceStatus = json.getString("residenceStatus");// 1、自购有贷款房 2、自有无贷款房 3、租用4、与父母同
        this.setResidencestatus(residenceStatus);

        String ipostAddress = json.getString("postAddress");// 邮寄地址：（1、单位地址 2、住宅地址
        this.setIpostaddress(ipostAddress);

        String cname = json.getString("name");// 姓名
        this.setCname(cname);

        String cenglishName = json.getString("englishName");// 英文名/拼音
        this.setCenglishname(cenglishName);

        String cemail = json.getString("email");// 邮箱
        this.setCemail(cemail);

        String cidcard = json.getString("cardID"); // 身份证号
        if(isNotNull(cidcard) && cidcard.length()>15){
            this.setCidcard(cidcard);
            int len = cidcard.length();
            int z = Integer.parseInt(cidcard.substring(len-2, len-1));
            this.setIsex(z%2+"");
        }
        // ihome_pid ihome_cid ihome_did
        String pcdid = json.getString("homeAddress"); // 省id-市id-区id
        if(isNotNull(pcdid)){
            String[] pids = pcdid.split("-");
            this.setIhome_pid(pids[0]);
            this.setIhome_cid(pids[1]);
            this.setIhome_did(pids[2]);
        }

        String chome_detailaddress = json.getString("homeDetailAddress"); // 住宅详细地址
        this.setChome_detailaddress(chome_detailaddress);

        String chome_postcode = json.getString("homePostcode"); // 住宅邮编
        this.setChome_postcode(chome_postcode);

        String iannualSalary = json.getString("annualSalary"); // 年薪:万元
        this.setIannualsalary(iannualSalary);

        String ccompanyname = json.getString("company"); // 公司名称
        this.setCcompanyname(ccompanyname);

        // icompany_pid icompany_cid icompany_did
        String cpcdid = json.getString("companyAddress"); // 省id-市id-区id
        if(isNotNull(cpcdid)){
            String[] cids = cpcdid.split("-");
            this.setIcompany_pid(cids[0]);
            this.setIcompany_cid(cids[1]);
            this.setIcompany_did(cids[2]);
        }
        // add by lcs  20160616 start
        String cpcdname = json.getString("companyAddressStr"); // 省-市-区
        if(isNotNull(cpcdname)){
            String[] cnames = cpcdname.split("-");
            this.setCcompanydistrictname(cnames[2]);
        }
        String cpcdnamehome = json.getString("homeAddressStr"); // 省-市-区
        if(isNotNull(cpcdnamehome)){
            String[] chpcdnames = cpcdnamehome.split("-");
            this.setChomedistrictname(chpcdnames[2]);
        }
        // add by lcs  20160616 end
        String ccompany_detailaddress = json.getString("companyDetailAddress"); // 住宅详细地址
        this.setCcompany_detailaddress(ccompany_detailaddress);

        String ccompany_postcode = json.getString("companyPostcode"); // 公司邮编
        this.setCcompany_postcode(nStr(ccompany_postcode));

        String ccompany_telnum = json.getString("companyTelNum"); // 公司固话号码
        // 021-3333333
        this.setCcompany_telnum(nStr(ccompany_telnum));

        String familyName = json.getString("familyName"); // 亲属名字
        this.setFamilyname(familyName);

        String ifamilyTies = json.getString("familyTies"); // 1.配偶 2.父母 3.子女
        // 4.兄弟姐妹
        this.setIfamilyties(ifamilyTies);

        String cfamilyPhoneNum = json.getString("familyPhoneNum"); // 亲属手机号码
        this.setCfamilyphonenum(cfamilyPhoneNum);

        String cphone = json.getString("userPhoneNum"); // 用户手机号码
        this.setCphone(cphone);

        String oldcphone = json.getString("userOldPhoneNum"); // 旧的用户手机号码
        this.setPrecphone(oldcphone);

        String cdepartmentName = json.getString("departmentName");// 任职部门
        this.setCdepartmentname(nStr(cdepartmentName));

        String chome_telnum = json.getString("homeTel");// 住宅电话
        this.setChome_telnum(nStr(chome_telnum));

        String cemergencyContactName = json.getString("emergencyContactName");// 紧急联系人姓名
        this.setCemergencycontactname(nStr(cemergencyContactName));

        String cemergencyPhone = json.getString("emergencyPhone");// 紧急联系人手机
        this.setCemergencyphone(nStr(cemergencyPhone));

        String cprimarySchoolName = json.getString("primarySchoolName");// 就读小学名字
        this.setCprimaryschoolname(nStr(cprimarySchoolName));

        String icreditAmount = json.getString("creditAmount");// 信用额度万元
        this.setIcreditamount(nStr(icreditAmount));

        // --------------身份证发证机关--------------------
        String idIssueAddress = json.getString("idIssueAddress");// 省id-市id-区id
        idIssueAddress = isNotNull(idIssueAddress)?idIssueAddress:"";
        String getIdCardCity = json.getString("getIdCardCity");// 身份证发证机关
        getIdCardCity = isNotNull(getIdCardCity)?getIdCardCity:"";

        this.setCidissueaddress(idIssueAddress + "-" + getIdCardCity);

        String haveBankCardSum = json.getString("haveBankCardSum");// 用逗号隔开,分别表示银行家数和信用卡张数
        this.setChavebankcredit(nStr(haveBankCardSum));// 例：2,2表示有2家银行2张卡卡个数不能低于银行家数

        // 身份证有效期 如果是短期有效返回给后台的是 例: 1,2000-2-3 表示 短期有效时间到2000年2月3号 如果是长期，就返回 2
        // 表示长期有效
        // null没填/20100101-20170101（起始-截至）/20170101（截至）
        String idExpirationTime = json.getString("idExpirationTime");
        this.setCidexpirationtime(idExpirationTime);

        // ------------申请记录特有信息----------------------
        json.getString("getCardCity");// 发卡城市选择
        json.getString("opencardType");// 1.柜台身份确认快递寄卡 2.网点身份确认现场取卡

        json.getString("gift");// 1.礼物1 2.礼物2
        // 当选择办卡方式选择的是现场取卡时才需要下面两个参数
        json.getString("areaAddress");// 选择区域id
        json.getString("dotAddress");// 选择网点id

        String ibankid = json.getString("bankid"); // 银行id
        this.setIbankid(ibankid);

        String icardid = json.getString("cardid"); // 卡id
        this.setCardid(icardid);

        this.setIcityid(json.getString("icityid"));

        this.setCmonthlypayment(json.getString("cmonthlypayment"));

        this.setCtmpphone(json.getString("tempUserPhoneNum"));


    }


    public boolean valibean(){
        if(isNotNull(cmonthlypayment)){
            Boolean b = isDouble(cmonthlypayment);
            if(!b){return false;}
        }
        return true;
    }





    public String getCtmpphone() {
        return ctmpphone;
    }

    public void setCtmpphone(String ctmpphone) {
        this.ctmpphone = ctmpphone;
    }

    public String getCcardcity() {
        return ccardcity;
    }

    public void setCcardcity(String ccardcity) {
        this.ccardcity = ccardcity;
    }

    public String getCmonthlypayment() {
        return cmonthlypayment;
    }

    public void setCmonthlypayment(String cmonthlypayment) {
        this.cmonthlypayment = cmonthlypayment;
    }

    public String getIcityid() {
        return icityid;
    }


    public void setIcityid(String icityid) {
        this.icityid = icityid;
    }


    public String getImaterialid() {
        return imaterialid;
    }

    public void setImaterialid(String imaterialid) {
        this.imaterialid = imaterialid;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getPrecphone() {
        return precphone;
    }

    public void setPrecphone(String precphone) {
        this.precphone = precphone;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCenglishname() {
        return cenglishname;
    }

    public void setCenglishname(String cenglishname) {
        this.cenglishname = cenglishname;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public String getIannualsalary() {
        return iannualsalary;
    }

    public void setIannualsalary(String iannualsalary) {
        this.iannualsalary = iannualsalary;
    }

    public String getCidcard() {
        return cidcard;
    }

    public void setCidcard(String cidcard) {
        this.cidcard = cidcard;
    }

    public String getMaritalstatus() {
        return maritalstatus;
    }

    public void setMaritalstatus(String maritalstatus) {
        this.maritalstatus = maritalstatus;
    }

    public String getIdegree() {
        return idegree;
    }

    public void setIdegree(String idegree) {
        this.idegree = idegree;
    }

    public String getResidencestatus() {
        return residencestatus;
    }

    public void setResidencestatus(String residencestatus) {
        this.residencestatus = residencestatus;
    }

    public String getIhome_pid() {
        return ihome_pid;
    }

    public void setIhome_pid(String ihome_pid) {
        this.ihome_pid = ihome_pid;
    }

    public String getIhome_cid() {
        return ihome_cid;
    }

    public void setIhome_cid(String ihome_cid) {
        this.ihome_cid = ihome_cid;
    }

    public String getIhome_did() {
        return ihome_did;
    }

    public void setIhome_did(String ihome_did) {
        this.ihome_did = ihome_did;
    }

    public String getChome_telnum() {
        return chome_telnum;
    }

    public void setChome_telnum(String chome_telnum) {
        this.chome_telnum = chome_telnum;
    }

    public String getChome_detailaddress() {
        return chome_detailaddress;
    }

    public void setChome_detailaddress(String chome_detailaddress) {
        this.chome_detailaddress = chome_detailaddress;
    }

    public String getChome_postcode() {
        return chome_postcode;
    }

    public void setChome_postcode(String chome_postcode) {
        this.chome_postcode = chome_postcode;
    }

    public String getIlivelen() {
        return ilivelen;
    }

    public void setIlivelen(String ilivelen) {
        this.ilivelen = ilivelen;
    }

    public String getIpostaddress() {
        return ipostaddress;
    }

    public void setIpostaddress(String ipostaddress) {
        this.ipostaddress = ipostaddress;
    }

    public String getCcompanyname() {
        return ccompanyname;
    }

    public void setCcompanyname(String ccompanyname) {
        this.ccompanyname = ccompanyname;
    }

    public String getIcompany_pid() {
        return icompany_pid;
    }

    public void setIcompany_pid(String icompany_pid) {
        this.icompany_pid = icompany_pid;
    }

    public String getIcompany_cid() {
        return icompany_cid;
    }

    public void setIcompany_cid(String icompany_cid) {
        this.icompany_cid = icompany_cid;
    }

    public String getIcompany_did() {
        return icompany_did;
    }

    public void setIcompany_did(String icompany_did) {
        this.icompany_did = icompany_did;
    }

    public String getCcompany_postcode() {
        return ccompany_postcode;
    }

    public void setCcompany_postcode(String ccompany_postcode) {
        this.ccompany_postcode = ccompany_postcode;
    }
    public String getCcompany_detailaddress() {
        return ccompany_detailaddress;
    }

    public void setCcompany_detailaddress(String ccompany_detailaddress) {
        this.ccompany_detailaddress = ccompany_detailaddress;
    }

    public String getInatureofunit() {
        return inatureofunit;
    }

    public void setInatureofunit(String inatureofunit) {
        this.inatureofunit = inatureofunit;
    }

    public String getCcompany_telnum() {
        return ccompany_telnum;
    }

    public void setCcompany_telnum(String ccompany_telnum) {
        this.ccompany_telnum = ccompany_telnum;
    }

    public String getIdepartment() {
        return idepartment;
    }

    public void setIdepartment(String idepartment) {
        this.idepartment = idepartment;
    }

    public String getCdepartmentname() {
        return cdepartmentname;
    }

    public void setCdepartmentname(String cdepartmentname) {
        this.cdepartmentname = cdepartmentname;
    }

    public String getItimeinjob() {
        return itimeinjob;
    }

    public void setItimeinjob(String itimeinjob) {
        this.itimeinjob = itimeinjob;
    }

    public String getIniatureofbusiness() {
        return iniatureofbusiness;
    }

    public void setIniatureofbusiness(String iniatureofbusiness) {
        this.iniatureofbusiness = iniatureofbusiness;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getIfamilyties() {
        return ifamilyties;
    }

    public void setIfamilyties(String ifamilyties) {
        this.ifamilyties = ifamilyties;
    }

    public String getCfamilyphonenum() {
        return cfamilyphonenum;
    }

    public void setCfamilyphonenum(String cfamilyphonenum) {
        this.cfamilyphonenum = cfamilyphonenum;
    }

    public String getCemergencycontactname() {
        return cemergencycontactname;
    }

    public void setCemergencycontactname(String cemergencycontactname) {
        this.cemergencycontactname = cemergencycontactname;
    }

    public String getIcemergencyties() {
        return icemergencyties;
    }

    public void setIcemergencyties(String icemergencyties) {
        this.icemergencyties = icemergencyties;
    }

    public String getCemergencyphone() {
        return cemergencyphone;
    }

    public void setCemergencyphone(String cemergencyphone) {
        this.cemergencyphone = cemergencyphone;
    }

    public String getCidexpirationtime() {
        return cidexpirationtime;
    }

    public void setCidexpirationtime(String cidexpirationtime) {
        this.cidexpirationtime = cidexpirationtime;
    }

    public String getCidissueaddress() {
        return cidissueaddress;
    }

    public void setCidissueaddress(String cidissueaddress) {
        this.cidissueaddress = cidissueaddress;
    }

    public String getChavebankcard() {
        return chavebankcard;
    }

    public void setChavebankcard(String chavebankcard) {
        this.chavebankcard = chavebankcard;
    }

    public String getChavebankcredit() {
        return chavebankcredit;
    }

    public void setChavebankcredit(String chavebankcredit) {
        this.chavebankcredit = chavebankcredit;
    }

    public String getIsex() {
        return isex;
    }

    public void setIsex(String isex) {
        this.isex = isex;
    }

    public String getCtheme() {
        return ctheme;
    }

    public void setCtheme(String ctheme) {
        this.ctheme = ctheme;
    }

    public String getCstartinfo() {
        return cstartinfo;
    }

    public void setCstartinfo(String cstartinfo) {
        this.cstartinfo = cstartinfo;
    }

    public String getIcreditamount() {
        return icreditamount;
    }

    public void setIcreditamount(String icreditamount) {
        this.icreditamount = icreditamount;
    }

    public String getCprimaryschoolname() {
        return cprimaryschoolname;
    }

    public void setCprimaryschoolname(String cprimaryschoolname) {
        this.cprimaryschoolname = cprimaryschoolname;
    }

    public String getCbak3() {
        return cbak3;
    }

    public void setCbak3(String cbak3) {
        this.cbak3 = cbak3;
    }

    public String getCbak2() {
        return cbak2;
    }

    public void setCbak2(String cbak2) {
        this.cbak2 = cbak2;
    }

    public String getCbak1() {
        return cbak1;
    }

    public void setCbak1(String cbak1) {
        this.cbak1 = cbak1;
    }

    public String getIstatus() {
        return istatus;
    }

    public void setIstatus(String istatus) {
        this.istatus = istatus;
    }

    public String getIbankid() {
        return ibankid;
    }

    public void setIbankid(String ibankid) {
        this.ibankid = ibankid;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public static final boolean isNotNull(String str) {
        return str != null && !"".equals(str.trim());
    }
    private final static Pattern pattern = Pattern.compile("[0-9]*");

    public static final Boolean isInteger(String str) {
        if(str==null||"".equals(str)){return false;}
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static final Boolean isDouble(String str){
        if(str==null){return false;}
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static final String nStr(String str){
        return str==null?"":str;
    }
    public static void main(String[] args) {
        String s="361021X";
        int len = s.length();
        int z = Integer.parseInt(s.substring(len-2, len-1));

        System.out.println(z%2);
    }

    public String getCcompanydistrictname() {
        return ccompanydistrictname;
    }

    public void setCcompanydistrictname(String ccompanydistrictname) {
        this.ccompanydistrictname = ccompanydistrictname;
    }

    public String getChomedistrictname() {
        return chomedistrictname;
    }

    public void setChomedistrictname(String chomedistrictname) {
        this.chomedistrictname = chomedistrictname;
    }
}
