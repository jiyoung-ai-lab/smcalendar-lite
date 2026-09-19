package com.waveapp.smcalendarlite.link;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.util.ComUtil;

public class EverNoteLink {
	
	private Context context;
	
	// Names of Evernote-specific Intent actions and extras
	public static final String ACTION_NEW_NOTE             = "com.evernote.action.CREATE_NEW_NOTE";
	
	public EverNoteLink(Context ctx) {
        this.context = ctx;
    }	
	  
	  /**
	   * Bring up an empty "New Note" activity in Evernote for Android.
	   */
	public void newNote(View view) {
		
	    Intent intent = new Intent();
	    intent.setAction(ACTION_NEW_NOTE);
	    try {
	    	context.startActivity(intent);
	    } catch (android.content.ActivityNotFoundException ex) {
	      //Toast.makeText(this, R.string.err_activity_not_found, Toast.LENGTH_SHORT).show();
	    } 
	}
	  
	  /**
	   * Bring up a "New Note" activity in Evernote for Android with the note content 
	   * and title prepopulated with values that we specify. 
	   */
	public void newNoteWithContent( String title, String message ) {
		
	    Intent intent = new Intent();
	    intent.setAction(ACTION_NEW_NOTE);

	    // Set the note's title and plaintext content
	    intent.putExtra(Intent.EXTRA_TITLE, title);
	    intent.putExtra(Intent.EXTRA_TEXT, message);

	    try {
	    	context.startActivity(intent);
	    } catch (android.content.ActivityNotFoundException ex) {
	      ComUtil.showToast(context, context.getResources().getString(R.string.err_not_evernote));
	    } 
	}


}
