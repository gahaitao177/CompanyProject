package com.caiyi.financial.nirvana.ccard.material.banks.jiaotong;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.danga.MemCached.MemCachedClient;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JiaoTongH5Helper {
    public static Logger logger = LoggerFactory.getLogger("materialBeanImpl");
	public static Map<String, String> jiaoTong_House = new HashMap<String, String>();
    public static Map<String, String> jiaoTong_TimeInJob = new HashMap<String, String>();
    public static Map<String, String> jiaoTong_Marrige = new HashMap<String, String>();
    public static Map<String, String> jiaoTong_Education = new HashMap<String, String>();
    public static Map<String, String> jiaoTong_Relation = new HashMap<String, String>();
	public static String curl="";

    static {
        jiaoTong_House.put("1", "1");
        jiaoTong_House.put("2", "0");
        jiaoTong_House.put("3", "3");
        jiaoTong_House.put("4", "4");
        jiaoTong_House.put("5", "6");

        jiaoTong_TimeInJob.put("1", "1");
        jiaoTong_TimeInJob.put("2", "1");
        jiaoTong_TimeInJob.put("3", "2");
        jiaoTong_TimeInJob.put("4", "3");
        jiaoTong_TimeInJob.put("5", "4");
        jiaoTong_TimeInJob.put("6", "5");

        jiaoTong_Marrige.put("1", "0");
        jiaoTong_Marrige.put("2", "1");
        jiaoTong_Marrige.put("3", "2");

        jiaoTong_Education.put("1", "0");
        jiaoTong_Education.put("2", "0");
        jiaoTong_Education.put("3", "1");
        jiaoTong_Education.put("4", "2");
        jiaoTong_Education.put("5", "4");

//		紧急联系人关系1.配偶 2.父母 3.子女 4.兄弟姐妹 5朋友
//		(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
        jiaoTong_Relation.put("1", "0");
        jiaoTong_Relation.put("2", "1");
        jiaoTong_Relation.put("3", "2");
        jiaoTong_Relation.put("4", "3");
        jiaoTong_Relation.put("5", "6");
    }
    
	public static void waitRun(int min) {
		long nowtime=System.currentTimeMillis();
		System.out.println(nowtime);
		long endtime=System.currentTimeMillis();
		while (endtime-nowtime<=min*1000) {
			endtime=System.currentTimeMillis();
		}
		System.out.println(endtime-nowtime);
	}
	
	private static void setClientCookies(CookieStore cookieStore,CookieManager CM) {
		Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies_ret;
		Iterator<com.gargoylesoftware.htmlunit.util.Cookie> it_ret;
		cookies_ret = CM.getCookies();
		it_ret = cookies_ret.iterator();
		cookieStore.clear();
		while (it_ret.hasNext()) {
			com.gargoylesoftware.htmlunit.util.Cookie c = it_ret.next();
			String name = c.getName();
			String value = c.getValue();
			BasicClientCookie bcookie = new BasicClientCookie(name, value);
			bcookie.setDomain(c.getDomain());
			bcookie.setExpiryDate(c.getExpires());
			bcookie.setPath(c.getPath());
			cookieStore.addCookie(bcookie);
		}
	}

	private static String getUrlParams(Map<String, String> parames) throws UnsupportedEncodingException {
		String urlparms="";
		for (String key : parames.keySet()) {
			if (StringUtils.isEmpty(urlparms)) {
				urlparms=key+"="+URLEncoder.encode(parames.get(key), "utf-8");
			}else {
				urlparms+="&"+key+"="+URLEncoder.encode(parames.get(key), "utf-8");
			}
		}
		return urlparms;
	}
	
	private static void goExit(WebClient webClient,MaterialBean bean,MaterialModel md) throws IOException, MalformedURLException {
		String url="https://creditcardapp.bankcomm.com/applynew/front/apply/mobileApplyCommon/exit.html?error=viewUI_00";	
		HtmlPage page=webClient.getPage(url);
		waitRun(4);
    	bean.setBusiErrCode(0);
        bean.setBusiErrDesc("您的申请信息已提交审核，请勿重复提交，谢谢您的配合！");
        bean.setBusiJSON("fail");
        logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的申请信息已提交审核，请勿重复提交，谢谢您的配合！");
	}
	
	
	
	
	
    
	public static int getBankMessage_JiaoTong(MaterialBean bean,MemCachedClient cc) {
        String url = "";
        String content = "";
        if (bean.getBusiErrCode() == 0) {
            return 0;
        }
        MaterialModel md = bean.getModel();
		CloseableHttpClient httpClient =null;
		CookieStore cookieStore=null;
        Map<String, String> parames = new HashMap<String, String>();
        Map<String, String> transferParameter = new HashMap<String, String>();
        WebClient webClient=null;
        String basePath="https://creditcardapp.bankcomm.com/applynew";
        
        String city1=bean.getProvinceName()+" "+bean.getCityName();
		logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] city1="+ city1);
        
		String city2=bean.getApplyCompanyProvinceName()+" "+bean.getApplyCompanyCityName();
		logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] city2="+ city2);
		
		
		logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyPost="+ md.getChome_postcode());
		logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] companyApplyPost="+ md.getCcompany_postcode());
		
		logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data="+ bean.getData());

        
        try {
			webClient= new WebClient();
			new JTConectionListener(webClient);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.setJavaScriptTimeout(50000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setCssEnabled(false);
			webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setTimeout(30000);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

			httpClient = HttpClients.createDefault();
			cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute("http.cookie-store", cookieStore);
			webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E238 Safari/601.1");
			webClient.addRequestHeader("Connection", "Keep-Alive");
			webClient.addRequestHeader("Host", "creditcardapp.bankcomm.com");
			webClient.addRequestHeader("Accept-Language", "zh-cn");
			webClient.addRequestHeader("Proxy-Connection", "keep-alive");
			webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");

            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Accept-Language", "zh-cn");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E238 Safari/601.1");
            requestHeaderMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");
            requestHeaderMap.put("Proxy-Connection", "keep-alive");

            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
			String trackCode="A022216511939";
            String cardCode="";
            String certNo = md.getCidcard();//证件号码
            String applyName = md.getCname();//用户真实姓名
            String applyTel = md.getCphone();//申请人手机号码

            HtmlPage page = webClient.getPage(basePath+"/front/apply/track/record.html?trackCode="+trackCode);
			content=page.getWebResponse().getContentAsString();
			waitRun(3);


	        parames.clear();
			url="https://creditcardapp.bankcomm.com/applynew/front/apply/checkVerify.json";
	        content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
	        JSONObject checkVerify = new JSONObject(content);
            String verifyPass = String.valueOf(checkVerify.get("verifyPass"));
            String accountBlock = String.valueOf(checkVerify.get("accountBlock"));
            String verifyResult = String.valueOf(checkVerify.get("verifyResult"));

            if ("true".equals(accountBlock)) {
            	url="https://creditcardapp.bankcomm.com/applynew/front/apply/exit.html?error=account_lock";
            	HtmlPage errorPage=webClient.getPage(url);
	            content=errorPage.getWebResponse().getContentAsString();
	            bean.setBusiErrCode(0);
                bean.setBusiErrDesc("抱歉，系统提示已达单日最大可申请量，请您明天再继续操作吧！");
                bean.setBusiJSON("fail");
                saveSigleHtml("系统已达最大申请量", content, "", md, "系统已达最大申请量", 5);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 抱歉，系统提示已达单日最大可申请量，请您明天再继续操作吧！");
                return 0;
			}else if (!"true".equals(verifyPass)) {
				//需要图片验证码
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 需要图片验证码="+content);
                saveSigleHtml("需要图片验证码", content, "", md, "需要图片验证码", 4);

			}




	        parames.clear();
            parames.put("applyName", applyName);
            parames.put("certNo", certNo);
            parames.put("applyTel", applyTel);
            parames.put("trackCode", trackCode);
            parames.put("cardCode", cardCode);
			CookieManager CM = webClient.getCookieManager();
			setClientCookies(cookieStore, CM);
            url=basePath+"/front/apply/identityCheck/check.json";
	        content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);

	        JSONObject resultObj = new JSONObject(content);

            String flag = String.valueOf(resultObj.get("flag"));
            String serial = String.valueOf(resultObj.get("serial"));
            String message = String.valueOf(resultObj.get("message"));
            String operateSuccessDate = String.valueOf(resultObj.get("operateSuccessDate"));
            String applyUuid = String.valueOf(resultObj.get("applyUuid"));
            String cimCode = String.valueOf(resultObj.get("cimCode"));
            String errorCode = String.valueOf(resultObj.get("errorCode"));
            String resultDesc="";
            if (!"true".equals(flag)) {
            	if ("NEW_006".equals(errorCode)) {

					url=basePath+"/front/apply/mobileApplyCommon/enterHolder.html?applyUuid="+applyUuid+"&trackCode="+trackCode;
					HtmlPage errorPage=webClient.getPage(url);
		            content=errorPage.getWebResponse().getContentAsString();
		            waitRun(4);
		            bean.setBusiErrCode(0);
	                bean.setBusiErrDesc("您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。温馨提示：如您已销户，可通过营业网点申请办理，或者销户6个月之后再通过网上办理");
	                bean.setBusiJSON("fail");
	                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您已经是交通银行信用卡持卡人客户");
	                return 0;
				}else if (!StringUtils.isEmpty(errorCode)) {
					url=basePath+"/front/apply/mobileApplyCommon/exit.html?error="+errorCode+"&trackCode="+trackCode+"&operateSuccessDate=";
					HtmlPage errorPage=webClient.getPage(url);
		            content=errorPage.getWebResponse().getContentAsString();
					if (content.contains("error-sm")) {
						List<HtmlElement> alist =(List<HtmlElement>)errorPage.getByXPath("//p[@class='error-sm']");
						resultDesc=alist.get(0).asText();
					}else {
						resultDesc="您不满足此次申请活动条件";
		                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 "+resultDesc+" content="+content);
					}
	                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 "+resultDesc);
	                waitRun(4);
	                bean.setBusiErrCode(0);
	                bean.setBusiErrDesc(resultDesc);
	                bean.setBusiJSON("fail");
				}else {
					bean.setBusiErrCode(0);
	                bean.setBusiErrDesc(message);
	                bean.setBusiJSON("fail");
				}
			}else {
				transferParameter.put("applyUuid", applyUuid);
				transferParameter.put("operateSuccessDate", operateSuccessDate);
				transferParameter.put("trackCode", trackCode);
				transferParameter.put("cimCode", cimCode);

                cc.set(md.getCidcard() + md.getCphone() + "_jiaotongCookies_CM", CM);
				cc.set(md.getCidcard() + md.getCphone() + "_jiaotongMaterialBean",bean);
				cc.set(md.getCidcard() + md.getCphone() + "_jiaotongCookies",cookieStore);
				cc.set(md.getCidcard() + md.getCphone() + "_jiaotongParameter",	transferParameter);
				logger.info("idcard[" + md.getCidcard() + "] mobile["+ md.getCphone() + "] 短信发送成功,短信编号：" + serial);
				bean.setBusiErrCode(1);
				bean.setBusiErrDesc("短信发送成功,短信编号：" + serial);
				bean.setBusiJSON("success");
				return 1;
			}


        } catch (Exception e) {
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  getBankMessage_JiaoTong 异常");
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] getBankMessage_JiaoTong 异常 content=" + content, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信验证码获取失败，请稍后再试");
            bean.setBusiJSON("fail");
            saveSigleHtml("短信验证码获取失败", content, "", md, e.getMessage(), 3);

            return 0;
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] getBankMessage_JiaoTong 异常", e);
                }
            }
            if (webClient!=null) {
				webClient.close();
			}
        }
        return 0;
    }

	private static void paramsLogs(MaterialModel md, Map<String, String> parames,String desc) {
		for (String key : parames.keySet()) {
			logger.info(desc+" parames idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] key["+key+"] value["+parames.get(key)+"]");
		}
	}

	public static int applyForTheJiaoTongBank(MaterialBean bean,MemCachedClient cc) {
		String jsonContent="";
		CloseableHttpClient httpClient = null;
        String url = "";
        String content = "";
        MaterialModel md = bean.getModel();
        Map<String, String> parames = new HashMap<String, String>();
        Map<String, String> htmls=new HashMap<String, String>();
        WebClient webClient= new WebClient();
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setJavaScriptTimeout(50000);
		webClient.getOptions().setCssEnabled(false);
		webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setTimeout(30000);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E238 Safari/601.1");
		webClient.addRequestHeader("Connection", "Keep-Alive");
		webClient.addRequestHeader("Host", "creditcardapp.bankcomm.com");
		webClient.addRequestHeader("Accept-Language", "zh-cn");
		webClient.addRequestHeader("Proxy-Connection", "keep-alive");
		webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
		new JTConectionListener(webClient);
        String basePath="https://creditcardapp.bankcomm.com/applynew";
        try {
        	Object cookieCMObj= cc.get(md.getCidcard() + md.getCphone() + "_jiaotongCookies_CM");
            Object cookieObj = cc.get(md.getCidcard() + md.getCphone() + "_jiaotongCookies");
            Object parameterObj = cc.get(md.getCidcard() + md.getCphone() + "_jiaotongParameter");
            Object materiallObj = cc.get(md.getCidcard() + md.getCphone() + "_jiaotongMaterialBean");
            if (cookieObj == null || parameterObj == null || materiallObj == null) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("短信验证码已失效");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  短信验证码已失效");
                return 0;
            }
            if (StringUtils.isEmpty(bean.getPhoneauthcode())) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("短信验证码不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  短信验证码为空");
                return 0;
            }
            CookieManager CM =(CookieManager) cookieCMObj;
        	Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies_ret = CM.getCookies();
			Iterator<com.gargoylesoftware.htmlunit.util.Cookie> it_ret = cookies_ret.iterator();
			while (it_ret.hasNext()) {
				com.gargoylesoftware.htmlunit.util.Cookie c = it_ret.next();
				String name = c.getName();
				String value = c.getValue();
            	System.out.println("name1="+name+"  value="+value);
            	webClient.getCookieManager().addCookie(c);
			}
			
			CookieManager CM2 =webClient.getCookieManager();
			cookies_ret = CM2.getCookies();
			it_ret = cookies_ret.iterator();
			while (it_ret.hasNext()) {
				com.gargoylesoftware.htmlunit.util.Cookie c = it_ret.next();
				String name = c.getName();
				String value = c.getValue();
            	System.out.println("name2="+name+"  value="+value);
			}
            
            
            MaterialBean pbean=(MaterialBean) materiallObj;
            md = pbean.getModel();
            Map<String, String> parameterMap = (HashMap<String, String>) parameterObj;
            CookieStore cookieStore = (CookieStore) cookieObj;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Accept-Language", "zh-cn");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E238 Safari/601.1");
            requestHeaderMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");
            requestHeaderMap.put("Proxy-Connection", "keep-alive");
            
            String dyCode = bean.getPhoneauthcode();
            if (StringUtils.isEmpty(dyCode)) {
            	bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("验证码不能为空！");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 验证码不能为空");
                return 0;
			}
            String applyUuid = parameterMap.get("applyUuid");
            //String operateSuccessDate = parameterMap.get("operateSuccessDate");
            String trackCode = parameterMap.get("trackCode");
            String cimCode = parameterMap.get("cimCode");
            String certNo = md.getCidcard();//证件号码
            String applyName = md.getCname();//用户真实姓名
            String applyTel = md.getCphone();//申请人手机号码
            String email = md.getCemail();//申请人邮箱
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyUuid="+applyUuid);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] trackCode="+trackCode);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] cimCode="+cimCode);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] certNo="+certNo);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyTel="+applyTel);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] email="+email);


            if (StringUtils.isEmpty(md.getCidexpirationtime())) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("身份证有效期不能为空！");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  身份证有效期不能为空");
                return 0;
            }
            String iddates[] = md.getCidexpirationtime().split(",");
            if (iddates.length != 2) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("身份证有效期不支持长期有效，请修改后申请");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 身份证有效期不支持长期有效，请修改后申请");
                return 0;
            }

            String applyIdBeginDate = iddates[0];//身份证有效期 开始 格式（yyyyMMdd）
            String applyIdEndDate = iddates[1];//身份证有效期 结束
            String cityId = pbean.getApplyCityId();//申请人所在城市ID
            String provinceId=pbean.getApplyProvinceId();//申请人所在省市
            String cardId = pbean.getApplyBankCardId();//申请卡类型ID
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyIdBeginDate="+applyIdBeginDate);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyIdEndDate="+applyIdEndDate);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] cityId="+cityId);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] provinceId="+provinceId);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] cardId="+cardId);

            
            
            if (StringUtils.isEmpty(cityId)||StringUtils.isEmpty(cardId)) {
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("卡片或者城市未匹配成功。");
				bean.setBusiJSON("fail");
                logger.info("卡片或者城市未匹配成功 idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] cityId="+cityId+" cardId="+cardId);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 卡片或者城市未匹配成功");
                return 0;
			}


            String applyHouseProvince = pbean.getApplyProvinceId();//住宅省市
            String applyHouseCity = pbean.getApplyCityId();//住宅城市
            String applyAddress1 =  md.getChome_detailaddress();//住宅区（如徐汇区）+住宅详细地址
            String applyPost = md.getChome_postcode();//邮政编码
            
            String applyHouseStatus = jiaoTong_House.get(md.getResidencestatus());//住宅状况 0自置无按揭  1自置有按揭 2商住两用 3租用 4与父母同住 5集体宿舍 6其他
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyHouseProvince="+applyHouseProvince);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyHouseCity="+applyHouseCity);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyAddress1="+applyAddress1);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyPost="+applyPost);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyHouseStatus="+applyHouseStatus);
            
            
            if (StringUtils.isEmpty(applyHouseStatus)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("无效的住宅状况！");
                bean.setBusiJSON("fail");
                logger.info("无效的住宅状况  idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyHouseStatus="+applyHouseStatus);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 无效的住宅状况");
                return 0;
            }
            String applyHouseBeginDate = "201308";//住宅入住年月
            int timeinjob = 0;
            // 初始化 Calendar 对象
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (jiaoTong_TimeInJob.get(md.getItimeinjob()) != null) {
                timeinjob = Integer.valueOf(jiaoTong_TimeInJob.get(md.getItimeinjob()));
            } else {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("无效的工作年限！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] timeinjob="+timeinjob);

                return 0;
            }
            calendar.add(Calendar.YEAR, -timeinjob);
            // 格式化显示
            String applyJobStartTime = (new SimpleDateFormat("yyyyMM")).format(calendar.getTime());
            if ("4".equals(applyHouseStatus)) {
                calendar.add(Calendar.YEAR, -6);
                // 格式化显示
                applyHouseBeginDate = (new SimpleDateFormat("yyyyMM")).format(calendar.getTime());

            } else {
                applyHouseBeginDate = applyJobStartTime;
            }
            
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyJobStartTime="+applyJobStartTime);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyHouseBeginDate="+applyHouseBeginDate);

            String applyHouseAreaCode = "";//住宅电话区号 (提示：如无住宅电话请填写直系亲属手机号码，区号不填写)
            String applyHousePhone = "";//住宅电话号码
            String homeTel="1";//联系方式 0直系亲属手机  1住宅电话
            if ((StringUtils.isEmpty(md.getChome_telnum())||md.getChome_telnum().length()<10)&&(StringUtils.isEmpty(md.getCemergencyphone())||md.getCemergencyphone().length()<11)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("紧急联系人电话不正确！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 紧急联系人和住宅电话为空");
                return 0;
            }
            
            String selfFamilyPhone="";
            if (md.getChome_telnum().length()>10) {
            	if (!md.getChome_telnum().contains("-")) {
                	selfFamilyPhone = md.getChome_telnum();
                	homeTel="0";
                } else {
                    String[] telnum = md.getChome_telnum().split("-");
                    applyHouseAreaCode = telnum[0];
                    applyHousePhone = telnum[1];
                }
			}else {
				homeTel="0";
				selfFamilyPhone=md.getCemergencyphone();
			}
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] homeTel="+ homeTel);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] selfFamilyPhone="+ selfFamilyPhone);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyHouseAreaCode="+ applyHouseAreaCode);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyHousePhone="+ applyHousePhone);
            
            String applyMarrige = jiaoTong_Marrige.get(md.getMaritalstatus());//婚姻状况(0 已婚 1未婚 2其他)
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyMarrige="+ applyMarrige);
            if (StringUtils.isEmpty(applyMarrige)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("婚姻状况不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 婚姻状况为空");
                return 0;
            }

            //1、硕士 2、博士及以上 3、本科 4、大专 5、高中、中专一下
            String applyEducation = jiaoTong_Education.get(md.getIdegree());//教育程度 (0研究生或以上,1大学（本科）,2大专,3高中,4中专,5初中及以下)
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyEducation="+ applyEducation);
            if (StringUtils.isEmpty(applyEducation)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("学历不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 学历为空");
                return 0;
            }
            
            //紧急联系人关系1.配偶 2.父母 3.子女 4.兄弟姐妹 5朋友
            //(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
            String linkManName = md.getFamilyname();//联系人姓名
            String linkManRelation = jiaoTong_Relation.get(md.getIfamilyties());//与您的关系(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
            String linkManTel = md.getCfamilyphonenum();//联系人移动电话
            
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] linkManTel="+ linkManTel);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] linkManRelation="+ linkManRelation);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] linkManName="+ linkManName);


            if (StringUtils.isEmpty(linkManName) || StringUtils.isEmpty(linkManRelation) || StringUtils.isEmpty(linkManTel) ) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("紧急联系人信息有误！");
                bean.setBusiJSON("fail");
                logger.info("紧急联系人信息有误  idcard[" + md.getCidcard() + "] "
                		+ "mobile[" + md.getCphone() + "] linkManName="
                		+linkManName+" linkManRelation="+linkManRelation+" linkManTel="+linkManTel);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 紧急联系人信息有误");
                return 0;
            }
            String wageIncome = md.getIannualsalary();//税前年薪收入 （单位W）
            if (StringUtils.isEmpty(wageIncome)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("年薪不能为空！");
                bean.setBusiJSON("fail");
                logger.info("年薪不能为空  idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] wageIncome="+wageIncome);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪为空");
                return 0;
            }
            if (!isNumeric(wageIncome)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("年薪必须为数字！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪必须为数字");
                return 0;
            }
            
            DecimalFormat df = new DecimalFormat("#0");
            double incomed=Double.parseDouble(wageIncome) * 10000;
            String applyIncome = df.format(incomed);//税前年薪收入（单位元）
            String preApproveId = "";
            if (incomed>9999999) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("年薪最多为9999999元，请核实您填写的年薪是否正确");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪最多为9999999元，请核实您填写的年薪是否正确");
                return 0;
			}
            if (incomed<10000) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("年薪最少为10000元");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪最少为10000元");
                return 0;
			}
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] wageIncome="+ wageIncome);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] incomed="+ incomed);
            
            String city1=pbean.getProvinceName()+" "+pbean.getCityName();
			logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] city1="+ city1);
            
			String city2=pbean.getApplyCompanyProvinceName()+" "+pbean.getApplyCompanyCityName();
			logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] city2="+ city2);

            String applyHasJob = "0";//就业状态：0全职/有薪, 1兼职,2家庭主妇,3退休人士,4自雇人士,5临时合同工,6学生,7失业人士,8其他
            String applyCompanyPropery = pbean.getApplyCompanyPropery();//单位性质: 0 机关事业单位,1社会团体,2国有企业,3三资企业,4上市公司,5民营,6私营,7个体
            String applyIndustryType = pbean.getApplyIndustryType();//行业类别:00 农、林、牧、渔业,01采掘业,02制造业,03电力、煤气及水的生产和供应业,04建筑业,05交通运输、仓储及邮电通信业,06信息传输、计算机服务及软件业,07批发和零售业,08住宿和餐饮业,09金融业,10房地产业,11租赁及商务服务业,12科学研究、技术服务业和地质勘查业,13水利、环境和公共设施管理业,14居民服务和其他服务业,15教育,16卫生、社会保障和社会福利业,17文化、体育和娱乐业,18公共管理及社会组织,19国际组织,21广告业,22电讯业,23银行业,24保险业,25法律业,26军事业,27出版业,28旅游观光业,29酒店业,30国家机关、政党机关和社会团体
            String applyJobPost = pbean.getApplyJobPost();//职位ID
            String applyCompanyName = md.getCcompanyname();//所属公司名称(工作单位名称)
            String applyCompanyDept = md.getCdepartmentname();//所属公司部门(任职部门)
            String applyCompanyProvince = pbean.getApplyCompanyProvince();//公司所属省份(单位省)
            String applyCompanyCity = pbean.getApplyCompanyCity();//公司所属城市(单位市)
            String applyCompanyAddress1 = md.getCcompany_detailaddress();//公司所属城市区(单位地区或县)
            String applyCompanyPost = md.getCcompany_postcode();//公司所属地邮编(邮政编码)
            

            
            
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyPropery="+ applyCompanyPropery);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyIndustryType="+ applyIndustryType);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyJobPost="+ applyJobPost);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyName="+ applyCompanyName);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyDept="+ applyCompanyDept);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyProvince="+ applyCompanyProvince);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyCity="+ applyCompanyCity);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyAddress1="+ applyCompanyAddress1);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyPost="+ applyCompanyPost);

            
            
            if (applyCompanyName.length()>13) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("公司名称不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 公司名称大于13个字符 applyCompanyName=" + applyCompanyName);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司名称大于13个字符 applyCompanyName=" + applyCompanyName);
                return 0;
			}
            if (applyCompanyDept.length()>13) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("任职部门不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 任职部门不能大于13个字符 applyCompanyDept=" + applyCompanyDept);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 任职部门不能大于13个字符 applyCompanyName=" + applyCompanyDept);
                return 0;
			}
            
            if (!applyHouseCity.equals(cityId)&&!applyCompanyCity.equals(cityId)) {
    			 bean.setBusiErrCode(-1);
                 bean.setBusiErrDesc("单位或住宅地址的城市必须有一个与您选择的所在城市相同");
                 bean.setBusiJSON("fail");
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 单位或住宅地址的城市必须有一个与您选择的所在城市相同 applyHouseCity="+applyHouseCity+" applyCompanyCity="+applyCompanyCity+" cityId="+cityId);
                 return 0;
			}

            if (StringUtils.isEmpty(md.getCcompany_telnum())) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("公司电话不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司电话为空");
                return 0;
            }
            String[] telnum = md.getCcompany_telnum().split("-");

            if (telnum.length != 2) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("公司电话格式不正确！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司电话格式不正确");
                return 0;
            }
            String applyCompanyAreaCode = telnum[0];//公司电话区号
            String applyCompanyPhone = telnum[1];//公司电话号码
            
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyAreaCode="+ applyCompanyAreaCode);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyCompanyPhone="+ applyCompanyPhone);
            
            String applyCompanySemicolon = "";//公司电话分号(选填)
            String applyPaymentMode = "1";//薪金支付方式 ：1 银行自动转帐支付,0不是银行自动转帐支付
            String applyAccountAddress = "1";//将信用卡、信用卡有关函件寄往本人 0住宅地址 1单位地址
            if ("2".equals(md.getIpostaddress())) {
                applyAccountAddress = "0";
            }
            
            String workPhone=applyCompanyAreaCode+applyCompanyPhone;
            String homePhone=applyHouseAreaCode+applyHousePhone;
            if (workPhone.equals(homePhone)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("您的住宅电话与单位电话重复，请您确认。真实有效的联系电话将有助您申请的审批。");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的住宅电话与单位电话重复，请您确认。真实有效的联系电话将有助您申请的审批。 workPhone="+workPhone+" homePhone="+homePhone);
            	return 0;
			}
            String workAddress=applyCompanyProvince+applyCompanyCity+applyCompanyAddress1;
            String houseAddress=applyHouseProvince+applyHouseCity+applyAddress1;
            if (houseAddress.equals(workAddress)) {
    			bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("您的住宅地址与单位地址重复，请您确认。真实有效的地址有助您申请的审批。");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的住宅地址与单位地址重复，请您确认。真实有效的地址有助您申请的审批。  houseAddress="+houseAddress+" workAddress="+workAddress);
            	return 0;
			}
            
            if (linkManTel.equals(applyTel)||linkManTel.equals(selfFamilyPhone)) {
    			bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("联系人手机号不能与本人手机号或直系亲属手机号相同");
				bean.setBusiJSON("fail");
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 resultDesc=联系人手机号不能与本人手机号或直系亲属手机号相同");
            	return 0;
			}
            
            
            if (StringUtils.isEmpty(applyCompanyPost)||applyCompanyPost.length()!=6) {
            	bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("邮编格式不正确");
				bean.setBusiJSON("fail");
                logger.info("邮编格式不正确 idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyCompanyPost="+applyPost);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 邮编格式不正确 applyCompanyPost"+applyCompanyPost);
                return 0;
			}
            
            if (StringUtils.isEmpty(applyPost)||applyPost.length()!=6) {
            	bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("邮编格式不正确");
				bean.setBusiJSON("fail");
                logger.info("邮编格式不正确 idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyPost="+applyPost);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 邮编格式不正确 applyPost="+applyPost);
                return 0;
			}
            
            parames.clear();
            parames.put("trackCode", trackCode);
            parames.put("userparam1", "");
            parames.put("userparam2", "");
            parames.put("userparam3", "");
            parames.put("applyUuid", applyUuid);
            parames.put("cimCode", cimCode);
            parames.put("cardCode", "");
            parames.put("applyName", applyName);
            parames.put("certNo", certNo);
            parames.put("cityId", cityId);
            parames.put("provinceId", provinceId);
            parames.put("applyTel", applyTel);   
            parames.put("cardId", "");
            parames.put("checkbox", "0");           
            parames.put("dyCode", dyCode);

            String urlparms= getUrlParams(parames);
            url=basePath+"/front/apply/identityCheck/checkSubmit.html?"+urlparms;
            HtmlPage htmlPage=webClient.getPage(url);
            waitRun(4);//不停顿跳过预审
            content=htmlPage.getWebResponse().getContentAsString();
            htmls.put("1-checkSubmit", content);
            
            if (content.contains("checkSubmit")) {
            	if (content.contains("errorTip")) {
            		String errorTip=htmlPage.getElementById("errorTip").asText();
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 errorTip="+errorTip);
                    bean.setBusiErrDesc(errorTip);
				}else {
                    bean.setBusiErrDesc("您填写的验证码或者身份证信息不正确请检查");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error 您填写的验证码或者身份证信息不正确请检查 ibankid=16 content="+content);
				}
            	bean.setBusiErrCode(-1);
                bean.setBusiJSON("fail");
            	return 0;
			}
            
            
            HtmlForm hf=htmlPage.getForms().get(0);
            String branchCode=hf.getInputByName("branchCode").getValueAttribute();
