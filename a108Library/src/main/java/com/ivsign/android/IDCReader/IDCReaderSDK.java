package com.ivsign.android.IDCReader;
public class IDCReaderSDK {
   private static final String TAG = "unpack";
   
	public IDCReaderSDK()
	{
		
	
	}
	// native functin interface
    public static native int wltInit(String workPath);
    
    public static native int wltGetBMP(byte[] wltdata, byte[] licdata);
    
    /* this is used to load the 'wltdecode' library on application
     */
    static {
        System.loadLibrary("wltdecode");
    }
   
}
