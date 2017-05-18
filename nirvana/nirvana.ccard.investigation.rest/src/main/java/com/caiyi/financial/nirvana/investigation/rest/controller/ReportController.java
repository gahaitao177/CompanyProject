package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseReportController;
import com.caiyi.financial.nirvana.investigation.util.InvestigationHelper;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario on 2016/7/22 0022.
 * 征信报告相关接口移植
 */
@Controller
@RequestMapping("/credit")
public class ReportController extends BaseReportController{
//    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
//
//    @Resource(name = Constant.HSK_CCARD_INVESTIGATION)
//    private IDrpcClient client;
//
//    @Autowired
//    MemCachedClient memCachedClient;

    /**
     * 征信信用报告问题认证获取
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping("/zxGetQuestions.go")
    public void investGetQuestions(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investGetQuestions(bean,request,response);
    }

    /**
     * 征信信用报告申请
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxApplyReport.go")
    public void investApplyReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investApplyReport(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 征信信用短信快捷申请
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxSpeedApplyReport.go")
    public void investSpeedApplyReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investSpeedApplyReport(bean, request, response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 征信获取信用报告
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxViewReport.go")
    public void investViewReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investViewReport(bean, request, response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 查询征信报告记录
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/queryUserCreditreFerence.go")
    public void queryUserCreditreFerence(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        super.queryUserCreditreFerence(bean, request, response);
    }

    /**
     * 检测是否已经收到申请回执短信
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxCheckMsgRecived.go")
    public void investCheckMsgRecived(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investCheckMsgRecived(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 查询申请状态
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxCheckApplyStatus.go")
    public void investCheckApplyStatus(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investCheckApplyStatus(bean, request, response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }



}