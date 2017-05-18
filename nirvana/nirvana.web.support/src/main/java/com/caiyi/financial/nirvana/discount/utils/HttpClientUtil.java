package com.caiyi.financial.nirvana.discount.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class HttpClientUtil {
	// http代理参数
	private static final boolean isproxy = false; // 设置代理开关


	//private static final String proxy_IP = "127.0.0.1";
	//private static final int proxy_POST = 8888;
	private static final String proxy_IP = "211.144.120.36";
	private static final int proxy_POST = 5094;

	private static final String proxy_USER = "test";
	private static final String proxy_PASSWD = "test";
	private static final String proxy_DOMAIN = "domain";
	// http连接参数
	private static final int proxy_CONNECTTIMEOUT = 20000; // 连接超时毫秒
	private static final int proxy_SOTIMEOUT = 30000; // 读取超时毫秒
	private static final boolean proxy_SO_REUSEADDR = true; // 是否重用地址
	private static final boolean proxy_TCP_NODELAY = true; // 是否禁用nodelay算法
	private static final boolean proxy_STALE_CONNECTION_CHECK = false; // 是否开启陈旧的连接检查

	/**
	 * 执行HttpClient GET请求
	 *
	 * @param url
	 * @return
	 */
	public static String callHttpGet(String url, Map<String, String> mapParams) {
		if(!url.contains("?")){
			url+="?";
		}
		if(mapParams!=null){
			for (Entry<String, String> k : mapParams.entrySet()) {
				url+=(k.getKey()+"="+k.getValue()+"&");
			}
		}
		HttpGet httpGet = new HttpGet(url);
		return httpExcute(httpGet);
	}

	public static String callHttpGet(String url, Map<String, String> mapParams, Map<String, String> header) throws Exception{
		if(!url.contains("?")){
			url+="?";
		}
		if(mapParams!=null){
			for (Entry<String, String> k : mapParams.entrySet()) {
				url+=(k.getKey()+"="+k.getValue()+"&");
			}
		}

		HttpGet httpGet = new HttpGet(url);
		if(header!=null){
			for (Entry<String, String> k : header.entrySet()) {
				httpGet.setHeader(k.getKey(), k.getValue());
			}
		}

		return httpExcute(httpGet);
	}


	public static String callHttpGet(String url, Map<String, String> mapParams, Map<String, String> header, CookieStore cs) throws Exception{
		if(!url.contains("?")){
			url+="?";
		}
		if(mapParams!=null){
			for (Entry<String, String> k : mapParams.entrySet()) {
				url+=(k.getKey()+"="+k.getValue()+"&");
			}
		}
		String cookieStr="";
		List<Cookie> list = cs.getCookies();
		for (Cookie cookie : list) {
			cookieStr += cookie.getName() + "=" + cookie.getValue() + ";";
		}
		HttpGet httpGet = new HttpGet(url);
		if(header!=null){
			for (Entry<String, String> k : header.entrySet()) {
				httpGet.setHeader(k.getKey(), k.getValue());
			}
		}
		if(cookieStr!=""){
			httpGet.setHeader("Cookie",cookieStr);
		}
		HttpClient httpClient = getHttpClient();
		return call(httpClient, httpGet, cs);
		//return httpExcute(httpGet,cs);
	}


	/**
	 * 执行HttpClient POST请求
	 * @param url
	 * @param mapParams
	 *            map参数
	 * @return
	 * @throws Exception
	 */
	public static String callHttpPost_Map(String url, Map<String, String> mapParams) {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> data = null;
		UrlEncodedFormEntity formEntity = null;
		if (mapParams != null) {
			data = new ArrayList<NameValuePair>(mapParams.size());
			for (Entry<String, String> entry : mapParams.entrySet()) {
				data.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 对参数编码
			try {
				formEntity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				System.out.println("HttpClient：编码POST参数失败,原因["+ e.getLocalizedMessage() + "]");
				return null;
			}
			post.setEntity(formEntity);
		}
		return httpExcute(post);
	}

	/**
	 * 执行HttpClient POST请求 设置Cookie
	 * @param url
	 * @param mapParams
	 *            map参数
	 * @return
	 * @throws Exception
	 */
	public static String callHttpPost_Map(String url, Map<String, String> mapParams,CookieStore cs) throws Exception {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> data = null;
		UrlEncodedFormEntity formEntity = null;
		String cookieStr = "";
		List<Cookie> list = cs.getCookies();
		for (Cookie cookie : list) {
			cookieStr += cookie.getName() + "=" + cookie.getValue() + ";";
		}
		if (mapParams != null) {
			data = new ArrayList<NameValuePair>(mapParams.size());
			for (Entry<String, String> entry : mapParams.entrySet()) {
				data.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 对参数编码
			try {
				formEntity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				System.out.println("HttpClient：编码POST参数失败,原因["+ e.getLocalizedMessage() + "]");
				return null;
			}
			post.setEntity(formEntity);
		}
		if(cookieStr!=null && cookieStr!=""){
			post.setHeader("Cookie",cookieStr);
		}
		HttpClient httpClient = getHttpClient();
		httpClient.getParams();
		return call(httpClient, post, cs);
		//return httpExcute(post,cs);
	}

	public static String callHttpPost_Map(String url, Map<String, String> mapParams,
										  Map<String, String> header, CookieStore cs) throws Exception{
		// TODO Auto-generated method stub
		HttpPost post = new HttpPost(url);
		List<NameValuePair> data = null;
		UrlEncodedFormEntity formEntity = null;
		String cookieStr = "";
		List<Cookie> list = cs.getCookies();
		for (Cookie cookie : list) {
			cookieStr += cookie.getName() + "=" + cookie.getValue() + ";";
		}
		if (mapParams != null) {
			data = new ArrayList<NameValuePair>(mapParams.size());
			for (Entry<String, String> entry : mapParams.entrySet()) {
				data.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 对参数编码
			try {
				formEntity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				System.out.println("HttpClient：编码POST参数失败,原因["+ e.getLocalizedMessage() + "]");
				return null;
			}
			post.setEntity(formEntity);
		}
		if(header!=null){
			for (Entry<String, String> k : header.entrySet()) {
				post.setHeader(k.getKey(), k.getValue());
			}
		}
		if(cookieStr!=null && cookieStr!=""){
			post.setHeader("Cookie",cookieStr);
		}
		HttpClient httpClient = getHttpClient();
		return call(httpClient, post, cs);
	}
	/**
	 * HttpClient调用执行 带cookie
	 * @param base
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	private static String call(HttpClient httpClient, HttpRequestBase base, CookieStore cs) throws Exception{
		HttpResponse response = httpClient.execute(base);

		StatusLine httpstatus = response.getStatusLine();
		if (httpstatus.getStatusCode() != HttpStatus.SC_OK) {
			System.out.println("HttpClient：调用[" + base.getURI()+ "]失败,状态码[" + httpstatus.getStatusCode() + "]");
			return null;
		}
		if(cs.getCookies().isEmpty()){
			Header[] headers = response.getAllHeaders();
			for (Header h : headers) {
				String name = h.getName();
				String value = h.getValue();
				if ("Set-Cookie".equalsIgnoreCase(name)) {
					String[] strs = value.split(";");
					for (int i = 0; i < strs.length - 1; i++) {
						String str = strs[i];
						String[] cookies = str.split("=");
						if(cookies.length>1){
							cs.addCookie(new BasicClientCookie(cookies[0], cookies[1]));
						}else{
							cs.addCookie(new BasicClientCookie(cookies[0], ""));
						}

					}
				}

			}
		}

		HttpEntity resEntity = response.getEntity();
		String charSet = EntityUtils.getContentCharSet(resEntity); // 获取字符编码
		if(charSet==null || "".equals("")){
			charSet="UTF-8";
		}
		String relsult = EntityUtils.toString(resEntity, charSet);
		EntityUtils.consume(resEntity); // 关闭流
		return relsult;
	}

	/**
	 * 执行HttpClient POST请求 带证书认证
	 * @param url
	 * @param mapParams
	 *            map参数
	 * @return
	 * @throws Exception
	 */
	public static String callHttpPostSSL_Map(String url, Map<String, String> mapParams, String cerpath, String cerpwd) {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> data = null;
		UrlEncodedFormEntity formEntity = null;
		if (mapParams != null) {
			data = new ArrayList<NameValuePair>(mapParams.size());
			for (Entry<String, String> entry : mapParams.entrySet()) {
				data.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 对参数编码
			try {
				formEntity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				System.out.println("HttpClient：编码POST参数失败,原因["+ e.getLocalizedMessage() + "]");
				return null;
			}
			post.setEntity(formEntity);
		}
		return httpSSLExcute(post, cerpath, cerpwd);
	}

	/**
	 * 执行HttpClient POST请求
	 *
	 * @param url
	 * @param body
	 *          字符串参数
	 * @param -bNeedProxy 是否需要代理
	 * @return 请求返回的串
	 */
	public static String callHttpPost_String(String url, String body) throws Exception {
		HttpPost post = new HttpPost(url);
		StringEntity entity = new StringEntity(body, HTTP.UTF_8);
		entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING,"UTF-8"));
		post.setEntity(entity);
		return httpExcute(post);
	}

	/**
	 * 执行HttpClient POST请求 带证书认证
	 *
	 * @param url
	 * @param body
	 *          字符串参数
	 * @param -bNeedProxy 是否需要代理
	 * @return 请求返回的串
	 */
	public static String callHttpPostSSL_String(String url, String body, String cerpath, String cerpwd) throws Exception {
		HttpPost post = new HttpPost(url);
		StringEntity entity = new StringEntity(body, HTTP.UTF_8);
		entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING,"UTF-8"));
		post.setEntity(entity);
		return httpSSLExcute(post, cerpath, cerpwd);
	}


	/**
	 * 调用http
	 * @param base
	 * @return
	 */
	private static String httpExcute(HttpRequestBase base){
		HttpClient httpClient = getHttpClient();
		return call(httpClient, base);
	}

	/**
	 * 调用http 证书方式
	 * @param base
	 * @param cerpath
	 * @param cerpwd
	 * @return
	 */
	private static String httpSSLExcute(HttpRequestBase base, String cerpath, String cerpwd){
		HttpClient httpClient = getHttpClientSSL(cerpath, cerpwd);
		return call(httpClient, base);
	}

	/**
	 * HttpClient调用执行
	 * @param base
	 * @return
	 */
	private static String call(HttpClient httpClient, HttpRequestBase base) {
		try {
			HttpResponse response = httpClient.execute(base);
			StatusLine httpstatus = response.getStatusLine();
			if (httpstatus.getStatusCode() != HttpStatus.SC_OK) {
				System.out.println("HttpClient：调用[" + base.getURI()+ "]失败,状态码[" + httpstatus.getStatusCode() + "]");
				return null;
			}
			HttpEntity resEntity = response.getEntity();
			String charSet = EntityUtils.getContentCharSet(resEntity); // 获取字符编码
			if(charSet==null || "".equals("")){
				charSet="UTF-8";
			}
			String relsult = EntityUtils.toString(resEntity, charSet);
			EntityUtils.consume(resEntity); // 关闭流
			return relsult;
		} catch (Exception e) {
			System.out.println("HttpClient：调用[" + base.getURI() + "]失败,原因["+ e.getLocalizedMessage() + "]");
			return null;
		} finally {
			if (httpClient != null) { // 关闭连接
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * 创建HttpClient对象_不带ssl验证
	 * @return
	 *      HttpClient
	 */
	private static HttpClient getHttpClient() {
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance(SSLSocketFactory.SSL);
			// 重写取消证书验证
			TrustManager trust = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {return null;}
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {}
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {}
			};
			ctx.init(null, new TrustManager[]{trust}, null);
		} catch (Exception e) {
			System.out.println("初始化HttpClient(无需证书验证)异常： [" +e.getMessage()+ "]");
			return null;
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schReg.register(new Scheme("https", 443, socketFactory));

		DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(schReg));

		initParameter(httpClient);
		return httpClient;
	}

	/**
	 * 创建HttpClient对象_带证书SSL双向验证
	 * @return
	 *      HttpClient
	 */
	private static HttpClient getHttpClientSSL(String cerpath, String cerpwd) {
		if(StringUtils.isEmpty(cerpath) || StringUtils.isEmpty(cerpwd)){
			System.out.println("初始化SSL_HttpClient(证书验证)参数cerpath、cerpwd不能为空");
			return null;
		}

		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance(SSLSocketFactory.SSL);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");//客户端私钥
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");//服务端授权证书

			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());

			ks.load(new FileInputStream(cerpath), cerpwd.toCharArray());
			tks.load(new FileInputStream(cerpath), cerpwd.toCharArray());

			kmf.init(ks, cerpwd.toCharArray());
			tmf.init(tks);

			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		} catch (Exception e) {
			System.out.println("初始化SSL_HttpClient(证书验证)异常： [" +e.getMessage()+ "]");
			return null;
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("https", 443, socketFactory));

		DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(schReg));

		initParameter(httpClient);
		return httpClient;
	}

	/**
	 * httpclient设置代理及参数
	 * @param httpClient
	 * @return
	 */
	private static HttpClient initParameter(DefaultHttpClient httpClient){
		// 处理是否需要代理
		if (isproxy) {
			// 加载代理配置
			HttpHost proxy = new HttpHost(proxy_IP, proxy_POST,HttpHost.DEFAULT_SCHEME_NAME);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
			// NT认证
			NTCredentials ntCredentials = new NTCredentials(proxy_USER,proxy_PASSWD, proxy_IP, proxy_DOMAIN);
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT,AuthScope.ANY_REALM, AuthScope.ANY_SCHEME),ntCredentials);
			// 认证协议类型，
			List<String> authpref = new ArrayList<String>();
			authpref.add(AuthPolicy.NTLM);
			authpref.add(AuthPolicy.BASIC);
			authpref.add(AuthPolicy.DIGEST);
			httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF,authpref);
		}
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, proxy_CONNECTTIMEOUT);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, proxy_SOTIMEOUT);
		httpClient.getParams().setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, proxy_TCP_NODELAY);
		httpClient.getParams().setBooleanParameter(CoreConnectionPNames.SO_REUSEADDR, proxy_SO_REUSEADDR);
		httpClient.getParams().setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, proxy_STALE_CONNECTION_CHECK);
		return httpClient;
	}
}
