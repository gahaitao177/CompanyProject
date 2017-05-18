package com.caiyi.financial.nirvana.ccard.material.banks.xingye;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpUtil{

    private String url;
    private String encoding;
    private Map<String, String> params;
    private Map<String, String> maps;
    private HttpURLConnection connection;

    public HttpUtil(String url, String encoding) {
        this.url = url;
        this.encoding = encoding;
    }

    public HttpUtil(String url, String encoding, Map<String, String> params, Map<String, String> pros) {
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
}