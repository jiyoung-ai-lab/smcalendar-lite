package com.waveapp.smcalendarlite;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.waveapp.smcalendarlite.handler.MenuHandler;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class InfoPopMenu extends PopView{
	   private final Context context;
	   private final LayoutInflater inflater;
	   private final View root;
	   private final TextView mLunarCal;
//	   private final TextView mNotice;
//	   private final LinearLayout mLunarLin;

	 
	   public InfoPopMenu(View anchor) {
	      super(anchor);  
	      context  = anchor.getContext();

		  ViewUtil.setLocaleFromPreference(context, context.getClass());
	      inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      root  = (ViewGroup) inflater.inflate(R.layout.info_menu_list, null);
	      setContentView(root);
	      //mTrack    = (ViewGroup) root.findViewById(R.id.viewRow); //팝업 View의 내용을 추가한 LinearLayout
//	      mLunarLin 	= (LinearLayout) root.findViewById(R.id.lin_menu); //음력달력 LinearLayout
	      mLunarCal    	= (TextView) root.findViewById(R.id.lunarcalendar); //음력달력 TextView
//	      mNotice    	= (TextView) root.findViewById(R.id.notice); 		//공지사항 TextView
		  
	      mLunarCal.setOnClickListener(new Button.OnClickListener( ) 
			{
				@Override
				public void onClick(View v)  {
					MenuHandler msgHd = new MenuHandler(context);
					msgHd.callLunarCalendar();
					window.dismiss();
				}
			
			} ) ;
//	      mNotice.setOnClickListener(new Button.OnClickListener( ) 
//			{
//				@Override
//				public void onClick(View v)  {
//					callNotice();
//					window.dismiss();
//				}
//			
//			} ) ;
	   }
	   
//	   private void callNotice() {
//			Intent intent = new Intent(this, Notice.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			startActivity(intent);
//	   }	
		
	   public void show () {
	      preShow(); //상속받은 PopView의 메서드
	      int[] location   = new int[2];
	      anchor.getLocationOnScreen(location);
	      root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	      root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	      window.showAtLocation(this.anchor, Gravity.CENTER, 0, 0); //가운데 정렬 하여 보임
	   }
	   
	   public void showAsDropDown (  ) {
		      preShow(); //상속받은 PopView의 메서드
		      int[] location   = new int[2];
		      anchor.getLocationOnScreen(location);
		      root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		      root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		      //window.showAtLocation(this.anchor, Gravity.RIGHT, XPos, YPos); //가운데 정렬 하여 보임
		      window.showAsDropDown(this.anchor);
	   }
	   public void showDropUp () {
		      preShow(); //상속받은 PopView의 메서드
		      int[] location   = new int[2];
		      anchor.getLocationOnScreen(location);
		      root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		      root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		      window.showAtLocation(this.anchor, Gravity.BOTTOM|Gravity.RIGHT, 30, 70);
		   }
	}

