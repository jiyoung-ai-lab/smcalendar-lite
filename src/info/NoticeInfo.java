package com.waveapp.smcalendarlite.info;


/*
 * 공지사항
 */
public class NoticeInfo {

	private long   id;
	private String key;
	private String locale;
	private String appid;
	private String title;
	private String content;
	private String applydate;
	private String readdate;
	private String version;
	private String confirmdate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
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
	public String getApplydate() {
		return applydate;
	}
	public void setApplydate(String applydate) {
		this.applydate = applydate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getReaddate() {
		return readdate;
	}
	public void setReaddate(String readdate) {
		this.readdate = readdate;
	}
	
	public String getConfirmdate() {
		return confirmdate;
	}
	public void setConfirmdate(String confirmdate) {
		this.confirmdate = confirmdate;
	}
}
