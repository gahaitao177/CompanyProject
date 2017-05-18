package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 职务
 */
public enum Post {
    //post(职务)：（1、一般员工 2、部门经理/处级 3、总经理/局级以上 4、主管/科级）

    //duty;//职务 1 ：单位公司主管/局级以上；2 ：部门主管/处级；3 ：科室经理；4 ：员工；5 ：其他；6 ：法人代表；7 ：股东/合伙人；
    A("1", "一般员工", "4", "员工"),
    B("2", "部门经理/处级", "2", "部门主管/处级"),
    C("3", "总经理/局级以上", "1", "单位公司主管/局级以上"),
    D("4", "主管/科级", "2", "科室经理");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    Post(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "Post{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static Post getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        Post[] ps = Post.values();
        for (Post p : ps) {
            if (p.guangDaKey.equals(guangDaKey)) {
                return p;
            }
        }
        return null;
    }

    public static String getGuangDaKey(String localKey) {
        if (StringUtils.isEmpty(localKey)) {
            throw new IllegalArgumentException("localKey is empty!!!");
        }
        Post[] ps = Post.values();
        for (Post p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}