//            String cityId=hf.getInputByName("cityId").getValueAttribute();
            String actSex=hf.getInputByName("actSex").getValueAttribute();
            String cardCode=hf.getInputByName("cardCode").getValueAttribute();
//            String cardId=hf.getInputByName("cardId").getValueAttribute();
            String isGetCreditCard=hf.getInputByName("isGetCreditCard").getValueAttribute();
            String requestTimes=hf.getInputByName("requestTimes").getValueAttribute();

            url=basePath+"/front/apply/new/preCheckResultHandle.html?"
            		+ "applyUuid="+applyUuid+"&branchCode="+branchCode+"&cityId="+cityId+"&actSex="+actSex+"&cardCode="+cardCode
            		+"&isGetCreditCard="+isGetCreditCard+"&cardId=&requestTimes="+requestTimes;
            HtmlPage mainPage=webClient.getPage(url);
            content=mainPage.getWebResponse().getContentAsString();
            String resultDesc="";
			htmls.put("2-preCheckResultHandle", content);
            if (content.contains("txt_in2em")) {
            	List<HtmlElement> alist=null;
            	try {
            		alist =(List<HtmlElement>)mainPage.getByXPath("//p[@class='txt_in2em']");
				} catch (Exception e) {
					logger.error("get HtmlElement 异常", e);
				}
        		if (alist!=null&&alist.size()>0) {
        			resultDesc=alist.get(0).asText();
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 resultDesc="+resultDesc);
                    if (resultDesc.contains("预审")) {
                    	bean.setBusiErrCode(3);
                		bean.setBusiErrDesc("非常抱歉，您未通过交通银行的预审。");
                		bean.setCstatus("2");
                        bean.setBusiJSON("success");
                        saveSigleHtml("预审未通过", content, "", md, "预审未通过", 2);
                        waitRun(4);
                        return 1;
    				}else {
    					bean.setBusiErrCode(0);
                		bean.setBusiErrDesc(resultDesc);
                        bean.setBusiJSON("fail");
                        saveSigleHtml("预审未通过", content, "", md, "预审未通过", 2);
                        waitRun(4);
                        return 0;
					}
				}else if (content.contains("预审")) {
					bean.setBusiErrCode(3);
            		bean.setBusiErrDesc("非常抱歉，您未通过交通银行的预审。");
            		bean.setCstatus("2");
                    bean.setBusiJSON("success");
                    saveSigleHtml("预审未通过", content, "", md, "预审未通过", 2);
                    waitRun(4);
                    return 1;
				}
        		
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 预审失败，请稍后再试="+content);

        		bean.setBusiErrCode(0);
        		bean.setBusiErrDesc("预审失败，请稍后再试");
                bean.setBusiJSON("fail");
                saveSigleHtml("预审未通过", content, "", md, "预审未通过", 2);
                waitRun(4);
                return 0;
			}else {
				waitRun(2);
			}
            String age=mainPage.getElementById("age").getAttribute("value");
            String lstStatus=mainPage.getElementById("lstStatus").getAttribute("value");
            if ("1".equals(lstStatus)) {
            	goExit(webClient,bean,md);
            	return 0;
			}
            
            parames.clear();
            parames.put("applyUuid", applyUuid);
            parames.put("cardCode", cardCode);
            parames.put("branchCode", branchCode);
            parames.put("isGetCreditCard", isGetCreditCard);
            parames.put("cityId", cityId);
            parames.put("cardId", "");
            parames.put("actSex", actSex);
            parames.put("age", age);
            parames.put("trackCode", trackCode);
            parames.put("applyTel", applyTel);
            parames.put("applyName", applyName);
            url=basePath+"/front/apply/mobileApplyCommon/findProductList.json";
	        jsonContent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            preApproveId = "";
            JSONObject jobj=getCardInfo(jsonContent, cardId);
            if (jobj==null) {
            	resultDesc="该地区不支持此张信用卡办理";
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc(resultDesc);
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 该地区不支持此张信用卡办理 jsonContent="+jsonContent);
            	return 0;
			}
            
            String prdId=String.valueOf(jobj.getJSONArray("cardInfo").getJSONObject(0).get("prdId"));
            String isNeedMemberId=String.valueOf(jobj.get("isNeedMemberId"));
            String memberRule=String.valueOf(jobj.get("cardMemberRule"));
            String haveCardCode=String.valueOf(jobj.get("cardCode"));
            String cardMemberInfoTemp=String.valueOf(jobj.get("cardMemberInfo"));
            mainPage.getElementById("isNeedMemberId").setAttribute("value", isNeedMemberId);
            mainPage.getElementById("memberRule").setAttribute("value", memberRule);
            mainPage.getElementById("haveCardCode").setAttribute("value", haveCardCode);
            mainPage.getElementById("cardMemberInfoTemp").setAttribute("value", cardMemberInfoTemp);
            String emvOrgType=mainPage.getElementById("emvOrgType").getAttribute("value");
            
            
