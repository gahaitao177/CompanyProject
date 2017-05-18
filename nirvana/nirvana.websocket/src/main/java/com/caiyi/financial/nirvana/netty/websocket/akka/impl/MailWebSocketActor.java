package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.bill.util.MailBillHelper;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;
import javax.inject.Named;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by wenshiliang on 2016/10/26.
 */
@MVCComponent
@Named("/notcontrol/mailbill.go")
@Scope("prototype")
public class MailWebSocketActor extends NeedLoginWebSocketActor {


    public MailWebSocketActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler,
                              Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap, String
                                      url) {
        super(channelHandlerContext, handler, requestParameterMap, requestHeaderMap, url);
    }

    @Resource(name = Constant.HSK_BILL_MAIL)
    public IDrpcClient client;

    private String cuserid;
    private String sid;
    private static final LongAdder TOTAL = new LongAdder();
    public static final Map<String, DefaultChannel> CHANNEL_MAP = new ConcurrentHashMap<>();


    protected boolean addOnline(String cuserid) {
        this.cuserid = cuserid;
        TOTAL.increment();
        logger.info("mail websocket actor addOnline, num {}", TOTAL.sum());
        sid = cuserid + "_" + getChannelHandlerContext().channel().id() + "_" + getJVMName();
        if (CHANNEL_MAP.containsKey(sid)) {
            DefaultChannel context = CHANNEL_MAP.get(sid);
            context.close();
        }
        CHANNEL_MAP.put(sid, new DefaultChannel(getChannelHandlerContext()));
        return true;
    }

    @Override
    protected void preDestroy() {
        if (sid != null && CHANNEL_MAP.containsKey(sid)) {
            CHANNEL_MAP.remove(sid);
            TOTAL.decrement();
        }
        logger.info("mail websocket actor destroy, num {}", TOTAL.sum());
    }


    @Override
    protected void onReceive0(Object message) {
        String msg = (String) message;

//        log.info("from client message count:" + webSocketMap.size());
        logger.info("from client message:" + msg);
        JSONObject resultJson = new JSONObject();
        String method = "";
        String mailtype = "";
        try {
            if (CheckUtil.isNullString(msg)) {
                sendMsg(BillConstant.fail, "传输格式错误", BillConstant.SYS);
                return;
            }
            String ipAddr = getChannelHandlerContext().channel().remoteAddress().toString();
            JSONObject jsb = JSONObject.parseObject(msg);
            method = String.valueOf(jsb.get("method"));
            mailtype = String.valueOf(jsb.get("mailType"));
//            String cuserid = (String)session.getAttributes().get("cuserId");
            logger.info("method:" + method + ",mailtype=" + mailtype + ",cuserid=" + cuserid);
            //TODO
            if (CheckUtil.isNullString(cuserid)) {
                sendMsg(BillConstant.fail, "未登录", method);
                return;
            }
//           base.setCuserId("797666a0104");
            Channel bean = JSONObject.parseObject(msg, Channel.class);
            bean.setCuserId(cuserid);
            bean.setSourceCode(sid);
            bean.setIpAddr(ipAddr);
            logger.info("setSourceCode", bean.getSourceCode());
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("操作成功");
            // 更新
            if (BillConstant.MAIL_BILL_UPDATE.equals(bean.getType())) {
                String result = client.execute(Constant.HSK_BILL_MAIL, new DrpcRequest("mail", "setCreditId", bean));
                bean = JSONObject.parseObject(result, Channel.class);
                if (bean.getBusiErrCode() == 3) {
                    resultJson = new JSONObject();
                    resultJson.put("code", String.valueOf(BillConstant.fail));
                    resultJson.put("desc", "非法数据");
                    resultJson.put("method", BillConstant.TASK);
                    resultJson.put("billId", bean.getBillId());
                    logger.info(resultJson.toString());
                    sendMsg(resultJson);
                    return;
                }
            }
            if (BillConstant.TASK.equals(method)) {
                //  登录
                int resultCode = MailBillHelper.mainLogin(bean);
                if (resultCode != 1) {
                    logger.info("------------------bean.getBusiErrCode():" + bean.getBusiErrCode());
                    logger.info("-------------------bean.getBusiErrDesc():" + bean.getBusiErrDesc());
//                    responseMessage(base.getBusiErrCode(),base.getBusiErrDesc(),BillConstant.TASK);
                    resultJson.put("code", String.valueOf(bean.getBusiErrCode()));
                    resultJson.put("desc", bean.getBusiErrDesc());
                    resultJson.put("method", BillConstant.TASK);
                    resultJson.put("billId", bean.getBillId());
                    if (!CheckUtil.isNullString(bean.getCurrency())) {
                        resultJson.put("imgcode", URLEncoder.encode(bean.getCurrency(), "utf-8"));
                    }
                    if (bean.getBusiErrCode() == 3) {
                        resultJson.put("flag", "1");
                    }
                    logger.info(resultJson.toString());
                    sendMsg(resultJson);
                    return;
                }

            } else if (BillConstant.EXTRACODE.equals(method)) {
                logger.info(cuserid + ",获取验证码:" + bean.getMailAddress());
                String base64Str = MailBillHelper.getVerifyCode(bean);
                if (CheckUtil.isNullString(base64Str)) {
                    sendMsg(BillConstant.fail, bean.getBusiErrDesc(), BillConstant.TASK);
                    return;
                }
                resultJson = new JSONObject();
                resultJson.put("code", String.valueOf(BillConstant.success));
                resultJson.put("desc", bean.getBusiErrDesc());
                resultJson.put("method", BillConstant.EXTRACODE);
                resultJson.put("imgcode", URLEncoder.encode(base64Str, "utf-8"));
                resultJson.put("billId", bean.getBillId());
                resultJson.put("flag", "1");
                logger.info(resultJson.toString());
                sendMsg(resultJson);
                return;
            } else if (BillConstant.CHECKMSG.equals(method)) {
                if (MailBillHelper.checkEmailCode(bean) != 1) {
                    resultJson = new JSONObject();
                    resultJson.put("code", String.valueOf(BillConstant.fail));
                    resultJson.put("desc", bean.getBusiErrDesc());
                    resultJson.put("method", BillConstant.EXTRACODE);
                    bean.setBusiErrCode(1);
                    String base64Str = MailBillHelper.getVerifyCode(bean);
                    if (!CheckUtil.isNullString(base64Str)) {
                        resultJson.put("imgcode", URLEncoder.encode(base64Str, "utf-8"));
                    } else {
                        resultJson.put("imgcode", "");
                    }
                    resultJson.put("billId", bean.getBillId());
                    logger.info(resultJson.toString());
                    sendMsg(resultJson);
                    return;
                }
            } else {
                sendMsg(BillConstant.fail, "非法数据", method);
                return;
            }
            String result = client.execute(Constant.HSK_BILL_MAIL, new DrpcRequest("mail", "createEmailBillTask",
                    bean));
            bean = JSONObject.parseObject(result, Channel.class);
            if (bean.getBusiErrCode() == 0) {
                resultJson.put("code", String.valueOf(BillConstant.fail));
                resultJson.put("desc", bean.getBusiErrDesc());
                resultJson.put("method", BillConstant.TASK);
                resultJson.put("taskId", bean.getTaskId());
                resultJson.put("billId", bean.getBillId());
                logger.info(resultJson.toString());
                sendMsg(resultJson);
                return;
            }
            resultJson = new JSONObject();
            resultJson.put("code", String.valueOf(BillConstant.success));
            resultJson.put("desc", bean.getBusiErrDesc());
            resultJson.put("method", BillConstant.TASK);
            resultJson.put("taskId", bean.getTaskId());
            resultJson.put("billId", bean.getBillId());
            resultJson.put("imgcode", bean.getCurrency());
            logger.info(resultJson.toString());
            sendMsg(resultJson);
            return;
        } catch (Exception e) {
            logger.error("base handleTextMessage 异常", e);
            resultJson.put("code", String.valueOf(BillConstant.fail));
            resultJson.put("desc", "传输格式错误");
            if (CheckUtil.isNullString(method)) {
                resultJson.put("method", BillConstant.SYS);
            } else {
                resultJson.put("mailtype", mailtype);
                resultJson.put("method", method);
            }
            sendMsg(resultJson);
        }
    }


}
