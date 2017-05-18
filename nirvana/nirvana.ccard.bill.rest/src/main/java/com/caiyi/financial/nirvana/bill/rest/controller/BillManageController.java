package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mario on 2016/7/13 0013.
 */
@RestController
@RequestMapping("/credit")
public class BillManageController {
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    private static Logger logger = LoggerFactory.getLogger(BillManageController.class);

    /**
     * 卡管理列表
     * 旧接口移植 qUserCardinfo
     * @param bean
     * @param response
     */
    @RequestMapping("/qUserCardinfo.go")
    public void queryUserCardinfo(Channel bean, HttpServletResponse response){
        String jsonres = client.execute(new DrpcRequest("billManage", "queryUserCardinfo", bean));
        bean = JSON.parseObject(jsonres,Channel.class);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
        sb.append(bean.getBusiXml());
        sb.append("</Resp>");
        XmlUtils.writeXml(sb.toString(), response);
    }

    /**
     * 查询账单流水
     * 旧接口移植 qCreditTransaction
     * @param bean
     * @param response
     */
    @RequestMapping("/qCreditTransaction.go")
    public void queryCreditTransaction(Channel bean, HttpServletResponse response){
        String jsonres = client.execute(new DrpcRequest("billManage", "queryCreditTransaction", bean));
        bean = JSON.parseObject(jsonres,Channel.class);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
        sb.append(bean.getBusiXml());
        sb.append("</Resp>");
        XmlUtils.writeXml(sb.toString(), response);
    }

    /**
     * 删除账单（账单改为已删除状态）
     * 旧接口移植 deleteBill
     * @param bean
     * @param response
     */
    @RequestMapping("/deleteBill.go")
    public void deleteBill(Channel bean, HttpServletResponse response){
        String jsonres = client.execute(new DrpcRequest("billManage", "deleteBill", bean));
        bean = JSON.parseObject(jsonres,Channel.class);
        XmlUtils.writeXml(bean.getBusiErrCode(),bean.getBusiErrDesc(),response);
    }

    /**
     * 标记账单已还款
     *
     * @param bean
     * @param response
     */
    @RequestMapping("/changeRepaymentStatus.go")
    public void changeRepaymentStatus(Channel bean, HttpServletResponse response){
        String jsonres = client.execute(new DrpcRequest("billManage", "changeRepaymentStatus", bean));
//        bean = JSON.parseObject(jsonres,Channel.class);
        JSONObject result=JSONObject.parseObject(jsonres);
        if(result!=null&&result.get("code")!=null) {
            XmlUtils.writeXml(result.getString("code"), result.getString("desc"), response);
        }else {
            XmlUtils.writeXml("-1", "请求异常", response);
        }
    }


}
