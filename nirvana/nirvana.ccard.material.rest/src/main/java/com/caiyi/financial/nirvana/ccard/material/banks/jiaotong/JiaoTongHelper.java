package com.caiyi.financial.nirvana.ccard.material.banks.jiaotong;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.danga.MemCached.MemCachedClient;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiaoTongHelper {
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


    public static BufferedImage getJiaoTongVcode(MaterialBean bean,MemCachedClient client) throws IOException {
        BufferedImage localBufferedImage = null;
        try {
            CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");
            String url = "https://creditcardapp.bankcomm.com/member/CheckCode.svl?v=" + Math.random();
            localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);
            client.set(bean.getOrderid() + bean.getIbankid() + "_jiaotongJDCXCookie", cookieStore);
        } catch (Exception e) {
            logger.error(bean.getIapplyid() + bean.getIbankid() + " getJiaoTongVcode 异常 ", e);
        }
        return localBufferedImage;
    }
    
    //交通进度查询
    public static int jiaoTongJDCX(MaterialBean bean, MemCachedClient client) {
        String url = "";
        String content = "";
        try {
            Object cookieObj = client.get(bean.getOrderid() + bean.getIbankid() + "_jiaotongJDCXCookie");
            if (cookieObj == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码已失效，请重新获取");
                bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
                return 0;
            }
            if (StringUtils.isEmpty(bean.getImgauthcode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("图片验证码不能为空");
                bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
                return 0;
			}
            if (StringUtils.isEmpty(bean.getIdcardid())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("证件ID不能为空");
                bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
                return 0;
			}
            CookieStore cookieStore = (CookieStore) cookieObj;
            CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");


            url = "https://creditcardapp.bankcomm.com/member/apply/status/inquiry.html";
            Map<String, String> parames = new HashMap<String, String>();
            parames.put("certNo", bean.getIdcardid());
            parames.put("vcode", bean.getImgauthcode());

            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
//            logger.info(bean.getOrderid() + " content=" + content);
            org.jsoup.nodes.Element element = Jsoup.parse(content);
            if (content.contains("errormsg")) {
                String errormsg = element.getElementById("errormsg").text();
                logger.info(bean.getOrderid() + " certNo=" + bean.getIdcardid() + " vcode=" + bean.getImgauthcode() + " errormsg=" + errormsg);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(errormsg);
                bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\""+errormsg+"\"}");
                return 0;
            }

//    		很抱歉，没有查询到您的申请记录，我们会在收到申请资料后以短信的形式通知您，请耐心等候。 返 回
//    		卡片类型： 青年黑银联普卡 恭喜您，您申请的青年黑银联普卡于2016-01-27已通过审核，请留意查收我行寄给您的EMS，编号为1151624596899。您可登录creditcard.bankcomm.com查询卡片邮寄信息。 物流查询结果： 2016-01-30 09:54:10 上海市,投递并签收，签收人：他人收 站点代收周一送达 返 回
            org.jsoup.nodes.Element detail = element.getElementById("contentWrapper-detail");
            String result = detail.text().replaceAll("返 回", "");
            String applyCard=bean.getApplyBankCardId();
            logger.info(bean.getOrderid() + " applyCard["+applyCard+"] result=" + result);
            String [] rs=result.split("。");
            StringBuffer sb=new StringBuffer();
            for (int i = 0; i < rs.length; i++) {
            	String desc=rs[i];
            	if (!StringUtils.isEmpty(desc)&&desc.contains(applyCard)) {
            		sb.append(desc);
    			}
    		}
            String presult=sb.toString();
            logger.info(bean.getOrderid() + " applyCard["+applyCard+"] presult=" + presult);
            if (presult.contains("没有查询到您的申请记录")) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("没有查询到申请记录");
                bean.setBusiJSON("{\"resultcode\":3,\"resultdesc\":\"\",\"resean\":\""+result+"\"}");
                return 0;
            } else if (presult.contains("通过审核")) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("通过");
                bean.setCstatus("1");
                bean.setBusiJSON("{\"resultcode\":1,\"resultdesc\":\"" + result + "\",\"resean\":\"\"}");
            } else if (presult.contains("审核中")) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("审核中");
                bean.setBusiJSON("{\"resultcode\":0,\"resultdesc\":\""+result+"\",\"resean\":\"\"}");
                bean.setCstatus("0");
            } else if (presult.contains("未达我行发卡标准") || result.contains("不能接纳")) {
                bean.setBusiErrDesc("未通过");
                bean.setBusiJSON("{\"resultcode\":2,\"resultdesc\":\""+result+"\",\"resean\":\"\"}");
                bean.setCstatus("2");
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未查询到卡片申请状态，请联系惠刷卡客服咨询！");
                bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"未查询到卡片申请状态，请联系惠刷卡客服咨询！\"}");
                return 0;
            }
            return 1;
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询进度错误，请联系惠刷卡客服咨询！");
            bean.setBusiJSON("fail");
            logger.error(bean.getIapplyid() + " jiaoTongJDCX 异常 content=" + content, e);
        }
        return 0;
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
	
	public static int getBankMessage_JiaoTong_NEW(MaterialBean bean,MemCachedClient client) {
        CloseableHttpClient httpClient = null;
        String url = "";
        String content = "";
        MaterialModel md = bean.getModel();
        if (bean.getBusiErrCode() == 0) {
            return 0;
        }
        WebClient webClient =null;
        try {
			webClient = new WebClient();
            Map<String, String> parames = new HashMap<String, String>();
            Map<String, String> transferParameter = new HashMap<String, String>();
			CookieStore cookieStore = null;
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.setJavaScriptTimeout(50000);
			webClient.getOptions().setCssEnabled(false);
			webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setTimeout(30000);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			httpClient = HttpClients.createDefault();
			cookieStore = new BasicCookieStore();
			new JTConectionListener(webClient);
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute("http.cookie-store", cookieStore);
			webClient.addRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			webClient.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
			webClient.addRequestHeader("Connection", "Keep-Alive");
			webClient.addRequestHeader("Host", "creditcardapp.bankcomm.com");
			webClient.addRequestHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			Map<String, String> requestHeaderMap = new HashMap<String, String>();
			requestHeaderMap.put("Accept-Language",	"zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			requestHeaderMap.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
			requestHeaderMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			requestHeaderMap.put("Connection", "Keep-Alive");
			requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");

			RequestConfig.custom().setConnectTimeout(30000);
			RequestConfig.custom().setSocketTimeout(30000);
			RequestConfig.custom().setConnectionRequestTimeout(30000);
			RequestConfig requestConfig = RequestConfig.custom().build();
			HtmlPage page = webClient.getPage("https://creditcardapp.bankcomm.com/applynew/front/apply/track/record.html?trackCode=A022216511939");
			waitRun(3);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "]0 JiaoTongHelper.curl==========="+ JiaoTongHelper.curl);
			JiaoTongHelper.curl="";
			content=page.asXml();
//			try {
//				String purl="https://creditcardapp.bankcomm.com"+page.getElementById("pccc_select_img").getAttribute("src");
//				webClient.getPage(purl);
//				logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] purl=" + purl);
//			} catch (Exception e) {
//				logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] load pic error",e);
//			}
			CookieManager CM = webClient.getCookieManager();
            setClientCookies(cookieStore, CM);
			url="https://creditcardapp.bankcomm.com/applynew/front/apply/identityCount/record.json?trackCode=A022216511939&length=1";
	        String content1 = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
	        logger.info("trackCode=A022216511939   content="+content1);

			String read = "";
			String trackCode = "A022216511939";
			String cimCode = "";
			String userparam1 = "";
			String userparam2 = "";
			String userparam3 = "";
			String mobileNo = "";
			String recomChannelType = "";
			try {
				read = page.getElementById("mrm_cbRead").getAttribute("value");
	            cimCode = page.getElementById("pccc_cimCode").getAttribute("value");
				recomChannelType =page.getElementById("pccc_recomChannelType_put").getAttribute("value");
				userparam1 =page.getElementsByName("userparam1").get(0).getAttribute("value");
				userparam2 =page.getElementsByName("userparam2").get(0).getAttribute("value");
				userparam3 =page.getElementsByName("userparam3").get(0).getAttribute("value");
				mobileNo =page.getElementsByName("mobileNo").get(0).getAttribute("value");

				logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] recomChannelType=" +
				recomChannelType+" read="+read+" cimCode="+cimCode+" userparam1="+userparam1+" userparam2="+userparam2+" userparam3="+userparam3);

			} catch (Exception e) {
				e.printStackTrace();
			}

            String certNo = md.getCidcard();//证件号码
            String cretName = md.getCname();//用户真实姓名
            String cardId = bean.getApplyBankCardId();//申请卡类型ID
            String org = "C";//银行卡办理默认C
            String cityId = bean.getApplyCityId();//申请人所在城市ID
            String provinceId = bean.getApplyProvinceId();//申请人所在省份ID
            String certType = "0";//申请人 证件类型 0 身份证
            String phone = md.getCphone();//申请人手机号码
            if (StringUtils.isEmpty(cardId)||StringUtils.isEmpty(cityId)) {
            	 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("卡片或者城市未匹配成功。");
                 bean.setBusiJSON("fail");
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 卡片或者城市未匹配成功。");
                 return 0;
			}

            //{certNo:身份证}  {cardId：卡片id}  {cityId:城市id}
            //https://creditcardapp.bankcomm.com/applynew/front/apply/common/productVal.json   第一步接口 参数 （certNo=431022199008254217&cardId=17&org=C&cimCode=&cityId=34&customer=1）
            parames.put("certNo", certNo);
            parames.put("cardId", cardId);
            parames.put("org", org);
            parames.put("cityId", cityId);
            parames.put("customer", "1");
            parames.put("cimCode", cimCode);


            //certNo="+certNo+"&cardId="+cardId+"&org="+org+"&cimCode=&cityId="+cityId+"&customer=1
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/common/productVal.json";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            if (StringUtils.isEmpty(content)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您的身份证信息未通过验证，请核对。");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 验证办卡地不成功 content=" + content + " certNo=" + certNo + " cardId=" + cardId + " cityId=" + cityId);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 验证办卡地不成功");
                return 0;
            }
            JSONObject idcardResult = new JSONObject(content);
            String irflag = String.valueOf(idcardResult.get("flag"));
            if (!irflag.equals("true")) {
                String  busiErrDesc= String.valueOf(idcardResult.get("errorType"));
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(busiErrDesc);
                if ("01".equals(busiErrDesc)) {
                    bean.setBusiErrDesc("您不能申请青年卡，请重新选择！");
				}else if ("02".equals(busiErrDesc)) {
					bean.setBusiErrDesc("您选择的城市不开放该卡产品，请重新选择！");
				}else if ("03".equals(busiErrDesc)) {
					bean.setBusiErrDesc("您选择的卡产品不能参加该活动，请重新选择！");
				}
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 验证办卡地不成功 content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 busiErrDesc="+busiErrDesc);
                return 0;
            }


            parames.clear();
            parames.put("trackCode", trackCode);
            parames.put("cimCode", cimCode);
            parames.put("recomType", "");
            parames.put("recomNumber", "");
            parames.put("recomChannelType", recomChannelType);
            parames.put("userparam1", userparam1);
            parames.put("userparam2", userparam2);
            parames.put("mobileNo", mobileNo);
            parames.put("applyName", cretName);
            parames.put("provinceId", provinceId);
            parames.put("cityId", cityId);
            parames.put("certType", certType);
            parames.put("certNo", certNo);
            parames.put("Read", read);
            parames.put("cardId", cardId);
            parames.put("org", org);
            parames.put("applyMemberNo", "");

            String urlparms= getUrlParams(parames);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/identityVerify.html?"+urlparms;
            HtmlPage page2=webClient.getPage(url);
            content=page2.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");

            if (StringUtils.isEmpty(content)) {
                paramsLogs(md, parames,"短信验证码获取失败");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码获取失败，请检查你填写的参数，或稍后再试");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 短信验证码获取失败");
                return 0;
			}
            Pattern p = Pattern.compile("\\s*|t|r|n");
            Matcher m = p.matcher(content);
            String dest = m.replaceAll("");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] dest=" + dest);
            if ("0".equals(dest)) {
                //您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。温馨提示：如您已销户，可通过我行营业网点申请办理，或在销户满6个月后通过网上申请办理。
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您已经是交通银行信用卡持卡人客户");
                return 0;
            } else if ("2".equals(dest)) {
            	paramsLogs(md, parames,"您填写的信息有误，请确认后重新填写-验证身份证");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您填写的信息有误，请确认后重新填写-验证身份证");
                return 0;
            } else if ("3".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("目前在线申请信用卡业务暂不开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务。");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 目前在线申请信用卡业务暂不开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务。");
				return 0;
			} else if ("5".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("目前在线申请信用卡业务暂只面向持有二代身份证的客户开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 目前在线申请信用卡业务暂只面向持有二代身份证的客户开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务");
				return 0;
			} else if ("6".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您已经是交通银行信用卡持卡人客户");
				return 0;
			} else if ("7".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("目前在线申请信用卡业务暂只面向18周岁（含）－65周岁（含）且持二代身份证的客户开放。");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 目前在线申请信用卡业务暂只面向18周岁（含）－65周岁（含）且持二代身份证的客户开放。");
				return 0;
			}else if ("NEW_013".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("您访问的页面不存在 ");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您访问的页面不存在");
				return 0;
			}

            if (content.contains("uniformRecomEnter")) {
                String msg="";
				org.jsoup.nodes.Element element = Jsoup.parse(content);
				msg = element.select(".blueTip").first().html();
				logger.info("idcard[" + md.getCidcard() + "] mobile["+ md.getCphone() + "] msg=" + msg);
	            waitRun(3);
	            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "]-1 JiaoTongHelper.curl==========="+ JiaoTongHelper.curl);
				JiaoTongHelper.curl="";
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(msg);
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  msg=" + msg);
                return 0;
            }

            org.jsoup.nodes.Element element = Jsoup.parse(content);
            String pccc_uuid = element.getElementById("pccc_uuid").val();
            //是否进行过预批核
            String preCheckStatus = element.getElementById("pccc_preCheckStatus").val();
            String isNeedPreCheck = element.getElementById("pccc_isNeedPreCheck").val();
            String cardType = element.getElementById("mrm_hdCardType").val();


            setClientCookies(cookieStore, CM);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第二步 获取电话验证码-----------------------pccc_uuid=" + pccc_uuid);
            parames.clear();
            parames.put("dyName", cretName);
            parames.put("dyTel", phone);
            parames.put("dyIdType", certType);
            parames.put("dyIdNo", certNo);
            parames.put("dyFun", "1");
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            //--
            parames.put("trackCode", trackCode);

            //dyName 真实姓名 ,dyTel 电话号码,dyIdType 证件ID，dyIdNo 身份证ID，pccc_uuid 验证uid
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/dynamicCode/sendDynamicCode.json";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            JSONObject resultObj = new JSONObject(content);
            String flag = String.valueOf(resultObj.get("flag"));
            String serial = String.valueOf(resultObj.get("serial"));
            String message = String.valueOf(resultObj.get("message"));
            if (flag.equals("true")) {
                transferParameter.put("preCheckStatus", preCheckStatus);
                transferParameter.put("isNeedPreCheck", isNeedPreCheck);
                transferParameter.put("cardType", cardType);
                transferParameter.put("pccc_uuid", pccc_uuid);
                transferParameter.put("trackCode", trackCode);
                transferParameter.put("cimCode", cimCode);
                transferParameter.put("userparam1", userparam1);
                transferParameter.put("userparam2", userparam2);
                transferParameter.put("userparam3", userparam3);
                transferParameter.put("mobileNo", mobileNo);
                transferParameter.put("read", read);

                client.set(md.getCidcard() + md.getCphone() + "_jiaotongCookies_CM", CM);
                client.set(md.getCidcard() + md.getCphone() + "_jiaotongMaterialBean", bean);
                client.set(md.getCidcard() + md.getCphone() + "_jiaotongCookies", cookieStore);
                client.set(md.getCidcard() + md.getCphone() + "_jiaotongParameter", transferParameter);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 短信发送成功,短信编号：" + serial);
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("短信发送成功,短信编号：" + serial);
                bean.setBusiJSON("success");
                return 1;
            } else {
            	paramsLogs(md, parames,"获取短信验证码失败");
            	String newIsQuit = String.valueOf(resultObj.get("newIsQuit"));
            	if ("true".equals(newIsQuit)||StringUtils.isEmpty(message)) {
					message="该电话号码已于6个月内有申请记录，无法重复申请。";
				}
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 获取短信验证码失败 message=" + message+" content="+content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  获取短信验证码失败message=" + message);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(message);
                bean.setBusiJSON("fail");
            }
        } catch (Exception e) {
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  getBankMessage_JiaoTong 异常");
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] getBankMessage_JiaoTong 异常 content=" + content, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信验证码获取失败，请稍后再试");
            bean.setBusiJSON("fail");
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


	public static int applyForTheJiaoTongBank_NEW(MaterialBean bean,MemCachedClient client) {
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
		webClient.addRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		webClient.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
		webClient.addRequestHeader("Connection", "Keep-Alive");
		webClient.addRequestHeader("Host", "creditcardapp.bankcomm.com");
		webClient.addRequestHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		new JTConectionListener(webClient);
        try {
        	Object cookieCMObj= client.get(md.getCidcard() + md.getCphone() + "_jiaotongCookies_CM");
            Object cookieObj = client.get(md.getCidcard() + md.getCphone() + "_jiaotongCookies");
            Object parameterObj = client.get(md.getCidcard() + md.getCphone() + "_jiaotongParameter");
            Object materiallObj = client.get(md.getCidcard() + md.getCphone() + "_jiaotongMaterialBean");
            if (cookieObj == null || parameterObj == null || materiallObj == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码已失效");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  短信验证码已失效");
                return 0;
            }
            if (StringUtils.isEmpty(bean.getPhoneauthcode())) {
                bean.setBusiErrCode(0);
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
            
//			monitorPro=false; JSESSIONID=0000bM2MgDs9FxitHrPTfqlBIJH:18dp9o6bp; _tcs=b7b2f89a-e0ac-4b69-ab68-c65af41c16f0; applynew_session_sticky=e733274f3017039bfa560ae8389f5d0f; _WIFI_MERCHANT_ID=null; WCM_ST_ID=f817ef21bbaeb168b346715dfc4c3e2d; CHANNEL=normal; NCtrack_2016_First_Time_10=1461572834387; NCtrack_2016_Cookie_Global_User_Id=_ck16042516271414279376447318578; NCtrack_2016_Return_Time_10=1461572834387; NCtrack_2016_Msrc_Channel_10=market_type_other%7C2016-04-25; 
            
            
            
            
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
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");
            String dyCode = bean.getPhoneauthcode();
            String preCheckStatus = parameterMap.get("preCheckStatus");
            String isNeedPreCheck = parameterMap.get("isNeedPreCheck");
            String cardType = parameterMap.get("cardType");
            String pccc_uuid = parameterMap.get("pccc_uuid");
            String trackCode = "A022216511939";
            String cimCode = parameterMap.get("cimCode");
            String userparam1 = parameterMap.get("userparam1");
            String userparam2 = parameterMap.get("userparam2");
            String userparam3 = parameterMap.get("userparam3");
            String mobileNo = parameterMap.get("mobileNo");
            String read = parameterMap.get("read");


            String certNo = md.getCidcard();//证件号码
            String cretName = md.getCname();//用户真实姓名
            String phone = md.getCphone();//申请人手机号码
            String certType = "0";//申请人 证件类型 0 身份证
            String org = "C";//银行卡办理默认C


            String email = md.getCemail();//申请人邮箱
            String recomType = "";//推荐类型 空代表没有推荐  01 电话 02其他
            String recomNumber = ""; //推荐值

            if (StringUtils.isEmpty(md.getCidexpirationtime())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份证有效期不能为空！");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  身份证有效期不能为空");
                return 0;
            }
            String iddates[] = md.getCidexpirationtime().split(",");
            if (iddates.length != 2) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份证有效期不支持长期有效，请修改后申请");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 身份证有效期不支持长期有效，请修改后申请");
                return 0;
            }

            String applyIdBeginDate = iddates[0];//身份证有效期 开始 格式（yyyyMMdd）
            String applyIdEndDate = iddates[1];//身份证有效期 结束

            String cityId = pbean.getApplyCityId();//申请人所在城市ID
            String cardId = pbean.getApplyBankCardId();//申请卡类型ID
            
            if (StringUtils.isEmpty(cityId)||StringUtils.isEmpty(cardId)) {
            	 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("卡片或者城市未匹配成功。");
                 bean.setBusiJSON("fail");
                 logger.info("卡片或者城市未匹配成功 idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] cityId="+cityId+" cardId="+cardId);
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 卡片或者城市未匹配成功");
                 return 0;
			}

            String applyNativeProvince = pbean.getApplyNativeProvince();//籍贯 省份
            String applyNativeCity = pbean.getApplyNativeCity();//籍贯 城市

            String applyHouseProvince = pbean.getApplyProvinceId();//住宅省市
            String applyHouseCity = pbean.getApplyCityId();//住宅城市
            String applyAddress1 = pbean.getApplyAddress();//住宅区（如徐汇区）

            String applyAddress2 = md.getChome_detailaddress();//住宅详细地址
            String applyPost = md.getChome_postcode();//邮政编码
            //（1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它）


            String applyHouseStatus = jiaoTong_House.get(md.getResidencestatus());//住宅状况 0自置无按揭  1自置有按揭 2商住两用 3租用 4与父母同住 5集体宿舍 6其他
            if (StringUtils.isEmpty(applyHouseStatus)) {
                bean.setBusiErrCode(0);
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
                bean.setBusiErrCode(0);
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

            String applyHouseAreaCode = "";//住宅电话区号 (提示：如无住宅电话请填写直系亲属手机号码，区号不填写)
            String applyHousePhone = "";//住宅电话号码

            if (StringUtils.isEmpty(md.getChome_telnum())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("住宅电话不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 住宅电话为空");
                return 0;
            }

            if (!md.getChome_telnum().contains("-")) {
                applyHousePhone = md.getChome_telnum();
            } else {
                String[] telnum = md.getChome_telnum().split("-");
                applyHouseAreaCode = telnum[0];
                applyHousePhone = telnum[1];
            }

            String applyCarNo = "";//车牌号码(如无，可不填)

            String applyMarrige = jiaoTong_Marrige.get(md.getMaritalstatus());//婚姻状况(0 已婚 1未婚 2其他)
            if (StringUtils.isEmpty(applyMarrige)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("婚姻状况不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 婚姻状况为空");
                return 0;
            }

            String applyFendnum = "0";//供养人数(0-4 个,5代表5个以上)
            //1、硕士 2、博士及以上 3、本科 4、大专 5、高中、中专一下
            String applyEducation = jiaoTong_Education.get(md.getIdegree());//教育程度 (0研究生或以上,1大学（本科）,2大专,3高中,4中专,5初中及以下)
            if (StringUtils.isEmpty(applyEducation)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("学历不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 学历为空");
                return 0;
            }
            String applyPriSchoolName = md.getCprimaryschoolname();//毕业小学名称
            if (StringUtils.isEmpty(applyPriSchoolName)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("毕业小学名称不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 毕业小学名称为空");
                return 0;
            }
            
            if (applyPriSchoolName.length()>13) {
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("毕业小学名称不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 毕业小学名称不能大于13个字符  applyPriSchoolName=" + applyPriSchoolName);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 毕业小学名称不能大于13个字符 applyPriSchoolName=" + applyPriSchoolName);
                return 0;
			}
            
            //紧急联系人关系1.配偶 2.父母 3.子女 4.兄弟姐妹 5朋友
            //(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
            String linkManName = md.getFamilyname();//联系人姓名
            String linkManRelation = jiaoTong_Relation.get(md.getIfamilyties());//与您的关系(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)

            String linkManCompany = "未知";//联系人单位名称
            String linkManTel = md.getCfamilyphonenum();//联系人移动电话
            String linkManHouseAreaCode = "";//联系人住宅电话区号(选填)
            String linkManHousePhone = "";//联系人住宅电话号码(选填)

            if (StringUtils.isEmpty(linkManName) || StringUtils.isEmpty(linkManRelation)
                    || StringUtils.isEmpty(linkManTel) || StringUtils.isEmpty(linkManCompany)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("紧急联系人信息有误！");
                bean.setBusiJSON("fail");
                logger.info("紧急联系人信息有误  idcard[" + md.getCidcard() + "] "
                		+ "mobile[" + md.getCphone() + "] linkManName="
                		+linkManName+" linkManRelation="+linkManRelation+" linkManTel="+linkManTel+" linkManCompany="+linkManCompany);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 紧急联系人信息有误");
                return 0;
            }
            String actPubId = "41";//成功办卡，即享１００元刷卡金活动
            String presentId = "399";//１００元刷卡金活动

            String wageIncome = md.getIannualsalary();//税前年薪收入 （单位W）
            if (StringUtils.isEmpty(wageIncome)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪不能为空！");
                bean.setBusiJSON("fail");
                logger.info("年薪不能为空  idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] wageIncome="+wageIncome);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪为空");
                return 0;
            }
            if (!isNumeric(wageIncome)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪必须为数字！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪必须为数字");
                return 0;
            }
            
            DecimalFormat df = new DecimalFormat("#0");
            String otherIncome = "0";//税前年其他收入（单位W）
            double incomed=Double.parseDouble(wageIncome) * 10000;
            String applyIncome = df.format(incomed);//税前年薪收入（单位元）
            String applyOtherinCome = "0";//税前年其他收入（单位元）
            String preApproveId = "0";

            if (incomed>9999999) {
           	    bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪最多为9999999元，请核实您填写的年薪是否正确");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪最多为9999999元，请核实您填写的年薪是否正确");
                return 0;
			}
            if (incomed<10000) {
           	    bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪最少为10000元");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪最少为10000元");
                return 0;
			}
            

            String applyHasJob = "0";//就业状态：0全职/有薪, 1兼职,2家庭主妇,3退休人士,4自雇人士,5临时合同工,6学生,7失业人士,8其他

            String applyCompanyPropery = pbean.getApplyCompanyPropery();//单位性质: 0 机关事业单位,1社会团体,2国有企业,3三资企业,4上市公司,5民营,6私营,7个体
            String applyIndustryType = pbean.getApplyIndustryType();//行业类别:00 农、林、牧、渔业,01采掘业,02制造业,03电力、煤气及水的生产和供应业,04建筑业,05交通运输、仓储及邮电通信业,06信息传输、计算机服务及软件业,07批发和零售业,08住宿和餐饮业,09金融业,10房地产业,11租赁及商务服务业,12科学研究、技术服务业和地质勘查业,13水利、环境和公共设施管理业,14居民服务和其他服务业,15教育,16卫生、社会保障和社会福利业,17文化、体育和娱乐业,18公共管理及社会组织,19国际组织,21广告业,22电讯业,23银行业,24保险业,25法律业,26军事业,27出版业,28旅游观光业,29酒店业,30国家机关、政党机关和社会团体
            String applyJobPost = pbean.getApplyJobPost();//职位ID


            String applyCompanyName = md.getCcompanyname();//所属公司名称(工作单位名称)
            String applyCompanyDept = "技术部";//所属公司部门(任职部门)
            String applyCompanyProvince = pbean.getApplyCompanyProvince();//公司所属省份(单位省)
            String applyCompanyCity = pbean.getApplyCompanyCity();//公司所属城市(单位市)
            String applyCompanyAddress1 = pbean.getApplyCompanyAddress();//公司所属城市区(单位地区或县)
            String applyCompanyAddress2 = md.getCcompany_detailaddress();//公司详细地址(单位详细地址)
            String applyCompanyPost = md.getCcompany_postcode();//公司所属地邮编(邮政编码)
            
            if (applyCompanyName.length()>13) {
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("公司名称不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 公司名称大于13个字符 applyCompanyName=" + applyCompanyName);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司名称大于13个字符 applyCompanyName=" + applyCompanyName);
                return 0;
			}
            if (applyCompanyDept.length()>13) {
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("任职部门不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 任职部门不能大于13个字符 applyCompanyDept=" + applyCompanyDept);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 任职部门不能大于13个字符 applyCompanyName=" + applyCompanyDept);
                return 0;
			}
            
            if (!applyHouseCity.equals(cityId)&&!applyCompanyCity.equals(cityId)) {
            	 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("单位或住宅地址的城市必须有一个与您选择的所在城市相同");
                 bean.setBusiJSON("fail");
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 单位或住宅地址的城市必须有一个与您选择的所在城市相同 applyHouseCity="+applyHouseCity+" applyCompanyCity="+applyCompanyCity+" cityId="+cityId);
                 return 0;
			}

            if (StringUtils.isEmpty(md.getCcompany_telnum())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("公司电话不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司电话为空");
                return 0;
            }
            String[] telnum = md.getCcompany_telnum().split("-");

            if (telnum.length != 2) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("公司电话格式不正确！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司电话格式不正确");
                return 0;
            }
    		if (applyHousePhone.equals(linkManTel)) {
    			bean.setBusiErrCode(0);
                bean.setBusiErrDesc("直系亲属电话不能与联系人电话一致");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 直系亲属电话不能与联系人电话一致 applyHousePhone=" + applyHousePhone +" linkManTel="+linkManTel);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 直系亲属电话不能与联系人电话一致");
                return 0;
    		}
            String applyCompanyAreaCode = telnum[0];//公司电话区号
            String applyCompanyPhone = telnum[1];//公司电话号码
            String applyCompanySemicolon = "";//公司电话分号(选填)
            String applyPaymentMode = "1";//薪金支付方式 ：1 银行自动转帐支付,0不是银行自动转帐支付
            String applyFrontJobMonths = "0";//前一单位工龄(单位月)
            String applyIsBocomClient = "0";//您是否为交通银行客户 1是 0否
            String applyOtherBankName = "";//您已持有的贷记卡发卡银行名称

            String applyAccountAddress = "1";//将信用卡、信用卡有关函件寄往本人 0住宅地址 1单位地址
            if ("2".equals(md.getIpostaddress())) {
                applyAccountAddress = "0";
            }


            parames.put("dyName", cretName);
            parames.put("dyTel", phone);
            parames.put("dyIdType", certType);
            parames.put("dyIdNo", certNo);
            parames.put("dyCode", dyCode);
            parames.put("dyFun", "1");
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            //--
            parames.put("trackCode", trackCode);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/dynamicCode/verifyDynamicCode.json";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            JSONObject resultObj = new JSONObject(content);
            String flag = String.valueOf(resultObj.get("flag"));
            int errorCount = resultObj.getInt("errorCount");
            String message = String.valueOf(resultObj.get("message"));

            if (flag.equals("true")) {
                preCheckStatus = "1";
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(message);
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 获取短信验证码失败 message=" + message);
                if (errorCount >= 5) {
                    bean.setBusiErrDesc("您的移动电话验证码已错误输入5次，请重新获取验证码");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的移动电话验证码已错误输入5次，请重新获取验证码");
                } else {
                    bean.setBusiErrDesc("您的移动电话验证码已错误输入" + errorCount + "次，请重新输入");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的移动电话验证码已错误输入" + errorCount + "次，请重新输入");
                }
                return 0;
            }

            if ("1".equals(isNeedPreCheck)) {
                url = "https://creditcardapp.bankcomm.com/applynew/front/apply/preCheck.json";
                parames.clear();
                parames.put("applyUuid", pccc_uuid);
                content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] preCheck="+content);
                try {
                	
                	for (int i = 0; i < 5; i++) {
                		url="https://creditcardapp.bankcomm.com/applynew/front/apply/getPreCheckResult.json";
                        parames.clear();
                        parames.put("applyUuid", pccc_uuid);
                        content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
                        JSONObject preCheckResult = new JSONObject(content);
                    	String pr = String.valueOf(preCheckResult.get("result"));
                    	if ("fail".equals(pr)) {
                    		bean.setBusiErrCode(3);
                    		bean.setBusiErrDesc("非常抱歉，您未通过交通银行的预审。");
                    		bean.setCstatus("2");
                            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 非常抱歉，您未通过交通银行的预审。");
                            bean.setBusiJSON("success");
                            return 1;
    					}else if ("success".equals(pr)) {
							break;
						}else {
							Thread.sleep(2000);
						}
                	}
                	
				} catch (Exception e) {
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 getPreCheckResult");
	                logger.info(" getPreCheckResult idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] content="+content);
				}
            }else {
                logger.info(" isNeedPreCheck idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] isNeedPreCheck="+isNeedPreCheck);
            }

            //进入完善资料步骤
            parames.clear();
            parames.put("trackCode", trackCode);
            parames.put("cimCode", cimCode);
            parames.put("userparam1", userparam1);
            parames.put("userparam2", userparam2);
            parames.put("userparam3", userparam3);
            parames.put("mobileNo", mobileNo);
            parames.put("applyTel", phone);
            parames.put("dyCode", dyCode);
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            parames.put("applyName", cretName);
            parames.put("certType", certType);
            parames.put("certNo", certNo);
            parames.put("cityId", cityId);
            parames.put("isNeedPreCheck", isNeedPreCheck);
            parames.put("preCheckStatus", preCheckStatus);
            parames.put("applyEmail", email);
            parames.put("recomType", recomType);
            parames.put("recomNumber", recomNumber);
            parames.put("CardType", cardType);
            parames.put("Read", read);
            parames.put("cardId", cardId);
            parames.put("org", org);
            parames.put("applyMemberNo", "");
            
            String urlparms= getUrlParams(parames);
            
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/personalInfo.html?"+urlparms;
            
            HtmlPage page=webClient.getPage(url);
            content=page.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            waitRun(3);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "]1 JiaoTongHelper.curl==========="+ JiaoTongHelper.curl);
			JiaoTongHelper.curl="";
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第二步 end 进入资料完善页面-----------------------");
            htmls.put("1-ziliaowanshan", content);
