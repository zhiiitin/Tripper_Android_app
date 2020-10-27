package com.example.tripper_android_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.app.NativeActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.tripper_android_app.chat.ChatMainFragment;
import com.example.tripper_android_app.notify.NotifyFragment;
import com.example.tripper_android_app.util.Common;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 設定app在背景時收到FCM，會自動顯示notification（前景時則不會自動顯示）
        // 如果想要很炫的通知方式就先在manifests註冊，然後在這邊做詳細設定
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "default";
            String channelName = "tripper";
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            ));
        }

        // 當notification被點擊時才會取得自訂資料
        // 點擊是呼叫activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String data = bundle.getString("data");
            Log.d(TAG, "dddddd DATA::" + data);
            Common.showToast(this, "DATA::" + data);
            Navigation.findNavController(this,R.id.chatMainFragment).navigate(R.id.action_create_Trip_LocationList_to_createTripLocationDetail);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("會不會執行這段語句");
        System.out.println("會不會執行這段語句");
        System.out.println("會不會執行這段語句");

        int messageType = getIntent().getIntExtra("message",1);
        if(messageType == 1){

            Navigation.findNavController(this,R.id.trip_HomePage).navigate(R.id.chatMainFragment);

        }
    }
}