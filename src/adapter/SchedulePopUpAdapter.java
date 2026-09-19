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
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;


/*
 * 
 */
public class SchedulePopUpAdapter extends ArrayAdapter<ScheduleInfo>
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
	public SchedulePopUpAdapter(Context context, int layoutResource, ArrayList<ScheduleInfo> objects)
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
		ScheduleInfo info = mList.get(position);

		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
		}

		if(info != null)
		{
			TextView mUserColor	 	= (TextView) convertView.findViewById(R.id.usercolor);
			TextView mGubun	 		= (TextView) convertView.findViewById(R.id.gubun);
			TextView mScheduleName 	= (TextView) convertView.findViewById(R.id.schedulename);
			
        	int infoColor 		= 0;
        	String gubunStr 	= "";
        	String scheduleStr 	= "";
        	
        	if ( info.getScheduleGubun() != null ) {
        		if ( info.getScheduleGubun().equals("S")) {
        			infoColor = info.getUseColor();
					if ( info.getAllDayYn() != null && !info.getAllDayYn().equals("")) {
						gubunStr = ComUtil.setYesReturnValue(info.getAllDayYn(), ComUtil.getStrResource(mCtx, R.string.allday));
					} else {
						gubunStr = SmDateUtil.getTimeFullFormat(mCtx,info.getStartTime());
					}	
					scheduleStr = info.getScheduleName();
        		} else {
    				if ( info.getHolidayYn() != null && info.getHolidayYn().trim().equals("Y")){
    					infoColor = mCtx.getResources().getColor(R.color.calsunday);
    				} else {
    					infoColor = mCtx.getResources().getColor(R.color.lightgray);
    				}
    				if ( info.getScheduleName() != null && !info.getScheduleName().trim().equals("")){
    					gubunStr 	= ComUtil.getSpecialDayText( mCtx, info.getScheduleGubun(), info.getSubName() );
    					scheduleStr = info.getScheduleName();
    				} else {
    					gubunStr 	= ComUtil.getSpecialDayText( mCtx, info.getScheduleGubun(), info.getSubName() );
    					scheduleStr = info.getSubName();
    				}	        				
        		}
        	}
        	
        	mUserColor.setBackgroundColor(infoColor);
        	mGubun.setText(gubunStr);
        	mScheduleName.setText(scheduleStr);

			
		}

		return convertView;
	}
}
