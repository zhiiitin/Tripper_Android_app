package com.example.tripper_android_app.blog;

public class Blog_SpotInformation {
    private String spotName;
    private String stayTime;
    private String locId;

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public Blog_SpotInformation(String spotName, String stayTime, String locId) {
        this.spotName = spotName;
        this.stayTime = stayTime;
        this.locId = locId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }
}
