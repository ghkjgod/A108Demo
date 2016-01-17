package com.siecom.framework.module;

import android.util.Log;

import com.siecom.framework.bean.BankCardInfoBean;
import com.siecom.framework.emvkernel.EMV_APPLIST;
import com.siecom.framework.listen.CardReadListen;
import com.siecom.tools.ByteTool;
import com.siecom.tools.SingletonThreadPool;
import com.siecom.tools.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2015/12/12.
 */
public class BankCardModule extends CommonModule {
    public final static int IC_CARD = 0x00;
    public final static int MSR_CARD = 0x02;
    public final static int PIC_CARD = 0x01;
    public final static int AUTO_FIND = 3;
    private byte track1[] = new byte[250];
    private byte track2[] = new byte[250];
    private byte track3[] = new byte[250];
    private static BankCardModule module = null;
    private static final String TAG = "BankCardModule";
    private CardReadListen callback;
    private int wantType = -1;
    private ReadCardThread readThread = null;
    private BankCardInfoBean bean;
    private EmvOptions options;
    private static ExecutorService threadPool = SingletonThreadPool.getInstance();
    private boolean lockFlag = false;

    @Override
    public boolean isFinish() {
        if (readThread == null || readThread.isThreadFinished()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void closeModule(boolean isConnected) {
        Log.e(TAG, "closeModule");
        if (readThread != null && !readThread.isThreadFinished()) {
            readThread.setFlag(false);
            while (!readThread.isThreadFinished()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            readThread = null;
        }
        wantType = -1;
    }

    /**
     * 下电
     */
    public void shutDownCard() {
        Log.e(TAG, "shutDownCard");
        lockFlag = true;
        try {
            api.Icc_Close((byte) 0x00);
            api.Picc_Close();
            api.Mcr_Close();
        } finally {
            lockFlag = false;
        }

    }

    public void setEMVOption(EmvOptions options) {

        this.options = options;
    }

    public void startReadCard(final int wantType) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (readThread != null && !readThread.isThreadFinished()) {
                    readThread.setFlag(false);
                    while (!readThread.isThreadFinished()) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    readThread = null;
                }
                while (lockFlag) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                readThread = new ReadCardThread(wantType);
                readThread.start();
                bean = new BankCardInfoBean();
            }
        };
        threadPool.submit(r);
    }

    @Override
    public int OpenModule() {
        int iret = 0;
        if (wantType == IC_CARD || wantType == AUTO_FIND) {

            api.Icc_Close((byte) 0x00);
            byte[] ATR = new byte[100];
            iret = api.Icc_Open((byte) 0x00, (byte) 0x01, ATR);//IC卡上电
            Log.e(TAG, "ret = " + iret);
            iret = 0;
        }
        if (iret != 0) {
            if (callback != null)
                callback.onReadCardFail(iret, "IC卡模块打开失败");
            return iret;
        }
        if (wantType == MSR_CARD || wantType == AUTO_FIND) {
            Log.e(TAG, "MSR_CARD ret = " + iret);
            for (int i = 0; i < 3; i++) {
                iret = api.Mcr_Open(); // 打开磁卡
                Log.e(TAG, "MSR_CARD ret = " + iret);
                if (iret == 0) {
                    break;
                }
                api.Mcr_Close();
            }
        }
        if (iret != 0) {
            if (callback != null)
                callback.onReadCardFail(iret, "磁卡模块打开失败");
            return iret;
        }
        if (wantType == PIC_CARD || wantType == AUTO_FIND) {

            for (int i = 0; i < 3; i++) {
                iret = api.Picc_Open(); // 打开
                Log.e(TAG, "PIC_CARD ret = " + iret);
                if (iret == 0) {
                    break;
                }
                api.Picc_Close();
            }
        }
        if (iret != 0) {
            if (callback != null)
                callback.onReadCardFail(iret, "非接模块打开失败");
            return iret;
        }
        return iret;
    }

    public void setCallback(CardReadListen callback) {
        this.callback = callback;
    }

