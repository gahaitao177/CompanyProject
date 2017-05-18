package com.caiyi.financial.nirvana.ccard.material.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wsl on 2016/4/1.
 */
public class ErrorFileUtil {
    public static Logger logger = LoggerFactory.getLogger("ErrorFileUtil");

    public static boolean saveFile(String url,Object param, String result, String fileName,String parentPath){
        boolean flag = false;
        File file = new File(parentPath);
        if(!file.exists()){
            if(!file.mkdirs()){
//                System.out.println("创建文件夹失败");
                logger.error("创建文件夹失败--------------------------------------");
            }
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(parentPath, fileName)));
            if(StringUtils.isNotEmpty(url)){
                writer.println("<!-- 申请地址");
                writer.println(url);
                writer.println(" -->");
            }
            if(param!=null){
                writer.println("<!-- 申请请求值");
                writer.println(JSON.toJSONString(param));
                writer.println(" -->");
            }
            writer.println(result);

            writer.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            return flag;
        }
    }
}
