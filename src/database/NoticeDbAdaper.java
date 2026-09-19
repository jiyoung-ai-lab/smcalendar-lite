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
 * 1.생성테이블 : notice
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 * 
 */

public class NoticeDbAdaper {

    public static final String KEY_ID 			= "_id";
    public static final String KEY_KEY 			= "key";
    public static final String KEY_LOCALE		= "locale";
    public static final String KEY_APPID 		= "appid";
    public static final String KEY_TITLE	 	= "title";
    public static final String KEY_CONTENT		= "content";
    public static final String KEY_APPLYDATE 	= "applydate";
    public static final String KEY_VERSION 		= "version";
    public static final String KEY_READDATE 	= "readdate";
    public static final String KEY_CONFIRMDATE 	= "confirmdate";
    public static final String KEY_MODIFYDATE 	= "modifydate";
    

    private static final String TAG = "NoticeDbAdaper";
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
        	// 생성
        	try {
        		Log.w(TAG, ">>>>>> DB CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_NOTICE);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_NOTICE);
            
        	} catch (SQLiteException e) { 
        		Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>  DB CREATE ERR!!" );
        	}
        }
        	
        @Override
        //사용자정보테이블 삭제후 생성(신규)
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(	TAG, 
//            		"Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_NOTICE );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NoticeDbAdaper(Context ctx) {
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
    public NoticeDbAdaper open() throws SQLException {

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
    public void beginTransaction() throws SQLException {
    	mDb.beginTransaction();
    }
    
    public void setTransactionSuccessful() throws SQLException {
    	mDb.setTransactionSuccessful();
    }  
    public void endTransaction() throws SQLException {
    	mDb.endTransaction();
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
    public long insertNotice( String key, String locale, String appid, 
    		String title, String content, String applydate, String version ) {

    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_KEY, 			key);
        initialValues.put(KEY_LOCALE, 		locale);
        initialValues.put(KEY_APPID, 		appid);
        initialValues.put(KEY_TITLE, 		title);
        initialValues.put(KEY_CONTENT,		content);
        initialValues.put(KEY_APPLYDATE, 	applydate);
        initialValues.put(KEY_VERSION, 		version);
        initialValues.put(KEY_READDATE, 	"");
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_NOTICE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNotice(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_NOTICE, KEY_ID + "=" + rowId, null) > 0;
    }
    public boolean deleteNoticeKey(String key) {

        return mDb.delete(ComConstant.DATABASE_TABLE_NOTICE, KEY_KEY + "= '" + key + "' ", null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotice( String appid, String locale ) {
    	
    	Cursor mCursor =
    		
             mDb.query(ComConstant.DATABASE_TABLE_NOTICE, new String[] {
        		KEY_ID, KEY_KEY, KEY_APPID, KEY_TITLE, KEY_CONTENT, 
        		KEY_APPLYDATE, KEY_VERSION, KEY_READDATE, KEY_CONFIRMDATE} 
        	  , KEY_APPID 	+ " = '" + appid  + "' and " +
        	  	KEY_LOCALE 	+ " = '" + locale + "' "
        	  , null, null, null, KEY_KEY + " DESC " );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }        
        return mCursor;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchMaxNotice( String appid ) {
    	
    	Cursor mCursor =
    		
             mDb.query(ComConstant.DATABASE_TABLE_NOTICE, new String[] {
        		KEY_KEY} 
        	  , KEY_APPID + " = '" + appid + "' " 
        	  , null, null, null, KEY_KEY + " DESC " );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }        
        return mCursor;
    }
    /*
     * 공지 미확인 건수
     */
    public Cursor fetchNotReadNotice(  String appid, String locale ) {
    	
    	Cursor mCursor =
    		
             mDb.query(ComConstant.DATABASE_TABLE_NOTICE, new String[] {
        		KEY_KEY} 
        	  , KEY_READDATE + " = '' " + " and " +
        	    KEY_APPID 	+ " ='" + appid + "' " + " and " +
        	    KEY_LOCALE 	+ " ='" + locale + "' "
        	  , null, null, null, null );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }        
        return mCursor;
    }    
    /*
     * 공지 확인여부 update
     */
    public boolean updateOtherSchedule(long rowId ) {
    	   	
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_READDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.update(ComConstant.DATABASE_TABLE_NOTICE, initialValues, KEY_ID + "=" + rowId, null) > 0;
    }     
//    /**
//     * Return a Cursor positioned at the note that matches the given rowId
//     * 
//     * @param rowId id of note to retrieve
//     * @return Cursor positioned to matching note, if found
//     * @throws SQLException if note could not be found/retrieved
//     */
//   
//    public Cursor fetchUsermanager(long rowId) throws SQLException {
//
//        Cursor mCursor =
//
//                mDb.query(true, ComConstant.DATABASE_TABLE_NOTICE, new String[] {
//                		KEY_USERID, KEY_USERID, KEY_NAME,KEY_BIRTH, KEY_RELATION, KEY_USERCOLOR, 
//                		KEY_TEL1, KEY_ADDRESS, KEY_MEMO, KEY_DELYN, KEY_DELDATE}  
//                	   ,KEY_USERID + "=" + rowId + " and " +
//                	    KEY_DELYN + " != 'Y'"
//                	  , null, null, null, null, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }
//    


}
