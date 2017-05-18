package com.caiyi.financial.nirvana.discount.ccard.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.discount.ccard.core.TestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by heshaohua on 2016/5/4.
 */
public class CheapServiceTest extends TestSupport {

    @Autowired
    CheapService cheapService;

    @Test
    public void testQuery_area() throws Exception {
//        Cheap cheap = new Cheap();
//        cheap.setIpareaid(101);
//        List<AreaDto> areaMap= cheapService.query_area(cheap);
////        System.out.println("list.size:"+list.size());
////        System.out.println(JSONObject.toJSONString(list));
//
//        String str = JSONObject.toJSONString(areaMap);
//
//        System.out.println(str);
        /**list = JSONObject.parseArray(str,Cheap.class);
        for(Cheap cheap1 : list){
            System.out.println(cheap1);
        }**/


    }

    public  static  void main(String[] args){
        JSONObject result=new JSONObject();
        JSONObject one=new JSONObject();
        JSONObject two=new JSONObject();
        JSONObject three=new JSONObject();
        JSONObject five=new JSONObject();
        JSONArray array=new JSONArray();
        JSONArray array1=new JSONArray();
        one.put("one","1");
        two.put("two","2");
        three.put("three","3");
        five.put("five","5");
//        array1.add(five);
        array.add(one);
        array.add(two);
        three.put("four",array);
        three.getJSONArray("four").add(five);
        array1.add(three.getJSONArray("four"));
        result.put("666",three.getJSONArray("four"));
        System.out.println(result.toString());
    }

}
