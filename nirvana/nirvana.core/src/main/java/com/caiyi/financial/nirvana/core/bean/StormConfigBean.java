package com.caiyi.financial.nirvana.core.bean;

import java.lang.reflect.Method;

/**
 * 读取StormConfig注解生成的对象
 */
public class StormConfigBean {

    /**
     * 调用的方法
     */
    private Method initMethod;
    /**
     * 调用顺序
     */
    private int order;

    /**
     * 调用initMethod方法的对象
     */
    private Object target;


    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
