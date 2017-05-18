package com.caiyi.financial.nirvana.ccard.material.banks.zhada;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.ccard.material.banks.zhongXin.HttpRequest;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario on 2016/6/13 0013.
 */
public class ZhadaUtils {
    private static Logger logger = LoggerFactory.getLogger("ZhadaUtils");
    //HttpRequest
    private HttpRequest httpRequest = new HttpRequest();
    //合作链接取Cookie
    private String URL_ROOT = "http://www.omsys.com.cn/sccredit/index.php?aid=c2NjcmVkaXRfNDQ4NF8yODBfeWVz";
    private String URL_SUBMIT = "https://online.forms.standardchartered.com/nfs-ofp/ofpservice.htm";
    /**
     * 渣打银行各字典表
     */
    /*月收入*/
    private Map<Integer, String> mapIncome;
    /*城市*/
    private Map<String, String> mapCity;

    public ZhadaUtils() {
        this.mapIncome = new HashMap<>();
        this.mapIncome.put(0, "< CNY 6,000");
        this.mapIncome.put(1, "CNY 6,000—CNY 9,999");
        this.mapIncome.put(2, "CNY 10,000-CNY 14,999");

        this.mapCity = new HashMap<>();
        this.mapCity.put("310100", "上海");
        this.mapCity.put("110100", "北京");
        this.mapCity.put("440300", "深圳");
        this.mapCity.put("440100", "广州");
        this.mapCity.put("510100", "成都");
    }

    /**
     * 默认值
     */
    private String CAMP_ID = "ZJ4484";
    private String FORM_ID = "CNR710";

    /**
     * 渣打银行办卡,页面跳转流程
     *
     * 1.访问 http://www.omsys.com.cn/sccredit/index.php?aid=c2NjcmVkaXRfNDQ4NF8yODBfeWVz
     * 2.提交信息，下一步访问：http://www.omsys.com.cn/sccredit/wapsave1/NDQ4NF8yODBfd2FwMw==?city=0&shenfen=0&shouru=1&ifhas=0
     * 3.第2步会自动跳转到：https://www.sc.com/cn/campaign/scb-cc/?pid=ZJ4484#iframe-container4484
     * 4.在第3步的页面中提交表单：https://online.forms.standardchartered.com/nfs-ofp/ofpservice.htm
     * 5.第4步提交中FormData包含第3步的pid：ZJ4484，全程无cookie，现直接进行第4步提交表单.
     */

    /**
     * 提交申请
     * 直接进行第4步提交表单
     */
    public int applayCard(MaterialBean bean) {
        //判断月收入，最低一档不符合，默认第二档
        logger.info("渣打银行申请办卡,全部Bean数据" + JSON.toJSONString(bean));
        try {
            Integer incomeLevel = 1;
            Integer yearIncome = Integer.parseInt(bean.getModel().getIannualsalary());
            if (yearIncome > 12) {
                incomeLevel = 2;
            }
            //校验城市,公司地址，家庭住址有其一即可
            String city = "";
            // 公司地址
            String companycity = bean.getModel().getIcompany_cid();

            //  家庭地址
            String homecity = bean.getModel().getIhome_cid();

            String ipostaddr = bean.getModel().getIpostaddress();

            logger.info("companycity:" + companycity + ",homecity:"+ homecity + ",ipostaddr:" + ipostaddr);
            if (mapCity.containsKey(companycity) && "1".equals(ipostaddr)) {
                city = mapCity.get(companycity);
            } else if (mapCity.containsKey(homecity) && "2".equals(ipostaddr)) {
                city = mapCity.get(homecity);
            } else {
                //不满足条件
                logger.info("所在地区不满足条件,仅限上海、北京、深圳、广州、成都。");
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("很抱歉您所在的地区不符合申请条件,仅限上海、北京、深圳、广州、成都。");
                return 0;
            }

            ZhadaData data = new ZhadaData(
                    bean.getModel().getCname(),
                    bean.getModel().getCphone(),
                    this.mapIncome.get(incomeLevel),
                    CAMP_ID,
                    FORM_ID,
                    city);

            String result = httpRequest.sendPost(URL_SUBMIT, data.toParams(), "");
            logger.info("渣打银行提交结果:"+result);
            if (result.contains("Succesfully")) {//提交成功
                //获取预约号
                String dateNum = result.substring(result.indexOf("Succesfully"), result.indexOf("Succesfully") + 26).substring(14);
                logger.info("提交成功，预约号:" + dateNum);
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("感谢您的预约申请！我们会尽快与您联系！您的预约号码为:" + dateNum);
                return 1;
            } else {//提交失败
                logger.info("提交失败.");
                ErrorRequestBean errBean = new ErrorRequestBean(bean, "很抱歉,您不符合渣打银行的申请条件,感谢您的支持.");
                errBean.setCerrordesc("很抱歉,您不符合渣打银行的申请条件,感谢您的支持.");
                errBean.setIerrortype(2);
                errBean.setCphone(bean.getModel().getCphone());
                BankApplyListener.sendError(BankEnum.zhada, BankApplyStepEnum.submit_apply, errBean);

                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("很抱歉,您不符合渣打银行的申请条件,感谢您的支持.");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("网络连接异常，请稍后再试.");
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "异常:" + e.getMessage());
            errBean.setCerrordesc("提交数据异常.");
            errBean.setIerrortype(0);
            errBean.setCphone(bean.getModel().getCphone());
            BankApplyListener.sendError(BankEnum.zhada, BankApplyStepEnum.submit_apply, errBean);
            return 0;
        }
    }
}
