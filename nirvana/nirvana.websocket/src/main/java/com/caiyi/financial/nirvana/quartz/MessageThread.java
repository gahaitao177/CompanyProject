package com.caiyi.financial.nirvana.quartz;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.netty.websocket.akka.NettyChannel;
import com.caiyi.financial.nirvana.netty.websocket.akka.impl.MessageWebSocketActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

/**
 * Created by lizhijie on 2016/9/29.
 */
public class MessageThread {

    private static Logger logger = LoggerFactory.getLogger(MessageThread.class);

    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;

    public void run() {
        logger.info("---------------run");
//        for (String userid: MessageHandler.webSocketMap.keySet()){
//            String userMessageResult=client.execute(new DrpcRequest("message","getValidMessageCount",userid));
//            JSONObject resultJson=JSONObject.parseObject(userMessageResult);
//            if(resultJson!=null&&"1".equals(resultJson.getString("code"))){
//                WebSocketSession socketSession=MessageHandler.webSocketMap.get(userid);
//                if(socketSession!=null){
//                    JSONObject data=new JSONObject();
//                    data.put("type",1);
//                    try {
//                        socketSession.sendMessage(new TextMessage(data.toJSONString()));
//                        logger.info("后台推送 用户={}，发送消息={}",  userid,data.toJSONString());
//                    } catch (IOException e) {
//                        logger.info("后台推送 用户={}，发送消息异常={}",  userid,e.toString());
//                    }
//                }
//            }
//        }
        //update by wsl in 2016年10月27日
        for (String userid : MessageWebSocketActor.CHANNEL_MAP.keySet()) {
            String userMessageResult = client.execute(new DrpcRequest("message", "getValidMessageCount", userid));
            JSONObject resultJson = JSONObject.parseObject(userMessageResult);
            if (resultJson != null && "1".equals(resultJson.getString("code"))) {
                NettyChannel channel = MessageWebSocketActor.CHANNEL_MAP.get(userid);
                if (channel != null && channel.isOpen()) {
                    JSONObject data = new JSONObject();
                    data.put("type", 1);
                    channel.sendMsg(data.toJSONString());
                } else {
                    logger.info("sid=" + userid + " is null or close", channel);
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Test start.");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-quartz.xml");


        MessageThread obj = context.getBean(MessageThread.class);
        obj.run();
        System.out.print("Test end..");
    }
}
