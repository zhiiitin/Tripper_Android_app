package com.example.tripper_android_app.friends;

import com.example.tripper_android_app.setting.member.Member;

import java.io.Serializable;

public class Friends extends Member implements Serializable {
    private int status;

    public Friends(int id, String account, String mail, String nickName, int loginType, String token, int status) {
        super(id, account, mail, nickName, loginType, token);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
