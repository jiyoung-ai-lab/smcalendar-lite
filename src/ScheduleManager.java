package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.ContactHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;
import com.waveapp.smcalendarlite.util.gsCalendarDlg;
 
public class ScheduleManager extends SMActivity implements OnClickListener, RadioGroup.OnCheckedChangeListener {

	private DialogInterface.OnCancelListener mDialogListener;
	private ListenToPhoneState listen; 
	
	int mSYear;    
	int mSMonth;    
	int mSDay; 
	
	int mEYear;    
	int mEMonth;    
	int mEDay;   	
	
	int mSHour;   
	int mSMinute; 
	
	int mEHour;   
	int mEMinute; 
	
	String mMsgParm;
	
	String mSDateParm;
	String mEDateParm;
	String mGubun;
	String mCycleValue;
	
	Long 		mRowId;
	Long 		mUserid;
	
	ViewGroup mRoot ;
	View mEdit;
    EditText 	mName, mTel1, mCostT, mMemo;
    TextView    mPhoneName;
    Spinner  	mAlarm;
    Spinner  	mUser;
    
    Spinner 	mRepeatdate;
    Spinner 	mAlarm2;
    
    CheckBox	mAllDayYn;
    
    RadioGroup	mCycle;
    
    ToggleButton 	mMon, mTue ,mWen, mThr, mFri, mSat, mSun;
    
    ScheduleDbAdaper mDbHelper;
   
    Button		mStartDate, mEndDate, mStartTime, mEndTime;
    
    Button 		confirmButton;
    Button 		cancelButton;
    
    ImageButton contactButton;
    ImageButton callButton;
    ImageButton userAddButton;
    
    ContactHandler conHd ;
    ArrayList<Long> userIdList;

    
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	
        super.onCreate(savedInstanceState);
        
        //1.DB Open / value reset
        mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open();
        
        //2.View ID Set
        setContentView(R.layout.schedulemanager);	

        //2-1.title
        //2-2.body
        mRoot			= (ViewGroup) findViewById(R.id.viewroot); 
        mName 			= (EditText) findViewById(R.id.name); 
        mUser 			= (Spinner) findViewById(R.id.userspinner); 
        mCycle	 		= (RadioGroup) findViewById(R.id.cycle);
        mSun 			= (ToggleButton) findViewById(R.id.sunday);
        mMon 			= (ToggleButton) findViewById(R.id.monday);
        mTue 			= (ToggleButton) findViewById(R.id.tuesday);
        mWen 			= (ToggleButton) findViewById(R.id.wednesday);
        mThr 			= (ToggleButton) findViewById(R.id.thursday);
        mFri 			= (ToggleButton) findViewById(R.id.friday);
        mSat 			= (ToggleButton) findViewById(R.id.saturday);
        mStartDate 		= (Button) findViewById(R.id.startdate); 
        mEndDate 		= (Button) findViewById(R.id.enddate); 
        mAllDayYn	 	= (CheckBox) findViewById(R.id.alldayyn); 
        mStartTime 		= (Button) findViewById(R.id.starttime); 
        mEndTime 		= (Button) findViewById(R.id.endtime); 
        mTel1	 		= (EditText) findViewById(R.id.tel1);
        mPhoneName	 	= (TextView) findViewById(R.id.tv_telname);
        mCostT	 		= (EditText) findViewById(R.id.cost);
        mAlarm 			= (Spinner) findViewById(R.id.alarmSpinner); 
        mMemo	 		= (EditText) findViewById(R.id.memo);
        confirmButton 	= (Button) findViewById(R.id.confirm);  
        cancelButton 	= (Button) findViewById(R.id.cancel);
        contactButton 	= (ImageButton) findViewById(R.id.btn_contact);
        callButton 		= (ImageButton) findViewById(R.id.btn_call);
        userAddButton	= (ImageButton) findViewById(R.id.btn_add);
 
        mRepeatdate		= (Spinner) findViewById(R.id.repeatdateSpinner); 
        mAlarm2 		= (Spinner) findViewById(R.id.alarmSpinner2); 
        
        //3.View Set(Defalut)
        mTel1.setCursorVisible(false);
        mTel1.setInputType(0);
        
