package com.waveapp.smcalendarlite;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.database.LunarDataDbAdaper;
import com.waveapp.smcalendarlite.database.SpecialDayDbAdaper;
import com.waveapp.smcalendarlite.handler.ComMenuHandler;
import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.RecycleUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;
import com.waveapp.smcalendarlite.util.gsCalendar;
import com.waveapp.smcalendarlite.util.gsCalendar.gsCalendarColorParam;

public class LunarCalendarMain extends SMActivity implements OnClickListener
{
	
	TextView tvs[] ;
	TextView m_gabza ;
	TextView m_gangi ;
	TextView m_description ;
	String  mFirstLunarDate;
	
	ImageButton btns[] ;
	ViewFlipper mFlipper;
	LinearLayout lv;
	Drawable [] gangiMd;
	
    int mYear;
    int mMonth;
    int mDay;
    
    long mId;
    
	int m_nPreScrollPosX = 0;    //scroll 용
	int m_nPreScrollPosY = 0;    //scroll 용
	int m_nPreCellPosX   = 0;    //cell용
	int m_nPreCellPosY   = 0;    //cell용
	int m_nPreActionMove = 0;    //cell용
	
	class myGsCalendar extends gsCalendar
	{

		Context mCtx ;
		LinearLayout mLv ;
		ScrollView mScroll ;
		
		public myGsCalendar(Context context, LinearLayout layout, ScrollView scroll) 
		{
			super(context, layout, scroll);
			this.mCtx = context;
			this.mLv = layout;
			this.mScroll = scroll;
		}
		
		@Override
		public void myClickEvent(int yyyy, int MM, int dd) 
		{

			super.myClickEvent(yyyy, MM, dd);
			
			//스케줄정보 존재여부에 따라 화면 분기
			mYear 	= yyyy;
			mMonth 	= MM;
			mDay 	= dd; 
		}

		/*
		 * next/previous page 
		 *  return true -> stop
		 * @see com.waveapp.smcalendar.util.gsCalendar#myTouchEvent(android.view.View, android.view.MotionEvent)
		 */
		@Override
		public boolean myTouchEvent(View v, MotionEvent event) {
			

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
//		   		if (event.getAction() == MotionEvent.ACTION_MOVE)
//				{
//		   			//ACTION_MOVE 처리여부
//		   			if ( m_nPreCellPosX != 0 || m_nPreCellPosY != 0 ) {
//		   				m_nPreActionMove = 1;
//
//		   			}
//				}				
		   		if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
		   			
//		   			if ( m_nPreActionMove > 0 ) {
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
//					}		   				
		   		}

			}

