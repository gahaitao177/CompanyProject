package com.caiyi.financial.nirvana.ccard.investigation.bo;


/**
 * Created by chenli on 2017/3/21.
 */
public class CreditScoreBO {

    //身份特质
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 婚姻
     */
    private String marriage;
    /**
     * 学历
     */
    private Integer eduCode;
    /**
     * 工作年限（公积金月数）
     */
    private Integer workingLife;
    /**
     * 职称
     */
    private String titleName;
    /**
     * 芝麻信用分
     */
    private Integer zhimaScore;

    //信用历史

    /**
     * 信用卡还款记录
     */
    private Integer creditRepaymentRecord;
    /**
     * 贷款还款记录
     */
    private Integer loanRepaymentRecord;
    /**
     * 信用卡开户时长
     */
    private Integer openTime;
    /**
     * 最近一年信用卡贷款通过率
     */
    private Double passLoanRate;


    //行为偏好

    /**
     * 月消费频次
     */
    private Integer monthConsumerTime;
    /**
     * 消费金额
     */
    private Double money;

    //履约能力


    /**
     * 信用卡额度
     */
    private Double creditLimit;
    /**
     * 信用卡数量
     */
    private Integer cardNum;
    /**
     * 信用卡还款率rate
     */
    private Double repaymentRate;
    /**
     * low是否还上最低还款额，0 等于最低还款额 -1 低于  1 高于
     */
    private Integer lowRepayment;
    /**
     * 公积金月缴额
     */
    private Double gjjMonth;
    /**
     * 社保月缴额
     */
    private Double sbMonth;
    /**
     * 个人房产
     */
    private Integer house;
    /**
     * 个人车产
     */
    private Integer car;
    //违约历史
    /**
     * 违约历史
     */
    private Integer contractHistory;
    //其他
    /**
     * 其他
     */
    private Integer other;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public Integer getEduCode() {
        return eduCode;
    }

    public void setEduCode(Integer eduCode) {
        this.eduCode = eduCode;
    }

    public Integer getWorkingLife() {
        return workingLife;
    }

    public void setWorkingLife(Integer workingLife) {
        this.workingLife = workingLife;
    }

    public Integer getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Integer openTime) {
        this.openTime = openTime;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public Integer getZhimaScore() {
        return zhimaScore;
    }

    public void setZhimaScore(Integer zhimaScore) {
        this.zhimaScore = zhimaScore;
    }

    public Integer getCreditRepaymentRecord() {
        return creditRepaymentRecord;
    }

    public void setCreditRepaymentRecord(Integer creditRepaymentRecord) {
        this.creditRepaymentRecord = creditRepaymentRecord;
    }

    public Integer getLoanRepaymentRecord() {
        return loanRepaymentRecord;
    }

    public void setLoanRepaymentRecord(Integer loanRepaymentRecord) {
        this.loanRepaymentRecord = loanRepaymentRecord;
    }


    public Double getPassLoanRate() {
        return passLoanRate;
    }

    public void setPassLoanRate(Double passLoanRate) {
        this.passLoanRate = passLoanRate;
    }

    public Integer getMonthConsumerTime() {
        return monthConsumerTime;
    }

    public void setMonthConsumerTime(Integer monthConsumerTime) {
        this.monthConsumerTime = monthConsumerTime;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public Double getRepaymentRate() {
        return repaymentRate;
    }

    public void setRepaymentRate(Double repaymentRate) {
        this.repaymentRate = repaymentRate;
    }

    public Integer getLowRepayment() {
        return lowRepayment;
    }

    public void setLowRepayment(Integer lowRepayment) {
        this.lowRepayment = lowRepayment;
    }

    public Double getGjjMonth() {
        return gjjMonth;
    }

    public void setGjjMonth(Double gjjMonth) {
        this.gjjMonth = gjjMonth;
    }

    public Double getSbMonth() {
        return sbMonth;
    }

    public void setSbMonth(Double sbMonth) {
        this.sbMonth = sbMonth;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Integer getCar() {
        return car;
    }

    public void setCar(Integer car) {
        this.car = car;
    }

    public Integer getContractHistory() {
        return contractHistory;
    }

    public void setContractHistory(Integer contractHistory) {
        this.contractHistory = contractHistory;
    }

    public Integer getOther() {
        return other;
    }

    public void setOther(Integer other) {
        this.other = other;
    }
}
