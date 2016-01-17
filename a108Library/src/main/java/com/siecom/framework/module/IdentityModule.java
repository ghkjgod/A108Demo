package com.siecom.framework.module;

import android.graphics.Bitmap;
import android.util.Log;

import com.siecom.framework.bean.IdentityInfoBean;
import com.siecom.framework.appinterface.APDU_RESP;
import com.siecom.framework.channel.ChannelInstance;
import com.siecom.framework.listen.IdentityListen;
import com.siecom.tools.SingletonThreadPool;
import com.siecom.tools.WltLib;

import java.util.concurrent.ExecutorService;

/**
 * Created by zhq on 2015/12/10.
 */
public class IdentityModule extends CommonModule {
    private IdentityInfoBean bean;
    private final static String TAG = "IdentityModule";
    private byte[] recvBuf = new byte[4096];
    private static IdentityModule module = null;
    private ReadCardThread VaIDCard_Thread = null;
    private IdentityListen callback = null;
    private static ExecutorService threadPool = SingletonThreadPool.getInstance();
    private boolean lockFlag = false;
    private IdentityModule() {

    }

    @Override
    public boolean isFinish() {
        if (VaIDCard_Thread == null || VaIDCard_Thread.isThreadFinished()) {
            return true;
        } else {
            return false;
        }
    }

    public void setCallback(IdentityListen callback) {
        this.callback = callback;
    }

    public static IdentityModule getInstance() {
        if (module == null) {
            module = new IdentityModule();
        }
        return module;
    }

