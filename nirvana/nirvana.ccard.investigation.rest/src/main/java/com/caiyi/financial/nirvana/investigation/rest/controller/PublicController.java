package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mario on 2016/9/8 0008.
 * 征信公共接口
 */
@RestController
@RequestMapping("/credit")
public class PublicController {

    @Autowired
    MemCachedClient memCachedClient;
    @Autowired
    LoginController loginController;
    @Autowired
    RegisterController registerController;
    @Autowired
    ResetPwdController resetPwdController;
    @Autowired
    ReportController reportController;

    /**
     * 征信公共接口
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxPublic.go")
    public void investPublic(Channel bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*
        校验安全sign
		 */
        if (bean.getSign() == null || !bean.getSign().equals("8A5880E44DB942F291F03D6A47511889")) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("没有权限.");
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }
        if (bean.getMethod() == null || bean.getMethod().equals("")) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("错误的请求.");
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }

        switch (bean.getMethod()) {
            //登录相关
            case "zxVerifyCode":
                loginController.getVerifyCode(bean, request, response);
                break;
            case "zxLogin":
                loginController.investLogin(bean, request, response);
                break;
            case "zxLoginOut":
                loginController.investLoginOut(bean, request, response);
                break;
            //注册相关
            case "zxCheckIdentity":
                registerController.investCheckIdentity(bean, request, response);
                break;
            case "zxCheckRegLoginnameHasUsed":
                registerController.investCheckAccountUsed(bean, request, response);
                break;
            case "zxRegistered":
                registerController.investRegister(bean, request, response);
                break;
            case "zxGetAcvitaveCode":
                registerController.investGetAcvitaveCode(bean, request, response);
                break;
            //账户操作，忘记、重置密码
            case "zxGoToResetPwd":
                resetPwdController.investGoToResetPwd(bean, request, response);
                break;
            case "zxGetResetAcvitaveCode":
                resetPwdController.investGetResetActivateCode(bean, request, response);
                break;
            case "zxGetResetQuestions":
                resetPwdController.investGetResetQuestions(bean, request, response);
                break;
            case "zxApplyResetPwd":
                resetPwdController.investApplyResetPwd(bean, request, response);
                break;
            //报告相关
            case "zxGetQuestions":
                reportController.investGetQuestions(bean, request, response);
                break;
            case "zxApplyReport":
                reportController.investApplyReport(bean, request, response);
                break;
            case "zxSpeedApplyReport":
                reportController.investSpeedApplyReport(bean, request, response);
                break;
            case "zxViewReport":
                reportController.investViewReport(bean, request, response);
                break;
            case "queryUserCreditreFerence":
                reportController.queryUserCreditreFerence(bean, request, response);
                break;
            case "zxCheckMsgRecived":
                reportController.investCheckMsgRecived(bean, request, response);
                break;
            case "zxCheckApplyStatus":
                reportController.investCheckApplyStatus(bean, request, response);
                break;
            default:
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("错误的方法.");
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                break;
        }
    }
}
