package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.waveapp.smcalendarlite.adapter.OtherCalendarListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.handler.OtherCalendarHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
 
/*
 * 다른 달력 스케줄을 customList형태로 보여준다
 */
public class SendOtherCalendar extends SMActivity implements OnClickListener{
	
	ArrayList<ScheduleInfo> mList;
    OtherCalendarListAdapter mAdapter;
    ListView listView;   
    TextView mOtherSchedule;  
    Spinner  	mUser; 
    
    LinearLayout mSendLin;
    ImageButton mSendBtn;
    TextView 	mSendTv;

    int sendCnt ;
    
    Long 	mID  ;
    String  mName;
    String  mMessage;
    String  mMsgParm;
    
    String  mCalendarChoice;

	Long 		mUserid;
    ArrayList<Long> userIdList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.othercalendar_list);
        
        //기능제한여부
        if ( !ComUtil.isFormallVer(this) ) {
        	finish();
        }
        listView 	= (ListView) findViewById(android.R.id.list);
        mOtherSchedule= (TextView) findViewById(R.id.tv_other); 
        mUser 		= (Spinner) findViewById(R.id.userspinner); 
        
        mSendLin 	= (LinearLayout)findViewById( R.id.lin_get ) ;
        mSendBtn 	= (ImageButton)findViewById( R.id.confirm ) ;
        mSendTv		= (TextView) findViewById(R.id.tv_confirm); 
        
        mUser.setVisibility(View.GONE);
        
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mCalendarChoice = savedInstanceState.getString(ComConstant.CALCHOICE_GUBUN);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCalendarChoice = extras.getString(ComConstant.CALCHOICE_GUBUN);
        }
        mOtherSchedule.setText(String.format(ComUtil.getStrResource(this, R.string.sendotherschedule), mCalendarChoice));
        mSendTv.setText(ComUtil.getStrResource(this, R.string.send));
        
        //
        fillData();
        
    	// activity button
    	setClickListener(); 
            
    }
    private void setClickListener () {
    	
        mSendLin.setOnClickListener(this);

    } 

	@Override
	public void onClick(View v) {
		
		if ( v == mSendLin ) {
			mSendBtn.setPressed(true);
			
			if (mAdapter.getCheckCount() > 0 ) {
				showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);
			} else {
				mMsgParm = ComUtil.getStrResource(this, R.string.msg_notselected) ;
				showDialog(ComConstant.DIALOG_INPUTCHECK);
			}
		}
		
	}   

    private void fillData() {
    	
    	// issue : 데이터건수에 따른 처리 필요 
    	// 1. 전체스케줄 가져오기 2.사용자id에 대해 가져오기
    	//    -. parent : string, child : scheduleinfo array
    	
    	mList = new ArrayList<ScheduleInfo>();
    	
        mAdapter = new OtherCalendarListAdapter(this, R.layout.othercalendar_row, mList);
        listView.setAdapter(mAdapter);
        
        getFromSmCalendar();
        
//        if ( mCalendarChoice != null && mCalendarChoice.equals("phone")) {
//        	
//        } else {
//        	//getFromGoogleCalendar();  	
//        	ComUtil.showToast(this, "Not Open");
//        }
    	
		mAdapter.notifyDataSetChanged();
		
        int tot = mAdapter.getCount();
        
        //공통 top menu setting       
        ComMenuHandler.callComTopMenuAction(this,  ComUtil.setActionTitle(this, R.string.title_sendotherschedule, tot ), View.GONE);
		

    }
	/*
     * 스케줄 db에서 조회 
     */
    private void getFromSmCalendar( ) {

//    	String today = DateUtil.getTodayDefault();
    	mList.clear();
    	
    	ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open();       	
        Cursor cur = mDbHelper.fetchAllSchedule();    
    			
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

			//other key set
			info.setOtherkind(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_OTHERKIND)));
			info.setOtherId(cur.getLong(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_OTHERID)));
			
			//List 객체에 데이터 add
			mList.add(info);
			
			//다음건 처리
			cur.moveToNext();
		}
		
		if ( cur != null ) cur.close();
		mDbUser.close();  
    	
    }

    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	//outState.putSerializable(UsermanagerDbAdaper.KEY_USERID, mUserid); 
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
        RecycleUtil.recursiveRecycle(listView);
        if ( mAdapter != null ) mAdapter = null;
    }

