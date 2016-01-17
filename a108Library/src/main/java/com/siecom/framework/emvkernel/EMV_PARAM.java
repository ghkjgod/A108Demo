package com.siecom.framework.emvkernel;

public class EMV_PARAM {
	private byte[] MerchName = new byte[64];
	private byte[] MerchCateCode = new byte[2];
	private byte[] MerchId = new byte[15];
	private byte[] TermId = new byte[8];
	private byte TerminalType;
	private byte[] Capability = new byte[3];
	private byte[] ExCapability = new byte[5];
	private byte TransCurrExp;
	private byte ReferCurrExp;
	private byte[] ReferCurrCode = new byte[2];
	private byte[] CountryCode = new byte[2];
	private byte[] TransCurrCode = new byte[2];
	private long ReferCurrCon;
	private byte TransType;
	private byte ForceOnline;
	private byte bExceptionFile;
	private byte bBatchCapture;
	private byte bSupportAdvices;
	private byte POSEntryMode;
	private byte PosKernelType;
	private byte GetDataPIN;
	private byte SurportPSESel;
	private byte[] TermTransQuali = new byte[4];
	private byte ECTSI;
	private byte EC_bTermLimitCheck;
	private long EC_TermLimit;
	private byte CL_bStatusCheck;
	private long CL_FloorLimit;
	private long TransLimit;
	private long CL_CVMLimit;

	public EMV_PARAM() {

	}

	public EMV_PARAM(byte[] merchName, byte[] merchCateCode, byte[] merchId,
			byte[] termId, byte terminalType, byte[] capability,
			byte[] exCapability, byte transCurrExp, byte referCurrExp,
			byte[] referCurrCode, byte[] countryCode, byte[] transCurrCode,
			long referCurrCon, byte transType, byte forceOnline,
			byte bExceptionFile, byte bBatchCapture, byte bSupportAdvices,
			byte pOSEntryMode, byte posKernelType, byte getDataPIN,
			byte surportPSESel, byte[] termTransQuali, byte eCTSI,
			byte eC_bTermLimitCheck, long eC_TermLimit, byte cL_bStatusCheck,
			long cL_FloorLimit, long transLimit, long cL_CVMLimit) {
		super();
		MerchName = merchName;
		MerchCateCode = merchCateCode;
		MerchId = merchId;
		TermId = termId;
		TerminalType = terminalType;
		Capability = capability;
		ExCapability = exCapability;
		TransCurrExp = transCurrExp;
		ReferCurrExp = referCurrExp;
		ReferCurrCode = referCurrCode;
		CountryCode = countryCode;
		TransCurrCode = transCurrCode;
		ReferCurrCon = referCurrCon;
		TransType = transType;
		ForceOnline = forceOnline;
		this.bExceptionFile = bExceptionFile;
		this.bBatchCapture = bBatchCapture;
		this.bSupportAdvices = bSupportAdvices;
		POSEntryMode = pOSEntryMode;
		PosKernelType = posKernelType;
		GetDataPIN = getDataPIN;
		SurportPSESel = surportPSESel;
		TermTransQuali = termTransQuali;
		ECTSI = eCTSI;
		EC_bTermLimitCheck = eC_bTermLimitCheck;
		EC_TermLimit = eC_TermLimit;
		CL_bStatusCheck = cL_bStatusCheck;
		CL_FloorLimit = cL_FloorLimit;
		TransLimit = transLimit;
		CL_CVMLimit = cL_CVMLimit;
	}

	public byte[] getMerchName() {
		return MerchName;
	}

	public void setMerchName(byte[] merchName) {
		MerchName = merchName;
	}

	public byte[] getMerchCateCode() {
		return MerchCateCode;
	}

	public void setMerchCateCode(byte[] merchCateCode) {
		MerchCateCode = merchCateCode;
	}

	public byte[] getMerchId() {
		return MerchId;
	}

	public void setMerchId(byte[] merchId) {
		MerchId = merchId;
	}

	public byte[] getTermId() {
		return TermId;
	}

	public void setTermId(byte[] termId) {
		TermId = termId;
	}

	public byte getTerminalType() {
		return TerminalType;
	}

	public void setTerminalType(byte terminalType) {
		TerminalType = terminalType;
	}

	public byte[] getCapability() {
		return Capability;
	}

	public void setCapability(byte[] capability) {
		Capability = capability;
	}

