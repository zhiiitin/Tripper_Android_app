package com.example.tripper_android_app.blog;

import java.io.Serializable;

public class Blog_Note implements Serializable {
    private String loc_Id;
    private String blog_Id;
    private String loc_Note;


    public Blog_Note(String loc_Id, String blog_Id, String loc_Note) {
        this.loc_Id = loc_Id;
        this.blog_Id = blog_Id;
        this.loc_Note = loc_Note;
    }

    public String getLoc_Id() {
        return loc_Id;
    }

    public void setLoc_Id(String loc_Id) {
        this.loc_Id = loc_Id;
    }

    public String getBlog_Id() {
        return blog_Id;
    }

    public void setBlog_Id(String blog_Id) {
        this.blog_Id = blog_Id;
    }

    public String getLoc_Note() {
        return loc_Note;
    }

    public void setLoc_Note(String loc_Note) {
        this.loc_Note = loc_Note;
    }
}
