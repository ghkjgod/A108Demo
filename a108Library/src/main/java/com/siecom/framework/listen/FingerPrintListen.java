package com.siecom.framework.listen;

import android.graphics.Bitmap;

public interface FingerPrintListen {

    public void onStart();
    public void onRead(String code);
    public void onFail(int ret, String msg);

    public void onGetImgSucc(Bitmap fingerPrintBMP);
    public void onGetImgProgress(int Progress);
    
    
}
