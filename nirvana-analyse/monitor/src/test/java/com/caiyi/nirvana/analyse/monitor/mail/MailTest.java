package com.caiyi.nirvana.analyse.monitor.mail;


import org.junit.Test;

/**
 * Created by been on 2017/3/7.
 */
public class MailTest {
    @Test
    public void test() throws Exception {
        MailService m = new MailService();
        String smtpUid = "service@m3.9188.com";
        String smtpPwd = "service9188cp123";
        String smtpServer = "m3.9188.com";
        String senderMail = "service@m3.9188.com";
        String senderNick = "9188彩票网";
        m.setSmtpUid(smtpUid);
        m.setSmtpPwd(smtpPwd);
        m.setSmtpServer(smtpServer);
        m.setSenderMail(senderMail);
        m.setSenderNick(senderNick);
        m.sendMsg("1274716363@qq.com,324493168@qq.com,fengchunmei@9188.com,wangli@youyuwo.com",
                "购彩上9188.com彩票网",
                "购彩上9188.com彩票网123456 <a href=\"http://www.9188.com/useraccount/\">www.9188.com</a>");
    }
}