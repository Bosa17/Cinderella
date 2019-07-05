package in.cinderella.testapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenfrvr.hashtagview.HashtagView;
import com.sinch.android.rtc.calling.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.cinderella.testapp.Activities.CallActivity;
import in.cinderella.testapp.Activities.Setting;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;
import in.cinderella.testapp.Utils.ShakeListener;

import static org.webrtc.ContextUtils.getApplicationContext;

public class Feed extends Fragment {
    //vars
    private static String TAG="Feed_fragment";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;
    private Call call;
    private DataHelper dataHelper;

    //widgets
    private RadioButton option0;
    private RadioButton option1;
    private RadioButton option2;
    private ImageView phone_shake;
    private ScrollView scrollView;
    private HashtagView hashtagView;
    private ShakeListener mShaker;
    private TextView username;
    private TextView karma;
    private ImageView settings_btn;
    private ImageView mask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        firebaseHelper=new FirebaseHelper(getActivity());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        dataHelper=new DataHelper(getActivity());;
        mask=(ImageView) view.findViewById(R.id.user_dp);
        scrollView=view.findViewById(R.id.scroll);
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
                    startActivity(new Intent(getActivity(), CallActivity.class));
                    vibe.vibrate(300);
                    Toast.makeText(getContext(),"Starting CallActivity!",Toast.LENGTH_LONG).show();
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
        hashtagView=(HashtagView) view.findViewById(R.id.channel_tags);
        List<String> DATA = new ArrayList<String>();
        DATA.add("BFF");
        DATA.add("naughty");
        DATA.add("FiftyShades");
        DATA.add("gaypride");


        hashtagView.setData(DATA, new HashtagView.DataTransform<String>() {

            @Override
            public CharSequence prepare(String item) {
                SpannableString spannableString = new SpannableString("#" + item);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }
        },new HashtagView.DataSelector<String>() {
            @Override
            public boolean preselect(String item) {
                return DATA.indexOf(item)  == 0;
            }
        });
        hashtagView.setSelectionLimit(1);
        hashtagView.addOnTagSelectListener(new HashtagView.TagsSelectListener() {
            @Override
            public void onItemSelected(Object item,boolean selected) {
                option0.setText("Man");
                option1.setText("Woman");
                option2.setText("Other");
                Toast.makeText(getContext(), item.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // Read from the database
        sync();
        updateWidgets(dataHelper.get());
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mShaker.resume();
        updateWidgets(dataHelper.get());
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
            username.setText(user.getUsername());
            karma.setText(String.valueOf(user.getKarma()));
            mask.setImageResource((int) user.getMask());
        }
        else{
            //TODO
        }
    }


}