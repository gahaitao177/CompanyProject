package com.youyu.demo.cat;

import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2017/5/16.
 */
public class MyCatContext implements Cat.Context {
    private Map<String, String> properties = new HashMap<String, String>();

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }
}
