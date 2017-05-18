package com.caiyi.financial.nirvana.discount.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public final class AESUtil {

	/** private constructor. */
	private AESUtil() {
	}

	/** the name of the transformation to create a cipher for. */
	private static final String TRANSFORMATION = "AES/ECB/PKCS7Padding";

	/** 算法名称 */
	private static final String ALGORITHM_NAME = "AES";

	/**
	 * aes 加密，AES/CBC/PKCS5Padding
	 * 
	 */
	public static byte[] encrypt(String cbcIv, String key, byte[] content)
			throws Exception {
		SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(),ALGORITHM_NAME);
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
		cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
		byte[] encrypted = cipher.doFinal(content);
		return encrypted;
	}

	/**
	 * aes 解密，AES/CBC/PKCS5Padding
	 * @return 明文
	 * @throws Exception
	 *             异常
	 */
	public static byte[] decrypt(String cbcIv, String key, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeSpect = new SecretKeySpec(key.getBytes(),ALGORITHM_NAME);
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
		cipher.init(Cipher.DECRYPT_MODE, skeSpect);
		byte[] decrypted = cipher.doFinal(encrypted);

		return decrypted;
	}

}
