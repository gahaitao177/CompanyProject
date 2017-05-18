package com.caiyi.financial.nirvana.discount.utils;

import com.caiyi.common.security.CaiyiEncrypt;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @Description:
 * @ClassName:
 * @date since 20140528 1:58:20
 * @author zhangnaiqi
 */
public final class CaiyiEncryptIOS {

	public CaiyiEncryptIOS() {
	}

	/**
	 *
	 *
	 * @param value
	 * @return
	 */
	public static String encryptStr(String value) {
		try {
			byte[] temp = AESUtil.encrypt("9188123123123345",
					"9188123123123345", value.getBytes("utf-8"));
			return Base64.encode(temp, "utf-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 *
	 * @param value
	 * @return
	 */
	public static String dencryptStr(String value) {
		try {
			byte[] temp = AESUtil.decrypt("9188123123123345",
					"9188123123123345", Base64.decode(value.getBytes("utf-8")));
			return new String(temp, "utf-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			NoSuchProviderException {
		System.out.println(dencryptStr("WQ7riXA6HOpJUJwQ6TvqhA=="));
		String mjiaStr = encryptStr("201587");
//		String mjiaStr = "NuV7uL/VnE7n7npVloPEmg==";
		System.out.println("加密后:" + mjiaStr);
		System.out.println("解密后:" + dencryptStr(mjiaStr));
		String mjiaStr2 = CaiyiEncrypt.encryptStr("201587");
		System.out.println(CaiyiEncrypt.dencryptStr(mjiaStr2));
	}
}