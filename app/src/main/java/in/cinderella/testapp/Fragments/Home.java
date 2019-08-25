package in.cinderella.testapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import in.cinderella.testapp.Activities.CallActivity;
import in.cinderella.testapp.Activities.Setting;
import in.cinderella.testapp.Models.ChapterModel;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.HashtagView;
import in.cinderella.testapp.Utils.ShakeListener;

public class Home extends Fragment {
    //vars
    private static String TAG=Home.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DataHelper dataHelper;
    private ChapterModel selectedChapter;
    private boolean isCardVisible;

    //widgets
    private ImageView privateMode;
    private RadioGroup PartnerPreferenceRadioGroup;
    private ImageView phone_shake;
    private ScrollView scrollView;
    private HashtagView hashTagView;
    private ShakeListener mShaker;
    private TextView pixies;
    private TextView pixies_cost_switcher;
    private TextSwitcher chapter_desc;
    private TextView username;
    private TextView partner;
    private TextView karma;
    private ImageView settings_btn;
    private ImageView mask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();
        isCardVisible=false;
        myRef = database.getReference();
        dataHelper=new DataHelper(getActivity());
        chapter_desc =view.findViewById(R.id.chapter_desc);
        mask=(ImageView) view.findViewById(R.id.user_dp);
        scrollView=view.findViewById(R.id.scroll);
        PartnerPreferenceRadioGroup=view.findViewById(R.id.parter_preference);
        privateMode=view.findViewById(R.id.private_btn);
        partner =(TextView) view.findViewById(R.id.user_partners);
        pixies=(TextView) view.findViewById(R.id.user_pixies);
        pixies_cost_switcher=(TextView) view.findViewById(R.id.pixie_cost_switcher);
        username=(TextView) view.findViewById(R.id.username);
        karma=(TextView)  view.findViewById(R.id.user_karma);
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
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("isPrivate",dataHelper.getIsPrivate());
                    bundle.putLong("pixieCost",getPixieCost());
                    bundle.putString("chapter",selectedChapter.getName());
                    bundle.putString("partnerPreference",getSelectedPartnerPreference());
                    startActivityForResult(new Intent(getActivity(), CallActivity.class).putExtras(bundle),2);
                    vibe.vibrate(300);
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
        privateMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataHelper.selectPrivate();
                new IsPrivateDialog.Builder(getContext(),dataHelper.getIsPrivate()).build().show();
                setPixieCost();
                initPrivateMode();
            }
        });
        PartnerPreferenceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                setPixieCost();
            }
        });
        hashTagView =(HashtagView) view.findViewById(R.id.channel_tags);
        initElements();
        initPrivateMode();

        // Read from the database
        sync();
        updateWidgets(dataHelper.get());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==-1){
            new RemoteCardDialog.Builder(getContext(),data.getStringExtra(getResources().getString(R.string.uid)),data.getLongExtra(getResources().getString(R.string.karma),0),data.getStringExtra(getResources().getString(R.string.fb_dp)),data.getStringExtra(getResources().getString(R.string.username)),data.getStringExtra(getResources().getString(R.string.quote)),data.getBooleanExtra("isPrivate",false)).setOnDismissListener(new remoteCardDismissListener()).build().show();
            isCardVisible=true;
            dataHelper.usePixies(data.getLongExtra("pixie_cost",0));
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

    private void sync(){
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
    }
    private long getPixieCost(){
        return Long.valueOf(pixies_cost_switcher.getText().toString());
    }

    private void initPrivateMode(){
        if (dataHelper.getIsPrivate())
            privateMode.setImageResource(R.drawable.ic_private_selected);
        else
            privateMode.setImageResource(R.drawable.ic_private);
    }
    private void initElements(){
        List<ChapterModel> chapters = new ArrayList<ChapterModel>();
        chapters.add(new ChapterModel("Chapter1","Once upon a time, in a land far far away, you meet someone special and meandering conversations flow into your soul, connecting you with a real friend... ",1));
        chapters.add(new ChapterModel("Chapter7","Time stands still, the spark with your partner ignites and melts all obstacles away...",1));
        chapters.add(new ChapterModel("Chapter69","Desires Overpower your fears and you live out the kinky life you always wanted...",2));
        selectedChapter=chapters.get(0);
        chapter_desc.setFactory(new TextViewFactory(R.style.ChapterTextView, true));
        chapter_desc.setCurrentText(chapters.get(0).getDesc());
        hashTagView.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item,boolean selected) {
                ChapterModel ch=(ChapterModel) item;
                chapter_desc.setInAnimation(getContext(), R.anim.fade_in);
                chapter_desc.setOutAnimation(getContext(), R.anim.fade_out);
                chapter_desc.setText(ch.getDesc());
                selectedChapter=ch;
                setPixieCost();
            }
        });
        hashTagView.setData(chapters, new HashtagView.DataStateTransform<ChapterModel>() {

            @Override
            public CharSequence prepare(ChapterModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }

            @Override
            public CharSequence prepareSelected(ChapterModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }
        },new HashtagView.DataSelector<ChapterModel>() {
            @Override
            public boolean preselect(ChapterModel item) {
                return chapters.indexOf(item)  == 0;
            }
        });
    }


    private void showSettings(){
        Intent intent =new Intent(getActivity(), Setting.class);
        startActivity(intent);
    }

    private void setPixieCost(){
        long s1=selectedChapter.getCost();
        long s2=dataHelper.getIsPrivate()?1:0;
        long s3=0;
        switch (getSelectedPartnerPreference()){
            case "Man":s3=0;
            break;
            case"Woman":s3=1;
            break;
            case "Any": s3=0;
        }
        pixies_cost_switcher.setText(String.valueOf(s1+s2+s3));
    }

    private String getSelectedPartnerPreference() {
        int radioButtonID = PartnerPreferenceRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) PartnerPreferenceRadioGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }
    /**
     * sets all the widgets
     * @param user
     */
    private void updateWidgets(UserModel user){
        if (user != null) {
            partner.setText(String.valueOf(dataHelper.getPartners()));
            username.setText(user.getUsername());
            pixies.setText(String.valueOf(user.getPixies()));
            karma.setText(String.valueOf(user.getKarma()));
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