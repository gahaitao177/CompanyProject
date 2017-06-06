package com.youyu.demo.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by User on 2017/5/15.
 */
public class Demo {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        context.start();

        try {
            Thread.sleep(1000 * 3600 * 24 * 365);
            System.in.read(); // 按任意键退出
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
