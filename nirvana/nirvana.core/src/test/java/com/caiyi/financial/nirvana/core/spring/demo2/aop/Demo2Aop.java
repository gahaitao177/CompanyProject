package com.caiyi.financial.nirvana.core.spring.demo2.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by wenshiliang on 2016/12/9.
 */

@Aspect
@Component
//@Configuration
public class Demo2Aop {

    public Demo2Aop() {
        System.out.println("初始化Aspect拦截器");
    }

    @Pointcut("execution(* com.caiyi.financial.nirvana.core.spring.demo2.CustomizeScanTest.*.*(..))")
    public  void pointcut() {
    }


    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint){
        System.out.println("前置拦截");
    }
    @After("pointcut()")
    public void doAfter(JoinPoint joinPoint){
        System.out.println("后置拦截");
    }
    @Around("pointcut()")
    public void doAround(ProceedingJoinPoint joinPoint){
        System.out.println("环绕..");
        try {
            joinPoint.proceed();
            System.out.println("环绕...");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
