package com.waveapp.smcalendarlite;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.waveapp.smcalendarlite.common.ColorArray;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.NoticeArray;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.database.CreateDbAdaper;
import com.waveapp.smcalendarlite.database.InstallCheckDbAdaper;
import com.waveapp.smcalendarlite.database.NoticeDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/*------------------------------------
 * 1.메인 process
 * -.db생성 및 기생성된 db 버전에 따라 분기처리  -> httpUrl download (기본테이블 생성은 : CreateSMDataFile)
 *  (국공일 , 음력)
 * -.사용자정보 없는 경우 기본정보 생성
 ------------------------------------*/

public class SMCalendar extends SMActivity {
	
	Dialog di;
	Context ctx ;
	String msg;
	
	
	SpecialDayDbAdaper mDbSpecial;
	
	File file;
	int totalDBSize;
	
    @Override   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //2.View ID Set
        setContentView(R.layout.main);	
        
        this.ctx = this; 
        
        /*------------------------------------
         * = 메인 process
         * -.기본 DB생성 
         * -.기본 DataBaseFile 다운로드(국공일,음력포함)
         * -.이미생성된 경우 달력화면 open
         * -.공지사항  (영문 미처리)
         * -.알람 추가
         * -.lite버전 정보 가져오기
         ------------------------------------*/
    	
        //0.기본테이블 생성 (데이터베이스 버전때문에 테이블 부분생성은 안됨) 
        //기본으로 생성한뒤 변경하든지... 
        //1. 데이터베이스 생성 및 변경 (중요) 
        CreateDbAdaper mDbCreate = new CreateDbAdaper(this);
        mDbCreate.open();
        mDbCreate.close();
        
