package com.caiyi.financial.nirvana.ccard.bill.bank.constant;

import com.caiyi.financial.nirvana.ccard.bill.bean.BankBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Linxingyu on 2017/1/12.
 */
public class BankList {
    public final static List<BankBean> BANK_LIST;

    static {
        List<BankBean> bankList = new ArrayList<>();
        bankList.add(new BankBean(1, "广发银行", "160120"));
        bankList.add(new BankBean(2, "中信银行", "160147"));
        bankList.add(new BankBean(3, "光大银行", "160113"));
        bankList.add(new BankBean(4, "农业银行", "160107"));
        bankList.add(new BankBean(5, "花旗银行", "160124"));

        bankList.add(new BankBean(7, "平安银行", "160117"));
        bankList.add(new BankBean(8, "华夏银行", "160110"));
        bankList.add(new BankBean(9, "浦发银行", "160115"));
        bankList.add(new BankBean(10, "兴业银行", "160118"));
        bankList.add(new BankBean(11, "民生银行", "160112"));
        bankList.add(new BankBean(12, "北京银行", "160119"));
        bankList.add(new BankBean(13, "建设银行", "160106"));
        bankList.add(new BankBean(14, "工商银行", "160105"));

        bankList.add(new BankBean(16, "交通银行", "160108"));

        bankList.add(new BankBean(19, "上海银行", "160114"));

        bankList.add(new BankBean(21, "招商银行", "160109"));

        bankList.add(new BankBean(41, "邮政银行", "160139"));

        bankList.add(new BankBean(67, "浙商银行", "160100"));

        BANK_LIST = Collections.unmodifiableList(bankList);
    }
}
