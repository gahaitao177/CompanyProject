package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 学历
 */
public enum Degree {
    //degree（学历）：（1、硕士 2、博士及以上 3、本科 4、大专 5、高中、中专一下）

    //education//教育程度 5：硕士 6：博士及以上 4：本科 3：大专 2：中专 1：高中及以下 7：其他。 默认4

    A("1", "硕士", "5", "硕士"),
    B("2", "博士及以上", "6", "博士及以上"),
    C("3", "本科", "4", "本科"),
    D("4", "大专", "3", "大专"),
    E("5", "高中、中专一下", "1", "高中及以下");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    Degree(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "Degree{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static Degree getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        Degree[] ps = Degree.values();
        for (Degree p : ps) {
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
        Degree[] ps = Degree.values();
        for (Degree p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}