        //1.데이터베이스 존재여부 확인 및 데이터베이스 버전반영 ** 중요
        if ( ViewUtil.isdatabaseExist ( this ) == false ) {
        	//2. 데이터베이스 재생성 (버전변경분 반영)
        	callDatabaseInitProcess();
        } else {
        	callInitProcess();
        }
        
    }

    private void  callDatabaseInitProcess () {

    	//생각을.... 파일사이즈도 체크하는걸 넣었는데 필요할까? 이중체크
    	file = getDatabaseFromWeb();
        if (  file != null ) {
        	new DBFromHttpUrlAsyncTask().execute(); 
        } else {
        	callInitProcess();
        } 
        
    }

    /*
     * 최초 process 정의
     * 0) 공지사항반영
     * 1) 알람서비스 기동
     * 2) 사용자정보 미존재시 등록 독려
     * 3) 기념일 정보 update 로직 추가 (2012년 -> 2013년으로 갱신)
     */
	private void callInitProcess () {

		//공지사항반영
		setNoticeData();
		
        //알람서비스 기동(일정,기념일,할일)
    	AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmService();	    	
    	ah.setAlarmServiceForSpecialday();
    	ah.setAlarmServiceForTodo();
        
        //사용자정보 존재여부확인
    	UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
        mDbUser.open();
        Cursor cur = mDbUser.fetchAllUsermanger();
        int cnt = cur.getCount();

        if ( cur != null && cnt > 0  ) {
        	updateHoliday2014();
        	//정상처리  -> 이동
//        	callCalendar();
        } else {
        	//사용자등록화면
        	insertDefaultUser(mDbUser);   
        	updateHoliday2014();
        	//-> 이동
//        	callCalendar();  
            ComUtil.showToastLong(this, ComUtil.getStrResource(this, R.string.hello));
        }        
        cur.close();
        mDbUser.close();
        
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        //달력화면 open 
        callCalendar();
    } 
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
    }	    
    @Override    
    protected void onResume() {        
    	super.onResume(); 

      
    }        
    @Override    
    protected void onPause() {        
    	super.onPause();
    	
    }
    @Override 
    protected void onDestroy() { 	
        super.onDestroy();  
    	if ( di != null && di.isShowing() ) {
    		di.dismiss();
    		di = null;
    	}  	
    }
    @Override
	protected void onStart() {
		super.onStart();
	}
    

    @Override
	protected void onRestart() {
		super.onRestart();
		
//		callInitProcess();
        
	}
   
    private void callCalendar() {
    	
    	Intent i = new Intent(this, CalendarMain.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(i);
        
        finish();
    }
    
    /*
     * 공지사항 생성
     */
    private void setNoticeData() {
        
    	String key = "";
    	String appid = VersionConstant.APPID;
    	
    	// 공지사항 반영여부 확인후 미반영시 insert
    	Cursor cur;    	
    	NoticeDbAdaper mNoticeDbHelper = new NoticeDbAdaper(this);
    	mNoticeDbHelper.open();
    	
    	//최대공지key 값 가져오기
    	cur = mNoticeDbHelper.fetchMaxNotice(appid);
    	int arrayLength = cur.getCount();
		for(int i = 0 ; i < arrayLength ; i++) {			
			key = cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_KEY));				
		}
        if ( cur != null ) cur.close();
       	
        //배열정보와 key 정보비교 없는 건만 반영 (다국어는 나중에)
        NoticeArray noticeArr = new NoticeArray(this);
        String [][] dataArr ;
        
        //한국어 공지      
        if ( appid.equals(VersionConstant.APP_NORMAL)) {        	
        	dataArr = noticeArr.getNoticeFormallyKr();
        } else {
        	dataArr = noticeArr.getNoticeLiteKr();
        }
        noticeDataInsert(mNoticeDbHelper, appid, ComConstant.LOCALE_KO, key, dataArr);
        
        //영문공지      
        if ( appid.equals(VersionConstant.APP_NORMAL)) {        	
        	dataArr = noticeArr.getNoticeFormallyEn();
        } else {
        	dataArr = noticeArr.getNoticeLiteEn();
        }
        noticeDataInsert(mNoticeDbHelper, appid, ComConstant.LOCALE_EN, key, dataArr);
        
        //일본어공지      
        if ( appid.equals(VersionConstant.APP_NORMAL)) {        	
        	dataArr = noticeArr.getNoticeFormallyJa();
        } else {
        	dataArr = noticeArr.getNoticeLiteJa();
        }
        noticeDataInsert(mNoticeDbHelper, appid, ComConstant.LOCALE_JA, key, dataArr);
        
        //중국어공지      
        if ( appid.equals(VersionConstant.APP_NORMAL)) {        	
        	dataArr = noticeArr.getNoticeFormallyZh();
        } else {
        	dataArr = noticeArr.getNoticeLiteZh();
        }
        noticeDataInsert(mNoticeDbHelper, appid, ComConstant.LOCALE_ZH, key, dataArr);   
        
        mNoticeDbHelper.close(); 
    	
    } 

    private void noticeDataInsert ( NoticeDbAdaper mNoticeDbHelper, String appid , String locale, String key, String [][] dataArr) {
        int len = dataArr.length;
        for(int i = 0 ; i < len ; i++) {
            if ( key != null && !key.equals("")) {
            	if ( key.equals(dataArr[i][0]) ) {
            		key = "";            		
            	}
            } else {
            	 mNoticeDbHelper.insertNotice(
            			 dataArr[i][0], locale, appid, dataArr[i][1], dataArr[i][2], dataArr[i][3], "");
            }    	
        }   	
    }

	//**** AsyncTask클래스는 항상 Subclassing 해서 사용 해야 함.****
	// <<사용 자료형>>
	// background 작업에 사용할 data의 자료형: String 형
	// background 작업 진행 표시를 위해 사용할 인자: Integer형
	// background 작업의 결과를 표현할 자료형: Long
	// Exception 처리 및 cancel 처리 
	// 진행형 progressbar 
	class DBFromHttpUrlAsyncTask extends AsyncTask < Void, Integer, Long > {
		
	     private ProgressDialog progressDialog = null;
	     int downloadedSize = 0;

	    	
	    HttpURLConnection conn = null;
    			
		// 이곳에 포함된 code는 AsyncTask가 execute 되자 마자 UI 스레드에서 실행됨.
		// 작업 시작을 UI에 표현하거나
		// background 작업을 위한 ProgressBar를 보여 주는 등의 코드를 작성.
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(ctx);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(ComUtil.getStrResource(ctx, R.string.info_createdatabase));
			progressDialog.setProgress(0);
			progressDialog.setTitle(ComUtil.getStrResource(ctx, R.string.app_name));
			progressDialog.setMax(0);
			progressDialog.setCancelable(false);	
			progressDialog.setButton(ComUtil.getStrResource(ctx, R.string.cancel), 
									new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) { 
					//process cancel
					DBFromHttpUrlAsyncTask.this.cancel(true);

				}
	  		});
			
	        progressDialog.show();
		}
		@Override
		protected Long doInBackground(Void... unused) {
			return createDatabaseFromHttpUrl();
		}  
		
		// onInBackground(...)에서 publishProgress(...)를 사용하면
		// 자동 호출되는 callback으로
		// 이곳에서 ProgressBar를 증가 시키고, text 정보를 update하는 등의
		// background 작업 진행 상황을 UI에 표현함.
		// (ProgressBar를 update 함) 
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			
//			SystemClock.sleep(300);
			
		}
		
		// onInBackground(...)가 완료되면 자동으로 실행되는 callback
		// 이곳에서 onInBackground가 리턴한 정보를 UI위젯에 표시 하는 등의 작업을 수행함.
		// (예제에서는 작업에 걸린 총 시간을 UI위젯 중 TextView에 표시함)
		@Override
		protected void onPostExecute(Long result) {
			
			if ( progressDialog.isShowing() ) progressDialog.dismiss();
//			progressDialog.dismiss();
			
			if ( result == 0 ) {
				
		        //1. 데이터베이스 생성 및 변경  
		        CreateDbAdaper mDbCreate = new CreateDbAdaper(ctx);
		        mDbCreate.open();
		        mDbCreate.close();	        
		         
				//2. install 완료여부 insert ** 중요
				insertInstallCheck();  
		        
		        //3.위젯갱신
				ViewUtil.updateAllWidget(ctx); 
				
				//4.초기설정
				callInitProcess();
				
				
			} else if ( result == 1 ) {
				//network 장애 warning message
				ComUtil.showToast(ctx, ComUtil.getStrResource(ctx, R.string.err_httpconnection));
				rollBackProcess();
				
			} else {
				//cancel warning message
				String message = String.format(ComUtil.getStrResource(ctx, R.string.err_unknown), Long.toString(result));
				ComUtil.showToast(ctx, message);
				rollBackProcess();
				
			}
		}
		
		// .cancel(boolean) 메소드가 true 인자로 
		// 실행되면 호출되는 콜백.
		// background 작업이 취소될때 꼭 해야될 작업은  여기에 구현.
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			//cancel warning message
			ComUtil.showToast(ctx, ComUtil.getStrResource(ctx, R.string.info_installcancel));
			
			rollBackProcess();

		}
		
	    private long createDatabaseFromHttpUrl ( ) {
	    	
	    	System.setProperty("http.keepAlive", "false");
	        
			try {
				//버전별로 db파일 생성
				//as-is : 1.0 https://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv1.0 최초버전
				//as-is : 1.1 https://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv1.1  기념일 추가
				//as-is : 2.0 https://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv2.0 알람colum추가
				//as-is : 2.1 https://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv2.1 기념일갱신
				//to-be : 2.2 https://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv2.2 기념일갱신
				//  ----> 테스트 경로로 검증후 반영
				URL url = new URL("http://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv2.2");
//				URL url = new URL("http://waveapp-smcalendar.googlecode.com/svn/trunk/db/smdbv2.1");
				//create the new connection
				conn = (HttpURLConnection) url.openConnection();
		
				//set up some things on the connection
//				
		        if (Build.VERSION.SDK_INT < 14) // //안드로이드 4.0에서 오류가 있어 막은 로직
		        {
		        	conn.setDoOutput(true);
//		        	conn.setDefaultUseCaches(false);
		        } 				
		        conn.setUseCaches(false);
		        conn.setRequestMethod("GET");
		        conn.setRequestProperty("Content-Type", "text/plain");
		
				//HttlUrl connect
		        conn.connect();
		        
		        //get data
		  	   	totalDBSize = conn.getContentLength();
		  	   	progressDialog.setMax(totalDBSize / 1024);
		  	   	
				//파일write	        
		  	   	InputStream inputStream = conn.getInputStream();
		  	   	BufferedInputStream buffeerInStream = new BufferedInputStream(inputStream);
				FileOutputStream fileOutput = new FileOutputStream(file);

				
				int read = -1;	
				byte[] buffer = new byte[1024];	
				downloadedSize = 0;
				while ((read = buffeerInStream.read(buffer, 0, 1024)) != -1) {	  	   	   		
					fileOutput.write(buffer, 0, read);
					downloadedSize += read;						
			    	progressDialog.setProgress( downloadedSize / 1024);
			        
				}
				
				fileOutput.flush();
				fileOutput.close();
				
				buffeerInStream.close(); 	     	  
				inputStream.close();
				
				if ( fileOutput != null ) fileOutput = null;
				if ( inputStream != null ) inputStream = null;
				if ( buffeerInStream != null ) buffeerInStream = null;
				
				conn.disconnect();

				return 0;

			} catch (UnknownHostException e) {
				
				conn.disconnect();
				conn = null;
				if ( progressDialog.isShowing() ) progressDialog.dismiss();					
				Log.e("UnknownHostException : ", e.toString());
//				e.printStackTrace();
				return 1;
		  	   	
			} catch (MalformedURLException e) {
				conn.disconnect();
				conn = null;
				if ( progressDialog.isShowing() ) progressDialog.dismiss();		
				Log.e("MalformedURLException : ", e.toString());
				e.printStackTrace();
				return 2;
			} catch (FileNotFoundException e) {
				if ( progressDialog.isShowing() ) progressDialog.dismiss();	
				Log.e("FileNotFoundException : ", e.toString());			
				e.printStackTrace();
				return 3;
	     	} catch (IOException e) {	
	     		if ( progressDialog.isShowing() ) progressDialog.dismiss();		
	      	   	Log.e("IOException : ", e.toString());
	      	   	return 4;
	      	} 
	      	
	    }
	    
	    private void rollBackProcess() {			
			
			//파일 삭제
			if ( file != null ) {
				file.delete();
				file = null;
			}		
			
			//database delete
			ctx.deleteDatabase(ComConstant.DATABASE_NAME);
			
			if ( conn != null ) {
				conn.disconnect();
				conn = null;
			}
			if ( progressDialog.isShowing() == true  ) progressDialog.dismiss();
			
			//app finish
			finish();			
	    } 
	}	

