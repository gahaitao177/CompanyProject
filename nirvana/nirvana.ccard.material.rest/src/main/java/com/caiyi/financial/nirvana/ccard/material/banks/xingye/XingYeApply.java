package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.DataUtil;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XingYeApply {
   private String errHtml;
   public static Logger logger = LoggerFactory.getLogger("materialBeanImpl");
   private static Map<String,String> CardToURL=new HashMap<String, String>();
   private static Map<String,String> CardToBankCardId=new HashMap<String, String>();
   static {
		   CardToURL.put("3587", "52050f966b7d49e5ae07be9fffc596e0");
		   CardToURL.put("5842", "bb0f679c7f2e48bb98256fa5f77a2832");
		   CardToURL.put("4116", "bb0f679c7f2e48bb98256fa5f77a2832");
		   CardToURL.put("3555", "52050f966b7d49e5ae07be9fffc596e0");
		   CardToURL.put("4015", "134eb2ab668644f480dc398184df32fe");
		   CardToURL.put("3903", "b0cf98195464479685861fbad953ec74");
		   CardToURL.put("3643", "b0cf98195464479685861fbad953ec74");
		   CardToURL.put("3590", "b0cf98195464479685861fbad953ec74");
   };
   static {
	   CardToBankCardId.put("3587", "28919A6AD49B52BFE0539E0F05A81B26");
	   CardToBankCardId.put("5842", "28919A6AD49A52BFE0539E0F05A81B26");
	   CardToBankCardId.put("3555", "28919A6AD49252BFE0539E0F05A81B26");
	   CardToBankCardId.put("4015", "28919A6AD49952BFE0539E0F05A81B26");
	   CardToBankCardId.put("3903", "28919A6AD4B052BFE0539E0F05A81B26");
	   CardToBankCardId.put("3643", "28919A6AD49E52BFE0539E0F05A81B26");
	   CardToBankCardId.put("3590", "28919A6AD49552BFE0539E0F05A81B26");
	   CardToBankCardId.put("4116", "28919A6AD49452BFE0539E0F05A81B26");
	   
   };
   
   public BufferedImage getCheckCode(MaterialBean bean, HttpServletRequest request,MemCachedClient client){
	   boolean test=false;
	   System.out.println("进入兴业");
	   CloseableHttpClient httpClient = HttpClients.createDefault();
	   CookieStore cookieStore = new BasicCookieStore();
	   HttpContext localContext = new BasicHttpContext();
		// 设置请求和传输超时时间
	   RequestConfig.custom().setConnectTimeout(20000);
	   RequestConfig.custom().setSocketTimeout(20000);
	   RequestConfig.custom().setConnectionRequestTimeout(20000);
	   RequestConfig requestConfig = RequestConfig.custom().build();
	   localContext.setAttribute("http.cookie-store", cookieStore);
	   Map<String,String> headers = getBasicHeader();
	   
	   String userIp=DataUtil.getRealIp(request).trim();
		if (!StringUtils.isEmpty(userIp)) {
			headers.put("X-Forwarded-For", userIp);
		}
//	   String first = "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/ApplyNotice/view";
//	   errHtml = HpClientUtil.httpGet(first, headers, httpClient,localContext, "UTF-8", requestConfig);
	   String index0="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/TwoBar/view?id=dba63b81504f4a61a0cac78a81bbadd0";
	   errHtml=HpClientUtil.httpGet(index0, headers, httpClient, localContext, "UTF-8", requestConfig);
	   
	   String first="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/TwoBar/view?id=";
	   String id=CardToURL.get(bean.getModel().getCardid());
	   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	   String fileName= "yzm_"+df.format(new Date())+Math.random()*100;
	   ErrorRequestBean req=new ErrorRequestBean(fileName, bean, "", first);
	   req.setIerrortype(2);
	   if(strIsNull(id)){
		   req.setCerrordesc("没有查询到您选择的卡");
		   req.setCphone(bean.getModel().getCphone());
		   req.setUrl(first);
		   req.setIerrortype(-1);
		   BankApplyListener.sendError(BankEnum.xingye, BankApplyStepEnum.img_code, req);
		   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+"图片验证码获取失败");
		   return null;
	   }else{
		   first+=id;
	   }
	   errHtml = HpClientUtil.httpGet(first, headers, httpClient,localContext, "UTF-8", requestConfig);
	   
	  
//	   String index = "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/ApplyNoticeTwo/view";
//	   errHtml=HpClientUtil.httpGet(index, headers, httpClient, localContext, "UTF-8", requestConfig);
	   

	   String index="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/CardProSelect/view?productType=&productName=&oneCard=1&hobby_id=&app_downsellflag=1";
	   headers.put("Referer", first);
	   errHtml=HpClientUtil.httpGet(index, headers, httpClient, localContext, "UTF-8", requestConfig);
	   
	   Map<String, String> para=new HashMap<String, String>();
	   if(strIsNull(errHtml)){
		   req.setCerrordesc("没有获得有效的卡号");
		   req.setCphone(bean.getModel().getCphone());
		   req.setIerrortype(-1);
		   req.setUrl(index);
		   BankApplyListener.sendError(BankEnum.xingye, BankApplyStepEnum.img_code, req);
		   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+"请求您选择的卡出错");
		   return null; 
	   }
	   Element doc=Jsoup.parse(errHtml);
	   if(doc!=null){
		  Elements elements= doc.select("table").select("td");
		  if(elements.size()>0){
			  for(Element e:elements){
				  String str=e.html();
				  if(str!=null&&str.contains(CardToBankCardId.get(bean.getModel().getCardid()))){
					  para.put("proType", e.getElementById("proType").val());
					  para.put("productName", e.getElementById("sampleType").val());
					  para.put("productType", e.getElementById("cardType").val());
					  para.put("cardName", e.getElementById("cardName").val());
					  para.put("card_id", e.getElementById("cardId").val());
				  }
			  }
		  }else{
			  req.setCerrordesc("没有查询到您选择的卡");
			  req.setCphone(bean.getModel().getCphone());
			  req.setUrl(index);
			  req.setIerrortype(-1);
			  BankApplyListener.sendError(BankEnum.xingye, BankApplyStepEnum.img_code, req);
			  logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+"没有查询到您选择的卡");
			  return null; 
		  }
	   }else{
		   req.setCerrordesc("没有查询到您选择的卡");
		   req.setCphone(bean.getModel().getCphone());
		   req.setUrl(index);
		   req.setIerrortype(-1);
		   BankApplyListener.sendError(BankEnum.xingye, BankApplyStepEnum.img_code, req);
		   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+"没有查询到您选择的卡");
		   return null; 
	   }
	   String ProductUrl="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/CardProSelect/add";
	   headers.put("Referer", index);
	   if(para.size()>0){
			errHtml = HpClientUtil.httpPost(ProductUrl, headers, para, httpClient,localContext, "UTF-8", requestConfig); 
	   }
	   if(!errHtml.contains("000000")){
		   req.setResult(errHtml);
		   req.setCerrordesc("选择的卡id失败");
		   req.setCphone(bean.getModel().getCphone());
		   req.setUrl(ProductUrl);
		   req.setIerrortype(-1);
		   BankApplyListener.sendError(BankEnum.xingye, BankApplyStepEnum.img_code, req);
		   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+"提交申请的卡参数失败，请重新选择卡片");
		   return null; 
	   }	   	   
	   String  Imgurl = "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/BaseInfo/getValidateImg";
	   headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/BaseInfo/view");
	   BufferedImage image=HpClientUtil.getRandomImageOfJPEG(Imgurl, headers, httpClient, localContext, requestConfig);

	  
	   if(image!=null){
		   String keyContext=bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-xingyeContext";
		   boolean res=client.set(keyContext, cookieStore);
		   if(res){
			   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+" 缓存设置成功");
		   } else{
			   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+" 缓存设置失败");
		   }
		   req.setCerrordesc("图片验证码获取成功");
		   BankApplyListener.sendSucess(BankEnum.xingye, BankApplyStepEnum.img_code);
		   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+" 图片验证码获取成功");
	   }else{
		   req.setCerrordesc("图片验证码获取失败");
		   req.setUrl(Imgurl);
		   req.setCphone(bean.getModel().getCphone());
		   req.setIerrortype(4);
		   BankApplyListener.sendError(BankEnum.xingye, BankApplyStepEnum.img_code, req);
		   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+"图片验证码获取失败");
		   
	   }
