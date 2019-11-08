package com.getcinderella.app.Activities;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getcinderella.app.Utils.KeyboardUtils;
import com.getcinderella.app.Utils.MessageAdapter;
import com.getcinderella.app.Utils.NotificationHelper;
import com.getcinderella.app.Utils.listener.ChatListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sinch.android.rtc.PushPair;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.getcinderella.app.Fragments.TwoButtonsDialogFragment;
import com.getcinderella.app.Models.BlockUserModel;
import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.AudioHelper;
import com.getcinderella.app.Utils.ConnectivityUtils;
import com.getcinderella.app.Utils.FirebaseHelper;
import com.getcinderella.app.Utils.ServiceDataHelper;
import com.getcinderella.app.Utils.ChatService;
import com.getcinderella.app.Utils.StringUtils;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

public class PartnerChatActivity extends BaseActivity implements ChatListener, MessageClientListener {

    static final String TAG = PartnerChatActivity.class.getSimpleName();
   //vars
    private String mChatType;
    private InterstitialAd mInterstitialAd;
    private FirebaseHelper firebaseHelper;
    private String name;
    private int mask;
    private int pixieCost;
    private long curr_pixie;
    private long sec;
    private String mRemoteUserId;
    private String roomId;
    private String participId;
    private String oppParticipId;
    private Vibrator vibe;
    private MessageAdapter mMessageAdapter;
    private UpdateChatDurationTask mDurationTask;
    private Timer mTimer;
    private boolean isChatEstablished;
    private boolean isPixieUpdated;
    private boolean isDeclined;

    //widgets
    private LinearLayout incoming;
    private LinearLayout outgoing;
    private LinearLayout chat_init_area;
    private CoordinatorLayout progressing;
    private TextView mDuration;
    private TextView remoteUser_incoming;
    private ImageView remoteMask_incoming;
    private TextView remoteUser_progressing;
    private ImageView remoteMask_progressing;
    private ImageView chat_warn;
    private ImageView mBtnSend;
    private TextView remoteUser_outgoing;
    private EditText chatTxt;
    private ImageView remoteMask_outgoing;
    private TextView remoteUserState_outgoing;
    private AudioHelper mAudioHelper;

    private class UpdateChatDurationTask extends TimerTask {

