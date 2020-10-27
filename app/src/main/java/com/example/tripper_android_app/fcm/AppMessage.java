package com.example.tripper_android_app.fcm;

import java.io.Serializable;

/**
* 類別說明：
* @author Connor Fan
* @version 建立時間:Oct 6, 2020 11:35:34 AM
* 
*/
public class AppMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final String FRIEND_NOTIFY = "F"; // 好友相關訊息
	private static final String BLOG_NOTIFY = "B"; // 網誌相關訊息
	private static final String GROUP_NOTIFY = "G"; // 揪團相關訊息
	private static final String TRIP_NOTIFY = "T"; // 行程相關訊息
	private String msgType;
	private int memberId;
	private String msgTitle;
	private String msgBody;
	private int msgStat;
	private int sendId;
	private int reciverId;

	public AppMessage(String msgType, int memberId, String msgTitle, String msgBody, int msgStat, int sendId, int reciverId) {
		super();
		this.msgType = msgType;
		this.memberId = memberId;
		this.msgTitle = msgTitle;
		this.msgBody = msgBody;
		this.msgStat = msgStat;
		this.sendId = sendId;
		this.reciverId = reciverId;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getMsgTitle() {
		return msgTitle;
	}
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}
	public String getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
	public int getSendId() {
		return sendId;
	}
	public void setSendId(int sendId) {
		this.sendId = sendId;
	}
	public int getReciverId() {
		return reciverId;
	}
	public void setReciverId(int reciverId) {
		this.reciverId = reciverId;
	}
	public int getMsgStat() {
		return msgStat;
	}
	public void setMsgStat(int msgStat) {
		this.msgStat = msgStat;
	}
}
