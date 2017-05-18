package com.caiyi.nirvana.analyse.util;

import org.apache.storm.utils.DRPCClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by been on 16/9/13.
 */
public interface DRPCSupport {
    String RESULT = "result";

    /**
     * 每次请求new 一个 drpc client
     *
     * @param func
     * @param params
     * @return
     * @throws Exception
     */
    default Map<String, Object> doBussiness(String func, String params) throws Exception {

        return _business(func, params);
    }

    default Map<String, Object> _business(String func, String params) throws Exception {
        DRPCClient client = DrpcClientFactory.getDefaultDRPCClient();
        Map<String, Object> map = new HashMap<>();
        try {
            String result = client.execute(func, params);
            map.put(RESULT, result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }

        return map;
    }


}
