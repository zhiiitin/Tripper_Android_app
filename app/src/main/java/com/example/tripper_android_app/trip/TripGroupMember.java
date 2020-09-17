package com.example.tripper_android_app.trip;

import java.io.Serializable;

public class TripGroupMember extends TripGroup implements Serializable {
    private String nickName;

    public TripGroupMember(String tripId, int memberId, String nickName) {
        super(tripId, memberId);
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
