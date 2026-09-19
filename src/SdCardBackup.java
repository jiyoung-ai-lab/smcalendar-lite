package com.waveapp.smcalendarlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.TodoMemoDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.util.ComUtil;

/*
 * 기능 : SD카드로 DB자료 Backup
 * 1.SD카드 존재여부확인
 * 2.존재시 일부DB정보 file backup 처리
 */
public class SdCardBackup extends SMActivity{

	Context ctx;
	ProgressDialog 	mProgress;
	String msg;
	String endmsg;
	
	Writer outWrite;

	String gubun = ";";
	
	//String version;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        ctx = this;
        
        msg 	= ComUtil.getStrResource(this, R.string.info_backup);
        endmsg 	= ComUtil.getStrResource(this, R.string.info_backup_end);
        
        mProgress = new ProgressDialog(this);
        
        new SDCardBackupAsyncTask().execute(); 
        
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
	class SDCardBackupAsyncTask extends AsyncTask < Void, Integer, Long > {
		
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
				SdCardFileCreate();
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
			//SystemClock.sleep(100);
			
		}
		
		// onInBackground(...)가 완료되면 자동으로 실행되는 callback
		// 이곳에서 onInBackground가 리턴한 정보를 UI위젯에 표시 하는 등의 작업을 수행함.
		// (예제에서는 작업에 걸린 총 시간을 UI위젯 중 TextView에 표시함)
		@Override
		protected void onPostExecute(Long result) {
			ComUtil.showToast(ctx, endmsg);
			mProgress.dismiss();
			finish();
		}
		
