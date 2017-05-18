package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 婚姻状态
 */
public enum MaritalStatus {
    //maritalStatus(婚姻状态)：（1、未婚 2、已婚 3、其它）
    //婚姻状况  1：未婚；2：已婚；3：其他
    A("1", "未婚", "1", "未婚"),
    B("2", "已婚", "2", "已婚"),
    C("3", "其他", "3", "其他");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    MaritalStatus(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "MaritalStatus{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static MaritalStatus getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        MaritalStatus[] ps = MaritalStatus.values();
        for (MaritalStatus p : ps) {
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
        MaritalStatus[] ps = MaritalStatus.values();
        for (MaritalStatus p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}