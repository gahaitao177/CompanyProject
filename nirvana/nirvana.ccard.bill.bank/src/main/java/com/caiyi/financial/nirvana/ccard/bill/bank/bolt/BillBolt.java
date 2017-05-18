package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillMonthDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.danga.MemCached.MemCachedClient;
import org.apache.storm.task.TopologyContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Mario on 2016/7/26 0026.
 * 账单相关接口移植
 * 为BillController提供支持
 */
@Bolt(boltId = "billCommon", parallelismHint = 1, numTasks = 1)
public class BillBolt extends BaseBolt {

    private BankImportService bankImportService;
    @Autowired
    MemCachedClient cc;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        bankImportService = getBean(BankImportService.class);
        logger.info("---------------------billCommon _prepare");
    }

    /**
     * 账单消费分析
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel billConsumeInfo(Channel bean) {
        try {
            String billid = bean.getBillId();
            if (CheckUtil.isNullString(billid)) {
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("无效的cardId");
                return bean;
            }

            BankBillDto billDto = bankImportService.getUserBankBillById(bean.getBillId());
            if (billDto == null) {
                logger.info("用户:" + bean.getCuserId() + "  cardId:" + bean.getBillId() + " 无效");
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("无效的cardId");
                return bean;
            }
            //未出账单金额
            Double nobillamount = billDto.getInobillamount();
            List<BillMonthDto> listBillMonth = bankImportService.getAllBillMonthByBillId(billid);
            if (listBillMonth == null || listBillMonth.size() == 0) {
                logger.info("用户:" + bean.getCuserId() + "  cardId:" + bean.getBillId() + " 暂无账单消费");
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("暂无账单消费");
                return bean;
            }
            StringBuilder sb = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#0.00");
            for (BillMonthDto item : listBillMonth) {
                String imonthid = item.getImonthid().toString();
                String cmonth = item.getCmonth();
                int isbill = item.getIsbill();
                Double ishouldrepayment = item.getIshouldrepayment();
                Double totalconsume;
                if("0".equals(isbill)){
                    totalconsume = nobillamount;
                }else{
                    totalconsume = ishouldrepayment;
                }
                if(totalconsume==null){
                    totalconsume = 0.00;
                }
                //String totalconsume = bankImportService.calcTotalConsume(imonthid);
               /* if (!CheckUtil.isNullString(totalconsume)) {
                    double dtotalconsume = Double.parseDouble(totalconsume);
                    totalconsume = df.format(dtotalconsume);
                } else {
                    totalconsume = "0.00";
                }*/
                //格式20160821
                String billDate = item.getCbilldate();
                if(!CheckUtil.isNullString(billDate)) {
                    SimpleDateFormat sdfIn = new SimpleDateFormat("yyyyMMdd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdfIn.parse(billDate));
                    SimpleDateFormat sdfOut = new SimpleDateFormat("MM/dd");
                    Date dateBillEnd = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.MONTH, -1);
                    Date dateBillStart = cal.getTime();
                    billDate = sdfOut.format(dateBillStart) + "-" + sdfOut.format(dateBillEnd);
                }
                sb.append("<MonthData month=\"" + cmonth + "\" totalBill=\"" + totalconsume + "\" billDay=\""+ billDate + "\">");

                List<Map<String, Object>> ListPjrs = bankImportService.queryConsumeByCostType(imonthid);
                for (Map<String, Object> pjrs : ListPjrs) {
                    String typeBill = pjrs.get("TYPEBILL") + "";//消费金额
                    if (!CheckUtil.isNullString(typeBill) && !typeBill.equals("null")) {
                        double dtotal = Double.parseDouble(typeBill);
                        typeBill = df.format(dtotal);
                    } else {
                        typeBill = "0.00";
                    }
                    String costid = pjrs.get("COSTID") + "";//类型ID
                    String costname = pjrs.get("CCOSTTYPENAME") + "";//消费类型
                    if (CheckUtil.isNullString(costname) || CheckUtil.isNullString(costid) || costname.equals("null")) {
                        costname = "其他支出";//账单流水没有消费类型，设为默认值其他
                        costid = "1";
                    }
                    sb.append("<Item typeName=\"" + costname + "\" typeId=\"" + costid + "\" typeBill=\"" + typeBill + "\"/>");
                }
                sb.append("</MonthData>");
            }
            bean.setBusiErrCode(1);
            bean.setBusiXml(sb.toString());
            logger.info("用户:" + bean.getCuserId() + "  cardId:" + bean.getBillId() + " 获取账单消费数据 success");
        } catch (Exception e) {
            logger.error("BillBolt billConsumeInfo异常", e);
            bean.setBusiErrCode(3);
            bean.setBusiErrDesc("查询账单消费记录失败,请重试");
        } finally {
            return bean;
        }
    }

    /**
     * 导入账单接口2
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel setCreditId(Channel bean) {
        try {
            if ("1".equals(bean.getType())) {
                if ("13".equals(bean.getBankId()) || "2".equals(bean.getBankId())
                        || "3".equals(bean.getBankId()) || "9".equals(bean.getBankId())
                        || "11".equals(bean.getBankId()) || "4".equals(bean.getBankId())
                        || "19".equals(bean.getBankId()) || "8".equals(bean.getBankId())) {
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(3);
                        bean.setBusiErrDesc("无效银行卡");
                        return bean;
                    }
                    BankBillDto bankBillDto = bankImportService.getUserBankBill(bean.getCard4Num(),Integer.parseInt(bean.getBankId()), bean.getCuserId());
                    if (bankBillDto == null) {
                        bean.setBusiErrCode(3);
                        bean.setBusiErrDesc("无效银行卡");
                        return bean;
                    }
                    bean.setCreditId(bankBillDto.getIcreditid() + "");
                    bean.setOutsideId(bankBillDto.getIoutsideid());
                    bean.setBillId(bankBillDto.getIbillid() + "");
                    bean.setIskeep(bankBillDto.getIskeep() + "");
                }
            }
        } catch (Exception e) {
            logger.error("setCreditId异常", e);
            bean.setBusiErrCode(3);
            bean.setBusiErrDesc("无效银行卡");
        } finally {
            return bean;
        }
    }

    /**
     * 获取银行验证码2
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel createBankBillTask2(Channel bean) {
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
                    if (CheckUtil.isNullString(bean.getBankRand()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo())
                            || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";" + bean.getBankSessionId() + ";" + bean.getBankRand() + ";" + bean.getIskeep() + ";" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";" + bean.getBankSessionId() + ";" + bean.getBankRand() + ";" + bean.getIskeep() + ";" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("10".equals(bean.getBankId())) {// 兴业
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCard6Num()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getIdCard6Num() + ";" + bean.getBankPwd() + ";" + bean.getIskeep() + ";" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getIdCard6Num() + ";" + bean.getBankPwd() + ";" + bean.getIskeep() + ";" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("7".equals(bean.getBankId())) {// 平安
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getBankRand()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo())
                            || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";" + bean.getBankSessionId() + ";" + bean.getBankRand() + ";" + bean.getIskeep() + ";"
                            + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankSessionId() + ";" + bean.getBankRand() + ";" + bean.getBankPwd() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getIdCardNo();
                    accountname = bean.getCard4Num();
                }
            } else if ("1".equals(bean.getBankId())) {// 广发
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getIskeep()) || CheckUtil.isNullString(bean.getIdCard6Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + ";" + bean.getBankPwd() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getIdCard6Num();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {// 更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankPwd() + ";" + bean.getIskeep() + ";" + bean.getClient() + ";" + bean.getIdCardNo() + ";" + bean.getIdCard6Num();
                    accountname = bean.getCard4Num();
                }
            } else if ("3".equals(bean.getBankId())) {// 光大银行
                String img_rand = (String) cc.get(bean.getCuserId() + bean.getBankId() + "_guangdaRand");//图片验证码
                if ("0".equals(bean.getType())) {// 导入 手机号登录
                    if (CheckUtil.isNullString(bean.getBankRand()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo())
                            || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getBankSessionId() + "@" + img_rand + "@" + bean.getBankRand() + "@" + bean.getIskeep() + "@"
                            + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getBankSessionId() + "@" + img_rand + "@" + bean.getBankRand() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("5".equals(bean.getBankId())) {//花旗
                if ("0".equals(bean.getType())) {// 导入
                    if (CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("16".equals(bean.getBankId())) {//交通银行
                if ("0".equals(bean.getType())) {//导入
                    if (CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {//更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("13".equals(bean.getBankId())) {//建设银行
                if ("0".equals(bean.getType())) {//导入
                    if (CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankSessionId() + "@" + bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {//更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getBankSessionId() + "@" + bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }

            } else if ("4".equals(bean.getBankId())) {//农业银行
                if ("0".equals(bean.getType())) {//导入
                    if (CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getBankRand()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getBankSessionId() + "@" + bean.getBankRand() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {//更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getBankSessionId() + "@" + bean.getBankRand() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else if ("2".equals(bean.getBankId())) {//中信银行
                if ("0".equals(bean.getType())) {//导入
                    if (CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIskeep())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getBankSessionId() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getIdCardNo();
                } else if ("1".equals(bean.getType())) {//更新
                    if (CheckUtil.isNullString(bean.getCard4Num())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("非法操作,缺少必要参数");
                        return bean;
                    }
                    params = bean.getIdCardNo() + "@" + bean.getBankPwd() + "@" + bean.getBankSessionId() + "@" + bean.getIskeep() + "@" + bean.getClient();
                    accountname = bean.getCard4Num();
                }
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("该银行暂不支持");
                return bean;
            }
            ImportTaskDto taskDto = bankImportService.queryTaskByUser2(bean.getType(), bean.getCuserId(), bean.getBankId(), 0, 2, accountname);
            String taskid = "";
            if (taskDto != null) {
                taskid = taskDto.getItaskid() + "";
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(taskid);
            } else {
                taskid = bankImportService.querySeqIdFormTask().toString();
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
                logger.info("rt===" + "  taskid=" + taskid + " getCuserId=" + bean.getCuserId() +
                        " bankid=" + bean.getBankId() + " bean.getType()=" + bean.getType() + " params=" + params + " accountname=" + accountname);
                bean.setTaskid(taskid);
                if (rt == 1) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(taskid);
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("导入失败请重试");
                }
            }
        } catch (Exception e) {
            logger.error("setCreditId异常", e);
            bean.setBusiErrCode(3);
            bean.setBusiErrDesc("无效银行卡");
        } finally {
            return bean;
        }
    }


    /**
     * 查询任务状态
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel showTaskState(Channel bean) {
        try {
            if (CheckUtil.isNullString(bean.getTaskid())) {
                bean.setBusiErrCode(999);
                bean.setBusiErrDesc("非法操作,缺少必要参数");
            } else {
                ImportTaskDto taskDto = bankImportService.queryTaskById(bean.getTaskid());
                if (taskDto != null) {
                    int ccode = taskDto.getIstate();
                    String cdesc = taskDto.getCdesc();
                    //0 银行模拟登录中 1 登录成功 2解析账单中 3（解析异常） 4解析完成（返回结果）
                    // 如果异常 返回错误说明及错误解决办法 add by lcs 20160229
                    String type = taskDto.getItype() + "";
                    String bankId = taskDto.getIbankid() + "";
                    bean.setBankId(bankId);
                    bean.setType(type);

                    bean.setBusiErrCode(ccode);
                    bean.setBusiErrDesc(cdesc);
                    addErrorResolvent(bean);
                } else {
                    bean.setBusiErrCode(5);
                    bean.setBusiErrDesc("查询异常");
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(5);
            bean.setBusiErrDesc("查询异常");
            logger.error("showTaskState异常", e);
        } finally {
            return bean;
        }
    }


    /**************************************************************************
     * 添加错误解决办法
     * @param bean
     */
    private void addErrorResolvent(Channel bean){
        try {
            logger.info( "code:" + bean.getBusiErrCode() + ";" +  bean.getBusiErrDesc());
            StringBuilder sb = new StringBuilder();
            sb.append("<questionList> ");
            HashMap<String,String> usedKey = new HashMap<String,String>();
            if (3 == bean.getBusiErrCode() || 5 == bean.getBusiErrCode()){
                // 获取并缓存银行导入信息信息
                Calendar cd = Calendar.getInstance();
                cd.setTime(new Date());
                int hourOfDay = cd.get(Calendar.HOUR_OF_DAY);

                // 问题列表
                String ccQuestionkKey = "taskQuestionXml" + hourOfDay;
                // 返回错误
                String ccErrorKey = "taskErrorXml" + hourOfDay;

                logger.info("缓存key:" + ccQuestionkKey  + "," + ccErrorKey );
                Object questionListObj = cc.get(ccQuestionkKey);
                Object errorListObj = cc.get(ccErrorKey);
//				Object questionListObj = null;
//				Object errorListObj = null;
                List<Element> questionNodeList = new ArrayList<Element>();
                List<Element> errorNodeList = new ArrayList<Element>();
                if (questionListObj != null){
                    questionNodeList = (ArrayList<Element>)questionListObj;
                    errorNodeList = (ArrayList<Element>)errorListObj;
                    logger.info("获取缓存成功");
                } else {
                    logger.info("采用在线地址/opt/export/data/busi/task_question.xml");
                    Document errorConfigXml = XmlTool.read(new URL("http://www.huishuaka.com/busi/task_question.xml"),"utf-8");
//					File xmlFile = new File("D:\\opt\\cert\\test.xml");
//					JXmlWapper errorConfigXml = JXmlWapper.parse(xmlFile);
                    questionNodeList =  errorConfigXml.getRootElement().elements("question");
                    errorNodeList = errorConfigXml.getRootElement().elements("taskret");
                    boolean ret = cc.set(ccQuestionkKey, questionNodeList, 60*60*3600);
                    if (ret){
                        logger.info("放置缓存成功" + ccQuestionkKey );
                    } else {
                        logger.info("放置缓存失败"+ ccQuestionkKey );
                    }

                    boolean retError = cc.set(ccErrorKey, errorNodeList, 60*60*3600);
                    if (retError){
                        logger.info("放置缓存成功" + ccErrorKey );
                    } else {
                        logger.info("放置缓存失败"+ ccErrorKey );
                    }
                }

                logger.info("questionNodeList:"+ questionNodeList.size() );
                logger.info("errorNodeList:"+ errorNodeList.size() );
                // 银行错误
//				if ("0".equals(bean.getType()) || "1".equals(bean.getType())){
                for (Element errorNode:errorNodeList){
                    String errorKeyWord = errorNode.attributeValue("keyword");
                    String bankid = errorNode.attributeValue("bankid");
                    String desc = bean.getBusiErrDesc();

                    logger.info("errorNode:"+ errorKeyWord + "," +  bankid + "," + desc);
                    if ("-1".equals(bankid) || bean.getBankId().equals(bankid)){
                        if (!CheckUtil.isNullString(desc) && desc.contains(errorKeyWord)){
                            String refId = errorNode.attributeValue("refid");

                            for (Element questionNode:questionNodeList){
                                String questionId = questionNode.attributeValue("id");
                                String question = questionNode.attributeValue("desc");
                                String solution = questionNode.attributeValue("solution");
                                logger.info("questionNode:"+ questionId + "," +  question + "," + solution);
                                if (!CheckUtil.isNullString(questionId) && questionId.equals(refId) && !usedKey.containsKey(questionId)){
                                    sb.append("<question ");
                                    sb.append(XmlTool.createAttrXml("des",question));
                                    sb.append(XmlTool.createAttrXml("solution",solution));
                                    sb.append(" />");
                                    usedKey.put(questionId, "1");
                                    logger.info("首先加入"+ question );
                                }
                            }
                        }
                    }

                }
//				}

                //
                for (Element questionNode:questionNodeList){
                    String questionId = questionNode.attributeValue("id");
                    String question = questionNode.attributeValue("desc");
                    String solution = questionNode.attributeValue("solution");
                    if ( !usedKey.containsKey(questionId)){
                        sb.append("<question ");
                        sb.append(XmlTool.createAttrXml("des",question));
                        sb.append(XmlTool.createAttrXml("solution",solution));
                        sb.append(" />");
                        usedKey.put(questionId, "1");
                    }
                }
            }
            sb.append("	</questionList>");

            logger.info("sb" + sb);
            bean.setBusiXml(sb.toString());
        }catch(Exception e){
            e.printStackTrace();
            logger.info("Exception"+ e );
        }

    }
}
