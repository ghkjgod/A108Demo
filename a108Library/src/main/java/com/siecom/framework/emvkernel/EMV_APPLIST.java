package com.siecom.framework.emvkernel;

public class EMV_APPLIST {
	public static final int MAX_APP_NUM = 32; // 鎼存梻鏁ら崚妤勩�閺堚偓婢舵艾褰茬�妯哄亶閻ㄥ嫬绨查悽銊︽殶
	public static final byte PART_MATCH = 0x00;// ASI(闁劌鍨庨崠褰掑帳)
	public static final byte FULL_MATCH = 0x01;// ASI(鐎瑰苯鍙忛崠褰掑帳)

	private byte[] AppName = new byte[33]; // 鏈湴搴旂敤鍚嶏紝浠?\x00'缁撳熬鐨勫瓧绗︿覆
	private byte[] AID = new byte[17]; // 搴旂敤鏍囧織
	private byte AidLen; // AID鐨勯暱搴?
	private byte SelFlag; // 閫夋嫨鏍囧織( 閮ㄥ垎鍖归厤/鍏ㄥ尮閰?
	private byte Priority; // 浼樺厛绾ф爣蹇?
	private byte TargetPer; // 鐩爣鐧惧垎姣旀暟
	private byte MaxTargetPer; // 鏈€澶х洰鏍囩櫨鍒嗘瘮鏁?
	private byte FloorLimitCheck; // 鏄惁妫€鏌ユ渶浣庨檺棰?
	private byte RandTransSel; // 鏄惁杩涜闅忔満浜ゆ槗閫夋嫨
	private byte VelocityCheck; // 鏄惁杩涜棰戝害妫€娴?
	private long FloorLimit; // 鏈€浣庨檺棰?
	private long Threshold; // 闃€鍊?
	private byte[] TACDenial = new byte[6]; // 缁堢琛屼负浠ｇ爜(鎷掔粷)
	private byte[] TACOnline = new byte[6]; // 缁堢琛屼负浠ｇ爜(鑱旀満)
	private byte[] TACDefault = new byte[6]; // 缁堢琛屼负浠ｇ爜(缂虹渷)
	private byte[] AcquierId = new byte[7]; // 鏀跺崟琛屾爣蹇?
	private byte[] dDOL = new byte[64]; // 缁堢缂虹渷DDOL
	private byte[] tDOL = new byte[64]; // 缁堢缂虹渷TDOL
	private byte[] Version = new byte[3]; // 搴旂敤鐗堟湰
	private byte[] RiskManData = new byte[10]; // 椋庨櫓绠＄悊鏁版嵁
	private byte EC_bTermLimitCheck; // CUP瑕佹眰姣忎釜AID闇€瑕佽繖涓弬鏁?
	private long EC_TermLimit; // 缁堢浜ゆ槗闄愰锛?
	private long CL_bStatusCheck; 
	private long CL_FloorLimit; // 闈炴帴瑙︾粓绔渶浣庨檺棰?
	private long CL_TransLimit; // 闈炴帴瑙︾粓绔氦鏄撻檺棰?
	private long CL_CVMLimit; // 闈炴帴瑙︾粓绔疌VM闄?
	private byte TermQuali_byte2;// 娴溿倖妲楅柌鎴︻杺娑撳孩鐦℃稉鐙滻D闂勬劙顤傞惃鍕灲閺傤厾绮ㄩ弸婊愮礉閸︺劌鍩涢崡鈥冲婢跺嫮鎮婇敍宀勨偓姘崇箖濮濄倕褰夐柌蹇曠处鐎涙ê鍨介弬顓犵波閺�

	public EMV_APPLIST() {

	}

	public EMV_APPLIST(byte[] appName, byte[] aID, byte aidLen, byte selFlag,
			byte priority, byte targetPer, byte maxTargetPer,
			byte floorLimitCheck, byte randTransSel, byte velocityCheck,
			long floorLimit, long threshold, byte[] tACDenial,
			byte[] tACOnline, byte[] tACDefault, byte[] acquierId, byte[] dDOL,
			byte[] tDOL, byte[] version, byte[] riskManData,
			byte eC_bTermLimitCheck, long eC_TermLimit,
			long cL_bStatusCheck, long cL_FloorLimit, long cL_TransLimit,
			long cL_CVMLimit, byte termQuali_byte2) {
		super();
		AppName = appName;
		AID = aID;
		AidLen = aidLen;
		SelFlag = selFlag;
		Priority = priority;
		TargetPer = targetPer;
		MaxTargetPer = maxTargetPer;
		FloorLimitCheck = floorLimitCheck;
		RandTransSel = randTransSel;
		VelocityCheck = velocityCheck;
		FloorLimit = floorLimit;
		Threshold = threshold;
		TACDenial = tACDenial;
		TACOnline = tACOnline;
		TACDefault = tACDefault;
		AcquierId = acquierId;
		this.dDOL = dDOL;
		this.tDOL = tDOL;
		Version = version;
		RiskManData = riskManData;
		EC_bTermLimitCheck = eC_bTermLimitCheck;
		EC_TermLimit = eC_TermLimit;
		CL_bStatusCheck = cL_bStatusCheck;
		CL_FloorLimit = cL_FloorLimit;
		CL_TransLimit = cL_TransLimit;
		CL_CVMLimit = cL_CVMLimit;
		TermQuali_byte2 = termQuali_byte2;
	}

