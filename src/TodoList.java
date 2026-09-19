package com.waveapp.smcalendarlite;


import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.waveapp.smcalendarlite.adapter.TodoListAdapter;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.TodoMemoDbAdaper;
import com.waveapp.smcalendarlite.handler.AlarmHandler;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.handler.MessageHandler;
import com.waveapp.smcalendarlite.info.TodoMemoInfo;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;

public class TodoList extends ListActivity  implements OnClickListener, OnItemClickListener
																	, OnItemLongClickListener {
	
	LinearLayout viewroot;
	
	ArrayList<TodoMemoInfo> mList;
    TodoListAdapter mAdapter;
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
        setContentView(R.layout.todo_list);
        
        //release 할 전체 view
        viewroot = (LinearLayout)findViewById( R.id.viewroot ) ;
        
        //2-2.body
        listView = (ListView) findViewById(android.R.id.list);
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
        	callTodoManager(ComUtil.getStrResource(this, R.string.add));
		}
	}     
	
    private void fillData() {
        //전체데이터 가져오기
    	// issue : 데이터건수에 따른 처리 필요
    	TodoMemoDbAdaper mDbHelper = new TodoMemoDbAdaper(this);
        mDbHelper.open();  
        
    	mList = new ArrayList<TodoMemoInfo>();
    	
        mAdapter = new TodoListAdapter(this, R.layout.todo_row, mList);
        listView.setAdapter(mAdapter);
        
		long id = 0;
		
    	Cursor cur = mDbHelper.fetchAllTodolist();
//        startManagingCursor(cur);
        
    	int arrayLength = cur.getCount();
    	
		for(int i = 0 ; i < arrayLength ; i++)
		{
			TodoMemoInfo info = new TodoMemoInfo();
			id = cur.getLong((cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_ID)));
		
			info.setId(id);
			info.setMemo(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_MEMO))));
			info.setTermyn(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_TERMYN))));
			info.setFinishterm(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_FINISHTERM))));
			info.setFinish(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_FINISH))));
			info.setComfirmdate(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_CONFIRMDATE))));
			info.setAlarm(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(TodoMemoDbAdaper.KEY_ALARM))));

			mList.add(info);
			cur.moveToNext();
			
		}
		
		if ( cur != null ) cur.close();
		mDbHelper.close(); 
		
		mAdapter.notifyDataSetChanged();
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_todo, mList.size() ), View.VISIBLE);  

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
    	super.onCreateContextMenu(menu, view, menuInfo);
    	
    	menu.setHeaderIcon(R.drawable.sm_menu_more);
    	menu.setHeaderTitle( mName );
    	MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.todo_ctx_menu, menu);
    } 
    
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);		
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	//완료의 경우 alert창으로 처리
    	switch(item.getItemId()) {
//        case R.id.menu_user_new: 
//        	mId = (long)0;        	
//        	callTodoManager(ComUtil.getStrResource(this, R.string.add));
//            return true;
        case R.id.menu_todo_modify:
        	callTodoManager(ComUtil.getStrResource(this, R.string.modify));
            return true; 
	    case R.id.menu_todo_copy:
	    	callTodoManager(ComUtil.getStrResource(this, R.string.copy));
	        return true; 
	    case R.id.menu_todo_finish:
	    	showDialog(ComConstant.DIALOG_YES_NO_MESSAGE);
	        return true; 	        
	    case R.id.menu_todo_delete:
	    	callTodoManager(ComUtil.getStrResource(this, R.string.delete));
	        return true;  	        
	    }         
    	
        return super.onContextItemSelected(item);

    }
  
    /*
     * 호출화면 : 등록(ScheduleManager)
     */
	private void callTodoManager( String gubun ) { 
		
		Bundle bundle = new Bundle();
		bundle.putLong(TodoMemoDbAdaper.KEY_ID, mId); 
		bundle.putString(ComConstant.TODO_GUBUN, gubun);
		
        Intent mIntent = new Intent(this, TodoManager.class);
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
		case ComConstant.DIALOG_YES_NO_MESSAGE:
			String mParam = ComUtil.getStrResource(this, R.string.todoend);
			di = msgHd.onCreateDialog(this, ComConstant.DIALOG_YES_NO_MESSAGE, 
					ComUtil.getStrResource(this, R.string.confirm), R.string.msg_endconfirm, mParam);
			di.setOnCancelListener( new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) { 
					finishEvent();
					
				}
			});
					
			return di;
		}
    return null;
    }   

	/*
	 * finish click event
	 */
	private void finishEvent() { 

		//2.keyValue set 
		Bundle bundle = new Bundle();
        if (mId != null && mId != (long)0) {
            bundle.putLong(TodoMemoDbAdaper.KEY_ID, mId);
        }
        
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);	        
        setResult(RESULT_OK, mIntent);
        
        //3.DataBase Insert/update
        finishState();
	}
    private void finishState() { 
    	
    	TodoMemoDbAdaper mDbTodoHelper = new TodoMemoDbAdaper(this);
        mDbTodoHelper.open(); 
        
        String finish = "Y";
        
    	boolean ret = mDbTodoHelper.updateFinishTodo(mId, finish);
    	
    	if ( ret == true ) {
    		String msg = String.format(ComUtil.getStrResource(this, R.string.msg_endcomplete));
    		ComUtil.showToast(this, msg);
    	}
    	fillData();
    	
    	mDbTodoHelper.close(); 
    	
		//알람갱신(할일)
		AlarmHandler ah = new AlarmHandler(this);
    	ah.setAlarmServiceForTodo();    	
    	
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
    	//fillData();
    }        
    @Override    
    protected void onPause() {        
    	super.onPause();
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
		
		TodoMemoInfo info = new TodoMemoInfo();
		info 	= mList.get(position);
		mId 	= info.getId();
		mName 	= info.getMemo();
		
        openContextMenu(listView);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int position,
			long id) {
		
		//메뉴호출		
		TodoMemoInfo info = new TodoMemoInfo();
		info 	= mList.get(position);
		mId 	= info.getId();
		mName 	= info.getMemo();
		
		openContextMenu(listView);
		
		return true;
	}
   
}
