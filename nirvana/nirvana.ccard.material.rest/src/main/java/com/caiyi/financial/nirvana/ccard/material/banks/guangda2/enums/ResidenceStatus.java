package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 现住宅
 */
public enum ResidenceStatus {
    //residenceStatus（现住宅）：（1、自购有贷款房 2、自有无贷款房 3、租用 4、与父母同住 5、其它）

    //housetype;//住房性质 1：商品房有按揭 2：商品房无按揭 3：已购公房 4：租用/月租房 5：单位集体宿舍 6：亲属/父母家 7：其他
    A("1", "自购有贷款房", "1", "商品房有按揭"),
    B("2", "自有无贷款房", "2", "商品房无按揭"),
    C("3", "租用", "4", "租用"),
    D("4", "与父母同住", "6", "亲属/父母家"),
    E("5", "其它", "7", "其它");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    ResidenceStatus(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "ResidenceStatus{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static ResidenceStatus getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        ResidenceStatus[] ps = ResidenceStatus.values();
        for (ResidenceStatus p : ps) {
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
        ResidenceStatus[] ps = ResidenceStatus.values();
        for (ResidenceStatus p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}