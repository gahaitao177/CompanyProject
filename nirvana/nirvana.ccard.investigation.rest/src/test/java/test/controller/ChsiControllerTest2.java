package test.controller;

import com.alibaba.fastjson.JSON;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.*;


/**
 * Created by lichuanshun on 2017/1/13.
 */
public class ChsiControllerTest2 {

    public static void main(String[] args) throws Exception {
        // 初次登录
        String indexUrl = "https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";

        Map<String,Object> context = new HashMap<>();
        context.put("success", false);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        // 设置请求和传输超时时间
        localContext.setAttribute("http.cookie-store", cookieStore);
        RequestConfig.custom().setConnectTimeout(30000);
        RequestConfig.custom().setSocketTimeout(30000);
        RequestConfig.custom().setConnectionRequestTimeout(30000);
        RequestConfig requestConfig = RequestConfig.custom().build();
        //请求头信息
        Map<String, String> requestHeaderMap = new HashMap<String, String>();
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
        requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        requestHeaderMap.put("Cache-Control", "max-age=0");
        requestHeaderMap.put("Connection", "keep-alive");
        requestHeaderMap.put("Host", "account.chsi.com.cn");
        requestHeaderMap.put("Upgrade-Insecure-Requests", "1");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) hrome/55.0.2883.75 Safari/537.36");
        //请求获取cookie
        String errorContent = HpClientUtil.httpGet(indexUrl, requestHeaderMap, httpClient, localContext, "utf-8",false, requestConfig);
        Document doc= Jsoup.parse(errorContent);
        String lt = "";
        Elements lts = doc.getElementsByAttributeValue("name","lt");
        if (lts != null){
            lt = lts.get(0).attr("value");
        }
        System.out.println("lt:" + lt);
        if (isNeedCaptcha(doc)){
            System.out.println("-------------------------------------------------");
        }
        //请求参数
        HashMap params = new HashMap();
//        params.put("username","492348312@qq.com");
//        params.put("password","xiaohuoban443");
//        params.put("username", "125816149@qq.com");
//        params.put("password","jl125816");
        params.put("username", "185158064@qq.com");
        params.put("password","lqwlove123456789");
        params.put("lt",lt);
        params.put("_eventId","submit");
        params.put("submit","登  录");
        System.out.println("params:" + params);

//        errorContent = httpPost(indexUrl, requestHeaderMap, params, httpClient, localContext, "utf-8", requestConfig);
        doc = Jsoup.parse(errorContent);
        if (isNeedCaptcha(doc)) {
            System.out.println("-------------------------------------------------");
            String captchaUrl = "https://account.chsi.com.cn/passport/captcha.image?id="+ Math.random();
            String filePath = "D:\\yzm";
            System.out.print(filePath);
            HpClientUtil.httpGetImage(captchaUrl,requestHeaderMap,filePath,httpClient,localContext,requestConfig);
            System.out.print("请输入：");
            Scanner input = new Scanner(System.in);
            String val = input.nextLine();       // 等待输入值
            System.out.println("您输入的是："+val);
            input.close(); // 关闭资源
            params.put("captcha",val);
        }
        //输入验证码后的请求地址
        System.out.println("带参数登陆请求地址：" + indexUrl);
        indexUrl = httpPostMy(indexUrl, requestHeaderMap, params, httpClient, localContext, "utf-8", requestConfig);
        System.out.println(indexUrl);

        //发送get请求
        System.out.println("重定向请求地址：" + indexUrl);
        System.out.println("cookieStore" + cookieStore);
        requestHeaderMap.put("Host", "my.chsi.com.cn");
        HpClientUtil.httpGet(indexUrl, requestHeaderMap, httpClient, localContext, "utf-8", false, requestConfig);

        indexUrl = "https://my.chsi.com.cn/archive/gdjy/xj/show.action";
        System.out.println("首页请求地址：" + indexUrl);
        errorContent = HpClientUtil.httpGet(indexUrl, requestHeaderMap, httpClient, localContext, "utf-8", false, requestConfig);
        System.out.println(errorContent);
        Document index = Jsoup.parse(errorContent);
    }



    private static  boolean isNeedCaptcha(Element doc){
        boolean isNeed = false;
        Element captcha = doc.getElementById("captcha");
        if (captcha == null){
            System.out.println("captcha:不需要验证码");
        }else {
            isNeed = true;
            System.out.println("captcha:需要验证码");
        }
        return isNeed;
    }

    public static String httpPostMy(String url, Map<String, String> headers, Map<String, String> parames, HttpClient httpclient, HttpContext localContext, String encode, RequestConfig requestConfig) {
        String context = "";
        HttpPost httpPost = null;
        InputStream in = null;
        HttpResponse e2 = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if(headers != null) {
                Iterator e = headers.keySet().iterator();

                while(e.hasNext()) {
                    String entity = (String)e.next();
                    httpPost.setHeader(entity, (String)headers.get(entity));
                }
            }
            if(parames != null) {
                ArrayList e1 = new ArrayList();
                Iterator entity1 = parames.entrySet().iterator();

                while(entity1.hasNext()) {
                    Map.Entry statusCode = (Map.Entry)entity1.next();
                    e1.add(new BasicNameValuePair((String)statusCode.getKey(), (String)statusCode.getValue()));
                }

                UrlEncodedFormEntity statusCode1 = new UrlEncodedFormEntity(e1, encode);
                httpPost.setEntity(statusCode1);
            }
            e2 = httpclient.execute(httpPost, localContext);
            System.out.println("e2:" + e2);
            System.out.println(JSON.toJSON(e2));
            HttpEntity entity2 = e2.getEntity();
            String statusCode2 = e2.getStatusLine().toString();
            System.out.println("statusCode2:" + statusCode2);
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            if(httpPost != null) {
                httpPost.abort();
            }
        }
        return e2.getLastHeader("Location").getValue();
    }


    public static String httpGetMy(String url, Map<String, String> headers, HttpClient httpclientme, HttpContext localContext, String encode, boolean isencode, RequestConfig requestConfig) {
        String context = "";
        HttpGet httpget = null;
        InputStream in = null;
        HttpResponse e1 = null;
        try {
            httpget = new HttpGet(url);
            httpget.setConfig(requestConfig);
            if (headers != null) {
                Iterator e = headers.keySet().iterator();

                while (e.hasNext()) {
                    String entity = (String) e.next();
                    httpget.setHeader(entity, (String) headers.get(entity));
                }
            }
            e1 = httpclientme.execute(httpget, localContext);
            String statusCode = e1.getStatusLine().toString();

        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            if (httpget != null) {
                httpget.abort();
            }

        }
        return e1.getLastHeader("Location").getValue();
    }

}