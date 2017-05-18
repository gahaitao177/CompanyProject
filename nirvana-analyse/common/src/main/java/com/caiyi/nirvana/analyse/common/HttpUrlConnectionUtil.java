package com.caiyi.nirvana.analyse.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnectionUtil {

    /**
     * 通过HttpURLConnection模拟post表单提交
     *
     * @param path
     * @param params 例如"name=zhangsan&age=21"
     * @return
     * @throws Exception
     */
    public static String sendPostRequestByForm(String path, String params) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// 提交模式
            conn.setConnectTimeout(10000);// 连接超时 单位毫秒
            conn.setReadTimeout(2000);// 读取超时 单位毫秒
            conn.setDoOutput(true);// 是否输入参数
            byte[] bypes = params.toString().getBytes();
            conn.getOutputStream().write(bypes);// 输入参数
            inStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
            StringBuffer responseResult = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (responseResult.length() != 0) {
                    responseResult.append("/n");
                }
                responseResult.append(line);
            }

            return responseResult.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != inStream) {
                inStream.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
//		String result = sendPostRequestByForm("http://192.168.2.47:8080/trade/joingame.go", "cproductId=10001&cperiodId=4&otherUserId=zdongya");
//		System.out.println(result);
//		 String rid = UUID.randomUUID().toString();
//         int ii = ("aaa" + DateUtil.getCurrentFormatDate("yyyyMMddHHmmss") + "100" + rid).hashCode();
//		String applyid = Integer.toHexString(ii).toUpperCase();
//        applyid = DateUtil.getCurrentDateTime().substring(2, 4) + StringUtil.LeftPad(applyid, "F", 8);
//        System.out.println(applyid);
    }

}
