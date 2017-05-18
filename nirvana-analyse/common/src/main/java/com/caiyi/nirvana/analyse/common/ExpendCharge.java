package com.caiyi.nirvana.analyse.common;

import java.text.NumberFormat;

/**
 * @author DONGYA
 */
public class ExpendCharge {
    private String name; //支出名称
    private String icoin; //图标
    private String ratio; //占比
    private String money; //金额
    private String sumMoney; //总金额

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcoin() {
        return icoin;
    }

    public void setIcoin(String icoin) {
        this.icoin = icoin;
    }

    public String getRatio() {
        if (CheckUtil.isNullString(getSumMoney()) || Double.parseDouble(getSumMoney()) <= 0d) {
            ratio = "0%";
        } else {
            double value = Double.parseDouble(getMoney()) / Double.parseDouble(getSumMoney());
            NumberFormat fmt = NumberFormat.getPercentInstance();
            fmt.setMaximumFractionDigits(0);//最多两位百分小数，如25.23%
            ratio = fmt.format(value);

        }

        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(String sumMoney) {
        this.sumMoney = sumMoney;
    }


}
