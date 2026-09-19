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

import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.SmDateUtil;
/**
 * 데이터베이스 생성 및 테이블 생성
 * 1.생성테이블 : usermanager
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 * <<변경이력>>
 * 2011.05.20 삭제일,연락처,사용자색깔항목추가
 * 
 */

public class UsermanagerDbAdaper {

    public static final String KEY_USERID 		= "_id";
    public static final String KEY_NAME 		= "name";
    public static final String KEY_BIRTH 		= "birthdate";
    public static final String KEY_RELATION 	= "relation";
    public static final String KEY_USERCOLOR	= "usercolor";
    public static final String KEY_ADDRESS 		= "address";
    public static final String KEY_TEL1 		= "tel1";
    public static final String KEY_MEMO 		= "memo";    
    public static final String KEY_DELYN 		= "delyn";
    public static final String KEY_DELDATE 		= "deldate";
    public static final String KEY_CONFIRMDATE 	= "confirmdate";
    public static final String KEY_MODIFYDATE 	= "modifydate";
    

    private static final String TAG = "UsermanagerDbAdaper";
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
        	//사용자정보테이블 생성
        	try {
        		Log.w(TAG, ">>>>>> DB CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_USERMANAGER);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_USERMANAGER);
            
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
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_USERMANAGER );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public UsermanagerDbAdaper(Context ctx) {
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
    public UsermanagerDbAdaper open() throws SQLException {

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
    public long insertUsermanager(	String name, String birth, String relation, String usercolor, 
    								String tel1, String address, String memo  ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, 	name);
        initialValues.put(KEY_BIRTH, 	birth);
        initialValues.put(KEY_RELATION, relation);
        initialValues.put(KEY_USERCOLOR,usercolor);
        initialValues.put(KEY_TEL1, 	tel1);
        initialValues.put(KEY_ADDRESS, 	address);
        initialValues.put(KEY_MEMO, 	memo);
        initialValues.put(KEY_DELYN, 	"");
        initialValues.put(KEY_DELDATE, 	"");
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_USERMANAGER, null, initialValues);
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
    public long insertRestoreUsermanager( String [] colname, String [] data  ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        
        int colcnt = colname.length;
        
        for ( int i = 0 ; i < colcnt ; i++ ) {
        	initialValues.put(colname[i], 	data[i]);
        }
        
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_USERMANAGER, null, initialValues);
    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteUsermanagerComplete(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_USERMANAGER, KEY_USERID + "=" + rowId, null) > 0;
    }
    /*
     * 사용자 삭제일자,구분만 update ( 종속정보 보존을 위한 정책 )
     */
    public boolean deleteUsermanager(long rowId) {
        	   	
        	//현재일자 시간 가져오기
        	String today = SmDateUtil.getToday();
            ContentValues initialValues = new ContentValues();

            initialValues.put(KEY_DELYN, 	"Y".trim());
            initialValues.put(KEY_DELDATE, 	today);
            
            return mDb.update(ComConstant.DATABASE_TABLE_USERMANAGER, initialValues, KEY_USERID + "=" + rowId, null) > 0;

    }
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllUsermanger() {
    	 Cursor mCursor =

             mDb.query(ComConstant.DATABASE_TABLE_USERMANAGER, new String[] {
        		KEY_USERID, KEY_NAME, KEY_BIRTH, KEY_RELATION, KEY_USERCOLOR, 
        		KEY_TEL1, KEY_ADDRESS, KEY_MEMO, KEY_DELYN, KEY_DELDATE} 
        	  , KEY_DELYN + " != 'Y'"
        	  , null, null, null, KEY_RELATION);
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
   
    public Cursor fetchUsermanager(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_USERMANAGER, new String[] {
                		KEY_USERID, KEY_USERID, KEY_NAME,KEY_BIRTH, KEY_RELATION, KEY_USERCOLOR, 
                		KEY_TEL1, KEY_ADDRESS, KEY_MEMO, KEY_DELYN, KEY_DELDATE}  
                	   ,KEY_USERID + "=" + rowId + " and " +
                	    KEY_DELYN + " != 'Y'"
                	  , null, null, null, null, null);
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
    
    public boolean updateUsermanager(long rowId, String name, String birth, String relation, String usercolor, 
									String tel1, String address, String memo ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, 		name);
        initialValues.put(KEY_BIRTH, 		birth);
        initialValues.put(KEY_RELATION, 	relation);
        initialValues.put(KEY_USERCOLOR,	usercolor);
        initialValues.put(KEY_TEL1, 		tel1);
        initialValues.put(KEY_ADDRESS, 		address);
        initialValues.put(KEY_MEMO, 		memo);
        initialValues.put(KEY_DELYN, 		"");
        initialValues.put(KEY_DELDATE, 		"");
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.update(ComConstant.DATABASE_TABLE_USERMANAGER, initialValues, KEY_USERID + "=" + rowId, null) > 0;
    }
    /*
     * 사용자이름 가져오기
     */    
    public static String getUserName(Context ctx, Long userid ) {
    	String rname = "";

    	try { 
    		UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(ctx);
    		mDbUser.open();
			Cursor cur = mDbUser.fetchUsermanager(userid); 
			rname = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_NAME)));
			if ( cur != null ) cur.close();
			mDbUser.close();
		} catch (Exception e) {
			//Log.e(this.TAG, e.getMessage());
		} finally {
		}
		return rname;
    }
    public static String getUserName(Context ctx, UsermanagerDbAdaper mDbUser, Long userid ) {
    	String rname = "";

    	try {     		
			Cursor cur = mDbUser.fetchUsermanager(userid); 
			rname = ComUtil.setBlank(cur.getString(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_NAME)));
			if ( cur != null ) cur.close();
		} catch (Exception e) {
			//Log.e(this.TAG, e.getMessage());
		} finally {
		}
		return rname;
    }    
    /*
     * 사용자 색상정보 가져오기
     */ 

/*
    public static int getUserColor(Context ctx, Long userid ) {
    	
    	int color = 0;
		
    	try {    	
    		UsermanagerDbAdaper mDbUser = new UsermanagerDbAdaper(ctx);
    		mDbUser.open();
			Cursor cur = mDbUser.fetchUsermanager(userid); 
			color = ComUtil.stringToInt( 
						ComUtil.setBlank(cur.getString(
							(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_USERCOLOR)))));
			if ( cur != null ) cur.close();
			mDbUser.close();
			
    	} catch (Exception e) {
			//Log.e(mCtx.get, e.getMessage());
		} finally {
		}
		
		return color;
    }
    */    
    public static int getUserColor(Context ctx, UsermanagerDbAdaper mDbUser, Long userid ) {
    	
    	int color = 0;
    	Cursor cur = null;
		
    	try {    	
    		
			cur = mDbUser.fetchUsermanager(userid); 
			color = ComUtil.stringToInt( 
						ComUtil.setBlank(cur.getString(
							(cur.getColumnIndexOrThrow(UsermanagerDbAdaper.KEY_USERCOLOR)))));
			
			if ( color == 0 ) color = ctx.getResources().getColor(R.color.gray);
			
			if ( cur != null ) {
				cur.close();
				cur = null;
			}
			
			
    	} catch (Exception e) {
    		if ( cur != null ) {
				cur.close();
				cur = null;
			}
		} finally {
		}
		
		return color;
    }        
}
