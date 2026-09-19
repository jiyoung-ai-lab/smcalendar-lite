package com.waveapp.smcalendarlite.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.info.TodoMemoInfo;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 할일관리-리스트 adapter
 * viewHolder 추가
 */
public class TodoListAdapter extends ArrayAdapter<TodoMemoInfo>
{
	private Context mCtx;
	private int mResource;
	private ArrayList<TodoMemoInfo> mList;
	private LayoutInflater mInflater;
	
    class ViewHolder {
    	
		LinearLayout lv ;
		TextView mMemo;
		TextView mFinishTerm;
		TextView mDDay;
		TextView mConfirmDate;
//        ImageView mRepeat;
		ImageView mAlarm;
    }
    
	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public TodoListAdapter(Context context, int layoutResource, ArrayList<TodoMemoInfo> objects)
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
		
		TodoMemoInfo info = mList.get(position);

		if(info == null) return null;
		
		if(convertView == null)
		{
			convertView = mInflater.inflate(mResource, null);
			
			holder = new ViewHolder();			
			holder.lv 			= (LinearLayout) convertView.findViewById(R.id.lin_main);
			holder.mMemo	 	= (TextView) convertView.findViewById(R.id.memo);
			holder.mFinishTerm	= (TextView) convertView.findViewById(R.id.finishterm);
			holder.mDDay		= (TextView) convertView.findViewById(R.id.dday);
			holder.mDDay		= (TextView) convertView.findViewById(R.id.dday);
			holder.mConfirmDate	= (TextView) convertView.findViewById(R.id.confirmdate);
//			holder.mRepeat 		= (ImageView) convertView.findViewById(R.id.repeat);
			holder.mAlarm		= (ImageView) convertView.findViewById(R.id.alarm);
			
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		//값 setting
		
		
		if ( ComUtil.setBlank(info.getFinish()) != null && !ComUtil.setBlank(info.getFinish()).equals("")) {			
			//SpannableString content = new SpannableString(info.getMemo());  			
			//content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			holder.mMemo.setPaintFlags(holder.mMemo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Text Middle Line
			holder.mMemo.setTextColor(mCtx.getResources().getColorStateList(R.color.darkgray));
			//holder.mMemo.sets
			holder.mMemo.setText(info.getMemo());
			
		} else {
			holder.mMemo.setPaintFlags(0); // Text Middle Lin
			holder.mMemo.setPaintFlags(holder.mMemo.getPaintFlags() | Paint.ANTI_ALIAS_FLAG);
			holder.mMemo.setTextColor(mCtx.getResources().getColorStateList(R.color.black));
			holder.mMemo.setText(info.getMemo());			
		}
		if (info.getTermyn()!= null && !info.getTermyn().equals("")){
			if (info.getFinishterm() != null && !info.getFinishterm().equals("")){
				holder.mFinishTerm.setText(SmDateUtil.getDateFullFormat(mCtx, info.getFinishterm(), true, false));
				holder.mDDay.setText(SmDateUtil.getDDayString(SmDateUtil.getDateGapFromToday(info.getFinishterm())));
			} else {
				holder.mFinishTerm.setText("");
				holder.mDDay.setText("");
			}
		} else {
			holder.mFinishTerm.setText(ComUtil.getStrResource(mCtx, R.string.notexist_termyn));
			holder.mDDay.setText("");
		}
		
		holder.mConfirmDate.setText(info.getComfirmdate().substring(0,16));
		
//		if ( info.getRepeat() != null && !info.getRepeat().trim().equals("") ) {
//			 holder.mRepeat.setVisibility(View.VISIBLE);
//		} else {
//			 holder.mRepeat.setVisibility(View.GONE);
//		}
		if ( info.getAlarm() != null && !info.getAlarm().trim().equals("") ) {
			 holder.mAlarm.setVisibility(View.VISIBLE);
		} else {
			 holder.mAlarm.setVisibility(View.GONE);
		}
		 
		return convertView;
	}
}
