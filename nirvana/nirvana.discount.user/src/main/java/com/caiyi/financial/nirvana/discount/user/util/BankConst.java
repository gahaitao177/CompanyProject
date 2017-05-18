package com.caiyi.financial.nirvana.discount.user.util;

import java.util.HashMap;

/**
 * Created by dengh on 2016/8/2.
 */
public class BankConst {
    public final static HashMap<Integer, String> Bank = new HashMap<Integer, String>();
    static{
        //select 'Bank.put('||ibankid||', "'||cbankname||'");' from tb_bank order by ibankid;
        Bank.put(1, "广发银行");
        Bank.put(2, "中信银行");
        Bank.put(3, "光大银行");
        Bank.put(4, "农业银行");
        Bank.put(5, "花旗银行");
        Bank.put(6, "渣打银行");
        Bank.put(7, "平安银行");
        Bank.put(8, "华夏银行");
        Bank.put(9, "浦发银行");
        Bank.put(10, "兴业银行");
        Bank.put(11, "民生银行");
        Bank.put(12, "北京银行");
        Bank.put(13, "建设银行");
        Bank.put(14, "工商银行");
        Bank.put(15, "中国银行");
        Bank.put(16, "交通银行");
        Bank.put(17, "广州银行");
        Bank.put(18, "包商银行");
        Bank.put(19, "上海银行");
        Bank.put(20, "重庆银行");
        Bank.put(21, "招商银行");
        Bank.put(41, "邮政银行");
        Bank.put(42, "中国银联");
        Bank.put(43, "天津银行");
        Bank.put(61, "江苏银行");
        Bank.put(62, "江苏农信");
        Bank.put(63, "杭州联合");
        Bank.put(64, "杭州银行");
        Bank.put(65, "徽商银行");
        Bank.put(66, "宁波银行");
    }
}