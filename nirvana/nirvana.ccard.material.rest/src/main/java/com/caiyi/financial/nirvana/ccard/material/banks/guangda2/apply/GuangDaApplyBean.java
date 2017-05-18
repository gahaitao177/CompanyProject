package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.apply;

import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums.*;
import org.apache.http.client.CookieStore;

import java.io.Serializable;

/**
 * Created by wsl on 2016/3/2.
 */
public class GuangDaApplyBean implements Serializable {
    private CookieStore cookieStore;

    /**
     * 个人信息
     */
    private String name;//姓名
    private String namepy;//英文名/拼音  大写，姓和名空格隔开
    private String id_no;//身份证号
    private String id_type = "A";
    private String merchantmsg=""; //空值，好像无用
    private String mobilephone;//手机号码
    //短信每日限制5次
    private String mobileHidden;// 发送手机号后设置为手机号，隐藏手机号
    private String verify_code;//图片验证码  4位数字
    private String dynPasswd;//动态密码

    private String passwdHidden="";// 空值，好像无用

    //推荐人相关
    private String recomment = "0";//是否他人推荐 1：是  0：否
    private String recomtel = "";//推荐人电话
    private String c2c_act_id = "";//ready方法中设置为""，不知道哪有赋值
    private String c2c_recom_flag = "";//ready方法中设置为""，不知道哪有赋值
    /**
     * 工作信息
     */

    private String marriage;//婚姻状况  1：未婚；2：已婚；3：其他
    private String education;//教育程度 5：硕士 6：博士及以上 4：本科 3：大专 2：中专 1：高中及以下 7：其他。 默认4
    private String housetype;//住房性质 1：商品房有按揭 2：商品房无按揭 3：已购公房 4：租用/月租房 5：单位集体宿舍 6：亲属/父母家 7：其他
    private String email;//电子邮箱
    private String comname;//单位名称
    private String cpy_kind;//单位性质 1：机关事业单位 2：三资 3：国有 4：股份制 5：私营 6：个体/自由职业者 7：其他 8：储蓄理财
    private String cpy_vocation;//单位所属行业 1：政府部门 2：科教文卫 3：邮电通讯 4：部队 5：IT/网络/计算机 6：商业/贸易 7：银行
    // 8：证券/投资/保险 9：制造业 10：农林畜牧 11：广告 12：旅游/餐饮/娱乐 13：交通运输 14：会计/律师 15：房地产/建筑/装饰 16：其他

    private String vocation_remark = "";//职业说明  行业为其他，需要职业说明
    private String duty;//职务 1 ：单位公司主管/局级以上；2 ：部门主管/处级；3 ：科室经理；4 ：员工；5 ：其他；6 ：法人代表；7 ：股东/合伙人；
    private String income;//年收入（万元）

    /**
     * 其他信息
     */
    private String comprovince;//单位地址 省
    private String comcityname;//市
    private String comareaname;//区
    private String comaddrHD = "";//空值，好像无用
    private String comaddr;//单位详细地址

    private String houseprovince;//家庭地址 省
    private String housecityname;//市
    private String houseareaname = "999";//区
    private String houseaddr = "";//家庭详细地址

    private String comphoneqh;//单位电话区号
    private String comphonetel;//单位电话
    private String familyname;//直亲姓名
    private String relation;//直亲关系1：父母；2：配偶；3：子女；4：其他；
    private String familymobile;//直亲手机
    private String idnovaliddate = "";//身份证有效期至 （非必填） 格式 yyyyMMdd 例 20160114

    private String czprovince;//面签城市 省
    private String czcityname;//市

    private String zhimaName = "1";

    private String zmvalue = "1";


    /**
     * 一堆隐藏信息
     */

    private String postaddrtype = "2";// 默认为2
    private String comzip = "";//工作区名称  comareaname.split("\\|")[2];
    private String homezip = "";//家庭区名称  houseareaname.split("\\|")[2];

    private String birth;// 生日
    private String sex;//F：女 M：男
    private String pro_code = "FHTG060000SJ03SHYC";// 渠道code FHTG060000SJ01SHYC 惠刷卡渠道

    private String comtel;//单位电话 comphonetel+"-"+comphoneqh


