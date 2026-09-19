package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class SpecialdayManager extends SMActivity implements OnClickListener,  RadioGroup.OnCheckedChangeListener {
	
	//datePicker
	private DatePickerDialog.OnDateSetListener mDateListener;
	private DialogInterface.OnCancelListener mDialogListener;
	
	int mSpecialYear;    
	int mSpecialMonth;    
	int mSpecialDay; 
	
	String mLocale;
	String mHolidayYn;
	String mGubun;
	String mYearStr;	
	String mMonthDay;
	String mMsgParm;
	String mLeapValue;
	
	Long 		mRowId;
	ViewGroup	mRoot;
    EditText 	mName, mMemo;
    //String      mPrefix;
    CheckBox	mRepeat;
//    Spinner 	mLeap;
    RadioGroup 	mLeapR;
    Spinner 	mEvent;
    Spinner 	mUserGroup;
    Spinner  	mAlarm;
    
    SpecialDayDbAdaper mDbHelper;
    
    Button 		mEdit;
    Button		mDate;
    Button 		confirmButton;
    Button 		cancelButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        
        //1.DB Open / value reset
        mDbHelper = new SpecialDayDbAdaper(this);
        mDbHelper.open();
        
        //2.View ID Set
        setContentView(R.layout.specialdaymanager);	
        
        mRoot			= (ViewGroup) findViewById(R.id.viewroot);
        mName 			= (EditText) findViewById(R.id.name);
        mEvent			= (Spinner) findViewById(R.id.eventSpinner); 
        mRepeat	 		= (CheckBox) findViewById(R.id.repeatyn); 
        mDate 			= (Button) findViewById(R.id.date);  
//        mLeap 			= (Spinner) findViewById(R.id.leapSpinner); 
        mLeapR	 		= (RadioGroup) findViewById(R.id.leap);
        mMemo	 		= (EditText) findViewById(R.id.memo);
        mUserGroup		= (Spinner) findViewById(R.id.usergroupSpinner); 
        mAlarm 			= (Spinner) findViewById(R.id.alarmSpinner); 
        
        confirmButton 	= (Button) findViewById(R.id.confirm);  
        cancelButton 	= (Button) findViewById(R.id.cancel);
        
        //3.View Set(Defalut)
       
        
        //4.Key Value Set ( Database Or Parameter)
        //-. instance에서, null인 경우는 intent에서
        mRowId = null;
        if (savedInstanceState != null) {
        	mRowId 	= savedInstanceState.getLong(SpecialDayDbAdaper.KEY_ID);
        	mGubun 	= savedInstanceState.getString(ComConstant.SPECIAL_GUBUN);
        	mYearStr 	= savedInstanceState.getString(SpecialDayDbAdaper.KEY_YEAR);
        	mMonthDay 	= savedInstanceState.getString(SpecialDayDbAdaper.KEY_MONTHDAY);
        }
        if (mRowId == null ) { 
        	Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	mRowId 		= extras.getLong(SpecialDayDbAdaper.KEY_ID);
            	mGubun 	= extras.getString(ComConstant.SPECIAL_GUBUN);
            	mYearStr 	= extras.getString(SpecialDayDbAdaper.KEY_YEAR);
            	mMonthDay 	= extras.getString(SpecialDayDbAdaper.KEY_MONTHDAY);
            }
        }
        
        //공통 tosp menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.specialday, mGubun ), View.INVISIBLE); 
    	       
        //5. Data Display
