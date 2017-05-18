package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.bean.ProvidentFundBean;
import com.caiyi.financial.nirvana.ccard.investigation.bean.SocialInsuranceBean;
import com.caiyi.financial.nirvana.ccard.investigation.constants.YouyuCreditScoreWeight;
import com.caiyi.financial.nirvana.ccard.investigation.dto.BillMonthInfoDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReportDetailsDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditScoreNewDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.QuotaReportDto;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.YouyuCreditMapper;
import com.caiyi.financial.nirvana.ccard.investigation.util.CreditScoreUtil;
import com.caiyi.financial.nirvana.ccard.investigation.util.YouyuCalcCreditScoreUtil;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Linxingyu on 2017/3/16.
 * 有鱼信用算分逻辑
 */
@Service
public class YouyuCreditService extends AbstractService {

    @Autowired
    private YouyuCreditMapper youyuCreditMapper;

    private CreditScoreNewDto creditScoreInfo = null;//查询到的征信信息
    private Map<String, Object> eduMap = new HashMap<>();//查询到的学信信息
    private Map<String, Object> billMap = new HashMap<>();//查询到的账单分析后的数据
    private Map<String, Object> socialMap = new HashMap<>();//查询到的社保公积金分析后的数据
    private Map<String, Integer> zxReportMap = new HashMap<>();//存放征信报告得分相关数据
    private Set<Integer> cardRepayScore = new HashSet<>();//信用卡还款记录得分
    private Pattern p = Pattern.compile("[^0-9|\\u5e74 |\\u6708 |\\u65e5 ]");//计算信用卡开户时长用到的正则
    private SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");//计算信用卡开户时长用到的日期格式化
    private Date NOW = new Date();////计算信用卡开户时长用到的当前日期

    /**
     * 有鱼信用算分
     *
     * @param scoreBean
     */
    public double creditScoreIndex(CreditScoreBean scoreBean) {
        long start = System.currentTimeMillis();
        double totalScore = 0.0;//总分
        double totalWeight = 0.0;//总权重
        //查询征信信息
        creditScoreInfo = youyuCreditMapper.queryCreditInfoByUserId(scoreBean.getCuserId());
        if (creditScoreInfo != null) {
            logger.info("已查询到征信数据，返回数据" + JSON.toJSON(creditScoreInfo));
            Integer icrid = creditScoreInfo.getZxId();
            if (icrid != null) {
                List<CreditReportDetailsDto> reports = youyuCreditMapper.queryCreditReportDetails(icrid);
                logger.info("已查询到征信报告数据，返回数据" + JSON.toJSON(reports));
                //分析征信报告
                analyzeZXInfos(reports);
            }
        }
        //查询学信信息
        eduMap = youyuCreditMapper.queryEducationInfo(scoreBean.getCuserId());
        if (eduMap != null && eduMap.size() > 0) {
            logger.info("已查询到学信数据，返回数据" + JSON.toJSON(eduMap));
        }
        //查询社保，公积金信息
        long socialStart = System.currentTimeMillis();
        ProvidentFundBean gjjBean = CreditScoreUtil.getGjjBean(scoreBean);
        long gjjEnd = System.currentTimeMillis();
        logger.info("查询公积金耗时：" + (gjjEnd - socialStart));
        SocialInsuranceBean sbBean = CreditScoreUtil.getSbBean(scoreBean);
        long socialEnd = System.currentTimeMillis();
        logger.info("查询社保耗时：" + (socialEnd - gjjEnd));
        analyzeSocialInfos(gjjBean, sbBean);
        //分析账单数据
        analyzeBillInfos(scoreBean.getCuserId());
        //身份特质算分
        double identityScore = getIdentityScore();
        if (identityScore > 0) {
            logger.info("身份特质得分：" + identityScore);
            totalWeight += YouyuCreditScoreWeight.CREDIT_IDENTITY;
            totalScore += identityScore * YouyuCreditScoreWeight.CREDIT_IDENTITY;
        }
        //信用历史算分
        double historyScore = getHistoryScore();
        if (historyScore > 0) {
            logger.info("信用历史得分：" + historyScore);
            totalWeight += YouyuCreditScoreWeight.CREDIT_HISTORY;
            totalScore += historyScore * YouyuCreditScoreWeight.CREDIT_HISTORY;
        }
        //行为偏好算分
        double consumeScore = getConsumeScore();
        if (consumeScore > 0) {
            logger.info("行为偏好得分：" + consumeScore);
            totalWeight += YouyuCreditScoreWeight.CREDIT_BEHAVIOR;
            totalScore += consumeScore * YouyuCreditScoreWeight.CREDIT_BEHAVIOR;
        }
        //履约能力算分
        double capacityScore = getCapacityScore();
        if (capacityScore > 0) {
            logger.info("履约能力得分：" + capacityScore);
            totalWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY;
            totalScore += capacityScore * YouyuCreditScoreWeight.CREDIT_CAPACITY;
        }
        //违约历史算分
        double breakScore = getBreakScore();
        if (breakScore > 0) {
            logger.info("违约历史得分：" + breakScore);
            totalWeight += YouyuCreditScoreWeight.CREDIT_BREAK;
            totalScore += breakScore * YouyuCreditScoreWeight.CREDIT_BREAK;
        }
        //计算总分
        if (totalWeight > 0) {
            totalScore = totalScore / totalWeight;
            System.out.println("总得分：" + totalScore);
        } else {
            System.out.println("此用户暂无分数！");
        }
        long end = System.currentTimeMillis();
        logger.info("有鱼信用算分接口耗时：" + (end - start));
        return totalScore;
    }

