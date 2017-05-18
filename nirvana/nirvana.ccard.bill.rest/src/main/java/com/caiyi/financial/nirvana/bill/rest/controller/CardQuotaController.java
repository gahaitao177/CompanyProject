package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSON;
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
 * Created by Linxingyu on 2017/2/6.
 */
@RestController
public class CardQuotaController {

    private static Logger logger = LoggerFactory.getLogger(CardQuotaController.class);

    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient drpcClient;

    /**
     * 提额首页
     *
     * @param channel
     * @return
     */
    @RequestMapping("/control/credit/raiseQuotaIndex.go")
    public BoltResult raiseQuotaIndex(Channel channel) {
        long start = System.currentTimeMillis();
        BoltResult result = new BoltResult();
        String cuserId = channel.getCuserId();
        if (CheckUtil.isNullString(cuserId)) {
            result.setCode("0");
            result.setDesc("参数错误");
            logger.info("|提额首页|raiseQuotaIndex|cuserId:" + cuserId);
            return result;
        }
        result = drpcClient.execute(new DrpcRequest("CardQuotaBolt", "raiseQuotaIndex", channel), BoltResult.class);
        logger.info("|提额首页|raiseQuotaIndex|cuserId:" + cuserId + "|result:" + JSON.toJSON(result));
        logger.info("提额首页耗时：" + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * 提额详情页
     *
     * @param channel
     * @return
     */
    @RequestMapping("/control/credit/getRaiseQuotaDetail.go")
    public BoltResult getRaiseQuotaDetail(Channel channel) {
        long start = System.currentTimeMillis();
        BoltResult result = new BoltResult();
        String billId = channel.getBillId();
        if (CheckUtil.isNullString(billId) || StringUtils.isNotInteger(billId)) {
            result.setCode("0");
            result.setDesc("参数错误");
            logger.info("|提额详情页|getRaiseQuotaDetail|billId:" + billId + "|result:" + JSON.toJSON(result));
            return result;
        }
        result = drpcClient.execute(new DrpcRequest("CardQuotaBolt", "getRaiseQuotaDetail", channel), BoltResult.class);
        logger.info("|提额详情页|getRaiseQuotaDetail|billId:" + billId + "|result:" + JSON.toJSON(result));
        logger.info("提额详情页耗时：" + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * 诊断报告页
     *
     * @param channel
     * @return
     */
    @RequestMapping("/control/credit/getRaiseQuotaReport.go")
    public BoltResult getRaiseQuotaReport(Channel channel) {
        long start = System.currentTimeMillis();
        BoltResult result = new BoltResult();
        String billId = channel.getBillId();
        if (CheckUtil.isNullString(billId) || StringUtils.isNotInteger(billId)) {
            result.setCode("0");
            result.setDesc("参数错误");
            logger.info("|诊断报告页|getRaiseQuotaReport|billId:" + billId + "|result:" + JSON.toJSON(result));
            return result;
        }
        result = drpcClient.execute(new DrpcRequest("CardQuotaBolt", "getRaiseQuotaReport", channel), BoltResult.class);
        logger.info("|诊断报告页|getRaiseQuotaReport|billId:" + billId + "|result:" + JSON.toJSON(result));
        logger.info("诊断报告页耗时：" + (System.currentTimeMillis() - start));
        return result;
    }
}
