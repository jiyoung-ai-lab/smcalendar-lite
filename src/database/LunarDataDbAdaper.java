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
import com.waveapp.smcalendarlite.util.ComUtil;

/**
 * 데이터베이스 생성 및 테이블 생성
 * 1.생성테이블 : lunardata
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 */

public class LunarDataDbAdaper {

    public static final String KEY_ID			= "_id";
    public static final String KEY_LEAP 		= "leap";
    public static final String KEY_SOLAR 		= "solar";
    public static final String KEY_LUNAR		= "lunar";
    public static final String KEY_SIXTYYEAR	= "sixtyyear";
    public static final String KEY_SIXTYMONTH	= "sixtymonth";
    public static final String KEY_SIXTYDAY		= "sixtyday";
    public static final String KEY_SOLARTERMS	= "solarterms";
    
    
    private static final String TAG = "LunarDataDbAdaper";
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
        		//Log.w(TAG, ">>>>>> DB lunardata CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_NEWSCHEDULE);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_LUNARDATA);
            
        	} catch (SQLiteException e) { 
        		Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>  DB specialday CREATE ERR!!" );
        	}
        }
        	
        @Override
        //스케줄테이블 삭제후 생성(신규)
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(	TAG, 
//            		"Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_LUNARDATA );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LunarDataDbAdaper(Context ctx) {
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
    public LunarDataDbAdaper open() throws SQLException {
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
    public long insertLunarData(String leap, String solar, String lunar, 
    						String sixtyyear, String sixtymonth, String sixtyday, String solarterms) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LEAP, 		leap);    
        initialValues.put(KEY_SOLAR, 		solar);       
        initialValues.put(KEY_LUNAR,		lunar);
        initialValues.put(KEY_SIXTYYEAR,	sixtyyear);
        initialValues.put(KEY_SIXTYMONTH,	sixtymonth);
        initialValues.put(KEY_SIXTYDAY,		sixtyday);
        initialValues.put(KEY_SOLARTERMS,	solarterms);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_LUNARDATA, null, initialValues);
    }
    /**
     * SolarTerm Update
     */
    public boolean updateSolarTerm(String solar, String solarterms) {

        ContentValues initialValues = new ContentValues();
        
        //initialValues.put(KEY_SOLAR,		solar);
        initialValues.put(KEY_SOLARTERMS,	solarterms);
        
        return mDb.update(ComConstant.DATABASE_TABLE_LUNARDATA, initialValues
        		, KEY_SOLAR + " = '" + solar + "' ", null
        	) > 0;

    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLunarData(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_LUNARDATA, KEY_ID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteBatchLunarData( ) {

        return mDb.delete(ComConstant.DATABASE_TABLE_LUNARDATA, null, null) > 0;
    }
  
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
   
    public Cursor fetchSolarToLunar( String date ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_LUNARDATA, new String[]  {
                		 KEY_ID					
                		,KEY_LEAP 		
                		,KEY_SOLAR 			
                		,KEY_LUNAR
                		,KEY_SIXTYYEAR
                		,KEY_SIXTYMONTH
                		,KEY_SIXTYDAY
                		,KEY_SOLARTERMS
                		}
                      , KEY_SOLAR + " ='" + date + "' "
                      , null, null, null, null, null);
             
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchSolarToLunarPeriod( String fromdate, String todate ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_LUNARDATA, new String[]  {
                		 KEY_ID					
                		,KEY_LEAP 		
                		,KEY_SOLAR 			
                		,KEY_LUNAR
                		,KEY_SIXTYYEAR
                		,KEY_SIXTYMONTH
                		,KEY_SIXTYDAY
                		,KEY_SOLARTERMS
                		}
                      , KEY_SOLAR + " >='" + fromdate + "' " + " and " +
                        KEY_SOLAR + " <='" + todate + "' "
                      , null, null, null, null, null);
             
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    } 
    
    public Cursor fetchLunarToSolar( String date, String leap ) throws SQLException {
    	
    	//양력 : 0, 음력평달:1, 음력윤달:2
    	if ( leap  == null || ( leap != null && leap.trim().equals(""))) {
    		leap ="1" ;
    	}

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_LUNARDATA, new String[]  {
                		 KEY_ID					
                		,KEY_LEAP 		
                		,KEY_SOLAR 			
                		,KEY_LUNAR
                		,KEY_SIXTYYEAR
                		,KEY_SIXTYMONTH
                		,KEY_SIXTYDAY
                		,KEY_SOLARTERMS
                		}
                	  , KEY_LUNAR + " ='" + date + "' " + " and " +
                	  	KEY_LEAP + "  ='" + leap + "' "
                	  , null, null, null, null, null);
        //Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>> date    " + date);        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    /*
     * 양력년도에 대한 60갑자 정보 가져오기 (조회기준은 음력 년도로  -> 조금 애매하지만)
     * -. 음력년도기준 첫번째 날로 조회
     */
    public Cursor fetchSixtyGapSearch( String fromdate ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_LUNARDATA, new String[]  {
//                		 KEY_ID					
//                		,KEY_LEAP 		
//                		,KEY_SOLAR 			
//                		,KEY_LUNAR
                		 KEY_SIXTYYEAR
                		,KEY_SIXTYMONTH
//                		,KEY_SIXTYDAY
                		}
                      , KEY_SOLAR + " ='" + fromdate + "' " 
                      , null, null, null, null, null);
             
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }     
    /*
     * 양력 -> 음력가져오기(음력일자, 윤달여부)
     */
    
    public  static String [] getSolarToLunar( Context ctx, LunarDataDbAdaper dbadapter, String date ) {
 
    	String lunar[] = new String[2];
 		
 		if ( date ==  null || ( date != null && 
 							      ( date.trim().equals("") || date.trim().length() != 8 ))) return lunar;

     	try {
    		  
			Cursor cur = dbadapter.fetchSolarToLunar(date);
			
			lunar[0] = ComUtil.setBlank(cur.getString(
					(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_LUNAR))));
			lunar[1] = ComUtil.setBlank(cur.getString(
					(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_LEAP))));
			
			if ( cur != null ) cur.close();
			
		} catch (SQLException e) {
    		
		} catch (Exception e) {
			//Log.e(this.TAG, e.getMessage());			
		} finally {
		}
		
		return lunar;
    }  
    /*
     * 음력 -> 양력가져오기(양력일자)
     */
    
    public static String getLunarToSolar( Context ctx, LunarDataDbAdaper adapter, String leap, String date ) {
  
    	String solar = "";
    	
    	if ( date ==  null || ( date != null && 
			      ( date.trim().equals("") || date.trim().length() != 8 ))) return solar;

    	try {
			Cursor cur = adapter.fetchLunarToSolar(date, leap);
			
			solar = ComUtil.setBlank(cur.getString(
					(cur.getColumnIndexOrThrow(LunarDataDbAdaper.KEY_SOLAR))));
			
			if ( cur != null ) cur.close();
			
		} catch (Exception e) {
			
			//Log.e(this.TAG, e.getMessage());
			
		} finally {
		}
		
		return solar;
    }     
}
