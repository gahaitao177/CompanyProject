package com.caiyi.financial.nirvana.ccard.investigation.dto;

/**
 * Created by jianghao on 2016/11/29.
 * 对应表 TB_ZX_CREDIT_SCORE
 */
public class CreditScoreDto {
    private int creditId;//信用ID
    private String userId;//用户id
    private int zxId;//征信ID
    private int gjjId;//公积金id
    private int sbId;//社保id
    private int xykId;//信用卡id
    private int privilegeCount;//等级特权数
    private String updateDate;//更新时间
    private String addTime;//添加时间
    private int creditScores;//用户积分
    private String levelCode;//信用积分等级
    private String levelName;//信用积分等级
    private int monthId; //积分月份表id
    private int rankNum;//排名
    private String month; //月份 格式 201612
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return updateDate;
    }

    public void setUpdateTime(String updateTime) {
        this.updateDate = updateTime;
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

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public int getMonthId() {
        return monthId;
    }

    public void setMonthId(int monthId) {
        this.monthId = monthId;
    }

    public int getRankNum() {
        return rankNum;
    }

    public void setRankNum(int rankNum) {
        this.rankNum = rankNum;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

}
