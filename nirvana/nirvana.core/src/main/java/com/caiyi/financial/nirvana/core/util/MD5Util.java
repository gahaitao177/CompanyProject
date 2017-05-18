package com.caiyi.financial.nirvana.core.util;

import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by heshaohua on 2016/5/31.
 */
public class MD5Util
{
    public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String compute(String paramString)
            throws Exception
    {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte1 = new byte[arrayOfChar.length];
        for (int i = 0; i < arrayOfChar.length; ++i)
            arrayOfByte1[i] = (byte)arrayOfChar[i];
        byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
        StringBuffer localStringBuffer = new StringBuffer();
        for (int j = 0; j < arrayOfByte2.length; ++j)
        {
            int k = arrayOfByte2[j] & 0xFF;
            if (k < 16)
                localStringBuffer.append("0");
            localStringBuffer.append(Integer.toHexString(k));
        }
        return localStringBuffer.toString();
    }

    public static String getHash(String paramString)
            throws Exception
    {
        int i = 0;
        FileInputStream localFileInputStream = new FileInputStream(paramString);
        byte[] arrayOfByte = new byte[1024];
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        int j = 0;
        while ((j = localFileInputStream.read(arrayOfByte)) > 0)
        {
            localMessageDigest.update(arrayOfByte, 0, j);
            ++i;
        }
        localFileInputStream.close();
        return toHexString(localMessageDigest.digest());
    }

    public static String toHexString(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
        for (int i = 0; i < paramArrayOfByte.length; ++i)
        {
            localStringBuffer.append(hexChar[((paramArrayOfByte[i] & 0xF0) >>> 4)]);
            localStringBuffer.append(hexChar[(paramArrayOfByte[i] & 0xF)]);
        }
        return localStringBuffer.toString();
    }

    public static void main(String[] paramArrayOfString)
            throws Exception
    {
        System.out.println(compute("1234567890").toUpperCase());
        System.out.println("conf.dir=" + System.getenv("conf.dir"));
    }
}
