package com.example.tripper_android_app.blog;

import java.io.Serializable;

public class Blog_SpotInfo implements Serializable {

    private String loc_Id;
    private String trip_Id;
    private String name;

    public Blog_SpotInfo(String loc_Id, String trip_Id, String name) {
        this.loc_Id = loc_Id;
        this.trip_Id = trip_Id;
        this.name = name;
    }

    public String getLoc_Id() {
        return loc_Id;
    }

    public void setLoc_Id(String loc_Id) {
        this.loc_Id = loc_Id;
    }

    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
