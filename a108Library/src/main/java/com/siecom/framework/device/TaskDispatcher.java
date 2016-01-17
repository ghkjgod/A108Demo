package com.siecom.framework.device;
import java.util.concurrent.ArrayBlockingQueue;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
/**
 * 任务派发线程
 * 
 * @author zouhuaqiu
 * 
 */
public class TaskDispatcher extends Thread {

    ArrayBlockingQueue<SiecomTask> taskQueue;
    SiecomTaskQueue mBasePeripheral;
    boolean isCancle = false;
    Handler handler;

    public SiecomTask getCurrentTask() {
        return currentTask;
    }

    /**
     * 当前正在执行的任务
     */
    SiecomTask currentTask;

    public void setCurrentTask(SiecomTask currentTask) {
        this.currentTask = currentTask;
    }

    public TaskDispatcher(ArrayBlockingQueue<SiecomTask> taskQueue,
            SiecomTaskQueue mBasePeripheral, Handler handler) {
        this.taskQueue = taskQueue;
        this.mBasePeripheral = mBasePeripheral;
        this.handler = handler;
        setPriority(Thread.NORM_PRIORITY - 1);
    }

    @Override
    public void run() {
        while (true) {
            try {
                currentTask = taskQueue.take();
                if (isCancle) {
                    break;
                }
                if (null != currentTask && !currentTask.isCancel()) {
                    mBasePeripheral.performanceTask(currentTask);
                }
            } catch (final InterruptedException e) {
                if (null != currentTask && !currentTask.isCancel()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putInt("code",e.hashCode());
                            bundle.putString("message", e.getMessage());
                            currentTask.deliverError(bundle);

                        }
                    });
                }
            } catch (final Exception e) {
                if (null != currentTask && !currentTask.isCancel()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putInt("code",e.hashCode());
                            bundle.putString("message", e.getMessage());
                            currentTask.deliverError(bundle);

                        }
                    });
                }
            }catch (final UnknownError e) {

                if (null != currentTask && !currentTask.isCancel()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putInt("code",e.hashCode());
                            bundle.putString("message", e.getMessage());
                            currentTask.deliverError(bundle);

                        }
                    });
               
                }
            }
        }
    }

    public void cancle() {
        isCancle = true;
    }
}
