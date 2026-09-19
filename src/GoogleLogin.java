package com.waveapp.smcalendarlite;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.link.GoogleRetrieve;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;

public class GoogleLogin extends SMActivity implements OnClickListener  {

	//datePicker
//	private DatePickerDialog.OnDateSetListener mDateListener;
//	private DialogInterface.OnCancelListener mDialogListener;
	
//	int mBirthYear;    
//	int mBirthMonth;    
//	int mBirthDay; 
//	String mUserColor;
//	String mGubun;
	String mMsgParm;
	
//	Long 		mRowId;
	
	ViewGroup	mRoot;
	EditText 	mEmail;
	EditText 	mPassword;
//	Button 		mEdit;
//	Button	 	mBirthDate;
//	Button 		mColor;
//    Spinner  	mRelation;
    
//    UsermanagerDbAdaper mDbHelper;
//
//    ArrayList<String> list, listKey;

    Button 		confirmButton;
    Button 		cancelButton;
    
    Drawable mDraw ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) { 
    	
        super.onCreate(savedInstanceState);
        
//        //1.DB Open
//        mDbHelper = new UsermanagerDbAdaper(this);
//        mDbHelper.open();
        
        //2.View ID Set
        setContentView(R.layout.google_login);	
        
        mRoot			= (ViewGroup)findViewById( R.id.viewroot ) ;
        mEmail 			= (EditText) findViewById(R.id.email);
        mPassword 		= (EditText) findViewById(R.id.password); 

//      mBirthDate 		= (Button) findViewById(R.id.birth_date);  
//      mColor 			= (Button) findViewById(R.id.usercolor); 
//      mRelation 		= (Spinner) findViewById(R.id.relationSpinner); 
//        mMemo 			= (EditText) findViewById(R.id.memo); 
        confirmButton 	= (Button) findViewById(R.id.confirm);  
        cancelButton 	= (Button) findViewById(R.id.cancel);

//        //임시
//        LinearLayout lin_birth = (LinearLayout) findViewById(R.id.lin_birth);
//        lin_birth.setVisibility(View.GONE);
        
//        //3.View Set(Defalut)
//        relationSpinnerSet();      

//        mDraw = getResources( ).getDrawable( R.drawable.sm_cal_schedule_cell );
//        mColor.setBackgroundDrawable(mDraw);
        
//        //4.Key Value Set ( Database Or Parameter)
//        //-. instance에서, null인 경우는 intent에서 
//        mRowId = null;
//        if (savedInstanceState != null) {
//        	mRowId = savedInstanceState.getLong(UsermanagerDbAdaper.KEY_USERID);
//        	mGubun 	= savedInstanceState.getString(ComConstant.USER_GUBUN);
//        }
//        if (mRowId == null) { 
//        	Bundle extras = getIntent().getExtras();
//            if (extras != null) {
//            	mRowId 	= extras.getLong(UsermanagerDbAdaper.KEY_USERID);
//            	mGubun 	= extras.getString(ComConstant.USER_GUBUN);
//            }
//        }
        
//        //구분값이 없는 경우 추가로 처리
//        if ( mGubun == null || ( mGubun != null && mGubun.trim().equals(""))) {
//        	mGubun = ComUtil.getStrResource(this, R.string.add);
//        }
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.googlelogin, "" ), View.INVISIBLE); 

//        //5. Data Display        
//    	populateFields();   
    	
