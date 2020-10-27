package com.example.tripper_android_app.util;

import android.util.Log;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class SendMessage {

    private static final String TAG = "TAG_SendMessage";
    private AppMessage message;
    private MainActivity activity;
    private boolean isSendOk = false;

    public SendMessage(MainActivity activity, AppMessage message) {
        this.message = message;
        this.activity = activity;
    }

    public boolean sendMessage(){
        if(Common.networkConnected(activity)){
            // message = new AppMessage(msgType, memberId, title, body, stat, sendId, recId);
            String url = Common.URL_SERVER + "FCMServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "sendMsg");
            jsonObject.addProperty("message", new Gson().toJson(message));
            String jsonOut = jsonObject.toString();
            Log.d(TAG, "jsonOut:: " + jsonOut);
            CommonTask sentAddFriendMsgTask = new CommonTask(url, jsonOut);
            try {
                String result = sentAddFriendMsgTask.execute().get();
                int count = Integer.parseInt(result);
                if(count > 0){
                    isSendOk = true;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "請確認網路連線狀態");
        }

        return isSendOk;
    }

    public boolean sendChatMessage(){
        if(Common.networkConnected(activity)){
            // message = new AppMessage(msgType, memberId, title, body, stat, sendId, recId);
            String url = Common.URL_SERVER + "FCMServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "sendChatMsg");
            jsonObject.addProperty("message", new Gson().toJson(message));
            String jsonOut = jsonObject.toString();
            Log.d(TAG, "jsonOut:: " + jsonOut);
            CommonTask sentAddFriendMsgTask = new CommonTask(url, jsonOut);
            try {
                String result = sentAddFriendMsgTask.execute().get();
                int count = Integer.parseInt(result);
                if(count > 0){
                    isSendOk = true;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "請確認網路連線狀態");
        }

        return isSendOk;
    }
}
