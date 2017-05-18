package com.caiyi.financial.nirvana.core.util;

import com.danga.MemCached.MemCachedClient;

/**
 * Created by heshaohua on 2016/6/1.
 */
public class CacheUtil {
    private static CacheUtil instance;
    private CacheUtil (){}

    private static MemCachedClient memCachedClient;

    public static synchronized CacheUtil getInstance() {
        if (instance == null) {
            instance = new CacheUtil();
        }
        return instance;
    }


    /**public static String getCacheValueByName(String fileName, String keyName){
        memCachedClient = SpringContextUtil.getBean(MemCachedClient.class);
        Object obj = memCachedClient.get(keyName);
        String apiHost = "";
        if (null!=obj){
            apiHost = (String)obj;
            System.out.println("从缓存中读取api地址=" + apiHost);
        }else{
            Properties pps = new Properties();
            try{
                apiHost = FileUtil.getValuesByKey(fileName, keyName);
            }catch(Exception e){
                e.printStackTrace();;
                System.out.println("没有读取到配置的9188接口地址");
            }
            memCachedClient.set(keyName,apiHost);
        }
        return apiHost;
    }**/
}
