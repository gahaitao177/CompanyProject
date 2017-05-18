package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.bill.bank.GuangDaBank;
import com.caiyi.financial.nirvana.bill.bank.JiaoTongBank;
import com.caiyi.financial.nirvana.bill.bank.ZhaoShangBank;
import com.caiyi.financial.nirvana.bill.bank.multibank.PluginBankService;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.util.BankHelper;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ResponseEntity;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;
import javax.inject.Named;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by zhoupinghua on 2016/02/17.
 */
@MVCComponent
@Named("/notcontrol/bankbillte.go")
@Scope("prototype")
public class BankTeWebSocketActor extends NeedLoginWebSocketActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(BankTeWebSocketActor.class);

    private static final LongAdder TOTAL = new LongAdder();
    public static final Map<String,DefaultChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private String cuserid;
    private String sid;

    @Resource(name = Constant.HSK_USER)
    private IDrpcClient client;

    public BankTeWebSocketActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler, Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap, String url) {
        super(channelHandlerContext, handler, requestParameterMap, requestHeaderMap, url);
    }


    protected boolean addOnline(String cuserid){
        this.cuserid = cuserid;
        TOTAL.increment();
        LOGGER.info("bank websocket actor addOnline, num {}",TOTAL.sum());
        sid = cuserid + "_"+getChannelHandlerContext().channel().id()+"_"+getJVMName();
        if(CHANNEL_MAP.containsKey(sid)){
            DefaultChannel context = CHANNEL_MAP.get(sid);
            context.close();
        }
        CHANNEL_MAP.put(sid,new DefaultChannel(getChannelHandlerContext()));
        return true;
    }

    @Override
    protected void preDestroy() {
        if(CHANNEL_MAP.containsKey(sid)){
            CHANNEL_MAP.remove(sid);
            TOTAL.decrement();
        }
        LOGGER.info("bank websocket actor destroy, num {}",TOTAL.sum());
        LOGGER.info("sid==="+sid);
        //windows 插件发送关闭窗口命令
        if(AbstractHttpService.loginContextMap.containsKey(sid+"userRand")){
            Channel bean = new Channel();
            bean.setSourceCode(sid);
            bean.setCuserId(cuserid);
            PluginBankService service = new PluginBankService();
            service.closeWindow(bean);
        }
    }


    @Override
    public void onReceive0(Object message) {
        String msg = (String)message;
        LOGGER.info("from client message:" + msg);
        ResponseEntity responseEntity = new ResponseEntity();
        String method="";
        String bankid="";
        String isFrist="";
        try{
            if (CheckUtil.isNullString(msg)){
                responseEntity.setCode(BillConstant.fail+"");
                responseEntity.setDesc("传输格式错误");
                responseEntity.setMethod("sys");
                sendMsg(JSONObject.toJSONString(responseEntity));
                return;
            }
            String ipAddr = getClientIp();
            LOGGER.info("from client ipAddr:" + ipAddr);
            JSONObject jsb = JSONObject.parseObject(msg);
            method=String.valueOf(jsb.get("method"));
            bankid=String.valueOf(jsb.get("bankId"));
            if (CheckUtil.isNullString(cuserid)){
                responseEntity.setCode(BillConstant.fail+"");
                responseEntity.setDesc("用户未登录");
                responseEntity.setMethod(method);
                sendMsg(JSONObject.toJSONString(responseEntity));
                return;
            }
            Channel bean=JSONObject.parseObject(msg,Channel.class);
            bean.setCuserId(cuserid);
            bean.setSourceCode(sid);
            bean.setIpAddr(ipAddr);
            LOGGER.info("souceCode============="+bean.getSourceCode());
            int code=0;
            if (BillConstant.EXTRACODE.equals(method)){
                isFrist=String.valueOf(jsb.get("isFrist"));
                if (CheckUtil.isNullString(isFrist)||(!"0".equals(isFrist)&&!"1".equals(isFrist))){
                    isFrist="0";
                }
                int flag= BankHelper.readExtracodeFlag(bean,isFrist);
                if (flag==-1){
                    responseEntity.setCode(BillConstant.fail+"");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setFlag(flag+"");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }else if (flag==0){
                    responseEntity.setCode(BillConstant.success+"");
                    responseEntity.setDesc("不需要图片验证码");
                    responseEntity.setFlag(flag+"");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                LOGGER.info(bean.getCuserId()+"  from client readExtracodeFlag success");
                //获取图片验证码
                String base64Img= BankHelper.getBankVerifyCode(bean,cc);
                if ("-1".equals(base64Img)){
                    responseEntity.setCode(BillConstant.fail+"");
                    responseEntity.setDesc("无效的银行类型");
                    responseEntity.setFlag(flag+"");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                }else if (CheckUtil.isNullString(base64Img)){
                    responseEntity.setCode(BillConstant.fail+"");
                    responseEntity.setDesc("获取验证码失败");
                    responseEntity.setFlag(flag+"");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                }else{
                    responseEntity.setCode(BillConstant.success+"");
                    responseEntity.setDesc("获取图片验证成功");
                    responseEntity.setFlag(flag+"");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    responseEntity.setImgcode(URLEncoder.encode(base64Img, "utf-8"));
                    sendMsg(JSONObject.toJSONString(responseEntity));
                }

            }else if (BillConstant.TASK.equals(method)){
                String result = client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("bank", "setCreditId", bean));
                bean=JSONObject.parseObject(result,Channel.class);
                if (bean.getBusiErrCode()==3){
                    responseEntity.setCode(BillConstant.fail+"");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                LOGGER.info(bean.getCuserId() + bean.getBankId() + "TASK begin["+bean.getBillId()+"]["+bean.getCreditId()+"]["+bean.getOutsideId()+"]");

                bean.setIpAddr(ipAddr);

                if (BillConstant.ZHAOSHANG.equals(bean.getBankId())) {
                    ZhaoShangBank zhaoShangBank = new ZhaoShangBank();
                    zhaoShangBank.taskReceve_te(bean, cc);
                    code = 0;
                } else if(BillConstant.JIAOTONG.equals(bean.getBankId())) {
                    JiaoTongBank jiaoTongBank = new JiaoTongBank();
                    jiaoTongBank.loginSendSms(bean, cc);
                    code = 0;
                }else {
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("验证码失效");
                    LOGGER.info(bean.getCuserId() + bean.getBankId() + "bankSession" + " 验证码失效");
                    code = 0;
                }

                if (bean.getBusiErrCode()==3){//需要图片验证码
                    String base64Img=BankHelper.getBankVerifyCode(bean,cc);
                    responseEntity.setCode(bean.getBusiErrCode()+"");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    responseEntity.setImgcode(URLEncoder.encode(base64Img, "utf-8"));
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }

                if (code==0){
                    responseEntity.setCode(bean.getBusiErrCode()+"");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    if (bean.getBusiErrCode()==2){
                        responseEntity.setPhoneCode(bean.getPhoneCode());
                    }
                    if(BillConstant.ZHAOSHANG.equals(bankid) && bean.getBusiErrCode().toString().equals(BillConstant.needimg+"")){
                        String base64Img = BankHelper.getBankVerifyCode(bean,cc);
                        responseEntity.setImgcode(URLEncoder.encode(base64Img, "utf-8"));
                    }
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
            } else if (BillConstant.SENDMSGLOGIN.equals(method)) {
                BankHelper.send_Sms(bean, cc, client);
                if (!CheckUtil.isNullString(bean.getPhoneCode())) {
                    responseEntity.setPhoneCode(bean.getPhoneCode());
                }
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(BillConstant.SENDMSGLOGIN);
                responseEntity.setCode(bean.getBusiErrCode() + "");
                responseEntity.setDesc(bean.getBusiErrDesc());
                sendMsg(JSONObject.toJSONString(responseEntity));
            }else if (BillConstant.CHECKMSGLOGIN.equals(method)) {
                code = BankHelper.check_Sms(bean, cc, client);
                if(code == 1){
                    if (BillConstant.ZHAOSHANG.equals(bean.getBankId())) {
                        bean.setBusiErrCode(BillConstant.needmsg);
                        bean.setBusiErrDesc("提额需要短信验证码!");
                    }
                }

                responseEntity.setCode(bean.getBusiErrCode() + "");
                responseEntity.setDesc(bean.getBusiErrDesc());
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(BillConstant.CHECKMSGLOGIN);
                sendMsg(JSONObject.toJSONString(responseEntity));
                return;
            }else if (BillConstant.SENDMSG.equals(method)) {
                BankHelper.send_teSms(bean, cc, client);
                if (!CheckUtil.isNullString(bean.getPhoneCode())) {
                    responseEntity.setPhoneCode(bean.getPhoneCode());
                }
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(BillConstant.SENDMSG);
                responseEntity.setCode(bean.getBusiErrCode() + "");
                responseEntity.setDesc(bean.getBusiErrDesc());
                sendMsg(JSONObject.toJSONString(responseEntity));
            } else if (BillConstant.CHECKMSG.equals(method)) {
                String result = client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("bank", "setCreditId", bean));
                bean = JSONObject.parseObject(result, Channel.class);
                code= BankHelper.check_teSms(bean, cc, client);

                if (code==0){
                    responseEntity.setCode(bean.getBusiErrCode()+"");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.CHECKMSG);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
            }else{
                responseEntity.setCode(BillConstant.fail+"");
                responseEntity.setDesc("非法的操作类型");
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(BillConstant.SYS);
                sendMsg(JSONObject.toJSONString(responseEntity));
            }

        }catch (Exception e){
            LOGGER.error("handleTextMessage 异常", e);
            responseEntity.setCode(BillConstant.fail+"");
            responseEntity.setDesc("传输格式错误");
            if (StringUtils.isEmpty(method)){
                responseEntity.setMethod(BillConstant.SYS);
            }else{
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(method);
            }
            sendMsg(JSONObject.toJSONString(responseEntity));
        }
    }


}
