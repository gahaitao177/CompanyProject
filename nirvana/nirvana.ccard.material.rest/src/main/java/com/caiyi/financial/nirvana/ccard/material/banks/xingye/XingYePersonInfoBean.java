package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import org.apache.http.client.CookieStore;

/**
 * Created by lzj on 2016/2/14 
 */
public class XingYePersonInfoBean {
	private CookieStore cookieStore;
	/**
     * 个人基本信息类
     */
	//    private String yearMoney = "";//年费,默认为空""
    private String realname;//中文名
    private String pinyin;//中文拼音(ZHANG SAN)
    private String indentificationId;//身份证号
    private String year;//证件有效时间(止) 年
    
    private String month;//证件有效时间(止) 月
    private String day;//证件有效时间(止) 日    
    private String identificationDate;  //证件有效时间(止) 20160220
    private String tel;//手机号码(必填)
    private String e_mail;  //电子邮箱
    private String isRecommend;//是否他人推荐("1":否，"0":是,虽然很别扭，但是页面确实是这个1、0的关系)
    private String recommendNum;//推荐号,不推荐则该字段为空
    private String education; //文化程度  ["3 ","2","本科"]
    private String province;//省、直辖市code ["fp005","4","广东"]
    private String city;//地级市、区code ["320c3468c2104a2482a1745ae91555ae","2","广州"]
    private String area;//县、县级市code ["cecb66d25aff42078b0a362a7caa103e","1","从化区"]
    private String address; //街道
    private String zipcode; //邮政编码
    private String workplace; //公司名称
    private String areacode; //电话区号
    private String phonenumber;//公司的电话号码
    private String extensionnumber; // 分机号
    private String identifyCode;//手机验证码
    
    private String workprovince;//公司所在的省、直辖市code ["fp005","4","广东"]
    private String workcity;//公司所在的地级市、区code ["320c3468c2104a2482a1745ae91555ae","2","广州"]
    private String workarea;//公司所在的县、县级市code ["cecb66d25aff42078b0a362a7caa103e","1","从化区"]
    private String workaddress; //公司所在的街道
    private String workzipcode; //公司所在的邮政编码
    private String workingYears;//工作年限  ["04","3","24-36个月"]
    private String salary; //薪水 整数
    private String nature; //公司性质 ["02","1","教育、科研"]
    private String department;//部门
    private String post;//岗位  ["03","2","基层主管"]    
    private String relativename;//联系姓名 
    private String relationship;//联系人与本人的关系  ["1 ","0","配偶"]
    private String relativetel;//联系人电话
    private String sendaddress; // 发送地址  H 表示当前地址  B 单位地址
    
    private String cardId; //card id标识
    private String isRecomend="0";//是否有人推荐    0 是 1 否
    private String identificationPeriod; // 有效期 1 长期有效 0 有效期限
    private String recommendNumber;//推荐人电话
    private String havecard="1";//是否有他行卡  0 否 1 是
    private String havetime="2";//他行卡持有时间  1 1年 2 1-2年 3 两年以上
    
    public XingYePersonInfoBean(CookieStore cookie){
    	this.cookieStore=cookie;
    }
    public XingYePersonInfoBean(){
    }
    public String getIdentifyCode() {
		return identifyCode;
	}

	public void setIdentifyCode(String identifyCode) {
		this.identifyCode = identifyCode;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getIsRecomend() {
		return isRecomend;
	}

	public void setIsRecomend(String isRecomend) {
		this.isRecomend = isRecomend;
	}

	public String getIdentificationPeriod() {
		return identificationPeriod;
	}

	public void setIdentificationPeriod(String identificationPeriod) {
		this.identificationPeriod = identificationPeriod;
	}

	public String getRecommendNumber() {
		return recommendNumber;
	}

	public void setRecommendNumber(String recommendNumber) {
		this.recommendNumber = recommendNumber;
	}

	public String getHavecard() {
		return havecard;
	}

	public void setHavecard(String havecard) {
		this.havecard = havecard;
	}

	public String getHavetime() {
		return havetime;
	}

	public void setHavetime(String havetime) {
		this.havetime = havetime;
	}

	private String cardKind;//卡类型
    public CookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}    
    public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getIndentificationId() {
		return indentificationId;
	}

	public void setIndentificationId(String indentificationId) {
		this.indentificationId = indentificationId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getIdentificationDate() {
		return identificationDate;
	}

	public void setIdentificationDate(String identificationDate) {
		this.identificationDate = identificationDate;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getE_mail() {
		return e_mail;
	}

	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getExtensionnumber() {
		return extensionnumber;
	}

	public void setExtensionnumber(String extensionnumber) {
		this.extensionnumber = extensionnumber;
	}

	public String getWorkprovince() {
		return workprovince;
	}

	public void setWorkprovince(String workprovince) {
		this.workprovince = workprovince;
	}

	public String getWorkcity() {
		return workcity;
	}

	public void setWorkcity(String workcity) {
		this.workcity = workcity;
	}

	public String getWorkarea() {
		return workarea;
	}

	public void setWorkarea(String workarea) {
		this.workarea = workarea;
	}

	public String getWorkaddress() {
		return workaddress;
	}

	public void setWorkaddress(String workaddress) {
		this.workaddress = workaddress;
	}

	public String getWorkzipcode() {
		return workzipcode;
	}

	public void setWorkzipcode(String workzipcode) {
		this.workzipcode = workzipcode;
	}

	public String getWorkingYears() {
		return workingYears;
	}

	public void setWorkingYears(String workingYears) {
		this.workingYears = workingYears;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getRelativename() {
		return relativename;
	}

	public void setRelativename(String relativename) {
		this.relativename = relativename;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getRelativetel() {
		return relativetel;
	}

	public void setRelativetel(String relativetel) {
		this.relativetel = relativetel;
	}
    public String getSendaddress() {
		return sendaddress;
	}

	public void setSendaddress(String sendaddress) {
		this.sendaddress = sendaddress;
	}

	public String getCardKind() {
        return cardKind;
    }

    public void setCardKind(String cardKind) {
        this.cardKind = cardKind;
    }

    public String getVerifyCode() {
        return identifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.identifyCode = verifyCode;
    }
    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getRecommendNum() {
        return recommendNum;
    }

    public void setRecommendNum(String recommendNum) {
        this.recommendNum = recommendNum;
    }

    @Override
    public String toString() {
        return "ZhongXinBaseInfoBean{" +
                "cardKind='" + cardKind + '\'' +
//                ", yearMoney='" + yearMoney + '\'' +
                ", realname='" + realname + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", indentificationId='" + indentificationId + '\'' +
                ", identificationDate='" + identificationDate + '\'' +
                ", tel='" + tel + '\'' +
                ", e_mail='" + e_mail + '\'' +
                ", education='" + education + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", address='" + address + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", workplace='" + workplace + '\'' +
                ", areacode='" + areacode + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", extensionnumber='" + extensionnumber + '\'' +
                ", workprovince='" + workprovince + '\'' +
                ", workcity='" + workcity + '\'' +
                ", workarea='" + workarea + '\'' +
                ", workaddress='" + workaddress + '\'' +
                ", workzipcode='" + workzipcode + '\'' +
                ", salary='" + salary + '\'' +
                ", nature='" + nature + '\'' +
                ", department='" + department + '\'' +
                ", post='" + post + '\'' +
                ", relativename='" + relativename + '\'' +
                ", relationship='" + relationship + '\'' +
                ", relativetel='" + relativetel + '\'' + 
                ", workingYears='" + workingYears + '\'' +
                '}';
    }
}
