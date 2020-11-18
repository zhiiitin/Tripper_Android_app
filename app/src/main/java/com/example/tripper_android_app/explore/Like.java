package com.example.tripper_android_app.explore;

public class Like {
    private String blogId;
    private String userId;
    private String name;
    private String accountId;

    public Like(String blogId, String userId, String name,String accountId) {
        this.blogId = blogId;
        this.userId = userId;
        this.name = name;
        this.accountId = accountId;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

