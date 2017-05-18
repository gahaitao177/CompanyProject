package com.caiyi.financial.nirvana.ccard.bill.bank.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.mapper.CardQuotaMapper;
import com.caiyi.financial.nirvana.ccard.bill.bean.ForeheadRecord;
import com.caiyi.financial.nirvana.ccard.bill.dto.*;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Linxingyu on 2017/2/6.
 * 信用卡提额
 */
@Service
public class CardQuotaIncrService extends AbstractService {

    @Autowired
    private CardQuotaMapper cardQuotaMapper;

    private static final Integer Y = 0;
    private static final Integer N = 1;
    private static final Integer PROMOTING = 2;//额度提升过程中
    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyyMMdd");//统一时间格式
    private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");//统一时间格式
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.0");//Double格式转换
    private static final String MONTH_REGEX1 = "\\d{6}";//月份格式
    private static final String MONTH_REGEX2 = "\\d{4}01";//跨年月份格式，针对201701处理出账日
    private static final String MONTH_REGEX3 = "\\d{4}12";//跨年月份格式，针对201612处理出账日
    private static final String DATE_PATTERN1 = "\\d{8}";//日期格式YYYYMMDD
    private static final String DATE_PATTERN2 = "\\d{6}-\\d{2}";//日期格式YYYYMM-DD
    private static final String DATE_PATTERN3 = "\\d{4}/\\d{2}/\\d{2}";//日期格式YYYY/MM/DD


    /**
     * 提额首页
     *
     * @return
     */
    public BoltResult raiseQuotaIndex(String cuserId) {
        BoltResult result = new BoltResult("0", "查询失败");
        try {
            //查询用户所有卡
            List<BankBillDto> cards = cardQuotaMapper.queryUserCards(cuserId);
            if (!cards.isEmpty()) {
                JSONObject data = new JSONObject();
                JSONArray cardInfos = new JSONArray();
                List<Integer> scores = new ArrayList<>();
                for (BankBillDto card : cards) {
                    JSONObject cardInfo = new JSONObject();
                    cardInfo.put("bankId", card.getIbankid());
                    Integer billId = card.getIbillid();
                    cardInfo.put("billId", billId);
                    cardInfo.put("card4num", card.getIcard4num());//卡号后四位
                    cardInfo.put("cardSurplusMoney", card.getIavailablequota());//剩余额度
                    cardInfo.put("cardSumMoney", card.getItotalquota());//总额度
                    cardInfo.put("cardUptoMoney", "");
                    JSONObject quotaReport = getQuotaReport(billId);//获取诊断报告
                    if (null != quotaReport) {
                        //计算单卡分值
                        scores.add(calcSingleScore(quotaReport.getInteger("overdueRepaymentNumber"),
                                quotaReport.getInteger("repaymentRateNumber"),
                                quotaReport.getDouble("averageUseRate")));
                    }
                    String cardUpType = getCardUpType(card);
                    cardInfo.put("cardupType", cardUpType);
                    if ("3".equals(cardUpType)) {
                        Integer isFixed = card.getIsFixed();
                        Integer isZero = card.getIsZero();
                        String fixedQuota = card.getFixedQuota();
                        String zeroQuota = card.getZeroQuota();
                        if (isFixed == 0) {
                            if (isZero == 0) {
                                cardInfo.put("cardUptoMoney",
                                        fixedQuota.compareTo(zeroQuota) > 0 ? fixedQuota : zeroQuota);//可提升额度，取固额和临额最大值
                            } else {
                                cardInfo.put("cardUptoMoney", fixedQuota);//只能提升固额
                            }
                        } else if (isZero == 0) {
                            cardInfo.put("cardUptoMoney", zeroQuota);//只能提升临额
                        }
                    }
                    if ("4".equals(cardUpType)) {
                        if (null != quotaReport) {
                            cardInfo.put("useTypeDesc", getUserTypeDesc(quotaReport.getDouble("averageUseRate")));
                        } else {
                            logger.info("raiseQuotaIndex|getQuotaReport|未查询到额度使用信息");
                            cardInfo.put("useTypeDesc", "未查询到额度使用信息");
                        }
                    }
                    cardInfos.add(cardInfo);
                }
                //计算总分
                data.put("cardInfos", cardInfos);
                data.put("updateTime", FORMAT2.format(new Date()));
                data.putAll(getScoreAndComment(scores));
                result.setData(data);
                result.setCode("1");
                result.setDesc("success");
            } else {
                logger.info("此用户暂无可用信用卡");
                result.setCode("1");
                result.setDesc("此用户暂无可用信用卡");
            }
        } catch (Exception e) {
            logger.error("提额首页错误|" + e.getMessage(), e);
        }
        logger.info("提额首页成功|" + JSON.toJSON(result));
        return result;
    }

