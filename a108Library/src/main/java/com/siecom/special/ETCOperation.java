package com.siecom.special;

import android.util.Log;

import com.siecom.framework.appinterface.APDU_RESP;
import com.siecom.framework.appinterface.APDU_SEND;
import com.siecom.framework.appinterface.Api;
import com.siecom.tools.ByteTool;


public class ETCOperation {
	
	private static ETCOperation module = new ETCOperation();
	
	private Api api;

	private ETCOperation() {
		api = new Api();

	}

	public static ETCOperation getInstance() {

		return module;

	}
	//卡片上电
	public int CardOpen(){
		byte[] ATR = new byte[100];
		int ret = api.Icc_Open((byte) 0x00, (byte) 0x01, ATR);
		return ret;
	}
	//检测卡片
	public int checkCard(){
		int iret = api.Icc_Detect((byte) 0x00);
		return iret;
	}
	//读取卡片发行基本数据 0015文件
	public int ReadBaseCardInfo(byte[] info){
		byte[] cmd = ByteTool.hexStr2Bytes("00A40000");
		byte[] sendData = ByteTool.hexStr2Bytes("3F00");
		short Lc = 0x02;
		short Le = 256;
		APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		APDU_RESP ApduResp = new APDU_RESP();
		int ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "OpenMain: ret = " + ret);
			return ret;
		}
		byte[] bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf", ByteTool.byte2hex(bf));
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){	
			return -901;
		}
		cmd = ByteTool.hexStr2Bytes("00A40000");
		sendData = ByteTool.hexStr2Bytes("1001");
		Lc = 0x02;
		Le = 256;
		ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		ApduResp = new APDU_RESP();
		ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "selectCardNo: ret = " + ret);
			return ret;
		}
		bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf2", ByteTool.byte2hex(bf));
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
		   return -902;		
		}
		cmd = ByteTool.hexStr2Bytes("00B09500");
		sendData = ByteTool.hexStr2Bytes("");
		Lc = 0x00;
		Le = 0x2b;
		ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		ApduResp = new APDU_RESP();
		ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "ret = " + ret);
			return ret;
		}
		bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf4", ByteTool.byte2hex(bf)+"===len:"+bf.length);
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
			   return -903;		
		}
		System.arraycopy(bf, 0, info, 0, ApduResp.LenOut);		
		
		return 0;
	}
	// 读取随机数
	public int readRand(byte[] rand){
		byte[]  cmd = ByteTool.hexStr2Bytes("00840000");
		byte[]  sendData = ByteTool.hexStr2Bytes("");
		short Lc = 0x00;
		short Le = 04;
		APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		APDU_RESP ApduResp = new APDU_RESP();
		int ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "selectCardNo: ret = " + ret);
			return ret;
		}
		byte[] bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf5", ByteTool.byte2hex(bf)+"===len:"+bf.length);
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
			   return -904;		
		}
		System.arraycopy(bf, 0, rand, 0, ApduResp.LenOut);	
		return 0;
	}
	/*
	 * 更新基本信息 0015 报文
	 * 04D695002FB0B2BBD5340100011710230310302302000472302015111120251110CDEE41433832303200000000000003F204C3C4
	 */
	public int updateBaseInfo(String data){
		byte[] b = ByteTool.hexStr2Bytes(data);
		byte[]  cmd = new byte[4];
		System.arraycopy(b, 0, cmd, 0, 4);//命令头 04D69500
		int length = ((int) b[4] < 0) ? ((int) b[4] + 256) : (int) b[4]; 
		byte[]  sendData = new byte[length]; //长度
		System.arraycopy(b, 5, sendData, 0, length);//命令头 04D69500
		short Lc = (short) length;
		short Le = 256;
		APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		APDU_RESP ApduResp = new APDU_RESP();
		int ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "selectCardNo: ret = " + ret);
			return ret;
		}
		byte[] bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf5", ByteTool.byte2hex(bf)+"===len:"+bf.length);
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
			   return -904;		
		}
		return 0;
		
	}
	/**
	 * 读取所持人信息 0016文件
	 * @param info
	 * @return
	 */
	public int readUserInfo(byte[] info){
		byte[] cmd = ByteTool.hexStr2Bytes("00A40000");
		byte[] sendData = ByteTool.hexStr2Bytes("3F00");
		short Lc = 0x02;
		short Le = 256;
		APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		APDU_RESP ApduResp = new APDU_RESP();
		int ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "OpenMain: ret = " + ret);
			return ret;
		}
		byte[] bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf", ByteTool.byte2hex(bf));
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){	
			return -901;
		}

		
		cmd = ByteTool.hexStr2Bytes("00B09600");
		sendData = ByteTool.hexStr2Bytes("");
		Lc = 0x00;
		Le = 0x37;
		ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		ApduResp = new APDU_RESP();
		ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "ret = " + ret);
			return ret;
		}
		bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf4", ByteTool.byte2hex(bf)+"===len:"+bf.length);
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
			   return -903;		
		}
		System.arraycopy(bf, 0, info, 0, ApduResp.LenOut);		
		
		return 0;
		
	}
	/**
	 * 更新用户信息的报文 0016
	 * 04D696003B0202C5CBBFA5C5CBBFA5C5CBBFA5C5CBBFA5C5CBBFA5333430313033313939313036313732303136000000000000000000000000000000829E7D84
	 * @param data
	 * @return
	 */
	public int updateUserInfo(String data){
		
		byte[] b = ByteTool.hexStr2Bytes(data);
		byte[]  cmd = new byte[4];
		System.arraycopy(b, 0, cmd, 0, 4);//命令头 04D69500
		int length = ((int) b[4] < 0) ? ((int) b[4] + 256) : (int) b[4]; 
		byte[]  sendData = new byte[length]; //长度
		System.arraycopy(b, 5, sendData, 0, length);//命令头 04D69500
		short Lc = (short) length;
		short Le = 256;
		APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
		APDU_RESP ApduResp = new APDU_RESP();
		int ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
		if (ret != 0) {
			Log.e("ret", "selectCardNo: ret = " + ret);
			return ret;
		}
		byte[] bf = new byte[ApduResp.LenOut];
		System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
		Log.e("bf5", ByteTool.byte2hex(bf)+"===len:"+bf.length);
		Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
		Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
		if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
			   return -904;		
		}
		return 0;
		
		
	}
	
	public void close(){
		
		api.Icc_Close((byte)0x00);
	}
	
	
	
}
