package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.SmDateUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;


/*
 * 기념일관리-리스트 adapter ( 달력 ) 
 * viewHolder 추가
 */
public class SpecialdayListAdapter2 extends ArrayAdapter<SpecialDayInfo>
{
	private Context mCtx;
	private int mResource;
	private ArrayList<SpecialDayInfo> mList;
	private LayoutInflater mInflater;
	
    class ViewHolder {
 
		TextView mName;
		//TextView mSubName ; 
		//TextView mRepeat;
		//TextView mGubun ; 
		//TextView mDate ; 
		//TextView mLeap ;
		TextView mDay ;
		//TextView mDDay ; 
		
    }
    
	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public SpecialdayListAdapter2(Context context, int layoutResource, ArrayList<SpecialDayInfo> objects)
	{
		super(context, layoutResource, objects);
		this.mCtx 		= context;
		this.mResource 	= layoutResource;
		this.mList 		= objects;
		this.mInflater 	= (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		ViewUtil.setLocaleFromPreference(mCtx, mCtx.getClass());
	}


	/*
	 * row data set
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		SpecialDayInfo info = mList.get(position);

		if(info == null) return null;
		
		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
			
			holder = new ViewHolder();
			holder.mName = (TextView) convertView.findViewById(R.id.name);
			holder.mDay= (TextView) convertView.findViewById(R.id.day);
			
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		//값 setting
		if ( holder.mName == null ||  info.getName() == null ) return null;
		
		holder.mName.setText(info.getName());
		holder.mDay.setText(SmDateUtil.getDateSimpleFormat(mCtx, info.getSolardate().substring( 4 ), ".", false));
//		holder.mDay.setText(SmDateUtil.getDateFormatPerGubun(mCtx,info.getSolardate(), ComConstant.GUBUN_DAY, true));

		return convertView;
	}
}
