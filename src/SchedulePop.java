package com.waveapp.smcalendarlite;
 

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.waveapp.smcalendarlite.adapter.SchedulePopUpAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class SchedulePop extends Dialog implements OnClickListener, OnItemClickListener{
	
	private final Context mCtx;
	SchedulePopUpAdapter mAdapter;
	ArrayList<ScheduleInfo> mList;
//	public int mYear;
//	public int mMonth;
//	public int mDay;
	ListView listView; 
	TextView tvDate; 
	TextView tvLunar; 
	ImageButton mClose ;

    ImageButton mScheduleDayBtn;
    LinearLayout mScheduleDayLin;
    ImageButton mWeekScheduleBtn;
    LinearLayout mWeekScheduleLin;
    
    ImageButton mScheduleAddBtn;
    LinearLayout mScheduleAddLin;
    ImageButton mSpecialdayAddBtn;
    LinearLayout mSpecialdayAddLin;
    ImageButton mTodoAddBtn;
    LinearLayout mTodoAddLin;
	
	public int mYear;
	public int mMonth;
	public int mDay;
	String mFromDate;
	long mID;
    String  mName;
    String  mMessage;
    String  mGubun;	
	
	public SchedulePop( Context context  )
	{
		super( context );
		this.mCtx 		= context;			
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        
	    //다국어처리
		ViewUtil.setLocaleFromPreference(mCtx, mCtx.getClass());
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.schedule_popup);
		listView 		= (ListView) findViewById(R.id.list_shedule);
		mClose  		= (ImageButton)findViewById( R.id.close ) ;
		
		mScheduleDayBtn 	= (ImageButton)findViewById( R.id.schedule_day ) ;
		mScheduleDayLin 	= (LinearLayout)findViewById( R.id.lin_schedule_day );
		mWeekScheduleBtn 	= (ImageButton)findViewById( R.id.weekschedule ) ;
		mWeekScheduleLin 	= (LinearLayout)findViewById( R.id.lin_weekschedule );        
        
		mScheduleAddBtn 	= (ImageButton)findViewById( R.id.schedule_add ) ;
		mScheduleAddLin 	= (LinearLayout)findViewById( R.id.lin_schedule_add );
		mSpecialdayAddBtn 	= (ImageButton)findViewById( R.id.specialday_add ) ;
		mSpecialdayAddLin 	= (LinearLayout)findViewById( R.id.lin_specialday_add );
		mTodoAddBtn 	= (ImageButton)findViewById( R.id.todo_add ) ;
		mTodoAddLin 	= (LinearLayout)findViewById( R.id.lin_todo_add );

		tvDate 		= (TextView) findViewById(R.id.tv_date);
		tvLunar		= (TextView) findViewById(R.id.tv_lunar);
		
		//날짜 Title
		mFromDate = SmDateUtil.getDateFormat(mYear, mMonth, mDay);
		
		//리스트데이터
		fillData();
		
		//click event 처리
		setClickListener();
    
	}
    private void setClickListener () {
        listView.setOnItemClickListener( this );
    	mClose.setOnClickListener( this ) ;
    	mScheduleDayLin.setOnClickListener( this ) ;
    	mWeekScheduleLin.setOnClickListener( this ) ;
    	mScheduleAddLin.setOnClickListener( this ) ;
    	mSpecialdayAddLin.setOnClickListener( this ) ;
    	mTodoAddLin.setOnClickListener( this ) ;
    }              

	public void setDate(int yyyy, int MM, int dd) {
		  this.mYear  = yyyy;
		  this.mMonth = MM;
		  this.mDay   = dd;	
	}
	
	public int getYear() {
		  return mYear; 
	}
	public int getMonth() {
		  return mMonth; 
	}
	public int getDay() {
		  return mDay; 
	}
    @Override
	public void onClick(View v) {    	
		
		if ( v == mClose ) {
			
			this.dismiss();
			
		} else if ( v == mScheduleDayLin ) {
			mScheduleDayBtn.setPressed(true);
			callScheduleTabForDate();
			this.dismiss();
			
		} else if ( v == mWeekScheduleLin ) {
			mWeekScheduleBtn.setPressed(true);
			callScheduleDayOfWeek();
			this.dismiss();
			
		}  else if ( v == mScheduleAddLin ) {
			mScheduleAddBtn.setPressed(true);
			callScheduleManager();
			this.dismiss();
			
		} else if( v == mSpecialdayAddLin ) {
			mSpecialdayAddBtn.setPressed(true);
			callSpecialdayManager();
			this.dismiss();
			
		}   else if( v == mTodoAddLin ) {
			mTodoAddBtn.setPressed(true);
			callTodoManager();
			this.dismiss();
			
		}
	}    

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
    	ScheduleInfo info = new ScheduleInfo();
    	info 	= mList.get(position);
    	mGubun  = info.getScheduleGubun();
    	mID 	= info.getId();
    	mName 	= info.getScheduleName();
    	
    	//공유메시지
    	mMessage = ComUtil.makeScheduleMsg(mCtx, info, mFromDate);
    	
    	//gubun : S-> 스케줄  B: 국공일  U:기념일
    	if ( mGubun != null && ( mGubun.equals("S") || mGubun.equals("U") )) {
    		registerForContextMenu(listView);
    		openContextMenu(listView);
//    		unregisterForContextMenu(ExlistView);	    		
    	} else if ( mGubun != null && mGubun.equals("B")){
    		//없음
    	} else {	    		
    		ComUtil.showToast(mCtx, ComUtil.getStrResource(mCtx, R.string.title_more));
    	}
		
	}
    /*
     * menu create (popup dialog : onMenuItemSelected 추가)
     */
	@Override
	public boolean onMenuItemSelected(int aFeatureId, MenuItem aMenuItem) {  
		if (aFeatureId == Window.FEATURE_CONTEXT_MENU)  { 
			return onContextItemSelected(aMenuItem);  
		} else  {
			return super.onMenuItemSelected(aFeatureId, aMenuItem); 
		}
	}   
	
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean  ret = super.onPrepareOptionsMenu(menu);    	
		return ret;
	}
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	
    	super.onCreateContextMenu(menu, view, menuInfo);   		
    	
    	menu.setHeaderIcon(R.drawable.sm_menu_more);
    	MenuInflater inflater = ((Activity) mCtx).getMenuInflater(); 
    	if ( mGubun != null && mGubun.equals("U")){
    		menu.setHeaderTitle( mName );    		
    		inflater.inflate(R.menu.specialday_ctx_menu, menu);
    	} else {
    		menu.setHeaderTitle( mName );
    		inflater.inflate(R.menu.schedule_ctx_menu, menu);
    	}
        
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	MenuHandler menuHd = new MenuHandler(mCtx);

    	this.dismiss();
		
    	switch(item.getItemId()) {
    	case R.id.menu_schedule_modify:
    		callScheduleManager(ComUtil.getStrResource(mCtx, R.string.modify));
    		return true; 
	    case R.id.menu_schedule_copy:
	    	callScheduleManager(ComUtil.getStrResource(mCtx, R.string.copy));
	        return true; 
	    case R.id.menu_schedule_delete:
	    	callScheduleManager(ComUtil.getStrResource(mCtx, R.string.delete));
	        return true;  
	    case R.id.menu_specialday_modify:
	    	callSpecialdayManager(ComUtil.getStrResource(mCtx, R.string.modify));
      		return true; 
	    case R.id.menu_specialday_copy:
	    	callSpecialdayManager(ComUtil.getStrResource(mCtx, R.string.copy));
	        return true; 
	    case R.id.menu_specialday_delete:
	    	callSpecialdayManager(ComUtil.getStrResource(mCtx, R.string.delete));
	        return true; 	        
	    case R.id.menu_kakaotalk:	    	
	    	menuHd.linkToKakaoTalk( mMessage, "");
	        return true;
	    case R.id.menu_evernote: 
	    	menuHd.linkToEverNote( mName, mMessage );
	        return true;	
	    case R.id.menu_sms:	
	    	menuHd.callSMSView( mMessage);
	        return true;	        
	    }         
    	
        return super.onContextItemSelected(item);

    }	
    private void fillData() {
    	
    	// issue : 데이터건수에 따른 처리 필요 
    	// 1. 전체스케줄 가져오기 2.사용자id에 대해 가져오기
    	//    -. parent : string, child : scheduleinfo array
    	
    	mList = new ArrayList<ScheduleInfo>();
    	
        mAdapter = new SchedulePopUpAdapter(mCtx, R.layout.schedule_popup_row, mList);
        listView.setAdapter(mAdapter);
        
    	getSpecialdayPerDate(mFromDate);
    	getSchedulePerDate(mFromDate);
    	
		mAdapter.notifyDataSetChanged();
		
		//title 날짜 및 음력일자 setting
		int cnt = mAdapter.getCount();
		StringBuffer title = new StringBuffer();
		title.append(SmDateUtil.getDateFullFormat(mCtx, mFromDate.substring(4), true, true));
		if ( cnt > 0) {
			title.append(" (");
			title.append(Integer.toString(cnt));
			title.append(")");			
		}
		tvDate.setText(title);
		
		//음력일자
        LunarDataDbAdaper mLunarDbHelper = new LunarDataDbAdaper(mCtx);
        mLunarDbHelper.open();
		String lunar[] = LunarDataDbAdaper.getSolarToLunar(mCtx, mLunarDbHelper, mFromDate);
		mLunarDbHelper.close();
		
		StringBuffer lunardate = new StringBuffer();
		if ( lunar[0] != null && lunar[0].length() == 8 ) {
			if ( lunar[1] != null && lunar[1].equals("2") ) {
				lunardate.append( "(" );
				lunardate.append( ComUtil.getStrResource(mCtx, R.string.yun) );
				lunardate.append( ")" );
			}
			lunardate.append( SmDateUtil.getDateSimpleFormat(mCtx, lunar[0].substring(4), ".", false));
						
		}
		
		//음력은 한중 경우만
		if ( ComConstant.LOCALE.equals(ComConstant.LOCALE_KO) ||
			 ComConstant.LOCALE.equals(ComConstant.LOCALE_ZH) ||
			 ComConstant.LOCALE.equals(ComConstant.LOCALE_JA) ) {
			tvLunar.setText(lunardate.toString());
			
		} else {
			tvLunar.setText("");	
		}		

    }
    
    /*
     * 호출화면 : 스케줄등록(ScheduleManager)
     */
	private void callScheduleManager( ) { 
		String date = SmDateUtil.getDateFormat(mYear, mMonth, mDay);

		Bundle bundle = new Bundle();
		if ( date != null && !date.trim().equals("")) {
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, date);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, date);
			bundle.putString(ComConstant.SCHEDULE_GUBUN, ComUtil.getStrResource(mCtx, R.string.add));
		}
        Intent mIntent = new Intent(mCtx, ScheduleManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	}	
	private void callScheduleManager( String gubun ) { 
		
		Bundle bundle = new Bundle();
		
		if ( mFromDate != null && !mFromDate.trim().equals("")) { 
			bundle.putLong(ScheduleDbAdaper.KEY_SCHDULEID, mID);  			
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, mFromDate);
			bundle.putString(ComConstant.SCHEDULE_GUBUN, gubun);
		}
        Intent mIntent = new Intent(mCtx, ScheduleManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	}
    /*
     * 호출화면 : 스케줄리스트-tab
     */	
	private void callScheduleTabForDate( ) { 
		
		String sDate = SmDateUtil.getDateFormat(mYear, mMonth, mDay);
		
		Bundle bundle = new Bundle();
		if ( sDate != null && !sDate.trim().equals("")) {
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, sDate);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, sDate);
		}
        Intent mIntent = new Intent(mCtx, ScheduleTabForDate.class);
        mIntent.putExtras(bundle);	
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_SELECT);
	}	
    /*
     * 호출화면 : 주간스케줄
     */	
	private void callScheduleDayOfWeek( ) { 
		
		String sDate = SmDateUtil.getDateFormat(mYear, mMonth, mDay);
		
		Bundle bundle = new Bundle();
		if ( sDate != null && !sDate.trim().equals("")) {
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, 	sDate);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, 		sDate);
		}
        Intent mIntent = new Intent(mCtx, ScheduleDayOfWeek.class);
        mIntent.putExtras(bundle);	
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_SELECT);
	}	
    /*
     * 호출화면 : 기념일등록(SpecialdayManager)
     */	
    
	private void callSpecialdayManager(  ) { 
		String year = ComUtil.setBlank(mYear);
		String monthday = SmDateUtil.getMonthDayFormat(mMonth, mDay);
		Bundle bundle = new Bundle();
		if ( monthday != null && !monthday.trim().equals("")) {
			bundle.putString(SpecialDayDbAdaper.KEY_YEAR, year);
			bundle.putString(SpecialDayDbAdaper.KEY_MONTHDAY, monthday);
			bundle.putString(ComConstant.SPECIAL_GUBUN, ComUtil.getStrResource(mCtx, R.string.add));
		}		
        Intent mIntent = new Intent(mCtx, SpecialdayManager.class);
        mIntent.putExtras(bundle);	
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
	}
	private void callSpecialdayManager( String gubun ) { 

		Bundle bundle = new Bundle();
		
		if ( mFromDate != null && !mFromDate.trim().equals("")) { 
			bundle.putLong(SpecialDayDbAdaper.KEY_ID, mID);
			bundle.putString(ComConstant.SPECIAL_GUBUN, gubun);
		}
        Intent mIntent = new Intent(mCtx, SpecialdayManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	}
    /*
     * 호출화면 : todo
     */	
	private void callTodoManager( ) { 

		Bundle bundle = new Bundle();
		bundle.putString(ComConstant.SPECIAL_GUBUN, ComUtil.getStrResource(mCtx, R.string.add));
		
        Intent mIntent = new Intent(mCtx, TodoManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) mCtx).startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	}   
	/*
     * 일별 스케줄 db에서 조회 
     */
    private void getSchedulePerDate( String scheduledate ) {
    	
    	// issue : 데이터건수에 따른 처리 필요 (to-do)
    	// 1. 일자별 스케줄 db select -> scheduleinfo에 set
    	// 2. ExpandedView에 setting
    	//    -. parent : string,  child : scheduleinfo array
    	Cursor cur;
    	Calendar iCal = Calendar.getInstance( ) ;
    	
    	//일별 스케줄 정보 db select
    	ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(mCtx);
        mDbHelper.open();  
    	cur = mDbHelper.fetchSchedulePerDate(scheduledate);
    	mDbHelper.close();
    	
    	UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(mCtx);
        mDbUser.open();
        
        //data setting
    	int arrayLength = cur.getCount();    	
		for(int i = 0 ; i < arrayLength ; i++)
		{
			ScheduleInfo info = new ScheduleInfo();
			long userid = cur.getLong((cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_USERID)));
			info.setUserId(userid);
			info.setUserName(UsermanagerDbAdaper.getUserName(mCtx, mDbUser, userid));
			info.setUserColor(UsermanagerDbAdaper.getUserColor(mCtx, mDbUser, userid));
			long scheduleid = cur.getLong((cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SCHDULEID)));
			info.setId(scheduleid);				
			info.setScheduleName(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_NAME)));
			
			String sCycle = cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_CYCLE));
			info.setCycle(sCycle);
			
			info.setStartDate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTDATE)));
			info.setEndDate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDDATE)));
			info.setStartTime(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTTIME)));
			info.setEndTime(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDTIME)));	
			info.setAlarmYn(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM)));
			info.setAllDayYn(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALLDAYYN)));

			info.setAlarm2(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM2)));
			info.setRepeatdate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_REPEATDATE)));
			
			//요일별 value set
			String sDayOfWeek = "";
			int[] arrDayofweek = new int [7];		
			if (sCycle != null && sCycle.trim().equals("Y")) {
						
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
				info.setDayOfWeek(arrDayofweek);
				
				sDayOfWeek =  ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SUN)), ComUtil.getStrResource(mCtx, R.string.sunday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_MON)), ComUtil.getStrResource(mCtx, R.string.monday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_TUE)), ComUtil.getStrResource(mCtx, R.string.tuesday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_WEN)), ComUtil.getStrResource(mCtx, R.string.wednesday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_THU)), ComUtil.getStrResource(mCtx, R.string.thursday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_FRI)), ComUtil.getStrResource(mCtx, R.string.friday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SAT)), ComUtil.getStrResource(mCtx, R.string.saturday)).substring(0,1);
			}				
			info.setDayOfWeekFullText(sDayOfWeek);
			
			
			String dbstartdate = info.getStartDate();
			String dbenddate   = info.getEndDate();		
			//매주  : 요일 체크
			//없음  : 기간전부
			String [] loop ;			
			if ( sCycle  != null && sCycle.equals("Y")) {
				loop = SmDateUtil.getDateFromDayOfWeekArr(arrDayofweek, scheduledate, scheduledate, dbstartdate, dbenddate, iCal);
			} else if ( sCycle  != null && sCycle.equals("M")) {
				loop = SmDateUtil.getDateFromEveryMonth(info.getRepeatdate(), scheduledate, scheduledate, dbstartdate, dbenddate, iCal);
			} else {
				loop = new String [] { scheduledate };				
			}

			//해당되는 날짜만큼 처리
			//ExpendableList 객체에 데이터 add (정규스케줄의 경우 한스케줄에 대해 여러일자가 도출될수 있음)
			if ( loop != null ) {
				int len = loop.length;
				for ( int idate = 0 ; idate < len ; idate++ ) {
					info.setScheduleDate(loop[idate].toString());
					info.setScheduleGubun("S");
					mAdapter.add(info);
				}
			}
			//다음건 처리
			cur.moveToNext();
		}
		if ( cur != null ) cur.close();
		mDbUser.close();
		
    }
    /*
     * 일별 기념일, 공휴일조회
     */
    private void getSpecialdayPerDate( String scheduledate ) {

    	Cursor cursol = null;
    	Cursor curlun = null;
    	int arrayLength = 0;
    	
    	SpecialDayDbAdaper mDbSpecialHelper = new SpecialDayDbAdaper(mCtx);
    	mDbSpecialHelper.open();  
    	
    	String year = scheduledate.substring(0, 4);
    	String monthday = scheduledate.substring(4);
    	
    	//양력
    	if ( scheduledate != null && scheduledate.length() == 8 ) {
    		cursol = mDbSpecialHelper.fetchSpecialDayForSolar( year, monthday );
    	}
    	//음력
    	if ( scheduledate != null && scheduledate.length() == 8 ) {
    		LunarDataDbAdaper dbadapter = new LunarDataDbAdaper(mCtx);
    		dbadapter.open();
    		String [] sDate = LunarDataDbAdaper.getSolarToLunar(mCtx, dbadapter, scheduledate);
    		year = SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_YEAR);
    		monthday = SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_MONTH)
    				+  SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_DAY);
    		curlun = mDbSpecialHelper.fetchSpecialDayForLunar( year, monthday, sDate[1]);
    		dbadapter.close();
    	}     	
        //data setting
    	//양력(국공일,기념일 모두 포함)  -> 국공일의 경우 local 체크
    	arrayLength = cursol.getCount();
		for(int i = 0 ; i < arrayLength ; i++)
		{
			String locale 	= ComUtil.setBlank(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LOCALE)));
			String gubun 	= ComUtil.setBlank(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_GUBUN)));
			//국공일중 국가코드가 동일한 건만 처리
			if ( locale != null && gubun != null && gubun.trim().equals(ComConstant.PUT_BATCH)
					&& !locale.trim().equals(ComConstant.NATIONAL)) {
				/////////
			} else {
				ScheduleInfo info = new ScheduleInfo();
				long id = cursol.getLong((cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
				info.setId(id);
				info.setScheduleName(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME)));
				info.setSubName(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME)));
				info.setScheduleDate(scheduledate);
				info.setCycle(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_REPEATYN)));
				info.setHolidayYn(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_HOLIDAYYN)));
				info.setScheduleGubun(gubun);
				//양력인 경우 입력한 날짜
				String syear = cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_YEAR));
				String smonthday = cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MONTHDAY));
				
				String fulltext = ComUtil.getSpinnerText(mCtx, R.array.arr_leap_key, R.array.arr_leap, "0")
								+ " " 
								+ SmDateUtil.getDateFullFormat(mCtx, syear + smonthday, false, false );
				info.setDayOfWeekFullText(fulltext);
				
				mAdapter.add(info);				
			}

			//다음건 처리
			cursol.moveToNext();
		}
    	//음력
    	arrayLength = curlun.getCount();
		for(int i = 0 ; i < arrayLength ; i++)
		{
			ScheduleInfo info = new ScheduleInfo();
			long id = curlun.getLong((curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
			info.setId(id);
			info.setScheduleName(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME)));
			info.setSubName(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME)));
			info.setScheduleDate(scheduledate);
			info.setCycle(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_REPEATYN)));
			info.setScheduleGubun(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_GUBUN)));
			info.setHolidayYn(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_HOLIDAYYN)));
			
			//음력인 경우 임시로 음력일자 setting
			String syear = curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_YEAR));
			String smonthday = curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MONTHDAY));
			String sleap = curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LEAP));
			String fulltext = ComUtil.getSpinnerText(mCtx, R.array.arr_leap_key, R.array.arr_leap, sleap)
							+ " " 
							+ SmDateUtil.getDateFullFormat( mCtx, syear + smonthday, false, false );
			
			info.setDayOfWeekFullText(fulltext);
			
			mAdapter.add(info);
			//다음건 처리
			curlun.moveToNext();
		}	
		if ( cursol != null ) cursol.close();
		if ( curlun != null ) curlun.close();
		//mDbUser.close();
		mDbSpecialHelper.close();
    }    
}		