    /**
     * 提额详情页
     *
     * @return
     */
    public BoltResult raiseQuotaDetail(Integer billId) {
        BoltResult result = new BoltResult("0", "查询失败");
        try {
            BankBillDto card = cardQuotaMapper.queryUserCard(billId);
            if (null == card) {
                logger.info("提额详情页|此卡片不存在|" + billId);
                result.setCode("1");
                result.setDesc("此卡片不存在");
                return result;
            }
            JSONObject data = new JSONObject();
            data.put("validityDate", "30-59天");//临额有效期
            data.put("cardupType", getCardUpType(card));//额度提升方式
            //2017/2/23 新增字段
            data.put("bankId", card.getIbankid() + "");
            data.put("card4Num", card.getIcard4num());
            data.put("importType", card.getIswebormail() + "");
            //2017/2/28新增字段
            if (PROMOTING == card.getIsFixed()) {
                //固额提升过程中
                data.put("isFixedMoneyPromoting", "0");
            } else {
                data.put("isFixedMoneyPromoting", "1");
            }
            if (PROMOTING == card.getIsZero()) {
                //临额提升过程中
                data.put("isZeroMoneyPromoting", "0");
            } else {
                data.put("isZeroMoneyPromoting", "1");
            }
            //申请方式
            JSONArray applyTypes = new JSONArray();
            try {
                applyTypes = getBankInfoById(card.getIbankid());
            } catch (Exception e) {
                logger.error("提额详情页|getBankInfoById|获取" + card.getIbankid() + "申请方式错误，请检查数据库中是否存在对应申请方式");
            }
            data.put("applyTypes", applyTypes);
            //额度诊断报告
            JSONObject comment = new JSONObject();
            JSONObject report = getQuotaReport(billId);
            if (null != report) {
                Integer overdueRepaymentNumber = report.getInteger("overdueRepaymentNumber");
                Integer repaymentRateNumber = report.getInteger("repaymentRateNumber");
                Double averageUseRate = report.getDouble("averageUseRate");
                try {
                    comment = getReportDetail(overdueRepaymentNumber, repaymentRateNumber, averageUseRate);
                } catch (Exception e) {
                    logger.error("提额详情页|getReportDetail|获取诊断报告详情失败");
                }
            }
            data.put("diagnoseReport", comment);
            //获取卡额度信息
            Map<String, Object> quotaInfo = cardQuotaMapper.queryQuotaInfo(billId);
            if (null != quotaInfo) {
                data.put("cardInfos", new JSONObject(quotaInfo));
            }
            result.setCode("1");
            result.setData(data);
            result.setDesc("success");
        } catch (Exception e) {
            logger.error("提额详情页错误|" + billId + "|" + e.getMessage(), e);
        }
        logger.info("提额详情页成功|" + JSON.toJSON(result));
        return result;
    }

    /**
     * 诊断报告页
     *
     * @return
     */
    public BoltResult raiseQuotaReport(Integer billId) {
        BoltResult result = new BoltResult("0", "查询失败");
        try {
            JSONObject data = getQuotaReport(billId);
            if (null != data) {
                result.setData(data);
                result.setCode("1");
                result.setDesc("success");
            } else {
                logger.info("诊断报告页|此卡无诊断报告");
                result.setCode("1");
                result.setDesc("此卡无诊断报告");
            }
        } catch (Exception e) {
            logger.error("诊断报告页错误|" + billId + "|" + e.getMessage() + e);
        }
        logger.info("诊断报告页成功|" + JSON.toJSON(result));
        return result;
    }

