package com.waveapp.smcalendarlite.handler;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.waveapp.smcalendarlite.CalendarMain;
import com.waveapp.smcalendarlite.LunarCalendarMain;
import com.waveapp.smcalendarlite.Notice;
import com.waveapp.smcalendarlite.PreferenceMain;
import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.ScheduleDayOfWeek;
import com.waveapp.smcalendarlite.ScheduleListForDate;
import com.waveapp.smcalendarlite.ScheduleListForUser;
import com.waveapp.smcalendarlite.ScheduleManager;
import com.waveapp.smcalendarlite.SpecialdayAllList;
import com.waveapp.smcalendarlite.SpecialdayList;
import com.waveapp.smcalendarlite.SpecialdayManager;
import com.waveapp.smcalendarlite.TodoList;
import com.waveapp.smcalendarlite.UserList;
import com.waveapp.smcalendarlite.UserManager;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.database.NoticeDbAdaper;
import com.waveapp.smcalendarlite.link.EverNoteLink;
import com.waveapp.smcalendarlite.link.KakaoLink;
import com.waveapp.smcalendarlite.util.ComUtil;

public class MenuHandler  extends SMActivity {
	
	private final Context mCtx;
	//private String  mParam1 ;
	
	public MenuHandler(Context ctx) {
        this.mCtx = ctx;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
//        MenuHandler menu = new MenuHandler(mCtx);
  
	}
    
//    @Override
	public void preMenuEvent(Menu menu) {
		
    	MenuItem item = menu.findItem(R.id.menu_notice);        
    	if ( item != null ) {
        	//공지사항
            String noticecnt = isNoticeNotRead();
            if ( noticecnt != null && !noticecnt.equals("")) {
            	String title = ComUtil.getStrResource(mCtx, R.string.pre_notice);
            	title = title + noticecnt;
            	item.setTitle(title);
            }     		
    	}
		return ;
	}

    
    public boolean onMenuItemEvent(MenuItem item) {
    	MenuHandler menu = new MenuHandler(mCtx);
    	
//        String noticecnt = isNoticeNotRead();
//        if ( noticecnt != null && !noticecnt.equals("")) {
//
//        	String title = item.getTitle().toString();
//        	title = title + noticecnt;
//        	item.setTitle(title);
//        }   	
        
        switch(item.getItemId()) {
//        case R.id.menu_user_new:
//        	menu.callUsermanager();
//            return true;
	        case R.id.menu_schedule_new:
	        	menu.callSchedulemanager();
	            return true; 
	        case R.id.menu_specialday_new:
	        	menu.callSpecialmanager();
	            return true;  
	//        case R.id.menu_specialday:
	//        	menu.callSpecialdayList();
	//            return true;              
	        case R.id.menu_shedule_userlist:
	        	menu.callScheduleUserList();
	            return true;
	        case R.id.menu_specialday_allliset:
	        	menu.callSpecialdayAllList();
	            return true;	                  
	//        case R.id.menu_lunarcalendar:
	//        	menu.callLunarCalendar();
	//            return true; 
	        case R.id.menu_week:
	        	menu.callScheduleDayOfWeek();
	            return true; 
	        case R.id.menu_notice:
	        	menu.callNotice();
	            return true;  	            
	        case R.id.menu_preference:
	        	menu.callPreference();
	            return true;    
        }        
        return super.onOptionsItemSelected(item);
    }