//            htmls.put("资料完善提交", content);
            if (!content.contains("mrm_txtBirthDate")) {
            	paramsLogs(md, parames," 进入资料完善页面 您填写的信息有误，请确认后重新填写");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 资料完善提交 content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 资料完善提交");
                return 0;
            }

            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第三步 begin 资料完善提交-----------------------");
            org.jsoup.nodes.Element element = Jsoup.parse(content);
            String cardCode = "";
            String applyChName = "";
            String applyBirdate = element.getElementById("mrm_txtBirthDate").val();//出生日
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyBirdate=" + applyBirdate);

            if (content.contains("actId")) {
                Elements radios = element.getElementsByAttributeValue("name", "actId");
                for (int i = 0; i < radios.size(); i++) {
                    org.jsoup.nodes.Element tempRadio = radios.get(i);
                    String checked = tempRadio.attr("checked");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] radio id=" + tempRadio.attr("id")+" value="+tempRadio.attr("value"));
                    if ("checked".equals(checked) || "true".equals(checked)) {
                    	actPubId=tempRadio.val();
                    }
                }
			}
            if (content.contains("presentId")) {
            	presentId = element.getElementsByAttributeValue("name", "presentId").first().val();
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] presentId=" +presentId);
			}else {
				presentId="";
			}
            applyChName = element.getElementsByAttributeValue("name", "applyChName").first().val();
            cimCode = element.getElementsByAttributeValue("name", "cimCode").first().val();
            cardCode = element.getElementsByAttributeValue("name", "cardCode").first().val();
            //trackCode = element.getElementsByAttributeValue("name", "trackCode").first().val();
            //userparam2=element.getElementsByAttributeValue("name", "userparam2").first().val();
            //userparam3=element.getElementsByAttributeValue("name", "userparam3").first().val();
            //mobileNo=element.getElementsByAttributeValue("name", "mobileNo").first().val();

            

            
            parames.clear();
            parames.put("applyUuid", pccc_uuid);
            parames.put("cimCode", cimCode);
            parames.put("cardCode", cardCode);
            parames.put("trackCode", trackCode);
            parames.put("cityId", cityId);//申请人所在城市ID
            parames.put("applyName", cretName);//申请人正式姓名
            parames.put("applyChName", applyChName);
            parames.put("certType", certType);//申请证件类型
            parames.put("certNo", certNo);//申请证件类型
            parames.put("applyIdBeginDate", applyIdBeginDate);//身份证有效期开始
            parames.put("applyIdEndDate", applyIdEndDate);//身份证有效期结束
            parames.put("effect", "false");//身份证有效期是否长期有效
            parames.put("applyNativeProvince", applyNativeProvince);//贯籍省
            parames.put("applyNativeCity", applyNativeCity);//贯籍城市
            parames.put("applyHouseProvince", applyHouseProvince);//住宅省市
            parames.put("applyHouseCity", applyHouseCity);//住宅城市
            parames.put("applyAddress1", applyAddress1);//住宅区（如徐汇区）
            parames.put("applyAddress2", applyAddress2);//住宅详细地址
            parames.put("applyPost", applyPost);//邮政编码
            parames.put("applyHouseStatus", applyHouseStatus);//住宅状况 0自置无按揭  1自置有按揭 2商住两用 3租用 4与父母同住 5集体宿舍 6其他
            parames.put("applyHouseBeginDate", applyHouseBeginDate);//住宅入住年月
            parames.put("applyHouseAreaCode", applyHouseAreaCode);//住宅电话区号 (提示：如无住宅电话请填写直系亲属手机号码，区号不填写)
            parames.put("applyHousePhone", applyHousePhone);//住宅电话号码
            parames.put("applyCarNo", applyCarNo);//车牌号码(如无，可不填)
            parames.put("applyMarrige", applyMarrige);//婚姻状况(0 已婚 1未婚 2其他)
            parames.put("applyFendnum", applyFendnum);//供养人数(0-4 个,5代表5个以上)
            parames.put("applyEducation", applyEducation);//教育程度 (0研究生或以上,1大学（本科）,2大专,3高中,4中专,5初中及以下)
            parames.put("applyPriSchoolName", applyPriSchoolName);//毕业小学名称
            parames.put("linkManName", linkManName);//联系人姓名
            parames.put("linkManRelation", linkManRelation);//与您的关系(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
            parames.put("linkManCompany", linkManCompany);//联系人单位名称
            parames.put("linkManTel", linkManTel);//联系人移动电话
            parames.put("linkManHouseAreaCode", linkManHouseAreaCode);//联系人住宅电话区号(选填)
            parames.put("linkManHousePhone", linkManHousePhone);//联系人住宅电话区号(选填)
            parames.put("actPubId", actPubId);//办卡活动
            parames.put("presentId", presentId);

            urlparms= getUrlParams(parames);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/workInfo.html?"+urlparms;
            HtmlPage page2=webClient.getPage(url);
            content=page2.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            htmls.put("2-wanshangongzhigongsi", content);
