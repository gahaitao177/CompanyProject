package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseRegisterController;
import com.caiyi.financial.nirvana.investigation.util.InvestigationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RegisterNewController extends BaseRegisterController {
    /**
     * 征信注册验证身份证是否有效
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxCheckIdentityNew.go")
    public void investCheckIdentity(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investCheckIdentity(bean, request, response);
        responseJson(bean,response);
    }

    /**
     * 征信注册检验用户名接口
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping("/control/investigation/zxCheckRegLoginnameHasUsedNew.go")
    public void investCheckAccountUsed(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investCheckAccountUsed(bean, request, response);
        responseJson(bean,response);
    }




    /**
     * 征信注册接口
     *
     * @param bean
     * @param request
     * @param response
     * @result iskeep=0   code 注册失败返回0,注册成功登录成功返回1，注册成功，登录失败并返回图片码为2，注册成功登录图片获取失败返回3（这个需要主动去登录页面登录了）
     * @result iskeep=1   之前未注册成功来登录返回4 ，（其他返回与上面相同）
     */
    @Override
    @RequestMapping("/control/investigation/zxRegisteredNew.go")
    public void investRegister(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String base64="";
        String decodeBase64="";
        Map<String,String> map=new HashMap<>();
        if ("1".equals(bean.getIskeep())){
            Object lObject=memCachedClient.get(bean.getCuserId() + "zhenxinLoginname");
            Object pObject=memCachedClient.get(bean.getCuserId() + "zhenxinPwdword");
            if (lObject==null||pObject==null){
                map.put("code", "4");
                map.put("desc", "登录页面失效，请返回登录页面登录");
            }else{
                String loginname= (String) lObject;
                String pwd= (String) pObject;

                if ("1".equals(bean.getClient())) {
                    bean.setLoginname(CaiyiEncryptIOS.encryptStr(loginname));
                    bean.setPassword(CaiyiEncryptIOS.encryptStr(pwd));
                } else {
                    bean.setLoginname(CaiyiEncrypt.encryptStr(loginname));
                    bean.setPassword(CaiyiEncrypt.encryptStr(pwd));
                }
                super.investLogin(bean, request, response);
                if (bean.getBusiErrCode()==0){
                    bean.setType("0");
                    getBase64Img(bean, request, response);
                    if (bean.getBusiErrCode()==1){
                        base64=bean.getSign();
                        decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
                        bean.setCode(decodeBase64);
                        map.put("code", "2");
                        map.put("base64img", bean.getSign());
                        map.put("decodeBase64img", bean.getCode());
                        memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
                        memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", pwd, 1000 * 60 * 50);
                    }else{
                        //加载图片失败
                        map.put("code", "3");
                        map.put("desc", bean.getBusiErrDesc());

                    }
                }else{
                    //登录成功
                    map.put("code", "1");
                    map.put("desc", bean.getBusiErrDesc());
                }

            }
        }else{
            super.investRegister(bean, request, response);
            if (bean.getBusiErrCode()==1){
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", bean.getLoginname(), 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", bean.getPassword(), 1000 * 60 * 50);
                if ("1".equals(bean.getClient())) {
                    bean.setLoginname(CaiyiEncryptIOS.encryptStr(bean.getLoginname()));
                    bean.setPassword(CaiyiEncryptIOS.encryptStr(bean.getPassword()));
                } else {
                    bean.setLoginname(CaiyiEncrypt.encryptStr(bean.getLoginname()));
                    bean.setPassword(CaiyiEncrypt.encryptStr(bean.getPassword()));
                }

                //注册成功 默认登录
                for (int i = 1; i <= res_picNums; i++) {
                    map.clear();
                    bean.setType("0");
                    getBase64Img(bean, request, response);
                    if (bean.getBusiErrCode()==1){
                        //加载图片成功，自动解析图片
                        base64=bean.getSign();
                        decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
                        bean.setCode(decodeBase64);
                        logger.info("decodeBase64" + decodeBase64);
                        super.investLogin(bean, request, response);
                        logger.info(bean.getCuserId() + " i[" + i + "] bean.getBusiErrCode()=" + bean.getBusiErrCode());
                        logger.info(bean.getCuserId()+" i["+i+"] bean.getBusiErrDesc()=" + bean.getBusiErrDesc());
                        if (bean.getBusiErrCode()==0){
                            if (i==res_picNums){
                                getBase64Img(bean, request, response);
                                if (bean.getBusiErrCode()==1){
                                    base64=bean.getSign();
                                    decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
                                    bean.setCode(decodeBase64);
                                    map.put("code", "2");
                                    map.put("base64img", bean.getSign());
                                    map.put("decodeBase64img", bean.getCode());
                                }else{
                                    //加载图片失败
                                    map.put("code", "3");
                                    map.put("desc", bean.getBusiErrDesc());
                                }
                            }
                        }else{
                            //登录成功
                            map.put("code", "1");
                            map.put("desc", bean.getBusiErrDesc());
                            break;
                        }
                    }else{
                        //加载图片失败
                        map.put("code", "3");
                        map.put("desc", bean.getBusiErrDesc());
                    }
                }
            }else{
                //注册失败返回0
                map.put("code", "0");
                map.put("desc", bean.getBusiErrDesc());
            }
        }
        XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
    }

    /**
     * 征信注册获取手机动态码
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxGetAcvitaveCodeNew.go")
    public void investGetAcvitaveCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investGetAcvitaveCode(bean, request, response);
        responseJson(bean,response);
    }
}
