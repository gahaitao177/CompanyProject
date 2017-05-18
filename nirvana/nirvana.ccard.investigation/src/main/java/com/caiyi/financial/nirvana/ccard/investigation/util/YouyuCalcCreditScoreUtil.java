package com.caiyi.financial.nirvana.ccard.investigation.util;

import com.caiyi.financial.nirvana.ccard.investigation.annotations.ZxBaseItem;

/**
 * Created by Linxingyu on 2017/3/16.
 */
public class YouyuCalcCreditScoreUtil {
    /**
     * 身份特质——年龄
     *
     * @return
     */
    @ZxBaseItem("CREDIT_IDENTITY_AGE")
    public static int getAgeScore(int age) {
//        int age = csbo.getAge();
        if (age >= 50) {
            return 10;
        }
        if (age >= 40) {
            return 50;
        }
        if (age >= 30) {
            return 100;
        }
        if (age >= 25) {
            return 80;
        }
        if (age >= 19) {
            return 50;
        }
        return 0;
    }

    /**
     * 身份特质——婚姻
     *
     * @return
     */
    public static int getMarriageScore(String mar) {
        if ("已婚".equals(mar)) {
            return 100;
        }
        if ("未婚".equals(mar)) {
            return 50;
        }
        return 0;
    }

    /**
     * 身份特质——学历
     *
     * @return
     */
    public static int getEducationScore(int eduCode) {
        if (1 == eduCode) {
            return 60;//大专
        }
        if (2 == eduCode) {
            return 70;//本科
        }
        if (3 == eduCode) {
            return 80;//硕士
        }
        if (4 == eduCode) {
            return 100;//博士
        }
        return 10;//大专以下
    }

    /**
     * 身份特质——工作年限
     *
     * @param num 公积金月数
     * @return
     */
    public static int getWorkTimeScore(int num) {
        if (num >= 240) {
            return 100;
        }
        if (num >= 60) {
            return 80;
        }
        if (num >= 36) {
            return 60;
        }
        if (num >= 12) {
            return 40;
        }
        return 10;
    }

    /**
     * 身份特质——职称
     *
     * @return
     */
    public static int getTitleScore(String titleName) {
        if ("初级".equals(titleName)) {
            return 10;
        }
        if ("中级".equals(titleName)) {
            return 60;
        }
        if ("副高级".equals(titleName)) {
            return 80;
        }
        if ("高级".equals(titleName)) {
            return 100;
        }
        return 10;
    }

    /**
     * 身份特质——芝麻信用分
     *
     * @return
     */
    public static int getZhiMaScore(int score) {
        if (score >= 700) {
            return 100;
        }
        if (score >= 650) {
            return 80;
        }
        if (score >= 600) {
            return 60;
        }
        if (score >= 550) {
            return 40;
        }
        return 0;
    }

    /**
     * 信用历史——信用卡还款记录
     * @param cardOverdueStatus 信用卡逾期状态  0  未逾期  1  有逾期
     * @return
     */
    public static int getCardRepayScore(Integer cardOverdueStatus) {
        if (0 == cardOverdueStatus) {
            return 100;
        }
        return 0;
    }

    /**
     * 信用历史——贷款还款记录
     *
     * @param loanOverdueStatus 贷款逾期状态  0  未逾期  1  有逾期
     * @return
     */
    public static int getLoanRepayScore(Integer loanOverdueStatus) {
        if (0 == loanOverdueStatus) {
            return 100;
        }
        return 0;
    }

    /**
     * 信用历史——信用卡开户时长
     *
     * @return
     */
    public static int getCardTimeScore(int num) {
        if (num >= 10 * 24) {
            return 100;
        }
        if (num >= 5 * 24) {
            return 80;
        }
        if (num >= 2 * 24) {
            return 60;
        }
        return 40;
    }

    /**
     * 信用历史——最近一年信用卡贷款通过率
     *
     * @return
     */
    public static int getLoanPassScore() {
        return 0;
    }

    /**
     * 行为偏好——月消费频次
     *
     * @return
     */
    public static int getConsumeNumScore(int num) {
        if (num >= 48) {
            return 100;
        }
        if (num >= 12) {
            return 80;
        }
        if (num >= 4) {
            return 10;
        }

        return 0;
    }

    /**
     * 行为偏好——消费金额
     *
     * @return
     */
    public static int getConsumeMoneyScore(double money) {
        if (money > 20000) {
            return 100;
        }
        if (money > 10000) {
            return 90;
        }
        if (money > 5000) {
            return 80;
        }
        if (money > 1000) {
            return 60;
        }
        return 20;
    }

    /**
     * 履约能力——信用卡额度
     *
     * @return
     */
    public static int getCardQuotaScore(double money) {
        if (money > 100000) {
            return 100;
        }
        if (money > 50000) {
            return 90;
        }
        if (money > 10000) {
            return 80;
        }
        return 30;
    }

    /**
     * 履约能力——信用卡数量
     *
     * @return
     */
    public static int getCardAmountScore(int num) {
        if (num >= 5) {
            return 50;
        }
        if (num == 4) {
            return 80;
        }
        if (num >= 2) {
            return 100;
        }
        return 50;
    }

    /**
     * 履约能力——信用卡还款率
     *
     * @param low  是否还上最低还款额，0 等于最低还款额 -1 低于  1 高于
     * @param rate 还款率
     * @return
     */
    public static int getCardRepayRateScore(int low, double rate) {
        if (-1 == low) {
            //低于最低还款额
            return 0;
        }
        if (0 == low) {
            //最低还款额
            return 10;
        }
        if (rate < 0.6) {
            return 40;
        }
        if (rate > 0.6 && rate < 1) {
            return 60;
        }
        return 100;
    }

    /**
     * 履约能力——公积金月缴额
     *
     * @return
     */
    public static int getGJJScore(double money) {
        return 0;
    }

    /**
     * 履约能力——社保月缴额
     *
     * @return
     */
    public static int getSBScore() {
        return 0;
    }

    /**
     * 履约能力——个人房产
     *
     * @return
     */
    public static int getHouseScore() {
        return 0;
    }

    /**
     * 履约能力——个人车产
     *
     * @return
     */
    public static int getCarScore() {
        return 0;
    }

    /**
     * 违约历史
     *
     * @return
     */
    public static int getBreakScore() {
        return 0;
    }

    /**
     * 其他
     *
     * @return
     */
    public static int getOtherScore() {
        return 0;
    }


}
