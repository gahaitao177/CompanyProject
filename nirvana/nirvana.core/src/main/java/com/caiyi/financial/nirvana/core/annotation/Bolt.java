package com.caiyi.financial.nirvana.core.annotation;

import java.lang.annotation.*;

/**
 * Created by wenshiliang on 2016/6/8.
 * 声明Bolt注解
 * 改为akka执行
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Bolt {
    String boltId();

    @Deprecated
    int parallelismHint() default 1;
    @Deprecated
    int numTasks() default 1;
    /**
     * 从DispatcherBolt分发策略
     * shuffle或者fields
     */
    @Deprecated
    String group() default  "shuffle";
    /**
     * group值为fields的Field值
     */
    @Deprecated
    String[] groupFields() default {};
    /**
     * steam流名称，默认为boltId
     */
    @Deprecated
    String streamId() default "";
}
