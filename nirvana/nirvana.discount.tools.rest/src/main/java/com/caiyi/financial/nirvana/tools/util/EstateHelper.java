package com.caiyi.financial.nirvana.tools.util;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

/**
 * Created by lichuanshun on 16/11/1.
 */
public class EstateHelper {
    private static Logger logger = LoggerFactory.getLogger(EstateHelper.class);
    // 数据版本 默认为0
    public static int DATA_VERSION = 0;
    public static JSONObject ESTATE_DATA = null;
    static {
        refreshEstateConf();
    }

    /**
     *  读取配置信息
     */
    public static void refreshEstateConf(){
        Scanner scanner = null;
        try {
            String fullFileName = SystemConfig.get("gfzg_cityconf_path");
            File file = new File(fullFileName);
            StringBuilder buffer = new StringBuilder();
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
            JSONObject gfzgConf = JSONObject.parseObject(buffer.toString());
            ESTATE_DATA = gfzgConf.getJSONObject("data");
            DATA_VERSION = gfzgConf.getJSONObject("data").getInteger("dataversion");
            logger.info("refreshEstateConf success" + DATA_VERSION);
        }catch (Exception e){
            logger.error("refreshEstateConf:" +e);
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

}
