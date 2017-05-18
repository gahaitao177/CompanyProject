package com.caiyi.financial.nirvana.discount.tools.bean;

import java.io.Serializable;

/**
 * Created by lizhijie on 2016/8/10.
 */
public class FinancialProductBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 通用返回值 1 2 表示成功返回结果 其它code值均表示失败
     */
    private String mCode;
    /**
     * 返回描述信息
     */
    private String mDesc;

    private String pid;

    private String pCode;

    private String pName;

    /**七日年化收益率*/
    private String mYearRate;
    /**每万份收益*/
    private String mDayProfit;

    private String pMinValue; //购买门槛

    private String pCachNum; //提现额度

    private String pCachTime; //提现到账时间

    private String pBindFund; //绑定基金

    private String pBackground; //背景

    private String pAnalysis; //专家分析

    private int pPriority = 0; //优先级

    private String pLogo;

    private String pUpdateTime; //更新时间

    private int profitNum;//收益好

    private int convenienceNum;//方便

    private int inTimeNum;//到账及时

    private int safeNum; //安全性高
    /**发起人*/
    private String mCreater ;
    /**提款额度特殊说明*/
    private String mCachTips;
    /**七天历史收益率 下划线分隔*/
    private String mHistoryProfit = "0,0,0,0,0,0";
    /**七天时间点 用下划线分隔*/
    private String mHistoryDate = "0,0,0,0,0,0";

    private String startPerson;

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    /**
     * 业务数据返回结果 json串
     */
    private String mJsonData;

    public String getStartPerson() {
        return startPerson;
    }

    public void setStartPerson(String startPerson) {
        this.startPerson = startPerson;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpMinValue() {
        return pMinValue;
    }

    public void setpMinValue(String pMinValue) {
        this.pMinValue = pMinValue;
    }

    public String getpCachNum() {
        return pCachNum;
    }

    public void setpCachNum(String pCachNum) {
        this.pCachNum = pCachNum;
    }

    public String getpCachTime() {
        return pCachTime;
    }

    public void setpCachTime(String pCachTime) {
        this.pCachTime = pCachTime;
    }

    public String getpBindFund() {
        return pBindFund;
    }

    public void setpBindFund(String pBindFund) {
        this.pBindFund = pBindFund;
    }

    public String getpBackground() {
        return pBackground;
    }

    public void setpBackground(String pBackground) {
        this.pBackground = pBackground;
    }

    public String getpAnalysis() {
        return pAnalysis;
    }

    public void setpAnalysis(String pAnalysis) {
        this.pAnalysis = pAnalysis;
    }

    public int getpPriority() {
        return pPriority;
    }

    public void setpPriority(int pPriority) {
        this.pPriority = pPriority;
    }

    public String getpLogo() {
        return pLogo;
    }

    public void setpLogo(String pLogo) {
        this.pLogo = pLogo;
    }

    public String getpUpdateTime() {
        return pUpdateTime;
    }

    public void setpUpdateTime(String pUpdateTime) {
        this.pUpdateTime = pUpdateTime;
    }

    public int getProfitNum() {
        return profitNum;
    }

    public void setProfitNum(int profitNum) {
        this.profitNum = profitNum;
    }

    public int getConvenienceNum() {
        return convenienceNum;
    }

    public void setConvenienceNum(int convenienceNum) {
        this.convenienceNum = convenienceNum;
    }

    public int getInTimeNum() {
        return inTimeNum;
    }

    public void setInTimeNum(int inTimeNum) {
        this.inTimeNum = inTimeNum;
    }

    public int getSafeNum() {
        return safeNum;
    }

    public void setSafeNum(int safeNum) {
        this.safeNum = safeNum;
    }

    /**
     * 设置七日年化收益率
     * @param value 七日年化收益率
     */
    public void setYearRate(String value) {
        this.mYearRate = value;
    }

    /**
     * 获取七日年化收益率
     * @return 七日年化收益率
     */
    public String getYearRate() {
        return this.mYearRate;
    }

    /**
     * 设置日万份收益
     * @param value 日万份收益
     */
    public void setDayProfit(String value) {
        this.mDayProfit = value;
    }

    /**
     * 获取日万份收益
     * @return 日万份收益
     */
    public String getDayProfit() {
        return this.mDayProfit;
    }

    /**
     * 设置发起人
     * @param value 发起人
     */
    public void setCreater(String value) {
        this.mCreater = value;
    }

    /**
     * 获取发起人
     * @return 发起人
     */
    public String getCreater() {
        return this.mCreater;
    }

    /**
     * 设置提款特殊说明
     * @param value 提款特殊说明
     */
    public void setCachTips(String value) {
        this.mCachTips = value;
    }

    /**
     * 获取提款特殊说明
     * @return 提款特殊说明
     */
    public String getCachTips() {
        return this.mCachTips;
    }

    /**
     * 设置七日收益值 下划线分隔
     * @param value 七日收益值 下划线分隔
     */
    public void setHistoryProfit(String value) {
        this.mHistoryProfit = value;
    }

    /**
     * 获取七日收益值 下划线分隔
     * @return 七日收益值 下划线分隔
     */
    public String getHistoryProfit() {
        return this.mHistoryProfit;
    }

    /**
     * 设置七日时间点 下划线分隔
     * @param value 七日收益值 下划线分隔
     */
    public void setHistoryDate(String value) {
        this.mHistoryDate = value;
    }

    /**
     * 获取七日时间点 下划线分隔
     * @return 七日收益值 下划线分隔
     */
    public String getHistoryDate() {
        return this.mHistoryDate;
    }

    /**
     * 设置业务数据json
     * @param data 业务数据json
     */
    public void setJsonData(String data) {
        this.mJsonData = data;
    }

    /**
     * 获取业务数据json
     * @return 业务数据json
     */
    public String getJsonData() {
        return this.mJsonData;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if(mJsonData!=null){
            ret.append("{");
            ret.append("\"code\":"+this.getmCode()+",");
            ret.append("\"desc\":\""+this.getmDesc()+"\",");
            if(this.getJsonData().contains("{") || this.getJsonData().contains("[")){
                ret.append("\"data\":"+this.getJsonData()+"");
            }else{
                ret.append("\"data\":\""+this.getJsonData()+"\"");
            }
            ret.append("}");

        }else{
            ret.append("{");
            ret.append("\"code\":"+this.getmCode()+",");
            ret.append("\"desc\":\""+this.getmDesc()+"\",");
            ret.append("\"data\":\""+this.getJsonData()+"\"");
            ret.append("}");
        }
        return ret.toString();
    }
}
