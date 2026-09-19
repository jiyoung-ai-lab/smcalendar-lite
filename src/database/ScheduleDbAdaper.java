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
 * 1.생성테이블 : schedule
 * 2.데이터핸들링 : insert,delete,update,fetch(all,select)
 * 3.데이터베이스삭제
 */

public class ScheduleDbAdaper {

    public static final String KEY_SCHDULEID= "_id";
    public static final String KEY_NAME 	= "name";
    public static final String KEY_USERID 	= "userid";
    public static final String KEY_CYCLE 	= "cycle";
    public static final String KEY_SUN 		= "sunday";
    public static final String KEY_MON 		= "monday";
    public static final String KEY_TUE 		= "tuesday";
    public static final String KEY_WEN 		= "wednesday";
    public static final String KEY_THU		= "thursday";
    public static final String KEY_FRI 		= "friday";
    public static final String KEY_SAT 		= "saturday";
    public static final String KEY_STARTDATE= "startdate";
    public static final String KEY_ENDDATE 	= "enddate";
    public static final String KEY_ALLDAYYN = "alldayyn";
    public static final String KEY_STARTTIME= "starttime";
    public static final String KEY_ENDTIME 	= "endtime";
    public static final String KEY_ALARM 	= "alarm";
    public static final String KEY_TEL1 	= "tel1";
    public static final String KEY_TEL2 	= "tel2";
    public static final String KEY_COST 	= "cost";
    public static final String KEY_MEMO 	= "memo";   
    public static final String KEY_OTHERKIND 	= "otherkind";
    public static final String KEY_OTHERID		= "otherid";  
    public static final String KEY_DELYN 		= "delyn";
    public static final String KEY_DELDATE 		= "deldate";    
    public static final String KEY_CONFIRMDATE 	= "confirmDate";
    public static final String KEY_MODIFYDATE 	= "modifyDate";
    public static final String KEY_REPEATDATE 	= "repeatdate";    
    public static final String KEY_ALARM2 		= "alarm2";
    
