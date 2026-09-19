package com.waveapp.smcalendarlite;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.waveapp.smcalendarlite.adapter.ScheduleExpListAdapter2;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 특정날짜 스케줄을 customList형태로 보여준다
 * --> 기간도 적용할지는 생각... 을...
 * default : today from calendar
 * parameter : if calendar click  -> click date
 */
public class ScheduleListForDate extends SMActivity  
				implements OnGroupClickListener, OnChildClickListener, OnClickListener{

    String mFromDate, mToDate;
    Long mUserid;
    
    ScheduleExpListAdapter2 mAdapter;
    ExpandableListView ExlistView; 
    Long 	mID  ;
    String  mName;
    String  mMessage;
    String  mGubun;
  	
    public ScheduleListForDate() {
		super();		
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);  
        
        setContentView(R.layout.schedule_datelist);
        ExlistView = (ExpandableListView) findViewById(R.id.list_schedule);
        ExlistView.setGroupIndicator(null);
        
        //default  : today
        mFromDate 	= SmDateUtil.getTodayDefault() ;
        mToDate 	= SmDateUtil.getTodayDefault() ;
        
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mUserid 	= savedInstanceState.getLong(ScheduleDbAdaper.KEY_USERID);
        	mFromDate 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_STARTDATE);
        	mToDate 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_ENDDATE);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	mUserid 	= extras.getLong(ScheduleDbAdaper.KEY_USERID);
        	mFromDate 	= extras.getString(ScheduleDbAdaper.KEY_STARTDATE);
        	mToDate 	= extras.getString(ScheduleDbAdaper.KEY_ENDDATE);
        }
        
        //mFromDate~mToDate 까지의 일별 스케줄 
        fillData();
        
        allChildListExpand();
        
        ExlistView.setOnGroupClickListener(this);
        ExlistView.setOnChildClickListener(this); 
     
    }

    private void fillData() {
    	
    	ArrayList<String> parent = new ArrayList<String>();
    	ArrayList<ArrayList<ScheduleInfo>> child = new ArrayList<ArrayList<ScheduleInfo>>();
    	Calendar iCal = Calendar.getInstance();
    	//adaper setting
    	mAdapter = new ScheduleExpListAdapter2(this, parent, child);
        ExlistView.setAdapter(mAdapter);
 
    	//mFromDate ~ mToDate 만큼 loop 
    	String [] sDateArr = SmDateUtil.getDateFromDate(mFromDate, mToDate, mFromDate, mToDate, iCal);
    	
    	int arrayLength = sDateArr.length;
    	for(int i = 0 ; i < arrayLength ; i++) {
    		String scheduledate = sDateArr[i].toString();
    		
    		//일자별스케줄 조회후 view에 setting
    		// 1.기념일,휴일
    		// 2.스케줄
    		getSpecialdayPerDate( scheduledate );
    		getSchedulePerDate( scheduledate );
    		//데이터가 한건도 없을때는 날짜title만
    		if ( mAdapter.getGroupCount() == 0 )  {
    			ScheduleInfo info = new ScheduleInfo();
    			info.setScheduleDate(mFromDate);
    			mAdapter.addItem(info);
    		}
    		
    	}
    	mAdapter.notifyDataSetChanged();
    	
        
        if ( mAdapter.getGroupCount() == 1 ) {
        	ComConstant.CNT_DAYSCHEDULE = mAdapter.getChildrenCount(0) ;
        } else if ( mAdapter.getGroupCount() > 1 ||  mAdapter.getGroupCount() == 0 ) {
        	ComConstant.CNT_DAYSCHEDULE = 0 ;
        }    	

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
    	ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open();  
    	cur = mDbHelper.fetchSchedulePerDate(scheduledate);
    	mDbHelper.close();
    	
    	UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
        mDbUser.open();
        
        //data setting
    	int arrayLength = cur.getCount();    	
		for(int i = 0 ; i < arrayLength ; i++)
		{
			ScheduleInfo info = new ScheduleInfo();
			long userid = cur.getLong((cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_USERID)));
			info.setUserId(userid);
			info.setUserName(UsermanagerDbAdaper.getUserName(this, mDbUser, userid));
			info.setUserColor(UsermanagerDbAdaper.getUserColor(this, mDbUser, userid));
			long scheduleid = cur.getLong((cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SCHDULEID)));
			info.setId(scheduleid);				
			info.setScheduleName(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_NAME)));
			
			String sCycle = cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_CYCLE));
			info.setCycle(sCycle);
			
			info.setStartDate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTDATE)));
			info.setEndDate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDDATE)));
			info.setStartTime(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTTIME)));
			info.setEndTime(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDTIME)));

			String alarm1 = cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM));
			String alarm2 = cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM2));
			if (( alarm1 != null && !alarm1.trim().equals("")) ||( alarm2 != null && !alarm2.trim().equals(""))){
				info.setAlarmYn("Y");
			} else {
				info.setAlarmYn("");
			}

			info.setAllDayYn(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALLDAYYN)));
			info.setTel(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_TEL1)));
			info.setMemo(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_MEMO)));
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
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SUN)), ComUtil.getStrResource(this, R.string.sunday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_MON)), ComUtil.getStrResource(this, R.string.monday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_TUE)), ComUtil.getStrResource(this, R.string.tuesday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_WEN)), ComUtil.getStrResource(this, R.string.wednesday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_THU)), ComUtil.getStrResource(this, R.string.thursday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_FRI)), ComUtil.getStrResource(this, R.string.friday)).substring(0,1)
					+ "" + ComUtil.setYesReturnValue(
								cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SAT)), ComUtil.getStrResource(this, R.string.saturday)).substring(0,1);
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
					mAdapter.addItem(info);
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
    	
    	SpecialDayDbAdaper mDbSpecialHelper = new SpecialDayDbAdaper(this);
    	mDbSpecialHelper.open();  
    	
    	String year = scheduledate.substring(0, 4);
    	String monthday = scheduledate.substring(4);
    	
    	//양력
    	if ( scheduledate != null && scheduledate.length() == 8 ) {
    		cursol = mDbSpecialHelper.fetchSpecialDayForSolar( year, monthday );
    	}
    	//음력
    	if ( scheduledate != null && scheduledate.length() == 8 ) {
    		LunarDataDbAdaper dbadapter = new LunarDataDbAdaper(this);
    		dbadapter.open();
    		String [] sDate = LunarDataDbAdaper.getSolarToLunar(this, dbadapter, scheduledate);
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
				info.setAlarmYn(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ALARM)));
