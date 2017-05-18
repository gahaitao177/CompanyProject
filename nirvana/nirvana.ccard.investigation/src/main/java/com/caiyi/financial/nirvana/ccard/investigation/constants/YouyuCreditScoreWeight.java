package com.caiyi.financial.nirvana.ccard.investigation.constants;

/**
 * Created by Linxingyu on 2017/3/16.
 * 有鱼信用体系分数百分比设置
 */
public class YouyuCreditScoreWeight {
    //身份特质
    public static final Double CREDIT_IDENTITY = 0.1;
    public static final Double CREDIT_IDENTITY_AGE = 0.05;//年龄
    public static final Double CREDIT_IDENTITY_MARRIAGE = 0.05;//婚姻
    public static final Double CREDIT_IDENTITY_EDUCATION = 0.25;//学历
    public static final Double CREDIT_IDENTITY_WORKTIME = 0.15;//工作年限
    public static final Double CREDIT_IDENTITY_TITLE = 0.20;//职称
    public static final Double CREDIT_IDENTITY_ZHIMA = 0.25;//芝麻信用分

    //信用历史
    public static final Double CREDIT_HISTORY = 0.3;
    public static final Double CREDIT_HISTORY_CARDREPAY = 0.3;//信用卡还款记录
    public static final Double CREDIT_HISTORY_LOANREPAY = 0.3;//贷款还款记录
    public static final Double CREDIT_HISTORY_CARDTIME = 0.2;//信用卡开户时长
    public static final Double CREDIT_HISTORY_LOANPASS = 0.4;//最近一年信用卡贷款通过率
    //行为偏好
    public static final Double CREDIT_BEHAVIOR = 0.2;
    public static final Double CREDIT_BEHAVIOR_CONSUMENUM = 0.2;//月消费频次
    public static final Double CREDIT_BEHAVIOR_CONSUMEMONEY = 0.8;//消费金额
    //履约能力
    public static final Double CREDIT_CAPACITY = 0.4;
    public static final Double CREDIT_CAPACITY_CARDQUOTA = 0.25;//信用卡额度
    public static final Double CREDIT_CAPACITY_CARDAMOUNT = 0.05;//信用卡数量
    public static final Double CREDIT_CAPACITY_CARDREPAYRATE = 0.25;//信用卡还款率
    public static final Double CREDIT_CAPACITY_GJJ = 0.2;//公积金月缴额
    public static final Double CREDIT_CAPACITY_SB = 0.1;//社保月缴额
    public static final Double CREDIT_CAPACITY_HOUSE = 0.5;//房产
    public static final Double CREDIT_CAPACITY_CAR = 0.15;//车产
    //违约历史
    public static final Double CREDIT_BREAK = 0.2;
    //其他
    public static final Double CREDIT_OTHER = 0.1;
}
