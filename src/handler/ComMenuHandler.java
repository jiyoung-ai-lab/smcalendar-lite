package com.waveapp.smcalendarlite.handler;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.waveapp.smcalendarlite.InfoPopMenu;
import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;

public class ComMenuHandler  extends SMActivity {

	private static int comHeight = 0;
	
	public static void callComTopMenuAction( final Context ctx, String actiontitle, int isvisible ) { 

		/*
		 * Title section
		 */

//		ImageButton comMenuHandler 	= (ImageButton)((Activity) ctx).findViewById( R.id.com_menuhandle ) ;
		TextView comAction 			= (TextView)((Activity) ctx).findViewById( R.id.com_action ) ;
		
		//main title (activity name setting)
		if ( comAction != null ) {
			comAction.setText(actiontitle);
		}
//		//menu 보이는 화면의 경우 옵션처리
//		if ( comMenuHandler != null  &&  isvisible == View.VISIBLE  ) {
//			comMenuHandler.setOnClickListener(new Button.OnClickListener( ) 
//			{
//				@Override
//				public void onClick(View v) 
//				{
//					if ( ComConstant.TOPVISABLE == View.VISIBLE ) {
//						ComConstant.TOPVISABLE = View.GONE;
//						
//					} else {
//						ComConstant.TOPVISABLE = View.VISIBLE;
//					}
//					
//					setTopMenuView( ctx );
//				}
//			
//			}); 	
//		}

//		//slide drawer
//		SlidingDrawer comSlide = (SlidingDrawer)((Activity) ctx).findViewById( R.id.sd_topmenu ) ;	
//		if ( comSlide != null ) {
//			comSlide.setOnDrawerOpenListener(new OnDrawerOpenListener( ) 
//			{
//	
//				@Override
//				public void onDrawerOpened() {
//					// TODO Auto-generated method stub
//					
//				}
//			
//			});  
//		}
		
		//스타일 변경 
		//int txtColor = ctx.getResources().getColor(Color.WHITE);
		
		/*
		//설정
		LinearLayout comPref = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin_preference ) ;
		if ( comPref != null ) {
			comPref.setOnClickListener(new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{
					MenuHandler msgHd = new MenuHandler(ctx);				
					msgHd.callPreference();
				}
			
			});
		}	
		*/	
		/*
		 * Menu section
		 */
		//기념일리스트
		LinearLayout comSpecialday = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin_specialday ) ;
		if ( comSpecialday != null ) {
			comSpecialday.setOnClickListener(new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{
					MenuHandler msgHd = new MenuHandler(ctx);
					msgHd.callSpecialdayList(); 	
				}
			
			});  
		}
		//사용자
		LinearLayout comUser = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin_user ) ;
		if ( comUser != null ) {
			comUser.setOnClickListener(new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{
					MenuHandler msgHd = new MenuHandler(ctx);
					msgHd.callUserList();	
				}
			
			});  
		}

		//할일		
		LinearLayout comTodo = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin_todo ) ;
		if ( comTodo != null ) {
			comTodo.setOnClickListener(new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{
					MenuHandler msgHd = new MenuHandler(ctx);
					msgHd.callTodoList();	
				}
			
			}); 
		}		
		//달력
		LinearLayout comCalendar = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin_calendar) ;
		if ( comCalendar != null ) {
			comCalendar.setOnClickListener(new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v) 
				{
					MenuHandler msgHd = new MenuHandler(ctx);
					msgHd.callCalendar();
				}
			
			}); 	
		}
		//정보 (sub menu) : Lite 기능제한항목
		LinearLayout comInfo = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin_info ) ;
		if ( comInfo != null ) {
			comInfo.setOnTouchListener(new LinearLayout.OnTouchListener( ) {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
				   	InfoPopMenu pop = new InfoPopMenu(v);
					pop.showAsDropDown();
			
					return false;
				}
				
			});
//			comInfo.setOnClickListener(new Button.OnClickListener( ) 
//			{
//				@Override
//				public void onClick(View v) 
//				{
////			    	Intent i = new Intent(ctx, InfoPopMenu.class);
////			    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
////			    	ctx.startActivity(i);
//					comInfo.
//					
//				}
//			
//			});
		}		
		
//		setTopMenuView(ctx, isvisible);
		
		//화면제어(등록화면이나 설정화면에서는 메뉴버튼 항목 안보이게
		LinearLayout comLin = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin ) ;
		if ( comLin != null ) {
			LinearLayout.LayoutParams parmas = (LayoutParams) comLin.getLayoutParams();
			comHeight = parmas.height;
			
			if ( isvisible != View.VISIBLE ) {
				comLin.removeAllViews();
				comLin.setBackgroundDrawable(null);
				comLin.setLayoutParams(new LinearLayout.LayoutParams(0,0));
				comLin.setVisibility(isvisible);
			}
		}

	}  
	
	public static int getComMenuHeight ( Context ctx ) {
//		ComUtil.showToast(ctx, Integer.toString(comHeight));
		return comHeight;
	}
	
//	private static void setTopMenuView( Context ctx ) {
//		
//		//화면제어(등록화면이나 설정화면에서는 메뉴버튼 항목 안보이게
//		LinearLayout comLin = (LinearLayout)((Activity) ctx).findViewById( R.id.com_lin ) ;
//		if ( comLin != null ) {
//			comLin.setVisibility(ComConstant.TOPVISABLE );			
//		}
//	}
	

//    private void setToggleBackGround ( LinearLayout lin ) {
//    	
//    	int len = 6;
//    	
//    	for(int i = 0 ; i < len ; i++) {
//    		if ( i == selectbtn) {    			
//    			btns[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.sm_option_click));
//    		} else {
//    			btns[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.sm_option_click2));
//    		}
//    	}
//    }
}
