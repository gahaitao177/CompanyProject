package com.caiyi.financial.nirvana.ccard.material.banks.minsheng;

import org.apache.http.client.CookieStore;

public class MingshengBean {
	
	private CookieStore myCookies = null;// 这次通话 所需要维持的Cookies
	
	private String verifycode; // 验证码   身份证号码验证时的图片验证码
	private String cardType; // 卡类型
	private String Identitycode; // 身份证号码 
	
	/***获取礼物需要提交的数据**/	

	private String cardColor;  // 卡颜色 卡样式 sysId
	private String giftId; // 礼物Id
	private String hidType; //不清楚 主要从HTML文件中获取
	
	
	
	private String riCname;//	邓宏
	private String riPinYInName1;//	DENG
	private String	riPinYInName2;//	HONG
	private String	province;//	15
	private String	provinceName;//	江西省
	private String	idnumCode1;//	131
	private String	idnumName;//	赣州市
	private String	idnumCode;//	4293
	private String	subIdnumName;//	宁都县

	
	private String idnumStartdate;//	
	private String	date_now;//	2015-09-25
	private String	idnumEnddate;//	
	private String cycaddtype;//	H
	private String	cycProvinceCode1;//	1
	private String	cycProvince1;//	河北省
	private String	cycCityCode1;//	1
	private String	cycCity1;//	石家庄市
	private String	cycCountyCode1;//	3
	private String cycCounty1;//	新华区

	private String	cycProvinceCode2;//	8
	private String cycProvince2;//	辽宁省
	private String	cycCityCode2;//	41
	private String	cycCity2;//	鞍山市
	private String	cycCountyCode2;//	2232
	private String	cycCounty2;//	海城市
	
	private String	cycDetail1;//	住宅详细地址1
	private String	post1;//	050000
	private String	cycProvinceCode	;//4
	private String	cycProvince;//	山东省
	private String	cycCityCode	;//14
	private String	cycCity	;//东营市
	private String	cycCountyCode;//	124
	private String	cycCounty	;//垦利县
	private String	DdlIDNumber2$districtCode;//	
		
	
	private String	cycDetail;//	单位详细地址
	private String	post;//	257500
	private String	hidyj;//	1
	private String	HidCardID;//	27
	private String	HidID;//	6
	private String	HidAT;//	
	private String	HidName1;//	邓
	private String	HidName2;//	宏
	private String	HidTitle;//	个人资料
	private String	HidBackUrl;//	/wsonline/login/success.jhtml
	private String	HidBackFlag	;//1
	private String	hidCookieId	;//

	private String compName	;//上海彩亿信息技术有限公司
	private String companyPhoneAare;//	021
	private String	companyPhone;//	4006739188
	private String	companyPhoneExt	;//
	private String	compPhone;//
	private String	workDate	;//2
	private String	MARST;//	S  婚姻状态 "M">已婚 "S">未婚 "O">其他
	

//	<option value="6">博士 </option>
//	<option value="5">硕士 </option>
//	<option value="4">本科 </option>
//	<option value="3">大专 </option>
//	<option value="2">高中/中专 </option>
//	<option value="1">初中及以下 </option>
	private String	education	;//
	private String	mobilephone	;//15821129261
	private String	mcode	;//727505
	private String	email	;//554010940@qq.com
	private String	cycQQ;//	554010940
	private String	homezonephone;//	
	private String	homephone;//
	private String	HidMcode;//	电话验证码