    public static final String KEY_CNT 			= "cnt";
    
    
    private static final String TAG = "ScheduleDbAdaper";
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
        		Log.w(TAG, ">>>>>> DB SCHEDULE CREATE Start!! : " + CreateDbAdaper.DATABASE_CREATE_SCHEDULE);
	            db.execSQL(CreateDbAdaper.DATABASE_CREATE_SCHEDULE);
            
        	} catch (SQLiteException e) { 
        		Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>  DB SCHEDULE CREATE ERR!!" );
        	}
        }
        	
        @Override
        //스케줄테이블 삭제후 생성(신규)
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(	TAG, 
//            		"Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            db.execSQL("DROP TABLE IF EXISTS "+ ComConstant.DATABASE_TABLE_SCHEDULE );
//            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ScheduleDbAdaper(Context ctx) {
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
    public ScheduleDbAdaper open() throws SQLException {
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
    public long insertSchedule(String name, Long userid, String cycle,
    			String sun, String mon, String thu, String wen, String thr, String fri, String sat,  
    			String startdate, String enddate, String alldayyn, String starttime, String endtime, 
    			String alarm, String tel1, String tel2, long cost, String memo, 
    			String repeatdate, String alarm2 ) {
   
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, 	name);
        initialValues.put(KEY_USERID, 	userid);
        initialValues.put(KEY_CYCLE, 	cycle);
        initialValues.put(KEY_MON, 		mon);
        initialValues.put(KEY_TUE, 		thu);
        initialValues.put(KEY_WEN, 		wen);
        initialValues.put(KEY_THU, 		thr);
        initialValues.put(KEY_FRI, 		fri);
        initialValues.put(KEY_SAT, 		sat);
        initialValues.put(KEY_SUN, 		sun);
        initialValues.put(KEY_STARTDATE,startdate);
        initialValues.put(KEY_ENDDATE, 	enddate);
        initialValues.put(KEY_ALLDAYYN, alldayyn);
        initialValues.put(KEY_STARTTIME,starttime);
        initialValues.put(KEY_ENDTIME, 	endtime);
        initialValues.put(KEY_ALARM, 	alarm);
        initialValues.put(KEY_TEL1, 	tel1);
        initialValues.put(KEY_TEL2, 	tel2);
        initialValues.put(KEY_COST, 	cost);
        initialValues.put(KEY_MEMO, 	memo);
        initialValues.put(KEY_DELYN, 	"");
        initialValues.put(KEY_DELDATE, 	"");        
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        initialValues.put(KEY_REPEATDATE, 	repeatdate);
        initialValues.put(KEY_ALARM2, 		alarm2);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_SCHEDULE, null, initialValues);
    }
    public long insertRestoreSchedule( String [] colname, String [] data  ) {
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        
        int colcnt = colname.length;
        
        for ( int i = 0 ; i < colcnt ; i++ ) {
        	initialValues.put(colname[i], 	data[i]);
        }
        
        initialValues.put(KEY_DELYN, 	"");
        initialValues.put(KEY_DELDATE, 	"");
        initialValues.put(KEY_CONFIRMDATE, 	today);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.insert(ComConstant.DATABASE_TABLE_SCHEDULE, null, initialValues);
    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteScheduleComplete(long rowId) {

        return mDb.delete(ComConstant.DATABASE_TABLE_SCHEDULE, KEY_SCHDULEID + "=" + rowId, null) > 0;
    }
    
    /*
     * 스케줄 삭제일자,구분만 update ( 종속정보 보존을 위한 정책 )
     */
    public boolean deleteSchedule(long rowId) {
        	   	
        	//현재일자 시간 가져오기
        	String today = SmDateUtil.getToday();
            ContentValues initialValues = new ContentValues();

            initialValues.put(KEY_DELYN, 	"Y".trim());
            initialValues.put(KEY_DELDATE, 	today);
            
            return mDb.update(ComConstant.DATABASE_TABLE_SCHEDULE, initialValues, KEY_SCHDULEID + "=" + rowId, null) > 0;

    }
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllSchedule() throws SQLException {
    	Cursor mCursor =
    			
    		mDb.query(ComConstant.DATABASE_TABLE_SCHEDULE, new String[] {
        		KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
        		KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
        		KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
        		KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO, KEY_OTHERKIND, KEY_OTHERID,
        		KEY_REPEATDATE, KEY_ALARM2}
    		  , KEY_DELYN + " != 'Y'"
    		  , null, null, null, 
    		  KEY_USERID + ", " + KEY_STARTDATE + " DESC "
    		  );
    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     * -> 유효스케줄 전체(정렬순서:사용자, 시작일자 역순)
     */
    public Cursor fetchBeforeSchedule( String date ) throws SQLException {
    	Cursor mCursor =
    			
	    		mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE, new String[] {
	        		KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
	        		KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
	        		KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
	        		KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
	        		KEY_REPEATDATE, KEY_ALARM2}
	    		, 	" ( "  + KEY_ENDDATE   + " >= '" + date + "' and " +
	  		         		 KEY_DELYN 	   + " != 'Y'  ) "
	  		    , null, null, null, 
	  		    KEY_USERID + " ASC , " + KEY_STARTDATE + " DESC " 
	  		    , null);
 

    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchAfterSchedule( String date ) throws SQLException {
    	Cursor mCursor =
    			
	    		mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE, new String[] {
	        		KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
	        		KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
	        		KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
	        		KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
	        		KEY_REPEATDATE, KEY_ALARM2}
	    		, 	" ( "  + KEY_ENDDATE   + " <  '" + date + "' and " +
	  		         		 KEY_DELYN 	   + " != 'Y' ) "
	  		    , null, null, null, 
	  		    KEY_USERID + ", " + KEY_STARTDATE + " DESC " 
	  		    , null);
 

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
   
    public Cursor fetchSchedule(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE, new String[]  {
                		KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
                		KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
                		KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
                		KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
                		KEY_REPEATDATE, KEY_ALARM2}
                	  , KEY_SCHDULEID + "=" + rowId + " and " +
                	  	KEY_DELYN + " != 'Y'"
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
     */
   
    public Cursor fetchUserSchedule(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE, new String[]  {
                		KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
                		KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
                		KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
                		KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
                		KEY_REPEATDATE, KEY_ALARM2}
                      , KEY_USERID + "=" + rowId+ " and " +
              	  		KEY_DELYN + " != 'Y'"
                      , null, null, null, KEY_STARTDATE, null);
      
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
     * -.특정일자에 해당하는 스케줄 가져오기(시작시간 순서)
     */
   
    public Cursor fetchSchedulePerDate( String sdate ) throws SQLException {

    	try {
            Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE 
                	  ,	new String[]  { 
                			  KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
                			  KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
                			  KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
                      		  KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
                      		  KEY_REPEATDATE, KEY_ALARM2}
          	  		  , 	" ( "  + KEY_STARTDATE + " <= '" + sdate + "' and " +
          	  		  		         KEY_ENDDATE   + " >= '" + sdate  + "' ) " + " and " +
          	  		  		  KEY_DELYN + " != 'Y' "
                	  , null, null, null, 
                	  KEY_STARTTIME + " ASC , " + KEY_NAME , null);
        
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;  		
    	} catch (SQLException e) {   
    		//오류가 생겨도 정상처리하기 위해
    		return null;
    	}

    }  
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     * -.특정일자에 해당하는 스케줄 가져오기(단, 사용자 우선순위 : TimeTable 전용)
     */
   
    public Cursor fetchSchedulePerDateSortUser( String sdate ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE 
                	  ,	new String[]  { 
                			  KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
                			  KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
                			  KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
                      		  KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
                      		  KEY_REPEATDATE, KEY_ALARM2}
          	  		  , 	" ( "  + KEY_STARTDATE + " <= '" + sdate + "' and " +
          	  		  		         KEY_ENDDATE   + " >= '" + sdate  + "' ) " + " and " +
          	  		  		  KEY_DELYN + " != 'Y' "
                	  , null, null, null, KEY_USERID + ", " + KEY_STARTTIME + ", " + KEY_NAME , null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }     
//    public Cursor fetchSchedulePerDateCnt( String sdate ) throws SQLException {
//
//        Cursor mCursor =
//
//                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE 
//                	  ,	new String[]  { 
//                				KEY_USERID, " count(*) AS " + KEY_CNT }
//          	  		  , 	" ( "  + KEY_STARTDATE + " <= '" + sdate + "' and " +
//          	  		  		         KEY_ENDDATE   + " >= '" + sdate  + "' ) " + " and " +
//          	  		  		  KEY_DELYN + " != 'Y' "
//                	  , null, KEY_USERID, null, null , null);
//        
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//    }    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     * -. 달력용(종료일자가 없이 시작일자만 있는 데이터가져오기) (메인달력 Only)
     * --> 정렬순서 : 시작시간기준 (단, 종일은 최하단으로)
     */
   
    public Cursor fetchFromToScheduleForCal(String fromdate, String todate ) throws SQLException {

        Cursor mCursor =

                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE 
                	  ,	new String[]  { 
                			  KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
                			  KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
                			  KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
                      		  KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
                      		  KEY_REPEATDATE, KEY_ALARM2}
                	  , 	" (( "  + KEY_STARTDATE + " <= '" + fromdate + "' and " +
                	  		"   "  + KEY_ENDDATE   + " >= '" + fromdate + "' ) " + " or " +
                	  		" ( "  + KEY_STARTDATE + " >= '" + fromdate + "' and " +
                	  		"   "  + KEY_STARTDATE + " <= '" + todate   + "' )) " + " and " +
                    	  	  KEY_DELYN + " != 'Y'"
                	  , null, null, null, 
                	  KEY_ALLDAYYN + " DESC, " + KEY_STARTTIME + " ASC , " + KEY_NAME , null);
        
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

                mDb.query(true, ComConstant.DATABASE_TABLE_SCHEDULE 
                	  ,	new String[]  { 
                			  KEY_SCHDULEID, KEY_NAME, KEY_USERID, KEY_CYCLE,
                			  KEY_SUN, KEY_MON, KEY_TUE, KEY_WEN, KEY_THU, KEY_FRI, KEY_SAT, 
                			  KEY_STARTDATE, KEY_ENDDATE, KEY_ALLDAYYN, KEY_STARTTIME, KEY_ENDTIME, 
                      		  KEY_ALARM, KEY_TEL1, KEY_TEL2, KEY_COST, KEY_MEMO,
                      		  KEY_REPEATDATE, KEY_ALARM2}
                	  , 	"  ( "  + KEY_ALARM	    + " >= '0'" + " or " +
                	    	"    "  + KEY_ALARM2	+ " >= '0'  )" + " and " +
                	  " (( "  + KEY_STARTDATE + " <= '" + fromdate + "' and " +
                	  		"   "  + KEY_ENDDATE   + " >= '" + fromdate + "' ) " + " or " +
                	  		" ( "  + KEY_STARTDATE + " >= '" + fromdate + "' and " +
                	  		"   "  + KEY_STARTDATE + " <= '99999999' )) " + " and " +
                    	  	  KEY_DELYN + " != 'Y'"
                	  , null, null, null, KEY_STARTTIME + " desc "  , null);
        
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
    
    public boolean updateSchedule(long rowId, String name, Long userid, String cycle,
    		String sun, String mon, String thu, String wen, String thr, String fri, String sat,  
    		String startdate, String enddate, String alldayyn, String starttime, String endtime, 
    		String alarm, String tel1, String tel2, long cost, String memo,
    		String repeatdate, String alarm2) {
    	   	
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, 	name);
        initialValues.put(KEY_USERID, 	userid);
        initialValues.put(KEY_CYCLE, 	cycle);
        initialValues.put(KEY_SUN, 		sun);
        initialValues.put(KEY_MON, 		mon);
        initialValues.put(KEY_TUE, 		thu);
        initialValues.put(KEY_WEN, 		wen);
        initialValues.put(KEY_THU, 		thr);
        initialValues.put(KEY_FRI, 		fri);
        initialValues.put(KEY_SAT, 		sat);
        initialValues.put(KEY_STARTDATE,startdate);
        initialValues.put(KEY_ENDDATE, 	enddate);
        initialValues.put(KEY_ALLDAYYN, alldayyn);
        initialValues.put(KEY_STARTTIME,starttime);
        initialValues.put(KEY_ENDTIME, 	endtime);
        initialValues.put(KEY_ALARM, 	alarm);
        initialValues.put(KEY_TEL1, 	tel1);
        initialValues.put(KEY_TEL2, 	tel2);
        initialValues.put(KEY_COST, 	cost);
        initialValues.put(KEY_MEMO, 	memo);
        initialValues.put(KEY_DELYN, 	"");
        initialValues.put(KEY_DELDATE, 	"");
        initialValues.put(KEY_MODIFYDATE, 	today);
        initialValues.put(KEY_REPEATDATE, 	repeatdate);
        initialValues.put(KEY_ALARM2, 		alarm2);
        
        return mDb.update(ComConstant.DATABASE_TABLE_SCHEDULE, initialValues, KEY_SCHDULEID + "=" + rowId, null) > 0;
    }
    /*
     * 가져온 경로별 key value setting
     */
    public boolean updateOtherSchedule(long rowId, String otherkind, long otherid ) {
    	   	
    	//현재일자 시간 가져오기
    	String today = SmDateUtil.getToday();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OTHERKIND, 	otherkind);
        initialValues.put(KEY_OTHERID, 		otherid);
        initialValues.put(KEY_MODIFYDATE, 	today);
        
        return mDb.update(ComConstant.DATABASE_TABLE_SCHEDULE, initialValues, KEY_SCHDULEID + "=" + rowId, null) > 0;
    }    
}
