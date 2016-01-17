package com.siecom.framework.listen;

public interface KeyBroadListen {
    public void onStart();
    public void onSucc(byte[] checkData);
    public void onReadPin(byte[] pin);
    public void onFail(int ret, String msg);
    public void onKeyNum(int num);
    public void onCancel();

	
}
