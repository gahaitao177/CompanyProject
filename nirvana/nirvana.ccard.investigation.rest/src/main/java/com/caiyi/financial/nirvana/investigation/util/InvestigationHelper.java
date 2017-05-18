package com.caiyi.financial.nirvana.investigation.util;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario on 2016/7/19 0019.
 * 征信用的一些辅助方法
 */
public class InvestigationHelper {
    private static Logger logger = LoggerFactory.getLogger(InvestigationHelper.class);
    public static String enUrl= SystemConfig.get("file.enUrl");
    /**
     * 产生请求配置
     *
     * @return
     */
    public static RequestConfig getRequestConfig() {
        RequestConfig.custom().setConnectTimeout(20000);
        RequestConfig.custom().setSocketTimeout(20000);
        RequestConfig.custom().setConnectionRequestTimeout(20000);
        RequestConfig requestConfig = RequestConfig.custom().build();
        return requestConfig;
    }

    /**
     * 默认请求报头
     *
     * @return
     */
    public static Map<String, String> getHeaderMap() {
        Map<String, String> requestHeaderMap = new HashMap<String, String>();
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate");
        requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
        requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        requestHeaderMap.put("Connection", "Keep-Alive");
        requestHeaderMap.put("Host", "ipcrs.pbccrc.org.cn");
        return requestHeaderMap;
    }

    /**
     * 获取真实ip地址
     *
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (ip.indexOf("192.168") > -1 || ip.indexOf("127.0.0") > -1) {
            String xf = request.getHeader("X-Forwarded-For");
            if (xf != null) {
                ip = xf.split(",")[0];
            }
        }
        return ip;
    }


    /***
     * 图片转化成base64位字符串
     * @param image BufferedImage对象
     * @param imgType 图片类型如jpeg,png
     * @return
     */
    public static String GetImageBase64(BufferedImage image,String imgType) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        try {
            ByteArrayOutputStream output=new ByteArrayOutputStream();
            ImageIO.write(image, imgType, output);
            data=output.toByteArray();
            // 对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(data);// 返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String decodeYzm(String base64code,String bankId,String imgtype){
        HttpRequester hq = new HttpRequester();
        String decode="";
        try {
            String strImg=  java.net.URLEncoder.encode(base64code, "utf-8");
            if (!StringUtils.isNotEmpty(enUrl)){
                enUrl="http://192.168.1.232:8080/captcha/hack";//##解析验证码图片接口地址
            }
            String url =enUrl+ "?captcha="+strImg + "&bankid="+bankId+"&imgtype="+imgtype ;
            logger.info("正在调用验证码解析:" + url);
            HttpRespons hr = hq.sendGet(url);
            //获取招商银行的登录sessionID
            String content=hr.getContent();
            System.out.println(content);
            JSONObject json=JSONObject.parseObject(content);
            String code=json.getString("code");
            if ("0".equals(code)){
                decode=json.getString("text");
                logger.info("识别成功 bankId["+bankId+"] imgtype["+imgtype+"] code="+decode);
            }else{
                logger.info("识别失败 bankId["+bankId+"] imgtype["+imgtype+"]"+content);
            }
        } catch (Exception e) {
            logger.error("decodeYzm 异常 bankId[" + bankId + "] imgtype[" + imgtype + "]", e);
        }
        return decode;
    }


}
