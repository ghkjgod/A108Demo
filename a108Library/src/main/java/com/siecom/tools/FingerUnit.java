package com.siecom.tools;

public class FingerUnit {
    public static String parseWellcom(byte[] data){
    	
    	int leng = data.length*2;
    	byte[] wellBuff = new byte[leng];
    	for(int i=0;i<data.length;i++){
    		wellBuff[i*2]		=  (byte) (((data[i]>>4)&0x0F) + 0x30);
    		wellBuff[i*2 + 1]   =  (byte) ((data[i]&0x0F) + 0x30);
    	}
    	
    	return new String(wellBuff);
    }
	
}
