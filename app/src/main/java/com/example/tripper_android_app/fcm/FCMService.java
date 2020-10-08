package com.example.tripper_android_app.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "TAG_FCMService";

    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = "";
        String body = "";
        if (notification != null) {
            title = notification.getTitle();
            body = notification.getBody();
        }

        // 取得自訂資料
        Map<String, String> map = remoteMessage.getData();
        String data = map.get("data");
        Log.d(TAG, "title: " + title + "\nbody: " + body + "\ndata: " + data);
    }


}
