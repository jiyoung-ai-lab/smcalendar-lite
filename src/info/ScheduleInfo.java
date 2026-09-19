package com.waveapp.smcalendarlite.info;

import java.util.ArrayList;

/*
 * 스케줄 및 사용자 리스트용 객체
 * (CustomListView 에 사용)
 */
public class ScheduleInfo {

	public String getAlarm2() {
		return alarm2;
	}
	public void setAlarm2(String alarm2) {
		this.alarm2 = alarm2;
	}
	public String getRepeatdate() {
		return repeatdate;
	}
	public void setRepeatdate(String repeatdate) {
		this.repeatdate = repeatdate;
	}
	private long id;
	private long userId;
	private long scheduleId;
	private String Cycle;
	private int userColor;
	private String userName;
	private String scheduleName;
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;

	private String alarmYn;
	private String strDayOfWeek;
	private String allDayYn;
	private String holidayYn;
	private String tel;
	private String memo;
	
	private int [] dayOfWeek;
	private boolean choice;
	private int  position;   //timeschedule 화면/ 주간스케줄 위치

	private String  schduleDate;

	private ArrayList<String>  schduleDate2;
	
	private String guBun;
	private String subName;

	//다른달력정보 가져올때만 사용하는 항목(4개)
	private String otherkind;
	private long otherId;
	private String cangetyn;
	private String description;
	
	private String alarm2;
	private String repeatdate;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}	
		
	public long getUserId()
	{
		return userId;
	}
	public void setUserId(long userid)
	{
		this.userId = userid;
	}	
	
	public String getUsername()
	{
		return userName;
	}
	public void setUserName(String username)
	{
		this.userName = username;
	}
	
	public int getUseColor()
	{
		return userColor;
	}
	public void setUserColor(int usercolor)
	{
		this.userColor = usercolor;
	}
	public long getScheduleId()
	{
		return scheduleId;
	}
	public void setScheduleId(Long scheduleid)
	{
		this.scheduleId = scheduleid;
	}
	
	public String getScheduleName()
	{
		return scheduleName;
	}
	public void setScheduleName(String schedulename)
	{
		this.scheduleName = schedulename;
	}
	public String getCycle()
	{
		return Cycle;
	}
	public void setCycle(String cycle)
	{
		this.Cycle = cycle;
	}	
	public String getStartDate()
	{
		return startDate;
	}
	public void setStartDate(String startdate)
	{
		this.startDate = startdate;
	}
	
	public String getEndDate()
	{
		return endDate;
	}
	public void setEndDate(String enddate)
	{
		this.endDate = enddate;
	}	
	
	public String getStartTime()
	{
		return startTime;
	}
	public void setStartTime(String starttime)
	{
		this.startTime = starttime;
	}
	
	public String getEndTime()
	{
		return endTime;
	}
	public void setEndTime(String endtime)
	{
		this.endTime = endtime;
	}
	
	public String getAlarmYn()
	{
		return alarmYn;
	}
	public void setAlarmYn(String alarmyn)
	{
		this.alarmYn = alarmyn;
	}
	
	//요일을 배열로 (순서 : 일월화수목금토일)
	public int [] getDayOfWeek()
	{
		return dayOfWeek;
	}
	
	public void setDayOfWeek(int [] dayofweek )
	{
		this.dayOfWeek = dayofweek;

	}	
	//요일을 String 값으로
	public String getDayOfWeekFullText()
	{
		return strDayOfWeek;
	}
	
	public void setDayOfWeekFullText(String strdayofweek )
	{
		this.strDayOfWeek = strdayofweek;

	}
	//스케줄일자 set
	public String  getScheduleDate()
	{
		return schduleDate;
	}	
	public void setScheduleDate( String  date )
	{
		this.schduleDate = date;
		
	}	
	//스케줄구분 ( U:기념일,B:국공일,S:일반스케줄)
	public String  getScheduleGubun()
	{
		return guBun;
	}	
	public void setScheduleGubun( String  gubun )
	{
		this.guBun = gubun;
		
	}		
	public ArrayList<String> getScheduleDate2()
	{
		return schduleDate2;
	}	
	public void setScheduleDate2( ArrayList<String> date )
	{
		this.schduleDate2 = date;		
	}
	
	public boolean isChoice() {
		return choice;
	}
	public void setChoice(boolean choice) {
		this.choice = choice;
	}
	
	public String getSubName() {
		return subName;
	}
	public void setSubName(String subName) {
		this.subName = subName;
	}
	
	public String getAllDayYn() {
		return allDayYn;
	}
	public void setAllDayYn(String allDayYn) {
		this.allDayYn = allDayYn;
	}	

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getHolidayYn() {
		return holidayYn;
	}
	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
	}

	public String getOtherkind() {
		return otherkind;
	}
	public void setOtherkind(String otherkind) {
		this.otherkind = otherkind;
	}
	public long getOtherId() {
		return otherId;
	}
	public void setOtherId(long otherId) {
		this.otherId = otherId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCangetyn() {
		return cangetyn;
	}
	public void setCangetyn(String cangetyn) {
		this.cangetyn = cangetyn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
}
