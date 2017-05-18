package com.caiyi.nirvana.analyse.monitor.sms;


import org.junit.Test;

/**
 * Created by been on 2017/3/7.
 */
public class SMSServiceTest {

    @Test
    public void test() throws Exception {
        String phone = "18810675066";
        String sysCode = "2001";
        String signature = "【有鱼金融】";
        String url = "http://192.168.1.93:9090/message/sms/send";
        SMSService service = new SMSService(url);
        String result = service.sendSMS(phone, "test1", sysCode, signature);
        System.out.println(result);
    }

}