package com.caiyi.nirvana.analyse.service;

import com.caiyi.nirvana.analyse.env.Profile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2017/1/20.
 */
public class RedisService {

    private JedisPool jedisPool;

    public RedisService() {
        Properties properties = new Properties();
        try {
            if (Profile.instance.isProd()) {
                properties.load(getClass().getResourceAsStream("/redis_prod.properties"));
            } else {
                properties.load(getClass().getResourceAsStream("/redis_dev.properties"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String host = properties.getProperty("host");
        int port = Integer.parseInt(properties.getProperty("port"));
        int maxIdle = Integer.parseInt(properties.getProperty("maxIdle"));
        int minIdle = Integer.parseInt(properties.getProperty("minIdle"));
        int maxWaitMillis = Integer.parseInt(properties.getProperty("maxWaitMillis"));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, 3000);
    }

    public boolean set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(key, value);
            if (Objects.equals("OK", result)) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public boolean set(String key, String value, long time, TimeUnit timeUnit) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            String result = jedis.set(key, value, "NX", "PX", timeUnit.toMillis(time));
            if (Objects.equals("OK", result)) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public long del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
