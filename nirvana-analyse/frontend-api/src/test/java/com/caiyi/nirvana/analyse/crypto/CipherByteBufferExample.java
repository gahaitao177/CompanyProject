package com.caiyi.nirvana.analyse.crypto;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.utils.Utils;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by been on 2017/1/10.
 * ref http://commons.apache.org/proper/commons-crypto/xref-test/org/apache/commons/crypto/examples/CipherByteBufferExample.html
 * Example showing the CryptoCipher API using a ByteBuffer
 */
public class CipherByteBufferExample extends BaseCrypto {

    @Test
    public void test() throws Exception {
        final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"), "AES");
        final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
        Properties properties = new Properties();
        //Creates a CryptoCipher instance with the transformation and properties.
        final String transform = "AES/CBC/PKCS5Padding";
        final ByteBuffer outBuffer;
        final int bufferSize = 1024;
        final int updateBytes;
        final int finalBytes;
        try (CryptoCipher encipher = Utils.getCipherInstance(transform, properties)) {
            ByteBuffer inBuffer = ByteBuffer.allocateDirect(bufferSize);
            outBuffer = ByteBuffer.allocateDirect(bufferSize);
            inBuffer.flip();// ready for the cipher to read it
            System.out.println("inBuffer=" + asString(inBuffer));
            encipher.init(Cipher.ENCRYPT_MODE, key, iv);
            updateBytes = encipher.update(inBuffer, outBuffer);
            System.out.println(updateBytes);
            // We should call do final at the end of encryption/decryption.
            finalBytes = encipher.doFinal(inBuffer, outBuffer);
            System.out.println(finalBytes);

        }
        outBuffer.flip();
        byte[] encoded = new byte[updateBytes + finalBytes];
        outBuffer.duplicate().get(encoded);
        System.out.println(Arrays.toString(encoded));

        // Now reverse the process

        try (CryptoCipher decipher = Utils.getCipherInstance(transform, properties)) {
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            ByteBuffer decoded = ByteBuffer.allocateDirect(bufferSize);
            decipher.update(outBuffer, decoded);
            decipher.update(outBuffer, decoded);
            decoded.flip();
            System.out.println("decoded=" + asString(decoded));
        }
    }
}