//        lunarSpinnerSet();
        eventSpinnerSet();
        userGroupSpinnerSet();
        alarmSpinnerSet();
    	populateFields();
    	
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
    	
    	mDate.setOnClickListener(this); 
		confirmButton.setOnClickListener(this); 
		cancelButton.setOnClickListener(this);
		//radio
		mLeapR.setOnCheckedChangeListener(this);
		
    }      
	public void onClick(View v) {
		
		if( v == mDate ) {
			mEdit = mDate;
			showDialog(ComConstant.DATE_DIALOG_ID);
		} else  if ( v == confirmButton ) {
			
			if ( mGubun != null && mGubun.equals(ComUtil.getStrResource(this, R.string.delete))) {				
				showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);				
			} else {				
				confirmEvent(v);				
			}
			
		} else  if ( v == cancelButton ) {
			setResult(RESULT_CANCELED); 
			finish();
		}
	}  
	private void confirmEvent(View v) { 
		
		//1.validation check
		if ( validationCheck() == false ) return;
				
		//2.keyValue set 
		Bundle bundle = new Bundle();
        if (mRowId != null && mRowId != (long)0) {
            bundle.putLong(SpecialDayDbAdaper.KEY_ID, mRowId);
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
            bundle.putLong(SpecialDayDbAdaper.KEY_ID, mRowId);
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
	 * 1.modify : id -> select
	 * 2.new    : date set( today )
	 */	
	private void populateFields() { 

		//기존스케줄 정보 가져오기
		if (mRowId != null && mRowId != (long)0) { 
			
			Cursor cur = mDbHelper.fetchSpecialDay(mRowId); 
			
//			startManagingCursor(cur);
			if ( cur != null  && cur.getCount() > 0 ) {
				
				mRowId 		= cur.getLong((cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
	            mName.setText(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME))));
	            mYearStr 	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_YEAR)));
	            mMonthDay 	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MONTHDAY)));

	            String date = mYearStr + mMonthDay;
	            setSpecialdayDate(date);
	            mDate.setText(SmDateUtil.getDateFullFormat(this,date, true, false));
	 
	            //mDate.setText(DateUtil.getSepDate(mYearStr + mMonthDay));
	            mRepeat.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_REPEATYN)))));
