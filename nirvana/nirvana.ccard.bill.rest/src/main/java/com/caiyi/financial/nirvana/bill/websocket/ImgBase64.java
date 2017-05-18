package com.caiyi.financial.nirvana.bill.websocket;

/**
 * Created by dengh on 2016/6/21.
 */


import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.util.BankHelper;
import com.hsk.cardUtil.HpClientUtil;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ImgBase64 {

    public static void main(String[] args) throws IOException {
//        String strImg = GetImageStr();
        String strImg="iVBORw0KGgoAAAANSUhEUgAAADwAAAAeCAYAAABwmH1PAAAAAXNSR0IArs4c6QAAAARnQU1BAACx\n" +
                "jwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAGMSURBVFhH7ZUxbsIwFIYZK7F17h06darEzsLQ\n" +
                "E1RsXIAdMXIANrgAAzMT4go5kBs7/sOLY+e9tGksWxk+yXJ+XvI923hWFIUampfZh8H3LDaTcO5M\n" +
                "wrkzCfs5q8Nqrg6X5vzn25eBzlX4836E2ctafZe5mt3Zn2MQCN/UaVO9JPRRTXE+/0SYtbLPTNWk\n" +
                "30h3C9/3aqsLWzgBiHN5XFuy2rYpm716kPnHcVH+bqFOd5rlCQtDVnfRjjuFSR7inLCs9ljCFIlw\n" +
                "yfvy1YA8xDW+vEFSO7SlnSZIGFQYQLxuQDkXFJfWtjkcge3x5s8x/Iuwm6fyLXFB7etOS9Ltiz+7\n" +
                "tbo6WY5RhF2ouB531ra12itabeu+Kx1FmELFQSPTOr/ArnLPqymqML2eIN6SH3qF8VLfw5oRhGkW\n" +
                "4npM5U0DTOYPZzimsKEjS8Wre3ceaIAc2ZaODBV36duAJIQBxEPyGq4BSQlTOHHgiicrDKTiIHlh\n" +
                "IBXPRhhw4tkJg5C4WFh0XyfAJJw7k3DeFOoHaIAJVpYI2HsAAAAASUVORK5CYII=";




        String base64Img=null;
        HttpRequester httprequest = new HttpRequester();

        CloseableHttpClient httpClient =null;
        String url="https://creditcard.ecitic.com/citiccard/cppnew/entry.do?func=entryebank&ebankPage=mainpage";
        httpClient = HttpClients.createDefault();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute("http.cookie-store", cookieStore);
        RequestConfig.custom().setConnectTimeout(20000);
        RequestConfig.custom().setSocketTimeout(20000);
        RequestConfig.custom().setConnectionRequestTimeout(20000);
        RequestConfig requestConfig = RequestConfig.custom().build();
        Map<String, String> requestHeaderMap=new HashMap<String, String>();
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate");
        requestHeaderMap.put("Host", "creditcard.ecitic.com");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        requestHeaderMap.put("Connection", "Keep-Alive");
        requestHeaderMap.put("Cache-Control", "private");
        requestHeaderMap.put("Accept-Language", "zh-CN");
//        if (!StringUtil.isEmpty(userIp)&&!userIp.equals("127.0.0.1")) {
//            requestHeaderMap.put("X-Forwarded-For", userIp);
//        }
//        logger.info(bean.getCuserId()+"zhongxin userIp="+userIp);
        String content= HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "GBK");

        String channel=content.substring(content.indexOf("var channel"), content.indexOf("var source"));
        String source =content.substring(content.indexOf("var source"), content.indexOf("var from"));
        channel=channel.substring(channel.indexOf("\"")+1, channel.lastIndexOf("\""));
        source=source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\""));
        String vcodeUrl="https://creditcard.ecitic.com/citiccard/cppnew/jsp/valicode.jsp?time="+System.currentTimeMillis();


        BufferedImage localBufferedImage=null;
        localBufferedImage=HpClientUtil.getRandomImageOfJPEG(vcodeUrl, requestHeaderMap, httpClient, localContext, requestConfig);
        base64Img= BankHelper.GetImageBase64(localBufferedImage, "jpeg");
        System.out.println(base64Img);
        url = "http://192.168.3.50:8080/captcha/hack?captcha=";

        try {
            // url =   java.net.URLDecoder.decode(url,   "utf-8");
            strImg=  java.net.URLEncoder.encode(base64Img,   "utf-8");
            url = url +strImg + "&bankid=2&imgtype=1" ;
            HttpRespons hr = httprequest.sendGet(url);
            //获取招商银行的登录sessionID
            content=hr.getContent();
            System.out.println(content);
            JSONObject json=JSONObject.parseObject(content);
            System.out.println(json.getString("text"));



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(url);
        GenerateImage(base64Img);
    }
    //图片转化成base64字符串
    public static String GetImageStr()
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = "D:\\javahsk\\captchaservice\\caiyi.captcha\\testData\\0394.png";//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "D:\\opt//222.jpg";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
