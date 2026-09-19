package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.UserInfo;
import com.waveapp.smcalendarlite.util.ViewUtil;


/*
 * 사용자관리-리스트 adapter
 * viewHolder 추가
 */
public class UserListAdapter extends ArrayAdapter<UserInfo>
{
	private Context mCtx;
	private int mResource;
	private ArrayList<UserInfo> mList;
	private LayoutInflater mInflater;
	
    class ViewHolder {
    	
		LinearLayout lv ;
		TextView mUserName;
		TextView mRelation;
		//TextView mBirthday ; 
		
    }
    
	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public UserListAdapter(Context context, int layoutResource, ArrayList<UserInfo> objects)
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
		
		UserInfo info = mList.get(position);

		if(info == null) return null;
		
		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
			
			holder = new ViewHolder();			
			holder.lv 			= (LinearLayout) convertView.findViewById(R.id.lin_user);
			holder.mUserName 	= (TextView) convertView.findViewById(R.id.username);
			holder.mRelation	= (TextView) convertView.findViewById(R.id.relation);
			//holder.mBirthday 	= (TextView) convertView.findViewById(R.id.birthday);
			
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		//값 setting
		holder.mUserName.setText(info.getUserName());
		holder.mRelation.setText(info.getRelationText());
		//holder.mBirthday.setText(info.getBirthday());
		
		Drawable draw = mCtx.getResources( ).getDrawable( R.drawable.sm_user_list );		
		draw.setColorFilter(ViewUtil.drawBackGroundColor ( info.getUsercolor()));
		holder.lv.setBackgroundDrawable(draw);

		return convertView;
	}
}
