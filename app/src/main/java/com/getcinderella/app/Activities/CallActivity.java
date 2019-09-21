package com.getcinderella.app.Activities;

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
import android.os.Handler;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
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

import com.getcinderella.app.Fragments.TwoButtonsDialogFragment;
import com.getcinderella.app.Models.BlockUserModel;
import com.getcinderella.app.Models.SceneModel;
import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.AudioHelper;
import com.getcinderella.app.Utils.ConnectivityUtils;
import com.getcinderella.app.Utils.FirebaseHelper;
import com.getcinderella.app.Utils.Permissions;
import com.getcinderella.app.Utils.ServiceDataHelper;
import com.getcinderella.app.Utils.StringUtils;

import static android.util.Log.d;

public class CallActivity extends BaseActivity implements SensorEventListener {
//    vars
    private String TAG= CallActivity.class.getSimpleName();
    private SinchClient sinchClient;
    private Call call;
    private Boolean isPrivate;
    private SceneModel scene;
    private String partnerPreference;
    private long pixieCost;
    private long curr_pixie;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private String mRemotuserQuote;
    private String userID;
    private String mRemoteUserFbDp;
    private long mRemoteUserCharisma;
    private String mRemoteUserName;
    private String mCallId=null;
    private AudioHelper mAudioHelper;
    private FirebaseHelper firebaseHelper;
    private Vibrator vibe;
    private boolean isIncomingCall;
    private boolean isCallEstablished;
    private boolean isAudioPlaying;
    private boolean isSensorPresent;
    private boolean isPixieUpdated;
    private boolean isInappropriate;
//    widgets
    private LinearLayout call_progressing;
    private LinearLayout call_init;
    private TextView mDuration;
    private TextView sceneOptionTextView;
    private TextView mRemoteUser;
    private ToggleButton ring_control;
    private ToggleButton speaker_control;
    private ImageView close_call;
    private ImageView mRemoteUserDp;
    private ImageView call_warn;
    private UpdateCallDurationTask mDurationTask;
    private Timer mTimer;
    private ImageView end_btn;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (call.getDetails().getDuration()/60==6 && call.getDetails().getDuration()%60==45)
                    {
                        vibe.vibrate(300);
                        Toast.makeText(CallActivity.this, "You will be charged 3 pixies per minute after 7 minutes", Toast.LENGTH_SHORT).show();
                        mDuration.setTextColor(getResources().getColor(R.color.the_temptation));
                    }
                    if (call.getDetails().getDuration()/60>=7 && call.getDetails().getDuration()%60==0 ) {
                        if (curr_pixie >= pixieCost + 3)
                            pixieCost += 3;
                        else {
                            endCall();
                            Toast.makeText(CallActivity.this, "Insufficient pixies!", Toast.LENGTH_SHORT).show();
                        }
                    }
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
        if (ConnectivityUtils.isNetworkSlow(this)){
            Toast.makeText(this,"2G and 3G connections are not supported",Toast.LENGTH_SHORT).show();
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
        mAudioHelper =new AudioHelper(this);
        call_progressing=findViewById(R.id.call_progressing);
        call_init=findViewById(R.id.call_init);
        sceneOptionTextView =findViewById(R.id.sceneOptionTextView);
        mRemoteUser=findViewById(R.id.remoteUser);
        mRemoteUserDp=findViewById(R.id.remoteUserDp);
        ring_control=findViewById(R.id.ring_control);
        ring_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked=ring_control.isChecked();
                if (checked){
                    mAudioHelper.stopRingtone();
                    ring_control.setBackground(getDrawable(R.drawable.ic_ring_off));
                }
                else{
                    mAudioHelper.playMusic();
                    ring_control.setBackground(getDrawable(R.drawable.ic_ring_on));
                }
            }
        });
        speaker_control=findViewById(R.id.speaker_control);
        speaker_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked=speaker_control.isChecked();
                if (!checked){
                    sinchClient.getAudioController().disableSpeaker();
                    speaker_control.setBackground(getDrawable(R.drawable.ic_ring_off));
                }
                else{
                    sinchClient.getAudioController().enableSpeaker();
                    speaker_control.setBackground(getDrawable(R.drawable.ic_ring_on));
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
        call_warn=findViewById(R.id.call_warn);
        call_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwoButtonsDialogFragment.show(
                        getSupportFragmentManager(),
                        "Do you want to report inappropriate behaviour and block partner?",
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                isInappropriate=true;
                                blockUser(call.getRemoteUserId()+"lol");
                                endCall();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);

                            }
                        });
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
        scene =(SceneModel) getIntent().getSerializableExtra("scene");
        Bundle callSettings = getIntent().getExtras();
        isPrivate = callSettings.getBoolean("isPrivate");
//        partnerPreference = callSettings.getString("partnerPreference");
        pixieCost = callSettings.getLong("pixieCost");
        curr_pixie=callSettings.getLong("pixies");
