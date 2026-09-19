/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.waveapp.smcalendarlite;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;


/**
 * An example of tab content that launches an activity via {@link android.widget.TabHost.TabSpec#setContent(android.content.Intent)}
 */
@SuppressWarnings("deprecation")
public class ScheduleTabForDate extends TabActivity implements OnClickListener , OnTabChangeListener {
	
	
	String mListTag 	= "scheduleList";
    String mTableTag	= "scheduletimetable";
    
    String mFromDate, mToDate;
    Long mUserid;
    Calendar m_Calendar;
    
	TextView tvs[] ;
	ImageButton btns[] ;
	 
    TextView m_yearTv ;				/// 년 표시용 텍스트
    TextView m_mothTv ;				/// 월 표시용 텍스트
    TextView m_dayTv ;				/// 날짜 표시용 텍스트
    TextView m_weekTv ;				/// 요일 표시용 텍스트
    	
    TabHost tabHost;
//    View mTabTitle1;
//    View mTabTitle2;
    //TabWidget tabWidget;
    //FrameLayout tabContent;
    TabSpec list;
    TabSpec timetable;
    
    ImageButton mAddBtn ;
    LinearLayout mAddLin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.schedule_tabview);   
       
       // tabHost = (TabHost)findViewById( android.R.id.tabhost ) ;
        tabHost = getTabHost();
        mAddBtn 	= (ImageButton) findViewById(R.id.add); 
        mAddLin 	= (LinearLayout)findViewById( R.id.lin_add ) ;
        
        //tab title setting view 
