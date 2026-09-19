package com.waveapp.smcalendarlite;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.ScheduleDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.link.BannerLink;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;
import com.waveapp.smcalendarlite.util.gsCalendar;
import com.waveapp.smcalendarlite.util.gsCalendar.gsCalendarColorParam;

/*
 * 주간 스케줄 화면 
 * -. 가로:2, 세로:4 (마지막 칸은 공란 : 달력view link )
 */
public class ScheduleDayOfWeek extends SMActivity  implements OnClickListener, OnTouchListener{
	
	Context m_context ;
	int cellCnt 	= 8;
	int dateCellCnt = 7;

	Calendar 		m_Calendar;
	myGsCalendar 	cal ;
	
	LinearLayout 	viewroot ;	
	LinearLayout	lv_calendar;
	ViewFlipper 	mFlipper;
	ScrollView 		mScroll ;
	LinearLayout  	m_targetLayout;		// 주간일정 layout

    //스케줄
	LinearLayout [ ] m_VLineLayout;		// 세로선(4개)
	LinearLayout [ ] m_HLineLayout;		// 가로선(4개)
	LinearLayout [ ] m_dayVLine ; 		///세로 라인 가로
	
	LinearLayout [ ] m_HorizontalLy ;	// 가로칸(4개)
	LinearLayout [ ] m_sectionLy ;		// 일정 ( 4 * 4 )
	
	
	//날짜칸 ( 날짜, 건수 : 기념일+스케줄)
	LinearLayout [ ] m_dateLy ;			// 날짜 textview (7개)
	TextView [ ] 	 m_cellWeek ;		// 요일 textview (7개)
    TextView [ ] 	 m_cellDate ;		// 날짜 textview (7개)
    TextView [ ] 	 m_cellCnt ;		// 일정건수textview (7개)
    
    LinearLayout [ ] m_dailyLy  ;		// 스케줄 and 기념일(7개)
    
    LinearLayout [ ][ ] m_scheduleLy  ;		// 스케줄용(7 * N)
    TextView [ ][ ]  m_cellScheduleColor  ;	// 스케줄색상	textview(7 * N)
    TextView [ ][ ]  m_cellScheduleTime  ;	// 스케줄시작시간	textview(7 * N)
    TextView [ ][ ]  m_cellScheduleName  ;	// 스케줄명 	textview(7 * N)
    
    LinearLayout [ ][ ] m_specialLy  ;			// 기념일용(7 * N)
    TextView [ ][ ]  m_cellSpecialColor  ;	// 기념일색상	textview(7 * N)
    TextView [ ][ ]  m_cellSpecialGubun  ;	// 기념일구분	textview(7 * N)
    TextView [ ][ ]  m_cellSpecialName  ;	// 기념일명 	textview(7 * N)
    

    LinearLayout 	m_calendarDate ;		// 미니달력(년월)
    TextView   		m_cellCalendarDate  ;	// 미니달력(년월) textview
    ImageButton   	m_imgPreMonth  ;		// 미니달력(이전월) 
    ImageButton   	m_imgNextMonth  ;		// 미니달력(다음월) 
	LinearLayout    m_calendarLy ;			// 미니달력
	LinearLayout    m_calHLineLayout ;		// 미니달력(하단 줄)
    
	TextView tvs[] ;
	ImageButton btns[] ;
    ImageButton mAddBtn ;
    LinearLayout mAddLin;	
    
    TextView m_yearTv ;				/// 년 표시용 텍스트   
    TextView m_monthTv ;			/// 월 표시용 텍스트  
    TextView m_monthweekTv ;		/// 주수 표시용 텍스트  

	////////////////////////////////////////
    float m_displayScale ;			/// 화면 사이즈에 따른 텍스트 크기 보정값 저장용 (density)
    float m_displayHeight ;			/// 화면 사이즈에 따른 텍스트 크기 보정값 저장용 (density)
    float m_displayWidth;
    float m_topTextSize ;			/// 텍스트 사이즈(위 라인의 변수와 곱해짐)
    float m_textSize ;				/// 스케줄명
    float m_timeTextSize ;				/// 스케줄명
    
    int m_cHeight 	= 130 ;			/// 한칸의 높이
    int m_cCalHeight= 170 ;			/// 한칸의 높이
    int m_tHeight 	= 30 ;
    int m_timeWidth = 75 ;
    int m_margin 	= 5 ;
    
    int m_lineSize = 1 ;			/// 경계선의 굵기
    
    int calWidth 	= 100;  //default
    int calHeight 	= 100;	//default
    
    gsColorParam cParam ;
    
    /// 있으면 적용하고 없으면 bgcolor로 처리함( 각각 개별적으로 )
    Drawable m_bgImgId = null ;				/// 전체 배경이미지
    Drawable m_cellBgImgId = null ;			/// 한칸의 배경 이미지
    
    public static class gsColorParam
    {
    	public int m_lineColor 			= 0xff000000 ;	/// 경계선 색
        public int m_tCellColor 		= 0xffffffff ;	/// 칸의 배경색
        public int m_sCellColor 		= 0xffffffff ;	/// 칸의 배경색
        public int m_tTextColor 		= 0xff000000 ;	/// 글씨색
        public int m_sTextColor 		= 0xff000000 ;	/// 글씨색
//        public int m_sSaturdayTextColor = 0xff000000 ;	/// 토요일
//        public int m_sSundayTextColor 	= 0xff000000 ;	/// 일요일/휴일

    }

	private String mFromDate;
	private String mToDate;
	
    Long 	mID  ;
    String  mName;
    String  mMessage;
    String  mGubun;
	
	ArrayList< String > mDateList 		= new ArrayList< String >();	
	ArrayList < ArrayList< ScheduleInfo > >	mScheduleList ;
	ArrayList < ArrayList< SpecialDayInfo > >	mSpecialdayList ;
	
	int m_nPreScrollPosX = 0;    //scroll 용
	int m_nPreScrollPosY = 0;    //scroll 용
	int m_nPreCellPosX   = 0;    //cell용
	int m_nPreCellPosY   = 0;    //cell용
	int m_nPreActionMove = 0;    //cell용
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        
        m_context = this;
        
        setContentView(R.layout.schedule_dayofweekview);
        
        viewroot		= (LinearLayout)findViewById( R.id.viewroot ) ; 
        mScroll 		= (ScrollView)findViewById( R.id.sc_cal ) ;
        m_targetLayout 	= (LinearLayout)findViewById( R.id.week_lLayout ) ;
        //view flipper
        mFlipper 		= (ViewFlipper)findViewById(R.id.vf_cal);
        
        // 년 월 일 표시할 텍스트뷰(년도만)
        tvs = new TextView[3] ;
        tvs[0] = (TextView)findViewById( R.id.year ) ;
        tvs[1] = (TextView)findViewById( R.id.month ) ;
        tvs[2] = (TextView)findViewById( R.id.week ) ;
        
        // 누르면 년 월 일 조절할 버튼
        btns = new ImageButton[4] ;
        btns[0] = null ; // 년도는 조절하지 않음
        btns[1] = null ; // 위와 동일
        btns[2] = (ImageButton)findViewById( R.id.previousweek) ;
        btns[3] = (ImageButton)findViewById( R.id.nextweek ) ; 
        