        @Override
        public void run() {
            PartnerChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sec/60==11 && sec%60==45 && mChatType.equals("1"))
                    {
                        vibe.vibrate(300);
                        Toast.makeText(PartnerChatActivity.this, "You will be charged 3 pixies per minute after 12:00", Toast.LENGTH_SHORT).show();
                        mDuration.setTextColor(getResources().getColor(R.color.the_temptation));
                    }
                    if (sec/60>=12 && sec%60==0 && mChatType.equals("1")) {
                        if (curr_pixie >= pixieCost + 3)
                            pixieCost += 3;
                        else {
                            endChat();
                            Toast.makeText(PartnerChatActivity.this, "Insufficient pixies!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    mDuration.setText(formatTimespan(sec++));
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_partner_chat);
        firebaseHelper=new FirebaseHelper(this);
        incoming=findViewById(R.id.incoming);
        outgoing=findViewById(R.id.outgoing);
        progressing=findViewById(R.id.chat_progressing);
        remoteMask_incoming=findViewById(R.id.remoteMask_incoming);
        remoteUser_incoming=findViewById(R.id.remoteUser_incoming);
        remoteMask_outgoing=findViewById(R.id.remoteMask_outgoing);
        remoteUser_progressing=findViewById(R.id.remoteUser_progressing);
        remoteMask_progressing=findViewById(R.id.remoteMask_progressing);
        remoteUser_outgoing=findViewById(R.id.remoteUser_outgoing);
        remoteUserState_outgoing=findViewById(R.id.chatState_outgoing);
        chat_init_area =findViewById(R.id.chat_init_area);
        mDuration=findViewById(R.id.chatDuration);
        chatTxt=findViewById(R.id.chatTxt);
        sec=0;
        roomId=null;
        participId=null;
        isChatEstablished =false;
        isPixieUpdated=false;
        isDeclined=false;
        if (getIntent().getBooleanExtra("isPremium",false))
            pixieCost=0;
        else
            pixieCost=3;
        ImageView progressing_end=findViewById(R.id.hangupButton);
        progressing_end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                endChat();
            }
        });
        ImageView outgoing_end=findViewById(R.id.endChat_outgoing);
        outgoing_end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                firebaseHelper.getRef().child("h").child(roomId).child("2p")
                        .setValue("f");
                endChat();
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
        ImageView block=(ImageView) findViewById(R.id.block);
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwoButtonsDialogFragment.show(
                        getSupportFragmentManager(),
                        "Do you want to BLOCK Partner and end chat?",
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                blockUser(getIntent().getStringExtra("remoteUser"));
                                endChat();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);

                            }
                        });
            }
        });
        chat_warn =(ImageView) findViewById(R.id.chat_warn);
        chat_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwoButtonsDialogFragment.show(
                        getSupportFragmentManager(),
                        "Do you want to report inappropriate behaviour and block partner?",
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                blockUser(mRemoteUserId);
                                endChat();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);

                            }
                        });
            }
        });
        mBtnSend = (ImageView) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        mChatType =getIntent().getStringExtra(ChatService.CHAT_TYPE);
        curr_pixie=getIntent().getLongExtra("pixies",0);
        if (mChatType.equals("1")){
            incoming.setVisibility(View.GONE);
            outgoing.setVisibility(View.VISIBLE );
        }
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.chats);
        messagesList.setAdapter(mMessageAdapter);
        mAudioHelper = new AudioHelper(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }


    @Override
    protected void onServiceConnected() {
        if(mChatType.equals("0")) {
            participId="1";
            oppParticipId="2";
            mAudioHelper.playRingtone();
            mRemoteUserId = getIntent().getStringExtra("remoteUser");
            roomId= getIntent().getStringExtra("roomId");
            updateWidgetsIncoming(mRemoteUserId);
        }
        else if(mChatType.equals("1")){
            participId="2";
            oppParticipId="1";
            mRemoteUserId = getIntent().getStringExtra("remoteUser");
            roomId = firebaseHelper.getRef().child("h").push().getKey();
            remoteUserState_outgoing.setText("Initiating");
            updateWidgetsOutgoing(mRemoteUserId);
            initChatOutgoing();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
    }

    @Override
    protected void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().removeMessageClientListener(this);
        }
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        if (isChatEstablished && mChatType.equals("1") && !isPixieUpdated)
            firebaseHelper.updatePixie(curr_pixie-pixieCost);
        firebaseHelper.removeRoom(roomId);
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

    private void answerClicked() {
        mAudioHelper.stopRingtone();
        initChatIncoming();
    }

    private void declineClicked() {
        mAudioHelper.stopRingtone();
        if (roomId!=null)
            firebaseHelper.getRef().child("h").child(roomId).child("1p")
                .setValue("f");
        endChat();
    }

    private void initChatOutgoing(){
        try {
            firebaseHelper.getRef().child("n").child(firebaseHelper.getUserID()).setValue(roomId+" "+mRemoteUserId);
            firebaseHelper.removeinitChat();
            firebaseHelper.getRef().child("h").child(roomId).child("1p")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String isC=snap.getValue(String.class);
                            if (isC!=null && isC.equals("t")) {
                                initChatRoom();
                            }
                            else if(isC!=null && isC.equals("f")){
                                endChat();
                            }
                            else if(isC!=null && isC.equals("b")){
                                Toast.makeText(PartnerChatActivity.this, "This Partner has BLOCKED you!!", Toast.LENGTH_SHORT).show();
                                endChat();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){}

    }
    private void initChatIncoming(){
        try {
            firebaseHelper.getRef().child("h").child(roomId).child("1p")
                    .setValue("t").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    initChatRoom();
                }
            });
        }catch(Exception ignore){}
    }

    private void initChatRoom(){
        setTyping(false);
        chatEstablished();
        onTypeButtonEnable();
        mMessageAdapter.addUser("");
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
            getSinchServiceInterface().sendMessage(mRemoteUserId, textBody);
            chatTxt.setText("");
            KeyboardUtils.hideKeyboard(this);
        }
    }

    private  void endChat(){
        if(roomId!=null)
            firebaseHelper.removeRoom(roomId);
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        mAudioHelper.stopRingtone();
        if (isChatEstablished && mChatType.equals("1")) {
            firebaseHelper.updatePixie(curr_pixie - pixieCost);
            isPixieUpdated=true;
        }
        if (!isChatEstablished && mChatType.equals("0")) {
            if(name!=null)
                new NotificationHelper(this).createMissedCallNotification("0"+name);
            else{
                new NotificationHelper(this).createMissedCallNotification("1"+mRemoteUserId);
            }
        }
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        getSinchServiceInterface().removeMessageClientListener(this);
        finish();
    }

    private void  updateWidgetsIncoming(String remoteid){
        try {
            firebaseHelper.getUserRef()
                    .document(remoteid)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel remote = documentSnapshot.toObject(UserModel.class);
                    name=StringUtils.extractFirstName(remote.getUsername());
                    mask=(int)remote.getMask();
                    remoteUser_incoming.setText(name);
                    remoteMask_incoming.setImageResource(mask);
                    mMessageAdapter.addOppUser(name,mask);
                }
            });
            firebaseHelper.getRef().child("h").child(roomId).child("2p")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String isC=snap.getValue(String.class);
                            if(isC==null || isC.equals("f")){
                                isDeclined=true;
                                endChat();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception e){
            Toast.makeText(this,"Could not Connect to User",Toast.LENGTH_SHORT).show();
            finish();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isChatEstablished && !isDeclined)
                    declineClicked();
            }
        }, 30000);
    }
    private void  updateWidgetsProgressing(){
        incoming.setVisibility(View.GONE);
        outgoing.setVisibility(View.GONE );
        progressing.setVisibility(View.VISIBLE);
        remoteUser_progressing.setText("You are now chatting with "+name +". Say Hi!");
        remoteMask_progressing.setImageResource(mask);
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

        }

    }
    private void  updateWidgetsOutgoing(String remoteid){
        try {
            firebaseHelper.getUserRef()
                    .document(remoteid)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel remote = documentSnapshot.toObject(UserModel.class);
                    name=StringUtils.extractFirstName(remote.getUsername());
                    mask=(int)remote.getMask();
                    remoteUser_outgoing.setText(name);
                    remoteMask_outgoing.setImageResource(mask);
                    mMessageAdapter.addOppUser(name,mask);
                }
            });
        }catch(Exception e){
            Toast.makeText(this,"Could not Connect to User",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private String formatTimespan(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void blockUser(String caller_id){
        BlockUserModel usr=new BlockUserModel();
        usr.setCaller_id(caller_id);
        if(mChatType.equals("0"))
            usr.setName(remoteUser_incoming.getText().toString());
        else
            usr.setName(remoteUser_progressing.getText().toString());
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(this);
        serviceDataHelper.blockUser(usr);
    }


    @Override
    public void chatEstablished() {
        vibe.vibrate(300);
        mAudioHelper.stopRingtone();
        isChatEstablished =true;
        updateWidgetsProgressing();
        getSinchServiceInterface().addMessageClientListener(this);
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
