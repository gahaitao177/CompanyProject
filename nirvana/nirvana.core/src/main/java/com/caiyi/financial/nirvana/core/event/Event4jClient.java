package com.caiyi.financial.nirvana.core.event;

import cn.aofeng.event4j.Event;
import cn.aofeng.event4j.EventDispatch;

/**
 * Created by terry on 2016/9/21.
 */
public class Event4jClient {
    static {
        //EventDispatch.getInstance().setConfigFile("/WebContent/WEB-INF/config/event4j.xml");
        EventDispatch.getInstance().init();
    }

    public static void eventTest(EventEnum ee,String method,String cuserid,String ip) {
        LogInfo info=new LogInfo();
        info.setEventEnum(ee);
        info.setMethod(method);
        info.setCuserId(cuserid);
        info.setIp(ip);
        Event<LogInfo> event = new Event<LogInfo>("ReadLineComplete", info);
        EventDispatch.getInstance().dispatch(event);
    }





}
