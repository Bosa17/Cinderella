package com.getcinderella.app.Activities;

import android.app.Activity;
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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getcinderella.app.Fragments.RemoteCardDialog;
import com.getcinderella.app.Models.RemoteUserConnection;
import com.getcinderella.app.Utils.ChatService;
import com.getcinderella.app.Utils.MessageAdapter;
import com.getcinderella.app.Utils.listener.ChatListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.getcinderella.app.Fragments.TwoButtonsDialogFragment;
import com.getcinderella.app.Models.SceneModel;
import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.AudioHelper;
import com.getcinderella.app.Utils.ConnectivityUtils;
import com.getcinderella.app.Utils.FirebaseHelper;
import com.getcinderella.app.Utils.ServiceDataHelper;
import com.getcinderella.app.Utils.StringUtils;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;


public class MatchActivity extends BaseActivity implements ChatListener{
//    vars
    private String TAG= MatchActivity.class.getSimpleName();
    private String mChatType;
    private MessageAdapter mMessageAdapter;
    private SceneModel scene;
    private String partnerPreference;
    private long pixieCost;
    private long curr_pixie;
    private int remoteMask;
    private String mRemoteUserID;
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
    private InterstitialAd mInterstitialAd;
    private boolean isChatEstablished;
    private boolean isPixieUpdated;
    private boolean isInappropriate;
    private boolean isPrivate;
    private boolean isPrivateTmp;
    private boolean isDeclined;
    private boolean isCancelled;
    private boolean isMatched;
    private boolean isInitiated;
    private boolean isChatEnded;
//    widgets
    private CoordinatorLayout chat_progressing;
    private LinearLayout chat_init;
    private LinearLayout chat_init_area;
    private LinearLayout incoming;
    private LinearLayout chat_matched;
    private TextView mDuration;
    private TextView sceneOptionTextView;
    private TextView sceneDescTextView;
    private TextView sceneDescTextView_incoming;
    private TextView mRemoteUser;
    private TextView chat_init_txt;
    private TextView state_outgoing;
    private EditText chatTxt;
    private TextView remoteUser_incoming;
    private ImageView remoteMask_incoming;
    private ToggleButton ring_control;
    private ImageView close_call;
    private ImageView close_call_matched;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        setContentView(R.layout.activity_match);
        firebaseHelper=new FirebaseHelper(this);
        userID=firebaseHelper.getUserID();
        firebaseHelper.setUnavailable();
        new ServiceDataHelper(this).putIsOnCall(true);
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mAudioHelper =new AudioHelper(this);
        chat_progressing =findViewById(R.id.chat_progressing);
        incoming=findViewById(R.id.incoming);
        chat_init =findViewById(R.id.chat_init);
        chat_init_area =findViewById(R.id.chat_init_area);
        chat_matched=findViewById(R.id.chat_matched);
        chatTxt=findViewById(R.id.chatTxt);
        state_outgoing=findViewById(R.id.state_outgoing);
        sceneOptionTextView =findViewById(R.id.sceneOptionTextView);
        sceneDescTextView =findViewById(R.id.sceneDescTextView);
        sceneDescTextView_incoming=findViewById(R.id.sceneDescTextView_incoming);
        mRemoteUser=findViewById(R.id.remoteUser);
        mRemoteUserDp=findViewById(R.id.remoteUserDp);
        remoteMask_incoming=findViewById(R.id.remoteMask_incoming);
        remoteUser_incoming=findViewById(R.id.remoteUser_incoming);
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
        close_call_matched=findViewById(R.id.closecall_matched);
        close_call_matched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        end_btn=findViewById(R.id.hangupButton);
        end_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChatEnded=true;
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
                                isChatEnded=true;
                                isInappropriate=true;
                                blockUser(mRemoteUserID);
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
        mDuration=findViewById(R.id.chatDuration);
        mChatType =getIntent().getStringExtra(ChatService.CHAT_TYPE);
        if (mChatType.equals("0")){
            chat_init.setVisibility(View.GONE);
        }
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        sec=0;
        mRemoteUserID =null;
        roomId=null;
        participId=null;
        isDeclined=false;
        isMatched=false;
        isInitiated=false;
        isChatEstablished =false;
        isPixieUpdated=false;
        isInappropriate=false;
        isCancelled=false;
        isChatEnded=false;
        if(mChatType.equals("1")) {
            mAudioHelper.playMusic();
            scene = (SceneModel) getIntent().getSerializableExtra("scene");
            Bundle callSettings = getIntent().getExtras();
            isPrivateTmp = callSettings.getBoolean("isPrivate");
            isPrivate=isPrivateTmp;
            partnerPreference = callSettings.getString("partnerPreference");
            switch(partnerPreference){
                case "Man": partnerPreference="1";
                    break;
                case "Woman":partnerPreference="2";
                    break;
                case "Any":if(new Random().nextInt(3)==1){
                                partnerPreference="2";
                            }
                            else{
                                partnerPreference="1";
                            }
            }
            pixieCost = callSettings.getLong("pixieCost");
            curr_pixie = callSettings.getLong("pixies");
            firebaseHelper.addUserToChannel(scene.getScene_no(),partnerPreference);
            initChat();
        }
        else{
            String [] combo=StringUtils.extractRoomIdandParticipID(getIntent().getStringExtra("comboID"));
            roomId=combo[0];
            participId=combo[1];
            oppParticipId=participId.equals("1")?"2":"1";
            mAudioHelper.playRingtone();
            isPrivate=getIntent().getBooleanExtra("isPrivate",false);
            scene=new ServiceDataHelper(this).getScene(getIntent().getStringExtra("scene"));
            pixieCost=isPrivate?5:3;
            isMatched=true;
            curr_pixie=getIntent().getLongExtra("pixies",0);
            firebaseHelper.getRef().child("h").child(roomId).child(participId+"p")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String isC=snap.getValue(String.class);
                            if(isC==null && !isChatEnded ){
                                isChatEnded=true;
                                endChat();
                            }
                            else{
                                initChatRoom(getIntent().getStringExtra("comboID"));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }


    @Override
    protected void onStop() {
        if (!isChatEnded) {
            new ServiceDataHelper(this).putIsOnCall(false);
            if (isChatEstablished) {
                mAudioHelper.stopRingtone();
                if (scene != null)
                    firebaseHelper.removeUserFromChannel(scene.getScene_no());
                if (roomId != null)
                    firebaseHelper.endChat(roomId);
                try {
                    mDurationTask.cancel();
                    mTimer.cancel();
                } catch (Exception e) {
                    Log.d(TAG, "No Timer ");
                }
                if (!isPixieUpdated)
                    firebaseHelper.updatePixie(curr_pixie - pixieCost);
                if (!isPrivate && !isPixieUpdated && mRemoteUserID != null) {
                    RemoteUserConnection remoteUser = new RemoteUserConnection();
                    remoteUser.setRemoteUserId(mRemoteUserID);
                    remoteUser.setRemoteUserQuote(mRemotuserQuote);
                    remoteUser.setRemoteUserName(mRemoteUserName);
                    remoteUser.setRemoteUserCharisma(mRemoteUserCharisma);
                    remoteUser.setRemoteUserDp(mRemoteUserFbDp);
                    ServiceDataHelper dataHelper = new ServiceDataHelper(this);
                    dataHelper.saveRemoteTmp(remoteUser);
                }
                finish();
            } else if (mChatType.equals("1") && scene != null) {
                firebaseHelper.removeUserFromChannel(scene.getScene_no());
                if (roomId != null)
                    firebaseHelper.endChat(roomId);
            }
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
    protected void onPause() {
        super.onPause();
        mAudioHelper.stopRingtone();
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
                        isCancelled=true;
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
        if (curr_pixie<3) {
            declineClicked();
            Toast.makeText(this, "Sorry!! You have insufficient Pixies. Invite ar watch ad to earn more for free", Toast.LENGTH_SHORT).show();
        }
        try {
            if(!isChatEnded)
            firebaseHelper.getRef().child("h").child(roomId).child("c")
                    .setValue("t");
        }catch(Exception ignore){}
        chatEstablished();
    }

    private void declineClicked() {
        mAudioHelper.stopRingtone();
        endChat();
    }

    private void initChat(){
        try {
            firebaseHelper.getRef().child("m").child(userID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String comboId=snap.getValue(String.class);
                            if (comboId!=null && !isMatched) {
                                isMatched=true;
                                initChatRoom(comboId);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch(Exception ignore){}
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
        getMessages();
    }
    public void initOpponent(){
        getOpponentId();
        findIsPrivate();

    }
    private void setPrivate(){
        if (! isChatEnded)
            firebaseHelper.setPrivate(roomId,participId,isPrivate);
    }
    private void setTyping(boolean isTyping){
        if (!isChatEnded)
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
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String p=snap.getValue(String.class);
                            if (p!=null)
                                isPrivate=(boolean)p.equals("t");
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
                            String tmpid =snap.getValue(String.class);
                            if (tmpid!=null) {
                                mRemoteUserID=tmpid;
                                isMatched = true;
                                updateWidgets();
                            }
                            else{
                                if (mChatType.equals("0")&&!isChatEnded) {
                                    isChatEnded = true;
                                    endChat();
                                }
                            }
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


    public void endChat() {
        isChatEnded=true;
        new ServiceDataHelper(this).putIsOnCall(false);
        if(roomId!=null)
            firebaseHelper.endChat(roomId);
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        mAudioHelper.stopRingtone();
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        if(isChatEstablished && !isInappropriate && mRemoteUserFbDp!=null) {
            firebaseHelper.updatePixie(curr_pixie-pixieCost);
            isPixieUpdated=true;
            new RemoteCardDialog.Builder(this, mRemoteUserID,mRemoteUserCharisma,mRemoteUserFbDp,mRemoteUserName,mRemotuserQuote,isPrivate).setOnDismissListener(new remoteCardDismissListener()).build().show();
        }
        else if(isInappropriate) {
            setResult(2);
            finish();
        }
        else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        roomId=null;
    }
    private class remoteCardDismissListener implements BlurPopupWindow.OnDismissListener {
        @Override
        public void onDismiss(BlurPopupWindow popupWindow) {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();
            setResult(Activity.RESULT_OK);
            MatchActivity.this.finish();
        }
    }
    private void  updateWidgets(){
        if (mChatType.equals("0"))
            updateWidgetsIncoming();
        else {
            chat_init.setVisibility(View.GONE);

            if (participId.equals("1")) {
                sceneOptionTextView.setText(scene.getOption0());
                mMessageAdapter.addUser("You(" + scene.getOption1() + ")");
            } else {
                sceneOptionTextView.setText(scene.getOption1());
                mMessageAdapter.addUser("You(" + scene.getOption0() + ")");
            }
            try {
                firebaseHelper.getUserRef()
                        .document(mRemoteUserID)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserModel remoteUser = documentSnapshot.toObject(UserModel.class);
                        mRemoteUserCharisma = remoteUser.getCharisma();
                        mRemoteUserFbDp = remoteUser.getFb_dp();
                        mRemotuserQuote = remoteUser.getQuote();
                        mRemoteUserName = StringUtils.extractFirstName(remoteUser.getA());
                        remoteMask = (int) remoteUser.getMask();
                        mRemoteUser.setText(mRemoteUserName);
                        mRemoteUserDp.setImageResource(remoteMask);
                        chat_matched.setVisibility(View.VISIBLE);
                    }
                });
                firebaseHelper.getRef().child("h").child(roomId).child("c")
                        .addValueEventListener(new ValueEventListener() {
                            private final Handler handler=new Handler();
                            private final long DELAY = 30000; // milliseconds
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snap) {
                                String c=snap.getValue(String.class);
                                if(c!=null && c.equals("t")){
                                    state_outgoing.setText("Answered");
                                    chatEstablished();
                                }
                                else if(c!=null && c.equals("o")){
                                    isInitiated=true;
                                    state_outgoing.setText("Waiting for Response...");
                                    this.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isChatEstablished && new ServiceDataHelper(MatchActivity.this).getIsOnCall()) {
                                                reset();
                                                handler.removeCallbacksAndMessages(null);
                                            }
                                        }
                                    }, DELAY);
                                }
                                else if(c!=null && c.equals("i")){
                                    isDeclined=false;
                                    state_outgoing.setText("Connecting...");
                                    this.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isChatEstablished && !isInitiated && new ServiceDataHelper(MatchActivity.this).getIsOnCall()) {
                                                firebaseHelper.getRef().child("a").child(partnerPreference).child(mRemoteUserID).child("m").setValue("f");
                                                reset();
                                                handler.removeCallbacksAndMessages(null);
                                            }
                                        }
                                    }, 17000);
                                }
                                else if (c==null && !isChatEstablished && !isCancelled){
                                    isDeclined=true;
                                    state_outgoing.setText("Declined");
                                    this.handler.removeCallbacksAndMessages(null);
                                    if(roomId!=null)
                                        firebaseHelper.getRef().child("h").child(roomId).child("c").removeEventListener(this);
                                    reset();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Could not Connect to User", Toast.LENGTH_SHORT).show();
                endChat();
            }

        }
    }


    private void reset(){
        state_outgoing.setText("Resetting...");
        if (roomId!=null)
            firebaseHelper.endChat(roomId);
        mRemoteUser.setText("");
        isInitiated=false;
        isMatched=false;
        roomId=null;
        isPrivate=isPrivateTmp;
        mRemoteUserID =null;
        participId=null;
        oppParticipId=null;
        isDeclined=true;
        chat_matched.setVisibility(View.GONE);
        chat_init.setVisibility(View.VISIBLE);
        firebaseHelper.removeUserFromChannel(scene.getScene_no());
        firebaseHelper.setUnavailable();
        new ServiceDataHelper(this).putIsOnCall(true);
        firebaseHelper.addUserToChannel(scene.getScene_no(),partnerPreference);
    }

    private void  updateWidgetsIncoming(){
        chat_init.setVisibility(View.GONE);
        state_outgoing.setText("Matched...");
        String scene_option;
        if (participId.equals("1")) {
            scene_option=scene.getOption0();
            mMessageAdapter.addUser("You(" +scene.getOption1() + ")");
        } else {
            scene_option=scene.getOption1();
            mMessageAdapter.addUser("You(" + scene.getOption0() + ")");
        }
        try {
            firebaseHelper.getUserRef()
                    .document(mRemoteUserID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel remoteUser = documentSnapshot.toObject(UserModel.class);
                    mRemoteUserCharisma = remoteUser.getCharisma();
                    mRemoteUserFbDp = remoteUser.getFb_dp();
                    mRemotuserQuote = remoteUser.getQuote();
                    mRemoteUserName = StringUtils.extractFirstName(remoteUser.getA());
                    remoteMask = (int) remoteUser.getMask();
                    remoteUser_incoming.setText(mRemoteUserName+"("+scene_option+")");
                    remoteMask_incoming.setImageResource(remoteMask);
                    sceneDescTextView_incoming.setText(scene.getDesc());
                    incoming.setVisibility(View.VISIBLE );
                }
            });
            firebaseHelper.getRef().child("h").child(roomId).child("c")
                    .setValue("o");
            firebaseHelper.getRef().child("h").child(roomId).child(participId+'p')
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String c=snap.getValue(String.class);
                            if(c==null && !isChatEnded){
                                isDeclined=true;
                                isChatEnded=true;
                                endChat();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Could not Connect to User", Toast.LENGTH_SHORT).show();
            endChat();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isChatEstablished && !isDeclined)
                    declineClicked();
            }
        }, 30000);
    }
    private String formatTimespan(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }



    private void initChatWidgets(){
        incoming.setVisibility(View.GONE);
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
        mMessageAdapter.addOppUser(mRemoteUserName + " (" + sceneOption + ")", remoteMask);
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
                                if (!isChatEnded)
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
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(this);
        serviceDataHelper.blockUser(caller_id);
    }

    @Override
    public void chatEstablished() {
        if (!isChatEstablished) {
            vibe.vibrate(300);
            isChatEstablished = true;
            firebaseHelper.removeUserFromChannel(scene.getScene_no());
            initChatWidgets();
            mAudioHelper.stopRingtone();
        }

    }

    @Override
    public void chatEnded() {
        try {
            mDurationTask.cancel();
            mTimer.cancel();
        }catch (Exception e){ Log.d(TAG, "No Timer ");}
        Toast.makeText(this,"Chat Ended",Toast.LENGTH_LONG).show();
        if(!isChatEnded)
            endChat();
    }


    public void onIncomingMessage( String message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
        chat_init_area.setVisibility(View.GONE);
    }


    public void onMessageSent( String message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
        chat_init_area.setVisibility(View.GONE);
        chatTxt.setText("");
    }

}
