package com.caiyi.nirvana.analyse.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;


/**
 * java根据url获取json对象
 *
 * 这里调用百度的ip定位api服务 详见 http://api.map.baidu.com/lbsapi/cloud/ip-location-api.htm
 * @author openks
 * @since 2013-7-16
 */
public class IpUtil {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = JSONObject.parseObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


    /*
       * 随机生成国内IP地址
      */
    public static String getRandomIp() {

        //ip范围
        int[][] range = {{607649792, 608174079},//36.56.0.0-36.63.255.255
                {1038614528, 1039007743},//61.232.0.0-61.237.255.255
                {1783627776, 1784676351},//106.80.0.0-106.95.255.255
                {2035023872, 2035154943},//121.76.0.0-121.77.255.255
                {2078801920, 2079064063},//123.232.0.0-123.235.255.255
                {-1950089216, -1948778497},//139.196.0.0-139.215.255.255
                {-1425539072, -1425014785},//171.8.0.0-171.15.255.255
                {-1236271104, -1235419137},//182.80.0.0-182.92.255.255
                {-770113536, -768606209},//210.25.0.0-210.47.255.255
                {-569376768, -564133889}, //222.16.0.0-222.95.255.255
        };

        Random rdint = new Random();
        int index = rdint.nextInt(10);
        String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
        return ip;
    }

    /*
     * 将十进制转换成ip地址
    */
    public static String num2ip(int ip) {
        int[] b = new int[4];
        String x = "";

        b[0] = ((ip >> 24) & 0xff);
        b[1] = ((ip >> 16) & 0xff);
        b[2] = ((ip >> 8) & 0xff);
        b[3] = (ip & 0xff);
        x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer
                .toString(b[3]);

        return x;
    }

    //每个key每天支持100万次调用，超过限制不返回数据。
    public static String getIpAddress(String ip) {
        JSONObject json = getCityJson(ip);
        return json.getJSONObject("content").getString("address");
    }

    public static JSONObject getCityJson(String ip) {
        JSONObject json = null;
        try {
            if ((int) (Math.random() * 2) == 1) { //创建2个key 防止100万次调用不够
                json = readJsonFromUrl("http://api.map.baidu.com/location/ip?ak=1nm701NM44jAUNY0fzrjOUNH&ip=" + ip);
            } else {
                json = readJsonFromUrl("http://api.map.baidu.com/location/ip?ak=21oRGluvu3y6NsPslWk1WWUE&ip=" + ip);
            }
            /**
             * json 格式
             * {"address":"CN|河南|驻马店|None|CHINANET|0|0","content":{"address":"河南省驻马店市",
             * "address_detail":{"city":"驻马店市","city_code":269,"district":"","province":"河南省","street":"",
             * "street_number":""},"point":{"x":"12696031.84","y":"3869592.87"}},"status":0}
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 10; i++) {
            System.out.println("Math.random ===" + (int) (Math.random() * 2));
            //这里调用百度的ip定位api服务 详见 http://api.map.baidu.com/lbsapi/cloud/ip-location-api.htm
            // JSONObject json = readJsonFromUrl("http://api.map.baidu
            // .com/location/ip?ak=F454f8a5efe5e577997931cc01de3974&ip="+getRandomIp());
            System.out.println(getIpAddress(getRandomIp()));
        }
    }

}