        //4.Key Value Set ( Database Or Parameter)
        //-. instance에서, null인 경우는 intent에서
        mRowId = null;
        if (savedInstanceState != null) {
        	mRowId 	= savedInstanceState.getLong(ScheduleDbAdaper.KEY_SCHDULEID);
        	mSDateParm 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_STARTDATE);
        	mEDateParm 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_ENDDATE);
        	mGubun 		= savedInstanceState.getString(ComConstant.SCHEDULE_GUBUN);
        }
        if (mRowId == null ) { 
        	Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	mRowId 		= extras.getLong(ScheduleDbAdaper.KEY_SCHDULEID);
            	mSDateParm 	= extras.getString(ScheduleDbAdaper.KEY_STARTDATE);
            	mEDateParm 	= extras.getString(ScheduleDbAdaper.KEY_ENDDATE);
            	mGubun 		= extras.getString(ComConstant.SCHEDULE_GUBUN);
            }
        }
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.schedule, mGubun ), View.INVISIBLE);        
 
        //5. Data Display
        alarmSpinnerSet();
        alarmSpinnerSet2();
        userSpinnerSet();
        repeatDateSpinnerSet();
        
        populateFields(); 
        
        //6.사용자정보 등록여부 체크
        int usize = userIdList.size();
        if ( usize == 0  ) {
        	mMsgParm = ComUtil.getStrResource(this, R.string.err_nouser) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
        }         	
    	
    	//gubun 값에 따라 화면/기능 분기처리 -- 화면조회뒤에 처리
    	setConfirmButton();
    	
    	// activity button
    	setClickListener();   
    }
    
    private void setConfirmButton () {
    	
    	confirmButton.setText(ComUtil.getStrResource(this, R.string.add));
    	
       if ( mGubun != null && !mGubun.trim().equals("")) {
        	
    	   confirmButton.setText(mGubun);
    	   
        	if ( mGubun.equals(ComUtil.getStrResource(this, R.string.copy))) {
        		mRowId = null;
        		String copytext = getResources().getString(R.string.copy_prefix).toString();        		
        		mName.setText(mName.getText().toString() + copytext);
        	} 
        }  
    }
    
    private void setClickListener () {
    	
		confirmButton.setOnClickListener(this); 
		cancelButton.setOnClickListener(this);
		contactButton.setOnClickListener(this);
		callButton.setOnClickListener(this);
		userAddButton.setOnClickListener(this);
		
		//sub activity
		mStartDate.setOnClickListener(this); 
		mEndDate.setOnClickListener(this); 
		mStartTime.setOnClickListener(this); 
		mEndTime.setOnClickListener(this);
		
		//checked changed 
		mAllDayYn.setOnClickListener(this);
		
		//radio
		mCycle.setOnCheckedChangeListener(this);
    } 
    
	public void onClick(View v) {

		if( v == mStartDate ) {
			openCalendarDialog( mStartDate );
			
		} else if( v == mEndDate ) {
			openCalendarDialog( mEndDate );
			
		} else if( v == mStartTime ) {
			
			mEdit = mStartTime;
			
			showDialog(ComConstant.TIME_DIALOG_ID_S);
			
		} else if( v == mEndTime ) {
			
			mEdit = mEndTime;
			showDialog(ComConstant.TIME_DIALOG_ID_E);	
			
		} else  if ( v == confirmButton ) 
		{
			if ( mGubun != null && mGubun.equals(ComUtil.getStrResource(this, R.string.delete))) {
				//1.dialog open (확인 메시지)				
				showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);
			} else {
				confirmEvent( );
			}
			
		} else  if ( v == cancelButton ) {
			
			setResult(RESULT_CANCELED); 
			finish();
			
		} else if ( v == contactButton ){
			
	        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
	        mIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);	
	        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        startActivityForResult(mIntent, ComConstant.ACTIVITY_PHONE_SELECT);
	        
		} else if ( v == callButton ){
			
			String pnumber = mTel1.getText().toString();
			if ( pnumber != null && !pnumber.trim().equals("")) {
				Intent mIntent = new Intent(Intent.ACTION_CALL);
				mIntent.setData(Uri.parse("tel:" + mTel1.getText()));
		        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        startActivityForResult(mIntent, ComConstant.ACTIVITY_PHONE_CALL);
			}	
			
		} else if ( v == userAddButton ){
			
	        Intent intent = new Intent(this, UserList.class);
	        startActivityForResult(intent, ComConstant.ACTIVITY_EDIT);

		} else if ( v == mAllDayYn ) {
			
			allDayChangeEvent();
			
		}
	}  
	/*
	 * click event 처리 fuction
	 */
	private void openCalendarDialog ( final View v ) {
		
		final gsCalendarDlg dlg = new gsCalendarDlg( this );
		final Context ctx = this;
		
		if( v == mStartDate ) {
			dlg.setDate (mSYear, mSMonth, mSDay);
		} else if( v == mEndDate ) {
			dlg.setDate (mEYear, mEMonth, mEDay);
		}

		dlg.setOnDismissListener(
	        	new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {					
						
						if( v == mStartDate ) {
							mSYear 	= dlg.getYear();
							mSMonth = dlg.getMonth();
							mSDay 	= dlg.getDay();
							((Button) v).setText(SmDateUtil.getDateFullFormat( ctx ,
									SmDateUtil.getDateFormat(mSYear, mSMonth, mSDay), true, false));
							
						} else if( v == mEndDate ) {
							mEYear 	= dlg.getYear();
							mEMonth = dlg.getMonth();
							mEDay 	= dlg.getDay();		
							((Button) v).setText(SmDateUtil.getDateFullFormat( ctx,
									SmDateUtil.getDateFormat(mEYear, mEMonth, mEDay), true, false));
						}
						
						//시작일자 입력시 종료일자보다 크면 종료일자 자동변경
						if ( v == mStartDate ) {
							String sdate = SmDateUtil.getDateFormat(mSYear, mSMonth, mSDay);
							String edate = SmDateUtil.getDateFormat(mEYear, mEMonth, mEDay);
							if ( Double.parseDouble(sdate)  >  Double.parseDouble(edate)) {
								mEYear 	= mSYear;
								mEMonth = mSMonth;
								mEDay 	= mSDay;	
								mEndDate.setText(SmDateUtil.getDateFullFormat( ctx,
										SmDateUtil.getDateFormat(mEYear, mEMonth, mEDay), true, false));
							}
						}
					}
	        	});
		dlg.show( ) ;	
		
	}
	/*
	 * radio button 선택값에 따라 화면 처리 및 return 값 처리
	 */	
	private void setCycleChange () {
		LinearLayout l_week = (LinearLayout) findViewById(R.id.lin_dayofweek); 
		LinearLayout l_month = (LinearLayout) findViewById(R.id.lin_repeatdate); 
		
		int selradio = mCycle.getCheckedRadioButtonId();
		//매주 선택시 반복값 setting
		if ( selradio == R.id.everyweek ) {
			l_week.setVisibility(View.VISIBLE);
			l_month.setVisibility(View.INVISIBLE);
			mCycleValue = "Y";
		} else if ( selradio == R.id.everymonth ) {
			l_week.setVisibility(View.INVISIBLE);
			l_month.setVisibility(View.VISIBLE);
			mCycleValue = "M";
		} else {
			l_week.setVisibility(View.INVISIBLE);
			l_month.setVisibility(View.INVISIBLE);
			mCycleValue = "";
		}
	}
	/*
	 * 스케줄 반복주기 값에 따라 radio button setting
	 */
	private void checkCycleGroup ( String cyclevalue ) {

		//매주 선택시 반복값 setting
		if ( cyclevalue != null && cyclevalue.trim().equals("Y") ) {
			mCycle.check(R.id.everyweek);
		} else if ( cyclevalue != null && cyclevalue.trim().equals("M") ) {
			mCycle.check(R.id.everymonth);
		} else {
			mCycle.check(R.id.onetime);
		}
		
		setCycleChange();
	}		
	private void allDayChangeEvent () {

		if ( mAllDayYn.isChecked()) {
			setSchduleTime("0000", 0);
			setSchduleTime("2400", 1);
			
		} else {
			setSchduleTime(SmDateUtil.getNowHourMinuteOclock(0), 0);
			setSchduleTime(SmDateUtil.getNowHourMinuteOclock(1), 1);
		}
		mStartTime.setText(SmDateUtil.getTimeFullFormat( this, SmDateUtil.getTimeFormat(mSHour, mSMinute)));
		mEndTime.setText(SmDateUtil.getTimeFullFormat( this, SmDateUtil.getTimeFormat(mEHour, mEMinute)));
		
		//알람 종일선택시 사용불가...(화면제어)
		if ( mAllDayYn.isChecked()) {
			mAlarm.setEnabled(false);
			mAlarm2.setEnabled(false);
		} else {
			mAlarm.setEnabled(true);
			mAlarm2.setEnabled(true);
		}
	}	
	
	/*
	 * confirm button event (등록편집복사, 삭제)
	 */
	private void confirmEvent() { 
		
		//1.validation check
		if ( validationCheck() == false ) return;
				
		//2.keyValue set 
		Bundle bundle = new Bundle();
        if (mRowId != null && mRowId != (long)0) {
            bundle.putLong(ScheduleDbAdaper.KEY_SCHDULEID, mRowId);
        }
        
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);	        
        setResult(RESULT_OK, mIntent);
        
        //3.DataBase Insert/update
    	saveState();        
        finish();
        
	}
	private void deleteEvent() { 

		//2.keyValue set 
		Bundle bundle = new Bundle();
        if (mRowId != null && mRowId != (long)0) {
            bundle.putLong(ScheduleDbAdaper.KEY_SCHDULEID, mRowId);
        }
        
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);	        
        setResult(RESULT_OK, mIntent);
        
        //3.DataBase Insert/update
        deleteState();   
        
        finish();
	}
	/*
	 * Field Set
	 * 1.modify : scheduleid -> select
	 * 2.new    : date set( today )
	 */	
	private void populateFields() { 
			
		//기존스케줄 정보 가져오기
		if (mRowId != null && mRowId != (long)0) {        
			Cursor cur = mDbHelper.fetchSchedule(mRowId);        
//			startManagingCursor(cur);
			
			if ( cur != null  && cur.getCount() > 0 ) {
	            mName.setText(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_NAME))));
	            mUserid = cur.getLong((cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_USERID)));
	            mUser.setSelection(ComUtil.getSpinnerFromDb(userIdList, mUserid));
	            setUserColorToSpinner(mUserid);
	    		
	            //mCycle.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_CYCLE)))));
	            mCycleValue = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_CYCLE)));
	            checkCycleGroup(mCycleValue);
	            
	            mSun.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SUN)))));
	            mMon.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_MON)))));
	            mTue.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_TUE)))));
	            mWen.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_WEN)))));
	            mThr.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_THU)))));
	            mFri.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_FRI)))));
	            mSat.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SAT)))));

	            String sdate = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTDATE)));
	            setSchduleDate(sdate, 0);
	            mStartDate.setText(SmDateUtil.getDateFullFormat(this, sdate, true, false));
	            String edate = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDDATE)));
	            setSchduleDate(edate, 1);
	            mEndDate.setText(SmDateUtil.getDateFullFormat(this, edate, true, false));
	            
