package com.waveapp.smcalendarlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class ScheduleTimeTable extends SMActivity {
	
	Context m_context ;	
	
	int m_nspecialCnt ; 
	int m_timeCnt;
	int m_userCnt ;
	int m_schCnt ; 
	
	ViewGroup mRoot ;
	LinearLayout m_nVLineLayout;	// 세로선(종일) : 기념일
	LinearLayout m_VLineLayout;		// 세로선
	
	
	LinearLayout m_ntimeLayout;		// 시간이 표기될 layout(종일)
	LinearLayout m_nscheduleLayout;	// 스케줄일 표기될 layout(종일)
	LinearLayout m_timeLayout;		// 시간이 표기될 layout
	LinearLayout m_scheduleLayout;	// 스케줄일 표기될 layout
	
	// 종일스케줄
	LinearLayout [ ] m_ntimeLy ;		// 시간용
	LinearLayout [ ] m_nscheduleLy ;	// 스케줄용
    TextView [ ] m_cellNTime ;			// 시간용 textview    
    TextView [ ] m_cellNSchedule  ;		// 스케줄용 textview 

    LinearLayout [ ] m_ntimeHLine ; 	/// 경계선 라인 가로
    //LinearLayout [ ] m_ntimeVLine ;		/// 경계선 라인 세로    
    LinearLayout [ ] m_nscheduleHLine ; /// 경계선 라인 가로
    //LinearLayout [ ] m_nscheduleVLine ;	/// 경계선 라인 세로
    
    //시간이 설정된 스케줄
	LinearLayout [ ] m_timeLy ;			// 시간용
	LinearLayout [ ] m_userLy ;			// 사용자별
	FrameLayout [ ] m_userFLy ;			// 사용자별
	LinearLayout [ ][ ] m_scheduleLy ;// 스케줄용
    TextView [ ] m_cellTime ;			// 시간용 textview 
    
    LinearLayout [ ][ ] m_scheduleTimeLy ;	// 스케줄시간section
    TextView [ ][ ] m_cellScheduleT  ;	// 스케줄용 textview(시간표기) 
    TextView [ ][ ] m_cellSchedule  ;	// 스케줄용 textview 
    
     
    LinearLayout [ ] m_timeHLine ; 	/// 경계선 라인 가로
    //LinearLayout [ ] m_timeVLine ;	/// 경계선 라인 세로    
    //LinearLayout [ ] m_scheduleHLine ; /// 경계선 라인 가로
    LinearLayout [ ] m_scheduleVLine ;	/// 경계선 라인 세로

    
	////////////////////////////////////////    	
    
    float m_displayScale ;			/// 화면 사이즈에 따른 텍스트 크기 보정값 저장용
    float m_displayHeight ;			/// 화면 사이즈에 따른 텍스트 크기 보정값 저장용
    float m_displayWidth;
    float m_textSize ;				/// 텍스트 사이즈(위 라인의 변수와 곱해짐)
    float m_topTextSize ;			/// 스케줄명
    
    int m_tHeight 	= 50 ;
    int m_sHeight 	= 50 ;
    int m_ntHeight 	= 40 ;
    int m_uMinWidth = 60 ;
    
    int m_maxUser = 6;
    int m_lineSize = 1 ;			/// 경계선의 굵기
    
    gsTimeTableColorParam cParam ;
    
    /// 있으면 적용하고 없으면 bgcolor로 처리함( 각각 개별적으로 )
    Drawable m_bgImgId = null ;				/// 전체 배경이미지
    Drawable m_cellBgImgId = null ;			/// 한칸의 배경 이미지
    
    public static class gsTimeTableColorParam
    {
    	public int m_lineColor 			= 0xff000000 ;	/// 경계선 색
        public int m_tCellColor 		= 0xffffffff ;	/// 칸의 배경색
        public int m_sCellColor 		= 0xffffffff ;	/// 칸의 배경색
        public int m_tTextColor 		= 0xff000000 ;	/// 글씨색
        public int m_sTextColor 		= 0xff000000 ;	/// 글씨색

    }
    
	private long mUserid;
	private String mFromDate;
	
    Long 	mID  ;
    String  mName;
    String  mMessage;
    String  mGubun;
    
	ArrayList< SpecialDayInfo > mNScheduleList;
	ArrayList< String > mNTimelist = new ArrayList<String>();
	
	String [ ][ ] mUserList ;   //userid, cnt
	ArrayList< ScheduleInfo > mScheduleList;   
	ArrayList< String > mTimelist = new ArrayList<String>();
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        
        m_context = this;
        
        setContentView(R.layout.schedule_tableview);
        
        //m_title = (TextView)findViewById( R.id.title) ;
        //m_dday = (TextView)findViewById( R.id.dday) ; 
        mRoot			= (ViewGroup)findViewById( R.id.viewroot ) ;
        m_nVLineLayout 	= (LinearLayout)findViewById( R.id.nvline_lLayout ) ;
        m_VLineLayout 	= (LinearLayout)findViewById( R.id.vline_lLayout ) ;
        
        m_ntimeLayout 		= (LinearLayout)findViewById( R.id.ntime_lLayout ) ;
        m_nscheduleLayout 	= (LinearLayout)findViewById( R.id.nschedule_lLayout ) ; 
        m_timeLayout 		= (LinearLayout)findViewById( R.id.time_lLayout ) ;
        m_scheduleLayout 	= (LinearLayout)findViewById( R.id.schedule_lLayout ) ;
        
        //parent parameter 값 setting
        getParameter( savedInstanceState ) ; 
        
        //화면 setting
        setAllView();

    }
    
    private void getParameter ( Bundle savedInstanceState ) {
        
        //default  : today
        mFromDate 	= SmDateUtil.getTodayDefault() ;
        
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
        
    }
    private void setAllView( ) {

    	getAllSchedule();
        initTimeTable( ); 

        setSchduleExistTime();
        setSchduleNoExistTime();

        setOnEvent( ) ;


    }    
    
    private void initTimeTable( ) {
    	
    	clearViewItem( );
    	layoutInitialization();
    	//setTitle();   	
    	
    }
    private void getAllSchedule( ) {


		//초기화
		mScheduleList = new ArrayList< ScheduleInfo >();
		mNScheduleList = new ArrayList< SpecialDayInfo >();
 
		//순서불변 : 종일스케줄을 하단에 위치시키기 위해
		getSpecialdayPerDate(mFromDate);
		getSchedulePerDate(mFromDate);
    	
    	setTimeList();
    }
    
	/// 모든 객체,화면 생성
	private void setSchduleExistTime( )
	{
		createViewItem( ) ;
        setLayoutParams( ) ;
        setLineParam( ) ;
        setContentext( ) ;
        
	}

	/// 모든 객체,화면 생성
	private void setSchduleNoExistTime( )
	{
		createViewItem2( ) ;
        setLayoutParams2( ) ;
        setLineParam2( ) ;
        setContentext2( ) ;
        
	}
	/*
    private void setTitle(  ) {
    	String title = DateUtil.getDateFullFormat(mFromDate, true)
						+ " ("
						+ Integer.toString(m_schCnt + m_nspecialCnt)
						+ "건)";
    	
    	m_dday.setText(DateUtil.getDDayString(
				DateUtil.getDateGapFromToday(mFromDate)));
    	
    	m_title.setText(title);
	}
	*/   
    
    private void clearViewItem(  ) {
    	
		// linaearlayout 삭제
    	m_nVLineLayout.removeAllViews();
    	m_ntimeLayout.removeAllViews();
		m_nscheduleLayout.removeAllViews();
		
		m_VLineLayout.removeAllViews();
		m_timeLayout.removeAllViews();
		m_scheduleLayout.removeAllViews();
	}
	
    // Layout 초기생성
	public void layoutInitialization(  )
	{ 
		//가로 : 시간기준, 세로:스케줄기준
		m_ntimeLy 			= new LinearLayout[ m_nspecialCnt ] ;
		m_nscheduleLy 		= new LinearLayout[ m_nspecialCnt ] ;      
		m_cellNTime 		= new TextView[ m_nspecialCnt ] ; 
		m_cellNSchedule		= new TextView[ m_nspecialCnt ] ; 
		
        m_ntimeHLine 		= new LinearLayout[ m_nspecialCnt ] ;
        m_nscheduleHLine 	= new LinearLayout[ m_nspecialCnt ] ;

        ///////////////////////////////////////////////////////
        
		m_timeLy 			= new LinearLayout[ m_timeCnt ] ;
		m_cellTime 			= new TextView[ m_timeCnt ] ; 
		
		m_userLy 			= new LinearLayout[ m_userCnt ] ;
		m_userFLy 			= new FrameLayout[ m_userCnt ] ;
		m_scheduleLy 		= new LinearLayout[ m_userCnt ][  ] ;

        m_scheduleTimeLy 		= new LinearLayout[ m_userCnt ][  ] ;
		m_cellScheduleT		= new TextView[ m_userCnt ][  ] ;
		m_cellSchedule		= new TextView[ m_userCnt ][  ] ;
		
        m_timeHLine 		= new LinearLayout[ m_timeCnt ] ;
        m_scheduleVLine 	= new LinearLayout[ m_userCnt ] ;
        
        /// 화면의 크기에 따른 보정값
        m_displayScale = this.getResources( ).getDisplayMetrics( ).density ;
        m_displayHeight = this.getResources( ).getDisplayMetrics( ).heightPixels ;
        m_displayWidth = this.getResources( ).getDisplayMetrics( ).widthPixels ;
        
        if ( ( m_displayHeight > 800 || m_displayWidth > 800 ) && m_displayScale == 1 ) {
        	m_topTextSize 	= m_displayScale * 15.0f ;
            m_textSize 		= m_displayScale * 13.0f ;  
        } else if ( m_displayScale == 2 ) {
            m_topTextSize 	= m_displayScale * 8.0f ;
            m_textSize 		= m_displayScale * 6.0f ;  
        } else if ( m_displayScale >= 3 ) {
            m_topTextSize 	= ( 2 ) * 8.0f ;
            m_textSize 		= ( 2 ) * 6.0f ;
        } else {
        	m_topTextSize 	= m_displayScale * 9.0f ;
            m_textSize 		= m_displayScale * 7.0f ;
        } 
        

        //화면 해상도에 따라 변경
        setViewSize();
        
        Resources res = getResources();
        cParam = new gsTimeTableColorParam( );
        cParam.m_lineColor 		= res.getColor(R.color.calline);
        cParam.m_sCellColor 	= res.getColor(R.color.calcellback);
        cParam.m_tCellColor 	= res.getColor(R.color.calcellback);
        cParam.m_sTextColor 	= res.getColor(R.color.white); 
        cParam.m_tTextColor 	= res.getColor(R.color.listtext);
        
	}		
	
	/*
	 * view사이즈 density에 따라 변경(고해상도 단말기 지원용)
	 */
	private void setViewSize ( ) {

	    if ( m_displayScale >= 2  ) {
	    	m_tHeight 	= (int) (m_tHeight * m_displayScale / 1.5) ;
	    	m_sHeight 	= (int) (m_sHeight * m_displayScale / 1.5) ;
	    	m_ntHeight 	= (int) (m_ntHeight * m_displayScale / 1.5) ;
	    	m_uMinWidth = (int) (m_uMinWidth * m_displayScale  / 1.5) ;
	    }
	    
	}	
	/// 레이아웃과 버튼 그리고 경계션으로 쓸 라인용 레이아웃들을 생성한다.
	// -. line layout 은 나중에...
	public void createViewItem(  )
	{
		
		m_scheduleLayout.setMinimumWidth( m_scheduleLayout.getWidth() / 4 );
		
		//세로 : timecnt, 가로:schedulecnt + 1
        for( int i = 0 ; i < m_timeCnt ; i++ )
        {
        	m_timeLy[ i ] 	= new LinearLayout( m_context ) ;
        	m_cellTime[ i ]   = new TextView( m_context ) ;
        	m_timeLayout.addView( m_timeLy[ i ] ) ;
        	m_timeLy[ i ].addView( m_cellTime[ i ] ) ;
        	
        	m_timeLy[ i ].setBackgroundColor(cParam.m_tCellColor);   
        	m_cellTime[ i ].setTextColor(cParam.m_tTextColor); 
        	//m_cellTime[ i ].setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        	
        	//가로줄
        	m_timeHLine[ i ] = new LinearLayout( m_context ) ;
        	m_timeLayout.addView( m_timeHLine[ i ] ) ;
        	
        	//세로줄        	
        	//m_timeHLine[ i ].setBackgroundColor(cParam.m_lineColor);   


        }
        
        //사용자건수만큼 가로칸 만들기 : user(가로) * 스캐줄(세로)
        //m_scheduleLayout -> m_userLy -> m_userFLy -> m_scheduleLy -> m_cellSchedule,m_cellScheduleT
    	for( int j = 0 ; j < m_userCnt ; j++ )
    	{    	
    		//user (가로)
    		m_userLy[ j ] 	= new LinearLayout( m_context ) ;
    		m_userFLy[ j ] 	= new FrameLayout( m_context ) ;
    		m_scheduleLayout.addView( m_userLy[ j ] ) ; 
    		m_userLy[ j ].setBackgroundColor(cParam.m_tCellColor);  
    		m_userLy[ j ].setOrientation(LinearLayout.VERTICAL);
    		
    		m_userLy[ j ].addView(m_userFLy[ j ]);
    		
    		//user당 스케줄(세로)
    		int cnt = ComUtil.stringToInt(mUserList[ j ][ 1 ] );
    		m_scheduleLy[ j ] 	 = new LinearLayout[ cnt ] ;
    		m_scheduleTimeLy[ j ] 	 = new LinearLayout[ cnt ] ;    		 
    		m_cellScheduleT[ j ] = new TextView[ cnt ] ; 
    		m_cellSchedule[ j ]  = new TextView[ cnt ] ; 
    		
    		for( int k = 0 ; k < cnt ; k++ ) {
    			m_scheduleLy[ j ][ k ]	= new LinearLayout( m_context ) ;
    			m_scheduleTimeLy[ j ][ k ]	= new LinearLayout( m_context ) ;
        		m_cellScheduleT[ j ][ k ] = new TextView( m_context ) ;
    			m_cellSchedule[ j ][ k ]  = new TextView( m_context ) ;
        		
    			m_userFLy[ j ].addView(m_scheduleLy[ j ][ k ]);
    			m_scheduleLy[ j ][ k ].addView( m_scheduleTimeLy[ j ][ k ] ) ;
    			m_scheduleTimeLy[ j ][ k ].addView( m_cellScheduleT[ j ][ k ] ) ;
            	m_scheduleLy[ j ][ k ].addView( m_cellSchedule[ j ][ k ] ) ;
            	m_scheduleLy[ j ][ k ].setOrientation(LinearLayout.VERTICAL);
            	m_cellSchedule[ j ][ k ].setTextColor(cParam.m_sTextColor); 
            	m_cellScheduleT[ j ][ k ].setTextColor(cParam.m_sTextColor);
            	
    		}
    		
        	//세로줄
        	m_scheduleVLine[ j ] = new LinearLayout( m_context ) ;
        	m_scheduleLayout.addView( m_scheduleVLine[ j ] ) ;
        	
        	//가로줄
        	//m_scheduleHLine[ j ].setBackgroundColor(cParam.m_lineColor);
	
    	}  
    	
        m_timeLayout.setBackgroundColor(cParam.m_tCellColor);  
        m_scheduleLayout.setBackgroundColor(cParam.m_tCellColor); 
	}	
	
	public void createViewItem2(  )
	{
		//세로 : timecnt, 가로:schedulecnt + 1
        for( int i = 0 ; i < m_nspecialCnt ; i++ )
        {
        	m_ntimeLy[ i ] 		= new LinearLayout( m_context ) ;
        	m_cellNTime[ i ]   	= new TextView( m_context ) ;
        	m_ntimeLayout.addView( m_ntimeLy[ i ] ) ;
        	m_ntimeLy[ i ].addView( m_cellNTime[ i ] ) ;
        	
        	m_ntimeLy[ i ].setBackgroundColor(cParam.m_tCellColor);   
        	m_cellNTime[ i ].setTextColor(cParam.m_tTextColor);

			setMargin(m_cellNTime[ i ], 0, 0, 5, 5);
			
        	//가로줄
        	m_ntimeHLine[ i ] = new LinearLayout( m_context ) ;
        	m_ntimeLayout.addView( m_ntimeHLine[ i ] ) ;
        	//세로줄 (없음)


        }
        
    	for( int j = 0 ; j < m_nspecialCnt ; j++ )
    	{
    		
    		m_nscheduleLy[ j ] 	= new LinearLayout( m_context ) ;
    		m_cellNSchedule[ j ] = new TextView( m_context ) ;
    		m_nscheduleLayout.addView( m_nscheduleLy[ j ] ) ;
        	m_nscheduleLy[ j ].addView( m_cellNSchedule[ j ] ) ;   
        	
        	m_nscheduleLy[ j ].setBackgroundColor(cParam.m_tCellColor);        	
        	m_cellNSchedule[ j ].setTextColor(cParam.m_tTextColor);
        	
        	//가로줄
        	m_nscheduleHLine[ j ] = new LinearLayout( m_context ) ;
        	m_nscheduleLayout.addView( m_nscheduleHLine[ j ] ) ;
        	
        	//세로줄 (없음)
	
    	}      
        m_ntimeLayout.setBackgroundColor(cParam.m_tCellColor);  
        m_nscheduleLayout.setBackgroundColor(cParam.m_tCellColor); 
	}	
	
	/// 레이아웃과 버튼의 배경색, 글씨색 등 ViewParams를 셋팅
	public void setLayoutParams(   )
	{
		
		if( m_bgImgId != null )
		{
			//m_targetLayout.setBackgroundDrawable( m_bgImgId ) ;
		}
		
		for( int i = 0 ; i < m_timeCnt ; i++ )
		{
			
//			m_cellTime[ i ].setLayoutParams(ew LinearLayout.LayoutParams ( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
			/// 각 라인을 구성하는 레이아웃들은 가로로 나열~
			m_timeLy[i].setLayoutParams(	/// 레이아웃 사이즈는 warp_content로 설정 
					new LinearLayout.LayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT  , m_tHeight - m_lineSize  ))  ) ;
			m_cellTime[ i ].setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT ,  LinearLayout.LayoutParams.WRAP_CONTENT   ) )  ;

			
        	/// 글씨 크기
        	m_cellTime[ i ].setTextSize( m_textSize );
        	m_cellTime[ i ].setGravity(Gravity.CENTER);  
        	
        	
        }
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		params.weight = 1;
		
		for( int j = 0 ; j < m_userCnt ; j++ )
		{
			/// 각 라인을 구성하는 레이아웃들은 가로로 나열~
			m_userLy[ j ].setLayoutParams(	params ) ;
			m_userLy[ j ].setMinimumWidth(m_uMinWidth);
			m_userFLy[ j ].setLayoutParams(	 new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT) ) ;
			
			//m_userFLy[ j ].setMinimumWidth(m_uMinWidth);			
			int cnt = ComUtil.stringToInt(mUserList[ j ][ 1 ] );
			for( int k = 0 ; k < cnt ; k++ ) {
				/// 각 라인을 구성하는 레이아웃들은 세로로 나열~
				m_scheduleLy[ j ][ k ].setLayoutParams(	/// 레이아웃 사이즈는 warp_content로 설정 
							new FrameLayout.LayoutParams( FrameLayout.LayoutParams.FILL_PARENT , m_sHeight ) ) ;
				m_scheduleLy[ j ][ k ].setClickable(true);
	 
				m_scheduleLy[ j ][ k ].setMinimumWidth(getLyaoutWidth((m_scheduleLayout)) / 3 );
				//1) 스케줄명
				m_cellSchedule[ j ][ k ].setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT  , LinearLayout.LayoutParams.WRAP_CONTENT  )  ) ;
				m_cellSchedule[ j ][ k ].setMaxLines(2);
