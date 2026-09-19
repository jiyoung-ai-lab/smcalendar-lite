/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.waveapp.smcalendarlite.widget;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMCalendar;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class SixDaysAppWidgetProvider extends AppWidgetProvider {
	
	//30분단위로 처리
	int termMinite = 30;
	private static int mFontColor = 0;
	private static final int WIDGET_UPDATE_INTERVAL = 1000 * 60 * 30; 
	private static PendingIntent mSender; 
	private static AlarmManager mManager;

	static ArrayList<ScheduleInfo> m_ScheduleList = new ArrayList<ScheduleInfo>();

	@Override
	public void onReceive(Context context, Intent intent) {
		
	       super.onReceive(context, intent);
//	       Log.w(">>>>>>>>>>>>>", "onReceive!! SixDay");
	       String action = intent.getAction();
	       
	       // 위젯 업데이트 인텐트를 수신했을 때 
	       if(action.equals("android.appwidget.action.APPWIDGET_UPDATE")) 
	       { 
	         removePreviousAlarm(); 
	         
	         //5분 단위로 보정(오차발생 최소화)  
	         Calendar iCal = Calendar.getInstance();
	         int min 	= iCal.get(Calendar.MINUTE);
	         int second = iCal.get(Calendar.SECOND);
	         int gap = (( min % termMinite) * 60 ) + second ;

	         long firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL - ( gap * 1000 ); 
	         mSender = PendingIntent.getBroadcast(context, ComConstant.WIDGET_SIXDAY_REQ, intent, 0); 
	         mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
	         mManager.set(AlarmManager.RTC_WAKEUP, firstTime, mSender); 
	       } 
	       // 위젯 제거 인텐트를 수신했을 때 
	       else if(action.equals("android.appwidget.action.APPWIDGET_DISABLED")) 
	       { 
	         removePreviousAlarm(); 
	       } 

	}   
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        
        super.onUpdate(context, appWidgetManager, appWidgetIds);    
        
//        Log.w(">>>>>>>>>>>>>>>>>>>>>", "onUpdate SixDay");
        
//        Log.w(TAG, "onUpdate");
	    //다국어처리
		ViewUtil.setLocaleFromPreference(context, context.getClass());
        
        // 현재 클래스로 등록된 모든 위젯의 리스트를 가져옴 
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())); 
     
        final int N = appWidgetIds.length; 
        for(int i = 0 ; i < N ; i++) { 
        	int appWidgetId = appWidgetIds[i]; 
        	updateAppWidget(context, appWidgetManager, appWidgetId); 
        	
        } 

    }
	

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
        	//삭제시 앱단위로 처리할 로직 여기 축
        }
        
        if ( m_ScheduleList != null ) m_ScheduleList = null;
    }

    @Override
    public void onEnabled(Context context) {
//        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
//    	Log.d(TAG, "onDisabled");
    }
	
    /*
     * 외부에서 위젯 일괄 update 할 경우 호출
     */
	public void callWidgetUpdate( Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = null;
		this.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	

	/*
	 * 위젯 화면 갱신 로직(주간일정)
	 */	
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) 
    { 
  	
    		//위젯 클릭시 링크되는 class : main class link
	        RemoteViews updateViews = new RemoteViews(context.getPackageName(),  R.layout.widget_weekly_4x1); 	        
	        Intent intent = null;
	        //ARM 로직적용(유료버전만)
	        intent = new Intent(context, SMCalendar.class);
	        
	        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);	        
	        updateViews.setOnClickPendingIntent(R.id.lin_main, pIntent);	
	        
			//1. 위젯 테마 설정
	        //widget style setting(초기화)
	        ViewUtil.setWidgetStyleFromPreference(context);   		        
			mFontColor = context.getResources().getColor(ComConstant.WIDGET_FONTCOLOR);
			if (Build.VERSION.SDK_INT >= 8) // android 2.1
			{
				updateViews.setInt(R.id.lin_main, "setBackgroundResource", ComConstant.WIDGET_BACK);
			}
			
	        //2.날짜값
	        String [] date = new String [ 6 ];
	        
	        //3.주간일정 화면 만들기 (일일일정 Include)
	        Calendar iCal = Calendar.getInstance();
	        iCal.add(Calendar.DAY_OF_MONTH, 1);
	        updateViews.removeAllViews(R.id.lin_section1);
	        updateViews.removeAllViews(R.id.lin_section2);

	        RemoteViews cellVertical= new RemoteViews(context.getPackageName(),  R.layout.widget_weekly_vertical);      
        	updateViews.addView(R.id.lin_section1, cellVertical);

			
	        //일자별 스케줄
	        for ( int i = 0 ; i < 6 ; i++ ) {
	        	RemoteViews cellViews 	= new RemoteViews(context.getPackageName(),  R.layout.widget_weekly_daycell);         
//	        	RemoteViews cellVertical= new RemoteViews(context.getPackageName(),  R.layout.widget_weekly_vertical);      
	        	date [ i ] = SmDateUtil.getDateFromCal(iCal);
	        	cellViews = setUpdateView(context, cellViews, date [ i ], i);
	        	if ( i < 3 ) {        		
	        		updateViews.addView(R.id.lin_section1, cellViews);
	        	} else {
	        		updateViews.addView(R.id.lin_section2, cellViews);
//	        		if ( i != 6 ) {
//	        			updateViews.addView(R.id.lin_section2, cellVertical);
//	        		}	        		
	        	}
	        	
	        	iCal.add(Calendar.DAY_OF_MONTH, 1);
	        }
	        
	        
	        appWidgetManager.updateAppWidget(appWidgetId, updateViews); 		      
    } 
    
	/*
	 * RemoteView에 데이터 뿌리는 부분
	 */
	private static RemoteViews setUpdateView ( Context context, RemoteViews updateViews, String today, int pos ) {
		
		m_ScheduleList = new ArrayList<ScheduleInfo>();

//		//일자(첫번째 일자는 별도로 처리
//		if ( pos == 0 ) {
//			updateViews.setTextViewText(R.id.total, ComUtil.getStrResource(context, R.string.today) ); 
//
//		} else {
			StringBuffer buffer = new StringBuffer();		
			buffer.append(SmDateUtil.getDateFormatPerGubun(context, today, ComConstant.GUBUN_DAY, true, false));
			buffer.append("(");
			buffer.append(SmDateUtil.getDayOfWeekFromDate(context, today));
			buffer.append(")");
			updateViews.setTextViewText(R.id.total, buffer.toString());			
//		}
		//첫번째 마지막 칸에 로고삽입
		if ( pos == 2 ) {
			updateViews.setViewVisibility(R.id.lin_logo, View.VISIBLE);
		}
		
		if (Build.VERSION.SDK_INT >= 8) // android 2.1
		{
			updateViews.setInt(R.id.color_1, "setBackgroundColor", 0);
			updateViews.setInt(R.id.color_2, "setBackgroundColor", 0);
//			updateViews.setInt(R.id.color_3, "setBackgroundColor", 0);
//			updateViews.setInt(R.id.color_4, "setBackgroundColor", 0);						
		} 

		updateViews.setTextColor(R.id.schedule_1, 	mFontColor);
		updateViews.setTextColor(R.id.schedule_2, 	mFontColor);
//		updateViews.setTextColor(R.id.schedule_3, 	mFontColor);
//		updateViews.setTextColor(R.id.schedule_4, 	mFontColor);
        updateViews.setTextViewText(R.id.schedule_1, "");
        updateViews.setTextViewText(R.id.schedule_2, "");
//        updateViews.setTextViewText(R.id.schedule_3, "");
//        updateViews.setTextViewText(R.id.schedule_4, "");
        
        //불필요한 view 삭제
        updateViews.setViewVisibility(R.id.lin_data3, View.GONE);
        updateViews.setViewVisibility(R.id.lin_data4, View.GONE);
//		updateViews.removeAllViews(R.id.lin_scheduletop_3);
//		updateViews.removeAllViews(R.id.lin_scheduletop_4);
		

        //데이터베이스 미존재시 skip
        if ( !ViewUtil.isdatabaseExist(context) ) {
        	return updateViews;
        }
        
        //데이터 가져오기
        m_ScheduleList = ViewUtil.getTodaySchedule( context, today );        
        int len = m_ScheduleList.size();
        
        //데이터가 있는 경우만 처리
        if (  len > 0 ) {
        	if ( pos == 0 ) {
//            	String todayStr =  ComUtil.getStrResource(context, R.string.todayschedule) + "(" + 
//				Integer.toString(len) + ")" ;
//            	updateViews.setTextViewText( R.id.total, todayStr );        		
        	}

            for ( int i = 0 ; i < len ; i ++ ) {
            	ScheduleInfo info = new ScheduleInfo();
            	info = m_ScheduleList.get(i);
            	
            	int infoColor 		= 0;
//            	String gubunStr 	= "";
            	String scheduleStr 	= "";
            	
            	if ( info.getScheduleGubun() != null ) {
            		if ( info.getScheduleGubun().equals("S")) {
            			infoColor = info.getUseColor();
            			
//    					if ( info.getAllDayYn() != null && !info.getAllDayYn().equals("")) {
//    						gubunStr = ComUtil.setYesReturnValue(info.getAllDayYn(), ComUtil.getStrResource(context, R.string.allday));
//    					} else {
//    						gubunStr = SmDateUtil.getTimeFullFormat(context,info.getStartTime());
//    					}	
    					scheduleStr = info.getScheduleName();
            		} else {
        				if ( info.getHolidayYn() != null && info.getHolidayYn().trim().equals("Y")){
        					infoColor = context.getResources().getColor(R.color.calsunday);
        				} else {
        					infoColor = context.getResources().getColor(R.color.lightgray);
        				}
        				if ( info.getScheduleName() != null && !info.getScheduleName().trim().equals("")){
//        					gubunStr 	= ComUtil.getSpecialDayText( context, info.getScheduleGubun(), info.getSubName() );
        					scheduleStr = info.getScheduleName();
        				} else {
//        					gubunStr 	= ComUtil.getSpecialDayText( context, info.getScheduleGubun(), info.getSubName() );
        					scheduleStr = info.getSubName();
        				}	        				
            		}
            	}

            	if ( i == 0 ) {
            		if (Build.VERSION.SDK_INT >= 8) // android 2.1
            		{
            			updateViews.setInt(R.id.color_1, "setBackgroundColor", infoColor);
            			
            		}
            		updateViews.setTextViewText(R.id.schedule_1, 	scheduleStr);
            	} else if ( i == 1 ) {
            		if (Build.VERSION.SDK_INT >= 8) // android 2.1
            		{
            			updateViews.setInt(R.id.color_2, "setBackgroundColor", infoColor);
            		}
            		updateViews.setTextViewText(R.id.schedule_2, 	scheduleStr);

            	}
            }    	
        }   
        
        return updateViews;
	}    
    /** 
     * 예약되어있는 알람을 취소합니다. 
     */ 
	public void removePreviousAlarm() { 
		
		if(mManager != null && mSender != null) { 
	        mSender.cancel(); 
	        mManager.cancel(mSender); 
		} 
	} 

}


