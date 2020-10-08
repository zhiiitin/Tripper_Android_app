package com.example.tripper_android_app.blog;

public class DateAndId {
    String s_Date;
    String trip_Id;


    public DateAndId(String s_Date, String trip_Id) {
        super();
        this.s_Date = s_Date;
        this.trip_Id = trip_Id;
    }



    public DateAndId(String trip_Id) {
        super();
        this.trip_Id = trip_Id;
    }


    public String getS_Date() {
        return s_Date;
    }

    public void setS_Date(String s_Date) {
        this.s_Date = s_Date;
    }

    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }
}
