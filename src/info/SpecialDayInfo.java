package com.waveapp.smcalendarlite.info;

import com.waveapp.smcalendarlite.util.SmDateUtil;

/*
 * 휴일,기념일 db에 사용(specialday)
 * (ListView 나 batch 에 사용)
 * -.특이사항 : batch의 경우 전문layout에 따라 처리
 * 2011.5.20 totoal lenght : 85byte
 *  	id(1.11)locale(12.2)gubun(14.1)holidayyn(15.1)name(15.20)repeatyn(36.1)
 * 	    year(37.4)month(41.2)day(43.2)memo(45.30)delyn(75.1)deldate(76.8)
 */
public class SpecialDayInfo extends Object implements Cloneable{

	public String getAlarm() {
		return Alarm;
	}
	public void setAlarm(String alarm) {
		Alarm = alarm;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	private long   Id;
	private String Locale;
	private String Gubun;
	private String Event;
	private String HolidayYn;
	private String Name;
	private String SubName;
	private String Repeatyn;
	private String Year;
	private String MonthDay;
	private String Month;
	private String Day;
	private String Leap;
	private String Memo;
	private String Alarm;
	private int UserColor;
	private String UserGroup;
	private boolean choice;
	
	public String getSubName() {
		return SubName;
	}
	public void setSubName(String subName) {
		SubName = subName;
	}
	public String getRepeatyn() {
		return Repeatyn;
	}
	public void setRepeatyn(String repeatyn) {
		Repeatyn = repeatyn;
	}
	public String getLeap() {
		return Leap;
	}
	public void setLeap(String Leap) {
		this.Leap = Leap;
	}
	public String getSolardate() {
		return solardate;
	}
	public void setSolardate(String solardate) {
		this.solardate = solardate;
	}


	private String solardate;
	private String ThisDate;

	//private String dayOfWeekKo;
	private String DDay;

	public long getId()
	{
		return Id;
	}
	public void setId(long id)
	{
		this.Id = id;
	}	
	
	public String getName()
	{
		return Name;
	}
	public void setName(String name)
	{
		this.Name = name;
	}
	
	public String getLocale()
	{
		return Locale;
	}
	public void setLocale(String locale)
	{
		this.Locale = locale;
	}
	
	public String getGubun()
	{
		return Gubun;
	}
	public void setGubun(String gubun)
	{
		this.Gubun = gubun;
	}
	public String getEvent() {
		return Event;
	}
	public void setEvent(String event) {
		Event = event;
	}
	
	public String getHolidayYn()
	{
		return HolidayYn;
	}
	public void setHolidayYn(String holidayyn)
	{
		this.HolidayYn = holidayyn;
	}
	public String getRepeatYn()
	{
		return Repeatyn;
	}
	public void setRepeatYn(String repeatyn)
	{
		this.Repeatyn = repeatyn;
	}
	public String getYear()
	{
		return Year;
	}
	public void setYear(String year)
	{
		this.Year = year;
	}	
	public String getMonthDay()
	{
		return MonthDay;
	}
	public void setMonthDay(String monthday)
	{
		this.MonthDay = monthday;
	}
	public String getMonth()
	{
		return Month;
	}
	public void setMonth(String month)
	{
		this.Month = month;
	}	
	public String getDay()
	{
		return Day;
	}
	public void setDay(String day)
	{
		this.Day = day;
	}	
	
	public String getThisDate()
	{
		return ThisDate;
	}
	public void setThisDate(String thisdate)
	{
		this.ThisDate = thisdate;
	}
	public String getMakeThisDate()
	{
		return ThisDate;
	}
	public void setMakeThisDate()
	{
		this.ThisDate = SmDateUtil.getDateFormat(
							Integer.parseInt(this.Year), 
							Integer.parseInt(this.MonthDay));
	}
	
	public String getDDay()
	{
		return DDay;
	}
	public void setDDay( String dday )
	{
		this.DDay = dday;
	}	


	public int getUserColor() {
		return UserColor;
	}
	public void setUserColor(int userColor) {
		UserColor = userColor;
	}
	
	public String getMemo() {
		return Memo;
	}
	public void setMemo(String memo) {
		Memo = memo;
	}

	public String getUserGroup() {
		return UserGroup;
	}
	public void setUserGroup(String userGroup) {
		UserGroup = userGroup;
	}
	public boolean isChoice() {
		return choice;
	}
	public void setChoice(boolean choice) {
		this.choice = choice;
	}	
}
