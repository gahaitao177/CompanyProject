package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;

import java.util.HashMap;

/**
 * 模拟提交接口测试
 * @author Administrator
 *
 */
public class TestJson {

//	static String host = "http://hsk.gs.9188.com";
	static String host = "http://localhost:8081";
	static String bankCheckCode = host+"/credit/apply_bankVerifyCode.go";
	static String bankMessage = host+"/credit/apply_bankMessage.go";
	
	static String apply_CreditCard = host+"/credit/apply_CreditCard.go";
//	static String apply_CreditCard = "http://hsk.gs.9188.com/credit/apply_CreditCard.go";
	public static void main(String[] args) throws Exception {
//		bankCheckCode();
//		
//		 BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));  
//         String code=strin.readLine();
//         getMessageCode(code);
//          strin=new BufferedReader(new InputStreamReader(System.in));  
//          code=strin.readLine();
//          applyCreditCard(code);
         System.out.println(CaiyiEncrypt.encryptStr("18717861758"));
	}
	
	/**
	 * 测试
	 * @throws Exception
	 */
	public static void bankCheckCode() throws Exception{
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("ibankid", "10"); 
		params.put("cphone", "18717861758");
		params.put("imgauthcode", "1245"); 
		params.put("orderid", "123");
		
		JSONObject json = new JSONObject();
		json.put("cardid", "3587");
		json.put("idExpirationTime", "20101002,20201002");		
		json.put("name", "刘伟国"); 
		json.put("englishName", "LIU WEI GUO");
		json.put("maritalStatus", "1");		
		json.put("userPhoneNum", "18717861758");
		json.put("degree", "3");//卡Id
//		json.put("homeAddress", "1-101-1");
//		json.put("companyAddress", "2-101-2");
		json.put("homeDetailAddress", "平安路长安大道100号"); //详细地址
		json.put("homePostcode", "450000"); //编码
		json.put("natureOfUnit", "3");  
		json.put("natureOfBusiness", "2");//行业性质
		json.put("cardID", "110101198706010096");
		
		json.put("company", "上海大河工信技术有限公司"); //详细地址
		json.put("companyDetailAddress", "上海市浦东新区世纪大道100号"); // 编码
		json.put("annualSalary", "15");
		json.put("natureOfBusiness", "2");// 行业性质 natureOfUnit
		json.put("natureOfUnit", "2");
		json.put("departmentName", "技术");

		json.put("post", "2");// 行业性质 natureOfUnit
//		json.put("email", "2565656@qq.com");
		
		json.put("emergencyContactName", "李四");
		json.put("familyTies", "1");
		json.put("emergencyPhone", "15836269529");
		 
		params.put("data", json.toString());
        
		HashMap<String, String> pros = new HashMap<String, String>();
		pros.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		pros.put("Accept-Language", "zh-CN,zh;q=0.8");
		
		HttpUtil http = new HttpUtil(bankCheckCode, "utf-8", params, pros);
		String result =http.getResponseString();
		System.out.println(result);
	}

