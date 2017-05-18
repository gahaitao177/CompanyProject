package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.bill.bank.*;
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
 * Created by wenshiliang on 2016/10/25.
 */
@MVCComponent
@Named("/notcontrol/bankbill.go")
@Scope("prototype")
public class BankWebSocketActor extends NeedLoginWebSocketActor {
    public static final Logger LOGGER = LoggerFactory.getLogger(BankWebSocketActor.class);

    private static final LongAdder TOTAL = new LongAdder();
    public static final Map<String, DefaultChannel> CHANNEL_MAP = new ConcurrentHashMap<>();


//    private static Map<ChannelHandlerContext,String> userIdMap = new ConcurrentHashMap<>();

    private String cuserid;
    private String sid;

    @Resource(name = Constant.HSK_USER)
    private IDrpcClient client;

    public BankWebSocketActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler,
                              Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap, String
                                      url) {
        super(channelHandlerContext, handler, requestParameterMap, requestHeaderMap, url);
    }


    protected boolean addOnline(String cuserid) {
        this.cuserid = cuserid;
        TOTAL.increment();
        LOGGER.info("bank websocket actor addOnline, num {}", TOTAL.sum());
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
        if (CHANNEL_MAP.containsKey(sid)) {
            CHANNEL_MAP.remove(sid);
            TOTAL.decrement();
        }
        LOGGER.info("bank websocket actor destroy, num {}", TOTAL.sum());
        LOGGER.info("sid===" + sid);
        //windows 插件发送关闭窗口命令
        if (AbstractHttpService.loginContextMap.containsKey(sid + "userRand")) {
            Channel bean = new Channel();
            bean.setSourceCode(sid);
            bean.setCuserId(cuserid);
            PluginBankService service = new PluginBankService();
            service.closeWindow(bean);
        }
        if (cc.get(sid+"puFa_getSmsToken")!=null){
            cc.delete(sid+"puFa_getSmsToken");//清除浦发短信缓存
        }
    }


    @Override
    public void onReceive0(Object message) {
        String msg = (String) message;
        LOGGER.info("from client message:" + msg);
        ResponseEntity responseEntity = new ResponseEntity();
        String method = "";
        String bankid = "";
        String isFrist = "";
        try {
            if (CheckUtil.isNullString(msg)) {
                responseEntity.setCode(BillConstant.fail + "");
                responseEntity.setDesc("传输格式错误");
                responseEntity.setMethod("sys");
                sendMsg(JSONObject.toJSONString(responseEntity));
                return;
            }
            String ipAddr = getClientIp();
            LOGGER.info("from client ipAddr:" + ipAddr);
            JSONObject jsb = JSONObject.parseObject(msg);
            method = String.valueOf(jsb.get("method"));
            bankid = String.valueOf(jsb.get("bankId"));
            if (CheckUtil.isNullString(cuserid)) {
                responseEntity.setCode(BillConstant.fail + "");
                responseEntity.setDesc("用户未登录");
                responseEntity.setMethod(method);
                sendMsg(JSONObject.toJSONString(responseEntity));
                return;
            }
            Channel bean = JSONObject.parseObject(msg, Channel.class);
            bean.setCuserId(cuserid);
            bean.setSourceCode(sid);
            bean.setIpAddr(ipAddr);
            LOGGER.info("souceCode=============" + bean.getSourceCode());
            int code = 0;
            if (BillConstant.EXTRACODE.equals(method)) {
                isFrist = String.valueOf(jsb.get("isFrist"));
                if (CheckUtil.isNullString(isFrist) || (!"0".equals(isFrist) && !"1".equals(isFrist))) {
                    isFrist = "0";
                }
                int flag = BankHelper.readExtracodeFlag(bean, isFrist);
                if (flag == -1) {
                    responseEntity.setCode(BillConstant.fail + "");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setFlag(flag + "");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                } else if (flag == 0) {
                    responseEntity.setCode(BillConstant.success + "");
                    responseEntity.setDesc("不需要图片验证码");
                    responseEntity.setFlag(flag + "");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                LOGGER.info(bean.getCuserId() + "  from client readExtracodeFlag success");
                //获取图片验证码
                String base64Img = BankHelper.getBankVerifyCode(bean,cc);
                if ("-1".equals(base64Img)) {
                    responseEntity.setCode(BillConstant.fail + "");
                    responseEntity.setDesc("无效的银行类型");
                    responseEntity.setFlag(flag + "");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                } else if (CheckUtil.isNullString(base64Img)) {
                    responseEntity.setCode(BillConstant.fail + "");
                    responseEntity.setDesc("获取验证码失败");
                    responseEntity.setFlag(flag + "");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                } else {
                    responseEntity.setCode(BillConstant.success + "");
                    responseEntity.setDesc("获取图片验证成功");
                    responseEntity.setFlag(flag + "");
                    responseEntity.setIsFrist(isFrist);
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(method);
                    responseEntity.setImgcode(URLEncoder.encode(base64Img, "utf-8"));
                    sendMsg(JSONObject.toJSONString(responseEntity));
                }
                //this.sendMessage("{code:\"1\",desc:\"获取图片验证成功\",imgcode:\"" + URLEncoder.encode(base64Img, "utf-8")
                // + "\",method:\"0\"}");

            } else if (BillConstant.TASK.equals(method)) {
                String result = client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("bank", "setCreditId", bean));
                bean = JSONObject.parseObject(result, Channel.class);
                if (bean.getBusiErrCode() == 3) {
                    responseEntity.setCode(BillConstant.fail + "");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                LOGGER.info(bean.getCuserId() + bean.getBankId() + "TASK begin[" + bean.getBillId() + "][" + bean
                        .getCreditId() + "][" + bean.getOutsideId() + "]");

                String ckey = bean.getCuserId() + bean.getBankId() + "bankSession";
                Object object = cc.get(ckey);
                String bankSessionId = "";
                if (object != null) {
                    bankSessionId = (String) object;
                }
                LOGGER.info(bean.getCuserId() + bean.getBankId() + "bankSession===" + bankSessionId);
                bean.setIpAddr(ipAddr);
                if (BillConstant.XINGYE.equals(bankid) || BillConstant.GUANGFA.equals(bankid)
                        || BillConstant.HUAQI.equals(bankid) || BillConstant.JIAOTONG.equals(bankid)
                        || BillConstant.JIANSHE.equals(bankid)) {
                    bean.setBankSessionId(bankSessionId);
                    code = 1;
                } else if (BillConstant.GUANGDA.equals(bankid)) {//光大银行需要短信验证码
                    GuangDaBank guangDaBank = new GuangDaBank();
                    code = guangDaBank.taskReceve(bean, client, cc);
                } else if (BillConstant.HUAXIA.equals(bankid)
                        || BillConstant.GONGSHANG.equals(bean.getBankId())
                        || BillConstant.ZHONGGUO.equals(bean.getBankId())) {
                    PluginBankService pluginService = new PluginBankService();
                    code = pluginService.taskReceve(bean, client, cc);
                    if (bean.getBusiErrCode() == 3) {
                        responseEntity = pluginService.extracodeResult(bean);
                        sendMsg(JSONObject.toJSONString(responseEntity));
                        return;
                    }
                }else if (BillConstant.PUFA.equals(bankid)){
                    PuFaBank pufa = new PuFaBank();
                    code = pufa.taskReceve(bean, client, cc);
                }else if (BillConstant.NONGYE.equals(bankid)){
                    NongYeBank nongye = new NongYeBank();
                    code = nongye.taskReceve(bean, client, cc);
                }else if (BillConstant.SHANGHAI.equals(bankid)){
                    ShangHaiBank shangHai = new ShangHaiBank();
                    code = shangHai.taskReceve(bean, client, cc);
                    if (bean.getBusiErrCode() == 3){//需要图片验证
                        responseEntity = shangHai.extracodeResult(bean);
                        sendMsg(JSONObject.toJSONString(responseEntity));
                        return;
                    }
                }else if (BillConstant.ZHONGXIN.equals(bankid)) {
                    ZhongXinBank zhongXin = new ZhongXinBank();
                    code = zhongXin.taskReceve(bean, client, cc);
                    if (bean.getBusiErrCode() == 3){//需要图片验证
                        responseEntity = zhongXin.extracodeResult(bean);
                        sendMsg(JSONObject.toJSONString(responseEntity));
                        return;
                    }
                }else if (BillConstant.MINSHENG.equals(bankid)){
                    MinShengBank minSheng = new MinShengBank();
                    code = minSheng.taskReceve(bean, client, cc);
                    if (bean.getBusiErrCode() == 3){//需要图片验证
                        responseEntity = minSheng.extracodeResult(bean);
                        sendMsg(JSONObject.toJSONString(responseEntity));
                        return;
                    }
                }else if (BillConstant.ZHAOSHANG.equals(bankid)) {
                    ZhaoShangBank zhaoShang = new ZhaoShangBank();
                    code = zhaoShang.taskReceve(bean, client, cc);
                }else {
                    if (!CheckUtil.isNullString(bankSessionId)) {
                        bean.setBankSessionId(bankSessionId);
                        if (BillConstant.PINGAN.equals(bean.getBankId())) {
                            code = PingAnBank.verifyMsg(bean, cc, ipAddr, LOGGER);
                            LOGGER.info(bean.getCuserId() + "PingAnBank code=" + code);
                        } else {
                            code = 1;
                        }
                    }else {
                        bean.setBusiErrCode(BillConstant.fail);
                        bean.setBusiErrDesc("验证码失效");
                        LOGGER.info(bean.getCuserId() + bean.getBankId() + "bankSession" + " 验证码失效");
                        code = 0;
                    }
                }
                if (code == 0) {
                    responseEntity.setCode(bean.getBusiErrCode() + "");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    if (bean.getBusiErrCode() == 2) {
                        responseEntity.setPhoneCode(bean.getPhoneCode());
                    }
                    if(BillConstant.ZHAOSHANG.equals(bankid) && bean.getBusiErrCode().toString().equals(BillConstant.needimg+"")){
                        String base64Img = BankHelper.getBankVerifyCode(bean,cc);
                        responseEntity.setImgcode(URLEncoder.encode(base64Img, "utf-8"));
                    }
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                LOGGER.info(bean.getCuserId() + " code=" + code);
                result = client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("bank", "createBankBillTask", bean));
                bean = JSONObject.parseObject(result, Channel.class);
                if (bean.getCode().equals("500")) {
                    LOGGER.info(bean.getCuserId() + " desc=" + bean.getDesc());
                    responseEntity.setCode(BillConstant.fail + "");
                    responseEntity.setDesc("调用服务失败");
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                } else {
                    responseEntity.setCode(bean.getBusiErrCode() + "");
                    if (bean.getBusiErrCode() == 1) {
                        responseEntity.setDesc("请求任务成功");
                        responseEntity.setTaskId(bean.getBusiErrDesc());
                    } else {
                        responseEntity.setDesc(bean.getBusiErrDesc());
                        responseEntity.setDesc("调用服务失败");
                    }
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.TASK);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                }
            } else if (BillConstant.SENDMSG.equals(method)) {
                BankHelper.send_Sms(bean, cc, client);
                if (BillConstant.GUANGDA.equals(bankid)) {
                    if (3 == bean.getBusiErrCode()) {//图片验证码错误,需要重新输入
                        GuangDaBank guangDaBank = new GuangDaBank();
                        responseEntity = guangDaBank.extracodeResult(bean);
                        sendMsg(JSONObject.toJSONString(responseEntity));
                        return;
                    }
                }
                if (!CheckUtil.isNullString(bean.getPhoneCode())) {
                    responseEntity.setPhoneCode(bean.getPhoneCode());
                }
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(BillConstant.SENDMSG);
                responseEntity.setCode(bean.getBusiErrCode() + "");
                responseEntity.setDesc(bean.getBusiErrDesc());
                sendMsg(JSONObject.toJSONString(responseEntity));
            } else if (BillConstant.CHECKMSG.equals(method)) {
                code = BankHelper.check_Sms(bean, cc, client);
                if (bean.getBusiErrCode() == 3) {//需要图片验证码
                    PluginBankService pluginBankService = new PluginBankService();
                    responseEntity = pluginBankService.extracodeResult(bean);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                if (code == 0) {
                    responseEntity.setCode(bean.getBusiErrCode() + "");
                    responseEntity.setDesc(bean.getBusiErrDesc());
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.CHECKMSG);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                    return;
                }
                String result = client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("bank", "createBankBillTask",
                        bean));
                bean = JSONObject.parseObject(result, Channel.class);
                if (bean.getCode().equals("500")) {
                    LOGGER.info(bean.getCuserId() + " desc=" + bean.getDesc());
                    responseEntity.setCode(BillConstant.fail + "");
                    responseEntity.setDesc("调用服务失败");
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.CHECKMSG);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                } else {
                    responseEntity.setCode(bean.getBusiErrCode() + "");
                    if (bean.getBusiErrCode() == 1) {
                        responseEntity.setDesc("验证请求任务成功");
                        responseEntity.setTaskId(bean.getBusiErrDesc());
                    } else {
                        responseEntity.setDesc(bean.getBusiErrDesc());
                    }
                    responseEntity.setBankId(bankid);
                    responseEntity.setMethod(BillConstant.CHECKMSG);
                    sendMsg(JSONObject.toJSONString(responseEntity));
                }
            } else {
                responseEntity.setCode(BillConstant.fail + "");
                responseEntity.setDesc("非法的操作类型");
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(BillConstant.SYS);
                sendMsg(JSONObject.toJSONString(responseEntity));
            }

        } catch (Exception e) {
            LOGGER.error("handleTextMessage 异常", e);
            responseEntity.setCode(BillConstant.fail + "");
            responseEntity.setDesc("传输格式错误");
            if (StringUtils.isEmpty(method)) {
                responseEntity.setMethod(BillConstant.SYS);
            } else {
                responseEntity.setBankId(bankid);
                responseEntity.setMethod(method);
            }
            sendMsg(JSONObject.toJSONString(responseEntity));
        }
    }

}
