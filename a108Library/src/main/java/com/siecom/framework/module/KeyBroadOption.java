package com.siecom.framework.module;

import java.io.Serializable;

public class KeyBroadOption implements Serializable {

	public final static int MAINKEY = 0;
	public final static int WORKKEY = 1;
	/**
	 * 为满足密钥更多需求改成对象传输
	 */
	private static final long serialVersionUID = 1L;
	private int key_type;// 0主密钥，1工作密钥
	private int wkey_no = 0;// 工作密钥编号默认是0
	private int mkey_no = 0;// 主密钥编号默认是0
	private byte[] keyData;
	public String cardNo = "";
	public String iAmount = "";
	public byte maxLen = 0x06;
    public int timeOut;

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getTimeOut() {

		return timeOut;
	}

	public void setMaxLen(byte maxLen) {
		this.maxLen = maxLen;
	}

	public byte getMaxLen() {
		return maxLen;
	}

	private byte mode = 0x00;//设置模式
	
	public  byte getMode(){
		
		return this.mode;
	}
	public void setMode(byte mode){
		this.mode = mode;
	}
	public int getMainKeyNo() {
		return mkey_no;
	}

	public void setMainKeyNo(int mkey_no) {
		this.mkey_no = mkey_no;
	}

	public int getWorkKeyNo() {
		return wkey_no;
	}
	public void setWorkKeyNo(int wkey_no) {
		this.wkey_no = wkey_no;
	}
	public byte[] getkeyData() {
		return keyData;
	}
	public void setkeyData(byte[] keyData) {
		this.keyData = keyData;
	}
	public void setType(int type) {
		this.key_type = type;
	}
	public int getType() {

		return key_type;
	}

}
