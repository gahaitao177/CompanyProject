package com.caiyi.nirvana.analyse.monitor.sms;

import com.caiyi.nirvana.analyse.monitor.BaseService;
import com.caiyi.nirvana.analyse.monitor.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by been on 2017/3/7.
 */
public class SMSService extends BaseService {

    public String invokeUrl;
    public String sysCode;
    public String signature;


    public SMSService(String invokeUrl) {
        this.invokeUrl = invokeUrl;
    }

    public SMSService(String invokeUrl, String sysCode, String signature) {
        this.invokeUrl = invokeUrl;
        this.sysCode = sysCode;
        this.signature = signature;
    }

    public String sendSMS(String phone, String content) throws Exception {
        return sendSMS(phone, content, sysCode, signature);
    }

    public String sendSMS(String phone, String content,
                          String sysCode, String signature) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("destination", phone);
        map.put("content", content);
        map.put("signature", signature);
        map.put("sysCode", sysCode);
        String result = HttpUtils.doPost(invokeUrl, map);
        logger.info("短信发送结果: " + result);
        return result;
    }
}