//	   String imgRand = "";//图片验证码
//	   if(!test){
//		  try {
//		      ImageIO.write(image, "jpg",new File("D:\\data\\img.jpg"));
//	          System.out.println("生成验证码：");
////	          BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
////	          imgRand=strin.readLine();
//		   } catch (IOException e) {
//			  e.printStackTrace();
//		   }
//	   }
	   return image;	  		 
   }
   
   public Map<String,String> getBasicHeader(){
	   Map<String,String> headers = new HashMap<String,String>();
	   headers.put("User-Agent", "Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
	   headers.put("Host", "ccshop.cib.com.cn:8010");
	   headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
       headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
       headers.put("Accept-Encoding", "gzip, deflate");
       headers.put("Connection", "Keep-Aliv");
       return headers;
   }
    
	/** 
	 *	发短信
	 * @throws Exception
	 */
	public  int  sendMessage(MaterialBean bean,MemCachedClient client) {
		if (!checkData(bean))
			return 0;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		bean.setLtype(1);
		String keyContext = bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard() + "-xingyeContext";
		String keyBean = bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard() + "-xingyeBean";
		client.set(keyBean, bean);
		BasicCookieStore cookie = (BasicCookieStore) client.get(keyContext);
		HttpContext localContext = new BasicHttpContext();
		// 设置请求和传输超时时间
		RequestConfig.custom().setConnectTimeout(20000);
		RequestConfig.custom().setSocketTimeout(20000);
		RequestConfig.custom().setConnectionRequestTimeout(20000);
		RequestConfig requestConfig = RequestConfig.custom().build();
		localContext.setAttribute("http.cookie-store", cookie);

		Map<String,String> headers=getBasicHeader();
		String url = "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/BaseInfo/add";
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/BaseInfo/view");

		Map<String, String> datas = new HashMap<String, String>();
		// datas.put("isRecomend", "1");
		// datas.put("indentificationId", "431021199011034892");
		// datas.put("identificationPeriod", "0");
		// datas.put("identificationDate", "20261103");
		// datas.put("telephone", "");
		// datas.put("identifyCode", "rfi3");
		MaterialModel mm = bean.getModel();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String fileName= "sendMessage_"+df.format(new Date())+Math.random()*100;
		ErrorRequestBean req = new ErrorRequestBean(fileName, bean,"", url);
		datas.put("indentificationId", mm.getCidcard());
		String effectiveDate = mm.getCidexpirationtime();
		if ("-1".equals(effectiveDate.trim())) {
			datas.put("identificationPeriod", "1");
			datas.put("identificationDate", "");
		} else {
			datas.put("identificationPeriod", "0");
			try {
				datas.put("identificationDate", effectiveDate.substring(
						effectiveDate.indexOf(",") + 1,
						effectiveDate.indexOf(",") + 9));
			} catch (Exception e) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("身份证有效期格式有误");
				bean.setBusiJSON("fail");
								
				req.setCerrordesc("身份证有效期格式有误");
				req.setCphone(bean.getModel().getCphone());
				req.setUrl(url);
				req.setIerrortype(2);
				logger.info(bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard()+"-"+bean.getModel().getCardid() + " 异常：" + e);
				return 0;
			}
		}
		datas.put("identifyCode1", bean.getImgauthcode());

		try {
			errHtml = HpClientUtil.httpPost(url, headers, datas, httpClient,localContext, "UTF-8", requestConfig);
		} catch (Exception e) {
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("银行服务异常，稍后再提交信息");
			bean.setBusiJSON("fail");
			logger.info(bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard() +"-"+bean.getModel().getCardid()+ " 异常：" + e);
			
			req.setIerrortype(0);
			req.setResult(e.toString());
			req.setCerrordesc("提交验证码异常");
			req.setCphone(bean.getModel().getCphone());
			req.setUrl(url);
			BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.img_code, req);
			return 0;
		}

		System.out.println("提交验证码：" + errHtml);
		JSONObject json = JSONObject.parseObject(errHtml);
		if ("000000".equals(json.get("code"))) {
			bean.setBusiErrCode(1);
			bean.setBusiJSON("图片验证码验证成功");
			logger.info(bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard()+"-"+bean.getModel().getCardid() + " 图片验证码验证成功");
			req.setResult("短信验证码提交成功");
			BankApplyListener.sendSucess(BankEnum.xingye,BankApplyStepEnum.img_code);
		} else {
			bean.setBusiErrCode(-1);
			if (!strIsNull(json.get("resInfo").toString()))
				bean.setBusiErrDesc(json.get("resInfo").toString());
			else{
				bean.setBusiErrDesc(json.get("msg").toString());
			}
			bean.setBusiJSON("图片验证码验证失败，请稍后再试");
			logger.info(bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard() +"-"+bean.getModel().getCardid()+ " 图片验证码验证失败");
			
			req.setIerrortype(1);
			req.setCerrordesc("短信验证码提交失败");
			req.setCphone(bean.getModel().getCphone());
			req.setResult(errHtml);
			req.setUrl(url);
			BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.img_code, req);
			return 0;
		}

		headers.put("User-Agent",
				"Opera/9.80 (Android 2.3.7; Linux; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
		headers.put("Host", "ccshop.cib.com.cn:8010");
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Referer","https://ccshop.cib.com.cn:8010/application/cardapp/Fast/PreliminaryVerify/view");

		Map<String, String> map = new HashMap<>();
		map.put("callCount", "1");
		map.put("page", "/application/cardapp/Fast/PersonalInfo/view");
		map.put("httpSessionId", "");
		map.put("c0-scriptName", "authVerification");
		map.put("c0-methodName", "getSmsCodePerson");
		map.put("scriptSessionId",UUID.randomUUID() + "" + Math.floor(Math.random() * 1000)); // scriptSessionId
		map.put("c0-id", "0");
		map.put("c0-param0", "string:" + bean.getModel().getCphone());// 手机
		map.put("c0-param1", "string:0");
		map.put("batchId", "0");// 验证码

		String sendMessageURL = "https://ccshop.cib.com.cn:8010/application/cardapp/dwr/call/plaincall/authVerification.getSmsCodePerson.dwr";
		errHtml = HpClientUtil.httpPost(sendMessageURL, headers, map,httpClient, localContext, "UTF-8", requestConfig);
		String result = errHtml;
		if (errHtml.contains("{") && errHtml.contains("}")) {
			result = errHtml.substring(errHtml.indexOf("{"),errHtml.indexOf("}") + 1);
			json = JSONObject.parseObject(result);
			if ("success".equals(json.get("resInfo").toString())) {
				bean.setBusiErrCode(1);
				bean.setBusiErrDesc("验证码发送成功");
				bean.setBusiJSON("success");
				logger.info(bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard()+"-"+bean.getModel().getCardid() + " 短信验证码获取成功");
				BankApplyListener.sendSucess(BankEnum.xingye,BankApplyStepEnum.phone_code);
				return 1;
			} else {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("验证码发送失败");
				bean.setBusiJSON("faid");
				logger.info(bean.getModel().getCphone() + "-"
						+ bean.getModel().getCidcard()+"-"+bean.getModel().getCardid() + " 短信验证码获取失败");
				req.setResult(errHtml);
				req.setIerrortype(1);
				req.setCerrordesc("短信验证码提交失败");
				req.setUrl(sendMessageURL);
				req.setCphone(bean.getModel().getCphone());
				BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.phone_code, req);
				return 0;
			}
		}
		bean.setBusiErrCode(0);
		bean.setBusiErrDesc("短信验证码发送失败");
		bean.setBusiJSON("faid");
		logger.info(bean.getModel().getCphone() + "-"+ bean.getModel().getCidcard() +"-"+bean.getModel().getCardid()+ " 短信验证码获取失败");

		req.setUrl(sendMessageURL);
		req.setIerrortype(1);
		req.setCerrordesc("短信验证码提交失败");
		req.setCphone(bean.getModel().getCphone());
		req.setResult(errHtml);
		BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.phone_code, req);
		return 0;
			
	}
	public int applyForXingYe(MaterialBean bean,HttpServletRequest request,MemCachedClient client){
		if (strIsNull(bean.getPhoneauthcode())) {
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("短信验证码不能为空");
			bean.setBusiJSON("fail");
			return 0;
		}
		CloseableHttpClient httpClient=HttpClients.createDefault();
		String keyContext = bean.getModel().getCphone() + "-" + bean.getModel().getCidcard()+ "-xingyeContext";
		CookieStore cookie = (CookieStore) client.get(keyContext);
		
		String keyBean=bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-xingyeBean";
		MaterialBean beanConvert=(MaterialBean) client.get(keyBean);
		HttpContext localContext = new BasicHttpContext();
		// 设置请求和传输超时时间
		RequestConfig.custom().setConnectTimeout(20000);
		RequestConfig.custom().setSocketTimeout(20000);
		RequestConfig.custom().setConnectionRequestTimeout(20000);
		RequestConfig requestConfig = RequestConfig.custom().build();
		localContext.setAttribute("http.cookie-store", cookie);
		
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		 String fileName= "submit_"+df.format(new Date())+Math.random()*100;
		 ErrorRequestBean req0=new ErrorRequestBean(fileName, bean, "", "submit");
		
		if(beanConvert.getLtype()!=1){
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("没有获得图片验证码");
			bean.setBusiJSON("fail");
			
			req0.setIerrortype(2);
			req0.setCerrordesc("没有获得图片验证码");
			req0.setCphone(bean.getModel().getCphone());
			BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.submit_apply,req0);
			return 0;
		}
		
		Map<String,String> headers=getBasicHeader();
		String userIp= DataUtil.getRealIp(request).trim();
		if (!StringUtils.isEmpty(userIp)) {
			headers.put("X-Forwarded-For", userIp);
		}
		String url = "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/PreliminaryVerify/add";
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/PreliminaryVerify/view");
		Map<String,String> datas = new HashMap<String,String>();		   
		MaterialModel mm=bean.getModel();
			
		   //持卡
		   datas.put("havecard", "1");
		   datas.put("havetime", "2");
		   errHtml=HpClientUtil.httpPost(url, headers, datas, httpClient, localContext, "UTF-8", requestConfig);   
		  System.out.println("PreliminaryVerify/add结果:"+errHtml);
		   datas.clear();
		   //个人信息
