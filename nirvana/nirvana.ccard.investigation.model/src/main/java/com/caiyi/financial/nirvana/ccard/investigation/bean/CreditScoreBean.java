package com.caiyi.financial.nirvana.ccard.investigation.bean;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

/**
 * Created by jianghao on 2016/12/1.
 */
public class CreditScoreBean extends BaseBean {
    private int creditId;//信用ID
    private int zxId;//征信ID
    private int gjjId;//公积金id
    private int sbId;//社保id
    private int xykId;//信用卡id
    private int privilegeCount;//等级特权数
    private String updateTime;//更新时间
    private String addTime;//添加时间
    private int creditScores;//用户积分
    private int levelCode;//信用积分等级
    private String levelName;//信用积分等级名
    private int updateCount;//更新次数
    private int monthScoreId;//月次分数id
    private int ruleCode;//更新规则编号
    private String cfDesc;//信用积分流水表描述
    private int forceCaculate; //是否需要更新计算积分
    private int countProject; //拥有的项目数
    private String  flowDesc; //描述积分更新

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public int getZxId() {
        return zxId;
    }

    public void setZxId(int zxId) {
        this.zxId = zxId;
    }

    public int getGjjId() {
        return gjjId;
    }

    public void setGjjId(int gjjId) {
        this.gjjId = gjjId;
    }

    public int getSbId() {
        return sbId;
    }

    public void setSbId(int sbId) {
        this.sbId = sbId;
    }

    public int getXykId() {
        return xykId;
    }

    public void setXykId(int xykId) {
        this.xykId = xykId;
    }

    public int getPrivilegeCount() {
        return privilegeCount;
    }

    public void setPrivilegeCount(int privilegeCount) {
        this.privilegeCount = privilegeCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getCreditScores() {
        return creditScores;
    }

    public void setCreditScores(int creditScores) {
        this.creditScores = creditScores;
    }

    public int getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(int levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getMonthScoreId() {
        return monthScoreId;
    }

    public void setMonthScoreId(int monthScoreId) {
        this.monthScoreId = monthScoreId;
    }

    public int getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(int ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getCfDesc() {
        return cfDesc;
    }

    public void setCfDesc(String cfDesc) {
        this.cfDesc = cfDesc;
    }

    public int getForceCaculate() {
        return forceCaculate;
    }

    public void setForceCaculate(int forceCaculate) {
        this.forceCaculate = forceCaculate;
    }

    public int getCountProject() {
        return countProject;
    }

    public void setCountProject(int countProject) {
        this.countProject = countProject;
    }

    public String getFlowDesc() {
        return flowDesc;
    }

    public void setFlowDesc(String flowDesc) {
        this.flowDesc = flowDesc;
    }

}
