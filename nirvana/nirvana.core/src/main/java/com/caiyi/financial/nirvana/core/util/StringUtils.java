package com.caiyi.financial.nirvana.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuweiguo on 2016/8/11.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{
    /**
     * 检查多个字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String... str){
        boolean ret = false;
        for (int i=0;i<str.length;i++) {
            if (isEmpty(str[i])) {
                ret = true;
                break;
            } else {
                if ("undefined".equals(str[i])) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 检查是否全部为非空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String... str) {
        return !isEmpty(str);
    }

    /**
     * @param str
     * @return
     */
    public static boolean isDouble(String str){
        if (null == str) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str){
        if (null == str) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotInteger(String str) {
        return !isInteger(str);
    }

    public static boolean isNotDouble(String str) {
        return !isDouble(str);
    }

    /**
     * 判断字符串是否为email
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null==email || "".equals(email)) return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
