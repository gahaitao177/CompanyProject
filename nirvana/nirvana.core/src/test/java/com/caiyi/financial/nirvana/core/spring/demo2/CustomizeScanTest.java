package com.caiyi.financial.nirvana.core.spring.demo2;

import com.caiyi.financial.nirvana.core.spring.demo2.aop.Demo2Aop;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Configuration
//@ComponentScan(basePackages = "com.caiyi.financial.nirvana.core.spring.demo2")
public class CustomizeScanTest {
 public static void main(String[] args) {
     AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
     annotationConfigApplicationContext.register(CustomizeScanTest.class, Demo2Aop.class);
     annotationConfigApplicationContext.refresh();


     annotationConfigApplicationContext.getBean(T1.class).print();
     ScanClass1 injectClass = annotationConfigApplicationContext.getBean(ScanClass1.class);
     injectClass.print();
     injectClass = (ScanClass1) annotationConfigApplicationContext.getBean("scanName");
     injectClass.print();
 }
    @CustomizeComponent(name = "scanName")
//    @Component
    public static class ScanClass1 {

        public void print() {
            System.out.println("scanClass1");
        }
    }

    @Component("tt")
    public static class T1{
        static {
            System.out.println("static t1");
        }
        public T1(){
            System.out.println("t1");
        }
        public void print() {
            System.out.println("t1 print");
        }
    }
}