//            System.out.println(content);
            if (!content.contains("preApproveId")) {
            	paramsLogs(md, parames," 完善供职公司资料 您填写的信息有误，请确认后重新填写");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 完善供职公司资料 content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 完善供职公司资料");
                return 0;
            }else {
            	paramsLogs(md, parames," 完善供职公司资料 success");
			}
            

            element = Jsoup.parse(content);
            preApproveId = element.getElementById("preApproveId").val();
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第三步  完善供职公司资料-----------------------");
            parames.clear();
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            parames.put("applyIncome", applyIncome);
            parames.put("applyOtherinCome", applyOtherinCome);
            parames.put("preApproveId", preApproveId);
            parames.put("applyHasJob", applyHasJob);//就业状态：0全职/有薪, 1兼职,2家庭主妇,3退休人士,4自雇人士,5临时合同工,6学生,7失业人士,8其他
            parames.put("applyCompanyPropery", applyCompanyPropery);//单位性质: 0 机关事业单位,1社会团体,2国有企业,3三资企业,4上市公司,5民营,6私营,7个体
            parames.put("applyIndustryType", applyIndustryType);//行业类别:00 农、林、牧、渔业,01采掘业,02制造业,03电力、煤气及水的生产和供应业,04建筑业,05交通运输、仓储及邮电通信业,06信息传输、计算机服务及软件业,07批发和零售业,08住宿和餐饮业,09金融业,10房地产业,11租赁及商务服务业,12科学研究、技术服务业和地质勘查业,13水利、环境和公共设施管理业,14居民服务和其他服务业,15教育,16卫生、社会保障和社会福利业,17文化、体育和娱乐业,18公共管理及社会组织,19国际组织,21广告业,22电讯业,23银行业,24保险业,25法律业,26军事业,27出版业,28旅游观光业,29酒店业,30国家机关、政党机关和社会团体
            parames.put("applyJobPost", applyJobPost);//职位ID
            parames.put("applyCompanyName", applyCompanyName);//所属公司名称(工作单位名称)
            parames.put("applyCompanyDept", applyCompanyDept);//所属公司部门(任职部门)
            parames.put("applyCompanyProvince", applyCompanyProvince);//公司所属省份(单位省)
            parames.put("applyCompanyCity", applyCompanyCity);//公司所属城市(单位市)
            parames.put("applyCompanyAddress1", applyCompanyAddress1);//公司所属城市区(单位地区或县)
            parames.put("applyCompanyAddress2", applyCompanyAddress2);//公司详细地址(单位详细地址)
            parames.put("applyCompanyPost", applyCompanyPost);//公司所属地邮编(邮政编码)
            parames.put("applyCompanyAreaCode", applyCompanyAreaCode);//公司电话区号
            parames.put("applyCompanyPhone", applyCompanyPhone);//公司电话号码
            parames.put("applyCompanySemicolon", applyCompanySemicolon);//公司电话分号(选填)
            parames.put("wageIncome", wageIncome);//税前年薪收入 （单位W）
            parames.put("otherIncome", otherIncome);//税前年其他收入（单位W）
            parames.put("applyPaymentMode", applyPaymentMode);//薪金支付方式 ：1 银行自动转帐支付,0不是银行自动转帐支付
            parames.put("applyJobStartTime", applyJobStartTime);//现公司入职时间(现单位工作起始年月)
            parames.put("applyFrontJobMonths", applyFrontJobMonths);//前一单位工龄(单位月)
            parames.put("applyIsBocomClient", applyIsBocomClient);//您是否为交通银行客户 1是 0否
            parames.put("applyOtherBankName", applyOtherBankName);//您已持有的贷记卡发卡银行名称
            parames.put("applyAccountAddress", applyAccountAddress);//将信用卡、信用卡有关函件寄往本人 0住宅地址 1单位地址
            parames.put("trackCode", trackCode);
            