    /**
     * 分析公积金、社保数据
     *
     * @param gjjBean
     * @param sbBean
     */
    private void analyzeSocialInfos(ProvidentFundBean gjjBean, SocialInsuranceBean sbBean) {
        if (gjjBean != null) {
            logger.info("已查询到公积金数据，返回数据" + JSON.toJSON(gjjBean));
            socialMap.put("gjjMonthNum", gjjBean.getMounthNum());
            socialMap.put("gjjMonthTotal", gjjBean.getMounthTotal());
        }
        if (sbBean != null) {
            logger.info("已查询到社保数据，返回数据" + JSON.toJSON(sbBean));
            socialMap.put("socialStatus", 1);
        }
    }


    /**
     * 分析账单数据
     *
     * @param cuserId
     */
    private void analyzeBillInfos(String cuserId) {
        long start = System.currentTimeMillis();
        List<CreditScoreNewDto> cards = null;
        Set<Integer> billScore = new HashSet<>();
        Set<Double> cardQuotaSet = new HashSet<>();
        //查询用户的卡，无征信查询所有，有征信根据征信姓名查询所有对应名称和无名账单
        if (creditScoreInfo != null && creditScoreInfo.getName() != null) {
            //有名，查询所有无名和对应名称的账单
            cards = youyuCreditMapper.queryBillsByCondition(cuserId, creditScoreInfo.getName());
        } else {
            //没有征信，按照无名查询
            cards = youyuCreditMapper.queryBillsByCondition(cuserId, null);
        }
        if (cards != null && cards.size() > 0) {
            logger.info("已查询到账单数据，返回数据" + JSON.toJSON(cards));
            int totalNum = 0;//消费频次记录
            int totalMoney = 0;//消费金额记录
            int cardSize = cards.size();
            for (CreditScoreNewDto card : cards) {
                if (card.getCardQuota() != null) {
                    //记录每张卡额度信息
                    cardQuotaSet.add(card.getCardQuota());
                }
                //分析账单数据，计算还款率得分
                List<BillMonthInfoDto> cInfos = youyuCreditMapper.queryMonthInfos(card.getBillId());
                if (cInfos != null && cInfos.size() > 0) {
                    List<QuotaReportDto> repayments = youyuCreditMapper.queryLatelyBillNew(card.getBillId());
                    int monthInfoSize = cInfos.size();
                    int monthNum = 0;
                    int monthMoney = 0;
                    //计算月消费频次，月消费金额
                    for (BillMonthInfoDto cinfo : cInfos) {
                        if (cinfo != null) {
                            billScore.add(CreditScoreUtil.getMonthRepay(cinfo, repayments));
                            int num = cinfo.getConsumeNum();
                            monthNum += num;
                            double money = cinfo.getIshouldrepayment();
                            if (money > 0) {
                                monthMoney += money;
                            }
                        }
                    }
                    totalNum += monthNum / monthInfoSize;
                    totalMoney += monthMoney / monthInfoSize;
                }
            }
            totalNum = totalNum / cardSize;
            billMap.put("totalNum", totalNum);
            totalMoney = totalMoney / cardSize;
            billMap.put("totalMoney", totalMoney);
            if (billScore != null && billScore.size() > 0) {
                billMap.put("cardRepayScore", Collections.min(billScore));
            }
            if (cardQuotaSet != null && cardQuotaSet.size() > 0) {
                billMap.put("cardQuota", Collections.max(cardQuotaSet));
            }

        }
        long end = System.currentTimeMillis();
        logger.info("分析账单数据耗时：" + (end - start));
    }

