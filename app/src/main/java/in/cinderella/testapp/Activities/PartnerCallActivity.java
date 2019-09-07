package in.cinderella.testapp.Activities;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import android.content.Context;
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

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.cinderella.testapp.Fragments.TwoButtonsDialogFragment;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.AudioHelper;
import in.cinderella.testapp.Utils.ConnectivityUtils;
import in.cinderella.testapp.Utils.FirebaseHelper;
import in.cinderella.testapp.Utils.SinchService;
import in.cinderella.testapp.Utils.StringUtils;

public class PartnerCallActivity extends BaseActivity implements SensorEventListener {

    static final String TAG = PartnerCallActivity.class.getSimpleName();
    private LinearLayout incoming;
    private LinearLayout outgoing;
    private LinearLayout progressing;
    private String mCallId;
    private String mCallType;
    private FirebaseHelper firebaseHelper;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView mDuration;
    private TextView remoteUser_incoming;
    private ImageView remoteMask_incoming;
    private TextView remoteUser_progressing;
    private ImageView remoteMask_progressing;
    private TextView remoteUser_outgoing;
    private ImageView remoteMask_outgoing;
    private TextView remoteUserState_outgoing;
    private AudioHelper mAudioHelper;
    private String name;
    private int mask;
    private int pixieCost;
    private Call call;
    private UpdateCallDurationTask mDurationTask;
    private Timer mTimer;
    private boolean isCallEstablished;
    private boolean isSensorPresent;
    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            PartnerCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (call.getDetails().getDuration()/60==6 && call.getDetails().getDuration()%60==45)
                    {
                        ((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
                        mDuration.setTextColor(getResources().getColor(R.color.the_temptation));
                    }
                    if (call.getDetails().getDuration()/60>=7 && call.getDetails().getDuration()%60==0)
                        pixieCost+=3;
                    mDuration.setText(updateCallDuration());
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_partner_call);
        firebaseHelper=new FirebaseHelper(this);
        incoming=findViewById(R.id.incoming);
        outgoing=findViewById(R.id.outgoing);
        progressing=findViewById(R.id.call_progressing);
        remoteMask_incoming=findViewById(R.id.remoteMask_incoming);
        remoteUser_incoming=findViewById(R.id.remoteUser_incoming);
        remoteMask_outgoing=findViewById(R.id.remoteMask_outgoing);
        remoteUser_progressing=findViewById(R.id.remoteUser_progressing);
        remoteMask_progressing=findViewById(R.id.remoteMask_progressing);
        remoteUser_outgoing=findViewById(R.id.remoteUser_outgoing);
        remoteUserState_outgoing=findViewById(R.id.callState_outgoing);
        mDuration=findViewById(R.id.callDuration);
        isCallEstablished=false;
        isSensorPresent=true;
        pixieCost=3;
        try {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }catch (Exception e){
            isSensorPresent=false;
        }
        ImageView progressing_end=findViewById(R.id.hangupButton);
        progressing_end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                endCall();
            }
        });
        ImageView outgoing_end=findViewById(R.id.endCall_outgoing);
        outgoing_end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                endCall();
            }
        });
        ImageView answer = (ImageView) findViewById(R.id.answer);
        answer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                answerClicked();
            }
        });
        ImageView decline = (ImageView) findViewById(R.id.decline);
        decline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                declineClicked();
            }
        });
        mCallType=getIntent().getStringExtra(SinchService.CALL_TYPE);
        if (mCallType.equals("1")){
            incoming.setVisibility(View.GONE);
            outgoing.setVisibility(View.VISIBLE );
        }
        mAudioHelper = new AudioHelper(this);

    }


    @Override
    protected void onServiceConnected() {
        if(mCallType.equals("0")) {
            getSinchServiceInterface().getAudioController().enableSpeaker();
            mAudioHelper.playRingtone();
            mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
            String mRemoteUserId = getIntent().getStringExtra("remoteUser");
            updateWidgetsIncoming(mRemoteUserId);
            call = getSinchServiceInterface().getCall(mCallId);
            if (call != null) {
                call.addCallListener(new SinchCallListener());
            } else {
                Log.e(TAG, "Started with invalid callId, aborting");
                finish();
            }
        }
        else if(mCallType.equals("1")){
            String mRemoteUserId = getIntent().getStringExtra("remoteUser");
            String userName=getIntent().getStringExtra("userName");
            Map<String,String> header=new HashMap<>();
            header.put("userName",StringUtils.extractFirstName(userName));
            call=getSinchServiceInterface().callUserWithHeader(mRemoteUserId+"lol",header);
            if (call != null) {
                remoteUserState_outgoing.setText(call.getState().toString());
                call.addCallListener(new SinchCallListener());
            } else {
                Log.e(TAG, "RemoteUser Not found, aborting");
                finish();
            }
            updateWidgetsOutgoing(mRemoteUserId);
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
    protected void onDestroy() {
        super.onDestroy();
        if (isCallEstablished && mCallType.equals("1"))
            firebaseHelper.updatePixie(getIntent().getLongExtra("pixies",0)-pixieCost);
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
            enableDisableViewGroup((ViewGroup)findViewById(R.id.REMOTE_CALL_LAYOUT).getParent(),false);
            Log.e("onSensorChanged","NEAR");
        } else {
            params.screenBrightness = -1.0f;
            this.getWindow().setAttributes(params);
            enableDisableViewGroup((ViewGroup)findViewById(R.id.REMOTE_CALL_LAYOUT).getParent(),true);
            Log.e("onSensorChanged","FAR");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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

    private void answerClicked() {
        mAudioHelper.stopRingtone();
        if (call != null) {
            Log.d(TAG, "Answering call");
            call.answer();
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioHelper.stopRingtone();
        endCall();
    }
    private  void endCall(){
        if (call!=null)
            call.hangup();
        mAudioHelper.stopRingtone();
        mAudioHelper.stopProgressTone();
        if (isCallEstablished && mCallType.equals("1"))
            firebaseHelper.updatePixie(getIntent().getLongExtra("pixies",0)-pixieCost);
        finish();
    }

    private void  updateWidgetsIncoming(String remoteid){
        try {
            firebaseHelper.getRef().child(getString(R.string.user_db))
                    .child(remoteid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserModel remote = dataSnapshot.getValue(UserModel.class);
                            name=StringUtils.extractFirstName(remote.getUsername());
                            mask=(int)remote.getMask();
                            remoteUser_incoming.setText(name);
                            remoteMask_incoming.setImageResource(mask);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception e){
            Toast.makeText(this,"Could not Connect to User",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void  updateWidgetsProgressing(){
        incoming.setVisibility(View.GONE);
        outgoing.setVisibility(View.GONE );
        progressing.setVisibility(View.VISIBLE);
        remoteUser_progressing.setText(name);
        remoteMask_progressing.setImageResource(mask);
        mDuration.setVisibility(View.VISIBLE);
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);

    }
    private void  updateWidgetsOutgoing(String remoteid){
        try {
            firebaseHelper.getRef().child(getString(R.string.user_db))
                    .child(remoteid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserModel remote = dataSnapshot.getValue(UserModel.class);
                            name=StringUtils.extractFirstName(remote.getUsername());
                            mask=(int)remote.getMask();
                            remoteUser_outgoing.setText(name);
                            remoteMask_outgoing.setImageResource(mask);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception e){
            Toast.makeText(this,"Could not Connect to User",Toast.LENGTH_SHORT).show();
            finish();
        }
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

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioHelper.stopProgressTone();
            try {
                mDurationTask.cancel();
                mTimer.cancel();
            }catch (Exception e){ Log.d(TAG, "No Timer ");}
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            endCall();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            isCallEstablished=true;
            updateWidgetsProgressing();
            mAudioHelper.stopRingtone();
            mAudioHelper.stopProgressTone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioHelper.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // no need to implement for managed push
        }

    }
}