	public byte[] getAppName() {
		return AppName;
	}

	public void setAppName(byte[] appName) {
		AppName = appName;
	}

	public byte[] getAID() {
		return AID;
	}

	public void setAID(byte[] aID) {
		AID = aID;
	}

	public byte getAidLen() {
		return AidLen;
	}

	public void setAidLen(byte aidLen) {
		AidLen = aidLen;
	}

	public byte getSelFlag() {
		return SelFlag;
	}

	public void setSelFlag(byte selFlag) {
		SelFlag = selFlag;
	}

	public byte getPriority() {
		return Priority;
	}

	public void setPriority(byte priority) {
		Priority = priority;
	}

	public byte getTargetPer() {
		return TargetPer;
	}

	public void setTargetPer(byte targetPer) {
		TargetPer = targetPer;
	}

	public byte getMaxTargetPer() {
		return MaxTargetPer;
	}

	public void setMaxTargetPer(byte maxTargetPer) {
		MaxTargetPer = maxTargetPer;
	}

	public byte getFloorLimitCheck() {
		return FloorLimitCheck;
	}

	public void setFloorLimitCheck(byte floorLimitCheck) {
		FloorLimitCheck = floorLimitCheck;
	}

	public byte getRandTransSel() {
		return RandTransSel;
	}

	public void setRandTransSel(byte randTransSel) {
		RandTransSel = randTransSel;
	}

	public byte getVelocityCheck() {
		return VelocityCheck;
	}

	public void setVelocityCheck(byte velocityCheck) {
		VelocityCheck = velocityCheck;
	}

	public long getFloorLimit() {
		return FloorLimit;
	}

	public void setFloorLimit(long floorLimit) {
		FloorLimit = floorLimit;
	}

	public long getThreshold() {
		return Threshold;
	}

	public void setThreshold(long threshold) {
		Threshold = threshold;
	}

	public byte[] getTACDenial() {
		return TACDenial;
	}

	public void setTACDenial(byte[] tACDenial) {
		TACDenial = tACDenial;
	}

	public byte[] getTACOnline() {
		return TACOnline;
	}

	public void setTACOnline(byte[] tACOnline) {
		TACOnline = tACOnline;
	}

	public byte[] getTACDefault() {
		return TACDefault;
	}

	public void setTACDefault(byte[] tACDefault) {
		TACDefault = tACDefault;
	}

	public byte[] getAcquierId() {
		return AcquierId;
	}

	public void setAcquierId(byte[] acquierId) {
		AcquierId = acquierId;
	}

	public byte[] getdDOL() {
		return dDOL;
	}

	public void setdDOL(byte[] dDOL) {
		this.dDOL = dDOL;
	}

	public byte[] gettDOL() {
		return tDOL;
	}

	public void settDOL(byte[] tDOL) {
		this.tDOL = tDOL;
	}

	public byte[] getVersion() {
		return Version;
	}

	public void setVersion(byte[] version) {
		Version = version;
	}

	public byte[] getRiskManData() {
		return RiskManData;
	}

	public void setRiskManData(byte[] riskManData) {
		RiskManData = riskManData;
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

	public long getCL_bStatusCheck() {
		return CL_bStatusCheck;
	}

	public void setCL_bStatusCheck(long cL_bStatusCheck) {
		CL_bStatusCheck = cL_bStatusCheck;
	}

	public long getCL_FloorLimit() {
		return CL_FloorLimit;
	}

	public void setCL_FloorLimit(long cL_FloorLimit) {
		CL_FloorLimit = cL_FloorLimit;
	}

	public long getCL_TransLimit() {
		return CL_TransLimit;
	}

	public void setCL_TransLimit(long cL_TransLimit) {
		CL_TransLimit = cL_TransLimit;
	}

	public long getCL_CVMLimit() {
		return CL_CVMLimit;
	}

	public void setCL_CVMLimit(long cL_CVMLimit) {
		CL_CVMLimit = cL_CVMLimit;
	}

	public byte getTermQuali_byte2() {
		return TermQuali_byte2;
	}

	public void setTermQuali_byte2(byte termQuali_byte2) {
		TermQuali_byte2 = termQuali_byte2;
	}

	public static int getMaxAppNum() {
		return MAX_APP_NUM;
	}

	public static byte getPartMatch() {
		return PART_MATCH;
	}

	public static byte getFullMatch() {
		return FULL_MATCH;
	}

	
}
