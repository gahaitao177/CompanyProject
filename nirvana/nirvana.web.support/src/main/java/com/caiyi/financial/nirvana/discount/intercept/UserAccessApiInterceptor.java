package com.caiyi.financial.nirvana.discount.intercept;

import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.utils.LoginUtil;
import com.caiyi.financial.nirvana.http.ParameterRequestWrapper;
import com.danga.MemCached.MemCachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


/**
 * 登录拦截器
 * Created by heshaohua on 2016/5/18.
 */
public class UserAccessApiInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MemCachedClient memCachedClient;
    @Resource(name = Constant.HSK_USER)
    IDrpcClient client;

    @Autowired
    LoginUtil loginUtil;

    //TokenUtil tokenUtil;

    /**
     * 验证登录
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = null;

        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        } else {
            return true;
        }
        Method method = handlerMethod.getMethod();
        SetUserDataRequired setData_annotation = method.getAnnotation(SetUserDataRequired.class);

        BaseBean base = new BaseBean();
        /**if (annotation != null) {//登录注释
            setUserData(request, response, base);
            String cuserid = base.getCuserId();
            String pwd = base.getPwd();

            if(!StringUtil.isEmpty(cuserid) && !StringUtil.isEmpty(pwd)){
                return true;
            }else{
                Document dom = DocumentHelper.createDocument();
                Element resp = new DOMElement("Resp");
                dom.setRootElement(resp);
                resp.addAttribute("code", "9009");
                resp.addAttribute("desc", "用户未登录");
                XmlUtils.writeXml(dom.asXML(), response);
                return false;
            }
        }**/
        if(setData_annotation != null){
            ParameterRequestWrapper requestWrapper = (ParameterRequestWrapper) request;
            //loginUtil.getUserData(requestWrapper, response, base);
            loginUtil.getUserDataNew(requestWrapper, response, base);
            /**
            if(StringUtil.isEmpty(base.getCuserId())){
                Document dom = DocumentHelper.createDocument();
                Element resp = new DOMElement("Resp");
                dom.setRootElement(resp);
                resp.addAttribute("code", "2002");
                resp.addAttribute("desc", "参数错误");
                XmlUtils.writeXml(dom.asXML(), response);
                return false;
            }**/
        }
        return true;
    }

}
