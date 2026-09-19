package com.waveapp.smcalendarlite.handler;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.GoogleCalendarInfo;
import com.waveapp.smcalendarlite.info.PhoneCalendarInfo;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.link.GoogleRetrieve;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 핸드폰에 내장된 일정 가져오기/내보내기
 * -.포맷이 너무 상이해서 어떻게 처리할지 고민이 필요한 부분... 
 * (2.1 : deleted colum 없음)
 */
public class OtherCalendarHandler extends SMActivity{
	
	private Context mCtx;
	private int mValidCnt;
	
	private ContentResolver cr;
	private Uri CALENDAR_URI;
	private Uri CALENDAR_ALERT_URI;
	private Uri CALENDAR_REMINDER_URI;
	
	static String cFREQ 	= "FREQ";
	static String cUNTIL 	= "UNTIL";
	static String cBYDAY 	= "BYDAY";
	static String cINTERVAL = "INTERVAL";
	static String cWKST 	= "WKST";
	static String cBYMONTH 	= "BYMONTH";
	static String cBYMONTHDAY 	= "BYMONTHDAY";
	
	
	static String cDAILY 	= "DAILY";
	static String cWEEKLY 	= "WEEKLY";	
	static String cMONTHLY 	= "MONTHLY";	
	static String cYEARLY 	= "YEARLY";	
//    public CalHandler(Context ctx) {
//        this.mCtx = ctx;        
//    }
    public OtherCalendarHandler( Context ctx ) {
        this.mCtx = ctx; 
        this.cr = mCtx.getContentResolver();

        if (Build.VERSION.SDK_INT <= 7) // android 2.1
        {
            CALENDAR_URI = Uri.parse("content://calendar/events");
            CALENDAR_ALERT_URI = Uri
                    .parse("content://calendar/calendar_alerts");
            CALENDAR_REMINDER_URI = Uri
                    .parse("content://calendar/reminders");
        } else if (Build.VERSION.SDK_INT >= 8) //android 2.2, 2.3
        {
            CALENDAR_URI = Uri
                    .parse("content://com.android.calendar/events");
            CALENDAR_ALERT_URI = Uri
                    .parse("content://com.android.calendar/calendar_alerts");
            CALENDAR_REMINDER_URI = Uri
                    .parse("content://com.android.calendar/reminders");
        }        
    }
    
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
 
	}

	public  final ArrayList<ScheduleInfo> getAllScheduelFromPhone () {
	       
		ArrayList<ScheduleInfo> schArr = new ArrayList<ScheduleInfo>();
		String[] projection ;	
		String[] date = new String[2];
		mValidCnt = 0;
		
		Cursor cur = null;
		
//        ContentResolver content = mCtx.getContentResolver();
		//deletd colume은 2.2이상만 있음... ㅋ
		 if (Build.VERSION.SDK_INT <= 7) // android 2.1	
		 {
			 projection = PhoneCalendarInfo.projectionForOld;	
			 cur = cr.query(CALENDAR_URI, projection, null, null, null);
		 } else {
			 projection = PhoneCalendarInfo.projection;	
			 cur = cr.query(CALENDAR_URI, projection, PhoneCalendarInfo.KEY_DELETED + "=0", null, null);
		 }
        

        if ( cur != null ) {
	        cur.moveToFirst();
	        Calendar iCal = Calendar.getInstance();
	        
	        //단말기내 달력정보 종류 : idx(0)  =>  1:내달력 2:phone명절 3:내달력 4:구글달력
	        //날짜는 calendar로 전환후 환산
	        int len = cur.getCount();
	        for ( int i = 0 ; i < len ; i++ ) { 
	        	int id 			= cur.getInt(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_ID));
	        	int calendar_id = cur.getInt(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_CALENDAR_ID));
	        	int eventStatus = cur.getInt(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_EVENT_STATUS));
//	        	int deleted 	= cur.getInt(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_DELETED));
	        	
	        	String title 	= cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_TITLE));
	        	String duration = cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_DURATION));
	        	String dtstart 	= cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_DTSTART));
	        	String dtend 	= cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_DTEND));	        	
	        	String alldayyn = cvtValueToYes(cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_ALLDAY)));
	        	String alarm 	= cvtValueToYes(cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_HAS_ALARM)));
	        	String lastdate	= cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_LAST_DATE));	  
	        	String organizer= cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_ORGANIZER));	        	
	        	//	        	int id = cur.getInt(31); 
//	        	int calendar_id = cur.getInt(0); 
//	        	int eventStatus = cur.getInt(5);
//	        	String duration = cur.getString(11);
//	        	int deleted 	= cur.getInt(30); 
  