//          preApproveId=&trackCode=A022216511939&applyUuid=f3d88cc7-2ddd-4aff-b9b0-521be5245945&cityId=34&prdId=42&emvOrgType=&applyMemberNo=
			String applyMemberNo = "";
			// 这里才是需要卡片ID的时候
			parames.clear();
			parames.put("preApproveId", preApproveId);
			parames.put("trackCode", trackCode);
			parames.put("applyUuid", applyUuid);
			parames.put("cityId", cityId);
			parames.put("prdId", prdId);
			parames.put("emvOrgType", emvOrgType);
			parames.put("applyMemberNo", applyMemberNo);
			url = basePath+ "/front/apply/mobileApplyCommon/saveMobilePage.json";
			jsonContent = HpClientUtil.httpPost(url, requestHeaderMap, parames,	httpClient, localContext, "utf-8", requestConfig);
			JSONObject resultJson = new JSONObject(jsonContent);
			String result = String.valueOf(resultJson.get("result"));
			if (!"true".equals(result)) {
				String errorPage = String.valueOf(resultJson.get("errorPage"));
				logger.info("idcard[" + md.getCidcard() + "] mobile["+ md.getCphone()+ "] data error ibankid=16 goExit jsonContent="+ jsonContent);
				if (!StringUtils.isEmpty(errorPage)) {
					goExit(webClient, bean, md);
					return 0;
				} else {
					resultDesc = "卡片数据请求服务异常,请稍后再试！";
					bean.setBusiErrCode(0);
					bean.setBusiErrDesc(resultDesc);
					bean.setBusiJSON("fail");
					logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 卡片数据请求服务异常,请稍后再试！ jsonContent="+ jsonContent);
					return 0;
				}
			}
			String billType="EMAIL";//账单方式 (EMAIL 您的账单将以电子邮件形式发送至您填写的电子邮箱) 您的信用卡对账单和相关函件将寄送至信用卡卡片寄送地址
			
            String applyChName=mainPage.getElementById("applyChName").getAttribute("value");;
            String effect="";
			parames.clear();
            parames.put("applyUuid", applyUuid);
            parames.put("billType", billType);
            parames.put("applyEmail", email);
            parames.put("applyName", applyName);
            parames.put("applyChName", applyChName);
            parames.put("applyIdBeginDate", applyIdBeginDate);
            parames.put("applyIdEndDate", applyIdEndDate);
            parames.put("effect", effect);
            parames.put("applyEducation", applyEducation);
            parames.put("applyMarrige", applyMarrige);
            parames.put("wageIncome", wageIncome);
            parames.put("applyIncome", applyIncome);
            parames.put("applyPaymentMode", applyPaymentMode);
            parames.put("applyAccountAddress", applyAccountAddress);

            url=basePath+"/front/apply/new/saveBaseInfo.json";
            jsonContent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);

            resultJson=new JSONObject(jsonContent);
            result=String.valueOf(resultJson.get("result"));
            String errorPage=String.valueOf(resultJson.get("errorPage"));
            if ("false".equals(result)) {
				JSONArray errobjs=resultJson.getJSONArray("errors");
				resultDesc="";
            	for (int i = 0; i < errobjs.length(); i++) {
					resultDesc+=String.valueOf(errobjs.getJSONObject(i).get("msg"))+";";
				}
            	bean.setBusiErrCode(0);
				bean.setBusiErrDesc(resultDesc);
				bean.setBusiJSON("fail");
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16  resultDesc="+ resultDesc);
            	return 0;
			}else if (!StringUtils.isEmpty(errorPage)) {
				goExit(webClient,bean,md);
				return 0;
			}
            
            
            
