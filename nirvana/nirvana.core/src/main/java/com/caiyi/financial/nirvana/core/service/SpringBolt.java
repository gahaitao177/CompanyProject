package com.caiyi.financial.nirvana.core.service;

import com.caiyi.financial.nirvana.core.util.SpringFactory;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by wenshiliang on 2016/7/26.
 */
public abstract class SpringBolt extends LoggingBolt {
    /**
     * 加载spring
     */
    private ApplicationContext springContext;

    /**
     * 由spring创建对象 service,托管给spring
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> tClass) throws BeansException {
        return springContext.getBean(tClass);
    }

    public Object getBean(String beanId) throws BeansException {
        return springContext.getBean(beanId);
    }

    public void init(Map stormConf, TopologyContext context){
        super.prepare(stormConf, context);
        springContext = SpringFactory.newApplicationContext();
    }

    public ApplicationContext getSpringContext(){
        return springContext;
    }

}
