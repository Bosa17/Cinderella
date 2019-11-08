package com.getcinderella.app.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getcinderella.app.Fragments.RemoteCardDialog;
import com.getcinderella.app.Utils.KeyboardUtils;
import com.getcinderella.app.Utils.MessageAdapter;
import com.getcinderella.app.Utils.listener.ChatListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

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
import com.getcinderella.app.Utils.ServiceDataHelper;
import com.getcinderella.app.Utils.StringUtils;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;


public class MatchActivity extends BaseActivity implements ChatListener, MessageClientListener {
//    vars
    private String TAG= MatchActivity.class.getSimpleName();
    private MessageAdapter mMessageAdapter;
    private SceneModel scene;
    private String partnerPreference;
    private long pixieCost;
    private long curr_pixie;
    private int remoteMask;
    private String mRemotuserID;
    private String mRemotuserQuote;
    private String userID;
    private String mRemoteUserFbDp;
    private long mRemoteUserCharisma;
    private String mRemoteUserName;
    private AudioHelper mAudioHelper;
    private String roomId;
    private String participId;
    private String oppParticipId;
    private FirebaseHelper firebaseHelper;
    private Vibrator vibe;
    private long sec;
    private boolean isChatEstablished;
    private boolean isAudioPlaying;
    private boolean isPixieUpdated;
    private boolean isInappropriate;
    private boolean isPrivate;
//    widgets
    private CoordinatorLayout chat_progressing;
    private LinearLayout chat_init;
    private LinearLayout chat_init_area;
    private LinearLayout chat_matched;
    private TextView mDuration;
    private TextView sceneOptionTextView;
    private TextView sceneDescTextView;
    private TextView mRemoteUser;
    private TextView chat_init_txt;
    private EditText chatTxt;
    private ToggleButton ring_control;
    private ImageView close_call;
    private ImageView mRemoteUserDp;
    private ImageView chat_init_mask;
    private ImageView call_warn;
    private ImageView mBtnSend;
    private UpdateChatDurationTask mDurationTask;
    private Timer mTimer;
    private ImageView end_btn;

    private class UpdateChatDurationTask extends TimerTask {

