package com.siecom.tools;


import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("unused")
public class FileUnits {
    private String SDPath;
    
    public FileUnits(){
        
        SDPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }
    public InputStream readFromSrcPath(String filename){
    	InputStream is= null;
		is =this.getClass().getResourceAsStream("/com/siecom/tools/"+filename);
    	return is;
    }
    /**
     * 
     * @param fileName
     * @return
*/
    public File createSDFile(String fileName){
        File file=new File(SDPath+fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    
    /**
     *
     * @param dirName
     * @return
*/
    public File createSDDir(String dirName){
        File file=new File(SDPath+dirName);
        file.mkdir();
        return file;
    }
    
    /**
     *
     * @param fileName
     * @return
*/
    public boolean isFileExist(String fileName){
        File file=new File(SDPath+fileName);
        return file.exists();
    }
    /**
     *
     * @param path
     * @param fileName
     * @param inputStream
     * @return
*/
    public File writeToSDfromInput(String path,String fileName,InputStream inputStream){
        createSDDir(path);
        File file=createSDFile(path+"/"+fileName);
        OutputStream outStream=null;
        try {
            outStream=new FileOutputStream(file);
            byte[] buffer=new byte[4*1024];
            while(inputStream.read(buffer)!=-1){
                outStream.write(buffer);
            }
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                outStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public byte[] readSDFile(String path,String fileName) {
		File file = new File(SDPath+path+"/"+fileName);
		byte[] b = null;
		try {
			@SuppressWarnings("resource")
			FileInputStream inputStream = new FileInputStream(file);
            b = new byte[inputStream.available()];
            inputStream.read(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
    @SuppressLint("SdCardPath")
	public static void writeFileToSD(String s) {  
        String sdStatus = Environment.getExternalStorageState();  
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");  
            return;  
        }  
        try {  
            String pathName="/sdcard/test/";  
            String fileName="yyy.txt";  
            File path = new File(pathName);  
            File file = new File(pathName + fileName);  
            if( !path.exists()) {  
                Log.d("TestFile", "Create the path:" + pathName);  
                path.mkdir();  
            }  
            if( !file.exists()) {  
                Log.d("TestFile", "Create the file:" + fileName);  
                file.createNewFile();  
            }  
            FileOutputStream stream = new FileOutputStream(file);  
           
            byte[] buf = s.getBytes();  
           
            stream.write(buf);            
            stream.close();  
              
        } catch(Exception e) {  
            Log.e("TestFile", "Error on writeFilToSD.");  
            e.printStackTrace();  
        }  
    }  
}