    private String cookie_id = "";//提交表单前赋值为""，没懂意思

    private String branch_code;//工作城市.split("\\|")[2]

    private String cxtype = "";// 一个空值，好像无用


    private String linkfrom;//
    private String termtype;//
    private String applyFlag;//
    private String diyFlag;//
    private String orderno;//
    private String req_card_id;// 信用卡id 22
    private String card_name;// 信用卡名称  例：“福”信用卡
    private String area_adress;// 从哪个网站跳转过来的
    private String kd_flag;//
    private String kd_address;//
    private String card_logo;//
    private String company_logo;//


    public String getZhimaName() {
        return zhimaName;
    }

    public void setZhimaName(String zhimaName) {
        this.zhimaName = zhimaName;
    }

    public String getZmvalue() {
        return zmvalue;
    }

    public void setZmvalue(String zmvalue) {
        this.zmvalue = zmvalue;
    }

    public GuangDaApplyBean() {
    }

    public GuangDaApplyBean(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }


    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamepy() {
        return namepy;
    }

    public void setNamepy(String namepy) {
        this.namepy = namepy;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public String getMerchantmsg() {
        return merchantmsg;
    }

    public void setMerchantmsg(String merchantmsg) {
        this.merchantmsg = merchantmsg;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getMobileHidden() {
        return mobileHidden;
    }

    public void setMobileHidden(String mobileHidden) {
        this.mobileHidden = mobileHidden;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public String getDynPasswd() {
        return dynPasswd;
    }

    public void setDynPasswd(String dynPasswd) {
        this.dynPasswd = dynPasswd;
    }

    public String getPasswdHidden() {
        return passwdHidden;
    }

    public void setPasswdHidden(String passwdHidden) {
        this.passwdHidden = passwdHidden;
    }

    public String getRecomment() {
        return recomment;
    }

    public void setRecomment(String recomment) {
        this.recomment = recomment;
    }

    public String getRecomtel() {
        return recomtel;
    }

    public void setRecomtel(String recomtel) {
        this.recomtel = recomtel;
    }

    public String getC2c_act_id() {
        return c2c_act_id;
    }

    public void setC2c_act_id(String c2c_act_id) {
        this.c2c_act_id = c2c_act_id;
    }

    public String getC2c_recom_flag() {
        return c2c_recom_flag;
    }

    public void setC2c_recom_flag(String c2c_recom_flag) {
        this.c2c_recom_flag = c2c_recom_flag;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getHousetype() {
        return housetype;
    }

    public void setHousetype(String housetype) {
        this.housetype = housetype;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComname() {
        return comname;
    }

    public void setComname(String comname) {
        this.comname = comname;
    }

    public String getCpy_kind() {
        return cpy_kind;
    }

    public void setCpy_kind(String cpy_kind) {
        this.cpy_kind = cpy_kind;
    }

    public String getCpy_vocation() {
        return cpy_vocation;
    }

    public void setCpy_vocation(String cpy_vocation) {
        this.cpy_vocation = cpy_vocation;
    }

    public String getVocation_remark() {
        return vocation_remark;
    }

    public void setVocation_remark(String vocation_remark) {
        this.vocation_remark = vocation_remark;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getComprovince() {
        return comprovince;
    }

    public void setComprovince(String comprovince) {
        this.comprovince = comprovince;
    }

    public String getComcityname() {
        return comcityname;
    }

    public void setComcityname(String comcityname) {
        this.comcityname = comcityname;
    }

    public String getComareaname() {
        return comareaname;
    }

    public void setComareaname(String comareaname) {
        this.comareaname = comareaname;
    }

    public String getComaddrHD() {
        return comaddrHD;
    }

    public void setComaddrHD(String comaddrHD) {
        this.comaddrHD = comaddrHD;
    }

    public String getComaddr() {
        return comaddr;
    }

    public void setComaddr(String comaddr) {
        this.comaddr = comaddr;
    }

    public String getHouseprovince() {
        return houseprovince;
    }

    public void setHouseprovince(String houseprovince) {
        this.houseprovince = houseprovince;
    }

    public String getHousecityname() {
        return housecityname;
    }

    public void setHousecityname(String housecityname) {
        this.housecityname = housecityname;
    }

    public String getHouseareaname() {
        return houseareaname;
    }

    public void setHouseareaname(String houseareaname) {
        this.houseareaname = houseareaname;
    }

    public String getHouseaddr() {
        return houseaddr;
    }

    public void setHouseaddr(String houseaddr) {
        this.houseaddr = houseaddr;
    }

    public String getComphonetel() {
        return comphonetel;
    }

    public void setComphonetel(String comphonetel) {
        this.comphonetel = comphonetel;
    }

    public String getComphoneqh() {
        return comphoneqh;
    }

    public void setComphoneqh(String comphoneqh) {
        this.comphoneqh = comphoneqh;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getFamilymobile() {
        return familymobile;
    }

    public void setFamilymobile(String familymobile) {
        this.familymobile = familymobile;
    }

    public String getIdnovaliddate() {
        return idnovaliddate;
    }

    public void setIdnovaliddate(String idnovaliddate) {
        this.idnovaliddate = idnovaliddate;
    }

    public String getCzprovince() {
        return czprovince;
    }

    public void setCzprovince(String czprovince) {
        this.czprovince = czprovince;
    }

    public String getCzcityname() {
        return czcityname;
    }

    public void setCzcityname(String czcityname) {
        this.czcityname = czcityname;
    }

    public String getPostaddrtype() {
        return postaddrtype;
    }

    public void setPostaddrtype(String postaddrtype) {
        this.postaddrtype = postaddrtype;
    }

    public String getComzip() {
        return comzip;
    }

    public void setComzip(String comzip) {
        this.comzip = comzip;
    }

    public String getHomezip() {
        return homezip;
    }

    public void setHomezip(String homezip) {
        this.homezip = homezip;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPro_code() {
        return pro_code;
    }

    public void setPro_code(String pro_code) {
        this.pro_code = pro_code;
    }

    public String getComtel() {
        return comtel;
    }

    public void setComtel(String comtel) {
        this.comtel = comtel;
    }

    public String getCookie_id() {
        return cookie_id;
    }

    public void setCookie_id(String cookie_id) {
        this.cookie_id = cookie_id;
    }

    public String getBranch_code() {
        return branch_code;
    }

    public void setBranch_code(String branch_code) {
        this.branch_code = branch_code;
    }

    public String getCxtype() {
        return cxtype;
    }

    public void setCxtype(String cxtype) {
        this.cxtype = cxtype;
    }

    public String getLinkfrom() {
        return linkfrom;
    }

    public void setLinkfrom(String linkfrom) {
        this.linkfrom = linkfrom;
    }

    public String getTermtype() {
        return termtype;
    }

    public void setTermtype(String termtype) {
        this.termtype = termtype;
    }

    public String getApplyFlag() {
        return applyFlag;
    }

    public void setApplyFlag(String applyFlag) {
        this.applyFlag = applyFlag;
    }

    public String getDiyFlag() {
        return diyFlag;
    }

    public void setDiyFlag(String diyFlag) {
        this.diyFlag = diyFlag;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getReq_card_id() {
        return req_card_id;
    }

    public void setReq_card_id(String req_card_id) {
        this.req_card_id = req_card_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getArea_adress() {
        return area_adress;
    }

    public void setArea_adress(String area_adress) {
        this.area_adress = area_adress;
    }

    public String getKd_flag() {
        return kd_flag;
    }

    public void setKd_flag(String kd_flag) {
        this.kd_flag = kd_flag;
    }

    public String getKd_address() {
        return kd_address;
    }

    public void setKd_address(String kd_address) {
        this.kd_address = kd_address;
    }

    public String getCard_logo() {
        return card_logo;
    }

    public void setCard_logo(String card_logo) {
        this.card_logo = card_logo;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    @Override
    public String toString() {
        return "GuangDaBean" +
                "\n 姓名 name='" + name + '\'' +
                "\n 拼音 namepy='" + namepy + '\'' +
                "\n 证件号 id_no='" + id_no + '\'' +
                "\n 证件类型 id_type='" + id_type + '\'' +
                "\n merchantmsg='" + merchantmsg + '\'' +
                "\n 手机号 mobilephone='" + mobilephone + '\'' +
                "\n 手机号隐藏 mobileHidden='" + mobileHidden + '\'' +
                "\n 图片验证码 verify_code='" + verify_code + '\'' +
                "\n 手机验证码 dynPasswd='" + dynPasswd + '\'' +
                "\n passwdHidden='" + passwdHidden + '\'' +
                "\n 是否有推荐人 recomment='" + recomment + '\'' +
                "\n 推荐人号码 recomtel='" + recomtel + '\'' +
                "\n c2c_act_id='" + c2c_act_id + '\'' +
                "\n c2c_recom_flag='" + c2c_recom_flag + '\'' +
                "\n 婚姻状况 marriage='" + marriage + '\'' + "   " + MaritalStatus.getByGuangDaKey(marriage) +
                "\n 教育程度 education='" + education + '\'' + "   " + Degree.getByGuangDaKey(education) +
                "\n 住宅情况 housetype='" + housetype + '\'' + "   " + ResidenceStatus.getByGuangDaKey(housetype) +
                "\n 邮箱 email='" + email + '\'' +
                "\n 单位名称 comname='" + comname + '\'' +
                "\n 单位性质 cpy_kind='" + cpy_kind + '\'' + "   " + NatureOfUnit.getByGuangDaKey(cpy_kind) +
                "\n 单位所属行业 cpy_vocation='" + cpy_vocation + '\'' + "   " + NatureOfBusiness.getByGuangDaKey(cpy_vocation) +
                "\n 职业说明 vocation_remark='" + vocation_remark + '\'' +
                "\n 职务 duty='" + duty + '\'' + "   " + Post.getByGuangDaKey(duty) +
                "\n 年收入 income='" + income + '\'' +
                "\n 工作省 comprovince='" + comprovince + '\'' +
                "\n 市 omcityname='" + comcityname + '\'' +
                "\n 区 comareaname='" + comareaname + '\'' +
                "\n comaddrHD='" + comaddrHD + '\'' +
                "\n 工作详细地址 comaddr='" + comaddr + '\'' +
                "\n 住宅省 houseprovince='" + houseprovince + '\'' +
                "\n 市 housecityname='" + housecityname + '\'' +
                "\n 区 houseareaname='" + houseareaname + '\'' +
                "\n 详细 houseaddr='" + houseaddr + '\'' +
                "\n 公司电话区号 comphoneqh='" + comphoneqh + '\'' +
                "\n 公司电话号 comphonetel='" + comphonetel + '\'' +
                "\n 直亲姓名 familyname='" + familyname + '\'' +
                "\n 直亲关系 relation='" + relation + '\'' + "   " + FamilyTies.getByGuangDaKey(relation) +
                "\n 联系电话 familymobile='" + familymobile + '\'' +
                "\n 身份证有效期至 idnovaliddate='" + idnovaliddate + '\'' +
                "\n czprovince='" + czprovince + '\'' +
                "\n czcityname='" + czcityname + '\'' +
                "\n postaddrtype='" + postaddrtype + '\'' +
                "\n comzip='" + comzip + '\'' +
                "\n homezip='" + homezip + '\'' +
                "\n birth='" + birth + '\'' +
                "\n sex='" + sex + '\'' +
                "\n pro_code='" + pro_code + '\'' +
                "\n comtel='" + comtel + '\'' +
                "\n cookie_id='" + cookie_id + '\'' +
                "\n branch_code='" + branch_code + '\'' +
                "\n cxtype='" + cxtype + '\'' +
                "\n linkfrom='" + linkfrom + '\'' +
                "\n termtype='" + termtype + '\'' +
                "\n applyFlag='" + applyFlag + '\'' +
                "\n diyFlag='" + diyFlag + '\'' +
                "\n orderno='" + orderno + '\'' +
                "\n 卡号 req_card_id='" + req_card_id + '\'' +
                "\n card_name='" + card_name + '\'' +
                "\n area_adress='" + area_adress + '\'' +
                "\n kd_flag='" + kd_flag + '\'' +
                "\n kd_address='" + kd_address + '\'' +
                "\n card_logo='" + card_logo + '\'' +
                "\n company_logo='" + company_logo + '\'';
    }
}