////	        	폰에 따라 다름... (갤.: 1:내달력 2:phone명절 3:내달력 4:구글달력 ) organizer parsing해서 처리 (holiday있으면 skip)
//   	        	for ( int j = 0 ; j < projection.length ; j++ ) { 
//	        		Log.w("Calendar", "CNT" + ComUtil.intToString(j)  + " : "  + cur.getInt(j) + " : "  + cur.getString(j)); 
//	        	}  
	        	
	        	if ( calendar_id == 1 || calendar_id == 3 
	        			||( calendar_id == 2 && eventStatus == 0 && (organizer != null  && !organizer.trim().equals("")) ) 
	        			||( calendar_id == 4 && eventStatus != 1 )) {
//	        		
	        		//0:확정되지않음 1:확정됨2:취소됨
	        		if (  ( eventStatus == 0 ||  eventStatus == 1 ) 
	        				 && ( organizer == null || 
	        					  (organizer != null  && organizer.trim().equals("")) || !ComUtil.isInStr("holiday", organizer)) ){
	        				
//	        			Log.w("Calendar", ">>>>>>>>>>>>>>>>>>title" + " : "  + title); 
//	        			Log.w("Calendar", ">>>>>>>>>>>>>>>>>>organizer" + " : "  + organizer); 
//	        			Log.w("Calendar", ">>>>>>>>>>>>>>>>>>ComUtil.isInStr(organizer)" + " : "  + Boolean.toString(ComUtil.isInStr(organizer, "holiday"))); 
	        			
	 
	    	        	
			        	ScheduleInfo sch = new ScheduleInfo();
			        	sch.setOtherId(id);
			        	sch.setOtherkind(ComConstant.CALCHOICE_GUBUN);
			        	if ( title == null || ( title != null && title.trim().equals(""))) {
			        		title = ComUtil.getStrResource(mCtx, R.string.isempty);
			        	}
			        	sch.setScheduleName(title);	
			        	sch.setAllDayYn(alldayyn);
			        	
			        	//default
			        	sch.setAlarmYn("");
			        	
			        	if ( alarm != null && alarm.equals("Y")){
			        	    Cursor alarmcur = cr.query(CALENDAR_REMINDER_URI
			        	    					,new String[] 	{   "event_id",                            
			    		        									"method",                                
			    		        									"minutes"
			        	    									}	  
			        	    					, "event_id=" + id , null, null);
			        	    
			        	    if ( alarmcur != null ) {			        	    	
				        	    alarmcur.moveToFirst();
				        	    int alarmlen = alarmcur.getCount();	
				        	    if ( alarmlen > 0 ) {
				        	    	sch.setAlarmYn(setAlarmMinute( mCtx, alarmcur.getInt(2)));
				        	    }
			        	    }
			        	    
			        	    alarmcur.close();
			        	       			        		
			        	}
			        	
	 		        	//시작일자,시간
			        	date = cvtDateTimeFromMili(iCal, dtstart);
			        	sch.setStartDate(date[0]);
			        	sch.setStartTime(date[1]);
			        	
			        	String rrule = cur.getString(cur.getColumnIndexOrThrow(PhoneCalendarInfo.KEY_RRULE));
			        	String rule [] = getFreqFromRRule(rrule);

				        	
			        	//예외처리 : 경우에 따라 종료일자,시간이 없는 경우나 다른곳에 저장됨.		
			        	//종료시간만 최종일자정보에서 가져옴(달력구조상 실질적인 종료일은 기간rule정보에 있음				      
			        	String tempdtdata = dtend;
			        	
		        		if ( tempdtdata == null )  tempdtdata = lastdate;
		        		if ( tempdtdata == null )  {
		        			if ( duration != null ) {
				                long start = Long.parseLong(dtstart);	
			        			
				                if ( alldayyn != null && alldayyn.equals("Y")) {
				        			int spos = duration.indexOf("P", 0);
				        			int epos = duration.indexOf("D", 0);
				        			long days = Long.parseLong(duration.substring(spos + 1,epos));
				        			
//				                    long days = (end - start + DateUtils.DAY_IN_MILLIS - 1)
//				                            / DateUtils.DAY_IN_MILLIS;
//				                    duration = "P" + days + "D";
				                    long end = ( days * DateUtils.DAY_IN_MILLIS ) - DateUtils.DAY_IN_MILLIS + 1 + start;
				                    tempdtdata = Long.toString(end);	
//				                    Log.w("Calendar", "tempdtdata:  " + Long.toString(end)); 
				                } else {
				        			int spos = duration.indexOf("P", 0);
				        			int epos = duration.indexOf("S", 0);
				        			long seconds = 0;
				        			if ( spos > 0 && epos > 0 ) {
				        				seconds = Long.parseLong(duration.substring(spos+ 1,epos));
				        			}
//				                    long seconds = (end - start) / DateUtils.SECOND_IN_MILLIS;
//				                    duration = "P" + seconds + "S";
				        			long end = (seconds * DateUtils.SECOND_IN_MILLIS) + start;
				        			tempdtdata = Long.toString(end);
				        			
				        			
//				        			Log.w("Calendar", "duration:  " + duration);
//				        			Log.w("Calendar", "days:  " + Long.toString(seconds));
//				        			Log.w("Calendar", "tempdtdata:  " + Long.toString(end)); 
				        			
				                }	
		        			}
		        		}
			    	        	
		        		//반복여부
			        	if ( rule[0] != null && rule[0].equals(cDAILY)){
			        		sch.setCycle("");
			        		sch.setDescription(getDescription(rule));

			        	} else if ( rule[0] != null && rule[0].equals(cWEEKLY)){
			        		sch.setCycle("Y");
			        		if ( rule[3] != null && ComUtil.stringToInt( rule[3] ) > 1 ){
			        			sch.setCangetyn("N");
			  				} 
			        		sch.setDescription(getDescription(rule));
			        		//주정보
			        		if (rule[2] != null ) {
			        			sch.setDayOfWeek(cvtDayOfWeekToCode(rule[2]));	
			        		}
			        	} else if ( rule[0] == null ) {
			        		//예외적으로 내달력의 경우 rule정보가 없는 경우가 있음	
			        		sch.setCycle("");

			        	} else {
			        		//SMCalenar에 반영하기 힘든 항목들...					        		
			        		sch.setCycle("Y");
			        		sch.setCangetyn("N");
			        		sch.setDescription(getDescription(rule));					        		
			        	}
		        		
		        		//종료일자,시간
			        	date = cvtDateTimeFromMili(iCal, tempdtdata);
//					    sch.setEndDate(date[0]);
			        	//종료일자의 경우 rule에 있는정보가 우선 없는 경우 dtend를 기준으로 함.
			        	if (rule[1] != null ) {
			        		sch.setEndDate(rule[1].substring(0, 8));
			        	} else {
			        		sch.setEndDate(date[0]);
			        	}
			        	
			        	if (date[1] != null ) {
			        		sch.setEndTime(date[1]);
			        	}			        	
			        	//유효건수
			        	if ( sch.getCangetyn() == null || 
			        			(sch.getCangetyn() != null && sch.getCangetyn().equals(""))) {
			        		mValidCnt++;
			        	}
			        	
			        	schArr.add(sch);
	        		}
	        	}
	        	cur.moveToNext();
	        	
	        }
        }
        
        if ( cur != null ) cur.close();		
        
        return schArr;
	}
	public final int getValidCnt(){
		return mValidCnt;
	}
	public  final ArrayList<ScheduleInfo> getAllScheduelFromGoogle () {
	       
		ArrayList<GoogleCalendarInfo> googleArr = new ArrayList<GoogleCalendarInfo>();
		ArrayList<ScheduleInfo> schArr = new ArrayList<ScheduleInfo>();
		
    	GoogleRetrieve retrieve = new GoogleRetrieve();
    	
    	if ( retrieve.GoogleCalendarRetrieve() ) {
    		googleArr = retrieve.getmList();
    	}

        //단말기내 달력정보 종류 : idx(0)  =>  1:내달력 2:phone명절 3:내달력 4:구글달
        //날짜는 calendar로 전환후 환산
        int len = googleArr.size();
        for ( int i = 0 ; i < len ; i++ ) {	        	
        	ScheduleInfo sch = new ScheduleInfo();
        	GoogleCalendarInfo gcal = new GoogleCalendarInfo();
        	gcal = googleArr.get(i);
        	sch.setScheduleName(gcal.getTitle());
        	//시작일자,시간
        	sch.setStartDate(gcal.getWhen());
        	sch.setEndDate(gcal.getWhen());
        	sch.setStartTime(gcal.getStartTime());
        	sch.setEndTime(gcal.getEndTime());
        	
        	schArr.add(sch);

        	
        }	
        
        return schArr;
	}		
	/*
	 * 반복일정 정보 parsing ( rrule -> schedule )
	 */
    private String[] getFreqFromRRule( String rrule ) {
    	
    	String[] retArr = new String[6];
    	
    	//rrule 이 없는 경우도 있음.(이건 뭥미)
    	if ( rrule == null || ( rrule != null && rrule.trim().equals(""))) return retArr;
    	
    	
    	String sep = ";";
    	String valuesep = "=";
    	
    	StringBuffer buffer = new StringBuffer();    	
    	buffer.append(rrule);
    	
    	int pos = 0 ;
    	String find = "";
    	int len = retArr.length;
    	for ( int i = 0 ; i < len ; i++) {
    		
    		int findpos = buffer.indexOf(sep, pos);
    		if ( findpos == -1 ) {
    			find = buffer.substring(pos).toString();

    		} else {
    			find = buffer.substring(pos, findpos).toString();
    		}  

			int valuepos = find.indexOf(valuesep, 0);
			if ( find.substring( 0, valuepos ).equals(cFREQ) ) {
				retArr[0] = find.substring(valuepos+1);
			} else if ( find.substring( 0, valuepos ).equals(cUNTIL) ) {
				retArr[1] = find.substring(valuepos+1);
			} else if ( find.substring( 0, valuepos ).equals(cBYDAY) ) {
				retArr[2] = find.substring(valuepos+1);
			} else if ( find.substring( 0, valuepos ).equals(cINTERVAL) ) {
				retArr[3] = find.substring(valuepos+1);
			} else if ( find.substring( 0, valuepos ).equals(cBYMONTHDAY) ) {
				retArr[4] = find.substring(valuepos+1);
			} else if ( find.substring( 0, valuepos ).equals(cBYMONTH) ) {
				retArr[5] = find.substring(valuepos+1);
			}
			
			if ( findpos == -1 ) break;
			
    		pos = findpos + 1;
    	}
    	
    	return retArr;
    	
    }
    /*
     * 상세반복정보 description 생성
     */
    private String getDescription ( String rrule [] ) {
    	
    	StringBuffer buffer = new StringBuffer();    	
    	
    	//반복
    	if ( rrule[0] != null && !rrule[0].equals("") ) {
    		String cycletxt = "";
    		
    		if (  rrule[0] != null && rrule[0].equals(cMONTHLY)){				//1) 월단위
    			cycletxt = ComUtil.getStrResource(mCtx, R.string.everymonth) + " ";
    			//특정주특정요일 월반복
    			if ( rrule[2] != null && rrule[2].length() == 3){
    				cycletxt = cycletxt + rrule[2].substring(0,1);
    				cycletxt = cycletxt + ComUtil.getStrResource(mCtx, R.string.pos) + " ";
    				int[] week = cvtDayOfWeekToCode(rrule[2].substring(1));
    				for ( int i = 0 ; i < week.length ; i++ ) {
    					if ( week[i] > 0 ) {
    						cycletxt = cycletxt + SmDateUtil.getDayOfWeekFromCode(mCtx, week[i]);
    					}
    				}
    				cycletxt = cycletxt + ComUtil.getStrResource(mCtx, R.string.dayofweek);
    			}
    			
    			//특정일 월반복
    			if ( rrule[4] != null && !rrule[4].equals("")){
    				cycletxt = cycletxt + " " + rrule[4] + ComUtil.getStrResource(mCtx, R.string.day);
    			}
    			
    		
    		} else if (  rrule[0] != null && rrule[0].equals(cWEEKLY)){				//2)주단위
    			if ( rrule[3] != null  ){
        	    	//interval
        	    	if ( ComUtil.stringToInt(rrule[3]) > 1  ) {
        	    		//간격
        	    		cycletxt = 	ComUtil.stringToInt(rrule[3]) +
        	    					ComUtil.getStrResource(mCtx, R.string.perweek) + " ";   	    		
        	    					
        	    	} else {
        	    		cycletxt = ComUtil.getStrResource(mCtx, R.string.everyweek)+ " ";
        	    	}   				
    			}
    	    	
    			if ( rrule[2] != null  ){
    				//요일
    				int[] week = cvtDayOfWeekToCode(rrule[2]);
    				for ( int i = 0 ; i < week.length ; i++ ) {
    					if ( week[i] > 0 ) {
    						cycletxt = cycletxt + SmDateUtil.getDayOfWeekFromCode(mCtx, week[i]);
    					}
    				} 				
    				cycletxt = cycletxt + ComUtil.getStrResource(mCtx, R.string.dayofweek);  				
    			}

    			
    		} else if (  rrule[0] != null && rrule[0].equals(cDAILY)){			//3) 일단위
    			
    			cycletxt = ComUtil.getStrResource(mCtx, R.string.onetime);
    			
    		} else if (  rrule[0] != null && rrule[0].equals(cYEARLY)){			//4) 년단위
    			
    			cycletxt = ComUtil.getStrResource(mCtx, R.string.everyyear)+ " ";
    			
    			if ( rrule[4] != null && rrule[5] != null  ){
    				
        			String yearmonth = ComUtil.fillSpaceToZero(rrule[5], 2) + ComUtil.fillSpaceToZero(rrule[4], 2);
        			cycletxt = cycletxt + SmDateUtil.getDateFullFormat(mCtx, yearmonth, false, false);				
    				
    			}

    		}
    		
    		buffer.append(cycletxt);
    	}

    	
//    	//To
//    	if ( rrule[1] != null && !rrule[1].equals("") ) {
//    		buffer.append(ComUtil.getStrResource(mCtx, R.string.to) + ":");
//    		if ( rrule[1].length() > 8 ) {
//    			buffer.append(SmDateUtil.getDateSimpleFormat(mCtx, rrule[1].substring(0,8), "/", false));
//    		} else {
//    			buffer.append(SmDateUtil.getDateSimpleFormat(mCtx, rrule[1], "/", false));
//    		}
//    	} 
    	
    	return buffer.toString();
		
    }     
	/*
	 * 반복일정 정보 parsing ( schedule ->  rrule )
	 */
    private String getFreqToRRule( ScheduleInfo sch ) {
    	
    	StringBuffer buffer = new StringBuffer(); 
    	
//    	String[] retArr = new String[4];
    	String sep = ";";
    	String valuesep = "=";
    	
    	//value setting
    	String freq 	= "";
    	String until 	= "";    	
    	String interval = "";    	
    	String byday = "";
    	String wkst = "";
    	
    	if ( sch.getCycle() != null && sch.getCycle().equals("Y") ) {
    		freq 	= cWEEKLY;
    		until	= sch.getEndDate() + "T" + sch.getEndTime() + "00Z";
    		interval= "1";
    		byday 	= cvtDayOfWeekToEngStr(sch.getDayOfWeek());
    		wkst = "SU";
    	} else {
//    		freq = cDAILY;
//    		byday = cvtDayOfWeekToEngStr(sch.getDayOfWeek());
    	}


    	//make rrule string
    	//1) 반복구분
    	if ( freq != null && !freq.equals("") ){
    		buffer.append(cFREQ);
    		buffer.append(valuesep);
    		buffer.append(freq);
    		buffer.append(sep);
    	}
    	//2)언제까지
    	if ( until != null && !until.equals("") ){
    		buffer.append(cUNTIL);
    		buffer.append(valuesep);
    		buffer.append(until);
    		buffer.append(sep);
    	}
    	//3)반복횟수
    	if ( interval != null && !interval.equals("") ){
    		buffer.append(cINTERVAL);
    		buffer.append(valuesep);
    		buffer.append(interval);
    		buffer.append(sep);
    	}
    	//4)알수없음... 
    	if ( wkst != null && !wkst.equals("") ){
    		buffer.append(cWKST);
    		buffer.append(valuesep);
    		buffer.append(wkst);
    		buffer.append(sep);
    	}	    	
    	//5)주char
    	if ( byday != null && !byday.equals("") ){
    		buffer.append(cBYDAY);
    		buffer.append(valuesep);
    		buffer.append(byday);
    	}			

    	
    	return buffer.toString();
    	
    }    
	/*
	 * week정보 전환 parsing ( week char - > code )
	 */
    private int[] cvtDayOfWeekToCode( String str ) {
    	
    	StringBuffer buffer = new StringBuffer();    	
    	buffer.append(str);
    	
    	String sep = ",";
    	int[] arrDayofweek = new int [7];	
    	for ( int i = 0 ; i < 7 ; i++) {
    		arrDayofweek[i] = 0;
    	}
    	
    	int pos = 0 ;
    	String find = "";
    	for ( int i = 0 ; i < 7 ; i++) {
    		
    		int findpos = buffer.indexOf(sep, pos);
    		if ( findpos == -1 ) {
    			find = buffer.substring(pos).toString();

    		} else {
    			find = buffer.substring(pos, findpos).toString();
    		}

			if ( find != null && find.equals("SU") ) {
				arrDayofweek[0] = ComConstant.DAYOFWEEK_SUNDAY;
			} else if ( find != null && find.equals("MO") ) {
				arrDayofweek[1] = ComConstant.DAYOFWEEK_MONDAY;
			} else if ( find != null && find.equals("TU") ) {
				arrDayofweek[2] = ComConstant.DAYOFWEEK_TUESDAY;
			} else if ( find != null && find.equals("WE") ) {
				arrDayofweek[3] = ComConstant.DAYOFWEEK_WEDNESDAY;
			} else if ( find != null && find.equals("TH") ) {
				arrDayofweek[4] = ComConstant.DAYOFWEEK_THURSDAY;
			} else if ( find != null && find.equals("FR") ) {
				arrDayofweek[5] = ComConstant.DAYOFWEEK_FRIDAY;
			} else if ( find != null && find.equals("SA") ) {
				arrDayofweek[6] = ComConstant.DAYOFWEEK_SATURDAY;
			}

			if ( findpos == -1 ) break;
			
    		pos = findpos + 1;
    	}
    	
    	return arrDayofweek;
    	
    }   
	/*
	 * week정보 전환 parsing ( week  code - >  char )
	 */
    private String cvtDayOfWeekToEngStr( int [] arrDayofweek ) {
    	
    	String sep = ",";
    	
    	StringBuffer buffer = new StringBuffer(); 
    	int len = arrDayofweek.length;
    	
    	for ( int i = 0 ; i < len ; i++) {

    		if ( buffer.length() > 0 && arrDayofweek[i] > 0 ) {
    			buffer.append(sep);
    		}
    		
    		if ( arrDayofweek[i] 		== ComConstant.DAYOFWEEK_SUNDAY ) {
    			buffer.append("SU");
    		} else if ( arrDayofweek[i] == ComConstant.DAYOFWEEK_MONDAY ) {
    			buffer.append("MO");
    		} else if ( arrDayofweek[i] == ComConstant.DAYOFWEEK_TUESDAY ) {
    			buffer.append("TU");
    		} else if ( arrDayofweek[i] == ComConstant.DAYOFWEEK_WEDNESDAY ) {
    			buffer.append("WE");
    		} else if ( arrDayofweek[i] == ComConstant.DAYOFWEEK_THURSDAY ) {
    			buffer.append("TH");
    		} else if ( arrDayofweek[i] == ComConstant.DAYOFWEEK_FRIDAY ) {
    			buffer.append("FR");
    		} else if ( arrDayofweek[i] == ComConstant.DAYOFWEEK_SATURDAY ) {
    			buffer.append("SA");
    		} 
    	}

    	
    	return buffer.toString();
    	
    }       
    /*
     * 1 -> Yes
     */
    private String cvtValueToYes( String value ) {
    	
    	String str = "";
    	
    	if ( value != null && value.equals("1")) {
    		str = "Y";
    	}
    	
    	return str;
    }
    /*
     * Yes -> 1
     */
    private String cvtValueFromYes( String value ) {
    	
    	String str = "";
    	
    	if ( value != null && value.equals("Y")) {
    		str = "1";
    	} else {
    		str = "0";
    	}
    	
    	return str;
    }   
   
    /*
     * alarm set (단, 일치하지 않을 경우 가장 가까운 값으로 change)
     */
    private String setAlarmMinute( Context ctx , int minute ) {
    	
    	String str = "";;
    	
    	if ( minute >= 0 ) {

    		int pos = ComUtil.getSpinner(ctx, R.array.arr_alarm_key, ComUtil.intToString(minute));
    		//해당되는 값이 없는 경우
    		if ( pos <= 0 ) {
    			Resources  res 	= ctx.getResources();		
    	        String[] sKeyArr = res.getStringArray(R.array.arr_alarm_key);
    	        int len = sKeyArr.length;
    	        int abs = -1;
    	        for ( int i = 0  ; i < len ; i++){
    	        	String sTemp = sKeyArr[i].toString();
    	        	int keymin = ComUtil.stringToInt(sTemp);
    	        	int tempAbs = Math.abs( keymin - minute ) ;
    	        	if ( abs < 0 || abs > tempAbs ) {
    	        		abs		= tempAbs;
    	        		str 	= sTemp;
    	        	}
    	        }   			
    		} else {
    			str = ComUtil.intToString(minute);
    		}
    	} else {
    		str = ComUtil.intToString(minute);
    	}
    	
    	return str;
    }
      
    private String [] cvtDateTimeFromMili ( Calendar iCal, String date ) {
    	
    	String [] str = new String[2];
    	
    	if ( date != null && !date.equals("") ){
		
    		iCal.setTimeInMillis(Long.parseLong(date));
    		
    		str[0] = SmDateUtil.getDateFormat(
					iCal.get(Calendar.YEAR), iCal.get(Calendar.MONTH)+ 1, iCal.get(Calendar.DAY_OF_MONTH));
		
			str[1] =  SmDateUtil.getTimeFormat(
					iCal.get(Calendar.HOUR_OF_DAY), iCal.get(Calendar.MINUTE)); 
    	} else {
    		
    		str[0] = "";
    		str[1] = "";
    	}
		
		return str;
		
    }
    
    private long cvtDateTimToMili (  Calendar iCal, String date, String time ) {
    	
    	long dtdata = (long) 0;
    	if (( date != null && date.length() == 8 ) &&
    		( time != null && time.length() == 4 )) {
	    	iCal.set(
		    	SmDateUtil.getDateToInt(date, ComConstant.GUBUN_YEAR),
		    	SmDateUtil.getDateToInt(date, ComConstant.GUBUN_MONTH),
		    	SmDateUtil.getDateToInt(date, ComConstant.GUBUN_DAY),
		    	
		    	SmDateUtil.getDateToInt(time, ComConstant.GUBUN_HOUR),
		    	SmDateUtil.getDateToInt(time, ComConstant.GUBUN_MINUTE),
		    	SmDateUtil.getDateToInt("00", ComConstant.GUBUN_SECOND)
	    	) ;
	    	
	    	dtdata = iCal.getTimeInMillis();
    	}
    	
    	return dtdata;
		
    }    
    
    public boolean insertCalendarData( ScheduleInfo info ){
//    throws SyncException {
    	
//    	bolean ret = false;
        ContentValues values = new ContentValues();
        if ( info == null) {
            Log.e("CalendarProvider.save()",
                    "Phone Calendar value is null ");
            return false;
        }
        if (info.getStartDate() == null || info.getEndDate() == null) {
            Log.e("CalendarProvider.save()",
                    "info.getStartDate() == null || info.getEndDate() ; cannot save calendar entry with id="
                            + Long.toString(info.getId()));
            return false;
        }
        if (info.getStartTime() == null || info.getEndTime() == null) {
            Log.e("CalendarProvider.save()",
                    "info.getStartTime() == null || info.getEndTime() ; cannot save calendar entry with id="
            				+ Long.toString(info.getId()));
            return false;
        }
        
//        PhoneCalendarInfo pho = new PhoneCalendarInfo();
        Calendar iCal = Calendar.getInstance();        
        
        long start = cvtDateTimToMili( iCal, info.getStartDate(), info.getStartTime());
        long end = cvtDateTimToMili( iCal, info.getEndDate(), info.getEndTime());

        int allday = ComUtil.stringToInt(cvtValueFromYes(info.getAllDayYn()));
        String rrule = getFreqToRRule(info);
        
        String duration;
        if ( allday > 0) {
            long days = (end - start + DateUtils.DAY_IN_MILLIS - 1)
                    / DateUtils.DAY_IN_MILLIS;
            duration = "P" + days + "D";
        } else {
            long seconds = (end - start) / DateUtils.SECOND_IN_MILLIS;
            duration = "P" + seconds + "S";
        }

        //알람정보 setting
        String alarmmin = info.getAlarmYn();
        String hasAlarm = "";
        if ( alarmmin != null && !alarmmin.equals("")) {
        	hasAlarm = "1";
        }
        
//        values.put("calendar_id", 	info.getOtherId());
        if ( info.getOtherId() > 0)
        	values.put("_id", 			info.getOtherId());
        values.put("calendar_id", 	1);
        values.put("title", 		info.getScheduleName());
        values.put("eventStatus", 	1);  //확정됨
        
        values.put("allDay", 		allday);
        values.put("dtstart", 		start);
        values.put("dtend", 		end);
        values.put("duration", 		duration);
        values.put("description", 	"From SMCalendar");
        values.put("eventLocation", "");
        values.put("visibility", 	0);
        values.put("transparency", 	0);
        values.put("hasAlarm", 		hasAlarm);
        values.put("rrule", 		rrule);
        values.put("lastDate", 		end);
//        values.put("eventTimezone", 		end);
        
//        values.put("exdate", 		info.getexDate());

        if (info.getOtherId() == 0 ) {
            Uri newUri = cr.insert(CALENDAR_URI, values);
            if (newUri == null) {
            	return false;
            }
//                throw new SyncException(e.getTitle(),
//                        "Unable to create new Calender Entry, provider returned null uri.");
            	info.setOtherId((int) ContentUris.parseId(newUri));
        } else {
            Uri uri = ContentUris.withAppendedId(CALENDAR_URI, info.getOtherId());
            int rtn = cr.update(uri, values, null, null);
            if ( rtn < 0 ) {
                Uri newUri = cr.insert(CALENDAR_URI, values);
                if (newUri == null){
                	return false;
                }
//                    throw new SyncException(e.getTitle(),
//                            "Unable to create new Calender Entry, provider returned null uri.");
                	info.setOtherId((int) ContentUris.parseId(newUri));
            }
        }

        
        if ( alarmmin != null && !alarmmin.equals("")) {
        	
            //delete existing alerts to replace them with the ones from the synchronisation		
            cr.delete(CALENDAR_ALERT_URI, "event_id=?",
                    new String[] { Long.toString(info.getOtherId()) });

            //remove reminder entry
            cr.delete(CALENDAR_REMINDER_URI, "event_id=?",
                    new String[] { Long.toString(info.getOtherId()) });

            //create reminder
            ContentValues reminderValues = new ContentValues();
            reminderValues.put("event_id", 	info.getOtherId());
            reminderValues.put("method", 	1);
            reminderValues.put("minutes", 	alarmmin);

            cr.insert(CALENDAR_REMINDER_URI, reminderValues);

//            //create alert
//            ContentValues alertValues = new ContentValues();
//
//            alertValues.put("event_id", info.getOtherId());
//            alertValues.put("begin", 	start);
//            alertValues.put("end", 		end);
//            alertValues.put("alarmTime",
//                    (start - Long.parseLong(alarmmin) * 60000));
//            alertValues.put("state", 0);
//            //alertValues.put("creationTime", value);
//            alertValues.put("minutes", Long.parseLong(alarmmin));
//
//            cr.insert(CALENDAR_ALERT_URI, alertValues);
        }
        return true;
    }
   
}

		//	        	description[i] = "Phone Calendar";                           
		//	        	dtstart[i] = managedCursor.getInt(8);                                  
		//	        	dtend[i] = managedCursor.getInt(9);                          
		//	        	allDay[i] = managedCursor.getInt(12);                              
		//	        	hasAlarm[i] = managedCursor.getInt(15);                                
		//	        	rdate[i] = managedCursor.getString(18);                                
		//	        	exdate[i] = managedCursor.getString(20);                        
		//	        	lastDate[i] = managedCursor.getInt(24);                          
		//	        	deleted[i] = managedCursor.getInt(30);    
			        	
		//	        	//calendar_id[i] = managedCursor.getInt(0);                              
		//	        	//Log.i("Calendar", "ID : " + calendar_id[i]);                           
		////	        	htmlUri[i] = managedCursor.getString(1);                               
		////	        	Log.i("Calendar", "htmlUri : " + htmlUri[i]);                          
		//	        	title[i] = cur.getString(2);                                 
		//	        	Log.i("Calendar", "title : " + title[i]);                              
		////	        	eventLocation[i] = managedCursor.getString(3);                         
		////	        	Log.i("Calendar", "eventLocation : " + eventLocation[i]);              
		//	        	description[i] = "Phone Calendar";                           
		////	        	eventStatus[i] = managedCursor.getInt(5);                              
		////	        	selfAttendeeStatus[i] = managedCursor.getInt(6);                       
		////	        	commentsUri[i] = managedCursor.getString(7);                           
		//	        	dtstart[i] = managedCursor.getInt(8);                                  
		//	        	dtend[i] = managedCursor.getInt(9);                                    
		////	        	eventTimezone[i] = managedCursor.getString(10);                        
		////	        	duration[i] = managedCursor.getString(11);                             
		//	        	allDay[i] = managedCursor.getInt(12);                                  
		////	        	visibility[i] = managedCursor.getInt(13);                              
		////	        	transparency[i] = managedCursor.getInt(14);                            
		//	        	hasAlarm[i] = managedCursor.getInt(15);                                
		////	        	hasExtendedProperties[i] = managedCursor.getInt(16);                   
		////	        	rrule[i] = managedCursor.getString(17);                                
		//	        	rdate[i] = managedCursor.getString(18);                                
		////	        	exrule[i] = managedCursor.getString(19);                               
		//	        	exdate[i] = managedCursor.getString(20);                               
		////	        	originalEvent[i] = managedCursor.getString(21);                        
		////	        	originalInstanceTime[i] = managedCursor.getInt(22);                    
		////	        	originalAllDay[i] = managedCursor.getInt(23);                          
		//	        	lastDate[i] = managedCursor.getInt(24);                                
		////	        	hasAttendeeData[i] = managedCursor.getInt(25);                         
		////	        	guestsCanModify[i] = managedCursor.getInt(26);                         
		////	        	guestsCanInviteOthers[i] = managedCursor.getInt(27);                   
		////	        	guestsCanSeeGuests[i] = managedCursor.getInt(28);                      
		////	        	organizer[i] = managedCursor.getString(29);                            
		//	        	deleted[i] = managedCursor.getInt(30);  
//}

	
