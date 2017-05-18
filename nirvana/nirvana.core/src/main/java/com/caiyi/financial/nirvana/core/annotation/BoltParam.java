package com.caiyi.financial.nirvana.core.annotation;

import java.lang.annotation.*;

/**
 * Created by wenshiliang on 2016/4/21.
 * bolt方法参数注解
 * 根据该注解value获得data中的属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.ANNOTATION_TYPE})
@Documented
public @interface BoltParam {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     */
    String value() default "";

    String explain() default "";
}