    /**
     * cardupType=4时，获取卡描述信息
     *
     * @param averageUseRate
     * @return
     */
    public String getUserTypeDesc(Double averageUseRate) {
        if (averageUseRate >= 0.25 && averageUseRate <= 0.65) {
            //一切优秀
            return "近6月额度使用率最佳";
        }
        if (averageUseRate < 0.2) {
            //额度使用率偏低
            return "近6月额度使用率偏低";
        }
        if (averageUseRate > 0.9) {
            //额度使用率偏高
            return "近6月额度使用率偏高";
        }
        //额度使用率健康
        return "近6月额度使用率健康";
    }

    /**
     * 获取提额首页分值及用卡评价
     *
     * @param scores
     * @return
     */
    private Map<String, Object> getScoreAndComment(List<Integer> scores) {
        Map<String, Object> socreAndComment = new HashMap<>();
        Map<String, String> creditLevel = new HashMap<>();
        creditLevel.put("level", "350,550,600,650,700,950");
        creditLevel.put("levelName", "较差,中等,良好,优秀,极好");
        socreAndComment.put("creditLevel", creditLevel);
        if (!scores.isEmpty()) {
            Integer score;
            Integer min = Collections.min(scores);
            if (min > 600) {
                score = Collections.max(scores);
            } else {
                score = Collections.min(scores);
            }
            socreAndComment.put("userPoints", score.toString());

            //评定分数
            if (score >= 700) {
                socreAndComment.put("comment", "用卡最佳");
            } else if (score >= 600 && score < 700) {
                socreAndComment.put("comment", "用卡优秀");
            } else if (score >= 500 && score < 600) {
                socreAndComment.put("comment", "用卡欠佳");
            } else {
                socreAndComment.put("comment", "用卡极差");
            }
        } else {
            socreAndComment.put("userPoints", "0");
        }
        return socreAndComment;
    }

    /**
     * 获取cardUpType
     *
     * @param card
     * @return
     */
    public String getCardUpType(BankBillDto card) {
        //查询所有上线银行
        List<Integer> bankList = cardQuotaMapper.queryPromoteBankList();
        if (bankList != null && bankList.size() > 0) {
            if (card.getIbankid() != null) {
                if (bankList.contains(card.getIbankid())) {
                    Integer isWebormail = card.getIswebormail();
                    Integer isZero = card.getIsZero();
                    Integer isFixed = card.getIsFixed();
                    if (Y == isZero || Y == isFixed) {
                        return "3";//3:可提额，显示提额的额度
                    }
                    if (N == isZero || N == isFixed) {
                        return "2";//2:此卡暂无可提升额度
                    }
                    if (Y == isWebormail) {
                        return "0";//1:此卡未查询过可提额度 且是 网银导入的
                    }
                    if (N == isWebormail) {
                        return "1";//0:此卡未查询过可提额度 且是 邮箱导入的
                    }
                }
            }
        }

        return "4";//非招行或者未查询到此卡
    }

