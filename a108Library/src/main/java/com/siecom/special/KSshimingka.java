package com.siecom.special;

import android.util.Log;

import com.siecom.framework.appinterface.APDU_RESP;
import com.siecom.framework.appinterface.APDU_SEND;
import com.siecom.framework.appinterface.Api;
import com.siecom.tools.ByteTool;


public class KSshimingka {
    private static final String TAG = "KSshimingka";
    private static KSshimingka module = new KSshimingka();
    private Api api;
    private ReadLoadThread readThread = null;
    private KsListen callback = null;
    public static  interface  KsListen{
         void onSucced(byte[] back);
         void  onFail();
    }
    private KSshimingka() {
        api = new Api();

    }


    public static KSshimingka getInstance() {

        return module;

    }

    public void RFIintForLoad(String  cipherFlag,String amount,String terminalCode,KsListen callback) {
        stopRead();
        this.callback =callback;
        readThread = new ReadLoadThread(cipherFlag,amount,terminalCode);
        readThread.start();

    }

    public void stopRead(){

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

    }

    public  int RFCreditForLoad(String date,String time,String mac){
        String msg = date+time+mac;
        byte[] cmd = ByteTool.hexStr2Bytes("80520000");
        byte[] sendData = ByteTool.hexStr2Bytes(msg);
        Log.e(TAG,sendData.length+"!!");
        short Lc = 0x0B;
        short Le = 0x04;
        APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
        APDU_RESP ApduResp = new APDU_RESP();
        int  ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
        if (ret != 0) {
            Log.e(TAG, "Icc_Command: ret = " + ret);
            return  ret;
        }
        byte[] bf = new byte[ApduResp.LenOut];
        System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
        Log.e("bf", ByteTool.byte2hex(bf));
        Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
        Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
        if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
            Log.e(TAG, "Icc_Command fail: ret = " + ret);
            return  -1;
        }
        return 0;
    }
    private class ReadLoadThread extends Thread {
        private boolean m_bThreadFinished = false;
        private boolean runFlag = true;
        private String  cipherFlag;
        private String amount;
        private String terminalCode;

        public  ReadLoadThread(String  cipherFlag,String amount,String terminalCode){
             this.amount = amount;
             this.cipherFlag = cipherFlag;
             this.terminalCode = terminalCode;
        }
        public boolean isThreadFinished() {

            return m_bThreadFinished;
        }

        public void setFlag(boolean flag) {

            runFlag = flag;

        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    api.Icc_Close((byte) 0x00);
                    byte[] ATR = new byte[100];
                    int  ret = api.Icc_Open((byte) 0x00, (byte) 0x01, ATR);//IC卡上电
                    while (runFlag) {
                        ret = api.Icc_Detect((byte) 0x00);
                        if (0 == ret) {
                            Log.e(TAG, "Icc_Detect");
                            String msg = cipherFlag+amount+terminalCode;
                            byte[] cmd = ByteTool.hexStr2Bytes("80500002");
                            byte[] sendData = ByteTool.hexStr2Bytes(msg);
                            Log.e(TAG,sendData.length+"!!");
                            short Lc = 0x0B;
                            short Le = 0x10;
                            APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
                            APDU_RESP ApduResp = new APDU_RESP();
                            ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
                            if (ret != 0) {
                                Log.e(TAG, "Icc_Command: ret = " + ret);
                                if(callback!=null){
                                    callback.onFail();
                                }
                                break;
                            }
                            byte[] bf = new byte[ApduResp.LenOut];
                            System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
                            Log.e("bf", ByteTool.byte2hex(bf));
                            Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
                            Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
                            if(ApduResp.SWA!=(byte)0x90||ApduResp.SWB!=(byte)0x00){
                                Log.e(TAG, "Icc_Command fail: ret = " + ret);
                                if(callback!=null){
                                    callback.onFail();
                                }
                                break;
                            }
                            if(callback!=null){
                                callback.onSucced(bf);
                            }
                            break;
                        }
                    }
                } finally {
                    Log.e(TAG, "m_bThreadFinished = true");
                    m_bThreadFinished = true;
                }
            }

        }
    }

    public void close(){

        api.Icc_Close((byte)0x00);
    }

    public int getCardInfo(byte[] cardNo, byte[] name, byte[] idCardNO) {
        try {
            byte[] ATR = new byte[100];
            int ret = api.Icc_Open((byte) 0x00, (byte) 0x01, ATR);
            if (ret != 0) {
                Log.e("ret", "Icc_Open: ret = " + ret);
                return ret;
            }
            byte[] cmd = ByteTool.hexStr2Bytes("00A40200");
            byte[] sendData = ByteTool.hexStr2Bytes("EF05");
            short Lc = 0x02;
            short Le = 256;

            APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
            APDU_RESP ApduResp = new APDU_RESP();

            ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
            if (ret != 0) {
                Log.e("ret", "selectCardNo: ret = " + ret);
                return ret;
            }

            byte[] bf = new byte[ApduResp.LenOut];
            System.arraycopy(ApduResp.DataOut, 0, bf, 0, ApduResp.LenOut);
            Log.e("bf", ByteTool.byte2hex(bf));
            Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
            Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));

            if (ApduResp.SWA == (byte) 0x90 && ApduResp.SWB == (byte) 0x00) {

                cmd = ByteTool.hexStr2Bytes("00B20704");
                sendData = ByteTool.hexStr2Bytes("");
                Lc = 0x00;
                Le = 256;

                ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
                ApduResp = new APDU_RESP();

                ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
                Log.e("ret:", ret + "!!");

                System.arraycopy(ApduResp.DataOut, 1, cardNo, 0, ApduResp.LenOut);

                Log.e("cardNo", new String(cardNo).trim());
                Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
                Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));

                if (ret != 0) {
                    Log.e("ret", "getCardNo: ret = " + ret);
                    return ret;
                }

            } else {

                return -9;
            }

            cmd = ByteTool.hexStr2Bytes("00A40200");
            sendData = ByteTool.hexStr2Bytes("EF06");
            Lc = 0x02;
            Le = 256;

            ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
            ApduResp = new APDU_RESP();

            ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
            if (ret != 0) {
                Log.e("ret", "selectName: ret = " + ret);
                return ret;
            }
            cmd = ByteTool.hexStr2Bytes("00B20204");
            sendData = ByteTool.hexStr2Bytes("");
            Lc = 0x00;
            Le = 256;

            ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
            ApduResp = new APDU_RESP();

            ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
            if (ret != 0) {
                Log.e("ret", "getName: ret = " + ret);
                return ret;
            }
            if (ApduResp.SWA == (byte) 0x90 && ApduResp.SWB == (byte) 0x00) {
                bf = new byte[ApduResp.LenOut];
                System.arraycopy(ApduResp.DataOut, 1, name, 0, ApduResp.LenOut);

                Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
                Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
            } else {

                return -9;
            }
            cmd = ByteTool.hexStr2Bytes("00B20104");
            sendData = ByteTool.hexStr2Bytes("");
            Lc = 0x00;
            Le = 256;

            ApduSend = new APDU_SEND(cmd, Lc, sendData, Le);
            ApduResp = new APDU_RESP();

            ret = api.Icc_Command((byte) 0x00, ApduSend, ApduResp);
            if (ret != 0) {
                Log.e("ret", "getName: ret = " + ret);
                return ret;
            }
            if (ApduResp.SWA == (byte) 0x90 && ApduResp.SWB == (byte) 0x00) {
                bf = new byte[ApduResp.LenOut];
                System.arraycopy(ApduResp.DataOut, 1, idCardNO, 0, ApduResp.LenOut);

                Log.e("SWA", ByteTool.byteToHexString(ApduResp.SWA));
                Log.e("SWB", ByteTool.byteToHexString(ApduResp.SWB));
            } else {

                return -9;
            }
        } finally {

            api.Icc_Close((byte) 0);
        }
        return 0;

    }

}
