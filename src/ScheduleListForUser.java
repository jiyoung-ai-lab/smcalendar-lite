package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.DialogInterface;
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

import com.waveapp.smcalendarlite.adapter.ScheduleExpListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;
 
/*
 * 사용자별 하위 스케줄을 customList형태로 보여준다
 */
public class ScheduleListForUser extends SMActivity  
				implements OnGroupClickListener, OnChildClickListener , OnClickListener{

	Long mUserid;
    
	LinearLayout viewroot;
    ScheduleExpListAdapter mAdapter;
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
        
        setContentView(R.layout.schedule_userlist);
        
//        //기능제한여부(광고로 전환)
//        if ( !ComUtil.isFormallVer(this) ) {
//        	finish();
//        }
        
      //release 할 전체 view
        viewroot = (LinearLayout)findViewById( R.id.viewroot ) ;
        
        //옵션버튼
        btns = new TextView[3] ;
        btns[0] = (TextView)findViewById( R.id.beforeschedule ) ;
        btns[1] = (TextView)findViewById( R.id.afterschedule ) ;
        btns[2] = (TextView)findViewById( R.id.allschedule ) ;
        mOptionId = 0;
        setOptionBackGround();
        
        //버튼제어
        mDeleteBtn 	= (ImageButton)findViewById( R.id.deleteall ) ;
        mDeleteLin	= (LinearLayout)findViewById( R.id.lin_delete ) ;
        mAddBtn 	= (ImageButton)findViewById( R.id.add ) ;
        mAddLin 	= (LinearLayout)findViewById( R.id.lin_add ) ;

        ExlistView = (ExpandableListView) findViewById(R.id.list_schedule);
        ExlistView.setGroupIndicator(null);
        
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mUserid = savedInstanceState.getLong(ScheduleDbAdaper.KEY_USERID);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserid = extras.getLong(ScheduleDbAdaper.KEY_USERID);
        }

        fillData();
        
    	// activity button
    	setClickListener(); 

        //광고배너
        BannerLink banner = new BannerLink();
        banner.callBannerLink(this); 
        
        //sample-더보기
        /*
        TheMore the = new TheMore(this, schCursor);        
        LayoutInflater inflater = getLayoutInflater();
        
        View footer = inflater.inflate(R.layout.more_list, null);
        theMore =  (Button) footer.findViewById(R.id.moreButton);
        listView.addFooterView(footer);
        */
            
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
	        callScheduleManager(ComUtil.getStrResource(this, R.string.add));
		}
		
	}    
    /*
    private class TheMore extends CursorAdapter{
        Cursor c ; 
	  public TheMore(Context context, Cursor c) {
	   super(context, c);
	   this.c = c ;
	  }
    }
 */

    private void fillData() {
    	
    	// issue : 데이터건수에 따른 처리 필요 
    	// 1. 전체스케줄 가져오기 2.사용자id에 대해 가져오기
    	//    -. parent : string, child : scheduleinfo array
    	
       	ArrayList<String> parent = new ArrayList<String>();
    	ArrayList<ArrayList<ScheduleInfo>> child = new ArrayList<ArrayList<ScheduleInfo>>();  
    	
    	//adaper setting
    	mAdapter = new ScheduleExpListAdapter(this, parent, child);
    	ExlistView.setAdapter(mAdapter);

    	getSchedule();
    	
		mAdapter.notifyDataSetChanged();
		
		allChildListExpand();
		
        int tot = ExlistView.getCount() - mAdapter.getGroupCount();
        
        //공통 top menu setting       
        ComMenuHandler.callComTopMenuAction(this,  ComUtil.setActionTitle(this, R.string.title_allschedule, tot ), View.GONE);
		

    }
	/*
     * 전체 스케줄 db에서 조회 
     */
    private void getSchedule( ) {
    	
    	Cursor cur = null;
    	String today = SmDateUtil.getTodayDefault();
    	
    	ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open();       	
    	if ( mUserid != null && mUserid > (long)0) {
    		cur = mDbHelper.fetchUserSchedule(mUserid);
    	} else {

        	if ( mOptionId == 0 ) {
        		cur = mDbHelper.fetchBeforeSchedule(today);    
        	} else if ( mOptionId == 1  ) {
        		cur = mDbHelper.fetchAfterSchedule(today);    
        	} else if ( mOptionId == 2  ) {
        		cur = mDbHelper.fetchAllSchedule();    
        	}     		
    	}
    			
    	mDbHelper.close();

    	if ( cur == null ) return;
    	
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
			info.setScheduleId(scheduleid);		
			
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

			//ExpendableList 객체에 데이터 add
			mAdapter.addItem(info);
			
			//다음건 처리
			cur.moveToNext();
		}
		
		if ( cur != null ) cur.close();
		mDbUser.close();    	
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
        inflater.inflate(R.menu.schedule_ctx_menu, menu);
        
    	super.onCreateContextMenu(menu, view, menuInfo);    	
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
		
		bundle.putLong(ScheduleDbAdaper.KEY_SCHDULEID, mID);  
		bundle.putString(ComConstant.SCHEDULE_GUBUN, gubun);
		
        Intent mIntent = new Intent(this, ScheduleManager.class);
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
		
	    	ScheduleInfo info = new ScheduleInfo();
	    	info 	= (ScheduleInfo) mAdapter.getChild(groupposition, childposition);
	    	mID 	= info.getScheduleId();
	    	mName 	= info.getScheduleName();
	    	mMessage = ComUtil.makeScheduleMsg(this, info, "");
	    	
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
		
		ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open(); 
        
		ScheduleInfo info = new ScheduleInfo();
		delCnt = 0;
		//1.check list get
		if ( mAdapter.getChildrenCount(0) == 0 ) return;
		int glen = mAdapter.getGroupCount();
		for(int i = 0 ; i < glen ; i++) {
			int clen = mAdapter.getChildrenCount(i);
			for(int j = 0 ; j < clen ; j++) {
				info = (ScheduleInfo) mAdapter.getChild(i, j);
				if ( info.isChoice()) {
			    	mID = info.getScheduleId();	
			    	
			        deleteState(mDbHelper); 			    	
				}
			}
		}	
		
		//삭제된건수만큼 메시지 처리 및 알람재설정
		if ( delCnt > 0 ) {
			String msg = String.format(ComUtil.getStrResource(this, R.string.msg_deletecomplete));
    		ComUtil.showToast(this, Integer.toString(delCnt) + ComUtil.getStrResource(this, R.string.count) + " " + msg);	
    		
    		
    		fillData();
            
    		//알람기동(일정)
        	AlarmHandler ah = new AlarmHandler(this);
        	ah.setAlarmService();
        	
        	//위젯갱신
        	ViewUtil.updateAllWidget(this);
		}
		
		mDbHelper.close(); 
        
	}
    private void deleteState( ScheduleDbAdaper mDbHelper ) {   
        
    	boolean ret = mDbHelper.deleteSchedule(mID); 
    	
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

}