        mAddBtn 	= (ImageButton) findViewById(R.id.add); 
        mAddLin 	= (LinearLayout)findViewById( R.id.lin_add ) ;
        //날짜버튼 setting
        setViewTarget( tvs ) ;       

        //parent parameter 값 setting
        getParameter( savedInstanceState ) ; 
        
        //초기화
        layoutInitialization();
        
        btns[2].setOnClickListener(this); 
        btns[3].setOnClickListener(this);
        mScroll.setOnTouchListener(this);
        mAddLin.setOnClickListener(this);
        
        //공통 title. menu
        ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.title_weekschedule, 0), View.INVISIBLE);  

        //광고배너
        BannerLink banner = new BannerLink();
        banner.callBannerLink(this); 
       
        ///언어값에 따라 날짜bar 구성 변경
        TextView year_tv 	= (TextView)findViewById( R.id.year_tv ) ;
        TextView month_tv 	= (TextView)findViewById( R.id.month_tv ) ;  
        TextView week_tv 	= (TextView)findViewById( R.id.week_tv ) ; 
        TextView gap 		= (TextView)findViewById( R.id.gap ) ;   
        
        if ( ComUtil.isLanguageFront(this))  {        	
        	ViewUtil.addViewLayoutParam(year_tv, RelativeLayout.RIGHT_OF, tvs[0]);
        	ViewUtil.addViewLayoutParam(gap, 	RelativeLayout.RIGHT_OF, year_tv);
        	ViewUtil.addViewLayoutParam(tvs[1], RelativeLayout.RIGHT_OF, gap);
        	ViewUtil.addViewLayoutParam(month_tv, RelativeLayout.RIGHT_OF, tvs[1]);
        	ViewUtil.addViewLayoutParam(tvs[2], RelativeLayout.RIGHT_OF, month_tv);
        	ViewUtil.addViewLayoutParam(week_tv, RelativeLayout.RIGHT_OF, tvs[2]);
        	
        	year_tv.setText(ComUtil.getStrResource(this, R.string.year));
        	month_tv.setText(ComUtil.getStrResource(this, R.string.month));
        	week_tv.setText(ComUtil.getStrResource(this, R.string.week));
        } else {
        	
        	ViewUtil.addViewLayoutParam(month_tv, RelativeLayout.RIGHT_OF, tvs[1]);
        	ViewUtil.addViewLayoutParam(gap, RelativeLayout.RIGHT_OF, month_tv); 
        	ViewUtil.addViewLayoutParam(tvs[0], RelativeLayout.RIGHT_OF, gap);
        	ViewUtil.addViewLayoutParam(year_tv, RelativeLayout.RIGHT_OF, tvs[0]);
        	ViewUtil.addViewLayoutParam(tvs[2], 	RelativeLayout.RIGHT_OF, year_tv);
        	ViewUtil.addViewLayoutParam(week_tv, RelativeLayout.RIGHT_OF, tvs[2]);        	
        	
        	year_tv.setText("");
        	month_tv.setText(" ");
        	week_tv.setText("'st");
        }  
    }
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		

		//super.myTouchEvent( v,event );
		//scroll 의 경우 action down 발생하지 않음--> move 로 체크
		
		if (  v == mScroll ) {
		
			if (event.getAction() == MotionEvent.ACTION_MOVE)
 			{
	   			//최초 좌표저장
	   			if ( m_nPreScrollPosX == 0 && m_nPreScrollPosY == 0 ) {
	   				m_nPreScrollPosX = (int)event.getRawX();
	   				m_nPreScrollPosY = (int)event.getRawY();
	   			}
			}	
	
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				//event.get
				int nPosX = (int)event.getRawX();
				int nPosY = (int)event.getRawY();
				
				//최초 좌표정보와 같을 경우 skip
				if ( m_nPreScrollPosX == 0 && m_nPreScrollPosY == 0 ) return false;
				
				int absX =  Math.abs( nPosX - m_nPreScrollPosX ) ;
				int absY =  Math.abs( nPosY - m_nPreScrollPosY ) ;
				
				//x, y 좌표 gap 비교  : y변경값이 클경우 scroll 우선처리
				if  ( absX <=  absY ) {
					m_nPreScrollPosX = 0;
					m_nPreScrollPosY = 0;
					return false;
				}
				
				if ( nPosX  < m_nPreScrollPosX )
				{
					MoveNextView();
					return true;
				}
				else if (nPosX > m_nPreScrollPosX)
				{
					MovewPreviousView();
					return true;
				}
				return true;
			}	
		} else	{

	   		if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
	   			//최초 좌표저장
	   			if ( m_nPreCellPosX == 0 && m_nPreCellPosY == 0 ) {
	   				m_nPreCellPosX = (int)event.getRawX();
	   				m_nPreCellPosY = (int)event.getRawY();

	   			}
			}