    /**
     * 获取额度诊断报告
     *
     * @param billId
     * @return
     */
    public JSONObject getQuotaReport(Integer billId) {
        try {
            List<CardQuotaInfoDto> cInfos = cardQuotaMapper.queryCardQuotaInfos(billId);
            if (null != cInfos && cInfos.size() > 0) {
                List<CardUseReportDto> cReport = new ArrayList<>();
                Integer overdueRepaymentNumber = 0;//未按时还款月数
                Integer repaymentRateNumber = 0;//还款率低于50%的月数
                Double averageUseRate = 0.0;//月平均额度使用率
                //查询最近7个月的还款流水
                List<QuotaReportDto> repayments = cardQuotaMapper.queryLatelyBillNew(billId);
                //还款流水分析，计算各项指标值
                for (CardQuotaInfoDto cInfo : cInfos) {
                    CardUseReportDto report = new CardUseReportDto();
                    String cmonth = cInfo.getCmonth();
                    report.setMonth(cmonth.substring(4, 6));//月份
                    Double useQuota = cInfo.getIshouldrepayment();//使用额度
                    Double monthQuota = cInfo.getImonthquota();//当月额度
                    Double lowestQuota = cInfo.getIlowestrepayment();//每月最低还款额
                    Double totalQuota = cInfo.getItotalquota();//总额度
                    //计算月额度使用率
                    Double repayUseRate = getRepayUseRate(useQuota, monthQuota, totalQuota);
                    report.setUseRate(getFormatDouble(repayUseRate));
                    averageUseRate += repayUseRate;
                    //计算月还款率
                    Double monthRepayRate = 1.0;
                    Double repay = 0.0;
                    if (null != cInfo.getCrepaymentdate()) {
                        Date endTime = getDate(FORMAT1.parse(getFormatDate(cInfo.getCrepaymentdate())), 1);
                        if (new Date().before(endTime)) {
                            report.setRepaymentRate(1.0);
                        } else {
                            //计算每月实际还款额
                            Date startTime = getDateByMonth(endTime, -1);
                            for (QuotaReportDto repayment : repayments) {
                                String occurdate = "";
                                if (cmonth.matches(MONTH_REGEX1)) {
                                    if (StringUtils.isNotEmpty(repayment.getCtradedate())) {
                                        occurdate = repayment.getCtradedate();
                                    } else if (StringUtils.isNotEmpty(repayment.getCoccurdate())) {
                                        occurdate = repayment.getCoccurdate();
                                    }
                                    if (occurdate.contains("12-") && cmonth.matches(MONTH_REGEX2)) {
                                        occurdate = Integer.valueOf(cmonth.substring(0, 4)) - 1 + occurdate;
                                    } else if (occurdate.contains("01-") && cmonth.matches(MONTH_REGEX3)) {
                                        occurdate = Integer.valueOf(cmonth.substring(0, 4)) + 1 + occurdate;
                                    } else {
                                        occurdate = cmonth.substring(0, 4) + occurdate;
                                    }
                                } else {
                                    continue;
                                }
                                Date occurDate = FORMAT1.parse(getFormatDate(occurdate));
                                if (occurDate.after(startTime) && occurDate.before(endTime)) {
                                    repay += repayment.getImoney();
                                }
                            }
                            monthRepayRate = getMonthRepayRate(useQuota, repay);
                            report.setRepaymentRate(getFormatDouble(monthRepayRate));
                            if (monthRepayRate < 0.5) {
                                repaymentRateNumber++;
                            }
                        }
                    } else {
                        report.setRepaymentRate(1.0);
                    }
                    //判断是否按时还款
                    Integer repayOnTime = isRepayOnTime(lowestQuota, repay);
                    report.setIsOnTimeRepayment(repayOnTime);
                    if (repayOnTime == 0) {
                        overdueRepaymentNumber++;
                    }
                    cReport.add(report);
                }
                averageUseRate = averageUseRate / cInfos.size();
                JSONObject data = new JSONObject();
                data.put("overdueRepaymentNumber", overdueRepaymentNumber);
                data.put("repaymentRateNumber", repaymentRateNumber);
                data.put("averageUseRate", getFormatDouble(averageUseRate));
                data.put("cardUseReport", cReport);
                return data;
            } else {
                logger.info("getQuotaReport|" + billId + "对应无数据");
            }
        } catch (Exception e) {
            logger.error("getQuotaReport|获取诊断报告出错，数据格式错误|" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 诊断报告详情
     *
     * @param overdueRepaymentNumber 未按时还款月数
     * @param repaymentRateNumber    还款率低于50%的月数
     * @param averageUseRate         月平均额度使用率
     * @return
     */
    public JSONObject getReportDetail(Integer overdueRepaymentNumber, Integer repaymentRateNumber, Double averageUseRate) {
        JSONObject comment = new JSONObject();
        if (overdueRepaymentNumber == 0) {
            //长期保持按时还款
            if (repaymentRateNumber <= 1) {
                //月还款率健康
                if (averageUseRate < 0.2) {
                    //额度使用率偏低
                    comment.put("reportType", "3");
                    comment.put("rePorttitle", "部分月份信用卡额度使用率偏低");
                    comment.put("reportContent", "最近半年刷卡不足，额度使用率偏低，建议额度使用率保持在20%以上");
                } else if (averageUseRate > 0.9) {
                    //额度使用率偏高
                    comment.put("reportType", "4");
                    comment.put("rePorttitle", "部分月份信用卡额度使用率偏高");
                    comment.put("reportContent", "最近半年额度使用率偏高，建议保持合理的信用卡额度使用率，有助提额，切忌长期最低还款");
                } else {
                    //额度使用率健康/一切优秀
                    comment.put("reportType", "5");
                    comment.put("rePorttitle", "最近半年信用卡使用优秀");
                    comment.put("reportContent", "最近半年信用卡各个指标使用良好，相信马上就可以提额了");
                }
            } else if (repaymentRateNumber >= 3) {
                //月还款率较差
                comment.put("reportType", "1");
                comment.put("rePorttitle", "部分月份信用卡还款率较差");
                comment.put("reportContent", "最近半年部分月份信用卡还款率偏低，建议最好全额还清，如有压力可办理分期缓解，分期并不会影响还款率，但是分期有助于提额");
            } else {
                //月还款率异常
                comment.put("reportType", "2");
                comment.put("rePorttitle", "部分月份信用卡还款率异常");
                comment.put("reportContent", "最近半年部分月份信用卡还款率偏低，建议最好全额还清，如有压力可办理分期缓解，分期并不会影响还款率，但是分期有助于提额");
            }
        } else {
            //长期未保持按时还款
            comment.put("reportType", "0");
            comment.put("rePorttitle", "部分月份信用卡未保持按时还款");
            comment.put("reportContent", "最近半年部分月份未保持按时还款，风险较高，建议一定要保证最低还款，以免影响信用");
        }
        return comment;
    }

    /**
     * 计算单卡分值
     *
     * @param overdueRepaymentNumber 未按时还款月数
     * @param repaymentRateNumber    还款率低于50%的月数
     * @param averageUseRate         月平均额度使用率
     * @return
     */
    public Integer calcSingleScore(Integer overdueRepaymentNumber, Integer repaymentRateNumber, Double averageUseRate) {
        Integer score = 651;
        if (averageUseRate >= 0.25 && averageUseRate <= 0.65) {
            //额度使用率最佳
            score += 100;
        } else if (averageUseRate < 0.2 || averageUseRate > 0.9) {
            //额度使用率偏低/偏高
        } else {
            //额度使用率健康
            score += 50;
        }
        if (overdueRepaymentNumber != 0) {
            //长期未保持按时还款
            score -= 300;
            return score;
        }
        if (repaymentRateNumber == 0) {
            //月还款率健康
        } else if (repaymentRateNumber >= 3) {
            //月还款率较差
            score -= 100;
        } else {
            //月还款率异常
            score -= 50;
        }
        return score;
    }

    /**
     * 判断是否按时还款
     * 最后还款日之前是否还上最低还款额，0 不按时， 1 按时
     *
     * @param lowestQuota
     * @param repay
     * @return
     */
    public Integer isRepayOnTime(Double lowestQuota, Double repay) {
        if (null != lowestQuota && lowestQuota > 0 && null != repay && repay > 0) {
            return repay >= lowestQuota ? 1 : 0;
        }
        return 1;
    }

    /**
     * 计算月还款率
     * 还款额度/使用额度
     *
     * @param useQuota
     * @param repay
     * @return
     */
    public Double getMonthRepayRate(Double useQuota, Double repay) {
        if (null != useQuota && useQuota > 0 && null != repay && repay > 0) {
            Double repayRate = repay / useQuota;
            return repayRate >= 1.0 ? 1.0 : repayRate;
        }
        return 1.0;
    }

    /**
     * 计算额度使用率
     * 使用额度/总额度
     *
     * @param useQuota
     * @param monthQuota
     * @param totalQuota
     * @return
     */

    public Double getRepayUseRate(Double useQuota, Double monthQuota, Double totalQuota) {
        if (null != useQuota && null != totalQuota) {
            if (null != monthQuota && 0 != monthQuota) {
                return Math.abs(useQuota) / monthQuota;
            } else {
                return Math.abs(useQuota) / totalQuota;
            }
        }
        return 0.0;
    }

    /**
     * 转换Double数据格式
     */
    public Double getFormatDouble(Double d) {
        try {
            d = Double.valueOf(DECIMAL_FORMAT.format(d));
        } catch (NumberFormatException e) {
        }
        return d;
    }

    /**
     * 根据银行id获取指定银行的提额申请方式信息
     *
     * @param bankId
     * @return
     */
    public JSONArray getBankInfoById(Integer bankId) {
        JSONArray applyArr = new JSONArray();
        BankApplyTypeDto bank = cardQuotaMapper.queryBankInfoById(bankId);
        // 短信申请
        JSONObject tempMsg = new JSONObject();
        String msg = bank.getMsg();
        if (!CheckUtil.isNullString(msg)) {
            tempMsg.put("type", "0");
            tempMsg.put("iBankUrl", "");
            tempMsg.put("phoneNum", bank.getMsgnum());
            tempMsg.put("message", msg);
            applyArr.add(tempMsg);
        }
        // 电话申请
        String tel = bank.getTel();
        if (!CheckUtil.isNullString(tel)) {
            JSONObject tempTel = new JSONObject();
            tempTel.put("type", "1");
            tempTel.put("iBankUrl", "");
            tempTel.put("phoneNum", bank.getTelnum());
            tempTel.put("message", tel);
            applyArr.add(tempTel);
        }
        // 微信申请
        String wechat = bank.getWechat();
        if (!CheckUtil.isNullString(wechat)) {
            JSONObject wechatTemp = new JSONObject();
            wechatTemp.put("type", "2");
            wechatTemp.put("iBankUrl", "");
            wechatTemp.put("phoneNum", "");
            wechatTemp.put("message", wechat);
            applyArr.add(wechatTemp);
        }
        // 网银申请
        String ebank = bank.getEbank();
        if (!CheckUtil.isNullString(ebank)) {
            JSONObject ebankTemp = new JSONObject();
            ebankTemp.put("type", "3");
            ebankTemp.put("iBankUrl", bank.getEbanklink());
            ebankTemp.put("phoneNum", "");
            ebankTemp.put("message", ebank);
            applyArr.add(ebankTemp);
        }
        return applyArr;
    }

    /**
     * 提额历史记录
     *
     * @param forRec
     * @return
     */
    public Integer saveForeheadRecord(ForeheadRecord forRec) {
        try {
            return cardQuotaMapper.saveForeheadRecord(forRec);
        } catch (Exception e) {
            logger.error("saveForeheadRecord 异常", e);
        }
        return 0;
    }

    /**
     * 提额成功更新bankbill表数据
     *
     * @param bankBillDto
     * @return
     */
    public Integer updateByPrimaryKeySelective(BankBillDto bankBillDto) {
        try {
            return cardQuotaMapper.updateByPrimaryKeySelective(bankBillDto);
        } catch (Exception e) {
            logger.error("updateByPrimaryKeySelective 异常", e);
        }
        return 0;
    }

    /**
     * 统一日期格式
     *
     * @param dateStr
     * @return
     */
    public String getFormatDate(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr)) {
            if (dateStr.matches(DATE_PATTERN1)) {
                return dateStr;//YYYYMMDD
            }
            if (dateStr.matches(DATE_PATTERN2)) {
                return dateStr.replace("-", "");//YYYYMM-DD
            }
            if (dateStr.matches(DATE_PATTERN3)) {
                return dateStr.replace("/", "");//YYYY/MM/DD
            }
        }
        return dateStr;
    }

    /**
     * 日期加减处理
     *
     * @param date
     * @param num
     * @return
     */
    public Date getDate(Date date, int num) {
        Date backTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + num);
        try {
            backTime = FORMAT1.parse(FORMAT1.format(now.getTime()));
        } catch (Exception e) {
            logger.error("日期加减操作异常");
        }
        return backTime;
    }

    /**
     * 月份加减处理
     *
     * @param date
     * @param month
     * @return
     */
    public Date getDateByMonth(Date date, int month) {
        Date backTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MONTH, month);
        try {
            backTime = FORMAT1.parse(FORMAT1.format(now.getTime()));
        } catch (Exception e) {
            logger.error("月份加减操作异常");
        }
        return backTime;
    }

}
