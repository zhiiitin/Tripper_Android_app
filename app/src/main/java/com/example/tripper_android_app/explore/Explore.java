package com.example.tripper_android_app.explore;

public class Explore {

    private  int id;
    private  String userName;
    private  String tittleName;

    public Explore(int id, String tittleName,String userName) {
        this.id = id;
        this.userName = userName;
        this.tittleName = tittleName;
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

    public String getTittleName() {
        return tittleName;
    }

    public void setTittleName(String tittleName) {
        this.tittleName = tittleName;
    }
}
