package com.caiyi.financial.nirvana.core.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.exception.BaseException;
import com.caiyi.financial.nirvana.core.exception.BoltException;
import com.caiyi.financial.nirvana.core.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/4/21.
 */
public abstract class BaseBolt implements ApplicationContextAware {
    public Logger logger = null;
    private Map<String,Method> methodMap = new HashMap<>();
    private ApplicationContext springContext;

    public Map<String, Method> getMethodMap() {
        return methodMap;
    }

    public BaseBolt(){
        logger = LoggerFactory.getLogger(getClass());
    }

    @PostConstruct
    public void init(){
        initMethod();
    }


    public void prepare(Map stormConf, TopologyContext context){
//        initField(this.getClass());
        _prepare(stormConf, context);
    }
    public void setApplicationContext(ApplicationContext springContext){
        this.springContext = springContext;
    }

    protected  void _prepare(Map stormConf, TopologyContext context){

    }


    public void initField(Class c){
        if(Object.class.equals(c)){
            return;
        }
        Field[] fields = c.getDeclaredFields();
        for (Field field: fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if(autowired!=null){
                try {
                    field.setAccessible(true);
                    logger.debug("注入{}",field.getType());
                    field.set(this,getBean(field.getType()));
                } catch (IllegalAccessException e) {
                    throw new BoltException("初始化service失败",e);
                }
            }
        }
        initField(c.getSuperclass());
    }

    public <T> T getBean(Class<T> tClass){
        return springContext.getBean(tClass);
    }

    private void initMethod(){
        logger.info("-----------初始化method");
        Method[] methods = getClass().getDeclaredMethods();
        /**
         初始化方法
         加载被BoltController注解的public 方法
         */
        for(Method m : methods){
            Annotation[] methodAnnotations = m.getDeclaredAnnotations();
            for(Annotation n : methodAnnotations){
                if(n instanceof BoltController){
                    String key = ((BoltController) n).value();
                    if(StringUtils.isEmpty(key)){
                        key = m.getName();
                    }
                    if(methodMap.containsKey(key)){
                        throw new BoltException("已经存在该方法,bolt无法初始化！");
                    }
                    methodMap.put(key,m);
                    logger.info("key : [{}] , method : [{}]",key,m.getName());
                }
            }
        }
        logger.info("-----------初始化method完成");
    }

    public Object execute(Method method,Object... params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(this,params);
    }


    public String execute(String methodStr,String dataStr,Tuple input){
        long start = System.currentTimeMillis();
        String resultStr = null;
        Object result;
        Method method = methodMap.get(methodStr);
        try{
            if(method==null){
                throw new BoltException("该方法不存在:"+methodStr);
            }
            Object[] params = parseParams(dataStr, input, method);
            result = execute(method,params);
        }catch(BaseException e){
            logger.error("出错",e);
            result = new BoltResult(BoltResult.ERROR,e.toString());
        } catch (InvocationTargetException e){
            logger.error("bolt execute出错",e);
            Throwable target = e.getTargetException();
            if(target instanceof BaseException){
                BaseException le = (BaseException) target;
                result = new BoltResult(le.getCode(),le.getMessage());
            }else{
                result = new BoltResult("500","method execute error");
            }
        }  catch (Exception e){
            logger.error("出错",e);
            result = new BoltResult(BoltResult.ERROR,"method execute error!");
        }
        long end = System.currentTimeMillis();
        long time = end-start;
        resultStr = JsonUtil.toJSONString(result);
        if(time>3000){
            logger.error("\n方法：{}\n参数：{}\n结果：{}\n执行时间超长：{}",method,dataStr,resultStr,time);
        }else if(time>1000){
            logger.warn("\n方法：{}\n参数：{}\n结果：{}\n执行时间较长:{}", method, dataStr, resultStr, time);
        } else if (logger.isDebugEnabled()){
            logger.debug("\n方法：{}\n参数：{}\n结果：{}\n执行时间:{}", method, dataStr, resultStr, time);
        }
        return resultStr;
    }

    private Object[] parseParams(String dataStr, Tuple input, Method method) {
        /**
         * 自动填充参数，解析com.caiyi.financial.nirvana.core.bean.DrpcRequest 中携带的data参数
         * BoltParam注解的参数 ，根据以注解value为key，从data的json对象中取值
         * Tuple类型的参数直接注入Tuple
         * String类型的参数注入data的jsonstring
         * 其他类型：交由fastjson解析data为对象传入
         */
        Class[] paramTypes = method.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        Annotation[][] paramsAnnotations = method.getParameterAnnotations();
        for(int i = 0;i<paramTypes.length;i++){
            Class paramType = paramTypes[i];
            Annotation[] annotations = paramsAnnotations[i];
            boolean flag = true;
            for(Annotation annotation : annotations){
                if(annotation instanceof BoltParam){
                    String key = ((BoltParam)annotation).value();
                    params[i] = JsonUtil.parseObject(dataStr,key,paramType);
                    flag = false;
                    break;
                }
            }
            if(flag){
                if(paramType.equals(String.class)){
                    params[i] = dataStr;
                }else if(paramType.equals(Tuple.class)){
                    params[i] = input;
                }else {
                    params[i] = JSONObject.parseObject(dataStr, paramType);
                }
            }

        }

        logger.debug("--------------------请求参数打印--------------------");
        if(params != null){
            for(Object param : params){
                    logger.debug(param!=null ? param.toString() : null);
            }
        }
        logger.debug("--------------------请求参数打印结束--------------------");
        return params;
    }


}
