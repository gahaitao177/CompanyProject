package com.caiyi.nirvana.analyse.util;

import com.caiyi.nirvana.analyse.env.Profile;

public class Constants {
    public static final String STORM_SERVCE = Profile.instance.isProd() ? "192.168.83.40" : "192.168.1.207"; //测试环境
    public static final String SYNC_KEY = "accountbook"; //数据同步key
    public static final String TOPOLOGY_NAME = "acctbookDRPC"; //拓扑名称
    public static final String USER_KEY = "iwannapie?!"; //用户体系验证KEY
    public static final String PRIVATEKEY = "http://www.9188.com/";

    private static String production = "";

    public static String DOMAIN = Profile.instance.isProd() ? "http://mobile.9188.com" : "http://t2015.9188.com"; //测试域名
    public static String FILEPREFIX = Profile.instance.isProd() ? "/opt/export/data/jz" : "/opt/export/data"; //文件访问地址


    public static final int TIME_MINUTE = 60000;
    public static final int TIME_TEN = 600000;
    public static final int TIME_HALFHOUR = 1800000;
    public static final int TIME_HOUR = 3600000;
    public static final int TIME_DAY = 86400000;
    public static final int TIME_MAX = -1702967296;
}
