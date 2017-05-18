package com.caiyi.nirvana.analyse.common;

import java.text.DecimalFormat;

public class ModUtil {
    private String name;
    private String money;
    private String income; //收入
    private String expend; //支出
    private String rate; //结余

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        if (CheckUtil.isNullString(money)) {
            money = "0.00";
        } else {
            money = new DecimalFormat("#.00").format(Double.parseDouble(money));
        }
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getIncome() {
        if (CheckUtil.isNullString(income)) {
            income = "0.00";
        } else {
            income = new DecimalFormat("#.00").format(Double.parseDouble(income));
        }
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpend() {
        if (CheckUtil.isNullString(expend)) {
            expend = "0.00";
        } else {
            expend = new DecimalFormat("#.00").format(Double.parseDouble(expend));
        }
        return expend;
    }

    public void setExpend(String expend) {
        this.expend = expend;
    }

    public String getRate() {
        double value = Double.parseDouble(getIncome()) - Double.parseDouble(getExpend());
        return new DecimalFormat("#.00").format(value);
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String toString() {
        return "modUtil【name:" + getName() + ";money:" + getMoney() + ";income:" + getIncome() + ";expend:" + getExpend() + ";rate:" + getRate() + "】";
    }

    public static void main(String[] args) {
        String value = "300.1415927";
        System.out.println(new DecimalFormat("#.00").format(Double.parseDouble(value)));
    }

}
