package com.caiyi.nirvana.analyse.crypto;

import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.junit.Test;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by been on 2017/1/10.
 */
public class StreamExample extends BaseCrypto {

    @Test
    public void test() throws Exception {
        final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"), "AES");
        final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
        Properties properties = new Properties();
        final String transform = "AES/CBC/PKCS5Padding";

        String input = "hello world!";
        //Encryption with CryptoOutputStream.m
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CryptoOutputStream cos = new CryptoOutputStream(transform, properties, outputStream, key, iv)) {
            cos.write(getUTF8Bytes(input));
            cos.flush();
        }
        System.out.println("Encrypted: " + Arrays.toString(outputStream.toByteArray()));
        // Decryption with CryptoInputStream.
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        try (CryptoInputStream cis = new CryptoInputStream(transform, properties, inputStream, key, iv)) {
            byte[] decryptedData = new byte[1024];
            int decryptedLen = 0;
            int i = 0;
            while (-1 < (i = cis.read(decryptedData, decryptedLen, decryptedData.length - decryptedLen))) {
                decryptedLen += i;
            }
            System.out.println("Decrypted: " + new String(decryptedData, 0, decryptedLen, StandardCharsets.UTF_8));

        }

    }
}
