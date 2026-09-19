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
 * 1.생성테이블 : specialday
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 */

public class SpecialDayDbAdaper {

    public static final String KEY_ID			= "_id";
    public static final String KEY_LOCALE 		= "locale";
    public static final String KEY_GUBUN 		= "gubun";
    public static final String KEY_EVENT 		= "event";
    public static final String KEY_HOLIDAYYN	= "holidayyn";
    public static final String KEY_NAME 		= "name";
    public static final String KEY_SUBNAME 		= "subname";
    public static final String KEY_REPEATYN 	= "repeatyn";
    public static final String KEY_YEAR			= "year";
    public static final String KEY_MONTHDAY		= "monthday";
    public static final String KEY_LEAP 		= "leap";
    //public static final String KEY_SOLARDATE	= "solardate";
    public static final String KEY_MEMO 		= "memo";
    public static final String KEY_DELYN 		= "delyn";
    public static final String KEY_DELDATE 		= "deldate";
    public static final String KEY_CONFIRMDATE 	= "confirmDate";
    public static final String KEY_MODIFYDATE 	= "modifyDate";    
    public static final String KEY_USERGROUP 	= "usergroup";
    public static final String KEY_ALARM 		= "alarm";
    
    
    private static final String TAG = "SpecialDayDbAdaper";
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
        		//Log.w(TAG, ">>>>>> DB specialday CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_NEWSCHEDULE);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_SPECIALDAY);
            
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
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_SPECIALDAY );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public SpecialDayDbAdaper(Context ctx) {
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
    public SpecialDayDbAdaper open() throws SQLException {
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
    public long insertSpecialDay(String locale, String gubun, String event, String holidayyn, String name, 
    			String subname, String repeatyn, String year, String monthday, String leap,  
    			String memo, String delyn, String deldate, String usergroup, String alarm ) {
   
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LOCALE, 		locale);    
        initialValues.put(KEY_GUBUN, 		gubun);  
        initialValues.put(KEY_EVENT, 		event);  
        initialValues.put(KEY_HOLIDAYYN,	holidayyn); 
        initialValues.put(KEY_NAME, 		name);      
        initialValues.put(KEY_SUBNAME, 		subname);     
        initialValues.put(KEY_REPEATYN, 	repeatyn); 
        initialValues.put(KEY_YEAR, 		year);
        initialValues.put(KEY_MONTHDAY, 	monthday);
        initialValues.put(KEY_LEAP, 		leap);  
        //initialValues.put(KEY_SOLARDATE, 	solardate);  
        initialValues.put(KEY_MEMO, 		memo);      
        initialValues.put(KEY_DELYN, 		delyn);       
        initialValues.put(KEY_DELDATE, 		deldate); 
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        initialValues.put(KEY_USERGROUP, 	usergroup);
        initialValues.put(KEY_ALARM, 		alarm);       
        
        return mDb.insert(ComConstant.DATABASE_TABLE_SPECIALDAY, null, initialValues);
    }
    
    /*
     * 기념일 db는 국공일을 포함하고 있어 상충됨(별도 db로 분리필요)
     * id -> 초기화
     */
    public long insertRestoreSpecialDay( String [] colname, String [] data  ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        
        int colcnt = colname.length;
        
        for ( int i = 0 ; i < colcnt ; i++ ) {
        	if ( colname[i] != null && !colname[i].equals(KEY_ID)) {
        		initialValues.put(colname[i], 	data[i]);
        	}        	
        }
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_SPECIALDAY, null, initialValues);
    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    /*
     * 특정 기념일 삭제
     */    
    public boolean deleteSpecialDay(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_SPECIALDAY, KEY_ID + "=" + rowId, null) > 0;
    }
    /*
     * 사용자등록 기념일 일괄삭제
     */
    public boolean deleteSpecialDayForRestore( String gubun) {

        return mDb.delete(ComConstant.DATABASE_TABLE_SPECIALDAY, 
        		KEY_GUBUN + "='" + gubun + "'" , null) > 0;
    }   
    /*
     * 일괄등록된 국가공휴일 정보 삭제
     */
    public boolean deleteBatchSpecialDay(String memo) {

        return mDb.delete(ComConstant.DATABASE_TABLE_SPECIALDAY
        		,	
        			KEY_MEMO  	+ "= '" + memo + "' "
        		, 	null) > 0;
    }
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllSpecialDayForType(String gubun, String sort ) {
    	
    	Cursor mCursor =
    		mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[] {
        		KEY_ID,					
        		KEY_LOCALE, 		
        		KEY_GUBUN, 	
        		KEY_EVENT, 
        		KEY_HOLIDAYYN,
        		KEY_SUBNAME, 	
        		KEY_NAME, 			
        		KEY_REPEATYN, 
        		KEY_YEAR,
        		KEY_MONTHDAY,
        		KEY_LEAP, 	
        		//KEY_SOLARDATE, 	
        		KEY_MEMO, 			
        		KEY_DELYN, 			
        		KEY_DELDATE,
        		KEY_USERGROUP,
        		KEY_ALARM      		
    		}
//  		  	  , KEY_LOCALE + " = '" + locale + "' " +  " and " +
  		      , KEY_GUBUN  + " = '" + gubun + "' " 
  		    , null, null, null, KEY_MONTHDAY + sort , null);
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
    public Cursor fetchAllSpecialDayForYearType(String gubun, String locale, String yeartype, String sort ) {
    	
    	Cursor mCursor =
    		mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[] {
        		KEY_ID,					
        		KEY_LOCALE, 		
        		KEY_GUBUN, 	
        		KEY_EVENT, 
        		KEY_HOLIDAYYN,
        		KEY_SUBNAME, 	
        		KEY_NAME, 			
        		KEY_REPEATYN, 
        		KEY_YEAR,
        		KEY_MONTHDAY,
        		KEY_LEAP, 	
        		//KEY_SOLARDATE, 	
        		KEY_MEMO, 			
        		KEY_DELYN, 			
        		KEY_DELDATE,
        		KEY_USERGROUP,
        		KEY_ALARM 
    		}
//  		  	  , KEY_LOCALE + " = '" + locale + "' " +  " and " +
  		      , KEY_MEMO  + " = '" + yeartype + "' "  + " and " +
  		      	KEY_LOCALE + " = '" + locale + "' "  + " and " +
  		        KEY_GUBUN + " = '" + gubun + "' " 
  		    , null, null, null, KEY_MONTHDAY + sort , null);
    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;    	
    }
  
    /**
     * 기념일 음력정보 모두 조회
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllSpecialDayLunar(String gubun) {
    	Cursor mCursor =
    		mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[] {
        		KEY_ID,					
        		KEY_LOCALE, 		
        		KEY_GUBUN, 	
        		KEY_EVENT, 
        		KEY_HOLIDAYYN,
        		KEY_SUBNAME, 	
        		KEY_NAME, 			
        		KEY_REPEATYN, 
        		KEY_YEAR,
        		KEY_MONTHDAY,
        		KEY_LEAP, 	
        		//KEY_SOLARDATE, 	
        		KEY_MEMO, 			
        		KEY_DELYN, 			
        		KEY_DELDATE,
        		KEY_USERGROUP,
        		KEY_ALARM  
        		}
//    		  , KEY_LOCALE + " = '" + locale + "' " +  " and " +
    		  , KEY_GUBUN  + " = '" + gubun + "' " + " and " +
    		  	KEY_LEAP   + " IN ( '1', '2' ) " 
    		  , null, null, null, KEY_MONTHDAY, null);
    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;    	
    }
    /**
     * 기념일,공휴일조회 (rowId)
     * 
     * @param rowId
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
   
    public Cursor fetchSpecialDay(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
                		KEY_ID,					
                		KEY_LOCALE, 		
                		KEY_GUBUN, 	
                		KEY_EVENT,
                		KEY_HOLIDAYYN,
                		KEY_SUBNAME, 	
                		KEY_NAME, 			
                		KEY_REPEATYN, 
                		KEY_YEAR,
                		KEY_MONTHDAY,
                		KEY_LEAP, 	
                		///KEY_SOLARDATE, 	
                		KEY_MEMO, 			
                		KEY_DELYN, 			
                		KEY_DELDATE,
                		KEY_USERGROUP,
                		KEY_ALARM  
                		}, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>> rowId    " + rowId);        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * 날짜별 기념일/공휴일(양력)
     * @param KEY_MONTHDAY or KEY_YEAR KEY_MONTHDAY
     */
   
    public Cursor fetchSpecialDayForSolar( String year, String sDate) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
                		KEY_ID,					
                		KEY_LOCALE, 		
                		KEY_GUBUN, 	
                		KEY_EVENT,
                		KEY_HOLIDAYYN,
                		KEY_SUBNAME, 	
                		KEY_NAME, 			
                		KEY_REPEATYN, 
                		KEY_YEAR,
                		KEY_MONTHDAY,
                		KEY_LEAP, 	
                		//KEY_SOLARDATE, 	
                		KEY_MEMO, 			
                		KEY_DELYN, 			
                		KEY_DELDATE,
                		KEY_USERGROUP,
                		KEY_ALARM 
        				}
            	,    KEY_LEAP      + " in ( '0', '')  " +  " and " +
            "(( " + KEY_REPEATYN   + " = 'Y' "  +  " and " +
                    KEY_MONTHDAY   + " = '" + sDate +  "' ) " +  " or " +
            "( " +  KEY_YEAR	   + " = '" + year  +  "' and " +
                    KEY_MONTHDAY   + " = '" + sDate + "' )) " 
            		
            	, null, null, null, KEY_MONTHDAY + ", " + KEY_HOLIDAYYN + " DESC " , null);
           
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }    
    /**
     * 날짜별 기념일/공휴일(음력) - 음력날짜 기준으로 조회
     * @param KEY_MONTHDAY or KEY_YEAR KEY_MONTHDAY
     * --> 임시조치 : 윤달의 경우 보이지 않는 경우 발생 (음력은 모두허용)
     * --> 20120507 윤달일치건만 처리.(평달처리 안함)
     */
   
    public Cursor fetchSpecialDayForLunar(String year, String monthday, String leap ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
                		KEY_ID,					
                		KEY_LOCALE, 		
                		KEY_GUBUN,
                		KEY_EVENT,
                		KEY_HOLIDAYYN,
                		KEY_SUBNAME, 	
                		KEY_NAME, 			
                		KEY_REPEATYN, 
                		KEY_YEAR,
                		KEY_MONTHDAY,
                		KEY_LEAP, 	
                		//KEY_SOLARDATE, 	
                		KEY_MEMO, 			
                		KEY_DELYN, 			
                		KEY_DELDATE,
                		KEY_USERGROUP,
                		KEY_ALARM  
        				}