//            urlparms= getUrlParams(parames);
//            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/optionalService.html?"+urlparms;
//            HtmlPage page3=webClient.getPage(url);
//            content=page3.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            setClientCookies(cookieStore, CM);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/optionalService.html";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            
            
            htmls.put("3-zixuanfuwu", content);
            if (!content.contains("自选服务项目")) {
            	paramsLogs(md, parames,"自选服务项目 -您填写的信息有误，请确认后重新填写");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 自选服务项目  content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 自选服务项目");
                return 0;
            }

            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第三步  第三步  完善资料确认-----------------------");
            String applyDealmode = "1";//交易密码选择: 0 仅使用签名确认交易,1使用密码确认交易 (信用卡刷卡消费默认使用签名确认交易，但您可选择开通POS消费验密功能，在中国大陆地区银联商户刷卡消费时使用密码确认交易，在除中国大陆地区以外的其他银联商户刷卡消费的，需按照商户收银员的指引选择使用签名或使用密码确认交易，上述两种交易确认方式均视为您本人对交易的有效授权。)
            String applyContractFlag = "0";//自动转账还款业务：0 不开通, 1开通
            String Automatic = "on";//您同意开通自动转账还款业务，且已阅读并同意遵守<a id="mrm_btnZdhk" class="blue dialogopen" dialogopenurl="/applynew/assets/pc/dialog/ZiDongHuanKuan.html" href="#nogo">《交通银行太平洋贷记卡自动转账还款业务条款》</a>,确认填写的资料完全属实。</span></label>
            String applyRepayBank = "BCOM";//开通了自动还款业务 这个选项有效 否则默认农业银行 无效 :ABCB农业银行,BCOM交通银行,BOCB中国银行,CCBB建设银行,CEBB光大银行,CMBB招商银行,PSBC邮政储蓄银行
            String applyRepayAccount = "";//自动还款借记卡号(开通了自动还款业务 这个选项有效)
            String applyRmbRepay = "0";//还款金额(0全部还款,1最低还款额)(开通了自动还款业务 这个选项有效)
            String applyRepayFrequency = "1";//扣款方式 (0到期还款日扣款,1两次扣款)(开通了自动还款业务 这个选项有效)
            String billType = "EMAIL";//账单方式 (EMAIL 您的账单将以电子邮件形式发送至您填写的电子邮箱) 您的信用卡对账单和相关函件将寄送至信用卡卡片寄送地址
            //String applyAddservice="";
            //&applyAddservice=4&applyAddservice=10

            //我同意开通“用卡无忧”服务，且已阅读并同意遵守 《用卡无忧服务条款及细则》 4
            //我同意开通“信用保障”服务，且已阅读并同意遵守 《信用保障服务条款及细则》。10

			parames.clear();
			parames.put("applyUuid", pccc_uuid);
			parames.put("uuid", pccc_uuid);
			parames.put("applyDealmode", applyDealmode);
			parames.put("applyContractFlag", applyContractFlag);
			parames.put("Automatic", Automatic);
			parames.put("applyRepayBank", applyRepayBank);//
			parames.put("applyRepayAccount", applyRepayAccount);//
			parames.put("applyRmbRepay", applyRmbRepay);//
			parames.put("applyRepayFrequency", applyRepayFrequency);//
			parames.put("billType", billType);//
            //--
            parames.put("trackCode", trackCode);
			
//			urlparms= getUrlParams(parames);
//			url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/view.html?"+ urlparms;
//			HtmlPage page4 = webClient.getPage(url);
//			content = page4.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			
			setClientCookies(cookieStore, CM);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/view.html";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
			System.out.println(content);
			htmls.put("4-zhuchikerenjibenziliao", content);
			if (!content.contains("newSubmit")) {
				logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] 主持卡人基本资料  content["+content+"]");
				paramsLogs(md, parames,"主持卡人基本资料 -您填写的信息有误，请确认后重新填写");
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
            	bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 主持卡人基本资料");
				return 0;
			}
			logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] --------------------第三步  完善资料确认提交-----------------------");
			parames.clear();
			parames.put("applyUuid", pccc_uuid);
			parames.put("preApproveId", preApproveId);
            //--
            parames.put("trackCode", trackCode);
			
			urlparms= getUrlParams(parames);
			url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/newSubmit.html?"+ urlparms;
			HtmlPage page5 = webClient.getPage(url);
			content = page5.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			waitRun(3);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "]2 JiaoTongHelper.curl==========="+ JiaoTongHelper.curl);

			JiaoTongHelper.curl="";
			htmls.put("5-wanshanziliaoqueren", content);
			if (!content.contains("成功递交")) {
				String msg="您填写的信息有误，请确认后重新填写";
				if (content.contains("tipText")) {
					try {
						org.jsoup.nodes.Document doc= Jsoup.parse(content);
						msg=doc.getElementsByClass("tipText").get(0).text();
						if (content.contains("您提交失败，请返回重新申请")) {
							msg="您提交失败，请返回重新申请!如果重复出现此提示，请核实您填写的资料是否正确，然后等待交通银行办卡人员联系，如未联系您则代表您没有通过交通银行预审。";
						}
					} catch (Exception e) {
						msg="您填写的信息有误，请确认后重新填写";
					}
				}
				saveHtml(md, htmls);
				paramsLogs(md, parames,"完善资料确认提交 -您填写的信息有误，请确认后重新填写");
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc(msg);
            	bean.setBusiJSON("fail");
            	
				logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] 完善资料确认提交  content["+content+"]");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 完善资料确认提交");
				return 0;
			}
