package com.caiyi.financial.nirvana.ccard.investigation.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.bean.ProvidentFundBean;
import com.caiyi.financial.nirvana.ccard.investigation.bean.SocialInsuranceBean;
import com.caiyi.financial.nirvana.ccard.investigation.dto.BillMonthInfoDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.QuotaReportDto;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.hsk.common.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Linxingyu on 2017/3/22.
 */
public class CreditScoreUtil {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreUtil.class);
    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyyMMdd");//统一时间格式
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.0");//Double格式转换
    private static final String MONTH_REGEX1 = "\\d{6}";//月份格式
    private static final String MONTH_REGEX2 = "\\d{4}01";//跨年月份格式，针对201701处理出账日
    private static final String MONTH_REGEX3 = "\\d{4}12";//跨年月份格式，针对201612处理出账日
    private static final String DATE_PATTERN1 = "\\d{8}";//日期格式YYYYMMDD
    private static final String DATE_PATTERN2 = "\\d{6}-\\d{2}";//日期格式YYYYMM-DD
    private static final String DATE_PATTERN3 = "\\d{4}/\\d{2}/\\d{2}";//日期格式YYYY/MM/DD
    private static Set<Integer> billScore = new HashSet<>();

    /**
     * 计算月还款率
     */
    public static int getMonthRepay(BillMonthInfoDto cInfo, List<QuotaReportDto> repayments) {
        int score = 0;
        try {
            //计算当前卡的月还款率
            int repayOnTime = 1;
            double monthRepay = 0.0;
            if (cInfo != null) {
                //还款流水分析，计算各项指标值
                String cmonth = cInfo.getCmonth();
                Double useQuota = cInfo.getIshouldrepayment();//使用额度
                Double lowestQuota = cInfo.getIlowestrepayment();//每月最低还款额
                if (useQuota <= 0 || lowestQuota <= 0) {
                    monthRepay++;
                } else {
                    Double monthRepayRate = 1.0;
                    Double repay = 0.0;
                    //计算月还款率
                    if (cInfo.getCrepaymentdate() != null) {
                        Date endTime = null;
                        endTime = getDate(FORMAT1.parse(getFormatDate(cInfo.getCrepaymentdate())), 1);
                        if (new Date().before(endTime)) {
                            monthRepay++;
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
                                        occurdate = Integer.parseInt(cmonth.substring(0, 4)) - 1 + occurdate;
                                    } else if (occurdate.contains("01-") && cmonth.matches(MONTH_REGEX3)) {
                                        occurdate = Integer.parseInt(cmonth.substring(0, 4)) + 1 + occurdate;
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
                            monthRepay += monthRepayRate;
                        }
                    }
                    //判断是否按时还款
                    repayOnTime = isRepayOnTime(lowestQuota, repay);
                }
                score = YouyuCalcCreditScoreUtil.getCardRepayRateScore(repayOnTime, monthRepay);
            }
        } catch (ParseException e) {
        }
        return score;
    }

    /**
     * 统一日期格式
     *
     * @param dateStr
     * @return
     */
    public static String getFormatDate(String dateStr) {
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
    public static Date getDate(Date date, int num) {
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
    public static Date getDateByMonth(Date date, int month) {
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

    /**
     * 判断是否按时还款
     * 是否还上最低还款额，0 等于最低还款额 -1 低于  1 高于
     *
     * @param lowestQuota
     * @param repay
     * @return
     */
    public static int isRepayOnTime(Double lowestQuota, Double repay) {
        if (null != lowestQuota && lowestQuota > 0 && null != repay && repay > 0) {
            if (repay == lowestQuota) {
                return 0;
            }
            if (repay > lowestQuota) {
                return 1;
            }
            return -1;
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
    public static Double getMonthRepayRate(Double useQuota, Double repay) {
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

    public static Double getRepayUseRate(Double useQuota, Double monthQuota, Double totalQuota) {
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
    public static Double getFormatDouble(Double d) {
        try {
            d = Double.valueOf(DECIMAL_FORMAT.format(d));
        } catch (NumberFormatException e) {
        }
        return d;
    }

    /**
     * 公积金登录
     *
     * @param scoreBean
     * @return
     */
    public static ProvidentFundBean getGjjBean(CreditScoreBean scoreBean) {
        ProvidentFundBean fundBean = new ProvidentFundBean();
        //请求公积金结果信息
        String url = SystemConfig.get("zxURL.gjj_url2");
        logger.info("公积金地址:{}", url);
        Map<String, String> param = new HashMap<>();
        param.put("releaseVersion", "1.9.0");
        param.put("from", "hsk");
        param.put("appId", scoreBean.getAppId());
        param.put("accessToken", scoreBean.getAccessToken());
        String result = null;
        try {
            result = HttpClientUtil.callHttpPost_Map(url, param);
        } catch (Exception e) {
            logger.error("请求公积金接口出错", e);
            return null;
        }
        logger.info("公积金内容:{}", result);
        JSONObject contendJson = JSONObject.parseObject(result);
        if (contendJson != null) {
            JSONObject data = contendJson.getJSONObject("results");
            if (data != null && data.getJSONArray("list") != null) {
                JSONObject gjjContend = data.getJSONArray("list").getJSONObject(0);
                if (gjjContend != null) {
                    String cpay = gjjContend.getString("cpay");
                    String cstate = gjjContend.getString("cstate");
                    String balance = gjjContend.getString("cbalance");
                    fundBean.setStatus(cstate);
                    if (StringUtils.isNotEmpty(balance)) {
                        fundBean.setTitle("总金额:" + String.format(balance, "%.2f"));
                    }
                    if (StringUtils.isNumeric(cpay)) {
                        fundBean.setMounthTotal(Double.parseDouble(cpay));
                    }
                    logger.info("月缴纳额度:{}", cpay);
                    logger.info("公积金缴纳状态:{}", cstate);
                    JSONArray records = gjjContend.getJSONArray("records");
                    int month = 0;
                    String lastUpdateMonth = "";
                    for (int i = 0; i < records.size(); i++) {
                        JSONArray json0 = records.getJSONObject(i).getJSONArray("record");
                        for (int j = 0; j < json0.size(); j++) {
                            JSONObject tem = json0.getJSONObject(j);
                            if (j == 0 && StringUtils.isEmpty(lastUpdateMonth)) {
                                lastUpdateMonth = tem.getString("item1");
                            }
                            String item2 = tem.getString("item2");
                            if (StringUtils.isNotEmpty(item2) && item2.contains("汇缴") && item2.contains("公积金")) {
                                month++;
                            }
                        }
                    }
                    fundBean.setMounthNum(month);
                    fundBean.setLastUpdateMonth(lastUpdateMonth);
                    logger.info("month 缴纳月数:{}", month);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return fundBean;
    }

    /**
     * 获得社保数据
     *
     * @param scoreBean
     * @return
     */
    public static SocialInsuranceBean getSbBean(CreditScoreBean scoreBean) {
        SocialInsuranceBean bean = new SocialInsuranceBean();
        String url = SystemConfig.get("zxURL.sb_url2");
        logger.info("社保地址:{}", url);
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("accessToken", scoreBean.getAccessToken());
        mapParams.put("appId", scoreBean.getAppId());
        mapParams.put("businessType", "1");
        mapParams.put("source", String.valueOf(scoreBean.getSource()));
        mapParams.put("releaseVersion", "1.9.0");
        String result = HttpClientUtil.callHttpPost_Map(url, mapParams);
        logger.info("请求的社保结果:{}", result);
        JSONObject json = JSONObject.parseObject(result);
        if (json != null && json.get("code") != null) {
            if (1 == json.getInteger("code")) {
                bean.setTitle("综合社保");
                return bean;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
