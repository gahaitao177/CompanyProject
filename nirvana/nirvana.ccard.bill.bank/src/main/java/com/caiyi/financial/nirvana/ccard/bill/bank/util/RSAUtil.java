package com.caiyi.financial.nirvana.ccard.bill.bank.util;

/**
 * Created by dengh on 2016/7/29.
 */

import org.apache.commons.codec.binary.Base64;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;




/**
 * 经典的数字签名算法RSA
 * 数字签名
 * */
public class RSAUtil {

    public static void main(String[] args) throws Exception {

        //生成密钥
//		generateKey();


        System.out.println("------------------通用测试-------------------");

        String  pr5 = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAk100nlv/WlrTkKmNdBwURQWneGofoIQoV77TLOVhffSRDNDjsNm6fmgQJeM7RNcITwv+NLAh+jVv0XxWIvt2xwIDAQABAkEAh/470OiVfozTMW1HXR+MlSXipv1IsplDobY4q/YDQnhC92EnCSLhLvxVNMghfNp9ztfR6htiFf9397MnvGPEAQIhAOxenwgEdWAf8SfnuTY8Ff6UydRlFL9zxL4OKZx653G/AiEAn5pEJoiXX7YcaLZRv2RVyM7BWPB3CGCkpeF7OiHvLPkCIBAIFuc3TjK31+Zp/BDmoGNE+i9yr6aQlo6BbWcUmvAHAiEAkwCY4tEOc9adpgi/lMRKixF8XnnleS7il/Lt+CZHUKkCIQChqk9biDo5qAiqZDNh4DgAhQ/0XeFLe/MxgDPTnFWwzQ==";
        String pu5="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJNdNJ5b/1pa05CpjXQcFEUFp3hqH6CEKFe+0yzlYX30kQzQ47DZun5oECXjO0TXCE8L/jSwIfo1b9F8ViL7dscCAwEAAQ==";


        String uid5 = "cc4a0768139";
        String so5 = "huishuaka";
        String t5 = "1469778999496";
        String s5 = uid5+so5 + t5;
        System.out.println("加签数据（uuid+source+timestamp）："+ s5);

        byte[] sb5=RSAUtil.sign(s5.getBytes("utf-8"), Base64.decodeBase64(pr5));
        String sign5 = Base64.encodeBase64String(sb5);

        String str5 = sign5.replace("=", "%3D");
        System.out.println("产生签名（signature）："+sign5);
        System.out.println("产生签名（16进制signature）："+str5.replace("+", "%2B"));


//        sign5 = com.vcredit.jdev.ccl.commons.utils.RSAUtil.signBase64(s5, pr5);
//        String ss = com.vcredit.jdev.ccl.commons.utils.RSAUtil.signBase64(s5, pr5);
//        System.out.println(com.vcredit.jdev.ccl.commons.utils.RSAUtil.verifyBase64(s5,pu5,ss));
    }





    //数字签名，密钥算法
    public static final String KEY_ALGORITHM="RSA";