        @Override
        public void run() {
            MatchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sec/60==11 && sec%60==45)
                    {
                        vibe.vibrate(300);
                        Toast.makeText(MatchActivity.this, "You will be charged 3 pixies per minute after 12:00 ", Toast.LENGTH_SHORT).show();
                        mDuration.setTextColor(getResources().getColor(R.color.the_temptation));
                    }
                    if (sec/60>=12 && sec%60==0 ) {
                        if (curr_pixie >= pixieCost + 3)
                            pixieCost += 3;
                        else {
                            endChat();
                            Toast.makeText(MatchActivity.this, "Insufficient pixies!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    mDuration.setText(formatTimespan(sec++));
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
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_match);
        firebaseHelper=new FirebaseHelper(this);
        userID=firebaseHelper.getUserID();
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mAudioHelper =new AudioHelper(this);
        chat_progressing =findViewById(R.id.chat_progressing);
        chat_init =findViewById(R.id.chat_init);
        chat_init_area =findViewById(R.id.chat_init_area);
        chat_matched=findViewById(R.id.chat_matched);
        chatTxt=findViewById(R.id.chatTxt);
        sceneOptionTextView =findViewById(R.id.sceneOptionTextView);
        sceneDescTextView =findViewById(R.id.sceneDescTextView);
        mRemoteUser=findViewById(R.id.remoteUser);
        mRemoteUserDp=findViewById(R.id.remoteUserDp);
        chat_init_mask = findViewById(R.id.chat_init_mask);
        chat_init_txt=findViewById(R.id.chat_init_txt);
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
                Toast.makeText(MatchActivity.this,"Chat Ended",Toast.LENGTH_LONG).show();
                endChat();
            }
        });
        call_warn=findViewById(R.id.chat_warn);
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
                                blockUser(mRemotuserID);
                                endChat();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);

                            }
                        });
            }
        });
        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.chats);
        messagesList.setAdapter(mMessageAdapter);
        mBtnSend = (ImageView) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        mDuration=findViewById(R.id.chatDuration);
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
        sec=0;
        mRemotuserID=null;
        roomId=null;
        participId=null;
        isChatEstablished =false;
        isAudioPlaying=false;
        isPixieUpdated=false;
        isInappropriate=false;
        firebaseHelper.addUserToChannel(scene.getScene_no(),partnerPreference);
    }


    @Override
    protected void onDestroy() {
        getSinchServiceInterface().removeMessageClientListener(this);
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        if(roomId!=null)
            firebaseHelper.removeRoom(roomId);
        mAudioHelper.stopRingtone();
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        if(isChatEstablished && !isPixieUpdated)
            firebaseHelper.updatePixie(curr_pixie-pixieCost);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isChatEstablished) {
            try {
                mDurationTask.run();
                mTimer.schedule(mDurationTask, 0, 1000);
            } catch (Exception e) {

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAudioHelper.stopRingtone();
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
    }


    @Override
    public void onBackPressed() {
        TwoButtonsDialogFragment.show(
                getSupportFragmentManager(),
                getString(R.string.dlg_chat_back_confirm),
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        endChat();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                    }
                });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        super.onServiceConnected(componentName, iBinder);
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient();
        }
        initChat();

    }

    private void initChat(){
        if (!isAudioPlaying){
            mAudioHelper.playMusic();
            isAudioPlaying=true;
        }
        try {
            firebaseHelper.getRef().child("m").child(userID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String comboId=snap.getValue(String.class);
                            if (comboId!=null) {
                                initChatRoom(comboId);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (!isChatEstablished) {
                                                firebaseHelper.removeUserFromChannel(scene.getScene_no());
                                                firebaseHelper.addUserToChannel(scene.getScene_no(),partnerPreference);
                                            }
                                        } catch (Exception ignore) {

                                        }
                                    }
                                }, 7000);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){}
        getSinchServiceInterface().addMessageClientListener(this);
    }

    private void initChatRoom(String comboId){
        String [] combo=StringUtils.extractRoomIdandParticipID(comboId);
        roomId=combo[0];
        participId=combo[1];
        oppParticipId=participId.equals("1")?"2":"1";
        onTypeButtonEnable();
        setPrivate();
        setTyping(false);
        initOpponent();
    }
    public void initOpponent(){
        getOpponentId();
        findIsPrivate();

    }
    private void setPrivate(){
        firebaseHelper.setPrivate(roomId,participId,isPrivate);
    }
    private void setTyping(boolean isTyping){
        firebaseHelper.setTyping(roomId,participId,isTyping);
    }
    private void setRemoteTyping(boolean isRemoteTyping){
        if (isRemoteTyping) {
            findViewById(R.id.isRemoteTypingView).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.isRemoteTypingView).setVisibility(View.GONE);
        }
    }
    private void findIsPrivate(){
        try {
            firebaseHelper.getRef().child("h").child(roomId).child("p"+oppParticipId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String p=snap.getValue(String.class);
                            if (p!=null)
                                isPrivate= p.equals("t");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){

        }
    }

    private void getOpponentId(){
        try {
            firebaseHelper.getRef().child("h").child(roomId).child(participId+'p')
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            mRemotuserID=snap.getValue(String.class);
                            updateWidgets();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){

        }
    }

    private void onTypeButtonEnable(){
        chatTxt.addTextChangedListener(new TextWatcher() {
            boolean isTyping = false;
            private Timer timer = new Timer();
            private final long DELAY = 3000; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mBtnSend.setEnabled(true);
                } else {
                    mBtnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isTyping) {
                    Log.d(TAG, "started typing");
                    setTyping(true);
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                Log.d(TAG, "stopped typing");
                                setTyping(false);
                            }
                        },
                        DELAY
                );
            }
        });
    }

    private void sendMessage() {

        String textBody = chatTxt.getText().toString().trim();
        if (textBody.length()>0) {
            getSinchServiceInterface().sendMessage(mRemotuserID, textBody);
            chatTxt.setText("");
            KeyboardUtils.hideKeyboard(this);
        }
    }


    public void endChat() {
        if(roomId!=null)
            firebaseHelper.removeRoom(roomId);
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        mAudioHelper.stopRingtone();
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        if(isChatEstablished && !isInappropriate && mRemoteUserFbDp!=null) {
            firebaseHelper.updatePixie(curr_pixie-pixieCost);
            isPixieUpdated=true;
            new RemoteCardDialog.Builder(this,mRemotuserID,mRemoteUserCharisma,mRemoteUserFbDp,mRemoteUserName,mRemotuserQuote,isPrivate).setOnDismissListener(new remoteCardDismissListener()).build().show();
            setResult(Activity.RESULT_OK);
        }
        else if(isInappropriate) {
            setResult(2);
            getSinchServiceInterface().removeMessageClientListener(this);
            finish();
        }
        else {
            setResult(Activity.RESULT_CANCELED);
            getSinchServiceInterface().removeMessageClientListener(this);
            finish();
        }
    }
    private class remoteCardDismissListener implements BlurPopupWindow.OnDismissListener {
        @Override
        public void onDismiss(BlurPopupWindow popupWindow) {
            getSinchServiceInterface().removeMessageClientListener(MatchActivity.this);
            finish();
        }
    }
    private void  updateWidgets(){
        chat_init.setVisibility(View.GONE);
        chat_matched.setVisibility(View.VISIBLE);
        if (participId.equals("1")) {
            sceneOptionTextView.setText(scene.getOption0());
            mMessageAdapter.addUser("You("+scene.getOption1()+")");
        }
        else {
            sceneOptionTextView.setText(scene.getOption1());
            mMessageAdapter.addUser("You("+scene.getOption0()+")");
        }
        try {
            firebaseHelper.getUserRef()
                    .document(mRemotuserID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel remoteUser = documentSnapshot.toObject(UserModel.class);
                    mRemoteUserCharisma =remoteUser.getCharisma();
                    mRemoteUserFbDp=remoteUser.getFb_dp();
                    mRemotuserQuote=remoteUser.getQuote();
                    mRemoteUserName=StringUtils.extractFirstName(remoteUser.getUsername());
                    remoteMask=(int) remoteUser.getMask();
                    mRemoteUser.setText(mRemoteUserName);
                    mRemoteUserDp.setImageResource( remoteMask);
                    mMessageAdapter.addOppUser(mRemoteUserName+" ("+sceneOptionTextView.getText()+")",remoteMask);
                }
            });
        }catch(Exception e){
            Toast.makeText(this,"Could not Connect to User",Toast.LENGTH_SHORT).show();
            endChat();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    chatEstablished();
                } catch (Exception ignore) {
                    endChat();
                }
            }
        }, 3000);
    }

    private String formatTimespan(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }



    private void initChatWidgets(){

        chat_matched.setVisibility(View.GONE);
        chat_progressing.setVisibility(View.VISIBLE);
        chat_init_mask.setImageResource(remoteMask);
        sceneDescTextView.setText(scene.getDesc());
        String sceneOption;
        if (participId.equals("1"))
            sceneOption=scene.getOption0();
        else
            sceneOption=scene.getOption1();
        chat_init_txt.setText("You are now chatting with "+mRemoteUserName+"  who is playing as the "+sceneOption);
        mDuration.setVisibility(View.VISIBLE);
        mTimer = new Timer();
        mDurationTask = new UpdateChatDurationTask();
        mTimer.schedule(mDurationTask, 0, 1000);
        try {
            firebaseHelper.getRef().child("h").child(roomId).child("t"+oppParticipId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String isT=snap.getValue(String.class);
                            if (isT==null){
                                chatEnded();
                                return;
                            }
                            if (isT.equals("t"))
                                setRemoteTyping(true);
                            else
                                setRemoteTyping(false);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }catch(Exception ignore){
            endChat();
        }
    }
    private void blockUser(String caller_id){
        BlockUserModel usr=new BlockUserModel();
        usr.setCaller_id(caller_id);
        usr.setName(mRemoteUser.getText().toString());
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(this);
        serviceDataHelper.blockUser(usr);
    }

    @Override
    public void chatEstablished() {
        vibe.vibrate(300);
        isChatEstablished =true;
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        initChatWidgets();
        mAudioHelper.stopRingtone();

    }

    @Override
    public void chatEnded() {
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        Toast.makeText(this,"Chat Ended",Toast.LENGTH_LONG).show();
        endChat();
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
        chat_init_area.setVisibility(View.GONE);
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
        chat_init_area.setVisibility(View.GONE);
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d(TAG, "onDelivered");
    }

}
