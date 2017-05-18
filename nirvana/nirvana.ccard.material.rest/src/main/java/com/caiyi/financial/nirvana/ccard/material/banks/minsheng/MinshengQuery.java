package com.caiyi.financial.nirvana.ccard.material.banks.minsheng;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MinshengQuery {
	 public static Logger logger = LoggerFactory.getLogger("materialBeanImpl");
	 
	 public static void main(String[] args){
		 MaterialBean bean = new MaterialBean();
		 bean.setIdcardid("120000199001101216");
		 minshengJDCX(bean);
	 }

    /*
        进度查询不可用 暂定都返回为 审核中

     */
    public static int minshengJDCX(MaterialBean bean) {
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("审核中");
        bean.setBusiJSON("{\"resultcode\":0,\"resultdesc\":\""+"卡片正在审核中"+"\",\"resean\":\"\"}");
        bean.setCstatus("0");
        return 0;
    }
        //交通进度查询
    public static int minshengJDCXTemp(MaterialBean bean) {
         String url = "";
         String content = "";
         try {
             if (StringUtils.isEmpty(bean.getIdcardid())) {
                 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("证件ID不能为空");
                 bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
                 return 0;
 			}
             CookieStore cookieStore = getMinShengCookie();
             CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
             HttpContext localContext = new BasicHttpContext();
             // 设置请求和传输超时时间
             RequestConfig.custom().setConnectTimeout(30000);
             RequestConfig.custom().setSocketTimeout(30000);
             RequestConfig.custom().setConnectionRequestTimeout(30000);
             RequestConfig requestConfig = RequestConfig.custom().build();
             if (cookieStore != null ){
            	   localContext.setAttribute("http.cookie-store", cookieStore);
             }
             Map<String, String> requestHeaderMap = new HashMap<String, String>();
             requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
             requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
             requestHeaderMap.put("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
             requestHeaderMap.put("Connection", "Keep-Alive");
             requestHeaderMap.put("Host", "ebank.cmbc.com.cn");
             requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");
             requestHeaderMap.put("Referer", "https://ebank.cmbc.com.cn/weblogic/servlets/EService/CSM/Processh405Svlt?txcode=h40500&channelID=u");
             
             url = "https://ebank.cmbc.com.cn/weblogic/servlets/EService/CSM/MainService?txcode=h40501";
             Map<String, String> parames = new HashMap<String, String>();
             parames.put("CC_CertiType", "1");  // 证件类型  1：身份证
             parames.put("CC_CertiNO", bean.getIdcardid());
             parames.put("txcode", "h40501");
             content = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gb2312", requestConfig);
             logger.info(bean.getOrderid() + " content=" + content);
             String result = "";
             try {
            	 if (StringUtils.isEmpty(content)){
            		 
            	 }else {
            		 if (content.contains("记录未找到") && !content.contains("申请状态")){
                		 result = "记录未找到";
                	 } else {
                         org.jsoup.nodes.Element element = Jsoup.parse(content);
                         org.jsoup.nodes.Element retEle = element.getElementById("formTable");
                         logger.info("retEle:" + retEle.toString());
                         org.jsoup.nodes.Element retTrEle = retEle.getElementsByTag("tr").get(1).getElementsByTag("td").get(1);
                         result = retTrEle.text().replaceAll("　", "").replaceAll(" ", "").trim();
                	 }
            	 }
             }catch(Exception e){
            	 logger.info("e:" + e);
             }
             logger.info("result:" + result + "end");
             if (result.contains("记录未找到")) {
                 bean.setBusiErrCode(1);
                 bean.setBusiErrDesc("记录未找到");
                 bean.setBusiJSON("{\"resultcode\":3,\"resultdesc\":\"\",\"resean\":\""+result+"\"}");
                 return 0;
             }  else if (result.contains("没有通过")) {
                 bean.setBusiErrDesc("未通过");
                 bean.setBusiJSON("{\"resultcode\":2,\"resultdesc\":\""+result+"\",\"resean\":\"\"}");
                 bean.setCstatus("2");
             } else if (result.contains("通过") && !result.contains("没有通过")) {
                 bean.setBusiErrCode(1);
                 bean.setBusiErrDesc("通过");
                 bean.setCstatus("1");
                 bean.setBusiJSON("{\"resultcode\":1,\"resultdesc\":\"" + result + "\",\"resean\":\"\"}");
             } else if (result.contains("审核中")) {
                 bean.setBusiErrCode(1);
                 bean.setBusiErrDesc("审核中");
                 bean.setBusiJSON("{\"resultcode\":0,\"resultdesc\":\""+result+"\",\"resean\":\"\"}");
                 bean.setCstatus("0");
             } else {
                 bean.setBusiErrCode(0);
                 bean.setBusiErrDesc("卡片正在审核中，可稍后再查!");
                 bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"卡片正在审核中，可稍后再查!\"}");
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
    public static CookieStore getMinShengCookie()  {
    	 CookieStore cookieStore = null;
        try {
            CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Host", "ebank.cmbc.com.cn");
            String url = "https://ebank.cmbc.com.cn/weblogic/servlets/EService/CSM/Processh405Svlt?txcode=h40500&channelID=u" ;
            String content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext);
            return cookieStore;
        } catch (Exception e) {
            
            return null;
        }
     
    }
    
    
}
