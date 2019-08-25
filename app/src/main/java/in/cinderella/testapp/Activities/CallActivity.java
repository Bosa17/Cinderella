package in.cinderella.testapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.MissingPermissionException;
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
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;
import in.cinderella.testapp.Utils.Permissions;
import in.cinderella.testapp.Utils.SinchService;
import in.cinderella.testapp.Utils.StringUtils;

import static android.util.Log.d;

public class CallActivity extends BaseActivity implements SensorEventListener {
//    vars
    private String TAG= CallActivity.class.getSimpleName();
    private SinchClient sinchClient;
    private Call call;
    private Boolean isPrivate;
    private String chapter;
    private String partnerPreference;
    private long pixieCost;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private String mRemotuserQuote;
    private String userID;
    private String mRemoteUserFbDp;
    private long mRemoteUserKarma;
    private String mRemoteUserName;
    private String mCallId=null;
    private AudioPlayer mAudioPlayer;
    private FirebaseHelper firebaseHelper;
    private Vibrator vibe;
    private boolean isCallEstablished;
    private boolean isAudioPlaying;
    private boolean isSensorPresent;
//    widgets
    private LinearLayout call_progressing;
    private LinearLayout call_init;
    private TextView mDuration;
    private TextView mRemoteUser;
    private ToggleButton ring_control;
    private ImageView close_call;
    private ImageView mRemoteUserDp;
    private UpdateCallDurationTask mDurationTask;
    private Timer mTimer;
    private ImageView end_btn;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_call);
        firebaseHelper=new FirebaseHelper(this);
        userID=firebaseHelper.getUserID();
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        call=null;
        mAudioPlayer=new AudioPlayer(this);
        call_progressing=findViewById(R.id.call_progressing);
        call_init=findViewById(R.id.call_init);
        mRemoteUser=findViewById(R.id.remoteUser);
        mRemoteUserDp=findViewById(R.id.remoteUserDp);
        ring_control=findViewById(R.id.ring_control);
        ring_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked=ring_control.isChecked();
                if (checked){
                    mAudioPlayer.stopRingtone();
                    ring_control.setBackground(getDrawable(R.drawable.ic_ring_off));
                }
                else{
                    mAudioPlayer.playRingtone();
                    ring_control.setBackground(getDrawable(R.drawable.ic_ring_on));
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
        isSensorPresent=true;
        try {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }catch (Exception e){
            isSensorPresent=false;
        }
        Bundle callSettings = getIntent().getExtras();
        chapter = callSettings.getString("chapter");
        isPrivate = callSettings.getBoolean("isPrivate");
        partnerPreference = callSettings.getString("partnerPreference");
        pixieCost = callSettings.getLong("pixieCost");
        switch(partnerPreference){
            case "Man": partnerPreference="1";
                break;
            case "Woman":partnerPreference="2";
                break;
            case "Any":partnerPreference="3";
                break;
        }
        if(isPrivate)
            partnerPreference="1"+partnerPreference;
        isCallEstablished=false;
        isAudioPlaying=false;
        boolean permissionsGranted = true;
        sinchInit();
        try {
            //mandatory checks
            sinchClient.checkManifest();
        } catch (MissingPermissionException e) {
            permissionsGranted = false;
            requestPermissions(Permissions.SINCH_PERMISSIONS, 2);
        }
        if (permissionsGranted) {
            sinchClient.start();
            makeCall(chapter, partnerPreference);
        }
        firebaseHelper.addUserToChannel(chapter,partnerPreference);
    }
    private void sinchInit(){
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(userID)
                .applicationKey(getString(R.string.sinch_key))
                .applicationSecret(getString(R.string.sinch_secret))
                .environmentHost("clientapi.sinch.com")
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new mCallClientListener());
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        getSinchServiceInterface().stopClient();
        unbindService();
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
        if (isSensorPresent)
            mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent)
            mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (sensorEvent.values[0] == 0){
            params.screenBrightness = 0.000f;
            this.getWindow().setAttributes(params);
            enableDisableViewGroup((ViewGroup)findViewById(R.id.CALL_LAYOUT).getParent(),false);
            Log.e("onSensorChanged","NEAR");
        } else {
            params.screenBrightness = -1.0f;
            this.getWindow().setAttributes(params);
            enableDisableViewGroup((ViewGroup)findViewById(R.id.CALL_LAYOUT).getParent(),true);
            Log.e("onSensorChanged","FAR");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            Log.i("resultcode",""+requestCode);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("resultcode",""+requestCode);
                sinchClient.start();
                makeCall(chapter,partnerPreference);
            }
            else {
                Toast.makeText(getParent(),  "Permission Denied", Toast.LENGTH_SHORT).show();
                endCall();
            }
        }
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

    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    public void makeCall(String chapter, String partnerPreference){
        if (!isAudioPlaying){
            mAudioPlayer.playRingtone();
            isAudioPlaying=true;
        }
        firebaseHelper.getRef().child(chapter)
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
            Log.d("lol", "makeCall: "+sinchClient.isStarted()+" "+sinchClient.getLocalUserId());
            call=sinchClient.getCallClient().callUser(rcvrID);
            call.addCallListener( new mCallListener());
        }catch(Exception e){
            Log.i("Call Ended",e.toString());
            endCall();
        }
    }

    public void endCall() {
        firebaseHelper.removeUserFromChannel(chapter);
        if (call!=null)
            call.hangup();
        if(isCallEstablished) {
            Intent intent = new Intent();
            intent.putExtra(getResources().getString(R.string.fb_dp), mRemoteUserFbDp);
            intent.putExtra(getResources().getString(R.string.username),mRemoteUserName);
            intent.putExtra(getResources().getString(R.string.karma),mRemoteUserKarma);
            intent.putExtra(getResources().getString(R.string.quote),mRemotuserQuote);
            intent.putExtra(getResources().getString(R.string.uid),call.getRemoteUserId());
            intent.putExtra("pixie_cost",pixieCost);
            intent.putExtra("isPrivate",isPrivate);
            setResult(Activity.RESULT_OK, intent);
        }
        else
            setResult(Activity.RESULT_CANCELED);
        mAudioPlayer.stopRingtone();
        finish();
    }

    private void  updateWidgets(String remoteid){
        call_init.setVisibility(View.GONE);
        call_progressing.setVisibility(View.VISIBLE);
        try {
            firebaseHelper.getRef().child(getString(R.string.user_db))
                    .child(remoteid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserModel remoteUser = dataSnapshot.getValue(UserModel.class);
                            mRemoteUserKarma=remoteUser.getKarma();
                            mRemoteUserFbDp=remoteUser.getFb_dp();
                            mRemotuserQuote=remoteUser.getQuote();
                            mRemoteUserName=StringUtils.extractFirstName(remoteUser.getUsername());
                            mRemoteUser.setText(mRemoteUserName);
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

    private class mCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "CallActivity ended. Reason: " + cause.toString());
            try {
                mDurationTask.cancel();
                mTimer.cancel();
            }catch (Exception e){ Log.d(TAG, "No Timer ");}
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            d(TAG, "CallActivity established");
            isCallEstablished=true;
            vibe.vibrate(300);
            firebaseHelper.removeUserFromChannel(chapter);
            updateWidgets(call.getRemoteUserId());
            mAudioPlayer.stopRingtone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = sinchClient.getAudioController();
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "CallActivity progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }
    private class mCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            call.answer();
            call.addCallListener(new mCallListener());
        }
    }


}