			return false;
		}
		
	    private void MoveNextView()
	    {
	    	//기능제한여부
	        if ( !ComUtil.isFormallVer(mCtx) ) {
	        	return;
	        }	    	
	    	cal.nextMonth();
			viewRefresh();
			ViewUtil.nextFlipper(mCtx, mFlipper);
	    }
	    
	    private void MovewPreviousView()
	    {	  
	    	//기능제한여부
	        if ( !ComUtil.isFormallVer(mCtx) ) {
	        	return;
	        }
	    	cal.preMonth();
			viewRefresh();
			ViewUtil.previousFlipper(mCtx, mFlipper);
	    }			
		
	}
	
	
	myGsCalendar cal ;

	SpecialDayDbAdaper 				mSpecialDbHelper;
	ArrayList<SpecialDayInfo> 		mSpecialList;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
        setContentView(R.layout.lunarcalendar_main);
        
        Resources  res = getResources();
		
        /// 달력을 띄울 대상 레이아웃
        lv = (LinearLayout)findViewById( R.id.calendar_lLayout ) ;
        ScrollView scroll = (ScrollView)findViewById( R.id.sc_cal ) ;
        
        //view flipper
        mFlipper = (ViewFlipper)findViewById(R.id.vf_cal);
        
        /// 년 월 일 표시할 텍스트뷰
        tvs = new TextView[3] ;
        tvs[0] = (TextView)findViewById( R.id.year ) ;
        tvs[1] = (TextView)findViewById( R.id.month ) ;
        tvs[2] = null ; /// 일은 표시하지 않음
        
        /// 누르면 년 월 일 조절할 버튼
        btns = new ImageButton[4] ;
        btns[0] = (ImageButton)findViewById( R.id.previousyear ) ;
        btns[1] = (ImageButton)findViewById( R.id.nextyear ) ;
        btns[2] = (ImageButton)findViewById( R.id.previousmonth ) ;
        btns[3] = (ImageButton)findViewById( R.id.nextmonth ) ;

        //60갑자 정보 보여주는 항목
        m_gabza = (TextView)findViewById( R.id.gabza_tv ) ;
        m_gangi = (TextView)findViewById( R.id.gangi_tv ) ;
        m_description = (TextView)findViewById( R.id.description ) ;
        
        /// 달력객체 생성
        cal = new myGsCalendar( this, lv , scroll ) ;
        
        /// 색상 설정할 객체 생성
        gsCalendarColorParam cParam = new gsCalendarColorParam( ) ;

        cParam.m_cellColor 			= res.getColor(R.color.calcellback);
        cParam.m_textColor 			= res.getColor(R.color.caldaytext);
        cParam.m_saturdayTextColor 	= res.getColor(R.color.calsaturdat);
        cParam.m_sundayTextColor 	= res.getColor(R.color.calsunday); 
        cParam.m_lineColor 			= res.getColor(R.color.calline);
        
        cParam.m_topCellColor 		= res.getColor(R.color.calcellweek);
        cParam.m_topTextColor 		= res.getColor(R.color.caldaytext);
        cParam.m_topSundayTextColor 	= res.getColor(R.color.caldaytext);
        cParam.m_topSaturdatTextColor 	= res.getColor(R.color.caldaytext); 
        
        /// 셋팅한 값으로 색상값 셋~
        cal.setColorParam( cParam ) ;
        
        //60갑자 한자 설명(한글/영문으로 구분)  : 일본어는 지원안함
        if ( ComConstant.LOCALE.equals(ComConstant.LOCALE_KO)) {
        	m_description.setText(getHanzaDescription());  
        } else if ( ComConstant.LOCALE.equals(ComConstant.LOCALE_ZH)) {
        	m_description.setText(getHanzaDescriptionZh());   
        } else {
        	m_description.setText(getHanzaDescriptionEn());  
        }
                
        //60갑자 이미지 가져오기
    	setGanGiResource();

        //화면방향체크
        Configuration conf =  getResources().getConfiguration();
        if ( Configuration.ORIENTATION_LANDSCAPE == conf.orientation ) {
        	//가로
            //공통 top menu setting        	
            ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.lunarcalendar, 0 ), View.INVISIBLE);
        	
        } else {
        	//세로
            //공통 top menu setting
            ComMenuHandler.callComTopMenuAction(this, ComUtil.setActionTitle(this, R.string.lunarcalendar, 0 ), View.VISIBLE);
        	
        }    
       
        //달력사이즈 setting ( 메뉴와 날짜바 적용후 보정처리)
        LinearLayout lin_date = (LinearLayout)findViewById( R.id.lin_date ) ;
        LinearLayout.LayoutParams dateParam = (LayoutParams) lin_date.getLayoutParams();		
        cal.setCalendarSizeMain( conf.orientation, ( ComMenuHandler.getComMenuHeight( this ) + dateParam.height + 40 )) ;
        
        cal.setTextSizePerScale(9, 7, 6, 6, 6);
        
        /// 누르면 반응할 버튼들 셋팅
        cal.setControl( btns ) ;
        
        /// 년 월 일을 띄울 텍스트뷰 셋팅
        cal.setViewTarget( tvs ) ;
        
        //방향전환시 달력초기화 방지를 위한 코딩... 아 정말 어디까지 해야하나.... -.-
        if (savedInstanceState != null) {
        	String calyear 	= savedInstanceState.getString(ComConstant.CAL_YEAR);
        	String calmonth = savedInstanceState.getString(ComConstant.CAL_MONTH);
        	String calday = savedInstanceState.getString(ComConstant.CAL_DAY);
        	if ( calyear != null && calmonth != null && !calyear.trim().equals("") && !calmonth.trim().equals(""))
        		cal.changeLandscape(calyear, calmonth, calday);
        }
        
        cal.initCalendar();
        
        /// 오늘날짜까지 색깔변경
        cal.setTodayStyle(  res.getColor(R.color.calcellselback)) ;
       
        btns[0].setOnClickListener(this); 
        btns[1].setOnClickListener(this); 
        btns[2].setOnClickListener(this); 
        btns[3].setOnClickListener(this); 
        
    	//날짜bar layout 변경
        setDateBar();
 
    }

    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
   //언어값에 따라 날짜bar 구성 변경
    protected void setDateBar() {
        
        TextView year_tv 	= (TextView)findViewById( R.id.year_tv ) ;
        TextView month_tv 	= (TextView)findViewById( R.id.month_tv ) ;   
        TextView gap 		= (TextView)findViewById( R.id.gap ) ; 
                
        if ( ComUtil.isLanguageFront(this))  {        	
        	ViewUtil.addViewLayoutParam(year_tv, RelativeLayout.RIGHT_OF, tvs[0]);
        	ViewUtil.addViewLayoutParam(gap, RelativeLayout.RIGHT_OF, year_tv);
        	ViewUtil.addViewLayoutParam(tvs[1], RelativeLayout.RIGHT_OF, gap);
        	ViewUtil.addViewLayoutParam(month_tv, RelativeLayout.RIGHT_OF, tvs[1]);
        	
        	year_tv.setText(ComUtil.getStrResource(this, R.string.year));
        	month_tv.setText(ComUtil.getStrResource(this, R.string.month));
        } else {
        	ViewUtil.addViewLayoutParam(month_tv, RelativeLayout.RIGHT_OF, tvs[1]);
        	ViewUtil.addViewLayoutParam(gap, RelativeLayout.RIGHT_OF, month_tv);
        	ViewUtil.addViewLayoutParam(tvs[0], RelativeLayout.RIGHT_OF, gap);
        	ViewUtil.addViewLayoutParam(year_tv, RelativeLayout.RIGHT_OF, tvs[0]);
        	
        	year_tv.setText("");
        	month_tv.setText(",");
        }   
            	
    }
	protected void viewRefresh() { 
		//drag 좌표정보 초기화
		m_nPreScrollPosX = 0;
		m_nPreScrollPosY = 0;
		m_nPreCellPosX = 0;
		m_nPreCellPosY = 0;
		m_nPreActionMove = 0;
		
		cal.setTodayStyle(  getResources().getColor(R.color.calcellselback)) ;

    	setLunarData();

    }

    @Override
	public void onClick(View v) {
    	//기능제한여부
        if ( !ComUtil.isFormallVer(this) ) {
        	return;
        }
		if ( v == btns[0] ) {
			
			cal.preYear();
			
		} else if ( v == btns[1] ) {
			
			cal.nextYear();
			
		} else if ( v == btns[2] ) {
			
			cal.preMonth();
			
		} else if ( v == btns[3] ) {
			
			cal.nextMonth();
		}
		
		if ( v == btns[0] || v == btns[1] || v == btns[2] || v == btns[3] ) {
			viewRefresh();			
		}
		
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //viewRefresh();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {        
    	super.onSaveInstanceState(outState);

    	
    	//화면 전환시 값 reset 방지용
    	String year = ComUtil.fillSpaceToZero(cal.getThisYear(), 4);
    	String month = ComUtil.fillSpaceToZero(cal.getThisMonth(), 2);
    	String day = ComUtil.fillSpaceToZero(cal.getThisDay(), 2);
		if ( year != null && !year.trim().equals("")) {
			outState.putSerializable(ComConstant.CAL_YEAR, 	year);
			outState.putSerializable(ComConstant.CAL_MONTH, month);
			outState.putSerializable(ComConstant.CAL_DAY, 	day);
		}

    }	    
    @Override 
    protected void onRestoreInstanceState(Bundle outState){
    	super.onRestoreInstanceState(outState);
    }	    
    @Override    
    protected void onResume() {        
    	super.onResume(); 
        
        //스케줄 및 휴일정보 
        viewRefresh();
    }        
    @Override    
    protected void onPause() {
    	super.onPause();
    }
    @Override 
    protected void onDestroy() { 
        super.onDestroy();  
        RecycleUtil.recursiveRecycle(lv);
        if ( mFlipper != null ) mFlipper = null;
        if ( gangiMd  != null) gangiMd = null;
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
    /*
     * 스케줄 정보 가져오기 
     */
	public void setLunarData( )
    {
		
		//1. Db에서 음력 정보가져오기 ( LunarDataInfo 객체로 return) 
		//-. 달력기준 년월일 기간에 따른 db정보 가져오기 ( 달력이다보니 시작종료일이 월초부터 말까지로 처리되는 ..)
		
		Calendar iCal 	= cal.getCalendar();
		
		String sYear 	= ComUtil.fillSpaceToZero(Integer.toString( iCal.get(Calendar.YEAR)), 4);
		String sMonth 	= ComUtil.fillSpaceToZero(Integer.toString( iCal.get(Calendar.MONTH) + 1), 2);
		String sSDate	= "01";
		String sEDate	= ComUtil.fillSpaceToZero(Integer.toString(cal.getLastDate()), 2);
		
		//달력의 경우 월단위로 출력되기 때문에 시작일 기준으로 조회
		String fromDay 	= sYear + sMonth + sSDate;
		String endDay 	= sYear + sMonth + sEDate;
		
		getLunarFromDB(iCal, fromDay, endDay);	
		
		//해당년도 60갑자정보 가져오기
		if ( mFirstLunarDate != null && !mFirstLunarDate.equals("")) {
			LunarDataDbAdaper mDbLunar = new LunarDataDbAdaper(this);
			mDbLunar.open();
			
			Cursor cur = mDbLunar.fetchSixtyGapSearch(mFirstLunarDate);
			
			if ( cur != null) {
				String sixtyyear = cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_SIXTYYEAR));
				String sixtymonth = cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_SIXTYMONTH));
				//띠 이미지
				m_gangi.setBackgroundDrawable(getGanGiImg(sixtyyear));
				//60갑자 text
				String gabza = getGabzaHanChar(sixtyyear) + 
							ComUtil.getStrResource(this, R.string.year)+ "\n" + 
							getGabzaHanChar(sixtymonth) +
							ComUtil.getStrResource(this, R.string.month);
				m_gabza.setText(gabza);
				
			}
			if ( cur != null ) cur.close();
			mDbLunar.close();
    	}
	}	


	/*
	 * lunardata(year, month fix)
	 */
    public void getLunarFromDB( Calendar iCal, String fromdate, String todate ) {
    	
    	mFirstLunarDate = "";
    	
    	//해상도에 따라 스케줄 최대수 변경
        int cellSchMaxCnt = cal.getScheduleMaxCnt();
    	int calLen =  cal.getCellDateSize();
		
		if (fromdate != null && todate != null) {
			//Data select 
			LunarDataDbAdaper mDbLunar = new LunarDataDbAdaper(this);
			mDbLunar.open();
			Cursor cur = mDbLunar.fetchSolarToLunarPeriod(fromdate, todate);
			mDbLunar.close();
			
			//loop
			int cnt = cur.getCount();
			
			//스케줄항목에 음력일자 setting
			cal.removeLunarView( calLen );
			cal.reCreateHolidayText( calLen );
//			cal.setScheduleLinearLayoutFL( calLen );

			cal.setScheduleLinearLayout( calLen, cellSchMaxCnt );
			
			for(int i = 0 ; i < cnt ; i++) {			
				
				String leap 	= cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_LEAP));				
				String solar 	= cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_SOLAR));
				String lunar 	= cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_LUNAR));
				String sixtyday	= cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_SIXTYDAY));
				String solarterm= cur.getString(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_SOLARTERMS));
				
				String sMonthDate 	=  solar.substring( 4 );
				for( int j = 0 ; j < calLen ; j++ ) {
					String sCalMonthDay = cal.getCellDateTextFromArray(j);
					//양력일자와 일치하는 칸에 데이터 setting
					if ( sCalMonthDay != null && !sCalMonthDay.equals("")) {	
						if ( sMonthDate != null && sMonthDate.equals(sCalMonthDay)) {
							//절기(우선 한국만)
							if ( ComConstant.LOCALE.equals(ComConstant.LOCALE_KO)) {
								if ( solarterm != null && !solarterm.trim().equals("")) {
									cal.setCellHolidayText( j , solarterm );
								}
							}
							cal.setLunarView( j );
							Drawable dw = getGanGiImg(sixtyday);
							cal.setCellLunarBackground( j, dw );
							
							//음력일자 setting
							int lunarmonth	= SmDateUtil.getDateToInt(lunar, ComConstant.GUBUN_MONTH);
							int lunarday	= SmDateUtil.getDateToInt(lunar, ComConstant.GUBUN_DAY);
							
							//년월 간지조회조건(해당월 음력 1일)
							if ( lunarday  == 1) {
								mFirstLunarDate = solar; 
							}
							StringBuffer buffer = new StringBuffer();
							if ( leap != null && leap.equals("2")) {
								buffer.append(ComUtil.getStrResource(this, R.string.yun));
								buffer.append("\n");
							}
							buffer.append(Integer.toString(lunarmonth));
							buffer.append(".");
							buffer.append(Integer.toString(lunarday));
													
							String sixtydayhan = getGabzaHanChar(sixtyday);
//							cal.setScheduleTextLayoutFL( j, getGanGiImg(sixtyday), sixtydayhan, buffer.toString() );
							
							cal.setScheduleTextLayoutForLunar( j, 2 , cellSchMaxCnt );
							cal.setCellScheduleText( j , 2 , buffer.toString()  ) ;		
							cal.setScheduleTextLayoutForLunar( j, 1 , cellSchMaxCnt );
							cal.setCellScheduleText( j , 1 , sixtydayhan ) ;						
							break;
						}
					}
				}
				cur.moveToNext();
			}			
			if ( cur != null ) cur.close();
		}	

	}
    /*
     * 12간지 이미지리소스 set
     */
    private Drawable [ ] setGanGiResource (  ) {
    	
    	int imgLen = ComConstant.GANGI.length;
    	
    	gangiMd = new Drawable[imgLen]; 
		
		for( int i = 0 ; i < imgLen ; i++ ) {
			switch (i) {
				case 0:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_mouse );	
					break;
				case 1:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_bull );	
					break;
				case 2:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_tiger );	
					break;
				case 3:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_rabbit );	
					break;
				case 4:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_dragon );	
					break;
				case 5:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_snake );	
					break;
				case 6:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_horse );	
					break;
				case 7:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_sheep );	
					break;
				case 8:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_monkey );	
					break;
				case 9:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_chicken );	
					break;
				case 10:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_dog );	
					break;
				case 11:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_pig );	
					break;				
				default:
					gangiMd[i] = getResources( ).getDrawable( R.drawable.sm_gangi_mouse );	
					break;
			}
		}

		return gangiMd;
    	
    }
    /*
     * 12간지 이미지 가져오기
     * parm : 60갑자
     */
    private Drawable getGanGiImg ( String gangichar ) {

    	if ( gangichar != null && gangichar.trim().length() == 2 ) {
    		
    		String gangi = gangichar.substring(1);
    		
    		int len = ComConstant.GANGI.length;
    		
    		for( int i = 0 ; i < len ; i++ ) {    			
    			if ( gangi.equals(ComConstant.GANGI[i].toString())){
    				return gangiMd[i];
    			}
    		}
    	}
		return null;
    	
    }    
    /*
     * 60갑자 한자전환
     * parm : 60갑자
     */
    private String getGabzaHanChar ( String gangichar ) {
    	
    	StringBuffer buffer = new StringBuffer();
    	
    	if ( gangichar != null && gangichar.trim().length() == 2 ) {
    		
    		String gan 	= gangichar.substring(0,1);
    		String gangi = gangichar.substring(1);
    		
    		int ganlen = ComConstant.GAN.length;
    		
    		for( int i = 0 ; i < ganlen ; i++ ) {    			
    			if ( gan.equals(ComConstant.GAN[i].toString())){
    				buffer.append(ComConstant.GANHAN[i].toString());
    				break;
    			}
    		}   
    		
    		int gangilen = ComConstant.GANGI.length;
    		
    		for( int i = 0 ; i < gangilen ; i++ ) {    			
    			if ( gangi.equals(ComConstant.GANGI[i].toString())){
    				buffer.append(ComConstant.GANGIHAN[i].toString());
    				break;
    			}
    		}
    	}
		return buffer.toString();
    	
    } 
    /*
     * 60갑자 한자 설명 (한글)
     * parm : 60갑자
     */
    private String getHanzaDescription ( ) {
    	
    	StringBuffer buffer = new StringBuffer();
    	
    	buffer.append("*띠기준 : 음력 1일  \n");
    	
    	buffer.append("10간   : ");
    	
		int ganlen = ComConstant.GAN.length;
		
		for( int i = 0 ; i < ganlen ; i++ ) { 
			buffer.append(ComConstant.GAN[i].toString());
			buffer.append("(");			
			buffer.append(ComConstant.GANHAN[i].toString());
			buffer.append(")");
		}   
		buffer.append("\n");
		
		buffer.append("12지: ");
		int gangilen = ComConstant.GANGI.length;
		
		for( int i = 0 ; i < gangilen ; i++ ) {    			
			buffer.append(ComConstant.GANGI[i].toString());
			buffer.append("(");			
			buffer.append(ComConstant.GANGIHAN[i].toString());
			buffer.append(")");
		}
		return buffer.toString();
    	
    }
    /*
     * 60갑자 한자 설명 (영문)
     * parm : 60갑자
     */
    private String getHanzaDescriptionEn ( ) {
    	
    	StringBuffer buffer = new StringBuffer();
    	
    	buffer.append("*Standard: Based on the 1st of the lunar\n");
		
		buffer.append("12Gangi: ");
		int gangilen = ComConstant.GANGIEn.length;
		
		for( int i = 0 ; i < gangilen ; i++ ) {    			
			buffer.append(ComConstant.GANGIEn[i].toString());
			buffer.append("(");			
			buffer.append(ComConstant.GANGIHAN[i].toString());
			buffer.append(")");
			if ( i == 5 ) buffer.append("\n              ");
		}
		return buffer.toString();    	
    }
    /*
     * 60갑자 한자 설명 (중국번체)
     * parm : 60갑자
     */
    private String getHanzaDescriptionZh ( ) {
    	
    	StringBuffer buffer = new StringBuffer();
    	
    	buffer.append("*基準 : 陰曆 一日  \n");
    	
    	buffer.append("天干   : ");
    	
		int ganlen = ComConstant.GAN.length;
		
		for( int i = 0 ; i < ganlen ; i++ ) { 
			buffer.append(ComConstant.GANHAN[i].toString());
//			buffer.append("(");			
//			buffer.append(ComConstant.GANHAN[i].toString());
//			buffer.append(")");
		}   
		buffer.append("\n");
		
		buffer.append("地支: ");
		int gangilen = ComConstant.GANGI.length;
		
		for( int i = 0 ; i < gangilen ; i++ ) {    			
			buffer.append(ComConstant.GANGIHAN[i].toString());
//			buffer.append("(");			
//			buffer.append(ComConstant.GANGIHAN[i].toString());
//			buffer.append(")");
		}
		return buffer.toString();
    	
    }    
}