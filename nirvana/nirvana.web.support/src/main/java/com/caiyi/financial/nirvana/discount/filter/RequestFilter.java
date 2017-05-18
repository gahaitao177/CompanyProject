package com.caiyi.financial.nirvana.discount.filter;

import com.caiyi.financial.nirvana.http.ParameterRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuweiguo on 2016/8/22.
 */
public class RequestFilter implements Filter{
    private final static Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(request);
//        String encoding = requestWrapper.getCharacterEncoding();
//        LOGGER.info(encoding);
//        for(Map.Entry<String,String[]> entry : ((Map<String,String[]>)requestWrapper.getParameterMap()).entrySet()){
//            LOGGER.info(entry.getKey()+"-----"+entry.getValue()[0]);
//        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        filterChain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {

    }
}
