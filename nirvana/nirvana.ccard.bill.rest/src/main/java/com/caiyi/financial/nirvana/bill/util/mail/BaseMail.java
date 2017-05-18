package com.caiyi.financial.nirvana.bill.util.mail;

import com.caiyi.financial.nirvana.discount.utils.SpringContextUtilBro;
import com.danga.MemCached.MemCachedClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lichuanshun on 16/7/13.
 */
public class BaseMail {

    public static MemCachedClient cc =  SpringContextUtilBro.getBean(MemCachedClient.class);
    public static Map<String,String> getBasicHeader() {
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Accept-Encoding","gzip, deflate");
        headers.put("Accept-Language","zh-CN");
        headers.put("Cache-Control","no-cache");
        headers.put("Connection","Keep-Alive");
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Host", "reg.163.com");
        headers.put("User-Agent", "Opera/9.80 (Windows NT 6.1; WOW64; U; zh-cn) Presto/2.10.289 Version/12.01");
        return headers;
    }
}
