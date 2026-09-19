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

/**
 * 데이터베이스 생성 및 테이블 생성
 * 1.생성테이블 : installcheck
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 */

public class InstallCheckDbAdaper {

    public static final String KEY_ID			= "_id";
    public static final String KEY_COMPLETE		= "complete";
    
    
    private static final String TAG = "InstallCheckDbAdaper";
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
        	
        	try {
        		//Log.w(TAG, ">>>>>> DB lunardata CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_NEWSCHEDULE);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_INSTALLCHECK);
            
        	} catch (SQLiteException e) { 
        		Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>  DB installcomplete CREATE ERR!!" );
        	}
        }
        	
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(	TAG, 
//            		"Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_INSTALLCHECK );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public InstallCheckDbAdaper(Context ctx) {
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
    public InstallCheckDbAdaper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
    	mDbHelper.close();
    	if ( mDb.isOpen() == true ) {
    		mDb.close();
    	}
    	mDb.releaseMemory();
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
    public long insertInstallCheckComplete( ) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_COMPLETE, 	"Y");
        
        return mDb.insert(ComConstant.DATABASE_TABLE_INSTALLCHECK, null, initialValues);
    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteInstallCheck(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_INSTALLCHECK, KEY_ID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteBatchLunarData( ) {

        return mDb.delete(ComConstant.DATABASE_TABLE_INSTALLCHECK, null, null) > 0;
    }
  
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
   
    public boolean fetchInstallCheck(  )  {
    	
    	Cursor mCursor = null;
    	//table 존재여부 체크 (없으면 false)
    	mCursor = mDb.rawQuery(" SELECT name FROM sqlite_master " + 
    						   " WHERE type='table' AND name='" + ComConstant.DATABASE_TABLE_INSTALLCHECK + "' "
    						   , null);
        if (mCursor != null) {
        	mCursor.moveToFirst();
        	if ( mCursor.getCount() <= 0 ) {
        		mCursor.close();
        		return false;
        	} else {
        		mCursor.close();
        	}
        }  else {
        	return false;
        } 

        
    	//table이 있는경우 데이터 존재시 true, 없는 경우 false    	
    	try {
	        mCursor =
	
	                mDb.query(true, ComConstant.DATABASE_TABLE_INSTALLCHECK, new String[]  {
	                		 KEY_ID					
	                		,KEY_COMPLETE 
	                		}
	                      , KEY_COMPLETE + "='Y'"
	                      , null, null, null, null, null);
	             
	        if (mCursor != null) {
	        	mCursor.moveToFirst();
	        	if ( mCursor.getCount()  > 0 ) {
	        		mCursor.close();
	        		return true;
	        	} else {
	        		mCursor.close();
	        		return false;
	        	}
	        } else {
	        	return false;
	        }
    	} catch (SQLException e) {   
    		//오류가 생겨도 정상처리하기 위해
    		if ( mCursor != null && !mCursor.isClosed()) mCursor.close();
    		return true;
		}
    	
    }
}
