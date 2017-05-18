package com.caiyi.nirvana.analyse;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.nirvana.analyse.enums.SystemEnum;
import com.caiyi.nirvana.analyse.model.MonitorEvent;
import org.junit.Test;

/**
 * Created by been on 2017/3/9.
 */
public class JsonTest {
    @Test
    public void transform() {
        MonitorEvent event = new MonitorEvent();
        event.setContent("eee");
        event.setIp("127.0.0.1");
        event.setKey("key");
        event.setLevel(1);
        event.setSystem(SystemEnum.ACCOUNT);
        event.setSystemName("记账");
        event.setSystemCode("0001");
        event.setUrl("url");
        System.out.println(JSONObject.toJSONString(event, true));
    }
}
