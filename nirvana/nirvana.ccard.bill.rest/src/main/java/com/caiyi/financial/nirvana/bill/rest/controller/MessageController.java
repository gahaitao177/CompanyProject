package com.caiyi.financial.nirvana.bill.rest.controller;

import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by lizhijie on 2016/9/18.
 */
@RestController
public class MessageController {
    private static Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    /**
     * 获得消息列表
     * @return
     */
    @RequestMapping("/control/credit/getMessageList.go")
    public String getMessageList(HttpServletRequest request){
        String cuserid=request.getParameter("cuserId");
        return client.execute(new DrpcRequest("message", "getMessageList",cuserid));
    }
}
