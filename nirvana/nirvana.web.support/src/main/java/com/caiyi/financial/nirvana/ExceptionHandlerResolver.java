package com.caiyi.financial.nirvana;

import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.exception.BaseException;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wenshiliang on 2016/7/5.
 */
public class ExceptionHandlerResolver extends SimpleMappingExceptionResolver {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerResolver.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        return super.doResolveException(request, response, handler, ex);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ResponseBody body = handlerMethod.getMethodAnnotation(ResponseBody.class);
//        = handlerMethod.getMethodAnnotation(RestController.class);
//        handlerMethod.get
        RestController restController = handlerMethod.getBeanType().getAnnotation(RestController.class);
        // 判断有没有@ResponseBody的注解没有的话调用父方法
        if (body == null && restController == null) {
            return super.doResolveException(request, response, handlerMethod, ex);
        }

        ModelAndView mv = new ModelAndView();
        //设置成500
        // update by lcs  不能设置为500
//        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        // 设置ContentType

        try {
            LOGGER.error("异常处理器拦截异常",ex);
            PrintWriter writer = response.getWriter();
            BoltResult result;
            if(ex instanceof BaseException){
                result = ((BaseException)ex).getBoltResult();
            }else{
                result = new BoltResult(BoltResult.ERROR,"系统出错");
            }
            if(request.getRequestURI().indexOf("control")>=0){
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "no-cache, must-revalidate");
                writer.print(result.toJsonString());
                writer.close();
            }else{
                //兼容老接口, 返回xml异常说明
                XmlUtils.writeXml(result.getCode(),result.getDesc(),response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mv;
    }
}
