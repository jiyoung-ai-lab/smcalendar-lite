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
import android.widget.RemoteViews;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMCalendar;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class CalendarAppWidgetProvider extends AppWidgetProvider {
	 
	//30분단위로 처리
	int termMinite = 30;
	private static int mFontColor = 0;
	private static int mBeforeFontColor = 0;
	private static final int WIDGET_UPDATE_INTERVAL = 1000 * 60 * 30;
	private static PendingIntent mSender; 
	private static AlarmManager mManager;

	Calendar 		m_Calendar;
	static ArrayList<ScheduleInfo> m_ScheduleList = new ArrayList<ScheduleInfo>();

	
	@Override
	public void onReceive(Context context, Intent intent) {
		
	       super.onReceive(context, intent);
	       
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
		         mSender = PendingIntent.getBroadcast(context, ComConstant.WIDGET_CALENDAR_REQ, intent, 0); 
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
//        Log.d(TAG, "onDeleted");

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
//        Log.d(TAG, "onDisabled");
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
	 * 위젯 화면 갱신 로직(달력)
	 */		
    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) 
    { 

		//위젯 클릭시 링크되는 class : main class link
        RemoteViews updateViews = new RemoteViews(context.getPackageName(),  R.layout.widget_calendar_4x4); 
        
        Intent intent = null;
        //ARM 로직적용(유료버전만)
        intent = new Intent(context, SMCalendar.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);	        
        updateViews.setOnClickPendingIntent(R.id.lin_main, pIntent);	

		//1. 위젯 테마 설정
        //widget style setting(초기화)
        ViewUtil.setWidgetStyleFromPreference(context);        
        mFontColor = context.getResources().getColor(ComConstant.WIDGET_FONTCOLOR);
		mBeforeFontColor = context.getResources().getColor(R.color.darkgray);
		if (Build.VERSION.SDK_INT >= 8) // android 2.1
		{
			updateViews.setInt(R.id.lin_main, "setBackgroundResource", ComConstant.WIDGET_BACK);
		}
        
        //2.당일일정 화면 만들기 (일일일정 Include)
        updateViews.removeAllViews(R.id.lin_section2);
        updateViews.removeAllViews(R.id.lin_datesection1);
        updateViews.removeAllViews(R.id.lin_datesection2);
        updateViews.removeAllViews(R.id.lin_datesection3);
        updateViews.removeAllViews(R.id.lin_datesection4);
        updateViews.removeAllViews(R.id.lin_datesection5);
        updateViews.removeAllViews(R.id.lin_datesection6);        
        
        //3.Top칸 (월정보)
        String today = SmDateUtil.getTodayDefault();
        String todayMM = SmDateUtil.getDateToStr(today, ComConstant.GUBUN_MONTH);
		updateViews.setTextColor(R.id.yyyy, 	mFontColor);
		updateViews.setTextColor(R.id.mm, 		mFontColor);
		updateViews.setTextColor(R.id.mmtxt, 	mFontColor);
        updateViews.setTextViewText(R.id.yyyy, 	SmDateUtil.getDateToStr(today, ComConstant.GUBUN_YEAR));
        updateViews.setTextViewText(R.id.mm, 	SmDateUtil.getDateToStr(today, ComConstant.GUBUN_MONTH));
        updateViews.setTextViewText(R.id.mmtxt, SmDateUtil.getMonthForEngFull(SmDateUtil.getDateToStr(today, ComConstant.GUBUN_MONTH)));
 
        //4.요일정보 update
        updateViews.setTextViewText(R.id.sunday, ComUtil.getStrResource(context, R.string.sunday));
        updateViews.setTextViewText(R.id.monday, ComUtil.getStrResource(context, R.string.monday));
        updateViews.setTextViewText(R.id.tuesday, ComUtil.getStrResource(context, R.string.tuesday));
        updateViews.setTextViewText(R.id.wednesday, ComUtil.getStrResource(context, R.string.wednesday));
        updateViews.setTextViewText(R.id.thursday, ComUtil.getStrResource(context, R.string.thursday));
        updateViews.setTextViewText(R.id.friday, ComUtil.getStrResource(context, R.string.friday));
        updateViews.setTextViewText(R.id.saturday, ComUtil.getStrResource(context, R.string.saturday));

		
        //데이터베이스 미존재시 skip
        if ( !ViewUtil.isdatabaseExist(context) ) {
        	return ;
        }
        
        //5.가로 7일, 세로 6칸  스케줄
		int vpos = 6;
		int hpos = 7;
		ArrayList<ArrayList<ScheduleInfo>> m_ScheduleList = new  ArrayList<ArrayList<ScheduleInfo>>();
		String [] cDate = setCalendarDate(context, vpos, hpos);	
//    	RemoteViews cellVertical= new RemoteViews(context.getPackageName(),  R.layout.widget_weekly_vertical);      
		
		m_ScheduleList = ViewUtil.getPeriodSchedule(context, cDate);
		
		if ( cDate == null || ( cDate != null && cDate.length != 42 )) {
			return;
		}
		
        for ( int i = 0 ; i < vpos ; i++ ) {
        	for ( int j = 0 ; j < hpos ; j++ ) {
        		RemoteViews cellViews 	= new RemoteViews(context.getPackageName(),  R.layout.widget_calendar_daycell);                		

        		String cellDate = cDate [ ( i * 7 ) + j ];
      		
            	cellViews = setUpdateView2(context, cellViews, cellDate, todayMM, m_ScheduleList.get(( i * 7 ) + j));

            	//cell design(오늘의 경우 색상변경)
            	if ( cellDate != null && cellDate.length() == 8 && cellDate.equals(today)) {
            		if (Build.VERSION.SDK_INT >= 8) // android 2.1
            		{
            			cellViews.setInt(R.id.total,  "setBackgroundResource", R.drawable.sm_widget_today);
            		}   
            	}

            	//add cell
            	if ( i == 0 ) {        		
            		updateViews.addView(R.id.lin_datesection1, cellViews);
            	} else if ( i == 1 ) { 
            		updateViews.addView(R.id.lin_datesection2, cellViews);
            	} else if ( i == 2 ) { 
            		updateViews.addView(R.id.lin_datesection3, cellViews);
            	} else if ( i == 3 ) { 
            		updateViews.addView(R.id.lin_datesection4, cellViews);
            	} else if ( i == 4 ) { 
            		updateViews.addView(R.id.lin_datesection5, cellViews);
            	} else {
            		updateViews.addView(R.id.lin_datesection6, cellViews);
            	}       	
            }
        	

        }
        
        appWidgetManager.updateAppWidget(appWidgetId, updateViews); 		      
    } 

	/*
	 * RemoteView에 데이터 뿌리는 부분
	 */
	private static RemoteViews setUpdateView2  ( Context context, RemoteViews updateViews, String date, String todayMM, ArrayList<ScheduleInfo> m_ScheduleList ) {
		
		StringBuffer buffer = new StringBuffer();		
		buffer.append(SmDateUtil.getDateFormatPerGubun(context, date, ComConstant.GUBUN_DAY, true, false));
		updateViews.setTextViewText(R.id.total, buffer.toString());	

//		int mBeforeDateColor = context.getResources().getColor(R.color.gray);
		
		if (Build.VERSION.SDK_INT >= 8) // android 2.1
		{
			updateViews.setInt(R.id.color_1, "setBackgroundColor", 0);
			updateViews.setInt(R.id.color_2, "setBackgroundColor", 0);
			updateViews.setInt(R.id.color_3, "setBackgroundColor", 0);
//			updateViews.setInt(R.id.color_4, "setBackgroundColor", 0);						
		} 
		
		String dateMM = SmDateUtil.getDateToStr(date, ComConstant.GUBUN_MONTH);
    	
    	//이전,이후달의 날짜의 경우 색상변경
    	if ( date != null && date.length() == 8 && !dateMM.equals(todayMM)) {
    		updateViews.setTextColor(R.id.total, 		mBeforeFontColor);  
    		updateViews.setTextColor(R.id.schedule_1, 	mBeforeFontColor);
    		updateViews.setTextColor(R.id.schedule_2, 	mBeforeFontColor);
    		updateViews.setTextColor(R.id.schedule_3, 	mBeforeFontColor);      		
    	} else {
    		updateViews.setTextColor(R.id.total, 		mFontColor);  
    		updateViews.setTextColor(R.id.schedule_1, 	mFontColor);
    		updateViews.setTextColor(R.id.schedule_2, 	mFontColor);
    		updateViews.setTextColor(R.id.schedule_3, 	mFontColor);  
//    		updateViews.setTextColor(R.id.schedule_4, 	mFontColor);    		
    	}

        updateViews.setTextViewText(R.id.schedule_1, "");
        updateViews.setTextViewText(R.id.schedule_2, "");
        updateViews.setTextViewText(R.id.schedule_3, "");
//        updateViews.setTextViewText(R.id.schedule_4, "");

        int len = m_ScheduleList.size();
        
        //데이터가 있는 경우만 처리
        if (  len > 0 ) {
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
//            		updateViews.setTextViewText(R.id.gubun_1, 		gubunStr);
            		updateViews.setTextViewText(R.id.schedule_1, 	scheduleStr);
            	} else if ( i == 1 ) {
            		if (Build.VERSION.SDK_INT >= 8) // android 2.1
            		{
            			updateViews.setInt(R.id.color_2, "setBackgroundColor", infoColor);
            		}	
//            		updateViews.setTextViewText(R.id.gubun_2, 		gubunStr);
            		updateViews.setTextViewText(R.id.schedule_2, 	scheduleStr);
            		
            	} else if ( i == 2 ) {
            		if (Build.VERSION.SDK_INT >= 8) // android 2.1
            		{
            			updateViews.setInt(R.id.color_3, "setBackgroundColor", infoColor);
            		}	
//            		updateViews.setTextViewText(R.id.gubun_3, 		gubunStr);
            		updateViews.setTextViewText(R.id.schedule_3,	scheduleStr);
//            	} else if ( i == 3 ) {
//            		if (Build.VERSION.SDK_INT >= 8) // android 2.1
//            		{
//            			updateViews.setInt(R.id.color_4, "setBackgroundColor", infoColor);
//            		}
////            		updateViews.setTextViewText(R.id.gubun_4, 		gubunStr);
//            		updateViews.setTextViewText(R.id.schedule_4,	scheduleStr);
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
	/// 달력을 구성하는 년 월 일을 셋팅하기 ( TO-DO  : 이전,다음월 정보도 포함한 형태로 출력)
	public String [] setCalendarDate( Context context, int vpos, int hpos )
	{
		int m_startPos = 0;
		int m_lastDay = 0;
		
		//해당칸에 대한 일자정보 보관	***중요
		String[ ] m_calFullDate = new String [ vpos * hpos ];		
//		Drawable pastDw = context.getResources().getDrawable(R.drawable.sm_premonth_back);


		/// 달력을 하나 복사해서 작업한다.
		Calendar iCal 		= (Calendar) Calendar.getInstance();
		Calendar iPreCal 	= (Calendar) iCal.clone( ) ;
		Calendar iNextCal 	= (Calendar) iCal.clone( ) ;
		
		/// 날짜를 1로 셋팅하여 달의 1일이 무슨 요일인지 구함
		iCal.set( Calendar.DATE, 1 ) ;
		m_startPos = iCal.get( Calendar.DAY_OF_WEEK ) - Calendar.SUNDAY ;
		
		/// 1달 더해서 다음달 1일로 만들었다가 1일을 빼면 달의 마지막날이 구해짐
		iCal.add( Calendar.MONTH, 1 ) ;
		iCal.add( Calendar.DATE, -1 ) ;
		
        m_lastDay = iCal.get( Calendar.DAY_OF_MONTH ) ;         /// 해달 달의 마지막날 겟~  
        
		/// 7번부터 처음 시작위치 전까지는 공백으로 채움
		//--> 이전달정보로 변경
		int pre_startday = 0;

        if (  m_startPos > 0 ) {
        	//이전달 마지막일(1일로 set -> 1일 빼기)
        	iPreCal.set( Calendar.DATE, 1 ) ;
        	iPreCal.add( Calendar.DATE, -1 ) ;
        	int pre_lastday = iPreCal.get( Calendar.DAY_OF_MONTH ) ;
        	pre_startday 	= pre_lastday - iPreCal.get( Calendar.DAY_OF_WEEK )  + Calendar.SUNDAY;
        } 
        
		for( int i = 0 ; i < m_startPos ; i++ )
		{

			String year  = ComUtil.fillSpaceToZero ( iPreCal.get( Calendar.YEAR ), 4 );
			String month = ComUtil.fillSpaceToZero ( iPreCal.get( Calendar.MONTH ) + 1 , 2);
			String predate = pre_startday + i + "" ;
			m_calFullDate [ i ] = year + month + ComUtil.fillSpaceToZero( predate, 2 );	
			
		}

		/// 시작위치부터는 1부터 해서 달의 마지막날까지 숫자로 채움
		String thisyear  = ComUtil.fillSpaceToZero(iCal.get( Calendar.YEAR ), 4)  ; 
		String thismonth = ComUtil.fillSpaceToZero(iCal.get( Calendar.MONTH ) + 1, 2)  ; 
		for( int i = 0 ; i < m_lastDay ; i++ )
		{
			int pos = i + m_startPos;
			String day = ( i + 1 ) + "";
			m_calFullDate [ pos ] = thisyear + thismonth + ComUtil.fillSpaceToZero( day, 2 );
		
		} 	
		
		/// 마지막날부터 끝까지는 공백으로 채움
		//-> 다음달 정보로 변경
		int next_startday = 0;

        if (  m_startPos + m_lastDay < 42 ) {
        	//이전달 마지막일(1일로 set -> 1일 빼기)
        	iNextCal.set( Calendar.DATE, 1 ) ;
        	iNextCal.add( Calendar.MONTH, 1 ) ;
        	next_startday 	= iNextCal.get( Calendar.DAY_OF_MONTH ) ;
        } 	

		for( int i = m_startPos + m_lastDay ; i < 42 ; i++ )
		{   
			if ( next_startday == 0 ) {
				m_calFullDate[ i ] = "";	
			} else {
				String year  = ComUtil.fillSpaceToZero ( iNextCal.get( Calendar.YEAR ), 4 );
				String month = ComUtil.fillSpaceToZero ( iNextCal.get( Calendar.MONTH ) + 1 , 2);
				String nextdate = next_startday + i -  ( m_startPos + m_lastDay ) + "";
				m_calFullDate[ i ] = year + month + ComUtil.fillSpaceToZero( nextdate, 2 );	
			}			
		}
		
		return m_calFullDate;
	}
	
}


