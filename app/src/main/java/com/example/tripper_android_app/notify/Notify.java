package com.example.tripper_android_app.notify;

import com.example.tripper_android_app.fcm.AppMessage;

import java.io.Serializable;

public class Notify extends AppMessage implements Serializable {
    private String nickname;
    private String notifyDateTime;

    public Notify(String msgType, int memberId, String msgTitle, String msgBody, int msgStat, int sendId, int reciverId, String nickname, String notifyDateTime) {
        super(msgType, memberId, msgTitle, msgBody, msgStat, sendId, reciverId);
        this.nickname = nickname;
        this.notifyDateTime = notifyDateTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNotifyDateTime() {
        return notifyDateTime;
    }

    public void setNotifyDateTime(String notifyDateTime) {
        this.notifyDateTime = notifyDateTime;
    }
}
