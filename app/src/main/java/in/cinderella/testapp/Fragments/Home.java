package in.cinderella.testapp.Fragments;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;

import java.util.List;

import in.cinderella.testapp.Activities.CallActivity;
import in.cinderella.testapp.Activities.MainActivity;
import in.cinderella.testapp.Activities.Setting;
import in.cinderella.testapp.Models.SceneModel;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.AlarmReceiver;
import in.cinderella.testapp.Utils.ConnectivityUtils;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.HashtagView;
import in.cinderella.testapp.Utils.ServiceDataHelper;
import in.cinderella.testapp.Utils.ShakeListener;

public class Home extends Fragment {
    //vars
    private static String TAG=Home.class.getSimpleName();
    private DatabaseReference myRef;
    private DataHelper dataHelper;
    private SceneModel selectedScene;
    private boolean isCardVisible;

    //widgets
    private ImageView privateMode;
    private RadioGroup PartnerPreferenceRadioGroup;
    private ImageView phone_shake;
    private ScrollView scrollView;
    private HashtagView hashTagView;
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
        myRef =  FirebaseDatabase.getInstance().getReference();
        ServiceDataHelper serviceDataHelper=new ServiceDataHelper(getActivity());
        serviceDataHelper.setOnSceneChangedListener(new ServiceDataHelper.OnSceneChangedListener() {
            @Override
            public void onSceneChanged() {
                ((MainActivity)getActivity()).refreshHome();
            }
        });
        dataHelper=new DataHelper(getActivity());
        scene_desc =view.findViewById(R.id.scene_desc);
        mask=(ImageView) view.findViewById(R.id.user_dp);
        scrollView=view.findViewById(R.id.scroll);
        PartnerPreferenceRadioGroup=view.findViewById(R.id.parter_preference);
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
//                            bundle.putString("partnerPreference", getSelectedPartnerPreference());
                            startActivityForResult(new Intent(getActivity(), CallActivity.class).putExtras(bundle).putExtra("scene", selectedScene), 2);
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
//        PartnerPreferenceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId)
//            {
//                setPixieCost();
//            }
//        });
        if (dataHelper.getIsPremium())
            isPremiumTextView.setVisibility(View.VISIBLE);
        hashTagView =(HashtagView) view.findViewById(R.id.scene_tags);
        initElements();
        initPrivateMode();

        pixies_cost_switcher.setFactory(new TextViewFactory(R.style.PixieCostTextView, true));
        setPixieCost();
        updateWidgets(dataHelper.get());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!dataHelper.isTutorialShown()) {
                    try {
                        FirebaseMessaging.getInstance().subscribeToTopic("all");
                        tutorial_btn.performClick();
                        dataHelper.putTutorialShown(true);
                    }catch(Exception e){
                        dataHelper.putTutorialShown(false);
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
                            myRef.child(getString(R.string.user_db))
                                    .child(dataHelper.getReferrer())
                                    .child(getString(R.string.pixies))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataHelper.updatePixieWithUid(dataHelper.getReferrer(),dataSnapshot.getValue(Long.class)+5);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            myRef.child(getString(R.string.user_db))
                                    .child(dataHelper.getReferrer())
                                    .child(getString(R.string.username))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            showInvitation(dataSnapshot.getValue(String.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            dataHelper.putIsRewardPossible(true);
                        }catch(Exception e){
                            dataHelper.putIsRewardPossible(true);
                        }
                    }
            }, 5000);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==-1){
            new RemoteCardDialog.Builder(getContext(),data.getStringExtra(getResources().getString(R.string.uid)),data.getLongExtra(getResources().getString(R.string.charisma),0),data.getStringExtra(getResources().getString(R.string.fb_dp)),data.getStringExtra(getResources().getString(R.string.username)),data.getStringExtra(getResources().getString(R.string.quote)),data.getBooleanExtra("isPrivate",false)).setOnDismissListener(new remoteCardDismissListener()).build().show();
            isCardVisible=true;
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
            Intent intent1 = new Intent(getContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, dataHelper.getLast_sign_at()+1000 * 60 * 60, pendingIntent);
        }
        try {
            myRef.child("c")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long timestamp=dataSnapshot.getValue(Long.class);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (dataHelper.getScene_timestamp()!=timestamp && ConnectivityUtils.isNetworkAvailable(getContext())){
                                    dataHelper.putScene_timestamp(timestamp);
                                    dataHelper.saveScene();
                                    ((MainActivity)getActivity()).refreshHome();
                                }
                            }catch(Exception ignore){
                                Toast.makeText(getContext(), "Unexpected problem contacting server. Check Network Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 7000);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            myRef.child(getString(R.string.user_db))
                    .child(dataHelper.getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataHelper.syncWithFirebase(dataSnapshot);
                    updateWidgets(dataHelper.get());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception ignore){
            Toast.makeText(getContext(),"Unexpected problem contacting server. Check Network Connection",Toast.LENGTH_SHORT).show();
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
            }
        });
        hashTagView.setData(scenes, new HashtagView.DataStateTransform<SceneModel>() {

            @Override
            public CharSequence prepare(SceneModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }

            @Override
            public CharSequence prepareSelected(SceneModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }
        });
    }


    private void showSettings(){
        Intent intent =new Intent(getActivity(), Setting.class);
        startActivity(intent);
    }

    private void setPixieCost(){
        long s1=dataHelper.getIsPrivate()?2:0;
        long s2=0;
//        switch (getSelectedPartnerPreference()){
//            case "Man":s2=4;
//            break;
//            case"Woman":s2=4;
//            break;
//            case"Any":s2=0;
//            break;
//        }
        int[] animV = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};
        pixies_cost_switcher.setInAnimation(getContext(), animV[0]);
        pixies_cost_switcher.setOutAnimation(getContext(), animV[1]);
        pixies_cost_switcher.setText(String.valueOf(String.valueOf(s1+s2+3)));
    }

//    private String getSelectedPartnerPreference() {
//        int radioButtonID = PartnerPreferenceRadioGroup.getCheckedRadioButtonId();
//        RadioButton radioButton = (RadioButton) PartnerPreferenceRadioGroup.findViewById(radioButtonID);
//        return radioButton.getText().toString();
//    }

    private void updateWidgets(UserModel user){

        if (user != null) {
            partner.setText(String.valueOf(dataHelper.getPartners()));
            username.setText(user.getUsername());
            pixies.setText(String.valueOf(user.getPixies()));
            charisma.setText(String.valueOf(user.getCharisma()));
            mask.setImageResource((int) user.getMask());
        }
        else{
            FirebaseAuth.getInstance().signOut();
            getActivity().finish();
        }
    }

    private class remoteCardDismissListener implements BlurPopupWindow.OnDismissListener {
        @Override
        public void onDismiss(BlurPopupWindow popupWindow) {
            isCardVisible=false;
            onResume();
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
            textView.setTextAppearance(styleId);

            return textView;
        }

    }


}