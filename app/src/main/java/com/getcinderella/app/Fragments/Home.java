package com.getcinderella.app.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.getcinderella.app.Activities.AliasActivity;
import com.getcinderella.app.Activities.GenderActivity;
import com.getcinderella.app.Activities.MatchActivity;
import com.getcinderella.app.Activities.QuoteActivity;
import com.getcinderella.app.Activities.Select_mask;
import com.getcinderella.app.Models.RemoteUserConnection;
import com.getcinderella.app.Utils.ChatService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import java.util.List;

import com.getcinderella.app.Activities.MainActivity;
import com.getcinderella.app.Activities.Setting;
import com.getcinderella.app.Models.SceneModel;
import com.getcinderella.app.Models.UserModel;
import com.getcinderella.app.R;
import com.getcinderella.app.Utils.ClaimPixiesNotification;
import com.getcinderella.app.Utils.ConnectivityUtils;
import com.getcinderella.app.Utils.DataHelper;
import com.getcinderella.app.Utils.HashtagView;
import com.getcinderella.app.Utils.ServiceDataHelper;
import com.getcinderella.app.Utils.ShakeListener;

public class Home extends Fragment {
    //vars
    private static String TAG=Home.class.getSimpleName();
    private FirebaseFirestore myRef;
    private DatabaseReference sceneRef;
    private DataHelper dataHelper;
    private SceneModel selectedScene;
    private boolean isCardVisible;
    private static int RC_SUCCESS_QUOTE=3;
    private static int RC_SUCCESS_GENDER=4;
    private static int RC_SUCCESS_ALIAS=6;
    private static int  RC_SUCCESS_MASK=5;
    //widgets
    private ImageView privateMode;
    private ImageView phone_shake;
    private ScrollView scrollView;
    private HashtagView hashTagView;
    private RadioGroup PartnerPreferenceRadioGroup;
    private ShakeListener mShaker;
    private TextView pixies;
    private TextView isPremiumTextView;
    private TextSwitcher pixies_cost_switcher;
    private TextSwitcher scene_desc;
    private TextView username;
    private TextView partner;
    private TextView charisma;
    private ImageView tutorial_btn;
    private ImageView settings_btn;
    private ImageView mask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        isCardVisible=false;
        sceneRef = FirebaseDatabase.getInstance().getReference();
        myRef = FirebaseFirestore.getInstance();
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(getActivity());
        serviceDataHelper.setOnSceneChangedListener(new ServiceDataHelper.OnSceneChangedListener() {
            @Override
            public void onSceneChanged() {
                ((MainActivity)getActivity()).refreshHome();
            }
        });

