package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Linxingyu on 2016/12/15.
 * 账务功能增强
 */
@RestController
public class BillConsumeController {

    private static Logger logger = LoggerFactory.getLogger(BillConsumeController.class);

    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient drpcClient;

    /**
     * 账单流水
     *
     * @param channel
     * @return
     */
    @RequestMapping("/control/credit/queryBillStream.go")
    public BoltResult queryBillStream(Channel channel) {
        BoltResult result = new BoltResult();
        String billId = channel.getBillId();
        logger.info("billId:" + billId);
        if (CheckUtil.isNullString(billId) || StringUtils.isNotInteger(billId)) {
            result.setCode("0");
            result.setDesc("参数错误");
            logger.info("——queryBillStream——billId:" + channel.getBillId() + "——result:" + JSONObject.toJSON(result));
            return result;
        }
        result = drpcClient.execute(new DrpcRequest("BillConsumeBolt", "queryBillStream", channel), BoltResult.class);
        logger.info("——queryBillStream——billId:" + channel.getBillId() + "——result:" + JSONObject.toJSON(result));
        return result;
    }


    /**
     * 消费类型分析
     *
     * @param channel
     * @return
     */
    @RequestMapping("/control/credit/queryConsumeType.go")
    public BoltResult queryConsumeType(Channel channel) {
        BoltResult result = new BoltResult();
        String billId = channel.getBillId();
        logger.info("billId:" + billId);
        if (CheckUtil.isNullString(billId) || StringUtils.isNotInteger(billId)) {
            result.setCode("0");
            result.setDesc("参数错误");
            logger.info("——queryConsumeType——billId:" + channel.getBillId() + "——result:" + JSONObject.toJSON(result));
            return result;
        }
        result = drpcClient.execute(new DrpcRequest("BillConsumeBolt", "queryConsumeType", channel), BoltResult.class);
        logger.info("——queryConsumeType——billId:" + channel.getBillId() + "——result:" + JSONObject.toJSON(result));
        return result;
    }

}
