package com.caiyi.financial.nirvana.core.annotation;

import java.lang.annotation.*;

/**
 * Created by wenshiliang on 2017/3/6.
 *  扫描 application.conf 的 annotation_scan路径，根据该注解生成storm的配置
 *  com.caiyi.financial.nirvana.core.util.DrpcConfig初始化后调用initMethod方法，参数为 DrpcConfig对象。
 *
 *
 *

 public class UserStormConfig {
    public void init(StormUtil.DrpcConfig config){
        System.out.println("userStorm config初始化");
    }
 }

 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface StormConfig {

    /**
     * 注解声明的类需要存在initMethod声明的方法，该方法的参数为 com.caiyi.financial.nirvana.core.util.DrpcConfig
     *
     * @return
     */
    String initMethod() default "init";

    /**
     * 多个StormConfig配置，优先order小的
     * order越大，越晚生效
     * @return
     */
    int order() default 0;
}
