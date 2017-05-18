package com.caiyi.financial.nirvana.ccard.bill.bank.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vi on 2016/11/21.
 */
public class EmailUtil {
    private static Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    private static String smtpUid;
    private static String smtpPwd;
    private static String smtpServer;
    private static String senderMail;
    private static String senderNick;
    private static String inceptAddress="yaoruikang@9188.com";
    //private static String inceptAddress = "hongshaobo@youyuwo.com";

    static {
        smtpUid = LocalConfig.getString("mail.smtpUid");
        smtpPwd = LocalConfig.getString("mail.smtpPwd");
        smtpServer = LocalConfig.getString("mail.smtpServer");
        senderMail = LocalConfig.getString("mail.senderMail");
        senderNick = LocalConfig.getString("mail.senderNick");

        logger.info("---------------------------");
        logger.info(smtpUid);
        logger.info(smtpPwd);
        logger.info(smtpServer);
        logger.info(senderMail);
        logger.info(senderNick);
        logger.info("---------------------------");


    }

    public static boolean sendEmail(String title,String content){
        boolean issuccess=false;
        for(int i = 0;i<3;i++){
            Mail mail = new Mail();
            mail.setSmtpUid(smtpUid);
            mail.setSmtpPwd(smtpPwd);
            mail.setSmtpServer(smtpServer);
            mail.setSenderMail(senderMail);
            mail.setSenderNick(senderNick);
            //发送邮件
            logger.info("-----发送邮件开始------");
            boolean flag = mail.sendMsg(inceptAddress,title,content);
            logger.info(inceptAddress);
            logger.error(title);
            logger.error(content);
            logger.info("-----发送邮件结束------");
            if(!flag){
                logger.error("发送邮件失败");
            }else{
                issuccess=true;
                break;
            }
            try {
                Thread.sleep(1000*i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return issuccess;
    }

    public static void main(String[] args) throws Exception {
        boolean result= EmailUtil.sendEmail("ttt","ttttt1");
        System.out.println(result);
    }
}