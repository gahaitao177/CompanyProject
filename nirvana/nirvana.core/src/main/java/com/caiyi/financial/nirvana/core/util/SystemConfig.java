package com.caiyi.financial.nirvana.core.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wsl on 2016/1/14.
 */
public class SystemConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfig.class);

    private static Config config;
    static {
        config = ConfigFactory.load();
        config.checkValid(ConfigFactory.defaultReference(), "simple-lib");

    }

    private static void init() {

    }


    public static String get(String key) {
        try {
            return config.getString(key);
        }catch (ConfigException e){
            LOGGER.warn("读取配置出错,key={}，异常：{}",key,e.getMessage());
            return null;
        }

    }
    public static List<Config> getConfigList(String key){
        return (List<Config>) config.getConfigList(key);
    }
    public static Config getConfig(String key){
        return config.getConfig(key);
    }
    public static int getInt(String key){
        try{
            return config.getInt(key);
        }catch (ConfigException e){
            throw new com.caiyi.financial.nirvana.core.exception.ConfigException("根据key（"+key+"）获得value(Int)失败",e);
        }

    }
    public static Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }
    /*
    application.conf必须参数
    
passwordSecret = loan
##springContext的名称
springContext = spring-context.xml
##drpc远程地址
drpc_ip = 192.168.1.207
##drpc的端口地址
drpc_port = 3772
##drpc服务标志唯一
drpc_service = demo_drpc
is_local = true

##storm部分参数
storm : {
  numAckers = 1
  numWorkers = 1
}
##初始化业务bolt
dispatcherBolt : [
  {
    boltId:"demo",
    className:"com.caiyi.financial.nirvana.discount.ccard.bolts.DemoBolt",
    parallelismHint:1,
    numTasks:1,
    group:"shuffle",
    groupFields:[],
    streamId:""
  }
]

     */
}
