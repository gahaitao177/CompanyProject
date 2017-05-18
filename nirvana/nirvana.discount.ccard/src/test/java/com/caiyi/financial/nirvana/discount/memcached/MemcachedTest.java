package com.caiyi.financial.nirvana.discount.memcached;

import com.danga.MemCached.MemCachedClient;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wenshiliang on 2016/6/1.
 */
public class MemcachedTest {
    @Test
    public void test(){
        MemCachedClient client = new ClassPathXmlApplicationContext("spring-context.xml").getBean(MemCachedClient.class);
        boolean flag =  client.set("aaa","tttttt");
        System.out.println(flag);
        System.out.println(client.get("aaa"));
    }
}
