package com.siecom.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.ivsign.android.IDCReader.IDCReaderSDK;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WltLib {

//	public static  Bitmap parsePhoto(byte[] recData){
//		FileUnits fileUtils = new FileUnits();
//		fileUtils.createSDDir("wltlib");
//		fileUtils.createSDFile("wltlib/zp.wlt");
//		String wltFilePath = Environment.getExternalStorageDirectory()+ "/wltlib/zp.wlt";
//		FileOutputStream os = null;
//		try {
//			os = new FileOutputStream(wltFilePath);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//		try {
//			os.write(recData, 0, 1024);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			return null;
//		}
//		try {
//			os.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//		String bmpPath  = Environment.getExternalStorageDirectory()+ "/wltlib/zp.bmp";
//		int ret = DecodeWlt.Wlt2Bmp(wltFilePath, bmpPath);
//		if(ret!=1){
//
//			return null;
//		}
//		FileInputStream fis = null;
//		Bitmap bmp = null;
//		try {
//			fis = new FileInputStream(bmpPath);
//			bmp = BitmapFactory.decodeStream(fis);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}finally {
//			try {
//				fis.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return bmp;
//	};
	private static byte[] datawlt = new byte[1384];

	public static void checkLibFile(){
		FileUnits fileUtils = new FileUnits();
		String dir = "wltlib";
		fileUtils.createSDDir(dir);
		if(!fileUtils.isFileExist(dir+"/base.dat")){
			Log.d("base.dat","no_exist");
			fileUtils.createSDFile("wltlib/base.dat");
			String basedatPath = Environment.getExternalStorageDirectory()+ "/wltlib/base.dat";
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(basedatPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();

				return;
			}
			try {
				os.write(LicByte.basedat, 0, LicByte.basedat.length);
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		if(!fileUtils.isFileExist(dir+"/license.lic")){
			Log.d("license.lic","no_exist");
			fileUtils.createSDFile("wltlib/license.lic");
			String basedatPath = Environment.getExternalStorageDirectory()+ "/wltlib/license.lic";
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(basedatPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			try {
				os.write(LicByte.licdat, 0, LicByte.licdat.length);
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

	}

	public static Bitmap parsePhotoOther(byte[] recData) {
		checkLibFile();
		String path = Environment.getExternalStorageDirectory() + "/wltlib";
		try {
			int ret = IDCReaderSDK.wltInit(path);
			if (ret == 0) {
				// byte[] datawlt = new byte[1384];
				byte[] byLicData = { (byte) 0x05, (byte) 0x00, (byte) 0x01,
						(byte) 0x00, (byte) 0x5B, (byte) 0x03, (byte) 0x33,
						(byte) 0x01, (byte) 0x5A, (byte) 0xB3, (byte) 0x1E,
						(byte) 0x00 };
				byte[] wz = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
						(byte) 0x96, (byte) 0x69, (byte) 0x05, (byte) 0x08,
						(byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0x01,
						(byte) 0x00, (byte) 0x04, (byte) 0x00 };

				int j = 270;
				for (int i = 0; i < 14; i++) {
					datawlt[i] = wz[i];
				}
				for (int i = 0; i < 1024; i++) {
					datawlt[j] = recData[i];
					j++;
				}
				Log.i("bmpData", "bmpData: 开始解码");
				int t = IDCReaderSDK.wltGetBMP(datawlt, byLicData);
				Log.i("bmpData", "bmpData: 解码完成");
				if (t == 1) {
					Log.i("bmpData", "bmpData:解码成功 ");
					FileInputStream fis = new FileInputStream(
							Environment.getExternalStorageDirectory()
									+ "/wltlib/zp.bmp");
					Bitmap bmp = BitmapFactory.decodeStream(fis);

					fis.close();
					return bmp;

				} else {
					Log.i("bmpData", "bmpData:照片解码异常 ");
					return null;
				}
			} else {
				Log.i("bmpData", "bmpData:照片解码异常 ");
				return null;
			}
		} catch (Exception e) {
			Log.i("bmpData", "bmpData:照片解码异常 " + e.toString());
			return null;
		}

	}
}
