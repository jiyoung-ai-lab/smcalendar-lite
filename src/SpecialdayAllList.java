package com.waveapp.smcalendarlite;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
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

import com.waveapp.smcalendarlite.adapter.SpecialdayAllListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;
 
/*
 * 그룹별 하위 기념일리스트를 customList형태로 보여준다
 */
public class SpecialdayAllList extends SMActivity  
				implements OnGroupClickListener, OnChildClickListener , OnClickListener{

	Long mUserid;
    
	LinearLayout viewroot;
    SpecialdayAllListAdapter mAdapter;
    ExpandableListView ExlistView; 
    
    TextView btns[];
    ImageButton mDeleteBtn;
    LinearLayout mDeleteLin;
    ImageButton mAddBtn;
    LinearLayout mAddLin;
    
    int mOptionId ;
    int delCnt ;
    
    Long 	mID  ;
    String  mName;
    String  mMessage;
    String  mMsgParm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);  
        
        setContentView(R.layout.specialday_alllist);
        
//        //기능제한여부(광고로 전환)
//        if ( !ComUtil.isFormallVer(this) ) {
//        	finish();
//        }
        
      //release 할 전체 view
        viewroot = (LinearLayout)findViewById( R.id.viewroot ) ;
        
        //옵션버튼
        btns = new TextView[3] ;
        btns[0] = (TextView)findViewById( R.id.beforespecialday ) ;
        btns[1] = (TextView)findViewById( R.id.afterspecialday ) ;
        btns[2] = (TextView)findViewById( R.id.allspecialday  ) ;
        mOptionId = 0;
        setOptionBackGround();
        
        //임시
        btns[1].setVisibility(View.GONE);
        btns[2].setVisibility(View.GONE);
        
        //버튼제어
        mDeleteBtn 	= (ImageButton)findViewById( R.id.deleteall ) ;
        mDeleteLin	= (LinearLayout)findViewById( R.id.lin_delete ) ;
        mAddBtn 	= (ImageButton)findViewById( R.id.add ) ;
        mAddLin 	= (LinearLayout)findViewById( R.id.lin_add ) ;

        ExlistView = (ExpandableListView) findViewById(R.id.list_specialday);
        ExlistView.setGroupIndicator(null);
        
