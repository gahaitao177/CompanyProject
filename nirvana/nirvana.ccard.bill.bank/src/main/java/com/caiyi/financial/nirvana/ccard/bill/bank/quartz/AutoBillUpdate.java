package com.caiyi.financial.nirvana.ccard.bill.bank.quartz;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bank.util.BankImportUtil;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.core.mock.BankDeployByFile;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.rbc.http.client.Cert;
import com.rbc.http.client.CertHelper;
import com.security.client.QuerySecurityInfoById;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by ljl on 2016/9/13.
 * 账单自动更新定时任务
 */
@Component
public class AutoBillUpdate {
    private static Logger logger = LoggerFactory.getLogger(AutoBillUpdate.class);
    @Autowired
    private BankImportService service;
    @Autowired
    private BankImportUtil importUtil;
    private final static String MD5_KEY = SystemConfig.get("security.md5Key");
    private final static String autoBanks = SystemConfig.get("autoBill.banks");
    private final static String time = SystemConfig.get("autoBill.time");
    private final static int pageSize = SystemConfig.getInt("autoBill.pageSize");
    private final static int maxSize = SystemConfig.getInt("autoBill.maxSize");
    private BankDeployByFile deployByFile;
    private static String[] banks;

    public static void initial(){
        logger.info("AutoBillUpdate--------start");
        banks = autoBanks.split(";");
        try {
            String certPath = SystemConfig.get("security.certPath");
            String certName = certPath + "/public.bin";
            File certFile = new File(certPath);
            if(!certFile.exists()){
                certFile.mkdirs();
                InputStream ins = AutoBillUpdate.class.getResourceAsStream("/public.bin");
                OutputStream fos = new FileOutputStream(certName);
                byte[] b = new byte[1024];
                while ((ins.read(b)) != -1) {
                    fos.write(b);
                }
                fos.close();
                ins.close();
            }
            String certId = SystemConfig.get("security.certId");
            String preUrl = SystemConfig.get("security.preUrl");
            String agentCode = SystemConfig.get("security.agentCode");
            String encodeIng = SystemConfig.get("security.encodeIng");
            CertHelper ch = CertHelper.getHttpCert();
            Cert cert = new Cert(certId, agentCode, preUrl, certName, encodeIng);
            ch.putCert(cert);
            logger.info("AutoBillUpdate 加载证书成功" );
        }catch (Exception e){
            e.printStackTrace();
            logger.error("AutoBillUpdate 加载证书失败" + e);
        }
    }
    static{
        initial();
    }

