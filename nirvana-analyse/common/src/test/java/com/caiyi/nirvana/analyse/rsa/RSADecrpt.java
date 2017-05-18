package com.caiyi.nirvana.analyse.rsa;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by been on 2017/1/14.
 * ref http://blog.csdn.net/wangqiuyun/article/details/42143957
 */
public class RSADecrpt {
    /**
     * 随机生成秘钥对
     *
     * @param filePath
     */
    public static void genKeyPair(String filePath) {

        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        } catch (Exception e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        try {
            String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
            String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
            IOUtils.write(publicKeyString, new FileOutputStream(new File("public.keystore")));
            IOUtils.write(privateKeyString, new FileOutputStream(new File("private.keystore")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RSAPrivateKey getPrivateKey(String path) throws Exception {
        byte[] buffer = Base64.decodeBase64(IOUtils.toString(new FileInputStream(new File(path))));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public static RSAPublicKey getPublicKey(String path) throws Exception {
        byte[] buffer = Base64.decodeBase64(IOUtils.toString(new FileInputStream(new File(path))));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePrivate(keySpec);
    }

    public static byte[] encrypt(RSAPublicKey publicKey, byte[] content) throws Exception {
        Cipher cipher = null;
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
//        cipher = Cipher.getInstance("RSA", new BouncyCastlePQCProvider());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }


}
