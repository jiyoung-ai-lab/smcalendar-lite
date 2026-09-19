package com.waveapp.smcalendarlite;

import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.widget.TimePicker;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class PreferenceMain extends PreferenceActivity implements OnPreferenceChangeListener {
	
	private DialogInterface.OnCancelListener mDialogListener;
	
	SharedPreferences mainPreference;
	PreferenceCategory basicCategory;
//	Preference notice;
	Preference appversion;
	ListPreference language;
	ListPreference holiday;
	Preference  sdbackup;
	Preference  sdrestore;
	ListPreference  getcalendar;
	ListPreference  sendcalendar;
	ListPreference  widgetback;
	
	EditTextPreference alarmtime;

	String noticetitle ;
	
	int mAlarmHour;   
	int mAlarmMinute; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// 1. es\xml\preferences.xml로 부터 Preference 계층구조를 읽어와
		// 2. 이 PreferenceActivity의 계층구조로 지정/표현 하고
		// 3. \data\data\패키지이름\shared_prefs\패키지이름_preferences.xml 생성
		// 4. 이 후 Preference에 변경 사항이 생기면 파일에 자동 저장
		addPreferencesFromResource(R.xml.preferences);
		
		mainPreference = PreferenceManager.getDefaultSharedPreferences(this); 
		
		//일반카테고리(언어)
		basicCategory 	= (PreferenceCategory)findPreference("category_basic");
//		notice 			= (Preference)findPreference("notice_pref");
		appversion 		= (Preference)findPreference("version_pref");
		language 		= (ListPreference)findPreference("lang_pref");
		holiday 		= (ListPreference)findPreference("holiday_pref");
		getcalendar 	= (ListPreference)findPreference("fromcalendar_pref");
		widgetback		= (ListPreference)findPreference("widgetback_pref");
		alarmtime		= (EditTextPreference)findPreference("alarmtime_pref");
		
		
//		topmenu 		= (ListPreference)findPreference("topmenuview_pref");
//		sendcalendar = (ListPreference)findPreference("tocalendar_pref");
		
		if ( appversion != null  ) {
			appversion.setSummary("V" + ComUtil.getAppVersion(this));
		}
		
		if ( language != null && language.getEntry() != null ) {
			language.setSummary(language.getEntry().toString());
		}
		if ( holiday != null && holiday.getEntry() != null ) {
			holiday.setSummary(holiday.getEntry().toString());
		}	
		if ( widgetback != null && widgetback.getEntry() != null ) {
			widgetback.setSummary(widgetback.getEntry().toString());
		}
		
		//lite버전과 normal 항목이 다름.. key 변경
		if ( getcalendar != null && VersionConstant.APPID.equals(VersionConstant.APP_LITE) ) {
			getcalendar.setEntries(R.array.arr_calendar_lite);
			getcalendar.setEntryValues(R.array.arr_calendar_lite_key);
		} else {
			getcalendar.setEntries(R.array.arr_calendar);
			getcalendar.setEntryValues(R.array.arr_calendar_key);		
		}
		
		//lite제한
		if ( widgetback != null && VersionConstant.APPID.equals(VersionConstant.APP_LITE) ) {
			widgetback.setEnabled(false);
			widgetback.setSummary("Not supported Lite version");
		} else {
			widgetback.setEnabled(true);	
		}
		
		if ( alarmtime != null ) {		
			String time = SmDateUtil.getTimeFullFormat(this, ComConstant.ALARM_DEFAULTTIME);
			alarmtime.setSummary(time);
			mAlarmHour 	 = SmDateUtil.getTimeToInt( ComConstant.ALARM_DEFAULTTIME, ComConstant.GUBUN_HOUR);                    
			mAlarmMinute = SmDateUtil.getTimeToInt( ComConstant.ALARM_DEFAULTTIME, ComConstant.GUBUN_MINUTE);
		}		
		
//		noticetitle = (String) notice.getTitle();
		
		sdbackup = (Preference)findPreference("sdbackup_pref");
		sdrestore = (Preference)findPreference("sdrestore_pref");
		
		language.setOnPreferenceChangeListener(this);
		holiday.setOnPreferenceChangeListener(this);
		getcalendar.setOnPreferenceChangeListener(this);
		widgetback.setOnPreferenceChangeListener(this);
//		sendcalendar.setOnPreferenceChangeListener(this);

		// category1의 활성화 비활성화 여부를 Preference 파일 중 "sub_checkbox"의 키 값으로 결정
		//category1.setEnabled(mainPreference.getBoolean("sub_checkbox", false));
	}	
	
	@Override
	protected void onResume() {
		super.onResume();

//		//안읽은 공지가 있는 경우 처리
//		notice.setTitle( noticetitle + isNoticeNotRead());

	}

	// Preference에서 클릭 발생시 호출되는 call back
	// Parameters:
	//  - PreferenceScreen : 이벤트가 발생한 Preference의 root
	//  - Preference : 이벤트를 발생시킨 Preference 항목
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		// sub_checkbox란 키를 가지고 있는 Preference항목이 이벤트 발생 시 실행 
		 if (sdbackup != null && preference.equals(sdbackup)) { 				
			//sd카드 사용가능여부 체크
			if ( ! ComUtil.hasStorage( true ) ) {
				ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.err_not_existsdcard));
				//finish();	
			} else {
				showDialog(R.string.msg_sdbackup);
			}					
			
			//SDcard BackUp
		} else if (sdrestore != null && preference.equals(sdrestore)) { 
			//sd카드 사용가능여부 체크
			if ( ! ComUtil.hasStorage( false ) ) {
				ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.err_not_existsdcard2));

			} else {			
				showDialog(R.string.msg_sdrestore);
			}
			//알람시간변경
		} else if (alarmtime != null && preference.equals(alarmtime)) { 
			showDialog(ComConstant.TIME_DIALOG_ID);
//		//공지사항링크
//		} else if (notice != null && preference.equals(notice)) { 
//			callNotice();
		} 
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
		super.setPreferenceScreen(preferenceScreen);
	}

	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		//
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}		
	/*
	 * Message Dialog Set
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		removeDialog(id);
		dialog = null;
		
	}
    @Override
    protected Dialog onCreateDialog(int id) { 
    	final MessageHandler msgHd = new MessageHandler(this);
    	Dialog di;
    	final Context ctx = this;
    	
    	switch (id) {   

		case R.string.msg_sdbackup:	
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.info), R.string.msg_sdbackup, "");
			di.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) { 
					
					callSDCardBackup();
				}
			});
					
			return di;	

		case R.string.msg_sdrestore:	
			
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.info), R.string.msg_sdrestore, "");
			di.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) { 
					callSDCardRestore();
				}
			});
					
			return di;
		case R.string.msg_litedatabackup:	
			
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.info), R.string.msg_litedatabackup, "");
			di.setOnCancelListener(new DialogInterface.OnCancelListener() {				
				@Override
				public void onCancel(DialogInterface dialog) { 
					callSDCardRestoreFromLite(  );
				}
			});
					
			return di;	
    	case ComConstant.TIME_DIALOG_ID: 
			msgHd.setTimeString(mAlarmHour, mAlarmMinute);
	    	alarmtime.getDialog().dismiss();
			di = msgHd.onCreateDialog(new TimePickerDialog.OnTimeSetListener() {        
		    	public void onTimeSet(TimePicker view, int hour,
			    		int minute) { 
		    			if ( msgHd.getReturn() == true ) {
			    			mAlarmHour 	 = hour;                    
			    			mAlarmMinute = minute; 
			    			ComConstant.ALARM_DEFAULTTIME = SmDateUtil.getTimeFormat(mAlarmHour, mAlarmMinute);
					    	alarmtime.setSummary(SmDateUtil.getTimeFullFormat(ctx, ComConstant.ALARM_DEFAULTTIME));
					    	alarmtime.setText(ComConstant.ALARM_DEFAULTTIME);
					        //알람서비스 기동(기념일,할일)
					    	AlarmHandler ah = new AlarmHandler(ctx);
					    	ah.setAlarmServiceForSpecialday();
					    	ah.setAlarmServiceForTodo();			    				
		    			}
			    	
				    	
		    		}    
			    });
			return null;
			
		}
    return null;
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object obj) {
		
		//언어변경시 -> 언어설정변경, 화면 refresh
		if(language != null && preference.equals(language)) {
			final int idx = language.findIndexOfValue((String)obj);
			language.setSummary(language.getEntries()[idx]);
			String locale = language.getEntryValues()[idx].toString();
	        
			//언어설정변경
			Configuration config = getBaseContext().getResources().getConfiguration();
        	Locale nLocale = new Locale(locale);
        	Locale.setDefault(nLocale);
        	config.locale = nLocale;        	
        	getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        	
        	//locale setting and value
	        ComConstant.setConstantStringForLocale(this, locale); 
	        
	        //화면 refresh
	        MenuHandler mh = new MenuHandler(this);
	        mh.callPreference();
	        
	    	//위젯갱신
	    	ViewUtil.updateAllWidget(this);
	        
		}
		//국공일 나라 변경시 -> 국공일 변수 변경, 위젯 refresh
		else if(holiday != null && preference.equals(holiday)) {
			final int idx = holiday.findIndexOfValue((String)obj);
			holiday.setSummary(holiday.getEntries()[idx]);
			String country = holiday.getEntryValues()[idx].toString();
			
			//해당국가의 국공일이 없는 경우 일괄 반영
			//--> 미정
	        
        	//국공일 setting and value
	        ComConstant.setConstantStringForCountry(this, country); 
			
	    	//위젯갱신
	    	ViewUtil.updateAllWidget(this);
	        
		}		
		//일정공유(일정정보가져오기 )
		else if(getcalendar != null && preference.equals(getcalendar)) {
			final int idx = getcalendar.findIndexOfValue((String)obj);
			String calchoice = getcalendar.getEntryValues()[idx].toString();
			//lite에서 가져오기
			if ( calchoice != null && calchoice.equals("lite")) {
				getDataFromSMcalendarLite();
			} else {
				getOtherCalendar(calchoice);
			}
			
		}
		//위젯배경가져오기
		else if(widgetback != null && preference.equals(widgetback)) {
			final int idx = widgetback.findIndexOfValue((String)obj);
			widgetback.setSummary(widgetback.getEntries()[idx].toString());
			String back  = widgetback.getEntryValues()[idx].toString();
			
			//widget style setting
	        ViewUtil.setWidgetStyle(this, back);
			
			//위젯갱신
			ViewUtil.updateAllWidget(this);
		}
//		//Top menu
//		else if(topmenu != null && preference.equals(topmenu)) {
//			final int idx 	= topmenu.findIndexOfValue((String)obj);
//			String visible 	= topmenu.getEntryValues()[idx].toString();
//			String topscrip = ComUtil.getStrResource(this, R.string.pre_topmenuview_summary)
//							+ "(" 
//							+ topmenu.getEntries()[idx] 
//							+ ")" ; 
//			topmenu.setSummary(	topscrip );			
//
//			ViewUtil.setTopMenuVisableToPref(this, Integer.parseInt(visible));
//
////	        //화면 refresh
////	        MenuHandler mh = new MenuHandler(this);
////	        mh.callPreference();
//			
//		}		
//		//일정공유(일정정보내보내기 )
//		else if(sendcalendar != null && preference.equals(sendcalendar)) {
//			final int idx = sendcalendar.findIndexOfValue((String)obj);
//			String calchoice = sendcalendar.getEntryValues()[idx].toString();
//			
//			sendOtherCalendar(calchoice);
//		}			

		return true;
	} 
	/*
	 * 화면이나 Activity 호출
	 */
