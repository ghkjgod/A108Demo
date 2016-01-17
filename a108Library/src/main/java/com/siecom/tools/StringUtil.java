package com.siecom.tools;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {
	public static int getTagLen(int start,String wantTag,String tagStr){
		int nIndex   = tagStr.indexOf(wantTag, start);
		int dataLen  = 0;
		if (nIndex != -1) {
			String len = tagStr.substring(nIndex + wantTag.length(),
					nIndex + wantTag.length()+2);
			dataLen = Integer.parseInt(len, 16);
			return dataLen;
		}
		return -1;
		
	}
	public static String tagParse(int start,String wantTag,String tagStr){
		String value = "";
		int nIndex   = tagStr.indexOf(wantTag, start);
		int dataLen  = 0;
		if (nIndex != -1) {
			String len = tagStr.substring(nIndex + wantTag.length(),
					nIndex + wantTag.length()+2);
			dataLen = Integer.parseInt(len, 16);
			value   = tagStr.substring(nIndex + wantTag.length()+2, nIndex + 2 + wantTag.length() + dataLen * 2);
		}
		return value;
	}
	public static String tagParse(String wantTag,String tagStr){
		String value = "";
		int nIndex   = tagStr.indexOf(wantTag);
		int dataLen  = 0;
		if (nIndex != -1) {
			String len = tagStr.substring(nIndex + wantTag.length(),
					nIndex + wantTag.length()+2);
			dataLen = Integer.parseInt(len, 16);
			value   = tagStr.substring(nIndex + wantTag.length()+2, nIndex + 2 + wantTag.length() + dataLen * 2);
		}
		return value;
	}
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		 String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		//String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	public static boolean stringHasNoChar(String s){
			for(int i=0;i<s.length();i++){
				if(!Character.isLetterOrDigit(s.charAt(i)))
					return true;
			}
			return false;
	}
	private StringUtil() {
		System.out.println("StringUtil Constructor");
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String bytesToHexString(byte[] src, int len) {
		StringBuilder stringBuilder = new StringBuilder("");
		if ((src == null) || (src.length <= 0)) {
			return null;
		}

		for (int i = 0; i < len; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}

		return stringBuilder.toString();
	}

	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString) {
		if ((hexString == null) || (hexString.equals(""))) {
			return null;
		}

		hexString = hexString.toUpperCase();

		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] by = new byte[length];

		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			by[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)]));
		}

		return by;
	}

	public static byte[] getBytesFromString(String src, String charset) {
		byte[] retByte = (byte[]) null;
		try {
			retByte = src.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return retByte;
	}

	public static String setBytesToString(byte[] src, String charset) {
		String retString = "";
		try {
			retString = new String(src, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return retString;
	}

	public static String UTF8ToGBK(String utf8String) {
		byte[] byGBK = (byte[]) null;

		byGBK = getBytesFromString(utf8String, "gbk");
		String gbkString = setBytesToString(byGBK, "gbk");

		return gbkString;
	}

	public static String GBKToUTF8(String gbkString) {
		String utf8String = "";
		byte[] byUTF8 = (byte[]) null;

		byUTF8 = getBytesFromString(gbkString, "utf-8");
		utf8String = setBytesToString(byUTF8, "utf-8");

		return utf8String;
	}

	public static void printBytes(byte[] b) {
		int length = b.length;
		System.out.print(String.format("length: %d, bytes: ",
				new Object[] { Integer.valueOf(length) }));
		for (int i = 0; i < length; i++) {
			System.out.print(String.format("%02X ",
					new Object[] { Byte.valueOf(b[i]) }));
		}

		System.out.println("");
	}

	public static void printBytes(byte[] b, int len) {
		System.out.print(String.format("length: %d, bytes: ",
				new Object[] { Integer.valueOf(len) }));
		for (int i = 0; i < len; i++) {
			System.out.print(String.format("%02X ",
					new Object[] { Byte.valueOf(b[i]) }));
		}

		System.out.println("");
	}
}