package com.caiyi.financial.nirvana.ccard.material.banks.minsheng;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.discount.Constants;
import com.danga.MemCached.MemCachedClient;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***提交的时候 调用**/
public class MinShengSubmit {

	public static void main(String[] args){
//		CacheClient cc = CacheClient.getInstance();
//		String cardtypedata =(String)cc.get("minShengJobC_1360730199001140312");
//		System.out.println(cardtypedata);
		MinShengSubmit ms=new MinShengSubmit();

		ms.applyindex();
	}
	public static Logger logger = LoggerFactory.getLogger("materialBeanImpl");
	private MingshengBean  minshengBean; // 提交信息
	private CookieStore myCookies = new BasicCookieStore();// 这次通话 所需要维持的Cookies
	private Map<String, String> Header = new HashMap<>();// 这次通话 需要维持的 header
	/****模拟的时候需要访问的url***/
	private String urlCheckIdentity = "https://creditcard.cmbc.com.cn/wsonline/login/login.jhtml?id=";
	private String  urlCode = "https://creditcard.cmbc.com.cn/wsonline/captcha.jpg?";	 // 图片验证码

	private String urlCheckIdPost = "https://creditcard.cmbc.com.cn/wsonline/login/loginCheck.jhtml";//提交
	/**选择礼物***/
	private String urlgift  ="https://creditcard.cmbc.com.cn/wsonline/login/success.jhtml";
	private String   urlgiftPost = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/apply.jhtml";
	/****用户资料**/
	private String urlUserInfo =	"https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo1.jhtml";
	private String urlUserInfo2="https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2.jhtml";
	/**短信验证链接**/
	private String urlPhone = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendSMS.jhtml";
	/**公司信息资料提交链接**/
	private String urlCompanyInfo="https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2Save.jhtml";
	/***获取联系人页面**/
	private String urLinkmanInfo= "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo3.jhtml";
	/**提交联系人页面****/
	private String urlpostLinkMan=   "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo3Save.jhtml";

/**错误返回*/
	private  String  errorResult="";


