package com.caiyi.financial.nirvana.core.spring.demo2;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomizeComponent {
    String id() default "";
    String name() default "";
}