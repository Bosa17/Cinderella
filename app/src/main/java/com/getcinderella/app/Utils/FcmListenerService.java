package com.getcinderella.app.Utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;


public class FcmListenerService extends FirebaseMessagingService {
    private final String PREFERENCE_FILE = "com.Cinderella.testapp.push.shared_preferences";
    SharedPreferences sharedPreferences;
    private ServiceDataHelper serviceDataHelper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Map data = remoteMessage.getData();
        Log.d("lol", remoteMessage.toString());
        serviceDataHelper=new ServiceDataHelper(getApplicationContext());
        NotificationHelper notificationHelper=new NotificationHelper(getApplicationContext());
        if (SinchHelpers.isSinchPushPayload(data)) {
            new ServiceConnection() {
                private Map payload;

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Context context = getApplicationContext();

                    if (payload != null) {
                        SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) service;
                        if (sinchService != null) {
                            NotificationResult result = sinchService.relayRemotePushNotificationPayload(payload);
                            // handle result, e.g. show a notification or similar
                            if (result.isValid() && result.isCall()) {
                                CallNotificationResult callResult = result.getCallResult();
                                Map<String, String> customHeaders = callResult.getHeaders();
                                if (callResult.isCallCanceled() || callResult.isTimedOut()) {
                                    String displayName = customHeaders.get("userName");
                                    if (!serviceDataHelper.getBlockUserCallerId().contains(callResult.getRemoteUserId()))
                                        notificationHelper.createMissedCallNotification(displayName != null && !displayName.isEmpty() ? displayName : callResult.getRemoteUserId());
                                }
                            }
                        }
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {}

                public void relayMessageData(Map<String, String> data) {
                    payload = data;
                    getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
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
}