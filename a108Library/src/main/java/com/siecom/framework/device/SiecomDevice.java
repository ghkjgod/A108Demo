package com.siecom.framework.device;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;

import com.siecom.framework.channel.ChannelInstance;
import com.siecom.framework.listen.DeviceConStatusListen;
import com.siecom.framework.module.BankCardModule;
import com.siecom.framework.module.EmvOptions;
import com.siecom.framework.module.KeyBroadOption;
import com.siecom.framework.module.SystemModule;
import com.siecom.tools.SingletonThreadPool;

import java.util.concurrent.ExecutorService;

/**
 * Created by zhq on 2015/12/10.
 */
public class SiecomDevice {

    private static SiecomTaskQueue queue = SiecomTaskQueue.getInstance();
    private static ExecutorService threadPool = SingletonThreadPool.getInstance();

    public static void getPin(int keyIndex,String cardNo,String amount,int timeOut,byte maxLen,byte encryptType,SiecomTask.TaskCallback taskCallback){

        SiecomTask task = new SiecomTask(SiecomTask.Operate.GET_PIN,taskCallback);
        KeyBroadOption unit = new KeyBroadOption();
        unit.setMaxLen(maxLen);
        unit.cardNo = cardNo;// 卡号
        unit.iAmount = amount;// 金额1.0元要这样000001.00，就是要满6位,不显示就不用设置
        unit.setMode(encryptType);// 00为x9.8加密,明文输入使用0x03
        unit.setMainKeyNo(keyIndex);// 使用哪组密钥加密，这里以主密钥的序列号为准
        unit.setTimeOut(timeOut);
        task.setParams(unit);
        queue.addTask(task);

    }

    public static void inputWorkKey(int keyIndex,int mainKeyIndex,byte[] keyData, SiecomTask.TaskCallback taskCallback){
        SiecomTask task = new SiecomTask(SiecomTask.Operate.INPUT_WORK_KEY,taskCallback);
        KeyBroadOption unit = new KeyBroadOption();
        unit.setType(KeyBroadOption.WORKKEY);// 携带的是工作密钥
        unit.setkeyData(keyData);// 密钥的内容
        unit.setMainKeyNo(mainKeyIndex);// 对应使用的主密钥的编码为0;
        unit.setWorkKeyNo(keyIndex);// 工作密钥的编码为0
        unit.setMode((byte) 0x00);// 0x00MK解密，0x01MK加密，0x02明文，一般使用0x00
        task.setParams(unit);
        queue.addTask(task);

    }

    public static void inputMainKey(int keyIndex,byte[] keyData,SiecomTask.TaskCallback taskCallback){
        SiecomTask task = new SiecomTask(SiecomTask.Operate.INPUT_MAIN_KEY,taskCallback);

        KeyBroadOption unit = new KeyBroadOption();
        unit.setType(KeyBroadOption.MAINKEY);// 携带的是主密钥
        unit.setkeyData(keyData);// 密钥的内容
        unit.setMainKeyNo(keyIndex);// 主密钥的编码为0;

        task.setParams(unit);
        queue.addTask(task);

    }

    public static void deviceConnStatusChange( DeviceConStatusListen statusListen ){

        ChannelInstance.setStatusListen(statusListen);
    }

    public static void cancelLastTask(boolean isConnected){
        queue.cancelCurrentTask(isConnected);
    }

    public static void connectToBtDevice(final BluetoothDevice device, SiecomTask.TaskCallback taskCallback){
        SiecomTask task = new SiecomTask(SiecomTask.Operate.CONNECT_BT,taskCallback);
        task.setParams(device);
        queue.addTask(task);
    }
    public static void ReadIdentity(boolean withFinger, SiecomTask.TaskCallback taskCallback,int timeOut){
        SiecomTask task = new SiecomTask(SiecomTask.Operate.READ_IDENTITY,taskCallback);
        task.setParams(withFinger);
        task.setTimeOut(timeOut);
        queue.addTask(task);
    }
    public static void ReadFinger(int fingerPrintType,  SiecomTask.TaskCallback taskCallback,int timeOut){
        SiecomTask task = new SiecomTask(SiecomTask.Operate.FINGER_PRINT,taskCallback);
        task.setParams(fingerPrintType);
        task.setTimeOut(timeOut);
        queue.addTask(task);
    }

    public static void ReadBankCard(int cardType,EmvOptions options,SiecomTask.TaskCallback taskCallback,int timeOut){
        SiecomTask.Operate operate = null;
        switch (cardType){
            case BankCardModule.IC_CARD:
                operate = SiecomTask.Operate.READ_IC_CARD;
                break;
            case BankCardModule.MSR_CARD:
                operate = SiecomTask.Operate.READ_MSR_CARD;
                break;
            case BankCardModule.PIC_CARD:
                operate = SiecomTask.Operate.READ_PIC_CARD;
                break;
            case BankCardModule.AUTO_FIND:
                operate = SiecomTask.Operate.READ_AUTO_FIND;
                break;
        }
        SiecomTask task = new SiecomTask(operate,taskCallback);
        task.setParams(options);
        task.setTimeOut(timeOut);
        queue.addTask(task);
    }
    public static void shutDownCard(){

        Runnable r = new Runnable() {
            @Override
            public void run() {

                BankCardModule.getInstance().shutDownCard();
            }
        };
        threadPool.submit(r);

    }
    public static int  SyncARPCExecuteScript(int ICType, String ARPC, String Script,byte[] ScriptResult, byte[] TC){

        return BankCardModule.getInstance().ARPCExecuteScript(ICType,ARPC,Script,ScriptResult,TC);

    }

    public static int SyncGetLog(int ICType, byte[] tlog, int[] length){

        return  BankCardModule.getInstance().GetLog(ICType, tlog, length);
    }


    public static int SyncWriteCodeName(String codeName){

        return SystemModule.writeCodeName(codeName);
    }
    public static String SyncReadCodeName(){

        return SystemModule.readCodeName();
    }

    public static String SyncReadSerialNo(){

        return SystemModule.readSerialNo();
    }
    public static void beep(){

           SystemModule.beep();
    }

    public static int SyncTestPsam(byte slot){

        return SystemModule.testPsamSlot(slot);
    }
    public static int SyncValidateARPC(byte[] content){

        return BankCardModule.getInstance().validateARPC(content);
    }

}
