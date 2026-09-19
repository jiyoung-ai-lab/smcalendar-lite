package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.info.SpecialDayInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;


/*
 * 기념일관리-리스트 adapter
 * viewHolder 추가
 */
public class SpecialdayListAdapter extends ArrayAdapter<SpecialDayInfo>
{
	private Context mCtx;
	private int mResource;
	private ArrayList<SpecialDayInfo> mList;
	private LayoutInflater mInflater;
	
    class ViewHolder {
 
		TextView mName;
		//TextView mSubName ; 
		TextView mRepeat;
		//TextView mGubun ; 
		TextView mEvent ;
		TextView mDate ; 
		TextView mLeap ;		
		TextView mDDay ; 
		
		//TextView mSolardate ;
		//TextView mYear ; 
		TextView mMonth ;
		TextView mDay ;	
    }
    
	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public SpecialdayListAdapter(Context context, int layoutResource, ArrayList<SpecialDayInfo> objects)
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
		
		SpecialDayInfo info = mList.get(position);

		if(info == null) return null;
		
		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
			
			holder = new ViewHolder();
			holder.mName 	= (TextView) convertView.findViewById(R.id.name);
//			holder.mSubName	= (TextView) convertView.findViewById(R.id.subname);
			holder.mRepeat	= (TextView) convertView.findViewById(R.id.repeat);
			holder.mEvent 	= (TextView) convertView.findViewById(R.id.event);
			//holder.mGubun 	= (TextView) convertView.findViewById(R.id.gubun);
			holder.mDate	= (TextView) convertView.findViewById(R.id.date);
			holder.mLeap	= (TextView) convertView.findViewById(R.id.leap);
//			holder.mSolardate= (TextView) convertView.findViewById(R.id.solardate);
			holder.mDDay 	= (TextView) convertView.findViewById(R.id.dday);
			//holder.mYear= (TextView) convertView.findViewById(R.id.year);
			holder.mMonth= (TextView) convertView.findViewById(R.id.month);
			holder.mDay= (TextView) convertView.findViewById(R.id.day);
			
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		//값 setting
		holder.mName.setText(info.getName());
		/*
		if ( info.getGubun() != null && info.getGubun().equals( "B" )) {
			holder.mSubName.setText(info.getSubName());
		} else {
			holder.mSubName.setText(ComConstant.SPECIALDAY);
		}
		*/
		//String pSep2 = ComUtil.getStrResource(ctx, R.string.month);
		holder.mRepeat.setText(ComUtil.setYesReturnValue(info.getRepeatYn(),ComUtil.getStrResource(mCtx, R.string.everyyear)));
		holder.mEvent.setText(ComUtil.getSpinnerText(mCtx, R.array.arr_event_key, R.array.arr_event, info.getEvent()));
		//holder.mGubun.setText(info.getGubun());
		holder.mDate.setText(SmDateUtil.getDateFullFormat(mCtx,info.getMakeThisDate(), false, false));
		holder.mLeap.setText(ComUtil.getSpinnerText(mCtx, R.array.arr_leap_key, R.array.arr_leap, info.getLeap()));
//		holder.mSolardate.setText(DateUtil.getDateFullFormat(info.getSolardate(), true));
		holder.mDDay.setText(info.getDDay());
		holder.mMonth.setText(SmDateUtil.getDateFormatPerGubun(mCtx,info.getSolardate(), ComConstant.GUBUN_MONTH, true, false));
		//holder.mYear.setText(DateUtil.getDateFullFormat(info.getSolardate().substring(0,6),false).substring(6));
		//holder.mYear.setText(DateUtil.getDateToStr(info.getSolardate(), ComConstant.GUBUN_YEAR));
		holder.mDay.setText(SmDateUtil.getDateFormatPerGubun(mCtx,info.getSolardate(), ComConstant.GUBUN_DAY, true, false));
//		holder.mDay.setText(DateUtil.getDateToStr(info.getSolardate(), ComConstant.GUBUN_DAY));
		
		return convertView;
	}
}
