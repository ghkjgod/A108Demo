package com.siecom.framework.device;

import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.siecom.framework.bean.BankCardInfoBean;
import com.siecom.framework.bean.IdentityInfoBean;
import com.siecom.framework.constconfig.ErrorDefine;
import com.siecom.framework.channel.ChannelInstance;
import com.siecom.framework.listen.CardReadListen;
import com.siecom.framework.listen.FingerPrintListen;
import com.siecom.framework.listen.KeyBroadListen;
import com.siecom.framework.module.BankCardModule;
import com.siecom.framework.module.EmvOptions;
import com.siecom.framework.module.FingerPrintModule;
import com.siecom.framework.listen.IdentityListen;
import com.siecom.framework.module.IdentityModule;
import com.siecom.framework.module.KeyBroadModule;
import com.siecom.framework.module.KeyBroadOption;
import com.siecom.tools.ByteTool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhq on 2015/12/10.
 */
public class SiecomTaskQueue {
    protected static Handler handler = null;
    protected static ArrayBlockingQueue<SiecomTask> taskQueue = new ArrayBlockingQueue<SiecomTask>(1);
    private TaskDispatcher mTaskDispatcher;
    private static SiecomTaskQueue instance = null;

    private static ExecutorService threadPool =  Executors.newFixedThreadPool(1);

    public static enum Result {
        START,SUCCEED, ERROR
    }

    private SiecomTaskQueue() {
        instance = this;
        taskQueue.clear();
        handler = new Handler();
        mTaskDispatcher = new TaskDispatcher(taskQueue, this, handler);
        mTaskDispatcher.start();

    }

    public static SiecomTaskQueue getInstance() {
        if (instance == null) {
            instance = new SiecomTaskQueue();
        }
        return instance;
    }

    public void cancelCurrentTask(boolean isConnected){
        Log.e("cancelCurrentTask","---cancelCurrentTask--");
        if(mTaskDispatcher.getCurrentTask()!=null){
        	mTaskDispatcher.getCurrentTask().setCancel(true, isConnected);
        }
    }

    public void addTask(SiecomTask task) {

        // 清除正在运行的任务


        SiecomTask lastTask =  mTaskDispatcher.getCurrentTask();
        if(lastTask!=null) {
            if (lastTask.operate == task.operate ) {
                //如果上一次任务没有完成且没有被取消
                if(!lastTask.isCancel()&&!lastTask.isFinish()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("code", -879);
                    bundle.putString("message", "您已经执行同样的操作且操作还在执行，请稍等");
                    deliverResult(task, bundle, Result.ERROR);
                    return;
                }
            }
            cancelCurrentTask(true);
        }
        try {
            taskQueue.add(task);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            cancelCurrentTask(true);

        }
    }

    /**
     * 处理任务
     *
     * @param task
     */
    protected void performanceTask(final SiecomTask task) {
        Log.e("task", task.operate.name());
        switch (task.operate) {
            case CONNECT_BT:
                connectBt(task);
                break;
            case READ_IDENTITY:
                ReadIdentity(task);
                break;
            case FINGER_PRINT:
                ReadFinger(task);
                break;
            case INPUT_MAIN_KEY:
                KeyBorad(task, KeyBroadModule.OPCODE_MAIN_KEY);
                break;
            case INPUT_WORK_KEY:
                KeyBorad(task,KeyBroadModule.OPCODE_WORK_KEY);
                break;
            case GET_PIN:
                KeyBorad(task,KeyBroadModule.OPCODE_GET_PIN);
                break;

            case READ_AUTO_FIND:
                ReadBankCard(task, BankCardModule.AUTO_FIND);
                break;
            case READ_IC_CARD:
                ReadBankCard(task, BankCardModule.IC_CARD);
                break;
            case READ_PIC_CARD:
                ReadBankCard(task, BankCardModule.PIC_CARD);
                break;
            case READ_MSR_CARD:
                ReadBankCard(task, BankCardModule.MSR_CARD);
                break;
        };

    }