//        switch(partnerPreference){
//            case "Man": partnerPreference="1";
//                break;
//            case "Woman":partnerPreference="2";
//                break;
//            case "Any":partnerPreference="3";
//        }
        partnerPreference="1";
        if(isPrivate)
            partnerPreference="1"+"1";
        isCallEstablished=false;
        isAudioPlaying=false;
        isIncomingCall=false;
        isPixieUpdated=false;
        isInappropriate=false;
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
            makeCall();
        }
        firebaseHelper.addUserToChannel(scene.getScene_no(),partnerPreference);
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
    protected void onDestroy() {
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        mAudioHelper.stopRingtone();
        if(isCallEstablished && !isPixieUpdated)
            firebaseHelper.updatePixie(curr_pixie-pixieCost);
        if (sinchClient != null) {
            sinchClient.terminate();
            sinchClient = null;
        }
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mAudioHelper.stopRingtone();
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

        } else {
            params.screenBrightness = -1.0f;
            this.getWindow().setAttributes(params);
            enableDisableViewGroup((ViewGroup)findViewById(R.id.CALL_LAYOUT).getParent(),true);

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
                makeCall();
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

    public void makeCall(){
        if (!isAudioPlaying){
            mAudioHelper.playMusic();
            isAudioPlaying=true;
        }
        try {
            firebaseHelper.getRef().child("m").child(userID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String rcvrID=snap.getValue(String.class);
                            findIsPrivate(rcvrID);
                            makeCall(rcvrID);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!isCallEstablished){
                                            firebaseHelper.removeUserFromMatched();
                                        }
                                    }catch(Exception ignore){

                                    }
                                }
                            }, 7000);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){
            Log.e("lol","Ki holo");
        }

    }


    public void makeCall(String rcvrID){
        if(rcvrID!=null && !rcvrID.equals("1")) {
            try {
                call = sinchClient.getCallClient().callUser(rcvrID);
                call.addCallListener(new mCallListener());
            } catch (Exception e) {
                endCall();
            }
        }
    }

    public void findIsPrivate(String rcvrId){
        try {
            firebaseHelper.getRef().child(scene.getScene_no())
                    .child(rcvrId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot prefSnapshot) {
                            if (String.valueOf(prefSnapshot.getValue()).length()==2){
                                isPrivate=true;
                            }else{
                                isPrivate=false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){

        }
    }

    public void endCall() {
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        mAudioHelper.stopRingtone();
        if (call!=null)
            call.hangup();
        if(isCallEstablished && !isInappropriate && mRemoteUserFbDp!=null) {
            firebaseHelper.updatePixie(curr_pixie-pixieCost);
            isPixieUpdated=true;
            Intent intent = new Intent();
            intent.putExtra(getResources().getString(R.string.fb_dp), mRemoteUserFbDp);
            intent.putExtra(getResources().getString(R.string.username),mRemoteUserName);
            intent.putExtra(getResources().getString(R.string.charisma), mRemoteUserCharisma);
            intent.putExtra(getResources().getString(R.string.quote),mRemotuserQuote);
            intent.putExtra(getResources().getString(R.string.uid),call.getRemoteUserId());
            intent.putExtra("isPrivate",isPrivate);
            setResult(Activity.RESULT_OK, intent);
        }
        else if(isInappropriate)
            setResult(2);
        else
            setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void  updateWidgets(String remoteid){
        call_init.setVisibility(View.GONE);
        call_progressing.setVisibility(View.VISIBLE);
        try {
            firebaseHelper.getUserRef()
                    .document(remoteid)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel remoteUser = documentSnapshot.toObject(UserModel.class);
                    mRemoteUserCharisma =remoteUser.getCharisma();
                    mRemoteUserFbDp=remoteUser.getFb_dp();
                    mRemotuserQuote=remoteUser.getQuote();
                    mRemoteUserName=StringUtils.extractFirstName(remoteUser.getUsername());
                    mRemoteUser.setText(mRemoteUserName);
                    mRemoteUserDp.setImageResource((int) remoteUser.getMask());
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
        if (isIncomingCall)
            sceneOptionTextView.setText(scene.getOption0());
        else
            sceneOptionTextView.setText(scene.getOption1());
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

    private void blockUser(String caller_id){
        BlockUserModel usr=new BlockUserModel();
        usr.setCaller_id(caller_id);
        usr.setName(mRemoteUser.getText().toString());
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(this);
        serviceDataHelper.blockUser(usr);
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
            vibe.vibrate(300);
            isCallEstablished=true;
            firebaseHelper.removeUserFromChannel(scene.getScene_no());
            updateWidgets(call.getRemoteUserId());
            mAudioHelper.stopRingtone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = sinchClient.getAudioController();
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "CallActivity progressing");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            sinchClient.getAudioController().disableSpeaker();
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
            findIsPrivate(call.getRemoteUserId());
            call.answer();
            isIncomingCall=true;
            call.addCallListener(new mCallListener());
        }
    }

}
