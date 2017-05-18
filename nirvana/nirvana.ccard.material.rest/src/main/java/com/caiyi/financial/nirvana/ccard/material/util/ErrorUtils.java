package com.caiyi.financial.nirvana.ccard.material.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wsl on 2016/3/16.
 */
@Deprecated
public class ErrorUtils {
    public static Logger logger = LoggerFactory.getLogger("ErrorUtils");
    private static DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    public static final String APPLY_PATH = "/opt/export/data/apply_error/";
    public static final String QUERY_PATH = "/opt/export/data/apply_query_error/";

    /**
     * 保存错误申请
     *
     * @param bankEnum 银行
     * @param param    请求参数
     * @param result   请求结果
     * @param fileName 文件名
     */
    public static void saveApplyFile(ErrorBankEnum bankEnum, Object param, String result, String fileName) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//年
        int month = cal.get(Calendar.MONTH);//月
        int date = cal.get(Calendar.DATE);//日
        String parentPath = APPLY_PATH+"/"+sdf.format(new Date())+"/"+bankEnum;
        saveFile(param, result, fileName, parentPath);
    }
    /**
     * 保存错误申请
     *
     * @param bankEnum 银行
     * @param param    请求参数
     * @param result   请求结果
     */
    public static void saveApplyFile(ErrorBankEnum bankEnum, Object param, String result) {
        saveApplyFile(bankEnum, param, result, System.currentTimeMillis() + ".html");
    }
    /**
     * 保存错误查询
     * @param bankEnum 银行
     * @param param    请求参数
     * @param result   请求结果
     * @param fileName 文件名
     */
    public static void saveQueryFile(ErrorBankEnum bankEnum, Object param, String result, String fileName) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//年
        int month = cal.get(Calendar.MONTH);//月
        int date = cal.get(Calendar.DATE);//日

        String parentPath = QUERY_PATH+"/"+year+"/"+month+"/"+date+"/"+bankEnum+"/";
        saveFile(param, result, fileName, parentPath);
    }

    /**
     * 保存错误查询
     * @param bankEnum 银行
     * @param param    请求参数
     * @param result   请求结果
     */
    public static void saveQueryFile(ErrorBankEnum bankEnum, Object param, String result) {
        saveQueryFile(bankEnum, param, result, System.currentTimeMillis() + ".html");
    }

    private static void saveFile(Object param, String result, String fileName,String parentPath){
        File file = new File(parentPath);
        if(!file.exists()){
            boolean flag = file.mkdirs();
            if(!flag){
//                System.out.println("创建文件夹失败");
            logger.error("创建文件夹失败--------------------------------------");
            }
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(parentPath, fileName)));
            writer.println("<!-- 申请请求值");
            writer.println(JSON.toJSONString(param));
            writer.println(" -->");
            writer.flush();
            writer.println(result);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    public static void main(String[] args) {
//        saveFile("test","ces ","aaa.txt","/data/test");
//        saveApplyFile(ErrorBankEnum.guangda,"测试","测试结果");

    }

}