    /**
     * 分析征信明细
     *
     * @param reports 查询到的征信报告详情
     */
    private void analyzeZXInfos(List<CreditReportDetailsDto> reports) {
        long start = System.currentTimeMillis();
        if (reports != null && reports.size() > 0) {
            Set<Integer> loanPassScore = new HashSet<>();//记录最近一年的信用卡贷款通过率
            Set<Integer> cardTimeSet = new HashSet<>();//记录信用卡开户时长
            for (CreditReportDetailsDto report : reports) {
                String detail = report.getCdetails();
                if (detail.contains("变成呆账")) {
                    cardRepayScore.add(0);
                }
                if (detail.contains("尚未激活")) {
                    cardRepayScore.add(80);
                }
                if (detail.contains("发放的贷记卡")) {
                    loanPassScore.add(100);
                    cardTimeSet.add(calcCardTime(detail));
                }
                if (detail.contains("发放的") && detail.contains("贷款")) {
                    loanPassScore.add(100);
                }
                if (detail.contains("个人住房")) {
                    zxReportMap.put("house", 100);
                }
                if (detail.contains("个人汽车贷款")) {
                    zxReportMap.put("car", 100);
                }
            }

            if (cardTimeSet != null && cardTimeSet.size() > 0) {
                zxReportMap.put("cardTime", Collections.max(cardTimeSet));
            }
            if (loanPassScore != null && loanPassScore.size() > 0) {
                zxReportMap.put("loanPassScore", Collections.max(loanPassScore));
            }
        }
        long end = System.currentTimeMillis();
        logger.info("分析征信明细耗时：" + (end - start));
    }

    /**
     * 违约历史算分
     *
     * @return
     */
    private double getBreakScore() {
        long start = System.currentTimeMillis();
        // TODO 违约历史暂时忽略
        double breakSum = 0.0;
        double breakScore = 100;
        long end = System.currentTimeMillis();
        logger.info("违约历史算分耗时：" + (end - start));
        return breakScore;
    }

    /**
     * 履约能力算分
     *
     * @return
     */
    private double getCapacityScore() {
        long start = System.currentTimeMillis();
        double capacityWeight = 0.0;
        double capacityScore = 0.0;
        if (billMap != null && billMap.size() > 0) {
            //信用卡额度
            Object cardQuota = billMap.get("cardQuota");
            if (cardQuota != null) {
                int cardQuotaScore = YouyuCalcCreditScoreUtil.getCardQuotaScore(Double.parseDouble(cardQuota.toString()));
                logger.info("信用卡额度得分：" + cardQuotaScore);
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_CARDQUOTA;
                capacityScore += cardQuotaScore * YouyuCreditScoreWeight.CREDIT_CAPACITY_CARDQUOTA;
            }
            Object cardRepayScore = billMap.get("cardRepayScore");
            if (cardRepayScore != null) {
                logger.info("平均还款率得分：" + cardRepayScore.toString());
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_CARDREPAYRATE;
                capacityScore += Integer.parseInt(cardRepayScore.toString()) * YouyuCreditScoreWeight.CREDIT_CAPACITY_CARDREPAYRATE;
            }
        }
        if (creditScoreInfo != null) {
            Integer cardNum = creditScoreInfo.getCardNum();
            if (cardNum != null) {
                //信用卡账户数
                int cardAmountScore = YouyuCalcCreditScoreUtil.getCardAmountScore(cardNum);
                logger.info("信用卡数量得分：" + cardAmountScore);
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_CARDAMOUNT;
                capacityScore += cardAmountScore * YouyuCreditScoreWeight.CREDIT_CAPACITY_CARDAMOUNT;
            }
        }
        if (zxReportMap != null && zxReportMap.size() > 0) {
            Integer house = zxReportMap.get("house");
            if (house != null) {
                logger.info("房产得分：" + house);
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_HOUSE;
                capacityScore += house * YouyuCreditScoreWeight.CREDIT_CAPACITY_HOUSE;
            }
            Integer car = zxReportMap.get("car");
            if (car != null) {
                logger.info("车产得分：" + car);
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_CAR;
                capacityScore += car * YouyuCreditScoreWeight.CREDIT_CAPACITY_CAR;
            }
        }
        if (socialMap != null && socialMap.size() > 0) {
            Object gjjMonthTotal = socialMap.get("gjjMonthTotal");
            if (gjjMonthTotal != null) {
                //公积金月缴额
                int gjjScore = 100;
                logger.info("公积金得分：" + gjjScore);
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_GJJ;
                capacityScore += gjjScore * YouyuCreditScoreWeight.CREDIT_CAPACITY_GJJ;
            }
            Object socialStatus = socialMap.get("socialStatus");
            if (socialStatus != null) {
                //社保月缴额
                int socialScore = 100;
                logger.info("社保得分：" + socialScore);
                capacityWeight += YouyuCreditScoreWeight.CREDIT_CAPACITY_SB;
                capacityScore += socialScore * YouyuCreditScoreWeight.CREDIT_CAPACITY_SB;
            }
        }
        if (capacityWeight > 0) {
            capacityScore = capacityScore / capacityWeight;
        }
        long end = System.currentTimeMillis();
        logger.info("履约能力算分耗时：" + (end - start));
        return capacityScore;
    }

