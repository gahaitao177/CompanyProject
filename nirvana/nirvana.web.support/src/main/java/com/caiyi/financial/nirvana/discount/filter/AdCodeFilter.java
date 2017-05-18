package com.caiyi.financial.nirvana.discount.filter;

import com.caiyi.financial.nirvana.http.ParameterRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by wenshiliang on 2016/12/30.
 */
public class AdCodeFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdCodeFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        ParameterRequestWrapper requestWrapper = (ParameterRequestWrapper) request;
        String adcode = requestWrapper.getParameter("adcode");
        if(adcode!=null && adcode.length()==6){
            String newAdcode = adcode.substring(0,4)+"00";
            LOGGER.info("将adcode {} 转换为 {}",adcode,newAdcode);
            requestWrapper.setParameter("adcode", newAdcode);
        }
        // add by lcs 2017.5.8 兼容重构后的新版本  start
        String adcodeNew = requestWrapper.getParameter("adCode");
        if(adcodeNew!=null && adcodeNew.length()==6){
            String newAdcode = adcodeNew.substring(0,4)+"00";
            LOGGER.info("将adcode {} 转换为 {}",adcodeNew,newAdcode);
            requestWrapper.setParameter("adCode", newAdcode);
        }
        // add by lcs 2017.5.8 兼容重构后的新版本 end
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {

    }
}
