package com.waveapp.smcalendarlite;


import java.util.ArrayList;

import android.app.ListActivity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.waveapp.smcalendarlite.adapter.UserListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.UserInfo;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

public class UserList extends ListActivity  implements OnClickListener, OnItemClickListener
																	, OnItemLongClickListener {


	LinearLayout viewroot;
	
	ArrayList<UserInfo> mList;
    UserListAdapter mAdapter;
    ListView listView;
    
    Long mId;
    String mName;

    ImageButton mAddBtn;
    LinearLayout mAddLin;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        //1.DB Open / value reset
        
        //2.View ID Set
        setContentView(R.layout.user_list);
        
        //release 할 전체 view
        viewroot = (LinearLayout)findViewById( R.id.viewroot ) ;
        
        //2-2.body
        listView 	= (ListView) findViewById(android.R.id.list);
        mAddBtn 	= (ImageButton)findViewById( R.id.add ) ;
        mAddLin 	= (LinearLayout)findViewById( R.id.lin_add ) ;
        
        
        fillData();

        mAddLin.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        
        registerForContextMenu(listView);
        
        //광고배너(cauly)
        BannerLink banner = new BannerLink();
        banner.callBannerLink(this); 
        
    }
    
	@Override
	public void onClick(View v) {
		if( v == mAddLin ) {
			mAddBtn.setPressed(true);
			mId = (long)0;        	
        	callUserManager(ComUtil.getStrResource(this, R.string.add));
		}
	}     
	
    private void fillData() {
        // 사용자정보 전체데이터 가져오기
    	// issue : 데이터건수에 따른 처리 필요
    	UsermanagerDbAdaper mDbHelper = new UsermanagerDbAdaper(this);
        mDbHelper.open();  
        
    	mList = new ArrayList<UserInfo>();
    	
        mAdapter = new UserListAdapter(this, R.layout.user_row, mList);
        listView.setAdapter(mAdapter);
        
		long userid = 0;
		
    	Cursor cur = mDbHelper.fetchAllUsermanger();
//        startManagingCursor(cur);
        
    	int arrayLength = cur.getCount();
    	
		for(int i = 0 ; i < arrayLength ; i++)
		{
			UserInfo info = new UserInfo();
			userid = cur.getLong((cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_USERID)));
		
			info.setId(userid);
			info.setUserName(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_NAME))));
	        info.setBirthday(SmDateUtil.getDateFullFormat(this,ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_BIRTH))), false, false));
	        String sRelation	= ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_RELATION)));
	        info.setRelationText(ComUtil.getSpinnerText(this, R.array.arr_relation_key, R.array.arr_relation, sRelation)); 
			int usercolor = ComUtil.stringToInt(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_USERCOLOR)));
			info.setUsercolor(usercolor);
			
			mList.add(info);
			cur.moveToNext();
			
		}
		
		if ( cur != null ) cur.close();
		mDbHelper.close(); 	
		
		mAdapter.notifyDataSetChanged();
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_user, mList.size() ), View.VISIBLE);  
        
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
    	//menu.setHeaderTitle(getResources().getString(R.string.title_user));
    	menu.setHeaderIcon(R.drawable.sm_menu_more);
    	menu.setHeaderTitle( mName );
    	MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.user_ctx_menu, menu);
        
    	super.onCreateContextMenu(menu, view, menuInfo);

    } 
    
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);		
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
//        case R.id.menu_user_new:
//        	mId = (long)0;        	
//        	callUserManager(ComUtil.getStrResource(this, R.string.add));
//            return true;
        case R.id.menu_user_modify:
        	callUserManager(ComUtil.getStrResource(this, R.string.modify));
            return true; 
	    case R.id.menu_user_copy:
	    	callUserManager(ComUtil.getStrResource(this, R.string.copy));
	        return true; 
	    case R.id.menu_user_delete:
	    	callUserManager(ComUtil.getStrResource(this, R.string.delete));
	        return true;  	        
	    }         
    	
        return super.onContextItemSelected(item);

    }
  
    /*
     * 호출화면 : 스케줄등록(ScheduleManager)
     */
	private void callUserManager( String gubun ) { 
		
		Bundle bundle = new Bundle();
		bundle.putLong(UsermanagerDbAdaper.KEY_USERID, mId); 
		bundle.putString(ComConstant.USER_GUBUN, gubun);
		
        Intent mIntent = new Intent(this, UserManager.class);
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
	protected void onRestart() {
		super.onRestart();
	}
    @Override 
    protected void onDestroy() { 	
        super.onDestroy();  
        RecycleUtil.recursiveRecycle(viewroot);
        unregisterForContextMenu(listView);
        RecycleUtil.recursiveRecycle(listView);
        if ( mAdapter != null ) mAdapter = null;
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		
		super.onListItemClick(listView, v, position, id);
		
		UserInfo info = new UserInfo();
		info 	= mList.get(position);
		mId 	= info.getId();
		mName	= info.getUserName();
		
        openContextMenu(listView);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int position,
			long id) {
		
		//메뉴호출		
		UserInfo info = new UserInfo();
		info 	= mList.get(position);
		mId 	= info.getId();
		mName	= info.getUserName();
		
		openContextMenu(listView);
		
		return true;
	}
   
}
