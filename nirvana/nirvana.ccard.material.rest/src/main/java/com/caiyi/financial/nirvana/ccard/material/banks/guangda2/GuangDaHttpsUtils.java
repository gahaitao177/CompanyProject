package com.caiyi.financial.nirvana.ccard.material.banks.guangda2;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by wsl on 2016/3/2.
 */
public class GuangDaHttpsUtils {
    public static Logger logger = LoggerFactory.getLogger("GuangDaHttpsUtils");
    static RequestConfig requestConfig;
    static {
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(30000).build();
    }

    public static CloseableHttpClient getHttpClient(CookieStore cookieStore) {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSSLSocketFactory(ssf)
//                .setProxy(new HttpHost("127.0.0.1", 8888))
                .setDefaultCookieStore(cookieStore)
//                .setConnectionTimeToLive()
                .build();
        return httpClient;
    }

    public static void close(CloseableHttpClient client, CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static String doPost(String url, CloseableHttpClient client, Header[] requestHeaders, Map<String, String> map) {
        HttpPost post = new HttpPost(url);
        if (requestHeaders != null) {
            post.setHeaders(requestHeaders);
        }

        post.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-CN; HUAWEI Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.8.5.689 U3/0.8.0 Mobile Safari/534.30");
        post.setConfig(requestConfig);
        if (map != null && map.size() > 0) {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            post.setEntity(new StringEntity(params.substring(0, params.length() - 1), "utf-8"));
        }
        CloseableHttpResponse response = null;
        String resEntityStr = null;
        try {
            response = client.execute(post);

            HttpEntity entity = response.getEntity();
            resEntityStr = EntityUtils.toString(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(null, response);
            return resEntityStr;
        }
    }


    public static String doGet(String url, CloseableHttpClient client, Header[] requestHeaders, Map<String, String> map) {
        if (map != null && map.size() > 0) {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url += "?" + params.substring(0, params.length() - 1);
        }
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        logger.info(url);
        if (requestHeaders != null) {
            get.setHeaders(requestHeaders);
        }
        get.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-CN; HUAWEI Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.8.5.689 U3/0.8.0 Mobile Safari/534.30");


        CloseableHttpResponse response = null;
        String resEntityStr = null;
        try {
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            resEntityStr = EntityUtils.toString(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(null, response);
            return resEntityStr;
        }
    }
}
