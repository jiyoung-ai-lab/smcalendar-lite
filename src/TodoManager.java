package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.TodoMemoDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.gsCalendarDlg;

public class TodoManager extends SMActivity implements OnClickListener, RadioGroup.OnCheckedChangeListener {

	private DialogInterface.OnCancelListener mDialogListener;
	
	 
	int mFinishYear;    
	int mFinishMonth;    
	int mFinishDay;

	String mGubun;
	String mMsgParm;
	String mTermValue; 
	
	Long 		mRowId;
	
	ViewGroup	mRoot;
	EditText 	mMemo;
	RadioGroup	mTermYn;
	Button 		mFinishTerm;
    Spinner  	mAlarm;
	CheckBox	mFinish;
	Button 		mEdit;
    
    TodoMemoDbAdaper mDbHelper;

    Button 		confirmButton;
    Button 		cancelButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	
        super.onCreate(savedInstanceState);
        
        //1.DB Open
        mDbHelper = new TodoMemoDbAdaper(this);
        mDbHelper.open();
        
        //2.View ID Set
        setContentView(R.layout.todomanager);	
        
        mRoot			= (ViewGroup)findViewById( R.id.viewroot ) ;
        mMemo 			= (EditText) findViewById(R.id.memo);
        mTermYn			= (RadioGroup) findViewById(R.id.termyn); 
        mFinishTerm 	= (Button) findViewById(R.id.finishterm);  
        mFinish 		= (CheckBox) findViewById(R.id.finish); 
        mAlarm 			= (Spinner) findViewById(R.id.alarmSpinner); 
        confirmButton 	= (Button) findViewById(R.id.confirm);  
        cancelButton 	= (Button) findViewById(R.id.cancel);

        //3.View Set(Defalut)

        //4.Key Value Set ( Database Or Parameter)
        //-. instance에서, null인 경우는 intent에서
        mRowId = null;
        if (savedInstanceState != null) {
        	mRowId = savedInstanceState.getLong(TodoMemoDbAdaper.KEY_ID);
        	mGubun 	= savedInstanceState.getString(ComConstant.TODO_GUBUN);
        }
        if (mRowId == null) { 
        	Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	mRowId 	= extras.getLong(TodoMemoDbAdaper.KEY_ID);
            	mGubun 	= extras.getString(ComConstant.TODO_GUBUN);
            }
        }
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.todo, mGubun ), View.INVISIBLE); 
        
        //5. Data Display     
