package com.caiyi.financial.nirvana.core.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by heshaohua on 2016/5/31.
 */
public class FileUtil {

    /**
     * 获取properties文件value值
     * @param filePath
     * @param key
     * @return
     */
    public static String getValuesByKey(String filePath, String key){
        Properties pps = new Properties();
        InputStream in = null;
        try{
            in = new BufferedInputStream(new FileInputStream(ClassLoader.getSystemResource(filePath).getPath()));
            pps.load(in);
            String value = pps.getProperty(key);

            return value;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }finally {
            if(in != null){
                try{
                    in.close();
                    in = null;
                }catch(IOException ef){
                    ef.printStackTrace();
                }
            }
        }
    }
}