//			cookies_ret = webClient.getCookieManager().getCookies();
//			it_ret = cookies_ret.iterator();
//			while (it_ret.hasNext()) {
//				com.gargoylesoftware.htmlunit.util.Cookie c = it_ret.next();
//				String name = c.getName();
//				String value = c.getValue();
//            	System.out.println("------name="+name+"  value="+value);
//			}
			paramsLogs(md, parames,"完善资料确认提交 -成功提交");
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("您的申请资料已成功递交，我们将尽快审核并可能通过02195559与您核实信息；为加快您的审核，在非工作时间我们也可能与您联系，还请注意接听。");
            bean.setBusiJSON("success");
            BankApplyListener.sendSucess(BankEnum.jiaotong, BankApplyStepEnum.submit_apply);
            return 1;

        } catch (Exception e) {
        	paramsLogs(md, parames,"申请失败 -您填写的信息有误，请确认后重新填写");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申请失败，请稍后再试");
            bean.setBusiJSON("fail");
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyForTheJiaoTongBank 异常 content[" + content + "]", e);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 applyForTheJiaoTongBank 异常");
			saveHtml(md, htmls);
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
	
	
	
	
	
	
	
    
	public static int getBankMessage_JiaoTong(MaterialBean bean,MemCachedClient client) {
        CloseableHttpClient httpClient = null;
        String url = "";
        String content = "";
        MaterialModel md = bean.getModel();
        if (bean.getBusiErrCode() == 0) {
            return 0;
        }
        WebClient webClient =null;
        try {
			webClient = new WebClient();
            Map<String, String> parames = new HashMap<String, String>();
            Map<String, String> transferParameter = new HashMap<String, String>();
			CookieStore cookieStore = null;
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.setJavaScriptTimeout(50000);
			webClient.getOptions().setCssEnabled(false);
			webClient.getCookieManager().setCookiesEnabled(true);// 开启cookie管理
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setTimeout(30000);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			httpClient = HttpClients.createDefault();
			cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute("http.cookie-store", cookieStore);
			webClient.addRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			webClient.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
			webClient.addRequestHeader("Connection", "Keep-Alive");
			webClient.addRequestHeader("Host", "creditcardapp.bankcomm.com");
			webClient.addRequestHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			Map<String, String> requestHeaderMap = new HashMap<String, String>();
			requestHeaderMap.put("Accept-Language",	"zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			requestHeaderMap.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
			requestHeaderMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			requestHeaderMap.put("Connection", "Keep-Alive");
			requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");

			RequestConfig.custom().setConnectTimeout(30000);
			RequestConfig.custom().setSocketTimeout(30000);
			RequestConfig.custom().setConnectionRequestTimeout(30000);
			RequestConfig requestConfig = RequestConfig.custom().build();
			HtmlPage page = webClient.getPage("https://creditcardapp.bankcomm.com/applynew/front/apply/track/record.html?trackCode=A022216511939");
			content=page.asXml();
			try {
				String purl="https://creditcardapp.bankcomm.com"+page.getElementById("pccc_select_img").getAttribute("src");
				webClient.getPage(purl);
				logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] purl=" + purl);
			} catch (Exception e) {
				logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] load pic error",e);
			}
			CookieManager CM = webClient.getCookieManager();
			Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies_ret = CM.getCookies();
			Iterator<com.gargoylesoftware.htmlunit.util.Cookie> it_ret = cookies_ret.iterator();
			while (it_ret.hasNext()) {
				com.gargoylesoftware.htmlunit.util.Cookie c = it_ret.next();
				String name = c.getName();
				String value = c.getValue();
				logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] cookieName=" + name + "  value=" + value);
				BasicClientCookie bcookie = new BasicClientCookie(name, value);
				bcookie.setDomain(c.getDomain());
				bcookie.setExpiryDate(c.getExpires());
				bcookie.setPath(c.getPath());
				cookieStore.addCookie(bcookie);
			}
        	
			url="https://creditcardapp.bankcomm.com/applynew/front/apply/identityCount/record.json?trackCode=A022216511939&length=1";
	        String content1 = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
	        logger.info("trackCode=A022216511939   content="+content1);
			
			String read = "";
			String trackCode = "A022216511939";
			String cimCode = "";
			String userparam1 = "";
			String userparam2 = "";
			String userparam3 = "";
			String mobileNo = "";
			String recomChannelType = "";
			try {
				read = page.getElementById("mrm_cbRead").getAttribute("value");
	            cimCode = page.getElementById("pccc_cimCode").getAttribute("value");
				recomChannelType =page.getElementById("pccc_recomChannelType_put").getAttribute("value");
				userparam1 =page.getElementsByName("userparam1").get(0).getAttribute("value");
				userparam2 =page.getElementsByName("userparam2").get(0).getAttribute("value");
				userparam3 =page.getElementsByName("userparam3").get(0).getAttribute("value");
				mobileNo =page.getElementsByName("mobileNo").get(0).getAttribute("value");
				
				logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] recomChannelType=" +
				recomChannelType+" read="+read+" cimCode="+cimCode+" userparam1="+userparam1+" userparam2="+userparam2+" userparam3="+userparam3);

			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	
