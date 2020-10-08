package com.example.tripper_android_app.blog;

public class Blog_SpotInformation {
    private String spotName;
    private String stayTime;

    public Blog_SpotInformation(String spotName, String stayTime) {
        this.spotName = spotName;
        this.stayTime = stayTime;
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
