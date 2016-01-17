package com.siecom.nativelibs;

import android.util.Log;

public class NativeFunction {
	  static
	  {
	    System.loadLibrary("Siecom"); 
	   
	  } 
	  
	  public static native int decode(String workPath,byte[] wltdata, byte[] licdata);

	  public static native void pt_Mac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt);

	  public static native void sk_Mac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt);

	  public static native void Crc16CCITT(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2);

	  public static native void Crc16CCITT2(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);

	  public static native void Lib_Des24(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte paramByte);

	  public static native void Lib_Des16(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte paramByte);



	  
}
