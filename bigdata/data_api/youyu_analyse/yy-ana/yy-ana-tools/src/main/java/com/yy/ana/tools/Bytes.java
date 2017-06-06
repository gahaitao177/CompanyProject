package com.yy.ana.tools;

import java.nio.charset.Charset;

/**
 * Created by User on 2017/5/26.
 */
public class Bytes {
    private static final String UTF8_ENCODING = "UTF-8";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /**
     * @param s
     * @return
     */
    public static byte[] toBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static long tolong(byte[] bytes) {
        return tolong(bytes, 0, 8);
    }

    public static long tolong(byte[] bytes, int offset, int length) {
        if (length == 8 && offset + length <= bytes.length) {
            long l = 0L;

            for (int i = offset; i < offset + length; ++i) {
                l <<= 8;
                l ^= (long) (bytes[i] & 255);
            }

            return l;
        } else {
            throw new RuntimeException("byte数组转long失败");
        }
    }

    public static String toString(byte[] b) {
        return b == null ? null : toString(b, 0, b.length);
    }

    public static String toString(byte[] b, int off, int len) {
        return b == null ? null : (len == 0 ? "" : new String(b, off, len, UTF8_CHARSET));
    }

}
