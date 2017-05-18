package com.caiyi.financial.nirvana.ccard.material.banks.guangfa;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpUtil{

    private String url;
    private String encoding;
    private HashMap<String, String> params;
    private HashMap<String, String> maps;
    private HttpURLConnection connection;

    public HttpUtil(String url, String encoding) {
        this.url = url;
        this.encoding = encoding;
    }

    public HttpUtil(String url, String encoding, HashMap<String, String> params, HashMap<String, String> pros) {
        this.url = url;
        this.encoding = encoding;
        this.maps = pros;
        this.params = params;
    }

    public InputStream getInputStream() throws Exception {
        URL _url = new URL(this.url);
        if(connection==null){
            connection = (HttpURLConnection) _url.openConnection();
            connection.setConnectTimeout(180000);
            connection.setReadTimeout(180000);
        }

        if (this.maps != null) {
            Iterator<String> keys = this.maps.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                connection.addRequestProperty(key, (String) this.maps.get(key));
            }
        }

        if (this.params != null) {
            connection.setDoOutput(true);
            StringBuffer sb = new StringBuffer();
            Iterator<String> keys = this.params.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = (String) this.params.get(key);
                if (null==value){
                    value = "";
                }
                sb.append(key).append("=").append(URLEncoder.encode(value, this.encoding)).append("&");
            }
            String tmp = sb.toString();
            if (tmp.endsWith("&")) {
                tmp = tmp.substring(0, tmp.lastIndexOf("&"));
            }
            OutputStream os = connection.getOutputStream();
            os.write(tmp.getBytes());
        }

        return connection.getInputStream();
    }

    public String getResponseString() throws Exception {
        String str = "";
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            str = new String(baos.toByteArray(), this.encoding);
        } finally {
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public Map<String, List<String>> getHeaderFields(){
        return this.connection.getHeaderFields();
    }

    public String getHeaderField(String name){
        return this.connection.getHeaderField(name);
    }

    public HttpURLConnection getConnection(){
        return this.connection;
    }

	public BufferedImage getRandomImageOfJPEG(String method,
			String sUrl, HashMap<String, String> paramMap,
			HashMap<String, String> requestHeaderMap) {
		BufferedImage image =null;
		HttpURLConnection httpUrlConnection = null;
		try {
			if (method.equalsIgnoreCase("GET") && paramMap != null) {
				StringBuffer param = new StringBuffer();
				int i = 0;
				for (String key : paramMap.keySet()) {
					if (i == 0)
						param.append("?");
					else
						param.append("&");
					param.append(key).append("=").append(paramMap.get(key));
					i++;
				}
				sUrl += param;
			}
			URL url = new URL(sUrl);
			if (method == null || (!"GET".equalsIgnoreCase(method) && !"POST".equalsIgnoreCase(method))) {
				method = "POST";
			}
			URL resolvedURL = url;
			URLConnection urlConnection = resolvedURL.openConnection();
			httpUrlConnection = (HttpURLConnection) urlConnection;
			httpUrlConnection.setRequestMethod(method);
			httpUrlConnection.setRequestProperty("Accept-Language","zh-cn,zh;q=0.5");
			// Do not follow redirects, We will handle redirects ourself
			httpUrlConnection.setInstanceFollowRedirects(false);
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setConnectTimeout(30000);
			httpUrlConnection.setReadTimeout(30000);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDefaultUseCaches(false);
			if (requestHeaderMap != null) {
				for (Map.Entry<String, String> entry : requestHeaderMap
						.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					if (key != null && val != null) {
						urlConnection.setRequestProperty(key, val);
					}
				}
			}
			
			if (method.equalsIgnoreCase("POST") && paramMap != null) {
				StringBuffer param = new StringBuffer();
				for (String key : paramMap.keySet()) {
					param.append("&");
					param.append(key).append("=").append(paramMap.get(key));
				}
				OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
				osw.write(param.toString());
				osw.flush();
				osw.close();
			}
			
			httpUrlConnection.connect();
			int responseCode = httpUrlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK|| responseCode == HttpURLConnection.HTTP_CREATED) {
				InputStream in=httpUrlConnection.getInputStream();
//				JPEGImageDecoder decoderFile = JPEGCodec.createJPEGDecoder(in);
//				image = decoderFile.decodeAsBufferedImage();
                                image = ImageIO.read(in);
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (httpUrlConnection != null) {
				httpUrlConnection.disconnect();
			}
		}
		return image;
	}
}