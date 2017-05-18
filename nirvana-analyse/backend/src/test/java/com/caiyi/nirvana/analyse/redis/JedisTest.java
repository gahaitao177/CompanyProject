package com.caiyi.nirvana.analyse.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * Created by been on 2017/1/13.
 */
public class JedisTest {

    @Test
    public void test() throws Exception {
        Jedis jedis = new Jedis("192.168.1.70", 26747);
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
        jedis.close();
    }
}
