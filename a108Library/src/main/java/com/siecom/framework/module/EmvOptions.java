package com.siecom.framework.module;

import java.io.Serializable;

import android.util.Log;

import com.siecom.tools.ByteTool;


public class EmvOptions implements Serializable {

	private static final long serialVersionUID = 1L;
	public byte[] datatime;
	public byte[] EMV_PARAM;
	public byte cardType = 0x00;
	public int authorizedAmount = 0;
	public int cashBack  = 0;
	public String[] tags = { "9F26", "9F27", "9F10", "9F02", "5F2A", "82", "9F1A", "9F03", "9F33", "9F34", "9F35",
			"9F1E", "84", "9F09", "9F41", "9F63" };
	public int TransNo = 1;

	public void setDatatime(byte[] datatime) {
		this.datatime = datatime;
	}

	public void setEMV_PARAM(byte[] EMV_PARAM) {
		this.EMV_PARAM = EMV_PARAM;
	}

	public void setAuthorizedAmount(int authorizedAmount) {
		this.authorizedAmount = authorizedAmount;
	}

	public void setCashBack(int cashBack) {
		this.cashBack = cashBack;
	}

	public void setTransNo(int transNo) {
		TransNo = transNo;
	}

	public byte[] getDatatime() {
		return datatime;
	}

	public byte[] getEMV_PARAM() {
		return EMV_PARAM;
	}

	public byte getCardType() {
		return cardType;
	}

	public int getAuthorizedAmount() {
		return authorizedAmount;
	}

	public int getCashBack() {
		return cashBack;
	}

	public String[] getTags() {
		return tags;
	}

	public int getTransNo() {
		return TransNo;
	}


	public  void setTags(String[] tags){
		this.tags = tags;
	}
	public EmvOptions(){
		
		EMV_PARAM = new byte[142];
	}
	private void setCardType(byte cardType) {

		this.cardType = cardType;

	}

	public void setTime(String data) {

		int year = Integer.valueOf(data.substring(0, 2));
		int month = Integer.valueOf(data.substring(2, 4));
		int day = Integer.valueOf(data.substring(4, 6));
		int hour = Integer.valueOf(data.substring(6, 8));
		int min = Integer.valueOf(data.substring(8, 10));
		int sec = Integer.valueOf(data.substring(10, 12));
		datatime = new byte[6];
		datatime[0] = (byte) year;
		datatime[1] = (byte) month;
		datatime[2] = (byte) day;
		datatime[3] = (byte) hour;
		datatime[4] = (byte) min;
		datatime[5] = (byte) sec;
		Log.e("time:", ByteTool.byte2hex(datatime));

	}

	public void setMerchName(byte[] MerchName) {// merchant name 64字节
		System.arraycopy(MerchName, 0, EMV_PARAM, 0, MerchName.length);
	}

	/**
	 * 参数2个字节 商户类别码(没要求可不设置)
	 * 
	 * @param MerchCateCode
	 */
	public void setMerchCateCode(byte[] MerchCateCode) {
		System.arraycopy(MerchCateCode, 0, EMV_PARAM, 64, 2);

	}

	/**
	 * 商户标志(商户号) 15字节
	 * 
	 * @param MerchId
	 */
	public void setMerchId(byte[] MerchId) {

		System.arraycopy(MerchId, 0, EMV_PARAM, 64 + 2, 15);
	}

	/**
	 * 终端标志(POS号) 8字节
	 * 
	 * @param TermId
	 */
	public void setTermId(byte[] TermId) {

		System.arraycopy(TermId, 0, EMV_PARAM, 64 + 2 + 15, 8);
	}

	/**
	 * 终端类型(详见终端规范表A.1)
	 * 
	 * @param TerminalType
	 */
	public void setTerminalType(byte TerminalType) {
		EMV_PARAM[64 + 2 + 15 + 8] = TerminalType;

	}

	/**
	 * 终端性能 3字节
	 * 
	 * @param Capability
	 */
	public void setCapability(byte[] Capability) {

		System.arraycopy(Capability, 0, EMV_PARAM, 64 + 2 + 15 + 9, 3);
	}

	/**
	 * 终端扩展性能 5字节
	 * 
	 * @param ExCapability
	 */
	public void setExCapability(byte[] ExCapability) {

		System.arraycopy(ExCapability, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3, 5);

	}

