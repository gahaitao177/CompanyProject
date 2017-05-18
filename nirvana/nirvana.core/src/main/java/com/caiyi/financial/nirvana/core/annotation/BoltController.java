package com.caiyi.financial.nirvana.core.annotation;

import java.lang.annotation.*;


/**
 * Created by wenshiliang on 2016/4/21.
 * bolt方法注解
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Documented
public @interface BoltController{

    String value() default "";

    /**
     * 方法说明， 当recordLog 为true时，cmenu作为说明入库
     * add by wsl in 2016年12月12日
     *  兼容贷款后台日志处理
     * @return
     */
    String cmenu() default "";

    /**
     * 该方法是否记录日志
     * add by wsl in 2016年12月12日
     *  兼容贷款后台日志处理
     * @return
     */
    boolean recordLog() default false;

    /**
     * 是否记录请求参数
     * add by wsl in 2016年12月12日
     *  兼容贷款后台日志处理
     * @return
     */
    boolean recordParam() default true;

    /**
     * 是否记录请求结果
     * add by wsl in 2016年12月12日
     *  兼容贷款后台日志处理
     * @return
     */
    boolean recordResult() default true;
}