//    	//6.gubun 값에 따라 화면/기능 분기처리 -- 화면조회뒤에 처리
//    	setConfirmButton();    	
        
    	//7.activity button
    	setClickListener();
   
    }
    
    /*
//     * 데이터처리구분에 따른 처리(등록,수정,삭제,조회)
//     */
//    private void setConfirmButton () {
//    	
//       confirmButton.setText(ComUtil.getStrResource(this, R.string.add));
//       
//       if ( mGubun != null && !mGubun.trim().equals("")) {
//        	
//    	   confirmButton.setText(mGubun);
//    	   
//        	if ( mGubun.equals(ComUtil.getStrResource(this, R.string.copy))) {
//        		mRowId = null;
//        		String copytext = getResources().getString(R.string.copy_prefix).toString();        		
//        		mEmail.setText(mEmail.getText().toString() + copytext);
//            	
//        	} 
//        }  
//    }
    /*
     * click listener collection
     */
    private void setClickListener () {
    	
		confirmButton.setOnClickListener(this); 
		cancelButton.setOnClickListener(this); 
//		mBirthDate.setOnClickListener(this);
//		mColor.setOnClickListener(this);
		
    }
    @Override
	public void onClick(View v) {
		
//		if( v == mBirthDate ) {
//			
//			mEdit = mBirthDate;
//			if (!isFinishing())
//				showDialog(ComConstant.DATE_DIALOG_ID);
//			
//		} else  if ( v == mColor ) {
//			
//			Intent mIntent = new Intent(this, ColorList.class);
//			mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//	        startActivityForResult(mIntent, ComConstant.ACTIVITY_SELECT);	
	        
		if ( v == confirmButton ) {
			
//			if ( mGubun != null && mGubun.equals(ComUtil.getStrResource(this, R.string.delete))) {
//				showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);
//			} else {
//				confirmEvent(v);
//			}
			confirmEvent(v);
			
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
				
//		//2.keyValue set 	
//		Bundle bundle = new Bundle(); 
//        if (mRowId != null) {
//            bundle.putLong(UsermanagerDbAdaper.KEY_USERID, mRowId);
//        }
//        
//        Intent mIntent = new Intent();
//        mIntent.putExtras(bundle);	        
//        setResult(RESULT_OK, mIntent);
//        
//        //3.DataBase Insert/update
    	confirmState();
    	
	}

	
	/*
	 * validation check
	 */
	private Boolean validationCheck() {
		
		mMsgParm = "";
		if (ComUtil.getNullCheck(mEmail.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_email) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mEmail.setFocusable(true);
			return false;	
		} else if (ComUtil.isEmail(this, mEmail.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_email) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mEmail.setFocusable(true);
			return false;	
		} else if (ComUtil.getNullCheck(mPassword.getText().toString()) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_password) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mPassword.setFocusable(true);
			return false;
		}
		return true;		
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	if ( requestCode == ComConstant.ACTIVITY_SELECT ) {
//    		if ( data != null ) {
//	    		Bundle extras = data.getExtras();
//	            if (extras != null) 
//	            	mUserColor = extras.getString(UsermanagerDbAdaper.KEY_USERCOLOR);
//	            	mDraw.setColorFilter(ViewUtil.drawBackGroundColor ( ComUtil.stringToInt(mUserColor) ));
//	            }
//    		}
    } 	
	@Override
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState);  
//    	outState.putSerializable(UsermanagerDbAdaper.KEY_USERID, mRowId); 
//    	outState.putSerializable(ComConstant.USER_GUBUN, mGubun);
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
//        if ( mDbHelper != null ) mDbHelper.close();
    } 
    
    /*
     * Call Login 
     */
    private void confirmState() { 
    	
    	boolean ret = false;

    	String sEmail 	= mEmail.getText().toString();
		String sPassword= mPassword.getText().toString();

		GoogleRetrieve google = new GoogleRetrieve();
		ret =  google.GoogleClientLogin(sEmail, sPassword);
        
		//로그인 오류시 메시지처리후 focus
    	if ( ret == false ) {    		
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_googleloginfail));
    		ComUtil.showToast(this, msg);
    		mPassword.setText("");
    		mPassword.setFocusable(true);
    	}  else {
    		ComUtil.showToast(this, "Not Open");
    		if ( google.GoogleCalendarRetrieve()) {
//				Bundle bundle = new Bundle();
//
//				bundle.putString(ComConstant.CALCHOICE_GUBUN, calchoice);
//				
//				Intent intent = new Intent(this, OtherCalendarList.class);
//				intent.putExtras(bundle);
//				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				
//				startActivity(intent);
    		}
            finish();
    	}
    	
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
//    	case ComConstant.DATE_DIALOG_ID:
//    		msgHd.setDayString(mBirthYear, mBirthMonth - 1 , mBirthDay);
//    		birthDatePickerListener(mEdit);
//    		di = msgHd.onCreateDialog(mDateListener);
//    		return di;
//    	
    	case ComConstant.DIALOG_INPUTCHECK: 
    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, 
    				ComUtil.getStrResource(this, R.string.inputcheck), R.string.msg_inputcheck, mMsgParm);
    		return di;
//		case ComConstant.DIALOG_YES_NO_MESSAGE:
//			dialogPickerListener();			
//			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
//					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_deleteconfirm, "");
//			di.setOnCancelListener(mDialogListener);
//					
//			return di;
		}
    return null;
    }

}