//            	,   KEY_LOCALE + " = '" + locale + "' " +  " and " +
//            	,	KEY_LEAP   	   + " = '" + leap +  "'  " +  " and " +
            	,	KEY_LEAP   	   + " in ( '1' , '2' ) " +  " and " +
            "(( " + KEY_REPEATYN   + " = 'Y' "  +  " and " +
                    KEY_MONTHDAY   + " = '" + monthday +  "' ) " +  " or " +
            "( " +  KEY_YEAR	   + " = '" + year  +  "' and " +
                    KEY_MONTHDAY   + " = '" + monthday + "' )) " 
            		
            	, null, null, null, null, null);
           
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    } 
    /**
     * 양력기념일 - 달력용(기간조회)
     * -.음력은 별도처리... 양력일이 년도에 따라 동적이라 아래 sql문은 불가함
     * @param locale, gubun, year, fmonthday, tmonthday
     */
   
    public Cursor fetchSpecialDayForCal( String gubun,  
    					String year, String fmonthday, String tmonthday ) throws SQLException {

    	
        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
                		KEY_ID,					
                		KEY_LOCALE, 		
                		KEY_GUBUN, 	
                		KEY_EVENT,
                		KEY_HOLIDAYYN,
                		KEY_SUBNAME, 	
                		KEY_NAME, 			
                		KEY_REPEATYN, 
                		KEY_YEAR,
                		KEY_MONTHDAY,
                		KEY_LEAP, 	
                		//KEY_SOLARDATE, 	
                		KEY_MEMO, 			
                		KEY_DELYN, 			
                		KEY_DELDATE,
                		KEY_USERGROUP,
                		KEY_ALARM 
        				}
