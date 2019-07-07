package in.cinderella.testapp.Activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import in.cinderella.testapp.Fragments.TwoButtonsDialogFragment;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.AudioPlayer;
import in.cinderella.testapp.Utils.ConnectivityUtils;
import in.cinderella.testapp.Utils.FirebaseHelper;

import static android.util.Log.d;

public class CallActivity extends AppCompatActivity {
    private String TAG= CallActivity.class.getSimpleName();
    private SinchClient sinchClient;
    private Call call;
    private String userID;
    private AudioPlayer mAudioPlayer;
    private FirebaseHelper firebaseHelper;
    private TextView mDuration;
    private TextView mRemoteUser;
    private ToggleButton ring_control;
    private ImageView close_call;
    private ImageView mRemoteUserDp;
    private UpdateCallDurationTask mDurationTask;
    private Timer mTimer;
    private boolean isCallEstablished;
    private SpinKitView spinKitView;
    private Button end_btn;
    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDuration.setText(updateCallDuration());
                }
            });
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!ConnectivityUtils.isNetworkAvailable(this)){
            Toast.makeText(this,"No Network Available!",Toast.LENGTH_SHORT).show();
            finish();
        }
        firebaseHelper=new FirebaseHelper(this);
        userID=firebaseHelper.getUserID();
        call=null;
//        callHelper=new CallHelper(this);
        sinchInit();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_call);
        mRemoteUser=findViewById(R.id.remoteUser);
        mRemoteUserDp=findViewById(R.id.remoteUserDp);
        spinKitView=findViewById(R.id.spin_kit);
        ring_control=findViewById(R.id.ring_control);
        ring_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked=ring_control.isChecked();
                if (checked){
                    mAudioPlayer.stopRingtone();
                    ring_control.setBackground(getDrawable(R.drawable.ic_ring_on));
                }
                else{
                    mAudioPlayer.playRingtone();
                    ring_control.setBackground(getDrawable(R.drawable.ic_ring_off));
                }
            }
        });
        close_call=findViewById(R.id.closecall);
        close_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        end_btn=findViewById(R.id.hangupButton);
        end_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endCall();
            }
        });
        mDuration=findViewById(R.id.callDuration);
        mAudioPlayer=new AudioPlayer(this);

        isCallEstablished=false;
        firebaseHelper.addUserToChannel();
        makeCall();
    }

    private void sinchInit(){
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(userID)
                .applicationKey(getString(R.string.sinch_key))
                .applicationSecret(getString(R.string.sinch_secret))
                .environmentHost("sandbox.sinch.com")
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sinchClient != null) {
            sinchClient.terminate();
            sinchClient = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        mAudioPlayer.stopRingtone();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isCallEstablished)
            mAudioPlayer.playRingtone();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAudioPlayer.stopRingtone();
    }

    @Override
    public void onBackPressed() {
        TwoButtonsDialogFragment.show(
                getSupportFragmentManager(),
                getString(R.string.dlg_call_back_confirm),
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        endCall();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                    }
                });
    }

    public void makeCall(){
        firebaseHelper.getRef().child(getString(R.string.channel))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot channelSnapshot:dataSnapshot.getChildren()){
                            if (!channelSnapshot.getKey().equals(firebaseHelper.getUserID())){
                                makeCall(channelSnapshot.getKey());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void makeCall(String rcvrID){
        try {
            call=sinchClient.getCallClient().callUser(rcvrID);
            call.addCallListener( new mCallListener());
        }catch(Exception e){
            Toast.makeText(this,"Low Internet Speed",Toast.LENGTH_SHORT).show();
            endCall();
        }
    }

    public void endCall() {
        firebaseHelper.removeUserFromChannel();
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private void  updateWidgets(String remoteid){
        spinKitView.setVisibility(View.GONE);
        close_call.setVisibility(View.GONE);
        mRemoteUser.setVisibility(View.VISIBLE);
        mRemoteUserDp.setVisibility(View.VISIBLE);
        try {
            firebaseHelper.getRef().child(getString(R.string.user_db))
                    .child(remoteid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserModel remoteUser = dataSnapshot.getValue(UserModel.class);
                            mRemoteUser.setText(remoteUser.getUsername());
                            mRemoteUserDp.setImageResource((int) remoteUser.getMask());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception e){
            Toast.makeText(this,"Could not Connect to User",Toast.LENGTH_SHORT).show();
            endCall();
        }
        mDuration.setVisibility(View.VISIBLE);
        end_btn.setVisibility(View.VISIBLE);
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }


    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private String updateCallDuration() {
        if (call != null) {
            return formatTimespan(call.getDetails().getDuration());
        }
        return "";
    }

    public class mCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            d(TAG, "CallActivity ended. Reason: " + cause.toString());
            try {
                mDurationTask.cancel();
                mTimer.cancel();
            }catch (Exception e){ Log.d(TAG, "No Timer ");}
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "CallActivity ended: " + call.getDetails().toString();
            Toast.makeText(CallActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            d(TAG, "CallActivity established");
            isCallEstablished=true;
            firebaseHelper.removeUserFromChannel();
            updateWidgets(call.getRemoteUserId());
            mAudioPlayer.stopRingtone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = sinchClient.getAudioController();
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            d(TAG, "CallActivity progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }
    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Toast.makeText(CallActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            call.answer();
            call.addCallListener(new mCallListener());
        }
    }

}
