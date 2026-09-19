package com.waveapp.smcalendarlite.info;

/*
 * Google Calendar XML Info
 */
public class GoogleCalendarInfo {
    private String entry;
    private String EID ;
    private String title;
    private String content;
    private String where;
    private String when ;
    private String valueString;
    private String endTime;
    private String startTime;
    
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
	public String getEID() {
		return EID;
	}
	public void setEID(String EID) {
		this.EID = EID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getWhen() {
		return when;
	}
	public void setWhen(String when) {
		this.when = when;
	}
	public String getValueString() {
		return valueString;
	}
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
}
