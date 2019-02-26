package in.zeitgeist.testapp.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import in.zeitgeist.testapp.Models.UserModel;
import in.zeitgeist.testapp.R;


public class FirebaseHelper {
    private static String TAG="FirebaseHelper.class";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID="";

    private Context mContext;

    public FirebaseHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }



    public void addNewUser(String userID,String username){
        long dp=(long)(Math.random()*10);
        UserModel user = new UserModel(username,dp);

        myRef.child("User")
                .child(userID)
                .setValue(user);

    }

    /**
     * retrieve user data from the firebase database
     * @param dataSnapshot
     * @return
     */
    public UserModel getUser(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserSettings: retrieving user account settings from firebase.");

        UserModel user = new UserModel();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
            if(ds.getKey().equals(mContext.getString(R.string.user_db))) {

                user.setUsername(
                        ds.child(userID)
                                .getValue(UserModel.class)
                                .getUsername()
                );
                user.setDp(
                        ds.child(userID)
                                .getValue(UserModel.class)
                                .getDp()
                );

            }
        }
        return user;

    }

}
