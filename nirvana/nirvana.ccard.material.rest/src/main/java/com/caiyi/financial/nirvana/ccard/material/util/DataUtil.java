package com.caiyi.financial.nirvana.ccard.material.util;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lizhijie on 2016/7/21.
 */
public class DataUtil {
    public static  int dencrypt_data(MaterialBean bean, HttpServletRequest request) throws Exception {
        int client = bean.getIclient();
        bean.setMediatype("json");
        if (client == 9) {
            // todo 测试添加的
            return 1;
        }
        if (client == 0) {
            bean.setData(CaiyiEncrypt.dencryptStr(bean.getData()));
        } else if (client == 1) {
            bean.setData(CaiyiEncryptIOS.dencryptStr(bean.getData()));
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiJSON("非法请求");
            return 0;
        }
        return 1;
    }
    public static int dencrypt(MaterialBean bean, HttpServletRequest request, HttpServletResponse response){
        int client = bean.getIclient();
        bean.setMediatype("json");
        if (client == 9) {
            // todo 测试添加的
            return 1;
        }
        if (client == 0) {
            bean.setCphone(CaiyiEncrypt.dencryptStr(bean.getCphone()));
        } else if (client == 1) {
            bean.setCphone(CaiyiEncryptIOS.dencryptStr(bean.getCphone()));
        } else {
            bean.setBusiJSON("解密异常");
            return 0;
        }
        if(bean.getCphone().equals("")){
            bean.setMediatype("json");
            bean.setBusiJSON("解密异常");
            return 0;
        }
        return 1;
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
    private final static Pattern pattern = Pattern.compile("[0-9]*");

    public static final Boolean isInteger(String str) {
        if(str==null||"".equals(str)){return false;}
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
