package com.caiyi.nirvana.analyse.cassandra;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.cassandra.test.Been;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

/**
 * Created by been on 2017/1/12.
 */
public class FastJsonTest {
    @Test
    public void test() {
        Been been = new Been(UUID.randomUUID(), "been", new Date());
        //Date 默认转换成 timestamp
        System.out.println(JSONObject.toJSONString(been));
        String data = "{\"ctime\":1484203056434,\"id\":\"5dabd8f7-ec14-47a1-83a1-cab0198d805f\",\"name\":\"been\"}";
        been = JSONObject.parseObject(data, Been.class);
        System.out.println(been.getName());
    }
}
