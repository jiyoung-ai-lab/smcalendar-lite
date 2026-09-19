package com.waveapp.smcalendarlite.handler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.TodoMemoDbAdaper;
import com.waveapp.smcalendarlite.service.SmAlarm_Service;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/*
 * 알람서비스(3가지 관리)
 * 1.일정알람
 * 2.기념일알람
 * 3.할일알람
 */
public class AlarmHandler  extends SMActivity {
	
	private final Context mCtx;
	private Calendar iCal;	
	
	String mAlarmTitle;
	String mAlarmSubName;
	
	
	public AlarmHandler(Context ctx) {
        this.mCtx = ctx;
    }
    @Override
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
    /*
     * 일정에서 등록된 알람 서비스 기동(시작시간기준알람, 종료시간기준알람 둘다 체크한뒤 처리)
     */
    public void setAlarmService( ) {
    	
    	iCal 			= null;
    	mAlarmTitle 	= "";
    	mAlarmSubName 	= "";
    	
        String fromdate = SmDateUtil.getTodayDefault();
        
        //데이터베이스 미존재시 skip(변경데이터베이스반영도 가능하게 처리)
        if ( !ViewUtil.isdatabaseExist(mCtx) ) {
        	return ;
        }
        
        //현재일 기준 유효한 스케줄중 알람정보 있는건 가져오기
        ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(mCtx);
        mDbHelper.open();
        Cursor cur = mDbHelper.fetchAlarmLatest(fromdate);
        
		//loop
		int total = cur.getCount();
		
		for(int i = 0 ; i < total ; i++) {
			
			String schedulename = cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_NAME));
			String cycle 		= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_CYCLE));
			String dbstartdate 	= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTDATE));
			String dbenddate 	= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDDATE));
			String dbstarttime 	= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTTIME));
			String dbendtime 	= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDTIME));
			String alarm 		= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM));
			String alarm2 		= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM2));
			String repeatdate	= cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_REPEATDATE));
			
			//요일별 value set
			int[] arrDayofweek = new int [7];				
			arrDayofweek[0] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SUN)), ComConstant.DAYOFWEEK_SUNDAY);
			arrDayofweek[1] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_MON)), ComConstant.DAYOFWEEK_MONDAY);
			arrDayofweek[2] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_TUE)), ComConstant.DAYOFWEEK_TUESDAY);
			arrDayofweek[3] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_WEN)), ComConstant.DAYOFWEEK_WEDNESDAY);
			arrDayofweek[4] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_THU)), ComConstant.DAYOFWEEK_THURSDAY);
			arrDayofweek[5] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_FRI)), ComConstant.DAYOFWEEK_FRIDAY);
			arrDayofweek[6] = ComUtil.setYesReturnValue(
					cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SAT)), ComConstant.DAYOFWEEK_SATURDAY);

			//매주  : 요일 체크
			//없음  : 기간전부
			//현재일 현재시간 기준으로 가장 가까운 시간에 알람 정보 가져오기
			//시작알람 처리후 종료알람과 비교 처리
			Calendar  latestDate  = null;
			//1.시작알람
			Calendar startLatestDate = null ;			
			if ( alarm != null && !alarm.trim().equals("")) {
				if ( cycle  != null && cycle.equals("Y")) {
					startLatestDate = SmDateUtil.getLatestDateFromDayOfWeek(arrDayofweek, fromdate, dbstartdate, dbenddate, dbstarttime, alarm );
				} else if ( cycle  != null && cycle.equals("M")) {
					startLatestDate = SmDateUtil.getLatestDateFromMonth(repeatdate, fromdate, dbstartdate, dbenddate, dbstarttime, alarm );
				} else {
					startLatestDate = SmDateUtil.getLatestDateFromDate(fromdate, dbstartdate, dbenddate,  dbstarttime, alarm);
				}				
			}

			//2.종료알람
			Calendar endLatestDate = null;
			if ( alarm2 != null && !alarm2.trim().equals("")) {
				if ( cycle  != null && cycle.equals("Y")) {
					endLatestDate = SmDateUtil.getLatestDateFromDayOfWeek(arrDayofweek, fromdate, dbstartdate, dbenddate, dbendtime, alarm2 );
				} else if ( cycle  != null && cycle.equals("M")) {
					endLatestDate = SmDateUtil.getLatestDateFromMonth(repeatdate, fromdate, dbstartdate, dbenddate, dbendtime, alarm2 );
				} else {
					endLatestDate = SmDateUtil.getLatestDateFromDate(fromdate, dbstartdate, dbenddate,  dbendtime, alarm2);
				}				
			}
			
			//3.시작,종료알람 비교
			String finalAlarm = "";
			if ( startLatestDate != null && endLatestDate != null ){
				if  ( startLatestDate.after(endLatestDate)) {
					latestDate = endLatestDate;
					finalAlarm = alarm2;
				} else {
					latestDate = startLatestDate;
					finalAlarm = alarm;
				}				
			} else if ( startLatestDate == null && endLatestDate != null ){
				latestDate = endLatestDate;
				finalAlarm = alarm2;
			} else if ( startLatestDate != null && endLatestDate == null ){
				latestDate = startLatestDate;
				finalAlarm = alarm;
			}
			
			
			if ( latestDate != null && ( iCal == null || ( iCal.after(latestDate)))) {
				iCal  			=  latestDate;
				mAlarmTitle 	= schedulename;
				//알람표기변경 : 시작시간 -> 시작시간(알람기준)
				Calendar tempCal = (Calendar) iCal.clone();				
				int alarmmin = ComUtil.stringToInt(finalAlarm);
				tempCal.add(Calendar.MINUTE, alarmmin);
				
				String date = SmDateUtil.getDateFromCal(tempCal);
				//오늘과 같은일자일 경우 날짜가 아닌 today로 표기
				if ( SmDateUtil.getTodayDefault().equals(date)) {
					date = ComUtil.getStrResource(mCtx, R.string.today);
				} else {
 					if ( date != null && date.length() == 8 ) {
 						date = SmDateUtil.getDateSimpleFormat(mCtx, date.substring(4), "/", true);
 					}					
				}
				mAlarmSubName 	= date 	+ " " 
										+ SmDateUtil.getTimeFullFormat( mCtx, dbstarttime)
										+ "~" 
										+ SmDateUtil.getTimeFullFormat( mCtx, dbendtime);
				
			}
			
			cur.moveToNext();
		}
		
		if ( cur != null ) cur.close();
		mDbHelper.close();
		
		//가져온 최신 스케줄 날짜 정보를 토대로 알람서비스 시작
		//단, 기존서비스와 비교해서 기존서비스가 앞선경우 skip (우선 기존서비스 무조건 stop)

		if ( iCal != null ) {
			
			stopAlarm ( ComConstant.SCHEDULE_GUBUN , ComConstant.ALARM_REQ );
			startAlarm( ComConstant.SCHEDULE_GUBUN , ComConstant.ALARM_REQ );
		} else {
			stopAlarm ( ComConstant.SCHEDULE_GUBUN , ComConstant.ALARM_REQ);
		}
		//return iCal;
    }	
    /*
     * 할일에서 등록된 알람 서비스 기동
     */
    public void setAlarmServiceForTodo( ) {

    	iCal 			= null;
    	mAlarmTitle 	= "";
    	mAlarmSubName 	= "";
    	
        String fromdate = SmDateUtil.getTodayDefault();
        //데이터베이스 미존재시 skip(변경데이터베이스반영도 가능하게 처리)
        if ( !ViewUtil.isdatabaseExist(mCtx) ) {
        	return ;
        }       
        //현재일 기준 유효한 할일정보중 알람정보 있는건 가져오기
        TodoMemoDbAdaper mTodoDbHelper = new TodoMemoDbAdaper(mCtx);
        mTodoDbHelper.open();
        Cursor cur = mTodoDbHelper.fetchAlarmLatest(fromdate);
        
		//loop
		int total = cur.getCount();
		
		for(int i = 0 ; i < total ; i++) {
			
			String memo 		= cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_MEMO));