//	   		if (event.getAction() == MotionEvent.ACTION_MOVE)
//			{
//	   			//ACTION_MOVE 처리여부
//	   			if ( m_nPreCellPosX != 0 || m_nPreCellPosY != 0 ) {
//	   				m_nPreActionMove = 1;
//
//	   			}
//			}
	   		if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
			{
//	   			if ( m_nPreActionMove > 0 ) {
					int nPosX = (int)event.getRawX();
					int nPosY = (int)event.getRawY();
					
					//최초 좌표정보와 같을 경우 skip
					if ( m_nPreCellPosX == 0 && m_nPreCellPosY == 0 ) return false;

					int absX =  Math.abs( nPosX - m_nPreCellPosX ) ;
					int absY =  Math.abs( nPosY - m_nPreCellPosY ) ;
					
					//x, y 좌표 gap 비교  : y변경값이 클경우 scroll 우선처리
					if  ( absX <=  absY ) {
						m_nPreCellPosX = 0;
						m_nPreCellPosY = 0;
						m_nPreActionMove = 0;
						return false;
					}
					
					if ( nPosX  < m_nPreCellPosX ) {
						MoveNextView();
						return true;
					} else if (nPosX > m_nPreCellPosX) {
						MovewPreviousView();
						return true;
					}	   				
//	   			}
			}
		}

		return false;
	}
	
    private void MoveNextView()
    {	    	
    	nextWeek();
    	setAllView();
    	ViewUtil.nextFlipper(this, mFlipper);
    }
    
    private void MovewPreviousView()
    {	    	
    	preWeek();
    	setAllView();
    	ViewUtil.previousFlipper(this, mFlipper);
    }  
    private void getParameter ( Bundle savedInstanceState ) {
        
        //default  : today
        mFromDate 	= SmDateUtil.getTodayDefault() ;
        mToDate 	= SmDateUtil.getTodayDefault() ;
        
        //instance가 null이 아닌경우에는 instance에서, null인 경우는 intent에서 값을 가져옴
        if (savedInstanceState != null) {
        	mFromDate 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_STARTDATE);
        	mToDate 	= savedInstanceState.getString(ScheduleDbAdaper.KEY_ENDDATE);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	mFromDate 	= extras.getString(ScheduleDbAdaper.KEY_STARTDATE);
        	mToDate 	= extras.getString(ScheduleDbAdaper.KEY_ENDDATE);
        }
        
        int year 	= SmDateUtil.getDateToInt(mFromDate, ComConstant.GUBUN_YEAR);
        int month 	= SmDateUtil.getDateToInt(mFromDate, ComConstant.GUBUN_MONTH);
        int day 	= SmDateUtil.getDateToInt(mFromDate, ComConstant.GUBUN_DAY);
        
        m_Calendar = Calendar.getInstance();
        m_Calendar.set(year, month - 1 , day);    
        
    }
    private void setAllView( ) {
    	
		//drag 좌표정보 초기화
		m_nPreScrollPosX = 0;
		m_nPreScrollPosY = 0;
		m_nPreCellPosX = 0;
		m_nPreCellPosY = 0;
		m_nPreActionMove = 0;
		
    	createViewDayOfWeek( ); 
    	getDaySchduleList();    	
    	setOnEvent();
    	printView( ) ;

    	//달력 (두번째 호출부터)
    	if ( cal != null ){
	    	initCalendarView();
	    	printCalendarView();
    	}

    }    
    
    private void createViewDayOfWeek( ) {
    	
    	clearViewItem( );
		createViewItem( ) ;
        setLayoutParams( ) ;
        setLineParam( ) ;
        setContentext( ) ; 
    	
    }
    private void getDaySchduleList( ) {

		//순서불변 
		getSpecialdayPerDate();
		getSchedulePerDate();
    	
		setSchduleList();
    }

	// 텍스트뷰를 넣어주면 각각 뿌려줌 (빈게 들어있으면 안뿌림)
    private void setViewTarget( TextView [] tv ) 
	{
		m_yearTv 		= tv[0] ;
		m_monthTv 		= tv[1] ;
        m_monthweekTv 	= tv[2] ;
        
	}
	
    //년 월 일을 출력해줌
    private void printView( )
	{
    	int year 	=  m_Calendar.get( Calendar.YEAR );
    	int month	= (m_Calendar.get( Calendar.MONTH ) + 1 ); 
    	int week  	=  m_Calendar.get( Calendar.WEEK_OF_YEAR );
    	
		/// 텍스트 뷰들이 있으면 그 텍스트 뷰에다가 년 월 일을 적어넣음
		if( m_yearTv != null )
			m_yearTv.setText( year+ ""  ) ;
		if( m_monthTv != null ) {
			if ( ComUtil.isLanguageFront(this) ) 
				m_monthTv.setText( month + "" ) ;
			else 
				m_monthTv.setText(SmDateUtil.getMonthForEng( month + "" )) ;
		}
		if( m_monthweekTv != null )
			m_monthweekTv.setText( week + "" ) ;
		

	}    
    
    //이전,다음버튼 click시 날짜값 update
	private void preWeek( )
	{
		m_Calendar.add( Calendar.DAY_OF_MONTH, -7 ) ;
		mFromDate = SmDateUtil.getDateFromCal(m_Calendar);
		mToDate = SmDateUtil.getDateFromCal(m_Calendar);
	}
	private void nextWeek( )
	{
		m_Calendar.add( Calendar.DAY_OF_MONTH, 7 ) ;
		mFromDate = SmDateUtil.getDateFromCal(m_Calendar);
		mToDate = SmDateUtil.getDateFromCal(m_Calendar);
	} 
    
    private void clearViewItem(  ) {
    	
		// linaearlayout 삭제
    	m_targetLayout.removeAllViews();
    	
	}
	
    // Layout 초기생성
	public void layoutInitialization(  )
	{ 
	    
		//가로 : 시간기준, 세로:스케줄기준
	    m_HorizontalLy 	= new LinearLayout[ 4 ] ;
	    m_dayVLine 		= new LinearLayout[ 4 ] ;      
	    m_sectionLy 	= new LinearLayout[ cellCnt ] ;	
	    
	    
	    //날짜
	    m_dateLy		= new LinearLayout[ dateCellCnt ] ;
	    m_cellWeek		= new TextView[ dateCellCnt ] ; 
	    m_cellDate		= new TextView[ dateCellCnt ] ; 
	    m_cellCnt		= new TextView[ dateCellCnt ] ; 
	    
	    //일정 (스케줄 + 기념일)
	    m_dailyLy 			= new LinearLayout[ dateCellCnt ] ;
	    
	    m_scheduleLy 		= new LinearLayout[ dateCellCnt ][ ] ; 
	    m_cellScheduleColor = new TextView[ dateCellCnt ][ ] ; 
	    m_cellScheduleTime	= new TextView[ dateCellCnt ][ ] ; 
	    m_cellScheduleName 	= new TextView[ dateCellCnt ][ ] ; 
	    
	    m_specialLy 		= new LinearLayout[ dateCellCnt ][ ] ;
	    m_cellSpecialColor 	= new TextView[ dateCellCnt ][ ] ; 
	    m_cellSpecialGubun	= new TextView[ dateCellCnt ][ ] ; 
	    m_cellSpecialName 	= new TextView[ dateCellCnt ][ ] ; 
	    
	    m_VLineLayout		= new LinearLayout[ 4 ] ;
	    m_HLineLayout 		= new LinearLayout[ 4 ] ;
	    
        /// 화면의 크기에 따른 보정값
        m_displayScale 	= ViewUtil.getDensity(this) ;
        m_displayHeight = this.getResources( ).getDisplayMetrics( ).heightPixels;
        m_displayWidth = this.getResources( ).getDisplayMetrics( ).widthPixels ;
        
	    if ( ( m_displayHeight > 800 || m_displayWidth > 800 ) && m_displayScale == 1 ) {
	        m_topTextSize 	= m_displayScale * 16.0f ;
	        m_textSize 		= m_displayScale * 15.0f ;
	        m_timeTextSize 	= m_displayScale * 13.0f ;            
	    } else if ( m_displayScale == 2 ) {
            m_topTextSize 	= m_displayScale * 7.0f ;
            m_textSize 		= m_displayScale * 8.0f ;
            m_timeTextSize 	= m_displayScale * 6.0f ;        	
	    } else if ( m_displayScale >= 3 ) {
            m_topTextSize 	= ( 2 ) * 7.0f ;
            m_textSize 		= ( 2 ) * 8.0f ;
            m_timeTextSize 	= ( 2 ) * 6.0f ; 

        } else {
            m_topTextSize 	= m_displayScale * 8.0f ;
            m_textSize 		= m_displayScale * 9.0f ;
            m_timeTextSize 	= m_displayScale * 7.0f ;        	
        }


        //화면간격 resizing
        setViewSize();
        
        Resources res = getResources();
        cParam = new gsColorParam( );
        cParam.m_lineColor 		= res.getColor(R.color.calline);
        cParam.m_sCellColor 	= res.getColor(R.color.calcellback);
        cParam.m_tCellColor 	= res.getColor(R.color.calcellback);
        cParam.m_sTextColor 	= res.getColor(R.color.white); 
        cParam.m_tTextColor 	= res.getColor(R.color.caldaytext);
//        
	}
	
	/*
	 * view사이즈 density에 따라 변경(고해상도 단말기 지원용)
	 */
	private void setViewSize ( ) {
		//화면보정값추가
		int width = 0;
		int height = 0;
		
        int displayWidth 	= this.getResources( ).getDisplayMetrics( ).widthPixels;
        int displayHeight 	= this.getResources( ).getDisplayMetrics( ).heightPixels;
        
        //달력사이즈 setting ( 메뉴와 날짜바 적용후 보정처리)
        LinearLayout lin_date = (LinearLayout)findViewById( R.id.lin_date ) ;
        LinearLayout.LayoutParams dateParam = (LayoutParams) lin_date.getLayoutParams();	
        
        Configuration conf 	=  getResources().getConfiguration();
		if ( conf.orientation == Configuration.ORIENTATION_LANDSCAPE ) {
//			width = displayWidth + 5;
			width = displayWidth;
			height = (int) (displayHeight * m_displayScale) ;
		} else {
			width = displayWidth + 5;
			height = displayHeight - dateParam.height;
		}
	    
	    m_cHeight 	= (int) (( height - (m_lineSize * 5) - ( 3 * m_displayScale )) / 4 );
	    m_tHeight 	= (int) ( m_cHeight / 7);
	    
	    m_cCalHeight= (int) ( m_cHeight + ( 2 * m_displayScale ) );
	    m_timeWidth = (int) ( width / ( 2 + 4 ));
	    
	}
	
	/// 레이아웃과 버튼 그리고 경계션으로 쓸 라인용 레이아웃들을 생성한다.
	// -. 8칸 짜리 레이아웃 만들기
	public void createViewItem(  )
	{
		
		//m_dayLayout.setMinimumWidth( m_scheduleLayout.getWidth() / 4 );
		
		//1.create section 
		//가로 : 4, 세로 : 2 ( m_sectionLy 8개 생성)
        for( int i = 0 ; i < cellCnt  ; i++ ) {
        	//짝수(가로칸  추가 , 세로칸, 세로줄 추가)
        	if( i % 2 == 0 ) {
        		
        		m_HorizontalLy[ i / 2 ] = new LinearLayout( m_context ) ;        		
	        	m_VLineLayout[ i / 2 ] 	= new LinearLayout( m_context ) ;	        	
	        	m_targetLayout.addView( m_HorizontalLy[ i / 2 ] ) ; 
	        	
	        	m_sectionLy[ i ] 			= new LinearLayout( m_context ) ;
	        	m_HorizontalLy[ i / 2 ].addView( m_sectionLy[ i ]) ; 
	        	m_HorizontalLy[ i / 2 ].addView( m_VLineLayout[ i / 2 ]) ; 
	        	
	        	 //세로줄        	
	            //m_VLineLayout[ i / 2 ].setBackgroundColor(cParam.m_lineColor); 
	            //m_HorizontalLy[ i / 2 ].setBackgroundColor(Color.RED);  
	            
	        	
	        //홀수( 세로칸 추가)
        	} else {
        		m_sectionLy[ i ] 		= new LinearLayout( m_context ) ;      	
        		m_HorizontalLy[ i / 2 ].addView( m_sectionLy[ i ]) ;  
        		
        		m_HLineLayout[ i / 2 ] 	= new LinearLayout( m_context ) ;
        		m_targetLayout.addView( m_HLineLayout[ i / 2 ]) ;
        	}
            
        }
        
      	//2.create data section 
        //0~6 : 일정
        for( int i = 0 ; i < dateCellCnt ; i++ ) {
        	
        	m_dateLy[ i ] 	= new LinearLayout( m_context ) ;  
        	m_dailyLy[ i ] 	= new LinearLayout( m_context ) ; 
        	m_sectionLy[ i ].addView( m_dateLy[ i ] ) ;  
        	m_sectionLy[ i ].addView( m_dailyLy[ i ] ) ; 
        	  
        	m_cellWeek[ i ]   = new TextView( m_context ) ;  
        	m_cellDate[ i ]   = new TextView( m_context ) ;   
        	m_cellCnt[ i ]   = new TextView( m_context ) ; 
        	m_dateLy[ i ].addView( m_cellWeek[ i ] ) ;  
        	m_dateLy[ i ].addView( m_cellCnt[ i ] ) ;  
        	m_dateLy[ i ].addView( m_cellDate[ i ] ) ; 
        }  


	      //7:달력view 추가
	      //달력년월일
	      m_calendarDate 		= new LinearLayout( m_context ) ;     
	      m_cellCalendarDate	= new TextView( m_context ) ; 
	      m_imgPreMonth			= new ImageButton( m_context ) ;
	      m_imgNextMonth		= new ImageButton( m_context ) ;
	      m_sectionLy[ 7 ].addView( m_calendarDate );
	      m_calendarDate.addView( m_imgPreMonth ) ;  
	      m_calendarDate.addView( m_cellCalendarDate ) ;  
	      m_calendarDate.addView( m_imgNextMonth ) ; 
	      
	      //8.달력/년월일 사이 줄      
	      m_calHLineLayout 	= new LinearLayout( m_context ) ;   
	      m_sectionLy[ 7 ].addView( m_calHLineLayout ) ; 
	      
	      //달력
	      m_calendarLy 	= new LinearLayout( m_context ) ; 
	      m_sectionLy[ 7 ].addView( m_calendarLy ) ; 
   
      
	}	
	
	/// 레이아웃과 버튼의 배경색, 글씨색 등 ViewParams를 셋팅
	public void setLayoutParams(   )
	{
		//full params(for linearlayout)
		LinearLayout.LayoutParams paramsLin 
				= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT , LinearLayout.LayoutParams.FILL_PARENT  );
		paramsLin.weight = 1;

		//height limit params(for linearlayout)
		LinearLayout.LayoutParams paramsLinH	 
				= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT , m_tHeight  );
		
		for( int i = 0 ; i < cellCnt ; i++ ) {
		
			if ( i % 2 == 0 ) {
				ViewUtil.setAttLinearView( m_HorizontalLy[ i / 2 ], paramsLin, LinearLayout.HORIZONTAL, 0 );
			}
			
			/// 일일 일정 section (날짜/일정리스트)
			ViewUtil.setAttLinearView( m_sectionLy[ i ], paramsLin, LinearLayout.VERTICAL, 0 );
			
			if ( i < dateCellCnt ) {

				//날짜 
				ViewUtil.setAttLinearView( m_dateLy[ i ], paramsLinH , LinearLayout.HORIZONTAL, getResources().getDrawable(R.drawable.sm_press_weekdate) );
				m_dateLy[ i ].setMinimumHeight(50);
				ViewUtil.setAttTextView( m_cellWeek[ i ], new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT  )
										, m_topTextSize, Gravity.LEFT, m_margin, 0, 0, false );
				ViewUtil.setAttTextView( m_cellCnt[ i ], new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT  )
								, m_timeTextSize, Gravity.LEFT, 0, m_margin, 0,  false );
				ViewUtil.setAttTextView( m_cellDate[ i ], new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT  )
								, m_topTextSize, Gravity.RIGHT, 0, m_margin, 1, false );
				
				//일정리스트
				ViewUtil.setAttLinearView( m_dailyLy[ i ], paramsLin, LinearLayout.VERTICAL, 0);
				m_dailyLy[ i ].setMinimumHeight( m_cHeight - ( m_tHeight * 2) );
				m_dailyLy[ i ].setBackgroundColor(this.getResources().getColor(R.color.listback));				
			} else {
				//달력날짜
				ViewUtil.setAttLinearView( m_calendarDate, paramsLinH, LinearLayout.HORIZONTAL, 0);	
				m_calendarDate.setMinimumHeight(50);
				ViewUtil.setAttTextView( m_cellCalendarDate, paramsLin, m_timeTextSize, Gravity.CENTER, 0, 0, 0, false );
				//달력
				ViewUtil.setAttLinearView( m_calendarLy, paramsLin, LinearLayout.VERTICAL, 0);
				m_calendarLy.setMinimumHeight( m_cHeight - ( m_tHeight * 2) );
				
				m_imgPreMonth.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.sm_press_left_arrow));
				m_imgNextMonth.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.sm_press_right_arrow));

			}
        }

	}

	
	public void setLineParam( )
	{
		for( int i = 0 ; i < 4 ; i ++ )
		{
			m_VLineLayout[ i ].setBackgroundColor( cParam.m_lineColor ) ;	/// 라인색
			m_VLineLayout[ i ].setLayoutParams(	/// 세로 라인이니까 가로는 꽉 가로는 두께만큼 
						new LinearLayout.LayoutParams( m_lineSize, LinearLayout.LayoutParams.FILL_PARENT ) ) ;
			m_HLineLayout[ i ].setBackgroundColor( cParam.m_lineColor ) ;	/// 라인색
			m_HLineLayout[ i ].setLayoutParams(	/// 가로 라인이니까 세로는 쭉~ 가로는 두께만큼 
						new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, m_lineSize ) ) ;

		}
		//달력줄
		
