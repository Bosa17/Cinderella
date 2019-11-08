package com.getcinderella.app.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

import java.util.Map;

import com.getcinderella.app.Activities.PartnerChatActivity;
import com.getcinderella.app.R;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

public class ChatService extends Service {
    private static final String ENVIRONMENT = "sandbox.sinch.com";

    public static final String CHAT_TYPE = "CHAT_TYPE";
    static final String TAG = ChatService.class.getSimpleName();

    private ServiceDataHelper dataHelper;
    private ChatServiceInterface mChatServiceInterface = new ChatServiceInterface();
    private SinchClient mSinchClient;

    private StartFailedListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new ServiceDataHelper(getApplicationContext());
        attemptAutoStart();
    }

    private void attemptAutoStart() {
        if (!dataHelper.getUID().equals("") ) {
            start(dataHelper.getUID());
        }
    }

    private void createClient(String userID) {
        mSinchClient = Sinch.getSinchClientBuilder()
                .context(getApplicationContext())
                .userId(userID)
                .applicationKey(getString(R.string.sinch_key))
                .applicationSecret(getString(R.string.sinch_secret))
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportMessaging(true);
        mSinchClient.startListeningOnActiveConnection();

        mSinchClient.addSinchClientListener(new MySinchClientListener());
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        super.onDestroy();
    }

    private void start(String uid) {
        if (mSinchClient == null) {
            createClient(uid);
        }
        boolean permissionsGranted = true;
        try {
            //mandatory checks
            mSinchClient.checkManifest();
        } catch (MissingPermissionException e) {
            permissionsGranted = false;

        }
        if (permissionsGranted) {
            Log.d(TAG, "Starting SinchClient");
            mSinchClient.start();
        }
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mChatServiceInterface;
    }

    public class ChatServiceInterface extends Binder {

        public boolean isStarted() {
            return ChatService.this.isStarted();
        }

        public void sendMessage(String recipientUserId, String textBody) {
            if (isStarted()) {
                WritableMessage message = new WritableMessage(recipientUserId, textBody);
                mSinchClient.getMessageClient().send(message);
            }
        }

        public void startClient(){
            ChatService.this.start(dataHelper.getUID());
        }

        public void addMessageClientListener(MessageClientListener listener) {
            ChatService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            ChatService.this.removeMessageClientListener(listener);
        }

        public void initChatWithPayload(final Map payload){
            if (dataHelper.getUID().isEmpty()) {
                start(dataHelper.getUID());
            }
            if(!dataHelper.getBlockUserCallerId().contains( payload.get("uID").toString())){
                Intent intent = new Intent(ChatService.this, PartnerChatActivity.class);
                intent.putExtra("roomId", payload.get("rID").toString());
                intent.putExtra("remoteUser", payload.get("uID").toString());
                intent.putExtra(CHAT_TYPE, "0");
                intent.putExtra("pixies", dataHelper.getPixies());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ChatService.this.startActivity(intent);
            }
            else {
                FirebaseDatabase.getInstance().getReference().child("h").child( payload.get("rID").toString()).child("1p")
                        .setValue("b");
            }
        }
    }

    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                                                      ClientRegistration clientRegistration) {
        }
    }
}