//			String confirmdate 	= cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_CONFIRMDATE));
			String finishterm 	= cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_FINISHTERM));
			String alarm 		= cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_ALARM));

			//매월반복일정의 경우 현재일 기준 최근일자 정보 가져오기
			//반복이 아닌경우 종료예정일 기준으로 처리
			Calendar  latestDate = SmDateUtil.getLatestDateFromDate( finishterm, "", "", ComConstant.ALARM_DEFAULTTIME, alarm);
			
			if ( latestDate != null && ( iCal == null || ( iCal.after(latestDate)))) {
				iCal  			= latestDate;
				mAlarmTitle 	= memo;
				//알람표기변경 : 시작시간 -> 시작시간(알람기준)
				Calendar tempCal = (Calendar) iCal.clone();
				int alarmmin = Integer.parseInt(alarm);
				tempCal.add(Calendar.MINUTE, alarmmin);
				
				String date = SmDateUtil.getDateFromCal(tempCal);
				//오늘과 같은일자일 경우 날짜가 아닌 today로 표기
				if ( SmDateUtil.getTodayDefault().equals(date)) {
					date = ComUtil.getStrResource(mCtx, R.string.today);
				} else {
 					if ( date != null && date.length() == 8 ) {
 						date = SmDateUtil.getDateSimpleFormat(mCtx, date.substring(4), "/", true);
 					}					
				}
				mAlarmSubName 	=  ComUtil.getStrResource(mCtx, R.string.todo) 
								+ ": "								
								+  date 
								+ "(" 
								+ SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(date))
								+ ")" ; 
			}
			
			cur.moveToNext();
		}
		
		if ( cur != null ) cur.close();
		mTodoDbHelper.close();
		
		//가져온 최신 스케줄 날짜 정보를 토대로 알람서비스 시작
		//단, 기존서비스와 비교해서 기존서비스가 앞선경우 skip (우선 기존서비스 무조건 stop)

		if ( iCal != null ) {
			
			stopAlarm(  ComConstant.TODO_GUBUN  , ComConstant.ALARM_TODO_REQ);
			startAlarm( ComConstant.TODO_GUBUN  , ComConstant.ALARM_TODO_REQ);
		} else {
			stopAlarm( ComConstant.TODO_GUBUN , ComConstant.ALARM_TODO_REQ );
		}
    }	
   
    /*
     * 기념일에서 등록된 알람 서비스 기동(7일)
     * -.기념일의 경우 양력,음력(평달,윤달)정보가 혼재함  >-> 별도 process로 처리
     * -.예외적으로 알람설정시 년도가 바뀌는 경우가 발생 처리에 혼선이 있어 현재일~-8일 정보를 건건히 조회하는 방식으로 처리
     */
    public void setAlarmServiceForSpecialday( ) {
    	iCal 			= null;
    	mAlarmTitle 	= "";
    	mAlarmSubName 	= "";
    	
    	//오늘 일자 가져오기
        Calendar iCalSpe = Calendar.getInstance();
        
        //데이터베이스 미존재시 skip(변경데이터베이스반영도 가능하게 처리)
        if ( !ViewUtil.isdatabaseExist(mCtx) ) {
        	return ;
        }       
        SpecialDayDbAdaper mSpeDbHelper = new SpecialDayDbAdaper(mCtx);
        mSpeDbHelper.open();	
        
        //날짜별로 조회후 처리하기 때문에 fromdate로 처리하면 됨..
 		for ( int i = 0; i < 8 ; i++) {
 			//기준일자 가져오기
 			String fromdate = SmDateUtil.getDateFromCal(iCalSpe);
 			
 	        //현재일 기준 유효한 할일정보중 알람정보 있는건 가져오기
 			//1.양력
 	        Cursor cur = mSpeDbHelper.fetchAlarmForSolar(ComConstant.PUT_USER, 
 	        		fromdate.substring(0,4), fromdate.substring(4));
 			//loop
 			int total = cur.getCount(); 			
 			for(int j = 0 ; j < total ; j++) {
 				
 				String name 	= cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME));
 				String alarm 	= cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ALARM));

 				//매월반복일정의 경우 현재일 기준 최근일자 정보 가져오기
 				//반복이 아닌경우 종료예정일 기준으로 처리
 				Calendar  latestDate  ;
 				latestDate = SmDateUtil.getLatestDateFromDate(fromdate, "", "",  ComConstant.ALARM_DEFAULTTIME, alarm);
 				
 				if ( latestDate != null && ( iCal == null || ( iCal.after(latestDate)))) {
 					iCal  			= latestDate;
 					mAlarmTitle 	= name;
 					//알람표기변경 : 시작시간 -> 시작시간(알람기준)
 					Calendar tempCal = (Calendar) iCal.clone();
 					int alarmmin = Integer.parseInt(alarm);
 					tempCal.add(Calendar.MINUTE, alarmmin);
 					
 					String date = SmDateUtil.getDateFromCal(tempCal);
 					//오늘과 같은일자일 경우 날짜가 아닌 today로 표기
 					if ( SmDateUtil.getTodayDefault().equals(date)) {
 						date = ComUtil.getStrResource(mCtx, R.string.today);
 					} else {
 	 					if ( date != null && date.length() == 8 ) {
 	 						date = SmDateUtil.getDateSimpleFormat(mCtx, date.substring(4), "/", true);
 	 					}					
 					}
 					
 					mAlarmSubName 	=  ComUtil.getStrResource(mCtx, R.string.specialday) 
									+ ": "								
									+  date 
									+ "(" 
									+ SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(date))
									+ ")" ; 
 					
 				}
 				
 				cur.moveToNext();
 			}
 			
 			if ( cur != null ) cur.close();

 			//2.음력
 	        //음력일자 가져오기
 	        LunarDataDbAdaper mLunarDbHelper = new LunarDataDbAdaper(mCtx);
 	        mLunarDbHelper.open();
 			String lunar[] = LunarDataDbAdaper.getSolarToLunar(mCtx, mLunarDbHelper, fromdate);
 			mLunarDbHelper.close();
 			
 			String lunardate = "";
 			String leap = "";
 			if ( lunar[0] != null && lunar[0].length() == 8 ) {
 				lunardate = lunar[0];
 				leap = lunar[1];
 	 	        cur = mSpeDbHelper.fetchAlarmForLunar(ComConstant.PUT_USER, 
 	 	        		lunardate.substring(0,4), lunardate.substring(4), leap); 				
 			}
 	        
 			//loop
 			total = cur.getCount(); 			
 			for(int j = 0 ; j < total ; j++) {
 				
 				String name 		= cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME));
 				String alarm 		= cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ALARM));
 				
 				//음력기준일의 경우 날짜 연산시 양력일자로 변환후 처리
 				Calendar  latestDate  ;
 				latestDate = SmDateUtil.getLatestDateFromDate(fromdate, "", "",  ComConstant.ALARM_DEFAULTTIME, alarm);
 				
 				if ( latestDate != null && ( iCal == null || ( iCal.after(latestDate)))) {
 					iCal  			= latestDate;
 					mAlarmTitle 	= name;
 					//알람표기변경 : 시작시간 -> 시작시간(알람기준)
 					Calendar tempCal = (Calendar) iCal.clone();
 					int alarmmin = Integer.parseInt(alarm);
 					tempCal.add(Calendar.MINUTE, alarmmin);
 					
 					String date = SmDateUtil.getDateFromCal(tempCal);
 					//오늘과 같은일자일 경우 날짜가 아닌 today로 표기
 					if ( SmDateUtil.getTodayDefault().equals(date)) {
 						date = ComUtil.getStrResource(mCtx, R.string.today);
 					} else {
 	 					if ( date != null && date.length() == 8 ) {
 	 						date = SmDateUtil.getDateSimpleFormat(mCtx, date.substring(4), "/", true);
 	 					}					
 					}
 					
 					mAlarmSubName 	= date 	+ "(" 
 											+ SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(date))
 											+ ")" ;
 					
 				}
 				
 				cur.moveToNext();
 			}
 			
 			if ( cur != null ) cur.close();
 			
 	        			
			///////////////////
 			iCalSpe.add(Calendar.DAY_OF_MONTH, 1 ) ;
 		}
 		
		mSpeDbHelper.close();	
		
		/////////////////////////////////////////////////////
		//가져온 최신 스케줄 날짜 정보를 토대로 알람서비스 시작
		//단, 기존서비스와 비교해서 기존서비스가 앞선경우 skip (우선 기존서비스 무조건 stop)

		if ( iCal != null ) {
			
			stopAlarm(  ComConstant.SPECIAL_GUBUN , ComConstant.ALARM_SPC_REQ);
			startAlarm( ComConstant.SPECIAL_GUBUN , ComConstant.ALARM_SPC_REQ );
		} else {
			stopAlarm(  ComConstant.SPECIAL_GUBUN , ComConstant.ALARM_SPC_REQ);
		}
		
    }	
   ////////////////////////////////////////////////////////////////////
    
   private void startAlarm( String alarmgubun, int alarmreq ) {

    	long starttime = iCal.getTimeInMillis(); 

    	Bundle bundle = new Bundle();
    	bundle.putString(ComConstant.ALARM_TITLE, 	mAlarmTitle);
    	bundle.putString(ComConstant.ALARM_SUBNAME, mAlarmSubName);
    	bundle.putString(ComConstant.ALARM_GUBUN, 	alarmgubun); 
    	
    	Intent intent = new Intent( mCtx,  SmAlarm_Service.class );
    	intent.putExtras(bundle);   
    	
    	PendingIntent mAlarmSender = PendingIntent.getService(mCtx, alarmreq, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
    	//Alarm Service Start
        AlarmManager am = (AlarmManager)mCtx.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, starttime, mAlarmSender);
        
//        Log.w(">>>>>>>>>>>>>>>>>>>>>>", "startAlarm : " + ComConstant.ALARM_GUBUN);
    }

	private void stopAlarm( String alarmgubun, int alarmreq ) {

    	Bundle bundle = new Bundle();
    	bundle.putString(ComConstant.ALARM_TITLE, mAlarmTitle);
    	bundle.putString(ComConstant.ALARM_SUBNAME, mAlarmSubName);
    	bundle.putString(ComConstant.ALARM_GUBUN, alarmgubun);

    	Intent intent = new Intent( mCtx,  SmAlarm_Service.class   );
    	intent.putExtras(bundle);     	
  
    	PendingIntent mAlarmSender = PendingIntent.getService(mCtx, alarmreq, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)mCtx.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if ( mAlarmSender != null ) am.cancel(mAlarmSender);
        
//        Log.w(">>>>>>>>>>>>>>>>>>>>>>", "stopAlarm : " + ComConstant.ALARM_GUBUN);

    }

}