//            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
//            CookieStore cookieStore = new BasicCookieStore();
//            HttpContext localContext = new BasicHttpContext();
//            // 设置请求和传输超时时间
//            RequestConfig.custom().setConnectTimeout(30000);
//            RequestConfig.custom().setSocketTimeout(30000);
//            RequestConfig.custom().setConnectionRequestTimeout(30000);
//            RequestConfig requestConfig = RequestConfig.custom().build();
//            localContext.setAttribute("http.cookie-store", cookieStore);
//            Map<String, String> requestHeaderMap = new HashMap<String, String>();
//            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
//            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            requestHeaderMap.put("Connection", "Keep-Alive");
//            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");
//            
//            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/track/record.html?trackCode=A022216511939";
//            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "utf-8", false, requestConfig);
//    		
//            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/index.html?trackCode=A022216511939&commercial_id=null";
//            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "utf-8", false, requestConfig);
//
//    		url="https://creditcardapp.bankcomm.com/applynew/front/apply/new/identity.html?cityId=&cardId=&provId=&applyName=&certType=&certNo=&trackCode=A022216511939&applyTel=&cimCode=&cardCode=&userparam1=&userparam2=&userparam3=&qualification=1";
//    		content=HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "utf-8",false, requestConfig);
//
//            org.jsoup.nodes.Element indexEle = Jsoup.parse(content);
//            String read = "";
//            String trackCode = "A022216511939";
//            String cimCode = "";
//            String userparam1 = "";
//            String userparam2 = "";
//            String userparam3 = "";
//            String mobileNo = "";
//            String recomChannelType = "";
//            try {
//                read = indexEle.getElementById("mrm_cbRead").val();
//                cimCode = indexEle.getElementById("pccc_cimCode").val();
//                recomChannelType = indexEle.getElementById("pccc_recomChannelType_put").val();
//                userparam1 = indexEle.getElementsByAttributeValue("name", "userparam1").first().val();
//                userparam2 = indexEle.getElementsByAttributeValue("name", "userparam2").first().val();
//                userparam3 = indexEle.getElementsByAttributeValue("name", "userparam3").first().val();
//                mobileNo = indexEle.getElementsByAttributeValue("name", "mobileNo").first().val();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            
            String certNo = md.getCidcard();//证件号码
            String cretName = md.getCname();//用户真实姓名
            String cardId = bean.getApplyBankCardId();//申请卡类型ID
            String org = "C";//银行卡办理默认C
            String cityId = bean.getApplyCityId();//申请人所在城市ID
            String provinceId = bean.getApplyProvinceId();//申请人所在省份ID
            String certType = "0";//申请人 证件类型 0 身份证
            String phone = md.getCphone();//申请人手机号码
            if (StringUtils.isEmpty(cardId)||StringUtils.isEmpty(cityId)) {
            	 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("卡片或者城市未匹配成功。");
                 bean.setBusiJSON("fail");
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 卡片或者城市未匹配成功。");
                 return 0;
			}

            //{certNo:身份证}  {cardId：卡片id}  {cityId:城市id}
            //https://creditcardapp.bankcomm.com/applynew/front/apply/common/productVal.json   第一步接口 参数 （certNo=431022199008254217&cardId=17&org=C&cimCode=&cityId=34&customer=1）
            parames.put("certNo", certNo);
            parames.put("cardId", cardId);
            parames.put("org", org);
            parames.put("cityId", cityId);
            parames.put("customer", "1");
            parames.put("cimCode", cimCode);
            //--
            parames.put("trackCode", trackCode);

            
            //certNo="+certNo+"&cardId="+cardId+"&org="+org+"&cimCode=&cityId="+cityId+"&customer=1
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/common/productVal.json";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            if (StringUtils.isEmpty(content)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您的身份证信息未通过验证，请核对。");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 验证办卡地不成功 content=" + content + " certNo=" + certNo + " cardId=" + cardId + " cityId=" + cityId);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 验证办卡地不成功");
                return 0;
            }
            JSONObject idcardResult = new JSONObject(content);
            String irflag = String.valueOf(idcardResult.get("flag"));
            if (!irflag.equals("true")) {
                String  busiErrDesc= String.valueOf(idcardResult.get("errorType"));
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(busiErrDesc);
                if ("01".equals(busiErrDesc)) {
                    bean.setBusiErrDesc("您不能申请青年卡，请重新选择！");
				}else if ("02".equals(busiErrDesc)) {
					bean.setBusiErrDesc("您选择的城市不开放该卡产品，请重新选择！");
				}else if ("03".equals(busiErrDesc)) {
					bean.setBusiErrDesc("您选择的卡产品不能参加该活动，请重新选择！");
				}
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 验证办卡地不成功 content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 busiErrDesc="+busiErrDesc);
                return 0;
            }
            

            parames.clear();
            parames.put("trackCode", trackCode);
            parames.put("cimCode", cimCode);
            parames.put("recomType", "");
            parames.put("recomNumber", "");
            parames.put("recomChannelType", recomChannelType);
            parames.put("userparam1", userparam1);
            parames.put("userparam2", userparam2);
            parames.put("mobileNo", mobileNo);
            parames.put("applyName", cretName);
            parames.put("provinceId", provinceId);
            parames.put("cityId", cityId);
            parames.put("certType", certType);
            parames.put("certNo", certNo);
            parames.put("Read", read);
            parames.put("cardId", cardId);
            parames.put("org", org);
            parames.put("applyMemberNo", "");
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/identityVerify.html";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            
            List<Cookie> cookies=cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
            	Cookie cookie=cookies.get(i);
            	System.out.println("name="+cookie.getName()+"  value="+cookie.getValue());
			}
            
            
            if (StringUtils.isEmpty(content)) {
                paramsLogs(md, parames,"短信验证码获取失败");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码获取失败，请检查你填写的参数，或稍后再试");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 短信验证码获取失败");
                return 0;
			}
            
            
            Pattern p = Pattern.compile("\\s*|t|r|n");
            Matcher m = p.matcher(content);
            String dest = m.replaceAll("");
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] dest=" + dest);
            if ("0".equals(dest)) {
                //您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。温馨提示：如您已销户，可通过我行营业网点申请办理，或在销户满6个月后通过网上申请办理。
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您已经是交通银行信用卡持卡人客户");
                return 0;
            } else if ("2".equals(dest)) {
            	paramsLogs(md, parames,"您填写的信息有误，请确认后重新填写-验证身份证");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您填写的信息有误，请确认后重新填写-验证身份证");
                return 0;
            } else if ("3".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("目前在线申请信用卡业务暂不开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务。");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 目前在线申请信用卡业务暂不开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务。");
				return 0;
			} else if ("5".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("目前在线申请信用卡业务暂只面向持有二代身份证的客户开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 目前在线申请信用卡业务暂只面向持有二代身份证的客户开放。您可至交通银行分行网点或交通银行直销办事处办理申请业务");
				return 0;
			} else if ("6".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("您已经是交通银行信用卡持卡人客户，抱歉无法参加此项只针对新客户申请的办卡活动，可进入现有客户快速申请通道办理业务。");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您已经是交通银行信用卡持卡人客户");
				return 0;
			} else if ("7".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("目前在线申请信用卡业务暂只面向18周岁（含）－65周岁（含）且持二代身份证的客户开放。");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 目前在线申请信用卡业务暂只面向18周岁（含）－65周岁（含）且持二代身份证的客户开放。");
				return 0;
			}else if ("NEW_013".equals(dest)) {
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("您访问的页面不存在 ");
				bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您访问的页面不存在");
				return 0;
			}
            
            
            
            if (content.contains("samecredit")) {
				String locationHref=content.substring(content.indexOf("location.href"), content.indexOf("</script>"));
            	locationHref=locationHref.substring(locationHref.indexOf("\"")+1, locationHref.lastIndexOf("\""));
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] locationHref=" + locationHref);
                content = HpClientUtil.httpGet("https://creditcardapp.bankcomm.com"+locationHref, requestHeaderMap, httpClient, localContext, "utf-8", false, requestConfig);
                
                String msg="";
                if (content.contains("blueTip")) {
                	org.jsoup.nodes.Element element = Jsoup.parse(content);
                	msg=element.select(".blueTip").first().html();
	                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] msg=" + msg);
				}else {
					msg="申请不成功，如果您已提交过申请请耐心等待或登录官方网站查询申请失败原因";
	                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] content=" + content);
				}
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(msg);
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  msg=" + msg);
                return 0;
            }
            
            org.jsoup.nodes.Element element = Jsoup.parse(content);
            String pccc_uuid = element.getElementById("pccc_uuid").val();
            //是否进行过预批核
            String preCheckStatus = element.getElementById("pccc_preCheckStatus").val();
            String isNeedPreCheck = element.getElementById("pccc_isNeedPreCheck").val();
            String cardType = element.getElementById("mrm_hdCardType").val();

            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第二步 获取电话验证码-----------------------pccc_uuid=" + pccc_uuid);
            parames.clear();
            parames.put("dyName", cretName);
            parames.put("dyTel", phone);
            parames.put("dyIdType", certType);
            parames.put("dyIdNo", certNo);
            parames.put("dyFun", "1");
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            //--
            parames.put("trackCode", trackCode);

            //dyName 真实姓名 ,dyTel 电话号码,dyIdType 证件ID，dyIdNo 身份证ID，pccc_uuid 验证uid
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/dynamicCode/sendDynamicCode.json";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            JSONObject resultObj = new JSONObject(content);
            String flag = String.valueOf(resultObj.get("flag"));
            String serial = String.valueOf(resultObj.get("serial"));
            String message = String.valueOf(resultObj.get("message"));
            if (flag.equals("true")) {
                transferParameter.put("preCheckStatus", preCheckStatus);
                transferParameter.put("isNeedPreCheck", isNeedPreCheck);
                transferParameter.put("cardType", cardType);
                transferParameter.put("pccc_uuid", pccc_uuid);
                transferParameter.put("trackCode", trackCode);
                transferParameter.put("cimCode", cimCode);
                transferParameter.put("userparam1", userparam1);
                transferParameter.put("userparam2", userparam2);
                transferParameter.put("userparam3", userparam3);
                transferParameter.put("mobileNo", mobileNo);
                transferParameter.put("read", read);

                client.set(md.getCidcard() + md.getCphone() + "_jiaotongMaterialBean", bean);
                client.set(md.getCidcard() + md.getCphone() + "_jiaotongCookies", cookieStore);
                client.set(md.getCidcard() + md.getCphone() + "_jiaotongParameter", transferParameter);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 短信发送成功,短信编号：" + serial);
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("短信发送成功,短信编号：" + serial);
                bean.setBusiJSON("success");
                return 1;
            } else {
            	paramsLogs(md, parames,"获取短信验证码失败");
            	String newIsQuit = String.valueOf(resultObj.get("newIsQuit"));
            	if ("true".equals(newIsQuit)||StringUtils.isEmpty(message)) {
					message="该电话号码已于6个月内有申请记录，无法重复申请。";
				}
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 获取短信验证码失败 message=" + message+" content="+content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  获取短信验证码失败message=" + message);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(message);
                bean.setBusiJSON("fail");
            }
        } catch (Exception e) {
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  getBankMessage_JiaoTong 异常");
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] getBankMessage_JiaoTong 异常 content=" + content, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信验证码获取失败，请稍后再试");
            bean.setBusiJSON("fail");
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
	
	public static int applyForTheJiaoTongBank(MaterialBean bean,MemCachedClient client) {
        CloseableHttpClient httpClient = null;
        String url = "";
        String content = "";
        MaterialModel md = bean.getModel();
        Map<String, String> parames = new HashMap<String, String>();
        Map<String, String> htmls=new HashMap<String, String>();
        try {
            Object cookieObj = client.get(md.getCidcard() + md.getCphone() + "_jiaotongCookies");
            Object parameterObj = client.get(md.getCidcard() + md.getCphone() + "_jiaotongParameter");
            Object materiallObj = client.get(md.getCidcard() + md.getCphone() + "_jiaotongMaterialBean");
            if (cookieObj == null || parameterObj == null || materiallObj == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码已失效");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  短信验证码已失效");
                return 0;
            }
            if (StringUtils.isEmpty(bean.getPhoneauthcode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  短信验证码为空");
                return 0;
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
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Host", "creditcardapp.bankcomm.com");
            String dyCode = bean.getPhoneauthcode();
            String preCheckStatus = parameterMap.get("preCheckStatus");
            String isNeedPreCheck = parameterMap.get("isNeedPreCheck");
            String cardType = parameterMap.get("cardType");
            String pccc_uuid = parameterMap.get("pccc_uuid");
            String trackCode = "A022216511939";
            String cimCode = parameterMap.get("cimCode");
            String userparam1 = parameterMap.get("userparam1");
            String userparam2 = parameterMap.get("userparam2");
            String userparam3 = parameterMap.get("userparam3");
            String mobileNo = parameterMap.get("mobileNo");
            String read = parameterMap.get("read");


            String certNo = md.getCidcard();//证件号码
            String cretName = md.getCname();//用户真实姓名
            String phone = md.getCphone();//申请人手机号码
            String certType = "0";//申请人 证件类型 0 身份证
            String org = "C";//银行卡办理默认C


            String email = md.getCemail();//申请人邮箱
            String recomType = "";//推荐类型 空代表没有推荐  01 电话 02其他
            String recomNumber = ""; //推荐值

            if (StringUtils.isEmpty(md.getCidexpirationtime())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份证有效期不能为空！");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16  身份证有效期不能为空");
                return 0;
            }
            String iddates[] = md.getCidexpirationtime().split(",");
            if (iddates.length != 2) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份证有效期不支持长期有效，请修改后申请");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 身份证有效期不支持长期有效，请修改后申请");
                return 0;
            }

            String applyIdBeginDate = iddates[0];//身份证有效期 开始 格式（yyyyMMdd）
            String applyIdEndDate = iddates[1];//身份证有效期 结束

            String cityId = pbean.getApplyCityId();//申请人所在城市ID
            String cardId = pbean.getApplyBankCardId();//申请卡类型ID
            
            if (StringUtils.isEmpty(cityId)||StringUtils.isEmpty(cardId)) {
            	 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("卡片或者城市未匹配成功。");
                 bean.setBusiJSON("fail");
                 logger.info("卡片或者城市未匹配成功 idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] cityId="+cityId+" cardId="+cardId);
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 卡片或者城市未匹配成功");
                 return 0;
			}

            String applyNativeProvince = pbean.getApplyNativeProvince();//籍贯 省份
            String applyNativeCity = pbean.getApplyNativeCity();//籍贯 城市

            String applyHouseProvince = pbean.getApplyProvinceId();//住宅省市
            String applyHouseCity = pbean.getApplyCityId();//住宅城市
            String applyAddress1 = pbean.getApplyAddress();//住宅区（如徐汇区）

            String applyAddress2 = md.getChome_detailaddress();//住宅详细地址
            String applyPost = md.getChome_postcode();//邮政编码
            //（1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它）


            String applyHouseStatus = jiaoTong_House.get(md.getResidencestatus());//住宅状况 0自置无按揭  1自置有按揭 2商住两用 3租用 4与父母同住 5集体宿舍 6其他
            if (StringUtils.isEmpty(applyHouseStatus)) {
                bean.setBusiErrCode(0);
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
                bean.setBusiErrCode(0);
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

            String applyHouseAreaCode = "";//住宅电话区号 (提示：如无住宅电话请填写直系亲属手机号码，区号不填写)
            String applyHousePhone = "";//住宅电话号码

            if (StringUtils.isEmpty(md.getChome_telnum())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("住宅电话不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 住宅电话为空");
                return 0;
            }

            if (!md.getChome_telnum().contains("-")) {
                applyHousePhone = md.getChome_telnum();
            } else {
                String[] telnum = md.getChome_telnum().split("-");
                applyHouseAreaCode = telnum[0];
                applyHousePhone = telnum[1];
            }

            String applyCarNo = "";//车牌号码(如无，可不填)

            String applyMarrige = jiaoTong_Marrige.get(md.getMaritalstatus());//婚姻状况(0 已婚 1未婚 2其他)
            if (StringUtils.isEmpty(applyMarrige)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("婚姻状况不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 婚姻状况为空");
                return 0;
            }

            String applyFendnum = "0";//供养人数(0-4 个,5代表5个以上)
            //1、硕士 2、博士及以上 3、本科 4、大专 5、高中、中专一下
            String applyEducation = jiaoTong_Education.get(md.getIdegree());//教育程度 (0研究生或以上,1大学（本科）,2大专,3高中,4中专,5初中及以下)
            if (StringUtils.isEmpty(applyEducation)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("学历不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 学历为空");
                return 0;
            }
            String applyPriSchoolName = md.getCprimaryschoolname();//毕业小学名称
            if (StringUtils.isEmpty(applyPriSchoolName)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("毕业小学名称不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 毕业小学名称为空");
                return 0;
            }
            
            if (applyPriSchoolName.length()>13) {
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("毕业小学名称不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 毕业小学名称不能大于13个字符  applyPriSchoolName=" + applyPriSchoolName);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 毕业小学名称不能大于13个字符 applyPriSchoolName=" + applyPriSchoolName);
                return 0;
			}
            
            //紧急联系人关系1.配偶 2.父母 3.子女 4.兄弟姐妹 5朋友
            //(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
            String linkManName = md.getFamilyname();//联系人姓名
            String linkManRelation = jiaoTong_Relation.get(md.getIfamilyties());//与您的关系(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)

            String linkManCompany = "未知";//联系人单位名称
            String linkManTel = md.getCfamilyphonenum();//联系人移动电话
            String linkManHouseAreaCode = "";//联系人住宅电话区号(选填)
            String linkManHousePhone = "";//联系人住宅电话号码(选填)

            if (StringUtils.isEmpty(linkManName) || StringUtils.isEmpty(linkManRelation)
                    || StringUtils.isEmpty(linkManTel) || StringUtils.isEmpty(linkManCompany)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("紧急联系人信息有误！");
                bean.setBusiJSON("fail");
                logger.info("紧急联系人信息有误  idcard[" + md.getCidcard() + "] "
                		+ "mobile[" + md.getCphone() + "] linkManName="
                		+linkManName+" linkManRelation="+linkManRelation+" linkManTel="+linkManTel+" linkManCompany="+linkManCompany);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 紧急联系人信息有误");
                return 0;
            }
            String actPubId = "41";//成功办卡，即享１００元刷卡金活动
            String presentId = "399";//１００元刷卡金活动

            String wageIncome = md.getIannualsalary();//税前年薪收入 （单位W）
            if (StringUtils.isEmpty(wageIncome)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪不能为空！");
                bean.setBusiJSON("fail");
                logger.info("年薪不能为空  idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] wageIncome="+wageIncome);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪为空");
                return 0;
            }
            if (!isNumeric(wageIncome)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪必须为数字！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪必须为数字");
                return 0;
            }
            
            DecimalFormat df = new DecimalFormat("#0");
            String otherIncome = "0";//税前年其他收入（单位W）
            double incomed=Double.parseDouble(wageIncome) * 10000;
            String applyIncome = df.format(incomed);//税前年薪收入（单位元）
            String applyOtherinCome = "0";//税前年其他收入（单位元）
            String preApproveId = "0";

            if (incomed>9999999) {
           	    bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪最多为9999999元，请核实您填写的年薪是否正确");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪最多为9999999元，请核实您填写的年薪是否正确");
                return 0;
			}
            if (incomed<10000) {
           	    bean.setBusiErrCode(0);
                bean.setBusiErrDesc("年薪最少为10000元");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 年薪最少为10000元");
                return 0;
			}
            

            String applyHasJob = "0";//就业状态：0全职/有薪, 1兼职,2家庭主妇,3退休人士,4自雇人士,5临时合同工,6学生,7失业人士,8其他

            String applyCompanyPropery = pbean.getApplyCompanyPropery();//单位性质: 0 机关事业单位,1社会团体,2国有企业,3三资企业,4上市公司,5民营,6私营,7个体
            String applyIndustryType = pbean.getApplyIndustryType();//行业类别:00 农、林、牧、渔业,01采掘业,02制造业,03电力、煤气及水的生产和供应业,04建筑业,05交通运输、仓储及邮电通信业,06信息传输、计算机服务及软件业,07批发和零售业,08住宿和餐饮业,09金融业,10房地产业,11租赁及商务服务业,12科学研究、技术服务业和地质勘查业,13水利、环境和公共设施管理业,14居民服务和其他服务业,15教育,16卫生、社会保障和社会福利业,17文化、体育和娱乐业,18公共管理及社会组织,19国际组织,21广告业,22电讯业,23银行业,24保险业,25法律业,26军事业,27出版业,28旅游观光业,29酒店业,30国家机关、政党机关和社会团体
            String applyJobPost = pbean.getApplyJobPost();//职位ID


            String applyCompanyName = md.getCcompanyname();//所属公司名称(工作单位名称)
            String applyCompanyDept = md.getCdepartmentname();//所属公司部门(任职部门)
            String applyCompanyProvince = pbean.getApplyCompanyProvince();//公司所属省份(单位省)
            String applyCompanyCity = pbean.getApplyCompanyCity();//公司所属城市(单位市)
            String applyCompanyAddress1 = pbean.getApplyCompanyAddress();//公司所属城市区(单位地区或县)
            String applyCompanyAddress2 = md.getCcompany_detailaddress();//公司详细地址(单位详细地址)
            String applyCompanyPost = md.getCcompany_postcode();//公司所属地邮编(邮政编码)
            
            if (applyCompanyName.length()>13) {
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("公司名称不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 公司名称大于13个字符 applyCompanyName=" + applyCompanyName);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司名称大于13个字符 applyCompanyName=" + applyCompanyName);
                return 0;
			}
            if (applyCompanyDept.length()>13) {
            	bean.setBusiErrCode(0);
                bean.setBusiErrDesc("任职部门不能大于13个字符");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 任职部门不能大于13个字符 applyCompanyDept=" + applyCompanyDept);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 任职部门不能大于13个字符 applyCompanyName=" + applyCompanyDept);
                return 0;
			}
            
            if (!applyHouseCity.equals(cityId)&&!applyCompanyCity.equals(cityId)) {
            	 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("单位或住宅地址的城市必须有一个与您选择的所在城市相同");
                 bean.setBusiJSON("fail");
                 logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 单位或住宅地址的城市必须有一个与您选择的所在城市相同 applyHouseCity="+applyHouseCity+" applyCompanyCity="+applyCompanyCity+" cityId="+cityId);
                 return 0;
			}

            if (StringUtils.isEmpty(md.getCcompany_telnum())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("公司电话不能为空！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司电话为空");
                return 0;
            }
            String[] telnum = md.getCcompany_telnum().split("-");

            if (telnum.length != 2) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("公司电话格式不正确！");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 公司电话格式不正确");
                return 0;
            }
    		if (applyHousePhone.equals(linkManTel)) {
    			bean.setBusiErrCode(0);
                bean.setBusiErrDesc("直系亲属电话不能与联系人电话一致");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 直系亲属电话不能与联系人电话一致 applyHousePhone=" + applyHousePhone +" linkManTel="+linkManTel);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 直系亲属电话不能与联系人电话一致");
                return 0;
    		}
            String applyCompanyAreaCode = telnum[0];//公司电话区号
            String applyCompanyPhone = telnum[1];//公司电话号码
            String applyCompanySemicolon = "";//公司电话分号(选填)
            String applyPaymentMode = "1";//薪金支付方式 ：1 银行自动转帐支付,0不是银行自动转帐支付
            String applyFrontJobMonths = "0";//前一单位工龄(单位月)
            String applyIsBocomClient = "0";//您是否为交通银行客户 1是 0否
            String applyOtherBankName = "";//您已持有的贷记卡发卡银行名称

            String applyAccountAddress = "1";//将信用卡、信用卡有关函件寄往本人 0住宅地址 1单位地址
            if ("2".equals(md.getIpostaddress())) {
                applyAccountAddress = "0";
            }


            parames.put("dyName", cretName);
            parames.put("dyTel", phone);
            parames.put("dyIdType", certType);
            parames.put("dyIdNo", certNo);
            parames.put("dyCode", dyCode);
            parames.put("dyFun", "1");
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            //--
            parames.put("trackCode", trackCode);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/dynamicCode/verifyDynamicCode.json";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            JSONObject resultObj = new JSONObject(content);
            String flag = String.valueOf(resultObj.get("flag"));
            int errorCount = resultObj.getInt("errorCount");
            String message = String.valueOf(resultObj.get("message"));

            if (flag.equals("true")) {
                preCheckStatus = "1";
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(message);
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 获取短信验证码失败 message=" + message);
                if (errorCount >= 5) {
                    bean.setBusiErrDesc("您的移动电话验证码已错误输入5次，请重新获取验证码");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的移动电话验证码已错误输入5次，请重新获取验证码");
                } else {
                    bean.setBusiErrDesc("您的移动电话验证码已错误输入" + errorCount + "次，请重新输入");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 您的移动电话验证码已错误输入" + errorCount + "次，请重新输入");
                }
                return 0;
            }

            if ("1".equals(isNeedPreCheck)) {
                url = "https://creditcardapp.bankcomm.com/applynew/front/apply/preCheck.json";
                parames.clear();
                parames.put("applyUuid", pccc_uuid);
                content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] preCheck="+content);
                try {
                	
                	for (int i = 0; i < 5; i++) {
                		url="https://creditcardapp.bankcomm.com/applynew/front/apply/getPreCheckResult.json";
                        parames.clear();
                        parames.put("applyUuid", pccc_uuid);
                        content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
                        JSONObject preCheckResult = new JSONObject(content);
                    	String pr = String.valueOf(preCheckResult.get("result"));
                    	if ("fail".equals(pr)) {
                    		bean.setBusiErrCode(3);
                    		bean.setBusiErrDesc("非常抱歉，您未通过交通银行的预审。");
                    		bean.setCstatus("2");
                            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 非常抱歉，您未通过交通银行的预审。");
                            bean.setBusiJSON("success");
                            return 1;
    					}else if ("success".equals(pr)) {
							break;
						}else {
							Thread.sleep(2000);
						}
                	}
                	
				} catch (Exception e) {
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 getPreCheckResult");
	                logger.info(" getPreCheckResult idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] content="+content);
				}
            }else {
                logger.info(" isNeedPreCheck idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] isNeedPreCheck="+isNeedPreCheck);
            }

            //进入完善资料步骤
            parames.clear();
            parames.put("trackCode", trackCode);
            parames.put("cimCode", cimCode);
            parames.put("userparam1", userparam1);
            parames.put("userparam2", userparam2);
            parames.put("userparam3", userparam3);
            parames.put("mobileNo", mobileNo);
            parames.put("applyTel", phone);
            parames.put("dyCode", dyCode);
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            parames.put("applyName", cretName);
            parames.put("certType", certType);
            parames.put("certNo", certNo);
            parames.put("cityId", cityId);
            parames.put("isNeedPreCheck", isNeedPreCheck);
            parames.put("preCheckStatus", preCheckStatus);
            parames.put("applyEmail", email);
            parames.put("recomType", recomType);
            parames.put("recomNumber", recomNumber);
            parames.put("CardType", cardType);
            parames.put("Read", read);
            parames.put("cardId", cardId);
            parames.put("org", org);
            parames.put("applyMemberNo", "");
            
            
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/personalInfo.html";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第二步 end 进入资料完善页面-----------------------");
            htmls.put("1-ziliaowanshan", content);
