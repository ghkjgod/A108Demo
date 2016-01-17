package com.siecom.framework.device;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.siecom.framework.module.BankCardModule;
import com.siecom.framework.module.CommonModule;
import com.siecom.tools.SingletonThreadPool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;


/**
 * Created by zhq on 2015/12/10.
 */
public class SiecomTask {

    public TaskCallback taskCallback;
    private CommonModule module = null;
    public static final int DEFAULT_TIME_OUT = 30 * 1000;
    private int TimeOut = DEFAULT_TIME_OUT;
    private boolean isCancel = false;
    private Object params;
    private static ExecutorService threadPool = SingletonThreadPool.getInstance();
    private Timer timer;
    public Operate operate;

    public static enum Operate {
        CONNECT_BT,
        READ_IDENTITY/* 获取身份信息 */,
        FINGER_PRINT,
        INPUT_MAIN_KEY,
        INPUT_WORK_KEY,
        READ_IC_CARD,
        READ_PIC_CARD,
        READ_MSR_CARD,
        READ_AUTO_FIND,
        GET_PIN,
    }

    public SiecomTask(Operate operate, TaskCallback taskCallback) {
        this.operate = operate;
        this.taskCallback = taskCallback;
    }


    public void deliverStart(Bundle bundle) {

        if (null != taskCallback && !isCancel) {
            taskCallback.onStart(bundle);
        }
    }

    /**
     * @param bundle 利用Handler 保證該方法在Mian綫程執行
     */
    public void deliverResponse(Bundle bundle) {

        if (null != timer) {
            timer.cancel();
        }
        if (null != taskCallback && !isCancel) {
            taskCallback.onSucceed(bundle);
        }
    }

    /**
     * code 错误码 message 错误文字描述的原因 put在Bundle
     *
     * @param bundle 利用Handler 保證該方法在Mian綫程執行 bundle.putString("message","错误原因")
     */
    public void deliverError(Bundle bundle) {
        if (null != timer) {
            timer.cancel();
        }
        if (null != taskCallback && !isCancel) {
            taskCallback.onError(bundle);
        }
    }

    /**
     * 得到任務超時時間（毫秒）
     *
     * @return
     */
    public int getTimeOut() {
        return TimeOut;
    }

    /**
     * 設置任務超時時間（毫秒）
     *
     * @param timeOut
     */
    public void setTimeOut(int timeOut) {
        if (timeOut < DEFAULT_TIME_OUT) {
            timeOut = DEFAULT_TIME_OUT;
        }
        TimeOut = timeOut;
    }

    /**
     * 在任务开始执行时 调用该方法开始倒计时（设置任务执行的超时 时间）
     *
     * @param handler （Main Handler）
     */
    public void startCountDown(final Handler handler) {

        if (isCancel())
            return;
        Log.e("tag", "startCountDown");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putInt("code", -888);
                        bundle.putString("message", "timeout");
                        deliverError(bundle);
                        setCancel(true, true);
                    }
                });

            }
        };
        timer = new Timer();
        timer.schedule(task, TimeOut);
    }

    public Object getParams() {
        return params;
    }

    /**
     * 执行任务所需要的参数 例如：打印时可以用其携带 打印数据
     *
     * @param params
     */
    public void setParams(Object params) {
        this.params = params;
    }

    public void setModule(CommonModule module) {

        this.module = module;
    }

    public CommonModule getModule() {
        return module;
    }

    public void closeModule(final boolean isConnected) {
        Log.e("task", "close");
        if (this.module != null) {
            module.closeModule(isConnected);
            //ic卡直接下电
            if(module instanceof BankCardModule){

                ((BankCardModule) module).shutDownCard();
            }

        }
    }

    public boolean isFinish(){
        if (this.module != null) {
            return module.isFinish();
        }
        return true;
    }

    public boolean isCancel() {

        return isCancel;
    }

    /**
     * 取消任务
     *
     * @param wantCancel
     */
    public void setCancel(final boolean wantCancel, final boolean isConnected) {

        Runnable r = new Runnable() {
            @Override
            public void run() {

                isCancel = wantCancel;
                Log.e("setCancel", isCancel + "！！");
                if (null != timer && isCancel) {
                    timer.cancel();
                }
                taskCallback = null;
                closeModule(isConnected);
            }
        };
        threadPool.submit(r);
    }

    /**
     * Callback 接口
     *
     * @author zouhuaqiu
     */
    public static interface TaskCallback {

        void onStart(Bundle bundle);

        void onSucceed(Bundle bundle);

        void onError(Bundle bundle);
    }
}
