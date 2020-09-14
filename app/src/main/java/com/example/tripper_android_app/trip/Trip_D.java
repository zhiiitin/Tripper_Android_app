package com.example.tripper_android_app.trip;

import java.io.Serializable;

/**
 * 類別說明：行程附檔_Detail
 * 
 * @author Cooper
 * @version 建立時間:Sep 3, 2020
 *
 */

public class Trip_D implements Serializable {
	private static final long serialVersionUID = 1L;
	private String transId;
	private String tripId;
	private int seqNo;
	private int locId;
	private String startDate;
	private String startTime;
	private String stayTime;
	private String memo;

	public Trip_D(String transId, String tripId, int seqNo, int locId, String startDate, String startTime,
			String stayTime, String memo) {
		super();
		this.transId = transId;
		this.tripId = tripId;
		this.seqNo = seqNo;
		this.locId = locId;
		this.startDate = startDate;
		this.startTime = startTime;
		this.stayTime = stayTime;
		this.memo = memo;
	}

	public Trip_D(String tripId, int seqNo, int locId, String startDate, String startTime, String stayTime,
			String memo) {
		super();
		this.tripId = tripId;
		this.seqNo = seqNo;
		this.locId = locId;
		this.startDate = startDate;
		this.startTime = startTime;
		this.stayTime = stayTime;
		this.memo = memo;
	}

	public Trip_D(String stayTime, String memo) {
		this.startTime = stayTime;
		this.memo = memo;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public int getLocId() {
		return locId;
	}

	public void setLocId(int locId) {
		this.locId = locId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStayTime() {
		return stayTime;
	}

	public void setStayTime(String stayTime) {
		this.stayTime = stayTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
