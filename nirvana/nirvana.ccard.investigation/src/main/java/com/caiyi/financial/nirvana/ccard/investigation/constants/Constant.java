package com.caiyi.financial.nirvana.ccard.investigation.constants;

/**
 * Created by lizhijie on 2016/12/8. 征信常量
 */
public class Constant {
    public  static  int SCORE_LEVEL_BAD=1; //较差
    public  static  int SCORE_LEVEL_COMMON=2;//中等
    public  static  int SCORE_LEVEL_GOOD=3;//良好
    public  static  int SCORE_LEVEL_BETTER=4;//优秀
    public  static  int SCORE_LEVEL_BEST=5;//极好

    //征信积分等级名称
    public  static  String SCORE_LEVEL_BAD_NAME="信用较差";
    public  static  String SCORE_LEVEL_COMMON_NAME="信用中等";
    public  static  String SCORE_LEVEL_GOOD_NAME="信用良好";
    public  static  String SCORE_LEVEL_BETTER_NAME="信用优秀";
    public  static  String SCORE_LEVEL_BEST_NAME="信用极好";

    public static  int ZX_BASE_NUM0=350; //征信、社保、信用卡和公积金的基础分
    public static  int ZX_BASE_NUM1=201; //征信、社保、信用卡和公积金的基础分

    //征信子表积分计算常量
    public static  int CREDIT_INVESTIGATION_NO_OVERSTAY_BIG=50;  //无逾期
    public static  int CREDIT_INVESTIGATION_NO_OVERSTAY_SMALL=5;

    public static  int CREDIT_INVESTIGATION_HAVE_CARD_BIG=50;  //有信用卡
    public static  int CREDIT_INVESTIGATION_HAVE_CARD_SMALL=5;

    public static  int CREDIT_INVESTIGATION_HAVE_LOAN_BIG=100; //有贷款
    public static  int CREDIT_INVESTIGATION_HAVE_LOAN_SMALL=10;

    //信用卡表积分计算常量 额度
    public static  int CREDIT_CARD_TOTAL_0TO1_BIG=50; //额度 0-1W
    public static  int CREDIT_CARD_TOTAL_0TO1_SMALL=5;

    public static  int CREDIT_CARD_TOTAL_1TO5_BIG=100; //额度 1-5W
    public static  int CREDIT_CARD_TOTAL_1TO5_SMALL=10;

    public static  int CREDIT_CARD_TOTAL_5TO10_BIG=150; //额度 5-10W
    public static  int CREDIT_CARD_TOTAL_5TO10_SMALL=15;

    public static  int CREDIT_CARD_TOTAL_OVER10_BIG=200; //额度 10W以上
    public static  int CREDIT_CARD_TOTAL_OVER10_SMALL=20;

    //公积金表积分计算常量
    public static  int GJJ_STATUS_NORMAL_BIG=50; //状态 正常
    public static  int GJJ_STATUS_NORMAL_SMALL=5;

    public static  int GJJ_MONTHS6_LOW_BIG=20; //缴纳月数 小于6
    public static  int GJJ_MONTHS6_LOW_SMALL=2;

    public static  int GJJ_MONTHS6_UP_BIG=50; //缴纳月数 小于6
    public static  int GJJ_MONTHS6_UP_SMALL=5;

    public static  int GJJ_MONTH_TOTAL500_LOW_BIG=20; //缴纳额度500以下
    public static  int GJJ_MONTH_TOTAL500_LOW_SMALL=2;

    public static  int GJJ_MONTH_TOTAL1000_LOW_BIG=30; //缴纳额度500以上,1000以下
    public static  int GJJ_MONTH_TOTAL1000_LOW_SMALL=3;

    public static  int GJJ_MONTH_TOTAL1000_UP_BIG=50; //缴纳额度1000以上
    public static  int GJJ_MONTH_TOTAL1000_UP_SMALL=5;

    //学信表积分计算常量
    public static  int XX_LEVEL_COLLEGE=5; //专科
    public static  int XX_LEVEL_UNDERGRADUATE=10; //本科
    public static  int XX_LEVEL_MASTER=15; //硕士
    public static  int XX_LEVEL_DOCTOR=20; //博士

    public static  String SCORE_LEVELS = "350,550,600,650,700,950";
    public static  String SCORE_LEVEL_NAMES = "较差,中等,良好,优秀,极好";

}