//    	LayoutInflater infalInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    	mTabTitle1 = infalInflater.inflate(R.layout.tabmenu_view, null);	
//    	mTabTitle2 = infalInflater.inflate(R.layout.tabmenu_view, null);	
        // 년 월 일 표시할 텍스트뷰
        tvs = new TextView[4] ;
        tvs[0] = (TextView)findViewById( R.id.year ) ;
        tvs[1] = (TextView)findViewById( R.id.month ) ;
        tvs[2] = (TextView)findViewById( R.id.day ) ;
        tvs[3] = (TextView)findViewById( R.id.dayofweek ) ;
        
        // 누르면 년 월 일 조절할 버튼
        btns = new ImageButton[4] ;
        btns[0] = null ; // 년도는 조절하지 않음
        btns[1] = null ; // 위와 동일
        btns[2] = (ImageButton)findViewById( R.id.previousday ) ;
        btns[3] = (ImageButton)findViewById( R.id.nextday ) ;        

        //날짜버튼 setting
        setViewTarget( tvs ) ;       

        //parent parameter 값 setting
        getParameter( savedInstanceState ) ; 
        
        //tab Content setting
        initTabContent ();
        
        //공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_dayschedule, ComConstant.CNT_DAYSCHEDULE ), View.VISIBLE);  
       
        //날짜 setting
        printView( ) ;
        
        tabHost.setOnTabChangedListener(this);
        mAddLin.setOnClickListener(this);
        
        btns[2].setOnClickListener(this); 
        btns[3].setOnClickListener(this);
        
        //광고배너
        BannerLink banner = new BannerLink();
        banner.callBannerLink(this); 
        
        ///언어값에 따라 날짜bar 구성 변경
        TextView year_tv 	= (TextView)findViewById( R.id.year_tv ) ;
        TextView month_tv 	= (TextView)findViewById( R.id.month_tv ) ;  
        TextView day_tv 	= (TextView)findViewById( R.id.day_tv ) ; 
        TextView gap 		= (TextView)findViewById( R.id.gap ) ;   
        
        if ( ComUtil.isLanguageFront(this) )  {        	
        	ViewUtil.addViewLayoutParam(year_tv, RelativeLayout.RIGHT_OF, tvs[0]);
        	ViewUtil.addViewLayoutParam(gap, 	RelativeLayout.RIGHT_OF, year_tv);
        	ViewUtil.addViewLayoutParam(tvs[1], RelativeLayout.RIGHT_OF, gap);
        	ViewUtil.addViewLayoutParam(month_tv, RelativeLayout.RIGHT_OF, tvs[1]);
        	ViewUtil.addViewLayoutParam(tvs[2], RelativeLayout.RIGHT_OF, month_tv);
        	ViewUtil.addViewLayoutParam(day_tv, RelativeLayout.RIGHT_OF, tvs[2]);
        	ViewUtil.addViewLayoutParam(tvs[3], RelativeLayout.RIGHT_OF, day_tv);
        	
        	year_tv.setText(ComUtil.getStrResource(this, R.string.year));
        	month_tv.setText(ComUtil.getStrResource(this, R.string.month));
        	day_tv.setText(ComUtil.getStrResource(this, R.string.day));
        } else {
        	
        	ViewUtil.addViewLayoutParam(month_tv, RelativeLayout.RIGHT_OF, tvs[1]);
        	ViewUtil.addViewLayoutParam(tvs[2], RelativeLayout.RIGHT_OF, month_tv);
        	ViewUtil.addViewLayoutParam(day_tv, RelativeLayout.RIGHT_OF, tvs[2]); 
        	ViewUtil.addViewLayoutParam(gap, 	RelativeLayout.RIGHT_OF, day_tv);
        	ViewUtil.addViewLayoutParam(tvs[0], RelativeLayout.RIGHT_OF, gap);
        	ViewUtil.addViewLayoutParam(year_tv, RelativeLayout.RIGHT_OF, tvs[0]);
        	ViewUtil.addViewLayoutParam(tvs[3], RelativeLayout.RIGHT_OF, year_tv);
        	
        	year_tv.setText("");
        	month_tv.setText(" ");
        	day_tv.setText(",");
        }  
       
    }

    private void getParameter ( Bundle savedInstanceState ) {
        
        //default  : today
        mFromDate 	= SmDateUtil.getTodayDefault() ;
        mToDate 	= SmDateUtil.getTodayDefault() ;
        
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mUserid 	= savedInstanceState.getLong(ScheduleDbAdaper.KEY_USERID);
        	mFromDate 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_STARTDATE);
        }	
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	mUserid 	= extras.getLong(ScheduleDbAdaper.KEY_USERID);
        	mFromDate 	= extras.getString(ScheduleDbAdaper.KEY_STARTDATE);
        }
        
        int year = SmDateUtil.getDateToInt(mFromDate, ComConstant.GUBUN_YEAR);
        int month = SmDateUtil.getDateToInt(mFromDate, ComConstant.GUBUN_MONTH);
        int day = SmDateUtil.getDateToInt(mFromDate, ComConstant.GUBUN_DAY);
        
        m_Calendar = Calendar.getInstance();
        m_Calendar.set(year, month - 1 , day);
        
    }
    
    @Override
	public void onClick(View v) {
		if ( v == btns[2] ) {
			preDay();
			changeTabContent( ) ;
	        //공통 top menu setting
	        //ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_dayschedule, ComConstant.CNT_DAYSCHEDULE ), View.VISIBLE);  
	 
			printView( ) ;
		} else if ( v == btns[3] ) {
			nextDay();
			changeTabContent( ) ;
	        //공통 top menu setting
	        //ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_dayschedule, ComConstant.CNT_DAYSCHEDULE ), View.VISIBLE);  
	 
			printView( ) ;
		} else if( v == mAddLin ) {	
			mAddBtn.setPressed(true);
        	callScheduleManager(ComUtil.getStrResource(this, R.string.add));
		}
	} 
    
	// 텍스트뷰를 넣어주면 각각 뿌려줌 (빈게 들어있으면 안뿌림)
    private void setViewTarget( TextView [] tv ) 
	{
		m_yearTv 	= tv[0] ;
		m_mothTv 	= tv[1] ;
        m_dayTv 	= tv[2] ;
        m_weekTv 	= tv[3] ;
	}
	
    //년 월 일을 출력해줌
    private void printView( )
	{
    	int year =  m_Calendar.get( Calendar.YEAR );
    	int month= (m_Calendar.get( Calendar.MONTH ) + 1 ); 
    	int day  =  m_Calendar.get( Calendar.DAY_OF_MONTH );
    	
		/// 텍스트 뷰들이 있으면 그 텍스트 뷰에다가 년 월 일을 적어넣음
		if( m_yearTv != null )
			m_yearTv.setText( year+ ""  ) ;
		if( m_mothTv != null ) {
			if ( ComUtil.isLanguageFront(this)) 
				m_mothTv.setText( ComUtil.fillSpaceToBlankF(month, 2)) ;
			else 
				m_mothTv.setText(SmDateUtil.getMonthForEng( month + "" )) ;
		}
		if( m_dayTv != null )
			m_dayTv.setText( ComUtil.fillSpaceToBlankF(day, 2)) ;
		
		if( m_weekTv != null )
			m_weekTv.setText( " " + SmDateUtil.getDayOfWeekFromDate( this, SmDateUtil.getDateFormat(year, month, day))) ;
		

	}    
    
    //이전,다음버튼 click시 날짜값 update
	private void preDay( )
	{
		m_Calendar.add( Calendar.DAY_OF_MONTH, -1 ) ;
		mFromDate = SmDateUtil.getDateFromCal(m_Calendar);
	}
	private void nextDay( )
	{
		m_Calendar.add( Calendar.DAY_OF_MONTH, 1 ) ;
		mFromDate = SmDateUtil.getDateFromCal(m_Calendar);
	} 
	
	//bundle 값 setting
	private Bundle setBundle( ) { 
		
		Bundle bundle = new Bundle();
		
		String date = mFromDate;
		
		if ( date != null && !date.trim().equals("")) {
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, date);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, date);
		}
		
		return bundle;
		
	}
	
	// tab setting
    private void initTabContent( ) 
	{
    	Bundle bundle = setBundle ();
		
		setTabListFordate (bundle);
		setTabTimeTable (bundle);

		tabHost.setCurrentTab(0);
		
		changeTabDesign();

	}
	// tab change setting
    private void changeTabContent( ) 
	{
    	
    	//선택된 tab tag 값
    	String tag = tabHost.getCurrentTabTag();
    	
    	//첫번째 tab 선택, tab삭제 (첫번째 tab이 선택안된 경우 error 발생)
    	tabHost.setCurrentTab(0);    	
    	tabHost.clearAllTabs();
    	
    	//intent bundle value set
    	Bundle bundle = setBundle();
    	
    	//tab add and action call
    	setTabListFordate (bundle);
        setTabTimeTable (bundle);
        
        //event 발생전 tab select
        tabHost.setCurrentTabByTag(tag);
        
        changeTabDesign();
         
	}    
    
    private void setTabListFordate( Bundle bundle ){
    	
    	Intent intent = new Intent(this, ScheduleListForDate.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtras(bundle);
    	
//    	TabSpec list = tabHost.newTabSpec(mListTag)
//					    	.setIndicator(ComUtil.getStrResource(this, R.string.list))
//					    	.setContent(intent);
//    	LayoutInflater infalInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    	View v = infalInflater.inflate(R.layout.tabmenu_view, null);	
    	
//    	TextView title = (TextView) mTabTitle1.findViewById(R.id.title);
//    	title.setText(ComUtil.getStrResource(this, R.string.list));
//    	title.setTextColor(R.color.white);
    	
    	TabSpec list = tabHost.newTabSpec(mListTag)
		//			    	.setIndicator( mTabTitle1 )
    						.setIndicator(ComUtil.getStrResource(this, R.string.list))
					    	.setContent(intent);
    	
        tabHost.addTab(list);
    }
    
    private void setTabTimeTable( Bundle bundle ){
   	    	
		Intent intent = new Intent(this, ScheduleTimeTable.class);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
//    	TextView title = (TextView) mTabTitle2.findViewById(R.id.title);
//    	title.setText(ComUtil.getStrResource(this, R.string.table));
//    	title.setTextColor(R.color.white);
    	
    	TabSpec timetable = tabHost.newTabSpec(mTableTag)
//					        .setIndicator(mTabTitle2)
    						.setIndicator(ComUtil.getStrResource(this, R.string.table))    	
					        .setContent(intent);
		
        tabHost.addTab(timetable);        
    }
    
	@Override
	public void onTabChanged(String tabId) {

		  int currentTab = tabHost.getCurrentTab();
		  
		  for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			  TextView  title = (TextView)tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			  if ( i == currentTab ) {
				  title.setTextColor(getResources().getColor(R.color.listtop));	
				  //title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			  } else {
				  title.setTextColor(getResources().getColor(R.color.white));
			  }
	    }
		  
	}   
    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	outState.putSerializable(UsermanagerDbAdaper.KEY_USERID, mUserid); 
    	outState.putSerializable(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
 
    }
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
    }	    
    @Override    
    protected void onResume() {        
    	super.onResume();
    	//공통 top menu setting
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_dayschedule, ComConstant.CNT_DAYSCHEDULE ), View.VISIBLE);  
 
