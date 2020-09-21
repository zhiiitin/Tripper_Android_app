package com.example.tripper_android_app.blog;

import java.io.Serializable;

public class Blog implements Serializable {

    private String blogTittle;
    private String blogDesc;
    private String blogNote;
    private String LocName;
    private int dayCount;
    private int locID;
    private int blogID;

    public Blog(String blogTittle, String blogDesc, String blogNote, String locName, int dayCount,int locID, int blogID) {
        this.blogTittle = blogTittle;
        this.blogDesc = blogDesc;
        this.blogNote = blogNote;
        LocName = locName;
        this.dayCount = dayCount;
        this.locID = locID;
        this.blogID = blogID;
    }
    public Blog(String blogTitle, String blogDesc2, String blogNote2) {
        this.blogTittle = blogTitle;
        this.blogDesc = blogDesc2;
        this.blogNote = blogNote2;
    }
    public int getBlogID(){
        return blogID;
    }

    public void setBlogID(int blogID) {
        this.blogID = blogID;
    }

    public int getLocId() {
        return locID;
    }
    public int setLocId() {
        return locID;
    }

    public String getBlogTittle() {
        return blogTittle;
    }

    public void setBlogTittle(String blogTittle) {
        this.blogTittle = blogTittle;
    }

    public String getBlogDesc() {
        return blogDesc;
    }

    public void setBlogDesc(String blogDesc) {
        this.blogDesc = blogDesc;
    }

    public String getBlogNote() {
        return blogNote;
    }

    public void setBlogNote(String blogNote) {
        this.blogNote = blogNote;
    }

    public String getLocName() {
        return LocName;
    }

    public void setLocName(String locName) {
        LocName = locName;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }
}
