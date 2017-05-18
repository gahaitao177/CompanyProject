package com.caiyi.financial.nirvana.core.quartz.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wenshiliang on 2016/10/9.
 */
public class T1Job {
    public Logger logger = LoggerFactory.getLogger(getClass());
    public String run(){
        logger.info(" run ");
        return "T1Job run result";
    }

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-quartz-test1.xml");
    }
}
