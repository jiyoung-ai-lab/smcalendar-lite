package com.waveapp.smcalendarlite.link;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.ContactHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;

public class SMSLink extends SMActivity implements OnClickListener{
	
	TextWatcher watcher = null;
	EditText 	mMemo;
	EditText 	mPhone;
	TextView 	mCharCnt;
	
	ImageButton mContact;	
	
	String 		mMessage;
	String 		mErrParm;
	
	Button 		sendButton;
    Button 		cancelButton;
   
	@Override    
	public void onCreate(Bundle savedInstanceState)     
	{        
		super.onCreate(savedInstanceState); 

		//2.View ID Set
        setContentView(R.layout.sendsms);	 
        
        mMemo 			= (EditText) findViewById(R.id.memo);
        mContact		= (ImageButton) findViewById(R.id.btn_contact); 
        mPhone			= (EditText) findViewById(R.id.phone); 
        mCharCnt		= (TextView) findViewById(R.id.tv_charcnt);         
        sendButton 		= (Button) findViewById(R.id.send);  
        cancelButton 	= (Button) findViewById(R.id.cancel);	
        
        //4.Key Value Set ( Database Or Parameter)
        //-. instance에서, null인 경우는 intent에서
        mMessage = null;
        if (savedInstanceState != null) {
        	mMessage = savedInstanceState.getString(ComConstant.FULL_MESSAGE);
        }
        if (mMessage == null) { 
        	Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	mMessage = extras.getString(ComConstant.FULL_MESSAGE);
            }
        } 
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_sms, "" ), View.INVISIBLE); 
        
    	//7.activity button
    	setClickListener();  
    	
    	
        //8. Data Display 
        mMemo.setText(mMessage);
        mCharCnt.setText(mMessage.getBytes().length + "/400byte");
        
     
	}   
 	/*
     * click listener collection
     */
    private void setClickListener () {
    	
    	sendButton.setOnClickListener(this); 
		cancelButton.setOnClickListener(this); 
		mContact.setOnClickListener(this);
		
		setTextChangedListener(mMemo, mCharCnt);
		mMemo.addTextChangedListener(watcher);
		
    }	
    @Override
	public void onClick(View v) {
		
		if( v == mContact ) {
	        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
	        mIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);	
	        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        startActivityForResult(mIntent, ComConstant.ACTIVITY_PHONE_SELECT);
				
		} else  if ( v == sendButton ) {
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
		String sPhoneNum = mPhone.getText().toString(); 
		String sMsg 	 = mMemo.getText().toString(); 
		
        sendMyMessage(sPhoneNum, sMsg);
    	
        ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.msg_sendsms));
        
        finish();

	} 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if ( requestCode == ComConstant.ACTIVITY_PHONE_SELECT ) {
        	ContactHandler conHd = new ContactHandler(this);
        	conHd.onPhoneResult(data);
        	mPhone.setText(conHd.getPhoneNumber()); 
//        	if ( conHd.getName()   != null &&  !conHd.getName().trim().equals(""))
//        		mPhoneName.setText("(" + conHd.getName() + ")" ); 
    	} 

    }
	private Boolean validationCheck() { 
		Boolean rtn = true;

		//전화번호,메모 필수처리
		if (ComUtil.getNullCheck(mPhone.getText().toString()) == false ){
			mErrParm = ComUtil.getStrResource(this, R.string.err_phone) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mPhone.setFocusable(true);
			rtn = false;
		} else if (ComUtil.getNullCheck(mMemo.getText().toString()) == false ){
			mErrParm = ComUtil.getStrResource(this, R.string.err_memo) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			mMemo.setFocusable(true);
			rtn = false;
		}
		return rtn;		
	}	
	//---sends an SMS message to another device---    
	private  void sendMyMessage(String phoneNumber, String message)    {
//		PendingIntent pi = PendingIntent.getActivity(this, 0,            
//				new Intent(this, SMSLink.class), 0);                        
//		SmsManager sms = SmsManager.getDefault();        
//		sms.sendTextMessage(phoneNumber, null, message, pi, null);  
		
		Intent intent = new Intent();
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		
		SmsManager sms = SmsManager.getDefault();  
		sms.sendTextMessage(phoneNumber, null, message, pi, null);  		
		
	} 
	  @Override
	    protected Dialog onCreateDialog(int id) { 
	    	MessageHandler msgHd = new MessageHandler(this);
	    	Dialog di;
	    	switch (id) {  
	    	case ComConstant.DIALOG_INPUTCHECK:
	    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, 
	    					ComUtil.getStrResource(this, R.string.inputcheck), R.string.msg_inputcheck, mErrParm);
	    		return di;
			}
	    return null;
	    }

    /*
	 * EditText Change Event
	 * 1.parameter set : EditText
	 */
	private void setTextChangedListener(final EditText ed, final TextView tv ) { 
		//final Context ctx = this;
		watcher =  new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				tv.setText(ed.getText().toString().getBytes().length + "/400byte");
			}
		};
	}	

	
}
