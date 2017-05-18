package com.caiyi.financial.nirvana.core.quartz.test1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/10/9.
 * 思路
 *   SchedulerFactoryBean.startScheduler
 *
 *   多节点quartz注册zk，只有一个节点注册成功，其他开启zk listener监听注册；
 *   当zk节点remoter后，其他节点再次进行注册，注册成功开启任务。
 *   奔溃节点重启后加入listener监听注册
 *   http://www.cnblogs.com/nfsnyy/p/5741593.html
 *   http://blog.csdn.net/pengpegv5yaya/article/details/37595889
 *   http://blog.csdn.net/liaomengge/article/details/51340908
 *   http://blog.csdn.net/sqh201030412/article/details/51456143
 *   http://blog.csdn.net/sqh201030412/article/details/51446434
 */
public class T2 {
    public static void main(String[] args) {
        ApplicationContext context =  new ClassPathXmlApplicationContext("spring-quartz-test2.xml");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SchedulerFactoryBean bean = context.getBean(org.springframework.scheduling.quartz.SchedulerFactoryBean.class);
        System.out.println(bean.isAutoStartup()+"--"+bean.isRunning()+"---"+bean.isSingleton());
        System.out.println("休眠完成,启用调度");
        bean.start();
        System.out.println(bean.isAutoStartup()+"--"+bean.isRunning()+"---"+bean.isSingleton());
        bean.stop();
        System.out.println(bean.isAutoStartup()+"--"+bean.isRunning()+"---"+bean.isSingleton());
        bean.start();
        System.out.println(bean.isAutoStartup()+"--"+bean.isRunning()+"---"+bean.isSingleton());
    }
}