//		m_calHLineLayout.setBackgroundColor( cParam.m_lineColor ) ;	/// 라인색
		LinearLayout.LayoutParams lineparams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, m_lineSize ) ;
		lineparams.leftMargin = 5;
		lineparams.rightMargin = 5;
		
		m_calHLineLayout.setLayoutParams( lineparams ) ;/// 가로 라인이니까 세로는 쭉~ 가로는 두께만큼 

		Drawable dw = this.getResources().getDrawable(R.drawable.sm_horizontal);
		dw.setAlpha(90);
		m_calHLineLayout.setBackgroundDrawable(dw);
	}
	
	/// 일자표기 화면에 setting ( 조회일자 없는 경우, 오늘일자기준 해당 주표기)
	public void setContentext( )
	{
		Calendar iCal  = (Calendar) m_Calendar.clone();
		
		// 주수 및 주시작,종료일 구하기
		int dayofweek 	= iCal.get( Calendar.DAY_OF_WEEK);
		
		//해당주 첫번째 날짜 구하기(일요일 기준)
		iCal.add(Calendar.DAY_OF_MONTH, (1 -  dayofweek ));
		
		String date = "";
		mDateList = new ArrayList<String>();
		for( int i = 0 ; i < dateCellCnt ; i++ )
    	{
			int year 	= iCal.get( Calendar.YEAR );
			int month 	= iCal.get( Calendar.MONTH  ) + 1;
			int day		= iCal.get( Calendar.DAY_OF_MONTH );
			
			date = SmDateUtil.getDateFormat(year, month, day);
			
			mDateList.add(date);
			
			iCal.add(Calendar.DAY_OF_MONTH, 1);
    	}
		
		int len = mDateList.size();
		for( int i = 0 ; i < len ; i++ )
    	{

			String str = SmDateUtil.getDateSimpleFormat(this, mDateList.get(i).substring(4), ComConstant.SEPERATE_DOT, false);
			m_cellWeek[ i ].setText( SmDateUtil.getDayOfWeekFromCode(this, i+1) ) ;
			m_cellWeek[ i ].setTextColor(cParam.m_sTextColor);
			m_cellWeek[ i ].setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			m_cellDate[ i ].setText( str ) ;
			m_cellDate[ i ].setTextColor(cParam.m_sTextColor);
			
			//오늘인 경우 처리
			if ( mDateList.get(i).toString().equals(SmDateUtil.getTodayDefault())) {
				m_dateLy[ i ].setBackgroundDrawable(this.getResources().getDrawable(R.drawable.sm_press_weekdatetoday));
				m_cellDate[ i ].setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			}
//			if ( i == 0 ) {
//				m_dateLy[ i ].setBackgroundResource(R.color.listtop_sunday);
//			} else if ( i == 6) {
//				m_dateLy[ i ].setBackgroundResource(R.color.listtop_saturday);
//			} else {
//				m_cellWeek[ i ].setTextColor(cParam.m_sTextColor);
//			}
    	}
	}

	
	private void setSchduleList () {

		
		//스케줄 건수만큼 loop  -> position 별로 add
		int len = mDateList.size();    //날짜수만큼 생성될듯.
		for( int i = 0 ; i < len ; i++ ) {
			
			//초기화
			m_cellCnt[ i ].setText("");
			
			///1)기념일,공휴일 생성
			int spcCnt = mSpecialdayList.get(i).size();
			setSpecialDayLy( i, spcCnt );
			
			//일자별  TextView 생성
			for( int j = 0 ; j < spcCnt ; j++ ) {
				SpecialDayInfo info  = new SpecialDayInfo();
				info = mSpecialdayList.get(i).get(j);
				if ( info.getHolidayYn() != null && info.getHolidayYn().trim().equals("Y")){
					//m_cellDate[ i ].setTextColor(getResources().getColor(R.color.calsunday));
					m_cellSpecialColor[ i ][ j ].setBackgroundColor(getResources().getColor(R.color.calsunday));
					m_cellSpecialGubun[ i ][ j ].setTextColor(getResources().getColor(R.color.calsunday));
//					m_cellSpecialName[ i ][ j ].setTextColor(getResources().getColor(R.color.calsunday));
				} else {
					m_cellSpecialColor[ i ][ j ].setBackgroundColor(getResources().getColor(R.color.lightgray));
				}
				
				if ( info.getName() != null && !info.getName().trim().equals("")){
					m_cellSpecialGubun[ i ][ j ].setText(ComUtil.getSpecialDayText( this, info.getGubun(), info.getSubName() ));
					m_cellSpecialName[ i ][ j ].setText(info.getName());
				} else {
					m_cellSpecialGubun[ i ][ j ].setText(ComUtil.getSpecialDayText( this, info.getGubun(), info.getSubName() ));
					m_cellSpecialName[ i ][ j ].setText(info.getSubName());
				}
			}		
			
			
			///2)스케줄 생성
			int schCnt = mScheduleList.get(i).size();
			setScheduleLy( i, schCnt );
			
			//일자별  TextView 생성
			for( int j = 0 ; j < schCnt ; j++ ) {
				ScheduleInfo info  = new ScheduleInfo();
				info = mScheduleList.get(i).get(j);
				if ( info.getAllDayYn() != null && !info.getAllDayYn().equals("")) {
					m_cellScheduleTime[ i ][ j ].setText(ComUtil.setYesReturnValue(info.getAllDayYn(), ComUtil.getStrResource(this, R.string.allday)));
				} else {
					m_cellScheduleTime[ i ][ j ].setText(SmDateUtil.getTimeFullFormat(this,info.getStartTime()));
				}
				m_cellScheduleColor[ i ][ j ].setBackgroundColor(info.getUseColor());
				m_cellScheduleName[ i ][ j ].setText(info.getScheduleName());
			}
			
			//날짜칸에 총건수 display
			int tot = spcCnt + schCnt;
			if ( tot > 0 ) {
				m_cellCnt[ i ].setText( "(" + ComUtil.intToString(spcCnt+schCnt) + ")");
				m_cellCnt[ i ].setTextColor(cParam.m_sTextColor);
			}
			
		}		
	}
		
	/*
	 * schedule data(year, month fix)
	 */
    public void getSchedulePerDate(  ) {	
    	
        /// 달력을 하나 복사해서 작업한다.
		if ( mDateList == null || ( mDateList != null && mDateList.size() == 0) ) { 
			return ;
		}
			
		ScheduleDbAdaper mDbHelper = new ScheduleDbAdaper(this);
        mDbHelper.open();
		UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(this);
		mDbUser.open();

        Cursor cur = null;
        mScheduleList 		= new ArrayList < ArrayList < ScheduleInfo >> () ;
        
		int len = mDateList.size();
		
		for ( int j = 0 ; j < len ; j++ ) {
			
			ArrayList < ScheduleInfo > tempList = new ArrayList < ScheduleInfo > ();
			
			String date = mDateList.get(j).toString();
			cur = mDbHelper.fetchSchedulePerDate( date );
			
			int lowlen = cur.getCount();			
			for(int i = 0 ; i < lowlen ; i++) {	
				ScheduleInfo sch = new ScheduleInfo();
				
				//데이터
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
				//매월  : 월체크
				ArrayList<String> list = new ArrayList<String>();
				Calendar iCal = Calendar.getInstance();
				String sCycle = sch.getCycle().toString();
				if (sCycle != null && sCycle.trim().equals("Y")) {
					String [] loop = SmDateUtil.getDateFromDayOfWeekArr(arrDayofweek, date, date, dbstartdate, dbenddate, iCal);
					if ( loop != null) {							
						int loopLen = loop.length;
						for ( int k = 0 ; k < loopLen ; k++ ) {
							list.add(loop[k].toString());
						}	
					}			
				} else if ( sCycle  != null && sCycle.equals("M")) {
					String [] loop = SmDateUtil.getDateFromEveryMonth(sch.getRepeatdate(), date, date, dbstartdate, dbenddate, iCal);
					if ( loop != null) {							
						int loopLen = loop.length;
						for ( int k = 0 ; k < loopLen ; k++ ) {
							list.add(loop[k].toString());
						}	
					}
				} else {
					list.add(date);
				}
				
				//스케줄 객체에 저장
				if ( list != null && list.size() > 0) {
					sch.setScheduleDate2(list);
					tempList.add(sch);					
				}	
				
				cur.moveToNext();
			}
			
			mScheduleList.add(tempList);
			
			if ( cur != null ) cur.close();
		}
		mDbUser.close();
		mDbHelper.close();

	}
    
	/*
	 * schedule data(year, month fix)
	 */
    public void getSpecialdayPerDate( ) {		
    	
        /// 달력을 하나 복사해서 작업한다.
		if ( mDateList == null || ( mDateList != null && mDateList.size() == 0) ) { 
			return ;
		}

        mSpecialdayList 		= new ArrayList < ArrayList < SpecialDayInfo >> () ;
		
    	Cursor cursol = null;
    	Cursor curlun = null;
    	int arrayLength = 0;
    	    	
    	SpecialDayDbAdaper mDbSpecialHelper = new SpecialDayDbAdaper(this);
    	mDbSpecialHelper.open();  
    	LunarDataDbAdaper mDbLunarHelper = new LunarDataDbAdaper(this);
		mDbLunarHelper.open();
		
    	int len = mDateList.size();
		
		for ( int j = 0 ; j < len ; j++ ) {
			ArrayList < SpecialDayInfo > tempList = new ArrayList < SpecialDayInfo > ();
			
			String date = mDateList.get(j).toString();
			String year = date.substring(0, 4);
	    	String monthday = date.substring(4);
	    	
	    	//// 1) 데이터 가져오기
	    	//양력
	    	cursol = mDbSpecialHelper.fetchSpecialDayForSolar(year, monthday );
	    	
	    	//음력
    		
    		String [] sDate = LunarDataDbAdaper.getSolarToLunar(this, mDbLunarHelper, date);
    		year = SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_YEAR);
    		monthday = SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_MONTH)
    				+  SmDateUtil.getDateToStr(sDate[0], ComConstant.GUBUN_DAY);
    		
    		curlun = mDbSpecialHelper.fetchSpecialDayForLunar(year, monthday, sDate[1]);
    		
    		//// 2) 값 setting
        	//양력(국공일,기념일 모두 포함)
        	arrayLength = cursol.getCount();
        	for(int i = 0 ; i < arrayLength ; i++)
    		{
    			String locale = ComUtil.setBlank(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_LOCALE)));
    			String gubun = ComUtil.setBlank(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_GUBUN)));
    			if ( locale != null && gubun != null && gubun.trim().equals(ComConstant.PUT_BATCH)
    					&& !locale.trim().equals(ComConstant.NATIONAL)) {
    				///////////////////////
    			} else {    		
    				SpecialDayInfo info = new SpecialDayInfo();
    				long id = cursol.getLong((cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_ID)));
    				info.setId(id);	
    				info.setName(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_NAME)));
    				info.setSubName(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_SUBNAME)));
    				info.setHolidayYn(cursol.getString(cursol.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_HOLIDAYYN)));
    				info.setGubun(gubun);
    				
    				tempList.add(info);
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
    			info.setHolidayYn(curlun.getString(curlun.getColumnIndexOrThrow(SpecialDayDbAdaper.KEY_HOLIDAYYN)));
    			
    			tempList.add(info);
    			//다음건 처리
    			curlun.moveToNext();
    		}	
    		
    		mSpecialdayList.add(tempList);
    		if ( cursol != null ) cursol.close();
    		if ( curlun != null ) curlun.close(); 		
		}

		
		mDbLunarHelper.close(); 
		mDbSpecialHelper.close();

	} 

	//스케줄 layout 생성  : 일자별로 생성
	private void setScheduleLy(  int position , int total ) {
		
		LinearLayout.LayoutParams params 
				= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT  );
		params.bottomMargin = 2;
		
		m_scheduleLy[ position ] 		= new LinearLayout[ total ] ;
		m_cellScheduleColor[ position ] = new TextView[ total ] ;
		m_cellScheduleTime[ position ] 	= new TextView[ total ] ;
		m_cellScheduleName[ position ] 	= new TextView[ total ] ;
		
		//스케줄 정보  linaearlayout 생성
		for( int i = 0 ; i < total ; i++ ) {
			m_scheduleLy[ position ][ i ] = new LinearLayout( m_context ) ;
			m_cellScheduleColor[ position ][ i ]= new TextView( m_context ) ;
			m_cellScheduleTime[ position ][ i ] = new TextView( m_context ) ;
			m_cellScheduleName[ position ][ i ] = new TextView( m_context ) ;
			
			//m_dailyLy[ position ].addView( m_cellScheduleName[ position ][ i ] ) ;
			m_dailyLy[ position ].addView( m_scheduleLy[ position ][ i ] ) ;
			ViewUtil.setAttLinearView(m_scheduleLy[ position ][ i ], params, LinearLayout.HORIZONTAL, 0 );
			m_scheduleLy[ position ][ i ].setBackgroundResource(R.drawable.sm_press_listrow);
			
			m_scheduleLy[ position ][ i ].addView( m_cellScheduleColor[ position ][ i ] ) ;
			ViewUtil.setAttTextView(m_cellScheduleColor[ position ][ i ], new LinearLayout.LayoutParams( m_timeWidth / 8, LinearLayout.LayoutParams.WRAP_CONTENT ), 
					m_timeTextSize, Gravity.LEFT|Gravity.CENTER, m_margin, 0, 0, false);
			
			m_scheduleLy[ position ][ i ].addView( m_cellScheduleTime[ position ][ i ] ) ;
			ViewUtil.setAttTextView(m_cellScheduleTime[ position ][ i ], new LinearLayout.LayoutParams( m_timeWidth, LinearLayout.LayoutParams.WRAP_CONTENT ), 
					m_timeTextSize, Gravity.LEFT|Gravity.CENTER, m_margin, 0, 0, false);

			m_scheduleLy[ position ][ i ].addView( m_cellScheduleName[ position ][ i ] ) ;
			ViewUtil.setAttTextView(m_cellScheduleName[ position ][ i ], new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT ), 
					m_textSize, Gravity.LEFT|Gravity.CENTER, 0, m_margin, 0, false);			

			//m_cellScheduleTime[ position ][ i ].setBackgroundColor(getResources().getColor(R.color.lightcyan));
			
		}
	}
	//기념일 layout 생성  : 일자별로 생성
	private void setSpecialDayLy(  int position , int total ) {
		LinearLayout.LayoutParams params 
					= new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT  );
		params.bottomMargin = 2;
		
		m_specialLy[ position ] 	  	= new LinearLayout[ total ] ;
		m_cellSpecialColor[ position ] 	= new TextView[ total ] ;
		m_cellSpecialGubun[ position ] 	= new TextView[ total ] ;
		m_cellSpecialName[ position ] 	= new TextView[ total ] ;
		
		//스케줄 정보  linaearlayout 생성
		for( int i = 0 ; i < total ; i++ ) {
			m_specialLy[ position ][ i ] 		= new LinearLayout( m_context ) ;
			m_cellSpecialColor[ position ][ i ] = new TextView( m_context ) ;
			m_cellSpecialGubun[ position ][ i ] = new TextView( m_context ) ;
			m_cellSpecialName[ position ][ i ] 	= new TextView( m_context ) ;
			
			m_dailyLy[ position ].addView( m_specialLy[ position ][ i ] ) ;
			ViewUtil.setAttLinearView(m_specialLy[ position ][ i ], params, LinearLayout.HORIZONTAL, 0 );

			m_specialLy[ position ][ i ].addView( m_cellSpecialColor[ position ][ i ] ) ;
			ViewUtil.setAttTextView(m_cellSpecialColor[ position ][ i ], new LinearLayout.LayoutParams( m_timeWidth / 8	, LinearLayout.LayoutParams.WRAP_CONTENT ), 
					m_timeTextSize, Gravity.LEFT|Gravity.CENTER, m_margin, 0, 0, false);
			
			m_specialLy[ position ][ i ].addView( m_cellSpecialGubun[ position ][ i ] ) ;
			ViewUtil.setAttTextView(m_cellSpecialGubun[ position ][ i ], new LinearLayout.LayoutParams( m_timeWidth, LinearLayout.LayoutParams.WRAP_CONTENT ), 
					m_timeTextSize, Gravity.LEFT|Gravity.CENTER, m_margin, 0, 0, false);			
			
			m_specialLy[ position ][ i ].addView( m_cellSpecialName[ position ][ i ] ) ;
			ViewUtil.setAttTextView(m_cellSpecialName[ position ][ i ], new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT ), 
					m_textSize, Gravity.LEFT|Gravity.CENTER, 0, m_margin, 0, false);			

		}
	}
   
    @Override
    public void onClick(View v) {

		if ( v == btns[2] ) {
			preWeek();
			setAllView( ) ;
			
		} else if ( v == btns[3] ) {
			nextWeek();
			setAllView( ) ;

		} else if( v == mAddLin ) {	
			mAddBtn.setPressed(true);
			mID = (long) 0;
        	callScheduleManager(ComUtil.getStrResource(this, R.string.add));
		}
	
	}
	/// 각 버튼들에 setOnClickListener 주기
	public void setOnEvent( )
	{
		int len = mDateList.size();    //날짜수만큼 생성될듯.
		for( int i = 0 ; i < len ; i++ ) {
			final int k = i ;
			
			///0)날짜클릭시 일정조회 또는 일정등록 링크
			m_dateLy[k].setOnClickListener( new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{		
					//일정이 있는  경우, 없는 경우로분기
					int spcCnt = mSpecialdayList.get(k).size();
					int schCnt = mScheduleList.get(k).size();
					
					if ( spcCnt + schCnt > 0 ) 	 {
						callScheduleTabForDate( mDateList.get(k).toString() );
					} else {
						mID = (long) 0;
						mFromDate	= mDateList.get(k).toString() ;
						mToDate 	= mDateList.get(k).toString() ;
			        	callScheduleManager(ComUtil.getStrResource(m_context, R.string.add));						
					}
					
				}
			
			} ) ;
			
			///1)기념일,공휴일
			int spcCnt = mSpecialdayList.get(k).size();
			for( int j = 0 ; j < spcCnt ; j++ ) {
				final int l = j ;
				m_specialLy[k][l].setOnClickListener( new Button.OnClickListener( ) 
				{
					@Override
					public void onClick(View v) 
					{						
						SpecialDayInfo info  = new SpecialDayInfo();
						info = mSpecialdayList.get(k).get(l);
						mID 	= info.getId();
						mName 	= info.getName();
						mGubun  = info.getGubun();
						mMessage = ComUtil.makeScheduleMsg(m_context, info);
						
						if ( mGubun != null &&  ( mGubun.equals("U") || mGubun.equals("S")) ) {
							registerForContextMenu(m_specialLy[k][l]);
				    		openContextMenu(m_specialLy[k][l]);
				    		unregisterForContextMenu(m_specialLy[k][l]);    		
				    	} 	
					}
				
				} ) ;
			}
			///2)스케줄
			int schCnt = mScheduleList.get(k).size();
			for( int j = 0 ; j < schCnt ; j++ ) {
				final int l = j ;
				m_scheduleLy[k][l].setOnClickListener( new Button.OnClickListener( ) 
				{
					@Override
					public void onClick(View v) 
					{		
						
						ScheduleInfo info  = new ScheduleInfo();
						info = mScheduleList.get(k).get(l);
						mGubun  = "S";
						mID 	= info.getScheduleId();
						mName 	= info.getScheduleName();
						
						String date  = mDateList.get(k).toString();
						mMessage = ComUtil.makeScheduleMsg(m_context, info, date);
						
						registerForContextMenu(m_scheduleLy[k][l]);
			    		openContextMenu(m_scheduleLy[k][l]);
			    		unregisterForContextMenu(m_scheduleLy[k][l]);

					}
				
				} ) ;
			}			
		}
		
		//달력버튼
		m_imgPreMonth.setOnClickListener( new Button.OnClickListener( ) 
		{
			@Override
			public void onClick(View v) 
			{	
				
				m_Calendar.add( Calendar.MONTH, -1 ) ;
				mFromDate 	= SmDateUtil.getDateFromCal(m_Calendar);
				mToDate 	= SmDateUtil.getDateFromCal(m_Calendar);
				
				setAllView( ) ;
			}
		
		} ) ;		
		m_imgNextMonth.setOnClickListener( new Button.OnClickListener( ) 
		{
			@Override
			public void onClick(View v) 
			{	
				
				m_Calendar.add( Calendar.MONTH, 1 ) ;
				mFromDate 	= SmDateUtil.getDateFromCal(m_Calendar);
				mToDate 	= SmDateUtil.getDateFromCal(m_Calendar);
				
				setAllView( ) ;
			}
		
		} ) ;	
	}
    
	private void initCalendarView() {
	    /// 달력객체 생성
        cal = new myGsCalendar( this, m_calendarLy  ) ;
        
        /// 색상 설정할 객체 생성
        gsCalendarColorParam cParamCal = new gsCalendarColorParam( ) ;
        
        Resources  res = getResources();
        cParamCal.m_cellColor 			= res.getColor(R.color.calcellback);
        cParamCal.m_textColor 			= res.getColor(R.color.caldaytext);
        cParamCal.m_saturdayTextColor 	= res.getColor(R.color.calsaturdat);
        cParamCal.m_sundayTextColor 	= res.getColor(R.color.calsunday); 
        cParamCal.m_lineColor 			= res.getColor(R.color.calline);
        
//        cParam.m_topCellColor 		= res.getColor(R.color.calcellweek);
        cParamCal.m_topCellColor 		= res.getColor(R.color.calcellback);
        cParamCal.m_topTextColor 		= res.getColor(R.color.caldaytext);
        cParamCal.m_topSundayTextColor 	= res.getColor(R.color.caldaytext);
        cParamCal.m_topSaturdatTextColor = res.getColor(R.color.caldaytext);  
        
        /// 셋팅한 값으로 색상값 셋~
        cal.setColorParam( cParamCal ) ;
        /// 최상단은 높이를 35로 준다(전체높이중 한 셀의 높이 600/7한 값에서 35로 변경되니 달력의 총 높이가 줄어든다.)
//        cal.setTopCellSize( 5 ) ;
        cal.setCalendarSize( calWidth, calHeight ) ;
        cal.setTextSizePerScale(6, 6, 0, 0);
        cal.initCalendar3( ) ;

	}

	/*
	 * 하단에 달력view setting
	 */
	private void printCalendarView() {
		
        //날짜setting
		cal.changeCal(m_Calendar);
		cal.setWeekStyle( getResources( ).getColor(R.color.calcellselback), mDateList);
	    m_cellCalendarDate.setText(SmDateUtil.getDateSimpleFormat(this,  SmDateUtil.getDateFromCal(m_Calendar).substring(0, 6), ".", false));
	    
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		if ( hasFocus ) {
			calWidth 	= m_sectionLy[7].getWidth();
//			calHeight 	= m_cCalHeight;
			calHeight 	= m_sectionLy[7].getHeight();
			
			if ( cal == null ) {
				initCalendarView();
				printCalendarView();					
			}
		}
			
        return;
	}
	
    @Override 
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState); 
    	outState.putSerializable(ScheduleDbAdaper.KEY_STARTDATE, mFromDate);
    	outState.putSerializable(ScheduleDbAdaper.KEY_ENDDATE, mToDate);
    }
    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
    	
    }	    
    @Override    
    protected void onResume() {        
    	super.onResume(); 
    	setAllView();
    }        
    @Override    
    protected void onPause() {        
    	super.onPause();
    }
    @Override 
    protected void onDestroy() { 	
        super.onDestroy();
        RecycleUtil.recursiveRecycle(viewroot);
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
    	MenuInflater inflater = getMenuInflater(); 
    	
    	if ( mGubun != null && mGubun.equals("U")){
    		menu.setHeaderTitle( mName );    		
    		inflater.inflate(R.menu.specialday_ctx_menu, menu);
    	} else {	
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
        case R.id.menu_schedule_modify:
        	callScheduleManager(ComUtil.getStrResource(this, R.string.modify));
            return true; 
	    case R.id.menu_schedule_copy:
	    	callScheduleManager(ComUtil.getStrResource(this, R.string.copy));
	        return true; 
	    case R.id.menu_schedule_delete:
	    	callScheduleManager(ComUtil.getStrResource(this, R.string.delete));
	        return true; 
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        setAllView();
    }
    /*
     * 호출화면 : 스케줄리스트-tab
     */	
	private void callScheduleTabForDate( String sDate ) { 
		
		Bundle bundle = new Bundle();
		if ( sDate != null && !sDate.trim().equals("")) {
			bundle.putString(ScheduleDbAdaper.KEY_STARTDATE, sDate);
			bundle.putString(ScheduleDbAdaper.KEY_ENDDATE, sDate);
		}
        Intent mIntent = new Intent(this, ScheduleTabForDate.class);
        mIntent.putExtras(bundle);	
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	startActivityForResult(mIntent, ComConstant.ACTIVITY_SELECT);
	} 
 
	
	class myGsCalendar extends gsCalendar
	{

		Context mCtx ;
		LinearLayout mLv ;
		
		public myGsCalendar(Context context, LinearLayout layout) 
		{
			super(context, layout);
			this.mCtx 		= context;
			this.mLv 		= layout;
		}
		
		@Override
		public void myClickEvent(int yyyy, int MM, int dd) 
		{
			super.myClickEvent(yyyy, MM, dd);
			
			//해당날짜에 해당하는 주 출력 (주의 : MM 보여지는 내용과 일치함으로 calendar setting시에는 -1 해야함.. )
			m_Calendar.set(yyyy, MM - 1, dd);
			mFromDate 	= SmDateUtil.getDateFromCal(m_Calendar);
			mToDate 	= SmDateUtil.getDateFromCal(m_Calendar);
			
			setAllView( ) ;
		}
		
//		@Override
//		public void myLongClickEvent(int yyyy, int MM, int dd ) 
//		{
//			
//		}	
			
		
	}
		
}
