package com.caiyi.financial.nirvana.core.cassandra;

import com.caiyi.financial.nirvana.core.cassandra.client.CassandraClient;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Mario on 2016/10/12 0012.
 * CassandClient示例
 */
public class CassandraPoolDemo {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-cassandra.xml");
        CassandraClient client = context.getBean(CassandraClient.class);
        Session s1 = client.connect();
        Session s2 = client.connect();
        Session s3 = client.connect();
        Session s4 = client.connect();
        Session s5 = client.connect();

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
        System.out.println(s5);

        client.getCluster().close();
    }
}
