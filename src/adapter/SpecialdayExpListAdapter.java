package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;


/*
 * 기념일관리-리스트 adapter
 * viewHolder 추가
 */
public class SpecialdayExpListAdapter extends BaseExpandableListAdapter
{
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context mCtx;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<SpecialDayInfo>> children;

    class ViewHolder {
 
		TextView mName;
		TextView mDate ; 
		TextView mDayOfWeek;
		TextView mLeap ;		
		TextView mDDay ;
		TextView mDay ;
		TextView mMemo ;

		ImageView mRepeat;
		ImageView mEvent ;	
		ImageView mAlarm ;
    }
	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public SpecialdayExpListAdapter(Context context, ArrayList<String> groups,
            ArrayList<ArrayList<SpecialDayInfo>> children) {
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
//    public void addItemGroup( String  parentstr ) {
//
//        if ( parentstr != null && parentstr.length() == 2 ) {
//        	groups.add(parentstr);
//        	int index = groups.indexOf(parentstr);
//	        if (children.get(index) == null ) {
//	            children.add(new ArrayList<SpecialDayInfo>());
//	        }
//        	
//        }
//    }	
    
    public void addItem(SpecialDayInfo child) {
    	//년월기준
    	String mm = child.getSolardate().substring( 4 , 6 );

        if (!groups.contains(mm)) {
            groups.add(mm);
        }
      //데이터 없을경우 title만
    	if ( child.getId() == (long) 0 ) {
    		/// child add 없음
    		//children.add(new ArrayList<SpecialDayInfo>());
    	}else{
    	
	        int index = groups.indexOf(mm);
	        if (children.size() < index + 1) {
	            children.add(new ArrayList<SpecialDayInfo>());
	        }
	        children.get(index).add(child);
//    		int index = children.size();
//    		children.add(new ArrayList<SpecialDayInfo>());
//    		children.get(index).add(child);
	        
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
    	
    	SpecialDayInfo info = (SpecialDayInfo) getChild(groupPosition, childPosition);
    	
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.specialday_exlist_child, null);
 
			holder = new ViewHolder();
			holder.mName 		= (TextView) convertView.findViewById(R.id.name);
			holder.mRepeat		= (ImageView) convertView.findViewById(R.id.repeat);
			holder.mDayOfWeek 	= (TextView) convertView.findViewById(R.id.dayofweek);
			holder.mEvent 		= (ImageView) convertView.findViewById(R.id.event);
			holder.mDate		= (TextView) convertView.findViewById(R.id.date);
			holder.mLeap		= (TextView) convertView.findViewById(R.id.leap);
			holder.mDDay 		= (TextView) convertView.findViewById(R.id.dday);
			holder.mDay			= (TextView) convertView.findViewById(R.id.day);
			holder.mMemo		= (TextView) convertView.findViewById(R.id.memo);
			holder.mAlarm 		= (ImageView) convertView.findViewById(R.id.alarm);
			
			convertView.setTag(holder);
         
        } else {
			
			holder = (ViewHolder) convertView.getTag();
		}

		//값 setting
        holder.mName.setText(info.getName());
        //등록된일자
        holder.mDate.setText(SmDateUtil.getDateFullFormat(mCtx,info.getMakeThisDate(), false, false));
        
        //해당년도 일자
        holder.mDayOfWeek.setText(SmDateUtil.getDayOfWeekFromDate(mCtx, info.getSolardate()));
//        holder.mDayOfWeek.setText(SmDateUtil.getDayOfWeekFromDate(mCtx, info.getMakeThisDate()));
//        holder.mDayOfWeek.setBackgroundDrawable(mCtx.getResources().getDrawable(R.drawable.sm_list_dayofweek));
    			 
		//holder.mLeap.setText(ComUtil.getSpinnerText(mCtx, R.array.arr_leap_key, R.array.arr_leap, info.getLeap()));
		holder.mLeap.setText(ComUtil.getLeapText( mCtx, info.getLeap()));
		
		int gap = SmDateUtil.getDateGapFromToday(info.getSolardate());
		if (  gap <= 0 ) {
			holder.mDDay.setTextColor(mCtx.getResources().getColor(R.color.red));
		} else {
			holder.mDDay.setTextColor(mCtx.getResources().getColor(R.color.listtext));
		}
		holder.mDDay.setText(info.getDDay());
		holder.mDay.setText(SmDateUtil.getDateFormatPerGubun(mCtx, info.getSolardate(), ComConstant.GUBUN_DAY, true, false));
		
		if ( info.getRepeatYn() != null && !info.getRepeatYn().trim().equals("") ) {
			 holder.mRepeat.setVisibility(View.VISIBLE);
		} else {
			 holder.mRepeat.setVisibility(View.GONE);
		}
		
		if ( info.getEvent() != null && !info.getEvent().trim().equals("") ) {
			 //holder.mEvent.setBackgroundDrawable(ViewUtil.getEventImageResource( mCtx , info.getEvent() ));
			 holder.mEvent.setImageDrawable(ViewUtil.getEventImageResource( mCtx , info.getEvent() ));
			 holder.mEvent.setVisibility(View.VISIBLE);
		} else {
			 holder.mEvent.setVisibility(View.GONE);
		}
		
		 //연락처, 메모추가(데이터 있는경우만 보여짐)
		 if ( info.getMemo() != null && !info.getMemo().trim().equals("") ) {
			 holder.mMemo.setVisibility(View.VISIBLE);
			 holder.mMemo.setText(info.getMemo().toString());
		 } else {
			 holder.mMemo.setVisibility(View.GONE);
			 holder.mMemo.setText("");
		 }	

		 //알람
		 if ( info.getAlarm() != null && !info.getAlarm().trim().equals("") ) {
			  holder.mAlarm.setVisibility(View.VISIBLE);
		 } else {
			  holder.mAlarm.setVisibility(View.GONE);
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
            convertView = infalInflater.inflate(R.layout.specialday_exlist_group, null);
        }
        
        TextView tv 	= (TextView) convertView.findViewById(R.id.month_top);
        String monthTxt = SmDateUtil.getDateFormatPerGubun(mCtx, group, ComConstant.GUBUN_MONTH, true, true)
        				+ " ("
        				+ getChildrenCount(groupPosition)
        				+ ")";
        tv.setText( monthTxt );

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
