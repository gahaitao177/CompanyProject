package com.caiyi.financial.nirvana.ccard.bill.bank.util;

import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.danga.MemCached.MemCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ljl on 2016/9/14.
 */
@Component
public class BankImportUtil {
    private static Logger logger = LoggerFactory.getLogger(BankImportUtil.class);
    @Autowired
    public BankImportService bankImportService;
    @Autowired
    public MemCachedClient cc;

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
                    if (CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getBankSessionId() + ";"	+ bean.getBankRand() + ";" + bean.getIskeep() + ";"	+ bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getBankSessionId() + ";"	+ bean.getBankRand() + ";" + bean.getIskeep() + ";"	+ bean.getClient();
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
                            || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";"	+ bean.getBankSessionId() + ";"	+ bean.getBankRand() + ";" + bean.getIskeep() + ";"
                            + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankSessionId() + ";" + bean.getBankRand()	+ ";" + bean.getBankPwd() + ";" + bean.getIskeep()+ ";" + bean.getClient() + ";" + bean.getIdCardNo();
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
                    if (CheckUtil.isNullString(bean.getBankRand())|| CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIdCardNo())
                            || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"	+ bean.getBankSessionId() + "@"	+ bean.getBankRand()  + "@" + bean.getOptRand() + "@" + bean.getIskeep() + "@"
                            + bean.getClient();
                    logger.info("params>>>>>>>>>>>>>>>>>>>>>>>>>>"+params);
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@"	+ bean.getBankSessionId() + "@"	+  bean.getBankRand() + "@" +  bean.getOptRand() + "@" + bean.getIskeep() + "@" + bean.getClient();
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
                    if (CheckUtil.isNullString(bean.getBankPwd())||CheckUtil.isNullString(bean.getIdCardNo())||CheckUtil.isNullString(bean.getIskeep())) {
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
                    if (CheckUtil.isNullString(bean.getBankPwd())||CheckUtil.isNullString(bean.getIdCardNo())||CheckUtil.isNullString(bean.getIskeep())) {
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
                    || "11".equals(bean.getBankId()) || "9".equals(bean.getBankId()) || "8".equals(bean.getBankId())) {// 农业,上海银行,民生银行,浦发银行
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIskeep())) {
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
            } else if ("2".equals(bean.getBankId())) {// 中信银行
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getIdCardNo())|| CheckUtil.isNullString(bean.getBankPwd())|| CheckUtil.isNullString(bean.getIskeep())) {
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
                task.setCdesc("模拟银行登录中");
                task.setCurlparams(params);
                task.setCaccountname(accountname);
                task.setIsend(2);
                task.setIsauto(bean.getIsauto());
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
            if(taskid!=null&&taskid>0){
                if("4".equals(bean.getBankId())||"19".equals(bean.getBankId())
                        ||"11".equals(bean.getBankId())||"9".equals(bean.getBankId())
                        ||"8".equals(bean.getBankId())){//农业,上海银行,民生,浦发
                    Object mindSession = cc.get(bean.getBankId()+bean.getCuserId() + "multi_bankSessionRand");
                    String mindRand;
                    if(mindSession!=null){
                        mindRand = (String) mindSession;
                        String key = taskid+"@"+bean.getBankId()+"taskStateSession";
                        cc.set(key, mindRand,1000*60*50);
                        cc.delete(bean.getBankId()+bean.getCuserId() + "multi_bankSessionRand");
                    }else{
                        logger.info("CreateTaskBankBill>>获取缓存数据失败");
                    }
                }
            }

        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("导入失败请重试");
            logger.error("createBillTask异常", e);
        }
        return  bean;
    }
}