//          parames.put("applyUuid", "");
//          parames.put("applyHouseAreaCode", "021");
//          parames.put("applyHousePhone", "64389188");
//          parames.put("selfFamilyPhone", "");
//          url=basePath+"/front/apply/mobileApplyCommon/dealHomeOrSelfPhone.json";
//	        String content2 = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
//	        System.out.println(content2);
          
          
            
//          String applyHouseProvince=mainPage.getElementById("pccc_applyHouseProvince").getAttribute("value");
//          String applyHouseCity=mainPage.getElementById("applyHouseCity").getAttribute("value");
            String lastApplyHousePhone="";
//          applyUuid=f3d88cc7-2ddd-4aff-b9b0-521be5245945&city=上海市 上海市
//          &applyHouseProvince=2&applyHouseCity=34&applyAddress1=东兰路万源路平南三四村14栋102&applyPost=200030&applyHouseStatus=3&applyHouseBeginDate=201406&homeTel=0&selfFamilyPhone=13479935182

          
          	parames.clear();
          	parames.put("applyUuid", applyUuid);
          	parames.put("city", city1);
          	parames.put("applyHouseProvince", applyHouseProvince);
          	parames.put("applyHouseCity", applyHouseCity);
          	parames.put("applyAddress1", applyAddress1);
          	parames.put("applyPost", applyPost);
          	parames.put("applyHouseStatus", applyHouseStatus);
          	parames.put("applyHouseBeginDate", applyHouseBeginDate);
          	parames.put("homeTel", homeTel);
          	parames.put("applyHouseAreaCode", applyHouseAreaCode);
          	parames.put("applyHousePhone", applyHousePhone);
          	parames.put("selfFamilyPhone", selfFamilyPhone);
          
          	url=basePath+"/front/apply/mobileApplyCommon/saveMobilePage.json";
          	jsonContent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
          	resultJson=new JSONObject(jsonContent);
          	result=String.valueOf(resultJson.get("result"));
          	errorPage=String.valueOf(resultJson.get("errorPage"));
