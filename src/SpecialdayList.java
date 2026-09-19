package com.waveapp.smcalendarlite;


import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.adapter.SpecialdayExpListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

public class SpecialdayList extends SMActivity  
						implements OnGroupClickListener, OnChildClickListener , OnClickListener{

	LinearLayout viewroot;
	
    //ArrayList<SpecialDayInfo> mList;
    SpecialdayExpListAdapter mAdapter;
    ExpandableListView ExlistView; 
    //ListView listView;
    Calendar m_Calendar;
    
	TextView tvs[] ;
	ImageButton btns[] ;
	 
    TextView m_yearTv ;				/// 년 표시용 텍스트
    
    Long mId;
    String mName;
    String mFromYear;
    String mMessage;
    
    ImageButton mAddBtn ;
    LinearLayout mAddLin;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //2.View ID Set
        setContentView(R.layout.specialday_exlist);
       
        //2-2.body
        //release 할 전체 view
        viewroot = (LinearLayout)findViewById( R.id.viewroot ) ;

        ExlistView = (ExpandableListView) findViewById(android.R.id.list);
        ExlistView.setGroupIndicator(null);
        
        mAddBtn 	= (ImageButton)findViewById( R.id.add ) ;
        mAddLin 	= (LinearLayout)findViewById( R.id.lin_add ) ; 
        
        
        // 년 월 일 표시할 텍스트뷰(년도만)
        tvs = new TextView[3] ;
        tvs[0] = (TextView)findViewById( R.id.year ) ;
//        tvs[1] = (TextView)findViewById( R.id.month ) ;
//        tvs[2] = (TextView)findViewById( R.id.day ) ;
        
        // 누르면 년 월 일 조절할 버튼
        btns = new ImageButton[2] ;
        btns[0] = (ImageButton)findViewById( R.id.previousyear ) ;
        btns[1] = (ImageButton)findViewById( R.id.nextyear ) ;        

        //날짜버튼 setting
        setViewTarget( tvs ) ;       

        //parent parameter 값 setting
        getParameter( savedInstanceState ) ; 
        
        //list data
        fillData(); 
       
        //날짜 setting
        printView( ) ;  
        
        btns[0].setOnClickListener(this); 
        btns[1].setOnClickListener(this);       
        mAddLin.setOnClickListener(this);
        ExlistView.setOnChildClickListener(this);
        ExlistView.setOnGroupClickListener(this);

        //광고배너(cauly)
        BannerLink banner = new BannerLink();
        banner.callBannerLink(this); 
        
    }
    private void getParameter ( Bundle savedInstanceState ) {
        
        //default  : today
        mFromYear 	= SmDateUtil.getTodayDefault() ;
        
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mFromYear 	= savedInstanceState.getString(SpecialDayDbAdaper.KEY_YEAR);
        }	
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	mFromYear 	= extras.getString(ScheduleDbAdaper.KEY_STARTDATE);
        }
        
        int year = SmDateUtil.getDateToInt(mFromYear, ComConstant.GUBUN_YEAR);
        
        m_Calendar = Calendar.getInstance();
        m_Calendar.set(Calendar.YEAR, year);
        
    }
    
    @Override
	public void onClick(View v) {
		if ( v == btns[0] ) {
			preYear();
			fillData( ) ;
			printView( ) ;
		} else if ( v == btns[1] ) {
			nextYear();
			fillData( ) ;
			printView( ) ;
		} else if( v == mAddLin ) {
			mAddBtn.setPressed(true);
			mId = (long)0;        	
        	callSpecialdayManager(ComUtil.getStrResource(this, R.string.add));
		}
	} 
    
	// 텍스트뷰를 넣어주면 각각 뿌려줌 (빈게 들어있으면 안뿌림)
    private void setViewTarget( TextView [] tv ) 
	{
		m_yearTv = tv[0] ;
	}
	
    //년 월 일을 출력해줌
    private void printView( )
	{
		/// 텍스트 뷰들이 있으면 그 텍스트 뷰에다가 년 월 일을 적어넣음
		if( m_yearTv != null )
			m_yearTv.setText( m_Calendar.get( Calendar.YEAR ) + "" ) ;

	}    
    
    //이전,다음버튼 click시 날짜값 update
	private void preYear ( )
	{
		m_Calendar.add( Calendar.YEAR, -1 ) ;
		mFromYear = SmDateUtil.getDateFromCal(m_Calendar);
	}
	private void nextYear( )
	{
		m_Calendar.add( Calendar.YEAR, 1 ) ;
		mFromYear = SmDateUtil.getDateFromCal(m_Calendar);
	} 
   
	
	/*
	 * 사용자등록 기념일 모두 조회
	 * -.단, 양력과 음력에 대한 process를 분기처리해야함...
	 *       음력의 경우 반복일정이 년도에 따라 월일이 매번 틀림
	 * -. 매년스케줄의 경우 가장 가까운 미래의 일정을 가져옴(양력 음력 동일)
	 * -. 조회기준 년도는 양력기준임 (음력의 경우 양력산출뒤 년도값 체크:다를경우 이전년도로 재산출)
	 */
    private void fillData() {
    	// issue : 데이터건수에 따른 처리 필요
    	// 1. 전체기념일 가져오기  
    	//    -. parent : string, child : specialday array 
    	
    	//현재년도(매년반복스케줄 산출시 사용) 기본 -> 변경가능
    	String thisyear = Integer.toString(m_Calendar.get(Calendar.YEAR));
    	
       	ArrayList<String> parent = new ArrayList<String>();
    	ArrayList<ArrayList<SpecialDayInfo>> child = new ArrayList<ArrayList<SpecialDayInfo>>();  

    	//adaper setting
    	mAdapter = new SpecialdayExpListAdapter(this, parent, child);
    	ExlistView.setAdapter(mAdapter);   
    	
        //기념일정보 조회
    	SpecialDayDbAdaper mDbHelper = new SpecialDayDbAdaper(this);
        mDbHelper.open(); 

    	Cursor cur = mDbHelper.fetchAllSpecialDayForType(ComConstant.PUT_USER, " ASC ");
    	mDbHelper.close();
//        startManagingCursor(cur);
        
    	//조회된 data fatch
        LunarDataDbAdaper mLunarDb = new LunarDataDbAdaper(this);
	    mLunarDb.open();
	    
	    int arrayLength = cur.getCount();
    	long id = 0;
    	ArrayList<SpecialDayInfo> infoList = new ArrayList<SpecialDayInfo>();
		for(int i = 0 ; i < arrayLength ; i++)
		{			
			SpecialDayInfo info = new SpecialDayInfo();
			id = cur.getLong((cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
			
			info.setId(id);
			info.setName(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME))));
			info.setSubName(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME))));
			info.setHolidayYn(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_HOLIDAYYN))));
			info.setGubun(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_GUBUN))));
	        info.setMemo(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MEMO))));
	        info.setUserGroup(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_USERGROUP))));
	                  
			String repeat = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_REPEATYN)));
			String monthday = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_MONTHDAY)));
	        String leap = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LEAP)));
	        info.setYear(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_YEAR))));
            String sEvent = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_EVENT)));
            String alarm = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ALARM)));
            
            info.setEvent(sEvent);
	        info.setRepeatYn(repeat);
			info.setMonthDay(monthday);
			info.setMakeThisDate();
			info.setLeap(leap);
			info.setAlarm(alarm);
			
			//음력산출 :입력된 일자 또는 매년 반복의 경우 올해년도로 산출
			String date = "";
	        if ( repeat != null && repeat.equals("Y")) {
	        	date = thisyear + monthday;
			} else {
				date = info.getYear() + monthday;
			} 			
			 
			String solardate = "";
			String otherSolardate = "";
			//양력날짜 setting
			if ( leap != null && ( leap.trim().equals("1") || leap.trim().equals("2") )) {
				//음력인 경우 양력산출
				solardate = SmDateUtil.getSolarFromDb( this, mLunarDb, leap, date ) ;
				
				//반복아니면
				if ( repeat != null && !repeat.equals("Y")) {
				//////////////////////////////
				//반복
				} else {
					//음력기념일이 양력 다음해로 넘어가는 경우가 있음 -> 년도값 다를 경우 이전년도 기념일 재산출... ㅎㄹㄹㄹ
					//--> 그리고 양력값이 2개 존재하는 경우도 있음... 예외적으로 1번더 처리 GR
					if ( solardate != null && solardate.length() == 8 && 
							!solardate.substring(0,4).equals(date.substring(0,4))) {
						String newyear = ComUtil.intToString(ComUtil.stringToInt(date.substring(0,4)) - 1);
						date = newyear + date.substring(4);
						//음력인 경우 양력산출
						solardate = SmDateUtil.getSolarFromDb( this, mLunarDb, leap, date ) ;
					} else {
						String newyear = ComUtil.intToString(ComUtil.stringToInt(date.substring(0,4)) - 1);
						date = newyear + date.substring(4);
						//음력인 경우 양력산출
						otherSolardate = SmDateUtil.getSolarFromDb( this, mLunarDb, leap, date ) ;				
					}					
				}

			} else {
				solardate = date;
			}
			

			if ( solardate != null && solardate.length() == 8 && SmDateUtil.isDate(solardate)) {
				//반복이 아닌경우 년도도 체크
				if ( repeat != null && !repeat.equals("Y")) {
//					if (info.getYear() != null && info.getYear().trim().equals(thisyear)) {
					if (solardate != null && solardate.substring(0,4).equals(thisyear)) {					
						info.setSolardate(solardate);
						info.setDDay(SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(solardate)));
						infoList.add(info);
					}	
				} else {	
					//(단,재산출된 양력과 조회년도가 일치할 경우만 처리)  -> 음력의 경우 다음해로 넘어가는 경우도 있음
					//--> 그리고 양력값이 2개 존재하는 경우도 있음... 예외적으로 1번더 처리 GR
					if (solardate != null && solardate.substring(0,4).equals(thisyear)) {
						info.setSolardate(solardate);
						info.setDDay(SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(solardate)));
						infoList.add(info);						
					}
					//이전해로 다시 구한 양력년도가 조회년도와 일치할 경우 add
					if (otherSolardate != null && otherSolardate.length() == 8 ) {
						if ( otherSolardate.substring(0,4).equals(thisyear)) {
							SpecialDayInfo otherinfo = new SpecialDayInfo();
							try {
								otherinfo = (SpecialDayInfo) info.clone();
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
							otherinfo.setSolardate(otherSolardate);
							otherinfo.setDDay(SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(otherSolardate)));
							infoList.add(otherinfo);
						}
					}

				}
			}
			
			cur.moveToNext();
		}
		//음력 때문에 정렬후 처리.... (후덜)
		Collections.sort(infoList ,myComparator);
		
		//ExpendableList 객체에 데이터 add
		int cnt = infoList.size();
		for ( int i = 0 ; i < cnt ; i++ ) {
			mAdapter.addItem(infoList.get(i));
		}
			
		if ( cur != null ) cur.close();
		mLunarDb.close();
		
		mAdapter.notifyDataSetChanged();

		allChildListExpand();
		
        int tot = ExlistView.getCount() - mAdapter.getGroupCount();
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_specialday, 
        		tot ), View.VISIBLE); 
 	
 
    }
        
    /*
     * menu create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.com_option_menu, menu);
        return true;
    }
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean  ret = super.onPrepareOptionsMenu(menu);  
        MenuHandler mh = new MenuHandler(this);
        mh.preMenuEvent(menu);  	
		return ret;
	}    
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
    	MenuHandler menu = new MenuHandler(this);
    	boolean ret = menu.onMenuItemEvent ( item );    
        return ret;
    }  
//    
//    @Override
//	public void onOptionsMenuClosed(Menu menu) {
//		super.onOptionsMenuClosed(menu);
//	} 
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
  
    	menu.setHeaderTitle( mName );
    	menu.setHeaderIcon(R.drawable.sm_menu_more);
    	MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.specialday_ctx_menu, menu);
        
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
//        case R.id.menu_specialday_new:
//        	mId = (long)0;        	
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
     * 호출화면 : 기념일등록(SpecialdayManager)
     */
	private void callSpecialdayManager( String gubun ) { 
		
		Bundle bundle = new Bundle();
		bundle.putLong(SpecialDayDbAdaper.KEY_ID, mId); 
		bundle.putString(ComConstant.SPECIAL_GUBUN, gubun);
		
        Intent mIntent = new Intent(this, SpecialdayManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);        
	}	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	outState.putSerializable(SpecialDayDbAdaper.KEY_YEAR, mFromYear);  
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
        RecycleUtil.recursiveRecycle(viewroot);
        RecycleUtil.recursiveRecycle(ExlistView);
        unregisterForContextMenu(ExlistView);
        if ( mAdapter != null ) mAdapter = null;
    }
	private void allChildListExpand() {
				
		if ( mAdapter.getChildrenCount(0) == 0 ) return;
		int len = mAdapter.getGroupCount();
		for(int i = 0 ; i < len ; i++) {
			ExlistView.expandGroup(i);
		}
    }
	@Override
	public boolean onChildClick(ExpandableListView arg0, View v, int groupposition,
			int childposition, long id) {
		
		SpecialDayInfo info = new SpecialDayInfo();
		info 	= (SpecialDayInfo) mAdapter.getChild(groupposition, childposition);
		
		mId 	= info.getId();
		mName 	= info.getName();
		mMessage = ComUtil.makeScheduleMsg(this, info );
		
		registerForContextMenu(ExlistView);
		openContextMenu(ExlistView);
		unregisterForContextMenu(ExlistView);
    		
		return false;
	}


	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// click 안되게 만듦(클릭시 이미지반전 처리필요)
		
		return true;
	}
	
	//Comparator 를 만든다. 
    private final static Comparator<SpecialDayInfo> myComparator 
    		= new Comparator<SpecialDayInfo>() { 
    	
        private final Collator   collator = Collator.getInstance(); 
        
        @Override 
	    public int compare(SpecialDayInfo fromobj,SpecialDayInfo toobj) { 
        	return collator.compare(fromobj.getSolardate(), toobj.getSolardate()); 
	    } 
	 }; 
}
