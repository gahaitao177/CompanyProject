package com.caiyi.financial.nirvana.ccard.investigation.dto;

import java.util.Date;

/**
 * Created by shaoqinghua on 2017/1/23.
 * 对应表：tb_xx_credit_education 学信学历表
 */
public class ChsiEducationDto {
    //学信学历id
    private int chsiEductionId;
    //学信账号id
    private int chsiAccountId;
    //姓名
    private String name;
    //性别 0:女 1:男
    private int sex;
    //出生日期
    private Date birthday;
    //民族
    private String nation;
    //证件编号
    private String code;
    //学校
    private String college;
    //等级 本科 研究生
    private String levels;
    //专业
    private String major;
    //学制
    private Double schooling;
    //学历类别 普通 研究生
    private String schoolingType;
    //学习形式
    private String learnForm;
    //分院
    private String department;
    //系（所、函授站）
    private String place;
    //班级
    private String iClass;
    //学号
    private String studentNo;
    //入学时间
    private Date joinTime;
    //毕业时间
    private Date graduate;
    //在籍状态 0:不在籍 1:在籍
    private int state;
    //在籍详细状态 在籍（注册学籍） 不在籍(毕业) 不在籍(退学) ....
    private String stateDetail;

    public int getChsiEductionId() {
        return chsiEductionId;
    }

    public void setChsiEductionId(int chsiEductionId) {
        this.chsiEductionId = chsiEductionId;
    }

    public int getChsiAccountId() {
        return chsiAccountId;
    }

    public void setChsiAccountId(int chsiAccountId) {
        this.chsiAccountId = chsiAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Double getSchooling() {
        return schooling;
    }

    public void setSchooling(Double schooling) {
        this.schooling = schooling;
    }

    public String getSchoolingType() {
        return schoolingType;
    }

    public void setSchoolingType(String schoolingType) {
        this.schoolingType = schoolingType;
    }

    public String getLearnForm() {
        return learnForm;
    }

    public void setLearnForm(String learnForm) {
        this.learnForm = learnForm;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getiClass() {
        return iClass;
    }

    public void setiClass(String iClass) {
        this.iClass = iClass;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getGraduate() {
        return graduate;
    }

    public void setGraduate(Date graduate) {
        this.graduate = graduate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateDetail() {
        return stateDetail;
    }

    public void setStateDetail(String stateDetail) {
        this.stateDetail = stateDetail;
    }
}
