package com.caiyi.nirvana.analyse.common;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {
    public final static boolean isNullString(String s) {
        if (s != null && s.length() > 0 && s.trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 生成6位随机码
     *
     * @return
     * @author xhs
     */
    public final static String randomNum() {
//		char[] codeSequence = {'0', '1', '2', '3', '4', '5', '6',
//				'7', '8', '9','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
//				'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U',
//				'V', 'W', 'X', 'Y', 'Z'};
        char[] codeSequence = {'0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9'};
        Random random = new Random();
        String strRand = "";
        StringBuffer randomCode = new StringBuffer("");
        for (int i = 0; i < 6; i++) {
            strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length - 1)]);
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }

    /**
     * 邮箱地址验证
     *
     * @return boolean
     * @author xhs
     */
    public final static boolean isEmail(String email) {
        String str = "^([a-z0-9A-Z]+[-_|.]*)+[a-z0-9A-Z]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 手机号码验证
     *
     * @return boolean
     * @author xhs
     */
    public final static boolean isMobilephone(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 用户名格式验证
     *
     * @return boolean
     * @author xhs
     */
    public final static boolean CheckUserName(String s) {
        String str = "[A-Za-z0-9_|\u4e00-\u9fa5]*";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            return false;
        }

        str = "习近平|李克强|法轮功";
        pattern = Pattern.compile(str);
        matcher = pattern.matcher(s);

        if (matcher.find()) {
            return false;
        }
        return true;
    }


    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
//        System.out.println(CheckUserName("我的用户名"));
//        System.out.println(isEmail("3-_.d.-7.7110p@1.com"));
    }
}