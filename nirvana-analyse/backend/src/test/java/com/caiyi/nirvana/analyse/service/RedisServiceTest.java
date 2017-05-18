package com.caiyi.nirvana.analyse.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by wenshiliang on 2017/1/20.
 */
public class RedisServiceTest {
    @Test
    public void set() throws Exception {
        RedisService redisService = new RedisService();
        boolean flag = redisService.set("test", "test1111");
        Assert.assertTrue(flag);
    }

    @Test
    public void set1() throws Exception {
        RedisService redisService = new RedisService();
        boolean flag = redisService.set("test", "test11222", 10, TimeUnit.SECONDS);
        Assert.assertTrue(flag);
        Assert.assertEquals("test11222", redisService.get("test"));
    }

    @Test
    public void del() throws Exception {
        RedisService redisService = new RedisService();
        System.out.println(redisService.del("test"));
    }

}