//	            mStartDate.setText(DateUtil.getSepDate());
//	            mEndDate.setText(DateUtil.getSepDate(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDDATE)))));
	            mAllDayYn.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALLDAYYN)))));
	            allDayChangeEvent();
	            
	            String stime = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTTIME)));
	            setSchduleTime(stime, 0);
	            mStartTime.setText(SmDateUtil.getTimeFullFormat(this, stime));

	            String etime = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDTIME)));
	            setSchduleTime(etime, 1);
	            mEndTime.setText(SmDateUtil.getTimeFullFormat(this, etime));  
	            
	           // mStartTime.setText(DateUtil.getSepTime(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTTIME)))));
	            //mEndTime.setText(DateUtil.getSepTime(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDTIME)))));
	            
	            String sAlarm	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM)));
	            mAlarm.setSelection(ComUtil.getSpinner(this, R.array.arr_alarm_key, sAlarm));
	            String sAlarm2	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM2)));
	            mAlarm2.setSelection(ComUtil.getSpinner(this, R.array.arr_alarm_key, sAlarm2));

	            String phonenumber = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_TEL1)));
	            mTel1.setText(phonenumber);  
	            ContactHandler conHd = new ContactHandler(this);
	            String pn = conHd.getNameFromPhoneNumber(phonenumber);
	            if ( pn  != null &&  !pn.trim().equals(""))
	            	mPhoneName.setText("(" + conHd.getNameFromPhoneNumber(phonenumber)+ ")");
	            mCostT.setText(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_COST))));
	            mMemo.setText(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_MEMO))));
	            String sRepeatdate	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_REPEATDATE)));
	            mRepeatdate.setSelection(ComUtil.getSpinner(this, R.array.arr_repeatdate_key, sRepeatdate));
	            				
			}

            if ( cur != null ) cur.close();
            
            
		}else {
			//new data
			if ( mSDateParm != null && !mSDateParm.equals("")) {
				setSchduleDate(mSDateParm, 0);
				mStartDate.setText(SmDateUtil.getDateFullFormat(this, mSDateParm, true, false));
			} else {
				setSchduleDate(SmDateUtil.getTodayDefault(), 0);
				mStartDate.setText(SmDateUtil.getDateFullFormat(this, SmDateUtil.getTodayDefault(), true, false));
			}
			
			if ( mEDateParm != null && !mEDateParm.equals("")) {
				setSchduleDate(mEDateParm, 1);
				mEndDate.setText(SmDateUtil.getDateFullFormat(this, mEDateParm, true, false));
			} else {
				setSchduleDate(SmDateUtil.getTodayDefault(), 1);
				mEndDate.setText(SmDateUtil.getDateFullFormat(this, SmDateUtil.getTodayDefault(), true, false));	
			}
			//str = getTimeFullFormat(strbuf.toString());
			String t1 = SmDateUtil.getNowHourMinuteOclock(0);
			setSchduleTime(t1, 0);
			mStartTime.setText(SmDateUtil.getTimeFullFormat(this, t1));
			String t2 = SmDateUtil.getNowHourMinuteOclock(1);
			setSchduleTime(t2, 1);
			mEndTime.setText(SmDateUtil.getTimeFullFormat(this, t2));
			
			mCycleValue = "";
			checkCycleGroup(mCycleValue);
			
			mAllDayYn.setChecked(false);
			
			//사용자색상지정(default)
			mUserid = ComUtil.setSpinnerFromDb(userIdList, mUser);	
			setUserColorToSpinner(mUserid);		
    		
		}
	}
	
	private void setSchduleDate ( String date , int position ) {
		
		if ( position == 0 ) {
			mSYear 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_YEAR);
			mSMonth = SmDateUtil.getDateToInt(date, ComConstant.GUBUN_MONTH);
			mSDay 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY);			
		} else if ( position == 1 ){
			mEYear 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_YEAR);
			mEMonth = SmDateUtil.getDateToInt(date, ComConstant.GUBUN_MONTH);
			mEDay 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY);	
		}
		
	}	
	private void setSchduleTime ( String time , int position ) {
		
		if ( position == 0 ) {
			mSHour 	= SmDateUtil.getTimeToInt(time, ComConstant.GUBUN_HOUR);
			mSMinute = SmDateUtil.getTimeToInt(time, ComConstant.GUBUN_MINUTE);		
		} else if ( position == 1 ){
			mEHour 	= SmDateUtil.getTimeToInt(time, ComConstant.GUBUN_HOUR);
			mEMinute = SmDateUtil.getTimeToInt(time, ComConstant.GUBUN_MINUTE);
		}
		
	}
	private void setRepeatDateDefault ( String date ) {
		int day = SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY);
		if ( day  > 0 ) {
			mRepeatdate.setSelection(ComUtil.getSpinner(this, R.array.arr_repeatdate_key, ComUtil.intToString(day)));
		}
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
	
	private Boolean validationCheck() {

		mMsgParm = "";
		
		String startdate = SmDateUtil.getDateFormat(mSYear, mSMonth, mSDay);
		String enddate 	 = SmDateUtil.getDateFormat(mEYear, mEMonth, mEDay);
//		String starttime = DateUtil.getTimeFormat(mSHour, mSMinute);
//		String endtime 	 = DateUtil.getTimeFormat(mEHour, mEMinute);
		
		//스케줄명 필수처리
		if (ComUtil.getNullCheck(mName.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_schedulename) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mName.setFocusable(true);
			return false;
			//스케줄명 필수처리
		} else if (mUserid  == (long)0 ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_user) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;			
		} else if ( mCycleValue != null && mCycleValue.trim().equals("Y")  ){
			boolean isweekcheck = false;
			if ( mSun.isChecked() == true ) isweekcheck = true;
			if ( mMon.isChecked() == true ) isweekcheck = true;
			if ( mTue.isChecked() == true ) isweekcheck = true;
			if ( mWen.isChecked() == true ) isweekcheck = true;
			if ( mThr.isChecked() == true ) isweekcheck = true;
			if ( mFri.isChecked() == true ) isweekcheck = true;
			if ( mSat.isChecked() == true ) isweekcheck = true;
			if ( isweekcheck == false) {
				mMsgParm = ComUtil.getStrResource(this, R.string.err_mustweekcheck)  ;
				showDialog(ComConstant.DIALOG_INPUTCHECK);
				return false;
			}

		} else if ( Double.parseDouble(startdate)  > Double.parseDouble(enddate) ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_enddate)  ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;
		} else if ( mCycleValue != null && !mCycleValue.trim().equals("") ){
			if ( Double.parseDouble(startdate)  == Double.parseDouble(enddate) ){
				mMsgParm = ComUtil.getStrResource(this, R.string.err_period)  ;
				showDialog(ComConstant.DIALOG_INPUTCHECK);	
				return false;		
			}
		} 		
		
		return true;		
	}
	@Override	
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState);  
    	outState.putSerializable(ScheduleDbAdaper.KEY_SCHDULEID, mRowId); 
    	outState.putSerializable(ScheduleDbAdaper.KEY_STARTDATE, mSDateParm);
    	outState.putSerializable(ScheduleDbAdaper.KEY_ENDDATE, mEDateParm);
    	outState.putSerializable(ComConstant.SCHEDULE_GUBUN, mGubun);   	
    	
    }	
    @Override    
    protected void onResume() {        
    	super.onResume(); 
    }        
    @Override    
    protected void onPause() {        
    	super.onPause();
    	//if ( mDbHelper != null ) mDbHelper.close();
    }
    @Override 
    protected void onDestroy() { 	
        super.onDestroy(); 
        
        RecycleUtil.recursiveRecycle(mRoot);
        if ( mDbHelper != null ) mDbHelper.close();
    }
    
    /*
     * Call Database
     */
    private void saveState() { 
    	
    	boolean ret = false;
    	
    	Long lUserid 		= mUserid.longValue(); 
    	String sName 		= mName.getText().toString(); 
    	String sCycle 		= mCycleValue;
    	String sSun 		= ComUtil.setCheckYN(mSun.isChecked());
    	String sMon 		= ComUtil.setCheckYN(mMon.isChecked());
    	String sTue 		= ComUtil.setCheckYN(mTue.isChecked());
    	String sWen 		= ComUtil.setCheckYN(mWen.isChecked());
    	String sThr 		= ComUtil.setCheckYN(mThr.isChecked());
    	String sFri 		= ComUtil.setCheckYN(mFri.isChecked());
    	String sSat 		= ComUtil.setCheckYN(mSat.isChecked());  
    	//주기가 없는 경우 clear
    	if ( sCycle == null || ( sCycle != null && (sCycle.trim().equals("")||sCycle.trim().equals("M")))) {
    		sSun = "";
    		sMon = "";
    		sTue = "";
    		sWen = "";
    		sThr = "";
    		sFri = "";
    		sSat = "";
    	}

    	String sStartDate 	= SmDateUtil.getDateFormat(mSYear, mSMonth, mSDay);
    	String sEndDate 	= SmDateUtil.getDateFormat(mEYear, mEMonth, mEDay);
    	String sAllDay 		= ComUtil.setCheckYN(mAllDayYn.isChecked());
    	String sStartTime 	= SmDateUtil.getTimeFormat(mSHour, mSMinute);
    	String sEndTime 	= SmDateUtil.getTimeFormat(mEHour, mEMinute);
    	String sTel1 		= mTel1.getText().toString(); 
    	String sAlarm 		= ComUtil.setSpinner(this, R.array.arr_alarm_key, mAlarm);
    	String sAlarm2 		= ComUtil.setSpinner(this, R.array.arr_alarm_key, mAlarm2);
        Long lCost 			= ComUtil.stringToLong(mCostT.getText().toString()) ;
        String sMemo 		= mMemo.getText().toString(); 
        String sRepeatdate	= ComUtil.setSpinner(this, R.array.arr_repeatdate_key, mRepeatdate);
        
        //신규
    	if (mRowId == null || ( mRowId != null &&  mRowId == (long)0)) {           
    		long id = mDbHelper.insertSchedule(
    				sName, lUserid, sCycle,
    				sSun, sMon, sTue, sWen, sThr, sFri, sSat,  
    				sStartDate, sEndDate, sAllDay, sStartTime, sEndTime,
    				sAlarm, sTel1, "", lCost, sMemo,
    				sRepeatdate, sAlarm2 );          
    		if (id > 0) mRowId = id;  
    	//수정
    	} else{
    		ret = mDbHelper.updateSchedule(mRowId, 
    				sName, lUserid, sCycle, 
    				sSun, sMon, sTue, sWen, sThr, sFri, sSat, 
    				sStartDate, sEndDate, sAllDay, sStartTime,sEndTime,
    				sAlarm, sTel1, "", lCost, sMemo,
    				sRepeatdate, sAlarm2 );
    	}
    	
    	if ( ret == true || ( mRowId != null &&  mRowId > (long)0)) {
    		
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_savecomplete));
    		ComUtil.showToast(this, msg);
    	}
    	//알람갱신(일정)  	
    	AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmService();
    	
    	//위젯갱신
    	ViewUtil.updateAllWidget(this);
    	
    }	
    private void deleteState() {   
        
    	boolean ret = mDbHelper.deleteSchedule(mRowId); 
    	
    	if ( ret == true ) {
    		
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_deletecomplete));
    		ComUtil.showToast(this, msg);
    	}
    	
    	//알람갱신(일정)
    	AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmService();
    	
    	//위젯갱신
    	ViewUtil.updateAllWidget(this);
        
    }		

    /*
	 * Dialog Call Event
	 * 1.parameter set  
	 * 2.EditText Set
	 */
	private void dialogPickerListener( ) { 

		mDialogListener =  new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) { 
				deleteEvent();
			}
		};
	}	
	/*
	 * Message Dialog Set
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		removeDialog(id);
		dialog = null;
		
	}	
    @Override
    protected Dialog onCreateDialog(int id) { 
    	
    	final MessageHandler msgHd = new MessageHandler(this);

    	final Context ctx = this;
    	Dialog di ;
    	
    	switch (id) { 
    	case ComConstant.TIME_DIALOG_ID_S: 
			msgHd.setTimeString(mSHour, mSMinute);
			di = msgHd.onCreateDialog(new TimePickerDialog.OnTimeSetListener() {
				
		    	public void onTimeSet(TimePicker view, int hour,
			    		int minute) { 
		    			if ( msgHd.getReturn() == true ) {
					    	mSHour 	 = hour;                    
					    	mSMinute = minute; 
					    	String time = SmDateUtil.getTimeFormat(mSHour, mSMinute);
					    	mStartTime.setText(SmDateUtil.getTimeFullFormat(ctx,time));
					    	
					    	//종료시간도 setting
					    	if ( hour < 24 ) {
					    		mEHour 	 = hour + 1;  
					    	} else {
					    		mEHour 	 = hour;  
					    	}                    
					    	mEMinute = minute;
					    	String etime = SmDateUtil.getTimeFormat(mEHour, mEMinute);
					    	mEndTime.setText(SmDateUtil.getTimeFullFormat(ctx,etime));
			    			
		    			}
			    	
			    	}    
			    });
			return null;
//			return di;

    	case ComConstant.TIME_DIALOG_ID_E: 
    			msgHd.setTimeString(mEHour, mEMinute);
    			di = msgHd.onCreateDialog(new TimePickerDialog.OnTimeSetListener() { 
    				
    		    	public void onTimeSet(TimePicker view, int hour,
    			    		int minute) { 
	    		    		if ( msgHd.getReturn() == true ) {
	    				    	mEHour 	 = hour;                    
	    				    	mEMinute = minute; 
	    				    	String time = SmDateUtil.getTimeFormat(mEHour, mEMinute);
	    				    	mEndTime.setText(SmDateUtil.getTimeFullFormat(ctx, time));
	    		    		} 
    		    		}
    		    		
    			    });
    			return null;
    		   		
    	case ComConstant.DIALOG_INPUTCHECK:
    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, 
    					ComUtil.getStrResource(this, R.string.inputcheck), R.string.msg_inputcheck, mMsgParm);
    		return di;
		case ComConstant.DIALOG_YES_NO_MESSAGE:
			dialogPickerListener();			
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_deleteconfirm, mMsgParm);
			di.setOnCancelListener(mDialogListener);
					
			return di;
    	}    	
    return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if ( requestCode == ComConstant.ACTIVITY_PHONE_SELECT ) {
        	ContactHandler conHd = new ContactHandler(this);
        	conHd.onPhoneResult(data);
        	mTel1.setText(conHd.getPhoneNumber()); 
        	if ( conHd.getName()   != null &&  !conHd.getName().trim().equals(""))
        		mPhoneName.setText("(" + conHd.getName() + ")" );  
    	} else if ( requestCode == ComConstant.ACTIVITY_PHONE_CALL ) {
    		TelephonyManager tMng = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
    		listen = new ListenToPhoneState();
    		tMng.listen(listen, PhoneStateListener.LISTEN_CALL_STATE);
    	} else if ( requestCode == ComConstant.ACTIVITY_SELECT_USER ) {
//        	Bundle extras = data.getExtras();
//            if (extras != null) {
//            	mUserid 	= extras.getLong(UsermanagerDbAdaper.KEY_USERID);
//            }
    	} else if ( requestCode == ComConstant.ACTIVITY_EDIT ) {
	         userSpinnerSet();
    	}

    }
    
    private class ListenToPhoneState extends PhoneStateListener {
    	public void onCallStateChanged( int state, String incommingNumber) {
    		//////////
    	}
    	private String stateName ( int state ) {
    		switch (state) {
    			case TelephonyManager.CALL_STATE_IDLE 	: return "IDLE";
    			case TelephonyManager.CALL_STATE_OFFHOOK: return "OFFHOOK";
    			case TelephonyManager.CALL_STATE_RINGING: return "RINGING";
    		}
    		return Integer.toString(state);
    	}
    }

    /*
     * spinner set
     */
	private void alarmSpinnerSet() {	

        Resources  res = getResources();
        String[] sArr 		= res.getStringArray(R.array.arr_alarm);
        String[] sKeyArr 	= res.getStringArray(R.array.arr_alarm_key);
        ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listKey = new ArrayList<String>();

        for ( int i = 0  ; i < sArr.length ; i++){
        	 list.add(sArr[i].toString());
        	 listKey.add(sKeyArr[i].toString());
        }
         
        ArrayAdapter<String> arrAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAlarm.setAdapter(arrAlarm);
        
        mAlarm.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        		}
        	}
        );
	}  
	private void alarmSpinnerSet2() {	

        Resources  res = getResources();
        String[] sArr 		= res.getStringArray(R.array.arr_alarm);
        String[] sKeyArr 	= res.getStringArray(R.array.arr_alarm_key);
        ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listKey = new ArrayList<String>();

        for ( int i = 0  ; i < sArr.length ; i++){
        	 list.add(sArr[i].toString());
        	 listKey.add(sKeyArr[i].toString());
        }
         
        ArrayAdapter<String> arrAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAlarm2.setAdapter(arrAlarm);
        
        mAlarm2.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        		}
        	}
        );
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
	private void repeatDateSpinnerSet() {

        Resources  res = getResources();
        String[] sArr 		= res.getStringArray(R.array.arr_repeatdate);
        String[] sKeyArr 	= res.getStringArray(R.array.arr_repeatdate_key);
        ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listKey = new ArrayList<String>();

        for ( int i = 0  ; i < sArr.length ; i++){
        	 list.add(sArr[i].toString());
        	 listKey.add(sKeyArr[i].toString());
        }
        
        ArrayAdapter<String> arrAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mRepeatdate.setAdapter(arrAlarm);
        
        mRepeatdate.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        		}
        	}
        );
        
        setRepeatDateDefault(mSDateParm);
        
	} 
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		setCycleChange();
        
	}

}