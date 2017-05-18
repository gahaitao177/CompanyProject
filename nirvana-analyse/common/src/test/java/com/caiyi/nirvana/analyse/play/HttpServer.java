package com.caiyi.nirvana.analyse.play;

import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;

/**
 * Created by been on 2017/1/16.
 */
public class HttpServer {
    private int port = 9999;
    private boolean isServerDone = false;


    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();
        server.run();
    }

    private void run() {

        SSLContext sslContext = this.createSSLContext();
        try {
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket();
            System.out.println("SSL server started .....");
            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                new ServerThread(sslSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpServer() {

    }

    public HttpServer(int port) {
        this.port = port;
    }

    private SSLContext createSSLContext() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(IOUtils.toInputStream("test.jks"), "password".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());
            KeyManager[] managers = keyManagerFactory.getKeyManagers();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(managers, trustManagers, null);
            return sslContext;
        } catch (Exception e) {

        }
        return null;
    }

}

class ServerThread extends Thread {
    private SSLSocket sslSocket = null;

    ServerThread(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
    }

    @Override
    public void run() {
        try {
            sslSocket.startHandshake();
            SSLSession sslSession = sslSocket.getSession();
            System.out.println(sslSession.getProtocol());
            System.out.println(sslSession.getCipherSuite());
//            InputStream inputStream = sslSocket.getInputStream();
            OutputStream outputStream = sslSocket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
            printWriter.write("hello".toCharArray());
            printWriter.flush();
            sslSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
