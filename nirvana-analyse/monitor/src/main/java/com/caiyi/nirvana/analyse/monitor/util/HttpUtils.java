package com.caiyi.nirvana.analyse.monitor.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by been on 2017/3/7.
 */
public class HttpUtils {
    public static String doPost(String url, Map<String, String> props) throws Exception {
        List<NameValuePair> nvp = new ArrayList<>();
        props.forEach((key, value) -> nvp.add(new BasicNameValuePair(key, value)));
        String result = Request
                .Post(url)
//                .bodyForm(nvp)
                .bodyString(JSONObject.toJSONString(props), ContentType.APPLICATION_JSON)
//                .body(new StringEntity(JSONObject.toJSONString(props)))
                .addHeader("Content-Type", "application/json")
                .execute().returnContent().asString();
        return result;

    }
}