	public byte[] getExCapability() {
		return ExCapability;
	}

	public void setExCapability(byte[] exCapability) {
		ExCapability = exCapability;
	}

	public byte getTransCurrExp() {
		return TransCurrExp;
	}

	public void setTransCurrExp(byte transCurrExp) {
		TransCurrExp = transCurrExp;
	}

	public byte getReferCurrExp() {
		return ReferCurrExp;
	}

	public void setReferCurrExp(byte referCurrExp) {
		ReferCurrExp = referCurrExp;
	}

	public byte[] getReferCurrCode() {
		return ReferCurrCode;
	}

	public void setReferCurrCode(byte[] referCurrCode) {
		ReferCurrCode = referCurrCode;
	}

	public byte[] getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(byte[] countryCode) {
		CountryCode = countryCode;
	}

	public byte[] getTransCurrCode() {
		return TransCurrCode;
	}

	public void setTransCurrCode(byte[] transCurrCode) {
		TransCurrCode = transCurrCode;
	}

	public long getReferCurrCon() {
		return ReferCurrCon;
	}

	public void setReferCurrCon(long referCurrCon) {
		ReferCurrCon = referCurrCon;
	}

	public byte getTransType() {
		return TransType;
	}

	public void setTransType(byte transType) {
		TransType = transType;
	}

	public byte getForceOnline() {
		return ForceOnline;
	}

	public void setForceOnline(byte forceOnline) {
		ForceOnline = forceOnline;
	}

	public byte getbExceptionFile() {
		return bExceptionFile;
	}

	public void setbExceptionFile(byte bExceptionFile) {
		this.bExceptionFile = bExceptionFile;
	}

	public byte getbBatchCapture() {
		return bBatchCapture;
	}

	public void setbBatchCapture(byte bBatchCapture) {
		this.bBatchCapture = bBatchCapture;
	}

	public byte getbSupportAdvices() {
		return bSupportAdvices;
	}

	public void setbSupportAdvices(byte bSupportAdvices) {
		this.bSupportAdvices = bSupportAdvices;
	}

	public byte getPOSEntryMode() {
		return POSEntryMode;
	}

	public void setPOSEntryMode(byte pOSEntryMode) {
		POSEntryMode = pOSEntryMode;
	}

	public byte getPosKernelType() {
		return PosKernelType;
	}

	public void setPosKernelType(byte posKernelType) {
		PosKernelType = posKernelType;
	}

	public byte getGetDataPIN() {
		return GetDataPIN;
	}

	public void setGetDataPIN(byte getDataPIN) {
		GetDataPIN = getDataPIN;
	}

	public byte getSurportPSESel() {
		return SurportPSESel;
	}

	public void setSurportPSESel(byte surportPSESel) {
		SurportPSESel = surportPSESel;
	}

	public byte[] getTermTransQuali() {
		return TermTransQuali;
	}

	public void setTermTransQuali(byte[] termTransQuali) {
		TermTransQuali = termTransQuali;
	}

	public byte getECTSI() {
		return ECTSI;
	}

	public void setECTSI(byte eCTSI) {
		ECTSI = eCTSI;
	}

	public byte getEC_bTermLimitCheck() {
		return EC_bTermLimitCheck;
	}

	public void setEC_bTermLimitCheck(byte eC_bTermLimitCheck) {
		EC_bTermLimitCheck = eC_bTermLimitCheck;
	}

	public long getEC_TermLimit() {
		return EC_TermLimit;
	}

	public void setEC_TermLimit(long eC_TermLimit) {
		EC_TermLimit = eC_TermLimit;
	}

	public byte getCL_bStatusCheck() {
		return CL_bStatusCheck;
	}

	public void setCL_bStatusCheck(byte cL_bStatusCheck) {
		CL_bStatusCheck = cL_bStatusCheck;
	}

	public long getCL_FloorLimit() {
		return CL_FloorLimit;
	}

	public void setCL_FloorLimit(long cL_FloorLimit) {
		CL_FloorLimit = cL_FloorLimit;
	}

	public long getTransLimit() {
		return TransLimit;
	}

	public void setTransLimit(long transLimit) {
		TransLimit = transLimit;
	}

	public long getCL_CVMLimit() {
		return CL_CVMLimit;
	}

	public void setCL_CVMLimit(long cL_CVMLimit) {
		CL_CVMLimit = cL_CVMLimit;
	}

	
}
