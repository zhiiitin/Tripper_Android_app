package com.example.tripper_android_app.explore;

public class Explore {

    private  int id;
    private  String userName;
    private  String blogName;

    public Explore(int id, String userName, String blogName) {
        this.id = id;
        this.userName = userName;
        this.blogName = blogName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }
}
