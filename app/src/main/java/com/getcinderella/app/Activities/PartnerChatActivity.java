package com.getcinderella.app.Activities;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getcinderella.app.Utils.MessageAdapter;
import com.getcinderella.app.Utils.NotificationHelper;
import com.getcinderella.app.Utils.listener.ChatListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import android.content.Context;
import android.os.Build;
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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.getcinderella.app.Fragments.TwoButtonsDialogFragment;
import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.AudioHelper;
import com.getcinderella.app.Utils.ConnectivityUtils;
import com.getcinderella.app.Utils.FirebaseHelper;
import com.getcinderella.app.Utils.ServiceDataHelper;
import com.getcinderella.app.Utils.ChatService;
import com.getcinderella.app.Utils.StringUtils;

public class PartnerChatActivity extends BaseActivity implements ChatListener {

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
    private boolean partnerDeclined;
    private boolean isDeclined;
    public  boolean isChatEnded;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
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
        isChatEnded =true;
        isChatEstablished =false;
        isPixieUpdated=false;
        partnerDeclined =false;
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
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.chats);
        messagesList.setAdapter(mMessageAdapter);
        mAudioHelper = new AudioHelper(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6258093238303404/8487357353");//ca-app-pub-3940256099942544/1033173712    ca-app-pub-6258093238303404/8487357353
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        firebaseHelper.setUnavailable();
        new ServiceDataHelper(this).putIsOnCall(true);
        if(mChatType.equals("0")) {
            participId="1";
            oppParticipId="2";
            mRemoteUserId = getIntent().getStringExtra("remoteUser");
            roomId= getIntent().getStringExtra("roomId");
            try{
                firebaseHelper.getRef().child("h").child(roomId).child("1p")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                String tmp = snap.getValue(String.class);
                                if(tmp!=null) {
                                    firebaseHelper.getRef().child("h").child(roomId).child("1p")
                                            .setValue("o");
                                    firebaseHelper.setUnavailable();
                                    new ServiceDataHelper(PartnerChatActivity.this).putIsOnCall(true);
                                    mAudioHelper.playRingtone();
                                    updateWidgetsIncoming(mRemoteUserId);
                                }
                                else{
                                    endChat();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            } catch (Exception e) {
                endChat();
            }

        }
        else if(mChatType.equals("1")){
            participId="2";
            oppParticipId="1";
            firebaseHelper.setUnavailable();
            new ServiceDataHelper(this).putIsOnCall(true);
            mAudioHelper.playMusic();
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
    protected void onStop() {
        if (isChatEstablished) {
            new ServiceDataHelper(this).putIsOnCall(false);
            isChatEnded =false;
            try {
                mDurationTask.cancel();
                mTimer.cancel();
            } catch (Exception e) {
                Log.d(TAG, "No Timer ");
            }
            if (mChatType.equals("1") && !isPixieUpdated)
                firebaseHelper.updatePixie(curr_pixie - pixieCost);
            firebaseHelper.endChat(roomId);
        }
        super.onStop();
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
        isDeclined=true;
        endChat();
    }

    private void initChatOutgoing(){
        try {
            firebaseHelper.getRef().child("n").child(firebaseHelper.getUserID()).setValue(roomId+" "+mRemoteUserId);
            firebaseHelper.removeinitChat();
            firebaseHelper.getRef().child("h").child(roomId).child(oppParticipId+"p")
                    .addValueEventListener(new ValueEventListener() {
                        private boolean isInitiated=false;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String isC=snap.getValue(String.class);
                            if (isC!=null && isC.equals("t")) {
                                initChatRoom();
                            }
                            else if(isC!=null && isC.equals("b")){
                                Toast.makeText(PartnerChatActivity.this, "This Partner has BLOCKED you!!", Toast.LENGTH_SHORT).show();
                                endChat();
                            }
                            else if(isC!=null && isC.equals("o")){
                                isInitiated=true;
                                Toast.makeText(PartnerChatActivity.this, "Waiting for response...", Toast.LENGTH_SHORT).show();
                            }
                            else if(isC!=null && isC.equals("i")){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isChatEstablished && !isInitiated && new ServiceDataHelper(PartnerChatActivity.this).getIsOnCall()) {
                                            Toast.makeText(PartnerChatActivity.this, "Could Not Establish Connection to Partner", Toast.LENGTH_SHORT).show();
                                            endChat();
                                        }
                                    }
                                }, 30000);
                            }
                            else if (isInitiated){
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
            firebaseHelper.getRef().child("h").child(roomId).child(participId+"p")
                    .setValue("t");
            initChatRoom();

        }catch(Exception ignore){}
    }

    private void initChatRoom(){
        if (!isChatEstablished) {
            setTyping(false);
            chatEstablished();
            onTypeButtonEnable();
            mMessageAdapter.addUser("");
        }
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

    private void getMessages(){
        firebaseHelper.getRef().child("h").child(roomId).child(participId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        String msg=snap.getValue(String.class);
                        if (msg!=null)
                            onIncomingMessage(msg);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage() {

        String textBody = chatTxt.getText().toString().trim();
        if (textBody.length()>0) {
            firebaseHelper.getRef().child("h").child(roomId).child(oppParticipId).setValue(textBody);
            onMessageSent(textBody);
        }
    }

    private  void endChat(){
        if(roomId!=null)
            firebaseHelper.endChat(roomId);
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        mAudioHelper.stopRingtone();
        if (isChatEstablished && mChatType.equals("1")) {
            firebaseHelper.updatePixie(curr_pixie - pixieCost);
            isPixieUpdated=true;
        }
        if (!isChatEstablished && mChatType.equals("0") && !isDeclined) {
            if(name!=null)
                new NotificationHelper(this).createMissedCallNotification("0"+name);
            else{
                new NotificationHelper(this).createMissedCallNotification("1"+mRemoteUserId);
            }
        }
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        isChatEnded =false;
        new ServiceDataHelper(this).putIsOnCall(false);
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
                    name=StringUtils.extractFirstName(remote.getA());
                    mask=(int)remote.getMask();
                    remoteUser_incoming.setText(name);
                    remoteMask_incoming.setImageResource(mask);
                    mMessageAdapter.addOppUser(name,mask);
                    incoming.setVisibility(View.VISIBLE);
                }
            });
            firebaseHelper.getRef().child("h").child(roomId).child("2p")
                    .addValueEventListener(new ValueEventListener() {
                        boolean hasChatEnded=false;
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String isC=snap.getValue(String.class);
                            if(isC==null ){
                                partnerDeclined =true;
                                hasChatEnded=true;
                                if(!isDeclined)
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
                if (!isChatEstablished && !partnerDeclined)
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
            getMessages();
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
                    name=StringUtils.extractFirstName(remote.getA());
                    mask=(int)remote.getMask();
                    remoteUser_outgoing.setText(name);
                    remoteMask_outgoing.setImageResource(mask);
                    mMessageAdapter.addOppUser(name,mask);
                    outgoing.setVisibility(View.VISIBLE);
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
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(this);
        serviceDataHelper.blockUser(caller_id);
    }


    @Override
    public void chatEstablished() {
        vibe.vibrate(300);
        mAudioHelper.stopRingtone();
        isChatEstablished =true;
        updateWidgetsProgressing();
    }

    @Override
    public void chatEnded() {
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        Toast.makeText(this,"Chat Ended",Toast.LENGTH_LONG).show();
    }


    public void onIncomingMessage( String message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
        chat_init_area.setVisibility(View.GONE);
    }


    public void onMessageSent( String  message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
        chat_init_area.setVisibility(View.GONE);
        chatTxt.setText("");
    }
}