//            htmls.put("资料完善提交", content);
            if (!content.contains("mrm_txtBirthDate")) {
            	paramsLogs(md, parames," 进入资料完善页面 您填写的信息有误，请确认后重新填写");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 资料完善提交 content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 资料完善提交");
                return 0;
            }

            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第三步 begin 资料完善提交-----------------------");
            org.jsoup.nodes.Element element = Jsoup.parse(content);
            String cardCode = "";
            String applyChName = "";
            String applyBirdate = element.getElementById("mrm_txtBirthDate").val();//出生日
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyBirdate=" + applyBirdate);

            if (content.contains("actId")) {
                Elements radios = element.getElementsByAttributeValue("name", "actId");
                for (int i = 0; i < radios.size(); i++) {
                    org.jsoup.nodes.Element tempRadio = radios.get(i);
                    String checked = tempRadio.attr("checked");
                    logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] radio id=" + tempRadio.attr("id")+" value="+tempRadio.attr("value"));
                    if ("checked".equals(checked) || "true".equals(checked)) {
                    	actPubId=tempRadio.val();
                    }
                }
			}
            if (content.contains("presentId")) {
            	presentId = element.getElementsByAttributeValue("name", "presentId").first().val();
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] presentId=" +presentId);
			}else {
				presentId="";
			}
            applyChName = element.getElementsByAttributeValue("name", "applyChName").first().val();
            cimCode = element.getElementsByAttributeValue("name", "cimCode").first().val();
            cardCode = element.getElementsByAttributeValue("name", "cardCode").first().val();
            //trackCode = element.getElementsByAttributeValue("name", "trackCode").first().val();
            //userparam2=element.getElementsByAttributeValue("name", "userparam2").first().val();
            //userparam3=element.getElementsByAttributeValue("name", "userparam3").first().val();
            //mobileNo=element.getElementsByAttributeValue("name", "mobileNo").first().val();

            

            
            parames.clear();
            parames.put("applyUuid", pccc_uuid);
            parames.put("cimCode", cimCode);
            parames.put("cardCode", cardCode);
            parames.put("trackCode", trackCode);
            parames.put("cityId", cityId);//申请人所在城市ID
            parames.put("applyName", cretName);//申请人正式姓名
            parames.put("applyChName", applyChName);
            parames.put("certType", certType);//申请证件类型
            parames.put("certNo", certNo);//申请证件类型
            parames.put("applyIdBeginDate", applyIdBeginDate);//身份证有效期开始
            parames.put("applyIdEndDate", applyIdEndDate);//身份证有效期结束
            parames.put("effect", "false");//身份证有效期是否长期有效
            parames.put("applyNativeProvince", applyNativeProvince);//贯籍省
            parames.put("applyNativeCity", applyNativeCity);//贯籍城市
            parames.put("applyHouseProvince", applyHouseProvince);//住宅省市
            parames.put("applyHouseCity", applyHouseCity);//住宅城市
            parames.put("applyAddress1", applyAddress1);//住宅区（如徐汇区）
            parames.put("applyAddress2", applyAddress2);//住宅详细地址
            parames.put("applyPost", applyPost);//邮政编码
            parames.put("applyHouseStatus", applyHouseStatus);//住宅状况 0自置无按揭  1自置有按揭 2商住两用 3租用 4与父母同住 5集体宿舍 6其他
            parames.put("applyHouseBeginDate", applyHouseBeginDate);//住宅入住年月
            parames.put("applyHouseAreaCode", applyHouseAreaCode);//住宅电话区号 (提示：如无住宅电话请填写直系亲属手机号码，区号不填写)
            parames.put("applyHousePhone", applyHousePhone);//住宅电话号码
            parames.put("applyCarNo", applyCarNo);//车牌号码(如无，可不填)
            parames.put("applyMarrige", applyMarrige);//婚姻状况(0 已婚 1未婚 2其他)
            parames.put("applyFendnum", applyFendnum);//供养人数(0-4 个,5代表5个以上)
            parames.put("applyEducation", applyEducation);//教育程度 (0研究生或以上,1大学（本科）,2大专,3高中,4中专,5初中及以下)
            parames.put("applyPriSchoolName", applyPriSchoolName);//毕业小学名称
            parames.put("linkManName", linkManName);//联系人姓名
            parames.put("linkManRelation", linkManRelation);//与您的关系(0配偶,1父母,2子女,3兄弟/姐妹,4亲戚,5同事,6朋友,7其它)
            parames.put("linkManCompany", linkManCompany);//联系人单位名称
            parames.put("linkManTel", linkManTel);//联系人移动电话
            parames.put("linkManHouseAreaCode", linkManHouseAreaCode);//联系人住宅电话区号(选填)
            parames.put("linkManHousePhone", linkManHousePhone);//联系人住宅电话区号(选填)
            parames.put("actPubId", actPubId);//办卡活动
            parames.put("presentId", presentId);
            

            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/workInfo.html";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
