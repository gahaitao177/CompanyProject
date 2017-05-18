package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lizhijie on 2016/10/25.
 */
@RestController
public class RestNewContanctController {
    private static Logger logger = LoggerFactory.getLogger(RestNewContanctController.class);
    @Resource(name = Constant.HSK_CCARD)
    IDrpcClient client;

    @RequestMapping("/notcontrol/qThemesAndContancts.go")
    public  String queryThemesAndContancts(HttpServletRequest request, HttpServletResponse response){
        String boltResult=client.execute(new DrpcRequest("newContanct","queryThemesAndConstancts"));
        try {
            JSONObject jsonObject= JSON.parseObject(boltResult);
            if (jsonObject!=null){
                if(jsonObject.get("data")!=null){
                    JSONObject data=jsonObject.getJSONObject("data");
                    JSONArray resultData=new JSONArray();
                    if(data!=null&&data.get("themes")!=null){
                       String themes= data.getString("themes");
                        JSONObject tmp=new JSONObject();
                        tmp.put("contents", JSON.parseArray(themes));
                        tmp.put("type", 1);
                        resultData.add(tmp);
                    }
                    if(data!=null&&data.get("contancts")!=null){
                        String contancts= data.getString("contancts");
                        JSONObject tmp2=new JSONObject();
                        JSONArray contanctArr=JSON.parseArray(contancts);
                        JSONArray list1=new JSONArray();
                        for (int i=0;i<contanctArr.size();i++){
                            JSONObject object=new JSONObject();
                            JSONObject json=contanctArr.getJSONObject(i);
                            if(json!=null){
                                object.put("accessurl",json.getString("caccessulr"));
                                object.put("contactid",json.getString("icontactid"));
                                object.put("content",json.getString("csummary"));
                                object.put("picurl",json.getString("cpicurl"));
                                object.put("title",json.getString("ctitle"));
                                object.put("rec",1);
                                list1.add(object);
                            }
                        }
                        tmp2.put("contents",list1);
                        tmp2.put("type",2);
                        resultData.add(tmp2);
                    }
                    jsonObject.put("data",resultData);
                    logger.info("queryThemesAndConstancts返回结果:{}",jsonObject);
                    return  jsonObject.toString();
                }else {
                    logger.info("queryThemesAndConstancts返回结果:{}",boltResult);
                    return boltResult;
                }
            }else {
                jsonObject.put("code",-1);
                jsonObject.put("desc","程序异常");
            }
            logger.info("queryThemesAndConstancts返回结果:{}",jsonObject.toString());
            return jsonObject.toString();
        }catch (Exception e){
            JSONObject errorJson=new JSONObject();
            errorJson.put("code",-1);
            errorJson.put("desc","程序异常");
            logger.info("queryThemesAndConstancts 异常{}",e);
            return errorJson.toString();
        }
    }
}