	/**
	 * 交易货币代码指数
	 */
	public void setTransCurrExp(byte TransCurrExp) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 5] = TransCurrExp;

	}

	/**
	 * 参考货币指数
	 * 
	 * @param ReferCurrExp
	 */
	public void setReferCurrExp(byte ReferCurrExp) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 6] = ReferCurrExp;

	}

	/**
	 * 参考货币代码 2字节
	 * 
	 * @param ReferCurrCode
	 */
	public void setReferCurrCode(byte[] ReferCurrCode) {

		System.arraycopy(ReferCurrCode, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7, 2);
	}

	/**
	 * 终端国家代码
	 * 
	 * @param CountryCode
	 */
	public void setCountryCode(byte[] CountryCode) {

		System.arraycopy(CountryCode, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 2, 2);
	}

	/**
	 * 交易货币代码
	 * 
	 * @param TransCurrCode
	 */
	public void setTransCurrCode(byte[] TransCurrCode) {

		System.arraycopy(TransCurrCode, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 2 + 2, 2);
	}

	/**
	 * 参考货币代码和交易代码的转换系数
	 * 
	 * @param ReferCurrCon
	 */
	public void setReferCurrCon(int ReferCurrCon) {
		byte[] b = ByteTool.intTo4bytes(ReferCurrCon);
		System.arraycopy(b, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 6, 4);
	}

	/**
	 * 设置交易类型
	 * 
	 * @param TransType
	 */
	public void setTransType(byte TransType) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 10] = TransType;

	}

	/**
	 * 强制联机交易
	 * 
	 * @param ForceOnline
	 */
	public void setForceOnline(byte ForceOnline) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 11] = ForceOnline;
	}

	/**
	 * 异常文件是否支持
	 * 
	 * @param bExceptionFile
	 */
	public void setbExceptionFile(byte bExceptionFile) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 12] = bExceptionFile;

	}

	/**
	 * 支持批捕获
	 * 
	 * @param bBatchCapture
	 */
	public void setbBatchCapture(byte bBatchCapture) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 13] = bBatchCapture;

	}

	/**
	 * 是否支持通知
	 * 
	 * @param bSupportAdvices
	 */
	public void setbSupportAdvices(byte bSupportAdvices) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 14] = bSupportAdvices;
	}

	/**
	 * Pos Entry Mode
	 * 
	 * @param POSEntryMode
	 */
	public void setPOSEntryMode(byte POSEntryMode) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 15] = POSEntryMode;
	}

	/**
	 * 内核类型0 -EMV KERNEL 1-PBOC KERNEL
	 * 
	 * @param PosKernelType
	 */
	public void setPosKernelType(byte PosKernelType) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 16] = PosKernelType;
	}

	/**
	 * 密码检测前是否读重试次数
	 */
	public void setGetDataPIN(byte GetDataPIN) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 17] = GetDataPIN;
	}

	/**
	 * 是否支持PSE选择方式
	 * 
	 * @param SupportPSESel
	 */
	public void setSupportPSESel(byte SupportPSESel) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 18] = SupportPSESel;

	}

	/**
	 * 终端交易限制 4字节
	 * 
	 * @param TermTransQuali
	 */

	public void setTermTransQuali(byte[] TermTransQuali) {

		System.arraycopy(TermTransQuali, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 19, 4);
	}

	/**
	 * 电子现金终端支持指示器
	 * 
	 * @param ECTSI
	 */
	public void setECTSI(byte ECTSI) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 23] = ECTSI;
	}

	/**
	 * 是否支持终端交易限额
	 * 
	 * @param EC_bTermLimitCheck
	 */
	public void setEC_bTermLimitCheck(byte EC_bTermLimitCheck) {

		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 24] = EC_bTermLimitCheck;
	}

	/**
	 * 终端交易限额，
	 * 
	 * @param EC_TermLimit
	 */
	public void setEC_TermLimit(int EC_TermLimit) {
		byte[] b = ByteTool.intTo4bytes(EC_TermLimit);
		System.arraycopy(b, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 25, 4);
	}

	/**
	 * 是否支持qPBOC状态检查
	 * 
	 * @param CL_bStatusCheck
	 */
	public void setCL_bStatusCheck(byte CL_bStatusCheck) {
		EMV_PARAM[64 + 2 + 15 + 9 + 3 + 7 + 29] = CL_bStatusCheck;

	}

	/**
	 * 非接触终端最低限额
	 * 
	 * @param CL_FloorLimit
	 */
	public void setCL_FloorLimit(int CL_FloorLimit) {
		byte[] b = ByteTool.intTo4bytes(CL_FloorLimit);
		System.arraycopy(b, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 30, 4);

	}

	/**
	 * 非接触终端交易限额
	 * 
	 * @param CL_TransLimit
	 */
	public void setCL_TransLimit(int CL_TransLimit) {
		byte[] b = ByteTool.intTo4bytes(CL_TransLimit);
		System.arraycopy(b, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 34, 4);

	}

	/**
	 * 非接触终端CVM限额
	 * 
	 * @param CL_CVMLimit
	 */
	public void setCL_CVMLimit(int CL_CVMLimit) {
		byte[] b = ByteTool.intTo4bytes(CL_CVMLimit);
		System.arraycopy(b, 0, EMV_PARAM, 64 + 2 + 15 + 9 + 3 + 7 + 38, 4);

	}

}