//            	,   KEY_LOCALE 	   + " = '" + locale +   "' "  		+  " and " +
            	,	KEY_GUBUN 	   + " = '" + gubun +    "' "  		+  " and " +
            		KEY_LEAP   	   + " = '0' " 						+  " and " +
            //양력
            "(( " + KEY_REPEATYN   + " = 'Y' "  +  		 "  and " +
            "   " + KEY_MONTHDAY + " >= '" + fmonthday + "' and " +
	  		"   " + KEY_MONTHDAY + " <= '" + tmonthday + "' ) " 	+ " or " +
	  		
            " ( " + KEY_YEAR	 + "  = '" + year  +  "' and " +
                    KEY_MONTHDAY + " >= '" + fmonthday + "' and " +
                    KEY_MONTHDAY + " <= '" + tmonthday +"' ) )" 
                  
                    
            	, null, null, null, KEY_MONTHDAY, null);
           
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }     
    /**
     * 일괄반영된 국공일 - 양력만(음력은 없음)
     * -.휴일만 가져옴 ( locale , gubun="B", thisdate )
     * @param year, fromdate, todate
     */
    
    public Cursor fetchMonthHoliDay( String locale, String year, String fromdate, String todate ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
                		KEY_ID,					
                		KEY_LOCALE, 		
                		KEY_GUBUN, 
                		KEY_EVENT,
                		KEY_HOLIDAYYN,
                		KEY_SUBNAME, 	
                		KEY_NAME, 			
                		KEY_REPEATYN, 
                		KEY_YEAR,
                		KEY_MONTHDAY,
                		KEY_LEAP, 	
                		//KEY_SOLARDATE, 	
                		KEY_MEMO, 			
                		KEY_DELYN, 			
                		KEY_DELDATE,
                		KEY_USERGROUP,
                		KEY_ALARM  
                		}
                	,   KEY_LOCALE + " = '" + locale + "' " +  " and " +
                		KEY_GUBUN  + " = 'B' " 	+ " and " +
                "(( " + KEY_REPEATYN   + " = 'Y' "  +  " and " +
                        KEY_MONTHDAY   + " >= '" + fromdate +  "' and " +
                		KEY_MONTHDAY   + " <= '" + todate +  "' ) " +  " or " +
                "( " +  KEY_YEAR	   + "  = '" + year 		+  "' and " +
                        KEY_MONTHDAY   + " >= '" + fromdate 	+  "' and " +
                		KEY_MONTHDAY   + " <= '" + todate 	+  "' ) )"                 		
                		
                	, null, null, null, KEY_MONTHDAY + ", " + KEY_HOLIDAYYN + " DESC " , null);
        
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
     * -. 양력 : 최신 알람정보 가져오기 (알람이 등록된 기념일 조회)
     */
   
    public Cursor fetchAlarmForSolar( String gubun, String year, String frommonthday ) throws SQLException {

    	Cursor mCursor =    	
			        mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
			        		KEY_ID,					
			        		KEY_LOCALE, 		
			        		KEY_GUBUN, 	
			        		KEY_EVENT,
			        		KEY_HOLIDAYYN,
			        		KEY_SUBNAME, 	
			        		KEY_NAME, 			
			        		KEY_REPEATYN, 
			        		KEY_YEAR,
			        		KEY_MONTHDAY,
			        		KEY_LEAP, 	
			        		//KEY_SOLARDATE, 	
			        		KEY_MEMO, 			
			        		KEY_DELYN, 			
			        		KEY_DELDATE,
			        		KEY_USERGROUP,
			        		KEY_ALARM 
							}
			    	,	KEY_GUBUN 	   + "  = '" + gubun +    "' "  		+  " and " +
			    		KEY_ALARM	   + " >= '0' "							+  " and " +
			    		KEY_LEAP   	   + "  = '0' " 						+  " and " +
			    		KEY_DELYN 	   + " != 'Y'"							+  " and " +
			    //양력
			    	"(( " + KEY_REPEATYN   	+ " = 'Y' "  				+ "  and " +
					"   " + KEY_MONTHDAY 	+ " = '" + frommonthday + "' ) " 	+ " or " +
					
			    " ( 	" + KEY_REPEATYN   	+ " != 'Y' "  				+ "  and " +
			    	"   " + KEY_YEAR	 + "  = '" + year  +  "' and " +
			    	"   " + KEY_MONTHDAY + " = '" + frommonthday + "' ))" 			          
			            
			    	, null, null, null, KEY_MONTHDAY, null);

        
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
     * -. 음력 : 최신 알람정보 가져오기 (알람이 등록된 기념일 조회)
     */
   
    public Cursor fetchAlarmForLunar( String gubun, String year, String frommonthday, String leap ) throws SQLException {

    	Cursor mCursor =    	
			        mDb.query(true, ComConstant.DATABASE_TABLE_SPECIALDAY, new String[]  {
			        		KEY_ID,					
			        		KEY_LOCALE, 		
			        		KEY_GUBUN, 	
			        		KEY_EVENT,
			        		KEY_HOLIDAYYN,
			        		KEY_SUBNAME, 	
			        		KEY_NAME, 			
			        		KEY_REPEATYN, 
			        		KEY_YEAR,
			        		KEY_MONTHDAY,
			        		KEY_LEAP, 	
			        		//KEY_SOLARDATE, 	
			        		KEY_MEMO, 			
			        		KEY_DELYN, 			
			        		KEY_DELDATE,
			        		KEY_USERGROUP,
			        		KEY_ALARM 
							}
			    	,	KEY_GUBUN 	   + "  = '" + gubun +    "' "  		+  " and " +
			    		KEY_ALARM	   + " >= '0' "							+  " and " +
			    		KEY_LEAP   	   + "  = '" + leap +    "' "  			+  " and " +
			    		KEY_DELYN 	   + " != 'Y'"							+  " and " +
			    //음력
			    	"(( " + KEY_REPEATYN   	+ " = 'Y' "  				+ "  and " +
					"   " + KEY_MONTHDAY 	+ " = '" + frommonthday + "' ) " 	+ " or " +
					
			    " ( 	" + KEY_REPEATYN   	+ " != 'Y' "  				+ "  and " +
			    	"   " + KEY_YEAR	 + "  = '" + year  +  "' and " +
			    	"   " + KEY_MONTHDAY + " = '" + frommonthday + "' ))" 			          
			            
			    	, null, null, null, KEY_MONTHDAY, null);

        
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
    
    public boolean updateSpecialday(long rowId, String locale, String gubun, String event, String holidayyn, String name, 
    		String subname, String repeatyn, String year, String monthday, String leap,  
			String memo, String delyn, String deldate, String usergroup, String alarm) {
    	   	
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LOCALE, 		locale);    
        initialValues.put(KEY_GUBUN, 		gubun); 
        initialValues.put(KEY_EVENT, 		event);        
        initialValues.put(KEY_HOLIDAYYN,	holidayyn); 
        initialValues.put(KEY_NAME, 		name);      
        initialValues.put(KEY_SUBNAME, 		subname);     
        initialValues.put(KEY_REPEATYN, 	repeatyn); 
        initialValues.put(KEY_YEAR, 		year);
        initialValues.put(KEY_MONTHDAY, 	monthday);
        initialValues.put(KEY_LEAP, 		leap);  
        //initialValues.put(KEY_SOLARDATE, 	solardate);  
        initialValues.put(KEY_MEMO, 		memo);    
        initialValues.put(KEY_DELYN, 		delyn);       
        initialValues.put(KEY_DELDATE, 		deldate);
        initialValues.put(KEY_MODIFYDATE, 	today);        
        initialValues.put(KEY_USERGROUP, 	usergroup);
        initialValues.put(KEY_ALARM, 		alarm);
        
        return mDb.update(ComConstant.DATABASE_TABLE_SPECIALDAY, initialValues, KEY_ID + "=" + rowId, null) > 0;
    }
    
}
