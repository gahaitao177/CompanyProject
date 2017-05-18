package com.caiyi.financial.nirvana.ccard.bill.mail.bolt;


import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.MailBill;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.MailBillDto;
import com.caiyi.financial.nirvana.ccard.bill.mail.service.MailImportService;
import com.caiyi.financial.nirvana.ccard.bill.mail.util.KafkaService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/5/9.
 * 邮箱导入相关
 */
@Bolt(boltId = "mail", parallelismHint = 1, numTasks = 1)
public class MailImportBolt extends BaseBolt {
     @Autowired
    private MailImportService mailImportService;
    public static HashMap<String,String> supportMail = new HashMap<String,String>();
    static {
        supportMail.put("0","true");// qq邮箱
        supportMail.put("2","true");// 163邮箱
        supportMail.put("3","true");// 126邮箱
    }

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        logger.info("---------------------mailImportService _prepare");
//        mailImportService = getBean(MailImportService.class);

    }
    @BoltController
    public List<Map<String,Object>> mailLogin(MailBill mailBillBean){
        logger.info("---------------------进入MailImportBolt mailLogin");
        logger.info("---------------------进入MailImportBolt " + mailBillBean.getCuserId());

        return mailImportService.select(mailBillBean);
    }

    /**
     *  根据用户信息设置CreditId(等信息
     * @param bean
     * @return
     */
    @BoltController
    public Channel setCreditId(Channel bean){
        logger.info("-------MailImportBolt setCreditId type=" + bean.getType() + ",mailType=" + bean.getMailType());
        // 是账单更新 并且是支持的邮箱
        logger.info("--------billid:" + bean.getBillId() + ",cuserId=" + bean.getCuserId());
        MailBillDto billDto = mailImportService.getUserBankBill(bean.getBillId(), bean.getCuserId());
        logger.info("billDto,",billDto);
            if (billDto == null || CheckUtil.isNullString(billDto.getIoutsideid())){
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("无效的邮箱账号");
                return bean;
            }
            mailImportService.updateOnlyDate(bean.getBillId(), bean.getCuserId());
            bean.setCreditId(String.valueOf(billDto.getIcreditid()));
            bean.setOutsideId(billDto.getIoutsideid());
            bean.setPassword(billDto.getCendperioddate());
            bean.setIskeep(String.valueOf((billDto.getIskeep())));
            bean.setBankId(String.valueOf(billDto.getIbankid()));
            bean.setDisplayName(billDto.getCname());
        logger.info("---------------------end BankImportBolt setCreditId");
        return bean;
    }

    /**
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel createEmailBillTask(Channel bean){
        try {
            String params = "";
            String accountname = "";
            if (CheckUtil.isNullString(bean.getType())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("非法操作 缺少必要参数");
                return bean;
            }

            logger.info("bean.getBankId():" + bean.getBankId());
            if ("0".equals(bean.getMailType())) {// QQ邮箱
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress())|| CheckUtil.isNullString(bean.getMailPwd())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作 缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getConfirmpassword() + ";"	+ bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient()+";"
                            +bean.getImportType() + ";" + bean.getIpAddr() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作 缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getConfirmpassword() + ";"+ bean.getBankSessionId()  + ";" + bean.getIskeep() + ";"	+ bean.getClient()+";"
                            +bean.getImportType() + ";" + bean.getIpAddr() + ";" + bean.getBankId() ;
                    accountname = bean.getBillId();
                }
            }else if("2".equals(bean.getMailType())){//163邮箱 126
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress())|| CheckUtil.isNullString(bean.getMailPwd())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作 缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";"	+ bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";"	+ bean.getBankSessionId()  + ";" + bean.getIskeep() + ";"	+ bean.getClient()+";" +  bean.getBankId();
                    accountname = bean.getBillId();
                }
            } else if ("3".equals(bean.getMailType())){  //126
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress())|| CheckUtil.isNullString(bean.getMailPwd())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作 缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";"	+ bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                    logger.info("bankSessionId>>>"+bean.getBankSessionId()+";params>>>"+params);
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作 缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";"	+ bean.getBankSessionId()  + ";" + bean.getIskeep() + ";"	+ bean.getClient()+";" +  bean.getBankId();
                    accountname = bean.getBillId();
                }
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("该邮箱暂不支持");
                return bean;
            }
            ImportTaskDto taskInfo = mailImportService.queryTaskByUser(bean.getType(), bean.getCuserId(),Integer.valueOf(bean.getMailType()),accountname);
            if (taskInfo != null){
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("操作成功");
                bean.setTaskId(String.valueOf(taskInfo.getItaskid()));
                logger.info("mailImportService queryTaskByUser success");
                return bean;
            } else {
                int newTaskId = mailImportService.querySeqIdFormTask();
                ImportTaskDto newTask = new ImportTaskDto();
                newTask.setItaskid(newTaskId);
                newTask.setCuserid(bean.getCuserId());
                newTask.setIbankid(Integer.valueOf(bean.getMailType()));
                newTask.setItype(Integer.valueOf(bean.getType()));
                newTask.setIstate(0);
                newTask.setIsend(2);
                newTask.setCdesc("邮箱模拟登录中");
                newTask.setCurlparams(params);
                newTask.setCaccountname(accountname);
                int rt = mailImportService.createTask(newTask);
                if (rt == 0){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("插入任务失败");
                    logger.info("mailImportService insert success");
                    return bean;
                }
                logger.info("用户id：" + bean.getCuserId() + ",用户ip:" + bean.getIpAddr() + ",taskid：" + newTaskId);
                logger.info("rt===" + rt + "  taskid=" + newTaskId	+ " getCuserId=" + bean.getCuserId() +
                        " mailtype=" + bean.getMailType() + " type="+ bean.getType() + " params=" + params + " accountname=" + accountname);
                JSONObject content = new JSONObject();
                content.put("taskid",newTaskId);
                content.put("bankid",newTask.getIbankid());
                content.put("busiErrCode","0");
                content.put("busiErrDesc", "");
                content.put("caccountname",accountname);
                content.put("cuserid", bean.getCuserId());
                content.put("cparams",params);
                content.put("type",bean.getType());
                content.put("mailType", bean.getMailType());
                content.put("sid",bean.getSourceCode());
                //
                JSONObject mailMsg = new JSONObject();
                mailMsg.put("content",content);
                logger.info("KafkaService pushToTopic mailMsg"+mailMsg.toJSONString());
                boolean sflag = KafkaService.pushToTopic(mailMsg.toJSONString());
                if (!sflag){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("插入任务失败");
                    logger.info("mailImportService pushToTopic fail");
                    return bean;
                }
                bean.setBusiErrCode(1);
                bean.setTaskId(String.valueOf(newTaskId));
                bean.setBusiErrDesc("操作成功");
                logger.info("mailImportService insert success");
            }
        }catch (Exception e){
            logger.error("MailImportBolt createEmailBillTask",e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("生成任务失败");
        }
        return bean;
    }
}
