package in.cinderella.testapp.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import in.cinderella.testapp.Activities.Connections;
import in.cinderella.testapp.Activities.Setting;
import in.cinderella.testapp.Models.UserModel;
import in.cinderella.testapp.R;
import in.cinderella.testapp.Utils.DataHelper;
import in.cinderella.testapp.Utils.FirebaseHelper;
import in.cinderella.testapp.Utils.SinchHelper;
import in.cinderella.testapp.Utils.UniversalImageLoader;

import static android.content.Context.MODE_PRIVATE;

public class Feed extends Fragment {
    //vars
    private static String TAG="Feed_fragment";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;
    private SinchHelper sinchHelper;
    private Call call;
    private DataHelper dataHelper;

    //widgets
    private ImageView rewards;
    private TextView username;
    private TextView karma;
    private ImageView settings_btn;
    private ImageView user_dp;
    private Button call_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        firebaseHelper=new FirebaseHelper(getActivity());
        database = FirebaseDatabase.getInstance();
        sinchHelper=new SinchHelper(getActivity(),firebaseHelper.getUserID());
        myRef = database.getReference();
        dataHelper=new DataHelper(getActivity());
        rewards=(ImageView) view.findViewById(R.id.rewards);
        user_dp=(ImageView) view.findViewById(R.id.user_dp);
        username=(TextView) view.findViewById(R.id.username);
        karma=(TextView)  view.findViewById(R.id.user_karma);
        call_btn=(Button) view.findViewById(R.id.call);
        settings_btn=(ImageView) view.findViewById(R.id.settings_btn);
        rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConnections();
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sinchHelper.makeCall("m9br3J93hihpVDLMs2pREu3UAg33");
                firebaseHelper.addKarma(dataHelper.getkarma()+10);
            }
        });
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
            }
        });
        // Read from the database
        sync();
        updateWidgets(dataHelper.get());
        return view;
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

    private void showConnections(){
        Intent intent=new Intent(getActivity(), Connections.class);
        startActivity(intent);
    }

    private void showSettings(){
        Intent intent =new Intent(getActivity(), Setting.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWidgets(dataHelper.get());
    }


    /**
     * sets all the widgets
     * @param user
     */
    private void updateWidgets(UserModel user){
        if (user != null) {
            username.setText(user.getUsername());
            karma.setText(Long.toString(user.getKarma()));
            user_dp.setImageResource((int) user.getMask());
        }
        else{
            //TODO
        }
    }


}