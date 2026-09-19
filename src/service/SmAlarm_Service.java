/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.waveapp.smcalendarlite.service;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMCalendar;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/**
 * This is an example of implementing an application service that will run in
 * response to an alarm, allowing us to move long duration work out of an
 * intent receiver.
 * 
 * @see AlarmService
 * @see AlarmService_Alarm
 */
public class SmAlarm_Service extends Service {

	NotificationManager mNM;
     
	String mAlarmTitle;
	String mAlarmSubName;
	String mAlarmGubun;
	
//	AlarmHandler am ;
	
    @Override
    public void onCreate() {
    	
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        Thread thr = new Thread(null, mTask, "SmAlarm_Service");
        thr.start();
        
//        Log.w(">>>>>>>>>>>>>>>>>>>>>>", "onCreate : " + ComConstant.ALARM_GUBUN);
    }

    @Override
	public void onLowMemory() {
		super.onLowMemory();
		onDestroy();
	}

	@Override
	public void onDestroy() {
		
		if ( mNM != null ) {
			mNM.cancel(11);
			mNM.cancel(12);
			mNM.cancel(13);
		}
       
//		if ( am != null ) {
//			//서비스 재기동
//			am.setAlarmService();
//			am.setAlarmServiceForSpecialday();
//			am.setAlarmServiceForTodo();
//		}
       
    }

    /**
     * The function that runs in our worker thread
     */
    Runnable mTask = new Runnable() {
        public void run() {
             // Normally we would do some work here...  for our sample, we will
            // just sleep for 30 seconds.
            long endTime = System.currentTimeMillis() + 15*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (mBinder) { 
                    try {
                        mBinder.wait();
                    } catch (Exception e) {
                    	break;
                    }
                }
           }

            // Done with our work...  stop the service!
            SmAlarm_Service.this.stopSelf();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
	public void onRebind(Intent intent) {
    	super.onRebind(intent);		
	}

	@Override
	public void onStart(Intent intent, int startId) {

		mAlarmTitle = getResources().getString(R.string.alarm_service_started);
		mAlarmSubName = SmDateUtil.getNowHourMinuteOclock(0);
		mAlarmGubun = "";
		if ( intent != null ) {
			Bundle extras = intent.getExtras();
			if (extras != null) { 
				mAlarmTitle 	= extras.getString(ComConstant.ALARM_TITLE);
				mAlarmSubName 	= extras.getString(ComConstant.ALARM_SUBNAME);
				mAlarmGubun 	= extras.getString(ComConstant.ALARM_GUBUN);
				// show the icon in the status bar
		        showNotification();
		        
//		        Log.w(">>>>>>>>>>>>>>>>>>>>>>", "onStart : " + mAlarmGubun);
			}
		}
    	

        //showDialog(ComConstant.DIALOG_INPUTCHECK);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        
    	// In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.alarm_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.sm_notify_calendar, text,
                System.currentTimeMillis());
        
        //alarm mode setting
        //s:sound, b:viblation
        String alarmmode = ViewUtil.getAlarmModeFromPref( this );
        if ( alarmmode != null && alarmmode.equals("S")) {
        	notification.defaults = Notification.DEFAULT_SOUND;
        } else if ( alarmmode != null && alarmmode.equals("B")) {
        	notification.defaults = Notification.DEFAULT_VIBRATE;
        } else if ( alarmmode != null && alarmmode.equals("SB")) {
        	notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
        }
       
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, ComConstant.ALARM_REQ,
//                new Intent(this, SMCalendar.class), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(this, ComConstant.ALARM_REQ,
                new Intent(this, SMCalendar.class), PendingIntent.FLAG_UPDATE_CURRENT);        
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, mAlarmTitle, mAlarmSubName , contentIntent);

        //dialog box call
        /*
        Intent mIntent = new Intent(SmAlarm_Service.this, SmAlarm_Dialog.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        startActivity(mIntent);
        */

		// Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        
        if ( mAlarmGubun != null && mAlarmGubun.equals(ComConstant.SCHEDULE_GUBUN)) {
        	mNM.notify(ComConstant.ALARM_SPE_CODE, notification);
        } else if ( mAlarmGubun != null && mAlarmGubun.equals(ComConstant.SPECIAL_GUBUN)) {
        	mNM.notify(ComConstant.ALARM_SPC_CODE, notification);
        } else if ( mAlarmGubun != null && mAlarmGubun.equals(ComConstant.TODO_GUBUN)) {
        	mNM.notify(ComConstant.ALARM_TODO_CODE, notification);
        }
        
		//서비스 재기동
		//알람기동(일정)
    	AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmService();
    	ah.setAlarmServiceForSpecialday();
    	ah.setAlarmServiceForTodo();        
//        
//        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(mIntent);
    }

    /**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    private final IBinder mBinder = new Binder() {
        @Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
		        int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };
    

}
