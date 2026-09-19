
package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 다른달력 정보 
 * 스케줄을 보여줄 때 사용
 * 2011.06.02 viewholder 추가
 */
public class OtherCalendarListAdapter extends ArrayAdapter<ScheduleInfo> {
    
	private Context mCtx;
	private int mResource;
	private ArrayList<ScheduleInfo> mList;
	private LayoutInflater mInflater;
	private CheckBox childCheckBox[]; // child layout cache 
	private int mCheckCnt;
	
    class ViewHolder {
    	
    	LinearLayout mUserColor;
    	CheckBox mChoice;
        TextView mScheduleName;
		TextView mDate;
		TextView mTime;
		TextView mAllDayYn;

        ImageView mCycle;
		ImageView mAlarm;
		
		LinearLayout mDayOfWeek;
		TextView[] mDayOfWeekTxt;
		//일정 추가정보들
		TextView mDescription;
		
    }
    
	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public OtherCalendarListAdapter(Context context, int layoutResource, ArrayList<ScheduleInfo> objects)
	{
		super(context, layoutResource, objects);
		this.mCtx 		= context;
		this.mResource 	= layoutResource;
		this.mList 		= objects;
		this.mInflater 	= (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * row data set
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		ScheduleInfo schedule = mList.get(position);

		if(schedule == null) return null;
		
		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
			
			holder = new ViewHolder();
			holder.mChoice			= (CheckBox) convertView.findViewById(R.id.choice);
			holder.mUserColor		= (LinearLayout) convertView.findViewById(R.id.usercolor);
			holder.mScheduleName	= (TextView) convertView.findViewById(R.id.schedulename);
			holder.mCycle 			= (ImageView) convertView.findViewById(R.id.cycle);
			holder.mDate 			= (TextView) convertView.findViewById(R.id.date);
			holder.mTime 			= (TextView) convertView.findViewById(R.id.time);			
			holder.mAllDayYn		= (TextView) convertView.findViewById(R.id.alldayyn);
			holder.mAlarm			= (ImageView) convertView.findViewById(R.id.alarm);

			holder.mDescription		= (TextView) convertView.findViewById(R.id.description);
			
			holder.mDayOfWeek 		= (LinearLayout) convertView.findViewById(R.id.lin_dayofweek);
			holder.mDayOfWeekTxt	= new TextView[7];
			
			
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}

        //Check Box 제어Checked()
        setCheckBoxView( convertView, position );
        holder.mChoice.setTag(schedule);
        holder.mChoice.setChecked(schedule.isChoice());
        		
		//값 setting
        String name = schedule.getScheduleName();
		if ( name == null || ( name != null && name.trim().equals(""))) {
			schedule.setScheduleName(ComUtil.getStrResource(mCtx, R.string.isempty));  
		}
		holder.mScheduleName.setText(schedule.getScheduleName());      
        
		if ( schedule.getCangetyn() != null && !schedule.getCangetyn().equals("")) {
			holder.mScheduleName.setPaintFlags(holder.mScheduleName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Text Middle Line
			holder.mScheduleName.setTextColor(mCtx.getResources().getColor(R.color.darkgray));
			holder.mChoice.setClickable(false);	
			holder.mChoice.setEnabled(false);	
		} else {
			holder.mScheduleName.setPaintFlags(0); // Text Middle Lin
			holder.mScheduleName.setPaintFlags(holder.mScheduleName.getPaintFlags() | Paint.ANTI_ALIAS_FLAG);
			holder.mScheduleName.setTextColor(mCtx.getResources().getColor(R.color.black));
			holder.mChoice.setClickable(true);
			holder.mChoice.setEnabled(true);	
		
		}

		holder.mCycle.setVisibility(View.VISIBLE);
		holder.mAlarm.setVisibility(View.VISIBLE);			 
		holder.mAllDayYn.setVisibility(View.VISIBLE);
		 
		holder.mDate.setText(SmDateUtil.getDateFullFormat(mCtx, schedule.getStartDate(),false, false)
					+ "~" + SmDateUtil.getDateFullFormat(mCtx, schedule.getEndDate(),false, false));
		 
		if ( schedule.getAllDayYn() != null && !schedule.getAllDayYn().trim().equals("")) {
			 holder.mTime.setText(ComUtil.setYesReturnValue(schedule.getAllDayYn(), ComUtil.getStrResource(mCtx, R.string.allday)));
		} else {
			 holder.mTime.setText(SmDateUtil.getTimeFullFormat(mCtx, schedule.getStartTime())
						+ "~" + SmDateUtil.getTimeFullFormat(mCtx, schedule.getEndTime()));
		}

		if ( schedule.getCycle() != null && !schedule.getCycle().trim().equals("") ) {
			 holder.mCycle.setVisibility(View.VISIBLE);
		} else {
			 holder.mCycle.setVisibility(View.GONE);
		}
		if ( schedule.getAlarmYn() != null && !schedule.getAlarmYn().trim().equals("") ) {
			 holder.mAlarm.setVisibility(View.VISIBLE);
		} else {
			 holder.mAlarm.setVisibility(View.GONE);
		}

		holder.mDescription.setText(schedule.getDescription());
		
        return convertView;  
	}
	
    public int getCheckCount() {
        return mCheckCnt;
    }   
    	
    public void setCheckBoxView( View childView, int position ) {

    	//데이터 없는 경우 skip
		if ( getCount() == 0 ) return ;
		
		//건수만큼 배열 생성, 단 최초호출시 1번만		
		if  ( childCheckBox == null ) {
			mCheckCnt = 0;
			int glen = getCount();
			childCheckBox = new CheckBox[glen];
		}
 
		//listener생성, 단 1번만
		childCheckBox[position] = (CheckBox) childView.findViewById(R.id.choice);
		childCheckBox[position].setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
			
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						ScheduleInfo sch = (ScheduleInfo)buttonView.getTag();
						sch.setChoice(isChecked);
						if ( isChecked ) {
							mCheckCnt++;
						} else {
							mCheckCnt--;
						}
					}
				});	
		
        return ;
    }  
    public void setCheckFromListClick( int position ) {
 
    	if ( childCheckBox[position].isClickable() == true ) {
        	if ( childCheckBox[position].isChecked()) {
        		childCheckBox[position].setChecked(false);
        	} else {
        		childCheckBox[position].setChecked(true);
        	}   		
    	}

  
        return ;
    }  
//    public void setCheckValue( int position, boolean check ) {
//    	 
////    	if ( childCheckBox[position].isClickable() == true ){
//    		childCheckBox[position].setChecked(check);	
////    	}
//    	
//  
//        return ;
//    } 
}
