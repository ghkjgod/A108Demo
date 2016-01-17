package com.siecom.framework.module;

import com.siecom.framework.appinterface.Api;

/**
 * Created by zhq on 2015/12/10.
 */
public class CommonModule {
    public static Api api = new Api() ;

    public int OpenModule(){

        return 0;
    }

    public void closeModule(boolean isConnected){


    }

    public boolean isFinish(){

        return true;
    }


}
