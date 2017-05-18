package com.caiyi.financial.nirvana.ccard.bill.bank.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.bill.bank.mapper.BankMapper;
import com.caiyi.financial.nirvana.ccard.bill.bank.mapper.BillMapper;
import com.caiyi.financial.nirvana.ccard.bill.bean.Bill;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.*;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.mock.BankDeployByFile;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lichuanshun on 16/8/17.
 * 卡管理相关服务
 */
@Service
public class BillService extends AbstractService {
    @Autowired
    BankMapper mapper;
    @Autowired
    BillMapper billMapper;

    /**
     * 根据userId 获取用户卡数据
     *
     * @param userId
     * @return
     */
    public List<Bill> getUserBankBillByUser(String userId) {
        try {
            List<BankBillDto> bills = mapper.getUserBankBillByUser(userId);
            logger.info("getUserBankBillByUser:", bills.size());
            return convertDto2Bill(bills);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getUserBankBillByUser 异常", e);
        }
        return null;
    }

    /**
     * 根据Billid获取Bill
     *
     * @param billId
     * @return
     */
    public BankBillDto getUserBankBillById(String billId) {
        try {
            return mapper.getUserBankBillById(billId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getUserBankBillById 异常", e);
        }
        return null;
    }

    /**
     * 根据Billid获取Bill
     *
     * @param billId
     * @return
     */
    public Bill getUserBankBillByBillId(String billId, String uid) {
        try {
            return convertDto2Bill(mapper.getUserBankBillByBillId(billId, uid));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getUserBankBillById 异常", e);
        }
        return null;
    }

    /**
     * 修改账单信息
     *
     * @return
     */
    public int updateBillInfo(Channel channel) {
        try {
            logger.info("uid=" + channel.getCuserId());
            logger.info("billid=" + channel.getBillId());
            logger.info("username=" + channel.getUsername());
            logger.info("cash=" + channel.getCashAmount());
            logger.info("card4num=" + channel.getCard4Num());
            return billMapper.updateBillInfo(channel.getUsername(), channel.getCard4Num(), channel.getCashAmount(), channel.getBillId(), channel.getCuserId());
        } catch (Exception e) {
            logger.error("updateBillInfo 异常", e);
        }
        return 0;
    }


    /**
     * @param bills
     * @return
     */
    private List<Bill> convertDto2Bill(List<BankBillDto> bills) {
        List<Bill> billList = new ArrayList<>();
        try {
            for (BankBillDto billDto : bills) {
                billList.add(convertDto2Bill(billDto));
            }
        } catch (Exception e) {
            logger.error("convertDto2Bill:", e);
        }
        return billList;
    }

    /**
     * @param bankBillDto
     * @return
     */
    private Bill convertDto2Bill(BankBillDto bankBillDto) {
        Bill bill = new Bill();
        try {
            logger.info(JSON.toJSONString(bankBillDto));
            // 账单id
            bill.setBillid(String.valueOf(bankBillDto.getIbillid()));
            // 银行id
            bill.setBankid(String.valueOf(bankBillDto.getIbankid()));
            // bankname
            String cardnum = bankBillDto.getIcard4num();
            BankDto bankDto = mapper.getBankById(String.valueOf(bankBillDto.getIbankid()));
            String dispName = bankDto.getCbankname();
            bill.setBankname(dispName);
            bill.setCard4num(cardnum);
            // 剩余最后还款日 小于0表示超出还款日期
            bill.setPaymentdays(calculateDate(bankBillDto.getCrepaymentdate()));
            // 剩余出账单日
            bill.setBilldays(calculateDate(bankBillDto.getCbilldate()));
            // 账单日
            bill.setBilldate(getCurrentBillDate(bankBillDto.getCbilldate()));
            // 还款日
            bill.setPaymentdate(getCurrentBillDate(bankBillDto.getCrepaymentdate()));
            // 未出账单金额
            if (bankBillDto.getInobillamount() != null) {
                bill.setUnsettledbill(String.valueOf(bankBillDto.getInobillamount()));
            } else {
                bill.setUnsettledbill("0");
            }
            // 应还金额
            bill.setShouldpayment(String.valueOf(bankBillDto.getIshouldrepayment()));
            // 可用额度
            bill.setAvailablebalance(String.valueOf(bankBillDto.getIavailablequota() == null ? "" : bankBillDto.getIavailablequota()));
            // 总额度
            bill.setBalance(String.valueOf(bankBillDto.getItotalquota() == null ? "0" : bankBillDto.getItotalquota()));
            // 导入类型 邮箱还是网银 0:网银 1:邮箱
            bill.setImporttype(String.valueOf(bankBillDto.getIswebormail()));
            // 邮箱类型
            String outSideId = bankBillDto.getIoutsideid();
            if ("1".equals(bill.getImporttype()) && !CheckUtil.isNullString(outSideId)) {
                String mailAndType = CaiyiEncrypt.dencryptStr(outSideId);
                if (!CheckUtil.isNullString(mailAndType) && mailAndType.contains("-")) {
                    bill.setMailtype(mailAndType.split("-")[1]);
                }
            }
            // 是否已还款标记：用户在前端设置 默认未还 0:未还 1:已还
//            bill.setRepayment(String.valueOf(bankBillDto.getIrepayment()));

            Integer irepayment = mapper.queryIrepaymentOfMaxMonthByBillid(bankBillDto.getIbillid());
            logger.info("ibillid={},repayment={}", bankBillDto.getIbillid(), irepayment);
            if (irepayment != null) {
                bill.setRepayment(String.valueOf(irepayment));
            } else {
                bill.setRepayment("0");
            }
            // TODO
            //  主卡副卡标记 1:主卡 2:副卡
            bill.setPrincipal("1");
            // 距上一次更新时间
            int hours = getHours(bankBillDto.getCupdate() != null ? bankBillDto.getCupdate() : bankBillDto.getCadddate());
            bill.setFromlastupdate(getLastUpdate(hours));
            // 是否更新标记：用于账单长久不更新提示用户更新
            bill.setUpdateflag("0");
            if (hours > 72) {
                bill.setUpdateflag("1");
            }
            // 是否支持网银导入 1表示支持 0不支持
            bill.setSupportebank(isSupportEbank(bill.getBankid()));
            // 最低应还金额
            bill.setMinshouldpayment(String.valueOf(bankBillDto.getIlowestrepayment()));
            //免息期
            bill.setFreedays(calculateFreeDays(bankBillDto.getCbilldate(), bankBillDto.getCrepaymentdate()));
            //积分
            bill.setIntegration(String.valueOf(bankBillDto.getIpoint()));
            // 真实姓名
            bill.setRealname(bankBillDto.getUname());
            //
            bill.setCash(String.valueOf(bankBillDto.getIcashamount() == null ? "0.0" : bankBillDto.getIcashamount()));

        } catch (Exception e) {
            logger.error("convertDto2Bill:", e);
            bill = null;
        }
        return bill;
    }

    /**
     * 根据账单日 获取最近的处长日期 如账单日:08 则返回08.08
     *
     * @param billDay (format:08)
     * @return
     */
    private String getCurrentBillDate(String billDay) {
        String currentDay = cleanNum(billDay);
        try {
            Calendar nowCal = Calendar.getInstance();
            int nowDay = nowCal.get(Calendar.DAY_OF_MONTH);
            logger.info("billDay:" + currentDay + ",nowDay:" + nowDay);
            if (Integer.valueOf(currentDay) < nowDay) {
                nowCal.add(Calendar.MONTH, 1);
            }
            logger.info("MONTH:" + nowCal.get(Calendar.MONTH));
            int month = nowCal.get(Calendar.MONTH) + 1;
            if (month < 10) {
                currentDay = "0" + String.valueOf(month) + "." + currentDay;
            } else {
                currentDay = String.valueOf(month) + "." + currentDay;
            }
        } catch (Exception e) {
            logger.error("getCurrentBillDate" + billDay, e);
        }
        return currentDay;
    }

    /**
     * 清理数字数据中的非法字符
     *
     * @param num
     * @return
     */
    private String cleanNum(String num) {
        String newNum = num;
        try {
            if (!CheckUtil.isNullString(num)) {
                newNum = num.replaceAll("[^0-9.]", "");
            }
        } catch (Exception e) {
            logger.error("checkNum:", e);
        }
        return newNum;
    }

    /**
     * 时间计算
     *
     * @param date
     * @return
     */
    private String calculateDate(String date) {
        String result = "0";
        try {
            if (Integer.parseInt(date) > 0) {
                Calendar cdNow = Calendar.getInstance();
                Calendar cd = Calendar.getInstance();
                cd.set(Calendar.DAY_OF_MONTH, Integer.valueOf(cleanNum(date)));
                int days = ((int) (cd.getTime().getTime() / 1000) - (int) (cdNow.getTime().getTime() / 1000)) / 3600 / 24;
                if (days < 0) {
                    cd.add(Calendar.MONTH, 1);
                    days = ((int) (cd.getTime().getTime() / 1000) - (int) (cdNow.getTime().getTime() / 1000)) / 3600 / 24;
                }
                result = String.valueOf(days);
            }
        } catch (Exception e) {
            logger.error("calculateDate:", e);
        }
        return result;
    }

    /**
     * 计算距离上次更新时间
     *
     * @param date
     * @return
     */
    private int getHours(Date date) {
        int hours = 0;
        if (date == null) {
            return 0;
        }
        try {
            logger.info("date:" + date.toString());
            Calendar cdNow = Calendar.getInstance();
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            hours = ((int) (cdNow.getTime().getTime() / 1000) - (int) (cd.getTime().getTime() / 1000)) / 3600;
            logger.info("hours:" + hours);
        } catch (Exception e) {
            logger.error("getHours:", e);
        }
        return hours;
    }

    /**
     * 计算距离上次更新时间
     *
     * @param hours
     * @return
     */
    private String getLastUpdate(int hours) {
        String fromlastupdate = "--";
        try {
            if (hours < 1) {
                fromlastupdate = "刚刚更新";
            } else if (hours < 24) {
                fromlastupdate = String.valueOf(hours) + "小时前更新";
            } else {
                int daysFromLastUpdate = hours / 24;
                if (daysFromLastUpdate <= 30) {
                    fromlastupdate = daysFromLastUpdate + "天前更新";
                } else {
                    int monthsFromLastUpdate = daysFromLastUpdate / 30;
                    if (monthsFromLastUpdate < 12) {
                        fromlastupdate = monthsFromLastUpdate + "月前更新";
                    } else {
                        fromlastupdate = monthsFromLastUpdate / 12 + "年前更新";
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getLastUpdate:", e);
        }
        return fromlastupdate;
    }

    /**
     * 计算免息天数
     *
     * @param billDate
     * @param paymentDate
     * @return
     */
    private String calculateFreeDays(String billDate, String paymentDate) {
        String freeDays = "50";
        if (CheckUtil.isNullString(billDate) || CheckUtil.isNullString(paymentDate)) {
            return freeDays;
        }
        try {
            Calendar lastMonthOfBillDate = Calendar.getInstance();
            lastMonthOfBillDate.set(Calendar.DAY_OF_MONTH, Integer.valueOf(billDate.trim()) - 1);
            Calendar cdBill = Calendar.getInstance();
            cdBill.set(Calendar.DAY_OF_MONTH, Integer.valueOf(billDate.trim()));
            Calendar cdPayment = Calendar.getInstance();
            if (Integer.valueOf(paymentDate) < Integer.valueOf(billDate)) {
                cdPayment.add(Calendar.MONTH, 1);
            }
            cdPayment.set(Calendar.DAY_OF_MONTH, Integer.valueOf(paymentDate.trim()));
            int days = ((int) (cdPayment.getTime().getTime() / 1000) - (int) (cdBill.getTime().getTime() / 1000)) / 3600 / 24;
            freeDays = String.valueOf(days + 31);
        } catch (Exception e) {
            logger.error("calculateFreeDays:", e);
        }
        return freeDays;

    }

    /**
     * @param bankId
     * @return 0:不支持  1:支持
     */
    private String isSupportEbank(String bankId) {
        String isSupport = "0";
        try {
            BankDeployByFile bankDeployByFile = new BankDeployByFile();
            String res = bankDeployByFile.readBankConfig(bankId);
            JSONObject bankJson = JSON.parseObject(res);
            JSONObject data = bankJson.getJSONObject("data");
            String accuntType = data.getJSONArray("bankList").getJSONObject(0).getString("accountType");
            logger.info("isSupportEbank bankId:" + bankId + "accuntType:" + accuntType);
            if (data != null && !CheckUtil.isNullString(accuntType)) {
                isSupport = "1";
            }
        } catch (Exception e) {
            logger.error("isSupportEbank", e);
        }
        return isSupport;
    }

    public static void main(String[] args) {
        BillService service = new BillService();

//        System.out.println(service.calculateFreeDays("17","06"));

//        Channel channel = new Channel();
//        channel.setCuserId("797666a0104");
//        channel.setBillId("3482");
//        channel.setUsername("lcstest");
//        channel.setCashAmount("9188");
//        channel.setCard4Num("11234");

//        service.updateBillInfo(channel);
//        System.out.println(service.getCurrentBillDate("06"));

//        String test = "3";
//        System.out.println(test !=null?test:"22");


        System.out.println("calStartDate" + service.calStartDate(""));
//        System.out.println(service.getFormatDate("2016/01/17"));

    }

    /**
     * 账单流水
     *
     * @param channel
     * @return
     */
    public BoltResult queryBillStream(Channel channel) {
        BoltResult result = new BoltResult("1", "success");

        try {
            //查询最近的六个月
            List<BillMonthDto> billMonthDtos = billMapper.queryLatestBillMonth(channel.getBillId());
            if (billMonthDtos != null && billMonthDtos.size() > 0) {
                JSONArray data = new JSONArray();
                for (BillMonthDto billMonthDto : billMonthDtos) {
                    JSONObject monthlyBill = new JSONObject();
                    //当前月份
                    monthlyBill.put("month", billMonthDto.getCmonth().substring(4, 6));
                    //本期应还额度
                    monthlyBill.put("money", getFormatMoney(billMonthDto.getIshouldrepayment()));
                    String billDate = billMonthDto.getCbilldate();
                    if (StringUtils.isNotEmpty(billDate)) {
                        String endDate = getFormatDate(billDate);
                        //账单结束日期
                        monthlyBill.put("endDate", endDate);
                        //账单开始日期
                        monthlyBill.put("startDate", calStartDate(endDate));
                    }
                    //  0：未知，1：已还，2：未还，3：未出
                    if (billMonthDto.getIsbill() == 0) {
                        monthlyBill.put("status", "3");
                    } else {
                        monthlyBill.put("status", "1");
                    }
                    //每月账单流水
                    List<BillDetailDto> billDetailDtos = new ArrayList<>();
                    String regex = "\\d{4}01";
                    if (billMonthDto.getCmonth().matches(regex)) {
                        //针对跨年的月账单流水进行特殊处理
                        billDetailDtos = billMapper.queryMonthlyBillSpecial(billMonthDto.getImonthid());
                    } else {
                        billDetailDtos = billMapper.queryMonthlyBill(billMonthDto.getImonthid());
                    }
                    for (BillDetailDto billDetailDto : billDetailDtos) {
                        if (billDetailDto.getCtradedate() == null) {
                            billDetailDto.setCtradedate(getFormatDate(billDetailDto.getCoccurdate()));
                        } else {
                            billDetailDto.setCtradedate(getFormatDate(billDetailDto.getCtradedate()));
                        }
                        billDetailDto.setImoney(getFormatMoney(Double.parseDouble(billDetailDto.getImoney())));
                    }
                    monthlyBill.put("bills", billDetailDtos);
                    data.add(monthlyBill);
                }
                result.setData(data);
            } else {
                result.setDesc("此用户最近6个月无账单流水");
            }
            logger.info("queryBillStream———billId:" + channel.getBillId() + "——result:" + JSON.toJSON(result));
        } catch (Exception e) {
            logger.error("queryBillStream:", e);
        }
        return result;
    }

    /**
     * 转换日期格式  固定转换为 08/08
     *
     * @return
     */
    public String getFormatDate(String dateStr) {
        if (StringUtils.isNotBlank(dateStr)) {
            dateStr = dateStr.replaceAll("[\u4e00-\u9fa5]+", "");//去除汉字
            String pat1 = "\\d{8}";//YYYYMMDD
            String pat2 = "\\d{2}-\\d{2}";//MM-DD
            String pat3 = "\\d{4}/\\d{2}/\\d{2}";//YYYY/MM/DD
            if (dateStr.matches(pat1)) {
                return new StringBuilder(dateStr.substring(4, 8)).insert(2, "/").toString();
            } else if (dateStr.matches(pat2)) {
                return dateStr.replace("-", "/");
            } else if (dateStr.matches(pat3)) {
                return new StringBuilder(dateStr.substring(5, 10)).toString();
            }
        }
        return "";
    }

    /**
     * 转换money格式
     */
    public String getFormatMoney(Double money) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return money == null ? "0.00" : df.format(money);
    }


    /**
     * 计算账单开始日期
     */
    public String calStartDate(String dateStr) {
        if (dateStr != null && dateStr != "") {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(dateStr));
                calendar.add(Calendar.MONTH, -1);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } catch (Exception e) {
                logger.info("计算账单开始日期出错" + e);
            }
            return sdf.format(calendar.getTime());
        } else {
            return dateStr;
        }
    }


    /**
     * 消费类型分析
     *
     * @param channel
     * @return
     */
//    public BoltResult queryConsumeType(Channel channel) {
//        BoltResult result = new BoltResult("1", "success");
//        try {
//            //查询最近消费的六个月
//            List<BillMonthDto> billMonthDtos = billMapper.queryLatestBillMonth(channel.getBillId());
//            if (billMonthDtos != null && billMonthDtos.size() > 0) {
//                //查询总消费占比————开始
//                List<BillConsumeAnalysisDto> totalConsumeBill = billMapper.queryTotalConsumeBill(channel.getBillId());
//                JSONObject data = new JSONObject();
//                //add by Lin 2017/01/18 添加消费时间段
//                Map<String,String> monthTime =  billMapper.queryMonthTime(channel.getBillId());
//                String startMonth = monthTime.get("STARTMONTH");
//                String endMonth = monthTime.get("ENDMONTH");
//                String headerTime = "";
//                 if (startMonth != null && startMonth != "" && endMonth != null && endMonth != ""){
//                     StringBuffer sb = new StringBuffer();
//                     sb.append(startMonth.substring(4,startMonth.length())).append("/").append(startMonth.substring(0,4))
//                             .append("-").append(endMonth.substring(4, startMonth.length())).append("/").append(endMonth.substring(0, 4));
//                     headerTime = sb.toString();
//                 }
//                data.put("headerTime", headerTime);
//                //add by Lin ===END
//                for (BillConsumeAnalysisDto bDto : totalConsumeBill) {
//                    bDto.setMoney(getFormatMoney(Double.valueOf(bDto.getMoney())));//money格式转换
//                }
//
//                int size = totalConsumeBill.size();
//                if (size <= 4) {
//                    BillConsumeAnalysisDto normalConsumeBillDto = billMapper.queryNormalConsumeBill(channel.getBillId());
//                    if (normalConsumeBillDto != null) {
//                        normalConsumeBillDto.setMoney(getFormatMoney(Double.valueOf(normalConsumeBillDto.getMoney())));
//                        totalConsumeBill.add(normalConsumeBillDto);
//                    }
//                    data.put("header", totalConsumeBill);
//                } else {
//                    JSONArray newTotalConsumeBill = new JSONArray();
//                    newTotalConsumeBill.add(totalConsumeBill.get(0));
//                    newTotalConsumeBill.add(totalConsumeBill.get(1));
//                    newTotalConsumeBill.add(totalConsumeBill.get(2));
//                    newTotalConsumeBill.add(totalConsumeBill.get(3));
//                    BillConsumeAnalysisDto normalConsume = new BillConsumeAnalysisDto();
//                    Double count = 0.00;
//                    for (int i = 4; i < size; i++) {
//                        count += Double.valueOf(totalConsumeBill.get(i).getMoney());//剩余的消费类型金额累加
//                    }
//                    BillConsumeAnalysisDto normalConsumeBillDto = billMapper.queryNormalConsumeBill(channel.getBillId());
//                    if (normalConsumeBillDto != null) {
//                        count += Double.valueOf(normalConsumeBillDto.getMoney());
//                    }
//                    if (count > 0) {
//                        normalConsume.setConsumeType("一般");
//                        normalConsume.setConsumeTypeId(1);
//                        normalConsume.setMoney(getFormatMoney(count));//money格式转换
//                        newTotalConsumeBill.add(normalConsume);
//                    }
//                    data.put("header", newTotalConsumeBill);
//                }
//                //查询总消费占比————结束
//
//                //查询消费分析————开始
//                JSONObject consumeBill = new JSONObject();
//                JSONArray billArr = new JSONArray();
//                for (BillMonthDto billMonthDto : billMonthDtos) {
//                    BillConsumeTypeDto bDto = new BillConsumeTypeDto();
//                    Double money = 0.00;
//                    bDto.setMonth(billMonthDto.getCmonth().substring(4, 6));
//                    // TODO: 2016/12/23
//                    bDto.setRepayStatus("1");
//                    List<BillConsumeAnalysisDto> billDtos = billMapper.queryConsumeBillByMonthId(billMonthDto.getImonthid());
//                    for (BillConsumeAnalysisDto billDto : billDtos) {
//                        billDto.setMoney(getFormatMoney(Double.valueOf(billDto.getMoney())));
//                        money += Double.valueOf(billDto.getMoney());
//                    }
//                    bDto.setMoney(getFormatMoney(money));
//                    bDto.setBills(billDtos);
//                    if (billMonthDto.getIsbill() == 0) {
//                        //未出消费
//                        consumeBill.put("unsettled", bDto);
//                    } else {
//                        billArr.add(bDto);
//                    }
//                }
//                if (billArr != null && billArr.size() > 0) {
//                    //本期消费
//                    consumeBill.put("current", billArr.remove(0));
//                    if (billArr.size() > 0) {
//                        //过往消费
//                        consumeBill.put("last", billArr);
//                    }
//                }
//                //查询消费分析————结束
//                data.put("list", consumeBill);
//                result.setData(data);
//            } else {
//                result.setDesc("此用户无历史消费记录");
//            }
//            logger.info("queryConsumeType———billId:" + channel.getBillId() + "——result:" + JSON.toJSON(result));
//        } catch (NumberFormatException e) {
//            logger.info("queryConsumeType", e);
//        }
//        return result;
//    }

    /**
     * 消费类型分析(新接口)
     *
     * @param channel
     * @return
     */
    public BoltResult queryConsumeType(Channel channel) {
        BoltResult result = new BoltResult("1", "success");
        try {
            //查询最近消费的六个月
            List<BillMonthDto> billMonthDtos = billMapper.queryLatestBillMonth(channel.getBillId());
            if (billMonthDtos != null && billMonthDtos.size() > 0) {
                //查询总消费占比————开始
                JSONObject data = new JSONObject();
                //add by Lin 2017/01/18 添加消费时间段
                Map<String, String> monthTime = billMapper.queryMonthTime(channel.getBillId());
                String startMonth = monthTime.get("STARTMONTH");
                String endMonth = monthTime.get("ENDMONTH");
                String headerTime = "";
                if (startMonth != null && startMonth != "" && endMonth != null && endMonth != "") {
                    StringBuffer sb = new StringBuffer();
                    sb.append(startMonth.substring(4, startMonth.length())).append("/").append(startMonth.substring(0, 4))
                            .append("-").append(endMonth.substring(4, startMonth.length())).append("/").append(endMonth.substring(0, 4));
                    headerTime = sb.toString();
                }
                data.put("headerTime", headerTime);
                //add by Lin ===END
                List<BillConsumeAnalysisDto> totalConsumeBill = billMapper.queryTotalConsumeBill(channel.getBillId());
                for (BillConsumeAnalysisDto bDto : totalConsumeBill) {
                    bDto.setMoney(getFormatMoney(Double.valueOf(bDto.getMoney())));//money格式转换
                }
                int size = totalConsumeBill.size();
                if (size <= 4) {
                    data.put("header", totalConsumeBill);
                } else {
                    JSONArray newTotalConsumeBill = new JSONArray();
                    newTotalConsumeBill.add(totalConsumeBill.get(0));
                    newTotalConsumeBill.add(totalConsumeBill.get(1));
                    newTotalConsumeBill.add(totalConsumeBill.get(2));
                    newTotalConsumeBill.add(totalConsumeBill.get(3));
                    BillConsumeAnalysisDto normalConsume = new BillConsumeAnalysisDto();
                    Double count = 0.00;
                    for (int i = 4; i < size; i++) {
                        count += Double.valueOf(totalConsumeBill.get(i).getMoney());//剩余的消费类型金额累加
                    }
                    if (count > 0) {
                        normalConsume.setConsumeType("一般");
                        normalConsume.setConsumeTypeId(9);
                        normalConsume.setMoney(getFormatMoney(count));//money格式转换
                        newTotalConsumeBill.add(normalConsume);
                    }
                    data.put("header", newTotalConsumeBill);
                }
                //查询总消费占比————结束

                //查询消费分析————开始
                JSONObject consumeBill = new JSONObject();
                JSONArray billArr = new JSONArray();
                for (BillMonthDto billMonthDto : billMonthDtos) {
                    BillConsumeTypeDto bDto = new BillConsumeTypeDto();
                    Double money = 0.00;
                    bDto.setMonth(billMonthDto.getCmonth().substring(4, 6));
                    // TODO: 2016/12/23
                    bDto.setRepayStatus("1");
                    List<BillConsumeAnalysisDto> billDtos = billMapper.queryConsumeBillByMonthId(billMonthDto.getImonthid());
                    for (BillConsumeAnalysisDto billDto : billDtos) {
                        billDto.setMoney(getFormatMoney(Double.valueOf(billDto.getMoney())));
                        money += Double.valueOf(billDto.getMoney());
                    }
                    bDto.setMoney(getFormatMoney(money));
                    bDto.setBills(billDtos);
                    if (billMonthDto.getIsbill() == 0) {
                        //未出消费
                        consumeBill.put("unsettled", bDto);
                    } else {
                        billArr.add(bDto);
                    }
                }
                if (billArr != null && billArr.size() > 0) {
                    //本期消费
                    consumeBill.put("current", billArr.remove(0));
                    if (billArr.size() > 0) {
                        //过往消费
                        consumeBill.put("last", billArr);
                    }
                }
                //查询消费分析————结束
                data.put("list", consumeBill);
                result.setData(data);
            } else {
                result.setDesc("此用户无历史消费记录");
            }
            logger.info("queryConsumeType———billId:" + channel.getBillId() + "——result:" + JSON.toJSON(result));
        } catch (NumberFormatException e) {
            logger.info("queryConsumeType", e);
        }
        return result;
    }
}
