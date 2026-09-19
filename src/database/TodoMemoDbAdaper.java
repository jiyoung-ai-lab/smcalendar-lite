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
 * 1.생성테이블 : todolist
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 * 
 */

public class TodoMemoDbAdaper {

    public static final String KEY_ID 			= "_id";
    public static final String KEY_MEMO 		= "memo"; 
    public static final String KEY_YEARMONTH 	= "yearmonth";
    public static final String KEY_TERMYN 		= "termyn";
    public static final String KEY_FINISHTERM 	= "finishterm";
    public static final String KEY_FINISH		= "finish";
    public static final String KEY_CONFIRMDATE 	= "confirmdate";
    public static final String KEY_MODIFYDATE 	= "modifydate";   
    public static final String KEY_ALARM 		= "alarm";
    
    private static final String TAG = "TodoMemoDbAdaper";
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
        	//할일메모테이블 생성
        	try {
        		Log.w(TAG, ">>>>>> DB CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_TODOMEMO);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_TODOMEMO);
            
        	} catch (SQLiteException e) { 
        		Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>  DB CREATE ERR!!" );
        	}
        }
        	
        @Override
        //할일메모테이블 삭제후 생성(신규)
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(	TAG, 
//            		"Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_TODOMEMO);
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TodoMemoDbAdaper(Context ctx) {
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
    public TodoMemoDbAdaper open() throws SQLException {

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
    public long insertTodolist(	String memo, String yearmonth, String termyn,  
    							String finishterm, String finish,
    							String alarm, String alarmtime) {
    	
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MEMO, 		memo);
        initialValues.put(KEY_YEARMONTH, 	yearmonth);
        initialValues.put(KEY_TERMYN,		termyn);
        initialValues.put(KEY_FINISHTERM, 	finishterm);
        initialValues.put(KEY_FINISH, 		finish);
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        initialValues.put(KEY_ALARM, 		alarm);    
        
        return mDb.insert(ComConstant.DATABASE_TABLE_TODOMEMO, null, initialValues);
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
    public long insertRestoreTodolist( String [] colname, String [] data  ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        
        int colcnt = colname.length;
        
        for ( int i = 0 ; i < colcnt ; i++ ) {
        	initialValues.put(colname[i], 	data[i]);
        }
        
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_TODOMEMO, null, initialValues);
    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTodolist(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_TODOMEMO, KEY_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllTodolist() {
    	 Cursor mCursor =
             mDb.query(ComConstant.DATABASE_TABLE_TODOMEMO, new String[] {
            		 KEY_ID, KEY_MEMO, KEY_YEARMONTH, KEY_TERMYN
            		,KEY_FINISHTERM, KEY_FINISH, KEY_CONFIRMDATE
            		,KEY_ALARM} 
        	  , null, null, null, null, KEY_FINISH + " ASC, " + KEY_FINISHTERM);
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
   
    public Cursor fetchTodolist(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_TODOMEMO, new String[] {
                		KEY_ID, 
                		KEY_MEMO, 
                		KEY_YEARMONTH, 
                		KEY_TERMYN, 
                		KEY_FINISHTERM, 
                		KEY_FINISH, 
                		KEY_CONFIRMDATE,
                		KEY_ALARM               		
                		} 
                	   ,KEY_ID + "=" + rowId
                	  , null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetchTodolistForPeriod(String startdate, String enddate) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_TODOMEMO, new String[] {
                		KEY_ID, 
                		KEY_MEMO, 
                		KEY_YEARMONTH, 
                		KEY_TERMYN, 
                		KEY_FINISHTERM, 
                		KEY_FINISH, 
                		KEY_CONFIRMDATE,
                		KEY_ALARM               		
                		} 
                	, 	" (( "  + KEY_FINISHTERM + " >= '" + startdate + "' and " +
                		"    "  + KEY_FINISHTERM + " <= '" + enddate  + "' ))  and " +
                				  KEY_FINISH + " != 'Y'"
                	  , null, null, null, null, null);
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
     * -. 최신 알람정보 가져오기 (알람이 등록된 스케줄 조회)
     */
   
    public Cursor fetchAlarmLatest( String fromdate ) throws SQLException {

        Cursor mCursor =

		            mDb.query(true, ComConstant.DATABASE_TABLE_TODOMEMO, new String[] {
		            		KEY_ID, 
		            		KEY_MEMO, 
		            		KEY_YEARMONTH, 
		            		KEY_TERMYN, 
		            		KEY_FINISHTERM, 
		            		KEY_FINISH, 
		            		KEY_CONFIRMDATE,
		            		KEY_ALARM               		
		            		}
                	  , 	"    "  + KEY_ALARM	    + " >= '0'" 		  + " and " +
                	    	"    "  + KEY_FINISHTERM + " >= '" + fromdate + "' and " +
                	  			KEY_FINISH + " != 'Y'"
                	  , null, null, null, KEY_FINISHTERM , null);
        
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
    
    public boolean updateTodolist(long rowId, String memo, String yearmonth, String termyn,  
									String finishterm, String finish,
//	    							String repeat, String repeatdate,
	    							String alarm, String alarmtime ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_MEMO, 		memo);
        initialValues.put(KEY_YEARMONTH, 	yearmonth);
        initialValues.put(KEY_TERMYN,		termyn);
        initialValues.put(KEY_FINISHTERM, 	finishterm);
        initialValues.put(KEY_FINISH, 		finish);
        initialValues.put(KEY_MODIFYDATE, 	today);
        initialValues.put(KEY_ALARM, 		alarm);
        
        return mDb.update(ComConstant.DATABASE_TABLE_TODOMEMO, initialValues, KEY_ID + "=" + rowId, null) > 0;
    }
    
    public boolean updateFinishTodo(long rowId, String finish ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_FINISH, 		finish);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.update(ComConstant.DATABASE_TABLE_TODOMEMO, initialValues, KEY_ID + "=" + rowId, null) > 0;
    }  
}
