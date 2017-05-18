package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseRegisterController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mario on 2016/7/20 0020.
 * 征信账户注册相关接口移植
 */
@Controller
public class RegisterController extends BaseRegisterController{
//    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);
//    @Resource(name = Constant.HSK_CCARD_INVESTIGATION)
//    private IDrpcClient client;
//
//    @Autowired
//    MemCachedClient memCachedClient;

    /**
     * 征信注册验证身份证是否有效
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/control/investigation/zxCheckIdentity.go")
    @Override
    public void investCheckIdentity(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investCheckIdentity(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 征信注册检验用户名接口
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/zxCheckRegLoginnameHasUsed.go")
    @Override
    public void investCheckAccountUsed(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investCheckAccountUsed(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 征信注册接口
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxRegistered.go")
    public void investRegister(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investRegister(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    /**
     * 征信注册获取手机动态码
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxGetAcvitaveCode.go")
    public void investGetAcvitaveCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investGetAcvitaveCode(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }
}
