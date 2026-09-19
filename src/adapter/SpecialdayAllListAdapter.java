
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
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

/*
 * 기념일리스트 출력시 그룹 단위 (parent : user )
 * 기념을 보여줄 때 사용
 */
public class SpecialdayAllListAdapter extends BaseExpandableListAdapter {
	

	@Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context mCtx;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<SpecialDayInfo>> children;
    private CheckBox parentCheckBox[]; // parent layout cache (group)
    private CheckBox childCheckBox[][]; // child layout cache (group,child)
    
    private boolean parentCheckBoxValue[]; // parent checkvalue (group)
    
    private int mCheckCnt;
    
    class ViewHolder {
    	
		CheckBox mChoice;
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
    public SpecialdayAllListAdapter(Context context, ArrayList<String> groups,
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
    public void addItem(SpecialDayInfo child) {
    	//그룹기준
    	String userid = ComUtil.setBlank(child.getUserGroup()) ;

        if (!groups.contains(userid)) {
            groups.add(userid);
        }
        //데이터 없을경우 title만
    	if ( child.getId() == (long) 0 ) {
    		/// child add 없음
    		//children.add(new ArrayList<SpecialDayInfo>());
    	}else{
    	
	        int index = groups.indexOf(userid);
	        if (children.size() < index + 1) {
	            children.add(new ArrayList<SpecialDayInfo>());
	        }
	        children.get(index).add(child);
	        
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
            convertView = infalInflater.inflate(R.layout.specialday_alllist_child, null);
            
			holder = new ViewHolder();
			holder.mChoice		= (CheckBox) convertView.findViewById(R.id.choice);
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

        //Check Box 제어Checked()
         setCheckBoxView( convertView, groupPosition, childPosition );		
         holder.mChoice.setTag(info);
         holder.mChoice.setChecked(info.isChoice());
         
         //multi select처리를 위한 tag
         childCheckBox[groupPosition][childPosition].setTag(info);
         
 		//값 setting
        holder.mName.setText(info.getName());
        //등록된일자
        holder.mDate.setText(SmDateUtil.getDateFullFormat(mCtx,info.getMakeThisDate(), false, false));
         
        //해당년도 일자
        holder.mDayOfWeek.setText(SmDateUtil.getDayOfWeekFromDate(mCtx, info.getSolardate()));
 		holder.mLeap.setText(ComUtil.getLeapText( mCtx, info.getLeap()));
 		
 		int gap = SmDateUtil.getDateGapFromToday(info.getSolardate());
 		if (  gap <= 0 ) {
 			holder.mDDay.setTextColor(mCtx.getResources().getColor(R.color.red));
 		} else {
 			holder.mDDay.setTextColor(mCtx.getResources().getColor(R.color.listtext));
 		}
 		holder.mDDay.setText(info.getDDay());
// 		holder.mDay.setText(SmDateUtil.getDateFormatPerGubun(mCtx, info.getSolardate(), ComConstant.GUBUN_DAY, true, false));
 		if ( info.getSolardate() != null && info.getSolardate().length() == 8 ) {
 			holder.mDay.setText(SmDateUtil.getDateSimpleFormat(mCtx, info.getSolardate().substring(4), ".", false));
 		} else {
 			holder.mDay.setText("");
 		}
 		
 		
 		if ( info.getRepeatYn() != null && !info.getRepeatYn().trim().equals("") ) {
 			 holder.mRepeat.setVisibility(View.VISIBLE);
 		} else {
 			 holder.mRepeat.setVisibility(View.GONE);
 		}
 		
 		if ( info.getEvent() != null && !info.getEvent().trim().equals("") ) {
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
            convertView = infalInflater.inflate(R.layout.specialday_alllist_group, null);
        }
//        LinearLayout lin = (LinearLayout) convertView.findViewById(R.id.lin_line);
        TextView tv = (TextView) convertView.findViewById(R.id.usergroup_top);
        CheckBox userchoice = (CheckBox) convertView.findViewById(R.id.userchoice);
        
        //Group CheckBox
        setGroupCheckBoxView(convertView, groupPosition);
        userchoice.setTag(group);
        userchoice.setChecked(parentCheckBoxValue[groups.indexOf(group)]);
        
        int cnt = getChildrenCount(groupPosition);
        String groupname = "";
//        int usercolor = 0;
        for ( int i = 0 ; i < cnt ; i++ ) {
        	SpecialDayInfo info = (SpecialDayInfo) getChild(groupPosition, i);
        	String ugroup = info.getUserGroup();
        	if ( group == ugroup ) {        		
        		groupname = ComUtil.getSpinnerText(mCtx, R.array.arr_usergroup_key, R.array.arr_usergroup, ugroup);  //임시
//        		usercolor = info.getUseColor();
        		break;
        	}
        }
        String str = groupname
					+ "("
					+ Integer.toString(getChildrenCount(groupPosition))
        			+ ")"; 
        
//        Drawable md = mCtx.getResources( ).getDrawable( R.drawable.sm_cal_schedule_cell );									
//		md.setColorFilter(ViewUtil.drawBackGroundColor ( usercolor ));		
//        lin.setBackgroundDrawable(md);
//        lin.setBackgroundColor(usercolor);
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
						SpecialDayInfo info = (SpecialDayInfo)buttonView.getTag();
						info.setChoice(isChecked);
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
    		SpecialDayInfo childtag = (SpecialDayInfo) children.get(index).get(i);
    		childtag.setChoice(isChecked);
    	}
    	
    	//2.multi select 처리 (view)
    	int glen = childCheckBox.length;
    	for ( int i = 0 ; i < glen ; i++ ) {
    		int clen = childCheckBox[i].length;
    		for ( int j = 0 ; j < clen ; j++ ) {    			
    			if ( childCheckBox[i][j] != null ) {
        			SpecialDayInfo childtag = (SpecialDayInfo) childCheckBox[i][j].getTag();         			
        			if ( grouptag != null && childtag != null && grouptag.equals(childtag.getUserGroup())) {
    					childCheckBox[i][j].setChecked(isChecked);
        			}    				

    			}
    		}
    	}    	
    	return;
    	
    }

}
