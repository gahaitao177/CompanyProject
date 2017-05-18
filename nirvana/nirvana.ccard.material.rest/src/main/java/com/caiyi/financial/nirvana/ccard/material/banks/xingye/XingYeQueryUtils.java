package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.util.ErrorBankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.ErrorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wsl on 2016/3/4.
 */
public class XingYeQueryUtils {
    public static Logger logger = LoggerFactory.getLogger("XingYeQueryUtils");

    public static int applyBankCreditCard(MaterialBean bean){

        String idcardid = bean.getIdcardid();
        if(StringUtils.isEmpty(idcardid)){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("参数错误");
            bean.setBusiJSON("{\"resultcode\":-1,\"resultdesc\":\"\",\"resean\":\"\"}");
            return 0;
        }

        Map<String,String> map =new HashMap<>();
        map.put("certType", "2020");
        map.put("certNo", idcardid);
        map.put("method:query", "确认");

        String respStr = doPost("https://personalbank.cib.com.cn/pers/creditCard/outer/querySchedule.do",map);
        String pattern = "jalert\\(\"([\\s\\S]*)\",\"温馨提示\"\\)";



        int code = 0;
        String msg = "审核中";

        //兴业修改 by wsl 20160322

        Document doc = Jsoup.parse(respStr, "utf-8");

        try {
            Element element =  doc.getElementById("form_label_scheduling").nextElementSibling();
            String resp = element.text();
            if(resp.contains("审核中")){

            }else if(resp.contains("未予核准")){
                //兴业目前未通过： 综合评分不足，未予核准，感谢您的申请
                code = 2;
                msg = "未通过";
            }else{
                throw new RuntimeException("其他状态都没有查到，先抛出异常，存入文件");
            }

        }catch (Exception e){
            e.printStackTrace();
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(respStr);
            if(m.find()){
                respStr = m.group(0);
                System.out.println(respStr);

                respStr = respStr.substring(8,respStr.indexOf("\",\""));

                if(respStr.contains("\\u672A\\u7533\\u8BF7\\u6216\\u8D44\\u6599\\u6682\\u672A\\u6536\\u5230")){
                    //未申请或资料暂未收到xingf
                    code = 0;
                    msg = "审核中";
                }else{
                    ErrorUtils.saveQueryFile(ErrorBankEnum.xingye, idcardid, respStr);
                    logger.info("兴业申请结果:"+respStr);
                }

            }else{
                code = 0;
                msg = "审核中";
                ErrorUtils.saveQueryFile(ErrorBankEnum.xingye, idcardid, respStr);
            }
        }





        bean.setBusiErrCode(1);
        bean.setBusiErrDesc(msg);
        bean.setCstatus(code + "");
        bean.setBusiJSON("{\"resultcode\": " + code + ",\"resultdesc\":\"" + msg + "\",\"resean\":\"\"}");
        return 1;




    }

    private static String doPost(String url, Map<String, String> map) {
        HttpPost post = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();

        post.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));

        post.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-CN; HUAWEI Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.8.5.689 U3/0.8.0 Mobile Safari/534.30");

        if (map != null && map.size() > 0) {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            post.setEntity(new StringEntity(params.substring(0, params.length() - 1), "utf-8"));
        }
        CloseableHttpResponse response = null;
        String resEntityStr = null;
        try {
            response = client.execute(post);

            HttpEntity entity = response.getEntity();
            resEntityStr = EntityUtils.toString(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(client!=null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return resEntityStr;
        }
    }
}