//    	repeatDateSpinnerSet();
        alarmSpinnerSet();  
        
    	populateFields(); 
        
    	//6.gubun 값에 따라 화면/기능 분기처리 -- 화면조회뒤에 처리
    	setConfirmButton();    	
        
    	//7.activity button
    	setClickListener();
   
    }
    
    /*
     * 데이터처리구분에 따른 처리(등록,수정,삭제,조회)
     */
    private void setConfirmButton () {
    	
       confirmButton.setText(ComUtil.getStrResource(this, R.string.add));
       
       if ( mGubun != null && !mGubun.trim().equals("")) {
        	
    	   confirmButton.setText(mGubun);
    	   
        	if ( mGubun.equals(ComUtil.getStrResource(this, R.string.copy))) {
        		mRowId = null;
        		String copytext = getResources().getString(R.string.copy_prefix).toString();        		
        		mMemo.setText(mMemo.getText().toString() + copytext);
            	
        	} 
        }  
    }
    /*
     * click listener collection
     */
    private void setClickListener () {
    	
		confirmButton.setOnClickListener(this); 
		cancelButton.setOnClickListener(this); 
		mFinishTerm.setOnClickListener(this);
		//radio
		mTermYn.setOnCheckedChangeListener(this);
		
    }
    @Override
	public void onClick(View v) {
		
		if( v == mFinishTerm ) {
			openCalendarDialog( mFinishTerm );
				
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
	/*
	 * click event 처리 fuction
	 */
	private void openCalendarDialog ( final View v ) {
		
		final gsCalendarDlg dlg = new gsCalendarDlg( this  );
		final Context ctx = this;
		if( v == mFinishTerm ) {
			dlg.setDate (mFinishYear, mFinishMonth, mFinishDay);
		} 

		dlg.setOnDismissListener(
	        	new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {					
						
						if( v == mFinishTerm ) {
							mFinishYear 	= dlg.getYear();
							mFinishMonth 	= dlg.getMonth();
							mFinishDay 		= dlg.getDay();
							((Button) v).setText(SmDateUtil.getDateFullFormat(ctx,
									SmDateUtil.getDateFormat(mFinishYear, mFinishMonth, mFinishDay), true, false));
							
						}
					}
	        	});
		dlg.show( ) ;	
		
	} 
	/*
	 * radio button 선택값에 따라 화면 처리 및 return 값 처리
	 */	
	private void setCycleChangeForTerm () {
		int selradio = mTermYn.getCheckedRadioButtonId();
		//종료일 선택시  setting
		if ( selradio == R.id.exist_termyn ) {
			mFinishTerm.setVisibility(View.VISIBLE);
			mTermValue = "Y";
		} else {
			mFinishTerm.setVisibility(View.INVISIBLE);
			mTermValue = "";
		}
	}
	/*
	 * 스케줄 반복주기 값에 따라 radio button setting
	 */
	private void checkCycleGroup ( String cyclevalue, RadioGroup group ) {

		if ( group != null && group == mTermYn) {
			//기한선택시
			if ( cyclevalue != null && cyclevalue.trim().equals("Y") ) {
				mTermYn.check(R.id.exist_termyn);
			} else {
				mTermYn.check(R.id.notexist_termyn);
			}	
			setCycleChangeForTerm();
		}
	}
	/*
	 * confirm click event
	 */
	private void confirmEvent(View v) { 
		
		//1.validation check
		if ( validationCheck() == false ) return;
				
		//2.keyValue set 	
		Bundle bundle = new Bundle();
        if (mRowId != null) {
            bundle.putLong(TodoMemoDbAdaper.KEY_ID, mRowId);
        }
        
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);	        
        setResult(RESULT_OK, mIntent);
        
        //3.DataBase Insert/update
    	saveState();  
    	
        finish();
	}
	/*
	 * delete click event
	 */
	private void deleteEvent() { 

		//2.keyValue set 
		Bundle bundle = new Bundle();
        if (mRowId != null && mRowId != (long)0) {
            bundle.putLong(TodoMemoDbAdaper.KEY_ID, mRowId);
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
	 * 1.modify : userid -> select
	 * 2.new    : birth date set( today )
	 */
	private void populateFields() {
		
		if (mRowId != null && mRowId > (long) 0 ) { 
			
			Cursor cur = mDbHelper.fetchTodolist(mRowId);    
			
//			startManagingCursor(cur);
			
            mMemo.setText(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_MEMO))));
            mTermValue = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_TERMYN)));
            checkCycleGroup(mTermValue, mTermYn);

            String finishterm = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_FINISHTERM)));
            if ( mTermValue != null && mTermValue.equals("Y") ) {
            	setFinishTerm(finishterm);
            	mFinishTerm.setText(SmDateUtil.getDateFullFormat(this, finishterm, true, false));
            } else {
            	setFinishTerm(SmDateUtil.getTodayDefault());
            	mFinishTerm.setText(SmDateUtil.getDateFullFormat(this,SmDateUtil.getTodayDefault(), true, false));
            }
            String sAlarm	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_ALARM)));
            mAlarm.setSelection(ComUtil.getSpinner(this, R.array.arr_alarmforother_key, sAlarm));          

            mFinish.setChecked(ComUtil.getCheckYN(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_FINISH)))));
            
            if ( cur != null ) cur.close();
            
		} else {			
			//new data
			mTermValue = "Y";
			checkCycleGroup(mTermValue, mTermYn);
			setFinishTerm(SmDateUtil.getTodayDefault());
			mFinishTerm.setText(SmDateUtil.getDateFullFormat(this,SmDateUtil.getTodayDefault(), true, false));
			mFinish.setChecked(false);
			mAlarm.setSelection(0);
			
		}
	}
	
	private void setFinishTerm ( String date ) {
		mFinishYear = SmDateUtil.getDateToInt(date, ComConstant.GUBUN_YEAR);
		mFinishMonth= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_MONTH);
		mFinishDay 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY);
		
	}	
	
	/*
	 * validation check
	 */
	private Boolean validationCheck() { 
		mMsgParm = "";
		
		if (ComUtil.getNullCheck(mMemo.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_memo ) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mMemo.setFocusable(true);
			return false;	
		} 
		return true;		
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if ( requestCode == ComConstant.ACTIVITY_SELECT ) {
    		if ( data != null ) {
	    		Bundle extras = data.getExtras();
	            if (extras != null) {
//	            	mUserColor = extras.getString(TodoMemoDbAdaper.KEY_USERCOLOR);
//	            	mDraw.setColorFilter(ViewUtil.drawBackGroundColor ( ComUtil.stringToInt(mUserColor) ));
	            }
    		}
    	}
    } 	
	@Override
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState);  
    	outState.putSerializable(TodoMemoDbAdaper.KEY_ID, mRowId); 
    	outState.putSerializable(ComConstant.USER_GUBUN, mGubun);
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
     * Call database( insert, update ) 
     */
    private void saveState() { 
    	
    	boolean ret = false;
    	
    	String sMemo 		= mMemo.getText().toString(); 
    	String sTermYn 		= mTermValue;
    	String sFinishTerm	= "";
    	if ( mTermValue != null && mTermValue.equals("Y")) {
    		sFinishTerm	= SmDateUtil.getDateFormat(mFinishYear, mFinishMonth, mFinishDay);
    	}
    	String sAlarm 		= ComUtil.setSpinner(this, R.array.arr_alarmforother_key, mAlarm);
        String sFinish		= ComUtil.setCheckYN(mFinish.isChecked());
        String sYearMonth	= SmDateUtil.getTodayDefault().substring(0,6);

        //new
        if (mRowId == null || ( mRowId != null &&  mRowId == (long)0)) {           
    		long id = mDbHelper.insertTodolist(sMemo, sYearMonth, sTermYn, sFinishTerm, sFinish 
    										, sAlarm, "");     
    		if (id > 0) mRowId = id;  
    	//modify
    	} else{
    		ret = mDbHelper.updateTodolist(mRowId, sMemo, sYearMonth, sTermYn, sFinishTerm, sFinish
    								, sAlarm, "");  
    	}
        
    	if ( ret == true || ( mRowId != null &&  mRowId != (long)0)) {
    		
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_savecomplete));
    		ComUtil.showToast(this, msg);
    	}
    	
		//알람갱신(할일)
		AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmServiceForTodo();
    	
    }    
    private void deleteState() {   
        
    	boolean ret = mDbHelper.deleteTodolist(mRowId); 
    	
    	if ( ret == true ) {
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_deletecomplete));
    		ComUtil.showToast(this, msg);
    	}
    	
		//알람갱신(할일)
		AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmServiceForTodo(); 	
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
    	MessageHandler msgHd = new MessageHandler(this);
    	Dialog di;
    	switch (id) {    
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
	public void onCheckedChanged(RadioGroup group, int checkedId) {
			setCycleChangeForTerm();
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
	
}

