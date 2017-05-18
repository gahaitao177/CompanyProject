package com.caiyi.financial.nirvana.discount.intercept;

import java.lang.annotation.*;

/**
 * Created by hsh on 2016/4/21.
 * bolt方法参数注解
 * 根据该注解value获得data中的属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SetUserDataRequired {

    /**landline
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     */
    String value() default "";

    //String explain();
}