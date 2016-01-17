package com.siecom.framework.module;

import android.util.Log;

import com.siecom.framework.channel.ChannelInstance;
import com.siecom.framework.listen.KeyBroadListen;
import com.siecom.tools.SerializeUtil;
import com.siecom.tools.SingletonThreadPool;
import com.siecom.tools.StringUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class KeyBroadModule extends CommonModule {

    private final static String TAG = "KeyBoradModule";
    public static final int OP_INPUTTING = 1;
    public static final int OP_CANCEL = -1;
    public static final int OP_FINISH = 0;
    public static final int OPCODE_MAIN_KEY = 0;
    public static final int OPCODE_WORK_KEY = 1;
    public static final int OPCODE_GET_PIN = 5;
    private static KeyBroadModule module = null;
    private KeyBroadListen callback = null;
    private ReadThread reader = null;
    private byte[] keyData = null;
    private KeyBroadOption option = null;
    private int pin_input_flag = 0;
    private int pin_input_num = 0;
    private Timer timer = null;
    private static ExecutorService threadPool = SingletonThreadPool.getInstance();
    private boolean lockFlag = false;
    private void startCountDown(int timeOut) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onFail(-880, "maybe disConnect!!");
                timer.cancel();
                closeModule(true);
            }
        };
        timer = new Timer();
        timer.schedule(task, (timeOut + 10) * 1000);
    }
    @Override
    public boolean isFinish(){
        if (reader == null || reader.isThreadFinished()) {
            return true;
        }else{
            return  false;
        }
    }
    /**
     * 获取输入内容
     *
     * @author
     */
    @Override
    public void closeModule(boolean isConnected) {
        Log.e(TAG, "closeModule");
        if (reader != null && !reader.isThreadFinished()) {
            reader.breakflag = true;
            if (reader.checkthread != null && !reader.checkthread.isThreadFinished()) {
                reader.checkthread.setRunFlag(false);
                while (!reader.isThreadFinished()&&!reader.checkthread.isThreadFinished()){
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                reader.checkthread = null;
            }
            reader = null;
        }
        if (callback != null) {
            callback = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        if (isConnected){
            lockFlag = true;
            api.GetPin_quit();
            lockFlag = false;
        }

    }

    public int GetPinStarKey() {
        Log.i(TAG, "getPinKey, wait for input key...");
        while (pin_input_flag == 0) {
            msleep(1);
        }
        pin_input_flag = 0;
        Log.i(TAG, "getPinKey pin_input_num = " + pin_input_num);
        return pin_input_num;
    }

    public class checkThread extends Thread {
        int keyNum;
        byte m_Mode = 0;
        byte m_MaxLen;
        private boolean runFlag = true;

        public void setRunFlag(boolean runflag) {
            this.runFlag = runflag;
        }

        private boolean m_bThreadFinished = false;

        public void setMode(byte Mode) {
            this.m_Mode = Mode;
        }

        public void setMaxLen(byte MaxLen) {
            this.m_MaxLen = MaxLen;
        }

        public boolean isThreadFinished() {
            return this.m_bThreadFinished;
        }

        @Override
        public void run() {
            Log.e(TAG, "checkThread run() begin");
            synchronized (this) {
                do {
                    try {
                        this.keyNum = GetPinStarKey();
                    } catch (Exception e) {
                        break;
                    }
                    if (this.keyNum == 59) {

                        break;
                    }
                    if (callback != null) {
                        callback.onKeyNum(keyNum);
                    }

                } while (runFlag
                        && ((this.m_Mode < 16) || (this.keyNum != this.m_MaxLen)));
                m_bThreadFinished = true;
            }
        }

    }

    public void startRun(final KeyBroadListen callback, final int opCode, final KeyBroadOption option) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
            closeModule(false);
            while (lockFlag){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            KeyBroadModule.this.callback = callback;
                KeyBroadModule.this.option = option;
            reader = new ReadThread(opCode);
            reader.start();
            }
        };
        threadPool.submit(r);
    }

    public class ReadThread extends Thread {
        private int mOpCode, ret;
        private checkThread checkthread = null;
        private boolean m_bThreadFinished = false;
        public boolean breakflag = false;

        public boolean isThreadFinished() {
            return m_bThreadFinished;
        }

        public ReadThread(int OpCode) {
            mOpCode = OpCode;
        }

        @Override
        public void run() {
            Log.e(TAG, "readThread_Run");

            synchronized (this) {
                try {
                    ret = handShark();
                    if (ret != 0) {
                        Log.e(TAG, "handShake failed ret = " + ret);
                        if (callback != null)
                            callback.onFail(ret, "密码键盘握手失败，请重新点击");
                        m_bThreadFinished = true;
                        return;
                    }
                    if (callback != null)
                        callback.onStart();
                    switch (mOpCode) {
                        case OPCODE_MAIN_KEY:
                            ret = inputMkey();
                            if (ret == 0) {
                                Log.e(TAG, "writePinMainKeySuccess");

                            } else {
                                Log.e(TAG, "writePinMainKeyfailed, ret = " + ret);
                                if (callback != null)
                                    callback.onFail(ret, "Pci_WritePinMKey failed");
                            }
                            break;

                        case OPCODE_WORK_KEY:
                            ret = inputWkey();
                            if (ret == 0) {
                                Log.d(TAG, "WriteWorkKey_success");


                            } else {
                                Log.e(TAG, "Pci_WritePinKey failed, ret = " + ret);
                                if (callback != null)
                                    callback.onFail(ret, "WritePinKey");
                            }
                            break;

                        case OPCODE_GET_PIN:
                            byte[] pinBlock = new byte[8];
                            if (getPin(pinBlock) == 0) {
                                if (callback != null) {
                                    callback.onReadPin(pinBlock);
                                }
                            } else if (ret == -7007) {
                                if (callback != null) {
                                    callback.onCancel();
                                }
                            } else if (ret == -7009) {
                                if (callback != null) {
                                    callback.onFail(ret, "get_pin_time_out!!");
                                }
                            } else {
                                if (callback != null) {
                                    callback.onFail(ret, "getPin fail");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }finally {
                    m_bThreadFinished = true;
                    Log.e(TAG,"m_bThreadFinished = "+m_bThreadFinished+"!!!");
                }

            }
        }

        private int inputWkey() {
            KeyBroadOption unit = option;
            keyData = unit.getkeyData();
            byte[] checkData = new byte[16];
            int[]  len = new int[1];
            try {
                ret = api.Pci_WritePinKey((byte) unit.getWorkKeyNo(),
                        (byte) keyData.length, keyData, unit.getMode(),
                        (byte) unit.getMainKeyNo(),checkData,len);
                if (ret == 0) {
                    if (callback != null)
                        callback.onSucc(checkData);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, e.getMessage());
                if (callback != null)
                    callback.onFail(e.hashCode(), e.getMessage());
            }
            return ret;
        }

        private int inputMkey() {
            KeyBroadOption unit = option;
            keyData = unit.getkeyData();
            try {
                ret = api.Pci_WritePinMKey((byte) unit.getMainKeyNo(),
                        (byte) keyData.length, keyData);
                if (ret == 0) {
                    if (callback != null)
                        callback.onSucc(new byte[16]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, e.getMessage());
                if (callback != null)
                    callback.onFail(e.hashCode(), e.getMessage());
            }
            return ret;
        }

        private int handShark() {
            try {
                for (int i = 0; i < 3; i++) {
                    ret = api.Handshake();
                    if (ret == 0) {
                        break;
                    }
                    ChannelInstance.resetChannel();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                if (callback != null)
                    callback.onFail(e.hashCode(), e.getMessage());
                m_bThreadFinished = true;
            }
            return ret;
        }

        private int getPin(byte[] pinBlock) {
            KeyBroadOption unit = option;
            byte mode = unit.getMode();
            byte keyno = (byte) unit.getMainKeyNo();
            byte maxLen = unit.getMaxLen();
            int TimeOut = unit.getTimeOut();
            if (mode == 0x00) {
                byte[] cardNo_b = unit.cardNo.getBytes();
                byte[] iAmount_b = new byte[30];
                if (unit.iAmount.length() > 0) {
                    byte[] c = unit.iAmount.getBytes();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < c.length; i++) {// 输出结果
                        sb.append(Integer.toHexString(c[i] & 0XFF));

                    }
                    while (sb.length()<28){
                        sb.append("0");
                    }
                    Log.e(TAG, "iAmount:" + sb.toString().length());
                    iAmount_b = StringUtil.hexStringToBytes(sb.toString());
                }
                try {
                    ret = api.Pci_GetPin(keyno, (byte) 4, (byte) maxLen, mode,
                            (byte) TimeOut, cardNo_b, (byte) 1, iAmount_b);

                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    m_bThreadFinished = true;

                }
            }
            if (mode == 0x03) {
                try {
                    ret = api.Pci_GetPin(keyno, (byte) 4, (byte) maxLen, mode,
                            (byte) TimeOut, new byte[13], (byte) 1,
                            new byte[30]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    m_bThreadFinished = true;

                }
            }

            startCountDown(TimeOut);

            if (ret == 0) {// 启动成功
                if (this.checkthread != null) {
                    this.checkthread.setRunFlag(false);
                    this.checkthread.interrupt();
                    this.checkthread = null;
                }
                this.checkthread = new checkThread();
                this.checkthread.setMode(mode);
                this.checkthread.setMaxLen(maxLen);
                this.checkthread.start();
                int ch = 0;
                do {
                    do {
                        ch = api.GetPinStarNum();
                    } while (ch == -1 && !breakflag);
                    pin_input_num = ch;
                    pin_input_flag = 1;
                } while ((ch != 59) && ((mode < 16) || (ch < maxLen)) && !breakflag);
                if(!breakflag) {
                    ret = api.readPIN(pinBlock);
                }else{
                    ret = -7008;
                }
                Log.e("readPin_ret:", "====" + ret + "====");
                if (timer != null) {
                    timer.cancel();
                }
            }
            return ret;

        }

    }

    private KeyBroadModule() {

    }

    public static KeyBroadModule getInstance() {

        if (module == null) {
            module = new KeyBroadModule();
        }
        return module;
    }

    public void msleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
