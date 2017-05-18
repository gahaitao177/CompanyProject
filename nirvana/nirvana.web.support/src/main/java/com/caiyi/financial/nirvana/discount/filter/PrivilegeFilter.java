package com.caiyi.financial.nirvana.discount.filter;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.exception.BaseException;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.LoginUtil;
import com.caiyi.financial.nirvana.discount.utils.SpringContextUtilBro;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.http.ParameterRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by heshaohua on 2016/6/12.
 */

public class PrivilegeFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrivilegeFilter.class);
    LoginUtil loginUtil;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        ParameterRequestWrapper requestWrapper = (ParameterRequestWrapper) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        BaseBean base = new BaseBean();
        if (loginUtil == null) {
            loginUtil = SpringContextUtilBro.getBean(LoginUtil.class);
        }
        boolean flag = false;
        String code = null;
        String msg = null;
        try {
            //flag = loginUtil.getUserData(requestWrapper, response);
            flag = loginUtil.getUserDataNew(requestWrapper, response);
            if (flag) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (BaseException e) {
            code = e.getCode();
            msg = e.getMessage();
        } catch (Exception e) {
            LOGGER.error("注入cuserId异常", e);
            code = "9009";
            msg = "用户未登录";
        }
        if (!flag) {
            String requestUri = requestWrapper.getRequestURI();
            //response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            if (!CheckUtil.isNullString(requestUri) && requestUri.contains("control")) {
                JSONObject result = new JSONObject();
                result.put("code", code);
                result.put("desc", msg);
                PrintWriter writer = response.getWriter();
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setHeader("Cache-Control", "no-cache, must-revalidate");
                System.out.println(result.toJSONString());
                writer.print(result.toJSONString());
                writer.close();
            } else {
                XmlUtils.writeXml(code, msg, response);
            }
        }

//        loginUtil.getUserData(requestWrapper, response, base);
//
//        String cuserid = base.getCuserId();
//        String pwd = base.getPwd();
//        String requestUri = requestWrapper.getRequestURI();
//        if(!StringUtils.isEmpty(cuserid) && !StringUtils.isEmpty(pwd)){
//            filterChain.doFilter(requestWrapper, response);
//        }else{
//            //设置成500
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            // update by lcs start 20160818 如果新接口 返回json
//            if (!CheckUtil.isNullString(requestUri) && requestUri.contains("control")){
//                JSONObject result = new JSONObject();
//                result.put("code","9009");
//                result.put("desc","用户未登录");
//                XmlUtils.writeJson(result.toJSONString(),response);
//            } else {
//                XmlUtils.writeXml("9009", "用户未登录", response);
//            }
//        }
    }

    @Override
    public void destroy() {
    }
}