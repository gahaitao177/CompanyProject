package com.caiyi.financial.nirvana.annotation;

import org.springframework.stereotype.Component;

/**
 * Created by wenshiliang on 2016/6/24.
 * 声明只被springmvc扫描的Component注解
 */
@Component
public @interface MVCComponent {
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     */
    String value() default "";
}
