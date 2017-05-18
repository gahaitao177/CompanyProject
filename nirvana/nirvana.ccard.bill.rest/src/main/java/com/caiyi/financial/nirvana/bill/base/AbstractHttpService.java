package com.caiyi.financial.nirvana.bill.base;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.rest.controller.BankController;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ResponseEntity;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.danga.MemCached.MemCachedClient;
import com.security.client.QuerySecurityInfoById;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Element;
import org.jsoup.select.Elements;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ljl on 2016/11/17.
 *http 基本流程以及公用方法
 */
public abstract class AbstractHttpService extends LoggingSupport {
    public static final Map<String, Object> loginContextMap = new ConcurrentHashMap<>();
    public final static int yzNum= SystemConfig.getInt("file.res_picNums");
    public final static String enUrl= SystemConfig.get("file.enUrl");
    public static String[] proxyips;
    static {
        String proxy_ip = SystemConfig.get("proxy_ip");
        if (!CheckUtil.isNullString(proxy_ip)) {
            proxyips = StringUtils.split(proxy_ip, ",");
        }
    }

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

        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(connManager).build();
        return httpClient;
    }


    public String httpGet(String url, LoginContext loginContext) throws Exception {
        logger.info("get url  " + url);
        String encode = loginContext.getEncoding();
        RequestBuilder builder = RequestBuilder
                .get()
                .setConfig(loginContext.getRequestConfig())
                .setCharset(StandardCharsets.UTF_8)
                .setUri(url);
        setBasicHeader(builder, loginContext);
        HttpUriRequest httpget = builder.build();
        HttpResponse response = loginContext.getHttpClient().execute(httpget, loginContext.getHttpContext());
        // 获取本地信息
        HttpEntity entity = response.getEntity();
        return httpContent(encode, response, entity);
    }

    private String httpContent(String encode, HttpResponse response,
                               HttpEntity entity) throws Exception {
        String result = "";
        int statusCode = response.getStatusLine().getStatusCode();
        logger.info("statusCode=="+statusCode);
        if (200==statusCode) {
            result = readHttpContent(entity, encode);
        } else {
            if (302==statusCode || 301 ==statusCode) {
                result = statusCode + response.getFirstHeader("Location").getValue();
            }
        }
        return result;
    }

    protected String readHttpContent(HttpEntity entity, String encode) throws Exception {
        StringBuffer buffer = new StringBuffer();
        InputStream in = entity.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
        String temp;
        while ((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append("\n");
        }
        in.close();
        return buffer.toString();
    }

    public String httpPost(String url, Map<String, String> parames, LoginContext loginContext) throws Exception {
        logger.info("post url " + url);
        String context = "";
        HttpUriRequest httpPost = null;
        String encode = loginContext.getEncoding();
        RequestBuilder builder = RequestBuilder.post(url)
                .setConfig(loginContext.getRequestConfig());
        setBasicHeader(builder, loginContext);
        if (parames != null && parames.size() > 0) {//带参数名的参数
            List<NameValuePair> paramList = buildNameValueParamList(parames);
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(paramList, encode);
            builder.setEntity(postEntity);
        }
        httpPost = builder.build();
        HttpResponse response = loginContext.getHttpClient().execute(httpPost, loginContext.getHttpContext());
        // 获取本地信息
        HttpEntity entity = response.getEntity();
        return httpContent(encode, response, entity);
    }

    private List<NameValuePair> buildNameValueParamList(Map<String, String> parameters) {
        List<NameValuePair> paramList = new ArrayList<>();
        Iterator it = parameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry parmEntry = (Map.Entry) it.next();
            paramList.add(new BasicNameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue()));
        }
        return paramList;
    }

    /**
     *json参数http请求
     * @param jsStr json数据
     * @param contentType 文本类型
     * @param loginContext http请求上下文
     */
    protected String httpRequestJson(String url, String jsStr, String contentType, LoginContext loginContext) throws Exception {
        logger.info("httpRequestJson url " + url +   " params "+jsStr);
        String encode = loginContext.getEncoding();
        HttpUriRequest httpPost;
        StringEntity s = new StringEntity(jsStr, Consts.UTF_8);
        s.setContentEncoding("UTF-8");
        s.setContentType(contentType);
        RequestBuilder builder = RequestBuilder.post(url)
                //.setUri(new URI(url))
                .setConfig(loginContext.getRequestConfig())
                .setEntity(s);
        setBasicHeader(builder, loginContext);
        httpPost = builder.build();
        HttpResponse response = loginContext.getHttpClient().execute(httpPost, loginContext.getHttpContext());
        // 打印响应信息
        HttpEntity entity = response.getEntity();
        return httpContent(encode, response, entity);
    }

    /**
     * 获取代理对象
     * @param isProxy 是否仅用代理
     */
    protected HttpHost getHttpProxy(boolean isProxy,List<String> notProxys) {
        HttpHost proxy = null;
        try {
            if (proxyips != null && proxyips.length > 0) {
                List<String> proxyList = new ArrayList<>();
                proxyList.addAll(Arrays.asList(proxyips));
                if (notProxys!=null && notProxys.size()>0){
                    proxyList.removeAll(notProxys);
                }
                if (!isProxy){
                    proxyList.add("default");
                }
                int x = new Random().nextInt(proxyList.size());
                String ipStr = proxyList.get(x);
                logger.info("proxy ip=="+ipStr);
                if (ipStr.equals("default")){
                    return proxy;
                }else{
                    String[] proxyinfo = StringUtils.split(ipStr, ":");
                    if (proxyinfo.length == 2) {
                        String ip = proxyinfo[0];
                        int port = Integer.parseInt(proxyinfo[1]);
                        proxy = new HttpHost(ip, port, "http");
                    }
                }
            }
        } catch (Exception e) {
            logger.error(getClass().getSimpleName() + " ----- 异常", e);
        }
        return proxy;
    }

    public String getYzm(String yzmUrl, String cuserid,
                         LoginContext loginContext) throws Exception {
        try {
            RequestBuilder builder = RequestBuilder
                    .get(yzmUrl)
                    .setConfig(loginContext.getRequestConfig());
            setBasicHeader(builder, loginContext);
            HttpUriRequest request = builder.build();
            logger.info("yzm uri:" + yzmUrl);
            HttpResponse response = loginContext.getHttpClient().execute(request, loginContext.getHttpContext());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.info("获取验证码失败");
                return null;
            } else {
                return saveYZM(cuserid, response);
            }
        } catch (Exception e) {
            logger.error(getClass().getSimpleName() + " ----- 异常", e);
            return null;
        }
    }

    /**
     * 保存图片
     */
    protected String saveImageFile(String cuserid, HttpResponse response) throws Exception {
        String filename = cuserid + ".png";
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getYzmLocation() + filename));
            entity.writeTo(bos);
            logger.info("将验证码保存到: " + getYzmLocation() + filename);
            bos.close();
        }
        return filename;
    }


    /**
     * 确定验证码的存放位置
     */
    private String getYzmLocation() {
        String location = "/Users/been/repo/caiyi/storm/bank/yzm/";
        if (OSUtil.isLinux()) {
            location = "/opt/image/";
        } else if (OSUtil.isWindows()) {
            location = "c://yzm/";
        }
        return location;
    }

    /**
     * 将yzm 保存到磁盘
     */
    public String saveYzm(String resourcesDataPath, String imageName,
                          BufferedImage localBufferedImage) throws Exception {
        File dir = new File(resourcesDataPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        logger.info("filename="+resourcesDataPath+imageName);
        File file = new File(resourcesDataPath, imageName);
        //将验证码图片存入文件系统
        boolean result = ImageIO.write(localBufferedImage, "gif", file);
        if (result) {
            return file.getName();
        } else {
            return null;
        }
    }

    /**
     * 本地测试生成图片文件
     * 将图片信息采用base64加密
     */
    private String saveYZM(String cuserid, HttpResponse response) throws Exception {
       /* if (!OSUtil.isLinux()) {
            //非线上环境讲验证码存入磁盘,用于测试,返回验证码位置
            return saveImageFile(cuserid, response);
        }*/
        HttpEntity entity = response.getEntity();
        byte[] bytes = EntityUtils.toByteArray(entity);
        return new BASE64Encoder().encode(bytes);
    }

    /**
     * 将base64字节码的图片数据保存到本地
     */
    protected void saveYzm(String cuserid,String base64Code){
        String filename = cuserid + ".png";
        try {
            byte[] randBytes = new BASE64Decoder()
                    .decodeBuffer(base64Code);
            ByteArrayInputStream bin = new ByteArrayInputStream(randBytes);
            BufferedImage image = ImageIO.read(bin);
            ImageIO.write(image, "png", new File(getYzmLocation()+filename));
            logger.info("将验证码保存到: " + getYzmLocation() + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int login(Channel bean,MemCachedClient cc){
        return 1;
    }
    public int loginAfter(Channel bean,MemCachedClient cc){
        return 1;
    }
    public String setYzm(Channel bean, MemCachedClient cc) {
        return "";
    }
    public int getSms(Channel bean, MemCachedClient cc){
        return 1;
    }
    public int checkSms(Channel bean,MemCachedClient cc){
        return 1;
    }

    /**
     * 读取控制台
     */
    public String readConsole() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    private void setBasicHeader(RequestBuilder builder, LoginContext loginContext) {
        Map<String, String> allHeader = loginContext.getHeaders();
        for (String key : allHeader.keySet()) {
            builder.addHeader(key, allHeader.get(key));
        }
    }

    protected Map<String, String> getBasicHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Connection", "keep-alive");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/47.0.2526.106 Safari/537.36");
        return headers;
    }

    /**
     * 解密参数
     * @param bean 参数
     * @return 执行结果
     */
    protected int dencrypt_data(Channel bean){
        if ("0".equals(bean.getType())) {
            return dencryptStr(bean);
        } else if ("1".equals(bean.getType()) || "2".equals(bean.getType()) || "3".equals(bean.getType())) {
            return querySecurityInfo(bean);
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("无效的操作类型");
            return 0;
        }
    }

    private int dencryptStr(Channel bean){
        if (StringUtils.isEmpty(bean.getIdCardNo()) || StringUtils.isEmpty(bean.getBankPwd())) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("登陆账号和密码不能为空！");
            return 0;
        }
        if ("1".equals(bean.getClient())) {
            bean.setDencryIdcard(CaiyiEncryptIOS.dencryptStr(bean
                    .getIdCardNo()));
            bean.setDencryBankPwd(CaiyiEncryptIOS.dencryptStr(bean
                    .getBankPwd()));
        } else {
            bean.setDencryIdcard(CaiyiEncrypt.dencryptStr(bean.getIdCardNo()));
            bean.setDencryBankPwd(CaiyiEncrypt.dencryptStr(bean
                    .getBankPwd()));
        }
        return 1;
    }

    private int querySecurityInfo(Channel bean){
        if (StringUtils.isEmpty(bean.getIskeep())
                || StringUtils.isEmpty(bean.getCreditId())) {
            logger.info("iskeep or creditid is null;iskeep=="+bean.getIskeep()+",creditid=="+bean.getCreditId());
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("非法操作,缺少必要参数");
            return 0;
        }
        if ("0".equals(bean.getIskeep())) {
            // 已保存密码
            QuerySecurityInfoById ssi = new QuerySecurityInfoById();
            ssi.setUid(bean.getCuserId());
            ssi.setCreditId(bean.getCreditId());
            try {
                ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId()
                        + BankController.MD5_KEY));
            } catch (Exception e) {
                e.printStackTrace();
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("解析参数失败");
                return 0;
            }
            ssi.setServiceID("2000");
            String s = ssi.call(30);
            logger.info("s=" + s);
            if (StringUtils.isEmpty(s)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("无效的银行卡信息");
                return 0;
            }
            org.dom4j.Document doc = XmlTool.stringToXml(s);
            Element ele = doc.getRootElement();
            String errcode = ele.attributeValue("errcode");
            if ("0".equalsIgnoreCase(errcode)) {
                bean.setDencryIdcard(XmlTool.getElementValue("accountName",ele));
                bean.setDencryBankPwd(XmlTool.getElementValue("accountPwd",ele));
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("无效的银行卡信息");
                return 0;
            }
        } else {
            return dencryptStr(bean);
        }
        return 1;
    }

    /**
     * 封装发送图片结果
     */
    public ResponseEntity extracodeResult(Channel bean){
        String base64Img = bean.getBankRand();
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setFlag("1");
        responseEntity.setIsFrist("1");
        responseEntity.setBankId(bean.getBankId());
        responseEntity.setMethod(BillConstant.EXTRACODE);
        responseEntity.setDesc(bean.getBusiErrDesc());
        if (StringUtils.isEmpty(base64Img)){
            responseEntity.setCode(BillConstant.fail+"");
            responseEntity.setImgcode("");
        }else{
            responseEntity.setCode(BillConstant.success+"");
            try {
                responseEntity.setImgcode(URLEncoder.encode(base64Img, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                logger.error("cuserId="+bean.getCuserId()+";"+
                        getClass().getSimpleName() + " ----extracodeResult 异常", e);
                responseEntity.setCode(BillConstant.fail+"");
            }
        }
        return responseEntity;
    }

    /**
     * 封装发送短信结果
     */
    public ResponseEntity sendSmsResult(Channel bean){
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setBankId(bean.getBankId());
        responseEntity.setMethod(BillConstant.SENDMSG);
        responseEntity.setCode(BillConstant.success+"");
        responseEntity.setDesc(bean.getBusiErrDesc());
        if (!CheckUtil.isNullString(bean.getPhoneCode())){
            responseEntity.setPhoneCode(bean.getPhoneNum());
        }
        return responseEntity;
    }

    protected LoginContext createLoginContext(BasicCookieStore cookieStore) {
        return createLoginContext(cookieStore, null);
    }

    protected LoginContext createLoginContext(BasicCookieStore cookieStore, HttpHost proxy) {
        HttpClientContext httpContext = HttpClientContext.create();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .build();
        CloseableHttpClient httpClient = getHttpClient(cookieStore,proxy);
        return new LoginContext(cookieStore, httpClient, getBasicHeader(), httpContext, requestConfig);
    }

    protected LoginContext createContextProxy(BasicCookieStore cookieStore, HttpHost proxy){
        HttpClientContext httpContext = HttpClientContext.create();
        httpContext.setAttribute("http.cookie-store", cookieStore);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(30000)
                .build();
        CloseableHttpClient httpClient;
        if (proxy==null){
            httpClient = HttpClients.createDefault();
        }else {
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        }
        return new LoginContext(cookieStore, httpClient, getBasicHeader(), httpContext, requestConfig);
    }

    /***
     * 消减页面多余元素
     * @param htmlStr 页面
     * @return List<String>
     */
    protected String lessHtml(String htmlStr) {
        try {
            //去样式，去标签，去空格，去&nbsp，去\t，去u12288, 去换行符
            String shortStr = htmlStr.replaceAll("<style[\\s\\S\\W]*?style>", "@@@").trim();
            shortStr = shortStr.replaceAll("<[\\s\\S\\W]*?>", "@@@").trim();
            shortStr = shortStr.replaceAll(" ", "").trim();
            shortStr = shortStr.replaceAll(String.valueOf((char) 12288), "").trim();
            shortStr = shortStr.replaceAll("&nbsp;", "").trim();
            shortStr = shortStr.replaceAll("\t", "").trim();
            shortStr = shortStr.replaceAll("[\r\n]", "").trim();
            shortStr = shortStr.replaceAll("[\\\\r\\\\n\\\\t]", "").trim();
            shortStr = shortStr.replaceAll("([@]{3})+", "@@@").trim();
            return shortStr;
        } catch (Exception err) {
            return null;
        }
    }


    /***
     * 将html解析为字符串链表
     * @param htmlStr 页面
     * @return List<String>
     */
    protected List<String> parseToList(String htmlStr) {
        try {
            String shortStr = lessHtml(htmlStr);
            //返回链表
            List<String> strList = new ArrayList<>();
            strList.addAll(Arrays.asList(shortStr.split("[@]+")));
            return strList;
        } catch (Exception err) {
            return null;
        }
    }

    /**
     * 带控件登录银行,获取控件加密结果
     * @param timespan 登陆页面的时间戳（农业，上海，浦发，民生必须）
     * @param pubkey 登陆页面的加密公钥，变动（华夏必须，其他银行为固定值）
     */
    protected boolean hackPassword(Channel bean,String timespan,String pubkey) throws Exception{
        String bankFlag = bean.getBankId().length() >= 2 ? "" + bean.getBankId() : "0" + bean.getBankId();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String servip = SystemConfig.get("pluginPassword.servip");
        String port = SystemConfig.get("pluginPassword.servport");
        String action = bean.getAction();
        if (StringUtils.isEmpty(action)){//默认值为hp,密码加密
            action = "hp";
        }
        String bankPwd =  URLEncoder.encode(bean.getDencryBankPwd().replaceAll("\\s*",""),"UTF-8");
        String bankRand = URLEncoder.encode(bean.getBankRand().replaceAll("\\s*",""),"UTF-8");
        String url = "http://"+servip+":"+port+"/windows/bank?method=hack&action="+action+"&taskid="+uuid+
                "&bankcode="+bankFlag+"&password="+bankPwd+"&captcha="+bankRand+"&timespan="+timespan+"&pubkey="+pubkey;
        LoginContext loginContext = createLoginContext(new BasicCookieStore());
        String result = httpGet(url,loginContext);
        logger.info("cuserId=="+bean.getCuserId()+"请求结果>>>>" + result);
        JSONObject json = JSONObject.parseObject(result);
        String code = json.get("Code").toString();
        if (code.equals("1")) {//请求成功
            String taskCode = json.get("TaskCode").toString();
            if (taskCode.equals("h")) {//加密成功
                String passwordHackStr = json.get("PasswordHackString").toString();
                bean.setPasswordHackStr(passwordHackStr);
                if (json.containsKey("MachineCode")){
                    bean.setMachineCode(json.get("MachineCode").toString());
                }
                if (json.containsKey("MachineInfo")){
                    bean.setMachineInfo(json.get("MachineInfo").toString());
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 更新任务执行状态，实时记录任务执行情况
     * @param bean bean对象
     * @param client drpc对象
     * @return 执行结果 0:失败 1:成功
     */
    public int changeCode(Channel bean,IDrpcClient client) {
        String result = client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("bank", "billTaskChangeCode", bean));
        bean= JSONObject.parseObject(result, Channel.class);
        if (bean.getBusiErrCode() == 1) {
            logger.info("cuserId=="+bean.getCuserId()+"任务状态更新成功");
        }else{
            logger.info("cuserId=="+bean.getCuserId()+"任务状态更新失败");
        }
        return 1;
    }

    /**
     * 设置表单参数
     */
    protected Map<String,String> setFormParams(org.jsoup.nodes.Element form){
        Map<String, String> params = new HashMap<>();
        Elements inputs = form.getElementsByTag("input");
        for (org.jsoup.nodes.Element input : inputs) {
            String name = input.attr("name");
            if (!StringUtils.isEmpty(name)){
                String value = input.attr("value");
                params.put(name, value);
            }
        }
        return params;
    }

    /**
     * 网银导入账单执行task接口(有时需要短信验证)
     * @param bean bean对象
     * @param client drpc对象
     * @return 执行结果 0:失败 1:成功
     */
    public int taskReceve(Channel bean, IDrpcClient client, MemCachedClient cc){
        client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("bank", "billTaskConsume", bean));
        int ret = dencrypt_data(bean);//参数解密
        if (ret==0){
            return ret;
        }
        int code = login(bean,cc);
        if (2 == bean.getBusiErrCode()) {//需要短信验证
            bean.setCode("3");
        } else if (3==bean.getBusiErrCode()){//需要图片验证码
            bean.setCode("2");
        } else if (1 == bean.getBusiErrCode()) {//登录成功
            bean.setCode("1");
        } else{//登录失败
            bean.setCode("0");
        }
        changeCode(bean,client);
        return code;
    }

    /**
     * 验证码图片识别
     * @param imgBase64 base64格式图片数据
     * @param ibankid 银行id
     * @param imgType 图片类型
     * @return
     */
    protected String distinguishCode(Channel bean,String imgBase64,String ibankid,String imgType){
        String url;
        try {
            url = enUrl + "?captcha=" + URLEncoder.encode(imgBase64, "utf-8") + "&bankid="+ibankid+"&imgtype="+imgType;
        } catch (UnsupportedEncodingException e) {
            logger.info(e.getMessage(), e);
            return "";
        }
        LoginContext loginContext = createLoginContext(new BasicCookieStore());
        String img_result;
        try {
            img_result = httpGet(url, loginContext);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "";
        }
        logger.info(bean.getCuserId() + " 自动识别验证码 " + img_result);
        if (StringUtils.isEmpty(img_result)) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("自动识别验证码失败！");
            return "";
        }
        JSONObject json = JSONObject.parseObject(img_result);
        String code = String.valueOf(json.get("code"));
        if (!"0".equals(code)) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("自动识别验证码失败！");
            return "";
        }
        String bankrand = json.getString("text");
        logger.info("cuserId="+bean.getCuserId()+" 自动识别验证码成功,bankrand="+bankrand);
        return bankrand;
    }
}
