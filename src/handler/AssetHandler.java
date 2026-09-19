package com.waveapp.smcalendarlite.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;

/*
 * asset data get/send 
 */
public class AssetHandler  extends SMActivity {
	
	private final Context mCtx;
	//private String  mParam1 ;
	
	public AssetHandler(Context ctx) {
        this.mCtx = ctx;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
	}
    
    public void CopyDbFromAsset( ) {
    	
    	  AssetManager manager = mCtx.getAssets();
    	  
    	  String packagename = mCtx.getPackageName();  
    	  String assetdbname = ComConstant.DATABASE_NAME + ".mp4";
    	  
    	  String folder = "/data/data/" + packagename + "/databases/";
    	  String filePath = folder + ComConstant.DATABASE_NAME;
    	  
  		
//  		String folder = ComUtil.getStrResource(this, R.string.app);
  		
    	  //1) folder 생성(foler 가 없는 경우 생성처리)
    	  File mDir = new File( folder ) ;  		
    	  if ( !mDir.exists() ) mDir.mkdirs() ;

    	  FileOutputStream fos = null;
    	  BufferedOutputStream bos = null;    	 

    	  try {

	    	   InputStream is = manager.open("db/" + assetdbname);
	
	    	   BufferedInputStream bis = new BufferedInputStream(is);
	//    	   InputStreamReader 	in = new InputStreamReader(FIO, "UTF-8");
	//			BufferedReader reader  = new BufferedReader(in);
	    		 
	     	  File file = new File(filePath);   	  
	      	 
	 	   	   //2) 기존에 파일이 존재하는 경우 skip 없으면 생성
	 	   	   if (!file.exists()) {
	 	   		   file.createNewFile();
	 	   	   }
	 	   	   	
	    	   fos = new FileOutputStream(file);	
	    	   bos = new BufferedOutputStream(fos);	    	 
	
	    	   int read = -1;	
	    	   byte[] buffer = new byte[1024];	
	    	   while ((read = bis.read(buffer, 0, 1024)) != -1) {
	    	   
	    		   
	    		   bos.write(buffer, 0, read);	
	    	   }
	
	    	   bos.flush(); 
	    	   bos.close();
	    	   fos.close();
	    	   bis.close();
	    	   is.close();
    	  } catch (IOException e) {	
	    	   Log.e("ErrorMessage : ", e.getMessage());
    	  }
  	
    }
    
}