	private String	HidCityArea	;//010,020,021,022,023,024,025,027,028,029,0310,0311,0312,0313,0314,0315,0316,0317,0318,0319,0335,0350,0351,0352,0353,0349,0354,0355,0356,0357,0358,0359,0370,0371,0372,0373,0374,0375,0376,0377,0378,0379,0391,0392,0393,0394,0395,0396,0398,0410,0411,0412,0413,0414,0415,0416,0417,0418,0419,0421,0427,0429,0431,0432,0433,0434,0435,0436,0437,0438,0439,0440,0451,0452,0453,0454,0455,0456,0457,0458,0459,0464,0467,0468,0469,0470,0471,0472,0473,0474,0475,0476,0477,0478,0483,0479,0482,0510,0511,0512,0513,0514,0515,0516,0517,0518,0519,0523,0527,0530,0531,0532,0533,0632,0543,0635,0633,0634,0534,0535,0536,0537,0538,0539,0546,0631,0550,0551,0552,0553,0554,0555,0556,0559,0557,0558,0566,0561,0562,0563,0564,0565,0570,0571,0572,0573,0574,0575,0576,0577,0578,0579,0580,0591,0592,0593,0594,0595,0596,0597,0598,0599,0660,0662,0663,0668,0750,0751,0752,0753,0754,0755,0756,0757,0758,0759,0760,0762,0763,0766,0768,0769,0710,0711,0712,0713,0714,0715,0716,0717,0718,0719,0722,0724,0728,0730,0731,0732,0733,0734,0735,0736,0737,0738,0739,0743,0744,0745,0746,0770,0771,0772,0773,0774,0775,0776,0777,0778,0779,0790,0791,0792,0793,0794,0795,0796,0797,0798,0799,0701,0812,0813,0816,0817,0818,0825,0826,0827,0830,0831,0832,0833,0834,0835,0836,0837,0838,0839,0851,0852,0853,0854,0855,0856,0857,0858,0859,0870,0691,0692,0871,0872,0873,0874,0875,0876,0877,0878,0879,0883,0886,0887,0888,0891,0892,0893,0894,0895,0896,0897,0898,0899,0890,0911,0912,0913,0914,0915,0916,0917,0919,0930,0931,0932,0933,0934,0935,0936,0937,0938,0941,0943,0951,0952,0953,0954,0955,0970,0971,0972,0973,0974,0975,0976,0977,0991,0990,0995,0902,0994,0909,0996,0997,0998,0903,0999,0993,0906,0901
	
	
	/*****联系人*******/
	private String familymemName1;//	赵杰
	private String familymemMobile1;//	15092385024
	private String	familymemShip1;//	4
	private String	familymemShip1Name;//	其他
	private String	LCustName;//	周伟新
	private String	LCustPhone;//	13824489127
	private String	LCustShip;//	3
	private String	lCustShipName;//	同事
	/****确认提交****/
	
	private String	cycProvince1Code;//		2
	private String	cycCity1Code;//		2
	private String	cycCounty1Code;//		2900
	private String	DdlIDNumber3$districtCode;//		1000
	private String  HidCookie;//
	private String	DeliveryAddress;//		B
	
	
	private String	userName;//	李波
	private String  openId;//
	private String	recommendId	;//
	private String	CompanyPhone;//	4006739188
	private String	AppName;//	邓宏
	private String	HidJuniorMessType;//

