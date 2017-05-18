package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;
import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by wenshiliang on 2016/10/26.
 */
@MVCComponent
@Named("/notcontrol/getValidMessageCount.go")
@Scope("prototype")
public class MessageWebSocketActor extends NeedLoginWebSocketActor {

    private static final LongAdder TOTAL = new LongAdder();
    public static final Map<String, DefaultChannel> CHANNEL_MAP = new ConcurrentHashMap<>();
    private String cuserid;
    @Resource(name = Constant.HSK_BILL_BANK)
    public IDrpcClient client;

    public MessageWebSocketActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler,
                                 Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap,
                                 String url) {
        super(channelHandlerContext, handler, requestParameterMap, requestHeaderMap, url);
    }

    @Override
    protected boolean addOnline(String cuserid) {
        if (cuserid == null) {
            return false;
        }
        this.cuserid = cuserid;
        logger.info("message websocket actor addOnline, num {}", TOTAL.sum());
        if (!CHANNEL_MAP.containsKey(cuserid)) {
            CHANNEL_MAP.put(cuserid, new DefaultChannel(getChannelHandlerContext()));
            TOTAL.increment();
            sendMsg(BillConstant.success, "连接成功", "sys");
            String resultBolt = client.execute(new DrpcRequest("message", "getValidMessageCount", cuserid));
            JSONObject json = JSONObject.parseObject(resultBolt);
            JSONObject result = new JSONObject();
            //1 表示有新的消息 0是无
            if (json != null && json.get("code") != null && json.getInteger("code") == 1) {
                result.put("type", 1);
            } else {
                result.put("type", 0);
            }
            sendMsg(result);
            return true;
        }
        return false;
    }


    @Override
    protected void preDestroy() {
        if (cuserid != null) {
            if (CHANNEL_MAP.containsKey(cuserid)) {
                CHANNEL_MAP.remove(cuserid);
                TOTAL.decrement();
            }
            logger.info("message websocket actor destroy, num {}", TOTAL.sum());
        }
    }

    @Override
    protected void onReceive0(Object message) {

    }


}
