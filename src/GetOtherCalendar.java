package com.waveapp.smcalendarlite;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.waveapp.smcalendarlite.adapter.OtherCalendarListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.handler.OtherCalendarHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;
 
/*
 * 다른 달력 스케줄을 customList형태로 보여준다
 */
public class GetOtherCalendar extends SMActivity implements OnClickListener, OnItemClickListener{


	Context ctx;
	ArrayList<ScheduleInfo> mList;
    OtherCalendarListAdapter mAdapter;
    
    ListView listView;   
    TextView mOtherSchedule;  
    Spinner  	mUser; 
    
    LinearLayout mGetLin;
    ImageButton mGetBtn;
    TextView 	mGetTv;
    LinearLayout mAllLin;
    ImageButton mAllBtn;
    TextView 	mAllTv;
    int mValidCnt;
    boolean allcheckyn ;
    int getCnt ;
    
    Long 	mID  ;
    String  mName;
    String  mMessage;
    String  mMsgParm;
    
    String  mCalendarChoice;

	Long 		mUserid;
    ArrayList<Long> userIdList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.othercalendar_list);

        ctx = this;
        listView 	= (ListView) findViewById(android.R.id.list);
        mOtherSchedule= (TextView) findViewById(R.id.tv_other); 
        mUser 		= (Spinner) findViewById(R.id.userspinner); 
        
        mGetLin 	= (LinearLayout)findViewById( R.id.lin_get ) ;
        mGetBtn 	= (ImageButton)findViewById( R.id.confirm ) ;
        mGetTv		= (TextView) findViewById(R.id.tv_confirm); 
        mAllLin 	= (LinearLayout)findViewById( R.id.lin_allcheck ) ;
        mAllBtn 	= (ImageButton)findViewById( R.id.all ) ;
        mAllTv		= (TextView) findViewById(R.id.tv_all);       
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mCalendarChoice = savedInstanceState.getString(ComConstant.CALCHOICE_GUBUN);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCalendarChoice = extras.getString(ComConstant.CALCHOICE_GUBUN);
        }
        
        mOtherSchedule.setText(String.format(ComUtil.getStrResource(this, R.string.getotherschedule), mCalendarChoice));
        mAllTv.setText(ComUtil.getStrResource(this, R.string.allget));
        mGetTv.setText(ComUtil.getStrResource(this, R.string.choiceget));
        
        userSpinnerSet();

		
		//사용자색상지정(default)
		mUserid = ComUtil.setSpinnerFromDb(userIdList, mUser);	
		setUserColorToSpinner(mUserid);
		
        fillData();
        
    	// activity button
    	setClickListener(); 
            
    }
    private void setClickListener () {
    	
        mGetLin.setOnClickListener(this);
        mAllLin.setOnClickListener(this);
        listView.setOnItemClickListener(this);

    } 

	@Override
	public void onClick(View v) {
		
		if ( v == mGetLin ) {
			
			mGetBtn.setPressed(true);
			
			if (mAdapter.getCheckCount() > 0 ) {
					showDialog(R.string.title_getotherschedule);
			} else {
				ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.msg_notselected));
			}		
		} else if ( v == mAllLin ) {
			
			mAllBtn.setPressed(true);
			
			if ( mValidCnt > 0 ) {
				showDialog(R.string.msg_getallschedule);
			} else {
				ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.err_notvaliddata));
			}
		}
	}  

    private void fillData() {
    	
    	// issue : 데이터건수에 따른 처리 필요 
    	// 1. 전체스케줄 가져오기 2.사용자id에 대해 가져오기
    	//    -. parent : string, child : scheduleinfo array
    	
    	mList = new ArrayList<ScheduleInfo>();
    	
        mAdapter = new OtherCalendarListAdapter(this, R.layout.othercalendar_row, mList);
        listView.setAdapter(mAdapter);
        
        if ( mCalendarChoice != null && mCalendarChoice.equals("phone")) {
        	getFromPhoneCalendar();
        } else {
        	//getFromGoogleCalendar();  	
        	ComUtil.showToast(this, "Not Open");
        }
    	
		mAdapter.notifyDataSetChanged();
		
        int tot = mAdapter.getCount();
        
        //공통 top menu setting       
        ComMenuHandler.callComTopMenuAction(this,  ComUtil.setActionTitle(this, R.string.title_getotherschedule, tot ), View.GONE);
		

    }
	/*
     * 핸드폰내 스케줄 db에서 조회 
     * (종료일 기준 역순 정렬)
     */
    private void getFromPhoneCalendar( ) {
    	
    	ArrayList<ScheduleInfo> list = new ArrayList<ScheduleInfo>();
    	
    	mList.clear();
    	
    	OtherCalendarHandler calHd = new OtherCalendarHandler(this);
    	list = calHd.getAllScheduelFromPhone(); 
    	mValidCnt = calHd.getValidCnt();

		//음력 때문에 정렬후 처리.... (후덜)
		Collections.sort( list , myComparator);
		Collections.reverse(list);
		mList.addAll(list);
		
    }