//		   datas.put("card_id","28919A6AD49352BFE0539E0F05A81B26"); 
		   datas.put("card_id",beanConvert.getApplyBankCardId());   
		   datas.put("realname",mm.getCname());
		   datas.put("marriage", XingYeConvertBean.marriages.get(mm.getMaritalstatus()));
		   datas.put("pinyin", mm.getCenglishname().toUpperCase());
		   datas.put("e_mail", mm.getCemail());
		   datas.put("pri_gender", "1");
		   datas.put("education", XingYeConvertBean.degrees.get(mm.getIdegree()));
		   datas.put("province", beanConvert.getApplyProvinceId());
		   datas.put("city", beanConvert.getApplyCityId());
		   datas.put("area", beanConvert.getApplyAddress());

		   datas.put("address", mm.getChome_detailaddress());
		   datas.put("address1", beanConvert.getModel().getCbak1());
		   datas.put("address2", "");
		   datas.put("address3", "");
		   datas.put("zipcode",mm.getChome_postcode());
		   datas.put("tel", mm.getCphone());
		   datas.put("identifyCode", bean.getPhoneauthcode());

		 String[] ccompanyTel=mm.getCcompany_telnum().split("-");
		 if(ccompanyTel.length==2){
			 if(ccompanyTel[0].length()==3||ccompanyTel[0].length()==4){
				 datas.put("areacode", ccompanyTel[0]);
				 datas.put("phonenumber", ccompanyTel[1]);
			 }else{
				 datas.put("areacode", "021");
				 datas.put("phonenumber", ccompanyTel[0]);
				 datas.put("extensionnumber", ccompanyTel[1]);
			 }

		 }else if(ccompanyTel.length==3){
			 if(ccompanyTel[0].length()==3||ccompanyTel[0].length()==4){
				 datas.put("areacode", ccompanyTel[0]);
				 datas.put("phonenumber", ccompanyTel[1]);
				 datas.put("extensionnumber", ccompanyTel[2]);
				 }
		 }else{
			 	bean.setBusiErrCode(0);
			 	bean.setBusiErrDesc("公司电话必须填写固话");
			 	bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"}");

			 	req0.setIerrortype(2);
				req0.setCerrordesc("公司电话必须填写固话");
				req0.setCphone(bean.getModel().getCphone());
				req0.setUrl(url);
				BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.submit_apply,req0);
			    return 0;
		 }
		 String personUrl="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/PersonalInfo/add";
		 headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/PersonalInfo/view");
		 errHtml=HpClientUtil.httpPost(personUrl, headers, datas, httpClient, localContext, "UTF-8", requestConfig);

		 JSONObject json=JSONObject.parseObject(errHtml);
		 if("err".equals(json.get("code"))){
			 bean.setBusiErrCode(-1);
		     bean.setBusiErrDesc(json.get("resInfo").toString());
		     bean.setBusiJSON("fail");

		     req0.setUrl(personUrl);
		     req0.setCerrordesc(json.get("resInfo").toString());
		     req0.setIerrortype(3);
		     req0.setCphone(mm.getCphone());
		     req0.setResult(errHtml);
//		     ErrorUtils.saveQueryFile(ErrorBankEnum.xingye, bean, json.get("resInfo").toString());
		     logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+" "+json.get("resInfo").toString());
		     BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.submit_apply,req0);
			 return 0;
		 }
		 datas.clear();

		 datas.put("workplace", mm.getCcompanyname());
		 datas.put("workprovince", beanConvert.getApplyCompanyProvince());
		 datas.put("workcity", beanConvert.getApplyCompanyCity());
		 datas.put("workarea", beanConvert.getApplyCompanyAddress());

		 datas.put("workaddress", mm.getCcompany_detailaddress());
		 datas.put("workaddress1", beanConvert.getModel().getCbak2());
		 datas.put("workaddress2", mm.getCcompany_detailaddress());
		 datas.put("workaddress3","");
		 datas.put("workzipcode", mm.getCcompany_postcode());

		 datas.put("workingYears", XingYeConvertBean.workTime.get(mm.getItimeinjob()));
		 datas.put("salary", mm.getIannualsalary());
		 datas.put("nature", XingYeConvertBean.industries.get(mm.getInatureofunit()));
		 datas.put("department", mm.getCdepartmentname());
		 datas.put("post", XingYeConvertBean.jobs.get(mm.getIdepartment()));
		 datas.put("plates", "");

		 url="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/WorkPropertyInfo/add";
		 headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/WorkPropertyInfo/view");
		 errHtml=HpClientUtil.httpPost(url, headers, datas, httpClient, localContext, "UTF-8", requestConfig);
		 System.out.println("WorkPropertyInfo/add结果："+errHtml);
		 datas.clear();

			 //联系人
		 datas.put("relativename", mm.getCemergencycontactname());
		 datas.put("relationship", XingYeConvertBean.relation.get(mm.getIfamilyties()));
		 datas.put("relativetel", mm.getCemergencyphone());
		 url="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/ContactionInfo/add";
		 headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/ContactionInfo/view");
		 errHtml=HpClientUtil.httpPost(url, headers, datas, httpClient, localContext, "UTF-8", requestConfig);
		 System.out.println("ContactionInfo/add结果："+errHtml);
		 datas.clear();

		 String ipostaddress = mm.getIpostaddress();// 邮寄地址：（1、单位地址  2、住宅地址
			if("1".equals(ipostaddress)){
				mm.setIpostaddress("B");
			}else{
				mm.setIpostaddress("H");
			}
		   datas.put("sendaddress", mm.getIpostaddress()); // 默认为单位地址
		   mm.setIpostaddress(ipostaddress);  //参数发送之后，设置为1、2,因为要保存资料

		   datas.put("password", "1"); //境内消费密码  默认开通
		   datas.put("notification", "0");  //交易短信通知 默认不开通
		   datas.put("sendmode", "1"); //账单邮寄方式 电子账单
		   url="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/OtherInformation/add";
		   headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/OtherInformation/view");
		   errHtml=HpClientUtil.httpPost(url, headers, datas, httpClient, localContext, "UTF-8", requestConfig);
		   System.out.println("OtherInformation/add结果:"+errHtml);
		   datas.clear();

		   String  addDeclareInfoURL="https://ccshop.cib.com.cn:8010/application/cardapp/Fast/Declaration/add";
		   headers.put("Referer", "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/Declaration/view");


		   //edit by lzj 20160407
