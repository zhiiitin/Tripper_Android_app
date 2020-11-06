package com.example.tripper_android_app.chat;

import java.util.ArrayList;
import java.util.List;

public class MessageDelegate {


    private static MessageDelegate instance = null;


    public static MessageDelegate getInstance() {
        if (instance == null) {
            instance = new MessageDelegate();
        }
        return instance;
    }


    private final List<OnMessageReceiveListener> listenerList = new ArrayList<>();


    private MessageDelegate() {}


    public void onMessage(String message) {
        listenerList.forEach(listener -> {
            listener.onMessage(message);
        });
    }

    public void addOnMessageReceiveListener(OnMessageReceiveListener listener) {
        listenerList.add(listener);
    }

   
    public void removeOnMessageReceiveListener(OnMessageReceiveListener listener) {
        listenerList.remove(listener);
    }

    public interface OnMessageReceiveListener {
        void onMessage(String message);
    }
}
