package com.caiyi.financial.nirvana.discount.tools.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.tools.bean.FinancialProductBean;
import com.caiyi.financial.nirvana.discount.tools.dto.CalculateParamDto;
import com.caiyi.financial.nirvana.discount.tools.mapper.FinancialMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by lizhijie on 2016/8/10.
 */
@Service
public class FinancialService extends AbstractService {
    @Autowired
    FinancialMapper financialMapper;
   public Map<String,String> queryFinProducts(){
//       JSONObject result=new JSONObject();
       Map<String,String> result=new HashMap<>();
       List<Map<String,Object>> financailList= financialMapper.queryFinancailProducts();
       List<FinancialProductBean> listFin=new ArrayList<FinancialProductBean>();
       if(financailList!=null&&financailList.size()>0){
            for(Map<String,Object> map:financailList){
                logger.info("读取理财产品"+map.get("cname"));
                FinancialProductBean fp=new FinancialProductBean();
                fp.setPid(map.get("IP_ID")==null?null:map.get("IP_ID").toString());
                fp.setpCode(map.get("CP_CODE")==null?null:map.get("CP_CODE").toString());
                fp.setpName(map.get("CNAME")==null?null:map.get("CNAME").toString());
                fp.setProfitNum(Integer.parseInt(map.get("IPROFIT")==null?"0":map.get("IPROFIT").toString()));
                fp.setConvenienceNum(Integer.parseInt(map.get("ICONVIENCE")==null?"0":map.get("ICONVIENCE").toString()));
                fp.setInTimeNum(Integer.parseInt(map.get("IINTIME")==null?"0":map.get("IINTIME").toString()));
                fp.setSafeNum(Integer.parseInt(map.get("ISAFE")==null?"0":map.get("ISAFE").toString()));

                fp.setpMinValue(map.get("CMINVALUE")==null?null:map.get("CMINVALUE").toString());
                fp.setpCachNum(map.get("ICACH")==null?null:map.get("ICACH").toString());
                fp.setpCachTime(map.get("CPAYMENT_TIME")==null?null:map.get("CPAYMENT_TIME").toString());
                fp.setpBindFund(map.get("CBIND_FUND")==null?null:map.get("CBIND_FUND").toString());
                fp.setpAnalysis(map.get("CANALYSIS")==null?null:map.get("CANALYSIS").toString());
                fp.setpUpdateTime(map.get("CTIME")==null?null:map.get("CTIME").toString());
                fp.setpBackground(map.get("CBACKGROUD")==null?null:map.get("CBACKGROUD").toString());
                fp.setStartPerson(map.get("CSTARTPERSON")==null?null:map.get("CSTARTPERSON").toString());
                fp.setpLogo(map.get("CLOGO")==null?null:map.get("CLOGO").toString());

                fp.setpPriority(Integer.parseInt(map.get("IPRIORITY")==null?"0":map.get("IPRIORITY").toString()));
//					fp.setYearRate(rs.get("i7days_profit"));//七日年化收益率    i7day_profit
//					fp.setDayProfit(rs.get("imillion_profit")); //日万份收益  imillin_profit

                fp.setYearRate(map.get("I7DAY_PROFIT")==null?null:map.get("I7DAY_PROFIT").toString());//七日年化收益率    i7day_profit
                fp.setDayProfit(map.get("IMILLIN_PROFIT")==null?null:map.get("IMILLIN_PROFIT").toString()); //日万份收益  imillin_profit

                String history_profit=map.get("CHISTORY_PROFIT")==null?"":map.get("CHISTORY_PROFIT").toString();
                String chistory_date=map.get("CHISTORY_DATE")==null?"":map.get("CHISTORY_DATE").toString();

                if(!StringUtils.isEmpty(history_profit)&&!StringUtils.isEmpty(chistory_date)){
                    String[] profitList=history_profit.split(",");
                    String[] dateList=chistory_date.split(",");
                    if(profitList.length==dateList.length){
                        TreeMap<String, String> tmp=new TreeMap<String, String>();
                        TreeMap<String, String>  resultData=new TreeMap<String, String>();
                        for(int j=0;j<profitList.length&&dateList[j].length()>9;j++){
                            tmp.put(dateList[j], profitList[j]);
                        }
                        for(String key:tmp.keySet()){
                            resultData.put(key.substring(5,10).replace("-", "/"), tmp.get(key));
                        }
                        StringBuffer profitStr=new StringBuffer();
                        StringBuffer dateStr=new StringBuffer();
                        for(String date:resultData.keySet()){
                            if(date.length()==5){
                                dateStr.append(date+",");
                                profitStr.append(resultData.get(date)+",");
                            }
                        }
                        fp.setHistoryDate(dateStr.substring(0, dateStr.length()-1));
                        fp.setHistoryProfit(profitStr.substring(0,profitStr.length()-1));
                    }
                }
                listFin.add(fp);
            }
       }
       if(listFin.size()>0){
           result.put("code","1");
           result.put("desc","获得产品列表成功");
           result.put("data", JSONArray.toJSONString(listFin));
       }else{
           result.put("code","0");
           result.put("desc","获得产品列表失败");
           logger.info("获得产品列表失败");
       }
       return result;
   }
    public JSONObject updateProduct(Map<String,String> map){
        JSONObject json=new JSONObject();
        if(StringUtils.isEmpty(map.get("ip_id"))){
            json.put("code","-1");
            json.put("desc","没有获得有效参数");
            logger.info("code:-1 ," + "desc:没有获得有效参数");
            return json;
        }
        if("-1".equals(map.get("profitNum"))&&"-1".equals(map.get("convenienceNum"))&&
                "-1".equals(map.get("inTimeNum"))&& "-1".equals(map.get("safeNum"))){
            json.put("code","-1");
            json.put("desc","没有获得投票的参数");
            logger.info("code:-1 ，desc:,没有获取投票的理财产品指标");
            return json;
        }
        Integer count=financialMapper.updateProduct(map);
        if(count>0){
            json.put("code","1");
            json.put("desc","投票成功");
            logger.info("code:1 ," + "desc:投票成功,keys="+map.keySet()+",values="+map.values());
        }else {
            json.put("code","0");
            json.put("desc","投票失败");
            logger.info("code:-1 ," + "desc:投票失败,keys="+map.keySet()+",values="+map.values());
        }
        return json;
    }
    public JSONObject calculate(String func){
        JSONObject jsonObject=new JSONObject();
        CalculateParamDto calculateParam=new CalculateParamDto();
        calculateParam.setMediatype("json");
        if(StringUtils.isEmpty(func)){
            jsonObject.put("code",0);
            jsonObject.put("desc","参数错误");
            return jsonObject;
        }
        if(!StringUtils.isEmpty(func)){
            if("wxyj".equals(func)){
                jsonObject= wxyj(calculateParam);
            }else if("fangdai".equals(func)){
                jsonObject=fangdai(calculateParam);
            }else if("getversion".equals(func)){
                jsonObject= getVersion(calculateParam);
            }else {
                jsonObject.put("code",0);
                jsonObject.put("desc","没有找到方法");
            }
        }else {
            jsonObject.put("code",0);
            jsonObject.put("desc","参数错误");
        }
        return jsonObject;
    }
    private JSONObject wxyj(CalculateParamDto bean){
        JSONObject jsonObject=new JSONObject();
//        JdbcRecordSet jrs = jcn.executeQuery(calsql, new Integer[] { 1 });
       List<CalculateParamDto> list =financialMapper.queryRateByType(1);
        JSONArray all = new JSONArray();
        JSONObject tmp = new JSONObject();
        for(CalculateParamDto calculate:list) {
            String itypeid = calculate.getItypeid();
            String cityname = calculate.getCcityname();
            String crcate = calculate.getCrate();
            String value = calculate.getCvalue();
            JSONObject t1 = tmp.getJSONObject(itypeid);
            if (t1 == null) {
                t1 = new JSONObject();
                t1.put("cityName", cityname);
                t1.put("id", itypeid);
            }
            JSONObject t1rate = t1.getJSONObject("rate");
            if (t1rate == null) {
                t1rate = new JSONObject();
            }
            t1rate.put(crcate, value);
            t1.put("rate", t1rate);
            tmp.put(itypeid, t1);
        }
        Set<Integer> keys = new TreeSet<Integer>();
        for(String key:tmp.keySet()){
            keys.add(Integer.parseInt(key));
        }
        for(int ki:keys){
            all.add(tmp.get(ki+""));
        }
        if(all.size()>0){
            jsonObject.put("code",1);
            jsonObject.put("desc","查询成功");
            jsonObject.put("data",all);
        }else {
            jsonObject.put("code",0);
            jsonObject.put("desc","查询失败");
        }
        return jsonObject;
    }
    private JSONObject fangdai(CalculateParamDto bean) {
        JSONObject buzz = fangdai_common(2);
        JSONObject coun = fangdai_common(3);
        JSONObject data = new JSONObject();
        JSONObject result = new JSONObject();
        data.put("BuzzRate", buzz);
        data.put("CounRate", coun);
        if(data.size()>0){
            result.put("code", 1);
            result.put("desc", "查询成功");
            result.put("data", data);
        }else {
            result.put("code", 0);
            result.put("desc", "查询失败");
        }
        return result;
    }

