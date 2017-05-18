package com.caiyi.financial.nirvana.core.util;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created by wsl on 2015/12/29.
 * 对fastjson简单封装
 */
public class JsonUtil {
    public static String toJSONString(Object obj){
        if(obj instanceof Page){
            //变成符合jqgrid的json
            Page page = (Page) obj;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("records",page.getTotal());
            jsonObject.put("pageNum",page.getPageNum());
            jsonObject.put("pageSize",page.getPageSize());
            jsonObject.put("totalPage",page.getPages());
            jsonObject.put("rows",page);
            return JSONObject.toJSONString(jsonObject, SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteDateUseDateFormat);
        }
        return JSONObject.toJSONString(obj, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteDateUseDateFormat);
    }
    public static Object parse(String str){
        return JSONObject.parse(str);
    }

    public static <T> List<T> parseArray(String str,Class<T> tClass){
        return JSONObject.parseArray(str, tClass);
    }
    public static <T> T parseObject(String str,Class<T> tClass){
        return JSONObject.parseObject(str, tClass);
    }

    public static JSONObject parseObject(String str){
        return JSONObject.parseObject(str);
    }

    public static <T> T parseObject(String str,String key,Class<T> tClass){
        String[] keys = key.split("\\.");
        JSONObject obj = JSONObject.parseObject(str);
        for(int i =0;i<keys.length-1;i++){
            obj = obj.getJSONObject(keys[i]);
        }
        return obj.getObject(keys[keys.length-1],tClass);
    }

}
