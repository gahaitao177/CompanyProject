package com.caiyi.financial.nirvana.quartz;

import com.caiyi.financial.nirvana.annotation.MVCComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by heshaohua on 2016/8/5.
 */
@MVCComponent
public class QuartzTask {


    public void run() {
        System.out.println("......run");
    }

    public static void main(String[] args) {
        System.out.println("Test start.");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-quartz.xml");


        QuartzTask obj = context.getBean(QuartzTask.class);
        obj.run();
        System.out.print("Test end..");
    }
}