	public final void callUsermanager () {
    	Intent i = new Intent(mCtx, UserManager.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 
	public final void callSchedulemanager () {
    	Intent i = new Intent(mCtx, ScheduleManager.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 
	public final void callSpecialmanager () {
    	Intent i = new Intent(mCtx, SpecialdayManager.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 
	public final void callSpecialdayList () {
    	Intent i = new Intent(mCtx, SpecialdayList.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 	
	public final void callUserList () {
    	Intent i = new Intent(mCtx, UserList.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    }	
	public final void callScheduleUserList () {
    	Intent i = new Intent(mCtx, ScheduleListForUser.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 
	public final void callSpecialdayAllList () {
    	Intent i = new Intent(mCtx, SpecialdayAllList.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 	
	public final void callScheduleDateList () {
    	Intent i = new Intent(mCtx, ScheduleListForDate.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	mCtx.startActivity(i);
    } 		
	public final void callCalendar () {
    	Intent i = new Intent(mCtx, CalendarMain.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	mCtx.startActivity(i);
    } 
	public final void callLunarCalendar () {
    	Intent i = new Intent(mCtx, LunarCalendarMain.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	mCtx.startActivity(i);
    } 
	public final void callNotice() {
    	Intent i = new Intent(mCtx, Notice.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	mCtx.startActivity(i);
    } 	
	public final void callPreference () {
    	Intent i = new Intent(mCtx, PreferenceMain.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	mCtx.startActivity(i);
    }  
	public final void callTodoList () {
    	Intent i = new Intent(mCtx, TodoList.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	mCtx.startActivity(i);
    } 	
	public final void callScheduleDayOfWeek () {
    	Intent i = new Intent(mCtx, ScheduleDayOfWeek.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	mCtx.startActivity(i);
    } 	

    /*
     * 호출화면 : 문자메시지(SMS) 2.1 가능 버전 (폰번호 넘기면 안됨 ㅎ)
     */
	public final void callSMSView( String message  ) {
    	//기능제한여부
        if ( !ComUtil.isFormallVer(mCtx) ) {
        	finish();
        	return;
        }		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setType("vnd.android-dir/mms-sms");
		i.putExtra("sms_body", message);
		mCtx.startActivity(i);		
	}	
//    /*
//     * 호출화면 : 문자메시지(SMS)
//     */
//	public final void callSMSView( String phonenumber, String message  ) {
//    	//기능제한여부
//        if ( !ComUtil.isFormallVer(mCtx) ) {
//        	finish();
//        	return;
//        }		
////		String sendUrl = "smsto:" + phonenumber;
////		String sendUrl = "sms:" + phonenumber;
////		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(sendUrl));
//		Intent i = new Intent(Intent.ACTION_VIEW);
//		i.setType("vnd.android-dir/mms-sms");
//		i.putExtra("sms_body", message);
//		mCtx.startActivity(i);		
//	}	
    /*
     * 카카오톡 링크 및 메시지 전달
     */
    public final void linkToKakaoTalk(  String message, String URLString ) {
    	
    	//기능제한여부
        if ( !ComUtil.isFormallVer(mCtx) ) {
        	finish();
        	return;
        }
        
		String appPackageId = mCtx.getPackageName();
		String appVersion = "";
		try {
			appVersion = mCtx.getPackageManager().getPackageInfo(mCtx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		KakaoLink kakaoLink = null;

		try {
			kakaoLink = new KakaoLink(mCtx, URLString, appPackageId, appVersion, message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (kakaoLink.isAvailable()) {
			mCtx.startActivity(kakaoLink.getIntent());
		} else {
		    // 카카오톡이 설치되어 있지 않은 경우에 대한 처리
			ComUtil.showToast(mCtx, mCtx.getResources().getString(R.string.err_not_kakaotalk));
		}
	}  
    /*
     * Evernote 링크 및 메시지 전달
     */
    public final void linkToEverNote( String title, String message  ) {
    	
    	//기능제한여부
        if ( !ComUtil.isFormallVer(mCtx) ) {
        	finish();return;
        }
        EverNoteLink evernote = new EverNoteLink(mCtx);
        evernote.newNoteWithContent(title, message);
        
	}  
	/*
	 * 읽지않은 공제사항확인
	 */
	private String isNoticeNotRead () {
		
		String str = "";
		
        //1.DB Open / value reset
    	NoticeDbAdaper mNoticeDbHelper = new NoticeDbAdaper(mCtx);
    	mNoticeDbHelper.open();
        
		Cursor cur = mNoticeDbHelper.fetchNotReadNotice( VersionConstant.APPID, ComConstant.LOCALE );
		
		if ( cur != null  && cur.getCount() == 0 ) {
			//안읽은 공지 있음
			str = "";
		} else {
			str = "(" + ComUtil.intToString(cur.getCount())+ ")";
		}
		
		cur.close();
		mNoticeDbHelper.close();
		
		return str;
	}
	  	
}
