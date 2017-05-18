package com.caiyi.financial.nirvana.bill.base;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by been on 2016/11/10.
 * 登录上下文,简化登录逻辑中函数的参数个数
 */
public class LoginContext {
    private final Logger logger = LogManager.getLogger(getClass());
    private final BasicCookieStore cookieStore;
    private final CloseableHttpClient httpClient;
    private final Map<String, String> headers;
    private final HttpClientContext httpContext;
    private final RequestConfig requestConfig;
    private String encoding = "UTF-8";

    public LoginContext(BasicCookieStore cookieStore,
                        CloseableHttpClient httpClient,
                        Map<String, String> headers,
                        HttpClientContext httpContext, RequestConfig requestConfig) {
        this.cookieStore = cookieStore;
        this.httpClient = httpClient;
        this.headers = headers;
        this.httpContext = httpContext;
        this.requestConfig = requestConfig;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpClientContext getHttpContext() {
        return httpContext;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    /**
     * 释放资源
     */
    public void close() {
        try {
            httpClient.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public String getCookieStr() {
        StringBuffer sb = new StringBuffer();
        for(Cookie cookie :cookieStore.getCookies()){
            String name = cookie.getName();
            String value = cookie.getValue();
            sb.append(name+"="+value).append(";");
        }
        return sb.toString();
    }

}
