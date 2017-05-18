package com.caiyi.financial.nirvana.core.mock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.BankDeployService;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过配置文件的方式读取网银配置
 * Created by ljl on 2016/8/25.
 */
public class BankDeployByFile implements BankDeployService{
    public static Map<String,Boolean> bankInLineMap = new HashMap<String,Boolean>();;
    private final static Logger logger = LoggerFactory.getLogger(BankDeployByFile.class);
    private static Map<String,String> deployResult = new HashMap<String,String>();

    static{
        refreshDeployResult("");
    }
    @Override
    public String readBankConfig(String inbankid){
       return readXmlToJson(inbankid);
    }

    @Override
    public String readBankConfig(){
        return readXmlToJson("");
    }

    public static String refreshDeployResult(String inbankid){
        String path = SystemConfig.get("file.filePath");
        String restStr = "";
        try{
            File file = new File(path);
            Document doc = XmlTool.read(file);
            JSONObject rest = new JSONObject();
            rest.put("code", "1");
            rest.put("desc", "");
            JSONArray bankList = new JSONArray();
            JSONObject dataJson = new JSONObject();
            String supportedEmailCodes;
            List<Element> eles;
            if(doc!=null){
                eles = doc.getRootElement().elements("bank");
                supportedEmailCodes = doc.getRootElement().elementText("supportedEmailCodes");
            }else{
                eles = new ArrayList<>();
                supportedEmailCodes = "";
            }
            dataJson.put("supportedEmailCodes",supportedEmailCodes);
            for (int i = 0; i < eles.size(); i++) {
                Element ele = eles.get(i);
                String bankId = ele.elementText("bankId");
                if(!StringUtils.isEmpty(inbankid)){
                    if(!bankId.equals(inbankid)){
                        continue;
                    }
                }
                String hotline = ele.elementText("hotline");
                String bankName = ele.elementText("bankName");
                Element loginType = ele.element("loginType");
                if(loginType!=null){
                    Element accountType = loginType.element("accountType");
                    String accountTypeName = "";
                    String passwordHint = "";
                    String showAuthCode = "";
                    String attribute1 = "";
                    String attribute2 = "";
                    String pwdType = "";
                    if(accountType!=null){
                        accountTypeName = accountType.elementText("accountTypeName");
                        passwordHint = accountType.elementText("passwordHint");
                        showAuthCode = accountType.elementText("showAuthCode");
                        attribute1 = accountType.elementText("attribute1");
                        attribute2 = accountType.elementText("attribute2");
                        pwdType = accountType.elementText("pwdType");
                    }
                    JSONObject bankJson = new JSONObject();
                    if(!StringUtils.isEmpty(accountTypeName)){
                        JSONArray accountJson = new JSONArray();
                        String[] types = accountTypeName.split("/");
                        for (String typeName : types) {
                            JSONObject typeObj = new JSONObject();
                            typeObj.put("accountTypeName", typeName);
                            typeObj.put("passwordHint", passwordHint);
                            if (StringUtils.isEmpty(attribute1)) {
                                attribute1 = "";
                            }
                            if (StringUtils.isEmpty(attribute2)) {
                                attribute2 = "";
                            }
                            typeObj.put("attribute1", attribute1);
                            typeObj.put("attribute2", attribute2);
                            typeObj.put("showAuthCode", showAuthCode);
                            typeObj.put("pwdType", pwdType);
                            accountJson.add(typeObj);
                        }
                        bankJson.put("accountType", accountJson);
                    }
                    bankJson.put("bankId", bankId);
                    bankJson.put("hotline", hotline);
                    bankJson.put("bankName", bankName);
                    bankList.add(bankJson);
                }
            }
            dataJson.put("bankList", bankList);
            rest.put("data", dataJson);
            restStr = rest.toString();
            deployResult.put(inbankid,restStr);
            return restStr;
        }catch (Exception e){
            logger.info("读取文件异常,文件路径："+path);
            logger.info("BankDeployByFile.refreshDeployResult异常",e);
        }
        return restStr;
    }

    public String readXmlToJson(String inbankid){
        if(deployResult.get(inbankid)==null){
            refreshDeployResult(inbankid);
        }
        return deployResult.get(inbankid);
    }

    //判断某网银是否上线
    public boolean isInLine(String bankId){
        Boolean isInLine = false;
        try{
            if(bankInLineMap.size()==0){
                checkBanksInLine();
            }
            isInLine = bankInLineMap.get(bankId);
            if(isInLine==null){
                isInLine = false;
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("BankDeployByFile.isInLine",e);
        }
        return isInLine;
    }

    //网银是否在线检测
    public void checkBanksInLine(){
        //获取配置信息
        String result = readXmlToJson("");
        JSONObject restObj = JSONObject.parseObject(result);
        logger.info("返回内容："+result);
        if(restObj.containsKey("data")) {
            JSONObject dataJson = restObj.getJSONObject("data");
            if (dataJson.containsKey("bankList")) {
                JSONArray bankList = dataJson.getJSONArray("bankList");
                for(int i=0;i<bankList.size();i++){
                    JSONObject bankJson = bankList.getJSONObject(i);
                    String ibankid = "";
                    if(bankJson.containsKey("bankId")){
                        ibankid = bankJson.getString("bankId");
                        if(bankJson.containsKey("accountType")){
                            bankInLineMap.put(ibankid,true);
                        }else{
                            bankInLineMap.put(ibankid,false);
                        }
                    }else{
                        logger.info("返回的文件内容没有银行ID信息");
                    }
                }
            }else{
                logger.info("返回的文件内容格式错误");
            }
        }else{
            logger.info("未读取到有效的文件内容");
        }
    }

}
