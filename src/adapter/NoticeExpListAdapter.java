
package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.NoticeInfo;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 공지사항을 보여줄 때 사용
 */
public class NoticeExpListAdapter extends BaseExpandableListAdapter {

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context mCtx;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<NoticeInfo>> children;
//    private ArrayList<NoticeInfo> groupsselect = new  ArrayList<NoticeInfo>();
   // private CheckBox childCheckBox[][]; // child layout cache (group,child)
    
    //private int mCheckCnt;
    
//    class ViewHolderTitle {
//    	
//        TextView mTitle;
//		TextView mApplyDate;
//		
//    }
    class ViewHolder {
    	
		TextView mContent;
		
    }    
    public NoticeExpListAdapter(Context context, ArrayList<String> groups,
    		ArrayList<ArrayList<NoticeInfo>> children) {
    	
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
    public void addItem(NoticeInfo childInfo) {
    	String id = Long.toString(childInfo.getId());
    	
        if (!groups.contains(id)) {
            groups.add(id);
//            groupsselect.add(childInfo);
        }
        int index = groups.indexOf(id);
        if (children.size() < index + 1) {
            children.add(new ArrayList<NoticeInfo>());
        }
        children.get(index).add(childInfo);
//        groupsselect.add(childInfo);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }
    public void setChild(int groupPosition, int childPosition, NoticeInfo info ) {
    	children.get(groupPosition).set(childPosition, info);
    }


	@Override
    public long getChildId(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition).getId();
    }
    
    // Return a child view. You can load your custom layout here.
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
    	
    	
    	ViewHolder holder;
    	
    	NoticeInfo  notice = ( NoticeInfo ) getChild(groupPosition, childPosition);
    	
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.notice_child, null);
            
			holder = new ViewHolder();
			holder.mContent 		= (TextView) convertView.findViewById(R.id.content);
			
			convertView.setTag(holder);
			
        } else {			
			holder = (ViewHolder) convertView.getTag();			
		}
        
         //값 setting
         holder.mContent.setText( notice.getContent() );
    	
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
    
    public Object getGroupKeyId(long id) {
    	String idstr = Long.toString(id);
    	int index = groups.indexOf(idstr);
    	return children.get(index).get(0);
    }
    
//    public Object getGroupData(int groupPosition) {
//    	
//    	return groupsselect.get(groupPosition);
//    }
    // Return a group view. You can load your custom layout here.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
    	
        NoticeInfo child = (NoticeInfo) getChild(groupPosition, 0);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.notice_group, null);
        }
            
        TextView mTitle			= (TextView) convertView.findViewById(R.id.title);
        TextView mReadDate 		= (TextView) convertView.findViewById(R.id.readdate);   
        TextView mApplyDate	= (TextView) convertView.findViewById(R.id.applydate);           
        
        if ( child.getReaddate() != null && !child.getReaddate().equals("")) {
        	mReadDate.setText( "" );
        	mReadDate.setBackgroundDrawable( null );
        } else {
        	
        	mReadDate.setText( "N" );
        	mReadDate.setBackgroundDrawable( mCtx.getResources().getDrawable(R.drawable.sm_notice_cnt) );
        }
       
//        convertView.setTag(groupPosition, child);
        
        mTitle.setText( child.getTitle() );
        mApplyDate.setText( SmDateUtil.getDateSimpleFormat(mCtx, child.getApplydate(), ComConstant.SEPERATE_DATE, false)  );
       
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return false;
    }
 

}
