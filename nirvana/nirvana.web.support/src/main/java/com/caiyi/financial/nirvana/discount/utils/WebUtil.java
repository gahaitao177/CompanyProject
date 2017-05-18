package com.caiyi.financial.nirvana.discount.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by heshaohua on 2016/4/28.
 */
public class WebUtil {
    /**
     * 功能函数。将变量值不为空的参数组成字符串
     * @param returnStr
     * @param paramId
     * @param paramValue
     * @return
     */
    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        } else {
            if (!paramValue.equals("")) {
                returnStr = paramId + "=" + paramValue;
            }
        }
        return returnStr;
    }

    /**
     * 获取IP
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (ip.indexOf("192.168") > -1 || ip.indexOf("127.0.0") > -1) {
            String xf = request.getHeader("X-Forwarded-For");
            if(xf != null){
                ip = xf.split(",")[0];
            }
        }
        return ip;
    }

    /**
     * 老版souce值转换为新版source值
     * @param source
     * @return
     * @author lwg 2015-11-24
     */
    public static int transformSource(int source){
        if (source>=1000&&source<2000){
            source+=4000;
        }else if (source==1||source==0){
            source=5000;
        }else if(source==2001){
            source=6000;
        }
        return source;
    }

}