//	private void callNotice() {
//		Intent intent = new Intent(this, Notice.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		startActivity(intent);
//	}
	
	private void callSDCardBackup() {
		Intent intent = new Intent(this, SdCardBackup.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	private void callSDCardRestore() {
		Intent intent = new Intent(this, SdCardRestore.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	private void callSDCardRestoreFromLite() {
        
		Bundle bundle = new Bundle();
		bundle.putString(ComConstant.FOLDER_FROMLITE, "lite");
		Intent intent = new Intent(this, SdCardRestore.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtras(bundle);
		startActivity(intent);
	}	
	private void getOtherCalendar( String calchoice ) {
		Bundle bundle = new Bundle();

		bundle.putString(ComConstant.CALCHOICE_GUBUN, calchoice);
		
		Intent intent = new Intent(this, GetOtherCalendar.class);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		startActivity(intent);
	}	
	
//	private void sendOtherCalendar( String calchoice ) {
//		Bundle bundle = new Bundle();
//
//		bundle.putString(ComConstant.CALCHOICE_GUBUN, calchoice);
//		
//		Intent intent = new Intent(this, SendOtherCalendar.class);
//		intent.putExtras(bundle);
//		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		
//		startActivity(intent);
//	}	


     /*
      * 뒤로가기 버튼 클릭시 홈으로 .....
      * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
      */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	       //event.startTracking();
	       MenuHandler msgHd = new MenuHandler(this);
	       msgHd.callCalendar();
	       finish();
	       return true;
	    }      
	         
	    return super.onKeyDown(keyCode, event);
	}

//	private String isNoticeNotRead () {
//		
//		String str = "";
//		
//        //1.DB Open / value reset
//    	NoticeDbAdaper mNoticeDbHelper = new NoticeDbAdaper(this);
//    	mNoticeDbHelper.open();
//        
//		Cursor cur = mNoticeDbHelper.fetchNotReadNotice( VersionConstant.APPID, ComConstant.LOCALE );
//		
//		if ( cur != null  && cur.getCount() == 0 ) {
//			//안읽은 공지 있음
//			str = "";
//		} else {
//			str = "(" + ComUtil.intToString(cur.getCount())+ ")";
//		}
//		
//		cur.close();
//		mNoticeDbHelper.close();
//		
//		return str;
//	}
	
	/*
	 * smcalendar lite에서 데이터 가져오기(정식버전에서만 처리되는 로직)
	 */
	private boolean getDataFromSMcalendarLite() {
		
		if ( VersionConstant.APPID.equals(VersionConstant.APP_NORMAL)) {
			//패키지에서 smclanedar lite 설치여부확인
			PackageManager pm = this.getPackageManager();
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			
			int len = packages.size();
			
			
			for ( int i = 0; i < len ; i++ ) {
				
				if ( packages.get(i).packageName.toString().equals("com.waveapp.smcalendarlite")) {
//					PackageInfo pi = null;
//					try {
//						pi = pm.getPackageInfo(packages.get(i).packageName.toString(), 0);
//					} catch (NameNotFoundException e) {
//						e.printStackTrace();
//					}
//					//20이상인 경우는 db가 다름 -> 비교필요(유료버전이 20이상만 정상처리 아님 error)
//					if ( pi != null && pi.versionCode >= 20 ) {
//						if ( ComUtil.getAppVersionCode(this) < 20 ) {
//							ComUtil.showToastLong(this, ComUtil.getStrResource(this, R.string.err_appversioncheck));
//							return true;
//						}
//					}
					
					//sd카드 사용가능여부 체크(없으면 skip)
					if ( ! ComUtil.hasStorage( false ) ) {
						ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.err_not_existsdcard2));
						return true;
					} else {
						showDialog(R.string.msg_litedatabackup);
						return false;
					}
				}

			}	
			ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.err_not_existfilelite));
		} else {
			
		}
		
		return true;
	}
 
    
}