    private JSONObject fangdai_common(int itype) {
        List<CalculateParamDto> list =financialMapper.queryRateByType(itype);
        JSONObject buz = new JSONObject();
        JSONObject tmp = null;
        for (CalculateParamDto calculate:list){
            String itypeid = calculate.getItypeid();
            String crate = calculate.getCrate();
            String val = calculate.getCvalue();
            tmp = new JSONObject();
            tmp.put(crate, val);
            buz.put(itypeid, tmp);
        }
        return buz;
    }
    private JSONObject getVersion(CalculateParamDto bean) {
        List<Map<String,Object>> list=financialMapper.queryCalversion();
        int wxyj = 0;
        int fangdai = 0;
        for(Map<String,Object> map:list){
            int itype = map.get("itype")==null?0:Integer.parseInt(map.get("itype").toString());
            int cversion = map.get("cversion")==null?0:Integer.parseInt(map.get("cversion").toString());
            if (3 == itype) {
                wxyj = cversion;
            }
            if (4 == itype) {
                fangdai = cversion;
            }
        }
        JSONObject data = new JSONObject();
        JSONObject result = new JSONObject();
        data.put("wxyj", wxyj);
        data.put("fangdai", fangdai);
        if(data.size()>0){
            result.put("code", 1);
            result.put("desc", "查询成功");
            result.put("data", data);
        }else {
            result.put("code", 0);
            result.put("desc", "查询失败");
        }
       return result;
    }
}
