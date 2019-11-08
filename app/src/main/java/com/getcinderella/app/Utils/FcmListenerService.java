package com.getcinderella.app.Utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.RemoteMessage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;


public class FcmListenerService extends FirebaseMessagingService {

    private ServiceDataHelper serviceDataHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Map data = remoteMessage.getData();
        serviceDataHelper=new ServiceDataHelper(getApplicationContext());
        NotificationHelper notificationHelper=new NotificationHelper(getApplicationContext());
        if (isChatRequest(data)) {
            new ServiceConnection() {
                private Map payload;

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (payload != null) {
                        ChatService.ChatServiceInterface chatService = (ChatService.ChatServiceInterface) service;
                        if (chatService != null) {
                            chatService.initChatWithPayload(payload);
                        }
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {}

                public void relayMessageData(Map<String, String> data) {
                    payload = data;
                    getApplicationContext().bindService(new Intent(getApplicationContext(), ChatService.class), this, BIND_AUTO_CREATE);
                }
            }.relayMessageData(data);
        }
        else{
            serviceDataHelper.saveSituationsFromFCM(data);
            notificationHelper.createFCMNotification(data);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    public boolean isChatRequest(Map data){
        return data.get("title") != null && data.get("title").toString().equals("chatService");
    }
}