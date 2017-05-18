package com.caiyi.nirvana.analyse.crypto;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.utils.Utils;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by been on 2017/1/10.
 * ref http://commons.apache.org/proper/commons-crypto/xref-test/org/apache/commons/crypto/examples/CipherByteArrayExample.html
 * Example showing use of the CryptoCipher API using a byte array
 */
public class CipherByteArrayExample extends BaseCrypto {

    @Test
    public void test() throws Exception {
        SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"), "AES");
        IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
        Properties props = new Properties();
        props.put(CryptoCipherFactory.CLASSES_KEY, CryptoCipherFactory.CipherProvider.JCE.getClassName());
        //Creates a CryptoCipher instance with the transformation and properties.
        String transfrom = "AES/CBC/PKCS5Padding";
        CryptoCipher encipher = Utils.getCipherInstance(transfrom, props);
        String sampleInput = "Hello, World!";
        System.out.println(sampleInput);
        byte[] input = getUTF8Bytes(sampleInput);
        byte[] output = new byte[32];
        //Initializes the cipher with ENCRYPT_MODE, key and iv.
        encipher.init(Cipher.ENCRYPT_MODE, key, iv);
        //Continues a multiple-part encryption/decryption operation for byte array.
        int updateBytes = encipher.update(input, 0, input.length, output, 0);
        System.out.println(updateBytes);
        //We must call doFinal at the end of encryption/decryption.
        int finalBytes = encipher.doFinal(input, 0, 0, output, updateBytes);
        System.out.println(finalBytes);
        //Closes the cipher.
        encipher.close();
        System.out.println(Arrays.toString(Arrays.copyOf(output, updateBytes + finalBytes)));


        // Now reverse the process using a different implementation with the same settings

        props.put(CryptoCipherFactory.CLASSES_KEY, CryptoCipherFactory.CipherProvider.JCE.getClassName());
        CryptoCipher decipher = Utils.getCipherInstance(transfrom, props);
        System.out.println("Cipher:  " + encipher.getClass().getCanonicalName());
        decipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decoded = new byte[32];
        decipher.doFinal(output, 0, updateBytes + finalBytes, decoded, 0);
        System.out.println("output: " + new String(decoded, StandardCharsets.UTF_8));


    }


}