//            htmls.put("完善供职公司资料", content);
            htmls.put("2-wanshangongzhigongsi", content);
            if (!content.contains("preApproveId")) {
            	paramsLogs(md, parames," 完善供职公司资料 您填写的信息有误，请确认后重新填写");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 完善供职公司资料 content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 完善供职公司资料");
                return 0;
            }else {
            	paramsLogs(md, parames," 完善供职公司资料 success");
			}
            element = Jsoup.parse(content);
            preApproveId = element.getElementById("preApproveId").val();
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第三步  完善供职公司资料-----------------------");
            parames.clear();
            parames.put("applyUuid", pccc_uuid);
            parames.put("uuid", pccc_uuid);
            parames.put("applyIncome", applyIncome);
            parames.put("applyOtherinCome", applyOtherinCome);
            parames.put("preApproveId", preApproveId);
            parames.put("applyHasJob", applyHasJob);//就业状态：0全职/有薪, 1兼职,2家庭主妇,3退休人士,4自雇人士,5临时合同工,6学生,7失业人士,8其他
            parames.put("applyCompanyPropery", applyCompanyPropery);//单位性质: 0 机关事业单位,1社会团体,2国有企业,3三资企业,4上市公司,5民营,6私营,7个体
            parames.put("applyIndustryType", applyIndustryType);//行业类别:00 农、林、牧、渔业,01采掘业,02制造业,03电力、煤气及水的生产和供应业,04建筑业,05交通运输、仓储及邮电通信业,06信息传输、计算机服务及软件业,07批发和零售业,08住宿和餐饮业,09金融业,10房地产业,11租赁及商务服务业,12科学研究、技术服务业和地质勘查业,13水利、环境和公共设施管理业,14居民服务和其他服务业,15教育,16卫生、社会保障和社会福利业,17文化、体育和娱乐业,18公共管理及社会组织,19国际组织,21广告业,22电讯业,23银行业,24保险业,25法律业,26军事业,27出版业,28旅游观光业,29酒店业,30国家机关、政党机关和社会团体
            parames.put("applyJobPost", applyJobPost);//职位ID
            parames.put("applyCompanyName", applyCompanyName);//所属公司名称(工作单位名称)
            parames.put("applyCompanyDept", applyCompanyDept);//所属公司部门(任职部门)
            parames.put("applyCompanyProvince", applyCompanyProvince);//公司所属省份(单位省)
            parames.put("applyCompanyCity", applyCompanyCity);//公司所属城市(单位市)
            parames.put("applyCompanyAddress1", applyCompanyAddress1);//公司所属城市区(单位地区或县)
            parames.put("applyCompanyAddress2", applyCompanyAddress2);//公司详细地址(单位详细地址)
            parames.put("applyCompanyPost", applyCompanyPost);//公司所属地邮编(邮政编码)
            parames.put("applyCompanyAreaCode", applyCompanyAreaCode);//公司电话区号
            parames.put("applyCompanyPhone", applyCompanyPhone);//公司电话号码
            parames.put("applyCompanySemicolon", applyCompanySemicolon);//公司电话分号(选填)
            parames.put("wageIncome", wageIncome);//税前年薪收入 （单位W）
            parames.put("otherIncome", otherIncome);//税前年其他收入（单位W）
            parames.put("applyPaymentMode", applyPaymentMode);//薪金支付方式 ：1 银行自动转帐支付,0不是银行自动转帐支付
            parames.put("applyJobStartTime", applyJobStartTime);//现公司入职时间(现单位工作起始年月)
            parames.put("applyFrontJobMonths", applyFrontJobMonths);//前一单位工龄(单位月)
            parames.put("applyIsBocomClient", applyIsBocomClient);//您是否为交通银行客户 1是 0否
            parames.put("applyOtherBankName", applyOtherBankName);//您已持有的贷记卡发卡银行名称
            parames.put("applyAccountAddress", applyAccountAddress);//将信用卡、信用卡有关函件寄往本人 0住宅地址 1单位地址
            //--
            parames.put("trackCode", trackCode);
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/optionalService.html";
            content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "utf-8", requestConfig);
//            htmls.put("自选服务项目", content);
            htmls.put("3-zixuanfuwu", content);
            if (!content.contains("自选服务项目")) {
            	paramsLogs(md, parames,"自选服务项目 -您填写的信息有误，请确认后重新填写");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
                bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] 自选服务项目  content=" + content);
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 自选服务项目");
                return 0;
            }

            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] --------------------第三步  第三步  完善资料确认-----------------------");
            url = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/view.html";
            String applyDealmode = "1";//交易密码选择: 0 仅使用签名确认交易,1使用密码确认交易 (信用卡刷卡消费默认使用签名确认交易，但您可选择开通POS消费验密功能，在中国大陆地区银联商户刷卡消费时使用密码确认交易，在除中国大陆地区以外的其他银联商户刷卡消费的，需按照商户收银员的指引选择使用签名或使用密码确认交易，上述两种交易确认方式均视为您本人对交易的有效授权。)
            String applyContractFlag = "0";//自动转账还款业务：0 不开通, 1开通
            String Automatic = "on";//您同意开通自动转账还款业务，且已阅读并同意遵守<a id="mrm_btnZdhk" class="blue dialogopen" dialogopenurl="/applynew/assets/pc/dialog/ZiDongHuanKuan.html" href="#nogo">《交通银行太平洋贷记卡自动转账还款业务条款》</a>,确认填写的资料完全属实。</span></label>
            String applyRepayBank = "BCOM";//开通了自动还款业务 这个选项有效 否则默认农业银行 无效 :ABCB农业银行,BCOM交通银行,BOCB中国银行,CCBB建设银行,CEBB光大银行,CMBB招商银行,PSBC邮政储蓄银行
            String applyRepayAccount = "";//自动还款借记卡号(开通了自动还款业务 这个选项有效)
            String applyRmbRepay = "0";//还款金额(0全部还款,1最低还款额)(开通了自动还款业务 这个选项有效)
            String applyRepayFrequency = "1";//扣款方式 (0到期还款日扣款,1两次扣款)(开通了自动还款业务 这个选项有效)
            String billType = "EMAIL";//账单方式 (EMAIL 您的账单将以电子邮件形式发送至您填写的电子邮箱) 您的信用卡对账单和相关函件将寄送至信用卡卡片寄送地址
            //String applyAddservice="";
            //&applyAddservice=4&applyAddservice=10

            //我同意开通“用卡无忧”服务，且已阅读并同意遵守 《用卡无忧服务条款及细则》 4
            //我同意开通“信用保障”服务，且已阅读并同意遵守 《信用保障服务条款及细则》。10

			parames.clear();
			parames.put("applyUuid", pccc_uuid);
			parames.put("uuid", pccc_uuid);
			parames.put("applyDealmode", applyDealmode);
			parames.put("applyContractFlag", applyContractFlag);
			parames.put("Automatic", Automatic);
			parames.put("applyRepayBank", applyRepayBank);//
			parames.put("applyRepayAccount", applyRepayAccount);//
			parames.put("applyRmbRepay", applyRmbRepay);//
			parames.put("applyRepayFrequency", applyRepayFrequency);//
			parames.put("billType", billType);//
            //--
            parames.put("trackCode", trackCode);
			url="https://creditcardapp.bankcomm.com/applynew/front/apply/new/view.html";
			content=HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext,"utf-8", requestConfig);
//			htmls.put("主持卡人基本资料", content);
			htmls.put("4-zhuchikerenjibenziliao", content);
			if (!content.contains("newSubmit")) {
				logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] 主持卡人基本资料  content["+content+"]");
				paramsLogs(md, parames,"主持卡人基本资料 -您填写的信息有误，请确认后重新填写");
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc("您填写的信息有误，请确认后重新填写");
            	bean.setBusiJSON("fail");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 主持卡人基本资料");
				return 0;
			}
			logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] --------------------第三步  完善资料确认提交-----------------------");
			parames.clear();
			parames.put("applyUuid", pccc_uuid);
			parames.put("preApproveId", preApproveId);
            //--
            parames.put("trackCode", trackCode);
			url="https://creditcardapp.bankcomm.com/applynew/front/apply/new/newSubmit.html";
			content=HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext,"utf-8", requestConfig);
//			htmls.put("完善资料确认提交", content);
			htmls.put("5-wanshanziliaoqueren", content);
			if (!content.contains("成功递交")) {
				
//				parames.clear();
//				parames.put("applyUuid", pccc_uuid);
//				parames.put("preApproveId", "1");
//				url="https://creditcardapp.bankcomm.com/applynew/front/apply/new/newSubmit.html";
//				content=HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext,"utf-8", requestConfig);
				
				String msg="您填写的信息有误，请确认后重新填写";
				if (content.contains("tipText")) {
					try {
						org.jsoup.nodes.Document doc= Jsoup.parse(content);
						msg=doc.getElementsByClass("tipText").get(0).text();
						if (content.contains("您提交失败，请返回重新申请")) {
							msg="您提交失败，请返回重新申请!如果重复出现此提示，请核实您填写的资料是否正确，然后等待交通银行办卡人员联系，如未联系您则代表您没有通过交通银行预审。";
						}
					} catch (Exception e) {
						msg="您填写的信息有误，请确认后重新填写";
					}
				}
				saveHtml(md, htmls);
				paramsLogs(md, parames,"完善资料确认提交 -您填写的信息有误，请确认后重新填写");
				bean.setBusiErrCode(0);
				bean.setBusiErrDesc(msg);
            	bean.setBusiJSON("fail");
				logger.info("idcard["+md.getCidcard()+"] mobile["+md.getCphone()+"] 完善资料确认提交  content["+content+"]");
                logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 完善资料确认提交");
				return 0;
			}
			paramsLogs(md, parames,"完善资料确认提交 -成功提交");
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("您的申请资料已成功递交，我们将尽快审核并可能通过02195559与您核实信息；为加快您的审核，在非工作时间我们也可能与您联系，还请注意接听。");
            bean.setBusiJSON("success");
            BankApplyListener.sendSucess(BankEnum.jiaotong,BankApplyStepEnum.submit_apply);
            return 1;

        } catch (Exception e) {
        	paramsLogs(md, parames,"申请失败 -您填写的信息有误，请确认后重新填写");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申请失败，请稍后再试");
            bean.setBusiJSON("fail");
            logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyForTheJiaoTongBank 异常 content[" + content + "]", e);
            logger.info("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] data error ibankid=16 applyForTheJiaoTongBank 异常");
			saveHtml(md, htmls);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("idcard[" + md.getCidcard() + "] mobile[" + md.getCphone() + "] applyForTheJiaoTongBank httpClient close 异常", e);
                }
            }
        }
        return 0;
    }

	private static void saveHtml(MaterialModel md, Map<String, String> htmls) {
		try {
			List<ErrorRequestBean> beans=new ArrayList<>();
			long times=System.currentTimeMillis();
			for (String key : htmls.keySet()) {
				String filename=md.getCphone()+"-"+md.getCidcard()+"-"+key+"-"+times+ ".html";
//				ErrorUtils.saveApplyFile(ErrorBankEnum.jiaotong, null, htmls.get(key), filename);
				ErrorRequestBean bean=new ErrorRequestBean(filename, null, htmls.get(key), null);
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

}
