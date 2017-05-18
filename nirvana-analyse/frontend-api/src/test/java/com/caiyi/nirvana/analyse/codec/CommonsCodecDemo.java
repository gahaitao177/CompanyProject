package com.caiyi.nirvana.analyse.codec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by been on 2017/1/10.
 * commons codec api demo
 */
public class CommonsCodecDemo {

    @Test
    public void testBase() throws Exception {
        Base64 base64 = new Base64();
        String input = "伍家华";
        byte[] encoded = base64.encode(input.getBytes());
        System.out.println(new String(encoded));
        System.out.println(new String(base64.decode(encoded)));
    }


    @Test
    public void testHex() throws Exception {
        Hex hex = new Hex();
        String input = "伍家华";
        byte[] encoded = hex.encode(input.getBytes());
        System.out.println(new String(encoded));
        System.out.println(new String(hex.decode(encoded)));
    }


    @Test
    public void testMd5() throws Exception {
        String data = DigestUtils.md5Hex("wujiahua");

        System.out.println(data);
        System.out.println(DigestUtils.sha256Hex("wujiahua"));
        System.out.println(DigestUtils.md5Hex(new FileInputStream(new File("pom.xml"))));
        System.out.println(DigestUtils.sha256Hex(new FileInputStream(new File("pom.xml"))));

    }

    @Test
    public void test1() throws Exception {
        System.out.println("www.youwu.wo.com".getBytes().length);
        System.out.println(Hex.encodeHexString("been".getBytes()));
        byte[] bytes = "been".getBytes();
        for (byte aByte : bytes) {
            System.out.println(aByte);
        }
    }


}
