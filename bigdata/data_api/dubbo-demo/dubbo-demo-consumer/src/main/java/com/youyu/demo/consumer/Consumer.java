package com.youyu.demo.consumer;

import com.youyu.demo.api.Provider;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by User on 2017/5/15.
 */
public class Consumer {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
            context.start();
            Provider demoService = (Provider) context.getBean("demoService"); // 获取bean
            try {
                System.out.println(demoService.build("测试"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