    public void autoRun(){
        deployByFile = new BankDeployByFile();
        logger.info("开始自动更新账单......");
        int totalSuccess = 0;
        try{
            loop:for(String ibankid:banks){
                logger.info("银行id="+ibankid+"开始自动更新账单......");
                if(deployByFile.isInLine(ibankid)){//配置文件存在该银行配置
                    long runsize = service.queryCountNoUpdateTimer(ibankid, time);
                    if(runsize==0){
                        continue;
                    }
                    long page = runsize / pageSize + 1;
                    int success = 0;
                    int fail = 0;
                    for (int i = 0; i < page; i++) {
                        int begin = i * pageSize + 1;
                        int end = (i + 1) * pageSize;
                        List<BankBillDto> billList = service.queryBillNoUpdateTimer(ibankid,time,begin,end);
                        for(BankBillDto bankBill:billList){
                            Integer icreditid = bankBill.getIcreditid();
                            String cuserId = bankBill.getCuserid();
                            Integer billid = bankBill.getIbillid();
                            Integer iskeep = bankBill.getIskeep();
                            String card4Num = bankBill.getIcard4num();
                             String cexpiredate = bankBill.getCexpiredate();
                            if (iskeep == 0) {//已保存密码
                                // 已保存密码
                                QuerySecurityInfoById ssi = new QuerySecurityInfoById();
                                ssi.setUid(cuserId);
                                ssi.setCreditId(String.valueOf(icreditid));
                                ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId()
                                        + MD5_KEY));
                                ssi.setServiceID("2000");
                                String s = ssi.call(30);
                                logger.info("s=" + s);
                                if (StringUtils.isEmpty(s)) {
                                    logger.info("无效的银行卡信息：银行id="+ibankid+";ibillid=="+billid);
                                    fail+=1;
                                    continue;
                                }
                                Document doc = XmlTool.stringToXml(s);
                                Element ele = doc.getRootElement();
                                String errcode = ele.attributeValue("errcode");
                                String idCardNo = "";
                                String bankpwd = "";
                                if ("0".equalsIgnoreCase(errcode)) {
                                    idCardNo = XmlTool.getElementValue("accountName",ele);
                                    bankpwd = XmlTool.getElementValue("accountPwd", ele);
                                    Channel bean = new Channel();
                                    bean.setBankId(ibankid);
                                    bean.setIdCardNo(CaiyiEncrypt.encryptStr(idCardNo));
                                    bean.setBankPwd(CaiyiEncrypt.encryptStr(bankpwd));
                                    bean.setCard4Num(card4Num);
                                    bean.setType("1");
                                    bean.setCuserId(cuserId);
                                    bean.setIskeep(String.valueOf(iskeep));
                                    bean.setIdCard6Num(cexpiredate);
                                    bean.setIclient(0);
                                    bean.setCreditId(String.valueOf(icreditid));
                                    bean.setIsauto(1);
                                    //创建任务,开始更新账单
                                    bean = importUtil.createBankBillTask(bean);
                                    logger.info("code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
                                    if(1==bean.getBusiErrCode()){//导入成功
                                        success+=1;
                                        totalSuccess+=1;
                                    }else{
                                        logger.info("创建账单更新任务失败:银行id="+ibankid+";ibillid=="+billid+";code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
                                        fail+=1;
                                    }
                                } else {
                                    fail+=1;
                                    logger.info("从安全库取帐号信息出错,无效的银行卡信息：银行id"+ibankid+";ibillid=="+billid);
                                }
                            } else {//没有保存
                                fail+=1;
                                logger.info("ibankid="+ibankid+";billid="+billid+";uid=="+cuserId+";未保存账户信息");
                            }
                            if(totalSuccess>=maxSize){
                                logger.info("本次自动更新已达到容许的最大的更新数,暂停更新,等待下次更新.....");
                                break loop;
                            }
                        }
                    }
                    logger.info("银行id="+ibankid+"可以自动更新账单总数=="+runsize+";page=="+page+";成功数=="+success+";失败数=="+fail);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
           logger.error("AutoBillUpdate.autoRun",e);
        }
    }

    public static void main(String[] args)
    {
       /*long start = System.currentTimeMillis();
        System.out.println("Test start:" + start);
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
        AutoBillUpdate obj = context.getBean(AutoBillUpdate.class);
        obj.autoRun();
        long end = System.currentTimeMillis();
        System.out.print("Test end:"+end+";总耗时:"+(end-start)/1000+"秒");
        for(int i=0;i<3;i++){
            Channel bean = new Channel();
            bean.setBankId("16");
            bean.setIdCardNo(CaiyiEncrypt.encryptStr("6222530254585479"));
            bean.setBankPwd(CaiyiEncrypt.encryptStr("870619"));
            bean.setCard4Num("5479");
            bean.setType("0");
            bean.setCuserId("ljl");
            bean.setIskeep(String.valueOf("0"));
            bean.setIdCard6Num("");
            bean.setIclient(0);
            //bean.setCreditId(String.valueOf(icreditid));
            bean.setIsauto(0);
            //创建任务,开始更新账单
            bean = obj.importUtil.createBankBillTask(bean);
            logger.info("code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            if(1==bean.getBusiErrCode()){//导入成功
                logger.info("创建账单更新任务成功:code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            }else{
                logger.info("创建账单更新任务失败:code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            }

            bean.setBankId("1");
            bean.setIdCardNo(CaiyiEncrypt.encryptStr("6225551352177768"));
            bean.setBankPwd(CaiyiEncrypt.encryptStr("881219"));
            bean.setCard4Num("7768");
            bean.setType("0");
            bean.setCuserId("ljl");
            bean.setIskeep(String.valueOf("0"));
            bean.setIdCard6Num("1216");
            bean.setIclient(0);
            //bean.setCreditId(String.valueOf(icreditid));
            bean.setIsauto(0);
            //创建任务,开始更新账单
            bean = obj.importUtil.createBankBillTask(bean);
            logger.info("code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            if(1==bean.getBusiErrCode()){//导入成功
                logger.info("创建账单更新任务成功:code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            }else{
                logger.info("创建账单更新任务失败:code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            }

            bean.setBankId("10");
            bean.setIdCardNo(CaiyiEncrypt.encryptStr("4512893942034100"));
            bean.setBankPwd(CaiyiEncrypt.encryptStr("295712"));
            bean.setCard4Num("4100");
            bean.setType("0");
            bean.setCuserId("ljl");
            bean.setIskeep(String.valueOf("0"));
            bean.setIdCard6Num("295712");
            bean.setIclient(0);
            //bean.setCreditId(String.valueOf(icreditid));
            bean.setIsauto(0);
            //创建任务,开始更新账单
            bean = obj.importUtil.createBankBillTask(bean);
            logger.info("code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            if(1==bean.getBusiErrCode()){//导入成功
                logger.info("创建账单更新任务成功:code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            }else{
                logger.info("创建账单更新任务失败:code="+bean.getBusiErrCode()+";desc="+bean.getBusiErrDesc());
            }
        }*/
        
    }
}