//    /*
//     * Exist Check Database
//     */
//    private boolean isdatabaseExist(  ) {
//    	boolean ret = true;
//        InstallCheckDbAdaper mCheckDbHelper = new InstallCheckDbAdaper(this);
//        mCheckDbHelper.open();
//        ret = mCheckDbHelper.fetchInstallCheck();
//        mCheckDbHelper.close();
//    	
//    	//오류로인한삭제방지로 default true
//    	return ret;
//    	
//    }	
    
    /*
     * 데이터베이스 완료체크값 
     */
    private boolean insertInstallCheck ( ) {
    	
        InstallCheckDbAdaper mCheckDbHelper = new InstallCheckDbAdaper(this);
        
        mCheckDbHelper.open();
        long id = mCheckDbHelper.insertInstallCheckComplete();
        mCheckDbHelper.close();
        
        if ( id > 0 ) return true;

        return false;
  	
    }  

    
    //최초사용시 기본사용자생성(사용편이성목적)
    private void insertDefaultUser ( UsermanagerDbAdaper mDbUser ) {
    	
    	ColorArray col = new ColorArray(this);
        Integer[] arr = col.getColorArray();
         
    	String name  	 = ComUtil.getStrResource(this, R.string.defaultuser);
    	String birth 	 = SmDateUtil.getTodayDefault();
    	String relation  = "0";
    	String usercolor = ComUtil.intToString(arr[9]);
    	String tel1 	 = "";
    	String address 	 = "";
    	String memo 	 = "";
    			
    	try {
    		mDbUser.insertUsermanager(name, birth, relation, usercolor, tel1, address, memo);
    	} catch( SQLException e ){
	    	e.printStackTrace();
	    } finally {
	    	///
	    }
    	
    }
 
    @Override
	public void onLowMemory() {
		super.onLowMemory();
		ComUtil.showToast(this, "Low Memory!!" );
		onDestroy();
	} 
    
    /*
     * db exist check and create file
     */		
    public File getDatabaseFromWeb(  ) {
    	
    	String packagename = this.getPackageName();  		  
    	String folder = "/data/data/" + packagename + "/databases/";
    	String filename = folder + ComConstant.DATABASE_NAME;
	
    	//1) folder 생성(foler 가 없는 경우 생성처리)
    	File mDir = new File( folder ) ;  		
    	if ( !mDir.exists() ) mDir.mkdirs() ;
    		
  	  	File file = new File(filename);   	  
   	 
   	   	//2) 기존에 파일이 존재하는 경우  delete 없으면 생성 후 처리 
   	   	if ( file.exists()) file.delete();  
   	   	
   	   	//2) 기존에 파일이 존재하는 경우  skip 없으면 생성 후 처리 (최소 3 MB) : 이중체크
   	   	if (file.exists() && file.length() > ( 1024 * 1024 * 3 ) ) { 
   	   		return null; 
   	   	} else {
   	   		if ( file.exists()&& file.length() <= ( 1024 * 1024 * 3 ) ) file.delete();
   	   		//파일생성
  	   	   	try {
				file.createNewFile();
			} catch (IOException e) {
				file = null;
				e.printStackTrace();
			}	
   	   		return file;   	   		 
   	   	}
   	   	
   }
    
    /*
     * 기념일 갱신을 위한 로직....
     */
    private void updateHoliday2014 () {
	        
        //2.국공일 정보 update
        //-> 국공일정보 존재하지 않는 경우 xml 값 읽어서 처리
        
		SpecialDayDbAdaper mDbSpecialPart = new SpecialDayDbAdaper(this);
		mDbSpecialPart.open();
        
        Cursor cur = mDbSpecialPart.fetchAllSpecialDayForYearType( ComConstant.PUT_BATCH, ComConstant.NATIONAL_KO, "holiday2014"," ASC " );
        int cnt = cur.getCount();
        if ( cur != null ) cur.close();
        mDbSpecialPart.close();
       
        
        //2013년 자료 없으면 xml 정보 처리
        if ( cnt == 0  ) {  
//        	ComUtil.showToastLong(this, "Holiday Update~~");
            msg 	= this.getResources().getString(R.string.info_batchholiday).toString(); 
        	new BathcHolidayAsyncTask().execute(); 
        	
        } else {
        	callCalendar();
        }
           
            
    }
    
	//**** AsyncTask클래스는 항상 Subclassing 해서 사용 해야 함.****
	// <<사용 자료형>>
	// background 작업에 사용할 data의 자료형: String 형
	// background 작업 진행 표시를 위해 사용할 인자: Integer형
	// background 작업의 결과를 표현할 자료형: Long
	class BathcHolidayAsyncTask extends AsyncTask < Void, Integer, Long >  {
		
		private ProgressDialog mProgress = null;
		
		// 이곳에 포함된 code는 AsyncTask가 execute 되자 마자 UI 스레드에서 실행됨.
		// 작업 시작을 UI에 표현하거나
		// background 작업을 위한 ProgressBar를 보여 주는 등의 코드를 작성.
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = new ProgressDialog(ctx);
			mProgress.setMessage(msg);
			mProgress.show();
		}
		@Override
		protected Long doInBackground(Void... unused) {
			
			return (long) holidayXmlParsing();
		}  
		
		// onInBackground(...)에서 publishProgress(...)를 사용하면
		// 자동 호출되는 callback으로
		// 이곳에서 ProgressBar를 증가 시키고, text 정보를 update하는 등의
		// background 작업 진행 상황을 UI에 표현함.
		// (예제에서는 UI스레드의 ProgressBar를 update 함) 
		@Override
		protected void onProgressUpdate(Integer... progress) {
//			mCount.setText(Integer.toString(insertCount));
			
		}
		
		// onInBackground(...)가 완료되면 자동으로 실행되는 callback
		// 이곳에서 onInBackground가 리턴한 정보를 UI위젯에 표시 하는 등의 작업을 수행함.
		// (예제에서는 작업에 걸린 총 시간을 UI위젯 중 TextView에 표시함)
		@Override
		protected void onPostExecute(Long result) {
			
			if ( mProgress.isShowing() == true  ) {
				mProgress.dismiss();
				mProgress = null;
			}
			
			callCalendar();
			
			//오류발생시 메시지 처리
			if ( result > 0 ) {
				ComUtil.showToastLong(ctx, ComUtil.getStrResource(ctx, R.string.err_batchholiday));
			}
			
		}
		
		// .cancel(boolean) 메소드가 true 인자로 
		// 실행되면 호출되는 콜백.
		// background 작업이 취소될때 꼭 해야될 작업은  여기에 구현.
		@Override
		protected void onCancelled() {

			super.onCancelled();
			
//			holidayBatchAllDelete("holiday2011");
			holidayBatchAllDelete("holiday2014");
			
			if ( mProgress.isShowing() == true  ) {
				mProgress.dismiss();
				mProgress = null;
			}
			
			if ( mDbSpecial != null ) mDbSpecial.close();
			
			callCalendar();
		}

	} 
	
	/*
     * xml parsing & db insert
     * holiday2014 : 휴일테이블 tag (depth : 2)
     *               colomn tag (depth : 3)
     */
	
    private int holidayXmlParsing() { 
    	
    	int event = 0;  
    	
    	String name = "";        
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
			InputStream inputFile = getResources().openRawResource(R.raw.holiday2014);
			parser.setInput(inputFile, "UTF-8");
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
			return 1;
			
		}	

		
        SpecialDayInfo special = new SpecialDayInfo();

        try{
        	//transaction 시작
//        	insertCount = 0;

            mDbSpecial = new SpecialDayDbAdaper(this);
            mDbSpecial.open();
            
        	mDbSpecial.beginTransaction();
        	
        	//데이터삭제            
            holidayBatchAllDelete("holiday2011");
    		holidayBatchAllDelete("holiday2012");
    		holidayBatchAllDelete("holiday2013");
    		holidayBatchAllDelete("holiday2014");
    		        	
        	//파일읽어서 생성
			while ( (event = parser.next()) != XmlPullParser.END_DOCUMENT) {
        		
        		int parserEvent = parser.getEventType();
        		
    			switch (parserEvent) {
				case XmlPullParser.START_TAG :
					name = parser.getName();
					if ( name != null && name.equals("holiday2014")) {
						//1.시작
						special =  new SpecialDayInfo();
					} else {
						//내용시작-없음
					}
					break;
				case XmlPullParser.END_TAG :
					name = parser.getName();
					if ( name != null && name.equals("holiday2014")) {
						//4.db insert
						//special.setMakeThisDate();
						holidayRowInsert(special, "holiday2014");
//						insertCount++;
					} else {
						//내용시작 - 없음
					}
					break;
				case XmlPullParser.TEXT :
					String value = ComUtil.setBlank(parser.getText()) ;
				
					if ( value != null && !value.trim().equals("")) {
						if ( name != null && name.equals("ID")) {
							//없음
						} else if ( name != null && name.equals("locale")) {
							special.setLocale(value);
						} else if ( name != null && name.equals("gubun")) {
							special.setGubun(value);
						} else if ( name != null && name.equals("holidayyn")) {
							special.setHolidayYn(value);
						} else if ( name != null && name.equals("name")) {
							special.setName(value);
						} else if ( name != null && name.equals("subname")) {
							special.setSubName(value);							
						} else if ( name != null && name.equals("repeatyn")) {
							special.setRepeatYn(value);
						} else if ( name != null && name.equals("year")) {
							special.setYear(value);
						} else if ( name != null && name.equals("month")) {
							special.setMonth(ComUtil.fillSpaceToZero(value, 2));
						} else if ( name != null && name.equals("day")) {
							special.setDay(ComUtil.fillSpaceToZero(value, 2));
						} else if ( name != null && name.equals("memo")) {
							special.setMemo(value);
						}
					}
					break;
				default:
					break;
				}
        	}
	    } catch( XmlPullParserException e ){
	    	e.printStackTrace(); 
	    	mDbSpecial.endTransaction();
	    	mDbSpecial.close();
	    	return 1 ;
	    	
	    	
	    } catch( IOException e ){
		     e.printStackTrace();
		     mDbSpecial.endTransaction();
		     mDbSpecial.close();
		     return 2;
		     
	    } finally {

	    }
    	mDbSpecial.setTransactionSuccessful();
    	mDbSpecial.endTransaction();
    	mDbSpecial.close();	 
    	
    	return 0;
	    //Log.w(">>>>>>>>>>>>>>>> holidayXmlParsing insertcnt  : ", Integer.toString(insertCount));
    } 
    
    /*
     * Call Database
     */
    private void holidayRowInsert(SpecialDayInfo special, String filename ) {   

    	String locale 		= ComUtil.setBlank(special.getLocale());
    	String gubun 		= ComUtil.setBlank(special.getGubun());
    	String holidayyn 	= ComUtil.setBlank(special.getHolidayYn());
    	String name		 	= ComUtil.setBlank(special.getName());
    	String subname		= ComUtil.setBlank(special.getSubName());
    	String repeatyn 	= ComUtil.setBlank(special.getRepeatYn());
    	String year 		= ComUtil.setBlank(special.getYear());
    	String monthday 	= 
    			ComUtil.setBlank(special.getMonth()) + ComUtil.setBlank(special.getDay());
    	
    	String memo 		=  ComUtil.setBlank(special.getMemo());
    	String delyn 		= "";
    	String deldate 		= ""; 
    	long id = 0;
    	try {
    		id = mDbSpecial.insertSpecialDay(locale, gubun, "", holidayyn, name, subname, repeatyn, 
					year, monthday, "", memo, delyn, deldate, "", "");
    	} catch( SQLException e ){
	    	e.printStackTrace();
	    	Log.e(">>>>>>>>>>>>>>>> holidayXmlParsing id  : ", Long.toString(id));
	    } finally {
	    	//Log.w(">>>>>>>>>>>>>>>> holidayXmlParsing finally  : ", Integer.toString(insertCount));
	    }
    	
    }
   
    private void holidayBatchAllDelete ( String filename ) {  

    	String memo 		= filename.trim();
    	
    	try {
    		 mDbSpecial.deleteBatchSpecialDay(memo);
    	} catch( SQLException e ){
	    	e.printStackTrace();
	    } finally {
	    	//Log.w(">>>>>>>>>>>>>>>> holidayXmlParsing finally  : ", Integer.toString(id));
	    }
    	
    }	
}