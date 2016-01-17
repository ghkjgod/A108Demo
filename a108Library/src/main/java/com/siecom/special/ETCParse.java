package com.siecom.special;

import com.siecom.tools.ByteTool;

import java.io.UnsupportedEncodingException;

public class ETCParse {
	public static class UserInfoBean{
		 public String biaoshi;
		 public String zhigong;
		 public String name;
		 public String IdNo;
		 public String IdType;
	}
	
	public static UserInfoBean userInfoParse(byte[] b){
		UserInfoBean bean = new UserInfoBean();
		byte[] biaoshi = new byte[1];//持卡人身份标识
		System.arraycopy(b, 0, biaoshi, 0, 1);
		bean.biaoshi = ByteTool.byte2hex(biaoshi);
		
		byte[] zhigong = new byte[1];//职工标识
		System.arraycopy(b, 1, zhigong, 0, 1);
		bean.zhigong  = ByteTool.byte2hex(zhigong);
		
		byte[] name = new byte[20];//姓名
		System.arraycopy(b, 2, name, 0, 20);	
		try {
			bean.name = new String(name,"gb2312").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] zhengjian = new byte[32];//证件号码
		System.arraycopy(b, 22, zhengjian, 0, 32);	
		try {
		bean.IdNo = new String(zhengjian,"gb2312").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] chikarenlei = new byte[1];//持卡人类型
		System.arraycopy(b, 54, chikarenlei, 0, 1);
		bean.IdType = ByteTool.byte2hex(chikarenlei);
		return bean;
	}
	
	/**
	 * 基本信息的bean
	 * @author Administrator
	 *
	 */
    public static class BaseInfoBean{
    	public String cardOrg;
    	public String cardType;
    	public String cardVersion;
    	public String netNo;
    	public String cardNo;
    	public String startTime;
    	public String endTime;
    	public String carNo;
    	public String userType;
    	public String color;
    }
    public static BaseInfoBean baseInfoParse(byte[] info){
    	BaseInfoBean bean  = new BaseInfoBean();
		byte[] flag = new byte[8];//发卡行标识
		System.arraycopy(info, 0, flag, 0, 8);
		bean.cardOrg = ByteTool.byte2hex(flag);
	
		byte[] cardType = new byte[1]; //卡片类型
		System.arraycopy(info, 8, cardType, 0, 1);
		
		bean.cardType = ByteTool.byte2hex(cardType);
		
		byte[] cardVersion = new byte[1];//卡片版本号
		System.arraycopy(info, 9, cardVersion, 0, 1);
		
		bean.cardVersion = ByteTool.byte2hex(cardVersion);
		
		byte[] netNo = new byte[2];//卡片网络编号
		System.arraycopy(info, 10, netNo, 0, 2);
		
		bean.netNo = ByteTool.byte2hex(netNo);
		
		byte[] innerNo = new byte[8];//用户内部编号
		System.arraycopy(info, 12, innerNo, 0, 8);

        bean.cardNo= ByteTool.byte2hex(innerNo);
		
		byte[] start = new byte[4];//启用时间
		System.arraycopy(info, 20, start, 0, 4);
		
		bean.startTime = ByteTool.byte2hex(start);
		
		byte[] end = new byte[4];//启用时间
		System.arraycopy(info, 24, end, 0, 4);
		bean.endTime = ByteTool.byte2hex(end);
		
		byte[] carNo = new byte[12];//车牌
		System.arraycopy(info, 28, carNo, 0, 12);
		try {
			bean.carNo = new String(carNo,"gb2312").trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] userType = new byte[1];
		System.arraycopy(info, 40, userType, 0, 1);
		bean.userType = ByteTool.byte2hex(userType);
		byte[] color = new byte[1];//颜色
		System.arraycopy(info, 41, color, 0, 1);
		String c = "";
		switch(color[0]){
		case 0x00:
			c = "蓝色";
			break;
		case 0x01:
			c = "黄色";
			break;
		case 0x02:
			c =  "黑色";
			break;
		case 0x03:
			c = "白色";
			break;
		default:
			c = "其他";
		    break;
		}
		bean.color = c;
    	return bean;
    }
    
	
}
