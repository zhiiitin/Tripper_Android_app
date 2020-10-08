package com.example.tripper_android_app.explore;

import java.io.Serializable;

public class Explore implements Serializable {

    private int blogId;
    private int userId;
    private String nickName;
    private String tittleName;
    private String blogDesc;

    public String getBlogDesc() {
        return blogDesc;
    }

    public void setBlogDesc(String blogDesc) {
        this.blogDesc = blogDesc;
    }

    public Explore(int blogId, int userId, String nickName, String tittleName, String blogDesc) {
        this.blogId = blogId;
        this.userId = userId;
        this.nickName = nickName;
        this.tittleName = tittleName;
        this.blogDesc = blogDesc;
    }

    public Explore(int blogId, int userId, String nickName, String tittleName) {
        super();
        this.blogId = blogId;
        this.userId = userId;
        this.nickName = nickName;
        this.tittleName = tittleName;
    }
    public int getBlogId() {
        return blogId;
    }
    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getTittleName() {
        return tittleName;
    }
    public void setTittleName(String tittleName) {
        this.tittleName = tittleName;
    }




}

