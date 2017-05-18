package com.caiyi.financial.nirvana.ccard.material.banks.pingan;

import java.io.Serializable;

/**
 * 
 * @author ljl
 * 平安办卡bean 
 * 平安申卡需要的信息
 */
public class PingAnBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//卡片信息
	private String creditcardTypeName;//信用卡类型
	private String cardLogo;// 信用卡logo标识
	private String cardOrganization;// 信用卡卡组织
	private String mainCardface;//信用卡卡面
	private String atCardUrl;//自选卡面图片url
	private String mainCardName;//卡片描述
	private String creditcardCategory;//卡片sortFlag
	private String creditcardType;//卡片typeFlag
	private String affinityCode;//卡片remark2
	private String currencyFlag;//卡片remark1
	//基本信息
	public static final String scc = "920000180";// 合作标识码
	public static final String ccp = "1a2a3a4a5a8a9a10a11a12a13";
	private String sccId;// 合作链接序列
	private String uname;// 姓名
	private String pingyin;// 姓名拼音
	private String referee;//推荐人姓名(选填)
	private String referee_phone;//推荐人手机号(选填)
	private String mobileNo;
	private String idCardNo;// 身份证号
	private String fzjg;// 发证机关
	private String cidexpirationtime;//身份证有效期
	private String begin_date;// 出生日期
	private String city;//申卡所在城市,有些城市无法申卡
	private String sex;// 性别 男:M;女:F
	// 个人信息
	private String email;//常用邮箱
	private String imarital_status;// 婚姻状态标识 1:未婚 2:已婚 9:其他
	private String ieducation;// 教育状态标识 02：博士及以上 03:硕士 04:本科 05:大专 07:高中及中专 08:初中及以下
	private String marital_status;// 婚姻状态描述
	private String education;// 教育状态描述
	private String emer_contact;// 紧急联系人
	private String emer_phone;// 紧急联系人电话
	private String guardian_relat;//
    private String iguardian_relat;//紧急联系人关系 1:配偶 2:父母 3:子女 4:亲戚 5:朋友 6:同学 7:同事 9:其他
	// 工作信息
	private String company;//单位名称
	private String department;// 任职部门
	private String position;// 现任职务
	private String workage;// 任职年限
	private String icompany_pid;//公司省会id
	private String icompany_cid;//公司市id
	private String icompany_did;//公司区id
	private String ccompany_pname;//公司省会name
	private String ccompany_cname;//公司市name
	private String ccompany_dname;//公司区name
	private String company_addr;// 工作详细地址
	private String jarea_code;// 工作区号
	private String jzip_code;// 工作邮政编码
	private String company_phone = "";// 公司电话(公司电话和家庭电话至少填一个)
	private String company_pzone = "";//公司电话区号
	private String company_pextension = "";//公司分机
	private String jsub_number;// 公司分机(选填)

	// 居住信息
	private String home_addr;
	private String ihome_pid;//住宅省id
	private String ihome_cid;//住宅市id
	private String ihome_did;//住宅区id
	private String chome_pname;//住宅省name
	private String chome_cname;//住宅市name
	private String chome_dname;//住宅区name
	private String home_statu;
	private String ihome_statu;// 房产状况 1:单位分配 2:自购无贷款 3:自购有贷款 4:租用 5:亲属住房 9:其他	
	private String month_pay;// 月还贷金额
	private String live_year;// 居住年限
	private String home_acode = "";// 住宅邮编
	private String home_phone = "";// 住宅电话
    private String home_pzone = "";//住宅电话区号
	// 辅助信息
	private String jk_addr;// 寄卡地址 0:公司 1:居住
	private String rand_code;//图片验证码 or短信验证码
	private String billingPostType = "1";
	private String autoPayOff = "1"; //默认不自动还款
    private String paymentLimitType = "2";//默认全额还款
    private String exchangeFlag = "10";//默认全额购汇
    private String bankAccount1 = "";//自扣关联帐号
    private String tradePassedFlag = "N";//N默认仅使用签名确认交易 Y使用密码确认交易
    private String logFlag = "A01";
	// 错误信息
	private String errJsMsg;
	private String errMsg;
    
	private String otpRand;//短信验证码
	
	public PingAnBean() {
		super();
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

	public String getCreditcardTypeName() {
		return creditcardTypeName;
	}

	public void setCreditcardTypeName(String creditcardTypeName) {
		this.creditcardTypeName = creditcardTypeName;
	}

	public String getCardLogo() {
		return cardLogo;
	}

	public void setCardLogo(String cardLogo) {
		this.cardLogo = cardLogo;
	}

	public String getCardOrganization() {
		return cardOrganization;
	}

	public void setCardOrganization(String cardOrganization) {
		this.cardOrganization = cardOrganization;
	}

	public String getMainCardface() {
		return mainCardface;
	}

	public void setMainCardface(String mainCardface) {
		this.mainCardface = mainCardface;
	}
    
	public String getAtCardUrl() {
		return atCardUrl;
	}

	public void setAtCardUrl(String atCardUrl) {
		this.atCardUrl = atCardUrl;
	}

	public String getMainCardName() {
		return mainCardName;
	}

	public void setMainCardName(String mainCardName) {
		this.mainCardName = mainCardName;
	}

	public String getCreditcardCategory() {
		return creditcardCategory;
	}

	public void setCreditcardCategory(String creditcardCategory) {
		this.creditcardCategory = creditcardCategory;
	}

	public String getCreditcardType() {
		return creditcardType;
	}

	public void setCreditcardType(String creditcardType) {
		this.creditcardType = creditcardType;
	}

	public String getAffinityCode() {
		return affinityCode;
	}

	public void setAffinityCode(String affinityCode) {
		this.affinityCode = affinityCode;
	}

	public String getCurrencyFlag() {
		return currencyFlag;
	}

	public void setCurrencyFlag(String currencyFlag) {
		this.currencyFlag = currencyFlag;
	}

	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	public String getReferee_phone() {
		return referee_phone;
	}

	public void setReferee_phone(String referee_phone) {
		this.referee_phone = referee_phone;
	}

	public String getRand_code() {
		return rand_code;
	}

	public void setRand_code(String rand_code) {
		this.rand_code = rand_code;
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
	
	public String getCidexpirationtime() {
		return cidexpirationtime;
	}

	public void setCidexpirationtime(String cidexpirationtime) {
		this.cidexpirationtime = cidexpirationtime;
	}

	public String getGuardian_relat() {
		return guardian_relat;
	}
	public void setGuardian_relat(String guardian_relat) {
		this.guardian_relat = guardian_relat;
	}
	
	public String getIguardian_relat() {
		return iguardian_relat;
	}

	public void setIguardian_relat(String iguardian_relat) {
		this.iguardian_relat = iguardian_relat;
	}

	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getWorkage() {
		return workage;
	}
	public void setWorkage(String workage) {
		this.workage = workage;
	}
	public String getJarea_code() {
		return jarea_code;
	}
	public void setJarea_code(String jarea_code) {
		this.jarea_code = jarea_code;
	}
	public String getJzip_code() {
		return jzip_code;
	}
	public void setJzip_code(String jzip_code) {
		this.jzip_code = jzip_code;
	}
	public String getJsub_number() {
		return jsub_number;
	}
	public void setJsub_number(String jsub_number) {
		this.jsub_number = jsub_number;
	}
	public String getMonth_pay() {
		return month_pay;
	}
	public void setMonth_pay(String month_pay) {
		this.month_pay = month_pay;
	}	
	public String getSccId() {
		return sccId;
	}
	public void setSccId(String sccId) {
		this.sccId = sccId;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPingyin() {
		return pingyin;
	}
	public void setPingyin(String pingyin) {
		this.pingyin = pingyin;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}
	public String getFzjg() {
		return fzjg;
	}
	public void setFzjg(String fzjg) {
		this.fzjg = fzjg;
	}
	public String getBegin_date() {
		return begin_date;
	}
	public void setBegin_date(String begin_date) {
		this.begin_date = begin_date;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImarital_status() {
		return imarital_status;
	}
	public void setImarital_status(String imarital_status) {
		this.imarital_status = imarital_status;
	}
	public String getIeducation() {
		return ieducation;
	}
	public void setIeducation(String ieducation) {
		this.ieducation = ieducation;
	}
	public String getMarital_status() {
		return marital_status;
	}
	public void setMarital_status(String marital_status) {
		this.marital_status = marital_status;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getEmer_contact() {
		return emer_contact;
	}
	public void setEmer_contact(String emer_contact) {
		this.emer_contact = emer_contact;
	}
	public String getEmer_phone() {
		return emer_phone;
	}
	public void setEmer_phone(String emer_phone) {
		this.emer_phone = emer_phone;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCompany_addr() {
		return company_addr;
	}
	public void setCompany_addr(String company_addr) {
		this.company_addr = company_addr;
	}
	
	public String getCompany_phone() {
		return company_phone;
	}

	public void setCompany_phone(String company_phone) {
		this.company_phone = company_phone;
	}

	public String getCompany_pzone() {
		return company_pzone;
	}

	public void setCompany_pzone(String company_pzone) {
		this.company_pzone = company_pzone;
	}

	public String getCompany_pextension() {
		return company_pextension;
	}

	public void setCompany_pextension(String company_pextension) {
		this.company_pextension = company_pextension;
	}

	public String getHome_pzone() {
		return home_pzone;
	}

	public void setHome_pzone(String home_pzone) {
		this.home_pzone = home_pzone;
	}

	public String getHome_addr() {
		return home_addr;
	}
	public void setHome_addr(String home_addr) {
		this.home_addr = home_addr;
	}
	
	public String getHome_statu() {
		return home_statu;
	}

	public void setHome_statu(String home_statu) {
		this.home_statu = home_statu;
	}

	public String getIhome_statu() {
		return ihome_statu;
	}

	public void setIhome_statu(String ihome_statu) {
		this.ihome_statu = ihome_statu;
	}

	public String getLive_year() {
		return live_year;
	}
	public void setLive_year(String live_year) {
		this.live_year = live_year;
	}
	public String getHome_acode() {
		return home_acode;
	}
	public void setHome_acode(String home_acode) {
		this.home_acode = home_acode;
	}
	public String getHome_phone() {
		return home_phone;
	}
	public void setHome_phone(String home_phone) {
		this.home_phone = home_phone;
	}
	public String getJk_addr() {
		return jk_addr;
	}
	public void setJk_addr(String jk_addr) {
		this.jk_addr = jk_addr;
	}
	
	public String getBillingPostType() {
		return billingPostType;
	}

	public String getAutoPayOff() {
		return autoPayOff;
	}

	public String getPaymentLimitType() {
		return paymentLimitType;
	}

	public String getExchangeFlag() {
		return exchangeFlag;
	}

	public String getBankAccount1() {
		return bankAccount1;
	}

	public String getTradePassedFlag() {
		return tradePassedFlag;
	}

	public String getLogFlag() {
		return logFlag;
	}

	public String getErrJsMsg() {
		return errJsMsg;
	}
	public void setErrJsMsg(String errJsMsg) {
		this.errJsMsg = errJsMsg;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
    	
	public String getOtpRand() {
		return otpRand;
	}

	public void setOtpRand(String otpRand) {
		this.otpRand = otpRand;
	}

	@Override
	public String toString() {
		return "PingAnBean [creditcardTypeName=" + creditcardTypeName
				+ ", cardLogo=" + cardLogo + ", cardOrganization="
				+ cardOrganization + ", mainCardface=" + mainCardface
				+ ", atCardUrl=" + atCardUrl + ", mainCardName=" + mainCardName
				+ ", creditcardCategory=" + creditcardCategory
				+ ", creditcardType=" + creditcardType + ", affinityCode="
				+ affinityCode + ", currencyFlag=" + currencyFlag + ", sccId="
				+ sccId + ", uname=" + uname + ", pingyin=" + pingyin
				+ ", referee=" + referee + ", referee_phone=" + referee_phone
				+ ", mobileNo=" + mobileNo + ", idCardNo=" + idCardNo
				+ ", fzjg=" + fzjg + ", cidexpirationtime=" + cidexpirationtime
				+ ", begin_date=" + begin_date + ", city=" + city + ", sex="
				+ sex + ", email=" + email + ", imarital_status="
				+ imarital_status + ", ieducation=" + ieducation
				+ ", marital_status=" + marital_status + ", education="
				+ education + ", emer_contact=" + emer_contact
				+ ", emer_phone=" + emer_phone + ", guardian_relat="
				+ guardian_relat + ", iguardian_relat=" + iguardian_relat
				+ ", company=" + company + ", department=" + department
				+ ", position=" + position + ", workage=" + workage
				+ ", icompany_pid=" + icompany_pid + ", icompany_cid="
				+ icompany_cid + ", icompany_did=" + icompany_did
				+ ", ccompany_pname=" + ccompany_pname + ", ccompany_cname="
				+ ccompany_cname + ", ccompany_dname=" + ccompany_dname
				+ ", company_addr=" + company_addr + ", jarea_code="
				+ jarea_code + ", jzip_code=" + jzip_code + ", company_phone="
				+ company_phone + ", company_pzone=" + company_pzone
				+ ", company_pextension=" + company_pextension
				+ ", jsub_number=" + jsub_number + ", home_addr=" + home_addr
				+ ", ihome_pid=" + ihome_pid + ", ihome_cid=" + ihome_cid
				+ ", ihome_did=" + ihome_did + ", chome_pname=" + chome_pname
				+ ", chome_cname=" + chome_cname + ", chome_dname="
				+ chome_dname + ", home_statu=" + home_statu + ", ihome_statu="
				+ ihome_statu + ", month_pay=" + month_pay + ", live_year="
				+ live_year + ", home_acode=" + home_acode + ", home_phone="
				+ home_phone + ", home_pzone=" + home_pzone + ", jk_addr="
				+ jk_addr + ", rand_code=" + rand_code + ", billingPostType="
				+ billingPostType + ", autoPayOff=" + autoPayOff
				+ ", paymentLimitType=" + paymentLimitType + ", exchangeFlag="
				+ exchangeFlag + ", bankAccount1=" + bankAccount1
				+ ", tradePassedFlag=" + tradePassedFlag + ", logFlag="
				+ logFlag +", otpRand="+otpRand+ "]";
	}
    
}
