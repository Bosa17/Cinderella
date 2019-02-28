package in.zeitgeist.testapp.Fragments;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import in.zeitgeist.testapp.Models.UserModel;
import in.zeitgeist.testapp.R;
import in.zeitgeist.testapp.Utils.FirebaseHelper;

public class Feed extends Fragment {
    //vars
    private static String TAG="Feed_fragment";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;
    private TextView mTvText;

    //widgets
    private TextView username;
    private ImageView settings_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        firebaseHelper=new FirebaseHelper(getActivity());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        username=(TextView) view.findViewById(R.id.username);
        settings_btn=(ImageView) view.findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettingsFragment();
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

    private void startSettingsFragment(){
        Settings s=new Settings();
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.nav_main_fragment,s).commit();
    }
    /**
     * sets all the widgets
     * @param user
     */
    private void updateWidgets(UserModel user){
        username.setText(user.getUsername());
    }
}