//		   ErrorRequestBean req=new ErrorRequestBean("提交资料", bean.toString(), "", addDeclareInfoURL);
		   errHtml=HpClientUtil.httpPost(addDeclareInfoURL, headers, datas, httpClient, localContext, "UTF-8", requestConfig);
		   System.out.println(errHtml);

		   json=JSONObject.parseObject(errHtml);
		   if("000000".equals(json.get("code"))){
			   if(json.get("msg")!=null&&json.get("msg").toString().contains("对您的申请进行审核")){
				   bean.setBusiErrCode(1);
				   bean.setBusiErrDesc(json.get("msg").toString());
				   logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+" 申卡成功");
				   bean.setBusiJSON("申卡成功");

				   //edit by lzj 20160407
				   req0.setResult("提交资料成功");
				   BankApplyListener.sendSucess(BankEnum.xingye,BankApplyStepEnum.submit_apply);
				   return 1;
			   }else{
				   bean.setBusiErrCode(0);
			        bean.setBusiErrDesc(json.get("msg").toString());
			        bean.setBusiJSON("申卡失败");
			        logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+" 申卡失败");
			        //edit by lzj 20160407
			        req0.setCerrordesc(json.get("msg").toString());
				    req0.setIerrortype(3);
				    req0.setCphone(mm.getCphone());
				    req0.setUrl(addDeclareInfoURL);
				    req0.setResult(errHtml);
					BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.submit_apply,req0);
					return 0;
			   }
		   }else{
			   	bean.setBusiErrCode(0);
		        bean.setBusiErrDesc(json.get("msg").toString());
		        bean.setBusiJSON("申卡失败");
//		        ErrorUtils.saveQueryFile(ErrorBankEnum.xingye, bean, json.get("msg").toString());
		        logger.info(bean.getModel().getCphone()+"-"+bean.getModel().getCidcard()+"-"+bean.getModel().getCardid()+" 申卡失败");
		        //edit by lzj 20160407
		        req0.setCerrordesc(json.get("msg").toString());
			    req0.setIerrortype(3);
			    req0.setCphone(mm.getCphone());
			    req0.setUrl(addDeclareInfoURL);
			    req0.setResult(errHtml);
				BankApplyListener.sendError(BankEnum.xingye,BankApplyStepEnum.submit_apply,req0);
		        return 0;
		   }

	}

	public boolean checkData(MaterialBean bean) {
		boolean res = false;
		if (strIsNull(bean.getImgauthcode())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("图片验证码不能为空");
			bean.setBusiJSON("fail");
			return res;
		}
		MaterialModel mm = bean.getModel();
		String effectiveDate = mm.getCidexpirationtime();

		if (effectiveDate.indexOf(",") <= 0&&!"-1".equals(effectiveDate.trim())){
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行 暂不支持的身份证号");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(XingYeConvertBean.marriages.get(mm.getMaritalstatus()))) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡不支持的 性别");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(XingYeConvertBean.degrees.get(mm.getIdegree()))) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡不支持的学历");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(mm.getChome_detailaddress())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡 家庭地址 不能为空");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(mm.getChome_postcode())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡家庭邮编 不能为空");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(mm.getCphone())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡申请的手机号码不能为空");
			bean.setBusiJSON("fail");
			return res;
		}
