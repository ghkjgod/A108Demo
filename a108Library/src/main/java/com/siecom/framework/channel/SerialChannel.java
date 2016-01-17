package com.siecom.framework.channel;

import android.content.Context;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.siecom.framework.constconfig.ErrorDefine;
import com.siecom.tools.ByteTool;

import java.io.IOException;

/**
 * Created by zhq on 2015/12/24.
 */
public class SerialChannel implements SiecomChannel {
    private Context context;
    private static D2xxManager ftD2xx;
    private static FT_Device ftDev;
    private int DevCount = -1;
    private int currentPortIndex = -1;
    private int portIndex = -1;
    private boolean uart_configured = false;
    final byte XON = 0x11; /* Resume transmission */
    final byte XOFF = 0x13; /* Pause transmission */
    final int MAX_NUM_BYTES = 1024;
    int baudRate; /* baud rate */
    byte stopBit; /* 1:1stop bits, 2:2 stop bits */
    byte dataBit; /* 8:8bit, 7: 7bit */
    byte parity; /* 0: none, 1: odd, 2: even, 3: mark, 4: space */
    byte flowControl; /* 0:none, 1: CTS/RTS, 2:DTR/DSR, 3:XOFF/XON */
    int iTotalBytes;
    public static final String TAG = "SerialChannel";
    byte[] buf = new byte[MAX_NUM_BYTES];
    public enum DeviceStatus {
        DEV_NOT_CONNECT, DEV_NOT_CONFIG, DEV_CONFIG
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int initChannel() {
        try {
            ftD2xx = D2xxManager.getInstance(this.context);
        } catch (D2xxManager.D2xxException e) {
            Log.e(TAG, "getInstance fail!!");
            return ErrorDefine.SERIAL_OPEN_FAIL;
        }
        portIndex = 0;
        baudRate = 9600;
        stopBit = 1;
        dataBit = 8;
        parity = 0;
        flowControl = 0;
        createDeviceList();
        if (DevCount > 0) {
            if (connectFunction()) {
                setConfig(baudRate, dataBit, stopBit, parity, flowControl);
                return 0;
            }
        }
        return ErrorDefine.SERIAL_OPEN_FAIL;
    }

    @Override
    public int write(byte[] command, int length) {
        if (command == null || length <= 0) {
            return ErrorDefine.SEND_NEED_PARAMETERS;
        }
        if (ftDev.isOpen() == false) {
            Log.d(TAG, "open fail");
            return ErrorDefine.SERIAL_DISCONN;
        }
        int ret = 0;
        try {
            ret = ftDev.write(command, length);
        } catch (NullPointerException e) {
            Log.e("ftdev_null", "err!!!");
            return ErrorDefine.SERIAL_NULL;
        }
        Log.e(TAG, ret + "write_command:" + ByteTool.byte2hex(command));
        if(ret<0){
            return ErrorDefine.SERIAL_SEND_FAIL;
        }
        return 0;
    }

    @Override
    public int available() throws IOException {
        return ftDev.getQueueStatus();
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        int revCount =ftDev.read(buf,length);
        System.arraycopy(buf,0,buffer,offset,length);
        return revCount;
    }

    @Override
    public int reset() {
        if(ftDev!=null)
            ftDev.close();
        ftDev=null;
        return initChannel();
    }
    @Override
    public void destroy() {
        DevCount = -1;
        currentPortIndex = -1;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ftDev != null) {
            if (true == ftDev.isOpen()) {
                ftDev.close();
            }
        }
    }
    /**
     * 枚举设备
     */
    private void createDeviceList() {
        int tempDevCount = ftD2xx.createDeviceInfoList(context);

        if (tempDevCount > 0) {
            if (DevCount != tempDevCount) {
                DevCount = tempDevCount;
            }
        } else {
            DevCount = -1;
            currentPortIndex = -1;
        }

    }
    /**
     * 连接
     * @return
     */
    public boolean connectFunction() {

        if (portIndex + 1 > DevCount) {
            portIndex = 0;
        }
        if (currentPortIndex == portIndex && ftDev != null
                && true == ftDev.isOpen()) {
            return false;
        }
        try {
            ftDev = ftD2xx.openByIndex(this.context, portIndex);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        uart_configured = false;
        if (ftDev == null) {
            return false;
        }
        if (true == ftDev.isOpen()) {
            currentPortIndex = portIndex;
            return true;
        } else {
            Log.d(TAG, "open fail");
            return false;
        }

    }
    /**
     * @param baud
     * @param dataBits
     * @param stopBits
     * @param parity
     * @param flowControl
     */
    public void setConfig(int baud, byte dataBits, byte stopBits, byte parity,
                          byte flowControl) {

        ftDev.setBitMode((byte) 0x00, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }
        ftDev.setDataCharacteristics(dataBits, stopBits, parity);
        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }
        ftDev.setFlowControl(flowCtrlSetting, XON, XOFF);

        uart_configured = true;
    }
}
