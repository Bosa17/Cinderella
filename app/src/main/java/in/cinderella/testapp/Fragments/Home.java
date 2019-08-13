package in.cinderella.testapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import in.cinderella.testapp.Activities.CallActivity;
import in.cinderella.testapp.Activities.Setting;
import in.cinderella.testapp.Models.ChannelModel;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;
import in.cinderella.testapp.Utils.HashtagView;
import in.cinderella.testapp.Utils.ShakeListener;

public class Home extends Fragment {
    //vars
    private static String TAG=Home.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;
    private DataHelper dataHelper;
    private boolean isCardVisible;

    //widgets
    private RadioButton option0;
    private RadioButton option1;
    private RadioButton option2;
    private ImageView phone_shake;
    private ScrollView scrollView;
    private HashtagView hashTagView;
    private ShakeListener mShaker;
    private TextView pixies;
    private TextView username;
    private TextView connection;
    private TextView karma;
    private ImageView settings_btn;
    private ImageView mask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        firebaseHelper=new FirebaseHelper(getActivity());
        database = FirebaseDatabase.getInstance();
        isCardVisible=false;
        myRef = database.getReference();
        dataHelper=new DataHelper(getActivity());;
        mask=(ImageView) view.findViewById(R.id.user_dp);
        scrollView=view.findViewById(R.id.scroll);
        connection=(TextView) view.findViewById(R.id.user_connections);
        pixies=(TextView) view.findViewById(R.id.user_pixies);
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
                    startActivityForResult(new Intent(getActivity(), CallActivity.class),2);
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
        option0=view.findViewById(R.id.option0);
        option1=view.findViewById(R.id.option1);
        option2=view.findViewById(R.id.option2);
        hashTagView =(HashtagView) view.findViewById(R.id.channel_tags);
        List<ChannelModel> CHANNELS = new ArrayList<ChannelModel>();
        CHANNELS.add(new ChannelModel ("IceBreakers","Man","Woman", "Any",0));
        CHANNELS.add(new ChannelModel ("SingersConnect","Man","Woman", "Any",0));
        CHANNELS.add(new ChannelModel ("FiftyShades","Man","Woman", "Any",0));


        hashTagView.setData(CHANNELS, new HashtagView.DataTransform<ChannelModel>() {

            @Override
            public CharSequence prepare(ChannelModel item) {
                SpannableString spannableString = new SpannableString("#" + item.getName());
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }
        },new HashtagView.DataSelector<ChannelModel>() {
            @Override
            public boolean preselect(ChannelModel item) {
                return CHANNELS.indexOf(item)  == 0;
            }
        });
        hashTagView.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item,boolean selected) {
                ChannelModel ch=(ChannelModel) item;
                option0.setText(ch.getOption0());
                option1.setText(ch.getOption1());
                option2.setText(ch.getOption2());
            }
        });
        // Read from the database
        sync();
        updateWidgets(dataHelper.get());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==-1){
            new RemoteCardDialog.Builder(getContext(),data.getStringExtra(getResources().getString(R.string.uid)),data.getLongExtra(getResources().getString(R.string.karma),0),data.getStringExtra(getResources().getString(R.string.fb_dp)),data.getStringExtra(getResources().getString(R.string.username)),data.getStringExtra(getResources().getString(R.string.quote))).setOnDismissListener(new remoteCardDismissListener()).build().show();
            isCardVisible=true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
                .child(firebaseHelper.getUserID()).addValueEventListener(new ValueEventListener() {
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

    private void showSettings(){
        Intent intent =new Intent(getActivity(), Setting.class);
        startActivity(intent);
    }

    /**
     * sets all the widgets
     * @param user
     */
    private void updateWidgets(UserModel user){
        if (user != null) {
            connection.setText(String.valueOf(dataHelper.getConnection()));
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

}