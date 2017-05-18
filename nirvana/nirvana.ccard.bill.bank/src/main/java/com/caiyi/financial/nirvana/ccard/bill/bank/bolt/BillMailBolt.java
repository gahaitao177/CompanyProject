package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.danga.MemCached.MemCachedClient;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by Mario on 2016/7/28 0028.
 * 账单邮箱相关接口移植
 */
@Bolt(boltId = "billMail", parallelismHint = 1, numTasks = 1)
public class BillMailBolt extends BaseBolt {

    private BankImportService bankImportService;
    @Autowired
    MemCachedClient cc;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        bankImportService = getBean(BankImportService.class);
        logger.info("---------------------billCommon _prepare");
    }

    /**
     * 设置CreditId
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel setMailCreditId(Channel bean) {
        try {
            if ("5".equals(bean.getType())) {
                if ("0".equals(bean.getMailType()) || "2".equals(bean.getMailType()) || "3".equals(bean.getMailType())) {
                    BankBillDto billDto = bankImportService.queryBillByUserIdBillId(bean);
                    if (billDto == null) {
                        bean.setBusiErrCode(3);
                        bean.setBusiErrDesc("无效的邮箱账号");
                        return bean;
                    }
                    bean.setCreditId(billDto.getIcreditid() + "");
                    bean.setOutsideId(billDto.getIoutsideid() + "");
                    bean.setPassword(billDto.getCexpiredate());
                    bean.setIskeep(billDto.getIskeep() + "");
                    // add by lcs 20151229
                    if (!CheckUtil.isNullString(bean.getOutsideId())) {
                        String mailIdAndType = CaiyiEncrypt.dencryptStr(bean.getOutsideId());
                        if (mailIdAndType.contains("-")) {
                            bean.setMailAddress(mailIdAndType.split("-")[0]);
                            bean.setMailType(mailIdAndType.split("-")[1]);
                        }
                    }
                    logger.error("setMailAddress" + bean.getMailAddress());
                    logger.error("setMailType" + bean.getMailType());
                }
            }
        } catch (Exception e) {
            logger.error("setMailCreditId异常", e);
            bean.setBusiErrCode(3);
            bean.setBusiErrDesc("无效的邮箱账号");
        } finally {
            return bean;
        }
    }

    /**
     * createEmailBillTask
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel createEmailBillTask(Channel bean) {
        try {
            String params = "";
            String accountname = "";
            if (CheckUtil.isNullString(bean.getType())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("非法操作,缺少必要参数");
                return bean;
            }

            logger.info("bean.getBankId():" + bean.getBankId());
            if ("0".equals(bean.getMailType())) {// QQ邮箱
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress()) || CheckUtil.isNullString(bean.getMailPwd()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
//					params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getConfirmpassword() + ";"	+ bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient()+";" +bean.getImportType();

                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getConfirmpassword() + ";" + bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";"
                            + bean.getImportType() + ";" + bean.getIpAddr() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
//					params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getConfirmpassword() + ";"+ bean.getBankSessionId()  + ";" + bean.getIskeep() + ";"	+ bean.getClient()+";" +bean.getImportType();

                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getConfirmpassword() + ";" + bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";"
                            + bean.getImportType() + ";" + bean.getIpAddr() + ";" + bean.getBankId();
                    accountname = bean.getBillId();
                }
            } else if ("2".equals(bean.getMailType())) {//163邮箱 126
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress()) || CheckUtil.isNullString(bean.getMailPwd()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getBillId();
                }
            } else if ("3".equals(bean.getMailType())) {  //126
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress()) || CheckUtil.isNullString(bean.getMailPwd()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                    logger.info("bankSessionId>>>" + bean.getBankSessionId() + ";params>>>" + params);
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getBankSessionId() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getBillId();
                }
            } else if ("4".equals(bean.getMailType())) {  //新浪
                if ("4".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getMailAddress()) || CheckUtil.isNullString(bean.getMailPwd()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getMailAddress();
                } else if ("5".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getBillId())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getBankId();
                    accountname = bean.getBillId();
                }
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("该邮箱暂不支持");
                return bean;
            }
            ImportTaskDto taskDto = bankImportService.queryTaskByUser2(bean.getType(), bean.getCuserId(), bean.getBankId(), 0, 2, accountname);
            String taskid = "";
            if (taskDto != null) {
                taskid = taskDto.getItaskid() + "";
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(taskid);
            } else {
                taskid = bankImportService.querySeqIdFormTask() + "";
                ImportTaskDto newTaskDto = new ImportTaskDto();
                newTaskDto.setItaskid(Integer.parseInt(taskid));
                newTaskDto.setCuserid(bean.getCuserId());
                newTaskDto.setIbankid(Integer.parseInt(bean.getBankId()));
                newTaskDto.setItype(Integer.parseInt(bean.getType()));
                newTaskDto.setIstate(0);
                newTaskDto.setCdesc("模拟银行登录中");
                newTaskDto.setCurlparams(params);
                newTaskDto.setCaccountname(accountname);
                int rt = bankImportService.createTask(newTaskDto);
                logger.info("rt===" + rt + "  taskid=" + taskid + " getCuserId=" + bean.getCuserId() +
                        " mailtype=" + bean.getMailType() + " type=" + bean.getType() + " params=" + params + " accountname=" + accountname);
                bean.setTaskid(taskid);
                /*String jsonString = "{\"content\":\"{\\\"taskid\\\":\\\""+taskid+"\\\",\\\"bankid\\\":\\\""+bean.getBankId()+"\\\""
                        + ",\\\"busiErrCode\\\":\\\"0\\\",\\\"busiErrDesc\\\":\\\"\\\",\\\"caccountname\\\":\\\""+accountname+"\\\""
						+ ",\\\"cuserid\\\":\\\""+bean.getCuserId()+"\\\",\\\"cparams\\\":\\\""+params+"\\\"}\",\"type\":\""+bean.getType()+"\",\"bankId\":\""+bean.getBankId()+"\"}";
				boolean sflag=KafkaService.pushToTopic(jsonString);*/
                boolean sflag = true;
                if (rt == 1 && sflag) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(taskid);
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                }
            }
            logger.info("用户id：" + bean.getCuserId() + ",用户ip:" + bean.getIpAddr() + ",taskid：" + taskid);
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("导入失败请重试");
            logger.error("createEmailBillTask异常", e);
        } finally {
            return bean;
        }
    }

    /**
     * updateBillInfoByMail
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel updateBillInfoByMail(Channel bean) {
        try {
            logger.info("updateBillByMail start :");

            if (!CheckUtil.isNullString(bean.getCuserId())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("邮件服务商升级打怪去啦，打完就回来");
                return bean;
            }

            // update by lcs 20160314 start
            if ((CheckUtil.isNullString(bean.getClient()) || !bean.getClient().matches("[0|1]")) || CheckUtil.isNullString(bean.getBillId())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("非法操作,缺少必要参数");
                return bean;
            }
            String params = bean.getClient() + ";" + bean.getBillId() + ";" + bean.getMailPwd() + ";" + bean.getIndePwd();

            Object getExcuteFlag = cc.get(bean.getCuserId() + params);
            logger.info("params-----" + bean.getCuserId() + params);
            logger.info("getExcuteFlag-----" + getExcuteFlag);
            if (getExcuteFlag != null) {
                bean.setBusiErrCode(405);
                bean.setBusiErrDesc("更新成功");
                return bean;
            }

            String taskid = "";
            ImportTaskDto taskDto = bankImportService.queryTaskByUser3("2", bean.getCuserId(), 0, 2, bean.getMailAddress());

            if (taskDto != null) {
                taskid = taskDto.getItaskid() + "";
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(taskid);
            } else {
                int nval = bankImportService.querySeqIdFormTask();
                taskid = nval + "";
                ImportTaskDto newTaskDto = new ImportTaskDto();
                newTaskDto.setItaskid(Integer.parseInt(taskid));
                newTaskDto.setCuserid(bean.getCuserId());
                newTaskDto.setIbankid(Integer.parseInt(bean.getBankId()));
                newTaskDto.setItype(3);
                newTaskDto.setIstate(0);
                newTaskDto.setCdesc("邮箱登录中");
                newTaskDto.setCurlparams(params);
                newTaskDto.setCaccountname(bean.getMailAddress());
                int rt = bankImportService.createTask(newTaskDto);
                logger.info("rt===" + rt + "  taskid=" + taskid + " getCuserId=" + bean.getCuserId() + " bankid=" + bean.getBankId() + " bean.getType()=" + bean.getType() + " params=" + params + " accountname=" + bean.getMailAddress());
                if (rt == 1) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(taskid);
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                }
            }
        } catch (Exception e) {
            logger.error("updateBillInfoByMail 异常");
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("读取邮箱线程失败");
        } finally {
            return bean;
        }
    }

    /**
     * billInfoByMail
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel billInfoByMail(Channel bean) {
        logger.info("readBillByMail start :");

        // 邮箱更新账单暂时停用  update by lcs 20151211
        if (!CheckUtil.isNullString(bean.getCuserId())){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("邮件服务商升级打怪去啦，打完就回来");
            return bean;
        }
        String mailType= bean.getMailType();
        if ((CheckUtil.isNullString(bean.getClient()) || !bean.getClient().matches("[0|1]")) || (CheckUtil.isNullString(mailType) || !mailType.matches("[0-8]{1}")) || CheckUtil.isNullString(bean.getMailAddress()) || CheckUtil.isNullString(bean.getMailPwd())){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("非法操作,缺少必要参数");
            return bean;
        }
        String params = bean.getMailAddress() + ";" + bean.getMailPwd() + ";" + bean.getMailType() + ";" + bean.getClient() + ";" + bean.getIskeep() + ";" + bean.getIndePwd();
        Object getExcuteFlag = cc.get(bean.getCuserId() + params);
        logger.info("params-----" + bean.getCuserId() + params);
        logger.info("getExcuteFlag-----" + getExcuteFlag);
        if (getExcuteFlag != null) {
            bean.setBusiErrCode(405);
            bean.setBusiErrDesc("导入成功");
            return bean;
        }
        System.out.println("cuserid-----" + bean.getCuserId());
        try {
            String taskid="";
            ImportTaskDto taskDto = bankImportService.queryTaskByUser3("2",bean.getCuserId(),0,2,bean.getMailAddress());
            if (taskDto!=null) {
                taskid=taskDto.getItaskid() + "";
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(taskid);
            }else {
                int nval = bankImportService.querySeqIdFormTask();
                taskid = nval + "";
                ImportTaskDto newTaskDto = new ImportTaskDto();
                newTaskDto.setItaskid(Integer.parseInt(taskid));
                newTaskDto.setCuserid(bean.getCuserId());
                newTaskDto.setIbankid(Integer.parseInt(bean.getBankId()));
                newTaskDto.setItype(2);
                newTaskDto.setIstate(0);
                newTaskDto.setCdesc("邮箱登录中");
                newTaskDto.setCurlparams(params);
                newTaskDto.setCaccountname(bean.getMailAddress());
                int rt = bankImportService.createTask(newTaskDto);
                logger.info("rt==="+rt+"  taskid="+taskid+" getCuserId="+bean.getCuserId()+" bankid="+bean.getBankId()+" bean.getType()="+bean.getType() +" params="+params+" accountname="+bean.getMailAddress());
                if (rt==1) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(taskid);
                }else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("读取邮箱线程失败");;
        } finally {
            return bean;
        }
    }
}
