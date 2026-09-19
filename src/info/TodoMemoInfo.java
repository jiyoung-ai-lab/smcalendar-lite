package com.waveapp.smcalendarlite.info;

/*
 * 할일 리스트용 객체
 * (CustomListView 에 사용)
 */

public class TodoMemoInfo {
	private long id;
	private String memo;
	private String termyn;
	private String finishterm;
	private String finish;
	private String comfirmdate;
	
//	private String repeat;
	private String alarm;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getTermyn() {
		return termyn;
	}
	public void setTermyn(String termyn) {
		this.termyn = termyn;
	}
	public String getFinishterm() {
		return finishterm;
	}
	public void setFinishterm(String finishterm) {
		this.finishterm = finishterm;
	}
	public String getFinish() {
		return finish;
	}
	public void setFinish(String finish) {
		this.finish = finish;
	}

	public String getComfirmdate() {
		return comfirmdate;
	}
	public void setComfirmdate(String comfirmdate) {
		this.comfirmdate = comfirmdate;
	}

//	public String getRepeat() {
//		return repeat;
//	}
//	public void setRepeat(String repeat) {
//		this.repeat = repeat;
//	}
	public String getAlarm() {
		return alarm;
	}
	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}	
}
