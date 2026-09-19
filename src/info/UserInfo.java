package com.waveapp.smcalendarlite.info;


/*
 * 사용자 리스트용 객체
 * (CustomListView 에 사용)
 */
public class UserInfo {
	private long id;
	private long userid;
	private int usercolor;
	private String userName;
	private String birthday;
	private String relation;
	private String relationText;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public int getUsercolor() {
		return usercolor;
	}
	public void setUsercolor(int usercolor) {
		this.usercolor = usercolor;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getRelationText() {
		return relationText;
	}
	public void setRelationText(String relationText) {
		this.relationText = relationText;
	}

	
		
}
