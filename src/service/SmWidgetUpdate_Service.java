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
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.waveapp.smcalendarlite.widget.CalendarAppWidgetProvider;
import com.waveapp.smcalendarlite.widget.DailyAppWidgetProvider;
import com.waveapp.smcalendarlite.widget.SixDaysAppWidgetProvider;
import com.waveapp.smcalendarlite.widget.WeeklyAppWidgetProvider;

/**
 * This is an example of implementing an application service that will run in
 * response to an alarm, allowing us to move long duration work out of an
 * intent receiver.
 * 
 * @see AlarmService
 * @see AlarmService_Alarm
 */
public class SmWidgetUpdate_Service extends Service {

	
    @Override
    public void onCreate() {
    	
        Thread thr = new Thread(null, mTask, "SmWidgetUpdate_Service");
        thr.start();
    }

    @Override
	public void onLowMemory() {
		super.onLowMemory();
		onDestroy();
	}

	@Override
	public void onDestroy() {
       /////////////////////
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
            SmWidgetUpdate_Service.this.stopSelf();
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

		//¿À´ÃÀÏÁ¤À§Á¬
		DailyAppWidgetProvider daily = new DailyAppWidgetProvider();
		daily.callWidgetUpdate(this);   
		
		//ÁÖ°£À§Á¬(4x1)
		SixDaysAppWidgetProvider weekly = new SixDaysAppWidgetProvider();
		weekly.callWidgetUpdate(this); 
		
		//ÁÖ°£À§Á¬(4x2)
		WeeklyAppWidgetProvider weekly2 = new WeeklyAppWidgetProvider();
		weekly2.callWidgetUpdate(this);     	
		
		//´Þ·ÂÀ§Á¬(4x4)
		CalendarAppWidgetProvider calendar = new CalendarAppWidgetProvider();
		calendar.callWidgetUpdate(this);  
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
