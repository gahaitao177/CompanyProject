package com.caiyi.financial.nirvana.discount.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class BankUtil {

	/**
	 * Returns a MessageDigest for the given <code>algorithm</code>.
	 *
	 * @param algorithm
	 *            The MessageDigest algorithm name.
	 * @return An MD5 digest instance.
	 * @throws RuntimeException
	 *             when a {@link NoSuchAlgorithmException} is
	 *             caught
	 */

	static MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element
	 * <code>byte[]</code>.
	 *
	 * @param data
	 *            Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(byte[] data) {
		return getDigest().digest(data);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element
	 * <code>byte[]</code>.
	 *
	 * @param data
	 *            Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(String data) {
		return md5(data.getBytes());
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex
	 * string.
	 *
	 * @param data
	 *            Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(byte[] data) {
		return toHexString(md5(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex
	 * string.
	 *
	 * @param data
	 *            Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(String data) {
		return toHexString(md5(data));
	}

	/**
	 * Converts a byte array to hex string.
	 *
	 * @param b -
	 *            the input byte array
	 * @return hex string representation of b.
	 */

	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_CHARS.charAt(b[i] >>> 4 & 0x0F));
			sb.append(HEX_CHARS.charAt(b[i] & 0x0F));
		}
		return sb.toString();
	}

	/**
	 * Converts a hex string into a byte array.
	 *
	 * @param s -
	 *            string to be converted
	 * @return byte array converted from s
	 */
	public static byte[] toByteArray(String s) {
		byte[] buf = new byte[s.length() / 2];
		int j = 0;
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) ((Character.digit(s.charAt(j++), 16) << 4) | Character
					.digit(s.charAt(j++), 16));
		}
		return buf;
	}

	private static final String HEX_CHARS = "0123456789abcdef";


	public static String appendParam(String returnStr, String paramId, String paramValue) {
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			if (!paramValue.equals("")) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}

	public static String getCookieParam(String cookie, String param){
		int begin = cookie.lastIndexOf(param);
		if (begin!=-1) {
			String beginStr = cookie.substring(begin);
			int end = beginStr.indexOf(";");
			if (end!=-1) {
				return beginStr.substring(0,end);
			}
		}
		return "";
	}

	public static String appendParam_all(String returnStr, String paramId, String paramValue) {
		if (!returnStr.equals("")) {
			returnStr = returnStr + "&" + paramId + "=" + paramValue;
		} else {
			returnStr = paramId + "=" + paramValue;
		}
		return returnStr;
	}

	/** 银行代号 */
	public static Map<String, String> bankInfoMap = new HashMap<String, String>();
	static {
		bankInfoMap.put("1","招商银行");
		bankInfoMap.put("2","工商银行");
		bankInfoMap.put("13","农业银行");
		bankInfoMap.put("3","建设银行");
		bankInfoMap.put("4","中国银行");
		bankInfoMap.put("6","交通银行");
		bankInfoMap.put("8","中信银行");
		bankInfoMap.put("9","兴业银行");
		bankInfoMap.put("10","光大银行");
		bankInfoMap.put("11","华夏银行");
		bankInfoMap.put("12","中国民生银行");
		bankInfoMap.put("25","中国储蓄银行");
		bankInfoMap.put("1000","广东发展银行");
		bankInfoMap.put("1001","深圳发展银行");
		bankInfoMap.put("4000","上海浦东发展银行");
		bankInfoMap.put("15","农村信用合作社");
		bankInfoMap.put("16","农村商业银行");
		bankInfoMap.put("17","农村合作银行");
		bankInfoMap.put("18","城市商业银行");
		bankInfoMap.put("19","城市信用合作社");
		bankInfoMap.put("23","平安银行");
		bankInfoMap.put("4001","上海银行");
		bankInfoMap.put("2000","北京银行");
		bankInfoMap.put("22","恒丰银行");
		bankInfoMap.put("24","渤海银行");
		bankInfoMap.put("1002","广州银行");
		bankInfoMap.put("1003","珠海南通银行");
		bankInfoMap.put("3000","天津银行");
		bankInfoMap.put("5000","浙商银行");
		bankInfoMap.put("5001","浙江商业银行");
		bankInfoMap.put("5002","宁波国际银行");
		bankInfoMap.put("5003","宁波银行");
		bankInfoMap.put("5004","温州银行");
		bankInfoMap.put("6000","南京银行");
		bankInfoMap.put("6001","常熟农村商业银行");
		bankInfoMap.put("7000","福建亚洲银行");
		bankInfoMap.put("7001","福建兴业银行");
		bankInfoMap.put("7002","徽商银行");
		bankInfoMap.put("7003","厦门国际银行");
		bankInfoMap.put("8000","青岛市商业银行");
		bankInfoMap.put("8001","济南市商业银行");
		bankInfoMap.put("9000","重庆银行");
		bankInfoMap.put("10000","成都市商业银行");
		bankInfoMap.put("11000","哈尔滨银行");
		bankInfoMap.put("12000","包头市商业银行");
		bankInfoMap.put("13000","南昌市商业银行");
		bankInfoMap.put("14000","贵阳商业银行");
		bankInfoMap.put("15000","兰州市商业银行");
	}
	public static String jiami(char[] a,char[] b,String newValue){
		int jiami = 0;
		int keyjiami = 0;
		String afterPass = "";
		int specialChar = 0;
		int ifUseYinshe = 1;
		if (keyjiami == 0 && jiami == 0 && ifUseYinshe == 1) {
			char everyone ;
			for (int i=0;i<newValue.length();i++ ) {
				if (specialChar == 1) {
					break;
				}
				everyone = newValue.charAt(i);
				for (int j =0;j<((b.length)/2);j++) {
					if (everyone == b[2*j]) {
						afterPass = afterPass + b[2*j+1];
						break;
					}
					if (j == (b.length)/2 - 1) {
						if (everyone != b[2*j]) {
							specialChar = 1;
							break;
						}
					}
				}
			}
			if (specialChar == 0) {
				System.out.println("afterPass="+afterPass);
			}else {
				String ret = "";
				afterPass = "";
				for(int i=0;i<newValue.length();i++) {
					String c = newValue.substring(i, i+1);
					String ts =EscapeUnescape.escape(c);
//					if(ts.substring(0,2) == "%u") {
					if("%u" .equals(ts.substring(0,2))) {
						ret = ret + ts.replace("%u","(^?)");
					} else {
						ret = ret + c;
					}
				}
				System.out.println("ret="+ret);
				for (int n=0;n<ret.length();n++ ) {
					everyone = ret.charAt(n);
					for (int w =0;w<((b.length)/2);w++) {
						if (everyone == b[2*w]) {
							afterPass = afterPass + b[2*w+1];
							break;
						}
					}
				}
				System.out.println("afterPass="+afterPass);
			}
			keyjiami = 1;
		}
		return afterPass;
	}
	public static String getURLParam(String cookie, String param){
		int begin = cookie.lastIndexOf(param);
		if (begin!=-1) {
			String beginStr = cookie.substring(begin);
			int end = beginStr.indexOf("&");
			if (end!=-1) {
				return beginStr.substring(beginStr.indexOf("=")+1,end);
			}
			if (beginStr.indexOf("=")!=-1) {
				return beginStr.substring(beginStr.indexOf("=")+1);
			}
		}
		return "";
	}
}