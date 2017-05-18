package com.caiyi.financial.nirvana.ccard.investigation.util;

import cn.aofeng.common4j.lang.StringUtil;
import com.caiyi.financial.nirvana.ccard.investigation.annotations.ZxBaseItem;
import com.caiyi.financial.nirvana.ccard.investigation.bo.CreditScoreBO;
import com.caiyi.financial.nirvana.ccard.investigation.constants.YouyuCreditScoreWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 征信分数计算工具类
 * Created by chenli on 2017/3/21.
 */
public class ZxCreditScoreUtil {

    private static  final Logger LOGGER = LoggerFactory.getLogger(ZxCreditScoreUtil.class);

    /**
     *计算征信分数
     * @param name 获取那一项的分数
     * @param creditScoreBO 基本数据
     * @param calculateClazz  计算的类
     * @param weightClazz 权重的类
     * @return
     */
    public static Double getScore(String name ,CreditScoreBO creditScoreBO,Class calculateClazz,Class weightClazz) {
        //获取基本项计算类的所有方法对象数组
        Method[] methods = calculateClazz.getDeclaredMethods();
        //获取权重类的类对象
        Class fc = weightClazz;
       //基本项计算的分数map (key是基本项属于的上一级，value是基本项上一级的分数（没有除以总权重的分数在后面计算）)
        Map<String,Double> sc= new HashMap();
        //属于同一个“基本项上级”的基本项总权重
        Map<String,Double> we = new HashMap();
        Double finalScore =0.0;
        Double finalWeight = 0.0;
        //遍历方法
        for (Method m:methods){
            if(!m.isAnnotationPresent(ZxBaseItem.class)){
                continue;
            }
            ZxBaseItem zx = m.getAnnotation(ZxBaseItem.class);
            //如果有注解就计算
            String str = zx.value();
            if(StringUtil.isEmpty(str)){
                continue;
            }
            //基本项上一级的key
            String key = str.substring(0,str.lastIndexOf("_"));
            try {
                //该基本项的权重
                Double weight = (Double) fc.getField(zx.value()).get(null);
                if(null==weight){
                    continue;
                }
                //该基本项的分数
                Double score = (Integer)m.invoke(null,creditScoreBO)*weight;
                //如果要返回的是该基本项直接返回
                if(zx.value().equals(name)){
                    return (Integer)m.invoke(null,creditScoreBO)*1.0;
                }
                //获取基本项上一级的所有基本项总分数和总权重
                if(sc.containsKey(key)){
                    sc.put(key,sc.get(key)+score);
                    we.put(key,we.get(key)+weight);
                }else{
                    sc.put(key,score);
                    we.put(key,weight);
                }
            } catch (Exception e){
                LOGGER.error(zx.value()+"：方法分数计算失败",e);
                return null;
            }
        }
        if(sc.isEmpty()){
            LOGGER.info("没有基本项");
            return null;
        }
        //总和了基本项后的2个map we(某一基本项上级的所有基本项的权重和) sc(某一基本项上级的所有基本项分数的和)
        String sum  =  "sum";
        for (String key :sc.keySet()){
            //除掉基本项后层次深度
            try {
                //当前权重
                Double weight = (Double) fc.getField(key).get(null);
                //当前层分数
                Double score =sc.get(key)*weight/we.get(key);
                if(key.equals(name)){
                    LOGGER.info(key+"的分数："+sc.get(key)/we.get(key)+"##########"+"权重："+weight);
                    return sc.get(key)/we.get(key);
                }
                finalScore += score;
                finalWeight += weight;
                sum = key.substring(0,key.indexOf("_"));
            }catch (Exception e){
                LOGGER.error(key+"：方法分数计算失败",e);
                return null;
            }
        }
        if(sum.equals(name)){
            LOGGER.info(name+"的分数："+finalScore/finalWeight);
            finalScore =  finalScore/finalWeight;
        }else {
            finalScore = null;
            LOGGER.info(name+"：分数项不存在");
        }
       return finalScore;
    }

    /**
     * 获取征信总分
     * @param creditScoreBO
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws InvocationTargetException
     */
    public static Double getCreditScore(CreditScoreBO creditScoreBO){
        return getScore("CREDIT",creditScoreBO,YouyuCalcCreditScoreUtil.class,YouyuCreditScoreWeight.class);
    }

    /**
     * 获取征信总分
     * @param name
     * @param creditScoreBO
     * @return
     */
    public static Double getCreditScore(String name,CreditScoreBO creditScoreBO){
        return getScore(name,creditScoreBO,YouyuCalcCreditScoreUtil.class,YouyuCreditScoreWeight.class);
    }


}