//    	String tag = tabHost.getCurrentTabTag();
//    	
//    	if ( tag != null && tag.equals(mListTag)) {
//    		//Toast.makeText(this, "onResume mListTag", Toast.LENGTH_SHORT).show();    
//    	} else if ( tag != null && tag.equals(mTableTag)) {
//    		//Toast.makeText(this, "onResume 	mTableTag", Toast.LENGTH_SHORT).show();   
//    	}
    }    
    
    @Override    
    protected void onPause() {        
    	super.onPause();
    }
    @Override  
    protected void onDestroy() { 	
        super.onDestroy();
        RecycleUtil.recursiveRecycle(tabHost);
    }	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        changeTabContent();

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
    private void changeTabDesign() {
    	
		int currentTab = tabHost.getCurrentTab();
		
        for(int tab=0; tab < tabHost.getTabWidget().getChildCount(); tab++) {
            tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = 50;
            //고해상도 별도처리
            if ( ViewUtil.getDensity(this) == 2) {
            	tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = 70;
            } else if ( ViewUtil.getDensity(this) == 3) {
            	tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = 120;
            }
            tabHost.getTabWidget().getChildAt(tab).setBackgroundResource(R.drawable.sm_tabchange);
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(tab).findViewById(android.R.id.title);
			  if ( tab == currentTab ) {
				  title.setTextColor(getResources().getColor(R.color.listtop));	
			  } else {
				  title.setTextColor(getResources().getColor(R.color.white));
			  }    
         }
    }
    /*
     * 호출화면 : 스케줄등록(ScheduleManager)
     */
	private void callScheduleManager( String gubun ) { 

		Bundle bundle = new Bundle();
		if ( mFromDate != null && !mFromDate.trim().equals("")) {
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, mFromDate);
		}
		bundle.putString(ComConstant.SCHEDULE_GUBUN, gubun);
		
        Intent mIntent = new Intent(this, ScheduleManager.class);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT);        
	}
}
