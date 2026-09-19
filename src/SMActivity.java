package com.waveapp.smcalendarlite;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;

import com.waveapp.smcalendarlite.util.ViewUtil;

public class SMActivity  extends Activity {

	@Override   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.setLocaleFromPreference(this, this.getClass());
    }

	@Override
	protected void onRestart() {
		super.onRestart();
		ViewUtil.setLocaleFromPreference(this, this.getClass());
	}


	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		removeDialog(id);
		dialog = null;
	}

//	@Override
//	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
//		super.onPrepareDialog(id, dialog, args);
//		removeDialog(id);
//		dialog = null;
//	}


	@Override
	protected Dialog onCreateDialog(int id) {
		ViewUtil.setLocaleFromPreference(this, this.getClass());		
		return super.onCreateDialog(id);
	}

//	@Override
//	protected Dialog onCreateDialog(int id, Bundle args) {			
//		ViewUtil.setLocaleFromPreference(this, this.getClass());
//		return super.onCreateDialog(id, args);		
//	}
	/*
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
//		ContextMenu dackmenu = menu;
//		menu.clear();
		ViewUtil.setLocaleFromPreference(this, this.getClass());
//		menu = dackmenu;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		Menu backmenu = menu;		
//		menu.clear();
		ViewUtil.setLocaleFromPreference(this, this.getClass());
//		menu = backmenu;
		
        return true;		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		ViewUtil.setLocaleFromPreference(this, this.getClass());
		return ret;
		
	}
}
