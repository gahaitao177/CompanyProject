package com.caiyi.financial.nirvana.nebula;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by terry on 2016/8/3.
 */
public class nebulaClient {


    public static void main(String[] args) throws Exception {


//    	 URL url = new URL("http://localhost:8081/repository/nebula-images/hsk/jiaotong/cuserid/1.jpg");
//
//         final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//         conn.setDoInput(true);
//         conn.setDoOutput(true);
//         conn.setRequestMethod("PUT");
//         conn.setRequestProperty("Content-Type","text/xml");
//         conn.setRequestProperty("Content-Length","0");
//         DataOutputStream os = new DataOutputStream( conn.getOutputStream() );
//         os.write( "".getBytes("UTF-8"), 0, 0);
//         os.flush();
//         os.close();
//         conn.connect();
//         InputStream is = conn.getInputStream();
//         Integer code = conn.getResponseCode();
//         final String contentType = conn.getContentType();
//         System.out.println(code+":"+contentType);
//        201
//        Created
//        200
//        OK
//        204
//        No Content
//        404
//        NOT_FOUND

        String filepath="D:\\opt\\artwork-lich-king03-large.jpg";
        uploadFile("/jiaotong/artwork-lich-king03-large.jpg",filepath);
//        uploadFileWithHttpMime("http://localhost:8081/repository/nebula-images/text.txt", filepath);
        String downpath="D:\\opt\\download\\jiaotong";
        downloadFile("jiaotong/artwork-lich-king03-large.jpg","lich-king.jpg",downpath);
        deleteFile("jiaotong/artwork-lich-king03-large.jpg");


    }


    public static boolean deleteFile(String filePath){
        try {
            URL url = new URL("http://localhost:8081/repository/nebula-images/"+filePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为DELETE请求
            conn.setRequestMethod("DELETE");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setDefaultUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4xMjM=");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.connect();

            int responseCode=conn.getResponseCode();
            if (responseCode==404){
                return false;
            }else if (responseCode==204){
                return true;
            }


            System.out.println(conn.getResponseCode());
            System.out.println(conn.getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean downloadFile(String filePath,String fileName,String pathDir){
        try {
            URL url = new URL("http://localhost:8081/repository/nebula-images/"+filePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("GET");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setDefaultUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Authorization","Basic YWRtaW46YWRtaW4xMjM=");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.connect();
            DataInputStream ins =null;
            DataOutputStream out=null;
            try {
                ins = new DataInputStream(conn.getInputStream());
                File f = new File(pathDir);
                if (!f.exists()) {
                    f.mkdirs();
                }
                // 密码键盘的位置
                out = new DataOutputStream(new FileOutputStream(pathDir + "/" + fileName));
                byte[] buffer = new byte[4096];
                int count = 0;
                while ((count = ins.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }
                int responseCode = conn.getResponseCode();
                System.out.println(responseCode);
                System.out.println(conn.getResponseMessage());
                if (responseCode==200){
                    return true;
                }else {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("下载文件异常！");
                e.printStackTrace();
            }finally{
                if (ins!=null) {
                    ins.close();
                }
                if (out!=null) {
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean uploadFile(String fileName,String filepath) {
        OutputStream out =null;
        DataInputStream in =null;
        try {
            File file = new File(filepath);
            // 换行符
            URL url = new URL("http://localhost:8081/repository/nebula-images/"+fileName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("PUT");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setDefaultUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Authorization","Basic YWRtaW46YWRtaW4xMjM=");
            conn.setRequestProperty("Charsert", "UTF-8");
            out = new DataOutputStream(conn.getOutputStream());
            in = new DataInputStream(new FileInputStream(file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读1KB数据,并且将文件数据写入到输出流中
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            conn.connect();
            int responseCode = conn.getResponseCode();
            System.out.println(responseCode);
            System.out.println(conn.getResponseMessage());
            if (responseCode==201){
                return true;
            }else {
                return false;
            }

//            // 最后添加换行
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }finally{
            try {
                if (in!=null) {
                    in.close();
                }
                if (out!=null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }




}
