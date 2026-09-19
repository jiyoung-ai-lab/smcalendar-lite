
package com.waveapp.smcalendarlite;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.waveapp.smcalendarlite.common.ColorArray;
import com.waveapp.smcalendarlite.database.UsermanagerDbAdaper;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

 
public class ColorList extends ListActivity {

    private Integer[] arr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.setLocaleFromPreference(this, this.getClass());
        
        setContentView(R.layout.color_list);
        
        //Resources res = getResources();
        
        ColorArray col = new ColorArray(this);
        arr = col.getColorArray();
        
        setListAdapter(new EfficientAdapter(this));
    }
    
	  @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	  	super.onListItemClick(l, v, position, id);
	  	
	  	Intent intent = getIntent();
	  	//String usercolor = ((TextView)v).getText().toString();
	  	String usercolor = ComUtil.intToString(arr[ position ]);
	  	intent = new Intent(this, UserManager.class);    
	  	intent.putExtra(UsermanagerDbAdaper.KEY_USERCOLOR, usercolor);  
	  	setResult(RESULT_OK, intent);
	  	finish();
	
	  }
	
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	      super.onActivityResult(requestCode, resultCode, intent);
	      
	
	  }
	  @Override
	  protected void onSaveInstanceState(Bundle outState) {        
	  	super.onSaveInstanceState(outState);    
	  }	
	  @Override    
	  protected void onResume() {        
	  	super.onResume(); 
	  }        
	  @Override    
	  protected void onPause() {        
	  	super.onPause();
	  }
	  @Override 
	  protected void onDestroy() { 	
	      super.onDestroy();  
	      
	  } 
 
    /**
     * @author developer
     *
     */
    class EfficientAdapter extends BaseAdapter {
    	
    	private final Context mCtx;
        private LayoutInflater mInflater;
        private Drawable   mDraw ;
        
        public EfficientAdapter(Context context) {
            this.mCtx = context;
            mInflater = LayoutInflater.from(context);
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return arr.length;
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            
            mDraw = mCtx.getResources( ).getDrawable( R.drawable.sm_cal_schedule_cell );
            
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.color_row, null);
                holder = new ViewHolder();
                holder.colorText = (TextView) convertView.findViewById(R.id.usercolor);
                convertView.setTag(holder);
            } else {
               
                holder = (ViewHolder) convertView.getTag();
            }
            
            //holder.colorText.setText(arr[position].toString());
            mDraw.setColorFilter(ViewUtil.drawBackGroundColor ( arr[position] ));
            holder.colorText.setBackgroundDrawable(mDraw);
            return convertView;
        }
        
//        private void drawBackGroundColor( int color) {
//        	
//    	   	if ( color != 0 ) {
//    		        ColorFilter filter = new PorterDuffColorFilter(color,
//    		        		PorterDuff.Mode.MULTIPLY);
//    		        
//    	   	}
//          }       

        class ViewHolder {
            TextView colorText;
        }
    }

   

}
