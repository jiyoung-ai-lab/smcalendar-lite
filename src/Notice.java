package com.waveapp.smcalendarlite;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.waveapp.smcalendarlite.adapter.NoticeExpListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.database.NoticeDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.NoticeInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 공지사항을 보여준다
 */
public class Notice extends SMActivity  
				implements OnGroupClickListener, OnChildClickListener{

	//Long mUserid;
    
    NoticeExpListAdapter mAdapter;
    ExpandableListView ExlistView; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.notice_list);
        ExlistView = (ExpandableListView) findViewById(R.id.list_notice);
        //ExlistView.setGroupIndicator(null);
        
        //공통 top menu setting       
        ComMenuHandler.callComTopMenuAction(this,  ComUtil.setActionTitle(this, R.string.pre_notice, 0 ), View.GONE);
        
        fillData();
        
    	// activity button
    	setClickListener(); 
 
            
    }
    private void setClickListener () {
        
        ExlistView.setOnChildClickListener(this);
        ExlistView.setOnGroupClickListener(this);
        
    } 


    private void fillData() {
    	
    	// issue : 데이터건수에 따른 처리 필요 
    	// 1. 전체스케줄 가져오기 2.사용자id에 대해 가져오기
    	//    -. parent : string, child : scheduleinfo array
    	//String [] aa = new String [3];
       	ArrayList<String> parent = new ArrayList<String>();
    	ArrayList<ArrayList<NoticeInfo>> child = new ArrayList< ArrayList<NoticeInfo>>();  
    	
    	//adaper setting
    	mAdapter = new NoticeExpListAdapter(this, parent, child);
    	ExlistView.setAdapter(mAdapter);
    	
    	getList();
		mAdapter.notifyDataSetChanged();

    }
	/*
     * 전체 공지사항 db에서 조회 
     */
    private void getList( ) {

    	//String today = DateUtil.getTodayDefault();
    	
    	NoticeDbAdaper mDbHelper = new NoticeDbAdaper(this);
        mDbHelper.open();     
        Cursor cur = mDbHelper.fetchAllNotice(VersionConstant.APPID, ComConstant.LOCALE);
    	mDbHelper.close();

    	if ( cur == null ) return;
 
        //data setting
    	int arrayLength = cur.getCount();
		for(int i = 0 ; i < arrayLength ; i++)
		{
			NoticeInfo info = new NoticeInfo();
			long id = cur.getLong((cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_ID)));
			String key 		= cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_KEY));
			String title 	= cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_TITLE));
			String content 	= cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_CONTENT));
			String applydate = cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_APPLYDATE));
			String readdate = cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_READDATE));
			String confirmdate = cur.getString(cur.getColumnIndexOrThrow(NoticeDbAdaper.KEY_CONFIRMDATE));
			
			info.setId(id);
			info.setKey(key);
			info.setTitle(title);
			info.setContent(content);
			info.setApplydate(applydate);
			info.setReaddate(readdate);
			//반영후 한달이상 지난경우 신규여부체크안함
			info.setConfirmdate(confirmdate);

			//ExpendableList 객체에 데이터 add
			mAdapter.addItem(info);
			
			//다음건 처리
			cur.moveToNext();
		}
		
		if ( cur != null ) cur.close();
		
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }


	@Override
	public boolean onChildClick(ExpandableListView arg0, View v, int groupposition,
			int childposition, long id) {
		return true;
	}


	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		

		if ( mAdapter.getChildrenCount(0) == 0 ) return false;
		
		long rowId = Long.parseLong((String)mAdapter.getGroup(groupPosition));
		
    	NoticeInfo info = new NoticeInfo();
    	info 	= (NoticeInfo)mAdapter.getGroupKeyId(rowId);
    	
    	if ( info.getReaddate() == null || (info.getReaddate() != null && info.getReaddate().trim().equals("") )) {
            //1.DB Open / value reset
        	NoticeDbAdaper mNoticeDbHelper = new NoticeDbAdaper(this);
        	mNoticeDbHelper.open();
            
    		mNoticeDbHelper.updateOtherSchedule(rowId);
    		
        	mNoticeDbHelper.close();   
        	
        	TextView mReadDate 		= (TextView) v.findViewById(R.id.readdate); 
        	mReadDate.setText( "" );
        	mReadDate.setBackgroundDrawable( null );
        	info.setReaddate(SmDateUtil.getToday());
        	mAdapter.setChild(groupPosition, 0, info);
        	
    	}
  	
		
//		ExlistView.expandGroup(groupPosition);
		/*
		//클릭시 확인일자 update
    	NoticeInfo info = new NoticeInfo();
    	info 	= (NoticeInfo)mAdapter.getGroupData(groupPosition);
    	
    	long rowId = info.getId();
    	
    	if ( info.getReaddate() != null && !info.getReaddate().equals("") ) {
            //1.DB Open / value reset
        	NoticeDbAdaper mNoticeDbHelper = new NoticeDbAdaper(this);
        	mNoticeDbHelper.open();
            
    		mNoticeDbHelper.updateOtherSchedule(rowId);
    		
    		fillData();
    		
        	mNoticeDbHelper.close();    		
    	}


	*/	
		return false;	
	}


}
