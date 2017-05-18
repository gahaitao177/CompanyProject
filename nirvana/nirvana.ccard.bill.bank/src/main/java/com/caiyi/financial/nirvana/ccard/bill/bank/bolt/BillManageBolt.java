package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BillService;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.MessageService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Bill;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillDetailDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BillMonthDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.danga.MemCached.MemCachedClient;
import com.util.string.StringUtil;
import org.apache.storm.task.TopologyContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Mario on 2016/7/12 0012.
 */
@Bolt(boltId = "billManage", parallelismHint = 1, numTasks = 1)
public class BillManageBolt extends BaseBolt {

    private BankImportService bankImportService;
    private BillService billService;
    @Autowired
    private MessageService messageService;
    @Autowired
    public MemCachedClient cc;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        bankImportService = getBean(BankImportService.class);
        billService = getBean(BillService.class);
//        cc = getBean(MemCachedClient.class);
        logger.info("---------------------BillManageBolt _prepare");
    }

    /**
     * 卡管理列表
     * 对应老接口 qUserCardinfo
     * Modified By zhaojie 2016/7/13 11:27:52
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel queryUserCardinfo(Channel bean) {
        try {
            StringBuilder sb = new StringBuilder();
            List<BankBillDto> listBbd = bankImportService.getUserCardInfo(bean);
            String ibillid = "";
            String startdate = "";
            String paymentday = "";
            Date StartDate1 = null;
            Date StartDate2 = null;
            Date payDay = null;
            String sql = "";
            String endperioddate = ""; //账单结束日
            String crepaymentdate = "";
            String billdate = "";

            String format = "yyyyMM";
            String format1 = "yyyyMMdd";
            String format2 = "yyyy.MM.dd";
            String format3 = "yyyy/MM/dd";
            String formatPayDay = "";
            for (BankBillDto item : listBbd) {
                ibillid = item.getIbillid() + "";
                endperioddate = item.getCendperioddate();
                crepaymentdate = item.getCrepaymentdate();
                billdate = item.getCbilldate();
                startdate = item.getCstartperioddate();
                endperioddate = resetNumerForCardInfo(endperioddate);
                crepaymentdate = resetNumerForCardInfo(crepaymentdate);
                billdate = resetNumerForCardInfo(billdate);
                startdate = resetNumerForCardInfo(startdate);
                // add by lcs 20160222
                if (!CheckUtil.isNullString(startdate)) {
                    if (startdate.length() >= 8) {
                        if (startdate.contains("/")) {
                            String[] startdays = startdate.split("/");
                            StartDate1 = this.string2Date(startdays[0] + startdays[1], format);
                            StartDate2 = this.string2Date(startdays[0] + startdays[1] + startdays[2], format1);
                        } else {
                            StartDate1 = this.string2Date(startdate.substring(0, 6), format);
                            StartDate2 = this.string2Date(startdate, format1);
                        }
                    }
                }
                sb.append("<row ");
                // add by lcs 20160217 处理账单日
                int days = 0;
                String billDateNewest = "";
                if (!CheckUtil.isNullString(billdate)) {
                    Calendar cd1 = Calendar.getInstance();
                    Calendar cd2 = Calendar.getInstance();
                    cd2.set(Calendar.DAY_OF_MONTH, Integer.valueOf(billdate.trim()));
                    days = ((int) (cd2.getTime().getTime() / 1000) - (int) (cd1.getTime().getTime() / 1000)) / 3600 / 24;
                    if (days < 0) {
                        cd2.add(Calendar.MONTH, 1);
                        days = ((int) (cd2.getTime().getTime() / 1000) - (int) (cd1.getTime().getTime() / 1000)) / 3600 / 24;
                        billDateNewest = this.date2String(cd2.getTime(), format2);
                    } else {
                        billDateNewest = this.date2String(cd2.getTime(), format2);
                    }
                }
                sb.append(XmlTool.createAttrXml("tobilldays", String.valueOf(days)));
                sb.append(XmlTool.createAttrXml("billdatenewest", billDateNewest));
                //多少天内还款
                List<BillMonthDto> listBillMonth = bankImportService.getBillMonthByBillId(ibillid);
                BillMonthDto billMonth = null;
                String tempDay = null;
                if (listBillMonth != null && listBillMonth.size() > 0) {
                    billMonth = bankImportService.getBillMonthByBillId(ibillid).get(0);
                    tempDay = billMonth.getCrepaymentdate();
                }
                if (tempDay != null) {
                    paymentday = tempDay;
                    if (CheckUtil.isNullString(paymentday)) {
                        sb.append(XmlTool.createAttrXml("paymentdays", ""));
                        sb.append(XmlTool.createAttrXml("crepaymentdate", ""));
                        logger.info("CERPAYMENTDATE 为空");
                    } else if (paymentday.length() < 8) {
                        sb.append(XmlTool.createAttrXml("paymentdays", ""));
                        sb.append(XmlTool.createAttrXml("crepaymentdate", ""));
                        logger.info("CERPAYMENTDATE 格式错误");
                    } else {
                        Date nowTime = new Date();
                        if (!StringUtil.isEmpty(paymentday)) {
                            if (paymentday.contains("/")) {
                                payDay = this.string2Date(paymentday, format3);
                            } else {
                                payDay = this.string2Date(paymentday, format1);
                            }
                            formatPayDay = this.date2String(payDay, format2);
                        }
                        long surPlusDate = (payDay.getTime() - nowTime.getTime()) / (1000 * 60 * 60 * 24) + 1;
                        if (surPlusDate < 0) {
                            sb.append(XmlTool.createAttrXml("paymentdays", "0"));
                        } else {
                            sb.append(XmlTool.createAttrXml("paymentdays", String.valueOf(surPlusDate)));
                        }
                        sb.append(XmlTool.createAttrXml("crepaymentdate", formatPayDay));
                    }
                } else {
                    sb.append(XmlTool.createAttrXml("paymentdays", ""));
                    sb.append(XmlTool.createAttrXml("crepaymentdate", ""));
                }
                //免息天数
                if (CheckUtil.isNullString(startdate)) {
                    sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    logger.info("CSTARTPERIODDATE 为空");
                } else if (startdate.length() < 8) {
                    sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    logger.info("CSTARTPERIODDATE 格式错误");
                } else if (CheckUtil.isNullString(crepaymentdate)) {
                    sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    logger.info("CERPAYMENTDATE 为空");
                } else if (crepaymentdate.length() < 2) {
                    sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    logger.info("CERPAYMENTDATE 格式错误");
                } else if (CheckUtil.isNullString(billdate)) {
                    sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    logger.info("CBILLDATE 为空");
                } else if (billdate.length() < 2) {
                    sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    logger.info("CBILLDATE 格式错误");
                } else {
                    try {
                        String payday = crepaymentdate.substring(crepaymentdate.length() - 2);
                        //还款日
                        if (StartDate1 != null && StartDate2 != null) {
                            Calendar cd = Calendar.getInstance();
                            cd.setTime(StartDate1);
                            if (Integer.parseInt(payday) > Integer.parseInt(billdate)) {
                                //同一个月
                                cd.add(Calendar.MONTH, 1);
                            } else {
                                //不在同一月
                                cd.add(Calendar.MONTH, 2);
                            }
                            payDay = cd.getTime();
                            paymentday = this.date2String(payDay, format);
                            paymentday = paymentday + payday;
                            payDay = this.string2Date(paymentday, format1);

                            long day = (payDay.getTime() - StartDate2.getTime()) / (1000 * 60 * 60 * 24);
                            sb.append(XmlTool.createAttrXml("graceperiod", String.valueOf(day)));
                        } else {
                            sb.append(XmlTool.createAttrXml("graceperiod", ""));
                        }
                    } catch (Exception e1) {
                        logger.info("e1:", e1);
                        sb.append(XmlTool.createAttrXml("graceperiod", ""));
                    }
                }
                //账单日期
                Date endperioddate2 = null;
                if (CheckUtil.isNullString(startdate)) {
                    sb.append(XmlTool.createAttrXml("paymentduedate", ""));
                    logger.info("CSTARTPERIODDATE 为空");
                } else if (startdate.length() < 8) {
                    sb.append(XmlTool.createAttrXml("paymentduedate", ""));
                    logger.info("CSTARTPERIODDATE 格式错误");
                } else if (CheckUtil.isNullString(endperioddate)) {
                    sb.append(XmlTool.createAttrXml("paymentduedate", ""));
                    logger.info("ENDPERIODDATE 为空");
                } else if (endperioddate.length() < 8) {
                    sb.append(XmlTool.createAttrXml("paymentduedate", ""));
                    logger.info("ENDPERIODDATE 格式错误");
                } else {
                    try {
                        if (endperioddate.contains("/")) {
                            endperioddate2 = this.string2Date(endperioddate, format3);
                        } else {
                            endperioddate2 = this.string2Date(endperioddate, format1);
                        }
                        sb.append(XmlTool.createAttrXml("paymentduedate", this.date2String(StartDate2, format2) + "-" + this.date2String(endperioddate2, format2)));
                    } catch (Exception e2) {
                        logger.info("e2:", e2);
                        sb.append(XmlTool.createAttrXml("paymentduedate", ""));
                    }
                }
                //多少天内出账单
                if (endperioddate2 != null) {
                    String nowDate = new SimpleDateFormat(format1).format(new Date());
                    Date nowTime = this.string2Date(nowDate, format1);
                    Date endperioddate3 = this.string2Date(this.date2String(endperioddate2, format1), format1);
                    long day = (endperioddate3.getTime() - nowTime.getTime()) / (1000 * 60 * 60 * 24) + 1;
                    if (day < 0) {
                        day = 0;
                    }
                    sb.append(XmlTool.createAttrXml("billdays", String.valueOf(day)));
                }
                // add by lcs 20160224
                String cardnum = item.getIcard4num();
                String dispName = item.getCname();
                BankDto bankDto = bankImportService.getBankById(item.getIbankid() + "");
                if (!CheckUtil.isNullString(cardnum)) {
                    dispName = bankDto.getIshortname() + cardnum;
                }
                String importtype = item.getIswebormail() + "";
                sb.append(XmlTool.createAttrXml("cardid", item.getIbillid() + ""));
                sb.append(XmlTool.createAttrXml("bankname", dispName));
                sb.append(XmlTool.createAttrXml("icreditid", item.getIcreditid() + ""));
                sb.append(XmlTool.createAttrXml("bankid", item.getIbankid() + ""));
                sb.append(XmlTool.createAttrXml("cstartperioddate", item.getCstartperioddate()));
                sb.append(XmlTool.createAttrXml("cendperioddate", item.getCendperioddate()));
                sb.append(XmlTool.createAttrXml("balance", item.getIshouldrepayment() + ""));
                sb.append(XmlTool.createAttrXml("minpayment", item.getIlowestrepayment() + ""));
                sb.append(XmlTool.createAttrXml("inobillamount", item.getInobillamount() + ""));
                sb.append(XmlTool.createAttrXml("cardnum", cardnum));
                sb.append(XmlTool.createAttrXml("cardtype", "0"));
                sb.append(XmlTool.createAttrXml("iavailablequota", item.getIavailablequota() + ""));
                sb.append(XmlTool.createAttrXml("iskeep", item.getIskeep() + ""));
                sb.append(XmlTool.createAttrXml("importtype", importtype));
                sb.append(XmlTool.createAttrXml("itotalquota", item.getItotalquota() + ""));
                sb.append(XmlTool.createAttrXml("cardintegral", item.getIpoint() + ""));

                // add by lcs 20160121 添加还款状态字段
                sb.append(XmlTool.createAttrXml("irepayment", item.getIrepayment() + ""));
                // 银行全称
                sb.append(XmlTool.createAttrXml("fullbankname", bankDto.getCbankname()));
                //得到最新的一条消费记录
                if (!CheckUtil.isNullString(ibillid) && billMonth != null) {
                    Integer imonthid = null;
                    imonthid = billMonth.getImonthid();
                    BillDetailDto billDetail = bankImportService.getLatestBillDetailByMonthId(imonthid + "");
                    if (billDetail != null) {
                        String type = null;
                        if (billDetail.getItype() == 1) {
                            type = "出账";
                        } else {
                            type = "入账";
                        }
                        sb.append(XmlTool.createAttrXml("lastmsg", billDetail.getCtradedate() + " " + type + " " + billDetail.getImoney()));
                    }
                }
                bean.setOutsideId(item.getIoutsideid());//外部账户ID
                logger.info("getOutsideId-" + bean.getOutsideId() + "isMaillBill:" + importtype);
                if (!StringUtil.isEmpty(bean.getOutsideId()) && "1".equals(importtype)) {
                    String mailtype = CaiyiEncrypt.dencryptStr(bean.getOutsideId());
                    logger.info("mailtype-" + mailtype);
                    if (mailtype.contains("-")) {
                        bean.setMailType(mailtype.split("-")[1]);
                        sb.append(XmlTool.createAttrXml("mailtype", bean.getMailType()));
                    }
                }
                // add by lcs 20160225 增加上次更新时间
                String lastUpdate = "";
                try {
                    Date updateDate = null;

                    if (item.getCupdate() == null) {
                        updateDate = item.getCupdate();
                    }
                    if (updateDate == null) {
                        updateDate = item.getCadddate();
                    }
                    Calendar cd1 = Calendar.getInstance();
                    cd1.setTime(new Date());
                    Calendar cd2 = Calendar.getInstance();
                    cd2.setTime(updateDate);
                    int hours = ((int) (cd1.getTime().getTime() / 1000) - (int) (cd2.getTime().getTime() / 1000)) / 3600;
                    logger.info("hours:" + hours);
                    if (hours < 1) {
                        lastUpdate = "(刚刚更新)";
                    } else if (hours < 24) {
                        lastUpdate = "(" + String.valueOf(hours) + "小时前更新)";
                    } else {
                        int daysFromLastUpdate = hours / 24;
                        if (daysFromLastUpdate <= 30) {
                            lastUpdate = "(" + String.valueOf(daysFromLastUpdate) + "天前更新)";
                        } else {
                            int monthsFromLastUpdate = daysFromLastUpdate / 30;
                            if (monthsFromLastUpdate < 12) {
                                lastUpdate = "(" + String.valueOf(monthsFromLastUpdate) + "月前更新)";
                            } else {
                                lastUpdate = "(" + String.valueOf(monthsFromLastUpdate / 12) + "年前更新)";
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                sb.append(XmlTool.createAttrXml("fromlastupdate", lastUpdate));
                sb.append(" />");
            }
            if (listBbd == null || listBbd.size() <= 0) {
                bean.setBusiErrDesc("未查询到卡信息");
            } else {
                bean.setBusiErrDesc("查询成功");
            }
            bean.setBusiXml(sb.toString());
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("网络连接异常");
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            return bean;
        }
    }

    /**
     * 查询账单流水
     * 对应老接口 qCreditTransaction
     * Modified By zhaojie 2016/7/16 14:03:37
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel queryCreditTransaction(Channel bean) {
        try {
            StringBuilder sb = new StringBuilder();
            Channel cardChannel = queryUserCardinfo(bean);
            if (cardChannel != null) {
                StringBuilder CardInfo;
                if (cardChannel.getBusiXml() != null && cardChannel.getBusiXml().length() > 0) {
                    CardInfo = new StringBuilder(cardChannel.getBusiXml());
                    CardInfo.replace(1, 4, "").insert(1, "cardinfo");
                } else {
                    CardInfo = new StringBuilder("<cardinfo/>");
                }
                sb.append(CardInfo);
            }
            String billstartday = "";
            String billendday = "";
            String money = "";

            String format1 = "yyyyMMdd";
            String format2 = "MM.dd";
            String format3 = "yyyyMM";

            List<BillMonthDto> listBillMonth = bankImportService.getAllBillMonths(bean.getBillId());
            if (listBillMonth != null && listBillMonth.size() > 0) {

                for (BillMonthDto item : listBillMonth) {
                    String imonthid = item.getImonthid() + "";
                    String billdate = item.getCbilldate();
                    // add by lcs
                    billdate = resetNumerForCardInfo(billdate);
                    sb.append("<rows ");
                    if (CheckUtil.isNullString(billdate)) {
                        sb.append(XmlTool.createAttrXml("billstartday", ""));
                        sb.append(XmlTool.createAttrXml("billendday", ""));
                        logger.info("billdate 为空");
                    } else if (billdate.length() < 6) {
                        sb.append(XmlTool.createAttrXml("billstartday", ""));
                        sb.append(XmlTool.createAttrXml("billendday", ""));
                        logger.info("billdate 格式错误");
                    } else {
                        //账单开始日
                        String startDate = "";
                        BankBillDto bankBill = bankImportService.getUserBankBillById(bean.getBillId());
                        if (bankBill != null) {
                            String cbilldate = bankBill.getCbilldate();
                            // add by lcs 20160328
                            cbilldate = resetNumerForCardInfo(cbilldate);
                            if (CheckUtil.isNullString(cbilldate)) {
                                sb.append(XmlTool.createAttrXml("billstartday", ""));
                                logger.info("账单日为空");
                            } else if (Integer.parseInt(cbilldate) > 28) {
                                sb.append(XmlTool.createAttrXml("billstartday", ""));
                                logger.info("账单日" + cbilldate);
                            } else if (Integer.parseInt(cbilldate) == 28 && Integer.parseInt(billdate.substring(4, 6)) == 3) {
                                Integer year = Integer.parseInt(billdate.substring(0, 4));
                                if (!(year % 4 == 0 && year % 100 != 0 || year % 400 == 0)) {
                                    billstartday = billdate.substring(0, 6) + "01";
                                }
                            } else {
                                if (CheckUtil.isNullString(cbilldate)) {
                                    cbilldate = "01";
                                    logger.info("cbilldate 格式错误=" + cbilldate);
                                }
                                if (cbilldate.length() < 2) {
                                    cbilldate = "0" + cbilldate;
                                }
                                startDate = String.valueOf((Integer.parseInt(cbilldate) + 1));
                                Calendar cd = Calendar.getInstance();
                                Date billstartday2 = this.string2Date(billdate.substring(0, 6), format3);
                                cd.setTime(billstartday2);
                                cd.add(Calendar.MONTH, -1);
                                Date billstartday3 = cd.getTime();
                                billstartday = this.date2String(billstartday3, format3);
                                billstartday2 = this.string2Date(billstartday + startDate, format1);
                                billstartday = this.date2String(billstartday2, format2);
                                sb.append(XmlTool.createAttrXml("billstartday", billstartday));
                            }
                        }

                        //账单结束日
                        if (billdate.length() < 8) {
                            sb.append(XmlTool.createAttrXml("billendday", ""));
                            logger.info("billdate 格式错误");
                        } else {
                            Date billendday2 = this.string2Date(billdate, format1);
                            billendday = this.date2String(billendday2, format2);
                            sb.append(XmlTool.createAttrXml("billendday", billendday));
                        }
                    }

                    String month = item.getCmonth();
                    month = resetNumerForCardInfo(month);
                    if (CheckUtil.isNullString(month)) {
                        logger.info("月份为空");
                        sb.append(XmlTool.createAttrXml("month", ""));
                    } else if (month.length() < 2) {
                        logger.info("月份格式错误");
                        sb.append(XmlTool.createAttrXml("month", ""));
                    } else {
                        month = month.substring(month.length() - 2);
                        sb.append(XmlTool.createAttrXml("month", month));
                    }

                    String paymentday = item.getCrepaymentdate();
                    paymentday = resetNumerForCardInfo(paymentday);
                    if (CheckUtil.isNullString(paymentday)) {
                        logger.info("还款日为空");
                        sb.append(XmlTool.createAttrXml("paymentday", ""));
                    } else if (paymentday.length() != 8) {
                        logger.info("还款日格式错误");
                        sb.append(XmlTool.createAttrXml("paymentday", ""));
                    } else {
                        Date paymentday2 = this.string2Date(paymentday, format1);
                        paymentday = this.date2String(paymentday2, format2);
                        sb.append(XmlTool.createAttrXml("paymentday", paymentday));
                    }

                    sb.append(XmlTool.createAttrXml("money", item.getIshouldrepayment() + ""));

                    sb.append(" >");

                    if (imonthid != null) {
                        List<BillDetailDto> listBillDetail = bankImportService.getBillDetailsByMonthId(imonthid);
                        if (listBillDetail != null && listBillDetail.size() > 0) {
                            for (BillDetailDto billDetail : listBillDetail) {
                                sb.append("<row ");
                                //Modified By zhaojie 2016/9/6 10:48:51
                                //日期格式统一：10-25
                                //10-25，1025，2016/10/25,2016-10-25
                                String tradeDay = billDetail.getCtradedate();
                                try {
                                    if (tradeDay != null && !tradeDay.equals("")) {
                                        if (tradeDay.contains("/")) {
                                            tradeDay =this.date2String(this.string2Date(tradeDay,"yyyy/MM/dd"),"MM-dd");
                                        }else if(tradeDay.length() == 4){
                                            tradeDay =this.date2String(this.string2Date(tradeDay,"MMdd"),"MM-dd");
                                        }else if(tradeDay.length() == 10){
                                            tradeDay =this.date2String(this.string2Date(tradeDay,"yyyy-MM-dd"),"MM-dd");
                                        }
                                    }
                                }catch (Exception e){
                                    logger.error("日期格式转换错误");
                                }

                                sb.append(XmlTool.createAttrXml("day", tradeDay));
                                sb.append(XmlTool.createAttrXml("billdesc", billDetail.getCdesc()));

                                money = billDetail.getImoney() + "";
                                if (!CheckUtil.isNullString(money)) {
                                    if (money.contains("-")) {
                                        money = money.substring(1);
                                    } else {
                                        money = "-" + money;
                                    }
                                }

                                sb.append(XmlTool.createAttrXml("money", money));
                                //消费类型
                                String icostid = billDetail.getIcosttype() + "";
                                String costTemp = bankImportService.getCostTypeName(icostid);
                                String costtype = "其他";
                                if (costTemp != null) {
                                    costtype = costTemp;
                                }
                                sb.append(XmlTool.createAttrXml("costtype", costtype));
                                sb.append(" />");
                            }
                        }
                    }
                    sb.append("</rows>");
                }
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("已查询");
                bean.setBusiXml(sb.toString());
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("未查询月份信息");
                // 未查询到月份信息  只返回卡信息 add by lcs 20160122
                bean.setBusiXml(sb.toString());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询失败");
            logger.error("queryCreditTransaction异常", e);
            e.printStackTrace();
        } finally {
            return bean;
        }
    }

    /**
     * 删除账单（账单改为已删除状态）
     * 旧接口移植 deleteBill
     * Modified By zhaojie 2016/7/18 11:52:11
     *
     * @param bean
     * @return
     */
    @BoltController
    public Channel deleteBill(Channel bean) {
        try {
            int res = bankImportService.deleteBankBill(bean.getBillId());
            if (res == 1) {
                messageService.deleteMessageByBillId(bean.getCuserId(),bean.getBillId());
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("删除成功");
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("删除异常");
            }
        } catch (Exception e) {
            bean.setBusiErrCode(5);
            bean.setBusiErrDesc("删除异常");
        } finally {
            return bean;
        }
    }

    /**
     * 标记账单已还款
     * 旧接口移植 changeRepaymentStatus
     * Modified By zhaojie 2016/7/18 11:52:18
     *
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject changeRepaymentStatus(Channel bean) {
        JSONObject json=new JSONObject();
        logger.info("changeRepaymentState start " + bean.getBillId() + ",还款状态=" + bean.getRepaymentStatus());
        if (CheckUtil.isNullString(bean.getBillId())) {
            json.put("code",-1);
            json.put("desc","非法的参数");
            return json;
        }
        try {
            int ret = bankImportService.changeRepaymentStatus(bean.getBillId(), bean.getRepaymentStatus());
            if (ret == 1) {
                json.put("code",1);
                json.put("desc","状态更新成功");
                logger.info("changeRepaymentState" + bean.getBillId() + "更新成功");
            } else {
                json.put("code",0);
                json.put("desc","状态更新失败");
                logger.info("changeRepaymentState" + bean.getBillId() + "更新失败");
            }
            return json;
        } catch (Exception e) {
            logger.error("changeRepaymentState异常={}", e.toString());
            json.put("code",-1);
            json.put("desc","操作异常");
            return json;
        }
    }

    /**********************************************************
     * 以下方法搬运自老工程
     *
     * @param item
     * @return
     */
    private String resetNumerForCardInfo(String item) {
        String newNumericStr = "";

        if (!CheckUtil.isNullString(item)) {
            newNumericStr = item.replaceAll("\\D", "");
            if (!isNumeric(newNumericStr)) {
                newNumericStr = "0";
            } else {
                if (item.length() == 9 && "00".equals(item.substring(6, 8))) {
                    newNumericStr = item.substring(0, 6) + item.substring(7, 9);
                }
            }
        }
        return newNumericStr;
    }

    private boolean isNumeric(String str) {
        if (CheckUtil.isNullString(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //yyyy-MM-dd HH:mm:ss
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    //oracle数据库使用的时间格式
    public static final String ORACLE_FORMAT = "yyyy-mm-dd hh24:mi:ss";

    /**
     * 格式化时间
     *
     * @param date
     * @param format
     * @return
     */
    public String date2String(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * 将字符串转换为时间
     *
     * @param str
     * @param format
     * @return
     * @throws ParseException
     */
    public Date string2Date(String str, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(str);
    }

    // 网银导入账单配置文件地址
    private final static String BANK_CONFIG_XML_PATH = "http://www.huishuaka.com/busi/bank/BankListNew.xml";

    private Element cardSortedByDate(Element resp) {
        logger.info("卡列表按还款日排序");
        Element oldResp = resp;
        resp = new DOMElement("Resp");
        try {
            Document xmlWap = XmlTool.stringToXml(oldResp.asXML());
            List<Element> nodeList = xmlWap.getRootElement().elements("row");
            logger.info("第一次排序");
            Collections.sort(nodeList, new Comparator<Element>() {
                public int compare(Element arg0, Element arg1) {
                    int ret = -1;
                    String argStr0 = arg0.attributeValue("paymentdays");
                    String argStr1 = arg1.attributeValue("paymentdays");
                    if (CheckUtil.isNullString(argStr0)) {
                        argStr0 = "0";
                    }
                    if (CheckUtil.isNullString(argStr1)) {
                        argStr1 = "0";
                    }
                    String argStr2 = arg0.attributeValue("tobilldays");
                    String argStr3 = arg1.attributeValue("tobilldays");
                    if (Integer.valueOf(argStr0) < Integer.valueOf(argStr1)) {
                        ret = 1;
                    }
                    if (!"0".equals(argStr0) && !"0".equals(argStr1) && Integer.valueOf(argStr0) > Integer.valueOf(argStr1)) {
                        ret = 1;
                    }
                    if ("0".equals(argStr0) && "0".equals(argStr1)) {
                        if (Integer.valueOf(argStr2) < Integer.valueOf(argStr3)) {
                            ret = 1;
                        }
                    }
                    return ret;
                }
            });
            logger.info("第二次排序");
            Collections.sort(nodeList, new Comparator<Element>() {
                public int compare(Element arg0, Element arg1) {
                    int ret = 0;
                    String argStr0 = arg0.attributeValue("paymentdays");
                    String argStr1 = arg1.attributeValue("paymentdays");
                    if (CheckUtil.isNullString(argStr0)) {
                        argStr0 = "0";
                    }
                    if (CheckUtil.isNullString(argStr1)) {
                        argStr1 = "0";
                    }
                    String argStr2 = arg0.attributeValue("tobilldays");
                    String argStr3 = arg1.attributeValue("tobilldays");
                    if ("0".equals(argStr0) && "0".equals(argStr1)) {
                        if (Integer.valueOf(argStr2) > 0 && Integer.valueOf(argStr3) > 0 && Integer.valueOf(argStr2) > Integer.valueOf(argStr3)) {
                            ret = 1;
                        }
                        if (Integer.valueOf(argStr2) > 0 && Integer.valueOf(argStr3) > 0 && Integer.valueOf(argStr2) < Integer.valueOf(argStr3)) {
                            ret = -1;
                        }
                    }
                    return ret;
                }
            });

            StringBuilder sbBank = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            HashMap<String, String> bankIdMap = new HashMap<String, String>();

            // 获取并缓存银行导入信息
            Calendar cd = Calendar.getInstance();
            cd.setTime(new Date());
            int hourOfDay = cd.get(Calendar.HOUR_OF_DAY);
            String ccBankXml = "BankListNew" + hourOfDay;
            logger.info("缓存key:" + ccBankXml);
            Object xmlObj = cc == null ? null : cc.get(ccBankXml);
            List<Element> bankNodeList = new ArrayList<Element>();
            if (xmlObj != null) {
                bankNodeList = (ArrayList<Element>) xmlObj;
                logger.info("获取缓存成功");
            } else {
                logger.info("采用在线地址www.huishuaka.com");
                Document bankXmlWap = XmlTool.read(new URL(BANK_CONFIG_XML_PATH), "utf-8");
                bankNodeList = bankXmlWap.getRootElement().elements("bank");
                boolean ret = cc == null ? false : cc.set(ccBankXml, bankNodeList, 60 * 60 * 3600);
                if (ret) {
                    logger.info("放置缓存成功" + bankNodeList.size());
                } else {
                    logger.info("放置缓存失败");
                }
            }

            for (Element sortedNode : nodeList) {
                //生成<row />
                sb = new StringBuilder();
                String xmlStr = sortedNode.asXML();
                sb.append(xmlStr.substring(xmlStr.indexOf("<row")));
                Document rowDom = DocumentHelper.parseText(sb.toString());
                resp.add(rowDom.getRootElement());

                //生成<bank></bank>
                String bankname = sortedNode.attributeValue("fullbankname");
                if (!bankIdMap.containsKey(bankname)) {
                    setBankConfig(bankname, sbBank, bankNodeList);
                    bankIdMap.put(bankname, "1");
                }
            }
            String bankListXml = ("<bankList>  " + sbBank + "</bankList>");
            Document bankListDom = DocumentHelper.parseText(bankListXml);
            resp.add(bankListDom.getRootElement());

            logger.info("卡列表按还款日排序成功");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("卡列表排序失败" + e);
            return oldResp;
        }
    }

    // 附加导入配置信息
    private void setBankConfig(String fullBankName, StringBuilder sb, List<Element> bankNodeList) {
        try {
            logger.info("附加导入配置信息");
            if (!CheckUtil.isNullString(fullBankName)) {
                for (Element xmlNode : bankNodeList) {
                    Element bankNameNode = xmlNode.element("bankName");
                    String xmlBankName = bankNameNode.getText();
                    if (fullBankName.equals(xmlBankName)) {
                        String xmlStr = xmlNode.asXML();
                        sb.append(xmlStr.substring(xmlStr.indexOf("<bank")));
                    }
                }
            }
            logger.info("附加导入配置信息成功");
        } catch (Exception e1) {
            e1.printStackTrace();
            logger.error("附加导入配置信息失败", e1);
        }
    }

    /**
     * 新版本卡管理页面卡列表
     *
     * @param bean
     * @return created by lcs 20160821
     */
    @BoltController
    public JSONObject billIndex(Channel bean) {
        List<Bill> bills = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        String desc = "查询成功";
        String code = "1";
        try {
            String uid = bean.getCuserId();
            String billid = bean.getBillId();
            logger.info("uid:" + bean.getCuserId() + ",billid:" + bean.getBillId());
            // billId 不为空 则只返回对应账单信息   否则,返回该用户名下全部卡
            if (CheckUtil.isNullString(billid)) {
                bills = billService.getUserBankBillByUser(uid);
            } else {
                Bill bill = billService.getUserBankBillByBillId(billid, uid);
                if (bill != null && !CheckUtil.isNullString(bill.getShouldpayment())) {
                    bills.add(bill);
                }
            }
            List<Bill> repayedBills = new ArrayList<>();
            List<Bill> norepayBills = new ArrayList<>();
            for(Bill bill:bills){
                //是否已还款标识 0:未还 1:已还
                String repayment = bill.getRepayment();
                if("0".equals(repayment)){
                    norepayBills.add(bill);
                }else{
                    repayedBills.add(bill);
                }
            }
            //未还列表和已还列表排序
            Collections.sort(norepayBills, new BillComparator());
            Collections.sort(repayedBills, new BillComparator());
            //列表合并,未还列表排在前
            bills.clear();
            bills.addAll(norepayBills);
            bills.addAll(repayedBills);
            logger.info(bills == null || bills.size() == 0 ? "未取到数据" : "成功" + bills.size());
        } catch (Exception e) {
            logger.error("billIndex:", e);
            code = "0";
            desc = "系统异常";
        }
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("data", bills);
        return jsonObject;
    }
    // 自定义比较器：按距离还款日,距离账单日的大小排序
    static class BillComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Bill b1 = (Bill) object1; // 强制转换
            Bill b2 = (Bill) object2;
            //先排距离还款日
            int ipaymentdays1 = Integer.valueOf(b1.getPaymentdays());
            int ipaymentdays2 = Integer.valueOf(b2.getPaymentdays());
            if (ipaymentdays1 != ipaymentdays2) {
                return ipaymentdays1 - ipaymentdays2;
            } else {
                //距离还款日相同，再排距离账单日
                int ibilldays1 = Integer.valueOf(b1.getBilldays());
                int ibilldays2 = Integer.valueOf(b2.getBilldays());
                if(ibilldays1 != ibilldays2){
                    return ibilldays1 - ibilldays2;
                }else{
                    int ibillid1 = Integer.valueOf(b1.getBillid());
                    int ibillid2 = Integer.valueOf(b2.getBillid());
                    return ibillid1 - ibillid2;
                }
            }
        }
    }
    /**
     * 修改账单日 尾号等信息
     *
     * @param bean
     * @return
     */

    @BoltController
    public JSONObject updateBillInfo(Channel bean) {
        JSONObject jsonObject = new JSONObject();
        int result = 1;
        jsonObject.put("code", "1");
        jsonObject.put("desc", "修改成功");
        try {
            result = billService.updateBillInfo(bean);

        } catch (Exception e) {
            logger.error("updateBillInfo:", e);
            result = 0;
        }
        if (result != 1) {
            jsonObject.put("code", "0");
            jsonObject.put("desc", "修改失败");
        }
        return jsonObject;
    }
}
