package com.caiyi.nirvana.analyse.crypto.bouncycastle;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.security.MessageDigest;

/**
 * Created by been on 2017/1/10.
 */
public class BCTest {
    @Test
    public void test() throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
        //初始化MessageDigest
        MessageDigest md = MessageDigest.getInstance("MD5");
        MessageDigest bcMd = MessageDigest.getInstance("MD5", new BouncyCastleProvider());

        byte[] data1 = md.digest("been".getBytes());
        byte[] data2 = bcMd.digest("been".getBytes());
        System.out.println(Hex.encodeHexString(data1));
        System.out.println(Hex.encodeHexString(data2));
    }
}