	public  MinShengSubmit(){
		minshengBean = new MingshengBean();

	}
	/**测试输入函数***/
	public  String  SystemInput(String info){
		BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
		System.out.print(info + "\n");
		String str = null;
		try {
			str = strin.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public   void setCardIdUrl(){
		long m_Random = Calendar.getInstance().getTimeInMillis();
		String	url   = "https://creditcard.cmbc.com.cn/wsonline/login/login.jhtml?id="+  minshengBean.getCardType()+ "&time=" + m_Random;
		urlCheckIdentity = url;
	}

	/**提交模拟过程**/
	public void applyindex(){
		InitHead(); // 初始化post头

		//-------------
		NetHttp.getCode(myCookies,urlCode );  //获取验证码


		logger.info("myCookies1: " + myCookies);
		String verifycode = SystemInput("请输入验证码：");
		logger.info("验证码：" + verifycode);

		minshengBean.setVerifycode(verifycode); //输入验证码




		setCardIdUrl();// 获取身份验证
		String page =  getPage(urlCheckIdentity);

		logger.info("myCookies2: " + myCookies);
		getCheckIdentityPage(page); // 加密身份证Id


		/**提交身份验证**/
		String   result   =   PostPage(urlCheckIdPost,SetIdentity());
		  logger.info("身份证验证结果："+ result);
		/**获取礼物页面**/
		page = getPage(urlgift);
		// 礼物页面数据获取
		getgiftInfo(page);
		/**提交礼物页面**/
		result = PostPage(urlgiftPost,setapplydata());
		logger.info("提交礼物结果："+ result);
		/**获取个人资料页面**/

		page = getPage(urlUserInfo);
		getPersonalPage(page);
		//
		//getAllAreaCode();



		//  logger.info("个人资料页面:\n" + page);
//		/**提交个人资料**/
//		setTestData2();
//		result =PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo1Save.jhtml",getPersonal());
//		logger.info(" 提交个人资料结果:\n" + result);
//		/**获取公司页面**/
//		page =getPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2.jhtml");
//		logger.info("公司页面:\n" + page);
//		setCompanyTestdata();
//		/**电话验证**/
//
//	//	minshengBean.setMobilephone("15821129261");
//
//
//		result = PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendSMS.jhtml",getMobilephoneData());
//		logger.info("短信发送结果：" + result);
//
//		//-----------
//		// 输入电话验证
//		String verify = SystemInput("请输入验证码：");
//		logger.info("电话号码：" + verify);
//		minshengBean.setMcode(verify);
//		/**提交公司资料**/
//		getCompanyInfo(page);
//		result = PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2Save.jhtml",getCompanyData());
//	//	logger.info("公司资料提交结果：" + result);

	}
	// pospag 发送页面
	public String  PostPage(String url, Map<String , String> postdata){
		CloseableHttpClient client = NetHttp.getHttpClient(myCookies);
		String result= NetHttp.doPost(url, client, Header, postdata);
		return result;
	}
	public String  getPage(String url){

		CloseableHttpClient client = NetHttp.getHttpClient(myCookies);
		String page= NetHttp.doget(url,client, null, null);
		return page;
	}

	public void setTestData1(){
		minshengBean.setCardType("6");
		minshengBean.setIdentitycode("360730199001140312");

		minshengBean.setCardColor("91");
		minshengBean.setGiftId("0066"); // 主要两个礼物Id0066   0054
		minshengBean.setHidType("5");
	}

	public void  setTestData2(){
		minshengBean.setRiCname("邓宏");
		minshengBean.setRiPinYInName1("DENG");
		minshengBean.setRiPinYInName2("HONG");
		minshengBean.setProvince("15");
		minshengBean.setProvinceName("江西省");
		minshengBean.setIdnumCode1("131");
		minshengBean.setIdnumName("赣州市");
		minshengBean.setIdnumCode("4293");
		minshengBean.setSubIdnumName("宁都县");
		minshengBean.setIdnumStartdate("");
		minshengBean.setDate_now("2015-09-25");
		minshengBean.setIdnumEnddate("");
		minshengBean.setCycaddtype("B");
		minshengBean.setCycProvinceCode1("-1");
		minshengBean.setCycProvince1("");
		minshengBean.setCycCityCode1("-1");
		minshengBean.setCycCity1("");
		minshengBean.setCycCountyCode1("-1");
		minshengBean.setCycCounty1("");
		minshengBean.setCycProvinceCode2("2");
		minshengBean.setCycProvince2("上海市");
		minshengBean.setCycCityCode2("2");
		minshengBean.setCycCity2("上海市");
		minshengBean.setCycCountyCode2("2900");
		minshengBean.setCycCounty2("闵行区");
		minshengBean.setCycDetail1("东兰路万源路平南三四村14弄102号");
		minshengBean.setPost1("201100");
		minshengBean.setCycProvinceCode("6");
		minshengBean.setCycProvince("上海市");
		minshengBean.setCycCityCode("20");
		minshengBean.setCycCity("上海市");
		minshengBean.setCycCountyCode("167");
		minshengBean.setCycCounty("徐汇区");
		minshengBean.setCycDetail("桂平路391A座501号");
		minshengBean.setPost("200030");
		minshengBean.setHidyj("1");
		minshengBean.setHidCardID("27");
		minshengBean.setHidID("6");
		minshengBean.setHidAT("");
		minshengBean.setHidName1("邓");
		minshengBean.setHidName2("宏");
		minshengBean.setHidTitle("个人资料");
		minshengBean.setHidBackUrl("/wsonline/login/success.jhtml");
		minshengBean.setHidBackFlag("1");
		minshengBean.setHidCookieId("");

	}

	/**获取身份证验证页面所需元素**/
	public  boolean getCheckIdentityPage(String htmlStr ){
		Document doc=Jsoup.parse(htmlStr);
		if(!htmlStr.contains("modulus") &&
				!htmlStr.contains("exponent")){
			logger.info("身份验证页面错误");
			//logger.info("htmlStr:" + htmlStr);
			return false;
		}

		String  modulus=   doc.getElementById("modulus").toString();
		String  exponent=  doc.getElementById("exponent").toString();

		modulus = modulus.substring(modulus.indexOf("value=\"") + "value=\"".length() ,modulus.length() );
		modulus = modulus.substring( 0,modulus.indexOf("\"") );
		exponent = exponent.substring(exponent.indexOf("value=\"") + "value=\"".length() ,exponent.length() );
		exponent = exponent.substring( 0,exponent.indexOf("\"") );



		/***这里直接把身份证号码加密**/
		RunJS  codeByjs = new RunJS();
//		String   Identity = minshengBean.getIdentitycode();
		String   Identity = "412321198703023018";
		String	 Identitycode  = codeByjs.encryptedString(Identity, exponent, modulus);
		minshengBean.setIdentitycode(Identitycode); // 把身份证号码加密
		//	logger.info( "Identitycode:"+ Identitycode);
		return true;
	}
	/**获取礼物页面所需要的信息**/
	public  boolean getgiftInfo(String htmlStr){
		Document doc=Jsoup.parse(htmlStr);
		if(!htmlStr.contains("hidType")){
			return false ;
		}
		String  hidType=   doc.getElementById("hidType").toString();
		logger.info("hidType:" + hidType);
		if(hidType.contains("value=")){
			hidType = hidType.substring(hidType.indexOf("value=\"") + "value=\"".length() ,hidType.length() );
			hidType = hidType.substring( 0,hidType.indexOf("\"") );
			logger.info("hidType:" + hidType);
			minshengBean.setHidType(hidType);

		}
		return true;

	}
	/**提交公司资料页面 需要获取的 字段**/
	public  void getCompanyInfo(String htmlStr){
		if(!htmlStr.contains("HidCityArea") ||
				!htmlStr.contains("HidBackUrl")||
				!htmlStr.contains("HidBackFlag")){
			return;
		}
		Document doc=Jsoup.parse(htmlStr);
		String  HidCityArea= doc.getElementById("HidCityArea").toString();
		String  HidBackUrl = doc.getElementById("HidBackUrl").toString();
		String  HidBackFlag = doc.getElementById("HidBackFlag").toString();
		if(HidCityArea.contains("value")){
			HidCityArea = HidCityArea.substring(HidCityArea.indexOf("value=\"") + "value=\"".length() ,HidCityArea.length() );
			HidCityArea = HidCityArea.substring( 0,HidCityArea.indexOf("\"") );
			logger.info("HidCityArea:" + HidCityArea);
			minshengBean.setHidCityArea(HidCityArea);
		}
		if(HidBackUrl.contains("value")){
			HidBackUrl = HidBackUrl.substring(HidBackUrl.indexOf("value=\"") + "value=\"".length() ,HidBackUrl.length() );
			HidBackUrl = HidBackUrl.substring( 0,HidBackUrl.indexOf("\"") );
			minshengBean.setHidBackUrl(HidBackUrl);
			logger.info("HidBackUrl:" + HidBackUrl);

		}
		if(HidBackFlag.contains("value")){
			HidBackFlag = HidBackFlag.substring(HidBackFlag.indexOf("value=\"") + "value=\"".length() ,HidBackFlag.length() );
			HidBackFlag = HidBackFlag.substring( 0,HidBackFlag.indexOf("\"") );
			minshengBean.setHidBackFlag(HidBackFlag);
			logger.info("HidBackFlag:" + HidBackFlag);
		}
	}

	/**传入省名  城市名得到城市Id**/
	/***
	 * LocalName  你要查找的地名
	 *  pId   要查找地名的上一级
	 *  Area    city 表示城市   county 县 区
	 * type 1 ， 2   网站有两种获取 地理位置的请求
	 * */
	public String getLocal(String LocalName,String pId, String Area,String type){

		String  url = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/getArea.jhtml";// 获取地址url
		Map<String,String> mapData =  setAreaData(Area, pId,type);
		CloseableHttpClient postClient = NetHttp.getHttpClient(myCookies);
		String result= NetHttp.doPost(url,postClient, Header,mapData);
		if(LocalName.contains("上海市")){
			LocalName = "上海市";
		}
		if(LocalName.contains("天津市")){
			LocalName = "天津市";
		}
		if(LocalName.contains("重庆市")){
			LocalName = "重庆市";
		}
		if(LocalName.contains("北京市")){
			LocalName = "北京市";
		}

		//开始通过结果来获取城市ID
		logger.info("获取城市：" + LocalName + "前置：" + pId);

		if(result.contains(LocalName)){
			//logger.info(result);
			result = result.substring(0,result.indexOf(LocalName));
			result = result.substring(result.lastIndexOf("value"));
			result= result.replaceAll("[^0-9]", "");
		}
		logger.info(result);
		return result;
	}

	/***设置获取位置数据**/
	public  Map<String, String> setAreaData(String areaType, String prev, String type ){
		Map<String,String> mapData = new HashMap<>();
		mapData.put("areaType", areaType);
		mapData.put("prev", prev);
		mapData.put("type", type);

		return mapData;
	}
	/*** 传入省名 获取ID****/
	public String getProvinceId(String provinceName, String type){
		Map<String,String> mapData = new HashMap<>();
		if(type.equals("2")){
			mapData.put("北京市", "1");
			mapData.put("上海市", "2");
			mapData.put("天津市", "3");
			mapData.put("重庆市", "4");
			mapData.put("河北省", "5");
			mapData.put("山西省", "6");
			mapData.put("内蒙古", "7");
			mapData.put("辽宁省", "8");
			mapData.put("吉林省", "9");

			mapData.put("黑龙江省", "10");
			mapData.put("江苏省", "11");
			mapData.put("浙江省", "12");
			mapData.put("安徽省", "13");
			mapData.put("福建省", "14");
			mapData.put("江西省", "15");
			mapData.put("山东省", "16");
			mapData.put("河南省", "17");
			mapData.put("湖北省", "18");
			mapData.put("湖南省", "19");
			mapData.put("广东省", "20");

			mapData.put("广西省", "21");
			mapData.put("广西壮族自治区", "21");
			mapData.put("海南省", "22");
			mapData.put("四川省", "23");
			mapData.put("贵州省", "24");
			mapData.put("云南省", "25");
			mapData.put("西藏", "26");
			mapData.put("西藏自治区", "26");
			mapData.put("陕西省", "27");
			mapData.put("甘肃省", "28");
			mapData.put("青海省", "29");
			mapData.put("宁夏", "30");
			mapData.put("宁夏回族自治区", "30");

			mapData.put("新疆", "31");
			mapData.put("新疆维吾尔自治区", "31");
		}
		if(type.equals("1")){
			mapData.put("河北省", "1");
			mapData.put("山西省", "2");
			mapData.put("北京市", "3");
			mapData.put("山东省", "4");
			mapData.put("天津市", "5");
			mapData.put("上海市", "6");
			mapData.put("江苏省", "7");
			mapData.put("浙江省", "8");
			mapData.put("湖北省", "9");
			mapData.put("陕西省", "10");



			mapData.put("河南省", "11");
			mapData.put("湖南省", "12");
			mapData.put("江西省", "13");
			mapData.put("安徽省", "14");
			mapData.put("广东省", "15");
			mapData.put("福建省", "16");
			mapData.put("四川省", "17");
			mapData.put("重庆市", "18");
			mapData.put("云南省", "19");
			mapData.put("广西省", "20");
			mapData.put("广西壮族自治区", "20");


			mapData.put("海南省", "21");
			mapData.put("贵州省", "22");
			mapData.put("辽宁省", "23");
			mapData.put("内蒙古", "24");
			mapData.put("吉林省", "25");
			mapData.put("黑龙江省", "26");
			mapData.put("甘肃省", "28");

		}
		return (String)mapData.get(provinceName);

	}

	/**设置身份证信息数据**/
	public  Map<String, String> SetIdentity(){
		Map<String,String> mapData = new HashMap<>();
		mapData.put("checkCode", minshengBean.getVerifycode());// 验证码
		mapData.put("proId", minshengBean.getCardType()); //卡类型
		mapData.put("dtype", "1");
		mapData.put("dname", "身份证");
		mapData.put("dnum", minshengBean.getIdentitycode()); //"加密的的身份证号码"
		mapData.put("ftype", "ph");
		long time = System.currentTimeMillis();
		mapData.put("time",  Long.toString(time));

//			 logger.info("checkCode: " +minshengBean.getVerifycode() );
//			 logger.info("proId: " +minshengBean.getCardType() );
//			 logger.info("dtype: " +"1" );
//			 logger.info("dname: " +"身份证" );
//			 logger.info("dnum:" +minshengBean.getIdentitycode() );
//			 logger.info("ftype:" +"ph" );
//			 logger.info("time" +Long.toString(time) );
		return mapData;

	}
	/***设置获取礼物数据**/
	public  Map<String, String>  setapplydata(){
		Map<String,String> mapData = new HashMap<>();
		mapData.put("checkFlag", "false");
		mapData.put("sysId", minshengBean.getCardColor());// 卡风格
		mapData.put("hidType",minshengBean.getHidType());//  这个字段可以写死
		mapData.put("giftId", minshengBean.getGiftId());// 礼物
		logger.info("hidType:" + minshengBean.getHidType());
		logger.info("sysId:" + minshengBean.getCardColor());
		logger.info("礼物：" + minshengBean.getGiftId());

		return mapData;

	}
	/**设置个人资料**/
	public  Map<String, String> getPersonal(){
		Map<String,String> mapData = new HashMap<>();
		mapData.put("cycaddtype", minshengBean.getCycaddtype()); // 交付卡地址类型  H 住宅 , B 单位
		mapData.put("cycCity", minshengBean.getCycCity());//单位城市   “xx市”
		mapData.put("cycCity1", minshengBean.getCycCity1());// 交付住宅市 “xx市”
		mapData.put("cycCity2", minshengBean.getCycCity2());//交付单位 市 “XX市”
		mapData.put("cycCityCode",minshengBean.getCycCityCode() );//单位所在市  Index；
		mapData.put("cycCityCode1", minshengBean.getCycCityCode1());// 交付住宅市 Index
		mapData.put("cycCityCode2", minshengBean.getCycCityCode2());// 交付单位市  Index
		mapData.put("cycCounty", minshengBean.getCycCounty()); // 单位所在区
		mapData.put("cycCounty1", minshengBean.getCycCounty1());// 交付住宅所在区
		mapData.put("cycCounty2",  minshengBean.getCycCounty2());//交付单位所在区
		mapData.put("cycCountyCode", minshengBean.getCycCountyCode());//单位所在区 IndexIndex
		mapData.put("cycCountyCode1", minshengBean.getCycCountyCode1());// 交付住宅所在区Index
		mapData.put("cycCountyCode2", minshengBean.getCycCountyCode2() );//交付单位所在区Index
		mapData.put("cycDetail", minshengBean.getCycDetail());//单位详细地址
		mapData.put("cycDetail1", minshengBean.getCycDetail1());// 住宅详细地址
		mapData.put("cycProvince", minshengBean.getProvince());// 单位省份
		mapData.put("cycProvince1",minshengBean.getCycProvince1() );// 交付住宅所在省
		mapData.put("cycProvince2",minshengBean.getCycProvince2() );// 交付单位所在省
		mapData.put("cycProvinceCode", minshengBean.getCycProvinceCode());// 单位省份Index
		mapData.put("cycProvinceCode1", minshengBean.getCycProvinceCode1());// 交付住宅所在省Index
		mapData.put("cycProvinceCode2", minshengBean.getCycProvinceCode2());// 交付单位所在省Index


		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date_now = dateFormat.format( now );

		mapData.put("date_now", date_now);
		/**以下数据可以直接为空**/
		mapData.put("DdlIDNumber1$districtCode", "");
		mapData.put("DdlIDNumber1$districtText", "");
		mapData.put("DdlIDNumber1$idnumberC", "");
		mapData.put("DdlIDNumber1$idnumberP", "");
		mapData.put("DdlIDNumber2$districtCode", "");
		mapData.put("DdlIDNumber2$districtText", "");
		mapData.put("DdlIDNumber2$idnumberC", "");
		mapData.put("DdlIDNumber2$idnumberP", "");
		mapData.put("DdlIDNumber3$districtCode", "");
		mapData.put("DdlIDNumber3$districtText", "");
		mapData.put("DdlIDNumber3$idnumberC", "");
		mapData.put("DdlIDNumber3$idnumberP", "");
		mapData.put("DdlIDNumber4$districtCode", "");
		mapData.put("DdlIDNumber4$districtText", "");
		mapData.put("DdlIDNumber4$idnumberC", "");
		mapData.put("DdlIDNumber4$idnumberP", "");
		/**************************/
		mapData.put("HidAT", minshengBean.getHidAT());
		mapData.put("HidBackFlag", minshengBean.getHidBackFlag());

		mapData.put("HidBackUrl", "/wsonline/login/success.jhtml");
		mapData.put("HidCardID", minshengBean.getHidBackUrl());
		mapData.put("hidCookieId", minshengBean.getHidCookieId());// 直接空值可以
		mapData.put("HidID", minshengBean.getHidID());
		/**************************/
		mapData.put("HidName1", minshengBean.getHidName1());// 姓   邓
		mapData.put("HidName2", minshengBean.getHidName2());// 字   宏
		mapData.put("HidTitle", "个人资料");
		/***证件类型**/
		mapData.put("hidyj", "1");// 证件身份证
		mapData.put("idnumCode", minshengBean.getIdnumCode());
		mapData.put("idnumCode1",minshengBean.getIdnumCode1() );
		mapData.put("idnumEnddate", minshengBean.getIdnumEnddate()); //  证件开始时间
		mapData.put("idnumName", minshengBean.getIdnumName());// 身份证所在市 “赣州市”
		mapData.put("idnumStartdate", minshengBean.getIdnumStartdate());// 证件结束时间
		mapData.put("post", minshengBean.getPost());// 单位邮编
		mapData.put("post1", minshengBean.getPost1()); //住宅邮编
		mapData.put("province", minshengBean.getProvince()); // 省份ID
		mapData.put("provinceName", minshengBean.getProvinceName()); //身份证省份名字 江西省
		mapData.put("riCname", minshengBean.getRiCname());//名字  邓宏
		mapData.put("riPinYInName1",minshengBean.getRiPinYInName1());// 名字拼音 姓
		mapData.put("riPinYInName2", minshengBean.getRiPinYInName2());// 名字拼音  字
		mapData.put("subIdnumName", minshengBean.getSubIdnumName());// 身份证所在地区
		return mapData;
	}
	/**设置提交公司资料测试**/
	public  void setCompanyTestdata(){

		minshengBean.setCompName("上海彩亿信息技术有限公司");//
		minshengBean.setCompanyPhoneAare("021");//	021
		minshengBean.setCompanyPhone("4006731");//
		minshengBean.setCompanyPhoneExt("");//
		minshengBean.setCompPhone("");//
		minshengBean.setWorkDate("2");//	2
		minshengBean.setMARST("S");//	S
		minshengBean.setEducation("4");//	4
		//	minshengBean.setMobilephone("15821129261");//	15821129261
		minshengBean.setMcode("");//	727505
		minshengBean.setEmail("554010940@qq.com");//
		minshengBean.setCycQQ("");//
		minshengBean.setHomezonephone("");//
		minshengBean.setHomephone("");//
		//	minshengBean.setHidMcode("");//	验证码
		minshengBean.setHidAT("");//
		//	minshengBean.setHidCityArea("010,020,021,022,023,024,025,027,028,029,0310,0311,0312,0313,0314,0315,0316,0317,0318,0319,0335,0350,0351,0352,0353,0349,0354,0355,0356,0357,0358,0359,0370,0371,0372,0373,0374,0375,0376,0377,0378,0379,0391,0392,0393,0394,0395,0396,0398,0410,0411,0412,0413,0414,0415,0416,0417,0418,0419,0421,0427,0429,0431,0432,0433,0434,0435,0436,0437,0438,0439,0440,0451,0452,0453,0454,0455,0456,0457,0458,0459,0464,0467,0468,0469,0470,0471,0472,0473,0474,0475,0476,0477,0478,0483,0479,0482,0510,0511,0512,0513,0514,0515,0516,0517,0518,0519,0523,0527,0530,0531,0532,0533,0632,0543,0635,0633,0634,0534,0535,0536,0537,0538,0539,0546,0631,0550,0551,0552,0553,0554,0555,0556,0559,0557,0558,0566,0561,0562,0563,0564,0565,0570,0571,0572,0573,0574,0575,0576,0577,0578,0579,0580,0591,0592,0593,0594,0595,0596,0597,0598,0599,0660,0662,0663,0668,0750,0751,0752,0753,0754,0755,0756,0757,0758,0759,0760,0762,0763,0766,0768,0769,0710,0711,0712,0713,0714,0715,0716,0717,0718,0719,0722,0724,0728,0730,0731,0732,0733,0734,0735,0736,0737,0738,0739,0743,0744,0745,0746,0770,0771,0772,0773,0774,0775,0776,0777,0778,0779,0790,0791,0792,0793,0794,0795,0796,0797,0798,0799,0701,0812,0813,0816,0817,0818,0825,0826,0827,0830,0831,0832,0833,0834,0835,0836,0837,0838,0839,0851,0852,0853,0854,0855,0856,0857,0858,0859,0870,0691,0692,0871,0872,0873,0874,0875,0876,0877,0878,0879,0883,0886,0887,0888,0891,0892,0893,0894,0895,0896,0897,0898,0899,0890,0911,0912,0913,0914,0915,0916,0917,0919,0930,0931,0932,0933,0934,0935,0936,0937,0938,0941,0943,0951,0952,0953,0954,0955,0970,0971,0972,0973,0974,0975,0976,0977,0991,0990,0995,0902,0994,0909,0996,0997,0998,0903,0999,0993,0906,0901");//	010,020,021,022,023,024,025,027,028,029,0310,0311,0312,0313,0314,0315,0316,0317,0318,0319,0335,0350,0351,0352,0353,0349,0354,0355,0356,0357,0358,0359,0370,0371,0372,0373,0374,0375,0376,0377,0378,0379,0391,0392,0393,0394,0395,0396,0398,0410,0411,0412,0413,0414,0415,0416,0417,0418,0419,0421,0427,0429,0431,0432,0433,0434,0435,0436,0437,0438,0439,0440,0451,0452,0453,0454,0455,0456,0457,0458,0459,0464,0467,0468,0469,0470,0471,0472,0473,0474,0475,0476,0477,0478,0483,0479,0482,0510,0511,0512,0513,0514,0515,0516,0517,0518,0519,0523,0527,0530,0531,0532,0533,0632,0543,0635,0633,0634,0534,0535,0536,0537,0538,0539,0546,0631,0550,0551,0552,0553,0554,0555,0556,0559,0557,0558,0566,0561,0562,0563,0564,0565,0570,0571,0572,0573,0574,0575,0576,0577,0578,0579,0580,0591,0592,0593,0594,0595,0596,0597,0598,0599,0660,0662,0663,0668,0750,0751,0752,0753,0754,0755,0756,0757,0758,0759,0760,0762,0763,0766,0768,0769,0710,0711,0712,0713,0714,0715,0716,0717,0718,0719,0722,0724,0728,0730,0731,0732,0733,0734,0735,0736,0737,0738,0739,0743,0744,0745,0746,0770,0771,0772,0773,0774,0775,0776,0777,0778,0779,0790,0791,0792,0793,0794,0795,0796,0797,0798,0799,0701,0812,0813,0816,0817,0818,0825,0826,0827,0830,0831,0832,0833,0834,0835,0836,0837,0838,0839,0851,0852,0853,0854,0855,0856,0857,0858,0859,0870,0691,0692,0871,0872,0873,0874,0875,0876,0877,0878,0879,0883,0886,0887,0888,0891,0892,0893,0894,0895,0896,0897,0898,0899,0890,0911,0912,0913,0914,0915,0916,0917,0919,0930,0931,0932,0933,0934,0935,0936,0937,0938,0941,0943,0951,0952,0953,0954,0955,0970,0971,0972,0973,0974,0975,0976,0977,0991,0990,0995,0902,0994,0909,0996,0997,0998,0903,0999,0993,0906,0901
		minshengBean.setHidTitle("个人资料");//	个人资料
		//	minshengBean.setHidBackUrl("wsonline/onlineapplication/userInfo1.jhtml");//	/
		//	minshengBean.setHidBackFlag("1");//	1


	}

	/**提交公司资料**/
	public  Map<String, String>  getCompanyData(){
		Map<String,String> mapData = new HashMap<>();
		logger.info("公司提交资料：111");


		mapData.put("compName",minshengBean.getCompName());
		mapData.put("companyPhoneAare",minshengBean.getCompanyPhoneAare());
		mapData.put("companyPhone",minshengBean.getCompanyPhone());

		mapData.put("companyPhoneExt",minshengBean.getCompanyPhoneExt());
		mapData.put("compPhone",minshengBean.getCompPhone());
		mapData.put("workDate",minshengBean.getWorkDate());
		mapData.put("MARST",minshengBean.getMARST());
		mapData.put("education",minshengBean.getEducation());

		mapData.put("email", minshengBean.getEmail());
		mapData.put("cycQQ", minshengBean.getCycQQ());

		mapData.put("homezonephone", minshengBean.getHomezonephone());
		mapData.put("homephone",minshengBean.getHomephone());

		mapData.put("HidAT",minshengBean.getHidAT());
		mapData.put("HidCityArea",minshengBean.getHidCityArea());
		mapData.put("HidTitle",minshengBean.getHidTitle());
		mapData.put("HidBackUrl",minshengBean.getHidBackUrl());
		mapData.put("HidBackFlag", minshengBean.getHidBackFlag());

		return mapData;
	}
	/**设置发送短信数据**/
	public  Map<String, String>  getMobilephoneData(){
		Map<String,String> mapData = new HashMap<>();
		mapData.put("mobile",minshengBean.getMobilephone());
		return mapData;

	}
	/**获取提交联系人页面要获取的元素**/
	public void getLinkManpage(String htmlstr ){
		if(!htmlstr.contains("HidCardID")||
				!htmlstr.contains("AppName")||
				!htmlstr.contains("HidTitle")||
				!htmlstr.contains("HidBackFlag")){
			return;
		}
		Document doc=Jsoup.parse(htmlstr);
		String  HidCardID= doc.getElementById("HidCardID").toString();
		String  AppName = doc.getElementById("AppName").toString();
		String  HidTitle = doc.getElementById("HidTitle").toString();
		String  HidBackFlag = doc.getElementById("HidBackFlag").toString();
		if(HidCardID.contains("value")){
			HidCardID = HidCardID.substring(HidCardID.indexOf("value=\"") + "value=\"".length() ,HidCardID.length() );
			HidCardID = HidCardID.substring( 0,HidCardID.indexOf("\"") );
			logger.info("HidCardID:" + HidCardID);
			minshengBean.setHidCardID(HidCardID);
		}
		if(AppName.contains("value")){
			AppName = AppName.substring(AppName.indexOf("value=\"") + "value=\"".length() ,AppName.length() );
			AppName = AppName.substring( 0,AppName.indexOf("\"") );
			logger.info("AppName:" + AppName);
			minshengBean.setAppName(AppName);
		}

		if(HidTitle.contains("value")){
			HidTitle = HidTitle.substring(HidTitle.indexOf("value=\"") + "value=\"".length() ,HidTitle.length() );
			HidTitle = HidTitle.substring( 0,HidTitle.indexOf("\"") );
			logger.info("HidTitle:" + HidTitle);
			minshengBean.setHidTitle(HidTitle);
		}

		if(HidBackFlag.contains("value")){
			HidBackFlag = HidBackFlag.substring(HidBackFlag.indexOf("value=\"") + "value=\"".length() ,HidBackFlag.length() );
			HidBackFlag = HidBackFlag.substring( 0,HidBackFlag.indexOf("\"") );
			logger.info("HidBackFlag:" + HidBackFlag);
			minshengBean.setHidBackFlag(HidBackFlag);
		}



	}

	/**提交联系人测试数据**/
	public void setLinkManTest(){
		minshengBean.setFamilymemName1("赵杰");	//赵杰
		minshengBean.setFamilymemMobile1("15092385024");	//	15092385024
		minshengBean.setFamilymemShip1("4");	//	4
		minshengBean.setFamilymemShip1Name("其他");	//	其他
		minshengBean.setLCustName("周伟新");	//	周伟新
		minshengBean.setLCustPhone("13824489127");	//	13824489127
		minshengBean.setLCustShip("3");	//	3
		minshengBean.setlCustShipName("同事");	//	同事

		minshengBean.setCompanyPhone("4006739188");	//	4006739188
		minshengBean.setAppName("邓宏");	//	邓宏
		minshengBean.setHidJuniorMessType("");	//
		//	minshengBean.setHidTitle("");	//	联系人资料
		//	minshengBean.setHidBackUrl("");	//	/wsonline/onlineapplication/userInfo2.jhtml
		//	minshengBean.setHidBackFlag("");	//	1
		//	minshengBean.setHidActiveUrl("");	//



	}
	/**提交联系人数据设置**/
	public  Map<String, String> getLinkManData(){





		Map<String,String> mapData = new HashMap<>();
		mapData.put("familymemName1",minshengBean.getFamilymemName1());
		mapData.put("familymemMobile1",minshengBean.getFamilymemMobile1());
		mapData.put("familymemShip1",minshengBean.getFamilymemShip1());
		mapData.put("familymemShip1Name",minshengBean.getFamilymemShip1Name());
		mapData.put("LCustName", minshengBean.getLCustName());
		mapData.put("LCustPhone", minshengBean.getLCustPhone());
		mapData.put("LCustShip", minshengBean.getLCustShip());
		mapData.put("lCustShipName", minshengBean.getlCustShipName());



		mapData.put("HidAT", minshengBean.getHidAT());
		mapData.put("HidCardID", minshengBean.getHidCardID());
		mapData.put("MobilePhone", minshengBean.getMobilephone());
		mapData.put("CompanyPhone", minshengBean.getCompanyPhone());
		mapData.put("AppName", minshengBean.getAppName());
		mapData.put("HidJuniorMessType", minshengBean.getHidJuniorMessType());
		mapData.put("HidTitle", minshengBean.getHidTitle());
		mapData.put("HidBackUrl", minshengBean.getHidBackUrl());
		mapData.put("HidBackFlag", minshengBean.getHidBackFlag());
		mapData.put("HidActiveUrl",minshengBean.getHidActiveUrl());
		if(minshengBean.getCardType().equals("15")){

			mapData.put("dropJunior","1");
		}

		return mapData;
	}
	/**申请完成**/
	//https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendWechat.jhtml
	public  Map<String, String>getUserNameData(){

		minshengBean.setHidTitle("申请完成");//	申请完成
		minshengBean.setHidBackUrl("wsonline/index/indexios.jhtml");
		minshengBean.setHidBackFlag("1");//
		Map<String,String> mapData = new HashMap<>();

		mapData.put("userName",minshengBean.getUserName() );
		//	mapData.put("openId", minshengBean.getOpenId());
		//	mapData.put("recommendId", minshengBean.getRecommendId());
		mapData.put("HidTitle", minshengBean.getHidTitle());
		mapData.put("HidBackUrl", minshengBean.getHidBackUrl());
		mapData.put("HidBackFlag", minshengBean.getHidBackFlag());

		return mapData;
	}

	public  void setUserNameData(){

		minshengBean.setUserName("邓宏");//	李波
		minshengBean.setOpenId("");//
		minshengBean.setRecommendId("");//
		minshengBean.setHidTitle("申请完成");//	申请完成
		minshengBean.setHidBackUrl("wsonline/index/indexios.jhtml");//	/wsonline/index/indexios.jhtml
		minshengBean.setHidBackFlag("1");//	1
	}






	/***设置提交确认 数据**/
	public  Map<String, String>getsubmitData(){
		Map<String,String> mapData = new HashMap<>();
		logger.info("手机号码：1" + minshengBean.getMobilephone());
		mapData.put("riCname", minshengBean.getRiCname());
		mapData.put("riPinYInName1",minshengBean.getRiPinYInName1());
		mapData.put("riPinYInName2",minshengBean.getRiPinYInName2());
		mapData.put("cycProvince1Code", minshengBean.getCycProvince1Code());
		mapData.put("cycProvince1",  minshengBean.getCycProvince1());
		mapData.put("cycCity1Code", minshengBean.getCycCity1Code());
		mapData.put("cycCity1",  minshengBean.getCycCity1());
		mapData.put("cycCounty1Code", minshengBean.getCycCounty1Code());
		mapData.put("cycCounty1", minshengBean.getCycCounty1());
		mapData.put("DdlIDNumber3$districtCode", minshengBean.getDdlIDNumber3$districtCode());
		mapData.put("cycDetail1", minshengBean.getCycDetail1());
		mapData.put("cycProvinceCode", minshengBean.getCycProvinceCode());
		mapData.put("DeliveryAddress", minshengBean.getDeliveryAddress());
		mapData.put("cycProvince", minshengBean.getCycProvince());
		mapData.put("cycCityCode", minshengBean.getCycCityCode());
		mapData.put("cycCity", minshengBean.getCycCity());
		mapData.put("cycCountyCode", minshengBean.getCycCountyCode());
		mapData.put("cycCounty", minshengBean.getCycCounty());
		mapData.put("DdlIDNumber2$districtCode", minshengBean.getDdlIDNumber2$districtCode());
		mapData.put("cycDetail", minshengBean.getCycDetail());
		mapData.put("HidName1", "0");
		mapData.put("HidName2", "0");
		mapData.put("HidTitle", minshengBean.getHidTitle());
		mapData.put("HidBackUrl", "/wsonline/onlineapplication/userInfo2.jhtml");
		mapData.put("HidBackFlag", minshengBean.getHidBackFlag());
		mapData.put("HidCookie", minshengBean.getHidCookie());
		logger.info("手机号码：2" + minshengBean.getMobilephone());
		// 新增
		mapData.put("mobilephone", minshengBean.getMobilephone());

		mapData.put("mcode", minshengBean.getMcode());
		mapData.put("HidMcode", minshengBean.getHidMcode());
		mapData.put("HidCustPhone", minshengBean.getFamilymemMobile1());
		mapData.put("HidCustPhone1", minshengBean.getLCustPhone());


		logger.info("riCname" + "  " + minshengBean.getRiCname());
		logger.info("riPinYInName1" + "  " + minshengBean.getRiPinYInName1());
		logger.info("riPinYInName2" + "  " + minshengBean.getRiPinYInName2());
		logger.info("cycProvince1Code" + "  " +  minshengBean.getCycProvince1Code());
		logger.info("cycProvince1" + "  " +   minshengBean.getCycProvince1());
		logger.info("cycCity1Code" + "  " +  minshengBean.getCycCity1Code());
		logger.info("cycCity1" + "  " +   minshengBean.getCycCity1());
		logger.info("cycCounty1Code" + "  " +  minshengBean.getCycCounty1Code());
		logger.info("cycCounty1" + "  " +  minshengBean.getCycCounty1());
		logger.info("DdlIDNumber3$districtCode" + "  " +  minshengBean.getDdlIDNumber3$districtCode());
		logger.info("cycDetail1" + "  " +  minshengBean.getCycDetail1());
		logger.info("cycProvinceCode" + "  " +  minshengBean.getCycProvinceCode());
		logger.info("DeliveryAddress" + "  " +  minshengBean.getDeliveryAddress());
		logger.info("cycProvince" + "  " +  minshengBean.getCycProvince());
		logger.info("cycCityCode" + "  " +  minshengBean.getCycCityCode());
		logger.info("cycCity" + "  " +  minshengBean.getCycCity());
		logger.info("cycCountyCode" + "  " +  minshengBean.getCycCountyCode());
		logger.info("cycCounty" + "  " +  minshengBean.getCycCounty());
		logger.info("DdlIDNumber2$districtCode" + "  " +  minshengBean.getDdlIDNumber2$districtCode());
		logger.info("cycDetail" + "  " +  minshengBean.getCycDetail());
		logger.info("HidName1" + "  " +  minshengBean.getHidName1());
		logger.info("HidName2" + "  " +  minshengBean.getHidName2());
		logger.info("HidTitle" + "  " +  minshengBean.getHidTitle());
		logger.info("HidBackUrl" + "  " +  "/wsonline/onlineapplication/userInfo2.jhtml");
		logger.info("HidBackFlag" + "  " +  minshengBean.getHidBackFlag());
		logger.info("HidCookie" + "  " +  minshengBean.getHidCookie());
		logger.info("mobilephone" + "  " +  minshengBean.getMobilephone());
		logger.info("mcode" + "  " +  minshengBean.getMcode());
		logger.info("HidMcode" + "  " +  minshengBean.getHidMcode());
		logger.info("HidCustPhone" + "  " +  minshengBean.getFamilymemMobile1());
		logger.info("HidCustPhone1" + "  " +  minshengBean.getLCustPhone());

		return mapData;
	}
	/**设置提交数据--测试数据**/

	public void SetsubmitData(){
		minshengBean.setRiCname("邓宏");//	邓测试
		minshengBean.setRiPinYInName1("DENG");//	DENG
		minshengBean.setRiPinYInName2("HONG");//	CESHI
		minshengBean.setCycProvince1Code("2");//	2
		minshengBean.setCycProvince1("上海市");//	上海市
		minshengBean.setCycCity1Code("2");//	2
		minshengBean.setCycCity1("上海市");//		上海市
		minshengBean.setCycCounty1Code("2900");//		2900
		minshengBean.setCycCounty1("徐汇区");//		徐汇区
		//	minshengBean.setDdlIDNumber3$districtCode("");//		1000
		minshengBean.setCycDetail1("桂平路391号A座501");//		桂平路391路A601号
		minshengBean.setCycProvinceCode("6");//		6
		minshengBean.setCycProvince("上海市");//		上海市
		minshengBean.setCycCityCode("20");//20
		minshengBean.setCycCity("上海市");//	上海市
		minshengBean.setCycCountyCode("167");//	167
		minshengBean.setCycCounty("徐汇区");//	徐汇区
		//	minshengBean.setDdlIDNumber2$districtCode("");//	448
		minshengBean.setCycDetail("桂平路391号A座501");//	桂平路400号B301号
		minshengBean.setHidName1("0");//	0
		minshengBean.setHidName2("0");//	0
		minshengBean.setHidTitle("信息确认");//
		minshengBean.setHidBackUrl("/wsonline/onlineapplication/userInfo2.jhtml");//	/wsonline/onlineapplication/userInfo2.jhtml
		//	minshengBean.setHidBackFlag("");//	1
		minshengBean.setHidCookie("");//
		minshengBean.setDeliveryAddress("B");//	B

	}
	/***从确认HTML文件中获取找资料**/
	public void getSubmitInfo(String html){
		String  DdlIDNumber3_districtCode = "";
		String  DdlIDNumber2_districtCode = "";
		String  HidBackFlag = "";
		Document doc=Jsoup.parse(html);




		if (html.contains("DdlIDNumber3_districtCode")){
			DdlIDNumber3_districtCode = doc.getElementById("DdlIDNumber3_districtCode").toString();
		}
		if(html.contains("DdlIDNumber2_districtCode")){
			DdlIDNumber2_districtCode = doc.getElementById("DdlIDNumber2_districtCode").toString();
		}
		if(html.contains("HidBackFlag")){
			HidBackFlag = doc.getElementById("HidBackFlag").toString();
		}
		if(DdlIDNumber3_districtCode !=null  &&
				DdlIDNumber3_districtCode.contains("value=") ){
			DdlIDNumber3_districtCode = DdlIDNumber3_districtCode.substring(DdlIDNumber3_districtCode.indexOf("value=\"") + "value=\"".length() ,DdlIDNumber3_districtCode.length() );
			DdlIDNumber3_districtCode = DdlIDNumber3_districtCode.substring( 0,DdlIDNumber3_districtCode.indexOf("\"") );
			logger.info("DdlIDNumber3_districtCode:" + DdlIDNumber3_districtCode);
			minshengBean.setDdlIDNumber3$districtCode(DdlIDNumber3_districtCode);

		}
		// 单位
		if(minshengBean.getDeliveryAddress().contains("B")) {
			minshengBean.setDdlIDNumber3$districtCode(minshengBean.getIdnumCode());

		}



		if(DdlIDNumber2_districtCode !=null  &&
				DdlIDNumber2_districtCode.contains("value=") ){
			DdlIDNumber2_districtCode = DdlIDNumber2_districtCode.substring(DdlIDNumber2_districtCode.indexOf("value=\"") + "value=\"".length() ,DdlIDNumber2_districtCode.length() );
			DdlIDNumber2_districtCode = DdlIDNumber2_districtCode.substring( 0,DdlIDNumber2_districtCode.indexOf("\"") );
			logger.info("DdlIDNumber2_districtCode:" + DdlIDNumber2_districtCode);


		}
		minshengBean.setDdlIDNumber2$districtCode(DdlIDNumber2_districtCode);
		if(HidBackFlag !=null  &&
				HidBackFlag.contains("value=") ){
			HidBackFlag = HidBackFlag.substring(HidBackFlag.indexOf("value=\"") + "value=\"".length() ,HidBackFlag.length() );
			HidBackFlag = HidBackFlag.substring( 0,HidBackFlag.indexOf("\"") );
			logger.info("HidBackFlag:" + HidBackFlag);
			minshengBean.setHidBackFlag(HidBackFlag);
		}
		minshengBean.setHidTitle("信息确认");//
		minshengBean.setHidBackUrl("/wsonline/onlineapplication/userInfo2.jhtml");//	/wsonline/onlineapplication/userInfo2.jhtml

		minshengBean.setHidCookie("");//


	}


	/**设置新的head*/
	public void setHead(String key, String value){


		Header.put(key, value);
	}
	/**初始化head值**/
	public void InitHead(){
		Header.put("Accept-Encoding","gzip, deflate, sdch");
		Header.put("Accept-Language","zh-CN,zh;q=0.8");
		Header.put("Connection","keep-alive");
		Header.put("Host","creditcard.cmbc.com.cn");
		Header.put("Upgrade-Insecure-Requests","1");
		Header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
		Header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		Header.put("X-Requested-With","XMLHttpRequest");
	}

	/***获取所有的省市县索引****/
//	public void getAllAreaCode(){
//		// 类型2
//		Map<String,String> mapData = new HashMap<>();
//		mapData.put("北京市", "1");
//		mapData.put("上海市", "2");
//		mapData.put("天津市", "3");
//		mapData.put("重庆市", "4");
//		mapData.put("河北省", "5");
//		mapData.put("山西省", "6");
//		mapData.put("内蒙古", "7");
//		mapData.put("辽宁省", "8");
//		mapData.put("吉林省", "9");
//
//		mapData.put("黑龙江省", "10");
//		mapData.put("江苏省", "11");
//		mapData.put("浙江省", "12");
//		mapData.put("安徽省", "13");
//		mapData.put("福建省", "14");
//		mapData.put("江西省", "15");
//		mapData.put("山东省", "16");
//		mapData.put("河南省", "17");
//		mapData.put("湖北省", "18");
//		mapData.put("湖南省", "19");
//		mapData.put("广东省", "20");
//
//		mapData.put("广西省", "21");
//
//		mapData.put("海南省", "22");
//		mapData.put("四川省", "23");
//		mapData.put("贵州省", "24");
//		mapData.put("云南省", "25");
//		mapData.put("西藏", "26");
//
//		mapData.put("陕西省", "27");
//		mapData.put("甘肃省", "28");
//		mapData.put("青海省", "29");
//		mapData.put("宁夏", "30");
//
//
//		mapData.put("新疆", "31");
//
//		Map<String,String> city =null;
//		Map<String,String> county = null;
//
//		String parentCode  = "100000"; // 母节点
//		Map<String , String>  curCode = null; // 子节点
//
//		Db db =  new Db();
//		db.Intdb();
//
//
//		for (Map.Entry<String, String> entry : mapData.entrySet()){
//			String key = entry.getKey();
//			String code = entry.getValue();
//			parentCode = "100000";
//					// 省级查询
//			key = key.replaceAll("省", "");
//			curCode  =db.getCodeByName(key,parentCode);
//
//			if(curCode ==null){
//				System.out.print("没有查找到" + curCode);
//
//			}else{
//
//				db.Insert_district_bank(curCode ,code,"1");
//			}
//
//
//		    String result = 	getLocal2(code, "city", "2");
//		    city=getColdeByResult(result); // 城市等级的 集合值
//
//
//
//			 String cityPrentId = db.getMapvalue(curCode);
//			// 县城一级获取， 城市一级的输出
//			for(Map.Entry<String, String> cityEntity : city.entrySet()){
//
//
//				logger.info(cityEntity.getKey() + "  " +  cityEntity.getValue() );
//
//				key = cityEntity.getKey().replaceAll("市", "");
//				key = key.replaceAll("县", "");
//				key = key.replaceAll("地区", "");
//				key = key.replaceAll("区", "");
//
//				key = key.replaceAll("州", "");
//
//				curCode  =db.getCodeByName(key, cityPrentId);
//				parentCode = db.getMapvalue(curCode);
//				if(curCode ==null){
//					logger.info("没有查找到" + cityPrentId +  cityEntity.getKey() );
//
//				}else{
//
//					db.Insert_district_bank(curCode ,cityEntity.getValue(),"1");
//				}
//
//
//
//
//
//				result  = 	getLocal2(cityEntity.getValue(), "county", "2");
//				county=getColdeByResult(result);
//                  String countyParentId = db.getMapvalue(curCode);
//				// 县城一级的输出
//				for(Map.Entry<String, String> countyEntity : county.entrySet()){
//
//					logger.info(countyEntity.getKey() + "  " +  countyEntity.getValue() );
//
//					key = countyEntity.getKey().replaceAll("市", "");
//					key = key.replaceAll("县", "");
//					key = key.replaceAll("地区", "");
//					key = key.replaceAll("区", "");
//					curCode  =db.getCodeByName(key,countyParentId);
//					parentCode = db.getMapvalue(curCode);
//					if(curCode ==null){
//						logger.info("没有查找到" + countyParentId +  countyEntity.getKey());
//
//					}else{
//
//						db.Insert_district_bank(curCode ,countyEntity.getValue(),"1");
//					}
//				}
//
//
//
//			}
//
//
//
//
//
//		}
//
//
//
//	}
	public String getLocal2(String pId, String Area,String type){
		String  url = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/getArea.jhtml";// 获取地址url
		Map<String,String> mapData =  setAreaData(Area, pId,type);
		CloseableHttpClient postClient = NetHttp.getHttpClient(myCookies);
		String result= NetHttp.doPost(url,postClient, Header,mapData);

	//	logger.info(result);

		return result;

	}
	public void getdiqu(String resulit){
		resulit = resulit.replaceAll("\\\\","");
		resulit = resulit.replaceAll("/","");
		resulit = resulit.replaceAll(" ","");
		resulit = resulit.replaceAll("","");
			logger.info(resulit);
		String key ="<optionvalue=([\\S\\s]*?)>([\\S\\s]*?)<option>";
		Pattern pat=Pattern.compile(key);
		Matcher matcher=pat.matcher(resulit);
		while (matcher.find()) {
			logger.info(matcher.group(1) + "  " + matcher.group(2));

			String pid =    matcher.group(1).replaceAll("\"","");
			    	logger.info("pid:" + pid);
			if( !"-1".equals(pid)){
				String a = getLocal2(pid, "county", "1");

				a = a.replaceAll("\\\\","");
				a = a.replaceAll("/","");
				a = a.replaceAll(" ","");
				a = a.replaceAll("","");
				logger.info("第三级" + a);

			}



		}


	}

	public Map<String, String> getColdeByResult(String result){

		result = result.replaceAll("\\\\","");
		result = result.replaceAll("/","");
		result = result.replaceAll(" ","");
		result = result.replaceAll("","");
	//	logger.info(result);
		String key ="<optionvalue=([\\S\\s]*?)>([\\S\\s]*?)<option>";
		Pattern pat=Pattern.compile(key);
		Matcher matcher=pat.matcher(result);
		Map<String, String> mapData = new HashMap<>();

		while (matcher.find()) {
		//	logger.info(matcher.group(1) + "  " + matcher.group(2));

			String pid =    matcher.group(1).replaceAll("\"","");
		//	logger.info("pid:" + pid);
			if( !"-1".equals(pid)){
				mapData.put(matcher.group(2),pid ); // 保存下来
			}

		}

		return  mapData;


	}


	//////

	//////////
	public void CardType(){
		Map<String,String> mapData = new HashMap<>();
		mapData.put("6", "民生in卡信用卡");
		mapData.put("24", "民生车车信用卡");
		mapData.put("32", "民生VISA全币种信用卡");
		mapData.put("15", "民生香格里拉联名信用卡");
		mapData.put("25", "民生全币种白金信用卡");
		mapData.put("11", "民生国航知音联名信用卡");
		mapData.put("14", "民生南航明珠联名信用卡");
		mapData.put("12", "民生东航联名信用卡");
		mapData.put("30", "民生海航联名信用卡");
		mapData.put("3", "民生女人花信用卡");
		mapData.put("7", "民生欢乐信用卡");
		mapData.put("2", "民生标准信用卡");
		mapData.put("16", "民生太平洋远东百货联名信用卡");
		mapData.put("29", "民生汉神百货联名信用卡");
		mapData.put("34", "民生合胜联名信用卡");

		for(Map.Entry<String, String> entry : mapData.entrySet()){
			String id = "6";//entry.getKey();
			String cardStr ="民生车车信用卡";// entry.getValue();
			InitHead(); // 初始化post头
			setCardIdUrl();// 获取身份验证
			String page =  getPage(urlCheckIdentity);
			getCheckIdentityPage(page); // 加密身份证Id
			NetHttp.getCode(myCookies,urlCode );  //获取验证码
			String verifycode = SystemInput("请输入验证码：");
			logger.info("验证码：" + verifycode);
			minshengBean.setVerifycode(verifycode); //输入验证码
			/**提交身份验证**/
			String   result   =   PostPage(urlCheckIdPost,SetIdentity());
			/**获取礼物页面**/
			page = getPage(urlgift);
			logger.info("卡类型" + id);
			logger.info("礼物页面：\n"+ page);
		}

	}
	////////图片验证 第一步/////
	public BufferedImage getVerifyCode(MaterialBean bean, MemCachedClient client) throws IOException{
		logger.info("获取图片验证码：");
		MaterialModel model = bean.getModel();
		//首先渠道链接

		String  url = "https://creditcard.cmbc.com.cn/wsonline/recommend/managerhome.jhtml?recommendI" +
				"nfo=q5lBPSejCTdPXFLO%2bgoYUgRuhtYJ%2b75I79BE3ThkvBx4z59%2b7%2f02mbr6%2f1WLiMw%2fEp3465" +
				"7QysOx39xTbMFq9rfCpBvA4%2bwvH8U%2bw3%2bszAa7bXKR8PQtP%2bTko5ohJo%2fO26ryxq87R8NI4GqOXdsiz" +
				"FYHb3nL%2bhUYoLH588PUyWGMelWQTheUcDFQnU8Gsa3CNcIeeTGMfTv2FSTdQgJDWse4nkNDQlqdWuQzt9tPwzGEas9U" +
				"pqJOUW5uafrouJrqbEPxAf3%2bAW0WQs8jKesrmWHjP9t6qUlbUiTJAjVa7xb%2fMM215fSE3mHt%2fpVURY2F2LLBCeUe4" +
				"pBByrjrs42r5g%3d%3d";
		InitHead();
		getPage(url);
		url = "https://creditcard.cmbc.com.cn/wsonline/index/index.jhtml";
		getPage(url);
		byte[] imgByte  = NetHttp.getCode(myCookies,urlCode );  //获取验证码 ;
		ByteArrayInputStream ms_bin = new ByteArrayInputStream(imgByte);
		BufferedImage   localBufferedImage = ImageIO.read(ms_bin);
		client.set(model.getCardid() + "mingsheng_cookie", myCookies, Constants.TIME_HOUR);

		return localBufferedImage;
	}
	/////手机验证  第二步/////
	public int getPhoneCode(MaterialBean bean,MemCachedClient client){
		/**首先获取维护的myCookies*/
		logger.info("第二步获取手机验证码 增加 返回值：");
		MaterialModel model = bean.getModel();
		myCookies= (CookieStore)client.get(model.getCardid() +"mingsheng_cookie");
	//	logger.info("输出cook：  ----------------------------1"+myCookies);

		InitHead();//
		boolean res = false;
		logger.info("开始转换数据");
		transformData1(bean,client);// 转换 资料
		res = IdCardPage(); // 身份证验证

		if(res == true){


			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.img_code);


		}
		else{

			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc(errorResult);
			bean.setBusiJSON("fail");

			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,urlCheckIdPost,0,"身份提交失败" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.img_code, errorBean);
			return 0;
		}
		res = giftPage();// 礼物页面

		if(res == true){


			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.img_code);
		}
		else{

			bean.setBusiErrCode(-1);
			//礼物提交失败
			bean.setBusiErrDesc(errorResult);
			bean.setBusiJSON("fail");

			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,urlgiftPost,1,"礼物提交失败" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.img_code, errorBean);

