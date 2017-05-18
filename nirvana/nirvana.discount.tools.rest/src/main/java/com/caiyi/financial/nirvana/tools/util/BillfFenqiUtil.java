package com.caiyi.financial.nirvana.tools.util;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

/**
 * Created by lichuanshun on 2017/4/13.
 */
public class BillfFenqiUtil {
    private static Logger logger = LoggerFactory.getLogger(BillfFenqiUtil.class);
    public static int DATA_VERSION = 0;
    public static JSONObject BILLFENQI_DATA = null;
    static {
        refreshBillFenqiConf();
    }


    /**
     *  读取配置信息
     */
    public static void refreshBillFenqiConf(){
        Scanner scanner = null;
        try {
            String fullFileName = SystemConfig.get("zdfq_bankconf_path");
            File file = new File(fullFileName);
            StringBuilder buffer = new StringBuilder();
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
            JSONObject gfzgConf = JSONObject.parseObject(buffer.toString());
            BILLFENQI_DATA = gfzgConf.getJSONObject("data");
            DATA_VERSION = gfzgConf.getJSONObject("data").getInteger("dataversion");
            logger.info("refreshBillFenqiConf success" + DATA_VERSION);
        }catch (Exception e){
            logger.error("refreshBillFenqiConf error:" +e.getMessage(),e);
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
