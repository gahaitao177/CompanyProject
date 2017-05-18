package com.caiyi.finanial.nirvana.websocket;

import com.neovisionaries.ws.client.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by wenshiliang on 2016/11/15.
 */
public class WebSocketTest {
    public static void main(String[] args) throws Exception {
//        String str = "ws://192.168.1.76:20001/demo";//测试环境
//        String str = "ws://103.47.136.148:18092/demo";//线上
//        String str = "wss://hsk.youyuwo.com/demo";//线上
//        String str = "ws://127.0.0.1:20001/demo";//本地
//        String str = "ws://192.168.1.207:18092/demo";//测试环境ng转发
        String str = "ws://192.168.1.121:18092/demo";//测试环境ng转发
//        String str = "ws://192.168.1.76:20001/notcontrol/getValidMessageCount
// .go?appId=lcURSCR2U016CC1L020ZA1B0J3Z100817&accessToken=+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvcCzWLSNqNiv
// /eU8N2S/TxMrpiUBpc011Wj6HIQaPeZ25V+Eso/PZtATPqxEozY8K+LIK5k1Ll/r71u5wupANWnOu+MRlErqj
// /SBSzgTn1Z4lntz5IYkXIHq5dBXsEunoHDIi5YUdYUQ==";

        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        if (str.indexOf("wss") >= 0) {
            factory = factory.setSSLContext(createSSLContext());
        }
        long start = System.currentTimeMillis();
        WebSocket ws = factory.createSocket(str);
        ws.connect();
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                System.out.println("message---->" + message);
            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
                websocket.sendText("hello");
            }

            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame
                    clientCloseFrame, boolean closedByServer) throws Exception {
                long end = System.currentTimeMillis();

                System.out.println("onDisconnected---->" + (end - start));

            }
        });
        //输入stop 停止
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            ws.sendText(s);
        }
    }

    public static SSLContext createSSLContext() throws Exception {
        X509TrustManager x509m = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };
        // 获取一个SSLContext实例
        SSLContext s = SSLContext.getInstance("TLS");
        // 初始化SSLContext实例
        s.init(null, new TrustManager[]{x509m},
                new java.security.SecureRandom());

        return s;
    }
}
