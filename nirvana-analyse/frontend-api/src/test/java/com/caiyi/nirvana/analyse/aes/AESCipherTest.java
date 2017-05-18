package com.caiyi.nirvana.analyse.aes;

import com.caiyi.nirvana.analyse.util.AESCipher;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by been on 2017/1/13.
 */
public class AESCipherTest {
    @Test
    public void testAES() throws Exception {
//        随机生成16位数字字母字符串
//        System.out.println(RandomStringUtils.random(16, true, true));
//        String key = "hvmPEfCegpsxNrcV";
//        String decrypted = AESCipher.encryptAES("been", key);
//        String data = AESCipher.decryptAES(decrypted, key);
//        System.out.println(data);
        byte[] content = IOUtils.toByteArray(new FileInputStream(new File("ios/encrypt.txt")));
        System.out.println(AESCipher.decryptAES(content));
    }


    /**
     * 解压andriod 上传数据
     *
     * @throws Exception
     */
    @Test
    public void testAndriodFromJunLin() throws Exception {
        //test file exists
//        String path = "andriod/test.bin";
        String path = "andriod/1486708094048.b";
        System.out.println(new File(path).getAbsoluteFile());
        byte[] keybs = {104, 118, 109, 80, 69, 102, 67, 101, 103, 112, 115, 120, 78, 114, 99, 86};
        byte[] ivbs = {119, 119, 119, 46, 121, 111, 117, 121, 117, 46, 119, 111, 46, 99, 111, 109};

        byte[] encrptedBytes = IOUtils.toByteArray(new FileInputStream(new File(path)));


        SecretKeySpec secretKey = new SecretKeySpec(keybs, "AES");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivbs);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] result = cipher.doFinal(encrptedBytes);
        System.out.println(new String(result));
    }

    @Test
    public void test2() throws Exception {
        IOUtils.write("been".getBytes(), new FileOutputStream(new File("ios/test.bin")));

    }

    @Test
    public void test3() {
        System.out.println(Arrays.toString("been".getBytes()));
        System.out.println(Arrays.toString("www.youyu.wo.com".getBytes()));
        System.out.println(Arrays.toString("hvmPEfCegpsxNrcV".getBytes()));
    }


    @Test
    public void testIOSFromHuiYang() throws Exception {
//        String path = "ios/encrypt.txt";
        String path = "andriod/test.bin";

        byte[] encrptedBytes = IOUtils.toByteArray(new FileInputStream(new File(path)));
        SecretKeySpec secretKey = new SecretKeySpec("hvmPEfCegpsxNrcV".getBytes(), "AES");

        IvParameterSpec ivParameterSpec = new IvParameterSpec("www.youyu.wo.com".getBytes());

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] result = cipher.doFinal(encrptedBytes);
        System.out.println(new String(result));
    }

    @Test
    public void testDecrypFile() throws Exception {
        byte[] enCodeFormat = "hvmPEfCegpsxNrcV".getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");

        byte[] initParam = "www.youyu.wo.com".getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        // 指定加密的算法、工作模式和填充方式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] data = IOUtils.toByteArray(new FileInputStream(new File("data-template.json")));
        byte[] encryptedBytes = cipher.doFinal(data);
        IOUtils.write(encryptedBytes, new FileOutputStream(new File("data-template.bin")));
    }


    @Test
    public void testUpload() throws Exception {
        String path = "andriod/1486708094048.b";
        File file = new File(path);
        String url = "http://192.168.2.18:8080/nirvana/uploadFile.go";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        FileBody fileBody = new FileBody(file);
        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("test", fileBody)
                .setCharset(StandardCharsets.UTF_8)
                .build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        System.out.println(result);

    }
}
