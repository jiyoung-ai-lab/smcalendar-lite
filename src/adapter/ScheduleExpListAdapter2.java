
package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/*
 * 스케줄리스트 출력시 날짜 단위  (parent : date)
 * 스케줄을 보여줄 때 사용
 * 2011.06.02 viewholder 추가
 */
public class ScheduleExpListAdapter2 extends BaseExpandableListAdapter {

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context mCtx;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<ScheduleInfo>> children;
    
    class ViewHolder {
    	
    	LinearLayout mUserColor;
        TextView mScheduleName;
		TextView mDate;
		TextView mTime;
		TextView mAllDayYn;

        ImageView mCycle;
		ImageView mAlarm;
		
		LinearLayout mDayOfWeek;
		TextView[] mDayOfWeekTxt;
		
		TextView mTel;
		TextView mMemo;
		
    }
    public ScheduleExpListAdapter2(Context context, ArrayList<String> groups,
            ArrayList<ArrayList<ScheduleInfo>> children) {
    	
        this.mCtx 		= context;
        this.groups 	= groups;
        this.children 	= children;
    }

    /**
     * A general add method, that allows you to add a Vehicle to this list
     * 
     * Depending on if the category opf the vehicle is present or not,
     * the corresponding item will either be added to an existing group if it 
     * exists, else the group will be created and then the item will be added
     * @param vehicle
     */
    public void addItem(ScheduleInfo childSch) {
    	//일자기준
    	String schduledate = childSch.getScheduleDate();
    	
        if (!groups.contains(schduledate)) {
            groups.add(schduledate);
        }
      //데이터 없을경우 title만
    	if ( childSch.getId() == (long) 0 ) {
    		/// child add 없음
    	}else{
    	
	        int index = groups.indexOf(schduledate);
	        if (children.size() < index + 1) {
	            children.add(new ArrayList<ScheduleInfo>());
	        }
	        children.get(index).add(childSch);
    	}
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    // Return a child view. You can load your custom layout here.
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
    	
    	ViewHolder holder;
    	
    	ScheduleInfo schedule = (ScheduleInfo) getChild(groupPosition, childPosition);
    	
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.schedule_datelist_child, null);
            
			holder = new ViewHolder();
			holder.mUserColor		= (LinearLayout) convertView.findViewById(R.id.usercolor);
			holder.mScheduleName	= (TextView) convertView.findViewById(R.id.schedulename);
			holder.mCycle 			= (ImageView) convertView.findViewById(R.id.cycle);
			holder.mDate 			= (TextView) convertView.findViewById(R.id.date);
			holder.mTime 			= (TextView) convertView.findViewById(R.id.time);			
			holder.mAllDayYn		= (TextView) convertView.findViewById(R.id.alldayyn);
			holder.mAlarm			= (ImageView) convertView.findViewById(R.id.alarm);
			
			holder.mDayOfWeek 		= (LinearLayout) convertView.findViewById(R.id.lin_dayofweek);
			holder.mDayOfWeekTxt	= new TextView[7];
			
			holder.mTel				= (TextView) convertView.findViewById(R.id.tel);
			holder.mMemo			= (TextView) convertView.findViewById(R.id.memo);
		
