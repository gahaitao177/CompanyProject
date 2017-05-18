package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;


import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bank.util.KafkaService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.ConsumeTaskDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.danga.MemCached.MemCachedClient;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by terry on 2016/5/25.
 */
@Bolt(boltId = "bank", parallelismHint = 1, numTasks = 1)
public class BankImportBolt extends BaseBolt {
    public BankImportService bankImportService;
    @Autowired
    public MemCachedClient cc;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        logger.info("---------------------BankImportBolt _prepare");
        bankImportService = getBean(BankImportService.class);
        cc = getBean(MemCachedClient.class);
    }

    @BoltController
    public List<Map<String,Object>> bankLogin(Channel channelBean){
        logger.info("---------------------进入BankImportBolt bankLogin");
        logger.info("---------------------进入BankImportBolt " + channelBean.getCuserId());

        return bankImportService.select(channelBean);
    }

    @BoltController
    public Channel setCreditId(Channel bean){
        logger.info("---------------------进入BankImportBolt setCreditId");

        if ("1".equals(bean.getType()) || "2".equals(bean.getType())
                || "3".equals(bean.getType())) {
            if ("13".equals(bean.getBankId())||"2".equals(bean.getBankId())
                    ||"3".equals(bean.getBankId())||"9".equals(bean.getBankId())
                    ||"4".equals(bean.getBankId())||"19".equals(bean.getBankId())
                    ||"8".equals(bean.getBankId())||"7".equals(bean.getBankId())
                    ||"14".equals(bean.getBankId())||"21".equals(bean.getBankId())
                    ||"15".equals(bean.getBankId()) || "11".equals(bean.getBankId())
                    ||"16".equals(bean.getBankId()) || "10".equals(bean.getBankId())) {
                if (CheckUtil.isNullString(bean.getCard4Num())) {
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("无效银行卡");
                    return bean;
                }
                BankBillDto bankBill = bankImportService.getUserBankBill(bean.getCard4Num(), Integer.valueOf(bean.getBankId()), bean.getCuserId());
                if (bankBill == null) {
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("无效银行卡");
                    return bean;
                }
                bean.setCreditId(String.valueOf(bankBill.getIcreditid()));
                bean.setOutsideId(bankBill.getIoutsideid());//外部账户ID
                bean.setBillId(String.valueOf(bankBill.getIbillid()));
                bean.setIskeep(String.valueOf(bankBill.getIskeep()));
                return bean;
            }
        }
        logger.info("---------------------end BankImportBolt setCreditId");
        return bean;
    }

    @BoltController
    public Channel createBankBillTask(Channel bean) {
        String sql = "";
        try {
            String params = "";
            String accountname = "";
            if (CheckUtil.isNullString(bean.getType())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("非法操作,缺少必要参数");
                return bean;
            }
            if ("21".equals(bean.getBankId())) {// 招商
                if ("0".equals(bean.getType())) {// 导入
                    //CheckUtil.isNullString(bean.getBankRand())||
                    if (CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getBankSessionId() + ";"	+ bean.getBankRand() + ";" + bean.getIskeep() + ";"	+ bean.getClient()+ ";"	+ bean.getTcId();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getBankSessionId() + ";"	+ bean.getBankRand() + ";" + bean.getIskeep() + ";"	+ bean.getClient()+ ";"	+ bean.getTcId();
                    accountname = bean.getCard4Num();
                }
            } else if ("10".equals(bean.getBankId())) {// 兴业
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCard6Num())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getIdCard6Num()+ ";" + bean.getBankPwd() + ";" + bean.getIskeep()+ ";" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getIdCard6Num()+ ";" + bean.getBankPwd() + ";" + bean.getIskeep() + ";" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("7".equals(bean.getBankId())) {// 平安
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getBankRand())|| CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())
                            || CheckUtil.isNullString(bean.getIskeep())|| CheckUtil.isNullString(bean.getReportHtml1())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getBankSessionId() + ";"	+
                            bean.getBankRand() + ";" + bean.getIskeep() + ";" + bean.getClient()+";"+bean.getReportHtml1();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())|| CheckUtil.isNullString(bean.getReportHtml1())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankSessionId() + ";" + bean.getBankRand()	+ ";" + bean.getBankPwd() + ";" +
                            bean.getIskeep()+ ";" + bean.getClient() + ";" + bean.getIdCardNo()+";"+bean.getReportHtml1();
                    accountname = bean.getCard4Num();
                }
            } else if ("1".equals(bean.getBankId())) {// 广发
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getIskeep())|| CheckUtil.isNullString(bean.getIdCard6Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getIskeep() + ";" + bean.getClient() + ";"+ bean.getIdCard6Num();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankPwd() + ";" + bean.getIskeep() + ";"+ bean.getClient() + ";" + bean.getIdCardNo() + ";"+ bean.getIdCard6Num();
                    accountname = bean.getCard4Num();
                }
            } else if ("3".equals(bean.getBankId())) {// 光大银行
                if ("0".equals(bean.getType())) {// 导入 手机号登录
                    if (StringUtils.isEmpty(bean.getBankPwd())|| StringUtils.isEmpty(bean.getIdCardNo())
                            || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"	+ bean.getBankSessionId()+ "@" + bean.getIskeep() + "@"
                            + bean.getClient();
                    logger.info("params>>>>>>>>>>>>>>>>>>>>>>>>>>"+params);
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {
                    if (StringUtils.isEmpty(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"	+ bean.getBankSessionId() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("5".equals(bean.getBankId())) {//花旗
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())	|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"	 + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"	 + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("16".equals(bean.getBankId())) {//交通银行
                if("0".equals(bean.getType())) {//导入
                    if (CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params=bean.getIdCardNo()+"@"+bean.getBankPwd()+"@"+bean.getIskeep()+"@"+bean.getClient();
                    accountname=bean.getIdCardNo();
                }else if("1".equals(bean.getType())) {//更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params=bean.getIdCardNo()+"@"+bean.getBankPwd()+"@"+bean.getIskeep()+"@"+bean.getClient();
                    accountname=bean.getCard4Num();
                }
            }else if ("13".equals(bean.getBankId())) {//建设银行
                if("0".equals(bean.getType())) {//导入
                    if (CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
//                    params=bean.getBankSessionId()+"@"+bean.getIdCardNo()+"@"+bean.getBankPwd()+"@"+bean.getIskeep()+"@"+bean.getClient();
                    params=bean.getIdCardNo()+"@"+bean.getBankPwd()+"@"+bean.getIskeep()+"@"+bean.getClient()+"@"+bean.getIpAddr();
                    accountname=bean.getIdCardNo();
                }else if("1".equals(bean.getType())) {//更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
//                    params=bean.getBankSessionId()+"@"+bean.getIdCardNo()+"@"+bean.getBankPwd()+"@"+bean.getIskeep()+"@"+bean.getClient();
                    params=bean.getIdCardNo()+"@"+bean.getBankPwd()+"@"+bean.getIskeep()+"@"+bean.getClient()+"@"+bean.getIpAddr();;
                    accountname=bean.getCard4Num();
                }
            } else if ("4".equals(bean.getBankId())|| "19".equals(bean.getBankId())
                    || "11".equals(bean.getBankId()) || "9".equals(bean.getBankId())
                    || "8".equals(bean.getBankId()) || "14".equals(bean.getBankId())
                    ||"15".equals(bean.getBankId())) {// 农业,上海银行,民生银行,浦发银行
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"+ bean.getBankSessionId() + "@" +
                            bean.getIskeep()+ "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"+ bean.getBankSessionId() +
                            "@" + bean.getIskeep()+ "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("2".equals(bean.getBankId())) {// 中信银行
                if ("0".equals(bean.getType())) {// 导入
                    if (StringUtils.isEmpty(bean.getIdCardNo())||
                            StringUtils.isEmpty(bean.getBankPwd())|| StringUtils.isEmpty(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"+ bean.getBankSessionId() + "@" + bean.getIskeep()+ "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"+ bean.getBankSessionId() + "@" + bean.getIskeep()+ "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("该银行暂不支持");
                return bean;
            }

            ImportTaskDto task=bankImportService.queryTaskByUser(bean.getType(),bean.getCuserId(),Integer.valueOf(bean.getBankId()),0,accountname);

            Integer taskid = -1;
            if (task != null) {
                taskid = task.getItaskid();
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(taskid+"");
            } else {
                taskid = bankImportService.querySeqIdFormTask();

                if (taskid==null||taskid<=0){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("生成任务失败，请重试");
                    return bean;
                }
                task=new ImportTaskDto();
                task.setItaskid(taskid);
                task.setCuserid(bean.getCuserId());
                task.setIbankid(Integer.valueOf(bean.getBankId()));
                task.setItype(Integer.valueOf(bean.getType()));
                task.setIstate(0);
                task.setIsauto(bean.getIsauto());
                task.setCdesc("模拟银行登录中");
                task.setCurlparams(params);
                task.setCaccountname(accountname);
                task.setIsend(2);
                int rt = bankImportService.createTask(task);
                logger.info("rt===" + rt + "  taskid=" + taskid + " getCuserId=" + bean.getCuserId() +
                        " bankid=" + bean.getBankId() + " bean.getType()=" + bean.getType() + " params=" + params + " accountname=" + accountname);
                bean.setTaskid(String.valueOf(taskid));

                String jsonString = "{\"content\":\"{\\\"taskid\\\":\\\""+taskid+"\\\",\\\"bankid\\\":\\\""+bean.getBankId()+"\\\""
                        + ",\\\"busiErrCode\\\":\\\"0\\\",\\\"busiErrDesc\\\":\\\"\\\",\\\"caccountname\\\":\\\""+accountname+"\\\""
                        + ",\\\"cuserid\\\":\\\""+bean.getCuserId()+"\\\",\\\"cparams\\\":\\\""+params+"\\\",\\\"sid\\\":\\\""+bean.getSourceCode()+"\\\",\\\"userIp\\\":\\\""+bean.getIpAddr()+"\\\"}\",\"type\":\""+bean.getType()+"\",\"bankId\":\""+bean.getBankId()+"\",\"isauto\":\""+bean.getIsauto()+"\"}";
                if (rt == 1) {
                    boolean sflag= KafkaService.pushToTopic(jsonString);
//                    boolean sflag=true;
                    if (!sflag) {
                        logger.info(bean.getTaskid()+" push kafka error jsonString="+jsonString);
                    }else {
                        logger.info(bean.getTaskid()+" push kafka seccuss jsonString="+jsonString);
                    }
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(String.valueOf(taskid));
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("导入失败请重试");
            logger.error("createBillTask异常", e);
        }
        return  bean;
    }

    /**
     * 带插件网银登录情况分析
     * @param bean
     *
     */
    @BoltController
    public Channel billTaskConsume(Channel bean) {
        try {
            String params = "";
            String accountname = "";
            String appVersion = bean.getAppversion();
            if ("0".equals(bean.getType())) {// 导入
                if (CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIskeep())) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("非法操作,缺少必要参数");
                    return bean;
                }
                params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient() + "@" + appVersion;
                accountname = bean.getIdCardNo();
            } else if ("1".equals(bean.getType())) {// 更新
                if (CheckUtil.isNullString(bean.getCard4Num())) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("非法操作,缺少必要参数");
                    return bean;
                }
                params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient() + "@" + appVersion;
                accountname = bean.getCard4Num();
            }
            ConsumeTaskDto task = bankImportService.queryConsumeTaskByUser(bean.getType(), bean.getCuserId(), bean.getBankId(), "0", accountname);
            Integer taskid = -1;
            if (task != null) {
                taskid = task.getItaskid();
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(taskid + "");
            } else {
                taskid = bankImportService.querySeqIdFromConsumeTask();
                if (taskid == null || taskid <= 0) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("生成任务失败，请重试");
                    return bean;
                }
                task = new ConsumeTaskDto();
                task.setItaskid(taskid);
                task.setCuserid(bean.getCuserId());
                task.setIbankid(Integer.valueOf(bean.getBankId()));
                task.setItype(Integer.valueOf(bean.getType()));
                task.setIstate(0);
                task.setCdesc("模拟银行登录中");
                task.setCurlparams(params);
                task.setCaccountname(accountname);
                task.setIsend(0);
                int rt = bankImportService.createConsumeTask(task);
                bean.setTaskid(String.valueOf(taskid));
                if(rt==1){
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(String.valueOf(taskid));
                }else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                }
                logger.info("rt===" + rt + "  taskid=" + taskid + " getCuserId=" + bean.getCuserId() +
                        " bankid=" + bean.getBankId() + " bean.getType()=" + bean.getType() + " params=" + params + " accountname=" + accountname);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("网银登录分析接口异常");
            logger.error("billTaskConsume异常", e);
        }
        return bean;
    }

    /**
     * 记录账单导入执行状态
     * @param bean
     */
    @BoltController
    public Channel billTaskChangeCode(Channel bean) {
        try {
            String accountname = "";
            if ("0".equals(bean.getType())) {// 导入
                accountname = bean.getIdCardNo();
            } else if ("1".equals(bean.getType())) {// 更新
                accountname = bean.getCard4Num();
            }
            ConsumeTaskDto task = bankImportService.queryConsumeTaskByUser(bean.getType(), bean.getCuserId(), bean.getBankId(), "0", accountname);
            int taskid = -1;
            if (task != null) {
                taskid = task.getItaskid();
            }
            if(taskid!=-1){
                int isend;//是否完成 0:未完 1:已完
                int istate;//执行状态 0:开始导入 1:导入成功 2:获取图片验证码 3:获取短信验证码 4:短信验证 5导入失败
                String code = bean.getCode();//任务执行的程度 0:导入失败 1:导入成功 2:获取图片验证码 3:获取短信验证码 4:短信验证
                String cdesc = bean.getBusiErrDesc();
                if("1".equals(code)){
                    isend = 1;
                    istate = 1;
                }else if("2".equals(code)){
                    isend = 0;
                    istate = 2;
                }else if("3".equals(code)){
                    isend = 0;
                    istate = 3;
                }else if("4".equals(code)){
                    isend = 0;
                    istate = 4;
                }else{
                    isend = 1;
                    istate = 5;
                }
                task = new ConsumeTaskDto();
                task.setIsend(isend);
                task.setIstate(istate);
                task.setCdesc(cdesc);
                task.setItaskid(taskid);
                int rt = bankImportService.updateConsumeTask(task);
                if (rt == 1) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(cdesc);
                    logger.info("任务进度[" + bean.getTaskid() + "] 更新成功,code["
                            + code + "] desc["
                            + bean.getBusiErrDesc() + "]");
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                    logger.info("任务进度[" + bean.getTaskid() + "] 更新失败,code["
                            + code + "] desc["
                            + bean.getBusiErrDesc() + "] 更新数据库失败");
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("登录状态更新异常");
            logger.error("billTaskChangeCode异常", e);
        }
        return bean;
    }

}
