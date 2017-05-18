package com.caiyi.financial.nirvana.bill.util;

import java.util.HashMap;

public class BillConstant {

    /***
     * 银行code定义
     */
    //广发银行
    public final static String GUANGFA = "1";
    //中信银行
    public final static String ZHONGXIN = "2";
    //光大银行
    public final static String GUANGDA = "3";
    //农业银行
    public final static String NONGYE = "4";
    //花旗银行
    public final static String HUAQI = "5";
    //平安银行
    public final static String PINGAN = "7";
    // 华夏银行
    public final static String HUAXIA = "8";
    //浦发银行
    public final static String PUFA = "9";
    //兴业银行
    public final static String XINGYE = "10";
    //民生银行
    public final static String MINSHENG = "11";
    //建设银行
    public final static String JIANSHE = "13";
    //工商银行
    public final static String GONGSHANG = "14";
    //中国银行
    public final static String ZHONGGUO = "15";

    //交通银行
    public final static String JIAOTONG = "16";
    //上海银行
    public final static String SHANGHAI = "19";
    //招商银行
    public final static String ZHAOSHANG = "21";


    /**
     * 导入方法类型和返回类型
     */
    public final static String EXTRACODE = "extracode";//获取验证码
    public final static String TASK = "task";//生成导入任务
    public final static String SENDMSG = "sendmsg";//发送短信验证码
    public final static String CHECKMSG = "checkmsg";//验证短信验证码并生成任务
    public final static String RESULT = "result";//任务结果返回
    public final static String SYS = "sys";//返回系统提示
    public final static String MESSAGE = "message";//返回系统提示 message类型的消息订阅

    public final static String SENDMSGLOGIN = "sendmsglogin";//发送短信验证码 提额登录使用
    public final static String CHECKMSGLOGIN = "checkmsglogin";//验证短信验证码并生成任务 提额登录使用


    /**
     * 返回码定义
     */
    public final static int htmlfail = -1;//页面失效返回
    public final static int fail = 0;//常规错误返回
    public final static int success = 1;//交互成功
    public final static int needmsg = 2;//需要短信验证码
    public final static int needimg = 3;//需要图片验证码


    public final static int needmsg_te = 4;//需要短信验证码 提额登录使用


    /**
    * 邮箱类型
    */
    public final static String MAIL_QQ = "0"; //QQ邮箱
    public final static String MAIL_163 = "2"; //163邮箱
    public final static String MAIL_126 = "3"; //126邮箱

    /**
     * 目前支持邮箱
     */
    public static final HashMap<String,String> supportMail = new HashMap<String,String>();
    static {
        supportMail.put(MAIL_QQ,"true");// qq邮箱
        supportMail.put(MAIL_163,"true");// 163邮箱
        supportMail.put(MAIL_126,"true");// 126邮箱
    }
    /**
     * 邮箱操作类型
     */

    // 邮箱导入
    public final static String MAIL_BILL_IMPORT = "4";
    // 邮箱更新
    public final static String MAIL_BILL_UPDATE = "5";
    // 安卓
    public final static String CLIENT_ANDROID = "0";
    // IOS
    public final static String CLIENT_IOS = "1";
    public static final int TIME_MINUTE = 60000;
    public static final int TIME_TEN = 600000;
    public static final int TIME_HALFHOUR = 1800000;
    public static final int TIME_HOUR = 3600000;
    public static final int TIME_DAY = 86400000;
    public static final int TIME_MAX = -1702967296;

    //
    public final static String MD5_KEY = "13da83f8-d230-46f9-a2b4-853b883bea38";


}
