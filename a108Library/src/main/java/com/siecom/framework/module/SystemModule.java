package com.siecom.framework.module;

import android.util.Log;

import com.siecom.framework.appinterface.APDU_RESP;
import com.siecom.framework.appinterface.APDU_SEND;
import com.siecom.tools.ByteTool;

/**
 * Created by Administrator on 2015/12/15.
 */
public class SystemModule extends CommonModule{
    private static SystemModule module = null;
    private static final String TAG = "SystemModule";

    private SystemModule() {


    }

    public static SystemModule getInstance() {

        if (module == null) {
            module = new SystemModule();
        }
        return module;

    }
     public static int writeCodeName(String codeName){
         if(codeName.getBytes().length<=0||codeName.getBytes().length>16){
           return -6;
         }
         byte[] code = new byte[16];
         System.arraycopy(codeName.getBytes(),0,code,0,codeName.getBytes().length);
         return api.Sys_writeSN(code);
     }

    public static String readCodeName(){
        byte[] code = new byte[16];
        int ret = api.Sys_GetSN(code);
        if(ret!=0){
            return null;
        }
        return new String(code).trim();
    }
    public static String readSerialNo(){
        byte[] SN = new byte[16];
        int ret = api.SysUniqId(SN);
        Log.e("readSerialNo:", "ret:"+ret+"---"+ByteTool.byte2hex(SN));
        return ByteTool.byte2hex(SN);
    }
    public static void beep(){
        new Thread(new Runnable() {
            @Override
            public void run() {
               api.Lib_Beep();
            }
        }).start();
    }

    public static int testPsamSlot(final byte slot){
        byte[] ATD = new byte[100];
        int ret =-1;
        try {
            ret = api.Icc_Open(slot, (byte) 0x01, ATD);
            if (ret != 0) {
                Log.e("Icc_Open", ret + "!!");
                return ret;
            }
            byte sendData[] = new byte[530];
            byte cmd[] = new byte[4];
            cmd[0] = 0x00; // 0-3 cmd
            cmd[1] = (byte) 0xa4;
            cmd[2] = 0x04;
            cmd[3] = 0x00;
            short Lc = 0x0e;
            short Le = 256;
            byte[] dataIn = new byte[512];
            // 02 B7 07 00 14 00 00 A4 04 00 00 0E 31 50 41 59 2E 53 59 53 2E 44 44
            // 46 30 B4 82
            String sendmsg = "1PAY.SYS.DDF01";
            sendData = sendmsg.getBytes();
            System.arraycopy(sendData, 0, dataIn, 0, sendData.length);
            APDU_SEND ApduSend = new APDU_SEND(cmd, Lc, dataIn, Le);
            APDU_RESP ApduResp = new APDU_RESP();
            ret = api.Icc_Command(slot, ApduSend, ApduResp);
            Log.e("testPsamSlot[ run ]", "Command ret = " + ret);
        }finally {
            api.Icc_Close(slot);
        }
        return ret;

    }

}
