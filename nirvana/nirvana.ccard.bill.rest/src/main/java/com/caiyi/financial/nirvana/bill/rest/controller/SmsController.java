package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.bill.bank.GuangDaBank;
import com.caiyi.financial.nirvana.bill.bank.ZhongXinBank;
import com.caiyi.financial.nirvana.bill.bank.multibank.PluginBankService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mario on 2016/7/28 0028.
 */
@RestController
@RequestMapping("/credit")
public class SmsController {
    private static Logger logger = LoggerFactory.getLogger(SmsController.class);
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    @Autowired
    MemCachedClient cc;

    /**
     * 验证短信验证码并生成任务
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/checkSms.go")
    public void checkSms(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            if ("2".equals(bean.getBankId())) {
                ZhongXinBank zhongXinBank = new ZhongXinBank();
                zhongXinBank.checkSms(bean,cc);
            } else if ("4".equals(bean.getBankId()) || "9".equals(bean.getBankId())
                    || "11".equals(bean.getBankId()) || "19".equals(bean.getBankId())
                    || "8".equals(bean.getBankId()) || "14".equals(bean.getBankId())) {//农业银行 浦发银行
                PluginBankService service = new PluginBankService();
                service.checkSms(bean,cc);
            } else if ("3".equals(bean.getBankId())) {
                GuangDaBank guangDaBank = new GuangDaBank();
                guangDaBank.checkSms(bean,cc);
            }
            if(bean.getBusiErrCode() == 0){
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            String jsonRes = client.execute(new DrpcRequest("bank", "createBankBillTask", bean));
            bean = JSON.parseObject(jsonRes, Channel.class);
        } catch (Exception e) {
            logger.error("check_Sms异常", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("验证短信失败!");
        } finally {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 获取银行验短信证码
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/sendSms.go")
    public void sendSms(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            if ("2".equals(bean.getBankId())) {
               ZhongXinBank zhongXinBank = new ZhongXinBank();
                zhongXinBank.getSms(bean,cc);
            } else if ("9".equals(bean.getBankId()) || "4".equals(bean.getBankId())
                    || "11".equals(bean.getBankId()) || "19".equals(bean.getBankId())
                    || "8".equals(bean.getBankId()) || "14".equals(bean.getBankId())) {
                logger.info(">>>>>>>>>>>>>>>>>安全控件银行登录开始发送短信验证码");
                PluginBankService service = new PluginBankService();
                int mt = service.getSms(bean,cc);
                bean.setBusiErrCode(mt);
            } else if ("3".equals(bean.getBankId())) {
                GuangDaBank service = new GuangDaBank();
                service.getSms(bean,cc);
            }
        } catch (Exception e) {
            logger.error("sendSms 发送短信验证码异常" + e.getMessage());
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
            sb.append(bean.getBusiXml());
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        }
    }



}
