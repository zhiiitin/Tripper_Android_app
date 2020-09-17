package com.example.tripper_android_app.location;

import java.io.Serializable;

public class Location_D extends Location implements Serializable {
    private String memos;
    private String stayTimes;

    public Location_D(String name, String address, String memos, String stayTimes) {
        super(name, address);
        this.memos = memos;
        this.stayTimes = stayTimes;
    }

    public Location_D() {
        super();
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