    private BankCardModule() {


    }

    public static BankCardModule getInstance() {

        if (module == null) {
            module = new BankCardModule();
        }
        return module;

    }

    private class ReadCardThread extends Thread {
        private boolean m_bThreadFinished = false;
        private boolean runFlag = true;
        private int cardType = -1;

        public ReadCardThread(int wantType) {
            this.cardType = wantType;
            BankCardModule.this.wantType = wantType;

        }

        public boolean isThreadFinished() {

            return m_bThreadFinished;
        }

        public void setFlag(boolean flag) {

            runFlag = flag;

        }

        @Override
        public void run() {

            try {
                runFlag = true;
                int ret = OpenModule();
                if (ret == 0) {
                    if (callback != null) {
                        callback.onStart();
                    }
                    synchronized (this) {
                        while (runFlag) {
                            if (cardType == IC_CARD || cardType == AUTO_FIND) {
                                ret = api.Icc_Detect((byte) 0x00);
                                if (0 == ret) {
                                    Log.e(TAG, "Icc_Detect");
                                    String str = transBeforeOnline((byte) IC_CARD);

                                    if (str == null) {

                                        byte[] ATR = new byte[100];
                                        api.Icc_Close((byte) 0x00);
                                        api.Icc_Open((byte) 0x00, (byte) 0x01, ATR);//IC卡上电

                                        continue;
                                    }
                                    bean.cardType = IC_CARD;
                                    if (str.indexOf("5A") != -1) {
                                        bean.cardNo = StringUtil.tagParse("5A", str);
                                        Log.e("cardno", bean.cardNo);
                                        int len5A = StringUtil.getTagLen(0, "5A", str);
                                        if (len5A == 0) {
                                            String tag57 = StringUtil.tagParse(len5A * 2 + 2,
                                                    "57", str);
                                            bean.cardNo = getCardNo(tag57);
                                        }

                                        if (bean.cardNo == null || bean.cardNo.length() == 0) {
                                            continue;
                                        }

                                        if (bean.cardNo.indexOf("F") != -1) {
                                            bean.cardNo = bean.cardNo.substring(0, bean.cardNo.indexOf("F"));
                                        }
                                    }

                                    int offset = str.indexOf("9F26");
                                    if (offset != -1)
                                        bean.ICChipData = str.substring(offset, str.length());
                                    else
                                        bean.ICChipData = str;
                                    if (callback != null)
                                        callback.onReadCardInfo(bean);
                                    break;

                                }
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (cardType == MSR_CARD || cardType == AUTO_FIND) {
                                ret = api.Mcr_Check();
                                if (0 == ret) {
                                    track1 = new byte[250];
                                    track2 = new byte[250];
                                    track3 = new byte[250];
                                    Log.e(TAG, "Mcr_Check");
                                    try {
                                        ret = api.Mcr_Read((byte) 0, (byte) 0, track1, track2,
                                                track3);
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                    Log.e(TAG, "ret = " + ret);
                                    if (ret > 0 && ret <= 7) {

                                        bean.cardType = MSR_CARD;
                                        String string = "";
                                        if ((ret & 0x01) == 0x01)
                                            string = "track1:" + new String(track1);
                                        bean.oneMagneticTrack = new String(track1).trim();
                                        if ((ret & 0x02) == 0x02) {
                                            string = string + "\n\ntrack2:"
                                                    + new String(track2);
                                            bean.twoMagneticTrack = new String(track2)
                                                    .trim();
                                            bean.cardNo = decodecardNo(new String(track2).trim());
                                        }
                                        if ((ret & 0x04) == 0x04)
                                            string = string + "\n\ntrack3:"
                                                    + new String(track3);
                                        bean.threeMagneticTrack = new String(track3).trim();
                                        Log.i("MSR", string);
                                        Log.i(TAG, "Lib_MsrRead succeed!");
                                        if (bean.cardNo == null || bean.cardNo.trim().length() == 0)
                                            continue;
                                        api.Mcr_Close();
                                        if (callback != null)
                                            callback.onReadCardInfo(bean);
                                    } else {
                                        continue;
                                    }
                                    break;
                                }
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (cardType == PIC_CARD || cardType == AUTO_FIND) {
                                byte CardType[] = new byte[3];
                                byte SerialNo[] = new byte[50];
                                ret = api.Picc_Check((byte) 0x01, CardType, SerialNo);
                                if (0 == ret) {
                                    Log.e(TAG, "Picc_Check");
                                    String str = transBeforeOnline((byte) PIC_CARD);

                                    if (str == null) {
                                        api.Picc_Close();
                                        api.Picc_Open();
                                        continue;
                                    }
                                    bean.cardType = PIC_CARD;
                                    if (str.indexOf("5A") != -1) {
                                        int len5A = StringUtil.getTagLen(0, "5A", str);

                                        if (len5A > 0) {
                                            bean.cardNo = StringUtil.tagParse("5A", str);
                                            Log.e("cardno", bean.cardNo);
                                        }
                                        if (len5A == 0) {
                                            String tag57 = StringUtil.tagParse(len5A * 2 + 2,
                                                    "57", str);
                                            Log.e("57", tag57 + "---");
                                            bean.cardNo = getCardNo(tag57);
                                        }

                                        if (bean.cardNo == null || bean.cardNo.length() == 0) {
                                            continue;
                                        }

                                        if (bean.cardNo.indexOf("F") != -1) {
                                            bean.cardNo = bean.cardNo.substring(0, bean.cardNo.indexOf("F"));
                                        }
                                    }

                                    int offset = str.indexOf("9F26");
                                    if (offset != -1)
                                        bean.ICChipData = str.substring(offset, str.length());
                                    else
                                        bean.ICChipData = str;
                                    if (callback != null)
                                        callback.onReadCardInfo(bean);
                                    break;
                                }

                            }

                        }
                    }
                }
            } finally {
                Log.e(TAG, "m_bThreadFinished = true");
                m_bThreadFinished = true;
            }


        }
    }

    public synchronized String transBeforeOnline(byte cardType) {
        int ret = -1;
        ret = api.EmvLib_Init();
        if (ret != 0) {
            Log.e(TAG, "init fail");
            return null;
        }
        if (options != null) {
            ret = api.EmvLib_SetTime(options.getDatatime());
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");// 设置日期格式
            String time = df.format(new Date());
            ret = api.EmvLib_SetTime(transDate(time));
        }
        if (ret != 0) {
            Log.v(TAG, "EmvLib_SetTime: ret = " + ret);
            return null;
        }
        int TransNo = 1;
        int authorizedAmount = 0;
        int cashBack = 0;
        String[] wantTags = {"5A", "57", "9F26", "9F27", "9F10", "9F37", "9F36",
                "95", "9A", "9C", "9F02", "5F2A", "82", "9F1A", "9F03",
                "9F33", "9F34", "9F35", "9F1E", "4F", "5F34"};
        if (options != null) {
            ret = api.EmvLib_SetParam(options.EMV_PARAM);
            Log.v(TAG, "EmvLib_SetParam: ret = " + ret);
            ret = Emv_AddApp(1);//极速模式
            Log.v(TAG, "Emv_AddApp: ret = " + ret);
            authorizedAmount = options.getAuthorizedAmount();
            cashBack = options.getCashBack();
            wantTags = options.getTags();
            TransNo = options.getTransNo();
        }
        if (cardType == 0x01) {
            if (options == null) {
                EmvOptions op = new EmvOptions();
                byte[] MerchCateCode = new byte[2];
                MerchCateCode[0] = 0x00;
                MerchCateCode[1] = 0x01;
                op.setMerchCateCode(MerchCateCode);
                String MerchId = "123456789012345";
                op.setMerchId(MerchId.getBytes());
                op.setMerchName("EMV LIBRARY".getBytes());
                op.setTermId("12345678".getBytes());
                op.setTerminalType((byte) 0x22);
                op.setSupportPSESel((byte) 0x01);
                byte[] Capability = {(byte) 0xE0, (byte) 0xC0, (byte) 0xC8};
                op.setCapability(Capability);
                byte[] ExCapability = {(byte) 0x60, (byte) 0x00, (byte) 0xF0,
                        (byte) 0x20, (byte) 0x01};
                op.setExCapability(ExCapability);
                byte[] CountryCode = {(byte) 0x01, (byte) 0x56};
                op.setCountryCode(CountryCode);
                op.setTransCurrCode(CountryCode);
                op.setTransCurrExp((byte) 0x02);
                op.setReferCurrCode(CountryCode);
                op.setCL_TransLimit(100000);
                op.setTransType((byte) 0x01);// 交易类型
                byte[] TermTransQuali = {0x24, 0x00, 0x00, (byte) 0x80};
                op.setTermTransQuali(TermTransQuali);
                op.setCL_bStatusCheck((byte) 0x00);
                op.setCL_CVMLimit(10000);
                op.setCL_FloorLimit(20000);
                ret = api.EmvLib_SetParam(op.EMV_PARAM);
                Log.v(TAG, "EmvLib_SetParam: ret = " + ret);
                ret = Emv_AddApp(1);//1.极速模式
                Log.v(TAG, "Emv_AddApp: ret = " + ret);
            }

        }
        ret = api.EmvLib_CoreGetAmt(authorizedAmount, cashBack);
        if (ret != 0) {
            Log.e(TAG, "EmvLib_CoreGetAmt: ret = " + ret);
            return null;
        }
        int[] Num = new int[1];
        byte[] AidList = new byte[180];
        try {
            ret = api.EmvLib_CreatAppList((byte) cardType, (byte) 0x00, AidList, Num);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "EmvLib_CreatAppList:" + e.getMessage());
            return null;
        }
        Log.e("num:", Num[0] + "!!");
        if (ret != 0) {
            Log.e(TAG, "EmvLib_CreatAppList: ret = " + ret);
            return null;
        }
        ret = api.EmvLib_SetSelectApp((byte) 0);
        if (ret != 0) {
            Log.e(TAG, "EmvLib_SetSelectApp: ret = " + ret);
            return null;
        }
        Log.e("ret:", "EmvLib_GetTags" + ret);
        byte[] ifGoOnline = new byte[1];
        byte[] ifNeedPin = new byte[1];
        if (cardType == IC_CARD) {
            ret = api.EmvLib_ProcTransBeforeOnline((byte) cardType, TransNo, ifGoOnline, ifNeedPin);
        } else if (cardType == PIC_CARD) {
            ret = api.EmvLib_qPBOCPreProcess();
            if (ret != 0) {
                Log.e(TAG, "EmvLib_qPBOCPreProcess: ret = " + ret);
                return null;
            }
            ret = api.EmvLib_ProcCLTransBeforeOnline((byte) cardType, TransNo, ifGoOnline, ifNeedPin);
            if (ret != 0) {
                Log.e(TAG, "EmvLib_ProcCLTransBeforeOnline: ret = " + ret);
                return null;
            }
        } else {
            return null;
        }
        if (ret != 0) {
            Log.e(TAG, "EmvLib_ProcTransBeforeOnline: ret = " + ret);
            return null;
        }
        int[] len = new int[1];
        byte[] dataOut = new byte[2048];
        ret = api.EmvLib_GetTags(wantTags, dataOut, len);
        if (ret != 0) {
            Log.v(TAG, "EmvLib_GetTags: ret = " + ret);
            return null;
        }
        byte[] out = new byte[len[0]];
        System.arraycopy(dataOut, 0, out, 0, len[0]);
        String returnstring = ByteTool.byte2hex(out);
        Log.e("string:", returnstring);
        return returnstring.trim();
    }


    public int GetLog(int ICType, byte[] tlog, int[] length) {
        if (api == null) {
            return -1;
        }
        int ret = -1;
        try {
            ret = api.EmvLib_GetLog(ICType, tlog, length);
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
        return ret;

    }

    public int ARPCExecuteScript(int ICType, String ARPC, String ScriptStr,
                                 byte[] ScriptResult, byte[] TC) {
        byte[] RspCode = null;
        byte[] AuthCode = "123456".getBytes();
        int AuthCodeLen = 0;
        byte ucResult = 0;
        byte[] IAuthData = new byte[8];
        byte[] Script = null;
        int IAuthDataLen = 0;
        int ScriptLen = 0;
        byte[] Result = new byte[1024];
        int[] RetLen = new int[1];
        int ret = -1;
        int nIndex = ARPC.indexOf("91");
        if (nIndex != -1) {
            String substr = ARPC.substring(nIndex + 2,
                    nIndex + 4);
            IAuthDataLen = Integer.parseInt(substr, 16);
            Log.e(TAG, "IAuthDataLen = " + IAuthDataLen);
            IAuthData = StringUtil.hexStringToBytes(ARPC.substring(nIndex + 4, nIndex + 4 + IAuthDataLen * 2));
            Log.e(TAG, "IAuthData = " + ByteTool.bytearrayToHexString(IAuthData, IAuthDataLen));
            RspCode = StringUtil.hexStringToBytes(ARPC.substring(nIndex + 4 + IAuthDataLen * 2 - 4, nIndex + 4 + IAuthDataLen * 2));
            Log.e(TAG, "RspCode = " + ByteTool.bytearrayToHexString(RspCode, 2));
        }
        nIndex = ScriptStr.indexOf("72");
        if (nIndex != -1) {
            String substr = ScriptStr.substring(nIndex + 2,
                    nIndex + 4);
            ScriptLen = Integer.parseInt(substr, 16) + 2;
            Log.e(TAG, "ScriptLen = " + ScriptLen);
            Script = StringUtil.hexStringToBytes(ScriptStr.substring(nIndex, nIndex + ScriptLen * 2));
            Log.e(TAG, "Script = " + ByteTool.bytearrayToHexString(Script, ScriptLen));
        }
        if (ICType == 0x00)
            ret = api.EmvLib_ProcTransComplete(ucResult, RspCode, AuthCode, AuthCodeLen, IAuthData, IAuthDataLen, Script, ScriptLen);
        else if (ICType == 0x01)
            ret = api.EmvLib_ProcCLTransComplete((byte) 0, RspCode, AuthCode, AuthCodeLen);
        else
            return ret;
        if (ret != 0) {
            Log.e(TAG, "EmvLib_ProcCLTransComplete: ret = " + ret);
            return ret;
        }
        ret = api.EmvLib_GetScriptResult(Result, RetLen);
        if (ret != 0) {
            Log.e(TAG, "EmvLib_GetScriptResult: ret = " + ret);
            return ret;
        }
        System.arraycopy("DF31".getBytes(), 0, ScriptResult, 0, 4);
        if (RetLen[0] > 9) {
            System.arraycopy(("" + RetLen[0]).getBytes(), 0, ScriptResult, 4, 2);
        } else {
            System.arraycopy(("0" + RetLen[0]).getBytes(), 0, ScriptResult, 4, 2);
        }
        System.arraycopy(ByteTool.bytearrayToHexString(Result, RetLen[0])
                .getBytes(), 0, ScriptResult, 6, RetLen[0] * 2);
        String[] tags = {"98"};

        byte[] dataOut = new byte[1024];
        int[] len1 = new int[1];
        ret = api.EmvLib_GetTags(tags, dataOut, len1);
        byte[] out = new byte[len1[0]];
        System.arraycopy(dataOut, 0, out, 0, len1[0]);
        if (ret == 0) {
            System.arraycopy(ByteTool.bytearrayToHexString(dataOut, len1[0])
                    .getBytes(), 0, TC, 0, len1[0] * 2);
        }
        return ret;
    }

    public int validateARPC(byte[] content) {

        int len = ByteTool.getBytesLength(content);
        byte ucResult = 0;
        byte[] RspCode = new byte[2];
        RspCode[0] = 0x30;
        RspCode[1] = 0x30;
        byte[] AuthCode = new byte[1];
        byte[] Script = new byte[1];
        int ret = api.EmvLib_ProcTransComplete(ucResult, RspCode, AuthCode, 0,
                content, len, Script, 0);
        Log.i("Validate ARPC", "ret = " + ret + " !!!!!!!!!!!!!!!");
        String[] tags = {"95"};
        byte[] dataOut = new byte[1024];
        int[] len1 = new int[1];
        ret = api.EmvLib_GetTags(tags, dataOut, len1);
        byte[] out = new byte[len1[0]];
        System.arraycopy(dataOut, 0, out, 0, len1[0]);
        if (ret == 0) {
            String tags_95 = ByteTool.byte2hex(out);
            if (tags_95 != null && !tags_95.equals("")) {
                if (tags_95.substring(tags_95.length() - 2, tags_95.length())
                        .equals("00")) {
                    ret = 0;
                } else {
                    ret = 1;
                }
            }
        }

        return ret;
    }

    public int Emv_AddApp(int AppType) {
        int ret = -1;
        byte[] AppName = new byte[33];
        byte AidLen = 0;
        byte SelFlag = 0;
        byte Priority = 0;
        byte TargetPer = 0;
        byte MaxTargetPer = 0;
        byte FloorLimitCheck = 1;
        byte RandTransSel = 1;
        byte VelocityCheck = 1;
        long FloorLimit = 2000;
        long Threshold = 0;
        byte[] TACDenial = new byte[6];
        byte[] TACOnline = new byte[6];
        byte[] TACDefault = new byte[6];
        byte[] AcquierId = new byte[7];
        byte[] dDOL = new byte[256];
        byte[] tDOL = new byte[256];
        byte[] Version = new byte[3];
        byte[] RiskManData = new byte[10];
        byte EC_bTermLimitCheck = 0;

        long EC_TermLimit = 0;
        long CL_bStatusCheck = 0;
        long CL_FloorLimit = 0;
        long CL_TransLimit = 0;
        long CL_CVMLimit = 0;
        byte TermQuali_byte2 = 0;


        byte[] TACDenial_1 = {(byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] TACOnline_1 = {(byte) 0xD8, (byte) 0x40, (byte) 0x04, (byte) 0xF8, (byte) 0x00};
        byte[] TACDefault_1 = {(byte) 0xD8, (byte) 0x40, (byte) 0x00, (byte) 0xA8, (byte) 0x00};
        byte[] AcquierId_1 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
        byte[] dDOL_1 = {(byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04};
        byte[] tDOL_1 = {(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04};
        byte[] Version_1 = {(byte) 0x00, (byte) 0x8c};

        System.arraycopy(TACDenial_1, 0, TACDenial, 0, TACDenial_1.length);
        System.arraycopy(TACOnline_1, 0, TACOnline, 0, TACOnline_1.length);
        System.arraycopy(TACDefault_1, 0, TACDefault, 0, TACDefault_1.length);
        System.arraycopy(AcquierId_1, 0, AcquierId, 0, AcquierId_1.length);
        System.arraycopy(dDOL_1, 0, dDOL, 0, dDOL_1.length);
        System.arraycopy(tDOL_1, 0, tDOL, 0, tDOL_1.length);
        System.arraycopy(Version_1, 0, Version, 0, Version_1.length);

        if (AppType != 1) {
            {
                AidLen = 5;
                byte[] AID1 = {(byte) 0xA1, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                FloorLimitCheck = 1;
                EMV_APPLIST EMV_TEST_ANOD = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);

                ret = api.EmvLib_AddApp(EMV_TEST_ANOD);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }

            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 16;
                FloorLimitCheck = 1;
                EMV_APPLIST EMV_TEST_ANOE = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(EMV_TEST_ANOE);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //EMV_TEST_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x99, (byte) 0x90, (byte) 0x90};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                EMV_APPLIST EMV_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(EMV_TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //JCB_TEST_APP
            {
                byte[] AID1 = {(byte) 0xF1, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x01, (byte) 0x23};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                EMV_APPLIST JCB_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(JCB_TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //VSDC_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                EC_bTermLimitCheck = 1;
                EC_TermLimit = 100000;
                EMV_APPLIST VSDC_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(VSDC_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //VSDC_APP3
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x03};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;
                EMV_APPLIST VSDC_APP3 = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(VSDC_APP3);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //VSDC_APP4
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x04};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;
                EMV_APPLIST VSDC_APP4 = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(VSDC_APP4);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //VSDC_APP5
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x05};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;
                EMV_APPLIST VSDC_APP5 = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(VSDC_APP5);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //VSDC_APP6
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x06};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;
                EMV_APPLIST VSDC_APP6 = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(VSDC_APP6);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //VSDC_APP7
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x07};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;
                EMV_APPLIST VSDC_APP7 = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(VSDC_APP7);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //TEST_APP
            {
                byte[] AID1 = {(byte) 0xd1, (byte) 0x56, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                byte[] Version1 = new byte[3];
                byte[] Version2 = {(byte) 0x00, (byte) 0x01};
                System.arraycopy(Version2, 0, Version1, 0, Version2.length);
                EMV_APPLIST TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version1, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //EMV_TEST2_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x99, (byte) 0x99, (byte) 0x01};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 6;
                FloorLimitCheck = 1;
                EMV_APPLIST EMV_TEST2_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(EMV_TEST2_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //JCB_TEST2_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x65, (byte) 0x10, (byte) 0x10};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                EMV_APPLIST JCB_TEST2_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(JCB_TEST2_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //MASTER_TEST_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x10, (byte) 0x10};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                EMV_APPLIST MASTER_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(MASTER_TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //AMEX_TEST_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x25, (byte) 0x01, (byte) 0x05, (byte) 0x01};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;

                byte[] Version1 = new byte[3];
                byte[] Version2 = {(byte) 0x00, (byte) 0x01};
                System.arraycopy(Version2, 0, Version1, 0, Version2.length);
                byte[] TACDenial1 = new byte[6];
                byte[] TACOnline1 = new byte[6];
                byte[] TACDefault1 = new byte[6];
                byte[] AcquierId1 = new byte[7];
                byte[] dDOL1 = new byte[256];
                byte[] tDOL1 = new byte[256];

                byte[] TACDenial2 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                byte[] TACOnline2 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                byte[] TACDefault2 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                byte[] AcquierId2 = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
                byte[] dDOL2 = {(byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04};
                byte[] tDOL2 = {(byte) 0x9F, (byte) 0x37, (byte) 0x04};

                System.arraycopy(TACDenial2, 0, TACDenial1, 0, TACDenial2.length);
                System.arraycopy(TACOnline2, 0, TACOnline1, 0, TACOnline2.length);
                System.arraycopy(TACDefault2, 0, TACDefault1, 0, TACDefault2.length);
                System.arraycopy(AcquierId2, 0, AcquierId1, 0, AcquierId2.length);
                System.arraycopy(dDOL2, 0, dDOL1, 0, dDOL2.length);
                System.arraycopy(tDOL2, 0, tDOL1, 0, tDOL2.length);


                EMV_APPLIST AMEX_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial1,
                        TACOnline1, TACDefault1, AcquierId1, dDOL1,
                        tDOL1, Version1, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(AMEX_TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //DISCOVER_TEST_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x52, (byte) 0x30, (byte) 0x10};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 7;
                FloorLimitCheck = 1;
                byte[] Version1 = new byte[3];
                byte[] Version2 = {(byte) 0x00, (byte) 0x01};
                System.arraycopy(Version2, 0, Version1, 0, Version2.length);
                EMV_APPLIST DISCOVER_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version1, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(DISCOVER_TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }
            //CUP_TEST_APP
            {
                byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x01, (byte) 0x02};
                byte[] AID = new byte[17];
                System.arraycopy(AID1, 0, AID, 0, AID1.length);
                AidLen = 8;
                FloorLimitCheck = 1;
                byte[] Version1 = new byte[3];
                byte[] Version2 = {(byte) 0x00, (byte) 0x30};
                System.arraycopy(Version2, 0, Version1, 0, Version2.length);
                EMV_APPLIST CUP_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                        Priority, TargetPer, MaxTargetPer,
                        FloorLimitCheck, RandTransSel, VelocityCheck,
                        FloorLimit, Threshold, TACDenial,
                        TACOnline, TACDefault, AcquierId, dDOL,
                        tDOL, Version1, RiskManData, EC_bTermLimitCheck,
                        EC_TermLimit, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                        CL_CVMLimit, TermQuali_byte2);
                ret = api.EmvLib_AddApp(CUP_TEST_APP);
                if (ret != 0) {
                    Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                    return ret;
                }
            }

        }

        //PBOC_TEST_APP
        {
            byte[] AID1 = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x1};
            byte[] AID = new byte[17];
            System.arraycopy(AID1, 0, AID, 0, AID1.length);
            AidLen = 7;
            FloorLimitCheck = 1;
            byte[] Version1 = new byte[3];
            byte[] Version2 = {(byte) 0x00, (byte) 0x30};
            System.arraycopy(Version2, 0, Version1, 0, Version2.length);
            byte EC_bTermLimitCheck1 = 1;
            long EC_TermLimit1 = 100000;
            EMV_APPLIST PBOC_TEST_APP = new EMV_APPLIST(AppName, AID, AidLen, SelFlag,
                    Priority, TargetPer, MaxTargetPer,
                    FloorLimitCheck, RandTransSel, VelocityCheck,
                    FloorLimit, Threshold, TACDenial,
                    TACOnline, TACDefault, AcquierId, dDOL,
                    tDOL, Version1, RiskManData, EC_bTermLimitCheck1,
                    EC_TermLimit1, CL_bStatusCheck, CL_FloorLimit, CL_TransLimit,
                    CL_CVMLimit, TermQuali_byte2);
            ret = api.EmvLib_AddApp(PBOC_TEST_APP);
            if (ret != 0) {
                Log.v(TAG, "EmvLib_AddApp: ret = " + ret);
                return ret;
            }
        }
        return 0;
    }

    public static String decodecardNo(String track) {
        int start = 0;
        if (track != null && track.trim().length() > 0) {

            int track2_end = track.indexOf("=");
            for (int i = 0; i < track.length(); i++) {
                if (Character.isDigit(track.charAt(i))) {
                    start = i;
                    break;
                }
            }
            String cardNo = track.substring(start, track2_end);
            return cardNo;
        }
        return "";
    }

    public static String getCardNo(String track2) {
        if (track2 == null)
            return null;
        int sta = 0;
        int end = track2.indexOf("D");
        if (end != -1) {
            String cardno = track2.substring(sta, end);
            return cardno;
        }
        return null;
    }

    public static byte[] transDate(String data) {

        int year = Integer.valueOf(data.substring(0, 2));
        int month = Integer.valueOf(data.substring(2, 4));
        int day = Integer.valueOf(data.substring(4, 6));
        int hour = Integer.valueOf(data.substring(6, 8));
        int min = Integer.valueOf(data.substring(8, 10));
        int sec = Integer.valueOf(data.substring(10, 12));
        byte[] datatime = new byte[6];
        datatime[0] = (byte) year;
        datatime[1] = (byte) month;
        datatime[2] = (byte) day;
        datatime[3] = (byte) hour;
        datatime[4] = (byte) min;
        datatime[5] = (byte) sec;

        return datatime;
    }

}