		// .cancel(boolean) 메소드가 true 인자로 
		// 실행되면 호출되는 콜백.
		// background 작업이 취소될때 꼭 해야될 작업은  여기에 구현.
		@Override
		protected void onCancelled() {

			super.onCancelled();
			
			if ( mProgress.isShowing() == true  ) mProgress.dismiss();

		}

	}	


   /*
    * 데이터 가져오기 
    * -.usermanager, specialday, schdule
    */
    private void SdCardFileCreate() throws IOException { 
    	
    	Cursor cur = null;
    	String tablename = "";

    	String folder = "";
    	//1.file name 생성
		//folder 생성(foler 가 없는 경우 생성처리)
		if ( VersionConstant.APPID.equals(VersionConstant.APP_NORMAL)) {
			folder = ComUtil.getStrResource(this, R.string.app_sdpathnormal);
		} else {
			folder = ComUtil.getStrResource(this, R.string.app_sdpathlite);
		}
		
		File mDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() , folder ) ;
		
		if ( !mDir.exists() ) mDir.mkdirs() ;
		 
    	File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
    							+ "/" + folder + "/"  + ComUtil.makeBackupFileName("")); 
 
    	//2.파일 생성    	
    	try {
			//파일이 존재하면 _old파일로 백업후 재생성(버전만체크)
			if ( file.exists() ) {
				File fileold = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
						+ "/" + folder + "/"  + ComUtil.makeBackupFileName("old")); 
				if ( fileold.exists()) {
					fileold.delete();					
				}
				file.renameTo(fileold);
			}

			//파일 생성
			file.createNewFile();
			
			FileOutputStream FOS = new FileOutputStream(file.getAbsolutePath());
			
			outWrite = new OutputStreamWriter(FOS,"UTF-8");

     	} catch (IllegalStateException e) {
            throw new IllegalStateException("Failed to create " + e.getMessage());
     	} catch (IOException e) {
     		 throw new IOException("Failed to create " + e.getMessage()); 
     	}


     	//3.데이터 가져와서 file write
     	//-.usermanager, specialday, schdule
     	//3-1. usermanager
     	
    	try {
    		//사용자정보 가져오기
    		tablename = ComConstant.DATABASE_TABLE_USERMANAGER;
    		UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
        	mDbUser.open();
			cur = mDbUser.fetchAllUsermanger();
			writeDataPerTable (tablename, cur );
			if ( cur != null ) cur.close();
			mDbUser.close();
			
			//기념일정보가져오기
			tablename = ComConstant.DATABASE_TABLE_SPECIALDAY;
    		SpecialDayDbAdaper mDbSpe = new SpecialDayDbAdaper(this);
    		mDbSpe.open();
			cur = mDbSpe.fetchAllSpecialDayForType(ComConstant.PUT_USER, " ASC ");
			writeDataPerTable (tablename, cur );
			if ( cur != null ) cur.close();
			mDbSpe.close();
						
			//스케줄정보가져오기
			tablename = ComConstant.DATABASE_TABLE_SCHEDULE;
    		ScheduleDbAdaper mDbSch = new ScheduleDbAdaper(this);
    		mDbSch.open();
			cur = mDbSch.fetchAllSchedule();
			writeDataPerTable (tablename, cur );
			if ( cur != null ) cur.close();
			mDbSch.close();
									
			//할일정보가져오기
			tablename = ComConstant.DATABASE_TABLE_TODOMEMO;
    		TodoMemoDbAdaper mDbTodo = new TodoMemoDbAdaper(this);
    		mDbTodo.open();
			cur = mDbTodo.fetchAllTodolist();
			writeDataPerTable (tablename, cur );
			if ( cur != null ) cur.close();
			mDbTodo.close();
			
			if ( cur != null ) cur.close();
    		
    	} catch( SQLException e ){
	    	e.printStackTrace();
    	} catch (Exception e) {
    		e.printStackTrace();
	    } finally {
	    	//
	    }
	    outWrite.flush();
	    outWrite.close();
	    
	    
    }	

	/*
	 * 데이터가져오기 
	 * format: name-YYYYMMDD-DBVersion
	 * -.header : tag(2:HR)tablename(20), colocnt(2), rowcnt(5)
	 * -.collayout : tag(2:CL)string[]
	 * -.body : tag(2:BD)string[]
	 * -.trailer : tag(2:TR)
	 */
    private void writeDataPerTable( String tablename, Cursor cur ) throws IOException  { 

    	//get colum name 
		String [] col = cur.getColumnNames();
		
		//get colum count
		int colcnt = cur.getColumnCount();			
		
		
		//loop cursor (데이터건수만큼 파일 write)
		int len = cur.getCount();
		
		//데이터건수 없을 경우 exit
		if ( len == 0 )	 return;
		
		//make header 
		String header = "HR" + ComUtil.fillSpaceToBlank(tablename, 20)
					+   ComUtil.fillSpaceToZero(Integer.toString(colcnt), 2)
					+   ComUtil.fillSpaceToZero(Integer.toString(len), 5)
					+   "\n";

		
		//make trailer 
		String trailer = "TR" 
					 +   "\n";
		
		outWrite.write(header);
		
		//make colum section
		StringBuffer buffer = new StringBuffer();		
		buffer.append("CL");

		for ( int j = 0 ; j < colcnt ; j++ ) {
			
			buffer.append(col[j].toString());
			//구분자추가, 단 마지막줄은 개행문자추가
			if ( j != ( colcnt - 1)) {
				buffer.append(gubun);
			} else {
				buffer.append("\n");
			}
		}

		outWrite.write(buffer.toString());
		
		//make data section	
		for ( int i = 0 ; i < len ; i++ ) {			
			buffer = new StringBuffer();
			buffer.append("BD");
			
			for ( int j = 0 ; j < colcnt ; j++ ) {
				String str = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(col[j].toString())));
				buffer.append(ComUtil.changeEnterKeyToOther(str));
				
				//구분자추가, 단 마지막줄은 개행문자추가
				if ( j != ( colcnt - 1)) {
					buffer.append(gubun);
				} else {
					buffer.append("\n");
				}
			}
			
			outWrite.write(buffer.toString());
			
			cur.moveToNext();
		} 
		outWrite.write(trailer);
    }


}
