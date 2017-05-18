package com.caiyi.financial.nirvana.netty.websocket.akka.impl;

import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import com.caiyi.financial.nirvana.discount.utils.LoginUtil;
import com.caiyi.financial.nirvana.netty.websocket.akka.WebSocketBaseActor;
import com.caiyi.financial.nirvana.netty.websocket.service.WebSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/10/26.
 * 需要登录的服务抽象，抽取一个公用的
 */
public abstract class NeedLoginWebSocketActor extends WebSocketBaseActor {
    public Logger logger;

    @Autowired
    LoginUtil loginUtil;

    public NeedLoginWebSocketActor(ChannelHandlerContext channelHandlerContext, WebSocketServerHandler handler,
                                   Map<String, String[]> requestParameterMap, Map<String, String> requestHeaderMap,
                                   String url) {
        super(channelHandlerContext, handler, requestParameterMap, requestHeaderMap, url);
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public boolean checkAvailable() {
        //校验可用

//        System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(1000000));// （单位：毫秒）
//        System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(1000000));
        Map<String, Object> resultMap = new HashMap<>();
//        this.session = session;
//        session.setMaxIdleTimeout(1000000);
//        Map<String, List<String>> map=session.getRequestParameterMap();
//        BaseBean base = new BaseBean();
        String token;
        String appId;
        try {
            token = getRequestParameterMap().get("accessToken")[0];
            appId = getRequestParameterMap().get("appId")[0];
        } catch (Exception e) {
            logger.info("获取accessToken and appId 失败", e);
            return false;
        }
        if (CheckUtil.isNullString(token) || CheckUtil.isNullString(appId)) {
            resultMap.put("code", BillConstant.fail);
            resultMap.put("desc", "连接错误,传输格式错误");
            resultMap.put("method", "sys");
            sendMsg(resultMap);
            return false;
        }
        String ipAddr = "";
        // update by lcs 20160821  采用新版本setuserdata
//        BankHelper.setUserData(token, appId, base, ipAddr, cc, client);

        BaseBean paramDto = new BaseBean();
        paramDto.setAccessToken(token);
        paramDto.setAppId(appId);
        try {
            TokenDto tokenDto = loginUtil.checkToken(paramDto);
            if (addOnline(tokenDto.getCuserId())) {
                resultMap.put("code", BillConstant.success);
                resultMap.put("desc", "连接成功");
                resultMap.put("method", "sys");
                sendMsg(resultMap);
                return true;
            } else {
                throw new UserException("" + BillConstant.fail, "连接失败");
            }
        } catch (UserException e) {
            logger.info("连接失败", e);
            resultMap.put("code", e.getCode());
            resultMap.put("desc", e.getMessage());
            resultMap.put("method", "sys");
            sendMsg(resultMap);
            return false;
        } catch (Exception e) {
            logger.error("连接失败", e);
            resultMap.put("code", BillConstant.fail);
            resultMap.put("desc", "连接失败");
            resultMap.put("method", "sys");
            sendMsg(resultMap);
            return false;
        }


//        BankHelper.setUserData(token, appId, ipAddr,base);
//        String cuserid=base.getCuserId();
//        if (CheckUtil.isNullString(base.getCuserId())){
//            resultMap.put("code",base.getBusiErrCode());
//            resultMap.put("desc",base.getBusiErrDesc());
//            resultMap.put("method","sys");
//            sendMsg(resultMap);
////            session.close();
//            return false;
//        }
//        if (addOnline(cuserid)){
//            resultMap.put("code",BillConstant.success);
//            resultMap.put("desc", "连接成功");
//            resultMap.put("method", "sys");
//            sendMsg(resultMap);
//            return true;
//        }else{
//            resultMap.put("code",BillConstant.fail);
//            resultMap.put("desc", "连接失败");
//            resultMap.put("method", "sys");
//            sendMsg(resultMap);
//            return false;
//        }
//        return super.checkAvailable();
    }

    protected abstract boolean addOnline(String cuserid);
}
