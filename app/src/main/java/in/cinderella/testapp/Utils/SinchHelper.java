package in.cinderella.testapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import in.cinderella.testapp.R;

public class SinchHelper {
    private SinchClient sinchClient;
    private Call call;
    private String TAG="SinchHelper.class";
    private Context mContext;
    private String userID;

    public SinchHelper(Context context,String uid){
        userID=uid;
        mContext = context;
        sinchClient = Sinch.getSinchClientBuilder()
                .context(mContext)
                .userId(userID)
                .applicationKey(mContext.getString(R.string.sinch_key))
                .applicationSecret(mContext.getString(R.string.sinch_secret))
                .environmentHost("sandbox.sinch.com")
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

    }

    public void makeCall(String rcvrID){
        call=sinchClient.getCallClient().callUser(rcvrID);
        call.addCallListener(new SinchCallListener());
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {

        }

        @Override
        public void onCallProgressing(Call progressingCall) {

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Toast.makeText(mContext, "incoming call", Toast.LENGTH_SHORT).show();
            call.answer();
            call.addCallListener(new SinchCallListener());
        }
    }

}