			return 0;
		}
		res = personalInfoPage(bean,client);// 个人资料提交
		if(res == true){
			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.img_code);
		}
		else{
		//	logger.info("个人资料提交结果失败" );
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc(errorResult);
			bean.setBusiJSON("fail");


			String url ="https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo1Save.jhtml";
			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,2,"个人资料" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.img_code, errorBean);

			return 0;
		}
		// 公司页面


		res = companyPage(); // 提交公司页面
		if(res== false){
			//logger.info("提交公司资料失败");
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("公司资料有误");
			bean.setBusiJSON("fail");
			String url ="https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2Save.jhtml";
			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,3,"公司资料" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.img_code, errorBean);

			return 0;
		}else{
			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.img_code);
		}

		res = linkManPage();
		if(res == false){
			logger.info("联系人提交失败    ");
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("联系人资料有误");



			String url ="https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo3Save.jhtml";
			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,4,"联系人提交失败" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.img_code, errorBean);
			bean.setBusiJSON("fail");
		}else{
			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.img_code);
		}
		String  page = getPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo4.jhtml");
		getSubmitInfo(page);

		String	result = PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendSMS.jhtml",getMobilephoneData());
		if(result.contains("success")){
			logger.info("发送短信成功");
			bean.setBusiErrCode(1);
			bean.setBusiErrDesc("短信发送成功");
			bean.setBusiJSON("fail");
			String jsonStr = JSON.toJSONString(minshengBean);
			client.set(model.getCardid() + "mingsheng_sub", jsonStr);
			logger.info("存入mingsheng_cookie");
			client.set(model.getCardid() + "mingsheng_cookie", myCookies, Constants.TIME_HOUR);


			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.phone_code);
			return 1;
		}
		errorResult = result;
		String url ="https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendSMS.jhtml";
		ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,5,"手机验证码失败" ,bean.getMobileNo());
		BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.phone_code, errorBean);

		bean.setBusiErrCode(0);
		bean.setBusiErrDesc("短信发送失败");
		bean.setBusiJSON("fail");
		logger.info("发送短信 失败");

		return 0;
	}
	/////最后的提交  最后一步/////
	public int submit(MaterialBean bean,MemCachedClient client){
		MaterialModel model = bean.getModel();
		myCookies= (CookieStore)client.get(model.getCardid() +"mingsheng_cookie");
	//	logger.info("输出cook：  ----------------------------2"+myCookies);
		String jsonStr = (String)client.get(model.getCardid() + "mingsheng_sub");
		minshengBean = JSON.parseObject(jsonStr, MingshengBean.class);
		int res = 0;
		//填写手机验证码

		String phonecode = bean.getPhoneauthcode(); // 手机验证码

		minshengBean.setMcode(phonecode);//	手机验证码
		InitHead();//
	    String 	result  =confirmPage();
		errorResult= result;
		if(result== null){
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("系统繁忙，请稍后再试");
			bean.setBusiJSON("fail");

		//	ErrorRequestBean errorBean   = new ErrorRequestBean(bean,"获取确认页面失败" );
			String url = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo4Save.jhtml";
			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,6,"获取确认页面失败" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.submit_apply, errorBean);
			return  0;

		}
		if(result.contains("短信验证失败")){
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("短信验证失败");
			bean.setBusiJSON("fail");
			logger.info("提交失败");


			String url = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendWechat.jhtml";
			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,7,"短信验证码" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.submit_apply, errorBean);

			return  0;
		}
		if(result.contains("success")){
			bean.setBusiErrCode(1);
			bean.setBusiErrDesc("成功提交");
			bean.setBusiJSON("fail");
			logger.info("提交成功");
			res = 1;

			BankApplyListener.sendSucess(BankEnum.minsheng, BankApplyStepEnum.submit_apply);
			return  1;
		}else{
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("信息提交失败");
			bean.setBusiJSON("fail");
		//	logger.info("信息提交失败");
		//	ErrorRequestBean errorBean   = new ErrorRequestBean(bean,"确认信息提交失败" );
			String url = "https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendWechat.jhtml";
			ErrorRequestBean errorBean   = new ErrorRequestBean(System.currentTimeMillis()+".html",bean,errorResult,url,8,"确认信息提交失败" ,bean.getMobileNo());
			BankApplyListener.sendError(BankEnum.minsheng, BankApplyStepEnum.submit_apply, errorBean);
			res = 0;
			return  0;

		}


	}
	// 公司页面提交
	public boolean companyPage(){
		String  page =getPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2.jhtml");
		getCompanyInfo(page); //获取公司页面 字段
		String   result = PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo2Save.jhtml",getCompanyData());
		//logger.info("公司提交结果 result:" +result );
		if(result.contains("success")){
			return true;
		}
		errorResult = result;
		return false;
	}
	// 确认提交页面
	public String confirmPage() {
		String page = null;
		String result = null;

		result = PostPage(
				"https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo4Save.jhtml",
				getsubmitData());
//		logger.info(" 确认页面result:" + result);
		if(result==null){
			return null;

		}
		if (!result.contains("success")) {
			return result;
		}
		result = PostPage(
				"https://creditcard.cmbc.com.cn/wsonline/onlineapplication/sendWechat.jhtml",
				getUserNameData());
		if (!result.contains("success")) {
			return result;
		}
//		logger.info("提交用户名：" + result);
		return result;
	}
	// 联系人页面提交
	public boolean linkManPage(){
		String  page =getPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo3.jhtml");
		/**提交联系人资料页面**/
		getLinkManpage(page);
		String result = PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo3Save.jhtml",getLinkManData());
		logger.info("提交联系人结果：" + result);
		if(result.contains("success")){
			return true;
		}
		errorResult = result;
		return false;
	}
	// 身份证 验证
	public boolean IdCardPage(){
		boolean res = false;
		setCardIdUrl();// 获取身份验证
		String page =  getPage(urlCheckIdentity);
		//	logger.info("page:/n" + page);
		res = getCheckIdentityPage(page); // 加密身份证Id
		if(res == false){

			return res;
		}
		/**提交身份验证**/
		String   result   =   PostPage(urlCheckIdPost,SetIdentity());

		logger.info("身份证提交结果：" + result);
		if(result.contains("success")){
			res = true;
		}else{
			res = false;
			errorResult = result;
		}
		return res;
	}
	// 礼物页面
	public boolean giftPage(){
		boolean res = false;
		String page = getPage(urlgift);
		//	logger.info("礼物页面：\n" + page);

		getgiftInfo(page);

		/**提交礼物页面**/
		String   result = PostPage(urlgiftPost,setapplydata());
		if(result.contains("success")){
			res = true;
		}
		errorResult = result;

		return res;

	}
	// 个人资料
	public boolean personalInfoPage(MaterialBean bean,MemCachedClient client){
		boolean res = false;
		String page = getPage(urlUserInfo);
		getPersonalPage(page);

		transformData2(bean,client);
		/**提交个人资料**/
		String   result =PostPage("https://creditcard.cmbc.com.cn/wsonline/onlineapplication/userInfo1Save.jhtml",getPersonal());
		if(result.contains("success")){
			res= true;
		}
		errorResult = result;

		return res;
	}



	public void  transformData1(MaterialBean bean,MemCachedClient client){
		// 数据模型
		MaterialModel model = bean.getModel();
		// 首先卡类型
		//logger.info("minShenCardType_"+bean.getModel().getCidcard());

		String cardtypedata =(String)client.get("minShenCardType_"+bean.getModel().getCidcard());

		if(cardtypedata==null){
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("要办的卡不存在");
			bean.setBusiJSON("fail");
		}
//		logger.info("要办的卡：" + cardtypedata);


		String cardData[]= cardtypedata.split("-");


		for(int i = 0; i < cardData.length; i ++){
			logger.info( "卡类型："+cardData[i]);
		}
		// "CardType-卡code-hidType"
		String CardType ="6" ;//cardData[0];// 卡类型
		String cardLeve ="91";// cardData[1]; // 卡等级
		String hidType  = "5";//cardData[2];
		String Identitycode = model.getCidcard();

		minshengBean.setCardType(CardType);
		minshengBean.setIdentitycode(Identitycode);
		minshengBean.setCardColor(cardLeve);

		minshengBean.setGiftId("0066"); // 主要两个礼物Id0066   0054
		minshengBean.setHidType(hidType);
		// 图片验证码
		String   verifycode = bean.getImgauthcode();
//		logger.info("图片验证码：" + verifycode);
		minshengBean.setVerifycode(verifycode);
		// 开始设置个人资料
		String realName = model.getCname();//真实 姓名
		String englishName[] =model.getCenglishname().split(" ");
		String pinYInName1 = englishName[0];

		String pinYInName2 = "";
		for(int i = 1; i < englishName.length; i++){
			pinYInName2 =pinYInName2 +englishName[i];
		}
//		logger.info("名字：" + realName);
//		logger.info("名字拼音：" + pinYInName1 + pinYInName2);

		minshengBean.setRiCname(realName);
		minshengBean.setRiPinYInName1(pinYInName1);
		minshengBean.setRiPinYInName2(pinYInName2);


	}
	public void getPersonalPage(String html){
		Document doc=Jsoup.parse(html);
		String HidYj="";
		String HidCardID ="";
		String HidID = "";
		String HidAT = "";


		String HidBackFlag = "";
		String hidCookieId = "";
		if(html.contains("HidYj")){
			HidYj= doc.getElementById("HidYj").toString();
			if(HidYj.contains("value")){
				HidYj = HidYj.substring(HidYj.indexOf("value=\"") + "value=\"".length() ,HidYj.length() );
				HidYj = HidYj.substring( 0,HidYj.indexOf("\"") );
				logger.info("HidYj:" + HidYj);
				minshengBean.setHidyj(HidYj);

			}
		}

		if(html.contains("HidCardID")){
			HidCardID= doc.getElementById("HidCardID").toString();
			if(HidCardID.contains("value")){
				HidCardID = HidCardID.substring(HidCardID.indexOf("value=\"") + "value=\"".length() ,HidCardID.length() );
				HidCardID = HidCardID.substring( 0,HidCardID.indexOf("\"") );
				logger.info("HidCardID:" + HidCardID);
				minshengBean.setHidCardID(HidCardID);

			}
		}

		if(html.contains("HidID")){
			HidID= doc.getElementById("HidID").toString();
			if(HidID.contains("value")){
				HidID = HidID.substring(HidID.indexOf("value=\"") + "value=\"".length() ,HidID.length() );
				HidID = HidID.substring( 0,HidID.indexOf("\"") );
				logger.info("HidID:" + HidID);
				minshengBean.setHidID(HidID);

			}
		}

		if(html.contains("HidAT")){
			HidAT= doc.getElementById("HidAT").toString();
			if(HidAT.contains("value")){
				HidAT = HidAT.substring(HidAT.indexOf("value=\"") + "value=\"".length() ,HidAT.length() );
				HidAT = HidAT.substring( 0,HidAT.indexOf("\"") );
				logger.info("HidAT:" + HidAT);
				minshengBean.setHidAT(HidAT);

			}
		}
		if(html.contains("HidBackFlag")){
			HidBackFlag= doc.getElementById("HidBackFlag").toString();
			if(HidBackFlag.contains("value")){
				HidBackFlag = HidBackFlag.substring(HidBackFlag.indexOf("value=\"") + "value=\"".length() ,HidBackFlag.length() );
				HidBackFlag = HidBackFlag.substring( 0,HidBackFlag.indexOf("\"") );
				logger.info("HidBackFlag:" + HidBackFlag);
				minshengBean.setHidBackFlag(HidBackFlag);

			}
		}
		if(html.contains("hidCookieId")){
			hidCookieId= doc.getElementById("hidCookieId").toString();
			if(hidCookieId.contains("value")){
				hidCookieId = hidCookieId.substring(hidCookieId.indexOf("value=\"") + "value=\"".length() ,hidCookieId.length() );
				hidCookieId = hidCookieId.substring( 0,hidCookieId.indexOf("\"") );
				logger.info("hidCookieId:" + hidCookieId);
				minshengBean.setHidCookieId(hidCookieId);

			}
		}

	}

	public void  transformData2(MaterialBean bean,MemCachedClient client){
		// 数据模型
		MaterialModel model = bean.getModel();
		// 首先卡类型
		String realName = minshengBean.getRiCname();

		//身份证   第二种地图类型、
		logger.info("地址类型：" + model.getCidcard());
		String idProvinceName =(String)client.get("minShengIdP_"+model.getCidcard());
		String  pcode =( String)client.get("minShengIdP_1"+model.getCidcard()); // 省份Id

		String idcityName = (String)client.get( "minShengIdC_" + model.getCidcard());

		String cCode = (String)client.get( "minShengIdC_1" + model.getCidcard());

		String idDestName = (String)client.get("minShengIdD_"+model.getCidcard());
		String dCode =  (String)client.get("minShengIdD_1"+model.getCidcard());

		minshengBean.setProvinceName(idProvinceName);// 身份证 省份
		minshengBean.setProvince(pcode);
		minshengBean.setIdnumName(idcityName);
		minshengBean.setIdnumCode1(cCode);
		minshengBean.setSubIdnumName(idDestName);
		minshengBean.setIdnumCode(dCode);

		minshengBean.setIdnumStartdate("");
		minshengBean.setDate_now("2015-09-25");
		minshengBean.setIdnumEnddate("");
		// 地址 家庭地址
		String homeP=(String)client.get("minShengHomeP_"+model.getCidcard());
		String homePcode2 =  (String)client.get("minShengHomeP_1"+model.getCidcard());
		String homePcode1 =  (String)client.get("minShengHomeP_0"+model.getCidcard());
		String homeC=(String)client.get("minShengHomeC_"+model.getCidcard());
		String homeCcode2 = (String)client.get("minShengHomeC_1"+model.getCidcard());
		String homeCcode1 =(String)client.get("minShengHomeC_0"+model.getCidcard());
		String homeD=(String)client.get("minShengHomeD_"+model.getCidcard());
		String homeDcode2 = (String)client.get("minShengHomeD_1"+model.getCidcard());
		String homeDcode1 = (String)client.get("minShengHomeD_0"+model.getCidcard());
		String homeDetail= model.getChome_detailaddress();// 家庭详细地址
		String homePostcode = model.getChome_postcode();
		//公司地址
		String jobP =(String)client.get("minShengJobP_" + model.getCidcard());
		String jobPcode2 =  (String)client.get("minShengJobP_1" + model.getCidcard());
		String jobPcode1 =  (String)client.get("minShengJobP_0" + model.getCidcard());
		String jobC =(String)client.get("minShengJobC_"+model.getCidcard());
		String jobCCcode2 = (String)client.get("minShengJobC_1"+model.getCidcard());
		String jobCCcode1 = (String)client.get("minShengJobC_0"+model.getCidcard());
		String jobD =(String)client.get("minShengJobD_"+model.getCidcard());
		String jobDcode2 = (String)client.get("minShengJobD_1"+model.getCidcard());
		String jobDcode1 =(String)client.get("minShengJobD_0"+model.getCidcard());
		String jobDetail = model.getCcompany_detailaddress();
		String jobPostcode = model.getCcompany_postcode();

		//提交单位地址
		String deliveryType = model.getIpostaddress();// 提交地址  1 单位地址2 家庭地址
		if("1".equals(deliveryType)){
			minshengBean.setDeliveryAddress("B");
		}
		if("2".equals(deliveryType)){
			minshengBean.setDeliveryAddress("H");
		}
		//地址1 类型
		minshengBean.setCycProvince1(homeP);
		minshengBean.setCycProvinceCode1(homePcode1);

		minshengBean.setCycCity1(homeC);
		minshengBean.setCycCityCode1(homeCcode1);

		minshengBean.setCycCounty1(homeD);
		minshengBean.setCycCountyCode1(homeDcode1);
		//地址2 类型

		minshengBean.setCycProvince2(homeP);
		minshengBean.setCycProvinceCode2(homePcode2);

		minshengBean.setCycCity2(homeC);
		minshengBean.setCycCityCode2(homeCcode2);

		minshengBean.setCycCounty2(homeD);
		minshengBean.setCycCountyCode2(homeDcode2);
		// 家庭详细地址
		minshengBean.setCycDetail1(homeDetail);
		minshengBean.setPost1(homePostcode);
		// 单位地址
		minshengBean.setCycProvince(jobP);
		minshengBean.setCycProvinceCode(jobPcode1);

		minshengBean.setCycCity(jobC);
		minshengBean.setCycCityCode(jobCCcode1);

		minshengBean.setCycCounty(jobD);
		minshengBean.setCycCountyCode(jobDcode1);

		minshengBean.setCycProvince1Code(jobPcode2);
		minshengBean.setCycCity1Code(jobCCcode2);
		minshengBean.setCycCounty1Code(jobDcode2);

		// 单位详细地址
		minshengBean.setCycDetail(jobDetail);
		minshengBean.setPost(jobPostcode);



		String chinaName1 =realName.substring(0,1);
		String chinaName2 =realName.substring(1,realName.length() );
		minshengBean.setHidName1(chinaName1);
		minshengBean.setHidName2(chinaName2);
		minshengBean.setHidTitle("个人资料");
		minshengBean.setHidBackUrl("/wsonline/login/success.jhtml");


		//  公司资料
		String companyName = model.getCcompanyname(); // 公司名字
		String allCompanyPhone[] = model.getCcompany_telnum().split("-");
		String companyPhoneAare= "";// 公司电话
		String CompanyPhone="";
		String CompanyPhoneExt=""; // 分机号
		for(int i = 0; i <allCompanyPhone.length; i++){
			if(i==0){
				companyPhoneAare =allCompanyPhone[0];
			}
			if(i== 1){
				CompanyPhone = allCompanyPhone[1];
			}
			if(i == 2){
				CompanyPhoneExt = allCompanyPhone[2];
			}
		}

		minshengBean.setCompName(companyName);//
		minshengBean.setCompanyPhoneAare(companyPhoneAare);//	021
		minshengBean.setCompanyPhone(CompanyPhone);//
		minshengBean.setCompanyPhoneExt(CompanyPhoneExt);//
		minshengBean.setCompPhone("");//

		String workLeve = model.getItimeinjob(); // //任职年数：1、一年以下 2、一年 3、两年 4、三年 5、四年 6、五年及以上
		String workDate="";
		switch(workLeve){
			case "1":
				workDate = "1";
				break;
			case "2":
				workDate = "1";
				break;
			case "3":
				workDate = "2";
				break;
			case "4":
				workDate = "3";
				break;
			case "5":
				workDate = "4";
				break;
			case "6":
				workDate = "5";
				break;
		}

		minshengBean.setWorkDate(workDate);//	工作年限
		// 婚姻
		String marstatus = model.getMaritalstatus();//maritalstatus;//1、未婚 2、已婚 3、其它
		switch(marstatus){
			case "1":
				minshengBean.setMARST("S");//	
				break;
			case "2":
				minshengBean.setMARST("M");//	
				break;
			case "3":
				minshengBean.setMARST("O");//	
				break;

		}


		// 教育程度   idegree;//1、博士及以上 2、硕士 3、本科 4、大专 5、高中、中专一下
		String idegree = model.getIdegree();
		switch(idegree){
			case "1":
				minshengBean.setEducation("6");
				break;
			case "2":
				minshengBean.setEducation("5");
				break;
			case "3":
				minshengBean.setEducation("4");
				break;
			case "4":
				minshengBean.setEducation("3");
				break;
			case "5":
				minshengBean.setEducation("2");
				break;
		}
		String mobilephone = model.getCphone(); // 手机
		//    String phonecode = bean.getPhoneauthcode(); // 手机验证码
		String mail = model.getCemail();//email 邮箱


		minshengBean.setMobilephone(mobilephone);//	15821129261

		minshengBean.setEmail(mail);//
		minshengBean.setCycQQ("");//  QQ号码可以为空

		// 住宅电话直接空了
		minshengBean.setHomezonephone("");//
		minshengBean.setHomephone("");//

		minshengBean.setHidMcode("");//	验证码
		minshengBean.setHidAT("");//
		//	minshengBean.setHidCityArea("010,020,021,022,023,024,025,027,028,029,0310,0311,0312,0313,0314,0315,0316,0317,0318,0319,0335,0350,0351,0352,0353,0349,0354,0355,0356,0357,0358,0359,0370,0371,0372,0373,0374,0375,0376,0377,0378,0379,0391,0392,0393,0394,0395,0396,0398,0410,0411,0412,0413,0414,0415,0416,0417,0418,0419,0421,0427,0429,0431,0432,0433,0434,0435,0436,0437,0438,0439,0440,0451,0452,0453,0454,0455,0456,0457,0458,0459,0464,0467,0468,0469,0470,0471,0472,0473,0474,0475,0476,0477,0478,0483,0479,0482,0510,0511,0512,0513,0514,0515,0516,0517,0518,0519,0523,0527,0530,0531,0532,0533,0632,0543,0635,0633,0634,0534,0535,0536,0537,0538,0539,0546,0631,0550,0551,0552,0553,0554,0555,0556,0559,0557,0558,0566,0561,0562,0563,0564,0565,0570,0571,0572,0573,0574,0575,0576,0577,0578,0579,0580,0591,0592,0593,0594,0595,0596,0597,0598,0599,0660,0662,0663,0668,0750,0751,0752,0753,0754,0755,0756,0757,0758,0759,0760,0762,0763,0766,0768,0769,0710,0711,0712,0713,0714,0715,0716,0717,0718,0719,0722,0724,0728,0730,0731,0732,0733,0734,0735,0736,0737,0738,0739,0743,0744,0745,0746,0770,0771,0772,0773,0774,0775,0776,0777,0778,0779,0790,0791,0792,0793,0794,0795,0796,0797,0798,0799,0701,0812,0813,0816,0817,0818,0825,0826,0827,0830,0831,0832,0833,0834,0835,0836,0837,0838,0839,0851,0852,0853,0854,0855,0856,0857,0858,0859,0870,0691,0692,0871,0872,0873,0874,0875,0876,0877,0878,0879,0883,0886,0887,0888,0891,0892,0893,0894,0895,0896,0897,0898,0899,0890,0911,0912,0913,0914,0915,0916,0917,0919,0930,0931,0932,0933,0934,0935,0936,0937,0938,0941,0943,0951,0952,0953,0954,0955,0970,0971,0972,0973,0974,0975,0976,0977,0991,0990,0995,0902,0994,0909,0996,0997,0998,0903,0999,0993,0906,0901");//	010,020,021,022,023,024,025,027,028,029,0310,0311,0312,0313,0314,0315,0316,0317,0318,0319,0335,0350,0351,0352,0353,0349,0354,0355,0356,0357,0358,0359,0370,0371,0372,0373,0374,0375,0376,0377,0378,0379,0391,0392,0393,0394,0395,0396,0398,0410,0411,0412,0413,0414,0415,0416,0417,0418,0419,0421,0427,0429,0431,0432,0433,0434,0435,0436,0437,0438,0439,0440,0451,0452,0453,0454,0455,0456,0457,0458,0459,0464,0467,0468,0469,0470,0471,0472,0473,0474,0475,0476,0477,0478,0483,0479,0482,0510,0511,0512,0513,0514,0515,0516,0517,0518,0519,0523,0527,0530,0531,0532,0533,0632,0543,0635,0633,0634,0534,0535,0536,0537,0538,0539,0546,0631,0550,0551,0552,0553,0554,0555,0556,0559,0557,0558,0566,0561,0562,0563,0564,0565,0570,0571,0572,0573,0574,0575,0576,0577,0578,0579,0580,0591,0592,0593,0594,0595,0596,0597,0598,0599,0660,0662,0663,0668,0750,0751,0752,0753,0754,0755,0756,0757,0758,0759,0760,0762,0763,0766,0768,0769,0710,0711,0712,0713,0714,0715,0716,0717,0718,0719,0722,0724,0728,0730,0731,0732,0733,0734,0735,0736,0737,0738,0739,0743,0744,0745,0746,0770,0771,0772,0773,0774,0775,0776,0777,0778,0779,0790,0791,0792,0793,0794,0795,0796,0797,0798,0799,0701,0812,0813,0816,0817,0818,0825,0826,0827,0830,0831,0832,0833,0834,0835,0836,0837,0838,0839,0851,0852,0853,0854,0855,0856,0857,0858,0859,0870,0691,0692,0871,0872,0873,0874,0875,0876,0877,0878,0879,0883,0886,0887,0888,0891,0892,0893,0894,0895,0896,0897,0898,0899,0890,0911,0912,0913,0914,0915,0916,0917,0919,0930,0931,0932,0933,0934,0935,0936,0937,0938,0941,0943,0951,0952,0953,0954,0955,0970,0971,0972,0973,0974,0975,0976,0977,0991,0990,0995,0902,0994,0909,0996,0997,0998,0903,0999,0993,0906,0901
		minshengBean.setHidTitle("个人资料");//	个人资料
		//	minshengBean.setHidBackUrl("wsonline/onlineapplication/userInfo1.jhtml");//	/
		//	minshengBean.setHidBackFlag("1");//	1 


		/**提交联系人 资料**/
		String familyname = model.getFamilyname();
		String familynameMobile1 =model.getCfamilyphonenum();
		minshengBean.setFamilymemName1(familyname);	//赵杰
		minshengBean.setFamilymemMobile1(familynameMobile1);

		String familyship=model.getIfamilyties();
		switch(familyship){
			case "1":
				minshengBean.setFamilymemShip1("1");	//	
				minshengBean.setFamilymemShip1Name("配偶");	//
				break;
			case "2":
				minshengBean.setFamilymemShip1("2");	//	
				minshengBean.setFamilymemShip1Name("父母");	//
				break;
			case "3":
				minshengBean.setFamilymemShip1("3");	//	
				minshengBean.setFamilymemShip1Name("子女");	//
				break;
			case "4":
				minshengBean.setFamilymemShip1("4");	//	
				minshengBean.setFamilymemShip1Name("其他");	//
				break;
		}

		/**紧急联系人**/
		String LCustName = model.getCemergencycontactname();
		String LCustPhone = model.getCemergencyphone();

		minshengBean.setLCustName(LCustName);	//	周伟新
		minshengBean.setLCustPhone(LCustPhone);	//	13824489127
		minshengBean.setLCustShip("1");	//	3
		minshengBean.setlCustShipName("朋友");	//	同事


		//minshengBean.setHidAT("");	//
		//	minshengBean.setHidCardID("");	//	27

		minshengBean.setCompanyPhone("4006739188");	//	4006739188
		minshengBean.setAppName(realName);	//	邓宏
		minshengBean.setHidJuniorMessType("");	//
		//	minshengBean.setHidTitle("");	//	联系人资料
		//	minshengBean.setHidBackUrl("");	//	/wsonline/onlineapplication/userInfo2.jhtml
		//	minshengBean.setHidBackFlag("");	//	1
		//	minshengBean.setHidActiveUrl("");	//	

		/////////////确认提交//////////////////


		minshengBean.setCycCounty1(homeD);//		徐汇区

		////提交完成///
		minshengBean.setUserName(realName);//	李波
		minshengBean.setOpenId("");//
		minshengBean.setRecommendId("");//


	}

}
