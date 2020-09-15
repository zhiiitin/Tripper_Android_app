package com.example.tripper_android_app.explore;

public class Explore {

    private int blogId;
    private String tittleName;
    private String userName;


    public Explore(int blogId, String tittleName, String userName) {
        super();
        this.blogId = blogId;
        this.tittleName = tittleName;
        this.userName = userName;
    }


    public int getBlogId() {
        return blogId;
    }


    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }


    public String getTittleName() {
        return tittleName;
    }


    public void setTittleName(String tittleName) {
        this.tittleName = tittleName;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }



}

