package com.caiyi.financial.nirvana.bill.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by lichuanshun on 16/8/29.
 */
public class TempBankInfos {
    private static JSONArray banksArr = new JSONArray();
    private static HashMap<String, JSONObject> banksMap = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(TempBankInfos.class);
    // 卡神攻略URL
    public static final String KASHENURL = "http://www.huishuaka.com/h5/1480.html";
    static {
        refreshTempBankConf();
    }

    /**
     * 排除已导入账单银行 全部银行信息
     *
     * @param importedBanks
     * @return
     */
    public static JSONArray getBankArr(JSONArray importedBanks) {
        JSONArray array = new JSONArray();
        // 排除已经导入账单的银行
        Set<String> banks =banksMap.keySet();
        for (int allIndex = 0; allIndex < banksArr.size(); allIndex++) {
            JSONObject bank = banksArr.getJSONObject(allIndex);
            boolean isImported = false;
            String bankId = bank.getString("id");
            String bankName = bank.getString("name");
            int size = importedBanks.size();
            if (size > 0){
                for (int index = 0; index < size; index++) {
                    JSONObject bill = importedBanks.getJSONObject(index);
                    if (bankId.equals(bill.getString("bankId"))) {
                        isImported = true;
                    }
                }
            }
            //
            if (!isImported) {
                JSONObject tempBank = new JSONObject();
                tempBank.put("bankId", bankId);
                tempBank.put("bankName", bankName);
                array.add(tempBank);
            }
        }
        return array;
    }

    /**
     * @param bankId
     * @return
     */
    public static JSONObject getBankInfoById(String bankId) {
        JSONObject bankInfos = new JSONObject();
        JSONArray applyArr = new JSONArray();
        JSONObject bank = banksMap.get(bankId);
        bankInfos.put("validityDate", bank.getString("validity"));
        // 短信申请
        JSONObject tempMsg = new JSONObject();
        String msg = bank.getString("msg");
        if (!CheckUtil.isNullString(msg)) {
            tempMsg.put("type", "0");
            tempMsg.put("iBankUrl", "");
            tempMsg.put("phoneNum", bank.getString("msgnum"));
            tempMsg.put("message", bank.getString("msg"));
            applyArr.add(tempMsg);
        }
        // 电话申请
        String tel = bank.getString("tel");
        if (!CheckUtil.isNullString(tel)) {
            JSONObject tempTel = new JSONObject();
            tempTel.put("type", "1");
            tempTel.put("iBankUrl", "");
            tempTel.put("phoneNum", bank.getString("telnum"));
            tempTel.put("message", bank.getString("tel"));
            applyArr.add(tempTel);
        }
        // 微信申请
        String wechat = bank.getString("wechat");
        if (!CheckUtil.isNullString(wechat)) {
            JSONObject wechatTemp = new JSONObject();
            wechatTemp.put("type", "2");
            wechatTemp.put("iBankUrl", "");
            wechatTemp.put("phoneNum", "");
            wechatTemp.put("message", bank.getString("wechat"));
            applyArr.add(wechatTemp);
        }
        // 网银申请
        String ebank = bank.getString("ebank");
        if (!CheckUtil.isNullString(ebank)) {
            JSONObject ebankTemp = new JSONObject();
            ebankTemp.put("type", "3");
            ebankTemp.put("iBankUrl", bank.getString("ebanklink"));
            ebankTemp.put("phoneNum", "");
            ebankTemp.put("message", bank.getString("ebank"));
            applyArr.add(ebankTemp);
        }
        bankInfos.put("applyTypes",applyArr);
        return bankInfos;
    }

    /**
     *  读取配置信息
     */
    public static void refreshTempBankConf(){
        Scanner scanner = null;
        try {
            String  fullFileName = SystemConfig.get("tempquotaconf");
            File file = new File(fullFileName);
            long lastModified = file.lastModified();
            long now = System.currentTimeMillis();
            logger.info( "now:" + now);
            logger.info(fullFileName + "lastModified:" + lastModified);
            StringBuilder buffer = new StringBuilder();
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
//            logger.info("buffer:" +buffer.toString());
            banksArr = JSONArray.parseArray(buffer.toString());
            setBanksMap();
            logger.info("readBankConf success");
        }catch (Exception e){
            logger.error("readBankConf:" ,e);
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     *
     */
    private static void setBanksMap(){
        int count = banksArr.size();
        if (count > 0){
            banksMap.clear();
            for (int index = 0; index < count; index++){
                JSONObject bank = banksArr.getJSONObject(index);
                banksMap.put(bank.getString("id"),bank);
            }
        }
    }
}