    private void ReadBankCard(final SiecomTask task, int cardType) {
        BankCardModule module = BankCardModule.getInstance();
        task.setModule(module);
        CardReadListen listen = new CardReadListen() {

            @Override
            public void onStart() {
                Bundle bundle = new Bundle();
                deliverResult(task, bundle, Result.START);
            }

            @Override
            public void onReadCardInfo(BankCardInfoBean bean) {
                // TODO Auto-generated method stub
                Log.e("card", bean.cardNo);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", bean);
                deliverResult(task, bundle, Result.SUCCEED);
            }

            @Override
            public void onReadCardFail(int ret, String msg) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putInt("code", ret);
                bundle.putString("message", msg);
                deliverResult(task, bundle, Result.ERROR);

            }
        };
        task.startCountDown(handler);
        module.setEMVOption((EmvOptions) task.getParams());
        module.setCallback(listen);
        module.startReadCard(cardType);

    }

    private void KeyBorad(final SiecomTask task,int opCode) {

        KeyBroadModule module = KeyBroadModule.getInstance();
        task.setModule(module);
        KeyBroadListen listen = new KeyBroadListen() {

            @Override
            public void onStart() {
                Bundle bundle = new Bundle();
                deliverResult(task, bundle, Result.START);
            }

            @Override
            public void onSucc(byte[] checkdata) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putByteArray("data",checkdata);
                Log.e("checkdata", ByteTool.byte2hex(checkdata));
                deliverResult(task, bundle, Result.SUCCEED);
            }
            @Override
            public void onReadPin(byte[] pin) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putInt("option",KeyBroadModule.OP_FINISH);
                bundle.putByteArray("password", pin);
                deliverResult(task, bundle, Result.SUCCEED);
            }
            @Override
            public void onFail(int ret, String msg) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putInt("code", ret);
                bundle.putString("message", msg);
                deliverResult(task, bundle, Result.ERROR);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putInt("option",KeyBroadModule.OP_CANCEL);
                deliverResult(task, bundle, Result.SUCCEED);
            }
            @Override
            public void onKeyNum(int num) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putInt("option",KeyBroadModule.OP_INPUTTING);
                bundle.putInt("keyNum", num);
                deliverResult(task, bundle, Result.SUCCEED);

            }

        };


        module.startRun(listen, opCode, (KeyBroadOption) task.getParams());

    }

    private void ReadFinger(final SiecomTask task) {

        task.startCountDown(handler);
        FingerPrintModule module = FingerPrintModule.getInstance();
        task.setModule(module);//自动关闭
        FingerPrintListen callback = new FingerPrintListen() {
            @Override
            public void onStart() {
                Bundle bundle = new Bundle();
                deliverResult(task, bundle, Result.START);
            }

            @Override
            public void onRead(String code) {
                Bundle bundle = new Bundle();
                bundle.putString("data", code);
                deliverResult(task, bundle, Result.SUCCEED);
            }

            @Override
            public void onFail(int ret, String msg) {

                Bundle bundle = new Bundle();
                bundle.putInt("code", ret);
                bundle.putString("message", msg);
                deliverResult(task, bundle, Result.ERROR);

            }

            @Override
            public void onGetImgSucc(Bitmap fingerPrintBMP) {

            }

            @Override
            public void onGetImgProgress(int Progress) {

            }
        };
        module.setCallback(callback);
        module.startRead((int) task.getParams());

    }

    private void ReadIdentity(final  SiecomTask task){
        //超时统计

        task.startCountDown(handler);
        IdentityModule  module =  IdentityModule.getInstance();
        task.setModule(module);//自动关闭

        IdentityListen callback = new IdentityListen() {
            @Override
            public void onStart() {
                Bundle bundle = new Bundle();
                deliverResult(task, bundle, Result.START);
            }

            @Override
            public void onReadSucc(IdentityInfoBean bean) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", bean);
                deliverResult(task, bundle, Result.SUCCEED);

            }

            @Override
            public void onReadFail(int ret, String msg) {
                Bundle bundle = new Bundle();
                bundle.putInt("code", ret);
                bundle.putString("message", msg);
                deliverResult(task, bundle, Result.ERROR);
            }
        };
        module.setCallback(callback);
        module.startRead((boolean) task.getParams());
    }

    private void connectBt(final SiecomTask task){
          Runnable r = new Runnable() {
              @Override
              public void run() {
                  Bundle bundle1 = new Bundle();
                  deliverResult(task, bundle1, Result.START);
                  int res  = ChannelInstance.initDevice(ChannelInstance.BTCONNECT,(BluetoothDevice)task.getParams());
                  Log.e("connectBt", res + "!!");
                  if(res==0){
                      Bundle bundle = new Bundle();
                      bundle.putParcelable("data",(BluetoothDevice) task.getParams());
                      deliverResult(task, bundle, Result.SUCCEED);
                  }else{
                      Bundle bundle = new Bundle();
                      bundle.putInt("code", res);
                      bundle.putString("message", ErrorDefine.getErrorDescribe(res));
                      deliverResult(task, bundle, Result.ERROR);
                  }
              }
          };
        threadPool.submit(r);
    }


    protected void deliverResult(final SiecomTask task, final Bundle bundle,
                                 final Result result) {

        if (null != handler) {
            if (null != task && !task.isCancel()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (result) {
                            case START:
                                task.deliverStart(bundle);
                                break;
                            case SUCCEED:
                                task.deliverResponse(bundle);
                                break;
                            case ERROR:
                                task.deliverError(bundle);
                                break;
                        }
                    }
                });
            }
        }
    }

    public void cancelTaskDispatcher() {
        if (null != mTaskDispatcher) {
            mTaskDispatcher.cancle();

        }
    }
}



