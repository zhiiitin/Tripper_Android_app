package com.example.tripper_android_app.location;

import java.io.Serializable;
import java.sql.Timestamp;

public class Location_D extends Location implements Serializable {

    private static final long serialVersionUID = 1L;
    private String memos;
    private String stayTimes;
    private String transId;
    private String startDate;
    private String startTime;

    public Location_D(String name, String address, String memos, String stayTimes) {
        super(name, address);
        this.memos = memos;
        this.stayTimes = stayTimes;
    }

    public Location_D(String transId, String startDate, String startTime, String name, String address, String memos, String stayTimes) {
        super(name, address);
        this.transId = transId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.memos = memos;
        this.stayTimes = stayTimes;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
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

    public String getStayTimes() {
        return stayTimes;
    }

    public void setStayTimes(String stayTimes) {
        this.stayTimes = stayTimes;
    }

    public String getMemos() {
        return memos;
    }

    public void setMemos(String memos) {
        this.memos = memos;
    }
}
