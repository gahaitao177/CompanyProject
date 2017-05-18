package com.caiyi.financial.nirvana.discount.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 */
public class LocalConfig {

    private static String envValue = "dev";//默认开发环境

    public static void main(String[] args) {

        System.out.println(LocalConfig.getString("luncene.indexdir"));
    }

    /*
    设置环境参数 dev 开发环境，pro 生产环境
     */
    public static void setEnv(String env) {
        LocalConfig.envValue = env;
    }

    /*
    处理参数,如果是生产环境，自动切换zookeeper,kafka地址
     */
    private static String ConvertArgs(String val) {
        String res = val;
//        if (envValue == "pro") {
//            if (val.equals("zookeeper.connect") || val.equals("kafka.metadata.broker.list")) {
//                res = envValue + "." + val;
//            }
//        }

        return res;
    }

    /*
    暂时仅支持getString，getInt,根据业务需要继续增加接口
     */
    public static String getString(String val) {
        Config pushConf = ConfigFactory.load("paoding-dic-home.properties");
        return pushConf.getString(ConvertArgs(val));
    }

    public static Integer getInt(String val) {
        Config pushConf = ConfigFactory.load("paoding-dic-home.properties");
        return pushConf.getInt(ConvertArgs(val));
    }
}
