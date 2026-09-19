/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.waveapp.smcalendarlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.util.SmDateUtil;

/**
 * 데이터베이스 생성 및 테이블 생성
 * 1.생성테이블 : newschedule
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 */

public class NewScheduleDbAdaper {

    public static final String KEY_ID			= "_id";
    public static final String KEY_SCHEDULEID	= "scheduleid";
    public static final String KEY_OLDDATE 		= "olddate";
    public static final String KEY_NEWDATE 		= "newdate";
    public static final String KEY_STARTTIME	= "oldstarttime";
    public static final String KEY_ENDTIME 		= "oldendtime";
    public static final String KEY_NEWSTIME		= "newstarttime";
    public static final String KEY_NEWETIME 	= "newendtime";
    public static final String KEY_CONFIRMDATE 	= "confirmDate";
    public static final String KEY_MODIFYDATE 	= "modifyDate";
    
    
    private static final String TAG = "NewScheduleDbAdaper";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, ComConstant.DATABASE_NAME, null, ComConstant.DATABASE_VERSION);
        }

        @Override
        /*
         * Table 일괄생성
         */
        public void onCreate(SQLiteDatabase db) {
        	//스케줄테이블 생성
        	try {
        		//Log.w(TAG, ">>>>>> DB NEWSCHEDULE CREATE Start!! : " + CreateDbAdaper.DATABASE_TABLE_NEWSCHEDULE);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_NEWSCHEDULE);
            
        	} catch (SQLiteException e) { 
        		Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>  DB NEWSCHEDULE CREATE ERR!!" );
        	}
        }
        	
        @Override
        //스케줄테이블 삭제후 생성(신규)
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(	TAG, 
//            		"Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_NEWSCHEDULE );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NewScheduleDbAdaper(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NewScheduleDbAdaper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
    	mDbHelper.close();
    	if ( mDb.isOpen() == true ) {
    		mDb.close();
    	}
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long insertNewSchedule(long scheduleid,  
    			String olddate, String newdate, String oldstarttime, 
    			String oldendtime, String newstarttime, String newendtime ) {
   
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SCHEDULEID, 	scheduleid);   
        initialValues.put(KEY_OLDDATE, 		olddate);      
        initialValues.put(KEY_NEWDATE, 		newdate);      
        initialValues.put(KEY_STARTTIME, 	oldstarttime); 
        initialValues.put(KEY_ENDTIME, 		oldendtime);   
        initialValues.put(KEY_NEWSTIME, 	newstarttime); 
        initialValues.put(KEY_NEWETIME, 	newendtime);   
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_NEWSCHEDULE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNewSchedule(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_NEWSCHEDULE, KEY_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNewSchedule() {
        return mDb.query(ComConstant.DATABASE_TABLE_NEWSCHEDULE, new String[] {
        		KEY_ID,					
        	    KEY_SCHEDULEID,	
        	    KEY_OLDDATE, 		
        	    KEY_NEWDATE, 		
        	    KEY_STARTTIME,	
        	    KEY_ENDTIME, 		
        	    KEY_NEWSTIME,		
        	    KEY_NEWETIME
        		}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
   
    public Cursor fetchNewSchedule(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_NEWSCHEDULE, new String[]  {
                		KEY_ID,					
                	    KEY_SCHEDULEID,	
                	    KEY_OLDDATE, 		
                	    KEY_NEWDATE, 		
                	    KEY_STARTTIME,	
                	    KEY_ENDTIME, 		
                	    KEY_NEWSTIME,		
                	    KEY_NEWETIME
                		}, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>> rowId    " + rowId);        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
   
    public Cursor fetchChangedSchedule(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_NEWSCHEDULE, new String[]  {
                		KEY_ID,					
                	    KEY_SCHEDULEID,	
                	    KEY_OLDDATE, 		
                	    KEY_NEWDATE, 		
                	    KEY_STARTTIME,	
                	    KEY_ENDTIME, 		
                	    KEY_NEWSTIME,		
                	    KEY_NEWETIME
                		}, KEY_SCHEDULEID + "=" + rowId, null,
                        null, null, null, null);
        Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>> KEY_SCHEDULEID    " + rowId);        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    
    public boolean updateNewSchedule(long rowId, long scheduleid,  
			String olddate, String newdate, String oldstarttime, 
			String oldendtime, String newstarttime, String newendtime ) {
    	   	
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SCHEDULEID, 	scheduleid);   
        initialValues.put(KEY_OLDDATE, 		olddate);      
        initialValues.put(KEY_NEWDATE, 		newdate);      
        initialValues.put(KEY_STARTTIME, 	oldstarttime); 
        initialValues.put(KEY_ENDTIME, 		oldendtime);   
        initialValues.put(KEY_NEWSTIME, 	newstarttime); 
        initialValues.put(KEY_NEWETIME, 	newendtime);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.update(ComConstant.DATABASE_TABLE_NEWSCHEDULE, initialValues, KEY_ID + "=" + rowId, null) > 0;
    }
    
}