//          	String errorCode=String.valueOf(resultJson.get("error"));
          
	        if ("true".equals(result)) {
				if (!StringUtils.isEmpty(selfFamilyPhone)&&selfFamilyPhone.length()==11) {
					lastApplyHousePhone=selfFamilyPhone;
				}
			}else if (!StringUtils.isEmpty(errorPage)) {
				goExit(webClient,bean,md);
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 goExit1 jsonContent="+ jsonContent);
				return 0;
			}else {
				resultDesc="住宅数据请求服务异常,请稍后再试";
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc(resultDesc);
				bean.setBusiJSON("fail");
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 住宅数据请求服务异常  jsonContent="+ jsonContent);
            	return 0;
			}
            
            parames.clear();
            parames.put("applyUuid", applyUuid);
            parames.put("applyHasJob", applyHasJob);
            parames.put("applyCompanyPropery", applyCompanyPropery);
            parames.put("applyIndustryType", applyIndustryType);
            parames.put("applyJobPost", applyJobPost);
            parames.put("applyCompanyName", applyCompanyName);
            parames.put("applyCompanyDept", applyCompanyDept);
            parames.put("city", city2);
            parames.put("applyCompanyProvince", applyCompanyProvince);
            parames.put("applyCompanyCity", applyCompanyCity);
            parames.put("applyCompanyAddress1", applyCompanyAddress1);
            parames.put("applyCompanyPost", applyCompanyPost);
            parames.put("applyCompanyAreaCode", applyCompanyAreaCode);
            parames.put("applyCompanyPhone", applyCompanyPhone);
            parames.put("applyCompanySemicolon", applyCompanySemicolon);
            parames.put("applyJobStartTime", applyJobStartTime);

            url=basePath+"/front/apply/mobileApplyCommon/saveMobilePage.json";
            jsonContent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            resultJson=new JSONObject(jsonContent);
            result=String.valueOf(resultJson.get("result"));
            errorPage=String.valueOf(resultJson.get("errorPage"));
            String lastApplyCompanySemicolon="";
            if ("true".equals(result)) {
            	lastApplyCompanySemicolon=applyCompanySemicolon;
			}else if (!StringUtils.isEmpty(errorPage)) {
				goExit(webClient,bean,md);
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 goExit2  jsonContent="+ jsonContent);
				return 0;
			}else {
				resultDesc="住宅数据请求服务异常,请稍后再试";
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc(resultDesc);
				bean.setBusiJSON("fail");
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 住宅数据请求服务异常  jsonContent="+ jsonContent);
            	return 0;
			}
            
            parames.clear();
            parames.put("applyUuid", applyUuid);
            parames.put("linkManName", linkManName);
            parames.put("linkManTel", linkManTel);
            parames.put("applyCompanySemicolon", lastApplyCompanySemicolon);
            parames.put("applyHousePhone", lastApplyHousePhone);
            parames.put("linkManRelation", linkManRelation);
            url=basePath+"/front/apply/mobileApplyCommon/saveMobilePage.json";
            jsonContent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            resultJson=new JSONObject(jsonContent);
            result=String.valueOf(resultJson.get("result"));
            errorPage=String.valueOf(resultJson.get("errorPage"));
            
            String prdIdForm="";
            String emvOrgTypeSubmit="";
            if ("true".equals(result)) {
            	prdIdForm=prdId;
            	emvOrgTypeSubmit=emvOrgType;
			}else if (!StringUtils.isEmpty(errorPage)) {
				goExit(webClient,bean,md);
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 goExit3  jsonContent="+ jsonContent);
				return 0;
			}else {
				resultDesc="住宅数据请求服务异常,请稍后再试";
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc(resultDesc);
				bean.setBusiJSON("fail");
				logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] data error ibankid=16 住宅数据请求服务异常  jsonContent="+ jsonContent);
            	return 0;
			}
            
            String applyEmailHead="";
            String applyEmailTail="";
            String graytest="mobileUI";
            
            URL urlPost = new URL(basePath+"/front/apply/new/cardInfoSave.html");
            WebRequest request=new WebRequest(urlPost, HttpMethod.POST);
            List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
            reqParam.add(new NameValuePair("applyUuid", applyUuid));
            reqParam.add(new NameValuePair("cityId", cityId));
            reqParam.add(new NameValuePair("preApproveId", preApproveId));
            reqParam.add(new NameValuePair("prdIdForm", prdIdForm));
            reqParam.add(new NameValuePair("emvOrgType", emvOrgType));
            reqParam.add(new NameValuePair("applyEmailHead", applyEmailHead));
            reqParam.add(new NameValuePair("applyEmailTail", applyEmailTail));
            reqParam.add(new NameValuePair("graytest", graytest));
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] cityId="+cityId);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] preApproveId="+preApproveId);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] prdIdForm="+prdIdForm);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] emvOrgType="+emvOrgType);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyEmailHead="+applyEmailHead);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] applyEmailTail="+applyEmailTail);
            logger.info("idcard["+ md.getCidcard()+ "] mobile["	+ md.getCphone()+ "] graytest="+graytest);

            
            request.setRequestParameters(reqParam);
            HtmlPage cardInfoSavePage = (HtmlPage)webClient.getPage(request);
            content=cardInfoSavePage.getWebResponse().getContentAsString();   
			htmls.put("3-cardInfoSave", content);
            if (!content.contains("成功递交")) {
            	String msg="您填写的信息有误，请确认后重新填写";
				if (content.contains("error-sm")) {
					try {
						//您提交失败，请返回重新申请
						org.jsoup.nodes.Document doc= Jsoup.parse(content);
						msg=doc.getElementsByClass("error-sm").get(0).text();
						if (content.contains("您提交失败，请返回重新申请")) {
							msg="您提交失败，请返回重新申请!如果重复出现此提示，请核实您填写的资料是否正确，然后等待交通银行办卡人员联系，如未联系您则代表您没有通过交通银行预审。";
						}
					} catch (Exception e) {
						msg="提交失败，您填写的信息有误，请确认后重新填写";
					}
				}
				saveHtml(md, htmls,"失败递交",1);
				paramsLogs(md, parames,"完善资料确认提交 -您填写的信息有误，请确认后重新填写");
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc(msg);
            	bean.setBusiJSON("fail");
            	logger.info("您填写的信息有误，请确认后重新填写 content="+content);
            	waitRun(4);
            	return 0;
            }
            paramsLogs(md, parames,"完善资料确认提交 -成功提交");
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("您的申请资料已成功递交，我们将尽快审核并可能通过02195559与您核实信息；为加快您的审核，在非工作时间我们也可能与您联系，还请注意接听。");
            bean.setBusiJSON("success");
            BankApplyListener.sendSucess(BankEnum.jiaotong,BankApplyStepEnum.submit_apply);
            waitRun(4);
            return 1;
        } catch (Exception e) {
        	paramsLogs(md, parames,"申请失败 -您填写的信息有误，请确认后重新填写");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申请失败，请稍后再试");
            bean.setBusiJSON("fail");
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyForTheJiaoTongBank 异常 jsonContent[" + jsonContent + "]", e);
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyForTheJiaoTongBank 异常 content[" + content + "]", e);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 applyForTheJiaoTongBank 异常");
			saveHtml(md, htmls,e.getMessage(),0);
			
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyForTheJiaoTongBank httpClient close 异常", e);
                }
            }
            if (webClient!=null) {
				webClient.close();
			}
        }
        return 0;
    }

	
	private static void saveSigleHtml(String errordesc,String htmlContent,String htmlUrl,MaterialModel md,String errorMsg,int type) {
		try {
			long times=System.currentTimeMillis();
			String filename=md.getCphone()+"-"+md.getCidcard()+"-"+errordesc+"-"+times+ ".html";
			ErrorRequestBean bean=new ErrorRequestBean(filename, md, htmlContent, htmlUrl,type,errorMsg,md.getCphone());
			BankApplyListener.sendError(BankEnum.jiaotong, BankApplyStepEnum.submit_apply, bean);
		} catch (Exception e) {
			
		}
		

	}
	
	private static void saveHtml(MaterialModel md, Map<String, String> htmls,String errorMsg,int type) {
		try {
			List<ErrorRequestBean> beans=new ArrayList<>();
			long times=System.currentTimeMillis();
			for (String key : htmls.keySet()) {
				String filename=md.getCphone()+"-"+md.getCidcard()+"-"+key+"-"+times+ ".html";
				//ErrorUtils.saveApplyFile(ErrorBankEnum.jiaotong, null, htmls.get(key), filename);
				ErrorRequestBean bean=new ErrorRequestBean(filename, md, htmls.get(key), null,type,errorMsg,md.getCphone());
				beans.add(bean);
			}
			BankApplyListener.sendError(BankEnum.jiaotong, BankApplyStepEnum.submit_apply, beans);
		} catch (Exception e) {
			for (String key: htmls.keySet()) {
				logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] "+key+" content["+htmls.get(key)+"]");
			}
		}
	}

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

	public static JSONObject getCardInfo(String jsonContent,String cid) {
		JSONObject jsonObj=new JSONObject(jsonContent);
		JSONArray cardList=jsonObj.getJSONArray("cardlist");
		for (int i = 0; i < cardList.length(); i++) {
			JSONObject jsonObject=cardList.getJSONObject(i);
			String cardid=(String) jsonObject.get("cardId");
			if (cid.equals(cardid)) {
				return jsonObject;
			}
		}
		return null;
	}
    
    
}
