package com.caiyi.financial.nirvana.netty.websocket.akka;

import akka.actor.UntypedActor;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.util.SpringFactory;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import com.danga.MemCached.MemCachedClient;
import com.rbc.http.client.Cert;
import com.rbc.http.client.CertHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

public abstract class WebSocketBaseActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketBaseActor.class);

    static {
        try {
            String certId = SystemConfig.get("security.certId");
            String certFile = SystemConfig.get("security.certFile");
            String preUrl = SystemConfig.get("security.preUrl");
            String agentCode = SystemConfig.get("security.agentCode");
            String encodeIng = SystemConfig.get("security.encodeIng");
            CertHelper ch = CertHelper.getHttpCert();
            Cert cert = new Cert(certId, agentCode, preUrl, certFile, encodeIng);
            ch.putCert(cert);
            LOGGER.info("BankWebSocketHandler 加载证书成功");
        } catch (Exception e) {
            LOGGER.error("BankWebSocketHandler 加载证书失败", e);
        }
    }


    private ChannelHandlerContext channelHandlerContext;
    private WebSocketServerHandler handler;
    private Map<String, String[]> requestParameterMap;//第一次请求为http请求，携带的参数
    private Map<String, String> requestHeaderMap;
    private String url;//对应的url地址


    public WebSocketBaseActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler,
                              Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap, String
                                      url) {
        this.channelHandlerContext = channelHandlerContext;
        this.handler = handler;
        this.requestParameterMap = requestParameterMap;
        this.requestHeaderMap = requestHeaderMap;
        this.url = url;
//        LOGGER.info("创建了一个WebSocketBaseActor");
    }

    @Autowired
    protected MemCachedClient cc;

    public Map<String, String[]> getRequestParameterMap() {
        return requestParameterMap;
    }

    public String getUrl() {
        return url;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    /**
     * 初始化 Actor 后，ActoRef会发送checkAvailable到onReceive中调用。
     * 返回false。证明不可用
     *
     * @return
     */
    public boolean checkAvailable() {
        return true;
    }


    public void onReceive(Object message) throws Exception {
        if ("stop".equals(message)) {
//            LOGGER.info(getJVMName());
//            LOGGER.info(getJVMName());`
            context().stop(self());
        } else if ("checkAvailable".equals(message)) {
            boolean flag = checkAvailable();
            if (!flag) {
                context().stop(self());
            }
        } else {
//            if("test".equals(message)){
//                sendMsg("test");
//                self().tell("1111",null);
//            }
            onReceive0(message);
        }

    }

    protected abstract void onReceive0(Object message);

    /**
     * postStop 时候调用。
     */
    protected abstract void preDestroy();

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(msg));
    }

    public void sendMsg(Map<String, Object> resultMap) {
        if (resultMap == null || resultMap.size() == 0) {
            LOGGER.info("发送消息失败");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            builder.append("\"")
                    .append(entry.getKey()).append("\":\"")
                    .append(entry.getValue().toString()).append("\",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        sendMsg(builder.toString());
    }

    public void sendMsg(int code, String desc, String method) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", String.valueOf(code));
        jsonObject.put("desc", desc);
        jsonObject.put("method", method);
        sendMsg(jsonObject);
    }


    @Override
    public void postStop() throws Exception {
        preDestroy();
        super.postStop();
        channelHandlerContext.close();
    }


    public String getJVMName() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return SpringFactory.getAddressIp() + ":" + jvmName;
    }

    public Map<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    /**
     * 获得websocket client端真实ip
     *
     * @return
     */
    public String getClientIp() {
//        requestHeaderMap.entrySet().forEach(entry -> {
//            LOGGER.info(entry.getKey() + "---------------" + entry.getValue());
//        });
        String ip = requestHeaderMap.get("X-Forwarded-For");
        if (ip == null) {
//            SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();
            InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
//            LOGGER.info(socketAddress.getClass().toString());
            ip = socketAddress.getHostName();
        } else if (ip.length() > 15 && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}