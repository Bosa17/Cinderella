package in.cinderella.testapp.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import java.util.Map;

import in.cinderella.testapp.Activities.PartnerCallActivity;
import in.cinderella.testapp.R;

public class SinchService extends Service {
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    public static final String CALL_ID = "CALL_ID";
    public static final String CALL_TYPE = "CALL_TYPE";
    static final String TAG = SinchService.class.getSimpleName();

    private ServiceDataHelper dataHelper;
    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
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
                .userId(userID+"lol")
                .applicationKey(getString(R.string.sinch_key))
                .applicationSecret(getString(R.string.sinch_secret))
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportCalling(true);
        mSinchClient.setSupportManagedPush(true);

        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
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
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public Call callUser(String userId) {
            return mSinchClient.getCallClient().callUser(userId);
        }
        public Call callUserWithHeader(String userId,Map<String,String> header) {
            return mSinchClient.getCallClient().callUser(userId,header);
        }

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String uid) {
            start(uid);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        public NotificationResult relayRemotePushNotificationPayload(final Map payload) {
            if (mSinchClient == null && !dataHelper.getUID().isEmpty()) {
                createClient(dataHelper.getUID());
            } else if (mSinchClient == null && dataHelper.getUID().isEmpty()) {
                Log.e(TAG, "Can't start a SinchClient as no username is available, unable to relay push.");
                return null;
            }
            return mSinchClient.relayRemotePushNotificationPayload(payload);
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

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Intent intent = new Intent(SinchService.this, PartnerCallActivity.class);
            intent.putExtra("remoteUser",call.getRemoteUserId().substring(0,call.getRemoteUserId().length()-3));
            intent.putExtra(CALL_ID,call.getCallId());
            intent.putExtra(CALL_TYPE,"0");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SinchService.this.startActivity(intent);
        }
    }
}
