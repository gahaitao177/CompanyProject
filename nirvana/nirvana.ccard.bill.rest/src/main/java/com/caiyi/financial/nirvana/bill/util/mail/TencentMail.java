package com.caiyi.financial.nirvana.bill.util.mail;
import com.caiyi.financial.nirvana.bill.util.BankHelper;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lichuanshun on 16/7/13.
 * 腾讯邮箱登录相关
 */
public class TencentMail extends BaseMail{

    /**
     *
     * @param bean
     * @return
     */
    public static int login(Channel bean,Logger logger) {
        String mailaddress=bean.getLoginname();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        // 设置请求和传输超时时间
        RequestConfig.custom().setConnectTimeout(20000);
        RequestConfig.custom().setSocketTimeout(20000);
        RequestConfig.custom().setConnectionRequestTimeout(20000);
        RequestConfig requestConfig = RequestConfig.custom().build();

        Map<String, String> requestHeaderMap=new HashMap<String, String>();
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch");
        requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13C75 Safari/601.1");
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        requestHeaderMap.put("Connection", "Keep-Alive");
        // add by lcs 20160216
        if (!CheckUtil.isNullString(bean.getIpAddr())){
            requestHeaderMap.put("X-Forwarded-For", bean.getIpAddr());
        }
        localContext.setAttribute("http.cookie-store", cookieStore);
        String url="";
        String errorContent="";

        //load首页链接的地址
		/*url="https://w.mail.qq.com/cgi-bin/loginpage?f=xhtml&kvclick="+URLEncoder.encode("loginpage|app_push|enter|ios", "utf-8")+"&&ad=false&f=xhtml&kvclick=loginpage%7Capp_push%7Center%7Cios%26ad%3Dfalse&s=session_timeout&f=xhtml&autologin=n&uin=&aliastype=&from=&tiptype=";
		errorContent=HpClientUtil.httpGet(url, requestHeaderMap, httpclient, localContext, "UTF-8", false , requestConfig);
		System.out.println(errorContent);
		if (!CheckUtil.isNullString(errorContent)) {
			return;
		}*/

        url="https://ui.ptlogin2.qq.com/cgi-bin/login?style=9&appid=522005705&daid=4&s_url=https%3A%2F%2Fw.mail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwsk%26delegate_url%3D%26f%3Dxhtml%26target%3D&hln_css=http%3A%2F%2Fmail.qq.com%2Fzh_CN%2Fhtmledition%2Fimages%2Flogo%2Fqqmail%2Fqqmail_logo_default_200h.png&low_login=1&hln_autologin=%E8%AE%B0%E4%BD%8F%E7%99%BB%E5%BD%95%E7%8A%B6%E6%80%81&pt_no_onekey=1";
        errorContent= HpClientUtil.httpGet(url, requestHeaderMap, httpclient, localContext, "UTF-8",false,requestConfig);

        url="https://ssl.ptlogin2.qq.com/check?pt_tea=1&uin="+mailaddress+"&appid=522005705&ptlang=2052&r="+Math.random();
        errorContent=HpClientUtil.httpGet(url, requestHeaderMap, httpclient, localContext, "UTF-8",false,requestConfig);

        // 正确格式 ptui_checkVC('0','!LAW','\x00\x00\x00\x00\x13\xb6\xcb\xd9','3df476d7145c143ff34d20cc5cbf4681346df9b88315ecde935267a395182964fdb93effe66ad368e059171453f37126f6480c7999c0ec97','0');

        if (!errorContent.contains("ptui_checkVC")||!errorContent.contains("(")||!errorContent.contains(")")) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("邮箱登录失败，请稍后再试或联系客服询问");
            logger.info(bean.getCuserId()+ " 接口参数格式不正确 ptui_checkVC="+errorContent);
            return 0;
        }
        String ptui_checkVC=errorContent.substring(errorContent.indexOf("(")+1, errorContent.indexOf(")")).replaceAll("'", "");
        String [] prs=ptui_checkVC.split(",");
        if (prs.length!=5) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("邮箱登录失败，请稍后再试或联系客服询问");
            logger.info(bean.getCuserId()+ " 接口参数格式不正确 ptui_checkVC="+errorContent);
            return 0;
        }
        String pcode=prs[0];
        cc.set(bean.getCuserId()+"QQMailCookie"+mailaddress, cookieStore, 1000*60*30);
        logger.info(bean.getCuserId()+"QQMailCookie"+mailaddress,cookieStore);
        if ("0".equals(pcode)) {//正常登录
            bean.setBankSessionId(ptui_checkVC);
            bean.setBusiErrCode(1);
        }else if ("1".equals(pcode)) {//异地登录
            cc.set(bean.getCuserId()+"QQMailPtui_checkVC"+mailaddress, ptui_checkVC, 1000*60*30);
            bean.setBusiErrCode(3);
            bean.setBusiErrDesc("异地登录需要验证码");
            bean.setCurrency(getVerifyCode(bean,logger));
            logger.info(bean.getCuserId()+ " 异地登录 ptui_checkVC["+errorContent+"] mailaddress["+mailaddress+"]");
            return 0;
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("帐号格式不正确，请检查");
            return 0;
        }
        return 1;
    }

    /**
     * 获取验证码
     * @param bean
     * @param logger
     * @return
     * @throws IOException
     */
    public static String getVerifyCode(Channel bean,Logger logger){
        BufferedImage localBufferedImage=null;
        String mailaddress=bean.getLoginname();
        String base64Str = null;
        String url="";
        String errorContent="";
        try {
            Object object=cc.get(bean.getCuserId()+"QQMailCookie"+mailaddress);
            Object object2=cc.get(bean.getCuserId()+"QQMailPtui_checkVC"+mailaddress);
            logger.info("getQQEmailVcode" + bean.getCuserId()+"QQMailCookie"+mailaddress);
            if (object==null||object2==null) {
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("请求已失效请重新操作");
                return null;
            }
            CookieStore cookieStore=(CookieStore) object;
            String ptui_checkVC=String.valueOf(object2);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig.custom().setConnectTimeout(20000);
            RequestConfig.custom().setSocketTimeout(20000);
            RequestConfig.custom().setConnectionRequestTimeout(20000);
            RequestConfig requestConfig = RequestConfig.custom().build();

            Map<String, String> requestHeaderMap=new HashMap<String, String>();
            requestHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch");
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13C75 Safari/601.1");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            localContext.setAttribute("http.cookie-store", cookieStore);

            String [] prs=ptui_checkVC.split(",");
            String verifycode=prs[1];
            // update by lcs 20161122 接口修改 start
            // url="https://ssl.captcha.qq.com/cap_union_show?captype=3&lang=2052&aid=522005705&uin="+mailaddress+"&cap_cd="+verifycode+"&pt_style=9&v="+Math.random();
            url = "https://ssl.captcha.qq.com/cap_union_show_new?aid=522005705&captype=&protocol=https&clientype=1" +
                    "&disturblevel=&apptype=2&noheader=0&uin=" + mailaddress + "&color=&" +
                    "cap_cd=" + verifycode  + "&rnd=" +  Math.random();
            errorContent=HpClientUtil.httpGet(url, requestHeaderMap, httpclient, localContext, "UTF-8",false,requestConfig);
            // update by lcs 20161122 接口修改 end

            // add test by lcs 20160704
            cc.add("testqqvcode" + bean.getCuserId(), errorContent,3600000);
            if (!errorContent.contains("g_click_cap_sig")||!errorContent.contains("g_cap_postmsg_seq")) {
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("邮箱登录失败，请稍后再试或联系客服询问！");
                logger.info(bean.getCuserId()+ " 接口参数格式不正确 g_click_cap_sig="+errorContent);
                return null;
            }
            // update by lcs 20160704 获取sig 方式修改 start
//			String vsig=errorContent.substring(errorContent.indexOf("g_click_cap_sig"),errorContent.indexOf("g_cap_postmsg_seq"));
//			String vsig=errorContent.substring(errorContent.indexOf("var g_click_cap_sig=\"") + "var g_click_cap_sig=\"".length(),errorContent.indexOf("\",g_cap_postmsg_seq=1,"));
//			vsig=vsig.substring(vsig.indexOf("\"")+1, vsig.lastIndexOf("\""));
//			logger.info(bean.getCuserId()+" 获取到接口访问参数vsig["+vsig+"]");

            // update by lcs 20160704 获取sig 方式修改 end

//			url="https://ssl.captcha.qq.com/getQueSig?aid=522005705&uin="+mailaddress+"&captype=48&sig="+vsig+"*&"+Math.random();
//			errorContent=HpClientUtil.httpGet(url, requestHeaderMap, httpclient, localContext, "UTF-8",false,requestConfig);
//
//			String mgsig=errorContent.substring(errorContent.indexOf("cap_getCapBySig"),errorContent.lastIndexOf("\""));
//			mgsig=mgsig.substring(mgsig.indexOf("\"")+1);
//			String mgsig = vsig;
            String mgsig = getSigFromHtml(errorContent, bean.getCuserId(),logger);
            logger.info(bean.getCuserId()+" 获取到接口访问参数mgsig["+mgsig+"]");
            if (CheckUtil.isNullString(mgsig)){
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("获取图片验证码失败");
                return null;
            }

            url="https://ssl.captcha.qq.com/getimgbysig?aid=522005705&uin="+mailaddress+"&sig="+mgsig;
            localBufferedImage=HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpclient, localContext, requestConfig);

            cc.set(bean.getCuserId()+"QQMailCookie"+mailaddress, cookieStore, 1000*60*30);
            cc.set(bean.getCuserId()+"QQMailPtui_checkVC"+mailaddress, ptui_checkVC);
            cc.set(bean.getCuserId()+"QQMailmgsig"+mailaddress, mgsig);
            base64Str = BankHelper.GetImageBase64(localBufferedImage,"jpeg");

        } catch (Exception e) {
            logger.error(bean.getCuserId()+" getQQEmailVcode异常 errorContent["+errorContent+"]", e);
//            bean.setBusiErrCode(0);
            base64Str = null;
        }
        if (CheckUtil.isNullString(base64Str)){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取验证码失败");
        }
        return base64Str;
    }

    //
    private static String getSigFromHtml(String htmlContent,String cuserid,Logger logger){
        String sig = "";
        try{
            int indexOne = htmlContent.indexOf("var g_click_cap_sig=\"") + "var g_click_cap_sig=\"".length();
            int index2 = htmlContent.indexOf("g_cap_postmsg_seq=1,");
            logger.info("index1=" + indexOne + ",index2:" + index2);
            sig = htmlContent.substring(indexOne,index2).replace("\",", "");
        }catch(Exception e){
            e.printStackTrace();
            logger.error(cuserid+" getQQEmailVcodegetSigFromHtml异常 errorContent", e);
        }
        return sig;
    }


    public static int checkEmailCode(Channel bean,Logger logger){
        String mailaddress=bean.getLoginname();
        String url="";
        String errorContent="";
        try {
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码不能为空");
                return 0;
            }
            Object object=cc.get(bean.getCuserId()+"QQMailCookie"+mailaddress);
            Object object2=cc.get(bean.getCuserId()+"QQMailPtui_checkVC"+mailaddress);
            Object object3=cc.get(bean.getCuserId()+"QQMailmgsig"+mailaddress);

            if (object==null||object2==null||object3==null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请求已失效请重新操作");
                return 0;
            }

            CookieStore cookieStore=(CookieStore) object;
            String ptui_checkVC=String.valueOf(object2);
            String mgsig=String.valueOf(object3);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig.custom().setConnectTimeout(20000);
            RequestConfig.custom().setSocketTimeout(20000);
            RequestConfig.custom().setConnectionRequestTimeout(20000);
            RequestConfig requestConfig = RequestConfig.custom().build();

            Map<String, String> requestHeaderMap=new HashMap<String, String>();
            requestHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch");
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13C75 Safari/601.1");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            // add by lcs 20160216
            if (!CheckUtil.isNullString(bean.getIpAddr())){
                requestHeaderMap.put("X-Forwarded-For", bean.getIpAddr());
            }
            localContext.setAttribute("http.cookie-store", cookieStore);
            String [] prs=ptui_checkVC.split(",");
            String pcode=prs[0];
            String verifycode=prs[1];
            String randstr=verifycode;
            String st=prs[2];
            String sig=prs[3];
            String rcode=prs[4];

            url="https://ssl.captcha.qq.com/cap_union_verify?aid=522005705&uin="+mailaddress+"&captype=48&ans="+bean.getCode()+"&sig="+mgsig+"&"+Math.random();
            errorContent=HpClientUtil.httpGet(url, requestHeaderMap, httpclient, localContext, "UTF-8",false,requestConfig);

            // add  by lcs 20160414 增加异常日志 测试消除后删除
            if (CheckUtil.isNullString(errorContent)){
                logger.info("templog" + bean.getCuserId() + "," + mailaddress + "," + bean.getMailPwd() + "," +  bean.getCode());
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("远程服务器异常,请重试");
                return 0;
            }
            String json=errorContent.substring(errorContent.indexOf("(")+1, errorContent.lastIndexOf(")"));

            JSONObject jsonOBJ=new JSONObject(json);
            randstr=String.valueOf(jsonOBJ.get("randstr"));
            sig=String.valueOf(jsonOBJ.get("sig"));
            rcode=String.valueOf(jsonOBJ.get("rcode"));
            if (!"0".equals(rcode)) {
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("验证码错误,请重试");
                bean.setCurrency(getVerifyCode(bean,logger));
                logger.info(bean.getCuserId()+" 验证码错误，请重试["+errorContent+"]");
                return 0;
            }
            cc.set(bean.getCuserId()+"QQMailCookie"+mailaddress, cookieStore, 1000*60*30);
            bean.setBankSessionId(pcode+","+randstr+","+st+","+sig+","+rcode);
            return 1;
        } catch (Exception e) {
            logger.error(bean.getCuserId()+" checkQQEmailCode异常 errorContent["+errorContent+"]", e);
        }
        return 0;
    }
}
