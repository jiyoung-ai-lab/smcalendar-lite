package com.waveapp.smcalendarlite.handler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.util.ComUtil;

public class ContactHandler extends SMActivity{
	
	private final Context mCtx;
	
	private int mId;    
	private String mPhoneNumber;    
	private String mName;
	
	String[] strArr = new String[] 
	    	                      {   ContactsContract.CommonDataKinds.Phone._ID
	    							, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
	    							, ContactsContract.CommonDataKinds.Phone.NUMBER };
	
    public ContactHandler(Context ctx) {
        this.mCtx = ctx;
    }

	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
	}

	/***
     * Contact Page 
     * @param Intent data
     * @return 
     */
	public final void onPhoneResult(Intent data ) {
    	Cursor cur = null;
    	
    	//전화번호가져오기
    	if (data != null) {   		
            Uri uri = data.getData();
            if (uri != null) {
                try {
                	cur = mCtx.getContentResolver().query(uri, strArr, null, null, null);
                    if (cur != null && cur.moveToFirst()) {
                    	//가져온id값으로 전화번호가져와서 setting	
                        setId(cur.getInt(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID)));
                        setPhoneNumber(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))));    
                        setName(ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).trim());    
                    }
                } finally {
                    if (cur != null)	cur.close();
                }
            }
    	}		
	}

    
    public final String getPhoneNumberFromContact(int id) {
    	String str = "";
    	Cursor cPhone = null;

    	try {
            cPhone = mCtx.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, strArr
           	     , ContactsContract.CommonDataKinds.Phone._ID + " = " + id, null, null );
            if ( cPhone != null && cPhone.getCount() > 0) {
            	cPhone.moveToFirst();
            	str = ComUtil.setBlank(cPhone.getString(cPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
    	} finally {
    		if (cPhone != null ) cPhone .close();
    	}
    	
        return     str;	
    }
    
    public final String getNameFromPhoneNumber( String phonenumber ) {
    	String str = "";
    	Cursor cPhone = null;
    	try {
    		if ( phonenumber != null && !phonenumber.trim().equals("")) {
	            cPhone = mCtx.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, strArr
	           	     , ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + phonenumber + "' ", null, null );
	            if ( cPhone != null && cPhone.getCount() > 0) {
	            	cPhone.moveToFirst();
	            	str = ComUtil.setBlank(cPhone.getString(cPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim());
	            }
	            
    		}
    	} finally {
    		if (cPhone != null ) cPhone .close();
    	}    	
    	return     str;	
    }

	 /*
	  * Contact data
	  */
	 public void setId(int id) { 
		this.mId 	= id;  	    
	 } 
	 public void setName(String name) { 
		this.mName = name;	    
	 } 	 
	 public void setPhoneNumber(String pnumber) {           
		this.mPhoneNumber 	= pnumber;  	    
	 } 	 
	 
	 public int getId() { 
		 return mId; 	    
	 } 	
	 public String getName() { 
		 return mName; 	    
	 } 
	 public String getPhoneNumber() { 
		 return mPhoneNumber; 	    
	 } 	 
	 public String getPhoneNumberFormat(String pNumber) { 
		 //000-0000-0000
		 return ComUtil.getSplitTel(pNumber).toString()	;    
	 }


}

	
