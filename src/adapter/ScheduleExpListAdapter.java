
package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/*
 * 스케줄리스트 출력시 사용자 단위 (parent : user )
 * 스케줄을 보여줄 때 사용
 */
public class ScheduleExpListAdapter extends BaseExpandableListAdapter {
	

	@Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context mCtx;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<ScheduleInfo>> children;
    private CheckBox parentCheckBox[]; // parent layout cache (group)
    private CheckBox childCheckBox[][]; // child layout cache (group,child)
    
    private boolean parentCheckBoxValue[]; // parent checkvalue (group)
    
    private int mCheckCnt;
    
    class ViewHolder {
    	
    	CheckBox mChoice;
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
    public ScheduleExpListAdapter(Context context, ArrayList<String> groups,
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
    	String userid = Long.toString(childSch.getUserId());
    	
        if (!groups.contains(userid)) {
            groups.add(userid);
        }
        int index = groups.indexOf(userid);
        if (children.size() < index + 1) {
            children.add(new ArrayList<ScheduleInfo>());
        }
        children.get(index).add(childSch);
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
            convertView = infalInflater.inflate(R.layout.schedule_userlist_child, null);
            
			holder = new ViewHolder();
			holder.mChoice			= (CheckBox) convertView.findViewById(R.id.choice);
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

        //Check Box 제어Checked()
         setCheckBoxView( convertView, groupPosition, childPosition );		
         holder.mChoice.setTag(schedule);
         holder.mChoice.setChecked(schedule.isChoice());
         
         //multi select처리를 위한 tag
         childCheckBox[groupPosition][childPosition].setTag(schedule);
         
         //값 setting
         holder.mScheduleName.setText(schedule.getScheduleName());
		 
 		//요일값 setting
 		 ViewUtil.getDayOfWeekTextSet(mCtx, holder.mDayOfWeek, holder.mDayOfWeekTxt, schedule);
 		 
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
            convertView = infalInflater.inflate(R.layout.schedule_userlist_group, null);
        }
        LinearLayout lin = (LinearLayout) convertView.findViewById(R.id.lin_line);
        TextView tv = (TextView) convertView.findViewById(R.id.username_top);
        CheckBox userchoice = (CheckBox) convertView.findViewById(R.id.userchoice);
        
        //Group CheckBox
        setGroupCheckBoxView(convertView, groupPosition);
        userchoice.setTag(group);
        userchoice.setChecked(parentCheckBoxValue[groups.indexOf(group)]);
        
        int cnt = getChildrenCount(groupPosition);
        String username = "";
        int usercolor = 0;
        for ( int i = 0 ; i < cnt ; i++ ) {
        	ScheduleInfo schedule = (ScheduleInfo) getChild(groupPosition, i);
        	long userid = schedule.getUserId();
        	if ( Long.parseLong(group) == userid ) {
        		username = schedule.getUsername();
        		usercolor = schedule.getUseColor();
        		break;
        	}
        }
        String str = username
					+ "("
					+ Integer.toString(getChildrenCount(groupPosition))
        			+ ")"; 
        
//        Drawable md = mCtx.getResources( ).getDrawable( R.drawable.sm_cal_schedule_cell );									
//		md.setColorFilter(ViewUtil.drawBackGroundColor ( usercolor ));		
//        lin.setBackgroundDrawable(md);
        lin.setBackgroundColor(usercolor);
        tv.setText(str);
        
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
    
    public int getCheckCount() {
        return mCheckCnt;
    }   
    
    public void setCheckBoxInit( View childView, int groupPosition ) {

    	//데이터 없는 경우 skip
		if ( getChildrenCount(0) == 0 ) return ;
		
		//group * child 건수만큼 배열 생성, 단 최초호출시 1번만		
		if  ( childCheckBox == null ) {
			
			mCheckCnt = 0;
			int glen = getGroupCount();
			childCheckBox = new CheckBox[glen][];			
			for(int i = 0 ; i < glen ; i++) {
				//group에 종속된 child 건수만큼 배열 생성
				int clen = getChildrenCount(i);
				childCheckBox[i] = new CheckBox[clen];
			}
		}
		
        return ;
    }   
    
    public void setCheckBoxView( View childView, int groupPosition, int childPosition ) {
		
    	//데이터 없는 경우 skip
		if ( getChildrenCount(0) == 0 ) return ;
		
		//group * child 건수만큼 배열 생성, 단 최초호출시 1번만	(체크박스 객체 -> position정보가 상대적)	
		if  ( childCheckBox == null ) {
			
			mCheckCnt = 0;
			int glen = getGroupCount();
			childCheckBox = new CheckBox[glen][];			
			for(int i = 0 ; i < glen ; i++) {
				//group에 종속된 child 건수만큼 배열 생성
				int clen = getChildrenCount(i);
				childCheckBox[i] = new CheckBox[clen];
			}
		}

		//listener생성, 단 1번만 (상대적 값임.. 유의할것): 보이는 정보에 대한 값... 인듯.	
		childCheckBox[groupPosition][childPosition] = (CheckBox) childView.findViewById(R.id.choice);

		childCheckBox[groupPosition][childPosition].setOnCheckedChangeListener(
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
    /*
     * group check box
     */
    public void setGroupCheckBoxView( View parnetView, int groupPosition) {

    	int glen = getGroupCount();
    	
    	//데이터 없는 경우 skip
		if ( glen == 0 ) return ;
		
		//group 건수만큼 배열 생성, 단 최초호출시 1번만		
		if  ( parentCheckBox == null ) {
			parentCheckBox = new CheckBox[glen];
			parentCheckBoxValue= new boolean[glen];
			for ( int i = 0 ; i < glen ; i++ ) {
				parentCheckBoxValue[i] = false;
			}
		}

		//listener생성, 단 1번만			
		parentCheckBox[groupPosition] = (CheckBox) parnetView.findViewById(R.id.userchoice);
		
		
		parentCheckBox[groupPosition].setOnCheckedChangeListener(				
				new CompoundButton.OnCheckedChangeListener() {			
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						String userid = (String)buttonView.getTag();
						int index = groups.indexOf(userid);
						
						parentCheckBoxValue[index] = isChecked;
						
						//보이는 child view check 처리용
						refreshChildView(userid, isChecked);
					}
				});	
		
        return ;
    } 
    
    /*
     * 멀티체크시 child position을 못해 전체 key 일치하는 경우 처리
     */
    private void refreshChildView ( String grouptag , boolean isChecked ) {
    	
    	int index = groups.indexOf(grouptag);
    	
    	//1.multi select 처리 (value)
    	int len = children.get(index).size();
    	for ( int i = 0 ; i < len ; i++ ) {
    		ScheduleInfo childtag = (ScheduleInfo) children.get(index).get(i);
    		childtag.setChoice(isChecked);
    	}
    	
    	//2.multi select 처리 (view)
    	int glen = childCheckBox.length;
    	for ( int i = 0 ; i < glen ; i++ ) {
    		int clen = childCheckBox[i].length;
    		for ( int j = 0 ; j < clen ; j++ ) {    			
    			if ( childCheckBox[i][j] != null ) {
        			ScheduleInfo childtag = (ScheduleInfo) childCheckBox[i][j].getTag();         			
        			if ( grouptag != null && childtag != null && grouptag.equals(Long.toString(childtag.getUserId()))) {
    					childCheckBox[i][j].setChecked(isChecked);
        			}    				

    			}
    		}
    	}    	
    	return;
    	
    }

}
