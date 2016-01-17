package com.siecom.framework.listen;

import com.siecom.framework.bean.IdentityInfoBean;

public interface IdentityListen {
    public void onStart();
    public void onReadSucc(IdentityInfoBean bean);
    public void onReadFail(int ret, String msg);
}
