package com.caiyi.financial.nirvana.ccard.material.banks.cityBank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lichuanshun on 16/6/16.
 */
public class CityBankUtils {
    // 花旗银行申请地址
    public static final String CITYBANK_APPLY_ADDRESS = "https://www.citibank.com.cn/ICARD/forms/mobile/index.html?code=DIZKMCNCCACNOM&ecid=DIZKMCNCCACNOM";
        public static Logger logger = LoggerFactory.getLogger("CityBankApply");
    // 花旗银行申请提交地址
    public static final String CITYBANK_SUBMIT_URL = "https://www.citibank.com.cn/CNGCB/apfa/genfm/ProcessForm.do";
    // 合作标示code
    public static final String COOPERATION_CODE = "DIZKMCNCCACNOM";

    // 支持城市
    public static Map<String,String> CITYBANK_CITY_MAP = new HashMap<>();
//    // 职业occupation
//    public static Map<String,String> CITYBANK_OCCUPATION_MAP = new HashMap<>();
    static {
        // 城市
        CITYBANK_CITY_MAP.put("310100", "sh");
        CITYBANK_CITY_MAP.put("110100", "bj");
        CITYBANK_CITY_MAP.put("440300", "sz");
        CITYBANK_CITY_MAP.put("440100", "gz");

    }
    //  请求头
    Map<String, String> hearders = new HashMap<String, String>();
    // 请求数据
    Map<String, String> data = new HashMap<String, String>();
    //  不通过预审标记
    public static boolean eligible = true;
}
