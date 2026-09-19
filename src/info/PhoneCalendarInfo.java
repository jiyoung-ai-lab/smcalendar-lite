package com.waveapp.smcalendarlite.info;


public class PhoneCalendarInfo {

	public static final String KEY_ID				= "_id";
	public static final String KEY_CALENDAR_ID 		= "calendar_id";
//	public static final String KEY_HTML_URI 		= "htmlUri";
	public static final String KEY_TITLE 			= "title";
	public static final String KEY_EVENT_LOCATION	= "eventLocation";
	public static final String KEY_DESCRIPTION 		= "description";  //5
	public static final String KEY_EVENT_STATUS 	= "eventStatus";
	public static final String KEY_SELF_ATTENDEESTATUS 	= "selfAttendeeStatus";
//	public static final String KEY_COMMENTS_URI		= "commentsUri";
	public static final String KEY_DTSTART 			= "dtstart";
	public static final String KEY_DTEND			= "dtend"; //10
	public static final String KEY_EVENT_TIMEZONE	= "eventTimezone";  
	public static final String KEY_DURATION 		= "duration";
	public static final String KEY_ALLDAY 			= "allDay";
//	public static final String KEY_VISIBILITY 		= "visibility";
//	public static final String KEY_TRANSPARENCY 	= "transparency";
	public static final String KEY_HAS_ALARM 		= "hasAlarm";
	public static final String KEY_HAS_EXTENDED_PROP= "hasExtendedProperties";
	public static final String KEY_RRULE 			= "rrule";
	public static final String KEY_RDATE 			= "rdate";
	public static final String KEY_EXRULE 			= "exrule"; //20
	public static final String KEY_EXDATE 			= "exdate";  
//	public static final String KEY_ORI_EVENT 		= "originalEvent";
	public static final String KEY_ORI_INSTANCETIME = "originalInstanceTime";
	public static final String KEY_ORI_ALLDAY 		= "originalAllDay";
	public static final String KEY_LAST_DATE 		= "lastDate";
	public static final String KEY_HAS_ATTENDEEDATA = "hasAttendeeData";
	public static final String KEY_GU_CANMODIFY 	= "guestsCanModify";
	public static final String KEY_GU_CANINVITEOTHERS= "guestsCanInviteOthers";
	public static final String KEY_GU_CANSEEGUESTS 	= "guestsCanSeeGuests";
	public static final String KEY_ORGANIZER 		= "organizer";
	public static final String KEY_DELETED 			= "deleted";

	public static final String[] projection = new String[] {
			 KEY_ID				      
			,KEY_CALENDAR_ID 		
//			,KEY_HTML_URI 		  
			,KEY_TITLE 			    
			,KEY_EVENT_LOCATION	
			,KEY_DESCRIPTION 		
			,KEY_EVENT_STATUS 	
			,KEY_SELF_ATTENDEESTATUS
//			,KEY_COMMENTS_URI		
			,KEY_DTSTART 			  
			,KEY_DTEND			    
			,KEY_EVENT_TIMEZONE	
			,KEY_DURATION 		  
			,KEY_ALLDAY 			  
//			,KEY_VISIBILITY 		
//			,KEY_TRANSPARENCY 	
			,KEY_HAS_ALARM 		  
			,KEY_HAS_EXTENDED_PROP 
			,KEY_RRULE 			 
			,KEY_RDATE 			 
			,KEY_EXRULE 			
			,KEY_EXDATE 			
//			,KEY_ORI_EVENT 		
			,KEY_ORI_INSTANCETIME 
			,KEY_ORI_ALLDAY 		  
			,KEY_LAST_DATE 		    
			,KEY_HAS_ATTENDEEDATA 
			,KEY_GU_CANMODIFY 	  
			,KEY_GU_CANINVITEOTHERS
			,KEY_GU_CANSEEGUESTS
			,KEY_ORGANIZER
			,KEY_DELETED
		} ;
	public static final String[] projectionForOld = new String[] {
		 KEY_ID				      
		,KEY_CALENDAR_ID 		
//		,KEY_HTML_URI 		  
		,KEY_TITLE 			    
		,KEY_EVENT_LOCATION	
		,KEY_DESCRIPTION 		
		,KEY_EVENT_STATUS 	
		,KEY_SELF_ATTENDEESTATUS
//		,KEY_COMMENTS_URI		
		,KEY_DTSTART 			  
		,KEY_DTEND			    
		,KEY_EVENT_TIMEZONE	
		,KEY_DURATION 		  
		,KEY_ALLDAY 			  
//		,KEY_VISIBILITY 		
//		,KEY_TRANSPARENCY 	
		,KEY_HAS_ALARM 		  
		,KEY_HAS_EXTENDED_PROP 
		,KEY_RRULE 			 
		,KEY_RDATE 			 
		,KEY_EXRULE 			
		,KEY_EXDATE 			
//		,KEY_ORI_EVENT 		
		,KEY_ORI_INSTANCETIME 
		,KEY_ORI_ALLDAY 		  
		,KEY_LAST_DATE 		    
		,KEY_HAS_ATTENDEEDATA 
		,KEY_GU_CANMODIFY 	  
		,KEY_GU_CANINVITEOTHERS
		,KEY_GU_CANSEEGUESTS
		,KEY_ORGANIZER
	} ;
	private int calendar_id;
	private String title;
	private String allDay;
	private int dtstart;
	private int dtend;
	private String duration;
	private String description;
	private String eventLocation;
	private String visibility;
	private String hasAlarm;
	private String rrule;
	private String exdate;
	private String lastDate;
	
	public int getCalendar_id() {
		return calendar_id;
	}
	public void setCalendar_id(int calendar_id) {
		this.calendar_id = calendar_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAllDay() {
		return allDay;
	}
	public void setAllDay(String allDay) {
		this.allDay = allDay;
	}
	public int getDtstart() {
		return dtstart;
	}
	public void setDtstart(int dtstart) {
		this.dtstart = dtstart;
	}
	public int getDtend() {
		return dtend;
	}
	public void setDtend(int dtend) {
		this.dtend = dtend;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEventLocation() {
		return eventLocation;
	}
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public String getHasAlarm() {
		return hasAlarm;
	}
	public void setHasAlarm(String hasAlarm) {
		this.hasAlarm = hasAlarm;
	}
	public String getRrule() {
		return rrule;
	}
	public void setRrule(String rrule) {
		this.rrule = rrule;
	}
	public String getExdate() {
		return exdate;
	}
	public void setExdate(String exdate) {
		this.exdate = exdate;
	}

	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
}
