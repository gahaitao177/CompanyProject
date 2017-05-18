package com.caiyi.financial.nirvana.bill.kafka;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.netty.websocket.akka.NettyChannel;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.BankWebSocketActor;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.DefaultChannel;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.MailWebSocketActor;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.MessageWebSocketActor;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mario on 2015/10/30.
 */
public class KafkaConsumer implements Runnable {
    private KafkaStream<byte[], byte[]> m_stream;
    private int m_threadNumber;
    private String type = "";
    public Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public KafkaConsumer(KafkaStream<byte[], byte[]> a_stream, int a_threadNumber, String a_type) {
        this.m_stream = a_stream;
        this.m_threadNumber = a_threadNumber;
        this.type = a_type;
    }

    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        logger.info("KafkaConsumer:run:start");
        while (it.hasNext()) {
//			logger.info("KafkaConsumer:BankWebSocketHandler getOnlineCount=" + BankWebSocketHandler.getOnlineCount());

            String _retStr = new String(it.next().message());
            try {
                String retStr = new String(_retStr.getBytes("utf-8"));
                logger.info("KafkaConsumer:run1" + retStr);
                JSONObject jsb = JSONObject.parseObject(retStr);
                String method = String.valueOf(jsb.get("method"));
                if (method.equals(BillConstant.RESULT)) {
                    String key = String.valueOf(jsb.get("sid"));
                    logger.info("KafkaConsumer:cuserId_sid=" + key);
                    DefaultChannel channel = null;
                    //update by wsl in 2016年10月26日 改为netty+akka实现webscoket
                    if ("mail".equals(type)) {
//						logger.info("KafkaConsumer:MailWebSocketHandler getOnlineCount="+ MailWebSocketHandler
// .webSocketMap.size() );
//						key = String.valueOf(jsb.get("sid"));
//						session = MailWebSocketHandler.webSocketMap.get(key);
                        channel = MailWebSocketActor.CHANNEL_MAP.get(key);
                    } else {
//						session = BankWebSocketHandler.webSocketMap.get(key);
                        channel = BankWebSocketActor.CHANNEL_MAP.get(key);
                    }
                    if (channel == null || !channel.isOpen()) {
                        logger.debug("type=" + type + "sid=" + key + "channel {} is null or close", channel);
                    } else {
                        sendMsg(channel, retStr);
                    }


//					if (session!= null&&session.isOpen()) {
//						synchronized(session) {
//							session.sendMessage(new TextMessage(retStr));
//						}
//					}else if (session!=null&&!session.isOpen()){
//						if ("mail".equals(type)){
//							MailWebSocketHandler.webSocketMap.remove(key);
//						}else{
//							BankWebSocketHandler.webSocketMap.remove(key);
//						}
//						logger.info("send error cuserId_sid[" + key + "] session is close");
//					}else{
//						logger.info("send error cuserId_sid["+key+"] session is null["+session+"]");
//					}
                } else if (method.equals(BillConstant.MESSAGE) || "message".equals(type)) {
                    String key = String.valueOf(jsb.get("userId"));
                    logger.info("KafkaConsumer:cuserId_sid=" + key);
                    DefaultChannel channel = MessageWebSocketActor.CHANNEL_MAP.get(key);
                    if (channel == null || !channel.isOpen()) {
                        logger.error("type=" + type + "sid=" + key + "channel {} is null or close", channel);
                    } else {
                        jsb.remove("userId");
                        sendMsg(channel, jsb.toString());
                    }
                }
            } catch (Exception e) {
                logger.error("KafkaConsumer:run:e ", e);
            }
        }
        logger.info("Shutting down Thread: " + m_threadNumber);
    }

    public void sendMsg(NettyChannel channel, String msg) {
        channel.sendMsg(msg); //不保证一定发送成功
    }
}
