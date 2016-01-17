package com.siecom.tools;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zouhuaqiu
 * @date 2014-08-21
 * 
 */
@SuppressLint("DefaultLocale")
public class EncryptUnit {
	
	
	
	public static final String Algorithm3DES = "DESede";// 0
	public static final String AlgorithmDES  = "DES";// 1
	
	private byte[] masterKey;
	private byte[] workingKey;
	private String card_no;
	
	
	public EncryptUnit setMasterKey(byte[] masterKey){
		
		this.masterKey = masterKey;
		return this;
	}
    public EncryptUnit setWorkingKey(byte[] workingKey){
    	
    	
    	this.workingKey= workingKey; 
    	return this;
    }
    
    public EncryptUnit setString(String card_no){
    	
    	this.card_no =card_no;
    	return this;
    }
    
    public byte[] inputPIN(String PIN){
    	
        String pin_str  =	builtPINStr(PIN);
    	String card_str =   builtCardStr(card_no);
    	
    	byte[] real_workingKey = decryptMode(workingKey,Algorithm3DES, masterKey);
    	
    	byte[] ex 		= exclusive(pin_str,card_str);
    	
    	return encryptMode(ex, real_workingKey,Algorithm3DES);
    	
    }
    /**
     * 
     * @param miwen
     * @return
     */
    public String decrypt(byte[] miwen){
    	
    	byte[] real_workingKey = decryptMode(workingKey,Algorithm3DES, masterKey);
    	byte[] src      = decryptMode(miwen, Algorithm3DES,real_workingKey);
    	String card_str =   builtCardStr(card_no);
    	byte[]     tmp  = unexclusive(src,card_str);
    	String str      = byte2HexStr(tmp);
    	int length  	= Integer.valueOf(str.substring(0, 2));
    	
    	String pin      =  str.substring(2,length+2);
    	return pin;
    }
    
    private static String builtPINStr(String PIN){
    	
    	int length = PIN.length();
    	String len = String.valueOf(length);
    	len = (len.length()==1)?("0"+len):len;
    	StringBuffer  sb = new StringBuffer();
    	sb.append(len);
    	sb.append(PIN);
    	for(;sb.length()<16;){
    		sb.append("F");
    	}
    	System.out.println("pin:"+sb);
    	
    	return sb.toString();
    	
    }
   private static String builtCardStr(String card_no){
    	
    	StringBuffer  sb = new StringBuffer();
    	sb.append("0000");
    	StringBuffer card_buff = new StringBuffer(card_no);
    	card_buff = card_buff.reverse();
    	card_buff  = new StringBuffer(card_buff.substring(1, 13)).reverse();
    	char[] ch=card_buff.toString().toCharArray();
    	for(int i=0;sb.length()<16;i++){
    		sb.append(ch[i]);
    	}
    	return sb.toString();
    	
    }

	/**
	 * ʹ
	 * 
	 * @param src
	 * @param key
	 *            ��Կ
	 * @param Algorithm
	 *            �㷨
	 * @return
	 */
	@SuppressLint("TrulyRandom")
	public  static byte[] encryptMode(byte[] src, byte[] key, String Algorithm) {
		try {
			SecretKey deskey = new SecretKeySpec(
					Algorithm.equals(Algorithm3DES) ? build3DesKey(key)
							: build3DesKey(key), Algorithm); // ������?
			Cipher c1 = Cipher.getInstance("DESede/ECB/NoPadding");// ʵ�������?���ܵ�Cipher������
			c1.init(Cipher.ENCRYPT_MODE, deskey); // ��ʼ��Ϊ����ģʽ
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * 
	 * @param src
	 *           
	 * @return
	 */
	public static byte[] decryptMode(byte[] src, String Algorithm, byte[] key) {
		try {
			SecretKey deskey = new SecretKeySpec(build3DesKey(key), Algorithm);
			Cipher c1 = Cipher.getInstance("DESede/ECB/NoPadding");
			c1.init(Cipher.DECRYPT_MODE, deskey); // ��ʼ��Ϊ����ģʽ
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	/*
	 * 
	 * 
	 * @param keyStr ��Կ�ַ�
	 * 
	 * @return
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] build3DesKey(byte[] temp)
			throws UnsupportedEncodingException {
		byte[] key = new byte[24]; 
		System.arraycopy(temp, 0, key, 0, temp.length);
		
		for (int i = 0; i < 8; i++) {
			key[16 + i] = temp[i];
		}
		return key;
	}

	/*
	 * ʵ���ֽ�������ʮ����Ƶ�ת�������?
	 */
	@SuppressLint("DefaultLocale")
	public static String byte2HexStr(byte[] b) {
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

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	/**
	 * 
	 * @param PIN
	 * @param card
	 * @return
	 */
	private static byte[] exclusive(String PIN,String card){
		byte[] array = hexStringToBytes(PIN);
		byte[] array2= hexStringToBytes(card);
	     
        for(int i=0;i<array.length;i++)
        {
            array[i]=(byte) (array[i]^array2[i]);
           
        }
       return array;
		
	}
    /**
     * �����?
     * @param miwen
     * @param card
     * @return
     */
	private static byte[] unexclusive(byte[] miwen,String card){
		byte[] array2= hexStringToBytes(card);
		for(int i=0;i<miwen.length;i++)
	    {
			  miwen[i]=(byte) (miwen[i]^array2[i]);
	           
	    }
		return miwen;
	}
}
