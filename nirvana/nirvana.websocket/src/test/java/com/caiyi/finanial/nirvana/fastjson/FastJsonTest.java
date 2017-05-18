package com.caiyi.finanial.nirvana.fastjson;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by been on 2016/12/1.
 */
public class FastJsonTest {
    @Test
    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        String result = JSONObject.toJSONString(map);
        System.out.println(result);
        SerializeWriter writer = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(writer, new SerializeConfig());
        ObjectSerializer intSerializer = new ObjectSerializer() {
            @Override
            public void write(JSONSerializer serializer, Object object,
                              Object fieldName, Type fieldType, int features) throws IOException {
                serializer.write(object.toString());
            }
        };
        serializer.getMapping().put(int.class, intSerializer);
        System.out.println(JSONObject.toJSONString(map, serializer.getMapping()));

    }
}
