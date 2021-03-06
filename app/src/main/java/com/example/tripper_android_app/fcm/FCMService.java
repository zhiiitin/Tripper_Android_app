package com.example.tripper_android_app.fcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.chat.MessageDelegate;
import com.example.tripper_android_app.notify.NotifyFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "TAG_FCMService";
    private final static int CHAT_TYPE = 1;
    private Context context ;
    private int recId = 0 ;


    // 前景接收
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = "";
        String body = "";
        System.out.println("### remoteMessage.getMessageId()::"+remoteMessage.getMessageId());

        if (notification != null) {
            System.out.println("notification != null");
            title = notification.getTitle();
            body = notification.getBody();
            MessageDelegate messageDelegate = MessageDelegate.getInstance();
            messageDelegate.onMessage(body);
        }
        // 取得自訂資料
        Map<String, String> map = remoteMessage.getData();
        String data = map.get("data");

//                    Intent intentGCM = new Intent(this,
//                    NotifyFragment.class);
//            startActivity(intentGCM);
        Log.d(TAG, "FCMService title: " + title + "\nbody: " + body + "\ndata: " + data);
    }



    private void sendNotification(String title, String body, String data) {
        System.out.println("#### sendNotification:: " + title + " " + body + " data::" + data);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        String channelId = "default";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "tripper",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message",CHAT_TYPE);
        intent.putExtra("recId" , recId) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.trippericon)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.trippericon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent,true)
                .build();
        notificationManager.notify(0, notification);
    }


}
