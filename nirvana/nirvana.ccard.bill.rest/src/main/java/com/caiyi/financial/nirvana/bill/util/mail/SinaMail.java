package com.caiyi.financial.nirvana.bill.util.mail;

import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario on 2016/7/28 0028.
 */
public class SinaMail extends BaseMail{
    public static int mailLogin(Channel bean,Logger logger){
        String mailaddress = bean.getLoginname();
        String mailPwd = bean.getPassword();

        HttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();

        RequestConfig defaultRequestConfig = RequestConfig.custom().setCircularRedirectsAllowed(true).build();
        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(50000).setConnectTimeout(50000).setConnectionRequestTimeout(50000).build();
        httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        localContext.setAttribute("http.request-config", requestConfig);
        localContext.setAttribute("http.cookie-store", cookieStore);
        Map<String, String> requestHeaderMap=new HashMap<String, String>();
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate");
        requestHeaderMap.put("Accept-Language", "zh-cn");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1");
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        requestHeaderMap.put("Connection", "Keep-Alive");
        requestHeaderMap.put("Host", "passport.weibo.cn");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        String content="";
        String url="";
        url="https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fm.weibo.cn%2F";
        content=HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gb2312",false, requestConfig);

        Map<String, String> parames=new HashMap<String, String>();
        parames.put("username", mailaddress);
        parames.put("password", mailPwd);
        parames.put("savestate", "1");
        parames.put("certType", "0");
        parames.put("ec", "1");
        parames.put("pagerefer", "");
        parames.put("entry", "mweibo");
        parames.put("wentry", "");
        parames.put("loginfrom", "");
        parames.put("client_id", "");
        parames.put("code", "");
        parames.put("qq", "");
        parames.put("hff", "");
        parames.put("hfp", "");

        requestHeaderMap.put("Referer", "https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fm.weibo.cn%2F");
        requestHeaderMap.put("Origin", "https://passport.weibo.cn");
        requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded");
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate");
        requestHeaderMap.put("Accept", "*/*");
        requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        url="https://passport.weibo.cn/sso/login";
        content=HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient,localContext, "gbk");

        JSONObject jsObj=new JSONObject(content);
        String retcode=String.valueOf(jsObj.get("retcode"));
        String emsg=String.valueOf(jsObj.get("msg"));

        if (!"20000000".equals(retcode)) {
            String errMsg = revert(emsg);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(errMsg);
            logger.info("新浪邮箱登录失败>>>>>>>>>"+content);
            return 0;
        }else{
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("邮箱登录成功");
            cc.set(bean.getCuserId()+"XingLangMailObject"+mailaddress, content, 1000*60*30);
            cc.set(bean.getCuserId()+"XingLangMailCookie"+mailaddress, cookieStore, 1000*60*30);
        }
        return 1;
    }

    public static String revert(String str) {
        str = (str == null ? "" : str);
        if (str.indexOf("\\u") == -1)// 如果不是unicode码则原样返回
            return str;
        StringBuffer sb = new StringBuffer(1000);
        for (int i = 0; i < str.length() - 6;) {
            String strTemp = str.substring(i, i + 6);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); j++) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a':
                        t = 10;
                        break;
                    case 'b':
                        t = 11;
                        break;
                    case 'c':
                        t = 12;
                        break;
                    case 'd':
                        t = 13;
                        break;
                    case 'e':
                        t = 14;
                        break;
                    case 'f':
                        t = 15;
                        break;
                    default:
                        t = tempChar - 48;
                        break;
                }

                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 6;
        }
        return sb.toString();
    }
}