    /**
     * 行为偏好算分
     *
     * @return
     */
    private double getConsumeScore() {
        long start = System.currentTimeMillis();
        double behaviorWeight = 0.0;
        double behaviorScore = 0.0;
        if (billMap != null && billMap.size() > 0) {
            Object totalNum = billMap.get("totalNum");
            if (totalNum != null) {
                int num = Integer.parseInt(totalNum.toString());
                behaviorWeight += YouyuCreditScoreWeight.CREDIT_BEHAVIOR_CONSUMENUM;
                int consumeNumScore = YouyuCalcCreditScoreUtil.getConsumeNumScore(num);
                logger.info("消费频次得分：" + consumeNumScore);
                behaviorScore += consumeNumScore * YouyuCreditScoreWeight.CREDIT_BEHAVIOR_CONSUMENUM;
            }
            Object totalMoney = billMap.get("totalMoney");
            if (totalMoney != null) {
                double money = Double.parseDouble(totalMoney.toString());
                behaviorWeight += YouyuCreditScoreWeight.CREDIT_BEHAVIOR_CONSUMEMONEY;
                int consumeMoneyScore = YouyuCalcCreditScoreUtil.getConsumeMoneyScore(money);
                logger.info("消费金额得分：" + consumeMoneyScore);
                behaviorScore += consumeMoneyScore * YouyuCreditScoreWeight.CREDIT_BEHAVIOR_CONSUMEMONEY;
            }
        }
        if (behaviorWeight > 0) {
            behaviorScore = behaviorScore / behaviorWeight;
        }
        long end = System.currentTimeMillis();
        logger.info("行为偏好算分耗时：" + (end - start));
        return behaviorScore;
    }

    /**
     * 身份特质算分
     *
     * @return
     */
    private double getIdentityScore() {
        long start = System.currentTimeMillis();
        double identityWeight = 0.0;
        double identityScore = 0.0;
        if (eduMap != null && eduMap.size() > 0) {
            Object ID = eduMap.get("code");
            if (ID != null) {
                //身份证
                int age = getAgeByID(ID.toString());
                int ageScore = YouyuCalcCreditScoreUtil.getAgeScore(age);
                logger.info("身份证得分：" + ageScore);
                identityWeight += YouyuCreditScoreWeight.CREDIT_IDENTITY_AGE;
                identityScore += ageScore * YouyuCreditScoreWeight.CREDIT_IDENTITY_AGE;
            }
            Object eduLevel = eduMap.get("eduLevel");
            if (eduLevel != null) {
                //学历
                int i = Integer.parseInt(eduLevel.toString());
                if (0 != i) {
                    int eduScore = YouyuCalcCreditScoreUtil.getEducationScore(i);
                    logger.info("学历得分：" + eduScore);
                    identityWeight += YouyuCreditScoreWeight.CREDIT_IDENTITY_EDUCATION;
                    identityScore += eduScore * YouyuCreditScoreWeight.CREDIT_IDENTITY_EDUCATION;
                }
            }
        }
        if (creditScoreInfo != null) {
            String marStatus = creditScoreInfo.getMarStatus();
            if (marStatus != null) {
                //婚姻状态
                int marScore = YouyuCalcCreditScoreUtil.getMarriageScore(marStatus);
                logger.info("婚姻得分：" + marScore);
                identityWeight += YouyuCreditScoreWeight.CREDIT_IDENTITY_MARRIAGE;
                identityScore += marScore * YouyuCreditScoreWeight.CREDIT_IDENTITY_MARRIAGE;
            }
        }
        if (socialMap != null && socialMap.size() > 0) {
            Object gjjMonthNum = socialMap.get("gjjMonthNum");
            if (gjjMonthNum != null) {
                //工作年限-公积金月数
                int workTimeScore = YouyuCalcCreditScoreUtil.getWorkTimeScore(Integer.parseInt(gjjMonthNum.toString()));
                logger.info("工作年限得分：" + workTimeScore);
                identityWeight += YouyuCreditScoreWeight.CREDIT_IDENTITY_WORKTIME;
                identityScore += workTimeScore * YouyuCreditScoreWeight.CREDIT_IDENTITY_WORKTIME;
            }
        }
        if (identityWeight > 0) {
            identityScore = identityScore / identityWeight;
        }
        long end = System.currentTimeMillis();
        logger.info("身份特质算分耗时：" + (end - start));
        return identityScore;
    }