    /**
     * 数字签名
     * 签名/验证算法
     * */
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";

    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     * */
    private static final int KEY_SIZE=1024;
    //公钥
    private static final String PUBLIC_KEY="RSAPublicKey";
    //私钥
    private static final String PRIVATE_KEY="RSAPrivateKey";

    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 初始化密钥对
     * @return Map 甲方密钥的Map
     * */
    public static Map<String,Object> initKey() throws Exception{
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        //甲方公钥
        RSAPublicKey publicKey=(RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        RSAPrivateKey privateKey=(RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        Map<String,Object> keyMap=new HashMap<String,Object>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }


    /**
     * 签名
     * @param
     * @param privateKey 密钥
     * @return byte[] 数字签名
     * */
    public static byte[] sign(byte[] data,byte[] privateKey) throws Exception{

        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey priKey=keyFactory.generatePrivate(pkcs8KeySpec);
        //实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        //初始化Signature
        signature.initSign(priKey);
        //更新
        signature.update(data);
        return signature.sign();
    }
    /**
     * 校验数字签名
     * @param data 待校验数据
     * @param publicKey 公钥
     * @param sign 数字签名
     * @return boolean 校验成功返回true，失败返回false
     * */
    public static boolean verify(byte[] data,byte[] publicKey,byte[] sign) throws Exception{
        //转换公钥材料
        //实例化密钥工厂
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(publicKey);
        //产生公钥
        PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);
        //实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        //初始化Signature
        signature.initVerify(pubKey);
        //更新
        signature.update(data);
        //验证
        return signature.verify(sign);
    }
    /**
     * 取得私钥
     * @param keyMap 密钥map
     * @return byte[] 私钥
     * */
    public static byte[] getPrivateKey(Map<String,Object> keyMap){
        Key key=(Key)keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }
    /**
     * 取得公钥
     * @param keyMap 密钥map
     * @return byte[] 公钥
     * */
    public static byte[] getPublicKey(Map<String,Object> keyMap) throws Exception{
        Key key=(Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }



    public static String getParamStr(String... param) {
        String params = "";
        for (String s : param) {
            params += s;
        }
        System.out.println("数据验签参数："+params);
        return params;
    }


    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data
     *            源数据
     * @param privateKey
     *            私钥(BASE64编码)
     * @return
     * @throws Exception
     */
//    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
//            throws Exception {
//        byte[] keyBytes = Coder.decryptBASE64(privateKey);
////    	byte[] keyBytes =org.opensaml.xml.util.Base64.decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.ENCRYPT_MODE, privateK);
//        int inputLen = data.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段加密
//        while (inputLen - offSet > 0) {
////        	System.out.println("加密长度"+(inputLen - offSet));
//            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
//                cache = cipher.doFinal(data, offSet,MAX_ENCRYPT_BLOCK);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_ENCRYPT_BLOCK;
//        }
//        byte[] encryptedData = out.toByteArray();
//        out.close();
//        return encryptedData;
//    }



//    /**
//     * <p>
//     * 公钥解密
//     * </p>
//     *
//     * @param encryptedData
//     *            已加密数据
//     * @param publicKey
//     *            公钥(BASE64编码)
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptByPublicKey(byte[] encryptedData,
//                                            String publicKey) throws Exception {
//        byte[] keyBytes = Coder.decryptBASE64(publicKey);
////        byte[] keyBytes =org.opensaml.xml.util.Base64.decode(publicKey);
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Key publicK = keyFactory.generatePublic(x509KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.DECRYPT_MODE, publicK);
//        int inputLen = encryptedData.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段解密
//        while (inputLen - offSet > 0) {
//            System.out.println("解密长度："+(inputLen - offSet));
//            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
//                cache = cipher
//                        .doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
//            } else {
//                cache = cipher
//                        .doFinal(encryptedData, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_DECRYPT_BLOCK;
//        }
//        byte[] decryptedData = out.toByteArray();
//        out.close();
//        return decryptedData;
//    }

    private static void generateKey() throws Exception{
        //初始化密钥
        //生成密钥对
        Map<String,Object> keyMap=RSAUtil.initKey();
        //公钥
        byte[] publicKey=RSAUtil.getPublicKey(keyMap);

        //私钥
        byte[] privateKey=RSAUtil.getPrivateKey(keyMap);
        System.out.println("公钥："+Base64.encodeBase64String(publicKey));
        System.out.println("私钥："+Base64.encodeBase64String(privateKey));
        System.out.println("================密钥对构造完毕,甲方将公钥公布给乙方，开始进行加密数据的传输=============");
        String str="RSA数字签名算法";
        System.out.println("原文:"+str);
        //甲方进行数据的加密
        byte[] sign=RSAUtil.sign(str.getBytes(), privateKey);
        System.out.println("产生签名："+Base64.encodeBase64String(sign));
        //验证签名
        str = "RSA数字签名算法";
        boolean status=RSAUtil.verify(str.getBytes(), publicKey, sign);
        System.out.println("状态："+status);
    }
}