//	            String sLeap		= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LEAP)));
//	            mLeap.setSelection(ComUtil.getSpinner(this, R.array.arr_leap_key, sLeap));
	            mLeapValue = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LEAP)));
	            checkLeapGroup(mLeapValue);
	                        
	            
	            String sEvent		= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_EVENT)));
	            mEvent.setSelection(ComUtil.getSpinner(this, R.array.arr_event_key, sEvent));
	            String sUserGroup	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_USERGROUP)));
	            mUserGroup.setSelection(ComUtil.getSpinner(this, R.array.arr_usergroup_key, sUserGroup));
	            String sAlarm	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ALARM)));
	            mAlarm.setSelection(ComUtil.getSpinner(this, R.array.arr_alarmforother_key, sAlarm));          
	           
	            mMemo.setText(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MEMO))));
	 				
			}
           
			 if ( cur != null ) cur.close();
			 
		}else {
			//new data
			if ( mYearStr != null && mMonthDay != null 
					&& !mYearStr.equals("") && !mMonthDay.equals("")) {
				setSpecialdayDate(mYearStr + mMonthDay);
				mDate.setText(SmDateUtil.getDateFullFormat(this, mYearStr + mMonthDay, true, false));
			} else {
				setSpecialdayDate(SmDateUtil.getTodayDefault());
				mDate.setText(SmDateUtil.getDateFullFormat(this, SmDateUtil.getTodayDefault(), true, false));
			}
			mRepeat.setChecked(true);
//			mLeap.setSelection(0);
			mEvent.setSelection(0);
			mUserGroup.setSelection(0);	
			mAlarm.setSelection(0);
			
			mLeapValue = "0";
			checkLeapGroup(mLeapValue);			
		}
	}
	/*
	 * 값에 따라 radio button setting
	 */
	private void checkLeapGroup ( String value ) {

		//매주 선택시 반복값 setting
		if ( value != null && value.trim().equals("0") ) {
			mLeapR.check(R.id.solar);
		} else if ( value != null && value.trim().equals("1") ) {
			mLeapR.check(R.id.lunar);
		} else {
			mLeapR.check(R.id.lunar2);
		}
		
		setLeapChange();
	}	
	/*
	 * radio button 선택값에 따라 화면 처리 및 return 값 처리
	 */	
	private void setLeapChange () {
		
		int selradio = mLeapR.getCheckedRadioButtonId();
		//매주 선택시 반복값 setting
		if ( selradio == R.id.lunar ) {
			mLeapValue = "1";
		} else if ( selradio == R.id.lunar2 ) {
			mLeapValue = "2";
		} else {
			mLeapValue = "0";
		}
	}	
	private void setSpecialdayDate ( String date ) {
		mSpecialYear 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_YEAR);
		mSpecialMonth = SmDateUtil.getDateToInt(date, ComConstant.GUBUN_MONTH);
		mSpecialDay 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY);
		
	}
	
	private Boolean validationCheck() { 
		
		mMsgParm = "";
				
		//스케줄명 필수처리
		if (ComUtil.getNullCheck(mName.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_specialdayname) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mName.setFocusable(true);
			return false;		
		}
		
		return true;		
	}
	@Override	
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState);  
    	outState.putSerializable(SpecialDayDbAdaper.KEY_ID, mRowId); 
    	outState.putSerializable(ComConstant.SPECIAL_GUBUN, mGubun);
    	outState.putSerializable(SpecialDayDbAdaper.KEY_YEAR, mYearStr);
    	outState.putSerializable(SpecialDayDbAdaper.KEY_MONTHDAY, mMonthDay);
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
        RecycleUtil.recursiveRecycle(mRoot);
    	if ( mDbHelper != null ) mDbHelper.close();
    }
    
    /*
     * Call Database
     */
    private void saveState() {   
        
    	boolean ret = false;
    	
    	String locale  		= ComConstant.LOCALE;
    	String gubun  		= ComConstant.PUT_USER;
    	String holidayyn  	= "";
    	
    	String name 		= mName.getText().toString(); 
    	String repeatyn 	= ComUtil.setCheckYN(mRepeat.isChecked());
    	String year 		= ComUtil.fillSpaceToZero(mSpecialYear, 4);
    	String monthday 	= ComUtil.fillSpaceToZero(mSpecialMonth, 2)
    						+ ComUtil.fillSpaceToZero(mSpecialDay, 2);
//    	String sLeap 		= ComUtil.setSpinner(this, R.array.arr_leap_key, mLeap);
    	String sLeap 		= mLeapValue;
    	String sEvent 		= ComUtil.setSpinner(this, R.array.arr_event_key, mEvent);
    	String sUserGroup	= ComUtil.setSpinner(this, R.array.arr_usergroup_key, mUserGroup);
    	String sAlarm 		= ComUtil.setSpinner(this, R.array.arr_alarmforother_key, mAlarm);
    	
    	//윤달의 경우 solardate 가 없을수도 있음... 음... 음... 아짱나
    	//반복되는 음력의 경우 solardate가 복수... 임... 아 더짱나
        
        LunarDataDbAdaper mLunarDb = new LunarDataDbAdaper(this);
	    mLunarDb.open();
	    
    	if ( sLeap != null && ! sLeap.trim().equals("1")) {
    		SmDateUtil.getSolarFromDb( this, mLunarDb, sLeap, year + monthday);
    	} 
    	
    	mLunarDb.close();
    	
    	String memo 		= mMemo.getText().toString(); 
        
        //신규
    	if (mRowId == null || ( mRowId != null &&  mRowId == (long)0)) {           
    		long id = mDbHelper.insertSpecialDay(
    				locale, gubun, sEvent, holidayyn, name, "", repeatyn, 
    				year, monthday, sLeap, memo, "", "", sUserGroup, sAlarm);
    		
    		if (id > 0) mRowId = id;  
    	//수정
    	} else{
    		ret = mDbHelper.updateSpecialday(mRowId, 
    				locale, gubun, sEvent, holidayyn, name, "", repeatyn, 
    				year, monthday, sLeap, memo, "", "", sUserGroup, sAlarm);
    	}
    	
    	if ( ret == true || ( mRowId != null &&  mRowId != (long)0)) {
    		
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_savecomplete));
    		ComUtil.showToast(this, msg);
    	}   
    	
		//알람갱신(기념일)
		AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmServiceForSpecialday();
    	
    	//위젯갱신
    	ViewUtil.updateAllWidget(this);    	
    	
    }	
    private void deleteState() {   
        
    	boolean ret = mDbHelper.deleteSpecialDay(mRowId); 
    	
    	if ( ret == true ) {
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_deletecomplete));
    		ComUtil.showToast(this, msg);
    	}
    	
		//알람갱신(기념일)
		AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmServiceForSpecialday();
    	
    	//위젯갱신
    	ViewUtil.updateAllWidget(this);
    	
    }		
    /*
	 * DatePicker Call Event
	 * 1.parameter set : year,month,day
	 * 2.EditText Set
	 */
	private void specialDatePickerListener(final Button ed, final MessageHandler msgHd) { 
		final Context ctx = this;
		mDateListener =  new DatePickerDialog.OnDateSetListener() {        
	    	public void onDateSet(DatePicker view, int year,
	    		int monthOfYear, int dayOfMonth) { 	
	    		if ( msgHd.getReturn() == true ) {
			    	mSpecialYear 	= year;                    
			    	mSpecialMonth = monthOfYear + 1;                    
			    	mSpecialDay 	= dayOfMonth; 
			    	String date = SmDateUtil.getDateFormat(mSpecialYear, mSpecialMonth, mSpecialDay);
			    	ed.setText(SmDateUtil.getDateFullFormat(ctx, date, true, false));	    			
	    		}
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
    	MessageHandler msgHd = new MessageHandler(this);
    	Dialog di;
    	switch (id) {    
    	case ComConstant.DATE_DIALOG_ID:    		
    		//setDatePickerset(mEdit);
    		msgHd.setDayString(mSpecialYear, mSpecialMonth - 1 , mSpecialDay);
    		specialDatePickerListener(mEdit , msgHd);
    		di = msgHd.onCreateDialog(mDateListener);
    		return null;

    	case ComConstant.DIALOG_INPUTCHECK:
    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, 
    					ComUtil.getStrResource(this, R.string.inputcheck), R.string.msg_inputcheck, mMsgParm);
    		return di;
		case ComConstant.DIALOG_YES_NO_MESSAGE:
			dialogPickerListener();			
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_deleteconfirm, 
					ComUtil.getStrResource(this, R.string.err_specialdayname));
			di.setOnCancelListener(mDialogListener);
					
			return di;
		}
    return null;
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
     * spinner set
     */
	private void eventSpinnerSet() {

        Resources  res = getResources();
        String[] sArr 		= res.getStringArray(R.array.arr_event);
        String[] sKeyArr 	= res.getStringArray(R.array.arr_event_key);
        ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listKey = new ArrayList<String>();

        for ( int i = 0  ; i < sArr.length ; i++){
        	 list.add(sArr[i].toString());
        	 listKey.add(sKeyArr[i].toString());
        }
        
        ArrayAdapter<String> arrAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mEvent.setAdapter(arrAlarm);
        
        mEvent.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        		}
        	}
        );
	}  
	private void userGroupSpinnerSet() {

        Resources  res = getResources();
        String[] sArr 		= res.getStringArray(R.array.arr_usergroup);
        String[] sKeyArr 	= res.getStringArray(R.array.arr_usergroup_key);
        ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listKey = new ArrayList<String>();

        for ( int i = 0  ; i < sArr.length ; i++){
        	 list.add(sArr[i].toString());
        	 listKey.add(sKeyArr[i].toString());
        }
        
        ArrayAdapter<String> arrAlarm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrAlarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        mUserGroup.setAdapter(arrAlarm);
        
        mUserGroup.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        		}
        	}
        );
	}  	
	
	private void alarmSpinnerSet() {	

        Resources  res = getResources();
        String[] sArr 		= res.getStringArray(R.array.arr_alarmforother);
        String[] sKeyArr 	= res.getStringArray(R.array.arr_alarmforother_key);
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
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		setLeapChange();
        
	}	
}