    /**
     * 信用历史算分
     *
     * @return
     */
    private double getHistoryScore() {
        long start = System.currentTimeMillis();
        double historyWeight = 0.0;
        double historyScore = 0.0;
        if (creditScoreInfo != null) {
            Integer cardOverdueStatus = creditScoreInfo.getCardOverdueStatus();
            if (cardOverdueStatus != null) {
                cardRepayScore.add(YouyuCalcCreditScoreUtil.getCardRepayScore(cardOverdueStatus));
            }
            if (cardRepayScore != null && cardRepayScore.size() > 0) {
                int cardRepayScore1 = Collections.min(cardRepayScore);//信用卡还款记录
                logger.info("信用卡还款记录得分：" + cardRepayScore1);
                historyWeight += YouyuCreditScoreWeight.CREDIT_HISTORY_CARDREPAY;
                historyScore += cardRepayScore1 * YouyuCreditScoreWeight.CREDIT_HISTORY_CARDREPAY;
            }
            Integer loanOverdueStatus = creditScoreInfo.getLoanOverdueStatus();
            if (loanOverdueStatus != null) {
                int loanRepayScore1 = YouyuCalcCreditScoreUtil.getLoanRepayScore(loanOverdueStatus);//贷款还款记录
                logger.info("贷款还款记录得分：" + loanRepayScore1);
                historyWeight += YouyuCreditScoreWeight.CREDIT_HISTORY_LOANREPAY;
                historyScore += loanRepayScore1 * YouyuCreditScoreWeight.CREDIT_HISTORY_LOANREPAY;
            }
            if (zxReportMap != null && zxReportMap.size() > 0) {
                Integer cardTime = zxReportMap.get("cardTime");
                if (cardTime != null) {
                    int cardTimeScore = YouyuCalcCreditScoreUtil.getCardTimeScore(cardTime);//信用卡开户时长
                    logger.info("信用卡开户时长得分：" + cardTimeScore);
                    historyWeight += YouyuCreditScoreWeight.CREDIT_HISTORY_CARDTIME;
                    historyScore += cardTimeScore * YouyuCreditScoreWeight.CREDIT_HISTORY_CARDTIME;
                }
                Integer loanPassScore = zxReportMap.get("loanPassScore");
                if (loanPassScore == null) {
                    loanPassScore = 80;//最近一年的信用卡贷款通过率
                }
                logger.info("最近一年的信用卡贷款通过率得分：" + loanPassScore);
                historyWeight += YouyuCreditScoreWeight.CREDIT_HISTORY_LOANPASS;
                historyScore += loanPassScore * YouyuCreditScoreWeight.CREDIT_HISTORY_LOANPASS;
            }
        }
        if (historyWeight > 0) {
            historyScore = historyScore / historyWeight;
        }
        long end = System.currentTimeMillis();
        logger.info("信用历史算分耗时：" + (end - start));
        return historyScore;
    }


    /**
     * 计算信用卡开户时长
     *
     * @param detail
     */
    private int calcCardTime(String detail) {
        int months = 0;
        try {
            String cardTime = p.matcher(detail.substring(0, 11)).replaceAll("");
            Date parse = FORMAT.parse(cardTime);
            int days = (int) ((NOW.getTime() - parse.getTime()) / (1000 * 60 * 60 * 24));
            months = days / 30;
        } catch (ParseException e) {
        }
        return months;
    }

    /**
     * 根据身份证得出年龄
     *
     * @param ID
     * @return
     */
    public static int getAgeByID(String ID) {
        int length = ID.length();
        String dates = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(new Date());
        if (length == 18) {
            dates = ID.substring(6, 10);
        } else {
            dates = "19" + ID.substring(6, 8);
        }
        return Integer.parseInt(year) - Integer.parseInt(dates);
    }
}
