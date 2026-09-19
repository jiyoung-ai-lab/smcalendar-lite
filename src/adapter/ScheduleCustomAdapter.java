package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.ScheduleInfo;
import com.waveapp.smcalendarlite.util.SmDateUtil;


/*
 * 
 */
public class ScheduleCustomAdapter extends ArrayAdapter<ScheduleInfo>
{
	private Context mCtx;
	private int mResource;
	private ArrayList<ScheduleInfo> mList;
	private LayoutInflater mInflater;

	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public ScheduleCustomAdapter(Context context, int layoutResource, ArrayList<ScheduleInfo> objects)
	{
		super(context, layoutResource, objects);
		this.mCtx 	= context;
		this.mResource 	= layoutResource;
		this.mList 		= objects;
		this.mInflater 	= (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ScheduleInfo schedule = mList.get(position);

		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
		}

		if(schedule != null)
		{
			//ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.username);
			TextView mUserName 		= (TextView) convertView.findViewById(R.id.username);
			TextView mUserNameTop	= (TextView) convertView.findViewById(R.id.username_top);
			TextView mScheduleName 	= (TextView) convertView.findViewById(R.id.schedulename);
			TextView mDayOfWeek 	= (TextView) convertView.findViewById(R.id.dayofweek);
			TextView mTime		 	= (TextView) convertView.findViewById(R.id.time);
			//°ª setting
			String username = schedule.getUsername();
			if ( username != null && !username.equals(""))
				mUserNameTop.setText(username);
			
			mUserName.setText(username);
			mScheduleName.setText(schedule.getScheduleName());
			mDayOfWeek.setText(schedule.getDayOfWeekFullText());
			mTime.setText(SmDateUtil.getTimeFullFormat( mCtx, schedule.getStartTime())
					+ "~" + SmDateUtil.getTimeFullFormat( mCtx, schedule.getEndTime()));
			
		}

		return convertView;
	}
}