//        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
//        if (savedInstanceState != null) {
//        	mUserid = savedInstanceState.getLong(SpecialDayDbAdaper.KEY_USERID);
//        }
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            mUserid = extras.getLong(SpecialDayDbAdaper.KEY_USERID);
//        }

        fillData();
        
    	// activity button
    	setClickListener(); 

        //광고배너
        BannerLink banner = new BannerLink();
        banner.callBannerLink(this); 

            
    }
    private void setClickListener () {
        
        ExlistView.setOnChildClickListener(this);
        ExlistView.setOnGroupClickListener(this);

    	
        //클릭이벤트
        btns[0].setOnClickListener(this); 
        btns[1].setOnClickListener(this);        
        btns[2].setOnClickListener(this);

        mDeleteLin.setOnClickListener(this);
        mAddLin.setOnClickListener(this);

    } 


	@Override
	public void onClick(View v) {
		
		if (( v == btns[0] ) ||  ( v == btns[1] ) || ( v == btns[2] )) {
			if ( v == btns[0] ) {				
				mOptionId = 0;				
			} else if ( v == btns[1] ) {				
				mOptionId = 1;				
			} else if ( v == btns[2] ) {				
				mOptionId = 2;				
			}
			setOptionBackGround();
	        fillData();	
	        
		} else if ( v == mDeleteLin) {
			mDeleteBtn.setPressed(true);
			if (mAdapter.getCheckCount() > 0 ) {
				showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);
			} else {
				ComUtil.showToast(this, ComUtil.getStrResource(this, R.string.msg_notselected));
			}
		} else if ( v == mAddLin ) {
			mAddBtn.setPressed(true);
			mID = (long)0;
	        callSpecialdayManager(ComUtil.getStrResource(this, R.string.add));
		}
		
	}    


    private void fillData() {
    	
    	// issue : 데이터건수에 따른 처리 필요 
    	// 1. 전체스케줄 가져오기 2.사용자id에 대해 가져오기
    	//    -. parent : string, child : scheduleinfo array
    	
       	ArrayList<String> parent = new ArrayList<String>();
    	ArrayList<ArrayList<SpecialDayInfo>> child = new ArrayList<ArrayList<SpecialDayInfo>>();  
    	
    	//adaper setting
    	mAdapter = new SpecialdayAllListAdapter(this, parent, child);
    	ExlistView.setAdapter(mAdapter);

    	getSpecialday();
    	
		mAdapter.notifyDataSetChanged();
		
		allChildListExpand();
		
        int tot = ExlistView.getCount() - mAdapter.getGroupCount();
        
        //공통 top menu setting       
        ComMenuHandler.callComTopMenuAction(this,  ComUtil.setActionTitle(this, R.string.title_allspecialday, tot ), View.GONE);
		

    }
    
    private void getSpecialday() {
    	// issue : 데이터건수에 따른 처리 필요
    	// 1. 전체기념일 가져오기  
    	//    -. parent : string, child : specialday array 
    	String today = SmDateUtil.getTodayDefault();
    	
       	ArrayList<String> parent = new ArrayList<String>();
    	ArrayList<ArrayList<SpecialDayInfo>> child = new ArrayList<ArrayList<SpecialDayInfo>>();  
    	
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
			String thisyear = today.substring( 0 , 4 ).toString();
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
		//d-day로 정렬
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
    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	outState.putSerializable(UsermanagerDbAdaper.KEY_USERID, mUserid); 
    }
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
        fillData();
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
        if ( mAdapter != null ) mAdapter = null;
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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

    	menu.setHeaderIcon(R.drawable.sm_menu_more);
    	menu.setHeaderTitle( mName );
    	MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.specialday_ctx_menu, menu);
        
    	super.onCreateContextMenu(menu, view, menuInfo);    	
    }
       
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	MenuHandler menuHd = new MenuHandler(this);
    	
    	switch(item.getItemId()) {
//        case R.id.menu_schedule_new:
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
	private void callSpecialdayManager( String gubun ) { 
		
		Bundle bundle = new Bundle();
		
		bundle.putLong(SpecialDayDbAdaper.KEY_ID, mID);  
		bundle.putString(ComConstant.SPECIAL_GUBUN, gubun);
		
        Intent mIntent = new Intent(this, SpecialdayManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);
        
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
    	MessageHandler msgHd = new MessageHandler(this);
    	Dialog di;
    	
    	switch (id) { 
    	case ComConstant.DIALOG_INPUTCHECK:
    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, "", R.string.msg_notselected, "");
    					
    		return di;
		case ComConstant.DIALOG_YES_NO_MESSAGE:
					
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_deleteconfirm, mMsgParm);
			di.setOnCancelListener( new DialogInterface.OnCancelListener() {
				
					@Override
					public void onCancel(DialogInterface dialog) { 
						deleteEvent();
					}
			});
					
			return di;
    	}    	
    return null;
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
    }

	@Override
	public boolean onChildClick(ExpandableListView arg0, View v, int groupposition,
			int childposition, long id) {
		
	    	SpecialDayInfo info = new SpecialDayInfo();
	    	info 	= (SpecialDayInfo) mAdapter.getChild(groupposition, childposition);
	    	mID 	= info.getId();
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

	private void deleteEvent() { 
		
		SpecialDayDbAdaper mDbHelper = new SpecialDayDbAdaper(this);
        mDbHelper.open(); 
        
		SpecialDayInfo info = new SpecialDayInfo();
		delCnt = 0;
		//1.check list get
		if ( mAdapter.getChildrenCount(0) == 0 ) return;
		int glen = mAdapter.getGroupCount();
		for(int i = 0 ; i < glen ; i++) {
			int clen = mAdapter.getChildrenCount(i);
			for(int j = 0 ; j < clen ; j++) {
				info = (SpecialDayInfo) mAdapter.getChild(i, j);
				if ( info.isChoice()) {
			    	mID = info.getId();
			    	
			        deleteState(mDbHelper); 			    	
				}
			}
		}	
		
		//삭제된건수만큼 메시지 처리 및 알람재설정
		if ( delCnt > 0 ) {
			String msg = String.format(ComUtil.getStrResource(this, R.string.msg_deletecomplete));
    		ComUtil.showToast(this, Integer.toString(delCnt) + ComUtil.getStrResource(this, R.string.count) + " " + msg);	
    		
    		
    		fillData();
//            
        	AlarmHandler ah = new AlarmHandler(this);
        	ah.setAlarmServiceForSpecialday();
        	
        	//위젯갱신
        	ViewUtil.updateAllWidget(this);
		}
		
		mDbHelper.close(); 
        
	}
    private void deleteState( SpecialDayDbAdaper mDbHelper ) {   
        
    	boolean ret = mDbHelper.deleteSpecialDay(mID); 
    	
    	if ( ret == true ) {
    		delCnt++;
    	}
    }

    private void setOptionBackGround () {
    	
    	int len = btns.length;
    	
    	for(int i = 0 ; i < len ; i++) {
    		if ( i == mOptionId) {    			
    			btns[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.sm_option_btn));
    			btns[i].setTextColor(getResources().getColor(R.color.listtop));
    		} else {
    			btns[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.sm_option_btn2));
    			btns[i].setTextColor(getResources().getColor(R.color.white));
    		}
    	}
    }
	//Comparator 를 만든다. 
    private final static Comparator<SpecialDayInfo> myComparator 
    		= new Comparator<SpecialDayInfo>() { 
    	
        private final Collator   collator = Collator.getInstance(); 
        
        @Override 
	    public int compare(SpecialDayInfo fromobj,SpecialDayInfo toobj) { 
        	//양력일자 기준
        	return collator.compare(fromobj.getSolardate(), toobj.getSolardate()); 
	    } 
	 }; 
}
