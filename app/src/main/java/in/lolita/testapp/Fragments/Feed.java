package in.lolita.testapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import in.lolita.testapp.Activities.Setting;
import in.lolita.testapp.Models.UserModel;
import in.lolita.testapp.R;
import in.lolita.testapp.Utils.FirebaseHelper;
import in.lolita.testapp.Utils.UniversalImageLoader;

public class Feed extends Fragment {
    //vars
    private static String TAG="Feed_fragment";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;
    private String mAppend = "file:/";

    //widgets
    private TextView username;
    private TextView karma;
    private ImageView settings_btn;
    private ImageView user_dp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        firebaseHelper=new FirebaseHelper(getActivity());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user_dp=(ImageView) view.findViewById(R.id.user_dp);
        username=(TextView) view.findViewById(R.id.username);
        karma=(TextView)  view.findViewById(R.id.user_karma);
        settings_btn=(ImageView) view.findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
            }
        });
        // Read from the database
        setUpFirebase();
        return view;
    }
    private void setUpFirebase(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // update the profile widgets with the retrieved data
                updateWidgets(firebaseHelper.getUser(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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
            karma.setText(Long.toString(user.getKarma()));
            UniversalImageLoader.setImage(user.getFb_dp(), user_dp, null, null,getContext());
        }
        else{
            //TODO
        }
    }
}