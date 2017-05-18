package com.caiyi.nirvana.analyse.crypto;

import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by been on 2017/1/10.
 * ref http://commons.apache.org/proper/commons-crypto/xref-test/org/apache/commons/crypto/examples/RandomExample.html
 * Example showing use of the CryptoRandom API
 */
public class RandomExample {

    @Test
    public void test() throws Exception {
        byte[] key = new byte[16];
        byte[] iv = new byte[32];
        Properties props = new Properties();
        props.put(CryptoRandomFactory.CLASSES_KEY, CryptoRandomFactory.RandomProvider.OS.getClassName());
        try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(props)) {
            System.out.println(random.getClass().getCanonicalName());
            random.nextBytes(key);
            random.nextBytes(iv);
        }
        System.out.println(Arrays.toString(key));
        System.out.println(Arrays.toString(iv));
    }
}
