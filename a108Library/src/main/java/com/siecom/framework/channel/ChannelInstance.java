package com.siecom.framework.channel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.siecom.framework.constconfig.ErrorDefine;
import com.siecom.framework.listen.DeviceConStatusListen;
import com.siecom.tools.Timer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhq on 2015/11/27.
 */
public class ChannelInstance {

    public static final String tag = "ChannelInstance";
    public static final int BTCONNECT = 0;
    public static final int UARTCONNECT = 1;
    public static final int USBCONNECT = 2;
    public static SiecomChannel channel = null;
    private static DeviceConStatusListen statusListen = null;
    private static boolean isQuit = false;
    private static Context context;

    private static int nowConnectType = 0;
    public static boolean isQuit() {
        return isQuit;
    }

    public static void setIsQuit(boolean isQuit) {
        ChannelInstance.isQuit = isQuit;
    }

    public static void setStatusListen(DeviceConStatusListen statusListen) {

        ChannelInstance.statusListen = statusListen;

    }

    public synchronized static int sendBytes(byte[] reqMsg, int reqMsgLen) {
        int rs = -1;
        for (int i = 0; i < 2; i++) {
            try {
                rs = channel.write(reqMsg, reqMsgLen);
                if (rs == 0) {
                    return rs;
                }
                if (channel.reset() != 0) {
                    if (statusListen != null)
                        statusListen.onDisconnect();
                    return ErrorDefine.ERR_SEND_REQ_FAILED;
                }
            } catch (java.lang.StackOverflowError e) {
                if (statusListen != null)
                    statusListen.onDisconnect();
                return ErrorDefine.ERR_SEND_REQ_FAILED;
            }

        }
        if (rs != 0) {
            if (statusListen != null)
                statusListen.onDisconnect();
        }
        return ErrorDefine.ERR_SEND_REQ_FAILED;
    }

    ;

    public synchronized static int receiveBytes(byte[] rspMsg, int wantedLen, int timeOutMs) {
        int reveiveLen = 0;
        int availabelLen = 0;
        int readLenPerTime = 0;
        isQuit = false;
        Timer tm = null;
        if (rspMsg == null) {
            return ErrorDefine.SEND_NEED_PARAMETERS;
        }
        if (channel == null) {
            return ErrorDefine.NEED_CONN_FIRST;
        }
        if (timeOutMs > 0) {
            tm = new Timer(timeOutMs);
            tm.start();
        }

        while (wantedLen > 0) {
            if (timeOutMs > 0) {
                if (tm.timeOut()) {
                    Log.e(tag, "tm.timeOut!!");
                    break;
                }
            }
            if (isQuit == true) {
                Log.e(tag, "isQuit_true!!");
                break;
            }
            try {
                availabelLen = channel.available();
            } catch (IOException e) {
                e.printStackTrace();
                if (statusListen != null)
                    statusListen.onDisconnect();
                return ErrorDefine.ERR_RECEIVE_RSP_FAILED;
            } catch (NullPointerException e) {
                if (statusListen != null)
                    statusListen.onDisconnect();
                return ErrorDefine.NEED_CONN_FIRST;
            }
            if (availabelLen > 0) {
                readLenPerTime = channel.read(rspMsg, reveiveLen, wantedLen);
                wantedLen -= readLenPerTime;
                reveiveLen += readLenPerTime;
            }
        }
        return reveiveLen;
    }

    public static void setContext(Context context){

        ChannelInstance.context = context;
    }
    /**
     * 初始化蓝牙连接的
     *
     * @param connectType
     * @param device
     * @return
     */
    public static int initDevice(int connectType, BluetoothDevice device) {

        if (connectType == BTCONNECT) {
            if (device == null) {
                return ErrorDefine.BLUE_DEVICE_NULL;
            }
            if (statusListen != null)
                statusListen.connecting();
            BtChannel btchannel = new BtChannel();
            btchannel.setDevice(device);
            channel = btchannel;
            nowConnectType= BTCONNECT;
        }
        if (connectType==UARTCONNECT){
            if(context==null){
                return ErrorDefine.SERIAL_NEED_CONTEXT;
            }
            SerialChannel serialChannel = new SerialChannel();
            serialChannel.setContext(context);
            channel = serialChannel;
            nowConnectType =UARTCONNECT;
        }
        if (channel == null)
            return ErrorDefine.NEED_CONN_FIRST;
        int rs = channel.initChannel();
        if (rs == 0 && statusListen != null) {
            statusListen.connected();
        } else if(statusListen != null) {
            statusListen.onDisconnect();
        }
        return rs;
    }

    /**
     * 初始化蓝牙连接的
     *
     * @param connectType
     * @param deviceName
     * @return
     */
    public static int initDevice(int connectType, String deviceName) {

        BluetoothDevice device = findDeviceByName(deviceName);
        return initDevice(connectType,device);

    }

    /**
     * 根据名字找蓝牙
     *
     * @param deviceName
     * @return
     */
    public static BluetoothDevice findDeviceByName(String deviceName) {
        BluetoothAdapter localBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (localBluetoothAdapter != null) {
            Set<?> localSet = localBluetoothAdapter.getBondedDevices();
            if (localSet.size() > 0) {
                Iterator<?> localIterator = localSet.iterator();
                while (true) {
                    if (!localIterator.hasNext())
                        return null;
                    BluetoothDevice localBluetoothDevice = (BluetoothDevice) localIterator
                            .next();
                    Log.i("BLEDevice", "Paired devices: name->"
                            + localBluetoothDevice.getName() + ", address->"
                            + localBluetoothDevice.getAddress());
                    if (localBluetoothDevice.getName().contains(deviceName)) {
                        return localBluetoothDevice;
                    }
                }
            }
        }
        return null;
    }
    public static void resetChannel(){
        if(channel!=null){
               channel.reset();
        }

    }
    public static void destroy() {
        channel.destroy();
    }

}
