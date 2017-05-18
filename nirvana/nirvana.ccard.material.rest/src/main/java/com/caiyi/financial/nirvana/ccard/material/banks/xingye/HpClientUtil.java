package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

public class HpClientUtil {
	private static SSLConnectionSocketFactory socketFactory;
	public static BufferedImage getRandomImageOfJPEG(String url,Map<String, String> headers,HttpClient httpclientme, HttpContext localContext,RequestConfig requestConfig) {
		HttpGet httpget=null;
		InputStream in = null;
		BufferedImage image =null;
		try {
			httpget = new HttpGet(url);
			httpget.setConfig(requestConfig);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpget.setHeader(key, headers.get(key));
				}
			}
			HttpResponse response = httpclientme.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();			
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in=entity.getContent();
				//JPEGImageDecoder decoderFile = JPEGCodec.createJPEGDecoder(in);
				//image = decoderFile.decodeAsBufferedImage();
                image = ImageIO.read(in);
				in.close();
				return image;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpget!=null) {
				httpget.abort();;
			}
		}
		return null; 
	}
	//获取ssl连接HttpClient
	public static String getSSLHttpPost(String url,List<NameValuePair> values,String cookie){
		InputStream in = null;
		String context="";
		HttpPost post = null;
		try {	
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[]{manager}, null);
			socketFactory = new SSLConnectionSocketFactory(sslcontext,NoopHostnameVerifier.INSTANCE);
			
			RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
					.setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,AuthSchemes.DIGEST))
					.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
			
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()       
					.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
			
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)       
					.setDefaultRequestConfig(defaultRequestConfig).build();
			post = new HttpPost(url);
			if(cookie!=null){
				post.setHeader("Cookie",cookie);
			}
			UrlEncodedFormEntity myEntity = new UrlEncodedFormEntity(values,Consts.UTF_8);
			post.setEntity(myEntity);
			
			CloseableHttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in = entity.getContent();
				StringBuffer buffer = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, Consts.UTF_8));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
				context=buffer.toString();
				in.close();
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (post!=null) {
				post.abort();;
			}
		}
		return context;
    }
	
	public static TrustManager manager = new X509TrustManager() {
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}
	}; 
		
	public static void httpGetImage(String url,Map<String, String> headers,String filePath,HttpClient httpclientme, HttpContext localContext,RequestConfig requestConfig) {
		HttpGet httpget=null;
		InputStream in = null;
		try {
			httpget = new HttpGet(url);
			httpget.setConfig(requestConfig);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpget.setHeader(key, headers.get(key));
				}
			}
			HttpResponse response = httpclientme.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();			
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in=entity.getContent();
				DataInputStream ins = new DataInputStream(in);
				File f = new File(filePath);//"d:/opt"
				if(!f.exists()){
					f.mkdirs();
				}
				// 图片位置
				DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath+"/code.bmp"));
				byte[] buffer = new byte[4096];
				int count = 0;
				while ((count = ins.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				out.close();
				ins.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpget!=null) {
				httpget.abort();;
			}
		}
		
	}
	
	public static void httpGetImage(String url,Map<String, String> headers,String filePath,HttpClient httpclientme, HttpContext localContext) {
		HttpGet httpget=null;
		InputStream in = null;
		try {
			httpget = new HttpGet(url);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpget.setHeader(key, headers.get(key));
				}
			}
			HttpResponse response = httpclientme.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();			
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in=entity.getContent();
				DataInputStream ins = new DataInputStream(in);
				File f = new File(filePath);//"d:/opt"
				if(!f.exists()){
					f.mkdirs();
				}
				// 图片位置
				DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath+"/code.bmp"));
				byte[] buffer = new byte[4096];
				int count = 0;
				while ((count = ins.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				out.close();
				ins.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpget!=null) {
				httpget.abort();;
			}
		}
		
	}
	
	public static String httpPost(String url,Map<String, String> headers,String parames,HttpClient httpclient, HttpContext localContext,String encode,RequestConfig requestConfig) {
		String context="";
		HttpPost httpPost=null;
		InputStream in = null;
		try {
			httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			//不带参数名的参数
			if (parames!=null && !parames.equals("")) {
				StringEntity myEntity = new StringEntity(parames, encode);
				httpPost.setEntity(myEntity); 
			}
            
			// 传递本地的http上下文给服务器
			HttpResponse response = httpclient.execute(httpPost, localContext);
			// 获取本地信息
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in = entity.getContent();
				StringBuffer buffer = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
				context=buffer.toString();
				in.close();
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpPost!=null) {
				httpPost.abort();;
			}
		}
		return context;
	}
	

	public static String httpPost(String url,Map<String, String> headers,String parames,HttpClient httpclient, HttpContext localContext,String encode) {
		String context="";
		HttpPost httpPost=null;
		InputStream in = null;
		try {
			httpPost = new HttpPost(url);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			//不带参数名的参数
			if (parames!=null && !parames.equals("")) {
				StringEntity myEntity = new StringEntity(parames, encode);
				httpPost.setEntity(myEntity); 
			}
            
			// 传递本地的http上下文给服务器
			HttpResponse response = httpclient.execute(httpPost, localContext);
			// 获取本地信息
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in = entity.getContent();
				StringBuffer buffer = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
				context=buffer.toString();
				in.close();
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpPost!=null) {
				httpPost.abort();;
			}
		}
		return context;
	}
	
	
	public static String httpPost(String url,Map<String, String> headers,Map<String, String> parames,HttpClient httpclient, HttpContext localContext,String encode,RequestConfig requestConfig) {
		String context="";
		HttpPost httpPost=null;
		InputStream in = null;		
		try {
			httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			
			if (parames!= null) {//带参数名的参数
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				Iterator it = parames.entrySet().iterator();
				while (it.hasNext()) {
					Entry parmEntry = (Entry) it.next();
					paramList.add(new BasicNameValuePair((String) parmEntry.getKey(),(String) parmEntry.getValue()));
				}
				UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(paramList, encode);
				httpPost.setEntity(postEntity);
			}
			
			// 传递本地的http上下文给服务器
			HttpResponse response = httpclient.execute(httpPost, localContext);
			// 获取本地信息
			HttpEntity entity = response.getEntity();
			
			String statusCode=response.getStatusLine().toString();
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				StringBuffer buffer = new StringBuffer();
				in=entity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
				context=buffer.toString();
				in.close();
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpPost!=null) {
				httpPost.abort();
			}
		}
		return context;
	}
	
	@SuppressWarnings("deprecation")
	public static DefaultHttpClient getHttpsClient()   
    {  
        try {          				
            SSLContext ctx = SSLContext.getInstance("TLS");  
            X509TrustManager tm = new X509TrustManager() {  
  
                @Override  
                public void checkClientTrusted(  
                        X509Certificate[] chain,
                        String authType)  
                        throws CertificateException {
                }  
  
                @Override  
                public void checkServerTrusted(  
                        X509Certificate[] chain,
                        String authType)  
                        throws CertificateException {
                }  
  
                @Override  
                public X509Certificate[] getAcceptedIssuers() {
                    return null;  
                }  
                  
            };  
            DefaultHttpClient client = new DefaultHttpClient();  
            ctx.init(null, new TrustManager[] { tm }, null);  
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);  
              
            ClientConnectionManager ccm = client.getConnectionManager();  
            SchemeRegistry sr = ccm.getSchemeRegistry();                      			
            //设置要使用的端口，默认是443  
            sr.register(new Scheme("https", 443, ssf));  
            return client;  
        } catch (Exception ex) {  
           
            return null;  
        }  
    }  
	
	/** 
     * 创建SSL安全连接 
     * 
     * @return 
     */  
    public static SSLConnectionSocketFactory createSSLConnSocketFactory() {  
        SSLConnectionSocketFactory sslsf = null;  
        try {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
  
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                    return true;  
                }  
            }).build();  
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {  
  
                @Override  
                public boolean verify(String arg0, SSLSession arg1) {  
                    return true;  
                }  
  
                @Override  
                public void verify(String host, SSLSocket ssl) throws IOException {  
                }  
  
                @Override  
                public void verify(String host, X509Certificate cert) throws SSLException {  
                }  
  
                @Override  
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {  
                }  
            });  
        } catch (GeneralSecurityException e) {  
            e.printStackTrace();  
        }  
        return sslsf;  
    }
    
	public static String httpPost(String url,Map<String, String> headers,Map<String, String> parames,HttpClient httpclient, HttpContext localContext,String encode) {
		String context="";
		HttpPost httpPost=null;
		InputStream in = null;

		try {
			httpPost = new HttpPost(url);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			
			if (parames!= null) {//带参数名的参数
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				Iterator it = parames.entrySet().iterator();
				while (it.hasNext()) {
					Entry parmEntry = (Entry) it.next();
					paramList.add(new BasicNameValuePair((String) parmEntry.getKey(),(String) parmEntry.getValue()));
				}
				UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(paramList, encode);
				httpPost.setEntity(postEntity);
			}
			
			// 传递本地的http上下文给服务器
			HttpResponse response = httpclient.execute(httpPost, localContext);
			// 获取本地信息
			HttpEntity entity = response.getEntity();
			
			String statusCode=response.getStatusLine().toString();
			
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				StringBuffer buffer = new StringBuffer();
				in=entity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
				context=buffer.toString();
				in.close();
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpPost!=null) {
				httpPost.abort();
			}
		}
		return context;
	}
	
	
	public static String httpGet(String url,Map<String, String> headers,HttpClient httpclientme, HttpContext localContext,String encode) {
		String context="";
		HttpGet httpget =null;
		InputStream in = null;
		try {
			httpget = new HttpGet(url);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpget.setHeader(key, headers.get(key));
				}
			}

			
			// 传递本地的http上下文给服务器
			HttpResponse response = httpclientme.execute(httpget, localContext);
			// 获取本地信息
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in=entity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in,encode));
				StringBuffer temp = new StringBuffer(); 
				String line = br.readLine(); 
				 while (line != null) { 
	                temp.append(line).append("\r\n"); 
	                line = br.readLine(); 
	            } 
				context=new String(temp.toString().getBytes(),encode); 
				br.close(); 
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpget!=null) {
				httpget.abort();
			}
		}
		return context;
	}
	
	
	
	public static String httpGet(String url,Map<String, String> headers,HttpClient httpclientme, HttpContext localContext,String encode,boolean isencode,RequestConfig requestConfig) {
		String context="";
		HttpGet httpget =null;
		InputStream in = null;
		try {
			httpget = new HttpGet(url);
			httpget.setConfig(requestConfig);
			if (headers != null){
				for (String key : headers.keySet()) {
					httpget.setHeader(key, headers.get(key));
				}
			}
			// 传递本地的http上下文给服务器
			HttpResponse response = httpclientme.execute(httpget, localContext);
			// 获取本地信息
			HttpEntity entity = response.getEntity();
			String statusCode=response.getStatusLine().toString();
			if ("HTTP/1.1 200 OK".equals(statusCode)||"HTTP/1.0 200 OK".equals(statusCode)) {
				in=entity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in,encode));
				StringBuffer temp = new StringBuffer(); 
				String line = br.readLine(); 
				 while (line != null) { 
	                temp.append(line).append("\r\n"); 
	                line = br.readLine(); 
	            } 
				if (isencode) {
					context=new String(temp.toString().getBytes(),encode); 
				}else {
					context=new String(temp.toString().getBytes()); 
				}
				br.close(); 
			}else {
				System.out.println(statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (httpget!=null) {
				httpget.abort();
			}
		}
		return context;
	}

	
	public static String httpGet(String url,Map<String, String> headers,HttpClient httpclientme, HttpContext localContext,String encode,RequestConfig requestConfig) {
		return httpGet(url, headers, httpclientme, localContext, encode, true, requestConfig);
	}
	
	public static String httpGet(String url,Map<String, String> headers,HttpClient httpclientme, HttpContext localContext) {
		return httpGet(url, headers, httpclientme, localContext, "UTF-8");
	}
}
