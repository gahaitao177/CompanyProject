package com.caiyi.financial.nirvana.ccard.material.banks.guangda2.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 行业性质
 */
public enum NatureOfBusiness {
    //natureOfBusiness(行业性质)：（1、金融业 2、IT通讯 3、服务业 4、制造业 5、建筑地产
    // 6、商贸零售 7、运输物流 8、法律咨询 9、教育科研 10、医疗卫生 11、机关团体 12、体育娱乐 13、旅游酒店餐饮 14、其它）

    //单位所属行业 1：政府部门 2：科教文卫 3：邮电通讯 4：部队 5：IT/网络/计算机 6：商业/贸易 7：银行
    // 8：证券/投资/保险 9：制造业 10：农林畜牧 11：广告 12：旅游/餐饮/娱乐 13：交通运输 14：会计/律师 15：房地产/建筑/装饰 16：其他
    A("1", "金融业", "7", "银行"),
    B("2", "IT通讯", "5", "IT/网络/计算机"),
    C("3", "服务业", "16", "其他"),
    D("4", "制造业", "9", "制造业"),
    E("5", "建筑地产", "15", "房地产/建筑/装饰"),
    F("6", "商贸零售", "6", "商业/贸易"),
    G("7", "运输物流", "13", "交通运输"),
    H("8", "法律咨询", "14", "会计/律师"),
    I("9", "教育科研", "2", "科教文卫"),
    J("10", "医疗卫生", "2", "科教文卫"),
    K("11", "机关团体", "1", "政府部门"),
    L("12", "体育娱乐", "12", "旅游/餐饮/娱乐"),
    M("13", "旅游酒店餐饮", "12", "旅游/餐饮/娱乐"),
    N("14", "其它", "16", "其他");

    private String localKey;
    private String localValue;
    private String guangDaKey;
    private String guangDaValue;

    NatureOfBusiness(String localKey, String localValue, String guangDaKey, String guangDaValue) {
        this.localKey = localKey;
        this.localValue = localValue;
        this.guangDaKey = guangDaKey;
        this.guangDaValue = guangDaValue;
    }

    @Override
    public String toString() {
        return "NatureOfBusiness{" +
                "localKey='" + localKey + '\'' +
                ", localValue='" + localValue + '\'' +
                ", guangDaKey='" + guangDaKey + '\'' +
                ", guangDaValue='" + guangDaValue + '\'' +
                '}';
    }

    public static NatureOfBusiness getByGuangDaKey(String guangDaKey) {
        if (StringUtils.isEmpty(guangDaKey)) {
            return null;
        }
        NatureOfBusiness[] ps = NatureOfBusiness.values();
        for (NatureOfBusiness p : ps) {
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
        NatureOfBusiness[] ps = NatureOfBusiness.values();
        for (NatureOfBusiness p : ps) {
            if (p.localKey.equals(localKey)) {
                return p.guangDaKey;
            }
        }
        return null;
    }
}