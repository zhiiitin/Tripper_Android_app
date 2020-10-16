package com.example.tripper_android_app.explore;

import java.io.Serializable;

public class Explore implements Serializable {

    private String blogId;
    private String userId;
    private String nickName;
    private String tittleName;
    private String blogDesc;

    public String getBlogDesc() {
        return blogDesc;
    }

    public void setBlogDesc(String blogDesc) {
        this.blogDesc = blogDesc;
    }

    public Explore(String blogId, String userId, String nickName, String tittleName, String blogDesc) {
        this.blogId = blogId;
        this.userId = userId;
        this.nickName = nickName;
        this.tittleName = tittleName;
        this.blogDesc = blogDesc;
    }

    public Explore(String blogId, String userId, String nickName, String tittleName) {
        super();
        this.blogId = blogId;
        this.userId = userId;
        this.nickName = nickName;
        this.tittleName = tittleName;
    }
    public String getBlogId() {
        return blogId;
    }
    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
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