	/**
	 * 测试
	 * @throws Exception
	 */
	public static void applyCreditCard(String phoneauthcode) throws Exception{
		HashMap<String, String> params = new HashMap<String, String>();

//		datas.put("indentificationId", mm.getCidcard());
//	   	datas.put("identificationPeriod", "0");
//		String effectiveDate=mm.getCidexpirationtime();
//		datas.put("identificationDate",effectiveDate.substring(effectiveDate.indexOf(",")+1,effectiveDate.indexOf(",")+9));
//		datas.put("telephone", "");
//		datas.put("identifyCode", bean.getImgauthcode());
		params.put("ibankid", "10");
		params.put("cphone", "18717861758");
//		params.put("imgauthcode", "6824");
		params.put("phoneauthcode", phoneauthcode);
		JSONObject json = new JSONObject();

		json.put("companyTelNum", "021-45678912");
		json.put("idExpirationTime", "20101002,20201002");

		json.put("name", "刘伟国");
		json.put("englishName", "LIU WEI GUO");
		json.put("cardid", "3587");
//		json.put("cardID", "410381199101197837");
		json.put("maritalStatus", "1");
		json.put("userPhoneNum", "18717861758");
		json.put("degree", "3");
		json.put("companyPostcode", "123456");
		json.put("homeAddress", "fp003-21ac4e7a2cf14df48f67ed65c2012853-c28b774ba51c48f397a93451a63b3284");
		json.put("companyAddress", "fp003-21ac4e7a2cf14df48f67ed65c2012853-c28b774ba51c48f397a93451a63b3284");

		json.put("homeDetailAddress", "长安大道100号"); //详细地址
		json.put("homePostcode", "450000"); //编码
		json.put("natureOfUnit", "3");
		json.put("natureOfBusiness", "2");//行业性质
		json.put("cardID", "110101198706010096");
		json.put("email", "2565656@qq.com");
//		datas.put("workplace", mm.getCcompanyname());
//		 map.put("workprovince", mm.getApplyCompanyProvince());
//		 map.put("workcity", mm.getApplyCompanyCity());
//		 map.put("workarea", mm.getApplyCompanyAddress());
//		 datas.put("address", mm.getCcompany_detailaddress());
//		 datas.put("workaddress1", mm.getCcompany_detailaddress());
//		 datas.put("workaddress2", mm.getCcompany_detailaddress());
//		 datas.put("address3", mm.getCcompany_detailaddress());
//		 datas.put("workingYears", XingYeConvertBean.workTime.get(mm.getItimeinjob()));
//		 datas.put("salary", mm.getIannualsalary());
//		 datas.put("nature", XingYeConvertBean.industries.get(mm.getInatureofunit()));
//		 datas.put("department", mm.getCdepartmentname());
//		 datas.put("post", XingYeConvertBean.jobs.get(mm.getIdepartment()));

		json.put("company", "上海大河工信技术有限公司"); //详细地址
		json.put("companyDetailAddress", "上海市浦东新区世纪大道100号"); // 编码
		json.put("annualSalary", "15");
		json.put("natureOfBusiness", "2");// 行业性质 natureOfUnit
		json.put("natureOfUnit", "2");
		json.put("departmentName", "技术");
		json.put("postAddress", "1");
		json.put("timeInJob", "2");
		json.put("post", "2");// 行业性质 natureOfUnit
		json.put("emergencyContactName", "李四");
		json.put("familyTies", "1");
		json.put("emergencyPhone", "15836269529");

		params.put("data", json.toString());

		HashMap<String, String> pros = new HashMap<String, String>();
		pros.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		pros.put("Accept-Language", "zh-CN,zh;q=0.8");

		HttpUtil http = new HttpUtil(apply_CreditCard, "utf-8", params, pros);
		String result =http.getResponseString();
		System.out.println(result);
	}

	/**
	 * 测试
	 * @throws Exception
	 */
	public static void getMessageCode(String imgCode) throws Exception{
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("ibankid", "10");
		params.put("cphone", "18717861758");
		params.put("imgauthcode", imgCode);
//		params.put("phoneauthcode", phoneauthcode);
		params.put("orderid", "123");
		JSONObject json = new JSONObject();

		json.put("cardid", "3587");
		json.put("idExpirationTime", "20101002,20201002");

		json.put("name", "李吉龙");
		json.put("englishName", "LI JI LONG");
//		json.put("cardID", "410381199101197837");
		json.put("maritalStatus", "1");
		json.put("userPhoneNum", "18717861758");
		json.put("degree", "3");//卡Id
		json.put("email", "2565656@qq.com");
		json.put("homeAddress", "110000-110100-110105");
		json.put("companyAddress", "110000-110100-110105");

		json.put("companyTelNum", "021-45678912");


		json.put("homeDetailAddress", "长安大道100号"); //详细地址
		json.put("homePostcode", "450000"); //编码
		json.put("natureOfUnit", "3");
		json.put("natureOfBusiness", "2");//行业性质
		json.put("cardID", "110101198706010096");

		json.put("company", "上海大河工信技术有限公司"); //详细地址
		json.put("companyDetailAddress", "上海市浦东新区世纪大道100号"); // 编码
		json.put("annualSalary", "15");
		json.put("natureOfBusiness", "2");// 行业性质 natureOfUnit
		json.put("natureOfUnit", "2");
		json.put("departmentName", "技术");
		json.put("postAddress", "1");
		json.put("post", "2");// 行业性质 natureOfUnit

		json.put("emergencyContactName", "李四");
		json.put("familyTies", "1");
		json.put("emergencyPhone", "15836269529");

		params.put("data", json.toString());

		HashMap<String, String> pros = new HashMap<String, String>();
		pros.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		pros.put("Accept-Lang uage", "zh-CN,zh;q=0.8");

		HttpUtil http = new HttpUtil(bankMessage, "utf-8", params, pros);
		String result =http.getResponseString();
		System.out.println(result);
	}
}