//    /*
//     * menu create
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater(); 
//        inflater.inflate(R.menu.com_option_menu, menu);
//        return true;
//    }
//    @Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//    	boolean  ret = super.onPrepareOptionsMenu(menu);  
//        MenuHandler mh = new MenuHandler(this);
//        mh.preMenuEvent(menu);  	
//		return ret;
//	}    
//    @Override
//    public boolean onOptionsItemSelected( MenuItem item) {
//    	MenuHandler menu = new MenuHandler(this);
//    	boolean ret = menu.onMenuItemEvent ( item );    
//        return ret;
//    }    
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
//
//    	menu.setHeaderIcon(R.drawable.sm_menu_more);
//    	menu.setHeaderTitle( mName );
//    	MenuInflater inflater = getMenuInflater(); 
//        inflater.inflate(R.menu.schedule_ctx_menu, menu);
//        
//    	super.onCreateContextMenu(menu, view, menuInfo);    	
//    }
//       
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//    	MenuHandler menuHd = new MenuHandler(this);
//    	
//    	switch(item.getItemId()) {
////        case R.id.menu_schedule_new:
////        	mID = (long)0;
////        	callScheduleManager(ComUtil.getStrResource(this, R.string.add));
////            return true;
//        case R.id.menu_schedule_modify:
//        	callScheduleManager(ComUtil.getStrResource(this, R.string.modify));
//            return true; 
//	    case R.id.menu_schedule_copy:
//	    	callScheduleManager(ComUtil.getStrResource(this, R.string.copy));
//	        return true; 
//	    case R.id.menu_schedule_delete:
//	    	callScheduleManager(ComUtil.getStrResource(this, R.string.delete));
//	        return true;  
//	    case R.id.menu_kakaotalk:	    	
//	    	menuHd.linkToKakaoTalk( mMessage, "");
//	        return true;
//	    case R.id.menu_sms:	  
//	    	menuHd.callSMSView( mMessage);
//	        return true;	        
//	    }         
//    	
//        return super.onContextItemSelected(item);
//
//    }

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
//    	case ComConstant.DIALOG_INPUTCHECK:
//    		di = msgHd.onCreateDialog(this, ComConstant.DIALOG_NO_MESSAGE, "", R.string.msg_notexist_delete, "");
//    					
//    		return di;
		case ComConstant.DIALOG_YES_NO_MESSAGE:
					
			mMsgParm = ComUtil.getStrResource(this, R.string.title_sendotherschedule);
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_finishconfirm, mMsgParm);
			di.setOnCancelListener( new DialogInterface.OnCancelListener() {
				
					@Override
					public void onCancel(DialogInterface dialog) { 
						confirmEvent();
					}
			});
					
			return di;
    	}    	
    	return null;
    }	


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

	/*
	 * confirm button event (google -> smcalendar)
	 */
	private void confirmEvent() { 
		
		ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open(); 
        
		ScheduleInfo info = new ScheduleInfo();
		sendCnt = 0;
		
		//1.check list get
		if ( mAdapter.getCount() == 0 ) return;
		
		int glen = mAdapter.getCount();
		for(int i = 0 ; i < glen ; i++) {
			info = (ScheduleInfo) mAdapter.getItem(i);
			if ( info.isChoice()) {
				//1.validation check
				if ( validationCheck(info) == false ) return;

			}
		}   
		for(int i = 0 ; i < glen ; i++) {
			info = (ScheduleInfo) mAdapter.getItem(i);
			if ( info.isChoice()) {
		    	saveState( info); 			    	
			}
		} 		
		//건수만큼 메시지 처리 및 알람재설정
		if ( sendCnt > 0 ) {
			String msg = String.format(ComUtil.getStrResource(this, R.string.msg_finishcomplete));
    		ComUtil.showToast(this, Integer.toString(sendCnt) + ComUtil.getStrResource(this, R.string.count) + " " + msg);	
    		
    		
    		fillData();
            
//        	AlarmHandler ah = new AlarmHandler(this);
//        	ah.setAlarmService();
		}
		  	
		mDbHelper.close();
//    	AlarmHandler ah = new AlarmHandler(this);
//    	ah.setAlarmService();
        
	}
    /*
     * Call Database ( 가져온 데이터 db에 insert)
     */
    private void saveState( ScheduleInfo info ) { 
    	
    	boolean ret = false;
    	OtherCalendarHandler calhd = new OtherCalendarHandler(this);
    	ret = calhd.insertCalendarData(info);
    	
    	if ( ret == true ) {
    		sendCnt++;
    	}
    }
	private Boolean validationCheck( ScheduleInfo info ) {

		mMsgParm = "";
		
		String schedulename = info.getScheduleName();
		//스케줄명 필수처리
		if (ComUtil.getNullCheck(schedulename) == false ){
			mMsgParm = ComUtil.getStrResource(this, R.string.err_schedulename) ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;
		} else if ( info.getCycle() != null && info.getCycle().trim().equals("Y")  ){
			boolean isweekcheck = false;
			int [] dayofweek = info.getDayOfWeek();
			if ( dayofweek[0] > 0 ) isweekcheck = true;
			if ( dayofweek[1] > 0 ) isweekcheck = true;
			if ( dayofweek[2] > 0 ) isweekcheck = true;
			if ( dayofweek[3] > 0 ) isweekcheck = true;
			if ( dayofweek[4] > 0 ) isweekcheck = true;
			if ( dayofweek[5] > 0 ) isweekcheck = true;
			if ( dayofweek[6] > 0 ) isweekcheck = true;
			if ( isweekcheck == false) {
				mMsgParm = schedulename +":" + ComUtil.getStrResource(this, R.string.err_mustweekcheck)  ;
				showDialog(ComConstant.DIALOG_INPUTCHECK);
				return false;
			}
		} else if ( info.getStartDate() == null || info.getEndDate() == null
				&& info.getStartTime() == null || info.getEndTime() == null){
			mMsgParm = schedulename +":" + ComUtil.getStrResource(this, R.string.err_date)  ;
			showDialog(ComConstant.DIALOG_INPUTCHECK);
			return false;
//		} else if ( info.getPosition() > 1 ){
//			mMsgParm = schedulename +":" + ComUtil.getStrResource(this, R.string.msg_limite_getschedule)  ;
//			showDialog(ComConstant.DIALOG_INPUTCHECK);
//			return false;
		}
		
		return true;		
	}
	

}
