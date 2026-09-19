package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class UserManager extends SMActivity implements OnClickListener  {
	
	//datePicker
	private DatePickerDialog.OnDateSetListener mDateListener;
	private DialogInterface.OnCancelListener mDialogListener;
	
	int mBirthYear;    
	int mBirthMonth;    
	int mBirthDay; 
	String mUserColor;
	String mGubun;
	String mMsgParm;
	
	Long 		mRowId;
	
	ViewGroup	mRoot;
	EditText 	mName;
	EditText 	mAddress, mMemo;
	Button 		mEdit;
	Button	 	mBirthDate;
	Button 		mColor;
    Spinner  	mRelation;
    
    UsermanagerDbAdaper mDbHelper;

    ArrayList<String> list, listKey;

    Button 		confirmButton;
    Button 		cancelButton;
    
    Drawable mDraw ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	
        super.onCreate(savedInstanceState);
        
        //1.DB Open
        mDbHelper = new UsermanagerDbAdaper(this);
        mDbHelper.open();
        
        //2.View ID Set
        setContentView(R.layout.usermanager);	
        
        mRoot			= (ViewGroup)findViewById( R.id.viewroot ) ;
        mName 			= (EditText) findViewById(R.id.name);
        mBirthDate 		= (Button) findViewById(R.id.birth_date);  
        mColor 			= (Button) findViewById(R.id.usercolor); 
        mRelation 		= (Spinner) findViewById(R.id.relationSpinner); 
        mAddress 		= (EditText) findViewById(R.id.address); 
        mMemo 			= (EditText) findViewById(R.id.memo); 
        confirmButton 	= (Button) findViewById(R.id.confirm);  
        cancelButton 	= (Button) findViewById(R.id.cancel);

        //임시
        LinearLayout lin_birth = (LinearLayout) findViewById(R.id.lin_birth);
        lin_birth.setVisibility(View.GONE);
        
        //3.View Set(Defalut)
        relationSpinnerSet();      

        mDraw = getResources( ).getDrawable( R.drawable.sm_cal_schedule_cell );
        mColor.setBackgroundDrawable(mDraw);
        
        //4.Key Value Set ( Database Or Parameter)
        //-. instance에서, null인 경우는 intent에서 
        mRowId = null;
        if (savedInstanceState != null) {
        	mRowId = savedInstanceState.getLong(UsermanagerDbAdaper.KEY_USERID);
        	mGubun 	= savedInstanceState.getString(ComConstant.USER_GUBUN);
        }
        if (mRowId == null) { 
        	Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	mRowId 	= extras.getLong(UsermanagerDbAdaper.KEY_USERID);
            	mGubun 	= extras.getString(ComConstant.USER_GUBUN);
            }
        }
        
        //구분값이 없는 경우 추가로 처리
        if ( mGubun == null || ( mGubun != null && mGubun.trim().equals(""))) {
        	mGubun = ComUtil.getStrResource(this, R.string.add);
        }
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.user, mGubun ), View.INVISIBLE); 

        //5. Data Display        
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
        		mName.setText(mName.getText().toString() + copytext);
            	
        	} 
        }  
    }
    /*
     * click listener collection
     */
    private void setClickListener () {
    	
		confirmButton.setOnClickListener(this); 
		cancelButton.setOnClickListener(this); 
		mBirthDate.setOnClickListener(this);
		mColor.setOnClickListener(this);
		
    }
    @Override
	public void onClick(View v) {
		
		if( v == mBirthDate ) {
			
			mEdit = mBirthDate;
			if (!isFinishing())
				showDialog(ComConstant.DATE_DIALOG_ID);
			
		} else  if ( v == mColor ) {
			
			Intent mIntent = new Intent(this, ColorList.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        startActivityForResult(mIntent, ComConstant.ACTIVITY_SELECT);	
	        
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
	 * confirm click event
	 */
	private void confirmEvent(View v) { 
		
		//1.validation check
		if ( validationCheck() == false ) return;
				
		//2.keyValue set 	
		Bundle bundle = new Bundle();
        if (mRowId != null) {
            bundle.putLong(UsermanagerDbAdaper.KEY_USERID, mRowId);
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
            bundle.putLong(UsermanagerDbAdaper.KEY_USERID, mRowId);
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
			
			Cursor user = mDbHelper.fetchUsermanager(mRowId);    
			
//			startManagingCursor(user);
			
            mName.setText(ComUtil.setBlank(user.getString(user.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_NAME))));
            mUserColor = ComUtil.setBlank(user.getString(user.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_USERCOLOR)));
            mDraw.setColorFilter(ViewUtil.drawBackGroundColor ( ComUtil.stringToInt(mUserColor)));
            
            String birth = ComUtil.setBlank(user.getString(user.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_BIRTH)));
            setBirthDate(birth);
            mBirthDate.setText(SmDateUtil.getDateFullFormat(this, birth, false, false));
            
            String sRelation	= ComUtil.setBlank(user.getString(user.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_RELATION)));
            mRelation.setSelection(ComUtil.getSpinner(this, R.array.arr_relation_key, sRelation)); 

            mAddress.setText(ComUtil.setBlank(user.getString(user.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_ADDRESS))));  
            mMemo.setText(ComUtil.setBlank(user.getString(user.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_MEMO))));  
		
            if ( user != null ) user.close();
            
		} else {			
			//new data
			mUserColor = "";
			setBirthDate(SmDateUtil.getTodayDefault());
			mBirthDate.setText(SmDateUtil.getDateFullFormat(this, SmDateUtil.getTodayDefault(), false, false));
			
		}
	}	
	
	private void setBirthDate ( String date ) {
		mBirthYear 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_YEAR);
		mBirthMonth = SmDateUtil.getDateToInt(date, ComConstant.GUBUN_MONTH);
		mBirthDay 	= SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY);
		
	}
	
	/*
	 * validation check
	 */
	private Boolean validationCheck() {
		
		mMsgParm = "";
		
		if (ComUtil.getNullCheck(mName.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_name) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mName.setFocusable(true);
			return false;	
//		} else if ( Double.parseDouble(birthdate)  > Double.parseDouble(today) ){
//			mMsgParm = ComUtil.getStrResource(this, R.string.err_birthday) ;
//			showDialog(ComConstant.DIALOG_INPUTCHECK);
//			rtn = false;
		} else if ( ComUtil.getNullCheck(mUserColor) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_style) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;	
		}
		return true;		
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if ( requestCode == ComConstant.ACTIVITY_SELECT ) {
    		if ( data != null ) {
	    		Bundle extras = data.getExtras();
	            if (extras != null) 
	            	mUserColor = extras.getString(UsermanagerDbAdaper.KEY_USERCOLOR);
	            	mDraw.setColorFilter(ViewUtil.drawBackGroundColor ( ComUtil.stringToInt(mUserColor) ));
	            }
    		}
    } 	
	@Override
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState);  
    	outState.putSerializable(UsermanagerDbAdaper.KEY_USERID, mRowId); 
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
    	
    	String sName 	= mName.getText().toString();        
    	String sBirth 	= SmDateUtil.getDateFormat(mBirthYear, mBirthMonth, mBirthDay);
    	String sRelation = ComUtil.setSpinner(this, R.array.arr_relation_key, mRelation);
    	String sTel1 = "";
        String sAddress	= mAddress.getText().toString();
        String sMemo 	= mMemo.getText().toString();

        //new
        if (mRowId == null || ( mRowId != null &&  mRowId == (long)0)) {           
    		long id = mDbHelper.insertUsermanager(sName, sBirth, sRelation, mUserColor, sTel1, sAddress, sMemo  );          
    		if (id > 0) mRowId = id;  
    	//modify
    	} else{
    		ret = mDbHelper.updateUsermanager(mRowId, sName, sBirth, sRelation, mUserColor, sTel1, sAddress, sMemo );
    	}
        
    	if ( ret == true || ( mRowId != null &&  mRowId != (long)0)) {
    		
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_savecomplete));
    		ComUtil.showToast(this, msg);
    	} 
    	
    	//위젯갱신
    	ViewUtil.updateAllWidget(this);
    	
    }    
    private void deleteState() {   
        
    	boolean ret = mDbHelper.deleteUsermanager(mRowId); 
       	
    	if ( ret == true ) {
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_deletecomplete));
    		ComUtil.showToast(this, msg);
    	}
    	//위젯갱신
    	ViewUtil.updateAllWidget(this);
    }	
    /*
     * spinner set 
     */
	private void relationSpinnerSet() {
       
        Resources res = getResources();
        String[] sRelationArr = res.getStringArray(R.array.arr_relation);
        String[] sRelationKeyArr = res.getStringArray(R.array.arr_relation_key);
        
        list = new ArrayList<String>();
        listKey = new ArrayList<String>();

        int len = sRelationArr.length;
        for ( int i = 0  ; i < len ; i++){
        	 list.add(sRelationArr[i].toString());
        	 listKey.add(sRelationKeyArr[i].toString());
        }
        
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mRelation.setAdapter(aa);
        
        mRelation.setOnItemSelectedListener(
        	new OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parents, View view, int position, long id){
        			//tv.setText(list.get(position));
        		}
        		public void onNothingSelected(AdapterView<?> arg0){
        			//tv.setText("1");
        		}
        	}
        );	
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
	 * DatePicker Call Event
	 * 1.parameter set : year,month,day
	 * 2.EditText Set
	 */
	private void birthDatePickerListener(final Button ed) { 	
		final Context ctx = this;
		mDateListener =  new DatePickerDialog.OnDateSetListener() {        
	    	public void onDateSet(DatePicker view, int year,
	    		int monthOfYear, int dayOfMonth) { 
		    	mBirthYear 	= year;                    
		    	mBirthMonth = monthOfYear + 1;                    
		    	mBirthDay 	= dayOfMonth; 
		    	String date = SmDateUtil.getDateFormat(mBirthYear, mBirthMonth, mBirthDay);
		    	ed.setText(SmDateUtil.getDateFullFormat(ctx, date, false, false));
		    	
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
    		msgHd.setDayString(mBirthYear, mBirthMonth - 1 , mBirthDay);
    		birthDatePickerListener(mEdit);
    		di = msgHd.onCreateDialog(mDateListener);
    		return null;
    	
    	case ComConstant.DIALOG_INPUTCHECK: 
    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, 
    				ComUtil.getStrResource(this, R.string.inputcheck), R.string.msg_inputcheck, mMsgParm);
    		return di;
		case ComConstant.DIALOG_YES_NO_MESSAGE:
			dialogPickerListener();			
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_deleteconfirm, "");
			di.setOnCancelListener(mDialogListener);
					
			return di;
		}
    return null;
    }

}

