package com.caiyi.financial.nirvana.ccard.material.banks.minsheng;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;

/**这个类主要是 访问页面***/
public class NetHttp {
	
	/**初始化Client**/
	public static CloseableHttpClient getHttpClient(CookieStore cookiestore ){
		SSLContext sslcontext = null;
		
		try {
			sslcontext =SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		SSLConnectionSocketFactory  ssf = new SSLConnectionSocketFactory(sslcontext,
//				new String[]{"TLSV1"},null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		CloseableHttpClient httpClient = HttpClients.
				custom()
//				.setSSLSocketFactory(ssf)
//				.setProxy(new HttpHost("127.0.0.1", 8888))
	            .setDefaultCookieStore(cookiestore)
	            .build();
		return httpClient;
	}
	/****关闭响应**/
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
	/***get 方法***/
	public static String doget(String url , CloseableHttpClient clent, Header[] requestHeaders,  Map<String, String> map ){
		if(map !=null && map.size()> 0){
			StringBuilder params = new StringBuilder();
			 for (Map.Entry<String, String> entry : map.entrySet()) {
               params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
          }
			 url += "?" + params.substring(0, params.length() - 1);	
		}
		HttpGet  httpget = new HttpGet(url);
		System.out.println("正在获取页面：\n" + url);
		if(requestHeaders !=null){
			httpget.setHeaders(requestHeaders);
		}
		
		CloseableHttpResponse response = null;
	    String resEntityStr = null;
	    
	    try {
	    	response =clent.execute(httpget);
	    	StatusLine  statuseline = response.getStatusLine();
			System.out.println(statuseline);
			HttpEntity  httpentity = response.getEntity();

//			httpentity.
	    	resEntityStr = EntityUtils.toString(httpentity);
	    	
	    	response.close();
	    }catch(IOException e){
	    	  e.printStackTrace();
	    }
	    finally {
          close(null, response);
          return resEntityStr;
      }
}
/**post 方法**/
	public static String  doPost(String url , CloseableHttpClient client,Map<String, String> requestHeaders, Map<String, String> map){
		HttpPost post = new HttpPost(url);
		
//		  post.setHeader("User-Agent", 
//				  "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-CN;" +
//				  " HUAWEI Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko)" +
//				  " Version/4.0 UCBrowser/10.8.5.689 U3/0.8.0 Mobile Safari/534.30");
		if(requestHeaders !=null){
			 for (String key : requestHeaders.keySet()) {
				    post.addHeader(key, requestHeaders.get(key));
				    post.setHeader(key, requestHeaders.get(key));
				  }
			
		}
		if(map!=null && map.size()>0){
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
           
            resEntityStr = EntityUtils.toString(entity,"utf-8");
            
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           close(null, response);
            return resEntityStr;
        }
	}
	
	/***获取验证图片****/
	public static byte[] getCode(CookieStore cookieStore, String url){
		 byte[] bytes = null;
		 CloseableHttpClient client = getHttpClient(cookieStore);
		 CloseableHttpResponse response = null;
		 long random = Calendar.getInstance().getTimeInMillis();
	//	 String url = "https://creditcard.cmbc.com.cn/wsonline/captcha.jpg?";	  
		 HttpGet get = new HttpGet(url);
		 try{
			 response = client.execute(get);
			 StatusLine statusline = response.getStatusLine();
			 Header[] a =  response.getAllHeaders();
			// Set-Cookie
			 Header[] b = response.getHeaders("Cookie");


			 System.out.println("statusline\n:" + statusline );
	
			 HttpEntity	 Entity   =  response.getEntity();
			 bytes = EntityUtils.toByteArray(Entity);
	//		 FileUtils.writeByteArrayToFile(new File("d:/data/c.jpg"),bytes);




			  

			   
		 }catch(IOException e){
			 e.printStackTrace();
			 
		 }finally{
			 close(null,response );
			 return bytes;
		 }
		 
		 
		
		
	} 
	
	

}