	private String  HidActiveUrl;//
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getRecommendId() {
		return recommendId;
	}
	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}
	public String getCycProvince1Code() {
		return cycProvince1Code;
	}
	public void setCycProvince1Code(String cycProvince1Code) {
		this.cycProvince1Code = cycProvince1Code;
	}
	public String getCycCity1Code() {
		return cycCity1Code;
	}
	public void setCycCity1Code(String cycCity1Code) {
		this.cycCity1Code = cycCity1Code;
	}
	public String getCycCounty1Code() {
		return cycCounty1Code;
	}
	public void setCycCounty1Code(String cycCounty1Code) {
		this.cycCounty1Code = cycCounty1Code;
	}
	public String getDdlIDNumber3$districtCode() {
		return DdlIDNumber3$districtCode;
	}
	public void setDdlIDNumber3$districtCode(String ddlIDNumber3$districtCode) {
		DdlIDNumber3$districtCode = ddlIDNumber3$districtCode;
	}
	public String getHidCookie() {
		return HidCookie;
	}
	public void setHidCookie(String hidCookie) {
		HidCookie = hidCookie;
	}
	public String getDeliveryAddress() {
		return DeliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		DeliveryAddress = deliveryAddress;
	}
	

	public String getFamilymemName1() {
		return familymemName1;
	}
	public void setFamilymemName1(String familymemName1) {
		this.familymemName1 = familymemName1;
	}
	public String getFamilymemMobile1() {
		return familymemMobile1;
	}
	public void setFamilymemMobile1(String familymemMobile1) {
		this.familymemMobile1 = familymemMobile1;
	}
	public String getFamilymemShip1() {
		return familymemShip1;
	}
	public void setFamilymemShip1(String familymemShip1) {
		this.familymemShip1 = familymemShip1;
	}
	public String getFamilymemShip1Name() {
		return familymemShip1Name;
	}
	public void setFamilymemShip1Name(String familymemShip1Name) {
		this.familymemShip1Name = familymemShip1Name;
	}
	public String getLCustName() {
		return LCustName;
	}
	public void setLCustName(String lCustName) {
		LCustName = lCustName;
	}
	public String getLCustPhone() {
		return LCustPhone;
	}
	public void setLCustPhone(String lCustPhone) {
		LCustPhone = lCustPhone;
	}
	public String getLCustShip() {
		return LCustShip;
	}
	public void setLCustShip(String lCustShip) {
		LCustShip = lCustShip;
	}
	public String getlCustShipName() {
		return lCustShipName;
	}
	public void setlCustShipName(String lCustShipName) {
		this.lCustShipName = lCustShipName;
	}

	public String getAppName() {
		return AppName;
	}
	public void setAppName(String appName) {
		AppName = appName;
	}
	public String getHidJuniorMessType() {
		return HidJuniorMessType;
	}
	public void setHidJuniorMessType(String hidJuniorMessType) {
		HidJuniorMessType = hidJuniorMessType;
	}
	public String getHidActiveUrl() {
		return HidActiveUrl;
	}
	public void setHidActiveUrl(String hidActiveUrl) {
		HidActiveUrl = hidActiveUrl;
	}


	
	
	public String getCompName() {
		return compName;
	}
	public void setCompName(String compName) {
		this.compName = compName;
	}
	public String getCompanyPhoneAare() {
		return companyPhoneAare;
	}
	public void setCompanyPhoneAare(String companyPhoneAare) {
		this.companyPhoneAare = companyPhoneAare;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public String getCompanyPhoneExt() {
		return companyPhoneExt;
	}
	public void setCompanyPhoneExt(String companyPhoneExt) {
		this.companyPhoneExt = companyPhoneExt;
	}
	public String getCompPhone() {
		return compPhone;
	}
	public void setCompPhone(String compPhone) {
		this.compPhone = compPhone;
	}
	public String getWorkDate() {
		return workDate;
	}
	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}
	public String getMARST() {
		return MARST;
	}
	public void setMARST(String mARST) {
		MARST = mARST;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	

	public String getRiCname() {
		return riCname;
	}
	public void setRiCname(String riCname) {
		this.riCname = riCname;
	}
	public String getMobilephone() {
		return mobilephone;
	}
	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	public String getMcode() {
		return mcode;
	}
	public void setMcode(String mcode) {
		this.mcode = mcode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCycQQ() {
		return cycQQ;
	}
	public void setCycQQ(String cycQQ) {
		this.cycQQ = cycQQ;
	}
	public String getHomezonephone() {
		return homezonephone;
	}
	public void setHomezonephone(String homezonephone) {
		this.homezonephone = homezonephone;
	}
	public String getHomephone() {
		return homephone;
	}
	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}
	public String getHidMcode() {
		return HidMcode;
	}
	public void setHidMcode(String hidMcode) {
		HidMcode = hidMcode;
	}
	public String getHidCityArea() {
		return HidCityArea;
	}
	public void setHidCityArea(String hidCityArea) {
		HidCityArea = hidCityArea;
	}
	public String getRiPinYInName1() {
		return riPinYInName1;
	}
	public void setRiPinYInName1(String riPinYInName1) {
		this.riPinYInName1 = riPinYInName1;
	}
	public String getRiPinYInName2() {
		return riPinYInName2;
	}
	public void setRiPinYInName2(String riPinYInName2) {
		this.riPinYInName2 = riPinYInName2;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getIdnumCode1() {
		return idnumCode1;
	}
	public void setIdnumCode1(String idnumCode1) {
		this.idnumCode1 = idnumCode1;
	}
	public String getIdnumName() {
		return idnumName;
	}
	public void setIdnumName(String idnumName) {
		this.idnumName = idnumName;
	}
	public String getIdnumCode() {
		return idnumCode;
	}
	public void setIdnumCode(String idnumCode) {
		this.idnumCode = idnumCode;
	}
	public String getSubIdnumName() {
		return subIdnumName;
	}
	public void setSubIdnumName(String subIdnumName) {
		this.subIdnumName = subIdnumName;
	}
	public String getIdnumStartdate() {
		return idnumStartdate;
	}
	public void setIdnumStartdate(String idnumStartdate) {
		this.idnumStartdate = idnumStartdate;
	}
	public String getDate_now() {
		return date_now;
	}
	public void setDate_now(String date_now) {
		this.date_now = date_now;
	}
	public String getIdnumEnddate() {
		return idnumEnddate;
	}
	public void setIdnumEnddate(String idnumEnddate) {
		this.idnumEnddate = idnumEnddate;
	}
	public String getCycaddtype() {
		return cycaddtype;
	}
	public void setCycaddtype(String cycaddtype) {
		this.cycaddtype = cycaddtype;
	}
	public String getCycProvinceCode1() {
		return cycProvinceCode1;
	}
	public void setCycProvinceCode1(String cycProvinceCode1) {
		this.cycProvinceCode1 = cycProvinceCode1;
	}
	public String getCycProvince1() {
		return cycProvince1;
	}
	public void setCycProvince1(String cycProvince1) {
		this.cycProvince1 = cycProvince1;
	}
	public String getCycCityCode1() {
		return cycCityCode1;
	}
	public void setCycCityCode1(String cycCityCode1) {
		this.cycCityCode1 = cycCityCode1;
	}
	public String getCycCity1() {
		return cycCity1;
	}
	public void setCycCity1(String cycCity1) {
		this.cycCity1 = cycCity1;
	}
	public String getCycCountyCode1() {
		return cycCountyCode1;
	}
	public void setCycCountyCode1(String cycCountyCode1) {
		this.cycCountyCode1 = cycCountyCode1;
	}
	public String getCycCounty1() {
		return cycCounty1;
	}
	public void setCycCounty1(String cycCounty1) {
		this.cycCounty1 = cycCounty1;
	}
	public String getCycProvinceCode2() {
		return cycProvinceCode2;
	}
	public void setCycProvinceCode2(String cycProvinceCode2) {
		this.cycProvinceCode2 = cycProvinceCode2;
	}
	public String getCycProvince2() {
		return cycProvince2;
	}
	public void setCycProvince2(String cycProvince2) {
		this.cycProvince2 = cycProvince2;
	}
	public String getCycCityCode2() {
		return cycCityCode2;
	}
	public void setCycCityCode2(String cycCityCode2) {
		this.cycCityCode2 = cycCityCode2;
	}
	public String getCycCity2() {
		return cycCity2;
	}
	public void setCycCity2(String cycCity2) {
		this.cycCity2 = cycCity2;
	}
	public String getCycCountyCode2() {
		return cycCountyCode2;
	}
	public void setCycCountyCode2(String cycCountyCode2) {
		this.cycCountyCode2 = cycCountyCode2;
	}
	public String getCycCounty2() {
		return cycCounty2;
	}
	public void setCycCounty2(String cycCounty2) {
		this.cycCounty2 = cycCounty2;
	}
	public String getCycDetail1() {
		return cycDetail1;
	}
	public void setCycDetail1(String cycDetail1) {
		this.cycDetail1 = cycDetail1;
	}
	public String getPost1() {
		return post1;
	}
	public void setPost1(String post1) {
		this.post1 = post1;
	}
	public String getCycProvinceCode() {
		return cycProvinceCode;
	}
	public void setCycProvinceCode(String cycProvinceCode) {
		this.cycProvinceCode = cycProvinceCode;
	}
	public String getCycProvince() {
		return cycProvince;
	}
	public void setCycProvince(String cycProvince) {
		this.cycProvince = cycProvince;
	}
	public String getCycCityCode() {
		return cycCityCode;
	}
	public void setCycCityCode(String cycCityCode) {
		this.cycCityCode = cycCityCode;
	}
	public String getCycCity() {
		return cycCity;
	}
	public void setCycCity(String cycCity) {
		this.cycCity = cycCity;
	}
	public String getCycCountyCode() {
		return cycCountyCode;
	}
	public void setCycCountyCode(String cycCountyCode) {
		this.cycCountyCode = cycCountyCode;
	}
	public String getCycCounty() {
		return cycCounty;
	}
	public void setCycCounty(String cycCounty) {
		this.cycCounty = cycCounty;
	}
	public String getDdlIDNumber2$districtCode() {
		return DdlIDNumber2$districtCode;
	}
	public void setDdlIDNumber2$districtCode(String ddlIDNumber2$districtCode) {
		DdlIDNumber2$districtCode = ddlIDNumber2$districtCode;
	}
	public String getCycDetail() {
		return cycDetail;
	}
	public void setCycDetail(String cycDetail) {
		this.cycDetail = cycDetail;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getHidyj() {
		return hidyj;
	}
	public void setHidyj(String hidyj) {
		this.hidyj = hidyj;
	}
	public String getHidCardID() {
		return HidCardID;
	}
	public void setHidCardID(String hidCardID) {
		HidCardID = hidCardID;
	}
	public String getHidID() {
		return HidID;
	}
	public void setHidID(String hidID) {
		HidID = hidID;
	}
	public String getHidAT() {
		return HidAT;
	}
	public void setHidAT(String hidAT) {
		HidAT = hidAT;
	}
	public String getHidName1() {
		return HidName1;
	}
	public void setHidName1(String hidName1) {
		HidName1 = hidName1;
	}
	public String getHidName2() {
		return HidName2;
	}
	public void setHidName2(String hidName2) {
		HidName2 = hidName2;
	}
	public String getHidTitle() {
		return HidTitle;
	}
	public void setHidTitle(String hidTitle) {
		HidTitle = hidTitle;
	}
	public String getHidBackUrl() {
		return HidBackUrl;
	}
	public void setHidBackUrl(String hidBackUrl) {
		HidBackUrl = hidBackUrl;
	}
	public String getHidBackFlag() {
		return HidBackFlag;
	}
	public void setHidBackFlag(String hidBackFlag) {
		HidBackFlag = hidBackFlag;
	}
	public String getHidCookieId() {
		return hidCookieId;
	}
	public void setHidCookieId(String hidCookieId) {
		this.hidCookieId = hidCookieId;
	}
	
	public String getVerifycode() {
		return verifycode;
	}
	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getIdentitycode() {
		return Identitycode;
	}
	public void setIdentitycode(String identitycode) {
		Identitycode = identitycode;
	}
	public String getCardColor() {
		return cardColor;
	}
	public void setCardColor(String cardColor) {
		this.cardColor = cardColor;
	}
	public String getGiftId() {
		return giftId;
	}
	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}
	public String getHidType() {
		return hidType;
	}
	public void setHidType(String hidType) {
		this.hidType = hidType;
	}
	public CookieStore getMyCookies() {
		return myCookies;
	}
	public void setMyCookies(CookieStore myCookies) {
		this.myCookies = myCookies;
	}

}
