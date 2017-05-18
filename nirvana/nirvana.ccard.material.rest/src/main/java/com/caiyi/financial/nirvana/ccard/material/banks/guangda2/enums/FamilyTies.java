package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 亲属关系
 */
public enum FamilyTies {
    //familyTies(亲属关系):(1.配偶 2.父母 3.子女 4.兄弟姐妹)

    //relation;//直亲关系1：父母；2：配偶；3：子女；4：其他；

    A("1", "配偶", "2", "配偶"),
    B("2", "父母", "1", "父母"),
    C("3", "子女", "3", "子女"),
    D("4", "兄弟姐妹", "4", "其他");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    FamilyTies(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "FamilyTies{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static FamilyTies getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        FamilyTies[] ps = FamilyTies.values();
        for (FamilyTies p : ps) {
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
        FamilyTies[] ps = FamilyTies.values();
        for (FamilyTies p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}