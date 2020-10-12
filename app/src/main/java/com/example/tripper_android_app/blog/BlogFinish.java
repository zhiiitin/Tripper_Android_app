package com.example.tripper_android_app.blog;

import java.io.Serializable;

public class BlogFinish implements Serializable {
    private String trip_Id ;
    private String blog_title ;
    private String blog_Info ;
    private String memberId ;

    public BlogFinish(String trip_Id, String blog_title, String blog_Info, String memberId) {
        this.trip_Id = trip_Id;
        this.blog_title = blog_title;
        this.blog_Info = blog_Info;
        this.memberId = memberId;
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
}
