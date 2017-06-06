package com.yy.ana.dict;

import com.yy.ana.bean.KeyValue;
import com.yy.ana.domain.BaseDto;
import com.yy.ana.domain.Dto;
import com.yy.ana.service.hbase.IHbaseService;
import com.yy.ana.tools.Bytes;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by User on 2017/5/31.
 */
public class PlatformDict {
    private static IHbaseService hbaseService;

    private static List<Dto> platformList = new ArrayList<Dto>();

    private static String tableName = "pkg_dic";
    private static String familys = "info";
    private static String columns = "info:app_platform,info:app_name";

    static {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        applicationContext.start();
        hbaseService = (IHbaseService) applicationContext.getBean("hbaseService");
    }

    public static void setPlatformList() throws Exception {
        Map<String, List<KeyValue>> results = hbaseService.scanByFilter(tableName, "", "", "", familys, columns);
        if (results != null && results.size() > 0) {
            for (String key : results.keySet()) {
                Dto dto = new BaseDto();
                dto.put("key", key);
                List<KeyValue> keyValues = results.get(key);
                for (KeyValue keyValue : keyValues) {
                    if ("app_name".equals(Bytes.toString(keyValue.getQualifier()))) {
                        dto.put("app_name", Bytes.toString(keyValue.getValue()));
                    }
                    if ("app_platform".equals(Bytes.toString(keyValue.getQualifier()))) {
                        dto.put("app_platform", Bytes.toString(keyValue.getValue()));
                    }
                }
                platformList.add(dto);

            }
        }
    }

    public static String getPlatformName(String key) {
        for (Dto dto : platformList) {
            if (key.equals(dto.getAsString("key"))) {
                return dto.getAsString("app_platform");
            }
        }
        return "";
    }
}
