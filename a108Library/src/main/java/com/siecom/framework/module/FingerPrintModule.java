package com.siecom.framework.module;

import android.util.Base64;
import android.util.Log;

import com.siecom.framework.channel.ChannelInstance;
import com.siecom.framework.constconfig.Config;
import com.siecom.framework.appinterface.APDU_RESP;
import com.siecom.framework.listen.FingerPrintListen;
import com.siecom.tools.ByteTool;
import com.siecom.tools.FingerUnit;
import com.siecom.tools.SingletonThreadPool;

import java.util.concurrent.ExecutorService;

/**
 * Created by zhq on 2015/12/10.
 */
public class FingerPrintModule extends CommonModule {
    private final String tag = "FingerPrintModule";
    public int slot[] = new int[20];
    private FingerPrintListen callback;
    private static FingerPrintModule module = null;
    private FingerPrintThread Finger_Thread = null;
    private static int fingerType = 0;
    private static ExecutorService threadPool = SingletonThreadPool.getInstance();
    private boolean lockFlag = false;
    private FingerPrintModule() {

    }
    @Override
    public void closeModule(boolean isConnected) {
        Log.e(tag, "closeModule");
        if (null != Finger_Thread && !Finger_Thread.isThreadFinished()) {
            Finger_Thread.setFlag(false);
            while (!Finger_Thread.isThreadFinished()){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            Finger_Thread = null;
        }
        if (isConnected) {
            lockFlag = true;
            api.TCD_Close();
            lockFlag = false;
        }
    }

    @Override
    public boolean isFinish() {
        if (Finger_Thread == null || Finger_Thread.isThreadFinished()) {
            return true;
        } else {
            return false;
        }
    }

    public void startRead(final int printType) {
        Log.e(tag,"startRead");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (null != Finger_Thread && !Finger_Thread.isThreadFinished()) {
                    Finger_Thread.setFlag(false);
                    while (!Finger_Thread.isThreadFinished()){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                    Finger_Thread = null;
                }
                while (lockFlag){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                fingerType = printType;
                Finger_Thread = new FingerPrintThread();
                Finger_Thread.start();
                Log.e(tag, "start!!!!");
            }
        };
        threadPool.submit(r);
    }

    public void setCallback(FingerPrintListen callback) {
        this.callback = callback;
    }

    public static FingerPrintModule getInstance() {
        if (module == null) {
            module = new FingerPrintModule();
        }
        return module;
    }

    @Override
    public int OpenModule() {
        int ret = 0;
        for (int p = 0; p < 5; p++) {
            ret = api.TCD_Open();
            if (ret == 0) {
                break;
            } else {
                api.TCD_Close();
                ChannelInstance.resetChannel();
            }
        }
        return ret;
    }



    public class FingerPrintThread extends Thread {
        private boolean runFlag = true;
        private boolean m_bThreadFinished = false;

        public boolean isThreadFinished() {
            return m_bThreadFinished;
        }

        public FingerPrintThread() {
            runFlag = true;
        }

        public void setFlag(boolean flag) {
            runFlag = flag;
        }

        @Override
        public void run() {
            int ret = 1;
            ret = OpenModule();
            if (ret != 0) {
                if (callback != null) {
                    callback.onFail(-884, "指纹模块打开失败");
                }
                api.IDCard_Close();
                m_bThreadFinished = true;
                return;
            }
            if (callback != null) {
                callback.onStart();
            }
            try {
                synchronized (this) {
                    while (runFlag) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        APDU_RESP ApduResp_TCD = new APDU_RESP();
                        ret = api.TCD_Read_Test(ApduResp_TCD);
                        if (0 == ret) {
                            String strInfo = "";
                            byte[] fingerByte = new byte[ApduResp_TCD.LenOut];
                            try {
                                System.arraycopy(ApduResp_TCD.DataOut, 0, fingerByte, 0, ApduResp_TCD.LenOut);
                                Log.e("fingerCode:", ByteTool.byte2hex(fingerByte));
                                if (fingerType == Config.WELL_FINGER) {
                                    strInfo = FingerUnit.parseWellcom(fingerByte);
                                } else if (fingerType == Config.SHENGYE_FINGER) {
                                    strInfo = Base64.encodeToString(fingerByte, Base64.NO_WRAP);
                                } else {
                                    strInfo = Base64.encodeToString(fingerByte, Base64.NO_WRAP);
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                continue;
                            }
                            callback.onRead(strInfo);
                            runFlag = false;
                            break;
                        } else {

                            //callback.onFail(ret, "未读取到指纹");
                        }
                    }
                }
            } finally {
                m_bThreadFinished = true;
            }

        }


    }


}
