package in.zeitgeist.testapp.Fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Profile extends Fragment {
    private static String TAG="Profile_Fragment";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseHelper firebaseHelper;

    //widgets
    private TextView username;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseHelper=new FirebaseHelper(getActivity());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        username=(TextView) view.findViewById(R.id.username);
        // Read from the database
        setUpFirebase();
        return view;
    }

    /**
     * retrive user data
     */
    private void setUpFirebase(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                    updateWidgets(firebaseHelper.getUser(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    /**
     * sets all the widgets
     * @param user
     */
    private void updateWidgets(UserModel user){
        username.setText(user.getUsername());
    }

}