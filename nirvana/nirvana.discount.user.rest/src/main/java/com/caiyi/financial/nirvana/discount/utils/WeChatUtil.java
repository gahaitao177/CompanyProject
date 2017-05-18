package com.caiyi.financial.nirvana.discount.utils;

/**
 * Created by dengh on 2016/7/27.
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WeChatUtil {
    public static String token = "wwwhuishuakacom";

    public static String SHA1(String inStr) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = inStr.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        return strDes;
    }

    public static String bytetoString(byte[] digest) {
        String str = "";
        String tempStr = "";

        for (int i = 1; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                str = str + "0" + tempStr;
            } else {
                str = str + tempStr;
            }
        }
        return str.toLowerCase();
    }

    public static boolean checkSignature(String signature, String timestamp,String nonce) {
        try {
            String[] arr = new String[] { token, timestamp, nonce };
            Arrays.sort(arr);
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                content.append(arr[i]);
            }
            String temStr = SHA1(content.toString());
            System.out.println("加密后的结果：" + temStr);
            return temStr != null ? temStr.equalsIgnoreCase(signature) : false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public static void main(String[] args) {
        String timestamp = "1444810884";
        String nonce = "wxshare";

        String[] arr = new String[] { token, timestamp, nonce };
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        System.out.println(content);
        String temStr = SHA1(content.toString());
        System.out.println("加密后的结果：" + temStr);

        System.out.println(checkSignature("272f9deae5681550db083702c546347ef50d1b5b","1444810884","wxshare"));

    }


}