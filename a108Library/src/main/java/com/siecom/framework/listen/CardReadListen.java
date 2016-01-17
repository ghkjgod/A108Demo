package com.siecom.framework.listen;

import com.siecom.framework.bean.BankCardInfoBean;

public interface CardReadListen {

	public  void onStart();

	public  void onReadCardInfo(BankCardInfoBean bean);

	public  void onReadCardFail(int ret, String msg);
	

}
