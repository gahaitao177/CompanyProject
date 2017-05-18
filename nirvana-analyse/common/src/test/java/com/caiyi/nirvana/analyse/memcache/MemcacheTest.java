package com.caiyi.nirvana.analyse.memcache;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by been on 2017/2/27.
 */
public class MemcacheTest {
    @Test
    public void testConnections() throws Exception {
//        String[] servers = {"192.168.1.37:25030"};
        String[] servers = {"192.168.1.51:10112"};
        SockIOPool pool = SockIOPool.getInstance("Test1");
        pool.setServers(servers);
        pool.setFailover(true);
        pool.setInitConn(10);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();
        MemCachedClient mcc = new MemCachedClient("Test1");
        System.out.println("add status:" + mcc.add("1", "Original"));
        System.out.println("Get from Cache:" + mcc.get("1"));
        System.out.println("add status:" + mcc.add("1", "Modified"));
        System.out.println("Get from Cache:" + mcc.get("1"));
        System.out.println("set status:" + mcc.set("1", "Modified"));
        System.out.println("Get from Cache after set:" + mcc.get("1"));
        System.out.println("remove status:" + mcc.delete("1"));
        System.out.println("Get from Cache after delete:" + mcc.get("1"));
        mcc.set("2", "2");
        mcc.set("3", "3");
        mcc.set("4", "4");
        mcc.set("5", "5");
        String[] keys = {"1", "2", "3", "INVALID", "5"};
        HashMap<String, Object> hm = (HashMap<String, Object>) mcc.getMulti(keys);
        for (String key : hm.keySet()) {
            System.out.println("KEY:" + key + " VALUE:" + hm.get(key));
        }
    }
}

