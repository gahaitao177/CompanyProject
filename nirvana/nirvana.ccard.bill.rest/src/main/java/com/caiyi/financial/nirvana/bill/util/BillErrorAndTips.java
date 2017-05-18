package com.caiyi.financial.nirvana.bill.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

/**
 * 账单导入的错误信息及提示
 * Created by lichuanshun on 16/9/2.
 */
public class BillErrorAndTips {
    // 账单与错误提示
    private static Logger logger = LoggerFactory.getLogger(BillErrorAndTips.class);
    public static JSONObject errorAndTips = new JSONObject();
    public static JSONArray nounExplain = new JSONArray();

    static {
        refreshTipConf();
        refreshExplainConf();
    }

    /**
     * @param keyword
     * @param bankId
     * @return
     */
    public static JSONArray getNounExplain(String keyword, String bankId) {
        logger.info("getNounExplain:" + keyword + "," + bankId);
        JSONArray nounExplainArr = new JSONArray();
        int size = nounExplain.size();
//        logger.info("nounExplain:" +nounExplain.size() + nounExplain);
        if (size > 0 && !CheckUtil.isNullString(keyword)) {
            for (int i = 0; i < size; i++) {
                JSONObject desc = nounExplain.getJSONObject(i);
                String[] keys = keyword.split("\\|");
                String descKey = desc.getString("keyword");
                for (String key : keys) {
                    if (!CheckUtil.isNullString(key)) {
                        JSONObject temp = new JSONObject();
                        if (key.equals(descKey)) {
                            temp.put("url", desc.getString("url") + "?bankid=" + bankId);
                            temp.put("keyword", key);
                            nounExplainArr.add(temp);
                        } else {
                            if (key.contains(descKey)) {
                                temp.put("url", desc.getString("url") + "?bankid=" + bankId);
                                temp.put("keyword", key);
                                nounExplainArr.add(temp);
                            }
                        }

                    }
                }
            }
        }
        return nounExplainArr;
    }

    /**
     * @param type
     * @param bankId
     * @return
     */
    public static JSONArray getErrorAndTips(String type, String bankId) {
        logger.info("getErrorAndTips:" + type + "," + bankId);
        // 如果是更新失败(4)并且bankid为空  则为邮箱导入失败页面
        if ("4".equals(type) && CheckUtil.isNullString(bankId)) {
            type = "5";
        }
        JSONArray tipsArr = errorAndTips.getJSONArray(type);
        if (("2".equals(type) || "4".equals(type)) && !CheckUtil.isNullString(bankId) && tipsArr.size() > 0) {
            JSONArray tipsArrNew = new JSONArray();
            logger.info("getErrorAndTips:" + type + "," + bankId + " add bankid to url");
            for (int i = 0; i < tipsArr.size(); i++) {
                JSONObject item = new JSONObject();
                JSONObject tip = tipsArr.getJSONObject(i);
                String title = tip.getString("title");
                item.put("title", title);
                if (!CheckUtil.isNullString(title) && title.contains("开通网银")) {
                    item.put("url", tip.getString("url") + bankId + ".html");
                } else {
                    item.put("url", tip.getString("url") + "?bankid=" + bankId);
                }
                tipsArrNew.add(item);
            }
            tipsArr = tipsArrNew;
        }
        return tipsArr;
    }

    /**
     * 读取文件 刷新内存中配置信息
     */
    public static void refreshTipConf() {
        Scanner scanner = null;
        try {
            String filePath = SystemConfig.get("bill_error_tip_path");
            logger.info("filePath:" + filePath + "刷新信息");
            File file = new File(filePath);
            long lastModified = file.lastModified();
            long now = System.currentTimeMillis();
            logger.info("now:" + now);
            logger.info(filePath + "lastModified:" + lastModified);
            StringBuilder buffer = new StringBuilder();
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
            errorAndTips = JSON.parseObject(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("refreshConfs error", e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * 读取文件 刷新内存中配置信息
     */
    public static void refreshExplainConf() {
        Scanner scanner = null;
        try {
            String filePath = SystemConfig.get("bill_noune_explain_path");
            logger.info("filePath:" + filePath + "刷新信息");
            File file = new File(filePath);
            long lastModified = file.lastModified();
            long now = System.currentTimeMillis();
            logger.info("now:" + now);
            logger.info(filePath + "lastModified:" + lastModified);
            StringBuilder buffer = new StringBuilder();
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
            nounExplain = JSONArray.parseArray(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("refreshConfs error", e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
