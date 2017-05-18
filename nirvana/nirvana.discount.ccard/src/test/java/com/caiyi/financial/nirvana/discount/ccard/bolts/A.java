package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.discount.ccard.bean.Demo;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.tuple.Tuple;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/4/22.
 */
public class A {
    public static void main(String[] args) {
        Tbolt tbolt = new TestBolt();
        Class c = tbolt.getClass();
        String func = "del";
        String data = "{\"id\":\"5\",\"dbwritetime\":\"t1\",\"clientwritetime\":\"t2\"}";
        Method[] methods = c.getDeclaredMethods();
        System.out.println(methods.length);

        Map<String,Method> methodMap = new HashMap<>();

        for(Method m : methods){
            Annotation[] methodAnnotations = m.getDeclaredAnnotations();
            for(Annotation n : methodAnnotations){
                if(n instanceof BoltController){
                    String key = ((BoltController) n).value();
                    if(StringUtils.isEmpty(key)){
                        key = m.getName();
                    }
                    if(methodMap.containsKey(key)){
                        throw new RuntimeException("已经存在该方法,bolt无法初始化！");
                    }
                    methodMap.put(key,m);
                }
            }
        }

        Method method = methodMap.get(func);

        if(method==null){
            throw new RuntimeException("该方法不存在！");
        }

        Class[] paramTypes = method.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        JSONObject jsonObject = JSON.parseObject(data);

        Annotation[][] paramsAnnotations = method.getParameterAnnotations();

        for(int i = 0;i<paramTypes.length;i++){
            Class paramType = paramTypes[i];
//            Annotation annotation =  paramType.getAnnotation(BoltParam.class);
            Annotation[] annotations = paramsAnnotations[i];
            boolean flag = true;
            for(Annotation annotation : annotations){
                if(annotation instanceof BoltParam){
                    String key = ((BoltParam)annotation).value();
                    String[] keys = key.split("\\.");
                    JSONObject obj = jsonObject;
                    for(int j =0;j<keys.length-1;j++){
                        obj = obj.getJSONObject(keys[j]);
                    }
                    params[i] = obj.getObject(keys[keys.length-1],paramType);
                    flag = false;
                }
            }
            if(flag){
                if(paramType.equals(String.class)){
                    params[i] = data;
                }else if(paramType.equals(Tuple.class)){
                    params[i] = null;
                }else {
                    params[i] = JSONObject.parseObject(data,paramType);
                }
            }

        }


        System.out.println("-----------请求参数--");
        for(Object param : params){
            System.out.println(param);
        }
        System.out.println("-----------请求参数--");



        try {
            Object result = null;
            switch (params.length){
                case 0:
                    result =  method.invoke(tbolt);
                    break;
                case 1:
                    result =  method.invoke(tbolt,params[0]);
                    break;
                case 2:
                    result =  method.invoke(tbolt,params[0],params[1]);
                    break;
                case 3:
                    result =  method.invoke(tbolt,params[0],params[1],params[2]);
                    break;
                default:
                    throw new RuntimeException("暂时不支持这么长的参数");
            }
            String resultStr = JSONObject.toJSONString(result);
            System.out.println(resultStr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

//
//        for(Method m : methods){
//            System.out.println("------------------");
//            Annotation[] methodAnnotations = m.getDeclaredAnnotations();
//            for(Annotation n : methodAnnotations){
//                System.out.println(n);
//                if(n instanceof BoltController){
//                    System.out.println(((BoltController) n).value());
//                }
//            }
//            System.out.println(m.getReturnType());
//            Class[] paramTypes = m.getParameterTypes();
//            for(Class paramType : paramTypes){
//                System.out.println(paramType);
//            }
//            System.out.println(m.getName());
//            System.out.println("------------------");
//        }
    }
}
class Tbolt{
    public void execute(){

    }
}
class TestBolt extends Tbolt{

    @BoltController("del")
    public int del(@BoltParam("id")String id,Demo aaa){
        System.out.println("id:"+id);
        System.out.println("aaa:"+aaa);
        return 1;
    }
    public int add(Demo demo){
        return 1;
    }
    public String select(Demo demo){
        return "test";
    }
    public List<Map<String,Object>> select(String id){
        return new ArrayList<>();
    }

}



