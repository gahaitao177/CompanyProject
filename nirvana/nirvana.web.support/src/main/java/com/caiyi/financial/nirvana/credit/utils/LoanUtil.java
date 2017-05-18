package com.caiyi.financial.nirvana.credit.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lichuanshun on 16/12/9.
 * 通过http请求获取 配置的 贷款参数
 */
public class LoanUtil {
    private static Logger logger = LoggerFactory.getLogger(LoanUtil.class);
    private static String LOAN_BASE_URL = "http://www.huishuaka.com";
    private static String LOAN_API = "/notcontrol/loan/channelList2.go";
    private static String ZHENGXIN_URL = "?orderBy=ihotorder&icitycode=%s&iorgid=zhengxin&cclient=%s&cposition=remen";
    private static String HSK_URL = "?orderBy=ihotorder&icitycode=%s&iorgid=huishuaka&cclient=%s&cposition=huishuaka";
    private static String YOUYU_URL = "?orderBy=ihotorder&icitycode=%s&iorgid=youyujinrong&cclient=%s&cposition=youyujinrong";
    private static String DEFULT_ADCODE = "310100";
    private static String DEFULT_PACKAGENAME = "com.caiyi.hsk";
    // 有鱼贷款比较特殊 在贷款链接地址 附加sid
    private static String SPECIAL_LOAN= "有鱼贷款";
    /**
     * 惠刷卡获取热门贷款
     * @param adcode
     * @param client
     * @param packageName
     * @param source
     * @return
     */
    public static JSONObject getHskHotLoan(String adcode,int client,String packageName,int source){
        return getHotLoan(adcode, client,HSK_URL,packageName,source);
    }

    /**
     * 有鱼金融获取热门贷款
     * @param adcode
     * @param client
     * @param packageName
     * @param source
     * @return
     */
    public static JSONObject getYouyuHotLoan(String adcode,int client,String packageName,int source){
        return getHotLoan(adcode, client,YOUYU_URL,packageName,source);
    }
    /**
     * 征信获取热门贷款
     * @param adcode
     * @param client
     * @param packageName
     * @param source
     * @return
     */
    public static JSONObject getZhengxinHotLoan(String adcode,int client,String packageName,int source){
        return getHotLoan(adcode, client,ZHENGXIN_URL,packageName,source);
    }
    /**
     * 获取热门贷款
     * @param adCode 六位城市adcode
     * @param client 客户端类型
     * @return
     */
    private static JSONObject getHotLoan(String adCode, int client,String params,String packageName,int source){
        JSONObject hotLoan = new JSONObject();
        hotLoan.put("code","1");
        hotLoan.put("desc","获取成功");
        // 设置默认值
        if (CheckUtil.isNullString(adCode)){
            adCode = DEFULT_ADCODE;
        }
        if (CheckUtil.isNullString(packageName)){
            packageName = DEFULT_PACKAGENAME;
        }

        try {
            String clientType = "az";
            if ( 1 == client){
                clientType = "ios";
            }
            String apiUrl = LOAN_BASE_URL + LOAN_API + String.format(params,adCode,clientType);
            String result = HttpClientUtil.callHttpPost_Map(apiUrl, null);
            logger.info("apiUrl:" + apiUrl);
            logger.info("result:" + result);
            JSONObject tempLoansJson =JSONObject.parseObject(result);
            if (!CheckUtil.isNullString(result) && "1".equals(tempLoansJson.getString("code"))){
                JSONArray tempArr = tempLoansJson.getJSONArray("data");
                JSONArray data = new JSONArray();
                String sid = packageName + source;
                for (Object loanObj : tempArr) {
                    if (data.size() == 2) {
                        break;
                    }
                    JSONObject oldLoan = (JSONObject) loanObj;
                    String preUrl = oldLoan.getString("cproductpageurl");
                    if (SPECIAL_LOAN.equals(oldLoan.getString("cproductname"))) {
                        oldLoan.put("curl", oldLoan.getString("curl") + "&sid=" + sid);

                    }
                    if (!CheckUtil.isNullString(preUrl)){
                        preUrl = preUrl + "&sid=" + sid + "&iproductid=" + oldLoan.getString("iproductid");
                    } else {
                        preUrl = oldLoan.getString("curl");
                    }
                    oldLoan.put("monthlyFee", getLoanRate(oldLoan.getString("cmonthlyrate")));
                    JSONObject tempLoan = new JSONObject();
                    tempLoan.put("loanName", oldLoan.getString("cproductname"));
                    tempLoan.put("loanDesc", oldLoan.getString("cprodesc"));
                    tempLoan.put("picUrl", oldLoan.getString("cprologo"));
                    tempLoan.put("applyNum", oldLoan.getString("capplynum"));
                    tempLoan.put("monthlyFee", oldLoan.getString("monthlyFee"));
                    tempLoan.put("loanUrl",preUrl);
                    data.add(tempLoan);
                }
                hotLoan.put("data",data);
            }

        } catch (Exception e) {
            hotLoan.put("code","0");
            hotLoan.put("desc","获取失败");
            logger.error("getHotLoan", e);
        }
        return hotLoan;
    }

    /**
     *
     * @param cmonthlyrate "0.3-0.8%|天"
     * @return
     */
    private static String getLoanRate(String cmonthlyrate){
        String loanRate = cmonthlyrate;
        try {
            if (!CheckUtil.isNullString(loanRate)){
                loanRate = "最低" + ("天".equals(cmonthlyrate.split("\\|")[1])?"日":"月") +"费" + cmonthlyrate.split("-")[0] + "%";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loanRate;
    }
    public static void main(String[] args) {
        System.out.println("111" +getHskHotLoan("110110",1,"com.caiyi.huishuaka",6000));
//        System.out.println(getLoanRate("0.3-0.8%|天"));


    }
}
