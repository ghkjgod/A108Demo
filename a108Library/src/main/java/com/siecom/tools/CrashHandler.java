package com.siecom.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
/*
 *
 * 
 */


public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	private UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler INSTANCE = new CrashHandler();
	private Context mContext;
	private Map<String, String> info = new HashMap<String, String>();
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private String exceptionMessage;

	private CrashHandler() {
	}

	
	public static CrashHandler getInstance() {
		return INSTANCE;
	}


	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		if (!handleException(ex) && mDefaultHandler != null) { 
			
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} 
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(1);
		}
	}

	
	public boolean handleException(Throwable ex) {
		if (ex == null)
			return false;
		new Thread() {
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "程序异常已经记录", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start(); 
		collectDeviceInfo(mContext); 
		exceptionMessage=saveCrashInfo2File(ex);
	//	Log.v("test","exceptionMessage length is "+exceptionMessage.getBytes("GBK").length);
		if(exceptionMessage!=null){
//			new Thread(new Runnable(){
//				public void run(){
//					Socket socket=null;
//					InputStream in=null;
//					OutputStream out=null;
//					try{
//						
//						socket = new Socket();
//						InetSocketAddress socketAddress = new InetSocketAddress(
//								SendMessage.getIp(), SendMessage.getPort());
//						socket.connect(socketAddress, 6000);
//
//						out = socket.getOutputStream();
//						in = socket.getInputStream();		
//				//		Log.v("test","exceptionMessage length is "+exceptionMessage.getBytes("GBK").length);
//						BytesCreate bc=new BytesCreate("022",null,mContext);
//						
//						byte [] sendByte=bc.getExceptionStr((exceptionMessage.getBytes("GBK")));
//						Log.v("test","sendByte length is "+sendByte.length);
//						out.write(sendByte);
//						
//					}catch(IOException e){
//						e.printStackTrace();
//						
//					}finally{
//						try{
//						if(socket!=null)
//							socket.close();
//						if(out!=null)
//							out.close();
//						if(in!=null)
//							in.close();
//						}catch(IOException e){
//							e.printStackTrace();
//						}
//					}
//				}
//			}).start();
		}
		return true;
	}

	
	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();
		String result = writer.toString();
		sb.append(result); 
		
		long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + "-" + timetamp + ".txt";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "crash");
				Log.i("CrashHandler", dir.toString());
				if (!dir.exists())
					dir.mkdir();
				FileOutputStream fos = new FileOutputStream(new File(dir,
						fileName));
				fos.write(sb.toString().getBytes());
				fos.close();
				return sb.toString();
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