    public void startRead(final boolean withFinger) {
        Log.e(TAG, "startRead");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (null != VaIDCard_Thread && !VaIDCard_Thread.isThreadFinished()) {
                    VaIDCard_Thread.setFlag(false);
                    while (!VaIDCard_Thread.isThreadFinished()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                while (lockFlag){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                VaIDCard_Thread = null;
                bean = new IdentityInfoBean();
                VaIDCard_Thread = new ReadCardThread();
                VaIDCard_Thread.setWithFinger(withFinger);
                VaIDCard_Thread.start();
            }
        };
        threadPool.submit(r);
    }

    @Override
    public int OpenModule() {
        int ret = 0;
        for (int p = 0; p < 5; p++) {
            ret = api.IDCard_Open();
            if (ret == 0) {
                break;
            } else {
                api.IDCard_Close();
                ChannelInstance.resetChannel();
            }
        }
        return ret;
    }

    @Override
    public void closeModule(boolean isConnected) {
        Log.e(TAG, "closeModule");
        if (null != VaIDCard_Thread && !VaIDCard_Thread.isThreadFinished()) {
            VaIDCard_Thread.setFlag(false);
            while (!VaIDCard_Thread.isThreadFinished()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (isConnected) {
            lockFlag = true;
            api.IDCard_Close();
            lockFlag =false;
        }
    }

    public class ReadCardThread extends Thread {
        private boolean m_bThreadFinished = false;
        public String m_strName = "";
        public String m_strGender = "";
        public String m_strNation = "";
        public String m_strBornDate = "";
        public String m_strAddress = "";
        public String m_strNumber = "";
        public String m_strAuthor = "";
        public String m_strPeriodFrom = "";
        public String m_strPeriodTo = "";
        private boolean runFlag = true;
        private boolean withFinger = false;

        public void setWithFinger(boolean withFinger) {
            this.withFinger = withFinger;
        }

        public boolean isWithFinger() {
            return withFinger;
        }

        public boolean isThreadFinished() {
            return m_bThreadFinished;
        }

        public ReadCardThread() {
            runFlag = true;
        }

        public void setFlag(boolean flag) {
            runFlag = flag;
        }

        @Override
        public void run() {
            try {
                int ret = 1;
                ret = OpenModule();
                if (ret != 0) {
                    if (callback != null) {
                        callback.onReadFail(-884, "身份证模块打开失败");
                    }
                    api.IDCard_Close();
                    m_bThreadFinished = true;
                    return;
                }
                Log.d("ReadCardThread[ run ]", "run() begin");
                if (callback != null) {
                    callback.onStart();
                }
                int i = 0;
                while (runFlag) {
                    synchronized (this) {
                        APDU_RESP ApduResp = new APDU_RESP();

                        try {
                            ret = api.IDCard_Test(ApduResp);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.e(TAG, e.getMessage());
                            //失败跳过再读取
                            continue;
                        }
                        Log.e(TAG, ret + "!!");
                        if (ret == 0) {
                            String tmpstr = "";
                            int myloopi, unicode;
                            for (myloopi = 0; myloopi < 15; myloopi++) {
                                unicode = ((ApduResp.DataOut[myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            m_strName = tmpstr;
                            // 性别
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 1; myloopi++) {
                                unicode = ((ApduResp.DataOut[30 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[30 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            try {
                                int sex_num = Integer.parseInt(tmpstr);
                                tmpstr = findSexZW(sex_num);
                            } catch (NumberFormatException e) {
                                //失败跳过再读取
                                continue;

                            }
                            m_strGender = tmpstr;
                            // 民族
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 2; myloopi++) {
                                unicode = ((ApduResp.DataOut[32 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[32 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            try {
                                int nation_num = Integer.parseInt(tmpstr);
                                tmpstr = findMinzuZW(nation_num);
                            } catch (NumberFormatException e) {
                                //失败跳过再读取
                                continue;
                            }
                            m_strNation = tmpstr;
                            // 出身日期
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 8; myloopi++) {

                                unicode = ((ApduResp.DataOut[36 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[36 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);

                            }
                            m_strBornDate = tmpstr.substring(0, 4)
                                    + tmpstr.substring(4, 6)
                                    + tmpstr.substring(6, 8);
                            // 详址
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 35; myloopi++) {
                                unicode = ((ApduResp.DataOut[52 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[52 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            m_strAddress = tmpstr;
                            // 身份证号码
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 18; myloopi++) {
                                unicode = ((ApduResp.DataOut[122 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[122 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            m_strNumber = tmpstr;
                            // 签发机关
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 15; myloopi++) {
                                unicode = ((ApduResp.DataOut[158 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[158 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            m_strAuthor = tmpstr;
                            // 有限期限
                            tmpstr = "";
                            for (myloopi = 0; myloopi < 16; myloopi++) {
                                unicode = ((ApduResp.DataOut[188 + myloopi * 2 + 1] & 0xff) << 8)
                                        | (ApduResp.DataOut[188 + myloopi * 2 + 0] & 0xff);
                                tmpstr += String.valueOf((char) unicode);
                            }
                            m_strPeriodFrom = tmpstr.substring(0, 8);
                            try {
                                m_strPeriodTo = tmpstr.substring(8, 16);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                m_strPeriodTo = tmpstr.substring(8, 10);
                            }
                            System.arraycopy(ApduResp.DataOut, 256, recvBuf, 0, 1024);
                            bean.fullName = m_strName;
                            bean.icon = decodePhoto(recvBuf);
                            bean.birthday = m_strBornDate;
                            bean.idNo = m_strNumber;
                            bean.idOrg = m_strAuthor;
                            bean.beginDate = m_strPeriodFrom;
                            bean.endDate = m_strPeriodTo;
                            bean.idAddr = m_strAddress;
                            bean.nation = m_strNation;
                            bean.sex = m_strGender;

                            if (withFinger) {
                                byte[] finger = new byte[1024];
                                int[] len = new int[1];
                                int res = api.IDCardFinger(finger, len);
                                if (res == 0) {
                                    if (len[0] > 0) {
                                        bean.fingerByte = finger;
                                    } else {
                                        bean.fingerByte = null;
                                    }
                                }
                            }

                            if (callback != null) {
                                callback.onReadSucc(bean);
                            }

                            runFlag = false;
                            i = 0;
                            api.IDCard_Close();
                            callback = null;
                            break;
                        }
                        if (1 == ret) {
                            if (i > 3 && callback != null) {
                                callback.onReadFail(ret,
                                        "找卡 失败,请将身份证拿开一下，再放回检测");
                                i = 0;
                            }
                            i++;

                        }
                        if (2 == ret) {
                            if (i > 3 && callback != null) {
                                callback.onReadFail(ret,
                                        "选卡 失败 ,请将身份证拿开一下，再放回检测");
                                i = 0;
                            }
                            i++;

                        }
                        if (3 == ret) {
                            if (i > 3 && callback != null) {
                                callback.onReadFail(ret,
                                        "选卡 失败 ,请将身份证拿开一下，再放回检测");
                                i = 0;
                            }
                            i++;
                        }
                    }
                }
            } finally {
                Log.e(TAG,"m_bThreadFinished = true");
                m_bThreadFinished = true;
            }
        }

    }

    ;

    private static String findSexZW(int sexcode) {
        if (sexcode == 0)
            return "未知";
        else if (sexcode == 1)
            return "男";
        else if (sexcode == 2)
            return "女";
        else if (sexcode == 9)
            return "未说明";
        return "未说明";
    }

    private static String[][] Nations = new String[][]{{"无"}, {"汉"},
            {"蒙古"}, {"回"}, {"藏"}, {"维吾尔"}, {"苗"}, {"彝"}, {"壮"},
            {"布依"}, {"朝鲜"}, {"满"}, {"侗"}, {"瑶"}, {"白"}, {"土家"},
            {"哈尼"}, {"哈萨克"}, {"傣"}, {"黎"}, {"傈僳"}, {"佤"}, {"畲"},
            {"高山"}, {"拉祜"}, {"水"}, {"东乡"}, {"纳西"}, {"景颇"},
            {"柯尔克孜"}, {"土"}, {"达斡尔"}, {"仫佬"}, {"羌"}, {"布朗"},
            {"撒拉"}, {"毛南"}, {"仡佬"}, {"锡伯"}, {"阿昌"}, {"普米"},
            {"塔吉克"}, {"怒"}, {"乌孜别克"}, {"俄罗斯"}, {"鄂温克"}, {"德昂"},
            {"保安"}, {"裕固"}, {"京"}, {"塔塔尔"}, {"独龙"}, {"鄂伦春"},
            {"赫哲"}, {"门巴"}, {"珞巴"}, {"基诺"}, {"其他"}, {"外国血"}};

    private static String findMinzuZW(int minzucode) {
        if (minzucode > 59)
            return "";
        return Nations[minzucode][0];
    }

    public Bitmap decodePhoto(byte[] data) {
        // 解码头像
        byte[] byWlt = recvBuf;
        Bitmap bmp = WltLib.parsePhotoOther(byWlt);
        return bmp;
    }

}
