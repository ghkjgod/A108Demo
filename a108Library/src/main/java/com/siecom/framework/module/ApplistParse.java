package com.siecom.framework.module;

import com.siecom.tools.ByteTool;

import java.util.ArrayList;
import java.util.List;


public class ApplistParse {
	
	
	public static List<String> Parse(byte[] applist,int num){
		   List<String>  list = new ArrayList<String>(num);	
	        int len =  0;
	        int pos = 0;
	        for(int i=0;i<num;i++){
	        	 pos = (i+1)*17;
	        	 byte[] aid_buff = new byte[17];
	        	 System.arraycopy(applist,pos, aid_buff, 0, len);
	        	 list.add(ByteTool.byte2hex(aid_buff));
	        }
            return list;
		   
	}

}
