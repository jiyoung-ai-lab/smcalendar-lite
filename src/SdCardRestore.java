package com.waveapp.smcalendarlite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.TodoMemoDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/*
 * 기능 : SD카드로 DB자료 Backup
 * 1.SD카드 존재여부확인
 * 2.존재시 일부DB정보 file backup 처리
 */
public class SdCardRestore extends SMActivity{

	ProgressDialog 	mProgress;
	Context ctx;
	String msg;
	String endmsg;
	String version;
	
	//Read inRead;
	String gubun = ";";
	
	String folder ;
	File file;
	int rowcnt;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        try {
			version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		ctx = this;
		msg 	= ComUtil.getStrResource(this, R.string.info_restore);
        endmsg 	= ComUtil.getStrResource(this, R.string.info_restore_end);
        
    	Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	String islite 	= extras.getString(ComConstant.FOLDER_FROMLITE);
        	if ( islite != null && !islite.equals("")) {
        		msg 	= ComUtil.getStrResource(this, R.string.info_restorefromlite);
                endmsg 	= ComUtil.getStrResource(this, R.string.info_restorefromlite_end);
                //err check        
                if ( fileValidationCheckForLite ()) {
                	mProgress = new ProgressDialog(this);
                	new SDCardRestoreAsyncTask().execute(); 
                }      		
        	}

        } else {
            //err check        
            if ( fileValidationCheck () ) {
            	mProgress = new ProgressDialog(this);
            	new SDCardRestoreAsyncTask().execute(); 
            }      	
        }
        

        
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if ( mProgress != null ) mProgress = null;
	}
	
	//**** AsyncTask클래스는 항상 Subclassing 해서 사용 해야 함.****
	// <<사용 자료형>>
	// background 작업에 사용할 data의 자료형: String 형
	// background 작업 진행 표시를 위해 사용할 인자: Integer형
	// background 작업의 결과를 표현할 자료형: Long
	class SDCardRestoreAsyncTask extends AsyncTask < Void, Integer, Long > {
		
		// 이곳에 포함된 code는 AsyncTask가 execute 되자 마자 UI 스레드에서 실행됨.
		// 작업 시작을 UI에 표현하거나
		// background 작업을 위한 ProgressBar를 보여 주는 등의 코드를 작성.
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress.setMessage(msg);
        	mProgress.show();
		}
		@Override
		protected Long doInBackground(Void... unused) {

			try {
				SdCardFileRead();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}  
		
		// onInBackground(...)에서 publishProgress(...)를 사용하면
		// 자동 호출되는 callback으로
		// 이곳에서 ProgressBar를 증가 시키고, text 정보를 update하는 등의
		// background 작업 진행 상황을 UI에 표현함.
		// (예제에서는 UI스레드의 ProgressBar를 update 함) 
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//SystemClock.sleep(10);
			
		}
		
		// onInBackground(...)가 완료되면 자동으로 실행되는 callback
		// 이곳에서 onInBackground가 리턴한 정보를 UI위젯에 표시 하는 등의 작업을 수행함.
		// (예제에서는 작업에 걸린 총 시간을 UI위젯 중 TextView에 표시함)
		@Override
		protected void onPostExecute(Long result) {
			ComUtil.showToast( ctx,endmsg);
			mProgress.dismiss();
			
			//알람갱신(일정,기념일,할일)
			AlarmHandler ah = new AlarmHandler(ctx);
	    	ah.setAlarmService();
	    	ah.setAlarmServiceForSpecialday();
	    	ah.setAlarmServiceForTodo();
	    	
        	//위젯갱신
        	ViewUtil.updateAllWidget(ctx);
        	
			finish();
		}
		
		// .cancel(boolean) 메소드가 true 인자로 
		// 실행되면 호출되는 콜백.
		// background 작업이 취소될때 꼭 해야될 작업은  여기에 구현.
		@Override
		protected void onCancelled() {

			//super.onCancelled();
			
			if ( mProgress.isShowing() == true  ) mProgress.dismiss();

		}

	}	


   /*
    * file 읽어 db에 저장
    * -.usermanager, specialday, schdule
    */
    private void SdCardFileRead() throws IOException { 
    	
    	String str ;
    	String tablename = "";
    	String [] colname = null ;
    	
    	int colcnt = 0;
    	rowcnt = 0;
    	
    	UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
    	SpecialDayDbAdaper 	mDbSpe 	= new SpecialDayDbAdaper(this);
    	ScheduleDbAdaper 	mDbSch 	= new ScheduleDbAdaper(this);
    	TodoMemoDbAdaper 	mDbTodo	= new TodoMemoDbAdaper(this);  	

    	long id_new = (long)0;
    	
    	//1.파일 read  	
    	try {
			FileInputStream FIO = new FileInputStream ( file.getAbsolutePath() );
			InputStreamReader 	in = new InputStreamReader(FIO, "UTF-8");
			BufferedReader reader  = new BufferedReader(in);
			
        	//한줄씩 읽으면서 db insert 처리
        	while ((str = reader.readLine()) != null) {
        		// >>> read header
        		if (ComUtil.isInStr("HR", str) )  {
            		tablename = str.substring(2, 22).trim();
            		colcnt = Integer.parseInt(str.substring(22, 24));
            		rowcnt = Integer.parseInt(str.substring(24, 29));
            		if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_USERMANAGER)) {            			
            			mDbUser.open();    
            			mDbUser.beginTransaction();
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_SCHEDULE)) { 
            			mDbSch.open(); 
            			mDbSch.beginTransaction();
            			
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_SPECIALDAY)) {
            			mDbSpe.open(); 
            			mDbSpe.beginTransaction();
            			
            			//기념일만 최초 일괄 삭제
            			mDbSpe.deleteSpecialDayForRestore(ComConstant.PUT_USER);
            			
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_TODOMEMO)) {
            			mDbTodo.open(); 
            			mDbTodo.beginTransaction();
            		}   
            	// >>> read trailer
        		} else if (ComUtil.isInStr("TR", str) )  {
        			colname = new String[0];
            		colcnt = 0;
            		rowcnt = 0; 

            		if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_USERMANAGER)) {
            			tablename = "";
            			mDbUser.setTransactionSuccessful();
            			mDbUser.endTransaction();
            			mDbUser.close();
            			mDbUser = null;
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_SCHEDULE)) { 
            			tablename = "";
            			mDbSch.setTransactionSuccessful();
            			mDbSch.endTransaction();
            			mDbSch.close();
            			mDbSch = null;
            			
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_SPECIALDAY)) {
            			tablename = "";
            			mDbSpe.setTransactionSuccessful();
            			mDbSpe.endTransaction();
            			mDbSpe.close();
            			mDbSpe = null;
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_TODOMEMO)) {
            			tablename = "";
            			mDbTodo.setTransactionSuccessful();
            			mDbTodo.endTransaction();
            			mDbTodo.close();
            			mDbTodo = null;
            		}
            	// >>> read colum
        		} else if (ComUtil.isInStr("CL", str) )  {
            		colname = new String[colcnt];
            		int startpos = 2;
            		int endpos 	 = 2;
            		for ( int i = 0 ; i < colcnt ; i++ ) {
            			endpos = str.indexOf(gubun, startpos);  
            			if ( endpos != -1)
            				colname[i] = str.substring(startpos, endpos).trim();
            			else 
            				colname[i] = str.substring(startpos).trim();
            			startpos = endpos + 1;
            		}
            	// >>> read body
        		} else if (ComUtil.isInStr("BD", str ) )  {
        			
            		String [] data = new String[colcnt];
            		int startpos = 2;
            		int endpos 	 = 2;
            		for ( int i = 0 ; i < colcnt ; i++ ) {
            			endpos = str.indexOf(gubun, startpos);    
            			if ( endpos != -1)
            				data[i] = ComUtil.setBlank(ComUtil.changeOtherToEnterKey(str.substring(startpos, endpos)));
            			else 
            				data[i] = ComUtil.setBlank(ComUtil.changeOtherToEnterKey(str.substring(startpos)));
            			startpos = endpos + 1;
            		} 
            		
            		long id = Long.parseLong(data[0]);
            		if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_USERMANAGER)) {            			
            			mDbUser.deleteUsermanagerComplete(id);
                    	id_new = mDbUser.insertRestoreUsermanager(colname, data);   
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_SCHEDULE)) {
            			mDbSch.deleteScheduleComplete(id);
                    	id_new = mDbSch.insertRestoreSchedule(colname, data);           			
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_SPECIALDAY)) {
//            			mDbSpe.deleteSpecialDayForRestore(id, ComConstant.PUT_USER);
                    	id_new = mDbSpe.insertRestoreSpecialDay(colname, data);
            		} else if ( tablename != null && tablename.equals(ComConstant.DATABASE_TABLE_TODOMEMO)) {
            			mDbTodo.deleteTodolist(id);
                    	id_new = mDbTodo.insertRestoreTodolist(colname, data);   
            		}
        		}
			}
        	
			reader.close();
			in.close();
			FIO.close();
     	} catch (IllegalStateException e) {
     		 Log.w("<<<<<<<<<<<<<<<<<<<< id : ", Long.toString(id_new));
            throw new IllegalStateException("Failed to create " + e.getMessage());
           
     	} catch (IOException e) {
     		 Log.w("<<<<<<<<<<<<<<<<<<<< id : ", Long.toString(id_new));
     		 throw new IOException("Failed to create " + e.getMessage()); 
    	} catch( SQLException e ){
//    		if ( mDbUser != null ) {
//    			mDbUser.endTransaction();
//    			mDbUser.close();
//    		}
//    		if ( mDbSpe != null ) {
//    			mDbSpe.endTransaction();
//    			mDbSpe.close();
//    		}
//    		if ( mDbSch != null ) {
//    			mDbSch.endTransaction();
//    			mDbSch.close();
//    		}
//    		if ( mDbTodo != null ) {
//    			mDbTodo.endTransaction();
//    			mDbTodo.close();
//    		}
	    	e.printStackTrace();
    	} catch (Exception e) {
    		 Log.w("<<<<<<<<<<<<<<<<<<<< id : ", Long.toString(id_new));
    		e.printStackTrace();
	    } finally {
//    		if ( mDbUser != null ) {
//    			mDbUser.endTransaction();
//    			mDbUser.close();
//    			mDbUser = null;
//    		}
//    		if ( mDbSpe != null ) {
//    			mDbSpe.endTransaction();
//    			mDbSpe.close();
//    			mDbSpe=null;
//    		}
//    		if ( mDbSch != null ) {
//    			mDbSch.endTransaction();
//    			mDbSch.close();
//    			mDbSch=null;
//    		}
//    		if ( mDbTodo != null ) {
//    			mDbTodo.endTransaction();
//    			mDbTodo.close();
//    			mDbTodo=null;
//    		}	    	
	    }
	    
    }	

	//파일 유효성체크
	private boolean fileValidationCheck() {
	
    	//folder 존재여부 확인
		if ( VersionConstant.APPID.equals(VersionConstant.APP_NORMAL)) {
			folder = ComUtil.getStrResource(this, R.string.app_sdpathnormal);
		} else {
			folder = ComUtil.getStrResource(this, R.string.app_sdpathlite);
		}
		
    	//1.file name 생성
		file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/" + folder + "/"  + ComUtil.makeBackupFileName("")); 
		
    	//2.파일 read  
		if ( !file.exists() )  {
			ComUtil.showToast(ctx, ComUtil.getStrResource(ctx, R.string.err_not_existfile));
			finish();
			return false;
		}
		return true;
	}
	//파일 유효성체크(for lite)
	private boolean fileValidationCheckForLite() {
	
    	//folder 존재여부 확인
		folder = ComUtil.getStrResource(this, R.string.app_sdpathlite);
		
    	//1.file name 생성
		file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/" + folder + "/"  + ComUtil.makeBackupFileName("")); 
		
    	//2.파일 read  
		if ( !file.exists() )  {
			ComUtil.showToast(ctx,ComUtil.getStrResource(ctx, R.string.err_not_existfilelite) );
			finish();
			return false;
		}
		return true;
	}
}