//				info.seta(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME)));
				
				//양력인 경우 입력한 날짜
				String syear = cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_YEAR));
				String smonthday = cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MONTHDAY));
				
				String fulltext = ComUtil.getSpinnerText(this, R.array.arr_leap_key, R.array.arr_leap, "0")
								+ " " 
								+ SmDateUtil.getDateFullFormat(this, syear + smonthday, false, false );
				info.setDayOfWeekFullText(fulltext);
				
				mAdapter.addItem(info);				
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
			info.setAlarmYn(curlun.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ALARM)));
			
			//음력인 경우 임시로 음력일자 setting
			String syear = curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_YEAR));
			String smonthday = curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MONTHDAY));
			String sleap = curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LEAP));
			String fulltext = ComUtil.getSpinnerText(this, R.array.arr_leap_key, R.array.arr_leap, sleap)
							+ " " 
							+ SmDateUtil.getDateFullFormat( this, syear + smonthday, false, false );
			
			info.setDayOfWeekFullText(fulltext);
			
			mAdapter.addItem(info);
			//다음건 처리
			curlun.moveToNext();
		}	
		if ( cursol != null ) cursol.close();
		if ( curlun != null ) curlun.close();
		//mDbUser.close();
		mDbSpecialHelper.close();
    }
        
    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	outState.putSerializable(ScheduleDbAdaper.KEY_USERID, mUserid);
    	outState.putSerializable(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
    	outState.putSerializable(ScheduleDbAdaper.KEY_ENDDATE, mToDate);
    }
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
        fillData();
        allChildListExpand();
    }	    
    @Override    
    protected void onResume() {        
    	super.onResume(); 
    }        
    @Override    
    protected void onPause() {        
    	super.onPause();
    }
    @Override 
    protected void onDestroy() { 	
        super.onDestroy();
        RecycleUtil.recursiveRecycle(ExlistView);
        if ( mAdapter != null ) mAdapter = null;
    }
    /*
     * menu create
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        return true;
//    }
//    
//    @Override
//    public boolean onOptionsItemSelected( MenuItem item) {
//    	MenuHandler menu = new MenuHandler(this);
//    	boolean ret = menu.onMenuItemEvent ( item );    
//        return ret;
//    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	menu.setHeaderIcon(R.drawable.sm_menu_more);
    	MenuInflater inflater = getMenuInflater(); 
    	
    	if ( mGubun != null && mGubun.equals("U")){
    		//menu.setHeaderTitle(getResources().getString(R.string.title_specialday));
    		menu.setHeaderTitle( mName );    		
    		inflater.inflate(R.menu.specialday_ctx_menu, menu);
    	} else {
    		//menu.setHeaderTitle(getResources().getString(R.string.title_schedule));	
    		menu.setHeaderTitle( mName );
    		inflater.inflate(R.menu.schedule_ctx_menu, menu);
    	}
    	
    	super.onCreateContextMenu(menu, view, menuInfo);
    	
    }
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
		
	}       
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	MenuHandler menuHd = new MenuHandler(this);
    	
    	switch(item.getItemId()) {
//        case R.id.menu_schedule_new:
//        	mID = (long)0;
//        	callScheduleManager(ComUtil.getStrResource(this, R.string.add));
//            return true;
        case R.id.menu_schedule_modify:
        	callScheduleManager(ComUtil.getStrResource(this, R.string.modify));
            return true; 
	    case R.id.menu_schedule_copy:
	    	callScheduleManager(ComUtil.getStrResource(this, R.string.copy));
	        return true; 
	    case R.id.menu_schedule_delete:
	    	callScheduleManager(ComUtil.getStrResource(this, R.string.delete));
	        return true;  
//        case R.id.menu_specialday_new:
//        	mID = (long)0;
//        	callSpecialdayManager(ComUtil.getStrResource(this, R.string.add));
//            return true;
        case R.id.menu_specialday_modify:
        	callSpecialdayManager(ComUtil.getStrResource(this, R.string.modify));
            return true; 
	    case R.id.menu_specialday_copy:
	    	callSpecialdayManager(ComUtil.getStrResource(this, R.string.copy));
	        return true; 
	    case R.id.menu_specialday_delete:
	    	callSpecialdayManager(ComUtil.getStrResource(this, R.string.delete));
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


    /*
     * 호출화면 : 스케줄등록(ScheduleManager)
     */
	private void callScheduleManager( String gubun ) { 
		
		Bundle bundle = new Bundle();
		
		if ( mFromDate != null && !mFromDate.trim().equals("")) { 
			bundle.putLong(ScheduleDbAdaper.KEY_SCHDULEID, mID);  			
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, mToDate);
			bundle.putString(ComConstant.SCHEDULE_GUBUN, gubun);
		}
        Intent mIntent = new Intent(this, ScheduleManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	}	
    /*
     * 호출화면 : 기념일등록(SpecialdayManager)
     */
	private void callSpecialdayManager( String gubun ) { 

		Bundle bundle = new Bundle();
		
		if ( mFromDate != null && !mFromDate.trim().equals("")) { 
			bundle.putLong(SpecialDayDbAdaper.KEY_ID, mID);
			bundle.putString(ComConstant.SPECIAL_GUBUN, gubun);
		}
        Intent mIntent = new Intent(this, SpecialdayManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
	}
	private void allChildListExpand() {

		if ( mAdapter.getChildrenCount(0) == 0 ) return;
		int len = mAdapter.getGroupCount();
		for(int i = 0 ; i < len ; i++) {
			ExlistView.expandGroup(i);			
		}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
        allChildListExpand();

    }
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onChildClick(ExpandableListView arg0, View v, int groupposition,
			int childposition, long id) {
	    	
	    	ScheduleInfo info = new ScheduleInfo();
	    	info = (ScheduleInfo) mAdapter.getChild(groupposition, childposition);
	    	mGubun  = info.getScheduleGubun();
	    	mID 	= info.getId();
	    	mName 	= info.getScheduleName();
	    	
	    	//공유메시지
	    	mMessage = ComUtil.makeScheduleMsg(this, info, mFromDate);
	    	
	    	//gubun : S-> 스케줄  B: 국공일  U:기념일
	    	if ( mGubun != null && ( mGubun.equals("S") || mGubun.equals("U") )) {
	    		registerForContextMenu(ExlistView);
	    		openContextMenu(ExlistView);
//	    		unregisterForContextMenu(ExlistView);	    		
	    	} else if ( mGubun != null && mGubun.equals("B")){
	    		//없음
	    	} else {	    		
	    		ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.title_more));
	    	}
		    
		    
			return false;
	}
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// click 안되게 만듦(클릭시 이미지반전 처리필요)
		
		return true;
	}
//	@Override
//	public boolean onLongClick(View v) {
//		//Toast.makeText(this, "main onLongClick call ", Toast.LENGTH_SHORT).show();
//		return false;
//	}

}
