package com.siecom.framework.channel;

import java.io.IOException;

/**
 * Created by zhq on 2015/11/27.
 * Use for blueTooth/Uart/usb
 */
public interface SiecomChannel {

    public int initChannel();

    public int write(byte[] command, int length);

    public int available() throws IOException;

    public int read(byte[] buffer,int wantedLen, int length);

    public int reset() ;

    public void destroy();

}
