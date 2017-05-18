package com.caiyi.financial.nirvana.ccard.material.banks.guangfa;

/**
 * Created by lwg
 */
public class Unicode {
    public static String unicodeToGB(String str){
        str = str.replaceAll("&#x", "\\\\u");
        str = str.replaceAll(";", "");
        str = decode(str);
        return str;
    }

    public static String decode(String ascii) {
        ascii = ascii.replaceAll("&#x", "\\\\u");
        ascii = ascii.replaceAll(";", "");

        int n = ascii.length() / 6;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0, j = 2; i < n; i++, j += 6) {
            String code = ascii.substring(j, j + 4);
            char ch = (char) Integer.parseInt(code, 16);
            sb.append(ch);
        }
        return sb.toString();
    }
}
