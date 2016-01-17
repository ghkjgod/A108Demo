package com.siecom.framework.listen;

/**
 * Created by zhq on 2015/12/11.
 */
public interface DeviceConStatusListen {
    /**
     * 断开连接
     */
    void onDisconnect();

    /**
     * 已经连接
     */
    void connected();

    /**
     * 正在连接
     */
    void connecting();

}
