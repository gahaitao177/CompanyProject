package com.caiyi.financial.nirvana.quartz;

import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.netty.websocket.akka.NettyChannel;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.BankTeWebSocketActor;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.BankWebSocketActor;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.MailWebSocketActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2016/8/5.
 */
public class CloseScoket {
    private static Logger logger = LoggerFactory.getLogger(CloseScoket.class);
    private static int timeout = SystemConfig.getInt("webscoket_timeout");

    public void run() {
        logger.info("CloseSocket begin=======================================");

        try {
            List<NettyChannel> list = new ArrayList<>();
            for (NettyChannel channel : BankWebSocketActor.CHANNEL_MAP.values()) {
                long systime = System.currentTimeMillis();
                if ((systime - channel.getCreateTime()) / 1000 / 60 >= timeout) {
                    list.add(channel);
                }
            }
            for (NettyChannel channel : BankTeWebSocketActor.CHANNEL_MAP.values()) {
                long systime = System.currentTimeMillis();
                if ((systime - channel.getCreateTime()) / 1000 / 60 >= timeout) {
                    list.add(channel);
                }
            }
            for (NettyChannel channel : MailWebSocketActor.CHANNEL_MAP.values()) {
                long systime = System.currentTimeMillis();
                if ((systime - channel.getCreateTime()) / 1000 / 60 >= timeout) {
                    list.add(channel);
                }
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                NettyChannel channel = list.get(i);
                channel.close();
            }
//            Map<String,WebSocketSession> bankwebSocketMap=BankWebSocketHandler.webSocketMap;
//            for (String key: bankwebSocketMap.keySet()){
//                WebSocketSession session=bankwebSocketMap.get(key);
//                long linkTime=(Long)session.getAttributes().get("linkTime");
//                long systime=System.currentTimeMillis();
//                if ((systime-linkTime)/1000/60>=timeout){
//                    BankWebSocketHandler.webSocketMap.remove(session);
//                    logger.info("remove key="+key);
//                    if (session.isOpen()){
//                        session.close();
//                    }
//                }
//            }
//            Map<String,WebSocketSession> mailwebSocketMap= MailWebSocketHandler.webSocketMap;
//            for (String key: mailwebSocketMap.keySet()){
//                WebSocketSession session=mailwebSocketMap.get(key);
//                long linkTime=(Long)session.getAttributes().get("linkTime");
//                long systime=System.currentTimeMillis();
//                if ((systime-linkTime)/1000/60>=timeout){
//                    MailWebSocketHandler.webSocketMap.remove(session);
//                    if (session.isOpen()){
//                        session.close();
//                    }
//                }
//            }

            //update by wsl in 2016年10月26日

            logger.info("CloseSocket end=======================================");
        } catch (Exception e) {
            logger.error("CloseScoket 异常", e);
        }

    }

    public static void main(String[] args) {
        System.out.println("Test start.");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-quartz.xml");


        CloseScoket obj = context.getBean(CloseScoket.class);
        obj.run();
        System.out.print("Test end..");
    }
}