//				m_cellSchedule[ j ][ k ].setEllipsize(TruncateAt.END);
//				m_cellSchedule[ j ][ k ].setSingleLine(true);
//				m_cellSchedule[ j ][ k ].setLines(2);
				m_cellSchedule[ j ][ k ].setHorizontallyScrolling(false);
//				scrollHorizontally
				m_cellSchedule[ j ][ k ].setTextSize( m_topTextSize ) ;	
				//2) 시간				
				m_scheduleTimeLy[ j ][ k ].setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT  , LinearLayout.LayoutParams.WRAP_CONTENT  )  ) ;
//				Drawable dw = this.getResources().getDrawable(R.drawable.sm_cal_schedule_cell);
//				ViewUtil.drawBackGroundColor(color)
//				dw.setAlpha(60);
//				m_scheduleTimeLy[ j ][ k ].setBackgroundDrawable(dw);				
				m_cellScheduleT[ j ][ k ].setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT  , LinearLayout.LayoutParams.WRAP_CONTENT )  ) ;
				m_cellScheduleT[ j ][ k ].setMaxLines(1);
				m_cellScheduleT[ j ][ k ].setTextSize( m_textSize ) ;
				
				
			}
		
		}	
	}
	
	public void setLayoutParams2(   )
	{
		
		if( m_bgImgId != null )
		{
			//m_targetLayout.setBackgroundDrawable( m_bgImgId ) ;
		}
		
		for( int i = 0 ; i < m_nspecialCnt ; i++ )
		{
			/// 각 라인을 구성하는 레이아웃들은 가로로 나열~
			m_ntimeLy[i].setLayoutParams(	/// 레이아웃 사이즈는 warp_content로 설정 
					new LinearLayout.LayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT  , m_ntHeight - m_lineSize  ))  ) ;
			m_cellNTime[ i ].setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT ,  LinearLayout.LayoutParams.FILL_PARENT   )  ) ;

        	/// 글씨 크기
			m_cellNTime[ i ].setTextSize( m_textSize ) ;
			m_cellNTime[ i ].setGravity(Gravity.CENTER);
        	
			/// 각 라인을 구성하는 레이아웃들은 가로로 나열~
			m_nscheduleLy[ i ].setLayoutParams(	/// 레이아웃 사이즈는 warp_content로 설정 
						new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT , m_ntHeight - m_lineSize ) ) ;
			m_nscheduleLy[ i ].setClickable(true);
			m_cellNSchedule[ i ].setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT  , m_ntHeight  )  ) ;
			
        	/// 글씨 크기
			m_cellNSchedule[ i ].setTextSize( m_topTextSize ) ;   
			m_cellNSchedule[ i ].setGravity(Gravity.CENTER_VERTICAL);
        }
 
	}	
	
	public void setLineParam( )
	{
		m_VLineLayout.setBackgroundColor( cParam.m_lineColor ) ;
		m_VLineLayout.setLayoutParams(	///  세로 라인이니까 세로는 쭉~ 가로는 두께만큼 
				new LinearLayout.LayoutParams( m_lineSize, LinearLayout.LayoutParams.FILL_PARENT ) ) ; 
		
		for( int i = 0 ; i < m_timeCnt ; i ++ )
		{
			m_timeHLine[ i ].setBackgroundColor( cParam.m_lineColor ) ;	/// 라인색
			m_timeHLine[ i ].setLayoutParams(	/// 가로 라인이니까 가로는 꽉 세로는 두께만큼 
						new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, m_lineSize ) ) ;
		}
		for( int j = 0 ; j < m_userCnt ; j ++ )
		{
			m_scheduleVLine[ j ].setBackgroundColor( cParam.m_lineColor ) ;	/// 라인색
			m_scheduleVLine[ j ].setLayoutParams(	/// 세로 라인이니까 세로는 쭉~ 가로는 두께만큼 
						new LinearLayout.LayoutParams( m_lineSize, LinearLayout.LayoutParams.FILL_PARENT ) ) ;
		}
