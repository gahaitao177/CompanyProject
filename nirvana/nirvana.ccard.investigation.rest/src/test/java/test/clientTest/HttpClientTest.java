package test.clientTest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhijie on 2016/12/19.
 */
public class HttpClientTest {

    public static void main(String[] args){
////        getGjjContends();
//        long start=System.currentTimeMillis();
////        String apiUrl = "http://gjj.9188.com/user/queryGjjSiPayRecord.go";
//        String apiUrl = "http://gjj_8095.gs.9188.com/user/queryGjjSiPayRecord.go";
//        Map mapParams = new HashMap<>();
////        mapParams.put("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsyfPugZ4V/7sNx3xHjCn+uqldLPZryIfx3nKzPeQtBEToRLMGHRsOAoABUkBusVePe3y3DEw76Hycu0oD7xkq6AxZy1/flAGmxzFj17FSWjQbqgOlBb81fMu//QiyyDhLoJ50u9uNUqw==");
////        mapParams.put("appId", "ltJX2016UKM12G2JAQ3L1TM13B9505B56");
//        mapParams.put("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvhLUS7BM6obVxt6H6RlQ4L4Oxe1iU90FOK27sVLT9pkU8AZU23p8AcH4XSREJIdwBjiACILcZCR16dmuQW6XIDt3eVR+/NLHQo86D4wtC6epu5GjUSTCJ9F6cJf0NscITNwvLT+uZRRg==");
//        mapParams.put("appId", "lcB2IL0IIQ161Z2A3SRL00H7R340E5281");
//
//        mapParams.put("businessType","1");
//        mapParams.put("source","13011");
//        mapParams.put("releaseVersion","1.9.6");
////        Map<String,String> header=new HashMap<>();
////        header.put("","");
//        String result = HttpClientUtil.callHttpPost_Map(apiUrl, mapParams);
//        System.out.println("222:"+result);
////        getSbContend();
//
//        System.out.println("耗时:"+(System.currentTimeMillis()-start));

        float x=0.3f-0.2f;
        float y=0.4f-0.3f;
        System.out.println("x:"+x);
        System.out.println("y:"+y);
        System.out.println("答案:"+(y==x));
    }
    public  static void getGjjContends(){
        //测试 请求公积金结果信息
        HttpRequester hq = new HttpRequester();
        String accessToken="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvsxW52Dgn7kMLu7APkVSXS1JXwYOoR48AbMd8IY5t8Efi0/8djBG3BgCvJbPuzdxzzhSnSLtR9qYb8LOrhQhhcozDuOnlpfGNCf6t0xJ5DKlxQQtPPGVuDjlwTwL2AGBhbI2q3CpxUyw==";
//        String url="http://gjj.9188.com/user/queryGjjSiPayRecord.go";
        String url="http://gjj_8095.gs.9188.com/user/queryGjjSiPayRecord.go";
        Map<String,String> param=new HashMap<>();
//        param.put("releaseVersion","1.9.6");
//        param.put("source","13011");
//        param.put("skin","0");
//        param.put("channel","官网");
//        param.put("tabVersion","1");
//        param.put("from","hsk");
        param.put("appId","lcN2016H1CZ2300QZ122ET2HYQWKD2638");
        param.put("accessToken",accessToken);

//        Map<String, String> propertys=new HashMap<>();
//        propertys.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        propertys.put("Accept-Language", "zh-CN,zh;q=0.8");
        HttpRespons respons=null;
        String result ="";
        try {
//             result = HttpClientUtil.callHttpPost_Map(url, param);
            respons=hq.sendPost(url,param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String contend=respons.getContent();
        System.out.println("contend:"+result);
        JSONObject contendJson=JSONObject.parseObject(contend);
        if(contendJson!=null){
            JSONObject data=contendJson.getJSONObject("results");
            if(data!=null&&data.getJSONArray("list")!=null){
                JSONObject gjjContend=data.getJSONArray("list").getJSONObject(0);
                if(gjjContend!=null){
                    String cpay=gjjContend.getString("cpay");
                    String cstate=gjjContend.getString("cstate");
                    System.out.println("月缴纳额度:"+cpay);
                    System.out.println("公积金缴纳状态:"+cstate);
                    JSONArray records=gjjContend.getJSONArray("records");
                    int month=0;
                    for (int i = 0; i <records.size() ; i++) {
                        JSONArray json0=records.getJSONObject(i).getJSONArray("record");
                        for (int j = 0; j < json0.size(); j++) {
                            JSONObject tem=json0.getJSONObject(j);
                            String item2=tem.getString("item2");
                            if(StringUtils.isNotEmpty(item2)&&item2.contains("汇缴")&&item2.contains("公积金")){
                                month++;
                            }
                        }
                    }
                    System.out.println("month 缴纳月数:"+month);
                }
            }
        }
    }
    public  static void getSbContend(){
        HttpRequester hq = new HttpRequester();
        String accessToken="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvNQ/S4bxROo6RduRv/0DDO6Zt2M7B1dCAsshBahAmcleJI/dhCOp197n0NKbMM+vkCvPXK1rKH46VupitUkkNAeagxMb1jxUm7q4oJ69SZ1OWRQVnACFtCLqNdB0du2I82DJu8U7gEaA==";
        String url="http://gjj_8095.gs.9188.com/user/queryGjjSiPayRecord.go";
        Map<String,String> param=new HashMap<>();
        param.put("releaseVersion","1.9.1");
        param.put("businessType","1");
        param.put("source","130000");
        param.put("from","hsk");
        param.put("appId","lc2YP0T170C11A0KH1J1I56YL4I67KK72");
        param.put("accessToken",accessToken);

        Map<String, String> propertys=new HashMap<>();
        propertys.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        propertys.put("Accept-Language", "zh-CN,zh;q=0.8");
        HttpRespons respons=null;
        try {
            respons=hq.sendGet(url,param,propertys);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String contend=respons.getContent();
        System.out.println("contend:"+contend);
        JSONObject contendJson=JSONObject.parseObject(contend);
        if(contend!=null){
            JSONObject data=contendJson.getJSONObject("results");
            if(data!=null&&data.getJSONArray("list")!=null){
                JSONObject gjjContend=data.getJSONArray("list").getJSONObject(0);
                if(gjjContend!=null){
                    String cpay=gjjContend.getString("cpay");
                    String cstate=gjjContend.getString("cstate");
                    System.out.println("月缴纳额度:"+cpay);
                    System.out.println("公积金缴纳状态:"+cstate);
                    JSONArray records=gjjContend.getJSONArray("records");
                    int month=0;
                    for (int i = 0; i <records.size() ; i++) {
                        JSONArray json0=records.getJSONObject(i).getJSONArray("record");
                        for (int j = 0; j < json0.size(); j++) {
                            JSONObject tem=json0.getJSONObject(j);
                            String item2=tem.getString("item2");
                            if(StringUtils.isNotEmpty(item2)&&item2.contains("汇缴")&&item2.contains("公积金")){
                                month++;
                            }
                        }
                    }
                    System.out.println("month 缴纳月数:"+month);
                }
            }
        }
    }
}
