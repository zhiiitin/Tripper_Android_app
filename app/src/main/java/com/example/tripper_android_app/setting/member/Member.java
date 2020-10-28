package com.example.tripper_android_app.setting.member;

import java.io.Serializable;

public class Member implements Serializable {
    private int id ;
    private String account ;
    private String password ;
    private String mail ;
    private String nickName ;
    private String token ;
    private int loginType ;
//    private Timestamp createTime ;
//    private Timestamp modifyTime ;
//    private Timestamp lastTime ;


    public Member(String account,String password) {
        this.account = account;
        this.password = password ;
    }

    // for 加好友功能
    public Member(int id, String account, String mail, String nickName, int loginType, String token) {
        super();
        this.id = id;
        this.account = account;
        this.mail = mail;
        this.nickName = nickName;
        this.token = token;
        this.loginType = loginType;
    }



    public Member(int id, String account ,String password , String nickname) {
        super();
        this.id = id;
        this.account = account;
        this.password = password ;
        this.nickName = nickname ;
    }

    public Member(int id, String account ,String password, String email, String nickname) {
        super();
        this.id = id;
        this.account = account;
        this.password = password ;
        this.mail = email ;
        this.nickName = nickname ;
    }

    public Member() {

    }


//    public Member(int id, String account, String password, String mail, String nickName, String token,
//                  Timestamp createTime) {
//        super();
//        this.id = id;
//        this.account = account;
//        this.password = password;
//        this.mail = mail;
//        this.nickName = nickName;
//        this.token = token;
//        this.createTime = createTime;
//    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
//    public Timestamp getCreateTime() {
//        return createTime;
//    }
//    public void setCreateTime(Timestamp createTime) {
//        this.createTime = createTime;
//    }
//    public Timestamp getModifyTime() {
//        return modifyTime;
//    }
//    public void setModifyTime(Timestamp modifyTime) {
//        this.modifyTime = modifyTime;
//    }
//    public Timestamp getLastTime() {
//        return lastTime;
//    }
//    public void setLastTime(Timestamp lastTime) {
//        this.lastTime = lastTime;
//    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }



}

