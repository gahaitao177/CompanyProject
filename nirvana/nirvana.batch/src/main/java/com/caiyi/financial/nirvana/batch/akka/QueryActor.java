package com.caiyi.financial.nirvana.batch.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.batch.service.UpdateService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/10/21.
 */
public class QueryActor extends UntypedActor {

    public Logger logger = LoggerFactory.getLogger(getClass());
    UpdateService updateService;

    public QueryActor(UpdateService updateService) {
        this.updateService = updateService;
    }
    private static String BAIFU_BAO = "https://www.baifubao.com/callback?cmd=1059&callback=phone&phone=";

    public String httpGet(String url, Map<String, String> params, String encode,
                          CloseableHttpClient httpClient, BasicCookieStore cookieStore) {
        String result = "";
        try {
            HttpGet httpGet = new HttpGet(url);

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute("http.cookie-store", cookieStore);

            HttpResponse response = httpClient.execute(httpGet, localContext);
            // 获取本地信息
            HttpEntity entity = response.getEntity();

            String statusCode = response.getStatusLine().toString();

            if ("HTTP/1.1 200 OK".equals(statusCode)) {
                result = readHttpContent(entity, encode);
            } else {

            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
        return result;

    }

    protected String readHttpContent(HttpEntity entity, String encode) throws Exception {
        StringBuffer buffer = new StringBuffer();

        InputStream in = entity.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
        logger.info("");
        String temp;
        while ((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append("\n");
        }
        in.close();
        return buffer.toString();

    }

    public static Props props(final UpdateService updateService) {
        return Props.create(new Creator<QueryActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public QueryActor create() throws Exception {
                return new QueryActor(updateService);
            }
        });
    }

    @Override
    public void onReceive(Object message)   {
        String phone = (String) message;
        String areaId = getAreaId(phone);
        if (areaId.equals("0")) {
            context().stop(self());
        } else {
            ActorRef actorRef = context().actorOf(UpdateActor.props(updateService));
            actorRef.tell(phone + ":" + areaId, self());
        }
    }

    /**
     * 获取areaid
     *
     * @param phone
     * @return
     */
    private String getAreaId(String phone) {
        String areaId = "0";
        // 城市名
        String cityName = getCityNameByPhone(phone);
        if (cityName == null) {
            return areaId;
        }
        //
        areaId = getAreaIdByCityName(cityName);
        return areaId;
    }

    /**
     * @param phone
     * @return
     */
    private String getCityNameByPhone(String phone) {
        String cityName = "";
        try {
            cityName = getPhoneAttributionBaidu(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /**
     * 根据城市名称获取areaId
     *
     * @param cityName
     * @return
     */
    private String getAreaIdByCityName(String cityName) {
        String areaId = "0";
        try {
            String sql = "select t.iareaid from tb_area t where t.iareatype=1 and t.careaname like '" + cityName + "%'";
            List<String> areaIds = updateService.query(sql);
            if (areaIds != null && areaIds.size() > 0) {
                areaId = areaIds.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return areaId;
    }

    /**
     * 百度查询归属地
     *
     * @param phone
     * @return
     * @throws Exception
     */
    private String getPhoneAttributionBaidu(String phone) throws Exception {
        Thread.sleep(250L);
        try {
            String url = "http://opendata.baidu.com/api.php?resource_id=6004&query=" + phone;
            HttpHost proxy = new HttpHost("182.254.159.166",9992);
            String result = httpGet(url, null, "gbk", getHttpClient(new BasicCookieStore(), proxy), null);
            JSONObject json = JSONObject.parseObject(result);
            System.out.println(json);
            if ("0".equals(json.getString("status"))) {
                return json.getJSONArray("data").getJSONObject(0).getString("city");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("exception for ++++++++++++++++  " + phone);
        }
        return null;
    }

//    private String getPhoneAttributionBaiFuBao(String phone) throws Exception {
//        Thread.sleep(250L);
//        try {
//            String url = "http://opendata.baidu.com/api.php?resource_id=6004&query=" + phone;
//            HttpHost proxy = new HttpHost("182.254.159.166",9992);
//            String result = httpGet(url, null, "gbk", getHttpClient(new BasicCookieStore(), proxy), null);
//            JSONObject json = JSONObject.parseObject(result);
//            System.out.println(json);
//            if ("0".equals(json.getString("status"))) {
//                return json.getJSONArray("data").getJSONObject(0).getString("city");
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            System.out.println("exception for ++++++++++++++++  " + phone);
//        }
//        return null;
//    }
    public CloseableHttpClient getHttpClient(BasicCookieStore cookieStore) {
        return getHttpClient(cookieStore, null);
    }

    public CloseableHttpClient getHttpClient(BasicCookieStore cookieStore, HttpHost proxy) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //信任任何链接
            TrustStrategy anyTrustStrategy = (x509Certificates, s) -> true;

            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();

            logger.info("");
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        return HttpClientBuilder
                .create()
                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(connManager).build();
    }

}