			convertView.setTag(holder);
         
        } else {
			
			holder = (ViewHolder) convertView.getTag();
		}

		//값 setting
        String name = schedule.getScheduleName();
		if ( name != null && !name.trim().equals("")) {
			holder.mScheduleName.setText(name);   
		} else {
			holder.mScheduleName.setText(schedule.getSubName());   
		}
             
        
		String gubun = schedule.getScheduleGubun();
		if ( gubun != null && ( gubun.equals("B") || gubun.equals("U"))) {
			 holder.mDayOfWeek.setVisibility(View.GONE);	 
			 holder.mAllDayYn.setVisibility(View.GONE);
			 holder.mTel.setVisibility(View.GONE);
			 holder.mMemo.setVisibility(View.GONE);
			 
			 if ( schedule.getHolidayYn() != null && schedule.getHolidayYn().trim().equals("Y")) {
				 holder.mScheduleName.setTextColor( mCtx.getResources().getColor(R.color.calsunday));
			 } else {
				 holder.mScheduleName.setTextColor( mCtx.getResources().getColor(R.color.black));
			 }
			 holder.mUserColor.setBackgroundColor(mCtx.getResources().getColor(R.color.gray));
			 
			//음력인 경우 입력된 음력일자
			 holder.mDate.setText(schedule.getDayOfWeekFullText());	
			 holder.mTime.setText(ComUtil.getSpecialDayText( mCtx, schedule.getScheduleGubun(), schedule.getSubName() ));

			 if ( schedule.getCycle() != null && !schedule.getCycle().trim().equals("") ) {
				 holder.mCycle.setVisibility(View.VISIBLE);
				 holder.mCycle.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.sm_list_repeat_foryear));
			 } else {
				 holder.mCycle.setVisibility(View.GONE);
				 holder.mCycle.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.sm_list_repeat));
			 }
			 
			 if ( schedule.getAlarmYn() != null && !schedule.getAlarmYn().trim().equals("") ) {
				 holder.mAlarm.setVisibility(View.VISIBLE);
			 } else {
				 holder.mAlarm.setVisibility(View.GONE);
			 }			 
		} else {
			 holder.mDayOfWeek.setVisibility(View.VISIBLE);	 
			 holder.mAllDayYn.setVisibility(View.VISIBLE);
			 
			 holder.mScheduleName.setTextColor( mCtx.getResources().getColor(R.color.black));
			 holder.mUserColor.setBackgroundColor(schedule.getUseColor());
			 
			 
			 holder.mDate.setText(SmDateUtil.getDateFullFormat(mCtx, schedule.getStartDate(),false, false)
						+ "~" + SmDateUtil.getDateFullFormat(mCtx, schedule.getEndDate(),false, false));
			 //holder.mAllDayYn.setText(ComUtil.setYesReturnValue(schedule.getAllDayYn(), ComConstant.ALLDAY));
			 if ( schedule.getAllDayYn() != null && !schedule.getAllDayYn().trim().equals("")) {
				 holder.mTime.setText(ComUtil.setYesReturnValue(schedule.getAllDayYn(), ComUtil.getStrResource(mCtx, R.string.allday)));
			 } else {
				 holder.mTime.setText(SmDateUtil.getTimeFullFormat(mCtx, schedule.getStartTime())
							+ "~" + SmDateUtil.getTimeFullFormat(mCtx, schedule.getEndTime()));
			 }
			 
			 //반복타입에 따라 배경 및 그외 view 처리
			 if ( schedule.getCycle() != null && schedule.getCycle().trim().equals("Y") ) {
				 holder.mCycle.setVisibility(View.VISIBLE);
				 holder.mCycle.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.sm_list_repeat));
				 //요일값 setting
		 		 ViewUtil.getDayOfWeekTextSet(mCtx, holder.mDayOfWeek, holder.mDayOfWeekTxt, schedule);
				 
			 } else if ( schedule.getCycle() != null && schedule.getCycle().trim().equals("M") ) {
				 holder.mCycle.setVisibility(View.VISIBLE);
				 holder.mCycle.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.sm_list_repeat_formonth));
				 //월반복일자 setting
				 ViewUtil.getRepeatMonthTextSet(mCtx, holder.mDayOfWeek, holder.mDayOfWeekTxt, schedule);
				 
			 } else {
				 holder.mCycle.setVisibility(View.GONE);
				 holder.mCycle.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.sm_list_repeat));
			 }
			 
			 if ( schedule.getAlarmYn() != null && !schedule.getAlarmYn().trim().equals("") ) {
				 holder.mAlarm.setVisibility(View.VISIBLE);
			 } else {
				 holder.mAlarm.setVisibility(View.GONE);
			 }
			 
			 //연락처, 메모추가(데이터 있는경우만 보여짐)
			 if ( schedule.getTel() != null && !schedule.getTel().trim().equals("") ) {
				 holder.mTel.setVisibility(View.VISIBLE);
				 holder.mTel.setText("Tel : " + schedule.getTel().toString());
			 } else {
				 holder.mTel.setVisibility(View.GONE);
				 holder.mTel.setText("");
			 }	
			 //연락처, 메모추가(데이터 있는경우만 보여짐)
			 if ( schedule.getMemo() != null && !schedule.getMemo().trim().equals("") ) {
				 holder.mMemo.setVisibility(View.VISIBLE);
				 holder.mMemo.setText(schedule.getMemo().toString());
			 } else {
				 holder.mMemo.setVisibility(View.GONE);
				 holder.mMemo.setText("");
			 }				 
		}
		
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
    	if ( children.size() > 0 )
    		return children.get(groupPosition).size();
    	else return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // Return a group view. You can load your custom layout here.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
    	 
        String group = (String) getGroup(groupPosition);
       
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.schedule_datelist_group, null);
        }
        
        TextView tv 	= (TextView) convertView.findViewById(R.id.date_top);
        TextView mDDay	= (TextView) convertView.findViewById(R.id.dday);
        
        mDDay.setText(SmDateUtil.getDDayString(
						SmDateUtil.getDateGapFromToday(group)));						
        
        String str = SmDateUtil.getDateFullFormat(mCtx, group, true, false)
        			+ " ("
        			+ Integer.toString(getChildrenCount(groupPosition))
        			+ ComUtil.getStrResource(mCtx, R.string.count)   
        			+ ")"; 
        
        tv.setText(str);
        
        convertView.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        mDDay.setVisibility(View.GONE);

//addFocusables(views, direction)
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
