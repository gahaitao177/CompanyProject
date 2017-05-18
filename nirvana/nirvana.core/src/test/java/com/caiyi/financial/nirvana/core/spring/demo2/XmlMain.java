package com.caiyi.financial.nirvana.core.spring.demo2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wenshiliang on 2016/12/9.
 */
public class XmlMain {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/com/caiyi/financial/nirvana/core/spring/demo2/spring-context.xml");
        context.getBean(CustomizeScanTest.T1.class).print();
        CustomizeScanTest.ScanClass1 injectClass = context.getBean(CustomizeScanTest.ScanClass1.class);
        injectClass.print();
        injectClass = (CustomizeScanTest.ScanClass1) context.getBean("scanName");
        injectClass.print();
    }
}
