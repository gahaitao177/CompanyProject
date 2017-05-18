package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 单位性质
 */
public enum NatureOfUnit {
    //natureOfUnit(单位性质)：（1、机关/事业 2、国有 3、股份制 4、外商独资 5、中外合作企业 6、私营/集体 7、个体）
    //单位性质 1：机关事业单位 2：三资 3：国有 4：股份制 5：私营 6：个体/自由职业者 7：其他 8：储蓄理财
    A("1", "机关/事业", "1", "机关事业单位"),
    B("2", "国有", "3", "国有"),
    C("3", "股份制", "4", "股份制"),
    D("4", "外商独资", "2", "三资"),
    E("5", "中外合作企业", "2", "三资"),
    F("6", "私营/集体", "5", "私营"),
    G("7", "个体", "6", "个体/自由职业者");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    NatureOfUnit(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "NatureOfUnit{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static NatureOfUnit getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        NatureOfUnit[] ps = NatureOfUnit.values();
        for (NatureOfUnit p : ps) {
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
        NatureOfUnit[] ps = NatureOfUnit.values();
        for (NatureOfUnit p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}