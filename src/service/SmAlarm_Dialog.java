package com.waveapp.smcalendarlite.service;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.waveapp.smcalendarlite.CalendarMain;
import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;

public class SmAlarm_Dialog extends SMActivity implements OnClickListener{
	
	private DialogInterface.OnCancelListener mDialogListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {  
    	
        super.onCreate(savedInstanceState);	
        showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);
        
    }

	@Override
	public void onClick(View arg0) {
		
	}

    
	/*
	 * Message Dialog Set
	 */
    //@Override
    protected Dialog onCreateDialog(int id) { 
    	
    	MessageHandler msgHd = new MessageHandler(this);
 
    	Dialog di;
    	
		dialogPickerListener();			
		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
				ComUtil.getStrResource(this, R.string.confirm), R.string.msg_alardialog, "알람테스트");
		di.setOnCancelListener(mDialogListener);
				
		return di;

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
				callCalendar();
				
			}
		};
	}	
    /*
     * 호출화면 : 문자메시지(SMSLink)
     */
	private void callCalendar( ) { 
		
        Intent mIntent = new Intent(this, CalendarMain.class);
        //mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	} 	
}
