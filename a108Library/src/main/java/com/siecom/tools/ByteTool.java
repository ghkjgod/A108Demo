package com.siecom.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.siecom.framework.bean.TradeLogBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ByteTool {

	public static byte[] toHexByte(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return baKeyword;
	}
	public static List<TradeLogBean> parseLog(byte[] log){
		int num = log[0];
		List<TradeLogBean> list  = new ArrayList<>();
		for (int i=0;i<num;i++){
			try {
				TradeLogBean bean = new TradeLogBean();
				byte[] tmp = new byte[6];
				System.arraycopy(log, 1 + i * 45, tmp, 0, 6);
				bean.setAmount(ByteTool.byte2hex(tmp));
				System.arraycopy(log, 1 + 6 + i * 45, tmp, 0, 6);
				bean.setOtherAmount(ByteTool.byte2hex(tmp));
				tmp = new byte[2];
				System.arraycopy(log, 1 + 6 + 6 + i * 45, tmp, 0, 2);
				bean.setTransNo(ByteTool.byteArray2int(tmp));
				tmp = new byte[1];
				System.arraycopy(log, 1 + 6 + 6 + 2 + i * 45, tmp, 0, 1);
				bean.setTransType(ByteTool.byte2hex(tmp));
				tmp = new byte[2];
				System.arraycopy(log, 1 + 6 + 6 + 2 + 1 + i * 45, tmp, 0, 2);
				bean.setCountryCode(ByteTool.byte2hex(tmp));

				tmp = new byte[2];
				System.arraycopy(log, 1 + 6 + 6 + 2 + 1 + 2 + i * 45, tmp, 0, 2);
				bean.setTransCurrCode(ByteTool.byte2hex(tmp));

				tmp = new byte[3];
				System.arraycopy(log, 1 + 6 + 6 + 2 + 1 + 2 + 2 + i * 45, tmp, 0, 3);
				bean.setDate(ByteTool.byte2hex(tmp));

				tmp = new byte[3];
				System.arraycopy(log, 1 + 6 + 6 + 2 + 1 + 2 + 2 +3+ i * 45, tmp, 0, 3);
				bean.setTime(ByteTool.byte2hex(tmp));
				tmp = new byte[20];
				System.arraycopy(log, 1 + 6 + 6 + 2 + 1 + 2 + 2 +3+3+ i * 45, tmp, 0, 20);
				bean.setMerchName(new String(tmp).trim());

				list.add(bean);
			}catch (ArrayIndexOutOfBoundsException e){
				break;
			}

		}
		return list;

	}


	// 把公钥 Modul值没有248 位字节的 全部用0代替补充至248字节 GJ 2015/7/20
	public static byte[] ComplementaryData(byte[] datas) {
		byte[] data = new byte[284];
		int j = 0;
		for (int i = 0; i < 284; i++) {
			if (i == 8) {
				if ((datas[8] & 0x0FF) != 248) {
					byte[] modul = new byte[248];
					System.arraycopy(datas, 9, modul, 0, (datas[8] & 0x0FF));
					System.arraycopy(modul, 0, data, 9, modul.length);
					i = 256;
					j = 9 + (datas[8] & 0x0FF);
					data[8] = (byte) (datas[8] & 0x0FF);
					continue;
				}
			}
			if (j == 0) {
				data[i] = datas[i];
			} else {
				data[i] = datas[j];
				j++;
			}
		}
		return data;

	}

	public static int getBytesLength(byte[] content) {
		int i = 0;
		for (int j = 0; j < content.length; j++) {
			if (content[j] != 0)
				i++;
			else
				break;
		}
		return i;

	}

	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	public static byte[] int2Bytes(int i) {
		byte[] result = new byte[2];
		result[0] = (byte) ((i >> 8) & 0xFF);
		result[1] = (byte) (i & 0xFF);
		return result;
	}
	public static String bytearrayToHexString0(byte[] b, int leng) {

		StringBuffer strbuf = new StringBuffer();

		for (int i = 0; i < leng; i++) {

			strbuf.append("0123456789ABCDEF"
					.charAt(((byte) ((b[i] & 0xf0) >> 4))));

			strbuf.append("0123456789ABCDEF".charAt((byte) (b[i] & 0x0f)));
		}
		return strbuf.toString();
	}
	public static byte[] intTo4bytes(int i) {
		byte[] a = new byte[4];
		a[0] = (byte) ((0xff000000 & i) >> 24);
		a[1] = (byte) ((0xff0000 & i) >> 16);
		a[2] = (byte) ((0xff00 & i) >> 8);
		a[3] = (byte) (0xff & i);

		return a;

	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";

		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;

		}

		return hs.toUpperCase();
	}

	public static byte[] int2OneByte(int num) {
		byte[] result = new byte[1];
		result[0] = (byte) (num & 0x000000ff);
		return result;
	}

	public static byte[] hexStr2Bytes(String src) {
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {

			ret[i] = (byte) Integer.parseInt(src.substring(i * 2, i * 2 + 2),
					16);
		}
		return ret;
	}

	public static byte[] streamCopy(List<byte[]> srcArrays) {
		byte[] destAray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			for (byte[] srcArray : srcArrays) {

				bos.write(srcArray);
			}
			bos.flush();
			destAray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		return destAray;
	}

	/**
	 * 获得流
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] getBytes(String s) {
		int len = s.length();

		byte[] destAray = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int i = 0; i < len; i++) {
			int v = s.charAt(i);
			bos.write((byte) ((v >>> 8) & 0xFF));
			bos.write((byte) ((v >>> 0) & 0xFF));
		}
		try {
			bos.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		destAray = bos.toByteArray();
		try {
			bos.close();
		} catch (IOException e) {
		}

		return destAray;

	}

	/**
	 * 16进制转二进制
	 * 
	 * @param hexString
	 * @return
	 */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
							hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * 16进制String数组转 byte数组组成的String
	 * 
	 * @param all
	 * @return
	 */

	public static String HexStringArrToString(String[] all) {

		int length = all.length;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) Integer.parseInt(all[i], 16);
		}
		return new String(bytes);
	}

	/**
	 * byte2int
	 * 
	 * @param b
	 * @param offset
	 * @return
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;

		}
		return value;
	}

	public static int byteArray2int(byte[] b) {
		byte[] a = new byte[4];
		int i = a.length - 1, j = b.length - 1;
		for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
			if (j >= 0)
				a[i] = b[j];
			else
				a[i] = 0;// 如果b.length不足4,则将高位补0
		}
		int v0 = (a[0] & 0xff) << 24;// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		int v1 = (a[1] & 0xff) << 16;
		int v2 = (a[2] & 0xff) << 8;
		int v3 = (a[3] & 0xff);
		return v0 + v1 + v2 + v3;
	}

	public static String bytesToString(byte[] b) {
		StringBuffer result = new StringBuffer("");
		int length = b.length;

		for (int i = 0; i < length; i++) {
			char ch = (char)(b[i] & 0xFF);
			if (ch == 0)
			{
				break;
			}
			result.append(ch);
		}

		return result.toString();
	}



	public static String bytearrayToHexString(byte[] b, int leng) {
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i < leng; i++) {
			strbuf.append("0123456789ABCDEF"
					.charAt((byte)((b[i] & 0xF0) >> 4)));
			strbuf.append("0123456789ABCDEF".charAt((byte)(b[i] & 0xF)));
			strbuf.append(" ");
		}
		return strbuf.toString();
	}
	public static String bytearrayToString(byte[] b, int leng) {
		StringBuffer strbuf = new StringBuffer();

		for (int i = 0; i < leng; i++) {
			strbuf.append("0123456789ABCDEF".charAt((byte)(b[i] & 0xF)));
			strbuf.append(" ");
		}
		return strbuf.toString();
	}

	public static byte[] stringToBytes(String s) {
		return s.getBytes();
	}

	public static void ShortToBytes(byte[] b, short x, int offset)
	{
		if (b.length - offset >= 2) {
			b[(offset + 1)] = (byte)(x >> 8);
			b[(offset + 0)] = (byte)(x >> 0);
		}
	}

	public static short BytesToShort(byte[] b, int offset)
	{
		short x = 0;
		if (b.length - offset >= 2) {
			x = (short)(b[(offset + 1)] << 8 | b[(offset + 0)] & 0xFF);
		}

		return x;
	}

	public static String byteToHexString(byte b)
	{
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("0123456789ABCDEF".charAt(b >> 4 & 0xF));
		sbBuffer.append("0123456789ABCDEF".charAt(b & 0xF));
		return sbBuffer.toString();
	}

	public static void IntToBytes(byte[] b, int x, int offset)
	{
		if (b.length - offset >= 4) {
			b[(offset + 3)] = (byte)(x >> 24);
			b[(offset + 2)] = (byte)(x >> 16);
			b[(offset + 1)] = (byte)(x >> 8);
			b[(offset + 0)] = (byte)(x >> 0);
		}
	}

	public static int BytesToInt(byte[] b, int offset)
	{
		int x = 0;
		if (b.length - offset >= 4) {
			x = (b[(offset + 3)] & 0xFF) << 24 |
					(b[(offset + 2)] & 0xFF) << 16 |
					(b[(offset + 1)] & 0xFF) << 8 | (b[(offset + 0)] & 0xFF) << 0;
		}

		return x;
	}

	public static void LongToBytes(byte[] b, long x, int offset)
	{
		if (b.length - offset >= 8) {
			b[(offset + 7)] = (byte)(int)(x >> 56);
			b[(offset + 6)] = (byte)(int)(x >> 48);
			b[(offset + 5)] = (byte)(int)(x >> 40);
			b[(offset + 4)] = (byte)(int)(x >> 32);
			b[(offset + 3)] = (byte)(int)(x >> 24);
			b[(offset + 2)] = (byte)(int)(x >> 16);
			b[(offset + 1)] = (byte)(int)(x >> 8);
			b[(offset + 0)] = (byte)(int)(x >> 0);
		}
	}

	public static long BytesToLong(byte[] b, int offset)
	{
		long x = 0L;
		if (b.length - offset >= 8) {
			x = (b[(offset + 7)] & 0xFF) << 56 |
					(b[(offset + 6)] & 0xFF) << 48 |
					(b[(offset + 5)] & 0xFF) << 40 |
					(b[(offset + 4)] & 0xFF) << 32 |
					(b[(offset + 3)] & 0xFF) << 24 |
					(b[(offset + 2)] & 0xFF) << 16 |
					(b[(offset + 1)] & 0xFF) << 8 | (b[(offset + 0)] & 0xFF) << 0;
		}

		return x;
	}

	public static void CharToBytes(byte[] b, char ch, int offset)
	{
		if (b.length - offset >= 2) {
			int temp = ch;
			for (int i = 0; i < 2; i++) {
				b[(offset + i)] = new Integer(temp & 0xFF).byteValue();
				temp >>= 8;
			}
		}
	}

	public static char BytesToChar(byte[] b, int offset)
	{
		int s = 0;

		if (b.length - offset >= 2) {
			if (b[(offset + 1)] > 0)
				s += b[(offset + 1)];
			else
				s += 256 + b[(offset + 0)];
			s *= 256;
			if (b[(offset + 0)] > 0)
				s += b[(offset + 1)];
			else {
				s += 256 + b[(offset + 0)];
			}
		}
		char ch = (char)s;
		return ch;
	}

	public static void FloatToBytes(byte[] b, float x, int offset)
	{
		if (b.length - offset >= 4) {
			int l = Float.floatToIntBits(x);
			for (int i = 0; i < 4; i++) {
				b[(offset + i)] = new Integer(l).byteValue();
				l >>= 8;
			}
		}
	}

	public static float BytesToFloat(byte[] b, int offset)
	{
		int l = 0;

		if (b.length - offset >= 4) {
			l = b[(offset + 0)];
			l &= 255;
			l = (int)(l | b[(offset + 1)] << 8);
			l &= 65535;
			l = (int)(l | b[(offset + 2)] << 16);
			l &= 16777215;
			l = (int)(l | b[(offset + 3)] << 24);
		}

		return Float.intBitsToFloat(l);
	}

	public static void DoubleToBytes(byte[] b, double x, int offset)
	{
		if (b.length - offset >= 8) {
			long l = Double.doubleToLongBits(x);
			for (int i = 0; i < 4; i++) {
				b[(offset + i)] = new Long(l).byteValue();
				l >>= 8;
			}
		}
	}

	public static double BytesToDouble(byte[] b, int offset)
	{
		long l = 0L;

		if (b.length - offset >= 8) {
			l = b[0];
			l &= 255L;
			l |= b[1] << 8;
			l &= 65535L;
			l |= b[2] << 16;
			l &= 16777215L;
			l |= b[3] << 24;
			l &= 4294967295L;
			l |= b[4] << 32;
			l &= 1099511627775L;
			l |= b[5] << 40;
			l &= 281474976710655L;
			l |= b[6] << 48;
			l &= 72057594037927935L;
			l |= b[7] << 56;
		}

		return Double.longBitsToDouble(l);
	}

	public static short toLH(short n)
	{
		byte[] b = new byte[2];
		b[0] = (byte)(n & 0xFF);
		b[1] = (byte)(n >> 8 & 0xFF);

		short ret = BytesToShort(b, 0);
		return ret;
	}

	public static short toHL(short n)
	{
		byte[] b = new byte[2];
		b[1] = (byte)(n & 0xFF);
		b[0] = (byte)(n >> 8 & 0xFF);

		short ret = BytesToShort(b, 0);
		return ret;
	}

	public static int toLH(int n)
	{
		byte[] b = new byte[4];
		b[0] = (byte)(n & 0xFF);
		b[1] = (byte)(n >> 8 & 0xFF);
		b[2] = (byte)(n >> 16 & 0xFF);
		b[3] = (byte)(n >> 24 & 0xFF);

		int ret = BytesToInt(b, 0);
		return ret;
	}

	public static int toHL(int n)
	{
		byte[] b = new byte[4];
		b[3] = (byte)(n & 0xFF);
		b[2] = (byte)(n >> 8 & 0xFF);
		b[1] = (byte)(n >> 16 & 0xFF);
		b[0] = (byte)(n >> 24 & 0xFF);

		int ret = BytesToInt(b, 0);
		return ret;
	}

	public static long toLH(long n)
	{
		byte[] b = new byte[8];
		b[0] = (byte)(int)(n & 0xFF);
		b[1] = (byte)(int)(n >> 8 & 0xFF);
		b[2] = (byte)(int)(n >> 16 & 0xFF);
		b[3] = (byte)(int)(n >> 24 & 0xFF);
		b[4] = (byte)(int)(n >> 32 & 0xFF);
		b[5] = (byte)(int)(n >> 40 & 0xFF);
		b[6] = (byte)(int)(n >> 48 & 0xFF);
		b[7] = (byte)(int)(n >> 56 & 0xFF);

		long ret = BytesToLong(b, 0);
		return ret;
	}

	public static long toHL(long n)
	{
		byte[] b = new byte[8];
		b[7] = (byte)(int)(n & 0xFF);
		b[6] = (byte)(int)(n >> 8 & 0xFF);
		b[5] = (byte)(int)(n >> 16 & 0xFF);
		b[4] = (byte)(int)(n >> 24 & 0xFF);
		b[3] = (byte)(int)(n >> 32 & 0xFF);
		b[2] = (byte)(int)(n >> 40 & 0xFF);
		b[1] = (byte)(int)(n >> 48 & 0xFF);
		b[0] = (byte)(int)(n >> 56 & 0xFF);

		long ret = BytesToLong(b, 0);
		return ret;
	}

	public static void encodeOutputBytes(byte[] b, short sLen)
	{
		if (b.length >= sLen + 2) {
			System.arraycopy(b, 0, b, 2, sLen);
			byte[] byShort = new byte[2];
			ShortToBytes(byShort, sLen, 0);
			System.arraycopy(byShort, 0, b, 0, byShort.length);
		}
	}

	public static short decodeOutputBytes(byte[] b)
	{
		byte[] byShort = new byte[2];
		System.arraycopy(b, 0, byShort, 0, byShort.length);
		short sLen = BytesToShort(byShort, 0);

		System.arraycopy(b, 2, b, 0, sLen);

		return sLen;
	}
	public static byte[] hexStringToBytes(String hex) 
	{
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();

		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}		
		return result;
	}

	public static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * ???????16?????????
	 * 
	 * @param b
	 * @return
	 */
	public static String bytesToHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

	/**
	 * ??????????????????????
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * ??????????????????????
	 */
	public static String str2Hexstr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	public static String byte2Hexstr(byte b) {
		String temp = Integer.toHexString(0xFF & b);
		if (temp.length() < 2) {
			temp = "0" + temp;
		}
		temp = temp.toUpperCase();
		return temp;
	}

	public static String str2Hexstr(String str, int size) {
		byte[] byteStr = str.getBytes();
		byte[] temp = new byte[size];
		System.arraycopy(byteStr, 0, temp, 0, byteStr.length);
		temp[size - 1] = (byte) byteStr.length;
		String hexStr = bytesToHexString(temp);
		return hexStr;
	}

	/**
	 * 16?????????????????飬???32??16???????????16???
	 * 
	 * @param str
	 * @return
	 */
	public static String[] hexStr2StrArray(String str) {
		// 32?????????????????16???
		int len = 32;
		int size = str.length() % len == 0 ? str.length() / len : str.length()
				/ len + 1;
		String[] strs = new String[size];
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				String temp = str.substring(i * len);
				for (int j = 0; j < len - temp.length(); j++) {
					temp = temp + "0";
				}
				strs[i] = temp;
			} else {
				strs[i] = str.substring(i * len, (i + 1) * len);
			}
		}
		return strs;
	}

	/**
	 * ??16????????????????????飬???????????????16?????????
	 * 
	 * @param hexstr
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		return out.toByteArray();
	}

	/**
	 * ??16?????????????????????????飬???????????????16?????????
	 * 
	 * @param hexstr
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] data) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	public static byte[] short2byte(short s) {
		byte[] size = new byte[2];
		size[0] = (byte) (s >>> 8);
		short temp = (short) (s << 8);
		size[1] = (byte) (temp >>> 8);

		// size[0] = (byte) ((s >> 8) & 0xff);
		// size[1] = (byte) (s & 0x00ff);
		return size;
	}

	public static short[] hexStr2short(String hexStr) {
		byte[] data = hexStringToBytes(hexStr);
		short[] size = new short[4];
		for (int i = 0; i < size.length; i++) {
			size[i] = getShort(data[i * 2], data[i * 2 + 1]);
		}
		return size;
	}

	public static short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}
	
	/**
	 * bitmap??base64
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * base64??bitmap
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
}