        dataHelper=new DataHelper(getActivity());
        if (dataHelper.getA().equals("")||dataHelper.getGender().equals("")||!dataHelper.getUsername().equals("")){
            dataHelper.putUsername("");
            Log.d("lol",dataHelper.getA()+" "+dataHelper.getGender()+" "+dataHelper.getUsername());
            startAliasActivity();
        }
        scene_desc =view.findViewById(R.id.scene_desc);
        mask=(ImageView) view.findViewById(R.id.user_dp);
        scrollView=view.findViewById(R.id.scroll);
        privateMode=view.findViewById(R.id.private_btn);
        partner =(TextView) view.findViewById(R.id.user_partners);
        isPremiumTextView =(TextView) view.findViewById(R.id.isPremiumTextView);
        pixies=(TextView) view.findViewById(R.id.user_pixies);
        pixies_cost_switcher=(TextSwitcher) view.findViewById(R.id.pixie_cost_switcher);
        username=(TextView) view.findViewById(R.id.username);
        charisma =(TextView)  view.findViewById(R.id.user_charisma);
        final Vibrator vibe = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        animShake.setStartOffset(300);
        phone_shake = (ImageView) view.findViewById(R.id.phone);
        phone_shake.startAnimation(animShake);
        mShaker = new ShakeListener(getContext());
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
            public void onShake()
            {   Rect scrollBounds = new Rect();
                scrollView.getHitRect(scrollBounds);
                if (phone_shake.getLocalVisibleRect(scrollBounds)) {
                    if (selectedScene !=null) {
                        if (getPixieCost() <= dataHelper.getPixies()) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isPrivate", dataHelper.getIsPrivate());
                            bundle.putLong("pixieCost", getPixieCost());
                            bundle.putLong("pixies", dataHelper.getPixies());
                            bundle.putString("mygender",dataHelper.getGender());
                            bundle.putString("partnerPreference", getSelectedPartnerPreference());
                            startActivityForResult(new Intent(getActivity(), MatchActivity.class).putExtras(bundle).putExtra("scene", selectedScene).putExtra(ChatService.CHAT_TYPE,"1"), 2);
                            vibe.vibrate(300);
                        } else {
                            ((MainActivity) getActivity()).navigateToPixie();
                            isCardVisible = true;
                            onPause();
                            Toast.makeText(getContext(), R.string.insufficient_pixies, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getContext(), "Choose a Scene First!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // imageView is not within the visible window
                }

            }
        });
        settings_btn=(ImageView) view.findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
            }
        });
        tutorial_btn=(ImageView) view.findViewById(R.id.tutorial_btn);
        tutorial_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TutorialDialog.Builder(getContext()).build().show();
            }
        });
        privateMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.selectPrivate();
                new IsPrivateDialog.Builder(getContext(),dataHelper.getIsPrivate()).build().show();
                setPixieCost();
                initPrivateMode();
            }
        });
        LinearLayout goto_pixies=view.findViewById(R.id.goto_pixies);
        goto_pixies.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).navigateToPixie();
                isCardVisible = true;
                onPause();
            }
        });
        PartnerPreferenceRadioGroup=view.findViewById(R.id.parter_preference);
        PartnerPreferenceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                setPixieCost();
            }
        });
        if (dataHelper.getIsPremium())
            isPremiumTextView.setVisibility(View.VISIBLE);
        hashTagView =(HashtagView) view.findViewById(R.id.scene_tags);
        initElements();
        initPrivateMode();
        sync();
        pixies_cost_switcher.setFactory(new TextViewFactory(R.style.PixieCostTextView, true));
        setPixieCost();
        updateWidgets(dataHelper.get());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dataHelper.isTutorialShown()) {
                    try {
                        tutorial_btn.performClick();
                        dataHelper.putTutorialShown(true);
                    }catch(Exception e){
                        dataHelper.putTutorialShown(false);
                    }
                    finally {
                        FirebaseMessaging.getInstance().subscribeToTopic("all");
                    }
                }
            }
        }, 2000);
        if (!dataHelper.isRewardPossible()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        try {
                            dataHelper.addPixies(5);
                            myRef.collection(getString(R.string.user_db))
                                    .document(dataHelper.getReferrer())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserModel referrer = documentSnapshot.toObject(UserModel.class);
                                    dataHelper.updatePixieWithUid(dataHelper.getReferrer(),referrer.getPixies()+5);
                                    showInvitation(referrer.getA());
                                    FirebaseDatabase.getInstance().getReference().child("i").child(dataHelper.getReferrer()).setValue(dataHelper.getA());
                                }
                            });
                            dataHelper.putIsRewardPossible(true);
                        }catch(Exception e){
                            dataHelper.putIsRewardPossible(true);
                        }
                    }
            }, 5000);
        }
        dataHelper.declareToken();
        try {
            sceneRef.child("c")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long timestamp=dataSnapshot.getValue(Long.class);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d(TAG, "run: "+dataHelper.getScene_timestamp());
                                        if (dataHelper.getScene_timestamp()!=timestamp && ConnectivityUtils.isNetworkAvailable(getContext())){
                                            dataHelper.putScene_timestamp(timestamp);
                                            dataHelper.saveScene();
                                            ((MainActivity)getActivity()).refreshHome();
                                        }
                                    }catch(Exception ignore){

                                    }
                                }
                            }, 3000);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



        }catch (Exception ignore){
            Toast.makeText(getContext(),"Unexpected problem contacting server. Check Network Connection",Toast.LENGTH_SHORT).show();
        }
        ((MainActivity)getActivity()).setOnDataChangedListener(new MainActivity.OnDataChangedListener() {
            @Override
            public void onDataChanged() {
                updateWidgets(dataHelper.get());
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && resultCode==2){
            Toast.makeText(getContext(), "Thank you for your report. Inappropriate Complaint has been registered!", Toast.LENGTH_LONG).show();
        }
        else if (requestCode==RC_SUCCESS_MASK){
            dataHelper.putMask2Firebase(data.getIntExtra(getString(R.string.mask),R.drawable.dp_1));
        }
        else if (requestCode==RC_SUCCESS_QUOTE){
            dataHelper.putQuote2Firebase(data.getStringExtra(getString(R.string.quote)));
            startSelect_mask();
        }
        else if (requestCode==RC_SUCCESS_GENDER){
            dataHelper.putGender2Firebase(data.getStringExtra(getString(R.string.gender)));
            if (dataHelper.getQuote().equals(""))
                startQuoteActivity();
        }
        else if (requestCode==RC_SUCCESS_ALIAS){
            dataHelper.putAlias2Firebase(data.getStringExtra(getString(R.string.alias)));
            if (dataHelper.getGender().equals(""))
                startGenderActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setPixieCost();
        if (!isCardVisible) {
            mShaker.resume();
            updateWidgets(dataHelper.get());
        }
    }
    @Override
    public void onPause()
    {
        mShaker.pause();
        super.onPause();
    }
    private void startSelect_mask(){
        startActivityForResult(new Intent(getContext(), Select_mask.class),RC_SUCCESS_MASK);
    }
    private void startQuoteActivity(){
        startActivityForResult(new Intent(getContext(), QuoteActivity.class),RC_SUCCESS_QUOTE);
    }
    private void startGenderActivity(){
        startActivityForResult(new Intent(getContext(), GenderActivity.class),RC_SUCCESS_GENDER);
    }
    private void startAliasActivity(){
        startActivityForResult(new Intent(getContext(), AliasActivity.class),RC_SUCCESS_ALIAS);
    }
    private void showInvitation(String name){
        new InvitationDialog.Builder(getContext(),name).build().show();
    }

    private void sync(){
        double millis=System.currentTimeMillis()-dataHelper.getLast_sign_at();
        double hours = millis/(1000 * 60 * 60);
        if (hours>1.0){
            dataHelper.putLast_sign_at(System.currentTimeMillis());
            dataHelper.putAds_watched(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        try {
                            new CongoPixiesDialog.Builder(getContext(),dataHelper.getIsPremium()).build().show();
                        }catch(Exception ignore){
                        }
                        finally {
                            dataHelper.congoPixies();
                        }
                }
            }, 2000);
            Intent intent1 = new Intent(getContext(), ClaimPixiesNotification.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, dataHelper.getLast_sign_at()+1000 * 60 * 60, pendingIntent);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataHelper.setAvailable();
            }
        }, 2000);
        if (!dataHelper.getRemoteTmp().getRemoteUserId().isEmpty()){
            RemoteUserConnection tmp=dataHelper.getRemoteTmp();
            Toast.makeText(getContext(), "App closed unexpectedly! Showing last matched user..", Toast.LENGTH_SHORT).show();
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            new RemoteCardDialog.Builder(getContext(),tmp.getRemoteUserId(),tmp.getRemoteUserCharisma(),tmp.getRemoteUserDp(),tmp.getRemoteUserName(),tmp.getRemoteUserQuote(),false).build().show();
                    }
                }, 2000);
            }catch (Exception e){
                Log.d("Homelol",e.getMessage());
            }
            dataHelper.saveRemoteTmp(null);
        }

    }
    private long getPixieCost(){
        TextView pixie_cost = (TextView) pixies_cost_switcher.getCurrentView();
        if (pixie_cost.getText().toString().length()>0) {
            return Long.valueOf(pixie_cost.getText().toString());
        }
        return 0;
    }

    private void initPrivateMode(){
        if (dataHelper.getIsPrivate())
            privateMode.setImageResource(R.drawable.ic_private_selected);
        else
            privateMode.setImageResource(R.drawable.ic_private);
    }

    private void initElements(){
        List<SceneModel> scenes = dataHelper.getScenes();
        scene_desc.setFactory(new TextViewFactory(R.style.SceneTextStyle, false));
        scene_desc.setCurrentText("Tap on any one of the scenes above ^ to enact it with a partner");
        hashTagView.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item,boolean selected) {
                SceneModel ch=(SceneModel) item;
                scene_desc.setInAnimation(getContext(), R.anim.fade_in);
                scene_desc.setOutAnimation(getContext(), R.anim.fade_out);
                scene_desc.setText(ch.getDesc());
                selectedScene =ch;
                if(!(dataHelper.getSceneNames()).contains(selectedScene.getName())){
                    ((MainActivity)getActivity()).refreshHome();
                }
            }
        });
        hashTagView.setData(scenes, new HashtagView.DataStateTransform<SceneModel>() {

            @Override
            public CharSequence prepare(SceneModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }

            @Override
            public CharSequence prepareSelected(SceneModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }
        });
    }


    private void showSettings(){
        Intent intent =new Intent(getActivity(), Setting.class);
        startActivity(intent);
    }

    private void setPixieCost(){
        long s=0;
        switch (getSelectedPartnerPreference()){
            case "Man":s=2;
            break;
            case"Woman":s=2;
            break;
            case"Any":s=0;
            break;
        }
        int[] animV = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};
        pixies_cost_switcher.setInAnimation(getContext(), animV[0]);
        pixies_cost_switcher.setOutAnimation(getContext(), animV[1]);
        pixies_cost_switcher.setText(String.valueOf(String.valueOf(s+3)));
    }

    private String getSelectedPartnerPreference() {
        int radioButtonID = PartnerPreferenceRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) PartnerPreferenceRadioGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }

    private void updateWidgets(UserModel user){

        if (user != null) {
            partner.setText(String.valueOf(dataHelper.getPartners()));
            username.setText(user.getA());
            pixies.setText(String.valueOf(user.getPixies()));
            charisma.setText(String.valueOf(user.getCharisma()));
            mask.setImageResource((int) user.getMask());
        }
        else{
            FirebaseAuth.getInstance().signOut();
            getActivity().finish();
        }
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @Override
        public View makeView() {
            final TextView textView = new TextView(getContext());

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }
//            textView.setTextAppearance(styleId);
            TextViewCompat.setTextAppearance(textView,styleId);
            return textView;
        }

    }

}