//		m_scheduleHLine[ 0 ].setBackgroundColor( cParam.m_lineColor ) ;	/// 라인색
//		m_scheduleHLine[ 0 ].setLayoutParams(	/// 가로 라인이니까 가로는 꽉 세로는 두께만큼 
//					new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, m_lineSize ) ) ;
	

	}
	public void setLineParam2( )
	{

		m_nVLineLayout.setBackgroundColor( cParam.m_lineColor ) ;
		m_nVLineLayout.setLayoutParams(	///  세로 라인이니까 세로는 쭉~ 가로는 두께만큼 
				new LinearLayout.LayoutParams( m_lineSize, LinearLayout.LayoutParams.FILL_PARENT ) ) ;
		
		for( int i = 0 ; i < m_nspecialCnt ; i ++ )
		{
			m_ntimeHLine[ i ].setBackgroundColor( cParam.m_lineColor ) ;	
			m_ntimeHLine[ i ].setLayoutParams(	/// 가로 라인이니까 가로는 꽉 세로는 두께만큼 
						new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, m_lineSize ) ) ;

			m_nscheduleHLine[ i ].setBackgroundColor( cParam.m_lineColor ) ;	
			m_nscheduleHLine[ i ].setLayoutParams(	///  세로 라인이니까 세로는 쭉~ 가로는 두께만큼 
						new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, m_lineSize ) ) ;
			
		}

	}	
	
	/// 데이터베이스에서 가져온 데이터 화면에 setting
	public void setContentext( )
	{
		//스케줄 최대건수에 대한 정의 필요.... 		
		for( int i = 0 ; i < m_timeCnt ; i++ ) {
			String time = mTimelist.get(i).toString();
			m_cellTime[ i ].setText(SmDateUtil.getTimeFullFormat(this, time, true));
			setMarginAndSize ( m_cellTime[ i ], ( m_tHeight / 10 ), 0, 0, 0 );
					
			m_cellTime[ i ].setBackgroundColor( cParam.m_tCellColor ) ;
		}

		//초기화
		int[][] totPos = new int[m_userCnt][2];
		for( int i = 0 ; i < m_userCnt ; i++ ) { 
			totPos[i][0] = -1;
			totPos[i][1] = 0;
		}
		//스케줄셋팅
		for( int i = 0 ; i < m_schCnt ; i++ ) {
			ScheduleInfo sch = new ScheduleInfo();
			sch = mScheduleList.get(i);	
			int pos1 = 0;
			int pos2 = 0;
			for( int j = 0 ; j < m_userCnt ; j++ ) {
				long guideUserid = ComUtil.stringToLong(mUserList[ j ][ 0 ] );
//				//스케줄 위치배정
				if ( guideUserid == sch.getUserId()) {
					pos1 = j;
					pos2 = sch.getPosition();
					break;
				}
			}
			//스케줄명
			String name = sch.getScheduleName().toString();
			m_cellSchedule[ pos1 ][ pos2 ].setText(name);
			setMargin(m_cellSchedule[ pos1 ][ pos2 ], 0, 5, 5, 5);	
			
			m_scheduleLy[ pos1 ][ pos2 ].setTag(sch);
			//시간 및 크기
			String stime = sch.getStartTime().toString();
			String etime = sch.getEndTime().toString();
			
			m_cellScheduleT[ pos1 ][ pos2 ].setEllipsize(TruncateAt.END);			 
			m_cellScheduleT[ pos1 ][ pos2 ].setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			m_cellScheduleT[ pos1 ][ pos2 ].setText(SmDateUtil.getTimeFullFormat(this, stime)+"~"+SmDateUtil.getTimeFullFormat(this, etime));
			setMargin(m_cellScheduleT[ pos1 ][ pos2 ], 5, 0, 5, 5);
			
			int position = getMargin( m_scheduleLy[ pos1 ][ pos2 ], stime, m_timeCnt, mTimelist);
			int gap 	 = getScheduleHeight( m_scheduleLy[ pos1 ][ pos2 ], stime, etime, m_timeCnt, mTimelist);
//			setMarginAndSize ( m_scheduleLy[ pos1 ][ pos2 ], ( m_sHeight *  ( position  - totPos[ pos1 ] ) ), 
//					( m_sHeight *  gap ), 0, 1 );
			
			//중복되는 일정이 있는경우 왼쪽 여백  -> 넓이 조절
			if ( totPos[ pos1 ][ 0 ] != -1 && 
					(totPos[ pos1 ][ 0 ] == position || ( totPos[ pos1 ][ 0 ] + 1) == position )) {
				totPos[ pos1 ][ 1 ] = totPos[ pos1 ][ 1 ]  + 50;
			} else {
				totPos[ pos1 ][ 1 ] = 0;
			}
			
			int usercolor = sch.getUseColor();
			Drawable draw = this.getResources( ).getDrawable( R.drawable.sm_time_schedule_cell );
			draw.setColorFilter(ViewUtil.drawBackGroundColor ( usercolor ) );

//			draw.setAlpha(95);						
			m_scheduleLy[ pos1 ][ pos2 ].setBackgroundDrawable(draw);
			
			//사이즈 위치 한번더 보정
			setMarginAndPosition( m_scheduleLy[ pos1 ][ pos2 ], ( m_sHeight *   position ), 
					( m_sHeight *  ( gap  + 1 ) ), 0, totPos[ pos1 ][ 1 ] );
			
			totPos[ pos1 ][ 0 ] = position;

		}		

	}
	/// 데이터베이스에서 가져온 데이터 화면에 setting
	public void setContentext2( )
	{
		
		for( int i = 0 ; i < m_nspecialCnt ; i++ ) {
			
			SpecialDayInfo info = new SpecialDayInfo();
			info = mNScheduleList.get(i);	
			
			String subname = info.getSubName().toString();
			String gubun = info.getGubun().toString();
			m_cellNTime[ i ].setText( ComUtil.getSpecialDayText(this, gubun, subname) );
			setMarginAndSize ( m_cellNTime[ i ], 0, 0, 0, 0 );	
			m_cellNTime[ i ].setBackgroundColor( cParam.m_tCellColor ) ;
			
			String name = info.getName().toString();
			if ( name != null && !name.trim().equals("")) {
				m_cellNSchedule[ i ].setText(name);
			} else {
				m_cellNSchedule[ i ].setText(info.getSubName().toString());
			}
			 
			if ( gubun != null && gubun.equals("S")) {
				int usercolor = info.getUserColor();
				Drawable draw = this.getResources( ).getDrawable( R.drawable.sm_time_schedule_cell );
				draw.setColorFilter(ViewUtil.drawBackGroundColor ( usercolor ) );
				m_cellNSchedule[ i ].setBackgroundDrawable(draw);
				m_cellNSchedule[ i ].setTextColor(Color.WHITE);
			} else {

				setMargin(m_cellNSchedule[ i ], 0, 0, 5, 5);	
			}
		}

	}	


	/*
	 * 스케줄 항목칸 위치 및 크기 지정 ( 기념일,종일에서 사용)
	 */
	public void setMarginAndSize ( View v, int topmargin, int height, int width, int weight ){
		
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();

		/* 해당 margin값 변경(항목은 LinearLayout.LayoutParams 참고) */ 

		if ( topmargin > 0 ) 	lp.topMargin = topmargin;
		if ( height > 0 ) 		lp.height = height;
		if ( width > 0 ) 		lp.width = width;
		if ( weight > 0 )		lp.weight = weight;
		
		/* 변경된 값으로 적용 */
		v.setLayoutParams(lp);
	}
	/*
	 * 스케줄 항목칸 위치 및 크기 지정 ( Time 별 스케줄 : 사용자별로 중복처리를 하기 위해 frame을 사용)
	 */
	public void setMarginAndPosition ( View v, int topmargin, int height, int width, int leftmargin ){
		
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
		
		/* 해당 margin값 변경(항목은 FrameLayout.LayoutParams 참고) */ 
		lp.gravity = Gravity.TOP; 
		if ( topmargin > 0 ) 	lp.topMargin 	= topmargin;
		if ( height > 0 ) 		lp.height 		= height;
		if ( width > 0 ) 		lp.width 		= width;
		if ( leftmargin > 0 )	lp.leftMargin 	= leftmargin;
		
		
		/* 변경된 값으로 적용 */
		v.setLayoutParams(lp);
	}
