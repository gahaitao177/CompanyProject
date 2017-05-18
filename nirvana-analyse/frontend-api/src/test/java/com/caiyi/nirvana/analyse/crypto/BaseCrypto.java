package com.caiyi.nirvana.analyse.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by been on 2017/1/10.
 */
public abstract class BaseCrypto {
    public byte[] getUTF8Bytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }


    /**
     * Converts ByteBuffer to String
     *
     * @param buffer input byte buffer
     * @return the converted string
     */
    public String asString(ByteBuffer buffer) {
        final ByteBuffer copy = buffer.duplicate();
        final byte[] bytes = new byte[Math.min(copy.remaining(), 50)];
        copy.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
