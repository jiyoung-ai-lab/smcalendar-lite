package com.waveapp.smcalendarlite.handler;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.util.SmDateUtil;

public class MessageHandler extends SMActivity {
	
	private final Context mCtx;
	private int mYear;    
	private int mMonth;    
	private int mDay;
	private int mHour;   
	private int mMinute; 
	
	private boolean mReturn;
	
    public MessageHandler(Context ctx) {
        this.mCtx = ctx;
    }

	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        
	}
	 
	/***
     * Alert Msg 
     * @param Context ctx, int id, String parm1, String param2
     * @return simple message, Dialog (y+n, y,n)
     */
    public final Dialog onCreateDialog(Context ctx, int id, String title, int body, String parm1) {

    	AlertDialog.Builder alt ;
    	AlertDialog alert;
    	
    	//View Design factor
    	Resources res 			= mCtx.getResources();
//    	View view = ((Activity) mCtx).getLayoutInflater().inflate(android.R.layout.select_dialog_item, null);
//		int vtTxColor 	= res.getColor(R.color.gray); 
//		int vbTxColor 	= res.getColor(R.color.gray); 

//		View view = ((Activity) mCtx).getLayoutInflater().inflate(R.layout.dlg_view, null);  
//		// View set				 
//		TextView txtTitle = (TextView) view.findViewById(R.id.title); 
//		TextView message = (TextView) view.findViewById(R.id.message); 
		
//		if ( title != null && !title.trim().equals("") ) {
//			txtTitle.setTextColor(vtTxColor);
//			txtTitle.setText(title);	
//		} else {
//			//txtTitle.setTextColor(vtTxColor);
//			//txtTitle.setText(title);	
//		}
		
//		message.setTextColor(vbTxColor);		
//		message.setText(String.format(res.getString(body), parm1));
		
		String message = String.format(res.getString(body), parm1);
    	switch (id) { 

	    	case ComConstant.DIALOG_SIMPLE_MESSAGE:    
	   		 	Dialog d = new Dialog(this);
	            Window window = d.getWindow();
	            window.setFlags(WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW, WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);
	            //d.setTitle( title );
	            d.show();
	            return d;
	            
		   	case ComConstant.DIALOG_YES_NO_MESSAGE:
				 alt = new AlertDialog.Builder(mCtx); 
//				 alt.setView(view) ;
				 	//
				 	//.setTitle(title)
				 alt.setTitle(title)
				 	.setMessage(message)
				 	.setIcon(R.drawable.sm_alert_dialog)
				 	.setCancelable(false)                                                                     
				    .setPositiveButton(ComConstant.MSG_BTN_YES,                                                                 
				     new DialogInterface.OnClickListener() {                                                  
				         public void onClick(DialogInterface dialog, int id) {  
				        	 dialog.cancel(); 
				        	 //finish();
				         }                                                                                    
				     })
			       	.setNegativeButton(ComConstant.MSG_BTN_NO, 
			       	new DialogInterface.OnClickListener() {        
				       	public void onClick(DialogInterface dialog, int id) { 
				       		dialog.dismiss();  
			       		
			       		}    
			       	});
				 
				 alert = alt.create(); 
				 //alert.show();
				 
				 return alert;

		   	case ComConstant.DIALOG_YES_MESSAGE:
				// View Show
		   		 alt = new AlertDialog.Builder(mCtx);                                     
//		   		alt.setView(view) ;
		   		alt.setIcon(R.drawable.sm_alert_dialog)
				 	.setTitle(title)
				 	.setMessage(message)
				    .setCancelable(false)                                                                     
				    .setPositiveButton(ComConstant.MSG_BTN_YES,                                                                 
				     new DialogInterface.OnClickListener() {                                                  
				         public void onClick(DialogInterface dialog, int id) {                                
				             dialog.cancel(); 
				             //finish();
				         }                                                                                    
				     });
				 
		   		 alert = alt.create();                                                            
		   		 //alert.show();
				 
				 return alert;
				 
		   	case ComConstant.DIALOG_NO_MESSAGE:
				// View Show
		   		 alt = new AlertDialog.Builder(mCtx);                                     
//		   		alt.setView(view) ; 
		   		alt.setIcon(R.drawable.sm_alert_dialog)
				 	.setTitle(title)
				 	.setMessage(message)
				    .setCancelable(false)                                                                     
				    .setNegativeButton(ComConstant.MSG_BTN_YES,                                                                 
				     new DialogInterface.OnClickListener() {                                                  
				         public void onClick(DialogInterface dialog, int id) {                                
				        	 dialog.dismiss();  
				        	 dialog = null;
				         }                                                                                    
				     });
				 
		   		 alert = alt.create();                                                            
		   		 //alert.show();
				 
				 return alert;
    	} 
    	
    	/*
    	 * ProgressDialog dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMessage("Please wait while loading...");
                dialog.setButton("취소", new DialogInterface.OnClickListener() {
                	@Override
    				public void onClick(DialogInterface dialog, int which) {
    					//progressThread.stop();
    				}

                });
                return dialog;
    	 */
    	
    return null;
    }   
  
	/***
     * DatePicker  
     * @param Context ctx, int id, String parm1, String param2
     * @return Dialog
     */
    public final Dialog onCreateDialog( DatePickerDialog.OnDateSetListener mDateSetListener ) {

    	final DatePickerDialog mDatapicker	
    			= new DatePickerDialog(mCtx, mDateSetListener, mYear, mMonth, mDay); 
    	
    	mReturn = true;
//    	if (Build.VERSION.SDK_INT >= 16) // //안드로이드 4.1에서 부터 좀 달라짐 -.-(버튼이 없어졌다..)
//    	{
//        	mDatapicker.setButton(DialogInterface.BUTTON_POSITIVE, ComConstant.MSG_BTN_YES,                                                                 
//   			     new DialogInterface.OnClickListener() {                                                  
//   	         public void onClick(DialogInterface dialog, int id) { 
//   	        	 mReturn = true;
//   	         }                                                                                    
//   	     });
//       	mDatapicker.setButton(DialogInterface.BUTTON_NEGATIVE, ComConstant.MSG_BTN_NO,                                                                 
//   			     new DialogInterface.OnClickListener() {                                                  
//   	         public void onClick(DialogInterface dialog, int id) {                                
//   	        	 mReturn = false;
//   	        	 mDatapicker.dismiss();
//   	         }                                                                                    
//   	     }); 
//    	}
    	
 
//		mDatapicker.setOnDismissListener(
//	        	new OnDismissListener() {
//					@Override
//					public void onDismiss(DialogInterface dialog) {
//						///
//					}
//				});
		   		
				
		mDatapicker.show();    		
		return mDatapicker;
    }
      
	/***
     * TimePicker  
     * @param Context ctx, int id, String parm1, String param2
     * @return Dialog
     */
    
    public final Dialog onCreateDialog( TimePickerDialog.OnTimeSetListener mTimeSetListener ) {

    	TimePickerDialog mTimepicker	
    			= new TimePickerDialog(mCtx, mTimeSetListener, mHour, mMinute, false); 
    	mReturn = true;
 
//    	
//    	if (Build.VERSION.SDK_INT >= 16) // //안드로이드 4.1에서 부터 좀 달라짐 -.-(버튼이 없어졌다..)
//    	{
//
//    		//임의방식    		
//        	mTimepicker.setButton(DialogInterface.BUTTON_POSITIVE, ComConstant.MSG_BTN_YES,                                                                 
//    			     new DialogInterface.OnClickListener() {                                                  
//    	         public void onClick(DialogInterface dialog, int id) { 
//    	        	 Log.w("BUTTON_POSITIVE  click!!!!" , " " );
//
//    	        	 mReturn = true;
////    	        	 mPicker.ont
////    	        	 dialog.dismiss();
////    	        	 dialog.cancel();
//    	         }                                                                                    
//    	     });
//        	mTimepicker.setButton(DialogInterface.BUTTON_NEGATIVE, ComConstant.MSG_BTN_NO,                                                                 
//    			     new DialogInterface.OnClickListener() {                                                  
//    	         public void onClick(DialogInterface dialog, int id) {   
//    	        	 Log.w("BUTTON_NEGATIVE  click!!!!" , " " );
//    	        	 mReturn = false;
////    	        	 dialog.dismiss();
////    	        	 mTimepicker.dismiss();
//    	         }                                                                                    
//    	     }); 
//        	mTimepicker.setButton(DialogInterface.BUTTON_NEUTRAL, ComConstant.MSG_BTN_DONE,                                                                 
//   			     new DialogInterface.OnClickListener() {                                                  
//   	         public void onClick(DialogInterface dialog, int id) {   
//   	        	 Log.w("BUTTON_NEUTRAL  click!!!!" , " " );
//   	        	 mReturn = true;
////   	        	 dialog.dismiss();
////   	        	 mTimepicker.dismiss();
//   	         }                                                                                    
//   	     });         	
//    	}

				
    	mTimepicker.show();    		
		return mTimepicker;
    }	
    
	    
	 /*
	  * Datepicker value
	  */
	 public void setYearString(int year) { 
		this.mYear 	= year;  	    
	 } 
	 public void setMonthString(int monthOfYear) { 
		this.mMonth = monthOfYear;	    
	 } 	 
	 public void setDayString(int dayOfMonth) {           
		this.mDay 	= dayOfMonth;  	    
	 } 	 
	 public void setDayString(int year, int monthOfYear, int dayOfMonth) { 
		 this.mYear 	= year;  
		 this.mMonth 	= monthOfYear;  
		 this.mDay 		= dayOfMonth; 
	 }
	 public int getYearString() { 
		 return mYear; 	    
	 } 	
	 public int getMonthString() { 
		 return mMonth; 	    
	 } 
	 public int getDayString() { 
		 return mDay; 	    
	 } 	 
	 public String getDateString() { 
		 //YYYY-MM-DD		 
		 return SmDateUtil.getDateFormat(mYear, mMonth, mDay);	    
	 }
	 /*
	  * Timepicker value
	  */
	 public void setHourString(int hour) { 
		this.mHour 	= hour;  	    
	 } 
	 public void setMinuteString(int minute) { 
		this.mMinute = minute;	    
	 }

	 public void setTimeString(int hour, int minute) { 
		 this.mHour 	= hour;  
		 this.mMinute 	= minute;  
	 }
	 public int getHourString() { 
		 return mHour; 	    
	 } 	
	 public int getMinuteString() { 
		 return mMinute; 	    
	 } 

	 /*
	  * return value 
	  */
	 public void setReturn( boolean ret ) { 
		this.mReturn 	= ret;  	    
	 } 
	 
	 public boolean getReturn () { 
		return mReturn;	    
	 }

}

	