//	/*
//	 * View 위치와 크기 여백 처리
//	 * -.view에 따라 Params항목이 상이함.
//	 */
//	public void setLayoutParams ( View v, int width, int height, int weight, int gravity ){
//		
//		ViewGroup.LayoutParams lp =  v.getLayoutParams();
//
//		
//		/* 해당 LayoutParams 변경(항목은 ViewGroup.LayoutParams 참고) */ 
//		
//		if ( width > 0 ) 		lp.width = width;
//		if ( height > 0 ) 		lp.height = height;
//		if ( weight > 0 ) 		lp.weight = weight;
//		if ( gravity > 0 ) 		lp.gravity = gravity;
//		
//		
//		/* 변경된 값으로 적용 */
//		v.setLayoutParams(lp);
//	}
	
	public void setMargin ( View v, int top, int bottom, int left, int right ){
		
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();

		/* 해당 margin값 변경(항목은 LinearLayout.LayoutParams 참고) */
		if ( top 	> 0 ) 	lp.topMargin 	= top;
		if ( bottom > 0 ) 	lp.bottomMargin = bottom;
		if ( left 	> 0 ) 	lp.leftMargin 	= left;
		if ( right	 > 0 )	lp.rightMargin 	= right;
		
		/* 변경된 값으로 적용 */
		v.setLayoutParams(lp);
	}
	/*
	 * 
	 */
	private int getMargin ( View v , String fromtime, int cnt, ArrayList<String>  list ){
		
		int position = 0;
		
		for( int i = 0 ; i < cnt ; i++ ) {		
			
			String gtime = list.get(i).toString();
			
			if ( gtime != null && fromtime != null && 
					gtime.trim().equals(fromtime.trim())) {
				position = i;
				return position;
			}				
		}
		return position;
	}	
	
	private int getScheduleHeight( View v , String fromtime, String totime, int cnt, ArrayList<String>  list ){
		int gap = 0;
		int start = 0;
		int end = 0;
		
		for( int i = 0 ; i < cnt ; i++ ) {		
			
			String gtime = list.get(i).toString();
			
			if ( gtime != null && fromtime != null && 
					gtime.trim().equals(fromtime.trim())) {
				start = i;
			}	
			if ( gtime != null && totime != null && 
					gtime.trim().equals(totime.trim())) {
				end = i;
			}				
		}
		gap = end - start ; 
		
		return gap;
	}	

	
	private void setTimeList () {
		ArrayList< String > list = new ArrayList<String>();
		mTimelist = new ArrayList<String>();
		mNTimelist = new ArrayList<String>();
		
		//1. 날짜가 있는 스케줄 정보
		m_schCnt = mScheduleList.size();
		 
		//스케줄내 시작시간,종료시간 list에 setting
		for ( int i = 0 ; i < m_schCnt ; i++ ){
			
			ScheduleInfo sch = new ScheduleInfo();
			sch = mScheduleList.get(i);
			
			String stime = sch.getStartTime();
			String etime = sch.getEndTime();
			
			list.add(stime);
			list.add(etime);
		}
		
		//list dup erase
		HashSet<Object> hs = new HashSet<Object>(list); 
		Iterator<Object> it = hs.iterator(); 
		while(it.hasNext()){ 
			mTimelist.add(it.next().toString());
		} 
		//list sort
		Collections.sort(mTimelist);

		m_timeCnt =  mTimelist.size();
		
		//2. 날짜가 없는 스케줄 정보
		m_nspecialCnt = mNScheduleList.size();
		 
		//스케줄내 시작시간,종료시간 setting
		for ( int i = 0 ; i < m_nspecialCnt ; i++ ){
			
			SpecialDayInfo info = new SpecialDayInfo();
			info = mNScheduleList.get(i);
			String name = info.getName();
			mNTimelist.add(name);
			
		}
	}
		
	/*
	 * schedule data(year, month fix)
	 */
    public void getSchedulePerDate( String scheduledate ) {	
    	
    	ArrayList< Long > userlist = new ArrayList< Long > ();
        /// 달력을 하나 복사해서 작업한다.
		if (scheduledate != null ) {

			//db select			
			ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
	        mDbHelper.open();
			Cursor cur = mDbHelper.fetchSchedulePerDateSortUser(scheduledate);			
			mDbHelper.close();
			
			//초기화
			UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
			mDbUser.open();
						
			//loop
			int len = cur.getCount();	
			
			for(int i = 0 ; i < len ; i++) {	
				ScheduleInfo sch = new ScheduleInfo();	
				sch.setScheduleId(cur.getLong(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_SCHDULEID)));				
				sch.setScheduleName(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_NAME)));
				long userid = cur.getLong((cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_USERID)));
				sch.setUserId(userid);
				sch.setUserColor(UsermanagerDbAdaper.getUserColor(this, mDbUser, userid));	
				sch.setCycle(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_CYCLE)));				
				sch.setStartDate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTDATE)));
				sch.setAllDayYn(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALLDAYYN)));		
				sch.setEndDate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDDATE)));
				sch.setStartTime(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_STARTTIME)));
				sch.setEndTime(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ENDTIME)));
				sch.setAlarm2(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_ALARM2)));
				sch.setRepeatdate(cur.getString(cur.getColumnIndexOrThrow(ScheduleDbAdaper.KEY_REPEATDATE)));				
				//요일별 value set
				int[] arrDayofweek = new int [7];				
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
	
				sch.setDayOfWeek(arrDayofweek);
				
				String dbstartdate = sch.getStartDate();
				String dbenddate = sch.getEndDate();
				
				//매주  : 요일 체크
				//없음  : 기간전부
				ArrayList<String> list = new ArrayList<String>();
				Calendar iCal = Calendar.getInstance();
				String sCycle = sch.getCycle().toString();
				if (sCycle != null && sCycle.trim().equals("Y")) {
					String [] loop = SmDateUtil.getDateFromDayOfWeekArr(arrDayofweek, scheduledate, scheduledate, dbstartdate, dbenddate, iCal);
					if ( loop != null) {							
						int loopLen = loop.length;
						for ( int k = 0 ; k < loopLen ; k++ ) {
							list.add(loop[k].toString());
						}	
					}			
				} else if ( sCycle  != null && sCycle.equals("M")) {
					String [] loop = SmDateUtil.getDateFromEveryMonth(sch.getRepeatdate(), scheduledate, scheduledate, dbstartdate, dbenddate, iCal);
					if ( loop != null) {							
						int loopLen = loop.length;
						for ( int k = 0 ; k < loopLen ; k++ ) {
							list.add(loop[k].toString());
						}	
					}
				} else {
					list.add(scheduledate);
				}
				
				//예외처리 (종일스케줄의 경우 비정규스케줄로 변경) 
				//그 외에는 스케줄 객체에 저장
				if ( sch.getAllDayYn() != null && sch.getAllDayYn().trim().equals("Y") ) {
					if ( list != null && list.size() > 0) {
						SpecialDayInfo info = new SpecialDayInfo();
						//long id = cursol.getLong((cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
						info.setId(sch.getScheduleId());
						info.setUserColor(sch.getUseColor());
						info.setName(sch.getScheduleName());
						info.setSubName(ComUtil.getStrResource(this, R.string.allday));
						info.setGubun("S");
						
						mNScheduleList.add(info);
					}	
				}
				else {
					if ( list != null && list.size() > 0) {
						sch.setScheduleDate2(list);
						userlist.add(userid);
						mScheduleList.add(sch);
					}					
				}
				cur.moveToNext();
			}
			if ( cur != null ) cur.close();
			mDbUser.close();

			//////////////////////////////////////////////////////
			//화면에 그려질 사용자 id 및 사용자별 스케줄 건수 생성
			ArrayList < Long > sortUserList = new ArrayList< Long >();
			
			//list dup erase
			HashSet<Object> hs = new HashSet<Object>(userlist); 
			Iterator<Object> it = hs.iterator(); 
			while(it.hasNext()){ 
				sortUserList.add((Long) it.next());
			}
			//list sort
			Collections.sort(sortUserList);
			
			//사용자수
			m_userCnt = sortUserList.size();	
			mUserList = new String [m_userCnt][2];
			int schcnt = userlist.size();
			for(int i = 0 ; i < m_userCnt ; i++) {
				mUserList[i][0] = sortUserList.get(i).toString();
				int count = 0;
				for(int j = 0 ; j < schcnt ; j++) {
					if ( ComUtil.stringToLong(mUserList[i][0]) == userlist.get(j) ) {
						count++;
					}
				}
				mUserList[i][1] = ComUtil.intToString(count);
			}
		}
		
		//스케줄 위치정보 저장
		int [][] pos = new int [m_userCnt][1];
		int cnt = mScheduleList.size();
		for(int i = 0 ; i < cnt ; i++) { 
			ScheduleInfo sch = new ScheduleInfo();
			sch = mScheduleList.get(i);
			for(int j = 0 ; j < m_userCnt ; j++) {	
				if ( sch.getUserId() == ComUtil.stringToLong(mUserList[j][0])) {
					if ( pos[j][0] < 0 ) pos[j][0] = 0;
					sch.setPosition(pos[j][0]);
					pos[j][0]++;
				}
			}	
			mScheduleList.set(i, sch);
		}
	}
    
	/*
	 * schedule data(year, month fix)
	 */
    public void getSpecialdayPerDate( String scheduledate ) {		
		
    	Cursor cursol = null;
    	Cursor curlun = null;
    	int arrayLength = 0;
    	    	
    	SpecialDayDbAdaper mDbSpecialHelper = new SpecialDayDbAdaper(this);
    	mDbSpecialHelper.open();  
    	
    	String year = scheduledate.substring(0, 4);
    	String monthday = scheduledate.substring(4);
    	
    	//양력
    	if ( scheduledate != null && scheduledate.length() == 8 ) {
    		cursol = mDbSpecialHelper.fetchSpecialDayForSolar(year, monthday );
    	}    
    	//음력
    	if ( scheduledate != null && scheduledate.length() == 8 ) {
    		LunarDataDbAdaper dbadapter = new LunarDataDbAdaper(this);
    		dbadapter.open();
    		String [] sDate = LunarDataDbAdaper.getSolarToLunar(this, dbadapter, scheduledate);
    		year = SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_YEAR);
    		monthday = SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_MONTH)
    				+  SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_DAY);
    		curlun = mDbSpecialHelper.fetchSpecialDayForLunar(year, monthday, sDate[1]);
    		dbadapter.close();
    	}  

        //data setting
    	//양력(국공일,기념일 모두 포함)
    	arrayLength = cursol.getCount();
    	for(int i = 0 ; i < arrayLength ; i++)
		{
			String locale = ComUtil.setBlank(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LOCALE)));
			String gubun = ComUtil.setBlank(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_GUBUN)));
			if ( locale != null && gubun != null && gubun.trim().equals(ComConstant.PUT_BATCH)
					&& !locale.trim().equals(ComConstant.NATIONAL)) {
				/////////////////////
			} else {    		
				SpecialDayInfo info = new SpecialDayInfo();
				long id = cursol.getLong((cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
				info.setId(id);	
				info.setName(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME)));
				info.setSubName(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME)));
				info.setGubun(gubun);
				
				mNScheduleList.add(info);
			}
			//다음건 처리
			cursol.moveToNext();
		}

    	//음력
    	arrayLength = curlun.getCount();
		for(int i = 0 ; i < arrayLength ; i++)
		{
			SpecialDayInfo info = new SpecialDayInfo();
			long id = curlun.getLong((curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
			info.setId(id);
			info.setName(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME)));
			info.setSubName(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME)));
			info.setGubun(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_GUBUN)));

			mNScheduleList.add(info);
			//다음건 처리
			curlun.moveToNext();
		}	

		if ( cursol != null ) cursol.close();
		if ( curlun != null ) curlun.close();
		//mDbUser.close();
		mDbSpecialHelper.close();
	} 
    
	/// 각 버튼들에 setOnClickListener 주기
	public void setOnEvent( )
	{
		//m_celly  : click event 로 변경
		/// 월화수목금토일 들어가있는 부분에는 눌러도 반응할 필요 없음
		// 2000601 스케줄이나 기념일정보가 없는 경우 스케줄등록화면 open
		for( int i = 0 ; i < m_userCnt ; i++ ){
			int sCnt = ComUtil.stringToInt(mUserList[i][1]);
			for( int j = 0 ; j < sCnt ; j++ )
			{
				final int k = i ;
				final int l = j ;
				m_scheduleLy[k][l].setOnClickListener( new Button.OnClickListener( ) 
				{
					@Override
					public void onClick(View v) 
					{						
						ScheduleInfo info = new ScheduleInfo();
						info 	= (ScheduleInfo) m_scheduleLy[k][l].getTag();
						mGubun  = "S";
						mID 	= info.getScheduleId();
						mName 	= info.getScheduleName();
						
						mMessage = ComUtil.makeScheduleMsg(m_context, info, mFromDate);
						
						registerForContextMenu(m_scheduleLy[k][l]);
			    		openContextMenu(m_scheduleLy[k][l]);
			    		unregisterForContextMenu(m_scheduleLy[k][l]);
			    		
//						Intent mIntent = new Intent(m_context, ScheduleManager.class);
//						mIntent.putExtra(ComConstant.SCHEDULE_GUBUN, ComUtil.getStrResource(m_context, R.string.modify));
//						mIntent.putExtra(ScheduleDbAdaper.KEY_SCHDULEID, mID); 
//						mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			    		startActivityForResult(mIntent, ComConstant.ACTIVITY_EDIT); 
			    		
	
					}
				
				} ) ;
				
			}
		}
		for( int j = 0 ; j < m_nspecialCnt ; j++ )
		{
			final int k = j ;
			m_nscheduleLy[k].setOnClickListener( new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{
					//종일스케줄도 포함되어있으니 주의
					SpecialDayInfo info = new SpecialDayInfo();
					info 	= mNScheduleList.get(k);
					mGubun  = info.getGubun();
					mID 	= info.getId();
					mName 	= info.getName();
					
					mMessage = ComUtil.makeScheduleMsg(m_context, info);	
					
			    	if ( mGubun != null &&  ( mGubun.equals("U") || mGubun.equals("S")) ) {
			    		registerForContextMenu(m_nscheduleLy[k]);
			    		openContextMenu(m_nscheduleLy[k]);
			    		unregisterForContextMenu(m_nscheduleLy[k]);  		
			    	} 						

				}
			
			} ) ;
			
		}		
	}
        
    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	outState.putSerializable(ScheduleDbAdaper.KEY_USERID, mUserid);
    	outState.putSerializable(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
    }
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
    	
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
        RecycleUtil.recursiveRecycle(mRoot);
    }
    /*
     * menu create
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater(); 
//        inflater.inflate(R.menu.user_menu, menu);
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
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, mFromDate);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        setAllView();
    }
	
	public int getLyaoutWidth ( LinearLayout lv ) {
		int width = 0;
		if ( lv != null ) {
			width = lv.getWidth();
		}
		return width;
	}
}
