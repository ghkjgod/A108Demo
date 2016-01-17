package com.siecom.framework.channel;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.siecom.framework.constconfig.ErrorDefine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by zhq on 2015/11/27.
 */
public class BtChannel implements SiecomChannel {

    public static final String tag = "BtChannel";
    public static BluetoothDevice device = null;
    public final static String customUUIDString = "00001101-0000-1000-8000-00805F9B34FB";
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    public  static BluetoothSocket socket = null;
    private InputStream btInStream = null;
    private OutputStream btOutStream = null;
    private UUID customUUID = UUID.fromString(customUUIDString);
    public void setDevice(BluetoothDevice device) {
        BtChannel.device = device;
    }
    public BluetoothDevice getDevice() {
        return device;
    }
    @Override
    public synchronized int initChannel() {
        if (device == null) {
            Log.e(tag, "BluetoothDevice is null");
            return ErrorDefine.BLUE_DEVICE_NULL;
        }
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) { // 已配对
            return ErrorDefine.BLUE_NO_PAIR;
        }
        if (socket != null) {
            Log.e(tag, "socket no null,now close");
            try {
                if (btOutStream != null)
                    btOutStream.close();
                btOutStream = null;
                if (btInStream != null)
                    btInStream.close();
                btInStream = null;
                if (socket != null)
                    socket.close();
                socket = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        try {
            socket = device.createRfcommSocketToServiceRecord(customUUID);// 创建socket
            socket.connect();
            btInStream = socket.getInputStream();
            btOutStream = socket.getOutputStream();
        } catch (IOException connectException) {
            socket = null;
            connectException.printStackTrace();
            Log.e(tag, "IO err:" + connectException.getMessage());
            return ErrorDefine.BLUE_CONN_ERR;
        }
        return 0;
    }

    @Override
    public int write(byte[] command, int length) {
        if (command == null || length <= 0) {
            return ErrorDefine.SEND_NEED_PARAMETERS;
        }
        if (btOutStream == null) {
            return ErrorDefine.NEED_CONN_FIRST;
        }
        try {
            btOutStream.write(command, 0, length);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(tag, "bt write err:" + e.getMessage());
            return ErrorDefine.ERR_SEND_REQ_FAILED;
        }
        return 0;
    }

    @Override
    public int available() throws IOException,NullPointerException{
        if (btInStream == null) {
            return ErrorDefine.NEED_CONN_FIRST;
        }
        return btInStream.available();
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) {
        if (btInStream == null) {
            return ErrorDefine.NEED_CONN_FIRST;
        }
        try {
            return btInStream.read(buffer, byteOffset, byteCount);
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorDefine.ERR_RECEIVE_RSP_FAILED;
        }
    }

    @Override
    public int reset() {
        int result = 0;
        FutureTask<Integer> future =
                new FutureTask<Integer>(new Callable<Integer>() {//使用Callable接口作为构造参数
                    @Override
                    public Integer call() {
                         return initChannel();
                    }
                });
        executor.execute(future);
        try {
            result = future.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            future.cancel(true);
            return ErrorDefine.BLUE_CONN_ERR;
        } catch (ExecutionException e) {
            future.cancel(true);
            return ErrorDefine.BLUE_CONN_ERR;
        } catch (TimeoutException e) {
            future.cancel(true);
            return ErrorDefine.BLUE_CONN_ERR;
        }catch (Exception e){
            future.cancel(true);
            return ErrorDefine.BLUE_CONN_ERR;
        }
        return result;

    }

    @Override
    public void destroy() {
        device  = null;
        try {
            if (btOutStream != null)
                btOutStream.close();
            btOutStream = null;
            if (btInStream != null)
                btInStream.close();
            btInStream = null;
            if (socket != null)
                socket.close();
            socket = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