//	/*
//     * Google Calendar  조회 (나중에)
//     */
//    private void getFromGoogleCalendar( ) {
//    	
//    	mList.clear();
//    	
//    	OtherCalendarHandler calHd = new OtherCalendarHandler(this);
//    	
//    	mList.addAll(calHd.getAllScheduelFromGoogle());
////    	
//    }    
    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    }
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
        fillData();
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
        RecycleUtil.recursiveRecycle(listView);
        if ( mAdapter != null ) mAdapter = null;
        if ( mList != null ) mList = null;        
 
    }

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		removeDialog(id);
		dialog = null;
		
	}	
	
    @Override
    protected Dialog onCreateDialog(int id) { 
    	MessageHandler msgHd = new MessageHandler(this);
    	Dialog di;
    	
    	switch (id) { 

    	case ComConstant.DIALOG_INPUTCHECK:
    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.inputcheck), R.string.msg_inputcheck, mMsgParm);
    					
    		return di;
		case R.string.title_getotherschedule:

			mMsgParm = ComUtil.getStrResource(this, R.string.title_getotherschedule);
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_finishconfirm, mMsgParm);
			di.setOnCancelListener( new DialogInterface.OnCancelListener() {
				
					@Override
					public void onCancel(DialogInterface dialog) { 
						
						allcheckyn = false;
						new BatchScheduleAsyncTask().execute(); 
						
					}
			});
					
			return di;
		case R.string.msg_getallschedule:
			
			mMsgParm = ComUtil.intToString(mValidCnt);
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_getallschedule, mMsgParm);
			di.setOnCancelListener( new DialogInterface.OnCancelListener() {
				
					@Override
					public void onCancel(DialogInterface dialog) { 
						allcheckyn = true;
						new BatchScheduleAsyncTask().execute(); 
						
					}
			});
					
			return di;

    	}  
    	
    	return null;
    }	


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

	/*
	 * confirm button event (phone  -> smcalendar)
	 */
	private void confirmEvent() { 
    	
		ScheduleInfo info = new ScheduleInfo();
		getCnt = 0;
		
		//1.check list get
		if ( mAdapter.getCount() == 0 ) return;
		
		//2.validation check
		int glen = mAdapter.getCount();
		for(int i = 0 ; i < glen ; i++) {
			info = (ScheduleInfo) mAdapter.getItem(i);
			if ( info.isChoice()) {
				if ( validationCheck(info) == false ) return;

			}
		}   
		
		//3.data insert
		ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open(); 		
		for(int i = 0 ; i < glen ; i++) {
			info = (ScheduleInfo) mAdapter.getItem(i);
			if ( info.isChoice()) {
		    	saveState(mDbHelper, info); 			    	
			}
		} 
		mDbHelper.close();

		
	}

	/*
	 * confirm button event (google -> smcalendar)
	 */
	private void confirmEventAll() { 
		
		//오래걸림...
        
		ScheduleInfo info = new ScheduleInfo();
		getCnt = 0;
		
		//1.check list get
		if ( mAdapter.getCount() == 0 ) return;
		
		//2.validation check
		int glen = mAdapter.getCount();
		for(int i = 0 ; i < glen ; i++) {
			info = (ScheduleInfo) mAdapter.getItem(i);
			if ( info.getCangetyn() == null || (info.getCangetyn() != null && info.getCangetyn().equals(""))) {
				if ( validationCheck(info) == false ) return;
			}
		}   
		
		//3.data insert
		ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open(); 
		for(int i = 0 ; i < glen ; i++) {
			info = (ScheduleInfo) mAdapter.getItem(i);
			if ( info.getCangetyn() == null || (info.getCangetyn() != null && info.getCangetyn().equals(""))) {
		    	saveState(mDbHelper, info); 
			}
		} 	
		mDbHelper.close();
        
	}	
    /*
     * Call Database ( 가져온 데이터 db에 insert)
     */
    private void saveState( ScheduleDbAdaper mDbHelper, ScheduleInfo info ) { 
    	
    	boolean ret = false;
			
    	Long lUserid 		= mUserid.longValue(); 
    	String sName 		= info.getScheduleName(); 
    	String sCycle 		= info.getCycle();
    	int [] dayofweek 	= info.getDayOfWeek();
    	String [] dayofweekStr = new String[7];
    	//주기가 있는 경우만 
    	if ( sCycle != null && !sCycle.trim().equals("")) {
        	for ( int i = 0 ; i < 7 ; i++ ) {
        		if ( dayofweek[i] > 0 ) dayofweekStr[i] = "Y";
        		else  dayofweekStr[i] = "";
        	}    		
    	} else {
        	for ( int i = 0 ; i < 7 ; i++ ) {
        		dayofweekStr[i] = "";
        	}
    	}
    		
    	String sStartDate 	= info.getStartDate();
    	String sEndDate 	= info.getEndDate();
    	String sAllDay 		= info.getAllDayYn();
    	String sStartTime 	= info.getStartTime();
    	String sEndTime 	= info.getEndTime();
    	String sTel1 		= ""; 
    	String sAlarm 		= info.getAlarmYn();
    	String sMemo		="from MyCalendar";

        //신규
		long id = mDbHelper.insertSchedule(
				sName, lUserid, sCycle,
				dayofweekStr[0], dayofweekStr[1], dayofweekStr[2], dayofweekStr[3], dayofweekStr[4],
				dayofweekStr[5], dayofweekStr[6],  
				sStartDate, sEndDate, sAllDay, sStartTime, sEndTime,
				sAlarm, sTel1, "", 0, sMemo, "", "");           

    	
    	if (  id != (long)0 ) {
    		//다른일정 key 정보 update
    		Long otherid 		= info.getOtherId();
    		String otherkind	= info.getOtherkind();
    		ret = mDbHelper.updateOtherSchedule(id, otherkind, otherid);
    		if ( ret == true ) {
    			getCnt++;
    		}
    		
    	}
    }
	private Boolean validationCheck( ScheduleInfo info ) {

		mMsgParm = "";
		
		String schedulename = info.getScheduleName();
		//스케줄명 필수처리
		if (ComUtil.getNullCheck(schedulename) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_schedulename) + 
						"(" + Long.toString(info.getOtherId()) + ")" ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;
		} else if ( info.getCycle() != null && info.getCycle().trim().equals("Y")  ){
			boolean isweekcheck = false;
			int [] dayofweek = info.getDayOfWeek();
			if ( dayofweek[0] > 0 ) isweekcheck = true;
			if ( dayofweek[1] > 0 ) isweekcheck = true;
			if ( dayofweek[2] > 0 ) isweekcheck = true;
			if ( dayofweek[3] > 0 ) isweekcheck = true;
			if ( dayofweek[4] > 0 ) isweekcheck = true;
			if ( dayofweek[5] > 0 ) isweekcheck = true;
			if ( dayofweek[6] > 0 ) isweekcheck = true;
			if ( isweekcheck == false) {
				mMsgParm = schedulename +":" + ComUtil.getStrResource(this, R.string.err_mustweekcheck)  ;
				showDialog(ComConstant.DIALOG_INPUTCHECK);
				return false;
			}
		} else if ( info.getStartDate() == null || info.getEndDate() == null
				&& info.getStartTime() == null || info.getEndTime() == null){
			mMsgParm = schedulename +":" + ComUtil.getStrResource(this, R.string.err_date)  ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;
		} else if ( info.getPosition() > 1 ){
			mMsgParm = schedulename +":" + ComUtil.getStrResource(this, R.string.msg_limite_getschedule)  ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;
		}
		
		return true;		
	}
	
	private void setUserColorToSpinner( long userid ){
		//초기화
		UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
		mDbUser.open();
		
		int usercolor = UsermanagerDbAdaper.getUserColor(this, mDbUser, userid);
        Drawable md = mUser.getBackground();
		md.setColorFilter(ViewUtil.drawBackGroundColor ( usercolor));
		
		mDbUser.close();
	}
	 
	private void userSpinnerSet() {
		
		
		ArrayList<String>  list = new ArrayList<String>();
		
		UsermanagerDbAdaper mDbUserHelper = new UsermanagerDbAdaper(this);	
		
		mDbUserHelper.open();
		Cursor cur = mDbUserHelper.fetchAllUsermanger();
		
		userIdList = new ArrayList<Long>();
				
    	int arrayLength = cur.getCount();
    	
		for(int i = 0 ; i < arrayLength ; i++)
		{
			long id = cur.getLong(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_USERID));
			userIdList.add(id);
			list.add(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_NAME)));
			//다음건 처리
			cur.moveToNext();			
		}
        if ( cur != null ) cur.close();
        mDbUserHelper.close(); 
        
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //sp.setPrompt(res.getString(R.string.arr_relation_title));
        
        mUser.setAdapter(aa);

        mUser.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        			//tv.setText(list.get(position));
        			mUserid = ComUtil.setSpinnerFromDb(userIdList, mUser);	
        			setUserColorToSpinner(mUserid);
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        			//tv.setText("1");
        		}
        	}
        );	

	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		
		mAdapter.setCheckFromListClick(position);

	}	
	
	//Comparator 를 만든다. 
    private final static Comparator<ScheduleInfo> myComparator 
    		= new Comparator<ScheduleInfo>() { 
    	
        private final Collator   collator = Collator.getInstance(); 
        
        @Override 
	    public int compare(ScheduleInfo fromobj,ScheduleInfo toobj) { 
        	return collator.compare(fromobj.getEndDate(), toobj.getEndDate()); 
        	
	    } 
	 }; 
	 
	//**** AsyncTask클래스는 항상 Subclassing 해서 사용 해야 함.****
	// <<사용 자료형>>
	// background 작업에 사용할 data의 자료형: String 형
	// background 작업 진행 표시를 위해 사용할 인자: Integer형
	// background 작업의 결과를 표현할 자료형: Long
	// Exception 처리 및 cancel 처리 
	// progressbar 
	class BatchScheduleAsyncTask extends AsyncTask < Void, Integer, Long > {
		
	     private ProgressDialog progressDialog = null;
    			
		// 이곳에 포함된 code는 AsyncTask가 execute 되자 마자 UI 스레드에서 실행됨.
		// 작업 시작을 UI에 표현하거나
		// background 작업을 위한 ProgressBar를 보여 주는 등의 코드를 작성.
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(ctx);
			progressDialog.setMessage(ComUtil.getStrResource(ctx, R.string.msg_pleasewating));
			progressDialog.setCancelable(false);				
	        progressDialog.show();
		}
		@Override
		protected Long doInBackground(Void... unused) {
			
			
			if (allcheckyn == true ) {
				confirmEventAll();
			} else {
				confirmEvent();
			}				
			
			return (long)0;
		}  
		
		// onInBackground(...)에서 publishProgress(...)를 사용하면
		// 자동 호출되는 callback으로
		// 이곳에서 ProgressBar를 증가 시키고, text 정보를 update하는 등의
		// background 작업 진행 상황을 UI에 표현함.
		// (ProgressBar를 update 함) 
		@Override
		protected void onProgressUpdate(Integer... progress) {
//				SystemClock.sleep(300);
			
		}
		
		// onInBackground(...)가 완료되면 자동으로 실행되는 callback
		// 이곳에서 onInBackground가 리턴한 정보를 UI위젯에 표시 하는 등의 작업을 수행함.
		// (예제에서는 작업에 걸린 총 시간을 UI위젯 중 TextView에 표시함)
		@Override
		protected void onPostExecute(Long result) {
			
			progressDialog.dismiss();
			
			//4.message
			//건수만큼 메시지 처리 및 알람재설정
			if ( getCnt > 0 ) {
				String msg = String.format(ComUtil.getStrResource(ctx, R.string.msg_finishcomplete));
	    		ComUtil.showToast(ctx, Integer.toString(getCnt) + ComUtil.getStrResource(ctx, R.string.count) + " " + msg);	
	    		
	    		fillData();
	            
	    		//알람갱신(일정)
	        	AlarmHandler ah = new AlarmHandler(ctx);
	        	ah.setAlarmService();
	        	
	        	//위젯갱신
	        	ViewUtil.updateAllWidget(ctx);
			}				
		}
		
		// .cancel(boolean) 메소드가 true 인자로 
		// 실행되면 호출되는 콜백.
		// background 작업이 취소될때 꼭 해야될 작업은  여기에 구현.
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			//cancel warning message
			ComUtil.showToast(ctx, ComUtil.getStrResource(ctx, R.string.msg_cancel));				
			if ( progressDialog.isShowing() == true  ) progressDialog.dismiss();

		}
	}	
	 
    @Override
	public void onLowMemory() {
		super.onLowMemory();
		ComUtil.showToast(this, "Low Memory!!" );
		onDestroy();
	} 	 
//	/*
//	 * Google Login -> Get Google Calendar (나중에)
//	 */
//	private boolean callGoogleCalendar() {
//		boolean ret = false;
//		//temp
//		String google_email = "qkrwl079@gmail.com";
//		//is Google login
//		
//		if ( google_email != null && !google_email.trim().equals("")) {
//			getFromGoogleCalendar( );
//			ret = true;
//		} else {
//			Intent intent = new Intent(this, GoogleLogin.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			startActivity(intent);	
//			ret = false;
//		}
//
//		
//		return ret;
//	}	
}
