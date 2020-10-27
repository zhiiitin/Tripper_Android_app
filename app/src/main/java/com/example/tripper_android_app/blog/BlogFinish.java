package com.example.tripper_android_app.blog;

import android.widget.TextView;

import java.io.Serializable;

public class BlogFinish implements Serializable {
    private String trip_Id ;
    private String blog_title ;
    private String blog_Info ;
    private String memberId ;
    private String startDate;
    private String startTime;
    private int status ;

    public BlogFinish(String blog_title, String blog_desc) {
        this.blog_title = blog_title;
        this.blog_Info = blog_desc;
    }


    public BlogFinish(String trip_Id, String blog_title, String blog_Info, String memberId ) {
        this.trip_Id = trip_Id;
        this.blog_title = blog_title;
        this.blog_Info = blog_Info;
        this.memberId = memberId;
    }


    public BlogFinish(String trip_Id, String blog_title, String blog_Info, String memberId, String startDate, String startTime) {
        this.trip_Id = trip_Id;
        this.blog_title = blog_title;
        this.blog_Info = blog_Info;
        this.memberId = memberId;
        this.startDate = startDate;
        this.startTime = startTime;
    }

    public BlogFinish(String trip_Id, String blog_title, String blog_Info, String memberId, int status ) {
        this.trip_Id = trip_Id;
        this.blog_title = blog_title;
        this.blog_Info = blog_Info;
        this.memberId = memberId;
        this.status = status;


    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }

    public String getBlog_title() {
        return blog_title;
    }

    public void setBlog_title(String blog_title) {
        this.blog_title = blog_title;
    }

    public String getBlog_Info() {
        return blog_Info;
    }

    public void setBlog_Info(String blog_Info) {
        this.blog_Info = blog_Info;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
