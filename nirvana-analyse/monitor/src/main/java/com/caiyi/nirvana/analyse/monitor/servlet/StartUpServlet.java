package com.caiyi.nirvana.analyse.monitor.servlet;

import com.caiyi.nirvana.analyse.env.Profile;
import com.caiyi.nirvana.analyse.monitor.MonitorService;
import com.caiyi.nirvana.analyse.monitor.mail.MailService;
import com.caiyi.nirvana.analyse.monitor.sms.SMSService;
import com.rbc.frame.core.ActionServlet;

import javax.servlet.ServletException;
import java.util.Properties;

/**
 * Created by been on 2017/3/7.
 */
public class StartUpServlet extends ActionServlet {
    private MonitorService monitorService;
    private SMSService smsService;
    private MailService mailService;
    private String topic;
    private String brokers;
    private String groupId;
    private Properties properties;

    @Override
    public void init() throws ServletException {
        log("init servlet ............ ");
        super.init();
        properties = new Properties();
        boolean prod = Profile.instance.isProd();
        try {
            if (prod) {
                properties.load(getClass().getResourceAsStream("/config_prod.properties"));
            } else {
                properties.load(getClass().getResourceAsStream("/config_dev.properties"));
            }
            smsService = createSMSService(properties);
            mailService = createMailService(properties);

            topic = properties.getProperty("topic");
            brokers = properties.getProperty("brokers");
            groupId = properties.getProperty("groupId");

            monitorService = new MonitorService(smsService, mailService, topic, groupId, brokers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                monitorService.startUp(properties);
            } catch (Exception e) {
                log("启动kafka consumer 失败.......");
                log(e.getMessage(), e);
            }
        }).start();
    }

    private MailService createMailService(Properties properties) {
        String smtpUid = properties.getProperty("smtpUid");
        String smtpPwd = properties.getProperty("smtpPwd");
        String smtpServer = properties.getProperty("smtpServer");
        String senderMail = properties.getProperty("senderMail");
        String senderNick = properties.getProperty("senderNick");
        return new MailService(smtpUid, smtpPwd, smtpServer, senderMail, senderNick);
    }

    private SMSService createSMSService(Properties properties) {
        String invokeUrl = properties.getProperty("sms.invokeUrl");
        String sysCode = properties.getProperty("sms.sysCode");
        String signature = properties.getProperty("sms.signature");
        return new SMSService(invokeUrl, sysCode, signature);
    }

}