//		if (strIsNull(bean.getPhoneauthcode())) {
//			bean.setBusiErrCode(0);
//			bean.setBusiErrDesc("短信验证码不能为空");
//			bean.setBusiJSON("fail");
//			return res;
//		}
		if (strIsNull(mm.getCcompany_detailaddress())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡申请的公司地址不能空");
			bean.setBusiJSON("fail");
			return res;
		}
		if (StringUtils.isNumeric(mm.getIannualsalary())&&strIsNum(mm.getIannualsalary())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡，薪水必须是0.5-1000的数字");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(XingYeConvertBean.jobs.get(mm.getIdepartment()))) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡不支持的职务");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(XingYeConvertBean.industries.get(mm.getInatureofunit()))) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡不支持的公司行业");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(bean.getApplyBankCardId())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("您申请的兴业银行卡暂不支持，请重新选择卡片");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(bean.getApplyProvinceId())
				|| strIsNull(bean.getApplyCityId())
				|| strIsNull(bean.getApplyAddress())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡不支持该家庭地址的申请");
			bean.setBusiJSON("fail");
			return res;
		}
		if (strIsNull(bean.getApplyCompanyProvince())
				|| strIsNull(bean.getApplyCompanyCity())
				|| strIsNull(bean.getApplyCompanyAddress())) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡不支持的公司地址申请");
			bean.setBusiJSON("fail");
			return res;
		}
		if(!strIsContainChinese(mm.getChome_detailaddress())){
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡家庭详细地址必须包含汉字");
			bean.setBusiJSON("fail");
			return res;
		}
		if(!strIsContainChinese(mm.getCcompany_detailaddress())){
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("兴业银行卡公司详细地址必须包含汉字");
			bean.setBusiJSON("fail");
			return res;
		}
		res = true;
		return res;
	}
	public boolean  strIsNull(String str){
		if(str==null||"".equals(str.trim()))
			return true;
		else
			return false;
	}

	public boolean strIsContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	public boolean strIsNum(String str) {
		try{
			Integer tmp=Integer.parseInt(str);
			if(0<tmp&&tmp<1000){
				return false;
			}else{
				return true;
			}
		}catch(Exception e){
			return false;
		}
	}
   public static void main(String[] args) {
	XingYeApply yingYe = new XingYeApply();
//	yingYe.applyForXingYe();
	
}
}
